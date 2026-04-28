package abate.abate.controladores;

import abate.abate.entidades.Cliente;
import abate.abate.entidades.ClienteEstadistica;
import abate.abate.entidades.ClientesEstadistica;
import abate.abate.entidades.Usuario;
import abate.abate.excepciones.MiException;
import abate.abate.servicios.ClienteServicio;
import abate.abate.servicios.ExcelServicio;
import abate.abate.util.ClienteEstadisticaComparador;
import java.io.IOException;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/cliente")
@PreAuthorize("hasAnyRole('ROLE_ADMIN')")
public class ClienteControlador {

    @Autowired
    private ClienteServicio clienteServicio;
    @Autowired
    private ExcelServicio excelServicio;

    @GetMapping("/registrar")
    public String registrarCliente() {

        return "cliente_registrar.html";
    }

    @PostMapping("/registro")
    public String registroCliente(@RequestParam String nombre, @RequestParam(required = false) Long cuit,
            @RequestParam(required = false) String localidad, @RequestParam(required = false) String direccion,
            @RequestParam(required = false) Long telefono, @RequestParam(required = false) String email, @RequestParam String estado,
            ModelMap modelo, HttpSession session) {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        try {

            clienteServicio.crearCliente(logueado.getIdOrg(), nombre, cuit, localidad, direccion, telefono, email, estado);

            return "redirect:/cliente/registrado";

        } catch (MiException ex) {

            modelo.put("nombre", nombre);
            modelo.put("cuit", cuit);
            modelo.put("localidad", localidad);
            modelo.put("direccion", direccion);
            modelo.put("telefono", telefono);
            modelo.put("email", email);
            modelo.put("error", ex.getMessage());

            return "cliente_registrar.html";
        }
    }

    @GetMapping("/registrado")
    public String clienteRegistrado(HttpSession session, ModelMap modelo) {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        Long id = clienteServicio.buscarUltimo(logueado.getIdOrg());
        modelo.put("cliente", clienteServicio.buscarCliente(id));
        modelo.put("exito", "Cliente REGISTRADO con éxito");

        return "cliente_mostrar.html";
    }

    @GetMapping("/listar")
    public String listarClientes(ModelMap modelo, HttpSession session) {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");
        List<Cliente> clientes = clienteServicio.buscarClientesHabNombreAsc(logueado.getIdOrg());
        Boolean flag = false;

        if (!clientes.isEmpty()) {
            flag = true;
        }

        modelo.put("flag", flag);
        modelo.addAttribute("clientes", clientes);

        return "cliente_listar.html";
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @GetMapping("/listarFiltro")
    public String listarFiltro(@RequestParam(required = false) Long id, @RequestParam(required = false) Boolean inhabilitado,
            ModelMap modelo, HttpSession session) {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");
        boolean filtrarInhabilitados = Boolean.TRUE.equals(inhabilitado);

        if (filtrarInhabilitados) {
            // lógica cuando el checkbox está marcado
            List<Cliente> clientes = clienteServicio.buscarClientesNombreAsc(logueado.getIdOrg());
            Boolean flag = false;

            if (!clientes.isEmpty()) {
                flag = true;
            }

            modelo.put("flag", flag);
            modelo.addAttribute("clientes", clientes);
            modelo.put("inhabilitado", Boolean.TRUE.equals(inhabilitado));

            return "cliente_listar.html";

        } else if (id != null) {

            modelo.addAttribute("clientes", clienteServicio.buscarClientesHabNombreAsc(logueado.getIdOrg()));
            modelo.put("cliente", clienteServicio.buscarCliente(id));

            return "cliente_listarFiltro.html";

        } else {

            List<Cliente> clientes = clienteServicio.buscarClientesHabNombreAsc(logueado.getIdOrg());
            Boolean flag = false;

            if (!clientes.isEmpty()) {
                flag = true;
            }

            modelo.put("flag", flag);
            modelo.addAttribute("clientes", clientes);

            return "cliente_listar.html";

        }

    }

    @GetMapping("/detalle/{id}")
    public String obtenerDetalle(@PathVariable Long id, ModelMap modelo) {

        modelo.put("cliente", clienteServicio.buscarCliente(id));

        return "fragmentos/detalle_cliente :: historialFragment";

    }

    @GetMapping("/modificar/{id}")
    public String modificar(@PathVariable Long id, ModelMap modelo) {

        modelo.put("cliente", clienteServicio.buscarCliente(id));

        return "cliente_modificar.html";

    }

    @PostMapping("/modifica/{id}")
    public String modifica(@RequestParam Long id, @RequestParam String nombre, @RequestParam(required = false) Long cuit,
            @RequestParam(required = false) String localidad, @RequestParam(required = false) String direccion,
            @RequestParam(required = false) Long telefono, @RequestParam(required = false) String email, @RequestParam String estado, ModelMap modelo) {

        try {
            clienteServicio.modificarCliente(id, nombre, cuit, localidad, direccion, telefono, email, estado);

            return "redirect:/cliente/modificado/" + id;

        } catch (MiException ex) {

            modelo.put("cliente", clienteServicio.buscarCliente(id));
            modelo.put("error", ex.getMessage());

            return "cliente_modificar.html";

        }
    }

    @GetMapping("/modificado/{id}")
    public String modificado(@PathVariable Long id, ModelMap modelo) {

        modelo.put("cliente", clienteServicio.buscarCliente(id));
        modelo.put("exito", "Cliente MODIFICADO con éxito");

        return "cliente_mostrar.html";

    }

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id, ModelMap modelo) {

        modelo.put("cliente", clienteServicio.buscarCliente(id));

        return "cliente_eliminar.html";
    }

