
package abate.abate.controladores;

import abate.abate.entidades.Usuario;
import abate.abate.excepciones.MiException;
import abate.abate.servicios.ProveedorServicio;
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
@RequestMapping("/proveedor")
@PreAuthorize("hasAnyRole('ROLE_ADMIN')")
public class ProveedorControlador {
    
    @Autowired
    private ProveedorServicio proveedorServicio;

    @GetMapping("/registrar")
    public String registrarProveedor() {

        return "proveedor_registrar.html";
    }

    @PostMapping("/registro")
    public String registroProveedor(@RequestParam String nombre, @RequestParam Long cuit,
            ModelMap modelo, HttpSession session) {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        try {

            proveedorServicio.crearProveedor(logueado.getIdOrg(), nombre, cuit);

            return "redirect:/proveedor/registrado";

        } catch (MiException ex) {

            modelo.put("nombre", nombre);
            modelo.put("cuit", cuit);
            modelo.put("error", ex.getMessage());

            return "proveedor_registrar.html";
        }
    }

    @GetMapping("/registrado")
    public String proveedorRegistrado(HttpSession session, ModelMap modelo) {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        Long id = proveedorServicio.buscarUltimo(logueado.getIdOrg());
        modelo.put("proveedor", proveedorServicio.buscarProveedor(id));
        modelo.put("exito", "Proveedor REGISTRADO con éxito");

        return "proveedor_mostrar.html";
        
    }
    
    @GetMapping("/listar")
    public String listar(ModelMap modelo, HttpSession session) {
        
        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        modelo.addAttribute("proveedores", proveedorServicio.buscarProveedoresAsc(logueado.getIdOrg()));

        return "proveedor_listar.html";

    }
    
    @GetMapping("/modificar/{id}")
    public String modificar(@PathVariable Long id, ModelMap modelo) {

        modelo.put("proveedor", proveedorServicio.buscarProveedor(id));

        return "proveedor_modificar.html";

    }

    @PostMapping("/modifica/{id}")
    public String modifica(@RequestParam Long id, @RequestParam String nombre, @RequestParam(required = false) Long cuit,
             @RequestParam String estado, ModelMap modelo) {

        try {
            
            proveedorServicio.modificarProveedor(id, nombre, cuit, estado);

            return "redirect:/proveedor/modificado/" + id;

        } catch (MiException ex) {

            modelo.put("proveedor", proveedorServicio.buscarProveedor(id));
            modelo.put("error", ex.getMessage());

            return "proveedor_modificar.html";

        }
    }
    
    @GetMapping("/modificado/{id}")
    public String modificado(@PathVariable Long id, ModelMap modelo) {

            modelo.put("proveedor", proveedorServicio.buscarProveedor(id));
            modelo.put("exito", "Proveedor MODIFICADO con éxito");

            return "proveedor_mostrar.html";    

    }
    
    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id, ModelMap modelo) {

        modelo.put("proveedor", proveedorServicio.buscarProveedor(id));

        return "proveedor_eliminar.html";
    }

    @GetMapping("/elimina/{id}")
    public String elimina(@PathVariable Long id, ModelMap modelo) {

        try {

            proveedorServicio.eliminarProveedor(id);

            return "redirect:/proveedor/eliminado";

        } catch (MiException ex) {

            modelo.put("proveedor", proveedorServicio.buscarProveedor(id));
            modelo.put("error", ex.getMessage());

            return "proveedor_eliminar.html";
        }
    }
    
    @GetMapping("/eliminado")
    public String eliminado(ModelMap modelo, HttpSession session) {
        
        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

            modelo.put("id", logueado.getId());
            modelo.put("exito", "Proveedor ELIMINADO con éxito");

            return "index_admin.html";    

    }
   
    
    
}
