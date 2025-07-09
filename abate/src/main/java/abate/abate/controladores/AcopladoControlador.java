
package abate.abate.controladores;

import abate.abate.entidades.Acoplado;
import abate.abate.entidades.AcopladoEstadistica;
import abate.abate.entidades.AcopladosEstadistica;
import abate.abate.entidades.Usuario;
import abate.abate.excepciones.MiException;
import abate.abate.servicios.AcopladoServicio;
import abate.abate.servicios.ExcelServicio;
import abate.abate.util.AcopladoEstadisticaComparador;
import java.io.IOException;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Map;
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
@RequestMapping("/acoplado")
@PreAuthorize("hasAnyRole('ROLE_ADMIN')")
public class AcopladoControlador {
    
    @Autowired
    private AcopladoServicio acopladoServicio;
    @Autowired
    private ExcelServicio excelServicio;

    @GetMapping("/registrar")
    public String registrar(ModelMap modelo) {
        
        modelo.addAttribute("acoplado", new Acoplado());

        return "acoplado_registrar.html";

    }

    @PostMapping("/registro")
    public String registro(@ModelAttribute Acoplado acoplado, ModelMap model, HttpSession session) {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        try {

            acopladoServicio.crearAcoplado(acoplado, logueado);

            return "redirect:/acoplado/registrado";

        } catch (MiException ex) {

            model.addAttribute("acoplado", acoplado);
            model.put("error", ex.getMessage());

            return "acoplado_registrar.html";
        }
    }

    @GetMapping("/registrado")
    public String registrado(HttpSession session, ModelMap modelo) {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        modelo.put("acoplado", acopladoServicio.buscarUltimoAcoplado(logueado.getIdOrg()));
        modelo.put("exito", "Acoplado REGISTRADO con éxito");

        return "acoplado_mostrar.html";
    }
    
    @GetMapping("/listar")
    public String listar(ModelMap modelo, HttpSession session) {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        modelo.addAttribute("acoplados", acopladoServicio.buscarAcopladosAsc(logueado.getIdOrg()));

        return "acoplado_listar.html";

    }
    
    @GetMapping("/listarFiltro")
    public String listarFiltro(@RequestParam Long id, ModelMap modelo, HttpSession session) {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");
        
        modelo.addAttribute("acoplados", acopladoServicio.buscarAcopladosAsc(logueado.getIdOrg()));
        modelo.put("acoplado", acopladoServicio.buscarAcoplado(id));

        return "acoplado_listarFiltro.html";
    }
    
    @GetMapping("/detalle/{id}")
    public String obtenerDetalle(@PathVariable Long id, ModelMap modelo) {
        
        modelo.put("acoplado", acopladoServicio.buscarAcoplado(id));

        return "fragmentos/detalle_acoplado :: historialFragment";

    }
    
    @GetMapping("/modificar/{id}")
    public String modificar(@PathVariable Long id, ModelMap modelo) {

        modelo.put("acoplado", acopladoServicio.buscarAcoplado(id));

        return "acoplado_modificar.html";

    }
    
    @PostMapping("/modifica")
    public String modifica(@ModelAttribute Acoplado acoplado, ModelMap model, HttpSession session) {
        
        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        try {

            acopladoServicio.modificarAcoplado(acoplado, logueado);

            return "redirect:/acoplado/modificado/" + acoplado.getId();

        } catch (MiException ex) {

            model.put("acoplado", acopladoServicio.buscarAcoplado(acoplado.getId()));
            model.put("error", ex.getMessage());

            return "acoplado_modificar.html";
        }

    }

    @GetMapping("/modificado/{id}")
    public String modificado(@PathVariable Long id, ModelMap modelo) {

        modelo.put("acoplado", acopladoServicio.buscarAcoplado(id));
        modelo.put("exito", "Acoplado MODIFICADO con éxito");

        return "acoplado_mostrar.html";

    }
    