    @GetMapping("/elimina/{id}")
    public String elimina(@PathVariable Long id, ModelMap modelo) {

        try {

            clienteServicio.eliminarCliente(id);

            return "redirect:/cliente/eliminado";

        } catch (MiException ex) {

            modelo.put("cliente", clienteServicio.buscarCliente(id));
            modelo.put("error", ex.getMessage());

            return "cliente_eliminar.html";
        }
    }

    @GetMapping("/eliminado")
    public String eliminado(ModelMap modelo, HttpSession session) {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");
        List<Cliente> clientes = clienteServicio.buscarClientesHabNombreAsc(logueado.getIdOrg());
        Boolean flag = false;

        if (!clientes.isEmpty()) {
            flag = true;
        }

        modelo.put("flag", flag);
        modelo.addAttribute("clientes", clientes);
        modelo.put("exito", "Cliente ELIMINADO con éxito");

        return "cliente_listar.html";

    }

    @GetMapping("/mostrarEstadistica")
    public String buscarEstadistica(ModelMap modelo, HttpSession session) throws ParseException {

        String desde = obtenerPrimerDiaMes();
        String hasta = obtenerFechaHasta();
        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        Map<Cliente, ClientesEstadistica> estadisticasPorCliente = clienteServicio.estadisticaClientes(desde, hasta, logueado.getIdOrg());
        Boolean flag = true;
        if (estadisticasPorCliente.size() <= 1) {
            flag = false;
        }

        modelo.addAttribute("estadistica", estadisticasPorCliente);
        modelo.put("desde", desde);
        modelo.put("hasta", hasta);
        modelo.put("flag", flag);

        return "cliente_estadisticaTodos.html";
    }

    @PostMapping("/mostrarEstadisticaFiltro")
    public String buscarEstadisticaFiltro(@RequestParam String desde, @RequestParam String hasta, ModelMap modelo, HttpSession session) throws ParseException {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        Map<Cliente, ClientesEstadistica> estadisticasPorCliente = clienteServicio.estadisticaClientes(desde, hasta, logueado.getIdOrg());
        Boolean flag = true;
        if (estadisticasPorCliente.size() <= 1) {
            flag = false;
        }

        modelo.addAttribute("estadistica", estadisticasPorCliente);
        modelo.put("desde", desde);
        modelo.put("hasta", hasta);
        modelo.put("flag", flag);

        return "cliente_estadisticaTodos.html";
    }

