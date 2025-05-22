
package abate.abate.controladores;

import abate.abate.entidades.TipoMantenimiento;
import abate.abate.entidades.Usuario;
import abate.abate.excepciones.MiException;
import abate.abate.servicios.TipoMantenimientoServicio;
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
@RequestMapping("/tipoMantenimiento")
@PreAuthorize("hasAnyRole('ROLE_ADMIN')")
public class TipoMantenimientoControlador {
    
    @Autowired
    private TipoMantenimientoServicio tipoMantenimientoServicio;
    
    @GetMapping("/registrar")
    public String registrar(ModelMap modelo) {
        
        modelo.addAttribute("aplicaA", TipoMantenimiento.AplicaA.values());

        return "tipoMantenimiento_registrar.html";

    }
    
    @PostMapping("/registro")
    public String registro(@RequestParam String nombre, @RequestParam List<TipoMantenimiento.AplicaA> aplicaA,  
            ModelMap modelo, HttpSession session) {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        try {

            tipoMantenimientoServicio.crearTipoMantenimiento(nombre, aplicaA, logueado);

            return "redirect:/tipoMantenimiento/registrado/" + logueado.getIdOrg();

        } catch (MiException ex) {

            modelo.put("nombre", nombre);
            modelo.addAttribute("aplicaA", TipoMantenimiento.AplicaA.values());
            modelo.put("error", ex.getMessage());

            return "tipoMantenimiento_registrar.html";
        }
    }

    @GetMapping("/registrado/{id}")
    public String tipoRegistrado(@PathVariable Long id, ModelMap modelo) {

        modelo.put("tipo", tipoMantenimientoServicio.buscarUltimo(id));
        modelo.put("exito", "Tipo de Mantenimiento REGISTRADO con éxito");

        return "tipoMantenimiento_mostrar.html";
    }
    
    @GetMapping("/listar")
    public String listar(ModelMap modelo, HttpSession session) {
        
        Usuario logueado = (Usuario) session.getAttribute("usuariosession");
        
        modelo.addAttribute("tipos", tipoMantenimientoServicio.buscarTiposAsc(logueado.getIdOrg()));

        return "tipoMantenimiento_listar.html";

    }
    
    @GetMapping("/modificar/{id}")
    public String modificar(@PathVariable Long id, ModelMap modelo) {

        
        modelo.put("tipo", tipoMantenimientoServicio.buscarTipo(id));
        modelo.addAttribute("aplicaA", TipoMantenimiento.AplicaA.values());

        return "tipoMantenimiento_modificar.html";

    }

    @PostMapping("/modifica")
    public String modifica(@RequestParam Long id, @RequestParam String nombre, @RequestParam List<TipoMantenimiento.AplicaA> aplicaA,  
            ModelMap modelo, HttpSession session) {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        try {
            
            tipoMantenimientoServicio.modificarTipoMantenimiento(id, nombre, aplicaA, logueado);

            return "redirect:/tipoMantenimiento/modificado/" + id;

        } catch (MiException ex) {

            modelo.put("nombre", nombre);
            modelo.addAttribute("aplicaA", TipoMantenimiento.AplicaA.values());
            modelo.put("error", ex.getMessage());

            return "tipoMantenimiento_modificar.html";
        }

    }
    
    @GetMapping("/modificado/{id}")
    public String modificado(@PathVariable Long id, ModelMap modelo) {

            modelo.put("tipo", tipoMantenimientoServicio.buscarTipo(id));
            modelo.put("exito", "Tipo de Mantenimiento MODIFICADO con éxito");

            return "tipoMantenimiento_mostrar.html";     

    }
    
    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id, ModelMap modelo) {

        modelo.put("tipo", tipoMantenimientoServicio.buscarTipo(id));

        return "tipoMantenimiento_eliminar.html";
    }

    @GetMapping("/elimina/{id}")
    public String elimina(@PathVariable Long id, ModelMap modelo) {

        try {

            tipoMantenimientoServicio.eliminarTipoMantenimiento(id);
            
            return "redirect:/tipoMantenimiento/eliminado";

        } catch (MiException ex) {

            modelo.put("tipo", tipoMantenimientoServicio.buscarTipo(id));
            modelo.put("error", ex.getMessage());

            return "tipoDocumentacion_eliminar.html";
        }
    }
    
    @GetMapping("/eliminado")
    public String eliminado(ModelMap modelo, HttpSession session) {
        
        Usuario logueado = (Usuario) session.getAttribute("usuariosession");
        
        modelo.put("exito", "Tipo de Mantenimiento ELIMINADO con éxito");
        modelo.addAttribute("tipos", tipoMantenimientoServicio.buscarTiposAsc(logueado.getIdOrg()));

        return "tipoMantenimiento_listar.html";   

    }
    
}
