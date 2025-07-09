
package abate.abate.controladores;

import abate.abate.entidades.Acoplado;
import abate.abate.entidades.Camion;
import abate.abate.entidades.Mantenimiento;
import abate.abate.entidades.TipoMantenimiento;
import abate.abate.entidades.Usuario;
import abate.abate.servicios.AcopladoServicio;
import abate.abate.servicios.CamionServicio;
import abate.abate.servicios.CombustibleServicio;
import abate.abate.servicios.MantenimientoServicio;
import abate.abate.servicios.TipoMantenimientoServicio;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
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
@RequestMapping("/mantenimiento")
@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_CHOFER')")
public class MantenimientoControlador {
    
    @Autowired
    private MantenimientoServicio mantenimientoServicio;
    @Autowired
    private TipoMantenimientoServicio tipoMantenimientoServicio;
    @Autowired
    private CamionServicio camionServicio;
    @Autowired
    private AcopladoServicio acopladoServicio;
    @Autowired
    private CombustibleServicio combustibleServicio;
    
    @GetMapping("/registrarChofer/{aplicaA}")
    public String registrarChofer(@PathVariable TipoMantenimiento.AplicaA aplicaA, ModelMap modelo, HttpSession session) {
        
        Usuario logueado = (Usuario) session.getAttribute("usuariosession");
        
        modelo.addAttribute("tipos", tipoMantenimientoServicio.buscarTiposAplicaA(logueado.getIdOrg(), aplicaA));
        modelo.put("aplica", aplicaA);
        modelo.put("idOrg", logueado.getIdOrg());

        if(aplicaA == TipoMantenimiento.AplicaA.CAMION){
            
            if(logueado.getCamion() != null){
            
            Camion camion = logueado.getCamion();
            int km = combustibleServicio.kmUltimaCarga(camion);
            modelo.addAttribute("camion", camion);
            modelo.put("km", km);
            modelo.put("kmProximo", km + 10000);
            modelo.put("kmAlarma", km + 9000);
            modelo.put("kmVigencia", 10000);
            } else {
            modelo.addAttribute("camion", null);
            } 
            
            return "mantenimiento_registrarCamionChofer.html";
            
        }  else {
            
            if(logueado.getAcoplado() != null){
                
            Acoplado acoplado = logueado.getAcoplado();
            int km = combustibleServicio.kmAcoplado(acoplado, obtenerFechaFija());    
            modelo.addAttribute("acoplado", acoplado);
            modelo.put("km", km);
            modelo.put("kmProximo", km + 10000);
            modelo.put("kmAlarma", km + 9000);
            modelo.put("kmVigencia", 10000);
            } else {
            modelo.addAttribute("acoplado", null);
            }
            
            return "mantenimiento_registrarAcopladoChofer.html";
        
        }   

    }
    
    @GetMapping("/registrar/{aplicaA}")
    public String registrar(@PathVariable TipoMantenimiento.AplicaA aplicaA, ModelMap modelo, HttpSession session) {
        
        Usuario logueado = (Usuario) session.getAttribute("usuariosession");
        
        modelo.addAttribute("tipos", tipoMantenimientoServicio.buscarTiposAplicaA(logueado.getIdOrg(), aplicaA));
        modelo.addAttribute("aplicaA", TipoMantenimiento.AplicaA.values());
        modelo.put("aplica", aplicaA);
        modelo.put("tipo", null);
        modelo.put("idOrg", logueado.getIdOrg());

        if(aplicaA == TipoMantenimiento.AplicaA.CAMION){
            if(logueado.getRol().equalsIgnoreCase("ADMIN")){
            modelo.addAttribute("camiones", camionServicio.buscarCamionesAsc(logueado.getIdOrg()));
            } else {
            modelo.addAttribute("camiones", logueado.getCamion());
            }
               
            modelo.put("camion", null);
            
            return "mantenimiento_registrarCamion.html";
            
        }  else {
            if(logueado.getRol().equalsIgnoreCase("ADMIN")){
            modelo.addAttribute("acoplados", acopladoServicio.buscarAcopladosAsc(logueado.getIdOrg()));
            } else {
            modelo.addAttribute("acoplados", logueado.getAcoplado());
            }
            
            return "mantenimiento_registrarAcoplado.html";
        }   

    }
    