    @PostMapping("/exportaEstadisticaTodos")
    public void exportaEstadisticaTodos(@RequestParam String desde, @RequestParam String hasta, HttpSession session, HttpServletResponse response) throws IOException, ParseException {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        Map<Cliente, ClientesEstadistica> estadisticas = clienteServicio.estadisticaClientes(desde, hasta, logueado.getIdOrg());

        String htmlContent = generateHtmlFromEstadistica(estadisticas);
        excelServicio.exportHtmlToExcelEstadisticaClientes(htmlContent, response);

    }

    @GetMapping("/imprimirEstadisticaTodos")
    public String imprimirEstadisticaTodos(@RequestParam String desde, @RequestParam String hasta, ModelMap modelo, HttpSession session) throws ParseException {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        Map<Cliente, ClientesEstadistica> estadisticas = clienteServicio.estadisticaClientes(desde, hasta, logueado.getIdOrg());

        modelo.addAttribute("estadistica", estadisticas);
        modelo.put("desde", desde);
        modelo.put("hasta", hasta);

        return "cliente_imprimirEstadisticaTodos.html";

    }

    @GetMapping("/estadistica/{id}")
    public String estadistica(@PathVariable Long id, ModelMap modelo) throws ParseException {

        String desde = obtenerFechaDesdeAño();
        String hasta = obtenerFechaHasta();

        List<ClienteEstadistica> lista = clienteServicio.estadisticaCliente(desde, hasta, id);
        Boolean flag = true;
        if (lista.size() <= 1) {
            flag = false;
        }

        Collections.sort(lista, ClienteEstadisticaComparador.ordenarMes);

        modelo.put("cliente", clienteServicio.buscarCliente(id));
        modelo.addAttribute("estadistica", lista);
        modelo.put("desde", desde);
        modelo.put("hasta", hasta);
        modelo.put("flag", flag);

        return "cliente_estadistica.html";

    }

    @PostMapping("/estadisticaFiltro")
    public String estadisticaFiltro(@RequestParam Long idCliente, @RequestParam String desde, @RequestParam String hasta, ModelMap modelo) throws ParseException {

        List<ClienteEstadistica> lista = clienteServicio.estadisticaCliente(desde, hasta, idCliente);
        Boolean flag = true;
        if (lista.size() <= 1) {
            flag = false;
        }

        Collections.sort(lista, ClienteEstadisticaComparador.ordenarMes);

        modelo.put("cliente", clienteServicio.buscarCliente(idCliente));
        modelo.addAttribute("estadistica", lista);
        modelo.put("desde", desde);
        modelo.put("hasta", hasta);
        modelo.put("flag", flag);

        return "cliente_estadistica.html";

    }

    @PostMapping("/estadisticaExporta")
    public void exportToExcel(@RequestParam String desde, @RequestParam String hasta, @RequestParam Long id, HttpServletResponse response) throws IOException, ParseException {

        Cliente cliente = clienteServicio.buscarCliente(id);
        List<ClienteEstadistica> lista = clienteServicio.estadisticaCliente(desde, hasta, id);

        Collections.sort(lista, ClienteEstadisticaComparador.ordenarMes);

        String htmlContent = generateHtmlFromObjects(lista);
        excelServicio.exportHtmlToExcelCliente(htmlContent, response, cliente);

    }

    @GetMapping("/imprimirEstadistica")
    public String imprimirEstadistica(@RequestParam Long idCliente, @RequestParam String desde, @RequestParam String hasta, ModelMap modelo, HttpSession session) throws ParseException {

        List<ClienteEstadistica> lista = clienteServicio.estadisticaCliente(desde, hasta, idCliente);

        Collections.sort(lista, ClienteEstadisticaComparador.ordenarMes);

        modelo.put("cliente", clienteServicio.buscarCliente(idCliente));
        modelo.addAttribute("estadistica", lista);
        modelo.put("desde", desde);
        modelo.put("hasta", hasta);

        return "cliente_imprimirEstadistica.html";

    }

