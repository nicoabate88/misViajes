package abate.abate.controladores;

import abate.abate.entidades.Camion;
import abate.abate.entidades.CamionEstadistica;
import abate.abate.entidades.CamionesEstadistica;
import abate.abate.entidades.Usuario;
import abate.abate.excepciones.MiException;
import abate.abate.servicios.AcopladoServicio;
import abate.abate.servicios.CamionServicio;
import abate.abate.servicios.ExcelServicio;
import abate.abate.util.CamionEstadisticaComparador;
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
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/camion")
@PreAuthorize("hasAnyRole('ROLE_ADMIN')")
public class CamionControlador {

    @Autowired
    private CamionServicio camionServicio;
    @Autowired
    private AcopladoServicio acopladoServicio;
    @Autowired
    private ExcelServicio excelServicio;
    
    @GetMapping("/registrar")
    public String registrarCamion(ModelMap modelo) {

        modelo.put("camion", new Camion());
        
        return "camion_registrar.html";
        
    }
    
     @PostMapping("/registro")
    public String registroCamion(@ModelAttribute Camion camion, ModelMap model, HttpSession session) {
        
        Usuario logueado = (Usuario) session.getAttribute("usuariosession");
        
        try {
        
        camionServicio.crearCamion(camion, logueado);
        
        return "redirect:/camion/registrado";
        
        } catch (MiException ex) {
            
            model.addAttribute("camion", camion);
            model.addAttribute("error", ex.getMessage());
            
            return "camion_registrar.html";
            
        }
    }

    @GetMapping("/registrado")
    public String camionRegistrado(HttpSession session, ModelMap modelo) {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        Long id = camionServicio.buscarUltimo(logueado.getIdOrg());

        modelo.put("camion", camionServicio.buscarCamion(id));
        modelo.put("exito", "Camión REGISTRADO con éxito");

        return "camion_mostrar.html";
    }

    @GetMapping("/listar")
    public String listar(ModelMap modelo, HttpSession session) {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");
        List<Camion> camiones = camionServicio.buscarCamionesHabAsc(logueado.getIdOrg());
        Boolean flag = false;

        if (!camiones.isEmpty()) {
            flag = true;
        }

        modelo.put("flag", flag);
        modelo.addAttribute("camiones", camiones);

        return "camion_listar.html";

    }
    
    @GetMapping("/listarFiltro")
    public String listarFiltro(@RequestParam(required = false) Long id, @RequestParam(required = false) Boolean inhabilitado,
            ModelMap modelo, HttpSession session) {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");
        boolean filtrarInhabilitados = Boolean.TRUE.equals(inhabilitado);

        if (filtrarInhabilitados) {
            // lógica cuando el checkbox está marcado
            List<Camion> camiones = camionServicio.buscarCamionesAsc(logueado.getIdOrg());
            modelo.addAttribute("camiones", camiones);
            Boolean flag = false;
            if (!camiones.isEmpty()) {
                flag = true;
            }

            modelo.put("flag", flag);
            modelo.addAttribute("camiones", camiones);
            modelo.put("inhabilitado", Boolean.TRUE.equals(inhabilitado));

            return "camion_listar.html";

        } else if (id != null) {

            modelo.put("camion", camionServicio.buscarCamion(id));
            modelo.addAttribute("camiones", camionServicio.buscarCamionesHabAsc(logueado.getIdOrg()));

            return "camion_listarFiltro.html";

        } else {

            List<Camion> camiones = camionServicio.buscarCamionesHabAsc(logueado.getIdOrg());
            Boolean flag = false;
            if (!camiones.isEmpty()) {
                flag = true;
            }

            modelo.put("flag", flag);
            modelo.addAttribute("camiones", camiones);

            return "camion_listar.html";

        }

    }
    
    @GetMapping("/detalle/{id}")
    public String obtenerDetalle(@PathVariable Long id, ModelMap modelo) {
        
        modelo.put("camion", camionServicio.buscarCamion(id));

        return "fragmentos/detalle_camion :: historialFragment";

    }