    @GetMapping("/registrar1")
    public String registrar1(@RequestParam Long idOrg, @RequestParam TipoMantenimiento.AplicaA aplicaA, ModelMap modelo, HttpSession session) {
        
        Usuario logueado = (Usuario) session.getAttribute("usuariosession");
        
        modelo.put("aplica", aplicaA);
        modelo.addAttribute("tipos", tipoMantenimientoServicio.buscarTiposAplicaA(idOrg, aplicaA));
        modelo.addAttribute("aplicaA", TipoMantenimiento.AplicaA.values());
        modelo.put("tipo", null);
        modelo.put("idOrg", idOrg);
        
        if(aplicaA == TipoMantenimiento.AplicaA.CAMION){
            if(logueado.getRol().equalsIgnoreCase("ADMIN")){
            modelo.addAttribute("camiones", camionServicio.buscarCamionesAsc(idOrg));
            } else {
            modelo.addAttribute("camiones", logueado.getCamion());
            }
            modelo.put("camion", null);
            
            return "mantenimiento_registrarCamion.html";
            
        } else {
            if(logueado.getRol().equalsIgnoreCase("ADMIN")){
            modelo.addAttribute("acoplados", acopladoServicio.buscarAcopladosAsc(idOrg));
            } else {
            modelo.addAttribute("acoplados", logueado.getAcoplado());
            }
            
            return "mantenimiento_registrarAcoplado.html";
        } 
    }
    
    @GetMapping("/registrar2")
    public String registrar2(@RequestParam Long idOrg, @RequestParam TipoMantenimiento.AplicaA aplicaA, @RequestParam(required = false) Long idTipo, 
            @RequestParam Long idEntidad,  ModelMap modelo, HttpSession session) {
        
        Usuario logueado = (Usuario) session.getAttribute("usuariosession");
        modelo.addAttribute("tipos", tipoMantenimientoServicio.buscarTiposAplicaA(idOrg, aplicaA));
        if(idTipo != null){
        modelo.put("tipo", tipoMantenimientoServicio.buscarTipo(idTipo));
        } else {
        modelo.put("tipo", null);
        }
        modelo.addAttribute("aplicaA", TipoMantenimiento.AplicaA.values());
        modelo.put("aplica", aplicaA);
        modelo.put("idOrg", idOrg);
        
        if(aplicaA == TipoMantenimiento.AplicaA.CAMION){

            Camion camion = camionServicio.buscarCamion(idEntidad);
            int km = combustibleServicio.kmUltimaCarga(camion);
            
            if(logueado.getRol().equalsIgnoreCase("ADMIN")){
            modelo.addAttribute("camiones", camionServicio.buscarCamionesAsc(idOrg));
            } else {
            modelo.addAttribute("camiones", logueado.getCamion());
            }
            modelo.put("camion", camion);
            modelo.put("km", km);
            modelo.put("kmProximo", km + 10000);
            modelo.put("kmAlarma", km + 9000);
            modelo.put("kmVigencia", 10000);
            
            return "mantenimiento_registrarCamion.html"; 
            
        } else {
            
            Acoplado acoplado = acopladoServicio.buscarAcoplado(idEntidad);
            int km = combustibleServicio.kmAcoplado(acoplado, obtenerFechaFija());
            
            if(logueado.getRol().equalsIgnoreCase("ADMIN")){
            modelo.addAttribute("acoplados", acopladoServicio.buscarAcopladosAsc(idOrg));
            } else {
            modelo.addAttribute("acoplados", logueado.getAcoplado());
            }
            modelo.put("acoplado", acoplado);
            modelo.put("km", km);
            modelo.put("kmProximo", km + 10000);
            modelo.put("kmAlarma", km + 9000);
            modelo.put("kmVigencia", 10000);
           
            return "mantenimiento_registrarAcoplado.html"; 
            
        }

    }   
    