    @GetMapping("/imprimirClientes")
    public String imprimirClientes(@RequestParam(required = false) Boolean inhabilitado, ModelMap modelo, HttpSession session) throws ParseException {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");
        boolean filtrarInhabilitados = Boolean.TRUE.equals(inhabilitado);

        if (filtrarInhabilitados) {
            // lógica cuando el checkbox está marcado
            modelo.addAttribute("clientes", clienteServicio.buscarClientesNombreAsc(logueado.getIdOrg()));

            return "cliente_imprimirClientes.html";

        } else {

            modelo.addAttribute("clientes", clienteServicio.buscarClientesHabNombreAsc(logueado.getIdOrg()));

            return "cliente_imprimirClientes.html";

        }

    }

    @PostMapping("/clientesExporta")
    public void clientesExporta(@RequestParam(required = false) Boolean inhabilitado, HttpSession session, HttpServletResponse response) throws IOException, ParseException {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");
        boolean filtrarInhabilitados = Boolean.TRUE.equals(inhabilitado);
        List<Cliente> clientes = new ArrayList();

        if (filtrarInhabilitados) {
            // lógica cuando el checkbox está marcado
            clientes = clienteServicio.buscarClientesNombreAsc(logueado.getIdOrg());

        } else {

            clientes = clienteServicio.buscarClientesHabNombreAsc(logueado.getIdOrg());

        }

        String htmlContent = generateHtmlFromClientes(clientes);
        excelServicio.exportHtmlToExcelClientesLista(htmlContent, response);

    }

    private String generateHtmlFromEstadistica(Map<Cliente, ClientesEstadistica> estadisticas) {
        StringBuilder sb = new StringBuilder();
        sb.append("<table>");
        sb.append("<thead><tr>")
                .append("<th>Cliente</th>")
                .append("<th>Viajes</th>")
                .append("<th>KM</th>")
                .append("<th>KG</th>")
                .append("<th>Neto</th>")
                .append("<th>Tarifa</th>")
                .append("<th>Neto/KM</th>");
        sb.append("</tr></thead>");
        sb.append("<tbody>");
        for (Map.Entry<Cliente, ClientesEstadistica> entry : estadisticas.entrySet()) {
            Cliente cliente = entry.getKey();
            ClientesEstadistica estadistica = entry.getValue();
            sb.append("<tr>")
                    .append("<td>").append(cliente.getNombre()).append("</td>")
                    .append("<td>").append(estadistica.getFlete()).append("</td>")
                    .append("<td>").append(estadistica.getKm()).append("</td>")
                    .append("<td>").append(estadistica.getKg()).append("</td>")
                    .append("<td>").append(estadistica.getNeto()).append("</td>")
                    .append("<td>").append(estadistica.getTarifa()).append("</td>")
                    .append("<td>").append(estadistica.getRentabilidad()).append("</td>");
            sb.append("</tr>");
        }
        sb.append("</tbody></table>");
        return sb.toString();
    }

    private String generateHtmlFromObjects(List<ClienteEstadistica> objects) {
        StringBuilder sb = new StringBuilder();
        sb.append("<table>");
        sb.append("<thead><tr>"
                + "<th>Año</th>"
                + "<th>Mes</th>"
                + "<th>Viajes</th>"
                + "<th>Km</th>"
                + "<th>Kg</th>"
                + "<th>Neto</th>"
                + "<th>Tarifa</th>"
                + "<th>Neto/Km</th>"
                + "</tr></thead>");
        sb.append("<tbody>");
        for (ClienteEstadistica e : objects) {
            sb.append("<tr><td>").append(e.getYear()).append("</td>"
                    + "<td>").append(e.getMonth()).append("</td>"
                    + "<td>").append(e.getFlete()).append("</td>"
                    + "<td>").append(e.getKm()).append("</td>"
                    + "<td>").append(e.getKg()).append("</td>"
                    + "<td>").append(e.getNeto()).append("</td>"
                    + "<td>").append(e.getTarifa()).append("</td>"
                    + "<td>").append(e.getRentabilidad()).append("</td>"
                    + "</tr>");
        }
        sb.append("</tbody></table>");
        return sb.toString();
    }

