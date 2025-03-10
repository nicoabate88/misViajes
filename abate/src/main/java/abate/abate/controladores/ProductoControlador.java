
package abate.abate.controladores;

import abate.abate.entidades.Usuario;
import abate.abate.excepciones.MiException;
import abate.abate.servicios.ProductoServicio;
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
@RequestMapping("/producto")
@PreAuthorize("hasAnyRole('ROLE_ADMIN')")
public class ProductoControlador {
    
    @Autowired
    private ProductoServicio productoServicio;

    @GetMapping("/registrar")
    public String registrar(ModelMap modelo) {

        return "producto_registrar.html";

    }

    @PostMapping("/registro")
    public String registro(@RequestParam String nombre, @RequestParam String estado, ModelMap model, HttpSession session) {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        try {

            productoServicio.crearProducto(logueado.getIdOrg(), nombre, estado);

            return "redirect:/producto/registrado";

        } catch (MiException ex) {

            model.put("nombre", nombre);
            model.put("error", ex.getMessage());

            return "producto_registrar.html";
        }
    }

    @GetMapping("/registrado")
    public String registrado(HttpSession session, ModelMap modelo) {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        Long id = productoServicio.buscarUltimo(logueado.getIdOrg());

        modelo.put("producto", productoServicio.buscarProducto(id));
        modelo.put("exito", "Producto REGISTRADO con éxito");

        return "producto_registrado.html";
    }
    
    @GetMapping("/listar")
    public String listar(ModelMap modelo, HttpSession session) {
        
        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        modelo.addAttribute("productos", productoServicio.buscarProductosAsc(logueado.getIdOrg()));

        return "producto_listar.html";

    }
    
    @GetMapping("/modificar/{id}")
    public String modificar(@PathVariable Long id, ModelMap modelo) {

        modelo.put("producto", productoServicio.buscarProducto(id));

        return "producto_modificar.html";

    }

    @PostMapping("/modifica")
    public String modifica(@RequestParam Long id, @RequestParam String nombre, @RequestParam String estado, ModelMap modelo) {

        try {
            
            productoServicio.modificarProducto(id, nombre, estado);

            return "redirect:/producto/modificado/" + id;


        } catch (MiException ex) {

            modelo.put("producto", productoServicio.buscarProducto(id));
            modelo.put("error", ex.getMessage());

            return "producto_modificar.html";
        }

    }
    
    @GetMapping("/modificado/{id}")
    public String modificado(@PathVariable Long id, ModelMap modelo) {

            modelo.put("producto", productoServicio.buscarProducto(id));
            modelo.put("exito", "Producto MODIFICADO con éxito");

            return "producto_registrado.html";       

    }
    
    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id, ModelMap modelo) {

        modelo.put("producto", productoServicio.buscarProducto(id));

        return "producto_eliminar.html";
    }

    @GetMapping("/elimina/{id}")
    public String elimina(@PathVariable Long id, ModelMap modelo) {

        try {

        productoServicio.eliminarProducto(id);
        
        return "redirect:/producto/eliminado/" + id;

        } catch (MiException ex) {

            modelo.put("producto", productoServicio.buscarProducto(id));
            modelo.put("error", ex.getMessage());

            return "producto_eliminar.html";
        }
    }
    
    @GetMapping("/eliminado/{id}")
    public String eliminado(@PathVariable Long id, ModelMap modelo, HttpSession session) {
        
        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        modelo.addAttribute("productos", productoServicio.buscarProductosAsc(logueado.getIdOrg()));
        modelo.put("exito", "Producto ELIMINADO con éxito");

        return "producto_listar.html";      

    }
    
    
}
