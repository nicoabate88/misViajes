package abate.abate.controladores;

import abate.abate.entidades.Camion;
import abate.abate.entidades.CamionEstadistica;
import abate.abate.entidades.CamionesEstadistica;
import abate.abate.entidades.Usuario;
import abate.abate.excepciones.MiException;
import abate.abate.servicios.CamionServicio;
import abate.abate.servicios.ExcelServicio;
import abate.abate.util.CamionEstadisticaComparador;
import java.io.IOException;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
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
@RequestMapping("/camion")
@PreAuthorize("hasAnyRole('ROLE_ADMIN')")
public class CamionControlador {

    @Autowired
    private CamionServicio camionServicio;
    @Autowired
    private ExcelServicio excelServicio;

    @GetMapping("/registrar")
    public String registrar(ModelMap modelo) {

        return "camion_registrar.html";

    }

    @PostMapping("/registro")
    public String registro(@RequestParam String dominio, @RequestParam String marca, @RequestParam String modelo, @RequestParam String azul, ModelMap model, HttpSession session) {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        try {

            camionServicio.crearCamion(logueado.getIdOrg(), marca, modelo, dominio, azul);

            return "redirect:/camion/registrado";

        } catch (MiException ex) {

            model.put("marca", marca);
            model.put("modelo", modelo);
            model.put("dominio", dominio);
            model.put("error", ex.getMessage());

            return "camion_registrar.html";
        }
    }

    @GetMapping("/registrado")
    public String camionRegistrado(HttpSession session, ModelMap modelo) {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        Long id = camionServicio.buscarUltimo(logueado.getIdOrg());

        modelo.put("camion", camionServicio.buscarCamion(id));
        modelo.put("exito", "Camión REGISTRADO con éxito");

        return "camion_registrado.html";
    }

    @GetMapping("/listar")
    public String listar(ModelMap modelo, HttpSession session) {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        modelo.addAttribute("camiones", camionServicio.buscarCamionesAsc(logueado.getIdOrg()));

        return "camion_listar.html";

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
                e.setConsumo((double) Math.round((100 * e.getLitro()) / e.getKmRecorrido()));
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
            e.setConsumo((double) Math.round((100 * e.getLitro()) / e.getKmRecorrido()));
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

    @GetMapping("/modificar/{id}")
    public String modificar(@PathVariable Long id, ModelMap modelo) {

        modelo.put("camion", camionServicio.buscarCamion(id));

        return "camion_modificar.html";

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
                estadistica.setConsumo((double) Math.round((estadistica.getLitro() * 100) / estadistica.getKmRecorrido()));
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
                estadistica.setConsumo((double) Math.round((estadistica.getLitro() * 100) / estadistica.getKmRecorrido()));
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

    @PostMapping("/modifica")
    public String modifica(@RequestParam Long id, @RequestParam String dominio, @RequestParam String marca, @RequestParam String modelo, @RequestParam String azul, ModelMap model) {

        try {

            camionServicio.modificarCamion(id, marca, modelo, dominio, azul);

            return "redirect:/camion/modificado/" + id;

        } catch (MiException ex) {

            model.put("camion", camionServicio.buscarCamion(id));
            model.put("error", ex.getMessage());

            return "camion_modificar.html";
        }

    }

    @GetMapping("/modificado/{id}")
    public String modificado(@PathVariable Long id, ModelMap modelo) {

        modelo.put("camion", camionServicio.buscarCamion(id));
        modelo.put("exito", "Camión MODIFICADO con éxito");

        return "camion_registrado.html";

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

        modelo.put("id", logueado.getId());
        modelo.put("exito", "Camión ELIMINADO con éxito");

        return "index_admin.html";

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
            e.setConsumo((double) Math.round((100 * e.getLitro()) / e.getKmRecorrido()));
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
            e.setConsumo((double) Math.round((100 * e.getLitro()) / e.getKmRecorrido()));
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

    @PostMapping("/exportarEstadisticaCamiones")
    public String exportarEstadisticaCamiones(@RequestParam String desde, @RequestParam String hasta, ModelMap modelo, HttpSession session) throws ParseException {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        Map<Camion, CamionesEstadistica> estadisticasPorCamion = camionServicio.estadisticaCamiones(desde, hasta, logueado.getIdOrg());

        for (CamionesEstadistica estadistica : estadisticasPorCamion.values()) {

            if (estadistica.getKmRecorrido() > 0) {
                estadistica.setConsumo((double) Math.round((estadistica.getLitro() * 100) / estadistica.getKmRecorrido()));
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
                estadistica.setConsumo((double) Math.round((estadistica.getLitro() * 100) / estadistica.getKmRecorrido()));
                estadistica.setRentabilidad((double) Math.round(estadistica.getNeto() / estadistica.getKmRecorrido()));
            } else {
                estadistica.setConsumo(0.0);
                estadistica.setRentabilidad(0.0);
            }

        }
        String htmlContent = generateHtmlFromEstadisticaCamiones(estadisticas);
        excelServicio.exportHtmlToExcelEstadisticaCamiones(htmlContent, response);
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

}