    @PostMapping("/registro")
    public String registro(@RequestParam TipoMantenimiento.AplicaA aplicaA, @RequestParam Long idTipo, @RequestParam Long idEntidad, 
            @RequestParam String observacion, @RequestParam String fecha, @RequestParam Integer km, @RequestParam Integer kmProximo,  
            @RequestParam Integer kmAlarma, ModelMap modelo, HttpSession session) throws ParseException {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");
            
        if(km == null){
            km = 0;
        }
        
        TipoMantenimiento tipo = tipoMantenimientoServicio.buscarTipo(idTipo);
        Date fechaMantenimiento = convertirFecha(fecha);
        String obsMayusculas = observacion.toUpperCase();
        Mantenimiento mantenimiento = new Mantenimiento();
        
        mantenimiento.setTipoMantenimiento(tipo);
        mantenimiento.setAplicaA(aplicaA);
        mantenimiento.setObservacion(obsMayusculas); 
        mantenimiento.setFecha(fechaMantenimiento);
        mantenimiento.setKm(km);
        mantenimiento.setKmProximo(kmProximo);
        mantenimiento.setKmAlarma(kmAlarma);
        mantenimiento.setEstado("VIGENTE");
        mantenimiento.setUsuario(logueado);
        mantenimiento.setIdOrg(logueado.getIdOrg());
        if(aplicaA == TipoMantenimiento.AplicaA.CAMION){
            Camion camion = camionServicio.buscarCamion(idEntidad);
            mantenimiento.setCamion(camion);
        } else {
            Acoplado acoplado = acopladoServicio.buscarAcoplado(idEntidad);
            mantenimiento.setAcoplado(acoplado);
        } 
        
        Mantenimiento buscar = mantenimientoServicio.buscarExistente(mantenimiento);
        
        if (buscar != null) {
            
            modelo.put("mantenimientoExistente", buscar);
            modelo.put("mantenimiento", mantenimiento);
            modelo.put("fecha", fecha);
            
            if(logueado.getRol().equalsIgnoreCase("ADMIN")){
            
            return "mantenimiento_registrarConfirmar.html"; 
            
            } else {
                
            return "mantenimiento_registrarConfirmarChofer.html";
                
            }
            
        } else {

        mantenimientoServicio.crearMantenimiento(mantenimiento, null);
        
        return "redirect:/mantenimiento/registrado";
        
        }   

    }
    
    @PostMapping("/confirmarRegistro")
    public String confirmarRegistro(@RequestParam TipoMantenimiento.AplicaA aplicaA, @RequestParam Long idTipo, @RequestParam Long idEntidad, 
            @RequestParam String observacion, @RequestParam String fecha, @RequestParam(required = false) Integer km, @RequestParam Integer kmProximo,  
            @RequestParam Integer kmAlarma, @RequestParam Long idMantenimientoExistente, ModelMap modelo, HttpSession session) throws ParseException {
        
            Usuario logueado = (Usuario) session.getAttribute("usuariosession");
            
        if(km == null){
            km = 0;
        }
        
        TipoMantenimiento tipo = tipoMantenimientoServicio.buscarTipo(idTipo);
        Date fechaMantenimiento = convertirFecha(fecha);
        String obsMayusculas = observacion.toUpperCase();
        Mantenimiento mantenimiento = new Mantenimiento();
        
        mantenimiento.setTipoMantenimiento(tipo);
        mantenimiento.setAplicaA(aplicaA);
        mantenimiento.setObservacion(obsMayusculas); 
        mantenimiento.setFecha(fechaMantenimiento);
        mantenimiento.setKm(km);
        mantenimiento.setKmProximo(kmProximo);
        mantenimiento.setKmAlarma(kmAlarma);
        mantenimiento.setEstado("VIGENTE");
        mantenimiento.setUsuario(logueado);
        mantenimiento.setIdOrg(logueado.getIdOrg());
        if(aplicaA == TipoMantenimiento.AplicaA.CAMION){
            Camion camion = camionServicio.buscarCamion(idEntidad);
            mantenimiento.setCamion(camion);
        }  else {
            Acoplado acoplado = acopladoServicio.buscarAcoplado(idEntidad);
            mantenimiento.setAcoplado(acoplado);
        } 
        
        Mantenimiento mantenimientoExistente = mantenimientoServicio.buscarMantenimiento(idMantenimientoExistente);

        mantenimientoServicio.crearMantenimiento(mantenimiento, mantenimientoExistente);
        
        return "redirect:/mantenimiento/registrado";
        
    }