    private String generateHtmlFromClientes(List<Cliente> clientes) {
        StringBuilder sb = new StringBuilder();
        sb.append("<table>");
        sb.append("<thead><tr>"
                + "<th>Nombre</th>"
                + "<th>CUIT</th>"
                + "<th>Estado</th>"
                + "</tr></thead>");
        sb.append("<tbody>");
        for (Cliente cliente : clientes) {
            sb.append("<tr>")
                    .append("<td>").append(cliente.getNombre()).append("</td>")
                    .append("<td>").append(cliente.getCuit()).append("</td>")
                    .append("<td>").append(cliente.getEstado()).append("</td>")
                    .append("</tr>");
        }
        sb.append("</tbody></table>");
        return sb.toString();

    }

    public String obtenerFechaDesdeAño() {

        LocalDate now = LocalDate.now();

        LocalDate firstDayOfYear = now.withDayOfYear(1);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        String formattedDate = firstDayOfYear.format(formatter);

        return formattedDate;

    }

    public String obtenerPrimerDiaMes() {

        LocalDate now = LocalDate.now();

        LocalDate firstDayOfMonth = now.withDayOfMonth(1);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        String formattedDate = firstDayOfMonth.format(formatter);

        return formattedDate;

    }

    public String obtenerFechaHasta() {

        LocalDate now = LocalDate.now();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        String formattedToday = now.format(formatter);

        return formattedToday;

    }

