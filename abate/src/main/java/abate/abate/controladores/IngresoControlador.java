package abate.abate.controladores;

import abate.abate.entidades.Caja;
import abate.abate.entidades.Imagen;
import abate.abate.entidades.Ingreso;
import abate.abate.entidades.Usuario;
import abate.abate.servicios.CajaServicio;
import abate.abate.servicios.ImagenServicio;
import abate.abate.servicios.IngresoServicio;
import java.text.ParseException;
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
@RequestMapping("/ingreso")
@PreAuthorize("hasAnyRole('ROLE_ADMIN')")
public class IngresoControlador {

    @Autowired
    private IngresoServicio ingresoServicio;
    @Autowired
    private CajaServicio cajaServicio;
    @Autowired
    private ImagenServicio imagenServicio;

    @GetMapping("/registrar/{id}")
    public String registrarIngreso(@PathVariable Long id, ModelMap modelo) {

        Caja caja = cajaServicio.buscarCaja(id);

        modelo.put("caja", caja);

        return "ingreso_registrar.html";
    }

    @PostMapping("/registro")
    public String registroEntrega(@RequestParam Long idChofer, @RequestParam String fecha,
            @RequestParam Double importe, @RequestParam(required = false) String observacion,
            ModelMap modelo, HttpSession session) throws ParseException {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        ingresoServicio.crearIngreso(logueado.getIdOrg(), idChofer, fecha, importe, observacion, logueado.getId());

        return "redirect:/ingreso/registrado";

    }

    @GetMapping("/registrado")
    public String ingresoRegistrado(HttpSession session, ModelMap modelo) {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        Long id = ingresoServicio.buscarUltimo(logueado.getIdOrg());
        Ingreso ingreso = ingresoServicio.buscarIngreso(id);

        modelo.put("ingreso", ingreso);
        modelo.put("exito", "Ingreso de Caja REGISTRADO con éxito");

        return "ingreso_mostrar.html";
    }

    @GetMapping("/listar/{id}")
    public String listar(@PathVariable Long id, ModelMap modelo) {

        modelo.addAttribute("ingresos", ingresoServicio.buscarIngresos(id));
        modelo.put("id", id);

        return "ingreso_listar.html";

    }

    @GetMapping("/modificar/{id}")
    public String modificar(@PathVariable Long id, ModelMap modelo) {

        modelo.put("ingreso", ingresoServicio.buscarIngreso(id));

        return "ingreso_modificar.html";

    }

    @PostMapping("/modifica")
    public String modifica(@RequestParam Long id, @RequestParam String fecha,
            @RequestParam Double importe, @RequestParam(required = false) String observacion,
            ModelMap modelo, HttpSession session) throws ParseException {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        ingresoServicio.modificarIngreso(id, fecha, importe, observacion, logueado.getId());

        return "redirect:/ingreso/modificado/" + id;
    }

    @GetMapping("/modificado/{id}")
    public String ingresoModificado(@PathVariable Long id, ModelMap modelo) {

        Ingreso ingreso = ingresoServicio.buscarIngreso(id);

        modelo.put("ingreso", ingreso);
        modelo.put("exito", "Ingreso de Caja MODIFICADO con éxito");

        return "ingreso_mostrar.html";
    }
    
    

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id, ModelMap modelo) {

        modelo.put("ingreso", ingresoServicio.buscarIngreso(id));

        return "ingreso_eliminar.html";
    }

    @GetMapping("/elimina/{id}")
    public String elimina(@PathVariable Long id, ModelMap modelo) {

        ingresoServicio.eliminarIngreso(id);
        
        return "redirect:/ingreso/eliminado";

    }
    
    @GetMapping("/eliminado")
    public String eliminado(ModelMap modelo, HttpSession session) {
        
        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        modelo.put("id", logueado.getId());
        modelo.put("exito", "Ingreso ELIMINADO con éxito");

        return "index_admin.html";      

    }

    @GetMapping("/imprimir/{id}")
    public String imprimir(@PathVariable Long id, ModelMap modelo, HttpSession session) {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");
        Ingreso ingreso = ingresoServicio.buscarIngreso(id);
        
        modelo.addAttribute("flag", false);
        
        if (logueado.getLogo() != null) {

            Long idLogo = logueado.getLogo().getId();
            Imagen logo = imagenServicio.obtenerImagenPorId(idLogo);
                
            modelo.addAttribute("imagenUrl", "/imagen/img/bytes/" + idLogo);
            modelo.addAttribute("flag", true);
                
        }

        modelo.put("ingreso", ingreso);

        return "ingreso_imprimir.html";
    }
}
