
package abate.abate.controladores;

import abate.abate.entidades.Usuario;
import abate.abate.excepciones.MiException;
import abate.abate.servicios.NeumaticoMarcaServicio;
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
@RequestMapping("/neumaticoMarca")
@PreAuthorize("hasAnyRole('ROLE_ADMIN')")
public class NeumaticoMarcaControlador {
    
    @Autowired
    private NeumaticoMarcaServicio marcaServicio;
    
    @GetMapping("/registrar")
    public String registrar() {

        return "neumaticoMarca_registrar.html";

    }

    @PostMapping("/registro")
    public String registro(@RequestParam String nombre, ModelMap modelo, HttpSession session) {
        
        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        try {

            marcaServicio.crearMarca(nombre, logueado);

            return "redirect:/neumaticoMarca/registrado/" +logueado.getIdOrg();

        } catch (MiException ex) {

            modelo.put("nombre", nombre);
            modelo.put("error", ex.getMessage());

            return "neumaticoMarca_registrar.html";
        }
    }

    @GetMapping("/registrado/{id}")
    public String registrado(@PathVariable Long id, ModelMap modelo) {

        modelo.put("marca", marcaServicio.buscarUltimo(id));
        modelo.put("exito", "Marca de Neumáticos REGISTRADA con éxito");

        return "neumaticoMarca_mostrar.html";
        
    }
    
    @GetMapping("/listar")
    public String listar(ModelMap modelo, HttpSession session) {
        
        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        modelo.addAttribute("marcas", marcaServicio.buscarMarcasAsc(logueado.getIdOrg()));

        return "neumaticoMarca_listar.html";

    }
    
    @GetMapping("/modificar/{id}")
    public String modificar(@PathVariable Long id, ModelMap modelo) {

        modelo.put("marca", marcaServicio.buscarMarca(id));

        return "neumaticoMarca_modificar.html";

    }

    @PostMapping("/modifica")
    public String modifica(@RequestParam Long id, @RequestParam String marca, ModelMap modelo, HttpSession session) {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");
        
        try {
            
            marcaServicio.modificarMarca(id, marca, logueado);

            return "redirect:/neumaticoMarca/modificado/" + id;


        } catch (MiException ex) {

            modelo.put("marca", marcaServicio.buscarMarca(id));
            modelo.put("error", ex.getMessage());

            return "neumaticoMarca_modificar.html";
        }

    }
    
    @GetMapping("/modificado/{id}")
    public String modificado(@PathVariable Long id, ModelMap modelo) {

            modelo.put("marca", marcaServicio.buscarMarca(id));
            modelo.put("exito", "Marca de Neumáticos MODIFICADA con éxito");

            return "neumaticoMarca_mostrar.html";       

    }
    
    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id, ModelMap modelo) {

        modelo.put("marca", marcaServicio.buscarMarca(id));

        return "neumaticoMarca_eliminar.html";
    }

    @GetMapping("/elimina/{id}")
    public String elimina(@PathVariable Long id, ModelMap modelo, HttpSession session) {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");
        
        try {

        marcaServicio.eliminarMarca(id, logueado.getIdOrg());
        
        return "redirect:/neumaticoMarca/eliminado/" + logueado.getIdOrg();

        } catch (MiException ex) {

            modelo.put("marca", marcaServicio.buscarMarca(id));
            modelo.put("error", ex.getMessage());

            return "neumaticoMarca_eliminar.html";
        }
    }
    
    @GetMapping("/eliminado/{id}")
    public String eliminado(@PathVariable Long id, ModelMap modelo) {

        modelo.put("exito", "Marca de Neumáticos ELIMINADA con éxito");
        modelo.addAttribute("marcas", marcaServicio.buscarMarcasAsc(id));

        return "neumaticoMarca_listar.html";     

    }
    
    
    
}
