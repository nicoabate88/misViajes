
package abate.abate.controladores;

import abate.abate.entidades.Usuario;
import abate.abate.excepciones.MiException;
import abate.abate.servicios.NeumaticoProveedorServicio;
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
@RequestMapping("/neumaticoProveedor")
@PreAuthorize("hasAnyRole('ROLE_ADMIN')")
public class NeumaticoProveedorControlador {
    
    @Autowired
    private NeumaticoProveedorServicio proveedorServicio;
    
    @GetMapping("/registrar")
    public String registrar() {

        return "neumaticoProveedor_registrar.html";

    }

    @PostMapping("/registro")
    public String registro(@RequestParam String nombre, ModelMap modelo, HttpSession session) {
        
        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        try {

            proveedorServicio.crearProveedor(nombre, logueado);

            return "redirect:/neumaticoProveedor/registrado/" + logueado.getIdOrg();

        } catch (MiException ex) {

            modelo.put("nombre", nombre);
            modelo.put("error", ex.getMessage());

            return "neumaticoProveedor_registrar.html";
        }
    }

    @GetMapping("/registrado/{id}")
    public String registrado(@PathVariable Long id, ModelMap modelo) {

        modelo.put("proveedor", proveedorServicio.buscarUltimo(id));
        modelo.put("exito", "Proveedor de Neumáticos REGISTRADO con éxito");

        return "neumaticoProveedor_mostrar.html";
        
    }
    
    @GetMapping("/listar")
    public String listar(ModelMap modelo, HttpSession session) {
        
        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        modelo.addAttribute("proveedores", proveedorServicio.buscarProveedoresAsc(logueado.getIdOrg()));

        return "neumaticoProveedor_listar.html";

    }
    
    @GetMapping("/modificar/{id}")
    public String modificar(@PathVariable Long id, ModelMap modelo) {

        modelo.put("proveedor", proveedorServicio.buscarProveedor(id));

        return "neumaticoProveedor_modificar.html";

    }

    @PostMapping("/modifica")
    public String modifica(@RequestParam Long id, @RequestParam String nombre, ModelMap modelo, HttpSession session) {
        
        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        try {
            
            proveedorServicio.modificarProveedor(id, nombre, logueado);

            return "redirect:/neumaticoProveedor/modificado/" + id;


        } catch (MiException ex) {

            modelo.put("proveedor", proveedorServicio.buscarProveedor(id));
            modelo.put("error", ex.getMessage());

            return "neumaticoProveedor_modificar.html";
        }

    }
    
    @GetMapping("/modificado/{id}")
    public String modificado(@PathVariable Long id, ModelMap modelo) {

            modelo.put("proveedor", proveedorServicio.buscarProveedor(id));
            modelo.put("exito", "Proveedor de Neumáticos MODIFICADO con éxito");

            return "neumaticoProveedor_mostrar.html";       

    }
    
    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id, ModelMap modelo) {

        modelo.put("proveedor", proveedorServicio.buscarProveedor(id));

        return "neumaticoProveedor_eliminar.html";
    }

    @GetMapping("/elimina/{id}")
    public String elimina(@PathVariable Long id, ModelMap modelo, HttpSession session) {
        
        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        try {

        proveedorServicio.eliminarProveedor(id, logueado.getIdOrg());
        
        return "redirect:/neumaticoProveedor/eliminado/" + logueado.getIdOrg();

        } catch (MiException ex) {

            modelo.put("proveedor", proveedorServicio.buscarProveedor(id));
            modelo.put("error", ex.getMessage());

            return "neumaticoProveedor_eliminar.html";
        }
    }
    
    @GetMapping("/eliminado/{id}")
    public String eliminado(@PathVariable Long id, ModelMap modelo) {

        modelo.put("exito", "Proveedor de Neumáticos ELIMINADO con éxito");
        modelo.addAttribute("proveedores", proveedorServicio.buscarProveedoresAsc(id));

        return "neumaticoProveedor_listar.html";     

    }
    
    
}
