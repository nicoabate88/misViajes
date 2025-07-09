package abate.abate.controladores;

import abate.abate.entidades.Usuario;
import javax.servlet.http.HttpSession;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/")

public class PortalControlador {

    @GetMapping("/")
    public String login(@RequestParam(required = false) String error, ModelMap modelo) {

        if (error != null) {

            modelo.put("error", "Usuario o Contraseña incorrecto");
        }

        return "login.html";
    
    }

    @GetMapping("/index")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_CHOFER', 'ROLE_CEO')")
    public String index(HttpSession session, ModelMap modelo) {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        if (logueado.getRol().equalsIgnoreCase("CHOFER")) {

            modelo.put("chofer", logueado);

            return "index_chofer.html";

        } else if (logueado.getRol().equalsIgnoreCase("ADMIN")) {

            modelo.put("id", logueado.getId());

            return "index_admin.html";

        } else {

            modelo.put("id", logueado.getId());

            return "index_ceo.html";

        }

    }

}
