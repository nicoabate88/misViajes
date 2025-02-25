package abate.abate.controladores;

import abate.abate.entidades.Camion;
import abate.abate.entidades.Combustible;
import abate.abate.entidades.Usuario;
import abate.abate.servicios.CamionServicio;
import abate.abate.servicios.ChoferServicio;
import abate.abate.servicios.CombustibleServicio;
import abate.abate.servicios.ExcelServicio;
import java.io.IOException;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
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
@RequestMapping("/combustible")
@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_CHOFER')")
public class CombustibleControlador {

    @Autowired
    private CombustibleServicio combustibleServicio;
    @Autowired
    private ChoferServicio choferServicio;
    @Autowired
    private CamionServicio camionServicio;
    @Autowired
    private ExcelServicio excelServicio;

    @GetMapping("/registrar")
    public String registrar(ModelMap modelo, HttpSession session) {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        modelo.put("chofer", choferServicio.buscarChofer(logueado.getId()));
        modelo.addAttribute("camiones", camionServicio.buscarCamionesAsc(logueado.getIdOrg()));

        return "combustible_registrar.html";
    }

    @PostMapping("/registrarChofer")
    public String registrarCarga(@RequestParam("idCamion") Long idCamion, ModelMap modelo, HttpSession session) {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");
        Camion camion = camionServicio.buscarCamion(idCamion);

        boolean flag = combustibleServicio.kmIniciales(camion);

        if (flag == true) {

            Combustible carga = combustibleServicio.cargaAnterior(camion);

            modelo.put("idChofer", logueado.getId());
            modelo.put("kmAnterior", carga.getKmCarga());
            modelo.put("fechaAnterior", carga.getFechaCarga());
            modelo.put("camion", camion);

            return "combustible_registrarChofer.html";

        } else {

            modelo.put("camion", camion);
            modelo.put("exito", "Comunicarse con su Administrador");

            return "combustible_mensajeKm.html";

        }

    }

    @GetMapping("/registrarAdmin/{id}")
    public String registrarCargaAdmin(@PathVariable Long id, ModelMap modelo) {

        Camion camion = camionServicio.buscarCamion(id);

        boolean flag = combustibleServicio.kmIniciales(camion);

        if (flag == true) {

            Combustible carga = combustibleServicio.cargaAnterior(camion);

            modelo.put("kmAnterior", carga.getKmCarga());
            modelo.put("fechaAnterior", carga.getFechaCarga());
            modelo.put("camion", camion);
            modelo.addAttribute("choferes", choferServicio.bucarChoferesNombreAsc(camion.getIdOrg()));

            return "combustible_registrarAdmin.html";

        } else {

            modelo.put("camion", camion);

            return "combustible_registrarPrimerCarga";

        }

    }

    @PostMapping("/registroPrimerCarga")
    public String registroPrimerCarga(@RequestParam Long idCamion, @RequestParam String fecha,
            @RequestParam Double km, ModelMap modelo, HttpSession session) throws ParseException {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        combustibleServicio.crearPrimerCarga(logueado.getIdOrg(), fecha, km, idCamion, logueado);

        return "redirect:/combustible/registradoPrimerCarga";

    }

    @GetMapping("/registradoPrimerCarga")
    public String registradoPrimerCarga(HttpSession session, ModelMap modelo) {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        Long id = combustibleServicio.buscarUltimo(logueado.getIdOrg());

        modelo.put("carga", combustibleServicio.buscarCombustible(id));
        modelo.put("exito", "KM de Camión REGISTRADO con éxito");

        return "combustible_registradoPrimerCarga";
    }

    @PostMapping("/registro")
    public String registroCarga(@RequestParam Long idCamion, @RequestParam Double kmAnterior, @RequestParam String fecha, @RequestParam Double km,
            @RequestParam Double litro, @RequestParam String completo, @RequestParam(required = false) Double azul, ModelMap modelo, HttpSession session) throws ParseException {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        combustibleServicio.crearCarga(logueado.getIdOrg(), idCamion, fecha, kmAnterior, km, litro, completo, azul, logueado);

        return "redirect:/combustible/registrado";

    }

    @GetMapping("/registrado")
    public String registrado(HttpSession session, ModelMap modelo) {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        Long id = combustibleServicio.buscarUltimo(logueado.getIdOrg());

        modelo.put("carga", combustibleServicio.buscarCombustible(id));
        modelo.put("exito", "Carga de Combustible REGISTRADA con éxito");

        return "combustible_registrado.html";
    }
    
