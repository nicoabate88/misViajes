
package abate.abate.controladores;

import abate.abate.dto.DocumentacionDTO;
import abate.abate.entidades.Acoplado;
import abate.abate.entidades.Camion;
import abate.abate.entidades.Documentacion;
import abate.abate.entidades.TipoDocumentacion;
import abate.abate.entidades.Usuario;
import abate.abate.excepciones.MiException;
import abate.abate.servicios.AcopladoServicio;
import abate.abate.servicios.CamionServicio;
import abate.abate.servicios.ChoferServicio;
import abate.abate.servicios.DocumentacionServicio;
import abate.abate.servicios.ExcelServicio;
import abate.abate.servicios.TipoDocumentacionServicio;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Controller
@RequestMapping("/documentacion")
@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_CHOFER')")
public class DocumentacionControlador {
    
    @Autowired
    private DocumentacionServicio documentacionServicio;
    @Autowired
    private TipoDocumentacionServicio tipoDocumentacionServicio;
    @Autowired
    private CamionServicio camionServicio;
    @Autowired
    private AcopladoServicio acopladoServicio;
    @Autowired
    private ChoferServicio choferServicio;
    @Autowired
    private ExcelServicio excelServicio;
    
    @GetMapping("/registrar/{aplicaA}")
    public String registrar(@PathVariable TipoDocumentacion.AplicaA aplicaA, ModelMap modelo, HttpSession session) {
        
        Usuario logueado = (Usuario) session.getAttribute("usuariosession");
        
        modelo.addAttribute("tipos", tipoDocumentacionServicio.buscarTiposAplicaA(logueado.getIdOrg(), aplicaA));
        modelo.addAttribute("aplicaA", TipoDocumentacion.AplicaA.values());
        modelo.put("aplica", aplicaA);
        modelo.put("idOrg", logueado.getIdOrg());

        if(aplicaA == TipoDocumentacion.AplicaA.CAMION){
            modelo.addAttribute("camiones", camionServicio.buscarCamionesHabAsc(logueado.getIdOrg()));
        } else if(aplicaA == TipoDocumentacion.AplicaA.ACOPLADO){
            modelo.addAttribute("acoplados", acopladoServicio.buscarAcopladosHabAsc(logueado.getIdOrg()));
        } else {
            modelo.addAttribute("choferes", choferServicio.bucarChoferesHabNombreAsc(logueado.getIdOrg()));
        }

        return "documentacion_registrar.html";

    }
    
    @GetMapping("/registrar1")
    public String registrar1(Long idOrg, @RequestParam TipoDocumentacion.AplicaA aplicaA, ModelMap modelo) {
        
        if(aplicaA == TipoDocumentacion.AplicaA.CAMION){
            modelo.addAttribute("camiones", camionServicio.buscarCamionesHabAsc(idOrg));
        } else if(aplicaA == TipoDocumentacion.AplicaA.ACOPLADO){
            modelo.addAttribute("acoplados", acopladoServicio.buscarAcopladosHabAsc(idOrg));
        } else {
            modelo.addAttribute("choferes", choferServicio.bucarChoferesHabNombreAsc(idOrg));
        }
        
        modelo.put("idOrg", idOrg);
        modelo.put("aplica", aplicaA);
        modelo.addAttribute("tipos", tipoDocumentacionServicio.buscarTiposAplicaA(idOrg, aplicaA));
        modelo.addAttribute("aplicaA", TipoDocumentacion.AplicaA.values());

        return "documentacion_registrar.html";

    }
    
    @PostMapping("/registro")
    public String registro(@RequestParam TipoDocumentacion.AplicaA aplicaA, @RequestParam Long idTipo, @RequestParam Long idEntidad, @RequestParam String fechaAlta, 
            @RequestParam String fechaVencimiento, @RequestParam String observacion, ModelMap modelo, HttpSession session) throws ParseException, MiException {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");
        
        try {
            
            documentacionServicio.crearDocumentacion(idTipo, aplicaA, idEntidad, fechaAlta, fechaVencimiento, observacion, logueado);
            
            return "redirect:/documentacion/registrado/ " +logueado.getIdOrg();
            
        } catch (MiException ex) {
            
        if(aplicaA == TipoDocumentacion.AplicaA.CAMION){
            modelo.addAttribute("camiones", camionServicio.buscarCamionesHabAsc(logueado.getIdOrg()));
        } else if(aplicaA == TipoDocumentacion.AplicaA.ACOPLADO){
            modelo.addAttribute("acoplados", acopladoServicio.buscarAcopladosHabAsc(logueado.getIdOrg()));
        } else {
            modelo.addAttribute("choferes", choferServicio.bucarChoferesHabNombreAsc(logueado.getIdOrg()));
        }
        
        modelo.put("idOrg", logueado.getIdOrg());
        modelo.put("tipo", tipoDocumentacionServicio.buscarTipo(idTipo));
        modelo.put("aplica", aplicaA);
        modelo.addAttribute("tipos", tipoDocumentacionServicio.buscarTiposAplicaA(logueado.getIdOrg(), aplicaA));
        modelo.addAttribute("aplicaA", TipoDocumentacion.AplicaA.values());
        modelo.put("fechaAlta", fechaAlta);
        modelo.put("fechaVencimiento", fechaVencimiento);
        modelo.put("obs", observacion);
        modelo.put("error", ex.getMessage());

        return "documentacion_registrar.html";
            
        }    

    }

    @GetMapping("/registrado/{id}")
    public String registrado(@PathVariable Long id, ModelMap modelo) {

        Long idDoc = documentacionServicio.buscarUltimo(id);
        
        modelo.put("documentacion", documentacionServicio.buscarDocumentacionDiasVigencia(idDoc));
        modelo.put("exito", "Documentación REGISTRADA con éxito");

        return "documentacion_mostrar.html";
    }
    
    @GetMapping("/registrarChofer/{aplicaA}")
    public String registrarChofer(@PathVariable TipoDocumentacion.AplicaA aplicaA, ModelMap modelo, HttpSession session) {
        
        Usuario logueado = (Usuario) session.getAttribute("usuariosession");
        
        modelo.addAttribute("tipos", tipoDocumentacionServicio.buscarTiposAplicaA(logueado.getIdOrg(), aplicaA));
        modelo.put("aplica", aplicaA);

        if(aplicaA == TipoDocumentacion.AplicaA.CAMION){
            modelo.addAttribute("camion", logueado.getCamion());
        } else if(aplicaA == TipoDocumentacion.AplicaA.ACOPLADO){
            modelo.addAttribute("acoplado", logueado.getAcoplado());
        } else {
            modelo.addAttribute("chofer", logueado);
        }

        return "documentacion_registrarChofer.html";

    }
    
    @PostMapping("/registroChofer")
    public String registroChofer(@RequestParam TipoDocumentacion.AplicaA aplicaA, @RequestParam Long idTipo, @RequestParam String fechaAlta, 
            @RequestParam String fechaVencimiento, @RequestParam String observacion, ModelMap modelo, HttpSession session) throws ParseException, MiException {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");
        
        Long idEntidad;
        
        try {
            
        if(aplicaA == TipoDocumentacion.AplicaA.CAMION){
            idEntidad = logueado.getCamion().getId();
        } else if(aplicaA == TipoDocumentacion.AplicaA.ACOPLADO){
            idEntidad = logueado.getAcoplado().getId();
        } else {
            idEntidad = logueado.getId();
        }
            
            documentacionServicio.crearDocumentacion(idTipo, aplicaA, idEntidad, fechaAlta, fechaVencimiento, observacion, logueado);
            
            return "redirect:/documentacion/registradoChofer/ " +logueado.getIdOrg();
            
        } catch (MiException ex) {
            
        if(aplicaA == TipoDocumentacion.AplicaA.CAMION){
            modelo.addAttribute("camion", logueado.getCamion());
        } else if(aplicaA == TipoDocumentacion.AplicaA.ACOPLADO){
            modelo.addAttribute("acoplado", logueado.getAcoplado());
        } else {
            modelo.addAttribute("chofer", logueado);
        }
        
        modelo.addAttribute("tipos", tipoDocumentacionServicio.buscarTiposAplicaA(logueado.getIdOrg(), aplicaA));
        modelo.put("aplica", aplicaA);
        modelo.put("fechaAlta", fechaAlta);
        modelo.put("fechaVencimiento", fechaVencimiento);
        modelo.put("obs", observacion);
        modelo.put("error", ex.getMessage());

        return "documentacion_registrarChofer.html";
            
        }    

    }
    
    @GetMapping("/registradoChofer/{id}")
    public String registradoChofer(@PathVariable Long id, ModelMap modelo) {

        Long idDoc = documentacionServicio.buscarUltimo(id);
        
        modelo.put("documentacion", documentacionServicio.buscarDocumentacionDiasVigencia(idDoc));
        modelo.put("exito", "Documentación REGISTRADA con éxito");

        return "documentacion_mostrarChofer.html";
    }
    
    @GetMapping("/modificar/{id}")
    public String modificar(@PathVariable Long id, ModelMap modelo) {
        
        Documentacion documentacion = documentacionServicio.buscarDocumentacion(id);
        
        if(documentacion.getAplicaA() == TipoDocumentacion.AplicaA.CAMION){
            modelo.addAttribute("camiones", camionServicio.buscarCamionesHabAsc(documentacion.getIdOrg()));
        } else if(documentacion.getAplicaA() == TipoDocumentacion.AplicaA.ACOPLADO){
            modelo.addAttribute("acoplados", acopladoServicio.buscarAcopladosHabAsc(documentacion.getIdOrg()));
        } else {
            modelo.addAttribute("choferes", choferServicio.bucarChoferesHabNombreAsc(documentacion.getIdOrg()));
        }
        
            modelo.put("documentacion", documentacion);
            modelo.addAttribute("tipos", tipoDocumentacionServicio.buscarTiposAplicaA(documentacion.getIdOrg(), documentacion.getAplicaA()));
            modelo.addAttribute("aplicaA", TipoDocumentacion.AplicaA.values());

            return "documentacion_modificar.html";

    }
    
    @GetMapping("/modificar1")
    public String modificar1(@RequestParam Long id, @RequestParam TipoDocumentacion.AplicaA aplicaA, ModelMap modelo) throws ParseException {
        
        Documentacion documentacion = documentacionServicio.buscarDocumentacion(id);
        if(aplicaA != documentacion.getAplicaA()){
            documentacion.setAplicaA(aplicaA);
        }
        
        if(documentacion.getAplicaA() == TipoDocumentacion.AplicaA.CAMION){
            modelo.addAttribute("camiones", camionServicio.buscarCamionesHabAsc(documentacion.getIdOrg()));
        } else if(documentacion.getAplicaA() == TipoDocumentacion.AplicaA.ACOPLADO){
            modelo.addAttribute("acoplados", acopladoServicio.buscarAcopladosHabAsc(documentacion.getIdOrg()));
        } else {
            modelo.addAttribute("choferes", choferServicio.bucarChoferesHabNombreAsc(documentacion.getIdOrg()));
        }
        
        modelo.put("documentacion", documentacion);
        modelo.addAttribute("tipos", tipoDocumentacionServicio.buscarTiposAplicaA(documentacion.getIdOrg(), documentacion.getAplicaA()));
        modelo.addAttribute("aplicaA", TipoDocumentacion.AplicaA.values());

        return "documentacion_modificar.html";

    }