    @GetMapping("/mostrarEstadistica/{id}")
    public String buscarEstadistica(@PathVariable Long id, ModelMap modelo) throws ParseException {

        String desde = obtenerFechaDesdeAño();
        String hasta = obtenerFechaHasta();

        ArrayList<CamionEstadistica> lista = camionServicio.estadisticaCamion(desde, hasta, id);
        Boolean flag = true;
        if (lista.size() <= 1) {
            flag = false;
        }

        for (CamionEstadistica e : lista) {
            
            if (e.getKmRecorrido() != 0.0) {
                Double consumo = ((100.0 * e.getLitro()) / e.getKmRecorrido());
                e.setConsumo(Math.round(consumo * 100.0) / 100.0);
                e.setRentabilidad((double) Math.round(e.getNeto() / e.getKmRecorrido()));
            } else {
                e.setConsumo(0.0);
                e.setRentabilidad(0.0);
            }
        }

        Collections.sort(lista, CamionEstadisticaComparador.ordenarMes);

        modelo.put("camion", camionServicio.buscarCamion(id));
        modelo.addAttribute("estadistica", lista);
        modelo.put("desde", desde);
        modelo.put("hasta", hasta);
        modelo.put("flag", flag);

        return "camion_estadistica.html";
    }

    @PostMapping("/mostrarEstadisticaFiltro")
    public String buscarEstadisticaFiltro(@RequestParam Long idCamion, @RequestParam String desde, @RequestParam String hasta, ModelMap modelo) throws ParseException {

        ArrayList<CamionEstadistica> lista = camionServicio.estadisticaCamion(desde, hasta, idCamion);
        Boolean flag = true;
        if (lista.size() <= 1) {
            flag = false;
        }

        for (CamionEstadistica e : lista) {
            if (e.getKmRecorrido() != 0.0) {
            Double consumo = ((100.0 * e.getLitro()) / e.getKmRecorrido());
                e.setConsumo(Math.round(consumo * 100.0) / 100.0);
                e.setRentabilidad((double) Math.round(e.getNeto() / e.getKmRecorrido()));
            } else {
                e.setConsumo(0.0);
                e.setRentabilidad(0.0);
            }
        }

        Collections.sort(lista, CamionEstadisticaComparador.ordenarMes);

        modelo.put("camion", camionServicio.buscarCamion(idCamion));
        modelo.addAttribute("estadistica", lista);
        modelo.put("desde", desde);
        modelo.put("hasta", hasta);
        modelo.put("flag", flag);

        return "camion_estadistica.html";
    }

    @GetMapping("/mostrarEstadisticaCamiones")
    public String buscarEstadisticaCamiones(ModelMap modelo, HttpSession session) throws ParseException {

        String desde = obtenerPrimerDiaMes();
        String hasta = obtenerFechaHasta();
        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        Map<Camion, CamionesEstadistica> estadisticasPorCamion = camionServicio.estadisticaCamiones(desde, hasta, logueado.getIdOrg());
        Boolean flag = true;
        if (estadisticasPorCamion.size() <= 1) {
            flag = false;
        }

        for (CamionesEstadistica estadistica : estadisticasPorCamion.values()) {

            if (estadistica.getKmRecorrido() > 0) {
                Double consumo = ((100.0 * estadistica.getLitro()) / estadistica.getKmRecorrido());
                estadistica.setConsumo(Math.round(consumo * 100.0) / 100.0);
                estadistica.setRentabilidad((double) Math.round(estadistica.getNeto() / estadistica.getKmRecorrido()));
            } else {
                estadistica.setConsumo(0.0);
                estadistica.setRentabilidad(0.0);
            }

        }

        modelo.addAttribute("estadistica", estadisticasPorCamion);
        modelo.put("desde", desde);
        modelo.put("hasta", hasta);
        modelo.put("flag", flag);

        return "camion_estadisticaTodos.html";
    }

    @PostMapping("/mostrarEstadisticaCamionesFiltro")
    public String buscarEstadisticaCamionesFiltro(@RequestParam String desde, @RequestParam String hasta, ModelMap modelo, HttpSession session) throws ParseException {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        Map<Camion, CamionesEstadistica> estadisticasPorCamion = camionServicio.estadisticaCamiones(desde, hasta, logueado.getIdOrg());
        Boolean flag = true;
        if (estadisticasPorCamion.size() <= 1) {
            flag = false;
        }

        for (CamionesEstadistica estadistica : estadisticasPorCamion.values()) {

            if (estadistica.getKmRecorrido() > 0) {
                Double consumo = ((100.0 * estadistica.getLitro()) / estadistica.getKmRecorrido());
                estadistica.setConsumo(Math.round(consumo * 100.0) / 100.0);
                estadistica.setRentabilidad((double) Math.round(estadistica.getNeto() / estadistica.getKmRecorrido()));
            } else {
                estadistica.setConsumo(0.0);
                estadistica.setRentabilidad(0.0);
            }
        }

        modelo.addAttribute("estadistica", estadisticasPorCamion);
        modelo.put("desde", desde);
        modelo.put("hasta", hasta);
        modelo.put("flag", flag);

        return "camion_estadisticaTodos.html";
    }
    
