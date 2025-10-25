package abate.abate.controladores;

import abate.abate.entidades.Camion;
import abate.abate.entidades.Combustible;
import abate.abate.entidades.Usuario;
import abate.abate.servicios.AcopladoServicio;
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
    private AcopladoServicio acopladoServicio;
    @Autowired
    private ExcelServicio excelServicio;

    @GetMapping("/registrar")
    public String registrar(ModelMap modelo, HttpSession session) {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");
        
        if(logueado.getCamion() != null){
            
        Camion camion = logueado.getCamion();

        boolean flag = combustibleServicio.kmIniciales(camion);

        if (flag == true) {

            Combustible carga = combustibleServicio.cargaAnterior(camion);

            modelo.put("idChofer", logueado.getId());
            modelo.put("kmAnterior", carga.getKmCarga());
            modelo.put("fechaAnterior", carga.getFechaCarga());
            modelo.put("camion", camion);
            modelo.addAttribute("camiones", camionServicio.buscarCamionesHabAsc(logueado.getIdOrg()));
            if(logueado.getAcoplado() != null){
            modelo.put("acoplado", logueado.getAcoplado());
            } else {
            modelo.put("acoplado", null);
            }
            modelo.addAttribute("acoplados", acopladoServicio.buscarAcopladosHabAsc(logueado.getIdOrg()));

            return "combustible_registrarChofer.html";

        } else {

            modelo.put("camion", camion);
            modelo.put("exito", "Comunicarse con su Administrador");

            return "combustible_mensajeKm.html";

        }
            
        } else {
             
        modelo.put("chofer", logueado);
        modelo.addAttribute("camiones", camionServicio.buscarCamionesHabAsc(logueado.getIdOrg()));

        return "combustible_registrar.html";
        
        }
        
    }

    @GetMapping("/registrarChofer")
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
            modelo.addAttribute("camiones", camionServicio.buscarCamionesHabAsc(logueado.getIdOrg()));
            if(logueado.getAcoplado() != null){
            modelo.put("acoplado", logueado.getAcoplado());
            } else {
            modelo.put("acoplado", null);
            }
            modelo.addAttribute("acoplados", acopladoServicio.buscarAcopladosHabAsc(logueado.getIdOrg()));

            return "combustible_registrarChofer.html";

        } else {

            modelo.put("camion", camion);
            modelo.put("exito", "Comunicarse con su Administrador");

            return "combustible_mensajeKm.html";

        }

    }
    
    @GetMapping("/registrarAdmin")
    public String registrarCargaAdmin(@RequestParam Long idOrg, @RequestParam String desde, @RequestParam String hasta, @RequestParam(required = false) Long idCamion,
            @RequestParam(required = false) Long idChofer, ModelMap modelo) {

            modelo.put("desde", desde);
            modelo.put("hasta", hasta);
            modelo.put("idCamion", idCamion);
            modelo.put("idChofer", idChofer);
        
            if(idCamion != null){
                
                Camion camion = camionServicio.buscarCamion(idCamion);
                
                boolean flag = combustibleServicio.kmIniciales(camion);
                
                if (flag == true) {

            Combustible carga = combustibleServicio.cargaAnterior(camion);

            modelo.put("kmAnterior", carga.getKmCarga());
            modelo.put("fechaAnterior", carga.getFechaCarga());
            modelo.put("camion", camion);
            modelo.addAttribute("choferes", choferServicio.bucarChoferesHabNombreAsc(idOrg));
            modelo.addAttribute("acoplados", acopladoServicio.buscarAcopladosHabAsc(idOrg));
            modelo.addAttribute("camiones", camionServicio.buscarCamionesHabAsc(idOrg));
            if(camion.getAcoplado() != null){
            modelo.put("acoplado", acopladoServicio.buscarAcoplado(camion.getAcoplado().getId()));
            } else {
                modelo.put("acoplado", null);
            }

            return "combustible_registrarAdmin.html";

        } else {

           modelo.put("camion", camion);         
                    
            return "combustible_registrarPrimerCarga";

        }
                
            } else {
        
            modelo.addAttribute("camiones", camionServicio.buscarCamionesHabAsc(idOrg));
            modelo.put("camion", null);
            modelo.put("acoplados", null);
            modelo.put("choferes", null);
            modelo.put("kmAnterior", null);
            modelo.put("fechaAnterior", null);
            modelo.put("acoplado", null);

            return "combustible_registrarAdmin.html";
            
            }

    }

    @GetMapping("/registrarAdminCamion")
    public String registrarCargaAdminCamion(@RequestParam Long camionId, @RequestParam String desde, @RequestParam String hasta, @RequestParam(required = false) Long idCamion,
            @RequestParam(required = false) Long idChofer, ModelMap modelo) {

        Camion camion = camionServicio.buscarCamion(camionId);
        modelo.put("desde", desde);
        modelo.put("hasta", hasta);
        modelo.put("idCamion", idCamion);
        modelo.put("idChofer", idChofer);
        modelo.put("camion", camion);

        boolean flag = combustibleServicio.kmIniciales(camion);

        if (flag == true) {

            Combustible carga = combustibleServicio.cargaAnterior(camion);
            
            if(camion.getAcoplado() != null){
            modelo.put("acoplado", acopladoServicio.buscarAcoplado(camion.getAcoplado().getId()));
            } else {
                modelo.put("acoplado", null);
            }

            modelo.put("kmAnterior", carga.getKmCarga());
            modelo.put("fechaAnterior", carga.getFechaCarga());
            modelo.addAttribute("choferes", choferServicio.bucarChoferesHabNombreAsc(camion.getIdOrg()));
            modelo.addAttribute("acoplados", acopladoServicio.buscarAcopladosHabAsc(camion.getIdOrg()));
            modelo.addAttribute("camiones", camionServicio.buscarCamionesHabAsc(camion.getIdOrg()));

            return "combustible_registrarAdmin.html";

        } else {

            return "combustible_registrarPrimerCarga";

        }

    }

    @PostMapping("/registroPrimerCarga")
    public String registroPrimerCarga(@RequestParam String desde, @RequestParam String hasta, @RequestParam(required = false) Long idCamion,
            @RequestParam(required = false) Long idChofer, @RequestParam Long camion, @RequestParam String fecha,
            @RequestParam Double km, ModelMap modelo, HttpSession session) throws ParseException {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        combustibleServicio.crearPrimerCarga(logueado.getIdOrg(), fecha, km, camion, logueado);

        return "redirect:/combustible/listarAdminFiltroGet?desde=" + desde + "&hasta=" + hasta +
           (idCamion != null ? "&idCamion=" + idCamion : "") +
           (idChofer != null ? "&idChofer=" + idChofer : "") +
                "&registroKM=" + "si";

    }

    @PostMapping("/registro")
    public String registroCarga(@RequestParam Long idCamion, @RequestParam Double kmAnterior, @RequestParam(required = false) Long idAcoplado, @RequestParam String fecha, @RequestParam Double km,
            @RequestParam Double litro, @RequestParam String completo, @RequestParam(required = false) Double azul, ModelMap modelo, HttpSession session) throws ParseException {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        combustibleServicio.crearCarga(logueado.getIdOrg(), idCamion, idAcoplado, fecha, kmAnterior, km, litro, completo, azul, logueado);

        return "redirect:/combustible/registrado/" +logueado.getIdOrg();

    }

    @GetMapping("/registrado/{id}")
    public String registrado(@PathVariable Long id, HttpSession session, ModelMap modelo) {

        modelo.put("carga", combustibleServicio.buscarUltimo(id));
        modelo.put("exito", "Carga de Combustible REGISTRADA con éxito");

        return "combustible_mostrar.html";
    }
    
    @PostMapping("/registroAdmin")
    public String registroCargaAdmin(@RequestParam String desde, @RequestParam String hasta, @RequestParam(required = false) Long idCamion,
            @RequestParam(required = false) Long idChofer, @RequestParam Double kmAnterior, @RequestParam Long camion, @RequestParam String fecha, @RequestParam Long chofer, 
            @RequestParam(required = false) Long idAcoplado, @RequestParam Double km, @RequestParam Double litro, @RequestParam String completo, @RequestParam(required = false) Double azul, 
            ModelMap modelo, HttpSession session) throws ParseException {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        combustibleServicio.crearCargaAdmin(logueado.getIdOrg(), camion, fecha, chofer, idAcoplado, kmAnterior, km, litro, completo, azul, logueado);

        return "redirect:/combustible/registradoAdmin?idOrg=" + logueado.getIdOrg() + "&desde=" + desde + "&hasta=" + hasta +
           (idCamion != null ? "&idCamion=" + idCamion : "") +
           (idChofer != null ? "&idChofer=" + idChofer : "");

    }

    @GetMapping("/registradoAdmin")
    public String registradoAdmin(@RequestParam Long idOrg, @RequestParam String desde, @RequestParam String hasta, @RequestParam(required = false) Long idCamion,
            @RequestParam(required = false) Long idChofer, ModelMap modelo) {

        modelo.put("desde", desde);
        modelo.put("hasta", hasta);
        modelo.put("idCamion", idCamion);
        modelo.put("idChofer", idChofer);
        modelo.put("carga", combustibleServicio.buscarUltimo(idOrg));
        modelo.put("exito", "Carga de Combustible REGISTRADA con éxito");

        return "combustible_mostrarAdmin.html";
    }
    
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @GetMapping("/aceptar")
    public String aceptar(@RequestParam Long idCarga, @RequestParam String desde, @RequestParam String hasta, @RequestParam(required = false) Long idCamion,
            @RequestParam(required = false) Long idChofer, ModelMap modelo, HttpSession session) {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");
        
        combustibleServicio.aceptarCarga(idCarga, logueado);

        return "redirect:/combustible/listarAdminFiltroGet?desde=" + desde + "&hasta=" + hasta +
           (idCamion != null ? "&idCamion=" + idCamion : "") +
           (idChofer != null ? "&idChofer=" + idChofer : "") +
                "&aceptar=" + "si";

    }
    
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @GetMapping("/volverPendiente")
    public String volverPendiente(@RequestParam Long idCarga, @RequestParam String desde, @RequestParam String hasta, @RequestParam(required = false) Long idCamion,
            @RequestParam(required = false) Long idChofer, ModelMap modelo) {
        
        modelo.put("carga", combustibleServicio.buscarCombustible(idCarga));
        modelo.put("desde", desde);
        modelo.put("hasta", hasta);
        modelo.put("idCamion", idCamion);
        modelo.put("idChofer", idChofer);

        return "combustible_volverPendiente.html";

    }
    
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @GetMapping("/pendiente")
    public String pendiente(@RequestParam Long idCarga, @RequestParam String desde, @RequestParam String hasta, @RequestParam(required = false) Long idCamion,
            @RequestParam(required = false) Long idChofer, ModelMap modelo, HttpSession session) {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");
        
        combustibleServicio.volverPendiente(idCarga, logueado);

        return "redirect:/combustible/listarAdminFiltroGet?desde=" + desde + "&hasta=" + hasta +
           (idCamion != null ? "&idCamion=" + idCamion : "") +
           (idChofer != null ? "&idChofer=" + idChofer : "") +
                "&retorna=" + "si";

    }
    
    @GetMapping("/mostrarChofer/{id}")
    public String mostrarChofer(@PathVariable Long id, ModelMap modelo){
        
        modelo.put("carga", combustibleServicio.buscarCombustible(id));

        return "combustible_mostrar.html";
        
    }
    
    @GetMapping("/mostrarAdmin")
    public String mostrarAdmin(@RequestParam Long idCarga, @RequestParam String desde, @RequestParam String hasta, @RequestParam(required = false) Long idCamion,
            @RequestParam(required = false) Long idChofer, ModelMap modelo){
        
        modelo.put("carga", combustibleServicio.buscarCombustible(idCarga));
        modelo.put("desde", desde);
        modelo.put("hasta", hasta);
        modelo.put("idCamion", idCamion);
        modelo.put("idChofer", idChofer);

        return "combustible_mostrarAdmin.html";
        
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
        Camion camion = camionServicio.buscarCamion(id);

        ArrayList<Combustible> cargas = combustibleServicio.buscarCargasIdCamion(id, desde, hasta);
        Double litro = 0.0;
        Double azul = 0.0;
        int registroKm = 0;
        Boolean flag = false;

        if(!cargas.isEmpty()){
        flag = true;
        for (Combustible c : cargas) {
            litro = litro + c.getLitro();
            if(c.getAzul() != null){
            azul = azul + c.getAzul().getLitro();
            }
            if(c.getConsumo() == null){
                registroKm = registroKm + 1;
            }
        }
        }
        
        modelo.put("flag", flag);
        modelo.addAttribute("cargas", cargas);
        modelo.put("desde", desde);
        modelo.put("hasta", hasta);
        modelo.put("idCamion", id);
        modelo.put("idChofer", null);
        modelo.put("camion", camion);
        modelo.put("chofer", null);
        modelo.put("idOrg", camion.getIdOrg());
        modelo.addAttribute("camiones", camionServicio.buscarCamionesHabAsc(camion.getIdOrg()));
        modelo.addAttribute("choferes", choferServicio.bucarChoferesHabNombreAsc(camion.getIdOrg()));
        modelo.put("cantidad", cargas.size() - registroKm);
        modelo.put("litros", litro);
        modelo.put("azul", azul);

        return "combustible_listarCargasAdmin.html";
    }
    
    @GetMapping("/listarAdminIndex")
    public String listarAdminIndex(ModelMap modelo, HttpSession session) throws ParseException {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");
        String desde = obtenerFechaDesde();
        String hasta = obtenerFechaHasta();

        ArrayList<Combustible> cargas = combustibleServicio.buscarCargas(logueado.getIdOrg(), desde, hasta);
        Double litro = 0.0;
        Double azul = 0.0;
        int registroKm = 0;
        Boolean flag = false;

        if(!cargas.isEmpty()){
        flag = true;
        for (Combustible c : cargas) {
            litro = litro + c.getLitro();
            if(c.getAzul() != null){
            azul = azul + c.getAzul().getLitro();
            }
            if(c.getConsumo() == null){
                registroKm = registroKm + 1;
            }
        } 
        }

        modelo.put("flag", flag);
        modelo.addAttribute("cargas", cargas);
        modelo.put("desde", desde);
        modelo.put("hasta", hasta);
        modelo.put("idCamion", null);
        modelo.put("idChofer", null);
        modelo.put("camion", null);
        modelo.put("chofer", null);
        modelo.put("idOrg", logueado.getIdOrg());
        modelo.addAttribute("camiones", camionServicio.buscarCamionesHabAsc(logueado.getIdOrg()));
        modelo.addAttribute("choferes", choferServicio.bucarChoferesHabNombreAsc(logueado.getIdOrg()));
        modelo.put("cantidad", cargas.size() - registroKm);
        modelo.put("litros", litro);
        modelo.put("azul", azul);

        return "combustible_listarCargasAdmin.html";
    }

    @PostMapping("/listarAdminFiltro")
    public String listarAdminFiltro(@RequestParam String desde, @RequestParam String hasta, @RequestParam(required = false) Long idCamion,
           @RequestParam(required = false) Long idChofer, ModelMap modelo, HttpSession session) throws ParseException {
        
        Usuario logueado = (Usuario) session.getAttribute("usuariosession");
        ArrayList<Combustible> cargas = new ArrayList();
        
        if (idCamion != null && idChofer == null) {
        
        cargas = combustibleServicio.buscarCargasIdCamion(idCamion, desde, hasta);
        
        modelo.addAttribute("cargas", cargas);
        modelo.put("camion", camionServicio.buscarCamion(idCamion));
        modelo.put("chofer", null);
        
        } else if (idCamion == null && idChofer != null) {
            
        cargas = combustibleServicio.buscarCargasIdChofer(idChofer, desde, hasta);
        
        modelo.addAttribute("cargas", cargas);
        modelo.put("camion", null);
        modelo.put("chofer", choferServicio.buscarChofer(idChofer));  
            
        } else if (idCamion == null && idChofer == null) {
            
        cargas = combustibleServicio.buscarCargas(logueado.getIdOrg(), desde, hasta);
        
        modelo.addAttribute("cargas", cargas);
        modelo.put("camion", null);
        modelo.put("chofer", null);   
            
        } else {
            
        cargas = combustibleServicio.buscarCargasCamionChofer(idCamion, idChofer, desde, hasta);
        
        modelo.addAttribute("cargas", cargas);
        modelo.put("camion", camionServicio.buscarCamion(idCamion));
        modelo.put("chofer", choferServicio.buscarChofer(idChofer));
            
        }
        
        Double litro = 0.0;
        Double azul = 0.0;
        int registroKm = 0;
        Boolean flag = false;

        if(!cargas.isEmpty()){
        flag = true;
        for (Combustible c : cargas) {
            litro = litro + c.getLitro();
            if(c.getAzul() != null){
            azul = azul + c.getAzul().getLitro();
            }
            if(c.getConsumo() == null){
                registroKm = registroKm + 1;
            }
        }
        }
        
        modelo.put("flag", flag);
        modelo.put("idOrg", logueado.getIdOrg());
        modelo.put("cantidad", cargas.size() - registroKm);
        modelo.put("litros", litro);
        modelo.put("azul", azul); 
        modelo.put("desde", desde);
        modelo.put("hasta", hasta);
        modelo.put("idCamion", idCamion);
        modelo.put("idChofer", idChofer);
        modelo.addAttribute("camiones", camionServicio.buscarCamionesHabAsc(logueado.getIdOrg()));
        modelo.addAttribute("choferes", choferServicio.bucarChoferesHabNombreAsc(logueado.getIdOrg()));

        return "combustible_listarCargasAdmin.html";
    }
    
    @GetMapping("/listarAdminFiltroGet")
    public String listarAdminFiltroGet(@RequestParam String desde, @RequestParam String hasta, @RequestParam(required = false) Long idCamion,
           @RequestParam(required = false) Long idChofer, @RequestParam(required = false) String registroKM, @RequestParam(required = false) String aceptar,
           @RequestParam(required = false) String eliminado, @RequestParam(required = false) String retorna, ModelMap modelo, HttpSession session) throws ParseException {
        
        Usuario logueado = (Usuario) session.getAttribute("usuariosession");
        ArrayList<Combustible> cargas = new ArrayList();
        
        if (idCamion != null && idChofer == null) {
        
        cargas = combustibleServicio.buscarCargasIdCamion(idCamion, desde, hasta);
        
        modelo.addAttribute("cargas", cargas);
        modelo.put("camion", camionServicio.buscarCamion(idCamion));
        modelo.put("chofer", null);
        
        } else if (idCamion == null && idChofer != null) {
            
        cargas = combustibleServicio.buscarCargasIdChofer(idChofer, desde, hasta);
        
        modelo.addAttribute("cargas", cargas);
        modelo.put("camion", null);
        modelo.put("chofer", choferServicio.buscarChofer(idChofer));  
            
        } else if (idCamion == null && idChofer == null) {
            
        cargas = combustibleServicio.buscarCargas(logueado.getIdOrg(), desde, hasta);
        
        modelo.addAttribute("cargas", cargas);
        modelo.put("camion", null);
        modelo.put("chofer", null);   
            
        } else {
            
        cargas = combustibleServicio.buscarCargasCamionChofer(idCamion, idChofer, desde, hasta);
        
        modelo.addAttribute("cargas", cargas);
        modelo.put("camion", camionServicio.buscarCamion(idCamion));
        modelo.put("chofer", choferServicio.buscarChofer(idChofer));
            
        }
        
        Double litro = 0.0;
        Double azul = 0.0;
        int registroKm = 0;
        Boolean flag = false;

        if(!cargas.isEmpty()){
        flag = true;
        for (Combustible c : cargas) {
            litro = litro + c.getLitro();
            if(c.getAzul() != null){
            azul = azul + c.getAzul().getLitro();
            }
            if(c.getConsumo() == null){
                registroKm = registroKm + 1;
            }
        }
        }
        
        modelo.put("flag", flag);
        modelo.put("idOrg", logueado.getIdOrg());
        modelo.put("cantidad", cargas.size() - registroKm);
        modelo.put("litros", litro);
        modelo.put("azul", azul); 
        modelo.put("desde", desde);
        modelo.put("hasta", hasta);
        modelo.put("idCamion", idCamion);
        modelo.put("idChofer", idChofer);
        modelo.addAttribute("camiones", camionServicio.buscarCamionesHabAsc(logueado.getIdOrg()));
        modelo.addAttribute("choferes", choferServicio.bucarChoferesHabNombreAsc(logueado.getIdOrg()));
        if(registroKM != null){
        modelo.put("exito", "KM de Camión REGISTRADO con éxito");
        }
        if(aceptar != null){
        modelo.put("exito", "Carga de Combustible CONFIRMADA con éxito");
        }
        if(eliminado != null){
        modelo.put("exito", "Carga de Combustible ELIMINADA con éxito");
        }
        if(retorna != null){
        modelo.put("exito", "Carga de Combustible RETORNADA a pendiente");
        }

        return "combustible_listarCargasAdmin.html";
    }


    @GetMapping("/mostrarConsumoAdmin/{id}")
    public String mostrarConsumoAdmin(@PathVariable Long id, ModelMap modelo) {

        Boolean flag = false;
        ArrayList<Combustible> cargas = combustibleServicio.buscarCargasCamion(id);
        if(!cargas.isEmpty()){
            flag = true;
        }

        modelo.put("consumo", combustibleServicio.consumoPromedioCamion(id));
        modelo.addAttribute("cargas", cargas);
        modelo.put("camion", camionServicio.buscarCamion(id));
        modelo.put("flag", flag);

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
            modelo.addAttribute("acoplados", acopladoServicio.buscarAcopladosHabAsc(carga.getIdOrg()));

            return "combustible_modificar.html";

        }
    }

    @PostMapping("/modifica/{id}")
    public String modifica(@RequestParam Long id, @RequestParam(required = false) Long idAcoplado, @RequestParam String fecha, @RequestParam Double km,
            @RequestParam Double litro, @RequestParam String completo, @RequestParam(required = false) Double azul, 
            ModelMap modelo, HttpSession session) throws ParseException {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");
        
        combustibleServicio.modificarCarga(id, idAcoplado, fecha, km, litro, completo, azul, logueado);

        return "redirect:/combustible/modificado/" + id;
    }
    
    @GetMapping("/modificado/{id}")
    public String modificado(@PathVariable Long id, ModelMap modelo) {

        modelo.put("carga", combustibleServicio.buscarCombustible(id));
        modelo.put("exito", "Carga de Combustible MODIFICADA con éxito");

        return "combustible_mostrar.html";       

    }
    
    @GetMapping("/modificarAdmin")
    public String modificarAdmin(@RequestParam Long idCarga, @RequestParam String desde, @RequestParam String hasta, @RequestParam(required = false) Long idCamion,
           @RequestParam(required = false) Long idChofer, ModelMap modelo) {

        Combustible carga = combustibleServicio.buscarCombustible(idCarga);
        
        modelo.put("desde", desde);
        modelo.put("hasta", hasta);
        modelo.put("idCamion", idCamion);
        modelo.put("idChofer", idChofer);

        if (carga.getKmAnterior() == null) {

            modelo.put("carga", carga);

            return "combustible_modificarPrimerCarga.html";

        } else {

           Combustible cargaAnterior = combustibleServicio.cargaAnteriorPorId(idCarga, carga.getCamion().getId());

            modelo.put("fechaAnterior", cargaAnterior.getFechaCarga());
            modelo.put("carga", carga);
            modelo.addAttribute("choferes", choferServicio.bucarChoferesHabNombreAsc(carga.getIdOrg()));
            modelo.addAttribute("acoplados", acopladoServicio.buscarAcopladosHabAsc(carga.getIdOrg()));
            modelo.put("flag", combustibleServicio.ultimaCarga(carga.getCamion(), idCarga));

            return "combustible_modificarAdmin.html";

        }
    }

    @PostMapping("/modificaAdmin")
    public String modificaAdmin(@RequestParam String desde, @RequestParam String hasta, @RequestParam(required = false) Long idCamion,
           @RequestParam(required = false) Long idChofer, @RequestParam Long idCarga, @RequestParam String fecha, @RequestParam(required = false) Long idAcoplado, 
           @RequestParam Double km, @RequestParam Double litro, @RequestParam String completo, @RequestParam(required = false) Double azul,
            ModelMap modelo, HttpSession session) throws ParseException {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");
        
        combustibleServicio.modificarCarga(idCarga, idAcoplado, fecha, km, litro, completo, azul, logueado);
        
        return "redirect:/combustible/modificadoAdmin?idCarga=" + idCarga + "&desde=" + desde + "&hasta=" + hasta +
           (idCamion != null ? "&idCamion=" + idCamion : "") +
           (idChofer != null ? "&idChofer=" + idChofer : "");

    }
    
    @GetMapping("/modificadoAdmin")
    public String modificadoAdmin(@RequestParam Long idCarga, @RequestParam String desde, @RequestParam String hasta, @RequestParam(required = false) Long idCamion,
           @RequestParam(required = false) Long idChofer, ModelMap modelo) {

        modelo.put("carga", combustibleServicio.buscarCombustible(idCarga));
        modelo.put("exito", "Carga de Combustible MODIFICADA con éxito");
        modelo.put("desde", desde);
        modelo.put("hasta", hasta);
        modelo.put("idCamion", idCamion);
        modelo.put("idChofer", idChofer);

        return "combustible_mostrarAdmin.html";       

    }

    @PostMapping("/modificaPrimerCarga")
    public String modifica(@RequestParam String desde, @RequestParam String hasta, @RequestParam(required = false) Long idCamion,
           @RequestParam(required = false) Long idChofer, @RequestParam Long idCarga, @RequestParam String fecha, @RequestParam Double km,
            ModelMap modelo) throws ParseException {

        combustibleServicio.modificarPrimerCarga(idCarga, fecha, km);

        return "redirect:/combustible/listarAdminFiltroGet?desde=" + desde + "&hasta=" + hasta +
           (idCamion != null ? "&idCamion=" + idCamion : "") +
           (idChofer != null ? "&idChofer=" + idChofer : "") +
                "&registroKM=" + "si";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id, ModelMap modelo) {

        modelo.put("carga", combustibleServicio.buscarCombustible(id));

        return "combustible_eliminar.html";

    }
    
    @GetMapping("/eliminarAdmin")
    public String eliminarAdmin(@RequestParam Long idCarga, @RequestParam String desde, @RequestParam String hasta, @RequestParam(required = false) Long idCamion,
           @RequestParam(required = false) Long idChofer, ModelMap modelo) {

        modelo.put("carga", combustibleServicio.buscarCombustible(idCarga));
        modelo.put("desde", desde);
        modelo.put("hasta", hasta);
        modelo.put("idCamion", idCamion);
        modelo.put("idChofer", idChofer);

        return "combustible_eliminarAdmin.html";

    }
    
    @GetMapping("/eliminaAdmin")
    public String elimina(@RequestParam Long idCarga, @RequestParam String desde, @RequestParam String hasta, @RequestParam(required = false) Long idCamion,
           @RequestParam(required = false) Long idChofer, ModelMap modelo) throws ParseException {

        combustibleServicio.eliminarCarga(idCarga);

        return "redirect:/combustible/listarAdminFiltroGet?desde=" + desde + "&hasta=" + hasta +
           (idCamion != null ? "&idCamion=" + idCamion : "") +
           (idChofer != null ? "&idChofer=" + idChofer : "") + 
                "&eliminado=" + "si";
    }

    @GetMapping("/elimina/{id}")
    public String elimina(@PathVariable Long id, ModelMap modelo, HttpSession session) throws ParseException {

        combustibleServicio.eliminarCarga(id);

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

            return "redirect:/combustible/eliminadoChofer/" +logueado.getId();

    }
    
    @GetMapping("/eliminadoChofer/{id}")
    public String eliminadoChofer(@PathVariable Long id, ModelMap modelo) throws ParseException {

        String desde = obtenerFechaDesde();
        String hasta = obtenerFechaHasta();
        
        ArrayList<Combustible> cargas = combustibleServicio.buscarCargasIdChofer(id, desde, hasta);

        modelo.put("chofer", choferServicio.buscarChofer(id));
        modelo.put("desde", desde);
        modelo.put("hasta", hasta);
        modelo.addAttribute("cargas", cargas);
        modelo.put("exito", "Carga de Combustible ELIMINADA con éxito");

        return "combustible_listarCargasChofer.html";  

    }
    
    @GetMapping("/eliminadoAdmin")
    public String eliminadoAdmin(ModelMap modelo, HttpSession session) {
        
        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

            modelo.put("exito", "Carga de Combustible ELIMINADA con éxito");
            modelo.put("id", logueado.getId());

            return "index_admin.html";    

    }

    @PostMapping("/exportarCamion")
    public String exportarCamion(@RequestParam String desde, @RequestParam String hasta, @RequestParam(required = false) Long idCamion, 
            @RequestParam(required = false) Long idChofer, ModelMap modelo, HttpSession session) throws ParseException {
    
        Usuario logueado = (Usuario) session.getAttribute("usuariosession");
        ArrayList<Combustible> cargas = new ArrayList();
        
        if (idCamion != null && idChofer == null) {
        
        cargas = combustibleServicio.buscarCargasIdCamionFechaAsc(idCamion, desde, hasta);
        
        } else if (idCamion == null && idChofer != null) {
            
        cargas = combustibleServicio.buscarCargasIdChoferFechaAsc(idChofer, desde, hasta);
  
            
        } else if (idCamion == null && idChofer == null) {
            
        cargas = combustibleServicio.buscarCargasFechaAsc(logueado.getIdOrg(), desde, hasta);
            
        } else {
            
        cargas = combustibleServicio.buscarCargasCamionChoferFechaAsc(idCamion, idChofer, desde, hasta);
            
        }
        
        modelo.put("desde", desde);
        modelo.put("hasta", hasta);
        modelo.put("idCamion", idCamion);
        modelo.put("idChofer", idChofer);
        modelo.addAttribute("cargas", cargas);

        return "combustible_exportarCargas.html";
        
    }

    @PostMapping("/exportaCamion")
    public void exportaCamion(@RequestParam String desde, @RequestParam String hasta, @RequestParam(required = false) Long idCamion, 
            @RequestParam(required = false) Long idChofer, HttpSession session, HttpServletResponse response) throws IOException, ParseException {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");
        ArrayList<Combustible> cargas = new ArrayList();
        
        if (idCamion != null && idChofer == null) {
        
        cargas = combustibleServicio.buscarCargasIdCamionFechaAsc(idCamion, desde, hasta);
        
        } else if (idCamion == null && idChofer != null) {
            
        cargas = combustibleServicio.buscarCargasIdChoferFechaAsc(idChofer, desde, hasta);
  
            
        } else if (idCamion == null && idChofer == null) {
            
        cargas = combustibleServicio.buscarCargasFechaAsc(logueado.getIdOrg(), desde, hasta);
            
        } else {
            
        cargas = combustibleServicio.buscarCargasCamionChoferFechaAsc(idCamion, idChofer, desde, hasta);
            
        }

        String htmlContent = generateHtmlFromObjects(cargas);
        excelServicio.exportHtmlToExcelCombustible(htmlContent, response);

    }
    
    @PostMapping("/exportarConsumo")
    public String exportarConsumo(@RequestParam Long idCamion, ModelMap modelo, HttpSession session) throws ParseException {

        ArrayList<Combustible> cargas = combustibleServicio.buscarCargasCamion(idCamion);
        
        modelo.put("consumo", combustibleServicio.consumoPromedioCamion(idCamion));
        modelo.addAttribute("cargas", cargas);
        modelo.put("camion", camionServicio.buscarCamion(idCamion));

        return "combustible_exportarConsumoAdmin.html";
        
    }

    @PostMapping("/exportaConsumo")
    public void exportaConsumo(@RequestParam Long idCamion, HttpServletResponse response) throws IOException, ParseException {

        ArrayList<Combustible> cargas = combustibleServicio.buscarCargasCamion(idCamion);
        
        Double consumo = combustibleServicio.consumoPromedioCamion(idCamion);
        Camion camion = camionServicio.buscarCamion(idCamion);
        
        String htmlContent = generateHtmlFromObjects(cargas);
        excelServicio.exportHtmlToExcelConsumoCombustible(htmlContent, response, camion, consumo);

    }
    
    @GetMapping("/imprimirAdmin")
    public String imprimirAdmin(@RequestParam String desde, @RequestParam String hasta, @RequestParam(required = false) Long idChofer,
            @RequestParam(required = false) Long idCamion, ModelMap modelo, HttpSession session) throws ParseException {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");
        ArrayList<Combustible> cargas = new ArrayList();

        if (idCamion != null && idChofer == null) {

            cargas = combustibleServicio.buscarCargasIdCamionFechaAsc(idCamion, desde, hasta);

        } else if (idCamion == null && idChofer != null) {

            cargas = combustibleServicio.buscarCargasIdChoferFechaAsc(idChofer, desde, hasta);

        } else if (idCamion == null && idChofer == null) {

            cargas = combustibleServicio.buscarCargasFechaAsc(logueado.getIdOrg(), desde, hasta);

        } else {

            cargas = combustibleServicio.buscarCargasCamionChoferFechaAsc(idCamion, idChofer, desde, hasta);

        }

        modelo.addAttribute("cargas", cargas);
        modelo.put("desde", desde);
        modelo.put("hasta", hasta);

        return "combustible_imprimirAdmin.html";
    }
    
    @GetMapping("/imprimirConsumo")
    public String imprimirConsumo(@RequestParam Long idCamion, ModelMap modelo) throws ParseException {

        ArrayList<Combustible> cargas = combustibleServicio.buscarCargasCamion(idCamion);
        
        modelo.put("consumo", combustibleServicio.consumoPromedioCamion(idCamion));
        modelo.addAttribute("cargas", cargas);
        modelo.put("camion", camionServicio.buscarCamion(idCamion));

        return "combustible_imprimirConsumoAdmin.html";
        
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
                + "<th>Fecha</th>"
                + "<th>Camión</th>"
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
                    + "<td>").append(carga.getChofer().getNombre()).append("</td>"      
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