    @GetMapping("/registrado")
    public String registrado(ModelMap modelo, HttpSession session) {
        
        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        Long id = mantenimientoServicio.buscarUltimo(logueado.getIdOrg());
        
        modelo.put("mantenimiento", mantenimientoServicio.buscarMantenimientoDiasVigencia(id));
        modelo.put("exito", "Mantenimiento REGISTRADO con éxito");

        if(logueado.getRol().equalsIgnoreCase("ADMIN")){
        
        return "mantenimiento_mostrar.html";
        
        } else { 
            
        return "mantenimiento_mostrarChofer.html";
            
        }
    }
    
    @GetMapping("/modificar/{id}")
    public String modificar(@PathVariable Long id, ModelMap modelo) {
        
        Mantenimiento mantenimiento = mantenimientoServicio.buscarMantenimientoDiasVigencia(id);
        
        modelo.put("mantenimiento", mantenimiento);
        modelo.addAttribute("tipos", tipoMantenimientoServicio.buscarTiposAplicaA(mantenimiento.getIdOrg(), mantenimiento.getAplicaA()));
        modelo.addAttribute("aplicaA", TipoMantenimiento.AplicaA.values());
        
        if(mantenimiento.getAplicaA() != TipoMantenimiento.AplicaA.ACOPLADO){
            
            return "mantenimiento_modificarCamion.html";
            
        } else {
            
            return "mantenimiento_modificarAcoplado.html";
            
        } 

    }

    @PostMapping("/modifica")
    public String modifica(@RequestParam Long id, @RequestParam String observacion, @RequestParam String fecha, @RequestParam(required = false) Integer km, 
            @RequestParam Integer kmProximo, @RequestParam Integer kmAlarma, ModelMap modelo, HttpSession session) throws ParseException {
        
        Usuario logueado = (Usuario) session.getAttribute("usuariosession");
        
        if(km == null){
            km = 0;
        }

        mantenimientoServicio.modificarMantenimiento(id, fecha, km, kmProximo, kmAlarma, observacion, logueado);
            
        return "redirect:/mantenimiento/modificado/" + id;
           
        }