    @GetMapping("/modificar/{id}")
    public String modificar(@PathVariable Long id, ModelMap modelo, HttpSession session) {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");
        modelo.put("camion", camionServicio.buscarCamion(id));
        modelo.addAttribute("acoplados", acopladoServicio.buscarAcopladosHabAsc(logueado.getIdOrg()));

        return "camion_modificar.html";

    }

    @PostMapping("/modifica")
    public String modifica(@ModelAttribute Camion camion, @RequestParam Long idAcoplado, ModelMap model, HttpSession session) {
        
        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        try {

            camionServicio.modificarCamion(camion, idAcoplado, logueado);

            return "redirect:/camion/modificado/" + camion.getId();

        } catch (MiException ex) {

            model.put("camion", camionServicio.buscarCamion(camion.getId()));
            model.addAttribute("acoplados", acopladoServicio.buscarAcopladosHabAsc(logueado.getIdOrg()));
            model.put("error", ex.getMessage());

            return "camion_modificar.html";
        }

    }

    @GetMapping("/modificado/{id}")
    public String modificado(@PathVariable Long id, ModelMap modelo) {

        modelo.put("camion", camionServicio.buscarCamion(id));
        modelo.put("exito", "Camión MODIFICADO con éxito");

        return "camion_mostrar.html";

    }

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id, ModelMap modelo) {

        modelo.put("camion", camionServicio.buscarCamion(id));

        return "camion_eliminar.html";
    }

    @GetMapping("/elimina/{id}")
    public String elimina(@PathVariable Long id, ModelMap modelo) {

        try {

            camionServicio.eliminarCamion(id);

            return "redirect:/camion/eliminado";

        } catch (MiException ex) {

            modelo.put("camion", camionServicio.buscarCamion(id));
            modelo.put("error", ex.getMessage());

            return "camion_eliminar.html";
        }
    }

    @GetMapping("/eliminado")
    public String eliminado(ModelMap modelo, HttpSession session) {
        
        Usuario logueado = (Usuario) session.getAttribute("usuariosession");
        List<Camion> camiones = camionServicio.buscarCamionesHabAsc(logueado.getIdOrg());
        Boolean flag = false;

        if (!camiones.isEmpty()) {
            flag = true;
        }

        modelo.put("flag", flag);
        modelo.addAttribute("camiones", camiones);
        modelo.put("exito", "Camión ELIMINADO con éxito");

        return "camion_listar.html";

    }

    public String obtenerFechaDesde() {

        LocalDate now = LocalDate.now();

        LocalDate firstDayOfPreviousMonth = now.minusMonths(1).withDayOfMonth(1);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        String formattedDate = firstDayOfPreviousMonth.format(formatter);

        return formattedDate;

    }
    
    public String obtenerFechaDesdeAño() {

        LocalDate now = LocalDate.now();

        LocalDate firstDayOfYear = now.withDayOfYear(1);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        String formattedDate = firstDayOfYear.format(formatter);

        return formattedDate;

    }

    public String obtenerFechaHasta() {

        LocalDate now = LocalDate.now();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        String formattedToday = now.format(formatter);

        return formattedToday;

    }

    public String obtenerPrimerDiaMes() {

        LocalDate now = LocalDate.now();

        LocalDate firstDayOfMonth = now.withDayOfMonth(1);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        String formattedDate = firstDayOfMonth.format(formatter);

        return formattedDate;

    }

    @PostMapping("/estadisticaExportar")
    public String estadisticaExportar(@RequestParam String desde, @RequestParam String hasta, @RequestParam Long id, ModelMap modelo) throws ParseException {

        ArrayList<CamionEstadistica> lista = camionServicio.estadisticaCamion(desde, hasta, id);

        for (CamionEstadistica e : lista) {
            if (e.getKmRecorrido() != 0.0) {
            Double consumo = ((100.0 * e.getLitro()) / e.getKmRecorrido());
            e.setConsumo(Math.round(consumo * 100.0) / 100.0);
            e.setRentabilidad((double) Math.round(e.getNeto() / e.getKmRecorrido()));
            } else {
                e.setConsumo(0.0);
                e.setRentabilidad(0.0);
            }
        }

        Collections.sort(lista, CamionEstadisticaComparador.ordenarMes);

        modelo.addAttribute("estadistica", lista);
        modelo.put("id", id);
        modelo.put("desde", desde);
        modelo.put("hasta", hasta);

        return "camion_estadisticaExportar.html";

    }

    @PostMapping("/estadisticaExporta")
    public void exportToExcel(@RequestParam String desde, @RequestParam String hasta, @RequestParam Long id, HttpServletResponse response) throws IOException, ParseException {

        Camion camion = camionServicio.buscarCamion(id);
        ArrayList<CamionEstadistica> myObjects = camionServicio.estadisticaCamion(desde, hasta, id);
        for (CamionEstadistica e : myObjects) {
            if (e.getKmRecorrido() != 0.0) {
            Double consumo = ((100.0 * e.getLitro()) / e.getKmRecorrido());
                e.setConsumo(Math.round(consumo * 100.0) / 100.0);
                e.setRentabilidad((double) Math.round(e.getNeto() / e.getKmRecorrido()));
            } else {
                e.setConsumo(0.0);
                e.setRentabilidad(0.0);
            }
        }

        Collections.sort(myObjects, CamionEstadisticaComparador.ordenarMes);

        String htmlContent = generateHtmlFromObjects(myObjects);
        excelServicio.exportHtmlToExcelEstadistica(htmlContent, response, camion);

    }
    
    @GetMapping("/imprimirEstadistica")
    public String imprimirEstadistica(@RequestParam String desde, @RequestParam String hasta, ModelMap modelo, HttpSession session) throws ParseException {
        
        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        Map<Camion, CamionesEstadistica> estadisticasPorCamion = camionServicio.estadisticaCamiones(desde, hasta, logueado.getIdOrg());

        for (CamionesEstadistica estadistica : estadisticasPorCamion.values()) {

            if (estadistica.getKmRecorrido() > 0) {
                Double consumo = ((100.0 * estadistica.getLitro()) / estadistica.getKmRecorrido());
                estadistica.setConsumo(Math.round(consumo * 100.0) / 100.0);
                estadistica.setRentabilidad((double) Math.round(estadistica.getNeto() / estadistica.getKmRecorrido()));
            } else {
                estadistica.setConsumo(0.0);
                estadistica.setRentabilidad(0.0);
            }
        }

        modelo.addAttribute("estadistica", estadisticasPorCamion);
        modelo.put("desde", desde);
        modelo.put("hasta", hasta);

        return "camion_imprimirEstadistica.html";
        
    }
    
    @GetMapping("/imprimirEstadisticaCamion")
    public String imprimirEstadisticaCamion(@RequestParam Long idCamion, @RequestParam String desde, @RequestParam String hasta, ModelMap modelo) throws ParseException {
        
        ArrayList<CamionEstadistica> lista = camionServicio.estadisticaCamion(desde, hasta, idCamion);

        for (CamionEstadistica e : lista) {
            if (e.getKmRecorrido() != 0.0) {
            Double consumo = ((100.0 * e.getLitro()) / e.getKmRecorrido());
            e.setConsumo(Math.round(consumo * 100.0) / 100.0);
            e.setRentabilidad((double) Math.round(e.getNeto() / e.getKmRecorrido()));
            } else {
                e.setConsumo(0.0);
                e.setRentabilidad(0.0);
            }
        }

        Collections.sort(lista, CamionEstadisticaComparador.ordenarMes);

        modelo.addAttribute("estadistica", lista);
        modelo.put("desde", desde);
        modelo.put("hasta", hasta);
        modelo.put("camion", camionServicio.buscarCamion(idCamion));

        return "camion_imprimirEstadisticaCamion.html";
          
    }

    @PostMapping("/exportarEstadisticaCamiones")
    public String exportarEstadisticaCamiones(@RequestParam String desde, @RequestParam String hasta, ModelMap modelo, HttpSession session) throws ParseException {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        Map<Camion, CamionesEstadistica> estadisticasPorCamion = camionServicio.estadisticaCamiones(desde, hasta, logueado.getIdOrg());

        for (CamionesEstadistica estadistica : estadisticasPorCamion.values()) {

            if (estadistica.getKmRecorrido() > 0) {
                Double consumo = ((100.0 * estadistica.getLitro()) / estadistica.getKmRecorrido());
                estadistica.setConsumo(Math.round(consumo * 100.0) / 100.0);
                estadistica.setRentabilidad((double) Math.round(estadistica.getNeto() / estadistica.getKmRecorrido()));
            } else {
                estadistica.setConsumo(0.0);
                estadistica.setRentabilidad(0.0);
            }
        }

        modelo.addAttribute("estadistica", estadisticasPorCamion);
        modelo.put("desde", desde);
        modelo.put("hasta", hasta);

        return "camion_estadisticaExportarTodos.html";

    }

    @PostMapping("/exportaEstadisticaCamiones")
    public void exportaEstadisticaCamiones(@RequestParam String desde, @RequestParam String hasta, HttpSession session, HttpServletResponse response) throws IOException, ParseException {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        Map<Camion, CamionesEstadistica> estadisticas = camionServicio.estadisticaCamiones(desde, hasta, logueado.getIdOrg());
        for (CamionesEstadistica estadistica : estadisticas.values()) {

            if (estadistica.getKmRecorrido() > 0) {
                Double consumo = ((100.0 * estadistica.getLitro()) / estadistica.getKmRecorrido());
                estadistica.setConsumo(Math.round(consumo * 100.0) / 100.0);
                estadistica.setRentabilidad((double) Math.round(estadistica.getNeto() / estadistica.getKmRecorrido()));
            } else {
                estadistica.setConsumo(0.0);
                estadistica.setRentabilidad(0.0);
            }

        }
        
        String htmlContent = generateHtmlFromEstadisticaCamiones(estadisticas);
        excelServicio.exportHtmlToExcelEstadisticaCamiones(htmlContent, response);
    }
    
    @GetMapping("/imprimirCamiones")
    public String imprimirCamiones(@RequestParam(required = false) Boolean inhabilitado, ModelMap modelo, HttpSession session) throws ParseException {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");
        boolean filtrarInhabilitados = Boolean.TRUE.equals(inhabilitado);

        if (filtrarInhabilitados) {
            // lógica cuando el checkbox está marcado
            modelo.addAttribute("camiones", camionServicio.buscarCamionesAsc(logueado.getIdOrg()));
            
            return "camion_imprimirCamiones.html";
        
        } else {

            modelo.addAttribute("camiones", camionServicio.buscarCamionesHabAsc(logueado.getIdOrg()));

            return "camion_imprimirCamiones.html";

        }

    }
    
    @PostMapping("/camionesExporta")
    public void camionesExporta(@RequestParam(required = false) Boolean inhabilitado, HttpSession session, HttpServletResponse response) throws IOException, ParseException {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");
        boolean filtrarInhabilitados = Boolean.TRUE.equals(inhabilitado);
        List<Camion> camiones = new ArrayList();

        if (filtrarInhabilitados) {
            // lógica cuando el checkbox está marcado
            camiones = camionServicio.buscarCamionesAsc(logueado.getIdOrg());
            
        } else {

            camiones = camionServicio.buscarCamionesHabAsc(logueado.getIdOrg());

        }

        String htmlContent = generateHtmlFromCamiones(camiones);
        excelServicio.exportHtmlToExcelCamionesLista(htmlContent, response);

    }
    
    private String generateHtmlFromCamiones(List<Camion> camiones) {
    StringBuilder sb = new StringBuilder();
    sb.append("<table>");
    sb.append("<thead><tr>"
            + "<th>Dominio</th>"
            + "<th>Marca</th>"
            + "<th>Modelo</th>"
            + "<th>Estado</th>"
            + "<th>Acoplado</th>"
            + "</tr></thead>");
    sb.append("<tbody>");
    for (Camion camion : camiones) {
        String dominioAcoplado = "-";
            if (camion.getAcoplado() != null) {
                dominioAcoplado = camion.getAcoplado().getDominio();
            }
        sb.append("<tr>")
                .append("<td>").append(camion.getDominio()).append("</td>")
                .append("<td>").append(camion.getMarca()).append("</td>")
                .append("<td>").append(camion.getModelo()).append("</td>")
                .append("<td>").append(camion.getEstado()).append("</td>")
                .append("<td>").append(dominioAcoplado).append("</td>")
                .append("</tr>");
    }
    sb.append("</tbody></table>");
    return sb.toString();
   
    }

    private String generateHtmlFromObjects(ArrayList<CamionEstadistica> objects) {
        StringBuilder sb = new StringBuilder();
        sb.append("<table>");
        sb.append("<thead><tr>"
                + "<th>Año</th>"
                + "<th>Mes</th>"
                + "<th>Viajes</th>"
                + "<th>Km</th>"
                + "<th>Litros</th>"
                + "<th>Consumo</th>"
                + "<th>Gastos</th>"
                + "<th>Neto</th>"
                + "<th>Rentabilidad</th>"
                + "</tr></thead>");
        sb.append("<tbody>");
        for (CamionEstadistica e : objects) {
            sb.append("<tr><td>").append(e.getYear()).append("</td>"
                    + "<td>").append(e.getMonth()).append("</td>"
                    + "<td>").append(e.getFlete()).append("</td>"
                    + "<td>").append(e.getKmRecorrido()).append("</td>"
                    + "<td>").append(e.getLitro()).append("</td>"
                    + "<td>").append(e.getConsumo()).append("</td>"
                    + "<td>").append(e.getGasto()).append("</td>"
                    + "<td>").append(e.getNeto()).append("</td>"
                    + "<td>").append(e.getRentabilidad()).append("</td>"
                    + "</tr>");
        }
        sb.append("</tbody></table>");
        return sb.toString();
    }

    private String generateHtmlFromEstadisticaCamiones(Map<Camion, CamionesEstadistica> estadisticas) {
        StringBuilder sb = new StringBuilder();
        sb.append("<table>");
        sb.append("<thead><tr>")
                .append("<th>Camión</th>")
                .append("<th>Viajes</th>")
                .append("<th>KM</th>")
                .append("<th>Diesel</th>")
                .append("<th>Consumo</th>")
                .append("<th>Gastos</th>")
                .append("<th>Neto</th>")
                .append("<th>Neto/KM</th>");
        sb.append("</tr></thead>");

        sb.append("<tbody>");
        for (Map.Entry<Camion, CamionesEstadistica> entry : estadisticas.entrySet()) {
            Camion camion = entry.getKey();
            CamionesEstadistica estadistica = entry.getValue();
            sb.append("<tr>")
                    .append("<td>").append(camion.getDominio()).append("</td>")
                    .append("<td>").append(estadistica.getFlete()).append("</td>")
                    .append("<td>").append(estadistica.getKmRecorrido()).append("</td>")
                    .append("<td>").append(estadistica.getLitro()).append("</td>")
                    .append("<td>").append(estadistica.getConsumo()).append("</td>")
                    .append("<td>").append(estadistica.getGasto()).append("</td>")
                    .append("<td>").append(estadistica.getNeto()).append("</td>")
                    .append("<td>").append(estadistica.getRentabilidad()).append("</td>");
            sb.append("</tr>");
        }
        sb.append("</tbody></table>");
        return sb.toString();
    }
    
    @PostMapping("/mostrarEstadisticaCamionesViajeDesc")
    public String buscarEstadisticaCamionesViajesDesc(@RequestParam String desde, @RequestParam String hasta,
            ModelMap modelo, HttpSession session) throws ParseException {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");
        Map<Camion, CamionesEstadistica> estadisticasPorCamion = camionServicio.estadisticaCamiones(desde, hasta, logueado.getIdOrg());
        Boolean flag = true;
        if (estadisticasPorCamion.size() <= 1) {
            flag = false;
        }

        for (CamionesEstadistica estadistica : estadisticasPorCamion.values()) {

            if (estadistica.getKmRecorrido() > 0) {
                Double consumo = ((100.0 * estadistica.getLitro()) / estadistica.getKmRecorrido());
                estadistica.setConsumo(Math.round(consumo * 100.0) / 100.0);
                estadistica.setRentabilidad((double) Math.round(estadistica.getNeto() / estadistica.getKmRecorrido()));
            } else {
                estadistica.setConsumo(0.0);
                estadistica.setRentabilidad(0.0);
            }
        }

        Map<Camion, CamionesEstadistica> estadisticasOrdenadas = estadisticasPorCamion.entrySet()
                .stream()
                .sorted(Map.Entry.<Camion, CamionesEstadistica>comparingByValue(
                        Comparator.comparing(CamionesEstadistica::getFlete).reversed() // Orden DESCENDENTE
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

        return "camion_estadisticaTodos.html";

    }
    
    @PostMapping("/mostrarEstadisticaCamionesKmDesc")
    public String buscarEstadisticaCamionesKmDesc(@RequestParam String desde, @RequestParam String hasta,
            ModelMap modelo, HttpSession session) throws ParseException {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");
        Map<Camion, CamionesEstadistica> estadisticasPorCamion = camionServicio.estadisticaCamiones(desde, hasta, logueado.getIdOrg());
        Boolean flag = true;
        if (estadisticasPorCamion.size() <= 1) {
            flag = false;
        }

        for (CamionesEstadistica estadistica : estadisticasPorCamion.values()) {

            if (estadistica.getKmRecorrido() > 0) {
                Double consumo = ((100.0 * estadistica.getLitro()) / estadistica.getKmRecorrido());
                estadistica.setConsumo(Math.round(consumo * 100.0) / 100.0);
                estadistica.setRentabilidad((double) Math.round(estadistica.getNeto() / estadistica.getKmRecorrido()));
            } else {
                estadistica.setConsumo(0.0);
                estadistica.setRentabilidad(0.0);
            }
        }

        Map<Camion, CamionesEstadistica> estadisticasOrdenadas = estadisticasPorCamion.entrySet()
                .stream()
                .sorted(Map.Entry.<Camion, CamionesEstadistica>comparingByValue(
                        Comparator.comparing(CamionesEstadistica::getKmRecorrido).reversed() // Orden DESCENDENTE
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

        return "camion_estadisticaTodos.html";

    }

    @PostMapping("/mostrarEstadisticaCamionesDieselDesc")
    public String buscarEstadisticaCamionesDieselDesc(@RequestParam String desde, @RequestParam String hasta,
            ModelMap modelo, HttpSession session) throws ParseException {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");
        Map<Camion, CamionesEstadistica> estadisticasPorCamion = camionServicio.estadisticaCamiones(desde, hasta, logueado.getIdOrg());
        Boolean flag = true;
        if (estadisticasPorCamion.size() <= 1) {
            flag = false;
        }

        for (CamionesEstadistica estadistica : estadisticasPorCamion.values()) {

            if (estadistica.getKmRecorrido() > 0) {
                Double consumo = ((100.0 * estadistica.getLitro()) / estadistica.getKmRecorrido());
                estadistica.setConsumo(Math.round(consumo * 100.0) / 100.0);
                estadistica.setRentabilidad((double) Math.round(estadistica.getNeto() / estadistica.getKmRecorrido()));
            } else {
                estadistica.setConsumo(0.0);
                estadistica.setRentabilidad(0.0);
            }
        }

        Map<Camion, CamionesEstadistica> estadisticasOrdenadas = estadisticasPorCamion.entrySet()
                .stream()
                .sorted(Map.Entry.<Camion, CamionesEstadistica>comparingByValue(
                        Comparator.comparing(CamionesEstadistica::getLitro).reversed() // Orden DESCENDENTE
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

        return "camion_estadisticaTodos.html";

    }

    @PostMapping("/mostrarEstadisticaCamionesConsumoDesc")
    public String buscarEstadisticaCamionesConsumoDesc(@RequestParam String desde, @RequestParam String hasta,
            ModelMap modelo, HttpSession session) throws ParseException {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");
        Map<Camion, CamionesEstadistica> estadisticasPorCamion = camionServicio.estadisticaCamiones(desde, hasta, logueado.getIdOrg());
        Boolean flag = true;
        if (estadisticasPorCamion.size() <= 1) {
            flag = false;
        }

        for (CamionesEstadistica estadistica : estadisticasPorCamion.values()) {

            if (estadistica.getKmRecorrido() > 0) {
                Double consumo = ((100.0 * estadistica.getLitro()) / estadistica.getKmRecorrido());
                estadistica.setConsumo(Math.round(consumo * 100.0) / 100.0);
                estadistica.setRentabilidad((double) Math.round(estadistica.getNeto() / estadistica.getKmRecorrido()));
            } else {
                estadistica.setConsumo(0.0);
                estadistica.setRentabilidad(0.0);
            }
        }

        Map<Camion, CamionesEstadistica> estadisticasOrdenadas = estadisticasPorCamion.entrySet()
                .stream()
                .sorted(Map.Entry.<Camion, CamionesEstadistica>comparingByValue(
                        Comparator.comparing(CamionesEstadistica::getConsumo).reversed() // Orden DESCENDENTE
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

        return "camion_estadisticaTodos.html";

    }
    
    @PostMapping("/mostrarEstadisticaCamionesGastoDesc")
    public String buscarEstadisticaCamionesGastoDesc(@RequestParam String desde, @RequestParam String hasta,
            ModelMap modelo, HttpSession session) throws ParseException {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");
        Map<Camion, CamionesEstadistica> estadisticasPorCamion = camionServicio.estadisticaCamiones(desde, hasta, logueado.getIdOrg());
        Boolean flag = true;
        if (estadisticasPorCamion.size() <= 1) {
            flag = false;
        }

        for (CamionesEstadistica estadistica : estadisticasPorCamion.values()) {

            if (estadistica.getKmRecorrido() > 0) {
                Double consumo = ((100.0 * estadistica.getLitro()) / estadistica.getKmRecorrido());
                estadistica.setConsumo(Math.round(consumo * 100.0) / 100.0);
                estadistica.setRentabilidad((double) Math.round(estadistica.getNeto() / estadistica.getKmRecorrido()));
            } else {
                estadistica.setConsumo(0.0);
                estadistica.setRentabilidad(0.0);
            }
        }

        Map<Camion, CamionesEstadistica> estadisticasOrdenadas = estadisticasPorCamion.entrySet()
                .stream()
                .sorted(Map.Entry.<Camion, CamionesEstadistica>comparingByValue(
                        Comparator.comparing(CamionesEstadistica::getGasto).reversed() // Orden DESCENDENTE
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

        return "camion_estadisticaTodos.html";

    }
    
    @PostMapping("/mostrarEstadisticaCamionesNetoDesc")
    public String buscarEstadisticaCamionesNetoDesc(@RequestParam String desde, @RequestParam String hasta,
            ModelMap modelo, HttpSession session) throws ParseException {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");
        Map<Camion, CamionesEstadistica> estadisticasPorCamion = camionServicio.estadisticaCamiones(desde, hasta, logueado.getIdOrg());
        Boolean flag = true;
        if (estadisticasPorCamion.size() <= 1) {
            flag = false;
        }

        for (CamionesEstadistica estadistica : estadisticasPorCamion.values()) {

            if (estadistica.getKmRecorrido() > 0) {
                Double consumo = ((100.0 * estadistica.getLitro()) / estadistica.getKmRecorrido());
                estadistica.setConsumo(Math.round(consumo * 100.0) / 100.0);
                estadistica.setRentabilidad((double) Math.round(estadistica.getNeto() / estadistica.getKmRecorrido()));
            } else {
                estadistica.setConsumo(0.0);
                estadistica.setRentabilidad(0.0);
            }
        }

        Map<Camion, CamionesEstadistica> estadisticasOrdenadas = estadisticasPorCamion.entrySet()
                .stream()
                .sorted(Map.Entry.<Camion, CamionesEstadistica>comparingByValue(
                        Comparator.comparing(CamionesEstadistica::getNeto).reversed() // Orden DESCENDENTE
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

        return "camion_estadisticaTodos.html";

    }
    
    @PostMapping("/mostrarEstadisticaCamionesRentabilidadDesc")
    public String buscarEstadisticaCamionesRentabilidadDesc(@RequestParam String desde, @RequestParam String hasta,
            ModelMap modelo, HttpSession session) throws ParseException {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");
        Map<Camion, CamionesEstadistica> estadisticasPorCamion = camionServicio.estadisticaCamiones(desde, hasta, logueado.getIdOrg());
        Boolean flag = true;
        if (estadisticasPorCamion.size() <= 1) {
            flag = false;
        }

        for (CamionesEstadistica estadistica : estadisticasPorCamion.values()) {

            if (estadistica.getKmRecorrido() > 0) {
                Double consumo = ((100.0 * estadistica.getLitro()) / estadistica.getKmRecorrido());
                estadistica.setConsumo(Math.round(consumo * 100.0) / 100.0);
                estadistica.setRentabilidad((double) Math.round(estadistica.getNeto() / estadistica.getKmRecorrido()));
            } else {
                estadistica.setConsumo(0.0);
                estadistica.setRentabilidad(0.0);
            }
        }

        Map<Camion, CamionesEstadistica> estadisticasOrdenadas = estadisticasPorCamion.entrySet()
                .stream()
                .sorted(Map.Entry.<Camion, CamionesEstadistica>comparingByValue(
                        Comparator.comparing(CamionesEstadistica::getRentabilidad).reversed() // Orden DESCENDENTE
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

        return "camion_estadisticaTodos.html";

    }

}
