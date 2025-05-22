
package abate.abate.controladores;

import abate.abate.entidades.TipoDocumentacion;
import abate.abate.entidades.Usuario;
import abate.abate.excepciones.MiException;
import abate.abate.servicios.TipoDocumentacionServicio;
import java.util.List;
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
@RequestMapping("/tipoDocumentacion")
@PreAuthorize("hasAnyRole('ROLE_ADMIN')")
public class TipoDocumentacionControlador {
    
    @Autowired
    private TipoDocumentacionServicio tipoDocumentacionServicio;
    
    @GetMapping("/registrar")
    public String registrar(ModelMap modelo) {
        
        modelo.addAttribute("aplicaA", TipoDocumentacion.AplicaA.values());

        return "tipoDocumentacion_registrar.html";

    }
    
    @PostMapping("/registro")
    public String registro(@RequestParam String nombre, @RequestParam List<TipoDocumentacion.AplicaA> aplicaA,  
            ModelMap modelo, HttpSession session) {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        try {

            tipoDocumentacionServicio.crearTipoDocumentacion(nombre, aplicaA, logueado);

            return "redirect:/tipoDocumentacion/registrado/" + logueado.getIdOrg();

        } catch (MiException ex) {

            modelo.put("nombre", nombre);
            modelo.addAttribute("aplicaA", TipoDocumentacion.AplicaA.values());
            modelo.put("error", ex.getMessage());

            return "tipoDocumentacion_registrar.html";
        }
    }

    @GetMapping("/registrado/{id}")
    public String semiRegistrado(@PathVariable Long id, ModelMap modelo) {

        modelo.put("tipo", tipoDocumentacionServicio.buscarUltimo(id));
        modelo.put("exito", "Tipo de Documentación REGISTRADO con éxito");

        return "tipoDocumentacion_mostrar.html";
    }
    
    @GetMapping("/listar")
    public String listar(ModelMap modelo, HttpSession session) {
        
        Usuario logueado = (Usuario) session.getAttribute("usuariosession");
        
        modelo.addAttribute("tipos", tipoDocumentacionServicio.buscarTiposAsc(logueado.getIdOrg()));

        return "tipoDocumentacion_listar.html";

    }
    
    @GetMapping("/modificar/{id}")
    public String modificar(@PathVariable Long id, ModelMap modelo) {

        
        modelo.put("tipo", tipoDocumentacionServicio.buscarTipo(id));
        modelo.addAttribute("aplicaA", TipoDocumentacion.AplicaA.values());

        return "tipoDocumentacion_modificar.html";

    }

    @PostMapping("/modifica")
    public String modifica(@RequestParam Long id, @RequestParam String nombre, @RequestParam List<TipoDocumentacion.AplicaA> aplicaA,  
            ModelMap modelo, HttpSession session) {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        try {
            
            tipoDocumentacionServicio.modificarTipoDocumentacion(id, nombre, aplicaA, logueado);

            return "redirect:/tipoDocumentacion/modificado/" + id;

        } catch (MiException ex) {

            modelo.put("nombre", nombre);
            modelo.addAttribute("aplicaA", TipoDocumentacion.AplicaA.values());
            modelo.put("error", ex.getMessage());

            return "tipoDocumentacion_modificar.html";
        }

    }
    
    @GetMapping("/modificado/{id}")
    public String modificado(@PathVariable Long id, ModelMap modelo) {

            modelo.put("tipo", tipoDocumentacionServicio.buscarTipo(id));
            modelo.put("exito", "Tipo de Documentación MODIFICADO con éxito");

            return "tipoDocumentacion_mostrar.html";     

    }
    
    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id, ModelMap modelo) {

        modelo.put("tipo", tipoDocumentacionServicio.buscarTipo(id));

        return "tipoDocumentacion_eliminar.html";
    }

    @GetMapping("/elimina/{id}")
    public String elimina(@PathVariable Long id, ModelMap modelo) {

        try {

            tipoDocumentacionServicio.eliminarTipoDocumentacion(id);
            
            return "redirect:/tipoDocumentacion/eliminado";

        } catch (MiException ex) {

            modelo.put("tipo", tipoDocumentacionServicio.buscarTipo(id));
            modelo.put("error", ex.getMessage());

            return "tipoDocumentacion_eliminar.html";
        }
    }
    
    @GetMapping("/eliminado")
    public String eliminado(ModelMap modelo, HttpSession session) {
        
        Usuario logueado = (Usuario) session.getAttribute("usuariosession");
        
        modelo.put("exito", "Tipo de Documentación ELIMINADO con éxito");
        modelo.addAttribute("tipos", tipoDocumentacionServicio.buscarTiposAsc(logueado.getIdOrg()));

        return "tipoDocumentacion_listar.html";   

    }
    

    
    
}