    @PostMapping("/modifica")
    public String modifica(@RequestParam Long id, @RequestParam TipoDocumentacion.AplicaA aplicaA, @RequestParam Long idTipo, @RequestParam Long idEntidad,
        @RequestParam String fechaAlta, @RequestParam String fechaVencimiento, @RequestParam String observacion, ModelMap modelo, HttpSession session) throws ParseException {
        
        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        try {
            documentacionServicio.modificarDocumentacion(id, idTipo, aplicaA, idEntidad, fechaAlta, fechaVencimiento, observacion, logueado);
            
            return "redirect:/documentacion/modificado/" + id;
            
        } catch (MiException ex) {
            
        Documentacion documentacion = documentacionServicio.buscarDocumentacion(id);
        if(idTipo != documentacion.getTipoDocumentacion().getId()){
            documentacion.setTipoDocumentacion(tipoDocumentacionServicio.buscarTipo(idTipo));
        } if(aplicaA != documentacion.getAplicaA()){
            documentacion.setAplicaA(aplicaA);
        }
        
        if(documentacion.getAplicaA() == TipoDocumentacion.AplicaA.CAMION){
            modelo.addAttribute("camiones", camionServicio.buscarCamionesHabAsc(documentacion.getIdOrg()));
        } else if(documentacion.getAplicaA() == TipoDocumentacion.AplicaA.ACOPLADO){
            modelo.addAttribute("acoplados", acopladoServicio.buscarAcopladosHabAsc(documentacion.getIdOrg()));
        } else {
            modelo.addAttribute("choferes", choferServicio.bucarChoferesHabNombreAsc(documentacion.getIdOrg()));
        }
        
        modelo.put("documentacion", documentacion);
        modelo.addAttribute("tipos",  tipoDocumentacionServicio.buscarTiposAplicaA(documentacion.getIdOrg(), documentacion.getAplicaA()));
        modelo.addAttribute("aplicaA", TipoDocumentacion.AplicaA.values());
        modelo.put("error", ex.getMessage());
        
        return "documentacion_modificar.html";
           
        }

    }

    @GetMapping("/modificado/{id}")
    public String modificado(@PathVariable Long id, ModelMap modelo) {

            modelo.put("documentacion", documentacionServicio.buscarDocumentacionDiasVigencia(id));
            modelo.put("exito", "Documentación MODIFICADA con éxito");

            return "documentacion_mostrar.html";        

    }
    
    @GetMapping("/modificarChofer/{id}")
    public String modificarChofer(@PathVariable Long id, ModelMap modelo) {
        
        Documentacion documentacion = documentacionServicio.buscarDocumentacion(id);
        
            modelo.put("documentacion", documentacion);
            modelo.addAttribute("tipos", tipoDocumentacionServicio.buscarTiposAplicaA(documentacion.getIdOrg(), documentacion.getAplicaA()));

            return "documentacion_modificarChofer.html";

    }
    
    @PostMapping("/modificaChofer")
    public String modificaChofer(@RequestParam Long id, @RequestParam Long idTipo, @RequestParam String fechaAlta, 
            @RequestParam String fechaVencimiento, @RequestParam String observacion, ModelMap modelo, HttpSession session) throws ParseException {
        
        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        Documentacion documentacion = documentacionServicio.buscarDocumentacion(id);
        
        try {
        
        Long idEntidad;
            
        if(documentacion.getAplicaA() == TipoDocumentacion.AplicaA.CAMION){
            idEntidad = documentacion.getCamion().getId();
        } else if(documentacion.getAplicaA() == TipoDocumentacion.AplicaA.ACOPLADO){
            idEntidad = documentacion.getAcoplado().getId();
        } else {
            idEntidad = documentacion.getChofer().getId();
        }
            
            documentacionServicio.modificarDocumentacion(id, idTipo, documentacion.getAplicaA(), idEntidad, fechaAlta, fechaVencimiento, observacion, logueado);
            
            return "redirect:/documentacion/modificadoChofer/" + id;
            
        } catch (MiException ex) {
            
        modelo.put("documentacion", documentacion);
        modelo.addAttribute("tipos", tipoDocumentacionServicio.buscarTiposAplicaA(documentacion.getIdOrg(), documentacion.getAplicaA()));
        modelo.put("error", ex.getMessage());
        
        return "documentacion_modificarChofer.html";
           
        }

    }
    
