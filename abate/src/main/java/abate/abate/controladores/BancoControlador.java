package abate.abate.controladores;

import abate.abate.entidades.Banco;
import abate.abate.entidades.Banco.EstadoBanco;
import abate.abate.entidades.Usuario;
import abate.abate.servicios.BancoServicio;
import java.util.ArrayList;
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
@RequestMapping("/banco")
@PreAuthorize("hasAnyRole('ROLE_ADMIN')")
public class BancoControlador {

    @Autowired
    private BancoServicio bancoServicio;

    @GetMapping("/listar")
    public String banco(@RequestParam(required = false, defaultValue = "false") Boolean registrado,
            @RequestParam(required = false, defaultValue = "false") Boolean actualizado, ModelMap modelo, HttpSession session) {

        Usuario usuario = (Usuario) session.getAttribute("usuariosession");

        Long idOrg = usuario.getIdOrg();

        ArrayList<Banco> lista = bancoServicio.buscarBancos(idOrg);

        modelo.addAttribute("lista", lista);
        
        if (registrado == true){
            
        modelo.put("exito", "Banco registrado correctamente.");
            
        }
        
        if (actualizado == true){
            
        modelo.put("exito", "Banco actualizado correctamente.");
            
        }

        return "banco_listar.html";

    }

    @GetMapping("/registrar-banco")
    public String registrarBanco(ModelMap modelo) {

        modelo.addAttribute("estados", EstadoBanco.values());

        return "banco_registrar.html";

    }

    @PostMapping("/guardar-banco")
    public String guardarBanco(@RequestParam String nombre,
            @RequestParam EstadoBanco estado, HttpSession session, ModelMap modelo) {

        try {

            Usuario usuario = (Usuario) session.getAttribute("usuariosession");

            Long idOrg = usuario.getIdOrg();

            bancoServicio.validarDatos(idOrg, nombre);

            Banco banco = new Banco();

            banco.setIdOrg(idOrg);

            banco.setNombre(nombre.toUpperCase());

            banco.setEstado(estado);

            bancoServicio.registrarBanco(banco);

            Boolean registrado = true;

            return "redirect:/banco/listar?&registrado=" + registrado;

        } catch (Exception e) {

            modelo.put("error", e.getMessage());
            modelo.addAttribute("estados", EstadoBanco.values());

            return "banco_registrar.html";

        }

    }

    @GetMapping("/modificar-banco/{id}")
    public String modificarBanco(@PathVariable Long id, ModelMap modelo) throws Exception {

        Banco banco = bancoServicio.buscarPorId(id);

        if (banco != null) {

            modelo.addAttribute("banco", banco);
            modelo.addAttribute("estados", EstadoBanco.values());

            return "banco_modificar.html";

        }

        return "redirect:/banco/listar";

    }

    @PostMapping("/actualizar-banco")
    public String actualizarBanco(@RequestParam Long id, @RequestParam String nombre,
            @RequestParam EstadoBanco estado, ModelMap modelo) throws Exception {

        try {

            Banco banco = bancoServicio.buscarPorId(id);

            bancoServicio.validarDatosModificar(banco, nombre);

            banco.setNombre(nombre.toUpperCase());

            banco.setEstado(estado);

            bancoServicio.registrarBanco(banco);

            Boolean actualizado = true;

            return "redirect:/banco/listar?&actualizado=" + actualizado;

        } catch (Exception e) {

            modelo.addAttribute("banco", bancoServicio.buscarPorId(id));
            modelo.addAttribute("estados", EstadoBanco.values());
            modelo.put("error", e.getMessage());

            return "banco_modificar.html";

        }
        
    }

}
