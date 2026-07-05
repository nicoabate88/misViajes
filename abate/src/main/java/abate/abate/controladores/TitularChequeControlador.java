
package abate.abate.controladores;

import abate.abate.entidades.TitularCheque;
import abate.abate.entidades.TitularCheque.EstadoTitular;
import abate.abate.entidades.Usuario;
import abate.abate.servicios.TitularChequeServicio;
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
@RequestMapping("/titularCheque")
@PreAuthorize("hasAnyRole('ROLE_ADMIN')")
public class TitularChequeControlador {
    
    @Autowired
    private TitularChequeServicio titularServicio;

    @GetMapping("/listar")
    public String titular(@RequestParam(required = false, defaultValue = "false") Boolean registrado,
            @RequestParam(required = false, defaultValue = "false") Boolean actualizado, ModelMap modelo, HttpSession session) {

        Usuario usuario = (Usuario) session.getAttribute("usuariosession");

        Long idOrg = usuario.getIdOrg();

        ArrayList<TitularCheque> lista = titularServicio.buscarTitulares(idOrg);

        modelo.addAttribute("lista", lista);
        
        if (registrado == true){
            
        modelo.put("exito", "Titular de Cheque registrado correctamente.");
            
        }
        
        if (actualizado == true){
            
        modelo.put("exito", "Titular de Cheque actualizado correctamente.");
            
        }

        return "titularCheque_listar.html";

    }

    @GetMapping("/registrar-titular")
    public String registrarTitular(ModelMap modelo) {

        modelo.addAttribute("estados", EstadoTitular.values());

        return "titularCheque_registrar.html";

    }

    @PostMapping("/guardar-titular")
    public String guardarTitular(@RequestParam String nombre,
            @RequestParam EstadoTitular estado, HttpSession session, ModelMap modelo) {

        try {

            Usuario usuario = (Usuario) session.getAttribute("usuariosession");

            Long idOrg = usuario.getIdOrg();

            titularServicio.validarDatos(idOrg, nombre);

            TitularCheque titular = new TitularCheque();

            titular.setIdOrg(idOrg);

            titular.setNombre(nombre.toUpperCase());

            titular.setEstado(estado);

            titularServicio.registrarTitular(titular);

            Boolean registrado = true;

            return "redirect:/titularCheque/listar?&registrado=" + registrado;

        } catch (Exception e) {

            modelo.put("error", e.getMessage());
            modelo.addAttribute("estados", EstadoTitular.values());

            return "titularCheque_registrar.html";

        }

    }

    @GetMapping("/modificar-titular/{id}")
    public String modificarBanco(@PathVariable Long id, ModelMap modelo) throws Exception {

        TitularCheque titular = titularServicio.buscarPorId(id);

        if (titular != null) {

            modelo.addAttribute("titular", titular);
            modelo.addAttribute("estados", EstadoTitular.values());

            return "titularCheque_modificar.html";

        }

        return "redirect:/titularCheque/listar";

    }

    @PostMapping("/actualizar-titular")
    public String actualizarTitular(@RequestParam Long id, @RequestParam String nombre,
            @RequestParam EstadoTitular estado, ModelMap modelo) throws Exception {

        try {

            TitularCheque titular = titularServicio.buscarPorId(id);

            titularServicio.validarDatosModificar(titular, nombre);

            titular.setNombre(nombre.toUpperCase());

            titular.setEstado(estado);

            titularServicio.registrarTitular(titular);

            Boolean actualizado = true;

            return "redirect:/titularCheque/listar?&actualizado=" + actualizado;

        } catch (Exception e) {

            modelo.addAttribute("titular", titularServicio.buscarPorId(id));
            modelo.addAttribute("estados", EstadoTitular.values());
            modelo.put("error", e.getMessage());

            return "titularCheque_modificar.html";

        }
        
    }
    
}