    @PostMapping("/registroAdmin")
    public String registroCargaAdmin(@RequestParam Long idCamion, @RequestParam Double kmAnterior, @RequestParam String fecha, @RequestParam Long idChofer, 
            @RequestParam Double km, @RequestParam Double litro, @RequestParam String completo, @RequestParam(required = false) Double azul, 
            ModelMap modelo, HttpSession session) throws ParseException {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        combustibleServicio.crearCargaAdmin(logueado.getIdOrg(), idCamion, fecha, idChofer, kmAnterior, km, litro, completo, azul, logueado);

        return "redirect:/combustible/registradoAdmin";

    }

    @GetMapping("/registradoAdmin")
    public String registradoAdmin(HttpSession session, ModelMap modelo) {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        Long id = combustibleServicio.buscarUltimo(logueado.getIdOrg());

        modelo.put("carga", combustibleServicio.buscarCombustible(id));
        modelo.put("exito", "Carga de Combustible REGISTRADA con éxito");

        return "combustible_registrado.html";
    }
    
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @GetMapping("/aceptar/{idCarga}")
    public String aceptar(@PathVariable Long idCarga, ModelMap modelo, HttpSession session) {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");
        
        combustibleServicio.aceptarCarga(idCarga, logueado);
        
        Long id = combustibleServicio.buscaridCamion(idCarga);

        return "redirect:/combustible/listarAdmin/" + id;

    }
    
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @GetMapping("/volverPendiente/{idCarga}")
    public String volverPendiente(@PathVariable Long idCarga, ModelMap modelo, HttpSession session) {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");
        
        combustibleServicio.volverPendiente(idCarga, logueado);
        
        Long id = combustibleServicio.buscaridCamion(idCarga);

        return "redirect:/combustible/listarAdmin/" + id;

    }

    @GetMapping("/listarChofer/{id}")
    public String listarCargas(@PathVariable Long id, ModelMap modelo) throws ParseException {

        String desde = obtenerFechaDesde();
        String hasta = obtenerFechaHasta();
        
        ArrayList<Combustible> cargas = combustibleServicio.buscarCargasIdChofer(id, desde, hasta);

        modelo.put("chofer", choferServicio.buscarChofer(id));
        modelo.put("desde", desde);
        modelo.put("hasta", hasta);
        modelo.addAttribute("cargas", cargas);

        return "combustible_listarCargasChofer.html";
    }

    @PostMapping("/listarChoferFiltro")
    public String listarCargas(@RequestParam Long id, @RequestParam String desde, @RequestParam String hasta, ModelMap modelo) throws ParseException {
        
        ArrayList<Combustible> cargas = combustibleServicio.buscarCargasIdChofer(id, desde, hasta);

        modelo.put("chofer", choferServicio.buscarChofer(id));
        modelo.put("desde", desde);
        modelo.put("hasta", hasta);
        modelo.addAttribute("cargas", cargas);

        return "combustible_listarCargasChofer.html";
    }

    @GetMapping("/listarAdmin/{id}")
    public String listarAdmin(@PathVariable Long id, ModelMap modelo) throws ParseException {

        String desde = obtenerFechaDesde();
        String hasta = obtenerFechaHasta();

        ArrayList<Combustible> cargas = combustibleServicio.buscarCargasIdCamion(id, desde, hasta);
        Double litro = 0.0;
        Double azul = 0.0;

        for (Combustible c : cargas) {
            litro = litro + c.getLitro();
            if(c.getAzul() != null){
            azul = azul + c.getAzul().getLitro();
            }
            
        }

        modelo.addAttribute("cargas", cargas);
        modelo.put("desde", desde);
        modelo.put("hasta", hasta);
        modelo.put("camion", camionServicio.buscarCamion(id));
        modelo.put("litros", litro);
        modelo.put("azul", azul);

        return "combustible_listarCargasAdmin.html";
    }

    @PostMapping("/listarAdminFiltro")
    public String listarAdminFiltro(@RequestParam Long id, @RequestParam String desde, @RequestParam String hasta, ModelMap modelo) throws ParseException {

        ArrayList<Combustible> cargas = combustibleServicio.buscarCargasIdCamion(id, desde, hasta);
        Double litro = 0.0;
        Double azul = 0.0;

        for (Combustible c : cargas) {
            litro = litro + c.getLitro();
            if(c.getAzul() != null){
            azul = azul + c.getAzul().getLitro();
            }
        }

        modelo.addAttribute("cargas", cargas);
        modelo.put("desde", desde);
        modelo.put("hasta", hasta);
        modelo.put("camion", camionServicio.buscarCamion(id));
        modelo.put("litros", litro);
        modelo.put("azul", azul);

        return "combustible_listarCargasAdmin.html";
    }

