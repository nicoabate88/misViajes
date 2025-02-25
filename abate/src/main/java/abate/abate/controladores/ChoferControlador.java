package abate.abate.controladores;

import abate.abate.entidades.Camion;
import abate.abate.entidades.ChoferesEstadistica;
import abate.abate.entidades.Usuario;
import abate.abate.excepciones.MiException;
import abate.abate.servicios.CamionServicio;
import abate.abate.servicios.ChoferServicio;
import abate.abate.servicios.ExcelServicio;
import java.io.IOException;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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
@RequestMapping("/chofer")
public class ChoferControlador {

    @Autowired
    private ChoferServicio choferServicio;
    @Autowired
    private CamionServicio camionServicio;
    @Autowired
    private ExcelServicio excelServicio;

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @GetMapping("/registrar")
    public String registrar(ModelMap modelo, HttpSession session) {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        modelo.addAttribute("camiones", camionServicio.buscarCamionesAsc(logueado.getIdOrg()));

        return "chofer_registrar.html";
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @PostMapping("/registro")
    public String registro(@RequestParam String nombre, @RequestParam Long cuil, @RequestParam(required = false) Long idCamion,
            @RequestParam String caja, @RequestParam String cuenta, @RequestParam Double porcentaje, @RequestParam String nombreUsuario, @RequestParam String password,
            @RequestParam String password2, ModelMap modelo, HttpSession session) {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        try {

            choferServicio.crearChofer(logueado.getIdOrg(), nombre, cuil, idCamion, caja, cuenta, nombreUsuario, porcentaje, password, password2);

            return "redirect:/chofer/registrado";

        } catch (MiException ex) {
            
            if(idCamion != null ){
                Camion camion = camionServicio.buscarCamion(idCamion);
                modelo.put("camion", camion);
            } 

            modelo.put("nombre", nombre);
            modelo.put("cuil", cuil);
            modelo.put("caja", caja);
            modelo.put("cuenta", cuenta);
            modelo.put("nombreUsuario", nombreUsuario);
            modelo.put("porcentaje", porcentaje);
            modelo.addAttribute("camiones", camionServicio.buscarCamionesAsc(logueado.getIdOrg()));
            modelo.put("error", ex.getMessage());

            return "chofer_registrar.html";
        }
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @GetMapping("/registrado")
    public String choferRegistrado(HttpSession session, ModelMap modelo) {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        Long id = choferServicio.buscarUltimo(logueado.getIdOrg());
        modelo.put("chofer", choferServicio.buscarChofer(id));
        modelo.put("exito", "Chofer REGISTRADO con éxito");

        return "chofer_registrado.html";
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @GetMapping("/listar")
    public String listar(ModelMap modelo, HttpSession session) {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");
        modelo.addAttribute("choferes", choferServicio.bucarChoferesNombreAsc(logueado.getIdOrg()));

        return "chofer_listar.html";
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_CHOFER')")
    @GetMapping("/mostrar/{id}")
    public String mostrar(@PathVariable Long id, ModelMap modelo) {

        modelo.put("chofer", choferServicio.buscarChofer(id));

        return "chofer_mostrar.html";

    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @GetMapping("/modificar/{id}")
    public String modificar(@PathVariable Long id, ModelMap modelo, HttpSession session) {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        modelo.put("chofer", choferServicio.buscarChofer(id));
        modelo.addAttribute("camiones", camionServicio.buscarCamionesAsc(logueado.getIdOrg()));

        return "chofer_modificar.html";

    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @PostMapping("/modifica/{id}")
    public String modifica(@RequestParam Long id, @RequestParam String nombre, @RequestParam Long cuil,
            @RequestParam(required = false) Long idCamion, @RequestParam Double porcentaje, @RequestParam String nombreUsuario, ModelMap modelo, HttpSession session) {

        try {
            
            choferServicio.modificarChofer(id, nombre, cuil, idCamion, nombreUsuario, porcentaje);

            return "redirect:/chofer/modificado/" + id;

        } catch (MiException ex) {

            Usuario logueado = (Usuario) session.getAttribute("usuariosession");

            modelo.put("chofer", choferServicio.buscarChofer(id));
            modelo.addAttribute("camiones", camionServicio.buscarCamionesAsc(logueado.getIdOrg()));
            modelo.put("error", ex.getMessage());

            return "chofer_modificar.html";

        }

    }
    
    @GetMapping("/modificado/{id}")
    public String modificado(@PathVariable Long id, ModelMap modelo) {

        modelo.put("chofer", choferServicio.buscarChofer(id));
        modelo.put("exito", "Chofer MODIFICADO con éxito");

        return "chofer_registrado.html";     

    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @GetMapping("/modificarPsw/{id}")
    public String modificarPsw(@PathVariable Long id, ModelMap modelo) {

        modelo.put("chofer", choferServicio.buscarChofer(id));

        return "chofer_modificarPsw.html";

    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @PostMapping("/modificaPsw/{id}")
    public String modificaPsw(@RequestParam Long id, @RequestParam String password, ModelMap modelo, HttpSession session) {

        choferServicio.modificarPswChofer(id, password);

        return "redirect:/chofer/modificadoPsw/" + id;

    }
    
    @GetMapping("/modificadoPsw/{id}")
    public String modificadoPsw(@PathVariable Long id, ModelMap modelo) {

        modelo.put("chofer", choferServicio.buscarChofer(id));
        modelo.put("exito", "Contraseña de Chofer MODIFICADA con éxito");

        return "chofer_registrado.html";      

    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_CHOFER')")
    @GetMapping("/modificarPswChofer/{id}")
    public String modificarPswChofer(@PathVariable Long id, ModelMap modelo) {

        modelo.put("chofer", choferServicio.buscarChofer(id));

        return "chofer_modificarPswChofer.html";

    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_CHOFER')")
    @PostMapping("/modificaPswChofer/{id}")
    public String modificaPswChofer(@RequestParam Long id, @RequestParam String password, ModelMap modelo) {

        choferServicio.modificarPswChofer(id, password);
        
        return "redirect:/chofer/modificadoPswChofer/" + id;

    }
    
    @GetMapping("/modificadoPswChofer/{id}")
    public String modificadoPswChofer(@PathVariable Long id, ModelMap modelo) {

        modelo.put("chofer", choferServicio.buscarChofer(id));
        modelo.put("exito", "Contraseña MODIFICADA con éxito");

        return "chofer_modificadoPsw.html";     

    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id, ModelMap modelo) {

        modelo.put("chofer", choferServicio.buscarChofer(id));

        return "chofer_eliminar.html";
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @GetMapping("/elimina/{id}")
    public String elimina(@PathVariable Long id, HttpSession session, ModelMap modelo) {


        try {

            choferServicio.eliminarChofer(id);
            
            return "redirect:/chofer/eliminado";

        } catch (MiException ex) {

            modelo.put("chofer", choferServicio.buscarChofer(id));
            modelo.put("error", ex.getMessage());

            return "chofer_eliminar.html";
        }
    }
    
    @GetMapping("/eliminado")
    public String eliminado(ModelMap modelo, HttpSession session) {
        
        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        modelo.put("id", logueado.getId());
        modelo.put("exito", "Chofer ELIMINADO con éxito");

        return "index_admin.html";   

    }

    
    @GetMapping("/mostrarEstadistica")
    public String buscarEstadistica(ModelMap modelo, HttpSession session) throws ParseException {

        String desde = obtenerPrimerDiaMes();
        String hasta = obtenerFechaHasta();
        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        Map<Usuario, ChoferesEstadistica> estadisticasPorChofer = choferServicio.estadisticaChoferes(desde, hasta, logueado.getIdOrg());
        Boolean flag = true;
        if (estadisticasPorChofer.isEmpty()) {
            flag = false;
        }

        for (ChoferesEstadistica estadistica : estadisticasPorChofer.values()) {
           
                if (estadistica.getKmRecorrido() > 0) {
                    estadistica.setConsumo( (double) Math.round ((estadistica.getLitro() * 100) / estadistica.getKmRecorrido()));
                    estadistica.setRentabilidad((double) Math.round(estadistica.getNeto() / estadistica.getKmRecorrido()));
                } else {
                    estadistica.setConsumo(0.0); 
                    estadistica.setRentabilidad(0.0);
                }
            
        } 

        modelo.addAttribute("estadistica", estadisticasPorChofer);
        modelo.put("desde", desde);
        modelo.put("hasta", hasta);
        modelo.put("flag", flag);

        return "chofer_estadistica.html";
    }
    
    @PostMapping("/mostrarEstadisticaFiltro")
    public String buscarEstadisticaFiltro(@RequestParam String desde, @RequestParam String hasta, ModelMap modelo, HttpSession session) throws ParseException {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");
        
        Map<Usuario, ChoferesEstadistica> estadisticasPorChofer = choferServicio.estadisticaChoferes(desde, hasta, logueado.getIdOrg());
        Boolean flag = true;
        if (estadisticasPorChofer.isEmpty()) {
            flag = false;
        }
        
         for (ChoferesEstadistica estadistica : estadisticasPorChofer.values()) {
           
                if (estadistica.getKmRecorrido() > 0) {
                    estadistica.setConsumo( (double) Math.round ((estadistica.getLitro() * 100) / estadistica.getKmRecorrido()));
                    estadistica.setRentabilidad((double) Math.round(estadistica.getNeto() / estadistica.getKmRecorrido()));
                } else {
                    estadistica.setConsumo(0.0); 
                    estadistica.setRentabilidad(0.0);
         }
         }
        
    
        modelo.addAttribute("estadistica", estadisticasPorChofer);
        modelo.put("desde", desde);
        modelo.put("hasta", hasta);
        modelo.put("flag", flag);

        return "chofer_estadistica.html";
    }
    
    
    @PostMapping("/exportarEstadistica")
    public String exportarEstadistica(@RequestParam String desde, @RequestParam String hasta, ModelMap modelo, HttpSession session) throws ParseException {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");
        
        Map<Usuario, ChoferesEstadistica> estadisticasPorChofer = choferServicio.estadisticaChoferes(desde, hasta, logueado.getIdOrg());
        
         for (ChoferesEstadistica estadistica : estadisticasPorChofer.values()) {
           
                if (estadistica.getKmRecorrido() > 0) {
                    estadistica.setConsumo( (double) Math.round ((estadistica.getLitro() * 100) / estadistica.getKmRecorrido()));
                    estadistica.setRentabilidad((double) Math.round(estadistica.getNeto() / estadistica.getKmRecorrido()));
                } else {
                    estadistica.setConsumo(0.0); 
                    estadistica.setRentabilidad(0.0);
                }
         }
        
    
        modelo.addAttribute("estadistica", estadisticasPorChofer);
        modelo.put("desde", desde);
        modelo.put("hasta", hasta);

        return "chofer_estadisticaExportar.html";
        
    }
    
    @PostMapping("/exportaEstadistica")
    public void exportaEstadistica(@RequestParam String desde, @RequestParam String hasta, HttpSession session, HttpServletResponse response) throws IOException, ParseException {
    
        Usuario logueado = (Usuario) session.getAttribute("usuariosession");
        
               Map<Usuario, ChoferesEstadistica> estadisticas = choferServicio.estadisticaChoferes(desde, hasta, logueado.getIdOrg());
        
         for (ChoferesEstadistica estadistica : estadisticas.values()) {
           
                if (estadistica.getKmRecorrido() > 0) {
                    estadistica.setConsumo( (double) Math.round ((estadistica.getLitro() * 100) / estadistica.getKmRecorrido()));
                    estadistica.setRentabilidad((double) Math.round(estadistica.getNeto() / estadistica.getKmRecorrido()));
                } else {
                    estadistica.setConsumo(0.0); 
                    estadistica.setRentabilidad(0.0);
                }
         }
            
    String htmlContent = generateHtmlFromEstadistica(estadisticas);
    excelServicio.exportHtmlToExcelEstadisticaChoferes(htmlContent, response);
    
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
    
    private String generateHtmlFromEstadistica(Map<Usuario, ChoferesEstadistica> estadisticas) {
    StringBuilder sb = new StringBuilder();
    sb.append("<table>");
    sb.append("<thead><tr>")
            .append("<th>Chofer</th>")
            .append("<th>KM</th>")
            .append("<th>Diesel</th>")
            .append("<th>Consumo</th>")
            .append("<th>Gastos</th>")
            .append("<th>Viajes</th>")
            .append("<th>Neto</th>")
            .append("<th>Neto/KM</th>");
    sb.append("</tr></thead>");

    sb.append("<tbody>");
    for (Map.Entry<Usuario, ChoferesEstadistica> entry : estadisticas.entrySet()) {
        Usuario chofer = entry.getKey();
        ChoferesEstadistica estadistica = entry.getValue();
        sb.append("<tr>")
                .append("<td>").append(chofer.getNombre()).append("</td>")
                .append("<td>").append(estadistica.getKmRecorrido()).append("</td>")
                .append("<td>").append(estadistica.getLitro()).append("</td>")
                .append("<td>").append(estadistica.getConsumo()).append("</td>")
                .append("<td>").append(estadistica.getGasto()).append("</td>")
                .append("<td>").append(estadistica.getFlete()).append("</td>")
                .append("<td>").append(estadistica.getNeto()).append("</td>")
                .append("<td>").append(estadistica.getRentabilidad()).append("</td>");
        sb.append("</tr>");
    }
    sb.append("</tbody></table>");
    return sb.toString();
}

}