    @GetMapping("/modificadoChofer/{id}")
    public String modificadoChofer(@PathVariable Long id, ModelMap modelo) {

            modelo.put("documentacion", documentacionServicio.buscarDocumentacionDiasVigencia(id));
            modelo.put("exito", "Documentación MODIFICADA con éxito");

            return "documentacion_mostrarChofer.html";        

    }
    
    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id,ModelMap modelo) {

        modelo.put("documentacion", documentacionServicio.buscarDocumentacionDiasVigencia(id));

        return "documentacion_eliminar.html";
    }

    @GetMapping("/elimina/{id}")
    public String elimina(@PathVariable Long id, ModelMap modelo) {

        documentacionServicio.eliminarDocumentacion(id);
        
        return "redirect:/documentacion/eliminado";
    }
    
    @GetMapping("/eliminado")
    public String eliminado(ModelMap modelo, HttpSession session) {
        
        Usuario logueado = (Usuario) session.getAttribute("usuariosession");
        
        if(logueado.getRol().equalsIgnoreCase("CHOFER")){
            
            modelo.put("chofer", logueado);
            modelo.put("exito", "Documentación ELIMINADO con éxito");

            return "index_chofer.html";
            
        } else {

        modelo.put("exito", "Documentacion ELIMINADA con éxito");

        return "documentacion_listarAdmin.html";
        
        }
    }
    
    @GetMapping("/listarChofer/{id}")
    public String listarChofer(@PathVariable Long id, ModelMap modelo) {
        
        Usuario chofer = choferServicio.buscarChofer(id);
        Boolean flag = false;
        if(chofer.getDocumentacion().equalsIgnoreCase("SI")){
            flag = true;
        }
        
        modelo.put("flag", flag);
        modelo.addAttribute("documentacion", documentacionServicio.buscarDocumentacionIdChofer(id));
        modelo.put("aplica", TipoDocumentacion.AplicaA.CHOFER);
        
        return "documentacion_listarChofer.html";
        
    }
    
    @GetMapping("/camionChofer")
    public String camionChofer(ModelMap modelo, HttpSession session) {
        
        Usuario logueado = (Usuario) session.getAttribute("usuariosession");
       
        Boolean flag = false;
        if(logueado.getDocumentacion().equalsIgnoreCase("SI") && logueado.getCamion() != null ) {
            flag = true;
        }
        
        if(logueado.getCamion() != null){
            
            modelo.addAttribute("camiones", camionServicio.buscarCamionesHabAsc(logueado.getIdOrg()));
            modelo.put("camion", logueado.getCamion());
            modelo.addAttribute("documentacion", documentacionServicio.buscarDocumentacionIdCamion(logueado.getCamion().getId()));
            modelo.put("flag", flag);
            modelo.put("aplica", TipoDocumentacion.AplicaA.CAMION);
            
            return "documentacion_listarCamionChofer.html";
            
        } else {
        
        modelo.addAttribute("camiones", camionServicio.buscarCamionesHabAsc(logueado.getIdOrg()));
        
        return "documentacion_listarCamionesChofer.html";
        
        }
        
    }
    
    @GetMapping("/listarCamionChofer")
    public String listarCamionChofer(@RequestParam Long idCamion, ModelMap modelo) {
        
        Camion camion = camionServicio.buscarCamion(idCamion);
        
        modelo.put("camion", camion);
        modelo.addAttribute("camiones", camionServicio.buscarCamionesHabAsc(camion.getIdOrg()));
        modelo.addAttribute("documentacion", documentacionServicio.buscarDocumentacionIdCamion(idCamion));
        
        return "documentacion_listarCamionChofer.html";
    }
    
    @GetMapping("/acopladoChofer")
    public String acopladoChofer(ModelMap modelo, HttpSession session) {
        
        Usuario logueado = (Usuario) session.getAttribute("usuariosession");
        
        Boolean flag = false;
        if(logueado.getDocumentacion().equalsIgnoreCase("SI") && logueado.getAcoplado() != null ) {
            flag = true;
        }
        
        if(logueado.getAcoplado() != null){
       
        modelo.put("acoplado", logueado.getAcoplado());
        modelo.addAttribute("acoplados", acopladoServicio.buscarAcopladosHabAsc(logueado.getIdOrg()));
        modelo.addAttribute("documentacion", documentacionServicio.buscarDocumentacionIdAcoplado(logueado.getAcoplado().getId()));
        modelo.put("flag", flag);
        modelo.put("aplica", TipoDocumentacion.AplicaA.ACOPLADO);
        
        return "documentacion_listarAcopladoChofer.html";
        
        } else {
            
            modelo.addAttribute("acoplados", acopladoServicio.buscarAcopladosHabAsc(logueado.getIdOrg()));
            return "documentacion_listarAcopladosChofer.html";
            
        }
        
    }
    
    @GetMapping("/listarAcopladoChofer")
    public String listarAcopladoChofer(@RequestParam Long idAcoplado, ModelMap modelo) {
        
        Acoplado acoplado = acopladoServicio.buscarAcoplado(idAcoplado);
        
        modelo.put("acoplado", acoplado);
        modelo.addAttribute("acoplados", acopladoServicio.buscarAcopladosHabAsc(acoplado.getIdOrg()));
        modelo.addAttribute("documentacion", documentacionServicio.buscarDocumentacionIdAcoplado(idAcoplado));
        
        return "documentacion_listarAcopladoChofer.html";
    }
    
    @GetMapping("/listarAdmin")
    public String listarAdmin(@RequestParam(required = false) String mensaje, ModelMap modelo) {
        
        if(mensaje != null){
        if(mensaje.equalsIgnoreCase("exito")){
        modelo.put("exito", "Las imágenes se han cargado correctamente.");
        } else if(mensaje.equalsIgnoreCase("error")){
        modelo.put("error", "Ocurrió un error al procesar las imagenes. No han sido cargadas.");
        }
        }
        
        return "documentacion_listarAdmin.html";
        
    }
    
    @GetMapping("/listarAdminVencimiento")
    public String listarAdminVencimiento(ModelMap modelo, HttpSession session) {
        
        Usuario logueado = (Usuario) session.getAttribute("usuariosession");
        int dias = 30;
        Boolean flag = false;
        
        List<Documentacion> documentaciones = documentacionServicio.obtenerDocumentacionesPorVencer(logueado.getIdOrg(), dias);

        Map<String, List<Documentacion>> documentacionPorTipo = documentaciones.stream()
    .sorted(Comparator.comparing(Documentacion::getDiasVigencia)) 
    .collect(Collectors.groupingBy(doc -> doc.getTipoDocumentacion().getNombre()));
        
        if(!documentaciones.isEmpty()){
            flag = true;
        }
        
        modelo.put("documentacion", documentacionPorTipo);
        modelo.put("dias", dias);
        modelo.put("flag", flag);
        
        return "documentacion_listarAdminVencimiento.html";
        
    }
    
    @PostMapping("/listarAdminVencimientoFiltro")
    public String listarAdminVencimientoFiltro(@RequestParam int dias, ModelMap modelo, HttpSession session) {
        
        Usuario logueado = (Usuario) session.getAttribute("usuariosession");
        Boolean flag = false;

        List<Documentacion> documentaciones = documentacionServicio.obtenerDocumentacionesPorVencer(logueado.getIdOrg(), dias);

        Map<String, List<Documentacion>> documentacionPorTipo = documentaciones.stream()
                .sorted(Comparator.comparing(Documentacion::getDiasVigencia))
                .collect(Collectors.groupingBy(doc -> doc.getTipoDocumentacion().getNombre()));

        if (!documentaciones.isEmpty()) {
            flag = true;
        }

        modelo.put("documentacion", documentacionPorTipo);
        modelo.put("dias", dias);
        modelo.put("flag", flag);

        return "documentacion_listarAdminVencimiento.html";

    }

    @GetMapping("/listarAdminCamiones")
    public String listarAdminCamiones(ModelMap modelo, HttpSession session) {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        Boolean flag = false;

        List<Documentacion> documentaciones = new ArrayList();

        Map<String, List<Documentacion>> documentacionPorTipo = documentaciones.stream()
                .sorted(Comparator.comparing(Documentacion::getDiasVigencia))
                .collect(Collectors.groupingBy(doc -> doc.getTipoDocumentacion().getNombre()));

        modelo.put("documentacion", documentacionPorTipo);
        modelo.put("aplica", TipoDocumentacion.AplicaA.CAMION);
        modelo.addAttribute("tipos", tipoDocumentacionServicio.buscarTiposAplicaA(logueado.getIdOrg(), TipoDocumentacion.AplicaA.CAMION));
        modelo.addAttribute("camiones", camionServicio.buscarCamionesHabAsc(logueado.getIdOrg()));
        modelo.put("camion", null);
        modelo.put("tipo", null);
        modelo.put("idCamion", null);
        modelo.put("idTipo", null);
        modelo.put("flag", flag);
        modelo.put("seleccioneC", "seleccione");
        modelo.put("seleccioneT", "seleccione");

        return "documentacion_listarAdminCamiones.html";

    }

    @PostMapping("/listarAdminCamionesFiltro")
    public String listarAdminCamionesFiltro(@RequestParam(required = false) Long idCamion, @RequestParam(required = false) Long idTipo, ModelMap modelo, HttpSession session) {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");
        Boolean flag = false;
        List<Documentacion> documentaciones = new ArrayList();

        if (idCamion != null && idTipo == null) {

            documentaciones = documentacionServicio.buscarDocumentacionIdCamion(idCamion);

            Map<String, List<Documentacion>> documentacionesPorTipo = documentaciones.stream()
                    .sorted(Comparator.comparing(Documentacion::getDiasVigencia))
                    .collect(Collectors.groupingBy(doc -> doc.getTipoDocumentacion().getNombre()));
            if (!documentaciones.isEmpty()) {
                flag = true;
            }

            modelo.put("documentacion", documentacionesPorTipo);
            modelo.put("camion", camionServicio.buscarCamion(idCamion));
            modelo.put("tipo", null);

        } else if (idCamion == null && idTipo != null) {

            documentaciones = documentacionServicio.buscarDocumentacionCamionesIdTipo(logueado.getIdOrg(), idTipo);

            Map<String, List<Documentacion>> documentacionesPorTipo = documentaciones.stream()
                    .sorted(Comparator.comparing(Documentacion::getDiasVigencia))
                    .collect(Collectors.groupingBy(doc -> doc.getTipoDocumentacion().getNombre()));
            if (!documentaciones.isEmpty()) {
                flag = true;
            }

            modelo.put("documentacion", documentacionesPorTipo);
            modelo.put("camion", null);
            modelo.put("tipo", tipoDocumentacionServicio.buscarTipo(idTipo));

        } else if (idCamion != null && idTipo != null) {

            documentaciones = documentacionServicio.buscarDocumentacionCamionIdTipo(idCamion, idTipo);

            Map<String, List<Documentacion>> documentacionesPorTipo = documentaciones.stream()
                    .sorted(Comparator.comparing(Documentacion::getDiasVigencia))
                    .collect(Collectors.groupingBy(doc -> doc.getTipoDocumentacion().getNombre()));
            if (!documentaciones.isEmpty()) {
                flag = true;
            }

            modelo.put("documentacion", documentacionesPorTipo);
            modelo.put("camion", camionServicio.buscarCamion(idCamion));
            modelo.put("tipo", tipoDocumentacionServicio.buscarTipo(idTipo));

        } else {

            documentaciones = documentacionServicio.buscarDocumentacionCamiones(logueado.getIdOrg());

            Map<String, List<Documentacion>> documentacionesPorTipo = documentaciones.stream()
                    .sorted(Comparator.comparing(Documentacion::getDiasVigencia))
                    .collect(Collectors.groupingBy(doc -> doc.getTipoDocumentacion().getNombre()));
            if (!documentaciones.isEmpty()) {
                flag = true;
            }

            modelo.put("documentacion", documentacionesPorTipo);
            modelo.put("camion", null);
            modelo.put("tipo", null);
            modelo.put("seleccioneC", null);
            modelo.put("seleccioneT", null);

        }

        modelo.put("aplica", TipoDocumentacion.AplicaA.CAMION);
        modelo.addAttribute("camiones", camionServicio.buscarCamionesHabAsc(logueado.getIdOrg()));
        modelo.addAttribute("tipos", tipoDocumentacionServicio.buscarTiposAplicaA(logueado.getIdOrg(), TipoDocumentacion.AplicaA.CAMION));
        modelo.put("flag", flag);
        modelo.put("idCamion", idCamion);
        modelo.put("idTipo", idTipo);

        return "documentacion_listarAdminCamiones.html";

    }
    
    @GetMapping("/listarAdminCamion")
    public String listarAdminCamion(@RequestParam Long id, @RequestParam(required = false) Long idCamion, ModelMap modelo) {
        
        modelo.addAttribute("documentacion", documentacionServicio.buscarDocumentacionDiasVigencia(id));
        modelo.put("idCamion", idCamion);
        
        return "documentacion_listarAdminCamion.html";
        
    }
    
    @GetMapping("/listarAdminAcoplados")
    public String listarAdminAcoplados(ModelMap modelo, HttpSession session) {
        
        Usuario logueado = (Usuario) session.getAttribute("usuariosession");
        Boolean flag = false;
        
        List<Documentacion> documentaciones = new ArrayList();

        Map<String, List<Documentacion>> documentacionPorTipo = documentaciones.stream()
    .sorted(Comparator.comparing(Documentacion::getDiasVigencia))
    .collect(Collectors.groupingBy(doc -> doc.getTipoDocumentacion().getNombre()));
        
        modelo.put("documentacion", documentacionPorTipo);
        modelo.put("aplica", TipoDocumentacion.AplicaA.ACOPLADO);
        modelo.addAttribute("acoplados", acopladoServicio.buscarAcopladosHabAsc(logueado.getIdOrg()));
        modelo.addAttribute("tipos", tipoDocumentacionServicio.buscarTiposAplicaA(logueado.getIdOrg(), TipoDocumentacion.AplicaA.ACOPLADO));
        modelo.put("acoplado", null);
        modelo.put("idAcoplado", null);
        modelo.put("idTipo", null);
        modelo.put("tipo", null);
        modelo.put("flag", flag);
        modelo.put("seleccioneA", "seleccione");
        modelo.put("seleccioneT", "seleccione");
        
        return "documentacion_listarAdminAcoplados.html";
        
    }
    
    @PostMapping("/listarAdminAcopladosFiltro")
    public String listarAdminAcopladosFiltro(@RequestParam(required = false) Long idAcoplado, @RequestParam(required = false) Long idTipo, ModelMap modelo, HttpSession session) {
        
       Usuario logueado = (Usuario) session.getAttribute("usuariosession");
        Boolean flag = false;
        List<Documentacion> documentaciones = new ArrayList();

        if (idAcoplado != null && idTipo == null) {

            documentaciones = documentacionServicio.buscarDocumentacionIdAcoplado(idAcoplado);

            Map<String, List<Documentacion>> documentacionesPorTipo = documentaciones.stream()
                    .sorted(Comparator.comparing(Documentacion::getDiasVigencia))
                    .collect(Collectors.groupingBy(doc -> doc.getTipoDocumentacion().getNombre()));
            if (!documentaciones.isEmpty()) {
                flag = true;
            }

            modelo.put("documentacion", documentacionesPorTipo);
            modelo.put("acoplado", acopladoServicio.buscarAcoplado(idAcoplado));
            modelo.put("tipo", null);

        } else if (idAcoplado == null && idTipo != null) {

            documentaciones = documentacionServicio.buscarDocumentacionAcopladosIdTipo(logueado.getIdOrg(), idTipo);

            Map<String, List<Documentacion>> documentacionesPorTipo = documentaciones.stream()
                    .sorted(Comparator.comparing(Documentacion::getDiasVigencia))
                    .collect(Collectors.groupingBy(doc -> doc.getTipoDocumentacion().getNombre()));
            if (!documentaciones.isEmpty()) {
                flag = true;
            }

            modelo.put("documentacion", documentacionesPorTipo);
            modelo.put("acoplado", null);
            modelo.put("tipo", tipoDocumentacionServicio.buscarTipo(idTipo));

        } else if (idAcoplado != null && idTipo != null) {

            documentaciones = documentacionServicio.buscarDocumentacionAcopladoIdTipo(idAcoplado, idTipo);

            Map<String, List<Documentacion>> documentacionesPorTipo = documentaciones.stream()
                    .sorted(Comparator.comparing(Documentacion::getDiasVigencia))
                    .collect(Collectors.groupingBy(doc -> doc.getTipoDocumentacion().getNombre()));
            if (!documentaciones.isEmpty()) {
                flag = true;
            }

            modelo.put("documentacion", documentacionesPorTipo);
            modelo.put("acoplado", acopladoServicio.buscarAcoplado(idAcoplado));
            modelo.put("tipo", tipoDocumentacionServicio.buscarTipo(idTipo));

        } else {

            documentaciones = documentacionServicio.buscarDocumentacionAcoplados(logueado.getIdOrg());

            Map<String, List<Documentacion>> documentacionesPorTipo = documentaciones.stream()
                    .sorted(Comparator.comparing(Documentacion::getDiasVigencia))
                    .collect(Collectors.groupingBy(doc -> doc.getTipoDocumentacion().getNombre()));
            if (!documentaciones.isEmpty()) {
                flag = true;
            }

            modelo.put("documentacion", documentacionesPorTipo);
            modelo.put("acoplado", null);
            modelo.put("tipo", null);
            modelo.put("seleccioneA", null);
            modelo.put("seleccioneT", null);

        }

        modelo.put("aplica", TipoDocumentacion.AplicaA.ACOPLADO);
        modelo.addAttribute("acoplados", acopladoServicio.buscarAcopladosHabAsc(logueado.getIdOrg()));
        modelo.addAttribute("tipos", tipoDocumentacionServicio.buscarTiposAplicaA(logueado.getIdOrg(), TipoDocumentacion.AplicaA.ACOPLADO));
        modelo.put("flag", flag);
        modelo.put("idAcoplado", idAcoplado);
        modelo.put("idTipo", idTipo);
        
        return "documentacion_listarAdminAcoplados.html";
        
    }
    
    @GetMapping("/listarAdminAcoplado")
    public String listarAdminAcoplado(@RequestParam Long id, @RequestParam(required = false) Long idAcoplado, ModelMap modelo) {
        
        modelo.addAttribute("documentacion", documentacionServicio.buscarDocumentacionDiasVigencia(id));
        modelo.put("idAcoplado", idAcoplado);
        
        return "documentacion_listarAdminAcoplado.html";
        
    }
    
    @GetMapping("/listarAdminChoferes")
    public String listarAdminChoferes(ModelMap modelo, HttpSession session) {
        
        Usuario logueado = (Usuario) session.getAttribute("usuariosession");
        Boolean flag = false;
        
        List<Documentacion> documentaciones = new ArrayList();

        Map<String, List<Documentacion>> documentacionPorTipo = documentaciones.stream()
    .sorted(Comparator.comparing(Documentacion::getDiasVigencia))
    .collect(Collectors.groupingBy(doc -> doc.getTipoDocumentacion().getNombre()));

        modelo.put("documentacion", documentacionPorTipo);
        modelo.put("aplica", TipoDocumentacion.AplicaA.CHOFER);
        modelo.addAttribute("tipos", tipoDocumentacionServicio.buscarTiposAplicaA(logueado.getIdOrg(), TipoDocumentacion.AplicaA.CHOFER));
        modelo.addAttribute("choferes", choferServicio.bucarChoferesHabNombreAsc(logueado.getIdOrg()));
        modelo.put("chofer", null);
        modelo.put("idChofer", null);
        modelo.put("idTipo", null);
        modelo.put("tipo", null);
        modelo.put("flag", flag);
        modelo.put("seleccioneC", "seleccione");
        modelo.put("seleccioneT", "seleccione");
        
        return "documentacion_listarAdminChoferes.html";
        
    }
    
    @PostMapping("/listarAdminChoferesFiltro")
    public String listarAdminChoferesFiltro(@RequestParam(required = false) Long idChofer, @RequestParam(required = false) Long idTipo, ModelMap modelo, HttpSession session) {
        
        Usuario logueado = (Usuario) session.getAttribute("usuariosession");
        Boolean flag = false;
        List<Documentacion> documentaciones = new ArrayList();

        if (idChofer != null && idTipo == null) {

            documentaciones = documentacionServicio.buscarDocumentacionIdChofer(idChofer);

            Map<String, List<Documentacion>> documentacionesPorTipo = documentaciones.stream()
                    .sorted(Comparator.comparing(Documentacion::getDiasVigencia))
                    .collect(Collectors.groupingBy(doc -> doc.getTipoDocumentacion().getNombre()));
            if (!documentaciones.isEmpty()) {
                flag = true;
            }

            modelo.put("documentacion", documentacionesPorTipo);
            modelo.put("chofer", choferServicio.buscarChofer(idChofer));
            modelo.put("tipo", null);

        } else if (idChofer == null && idTipo != null) {

            documentaciones = documentacionServicio.buscarDocumentacionChoferesIdTipo(logueado.getIdOrg(), idTipo);

            Map<String, List<Documentacion>> documentacionesPorTipo = documentaciones.stream()
                    .sorted(Comparator.comparing(Documentacion::getDiasVigencia))
                    .collect(Collectors.groupingBy(doc -> doc.getTipoDocumentacion().getNombre()));
            if (!documentaciones.isEmpty()) {
                flag = true;
            }

            modelo.put("documentacion", documentacionesPorTipo);
            modelo.put("chofer", null);
            modelo.put("tipo", tipoDocumentacionServicio.buscarTipo(idTipo));

        } else if (idChofer != null && idTipo != null) {

            documentaciones = documentacionServicio.buscarDocumentacionChoferIdTipo(idChofer, idTipo);

            Map<String, List<Documentacion>> documentacionesPorTipo = documentaciones.stream()
                    .sorted(Comparator.comparing(Documentacion::getDiasVigencia))
                    .collect(Collectors.groupingBy(doc -> doc.getTipoDocumentacion().getNombre()));
            if (!documentaciones.isEmpty()) {
                flag = true;
            }

            modelo.put("documentacion", documentacionesPorTipo);
            modelo.put("chofer", choferServicio.buscarChofer(idChofer));
            modelo.put("tipo", tipoDocumentacionServicio.buscarTipo(idTipo));

        } else {

            documentaciones = documentacionServicio.buscarDocumentacionChoferes(logueado.getIdOrg());

            Map<String, List<Documentacion>> documentacionesPorTipo = documentaciones.stream()
                    .sorted(Comparator.comparing(Documentacion::getDiasVigencia))
                    .collect(Collectors.groupingBy(doc -> doc.getTipoDocumentacion().getNombre()));
            if (!documentaciones.isEmpty()) {
                flag = true;
            }

            modelo.put("documentacion", documentacionesPorTipo);
            modelo.put("chofer", null);
            modelo.put("tipo", null);
            modelo.put("seleccioneC", null);
            modelo.put("seleccioneT", null);

        }

        modelo.put("aplica", TipoDocumentacion.AplicaA.CHOFER);
        modelo.addAttribute("choferes", choferServicio.bucarChoferesHabNombreAsc(logueado.getIdOrg()));
        modelo.addAttribute("tipos", tipoDocumentacionServicio.buscarTiposAplicaA(logueado.getIdOrg(), TipoDocumentacion.AplicaA.CHOFER));
        modelo.put("flag", flag);
        modelo.put("idChofer", idChofer);
        modelo.put("idTipo", idTipo);
        
        return "documentacion_listarAdminChoferes.html";
        
    }
    
    @GetMapping("/listarAdminChofer")
    public String listarAdminChofer(@RequestParam Long id, @RequestParam(required = false) Long idChofer, ModelMap modelo) {
        
        modelo.addAttribute("documentacion", documentacionServicio.buscarDocumentacionDiasVigencia(id));
        modelo.put("idChofer", idChofer);
        
        return "documentacion_listarAdminChofer.html";
        
    }
    
    @GetMapping("/registrarMasivo")
    public String registrarMasivo(ModelMap modelo, HttpSession session) {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        modelo.addAttribute("camiones", camionServicio.buscarCamionesHabAsc(logueado.getIdOrg()));
        modelo.addAttribute("tiposCamion", tipoDocumentacionServicio.buscarTiposAplicaA(logueado.getIdOrg(), TipoDocumentacion.AplicaA.CAMION));
        
        return "documentacion_registrarMasivo.html"; 
    }
    
    @PostMapping("/registroMasivo")
    public String registroMasivo(@RequestParam("documentacionCamionJson") String documentacionCamionJson, @RequestParam Long idCamion,
    ModelMap modelo, RedirectAttributes redirectAttributes, HttpSession session) throws JsonProcessingException, ParseException {
    
    Usuario logueado = (Usuario) session.getAttribute("usuariosession");
    ObjectMapper mapper = new ObjectMapper();
    
    List<DocumentacionDTO> documentacionCamionDTO = Arrays.asList(mapper.readValue(documentacionCamionJson, DocumentacionDTO[].class));
    
    List<Documentacion> documentacionCamion = new ArrayList<>();
    for (DocumentacionDTO dto : documentacionCamionDTO) {
    Documentacion d = new Documentacion();
    TipoDocumentacion tipo = tipoDocumentacionServicio.buscarTipo(dto.getTipoDocumentacion());
    tipo.setId(dto.getTipoDocumentacion());
    String obsMayusculas = dto.getObservacion().toUpperCase();
    d.setTipoDocumentacion(tipo);
    d.setAplicaA(TipoDocumentacion.AplicaA.valueOf(dto.getAplicaA()));
    d.setFechaAlta(dto.getFechaAlta());
    d.setFechaVencimiento(dto.getFechaVencimiento());
    d.setObservacion(obsMayusculas);
    documentacionCamion.add(d);
    }

    List<Documentacion> documentacionTotales = new ArrayList<>();
    documentacionTotales.addAll(documentacionCamion);
    List<Documentacion> documentacionNuevoCamion = new ArrayList<>();
    
    for (Documentacion d : documentacionTotales) {
        d.setEstado("VIGENTE");
        d.setIdOrg(logueado.getIdOrg());
        d.setUsuario(logueado);
            Camion camion = camionServicio.buscarCamion(idCamion);
            d.setCamion(camion);
            
        d = documentacionServicio.crearDocumentacionMasivo(d);

        documentacionNuevoCamion.add(d);        

    }
    
        redirectAttributes.addFlashAttribute("documentacionC", documentacionNuevoCamion);
    
        return "redirect:/documentacion/registradoMasivo";
        
    }
    
    @GetMapping("/registradoMasivo")
    public String registradoMasivo(@ModelAttribute("documentacionC") List<Documentacion> documentacionC, ModelMap modelo) {

         modelo.put("exito", "La carga de Documentación se ha REGISTRADO con éxito");
         modelo.addAttribute("documentacionC", documentacionC);
        
        return "documentacion_mostrarMasivo.html";
    }
    
    @GetMapping("/registrarMasivoA")
    public String registrarMasivoA(ModelMap modelo, HttpSession session) {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        modelo.addAttribute("acoplados", acopladoServicio.buscarAcopladosHabAsc(logueado.getIdOrg()));
        modelo.addAttribute("tiposAcoplado", tipoDocumentacionServicio.buscarTiposAplicaA(logueado.getIdOrg(), TipoDocumentacion.AplicaA.ACOPLADO));
        
        return "documentacion_registrarMasivoAcoplado.html"; 
    }
    
    @PostMapping("/registroMasivoA")
    public String registroMasivoA(@RequestParam("documentacionAcopladoJson") String documentacionAcopladoJson, @RequestParam(required = false) Long idAcoplado,
    ModelMap modelo, RedirectAttributes redirectAttributes, HttpSession session) throws JsonProcessingException, ParseException {
    
    Usuario logueado = (Usuario) session.getAttribute("usuariosession");
    ObjectMapper mapper = new ObjectMapper();

    List<DocumentacionDTO> documentacionAcopladoDTO = Arrays.asList(mapper.readValue(documentacionAcopladoJson, DocumentacionDTO[].class));
    
    List<Documentacion> documentacionAcoplado = new ArrayList<>();
    for (DocumentacionDTO dto : documentacionAcopladoDTO) {
    Documentacion d = new Documentacion();
    TipoDocumentacion tipo = tipoDocumentacionServicio.buscarTipo(dto.getTipoDocumentacion());
    tipo.setId(dto.getTipoDocumentacion());
    String obsMayusculas = dto.getObservacion().toUpperCase();
    d.setTipoDocumentacion(tipo);
    d.setAplicaA(TipoDocumentacion.AplicaA.valueOf(dto.getAplicaA()));
    d.setFechaAlta(dto.getFechaAlta());
    d.setFechaVencimiento(dto.getFechaVencimiento());
    d.setObservacion(obsMayusculas);
    documentacionAcoplado.add(d);
    }


    List<Documentacion> documentacionTotales = new ArrayList<>();
    documentacionTotales.addAll(documentacionAcoplado);
    List<Documentacion> documentacionNuevoAcoplado = new ArrayList<>();
    
    for (Documentacion d : documentacionTotales) {
        d.setEstado("VIGENTE");
        d.setIdOrg(logueado.getIdOrg());
        d.setUsuario(logueado);
            Acoplado acoplado = acopladoServicio.buscarAcoplado(idAcoplado);
            d.setAcoplado(acoplado);
            
        d = documentacionServicio.crearDocumentacionMasivo(d);
        documentacionNuevoAcoplado.add(d);
        }

    redirectAttributes.addFlashAttribute("documentacionA", documentacionNuevoAcoplado);

    return "redirect:/documentacion/registradoMasivoA";

    }
    
    @GetMapping("/registradoMasivoA")
        public String registradoMasivoA(@ModelAttribute("documentacionA") List<Documentacion> documentacionA, ModelMap modelo) {

         modelo.put("exito", "La carga de Documentación se ha REGISTRADO con éxito");
         modelo.addAttribute("documentacionA", documentacionA);
        
        return "documentacion_mostrarMasivoAcoplado.html";
    }
    
    
    @GetMapping("/registrarMasivoTipo")
    public String registrarMasivoTipo(ModelMap modelo, HttpSession session) {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        modelo.addAttribute("camiones", camionServicio.buscarCamionesHabAsc(logueado.getIdOrg()));
        modelo.addAttribute("tiposCamion", tipoDocumentacionServicio.buscarTiposAplicaA(logueado.getIdOrg(), TipoDocumentacion.AplicaA.CAMION));
        
        return "documentacion_registrarMasivoTipo.html"; 
    }
    
    @PostMapping("/registroMasivoTipo")
    public String registroMasivoTipo(@RequestParam(required = false) Long tipoCamion,
            @RequestParam("documentacionCamionJson") String documentacionCamionJson,
            ModelMap modelo, RedirectAttributes redirectAttributes, HttpSession session) throws JsonProcessingException, ParseException {
        
    Usuario logueado = (Usuario) session.getAttribute("usuariosession");
    ObjectMapper mapper = new ObjectMapper();
    
    List<DocumentacionDTO> documentacionCamionDTO = Arrays.asList(mapper.readValue(documentacionCamionJson, DocumentacionDTO[].class));
    
    List<Documentacion> documentacionCamion = new ArrayList<>();
    for (DocumentacionDTO dto : documentacionCamionDTO) {
    Documentacion d = new Documentacion();
    TipoDocumentacion tipo = tipoDocumentacionServicio.buscarTipo(dto.getTipoDocumentacion());
    Camion camion = camionServicio.buscarCamion(dto.getCamion());
    String obsMayusculas = dto.getObservacion().toUpperCase();
    d.setTipoDocumentacion(tipo);
    d.setCamion(camion);
    d.setAplicaA(TipoDocumentacion.AplicaA.valueOf(dto.getAplicaA()));
    d.setFechaAlta(dto.getFechaAlta());
    d.setFechaVencimiento(dto.getFechaVencimiento());
    d.setObservacion(obsMayusculas);
    d.setEstado("VIGENTE");
    d.setIdOrg(logueado.getIdOrg());
    d.setUsuario(logueado);
    
    d = documentacionServicio.crearDocumentacionMasivoTipo(d);
    
    documentacionCamion.add(d);
    
    }
    
    for(Documentacion d : documentacionCamion){
        d.setEstado(d.getCamion().getDominio());
    }

    redirectAttributes.addFlashAttribute("documentacionC", documentacionCamion);
    redirectAttributes.addFlashAttribute("tipoCamion", tipoCamion);

    return "redirect:/documentacion/registradoMasivoTipo";

    }
    
    @GetMapping("/registradoMasivoTipo")
    public String registradoMasivoTipo(@ModelAttribute("documentacionC") List<Documentacion> documentacionC,  
            @ModelAttribute("tipoCamion") Long tipoCamion, ModelMap modelo) {
        
         modelo.put("exito", "La carga de Documentación se ha REGISTRADO con éxito");
         modelo.addAttribute("documentacionC", documentacionC);
         modelo.addAttribute("tipoCamion", tipoDocumentacionServicio.buscarTipo(tipoCamion));
        
        return "documentacion_mostrarMasivoTipo.html";
    }
    
    @GetMapping("/registrarMasivoTipoA")
    public String registrarMasivoTipoA(ModelMap modelo, HttpSession session) {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");
        modelo.addAttribute("acoplados", acopladoServicio.buscarAcopladosHabAsc(logueado.getIdOrg()));
        modelo.addAttribute("tiposAcoplado", tipoDocumentacionServicio.buscarTiposAplicaA(logueado.getIdOrg(), TipoDocumentacion.AplicaA.ACOPLADO));
        
        return "documentacion_registrarMasivoTipoAcoplado.html"; 
    }
    
    @PostMapping("/registroMasivoTipoA")
    public String registroMasivoTipoA(@RequestParam(required = false) Long tipoAcoplado,
            @RequestParam("documentacionAcopladoJson") String documentacionAcopladoJson,
            ModelMap modelo, RedirectAttributes redirectAttributes, HttpSession session) throws JsonProcessingException, ParseException {
        
    Usuario logueado = (Usuario) session.getAttribute("usuariosession");
    ObjectMapper mapper = new ObjectMapper();

    List<DocumentacionDTO> documentacionAcopladoDTO = Arrays.asList(mapper.readValue(documentacionAcopladoJson, DocumentacionDTO[].class));
    
    List<Documentacion> documentacionAcoplado = new ArrayList<>();
    for (DocumentacionDTO dto : documentacionAcopladoDTO) {
    Documentacion d = new Documentacion();
    TipoDocumentacion tipo = tipoDocumentacionServicio.buscarTipo(dto.getTipoDocumentacion());
    Acoplado acoplado = acopladoServicio.buscarAcoplado(dto.getAcoplado());
    String obsMayusculas = dto.getObservacion().toUpperCase();
    d.setTipoDocumentacion(tipo);
    d.setAcoplado(acoplado);
    d.setAplicaA(TipoDocumentacion.AplicaA.valueOf(dto.getAplicaA()));
    d.setFechaAlta(dto.getFechaAlta());
    d.setFechaVencimiento(dto.getFechaVencimiento());
    d.setObservacion(obsMayusculas);
    d.setEstado("VIGENTE");
    d.setIdOrg(logueado.getIdOrg());
    d.setUsuario(logueado);
    
    d = documentacionServicio.crearDocumentacionMasivoTipo(d);
    
    documentacionAcoplado.add(d);
    
    }
    
    for(Documentacion d : documentacionAcoplado){
        d.setEstado(d.getAcoplado().getDominio());
    }

    redirectAttributes.addFlashAttribute("documentacionA", documentacionAcoplado);
    redirectAttributes.addFlashAttribute("tipoAcoplado", tipoAcoplado);

    return "redirect:/documentacion/registradoMasivoTipoA";

    }
    
    @GetMapping("/registradoMasivoTipoA")
    public String registradoMasivoTipoA(@ModelAttribute("documentacionA") List<Documentacion> documentacionA,  
        @ModelAttribute("tipoAcoplado") Long tipoAcoplado, ModelMap modelo) {
        
         modelo.put("exito", "La carga de Documentación se ha REGISTRADO con éxito");
         modelo.addAttribute("documentacionA", documentacionA);
         modelo.addAttribute("tipoAcoplado", tipoDocumentacionServicio.buscarTipo(tipoAcoplado));
        
        return "documentacion_mostrarMasivoTipoAcoplado.html";
    }
    
    @GetMapping("/registrarMasivoTipoChofer")
    public String registrarMasivoTipoChofer(ModelMap modelo, HttpSession session) {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        modelo.addAttribute("choferes", choferServicio.bucarChoferesHabNombreAsc(logueado.getIdOrg()));
        modelo.addAttribute("tiposChofer", tipoDocumentacionServicio.buscarTiposAplicaA(logueado.getIdOrg(), TipoDocumentacion.AplicaA.CHOFER));
        
        return "documentacion_registrarMasivoTipoChofer.html"; 
    }
    
    @PostMapping("/registroMasivoTipoChofer")
    public String registroMasivoTipoChofer(@RequestParam(required = false) Long tipoChofer, @RequestParam("documentacionChoferJson") String documentacionChoferJson, 
            ModelMap modelo, RedirectAttributes redirectAttributes, HttpSession session) throws JsonProcessingException, ParseException {
        
    Usuario logueado = (Usuario) session.getAttribute("usuariosession");
    ObjectMapper mapper = new ObjectMapper();
    
    List<DocumentacionDTO> documentacionChoferDTO = Arrays.asList(mapper.readValue(documentacionChoferJson, DocumentacionDTO[].class));
    
    List<Documentacion> documentacionChofer = new ArrayList<>();
    for (DocumentacionDTO dto : documentacionChoferDTO) {
    Documentacion d = new Documentacion();
    TipoDocumentacion tipo = tipoDocumentacionServicio.buscarTipo(dto.getTipoDocumentacion());
    Usuario chofer = choferServicio.buscarChofer(dto.getChofer());
    String obsMayusculas = dto.getObservacion().toUpperCase();
    d.setTipoDocumentacion(tipo);
    d.setChofer(chofer);
    d.setAplicaA(TipoDocumentacion.AplicaA.valueOf(dto.getAplicaA()));
    d.setFechaAlta(dto.getFechaAlta());
    d.setFechaVencimiento(dto.getFechaVencimiento());
    d.setObservacion(obsMayusculas);
    d.setEstado("VIGENTE");
    d.setIdOrg(logueado.getIdOrg());
    d.setUsuario(logueado);
    
    d = documentacionServicio.crearDocumentacionMasivoTipo(d);
    
    documentacionChofer.add(d);
    
    }
    
    for(Documentacion d : documentacionChofer){
        d.setEstado(d.getChofer().getNombre());
    }
    
    redirectAttributes.addFlashAttribute("documentacionChofer", documentacionChofer);
    redirectAttributes.addFlashAttribute("tipoChofer", tipoChofer);
    
    return "redirect:/documentacion/registradoMasivoTipoChofer";
    
    }
    
    @GetMapping("/registradoMasivoTipoChofer")
    public String registradoMasivoTipoChofer(@ModelAttribute("documentacionChofer") List<Documentacion> documentacionChofer,
        @ModelAttribute("tipoChofer") Long tipoChofer, ModelMap modelo) {
        
         modelo.put("exito", "La carga de Documentación se ha REGISTRADO con éxito");
         modelo.addAttribute("documentacionChofer", documentacionChofer);
         modelo.addAttribute("tipoChofer", tipoDocumentacionServicio.buscarTipo(tipoChofer));
        
        return "documentacion_mostrarMasivoTipoChofer.html";
        
    }
    
    @GetMapping("/registrarMasivoAdmin")
    public String registrarMasivoAdmin(ModelMap modelo, HttpSession session) {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        modelo.addAttribute("choferes", choferServicio.bucarChoferesHabNombreAsc(logueado.getIdOrg()));
        modelo.addAttribute("tipos", tipoDocumentacionServicio.buscarTiposAplicaA(logueado.getIdOrg(), TipoDocumentacion.AplicaA.CHOFER));
        
        return "documentacion_registrarMasivoAdmin.html"; 
    }
    
    @PostMapping("/registroMasivoAdmin")
    public String registroMasivoAdmin(@RequestParam("documentacionChoferJson") String documentacionChoferJson,
    @RequestParam(required = false) Long idChofer, ModelMap modelo, RedirectAttributes redirectAttributes, HttpSession session) throws JsonProcessingException, ParseException {
    
    Usuario logueado = (Usuario) session.getAttribute("usuariosession");
    ObjectMapper mapper = new ObjectMapper();
    
    List<DocumentacionDTO> documentacionChoferDTO = Arrays.asList(mapper.readValue(documentacionChoferJson, DocumentacionDTO[].class));
    
    List<Documentacion> documentacionChofer = new ArrayList<>();
    for (DocumentacionDTO dto : documentacionChoferDTO) {
    Documentacion d = new Documentacion();
    TipoDocumentacion tipo = tipoDocumentacionServicio.buscarTipo(dto.getTipoDocumentacion());
    tipo.setId(dto.getTipoDocumentacion());
    String obsMayusculas = dto.getObservacion().toUpperCase();
    d.setTipoDocumentacion(tipo);
    d.setAplicaA(TipoDocumentacion.AplicaA.valueOf(dto.getAplicaA()));
    d.setFechaAlta(dto.getFechaAlta());
    d.setFechaVencimiento(dto.getFechaVencimiento());
    d.setObservacion(obsMayusculas);
    documentacionChofer.add(d);
    }
    List<Documentacion> documentacionNuevoChofer = new ArrayList<>();
    for (Documentacion d : documentacionChofer) {
        d.setEstado("VIGENTE");
        d.setIdOrg(logueado.getIdOrg());
        d.setUsuario(logueado);
        Usuario chofer = choferServicio.buscarChofer(idChofer);
        d.setChofer(chofer);
            
        d = documentacionServicio.crearDocumentacionMasivo(d);
        
        documentacionNuevoChofer.add(d);

    }
    
    redirectAttributes.addFlashAttribute("documentacionC", documentacionNuevoChofer);

    return "redirect:/documentacion/registradoMasivoAdmin";

   }
    @GetMapping("/registradoMasivoAdmin")
    public String registradoMasivoAdmin(@ModelAttribute("documentacionC") List<Documentacion> documentacionC, ModelMap modelo) {

         modelo.put("exito", "La carga de Documentación se ha REGISTRADO con éxito");
         modelo.addAttribute("documentacionC", documentacionC);
        
        return "documentacion_mostrarMasivoAdmin.html";
    }

    
    
     @GetMapping("/registrarMasivoChofer")
    public String registrarMasivoChofer(ModelMap modelo, HttpSession session) {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        modelo.addAttribute("chofer", logueado);
        modelo.addAttribute("tipos", tipoDocumentacionServicio.buscarTiposAplicaA(logueado.getIdOrg(), TipoDocumentacion.AplicaA.CHOFER));
        
        return "documentacion_registrarMasivoChofer.html"; 
    }
    
    @PostMapping("/registroMasivoChofer")
    public String registroMasivoChofer(@RequestParam("documentacionChoferJson") String documentacionChoferJson,
    @RequestParam(required = false) Long idChofer, ModelMap modelo, RedirectAttributes redirectAttributes, HttpSession session) throws JsonProcessingException, ParseException {
    
    Usuario logueado = (Usuario) session.getAttribute("usuariosession");
    ObjectMapper mapper = new ObjectMapper();
    
    List<DocumentacionDTO> documentacionChoferDTO = Arrays.asList(mapper.readValue(documentacionChoferJson, DocumentacionDTO[].class));
    
    List<Documentacion> documentacionChofer = new ArrayList<>();
    for (DocumentacionDTO dto : documentacionChoferDTO) {
    Documentacion d = new Documentacion();
    TipoDocumentacion tipo = tipoDocumentacionServicio.buscarTipo(dto.getTipoDocumentacion());
    tipo.setId(dto.getTipoDocumentacion());
    String obsMayusculas = dto.getObservacion().toUpperCase();
    d.setTipoDocumentacion(tipo);
    d.setAplicaA(TipoDocumentacion.AplicaA.valueOf(dto.getAplicaA()));
    d.setFechaAlta(dto.getFechaAlta());
    d.setFechaVencimiento(dto.getFechaVencimiento());
    d.setObservacion(obsMayusculas);
    documentacionChofer.add(d);
    }
    List<Documentacion> documentacionNuevoChofer = new ArrayList<>();
    for (Documentacion d : documentacionChofer) {
        d.setEstado("VIGENTE");
        d.setIdOrg(logueado.getIdOrg());
        d.setUsuario(logueado);
        d.setChofer(logueado);
            
        d = documentacionServicio.crearDocumentacionMasivo(d);
        
        documentacionNuevoChofer.add(d);

    }
    
    redirectAttributes.addFlashAttribute("documentacionC", documentacionNuevoChofer);

    return "redirect:/documentacion/registradoMasivoChofer";

}
    @GetMapping("/registradoMasivoChofer")
    public String registradoMasivoChofer(@ModelAttribute("documentacionC") List<Documentacion> documentacionC, ModelMap modelo, HttpSession session) {
        
        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

         modelo.put("exito", "La carga de Documentación se ha REGISTRADO con éxito");
         modelo.put("chofer", logueado);
         modelo.addAttribute("documentacionC", documentacionC);
        
        return "documentacion_mostrarMasivoChofer.html";
    }
    
    @GetMapping("/registrarMasivoChoferVehiculo")
    public String registrarMasivoChoferVehiculo(ModelMap modelo, HttpSession session) {
        
        Usuario chofer = (Usuario) session.getAttribute("usuariosession");
        
        modelo.put("camion", chofer.getCamion());
        modelo.put("acoplado", chofer.getAcoplado());
        modelo.addAttribute("tiposCamion", tipoDocumentacionServicio.buscarTiposAplicaA(chofer.getIdOrg(), TipoDocumentacion.AplicaA.CAMION));
        modelo.addAttribute("tiposAcoplado", tipoDocumentacionServicio.buscarTiposAplicaA(chofer.getIdOrg(), TipoDocumentacion.AplicaA.ACOPLADO));
        
        return "documentacion_registrarMasivoChoferVehiculo.html"; 
        
    }
    
    @PostMapping("/registroMasivoChoferVehiculo")
    public String registroMasivoChoferVehiculo(@RequestParam("documentacionCamionJson") String documentacionCamionJson,
                             @RequestParam("documentacionAcopladoJson") String documentacionAcopladoJson,
                             ModelMap modelo, RedirectAttributes redirectAttributes, HttpSession session) throws JsonProcessingException, ParseException {
    
    Usuario logueado = (Usuario) session.getAttribute("usuariosession");
    ObjectMapper mapper = new ObjectMapper();
    
    List<DocumentacionDTO> documentacionCamionDTO = Arrays.asList(mapper.readValue(documentacionCamionJson, DocumentacionDTO[].class));
    List<DocumentacionDTO> documentacionAcopladoDTO = Arrays.asList(mapper.readValue(documentacionAcopladoJson, DocumentacionDTO[].class));
    
    List<Documentacion> documentacionCamion = new ArrayList<>();
    for (DocumentacionDTO dto : documentacionCamionDTO) {
    Documentacion d = new Documentacion();
    TipoDocumentacion tipo = tipoDocumentacionServicio.buscarTipo(dto.getTipoDocumentacion());
    tipo.setId(dto.getTipoDocumentacion());
    String obsMayusculas = dto.getObservacion().toUpperCase();
    d.setTipoDocumentacion(tipo);
    d.setAplicaA(TipoDocumentacion.AplicaA.valueOf(dto.getAplicaA()));
    d.setFechaAlta(dto.getFechaAlta());
    d.setFechaVencimiento(dto.getFechaVencimiento());
    d.setObservacion(obsMayusculas);
    documentacionCamion.add(d);
    }
    
    List<Documentacion> documentacionAcoplado = new ArrayList<>();
    for (DocumentacionDTO dto : documentacionAcopladoDTO) {
    Documentacion d = new Documentacion();
    TipoDocumentacion tipo = tipoDocumentacionServicio.buscarTipo(dto.getTipoDocumentacion());
    tipo.setId(dto.getTipoDocumentacion());
    String obsMayusculas = dto.getObservacion().toUpperCase();
    d.setTipoDocumentacion(tipo);
    d.setAplicaA(TipoDocumentacion.AplicaA.valueOf(dto.getAplicaA()));
    d.setFechaAlta(dto.getFechaAlta());
    d.setFechaVencimiento(dto.getFechaVencimiento());
    d.setObservacion(obsMayusculas);
    documentacionAcoplado.add(d);
    }


    List<Documentacion> documentacionTotales = new ArrayList<>();
    documentacionTotales.addAll(documentacionCamion);
    documentacionTotales.addAll(documentacionAcoplado);
    List<Documentacion> documentacionNuevoCamion = new ArrayList<>();
    List<Documentacion> documentacionNuevoAcoplado = new ArrayList<>();
    
    for (Documentacion d : documentacionTotales) {
        d.setEstado("VIGENTE");
        d.setIdOrg(logueado.getIdOrg());
        d.setUsuario(logueado);
        if (d.getAplicaA().equals(TipoDocumentacion.AplicaA.CAMION)) {
            Camion camion = logueado.getCamion();
            d.setCamion(camion);
        } else if (d.getAplicaA().equals(TipoDocumentacion.AplicaA.ACOPLADO)) {
            Acoplado acoplado = logueado.getAcoplado();
            d.setAcoplado(acoplado);
        }
            
        d = documentacionServicio.crearDocumentacionMasivo(d);
        
        if(d.getCamion() != null){
        documentacionNuevoCamion.add(d);
        } else {
        documentacionNuevoAcoplado.add(d);
        }
        }
    
    redirectAttributes.addFlashAttribute("documentacionC", documentacionNuevoCamion);
    redirectAttributes.addFlashAttribute("documentacionA", documentacionNuevoAcoplado);

    return "redirect:/documentacion/registradoMasivoChoferVehiculo";

}
    
    @GetMapping("/registradoMasivoChoferVehiculo")
    public String registradoMasivoChoferVehiculo(@ModelAttribute("documentacionC") List<Documentacion> documentacionC,
            @ModelAttribute("documentacionA") List<Documentacion> documentacionA, ModelMap modelo) {
        
         modelo.put("exito", "La carga de Documentación se ha REGISTRADO con éxito");
         modelo.addAttribute("documentacionC", documentacionC);
         modelo.addAttribute("documentacionA", documentacionA);
        
        return "documentacion_mostrarMasivoChoferVehiculo.html";
    }
    
    
      @PostMapping("/exportarVencimiento")
    public String exportarVencimiento(@RequestParam Integer dias, ModelMap modelo, HttpSession session) throws ParseException {
        
        Usuario logueado = (Usuario) session.getAttribute("usuariosession");
        
        List<Documentacion> documentaciones = documentacionServicio.obtenerDocumentacionesPorVencer(logueado.getIdOrg(), dias);

        Map<String, List<Documentacion>> documentacionPorTipo = documentaciones.stream()
    .sorted(Comparator.comparing(Documentacion::getDiasVigencia)) 
    .collect(Collectors.groupingBy(doc -> doc.getTipoDocumentacion().getNombre()));
        
        modelo.put("documentacion", documentacionPorTipo);
        modelo.put("dias", dias);
        
        return "documentacion_exportarVencimientos.html";

    }
    
     @PostMapping("/exportaVencimiento")
    public void exportaVencimiento(@RequestParam Integer dias, HttpSession session, HttpServletResponse response) throws IOException, ParseException {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");
        
        List<Documentacion> documentaciones = documentacionServicio.obtenerDocumentacionesPorVencer(logueado.getIdOrg(), dias);

        Map<String, List<Documentacion>> documentacionPorTipo = documentaciones.stream()
    .sorted(Comparator.comparing(Documentacion::getDiasVigencia)) 
    .collect(Collectors.groupingBy(doc -> doc.getTipoDocumentacion().getNombre()));

            String htmlContent = generateHtmlFromObjects(documentacionPorTipo);
            excelServicio.exportHtmlToExcelDocumentacion(htmlContent, response);

    }
    
    @PostMapping("/exportarCamiones")
    public String exportarCamiones(@RequestParam(required = false) Long idCamion, @RequestParam(required = false) Long idTipo, ModelMap modelo, HttpSession session) throws ParseException {

       Usuario logueado = (Usuario) session.getAttribute("usuariosession");
        List<Documentacion> documentaciones = new ArrayList();

        if (idCamion != null && idTipo == null) {

            documentaciones = documentacionServicio.buscarDocumentacionIdCamion(idCamion);

        Map<String, List<Documentacion>> documentacionesPorTipo = documentaciones.stream()
    .sorted(Comparator.comparing(Documentacion::getDiasVigencia)) 
    .collect(Collectors.groupingBy(doc -> doc.getTipoDocumentacion().getNombre()));

            modelo.put("documentacion", documentacionesPorTipo);

        } else if (idCamion == null && idTipo != null) {

            documentaciones = documentacionServicio.buscarDocumentacionCamionesIdTipo(logueado.getIdOrg(), idTipo);

            Map<String, List<Documentacion>> documentacionesPorTipo = documentaciones.stream()
    .sorted(Comparator.comparing(Documentacion::getDiasVigencia)) 
    .collect(Collectors.groupingBy(doc -> doc.getTipoDocumentacion().getNombre()));
            
            modelo.put("documentacion", documentacionesPorTipo);

        } else if (idCamion != null && idTipo != null) {

            documentaciones = documentacionServicio.buscarDocumentacionCamionIdTipo(idCamion, idTipo);

            Map<String, List<Documentacion>> documentacionesPorTipo = documentaciones.stream()
    .sorted(Comparator.comparing(Documentacion::getDiasVigencia)) 
    .collect(Collectors.groupingBy(doc -> doc.getTipoDocumentacion().getNombre()));
            
            modelo.put("documentacion", documentacionesPorTipo);

        } else {

            documentaciones = documentacionServicio.buscarDocumentacionCamiones(logueado.getIdOrg());

        Map<String, List<Documentacion>> documentacionesPorTipo = documentaciones.stream()
    .sorted(Comparator.comparing(Documentacion::getDiasVigencia)) 
    .collect(Collectors.groupingBy(doc -> doc.getTipoDocumentacion().getNombre()));

            modelo.put("documentacion", documentacionesPorTipo);

        }

        modelo.put("idCamion", idCamion);
        modelo.put("idTipo", idTipo);
        
        return "documentacion_exportarCamiones.html";

    }
    
     @PostMapping("/exportaCamiones")
    public void exportaCamion(@RequestParam(required = false) Long idCamion, @RequestParam(required = false) Long idTipo, HttpSession session, HttpServletResponse response) throws IOException, ParseException {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");
        List<Documentacion> documentaciones = new ArrayList();

        if (idCamion != null && idTipo == null) {

            documentaciones = documentacionServicio.buscarDocumentacionIdCamion(idCamion);

        Map<String, List<Documentacion>> documentacionesPorTipo = documentaciones.stream()
    .sorted(Comparator.comparing(Documentacion::getDiasVigencia)) 
    .collect(Collectors.groupingBy(doc -> doc.getTipoDocumentacion().getNombre()));

            String htmlContent = generateHtmlFromObjects(documentacionesPorTipo);
            excelServicio.exportHtmlToExcelDocumentacion(htmlContent, response);

        } else if (idCamion == null && idTipo != null) {

            documentaciones = documentacionServicio.buscarDocumentacionCamionesIdTipo(logueado.getIdOrg(), idTipo);

            Map<String, List<Documentacion>> documentacionesPorTipo = documentaciones.stream()
    .sorted(Comparator.comparing(Documentacion::getDiasVigencia)) 
    .collect(Collectors.groupingBy(doc -> doc.getTipoDocumentacion().getNombre()));
            
            String htmlContent = generateHtmlFromObjects(documentacionesPorTipo);
            excelServicio.exportHtmlToExcelDocumentacion(htmlContent, response);

        } else if (idCamion != null && idTipo != null) {

            documentaciones = documentacionServicio.buscarDocumentacionCamionIdTipo(idCamion, idTipo);

            Map<String, List<Documentacion>> documentacionesPorTipo = documentaciones.stream()
    .sorted(Comparator.comparing(Documentacion::getDiasVigencia)) 
    .collect(Collectors.groupingBy(doc -> doc.getTipoDocumentacion().getNombre()));
            
            String htmlContent = generateHtmlFromObjects(documentacionesPorTipo);
            excelServicio.exportHtmlToExcelDocumentacion(htmlContent, response);

        } else {

            documentaciones = documentacionServicio.buscarDocumentacionCamiones(logueado.getIdOrg());

        Map<String, List<Documentacion>> documentacionesPorTipo = documentaciones.stream()
    .sorted(Comparator.comparing(Documentacion::getDiasVigencia)) 
    .collect(Collectors.groupingBy(doc -> doc.getTipoDocumentacion().getNombre()));

            String htmlContent = generateHtmlFromObjects(documentacionesPorTipo);
            excelServicio.exportHtmlToExcelDocumentacion(htmlContent, response);

        }
    }
    
    @PostMapping("/exportarAcoplados")
    public String exportarAcoplados(@RequestParam(required = false) Long idAcoplado, @RequestParam(required = false) Long idTipo, ModelMap modelo, HttpSession session) throws ParseException {
        
        Usuario logueado = (Usuario) session.getAttribute("usuariosession");
        List<Documentacion> documentaciones = new ArrayList();

        if (idAcoplado != null && idTipo == null) {

            documentaciones = documentacionServicio.buscarDocumentacionIdAcoplado(idAcoplado);

            Map<String, List<Documentacion>> documentacionesPorTipo = documentaciones.stream()
                    .sorted(Comparator.comparing(Documentacion::getDiasVigencia))
                    .collect(Collectors.groupingBy(doc -> doc.getTipoDocumentacion().getNombre()));

            modelo.put("documentacion", documentacionesPorTipo);

        } else if (idAcoplado == null && idTipo != null) {

            documentaciones = documentacionServicio.buscarDocumentacionAcopladosIdTipo(logueado.getIdOrg(), idTipo);

            Map<String, List<Documentacion>> documentacionesPorTipo = documentaciones.stream()
                    .sorted(Comparator.comparing(Documentacion::getDiasVigencia))
                    .collect(Collectors.groupingBy(doc -> doc.getTipoDocumentacion().getNombre()));

            modelo.put("documentacion", documentacionesPorTipo);

        } else if (idAcoplado != null && idTipo != null) {

            documentaciones = documentacionServicio.buscarDocumentacionAcopladoIdTipo(idAcoplado, idTipo);

            Map<String, List<Documentacion>> documentacionesPorTipo = documentaciones.stream()
                    .sorted(Comparator.comparing(Documentacion::getDiasVigencia))
                    .collect(Collectors.groupingBy(doc -> doc.getTipoDocumentacion().getNombre()));

            modelo.put("documentacion", documentacionesPorTipo);

        } else {

            documentaciones = documentacionServicio.buscarDocumentacionAcoplados(logueado.getIdOrg());

            Map<String, List<Documentacion>> documentacionesPorTipo = documentaciones.stream()
                    .sorted(Comparator.comparing(Documentacion::getDiasVigencia))
                    .collect(Collectors.groupingBy(doc -> doc.getTipoDocumentacion().getNombre()));

            modelo.put("documentacion", documentacionesPorTipo);

        }
        
        modelo.put("idAcoplado", idAcoplado);
        modelo.put("idTipo", idTipo);
        
        return "documentacion_exportarAcoplados.html";

    }
    
     @PostMapping("/exportaAcoplados")
    public void exportaAcoplados(@RequestParam(required = false) Long idAcoplado, @RequestParam(required = false) Long idTipo, HttpSession session, HttpServletResponse response) throws IOException, ParseException {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");
        List<Documentacion> documentaciones = new ArrayList();

        if (idAcoplado != null && idTipo == null) {

            documentaciones = documentacionServicio.buscarDocumentacionIdAcoplado(idAcoplado);

            Map<String, List<Documentacion>> documentacionesPorTipo = documentaciones.stream()
                    .sorted(Comparator.comparing(Documentacion::getDiasVigencia))
                    .collect(Collectors.groupingBy(doc -> doc.getTipoDocumentacion().getNombre()));

            String htmlContent = generateHtmlFromObjects(documentacionesPorTipo);
            excelServicio.exportHtmlToExcelDocumentacion(htmlContent, response);

        } else if (idAcoplado == null && idTipo != null) {

            documentaciones = documentacionServicio.buscarDocumentacionAcopladosIdTipo(logueado.getIdOrg(), idTipo);

            Map<String, List<Documentacion>> documentacionesPorTipo = documentaciones.stream()
                    .sorted(Comparator.comparing(Documentacion::getDiasVigencia))
                    .collect(Collectors.groupingBy(doc -> doc.getTipoDocumentacion().getNombre()));

            String htmlContent = generateHtmlFromObjects(documentacionesPorTipo);
            excelServicio.exportHtmlToExcelDocumentacion(htmlContent, response);

        } else if (idAcoplado != null && idTipo != null) {

            documentaciones = documentacionServicio.buscarDocumentacionAcopladoIdTipo(idAcoplado, idTipo);

            Map<String, List<Documentacion>> documentacionesPorTipo = documentaciones.stream()
                    .sorted(Comparator.comparing(Documentacion::getDiasVigencia))
                    .collect(Collectors.groupingBy(doc -> doc.getTipoDocumentacion().getNombre()));

            String htmlContent = generateHtmlFromObjects(documentacionesPorTipo);
            excelServicio.exportHtmlToExcelDocumentacion(htmlContent, response);

        } else {

            documentaciones = documentacionServicio.buscarDocumentacionAcoplados(logueado.getIdOrg());

            Map<String, List<Documentacion>> documentacionesPorTipo = documentaciones.stream()
                    .sorted(Comparator.comparing(Documentacion::getDiasVigencia))
                    .collect(Collectors.groupingBy(doc -> doc.getTipoDocumentacion().getNombre()));

            String htmlContent = generateHtmlFromObjects(documentacionesPorTipo);
            excelServicio.exportHtmlToExcelDocumentacion(htmlContent, response);

        }

    }
    
    @PostMapping("/exportarChoferes")
    public String exportarChoferes(@RequestParam(required = false) Long idChofer, @RequestParam(required = false) Long idTipo, ModelMap modelo, HttpSession session) throws ParseException {
        
        Usuario logueado = (Usuario) session.getAttribute("usuariosession");
        List<Documentacion> documentaciones = new ArrayList();

        if (idChofer != null && idTipo == null) {

            documentaciones = documentacionServicio.buscarDocumentacionIdChofer(idChofer);

            Map<String, List<Documentacion>> documentacionesPorTipo = documentaciones.stream()
                    .sorted(Comparator.comparing(Documentacion::getDiasVigencia))
                    .collect(Collectors.groupingBy(doc -> doc.getTipoDocumentacion().getNombre()));

            modelo.put("documentacion", documentacionesPorTipo);

        } else if (idChofer == null && idTipo != null) {

            documentaciones = documentacionServicio.buscarDocumentacionChoferesIdTipo(logueado.getIdOrg(), idTipo);

            Map<String, List<Documentacion>> documentacionesPorTipo = documentaciones.stream()
                    .sorted(Comparator.comparing(Documentacion::getDiasVigencia))
                    .collect(Collectors.groupingBy(doc -> doc.getTipoDocumentacion().getNombre()));

            modelo.put("documentacion", documentacionesPorTipo);

        } else if (idChofer != null && idTipo != null) {

            documentaciones = documentacionServicio.buscarDocumentacionChoferIdTipo(idChofer, idTipo);

            Map<String, List<Documentacion>> documentacionesPorTipo = documentaciones.stream()
                    .sorted(Comparator.comparing(Documentacion::getDiasVigencia))
                    .collect(Collectors.groupingBy(doc -> doc.getTipoDocumentacion().getNombre()));
            
            modelo.put("documentacion", documentacionesPorTipo);

        } else {

            documentaciones = documentacionServicio.buscarDocumentacionChoferes(logueado.getIdOrg());

            Map<String, List<Documentacion>> documentacionesPorTipo = documentaciones.stream()
                    .sorted(Comparator.comparing(Documentacion::getDiasVigencia))
                    .collect(Collectors.groupingBy(doc -> doc.getTipoDocumentacion().getNombre()));

            modelo.put("documentacion", documentacionesPorTipo);

        }

        modelo.put("idChofer", idChofer);
        modelo.put("idTipo", idTipo);
        
        return "documentacion_exportarChoferes.html";

    }
    
     @PostMapping("/exportaChoferes")
    public void exportaChoferes(@RequestParam(required = false) Long idChofer, @RequestParam(required = false) Long idTipo, HttpSession session, HttpServletResponse response) throws IOException, ParseException {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");
        List<Documentacion> documentaciones = new ArrayList();

        if (idChofer != null && idTipo == null) {

            documentaciones = documentacionServicio.buscarDocumentacionIdChofer(idChofer);

            Map<String, List<Documentacion>> documentacionesPorTipo = documentaciones.stream()
                    .sorted(Comparator.comparing(Documentacion::getDiasVigencia))
                    .collect(Collectors.groupingBy(doc -> doc.getTipoDocumentacion().getNombre()));

            String htmlContent = generateHtmlFromObjects(documentacionesPorTipo);
            excelServicio.exportHtmlToExcelDocumentacion(htmlContent, response);

        } else if (idChofer == null && idTipo != null) {

            documentaciones = documentacionServicio.buscarDocumentacionChoferesIdTipo(logueado.getIdOrg(), idTipo);

            Map<String, List<Documentacion>> documentacionesPorTipo = documentaciones.stream()
                    .sorted(Comparator.comparing(Documentacion::getDiasVigencia))
                    .collect(Collectors.groupingBy(doc -> doc.getTipoDocumentacion().getNombre()));

            String htmlContent = generateHtmlFromObjects(documentacionesPorTipo);
            excelServicio.exportHtmlToExcelDocumentacion(htmlContent, response);

        } else if (idChofer != null && idTipo != null) {

            documentaciones = documentacionServicio.buscarDocumentacionChoferIdTipo(idChofer, idTipo);

            Map<String, List<Documentacion>> documentacionesPorTipo = documentaciones.stream()
                    .sorted(Comparator.comparing(Documentacion::getDiasVigencia))
                    .collect(Collectors.groupingBy(doc -> doc.getTipoDocumentacion().getNombre()));
            
            String htmlContent = generateHtmlFromObjects(documentacionesPorTipo);
            excelServicio.exportHtmlToExcelDocumentacion(htmlContent, response);

        } else {

            documentaciones = documentacionServicio.buscarDocumentacionChoferes(logueado.getIdOrg());

            Map<String, List<Documentacion>> documentacionesPorTipo = documentaciones.stream()
                    .sorted(Comparator.comparing(Documentacion::getDiasVigencia))
                    .collect(Collectors.groupingBy(doc -> doc.getTipoDocumentacion().getNombre()));

            String htmlContent = generateHtmlFromObjects(documentacionesPorTipo);
            excelServicio.exportHtmlToExcelDocumentacion(htmlContent, response);

        }
        
    }
    
        private String generateHtmlFromObjects(Map<String, List<Documentacion>> objects) {
        StringBuilder sb = new StringBuilder();
        sb.append("<table>");
        sb.append("<thead><tr>"
                + "<th>Entidad</th>"
                + "<th>Tipo de Documentación</th>"
                + "<th>Fecha de Alta</th>"
                + "<th>Vencimiento</th>"
                + "<th>Vigencia</th>"
                + "<th>Estado</th>"
                + "<th>Observación</th>"
                + "</tr></thead>");
        sb.append("<tbody>");
        for (Map.Entry<String, List<Documentacion>> entry : objects.entrySet()) {
            List<Documentacion> documentos = entry.getValue(); // lista de valores
            for (Documentacion documento : documentos) {
                String dominio = "";
                if (documento.getAcoplado() != null) {
                    dominio = documento.getAcoplado().getDominio();
                } else if (documento.getCamion() != null) {
                    dominio = documento.getCamion().getDominio();
                } else if (documento.getChofer() != null) {
                    dominio = documento.getChofer().getNombre();
                }
                sb.append("<tr><td>").append(dominio).append("</td>"
                        + "<td>").append(documento.getTipoDocumentacion().getNombre()).append("</td>"
                        + "<td>").append(documento.getFechaAlta()).append("</td>"
                        + "<td>").append(documento.getFechaVencimiento()).append("</td>"
                        + "<td>").append(documento.getDiasVigencia()).append("</td>"
                        + "<td>").append(documento.getEstado()).append("</td>"
                        + "<td>").append(documento.getObservacion()).append("</td>"
                        + "</tr>");
            }
        }
        sb.append("</tbody></table>");
        return sb.toString();
    }
        
    @GetMapping("/imprimirCamiones")
    public String imprimirCamiones(@RequestParam(required = false) Long idCamion, @RequestParam(required = false) Long idTipo, ModelMap modelo, HttpSession session) {
        
        Usuario logueado = (Usuario) session.getAttribute("usuariosession");
        List<Documentacion> documentaciones = new ArrayList();

        if (idCamion != null && idTipo == null) {

            documentaciones = documentacionServicio.buscarDocumentacionIdCamion(idCamion);

            Map<String, List<Documentacion>> documentacionesPorTipo = documentaciones.stream()
                    .sorted(Comparator.comparing(Documentacion::getDiasVigencia))
                    .collect(Collectors.groupingBy(doc -> doc.getTipoDocumentacion().getNombre()));

            modelo.put("documentacion", documentacionesPorTipo);
            modelo.put("camion", camionServicio.buscarCamion(idCamion));

        } else if (idCamion == null && idTipo != null) {

            documentaciones = documentacionServicio.buscarDocumentacionCamionesIdTipo(logueado.getIdOrg(), idTipo);

            Map<String, List<Documentacion>> documentacionesPorTipo = documentaciones.stream()
                    .sorted(Comparator.comparing(Documentacion::getDiasVigencia))
                    .collect(Collectors.groupingBy(doc -> doc.getTipoDocumentacion().getNombre()));


            modelo.put("documentacion", documentacionesPorTipo);

        } else if (idCamion != null && idTipo != null) {

            documentaciones = documentacionServicio.buscarDocumentacionCamionIdTipo(idCamion, idTipo);

            Map<String, List<Documentacion>> documentacionesPorTipo = documentaciones.stream()
                    .sorted(Comparator.comparing(Documentacion::getDiasVigencia))
                    .collect(Collectors.groupingBy(doc -> doc.getTipoDocumentacion().getNombre()));

            modelo.put("documentacion", documentacionesPorTipo);
            modelo.put("camion", camionServicio.buscarCamion(idCamion));

        } else {

            documentaciones = documentacionServicio.buscarDocumentacionCamiones(logueado.getIdOrg());

            Map<String, List<Documentacion>> documentacionesPorTipo = documentaciones.stream()
                    .sorted(Comparator.comparing(Documentacion::getDiasVigencia))
                    .collect(Collectors.groupingBy(doc -> doc.getTipoDocumentacion().getNombre()));

            modelo.put("documentacion", documentacionesPorTipo);

        }
        
        modelo.put("idCamion", idCamion);

        return "documentacion_imprimirCamiones.html";
    }
    
    @GetMapping("/imprimirAcoplados")
    public String imprimirAcoplados(@RequestParam(required = false) Long idAcoplado, @RequestParam(required = false) Long idTipo, ModelMap modelo, HttpSession session) {
        
        Usuario logueado = (Usuario) session.getAttribute("usuariosession");
        List<Documentacion> documentaciones = new ArrayList();

        if (idAcoplado != null && idTipo == null) {

            documentaciones = documentacionServicio.buscarDocumentacionIdAcoplado(idAcoplado);

            Map<String, List<Documentacion>> documentacionesPorTipo = documentaciones.stream()
                    .sorted(Comparator.comparing(Documentacion::getDiasVigencia))
                    .collect(Collectors.groupingBy(doc -> doc.getTipoDocumentacion().getNombre()));

            modelo.put("documentacion", documentacionesPorTipo);
            modelo.put("acoplado", acopladoServicio.buscarAcoplado(idAcoplado));

        } else if (idAcoplado == null && idTipo != null) {

            documentaciones = documentacionServicio.buscarDocumentacionAcopladosIdTipo(logueado.getIdOrg(), idTipo);

            Map<String, List<Documentacion>> documentacionesPorTipo = documentaciones.stream()
                    .sorted(Comparator.comparing(Documentacion::getDiasVigencia))
                    .collect(Collectors.groupingBy(doc -> doc.getTipoDocumentacion().getNombre()));

            modelo.put("documentacion", documentacionesPorTipo);

        } else if (idAcoplado != null && idTipo != null) {

            documentaciones = documentacionServicio.buscarDocumentacionAcopladoIdTipo(idAcoplado, idTipo);

            Map<String, List<Documentacion>> documentacionesPorTipo = documentaciones.stream()
                    .sorted(Comparator.comparing(Documentacion::getDiasVigencia))
                    .collect(Collectors.groupingBy(doc -> doc.getTipoDocumentacion().getNombre()));

            modelo.put("documentacion", documentacionesPorTipo);
            modelo.put("acoplado", acopladoServicio.buscarAcoplado(idAcoplado));

        } else {

            documentaciones = documentacionServicio.buscarDocumentacionAcoplados(logueado.getIdOrg());

            Map<String, List<Documentacion>> documentacionesPorTipo = documentaciones.stream()
                    .sorted(Comparator.comparing(Documentacion::getDiasVigencia))
                    .collect(Collectors.groupingBy(doc -> doc.getTipoDocumentacion().getNombre()));

            modelo.put("documentacion", documentacionesPorTipo);

        }
        
        modelo.put("idAcoplado", idAcoplado);

        return "documentacion_imprimirAcoplados.html";
    }
    
    @GetMapping("/imprimirChoferes")
    public String imprimirChoferes(@RequestParam(required = false) Long idChofer, @RequestParam(required = false) Long idTipo, ModelMap modelo, HttpSession session) {
        
        Usuario logueado = (Usuario) session.getAttribute("usuariosession");
        List<Documentacion> documentaciones = new ArrayList();

        if (idChofer != null && idTipo == null) {

            documentaciones = documentacionServicio.buscarDocumentacionIdChofer(idChofer);

            Map<String, List<Documentacion>> documentacionesPorTipo = documentaciones.stream()
                    .sorted(Comparator.comparing(Documentacion::getDiasVigencia))
                    .collect(Collectors.groupingBy(doc -> doc.getTipoDocumentacion().getNombre()));

            modelo.put("documentacion", documentacionesPorTipo);
            modelo.put("chofer", choferServicio.buscarChofer(idChofer));

        } else if (idChofer == null && idTipo != null) {

            documentaciones = documentacionServicio.buscarDocumentacionChoferesIdTipo(logueado.getIdOrg(), idTipo);

            Map<String, List<Documentacion>> documentacionesPorTipo = documentaciones.stream()
                    .sorted(Comparator.comparing(Documentacion::getDiasVigencia))
                    .collect(Collectors.groupingBy(doc -> doc.getTipoDocumentacion().getNombre()));

            modelo.put("documentacion", documentacionesPorTipo);

        } else if (idChofer != null && idTipo != null) {

            documentaciones = documentacionServicio.buscarDocumentacionChoferIdTipo(idChofer, idTipo);

            Map<String, List<Documentacion>> documentacionesPorTipo = documentaciones.stream()
                    .sorted(Comparator.comparing(Documentacion::getDiasVigencia))
                    .collect(Collectors.groupingBy(doc -> doc.getTipoDocumentacion().getNombre()));

            modelo.put("documentacion", documentacionesPorTipo);
            modelo.put("chofer", choferServicio.buscarChofer(idChofer));

        } else {

            documentaciones = documentacionServicio.buscarDocumentacionChoferes(logueado.getIdOrg());

            Map<String, List<Documentacion>> documentacionesPorTipo = documentaciones.stream()
                    .sorted(Comparator.comparing(Documentacion::getDiasVigencia))
                    .collect(Collectors.groupingBy(doc -> doc.getTipoDocumentacion().getNombre()));

            modelo.put("documentacion", documentacionesPorTipo);

        }
        
        modelo.put("idChofer", idChofer);
        modelo.put("idTipo", idTipo);

        return "documentacion_imprimirChoferes.html";
    }
    
    @GetMapping("/imprimirVencimiento/{dias}")
    public String imprimirVencimiento(@PathVariable Integer dias, ModelMap modelo, HttpSession session) {
        
        Usuario logueado = (Usuario) session.getAttribute("usuariosession");
        
        List<Documentacion> documentaciones = documentacionServicio.obtenerDocumentacionesPorVencer(logueado.getIdOrg(), dias);

        Map<String, List<Documentacion>> documentacionPorTipo = documentaciones.stream()
    .sorted(Comparator.comparing(Documentacion::getDiasVigencia)) 
    .collect(Collectors.groupingBy(doc -> doc.getTipoDocumentacion().getNombre()));
        
        modelo.put("documentacion", documentacionPorTipo);
        modelo.put("dias", dias);

        return "documentacion_imprimirVencimientos.html";
    }

}