    @GetMapping("/mostrarConsumoAdmin/{id}")
    public String mostrarConsumoAdmin(@PathVariable Long id, ModelMap modelo) {

        ArrayList<Combustible> cargas = combustibleServicio.buscarCargasCamion(id);
        Boolean flag = true;
        if (cargas.isEmpty()) {
            flag = false;
        }

        modelo.put("consumo", combustibleServicio.consumoPromedioCamion(id));
        modelo.addAttribute("cargas", cargas);
        modelo.put("flag", flag);
        modelo.put("id", id);
        modelo.put("camion", camionServicio.buscarCamion(id));

        return "combustible_mostrarConsumoAdmin.html";

    }

    @GetMapping("/mostrarConsumoChofer/{id}")
    public String mostrarConsumo(@PathVariable Long id, ModelMap modelo) {

        ArrayList<Combustible> cargas = combustibleServicio.buscarCargasCamion(id);
        
        modelo.put("consumo", combustibleServicio.consumoPromedioCamion(id));
        modelo.addAttribute("cargas", cargas);
        modelo.put("camion", camionServicio.buscarCamion(id));

        return "combustible_mostrarConsumoChofer.html";

    }

    @GetMapping("/modificar/{id}")
    public String modificar(@PathVariable Long id, ModelMap modelo) {

        Combustible carga = combustibleServicio.buscarCombustible(id);

        if (carga.getKmAnterior() == null) {

            modelo.put("carga", carga);

            return "combustible_modificarPrimerCarga.html";

        } else {

            Combustible cargaAnterior = combustibleServicio.cargaAnteriorPorId(id, carga.getCamion().getId());

            modelo.put("fechaAnterior", cargaAnterior.getFechaCarga());
            modelo.put("carga", carga);
            modelo.put("flag", combustibleServicio.ultimaCarga(carga.getCamion(), id));

            return "combustible_modificar.html";

        }
    }

    @PostMapping("/modifica/{id}")
    public String modifica(@RequestParam Long id, @RequestParam String fecha, @RequestParam Double km,
            @RequestParam Double litro, @RequestParam String completo, @RequestParam(required = false) Double azul, 
            ModelMap modelo, HttpSession session) throws ParseException {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");
        
        combustibleServicio.modificarCarga(id, fecha, km, litro, completo, azul, logueado);

        return "redirect:/combustible/modificado/" + id;
    }
    
    @GetMapping("/modificado/{id}")
    public String modificado(@PathVariable Long id, ModelMap modelo) {

        modelo.put("carga", combustibleServicio.buscarCombustible(id));
        modelo.put("exito", "Carga de Combustible MODIFICADA con éxito");

        return "combustible_modificado.html";       

    }
    
    @GetMapping("/modificarAdmin/{id}")
    public String modificarAdmin(@PathVariable Long id, ModelMap modelo) {

        Combustible carga = combustibleServicio.buscarCombustible(id);

        if (carga.getKmAnterior() == null) {

            modelo.put("carga", carga);

            return "combustible_modificarPrimerCarga.html";

        } else {

           Combustible cargaAnterior = combustibleServicio.cargaAnteriorPorId(id, carga.getCamion().getId());

            modelo.put("fechaAnterior", cargaAnterior.getFechaCarga());
            modelo.put("carga", carga);
            modelo.addAttribute("choferes", choferServicio.bucarChoferesNombreAsc(carga.getIdOrg()));
            modelo.put("flag", combustibleServicio.ultimaCarga(carga.getCamion(), id));

            return "combustible_modificarAdmin.html";

        }
    }

    @PostMapping("/modificaAdmin/{id}")
    public String modificaAdmin(@RequestParam Long id, @RequestParam String fecha, @RequestParam Double km,
            @RequestParam Double litro, @RequestParam String completo, @RequestParam(required = false) Double azul,
            ModelMap modelo, HttpSession session) throws ParseException {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");
        
        combustibleServicio.modificarCarga(id, fecha, km, litro, completo, azul, logueado);
        
        return "redirect:/combustible/modificadoAdmin/" + id;

    }
    
    @GetMapping("/modificadoAdmin/{id}")
    public String modificadoAdmin(@PathVariable Long id, ModelMap modelo) {

        modelo.put("carga", combustibleServicio.buscarCombustible(id));
        modelo.put("exito", "Carga de Combustible MODIFICADA con éxito");

        return "combustible_modificadoAdmin.html";       

    }

    @PostMapping("/modificaPrimerCarga")
    public String modifica(@RequestParam Long id, @RequestParam String fecha, @RequestParam Double km,
            ModelMap modelo) throws ParseException {

        combustibleServicio.modificarPrimerCarga(id, fecha, km);

        return "redirect:/combustible/modificadoPrimeraCarga/" + id;
    }
    