    @PostMapping("/estadisticaViajeDesc")
    public String estadisticaViajeDesc(@RequestParam String desde, @RequestParam String hasta, ModelMap modelo, HttpSession session) throws ParseException {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        Map<Cliente, ClientesEstadistica> estadisticas = clienteServicio.estadisticaClientes(desde, hasta, logueado.getIdOrg());
        Boolean flag = true;
        if (estadisticas.size() <= 1) {
            flag = false;
        }

        Map<Cliente, ClientesEstadistica> estadisticasOrdenadas = estadisticas.entrySet()
                .stream()
                .sorted(Map.Entry.<Cliente, ClientesEstadistica>comparingByValue(
                        Comparator.comparing(ClientesEstadistica::getFlete).reversed() // Orden DESCENDENTE
                ))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));

        modelo.addAttribute("estadistica", estadisticasOrdenadas);
        modelo.put("desde", desde);
        modelo.put("hasta", hasta);
        modelo.put("flag", flag);

        return "cliente_estadisticaTodos.html";

    }

    @PostMapping("/estadisticaKmDesc")
    public String estadisticaKmDesc(@RequestParam String desde, @RequestParam String hasta, ModelMap modelo, HttpSession session) throws ParseException {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        Map<Cliente, ClientesEstadistica> estadisticas = clienteServicio.estadisticaClientes(desde, hasta, logueado.getIdOrg());
        Boolean flag = true;
        if (estadisticas.size() <= 1) {
            flag = false;
        }

        Map<Cliente, ClientesEstadistica> estadisticasOrdenadas = estadisticas.entrySet()
                .stream()
                .sorted(Map.Entry.<Cliente, ClientesEstadistica>comparingByValue(
                        Comparator.comparing(ClientesEstadistica::getKm).reversed() // Orden DESCENDENTE
                ))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));

        modelo.addAttribute("estadistica", estadisticasOrdenadas);
        modelo.put("desde", desde);
        modelo.put("hasta", hasta);
        modelo.put("flag", flag);

        return "cliente_estadisticaTodos.html";

    }

    @PostMapping("/estadisticaKgDesc")
    public String estadisticaKgDesc(@RequestParam String desde, @RequestParam String hasta, ModelMap modelo, HttpSession session) throws ParseException {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        Map<Cliente, ClientesEstadistica> estadisticas = clienteServicio.estadisticaClientes(desde, hasta, logueado.getIdOrg());
        Boolean flag = true;
        if (estadisticas.size() <= 1) {
            flag = false;
        }

        Map<Cliente, ClientesEstadistica> estadisticasOrdenadas = estadisticas.entrySet()
                .stream()
                .sorted(Map.Entry.<Cliente, ClientesEstadistica>comparingByValue(
                        Comparator.comparing(ClientesEstadistica::getKg).reversed() // Orden DESCENDENTE
                ))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));

        modelo.addAttribute("estadistica", estadisticasOrdenadas);
        modelo.put("desde", desde);
        modelo.put("hasta", hasta);
        modelo.put("flag", flag);

        return "cliente_estadisticaTodos.html";

    }

    @PostMapping("/estadisticaNetoDesc")
    public String estadisticaNetoDesc(@RequestParam String desde, @RequestParam String hasta, ModelMap modelo, HttpSession session) throws ParseException {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        Map<Cliente, ClientesEstadistica> estadisticas = clienteServicio.estadisticaClientes(desde, hasta, logueado.getIdOrg());
        Boolean flag = true;
        if (estadisticas.size() <= 1) {
            flag = false;
        }

        Map<Cliente, ClientesEstadistica> estadisticasOrdenadas = estadisticas.entrySet()
                .stream()
                .sorted(Map.Entry.<Cliente, ClientesEstadistica>comparingByValue(
                        Comparator.comparing(ClientesEstadistica::getNeto).reversed() // Orden DESCENDENTE
                ))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));

        modelo.addAttribute("estadistica", estadisticasOrdenadas);
        modelo.put("desde", desde);
        modelo.put("hasta", hasta);
        modelo.put("flag", flag);

        return "cliente_estadisticaTodos.html";

    }

    @PostMapping("/estadisticaTarifaDesc")
    public String estadisticaTarifaDesc(@RequestParam String desde, @RequestParam String hasta, ModelMap modelo, HttpSession session) throws ParseException {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        Map<Cliente, ClientesEstadistica> estadisticas = clienteServicio.estadisticaClientes(desde, hasta, logueado.getIdOrg());
        Boolean flag = true;
        if (estadisticas.size() <= 1) {
            flag = false;
        }

        Map<Cliente, ClientesEstadistica> estadisticasOrdenadas = estadisticas.entrySet()
                .stream()
                .sorted(Map.Entry.<Cliente, ClientesEstadistica>comparingByValue(
                        Comparator.comparing(ClientesEstadistica::getTarifa).reversed() // Orden DESCENDENTE
                ))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));

        modelo.addAttribute("estadistica", estadisticasOrdenadas);
        modelo.put("desde", desde);
        modelo.put("hasta", hasta);
        modelo.put("flag", flag);

        return "cliente_estadisticaTodos.html";

    }

    @PostMapping("/estadisticaRentabilidadDesc")
    public String estadisticaRentabilidadDesc(@RequestParam String desde, @RequestParam String hasta, ModelMap modelo, HttpSession session) throws ParseException {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        Map<Cliente, ClientesEstadistica> estadisticas = clienteServicio.estadisticaClientes(desde, hasta, logueado.getIdOrg());
        Boolean flag = true;
        if (estadisticas.size() <= 1) {
            flag = false;
        }

        Map<Cliente, ClientesEstadistica> estadisticasOrdenadas = estadisticas.entrySet()
                .stream()
                .sorted(Map.Entry.<Cliente, ClientesEstadistica>comparingByValue(
                        Comparator.comparing(ClientesEstadistica::getRentabilidad).reversed() // Orden DESCENDENTE
                ))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));

        modelo.addAttribute("estadistica", estadisticasOrdenadas);
        modelo.put("desde", desde);
        modelo.put("hasta", hasta);
        modelo.put("flag", flag);

        return "cliente_estadisticaTodos.html";

    }

}