     @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id, ModelMap modelo) {

        modelo.put("acoplado", acopladoServicio.buscarAcoplado(id));

        return "acoplado_eliminar.html";
    }

    @GetMapping("/elimina/{id}")
    public String elimina(@PathVariable Long id, ModelMap modelo) {

        try {

            acopladoServicio.eliminarAcoplado(id);

            return "redirect:/acoplado/eliminado";

        } catch (MiException ex) {

            modelo.put("acoplado", acopladoServicio.buscarAcoplado(id));
            modelo.put("error", ex.getMessage());

            return "acoplado_eliminar.html";
        }
    }

    @GetMapping("/eliminado")
    public String eliminado(ModelMap modelo, HttpSession session) {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        modelo.addAttribute("acoplados", acopladoServicio.buscarAcopladosAsc(logueado.getIdOrg()));
        modelo.put("exito", "Acoplado ELIMINADO con éxito");

        return "acoplado_listar.html";

    }
    
    @GetMapping("/mostrarEstadisticaAcoplados")
    public String buscarEstadisticaAcoplados(ModelMap modelo, HttpSession session) throws ParseException {

        String desde = obtenerPrimerDiaMes();
        String hasta = obtenerFechaHasta();
        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        Map<Acoplado, AcopladosEstadistica> estadisticasPorAcoplado = acopladoServicio.estadisticaAcoplados(desde, hasta, logueado.getIdOrg());
        Boolean flag = true;
        if (estadisticasPorAcoplado.size() <= 1) {
            flag = false;
        }

        modelo.addAttribute("estadistica", estadisticasPorAcoplado);
        modelo.put("desde", desde);
        modelo.put("hasta", hasta);
        modelo.put("flag", flag);

        return "acoplado_estadisticaTodos.html";
    }

    @PostMapping("/mostrarEstadisticaAcopladosFiltro")
    public String buscarEstadisticaAcopladosFiltro(@RequestParam String desde, @RequestParam String hasta, ModelMap modelo, HttpSession session) throws ParseException {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        Map<Acoplado, AcopladosEstadistica> estadisticasPorAcoplado = acopladoServicio.estadisticaAcoplados(desde, hasta, logueado.getIdOrg());
        Boolean flag = true;
        if (estadisticasPorAcoplado.size() <= 1) {
            flag = false;
        }

        modelo.addAttribute("estadistica", estadisticasPorAcoplado);
        modelo.put("desde", desde);
        modelo.put("hasta", hasta);
        modelo.put("flag", flag);

        return "acoplado_estadisticaTodos.html";
    }
    
    @GetMapping("/mostrarEstadistica/{id}")
    public String buscarEstadistica(@PathVariable Long id, ModelMap modelo) throws ParseException {

        String desde = obtenerFechaDesdeAño();
        String hasta = obtenerFechaHasta();

        List<AcopladoEstadistica> lista = acopladoServicio.estadisticaAcoplado(desde, hasta, id);
        Boolean flag = true;
        if (lista.size() <= 1) {
            flag = false;
        }

        Collections.sort(lista, AcopladoEstadisticaComparador.ordenarMes);

        modelo.put("acoplado", acopladoServicio.buscarAcoplado(id));
        modelo.addAttribute("estadistica", lista);
        modelo.put("desde", desde);
        modelo.put("hasta", hasta);
        modelo.put("flag", flag);

        return "acoplado_estadistica.html";
    }

    @PostMapping("/mostrarEstadisticaFiltro")
    public String buscarEstadisticaFiltro(@RequestParam Long idAcoplado, @RequestParam String desde, @RequestParam String hasta, ModelMap modelo) throws ParseException {

        List<AcopladoEstadistica> lista = acopladoServicio.estadisticaAcoplado(desde, hasta, idAcoplado);
        Boolean flag = true;
        if (lista.size() <= 1) {
            flag = false;
        }

        Collections.sort(lista, AcopladoEstadisticaComparador.ordenarMes);

        modelo.put("acoplado", acopladoServicio.buscarAcoplado(idAcoplado));
        modelo.addAttribute("estadistica", lista);
        modelo.put("desde", desde);
        modelo.put("hasta", hasta);
        modelo.put("flag", flag);

        return "acoplado_estadistica.html";
    }
    
    @PostMapping("/estadisticaExportar")
    public String estadisticaExportar(@RequestParam String desde, @RequestParam String hasta, @RequestParam Long id, ModelMap modelo) throws ParseException {

        List<AcopladoEstadistica> lista = acopladoServicio.estadisticaAcoplado(desde, hasta, id);

        Collections.sort(lista, AcopladoEstadisticaComparador.ordenarMes);

        modelo.addAttribute("estadistica", lista);
        modelo.put("id", id);
        modelo.put("desde", desde);
        modelo.put("hasta", hasta);

        return "acoplado_estadisticaExportar.html";

    }

    @PostMapping("/estadisticaExporta")
    public void exportToExcel(@RequestParam String desde, @RequestParam String hasta, @RequestParam Long id, HttpServletResponse response) throws IOException, ParseException {

        Acoplado acoplado = acopladoServicio.buscarAcoplado(id);
        List<AcopladoEstadistica> lista = acopladoServicio.estadisticaAcoplado(desde, hasta, id);

        Collections.sort(lista, AcopladoEstadisticaComparador.ordenarMes);

        String htmlContent = generateHtmlFromObjects(lista);
        excelServicio.exportHtmlToExcelEstadisticaAcoplado(htmlContent, response, acoplado);

    }

        
    @PostMapping("/exportarEstadisticaAcoplados")
    public String exportarEstadisticaAcoplados(@RequestParam String desde, @RequestParam String hasta, ModelMap modelo, HttpSession session) throws ParseException {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        Map<Acoplado, AcopladosEstadistica> estadisticasPorAcoplado = acopladoServicio.estadisticaAcoplados(desde, hasta, logueado.getIdOrg());

        modelo.addAttribute("estadistica", estadisticasPorAcoplado);
        modelo.put("desde", desde);
        modelo.put("hasta", hasta);

        return "acoplado_estadisticaExportarTodos.html";

    }

    @PostMapping("/exportaEstadisticaAcoplados")
    public void exportaEstadisticaAcoplados(@RequestParam String desde, @RequestParam String hasta, HttpSession session, HttpServletResponse response) throws IOException, ParseException {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        Map<Acoplado, AcopladosEstadistica> estadisticas = acopladoServicio.estadisticaAcoplados(desde, hasta, logueado.getIdOrg());
        
        String htmlContent = generateHtmlFromEstadisticaAcoplados(estadisticas);
        excelServicio.exportHtmlToExcelEstadisticaAcoplados(htmlContent, response);
    }
    
    private String generateHtmlFromObjects(List<AcopladoEstadistica> objects) {
        StringBuilder sb = new StringBuilder();
        sb.append("<table>");
        sb.append("<thead><tr>"
                + "<th>Año</th>"
                + "<th>Mes</th>"
                + "<th>Viajes</th>"
                + "<th>Km</th>"
                + "</tr></thead>");
        sb.append("<tbody>");
        for (AcopladoEstadistica e : objects) {
            sb.append("<tr><td>").append(e.getYear()).append("</td>"
                    + "<td>").append(e.getMonth()).append("</td>"
                    + "<td>").append(e.getFlete()).append("</td>"
                    + "<td>").append(e.getKmRecorrido()).append("</td>"
                    + "</tr>");
        }
        sb.append("</tbody></table>");
        return sb.toString();
    }
    
     private String generateHtmlFromEstadisticaAcoplados(Map<Acoplado, AcopladosEstadistica> estadisticas) {
        StringBuilder sb = new StringBuilder();
        sb.append("<table>");
        sb.append("<thead><tr>")
                .append("<th>Acoplado</th>")
                .append("<th>Viajes</th>")
                .append("<th>KM</th>");
        sb.append("</tr></thead>");

        sb.append("<tbody>");
        for (Map.Entry<Acoplado, AcopladosEstadistica> entry : estadisticas.entrySet()) {
            Acoplado acoplado = entry.getKey();
            AcopladosEstadistica estadistica = entry.getValue();
            sb.append("<tr>")
                    .append("<td>").append(acoplado.getDominio()).append("</td>")
                    .append("<td>").append(estadistica.getFlete()).append("</td>")
                    .append("<td>").append(estadistica.getKmRecorrido()).append("</td>");
            sb.append("</tr>");
        }
        sb.append("</tbody></table>");
        return sb.toString();
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
    
        public String obtenerFechaDesdeAño() {

        LocalDate now = LocalDate.now();

        LocalDate firstDayOfYear = now.withDayOfYear(1);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        String formattedDate = firstDayOfYear.format(formatter);

        return formattedDate;

    }
    

    
}