    @GetMapping("/modificadoPrimeraCarga/{id}")
    public String modificadoPrimeraCarga(@PathVariable Long id, ModelMap modelo) {

        modelo.put("carga", combustibleServicio.buscarCombustible(id));
        modelo.put("exito", "KM de Camión MODIFICADO con éxito");

        return "combustible_modificado.html";      

    }

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id, ModelMap modelo) {

        modelo.put("carga", combustibleServicio.buscarCombustible(id));

        return "combustible_eliminar.html";

    }
    
    @GetMapping("/eliminarAdmin/{id}")
    public String eliminarAdmin(@PathVariable Long id, ModelMap modelo) {

        modelo.put("carga", combustibleServicio.buscarCombustible(id));

        return "combustible_eliminarAdmin.html";

    }

    @GetMapping("/elimina/{id}")
    public String elimina(@PathVariable Long id, ModelMap modelo, HttpSession session) throws ParseException {

        combustibleServicio.eliminarCarga(id);

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        if (logueado.getRol().equalsIgnoreCase("CHOFER")) {

            return "redirect:/combustible/eliminadoChofer";

        } else {

            return "redirect:/combustible/eliminadoAdmin";
        }
    }
    
    @GetMapping("/eliminadoChofer")
    public String eliminadoChofer(ModelMap modelo, HttpSession session) {
        
        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

           modelo.put("chofer", logueado);
           modelo.put("exito", "Carga de Combustible ELIMINADA con éxito");

           return "index_chofer.html";      

    }
    
    @GetMapping("/eliminadoAdmin")
    public String eliminadoAdmin(ModelMap modelo, HttpSession session) {
        
        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

            modelo.put("exito", "Carga de Combustible ELIMINADA con éxito");
            modelo.put("id", logueado.getId());

            return "index_admin.html";    

    }

    @PostMapping("/exportar")
    public String exportar(@RequestParam Long idCamion, ModelMap modelo) throws ParseException {

        modelo.addAttribute("cargas", combustibleServicio.buscarCargasCamion(idCamion));
        modelo.put("id", idCamion);

        return "combustible_exportar.html";

    }

    @PostMapping("/exporta")
    public void exporta(@RequestParam Long idCamion, HttpServletResponse response) throws IOException, ParseException {

        ArrayList<Combustible> myObjects = combustibleServicio.buscarCargasCamion(idCamion);
        Double consumo = combustibleServicio.consumoPromedioCamion(idCamion);

        String htmlContent = generateHtmlFromObjects(myObjects);
        excelServicio.exportHtmlToExcelCombustible(htmlContent, response, consumo);

    }

       public String obtenerFechaDesde() {

        LocalDate now = LocalDate.now();

         LocalDate firstDayOfPreviousMonth = now.minusMonths(1).withDayOfMonth(1);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        String formattedDate = firstDayOfPreviousMonth.format(formatter);

        return formattedDate;

    }

    public String obtenerFechaHasta() {

        LocalDate now = LocalDate.now();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        String formattedToday = now.format(formatter);

        return formattedToday;

    }

    private String generateHtmlFromObjects(ArrayList<Combustible> objects) {
        StringBuilder sb = new StringBuilder();
        sb.append("<table>");
        sb.append("<thead><tr>"
                + "<th>Fecha de Carga</th>"
                + "<th>Dominio Camión</th>"
                + "<th>Chofer</th>"
                + "<th>Km Anterior</th>"
                + "<th>Km Carga</th>"
                + "<th>Km Recorrido</th>"
                + "<th>Litros</th>"
                + "<th>Consumo Carga</th>"
                + "<th>Tanque Lleno</th>"
                + "<th>Consumo Tanque</th>"
                + "</tr></thead>");
        sb.append("<tbody>");
        for (Combustible carga : objects) {
            sb.append("<tr><td>").append(carga.getFechaCarga()).append("</td>"
                    + "<td>").append(carga.getCamion().getDominio()).append("</td>"
                    + "<td>").append(carga.getUsuario().getNombre()).append("</td>"
                    + "<td>").append(carga.getKmAnterior()).append("</td>"
                    + "<td>").append(carga.getKmCarga()).append("</td>"
                    + "<td>").append(carga.getKmRecorrido()).append("</td>"
                    + "<td>").append(carga.getLitro()).append("</td>"
                    + "<td>").append(carga.getConsumo()).append("</td>"
                    + "<td>").append(carga.getCompleto()).append("</td>"
                    + "<td>").append(carga.getConsumoPromedio()).append("</td>"
                    + "</tr>");
        }
        sb.append("</tbody></table>");
        return sb.toString();
    }

}