    @GetMapping("/modificado/{id}")
    public String modificado(@PathVariable Long id, ModelMap modelo, HttpSession session) {
        
        Usuario logueado = (Usuario) session.getAttribute("usuariosession");
        
            modelo.put("mantenimiento", mantenimientoServicio.buscarMantenimientoDiasVigencia(id));
            modelo.put("exito", "Mantenimiento MODIFICADO con éxito");
        
        if(logueado.getRol().equalsIgnoreCase("ADMIN")){

            return "mantenimiento_mostrar.html";        
            
        } else {
            
           return "mantenimiento_mostrarChofer.html"; 
           
        }

    }
    
    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id, ModelMap modelo) {

        modelo.put("mantenimiento", mantenimientoServicio.buscarMantenimientoDiasVigencia(id));

        return "mantenimiento_eliminar.html";
    }

    @GetMapping("/elimina/{id}")
    public String elimina(@PathVariable Long id, ModelMap modelo) {

        mantenimientoServicio.eliminarMantenimiento(id);
        
        return "redirect:/mantenimiento/eliminado";
    }
    
    @GetMapping("/eliminado")
    public String eliminado(ModelMap modelo, HttpSession session) {
        
        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        modelo.put("exito", "Mantenimiento ELIMINADO con éxito");
        
        if(logueado.getRol().equalsIgnoreCase("ADMIN")){

        return "mantenimiento_listarAdmin.html";
        
        } else {
            
        return "mantenimiento_listarChofer.html";
            
        }
    }
    
    @GetMapping("/listarChofer")
    public String listarChofer(ModelMap modelo) {
        
        return "mantenimiento_listarChofer.html";
        
    }
    
    @GetMapping("/listarChoferCamiones")
    public String camionChofer(ModelMap modelo, HttpSession session) {
        
        Usuario logueado = (Usuario) session.getAttribute("usuariosession");
        
        Boolean flag = false;
        
        if(logueado.getCamion() != null) {
        if(logueado.getMantenimiento().equalsIgnoreCase("SI")){
            flag = true;
        }

        modelo.put("flag", flag); 
        modelo.put("aplica", TipoMantenimiento.AplicaA.CAMION);
        modelo.put("camion", logueado.getCamion());
        modelo.addAttribute("camiones", camionServicio.buscarCamionesAsc(logueado.getIdOrg()));
        modelo.addAttribute("mantenimientos", mantenimientoServicio.buscarMantenimientoIdCamion(logueado.getCamion().getId()));
        
        return "mantenimiento_listarChoferCamion.html";
            
        } else {

        modelo.put("flag", flag); 
        modelo.put("aplica", TipoMantenimiento.AplicaA.CAMION);
        modelo.addAttribute("camiones", camionServicio.buscarCamionesAsc(logueado.getIdOrg()));
        
        return "mantenimiento_listarChoferCamiones.html";
        
        }
        
    }
    
    @GetMapping("/listarChoferCamion")
    public String listarChoferCamion(@RequestParam Long idCamion, ModelMap modelo, HttpSession session) {
        
        Usuario logueado = (Usuario) session.getAttribute("usuariosession");
        
        Camion camion = camionServicio.buscarCamion(idCamion);
        
        Boolean flag = false;
        if(logueado.getMantenimiento().equalsIgnoreCase("SI") && logueado.getCamion() != null){
            if(logueado.getCamion().getId() == camion.getId()){
            flag = true;
            }
        }
        
        modelo.put("flag", flag); 
        modelo.put("camion", camionServicio.buscarCamion(idCamion));
        modelo.put("aplica", TipoMantenimiento.AplicaA.CAMION);
        modelo.addAttribute("camiones", camionServicio.buscarCamionesAsc(logueado.getIdOrg()));
        modelo.addAttribute("mantenimientos", mantenimientoServicio.buscarMantenimientoIdCamion(idCamion));
        
        return "mantenimiento_listarChoferCamion.html";
        
    }
    
    @GetMapping("/listarHistorialChoferCamion/{id}")
    public String listarHistorialChoferCamion(@PathVariable Long id, ModelMap modelo) {
        
        List<Mantenimiento> mantenimientos = mantenimientoServicio.buscarHistorialCamion(id);
        
        Map<String, List<Mantenimiento>> mantenimientoPorTipo = mantenimientos.stream()
    .sorted(Comparator.comparing(Mantenimiento::getFecha).reversed())
    .collect(Collectors.groupingBy(doc -> doc.getTipoMantenimiento().getNombre()));
        
        modelo.put("mantenimientos", mantenimientoPorTipo);
        modelo.put("camion", camionServicio.buscarCamion(id));
        
        return "mantenimiento_listarChoferHistorialCamion.html";
        
    }
    
    @GetMapping("/listarChoferAcoplados")
    public String acopladosChofer(ModelMap modelo, HttpSession session) {
        
        Usuario logueado = (Usuario) session.getAttribute("usuariosession");
        
        Boolean flag = false;
        
        if(logueado.getAcoplado() != null) {
        if(logueado.getMantenimiento().equalsIgnoreCase("SI")){
            flag = true;   
        }
           
        modelo.put("flag", flag); 
        modelo.put("aplica", TipoMantenimiento.AplicaA.ACOPLADO);
        modelo.put("acoplado", logueado.getAcoplado());
        modelo.addAttribute("acoplados", acopladoServicio.buscarAcopladosAsc(logueado.getIdOrg()));
        modelo.addAttribute("mantenimientos", mantenimientoServicio.buscarMantenimientoIdAcoplado(logueado.getAcoplado().getId()));
        
        return "mantenimiento_listarChoferAcoplado.html";
            
        } else {

        modelo.put("flag", flag); 
        modelo.put("aplica", TipoMantenimiento.AplicaA.ACOPLADO);
        modelo.addAttribute("acoplados", acopladoServicio.buscarAcopladosAsc(logueado.getIdOrg()));
        
        return "mantenimiento_listarChoferAcoplados.html";
        
        }
        
    }
    
    @GetMapping("/listarChoferAcoplado")
    public String listarChoferAcoplado(@RequestParam Long idAcoplado, ModelMap modelo, HttpSession session) {
        
        Usuario logueado = (Usuario) session.getAttribute("usuariosession");
        
        Acoplado acoplado = acopladoServicio.buscarAcoplado(idAcoplado);
        
        Boolean flag = false;
        if(logueado.getMantenimiento().equalsIgnoreCase("SI") && logueado.getAcoplado() != null){
            if(logueado.getAcoplado().getId() == acoplado.getId()){
            flag = true;
            }
        }
        
        modelo.put("flag", flag);
        modelo.put("aplica", TipoMantenimiento.AplicaA.ACOPLADO);
        modelo.put("acoplado", acopladoServicio.buscarAcoplado(idAcoplado));
        modelo.addAttribute("acoplados", acopladoServicio.buscarAcopladosAsc(logueado.getIdOrg()));
        modelo.addAttribute("mantenimientos", mantenimientoServicio.buscarMantenimientoIdAcoplado(idAcoplado));
        
        return "mantenimiento_listarChoferAcoplado.html";
        
    }
    
    @GetMapping("/listarHistorialChoferAcoplado/{id}")
    public String listarHistorialChoferSemi(@PathVariable Long id, ModelMap modelo) {
        
        List<Mantenimiento> mantenimientos = mantenimientoServicio.buscarHistorialAcoplado(id);
        
        Map<String, List<Mantenimiento>> mantenimientoPorTipo = mantenimientos.stream()
    .sorted(Comparator.comparing(Mantenimiento::getFecha).reversed())
    .collect(Collectors.groupingBy(doc -> doc.getTipoMantenimiento().getNombre()));
        
        modelo.put("mantenimientos", mantenimientoPorTipo);
        modelo.put("acoplado", acopladoServicio.buscarAcoplado(id));
        
        return "mantenimiento_listarChoferHistorialAcoplado.html";
        
    }
    
    @GetMapping("/listarAdmin")
    public String listarAdmin() {
        
        return "mantenimiento_listarAdmin.html";
        
    }
    
    @GetMapping("/listarAdminVencimiento")
    public String listarAdminVencimiento(ModelMap modelo, HttpSession session) {
        
        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        int km = 1000;
        
        List<Mantenimiento> mantenimientos = mantenimientoServicio.obtenerMantenimientosPorVencer(logueado.getIdOrg(), km);

        Map<String, List<Mantenimiento>> mantenimientoPorTipo = mantenimientos.stream()
    .sorted(Comparator.comparing(Mantenimiento::getKmVigencia)) 
    .collect(Collectors.groupingBy(doc -> doc.getTipoMantenimiento().getNombre()));
        
        modelo.put("mantenimientos", mantenimientoPorTipo);
        modelo.put("km", km);
        
        return "mantenimiento_listarAdminVencimiento.html";
        
    }
    
    @PostMapping("/listarAdminVencimientoFiltro")
    public String listarAdminVencimientoFiltro(@RequestParam int km, ModelMap modelo, HttpSession session) {
        
        Usuario logueado = (Usuario) session.getAttribute("usuariosession");
        
        List<Mantenimiento> mantenimientos = mantenimientoServicio.obtenerMantenimientosPorVencer(logueado.getIdOrg(), km);

        Map<String, List<Mantenimiento>> mantenimientoPorTipo = mantenimientos.stream()
    .sorted(Comparator.comparing(Mantenimiento::getKmVigencia)) 
    .collect(Collectors.groupingBy(doc -> doc.getTipoMantenimiento().getNombre()));
        
        modelo.put("mantenimientos", mantenimientoPorTipo);
        modelo.put("km", km);
        
        return "mantenimiento_listarAdminVencimiento.html";
        
    }
    
    @GetMapping("/listarAdminCamiones")
    public String listarAdminCamiones(ModelMap modelo, HttpSession session) {
        
        Usuario logueado = (Usuario) session.getAttribute("usuariosession");
        
        List<Mantenimiento> mantenimientos = mantenimientoServicio.buscarMantenimientosCamiones(logueado.getIdOrg());
        
        Map<String, List<Mantenimiento>> mantenimientoPorTipo = mantenimientos.stream()
    .sorted(Comparator.comparing(Mantenimiento::getKmVigencia)) 
    .collect(Collectors.groupingBy(doc -> doc.getTipoMantenimiento().getNombre()));
        
        modelo.put("mantenimientos", mantenimientoPorTipo);
        modelo.put("aplica", TipoMantenimiento.AplicaA.CAMION);
        modelo.addAttribute("camiones", camionServicio.buscarCamionesAsc(logueado.getIdOrg()));
        modelo.put("idCamion", null);
        modelo.put("camion", null);
        
        return "mantenimiento_listarAdminCamiones.html";
        
    }
    
    @PostMapping("/listarAdminCamionFiltro")
    public String listarAdminCamionFiltro(@RequestParam(required = false) Long idCamion, ModelMap modelo, HttpSession session) {
        
        Usuario logueado = (Usuario) session.getAttribute("usuariosession");
        
        if(idCamion != null) {
        
        List<Mantenimiento> mantenimientos = mantenimientoServicio.buscarMantenimientoIdCamion(idCamion);
        
        Map<String, List<Mantenimiento>> mantenimientoPorTipo = mantenimientos.stream()
    .sorted(Comparator.comparing(Mantenimiento::getKmVigencia)) 
    .collect(Collectors.groupingBy(doc -> doc.getTipoMantenimiento().getNombre()));
        
        modelo.put("mantenimientos", mantenimientoPorTipo);
        modelo.put("aplica", TipoMantenimiento.AplicaA.CAMION);
        modelo.addAttribute("camiones", camionServicio.buscarCamionesAsc(logueado.getIdOrg()));
        modelo.put("camion", camionServicio.buscarCamion(idCamion));
        modelo.put("idCamion", idCamion);
        
        } else {
            
        List<Mantenimiento> mantenimientos = mantenimientoServicio.buscarMantenimientosCamiones(logueado.getIdOrg());
        
        Map<String, List<Mantenimiento>> mantenimientoPorTipo = mantenimientos.stream()
    .sorted(Comparator.comparing(Mantenimiento::getKmVigencia)) 
    .collect(Collectors.groupingBy(doc -> doc.getTipoMantenimiento().getNombre()));
        
        modelo.put("mantenimientos", mantenimientoPorTipo);
        modelo.put("aplica", TipoMantenimiento.AplicaA.CAMION);
        modelo.addAttribute("camiones", camionServicio.buscarCamionesAsc(logueado.getIdOrg()));
        modelo.put("camion", null);
        modelo.put("idCamion", null);
            
        }
        
        return "mantenimiento_listarAdminCamiones.html";
        
    }
    
    @GetMapping("/listarAdminCamion")
    public String listarAdminCamion(@RequestParam Long id, @RequestParam(required = false) Long idCamion, ModelMap modelo) {
        
        modelo.put("mantenimiento", mantenimientoServicio.buscarMantenimientoDiasVigencia(id));
        modelo.put("idCamion", idCamion);
        
        return "mantenimiento_listarAdminCamion.html";
        
    }
    
    @GetMapping("/listarAdminAcoplados")
    public String listarAdminSemis(ModelMap modelo, HttpSession session) {
        
        Usuario logueado = (Usuario) session.getAttribute("usuariosession");
        
        List<Mantenimiento> mantenimientos = mantenimientoServicio.buscarMantenimientosAcoplados(logueado.getIdOrg());

        Map<String, List<Mantenimiento>> mantenimientoPorTipo = mantenimientos.stream()
    .sorted(Comparator.comparing(Mantenimiento::getKmVigencia))
    .collect(Collectors.groupingBy(doc -> doc.getTipoMantenimiento().getNombre()));
        
        modelo.put("mantenimientos", mantenimientoPorTipo);
        modelo.put("aplica", TipoMantenimiento.AplicaA.ACOPLADO);
        modelo.addAttribute("acoplados", acopladoServicio.buscarAcopladosAsc(logueado.getIdOrg()));
       modelo.put("idAcoplado", null);
        modelo.put("acoplado", null);
        
        return "mantenimiento_listarAdminAcoplados.html";
        
    }
    
    @PostMapping("/listarAdminAcopladoFiltro")
    public String listarAdminAcopladoFiltro(@RequestParam(required = false) Long idAcoplado, ModelMap modelo, HttpSession session) {
        
        Usuario logueado = (Usuario) session.getAttribute("usuariosession");
        
        if(idAcoplado != null) {
        
        List<Mantenimiento> mantenimientos = mantenimientoServicio.buscarMantenimientoIdAcoplado(idAcoplado);
        
        Map<String, List<Mantenimiento>> mantenimientoPorTipo = mantenimientos.stream()
    .sorted(Comparator.comparing(Mantenimiento::getKmVigencia)) 
    .collect(Collectors.groupingBy(doc -> doc.getTipoMantenimiento().getNombre()));
        
        modelo.put("mantenimientos", mantenimientoPorTipo);
        modelo.put("aplica", TipoMantenimiento.AplicaA.ACOPLADO);
        modelo.addAttribute("acoplados", acopladoServicio.buscarAcopladosAsc(logueado.getIdOrg()));
        modelo.put("acoplado", acopladoServicio.buscarAcoplado(idAcoplado));
        modelo.put("idAcoplado", idAcoplado);
        
        } else {
            
        List<Mantenimiento> mantenimientos = mantenimientoServicio.buscarMantenimientosAcoplados(logueado.getIdOrg());
        
        Map<String, List<Mantenimiento>> mantenimientoPorTipo = mantenimientos.stream()
    .sorted(Comparator.comparing(Mantenimiento::getKmVigencia)) 
    .collect(Collectors.groupingBy(doc -> doc.getTipoMantenimiento().getNombre()));
        
        modelo.put("mantenimientos", mantenimientoPorTipo);
        modelo.put("aplica", TipoMantenimiento.AplicaA.ACOPLADO);
        modelo.addAttribute("acoplados", acopladoServicio.buscarAcopladosAsc(logueado.getIdOrg()));
        modelo.put("acoplado", null);
        modelo.put("idAcoplado", null);
            
        }
        
        return "mantenimiento_listarAdminAcoplados.html";
        
    }
    
    @GetMapping("/listarAdminAcoplado")
    public String listarAdminAcoplado(@RequestParam Long id, @RequestParam(required = false) Long idAcoplado, ModelMap modelo) {
        
        modelo.addAttribute("mantenimiento", mantenimientoServicio.buscarMantenimientoDiasVigencia(id));
        modelo.put("idAcoplado", idAcoplado);
        
        return "mantenimiento_listarAdminAcoplado.html";
        
    }
    
    @GetMapping("/listarHistorialCamionAdmin")
    public String listarHistorialCamionAdmin(@RequestParam Long id, @RequestParam(required = false) Long idCamion, ModelMap modelo) {
        
        List<Mantenimiento> mantenimientos = mantenimientoServicio.buscarHistorialCamion(id);
        
        Map<String, List<Mantenimiento>> mantenimientoPorTipo = mantenimientos.stream()
    .sorted(Comparator.comparing(Mantenimiento::getFecha).reversed())
    .collect(Collectors.groupingBy(doc -> doc.getTipoMantenimiento().getNombre()));
        
        modelo.put("mantenimientos", mantenimientoPorTipo);
        modelo.put("camion", camionServicio.buscarCamion(id));
        modelo.put("idCamion", idCamion);
        
        return "mantenimiento_listarHistorialCamionAdmin.html";
        
    }
    
    @GetMapping("/listarHistorialAcopladoAdmin")
    public String listarHistorialAcopladoAdmin(@RequestParam Long id, @RequestParam(required = false) Long idAcoplado, ModelMap modelo) {
        
        List<Mantenimiento> mantenimientos = mantenimientoServicio.buscarHistorialAcoplado(id);
        
        Map<String, List<Mantenimiento>> mantenimientoPorTipo = mantenimientos.stream()
    .sorted(Comparator.comparing(Mantenimiento::getFecha).reversed())
    .collect(Collectors.groupingBy(doc -> doc.getTipoMantenimiento().getNombre()));
        
        modelo.put("mantenimientos", mantenimientoPorTipo);
        modelo.put("acoplado", acopladoServicio.buscarAcoplado(id));
        modelo.put("idAcoplado", idAcoplado);
        
        return "mantenimiento_listarHistorialAcopladoAdmin.html";
        
    }
    
        public Date convertirFecha(String fecha) throws ParseException {
        SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd");
        return formato.parse(fecha);
    }
        
            
       public static Date obtenerFechaFija() {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            return sdf.parse("01-01-2024");
        } catch (ParseException e) {
            e.printStackTrace();
            return null; // o lanzar una excepción personalizada
        }
    }
    
}
