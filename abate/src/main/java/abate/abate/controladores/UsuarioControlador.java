package abate.abate.controladores;

import abate.abate.entidades.Usuario;
import abate.abate.excepciones.MiException;
import abate.abate.servicios.UsuarioServicio;
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
@RequestMapping("/usuario")
public class UsuarioControlador {

    @Autowired
    private UsuarioServicio usuarioServicio;

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_CEO')")
    @GetMapping("/registrar")
    public String registrarUsuario() {

        return "usuario_registrar.html";
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_CEO')")
    @PostMapping("/registro")
    public String registroUsuario(@RequestParam String nombre, @RequestParam String nombreUsuario,
            @RequestParam String password, @RequestParam String password2, ModelMap modelo, HttpSession session) {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        try {

            usuarioServicio.crearUsuario(nombre, nombreUsuario, password, password2, logueado);

            return "redirect:/usuario/registrado";

        } catch (MiException ex) {

            modelo.put("nombre", nombre);
            modelo.put("nombreUsuario", nombreUsuario);
            modelo.put("error", ex.getMessage());

            return "usuario_registrar.html";
        }
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_CEO')")
    @GetMapping("/registrado")
    public String usuarioRegistrado(HttpSession session, ModelMap modelo) {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        Long id = usuarioServicio.buscarUltimoUsuario(logueado.getIdOrg());

        modelo.put("usuario", usuarioServicio.buscarUsuario(id));
        modelo.put("exito", "Usuario REGISTRADO con éxito");

        return "usuario_registrado.html";
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_CEO')")
    @GetMapping("/listar")
    public String listar(ModelMap modelo, HttpSession session) {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        modelo.addAttribute("usuarios", usuarioServicio.buscarUsuarios(logueado.getIdOrg()));

        return "usuario_listar.html";
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_CEO')")
    @GetMapping("/mostrar/{id}")
    public String mostrar(@PathVariable Long id, ModelMap modelo) {

        modelo.put("usuario", usuarioServicio.buscarUsuario(id));

        return "usuario_mostrar.html";
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_CEO')")
    @GetMapping("/mostrarChofer/{id}")
    public String mostrarChofer(@PathVariable Long id, ModelMap modelo) {

        modelo.put("usuario", usuarioServicio.buscarUsuario(id));

        return "usuario_mostrarChofer.html";
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_CEO')")
    @GetMapping("/modificar/{id}")
    public String modificar(@PathVariable Long id, ModelMap modelo) {

        modelo.put("usuario", usuarioServicio.buscarUsuario(id));

        return "usuario_modificar.html";

    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_CEO')")
    @PostMapping("/modifica")
    public String modifica(@RequestParam Long id, @RequestParam String nombre, @RequestParam String nombreUsuario, ModelMap modelo) {

        try {

            usuarioServicio.modificarUsuario(id, nombre, nombreUsuario);

            return "redirect:/usuario/modificado/" + id;

        } catch (MiException ex) {

            modelo.put("usuario", usuarioServicio.buscarUsuario(id));
            modelo.put("error", ex.getMessage());

            return "usuario_modificar.html";

        }

    }
    
    @GetMapping("/modificado/{id}")
    public String modificado(@PathVariable Long id, ModelMap modelo) {

            modelo.put("usuario", usuarioServicio.buscarUsuario(id));
            modelo.put("exito", "Usuario MODIFICADO con éxito");

            return "usuario_registrado.html";       

    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @GetMapping("/modificarPsw/{id}")
    public String modificarPsw(@PathVariable Long id, ModelMap modelo) {

        modelo.put("usuario", usuarioServicio.buscarUsuario(id));

        return "usuario_modificarPsw.html";

    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @PostMapping("/modificaPsw/{id}")
    public String modificaPsw(@RequestParam Long id, @RequestParam String password, ModelMap modelo, HttpSession session) {

        usuarioServicio.modificarPswUsuario(id, password);

        return "redirect:/usuario/modificadoPsw/" + id;
    }
    
    @GetMapping("/modificadoPsw/{id}")
    public String modificadoPsw(@PathVariable Long id, ModelMap modelo) {

        modelo.put("usuario", usuarioServicio.buscarUsuario(id));
        modelo.put("exito", "Contraseña de Usuario MODIFICADA con éxito");

        return "usuario_registrado.html";       

    }

    //metodo para crear usuario CEO, es necesario quitar PreAuthorize
    
    @PreAuthorize("hasAnyRole('ROLE_CEO')")
    @GetMapping("/registrarCeo")
    public String registrarUsuarioCeo() {

        return "usuario_registrarCeo.html";

    }
    
    //metodo para crear usuario CEO

    @PreAuthorize("hasAnyRole('ROLE_CEO')")
    @PostMapping("/registroCeo")
    public String registroUsuarioCeo(@RequestParam String nombre, @RequestParam String nombreUsuario,
            @RequestParam String password, @RequestParam String password2, ModelMap modelo) {

        usuarioServicio.crearUsuarioCeo(nombre, nombreUsuario, password, password2);

        Long id = usuarioServicio.buscarUltimoUsuarioCeo();

        modelo.put("usuario", usuarioServicio.buscarUsuario(id));
        modelo.put("exito", "Usuario REGISTRADO con éxito");

        return "usuario_mostrarCeo.html";

    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @GetMapping("/modificarPswCeo/{id}")
    public String modificarPswCeo(@PathVariable Long id, ModelMap modelo) {

        modelo.put("usuario", usuarioServicio.buscarUsuario(id));

        return "usuario_modificarPswCeo.html";

    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @PostMapping("/modificaPswCeo/{id}")
    public String modificaPswCeo(@RequestParam Long id, @RequestParam String password, ModelMap modelo, HttpSession session) {

        usuarioServicio.modificarPswUsuario(id, password);

        modelo.put("usuario", usuarioServicio.buscarUsuario(id));
        modelo.put("exito", "Contraseña de Usuario MODIFICADA con éxito");

        return "usuario_registrado.html";

    }

    @PreAuthorize("hasAnyRole('ROLE_CEO')")
    @GetMapping("/registrarAdmin")
    public String registrarUsuarioAdmin() {

        return "usuario_registrarAdmin.html";
    }

    @PreAuthorize("hasAnyRole('ROLE_CEO')")
    @PostMapping("/registroAdmin")
    public String registroUsuarioAdmin(@RequestParam String nombre, @RequestParam Long cuil, @RequestParam String nombreUsuario, @RequestParam Long idOrg,
            @RequestParam String empresa, @RequestParam String direccion, @RequestParam String localidad, @RequestParam String telefono,
            @RequestParam String password, @RequestParam String password2, ModelMap modelo) {

        try {

            usuarioServicio.crearUsuarioAdmin(idOrg, nombre, nombreUsuario, cuil, empresa, direccion, localidad, telefono, password, password2);

            Long id = usuarioServicio.buscarUltimoUsuario(idOrg);

            modelo.put("usuario", usuarioServicio.buscarUsuario(id));
            modelo.put("exito", "Usuario REGISTRADO con éxito");

            return "usuario_mostrar.html";

        } catch (MiException ex) {

            modelo.put("nombre", nombre);
            modelo.put("cuil", cuil);
            modelo.put("nombreUsuario", nombreUsuario);
            modelo.put("empresa", empresa);
            modelo.put("direccion", direccion);
            modelo.put("localidad", localidad);
            modelo.put("telefono", telefono);
            modelo.put("error", ex.getMessage());

            return "usuario_registrar.html";
        }
    }

    @PreAuthorize("hasAnyRole('ROLE_CEO')")
    @GetMapping("/listarAdmin")
    public String listarAdmin(ModelMap modelo) {

        modelo.addAttribute("usuarios", usuarioServicio.buscarUsuariosAdmin());

        return "usuario_listarAdmin.html";
    }

    @PreAuthorize("hasAnyRole('ROLE_CEO')")
    @GetMapping("/mostrarAdmin/{idOrg}")
    public String mostrarAdmin(@PathVariable Long idOrg, ModelMap modelo) {

        modelo.addAttribute("usuarios", usuarioServicio.buscarUsuarios(idOrg));

        return "usuario_mostrarAdmin.html";
    }
    
    @PreAuthorize("hasAnyRole('ROLE_CEO')")
    @GetMapping("/modificarPswAdmin/{id}")
    public String modificarPswAdmin(@PathVariable Long id, ModelMap modelo) {

        modelo.put("usuario", usuarioServicio.buscarUsuario(id));

        return "usuario_modificarPswAdmin.html";

    }

    @PreAuthorize("hasAnyRole('ROLE_CEO')")
    @PostMapping("/modificaPswAdmin/{id}")
    public String modificaPswAdmin(@RequestParam Long id, @RequestParam String password, ModelMap modelo, HttpSession session) {

        usuarioServicio.modificarPswUsuario(id, password);

        return "redirect:/usuario/modificadoPswAdmin/" + id;
    }
    
    @GetMapping("/modificadoPswAdmin/{id}")
    public String modificadoPswAdmin(@PathVariable Long id, ModelMap modelo) {

        modelo.put("usuario", usuarioServicio.buscarUsuario(id));
        modelo.put("exito", "Contraseña de Usuario MODIFICADA con éxito");

        return "usuario_mostrar.html";       

    }

}
