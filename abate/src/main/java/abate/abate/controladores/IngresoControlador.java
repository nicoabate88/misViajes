package abate.abate.controladores;

import abate.abate.entidades.Caja;
import abate.abate.entidades.Ingreso;
import abate.abate.entidades.Usuario;
import abate.abate.entidades.ValorI;
import abate.abate.entidades.ValorI.TipoValorI;
import abate.abate.servicios.CajaServicio;
import abate.abate.servicios.IngresoServicio;
import java.text.ParseException;
import java.util.ArrayList;
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
@RequestMapping("/ingreso")
@PreAuthorize("hasAnyRole('ROLE_ADMIN')")
public class IngresoControlador {

    @Autowired
    private IngresoServicio ingresoServicio;
    @Autowired
    private CajaServicio cajaServicio;

    @GetMapping("/registrar/{id}")
    public String registrarIngreso(@PathVariable Long id, ModelMap modelo) {

        Caja caja = cajaServicio.buscarCaja(id);

        modelo.put("caja", caja);
        modelo.addAttribute("tiposValor", TipoValorI.values());

        return "ingreso_registrar.html";
    }

    @PostMapping("/registro")
    public String registroEntrega(@RequestParam Long idChofer, @RequestParam String fecha,
            @RequestParam(required = false) List<Double> importes,
            @RequestParam(required = false) List<String> observacionesValores, @RequestParam(required = false) List<TipoValorI> tipos, 
            @RequestParam(required = false) String observacion, ModelMap modelo, HttpSession session) throws ParseException {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");
        
        List<ValorI> valores = new ArrayList<>();

        if (importes != null) {
            for (int i = 0; i < importes.size(); i++) {

                if (importes.get(i) != null && importes.get(i) > 0) {

                    ValorI v = new ValorI();
                    v.setImporte(importes.get(i));

                    if (observacionesValores != null && observacionesValores.size() > i) {
                        v.setObservacion(observacionesValores.get(i));
                    }

                    if (tipos != null && tipos.size() > i) {
                        v.setTipo(tipos.get(i));
                    }

                    valores.add(v);
                }
            }
        }

        ingresoServicio.crearIngreso(logueado.getIdOrg(), idChofer, fecha, valores, observacion, logueado);

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

    @GetMapping("/modificar")
    public String modificar(@RequestParam Long id, @RequestParam Long idTransaccion, @RequestParam Long idCaja, 
            @RequestParam String desde, @RequestParam String hasta, ModelMap modelo) {

        modelo.put("ingreso", ingresoServicio.buscarIngreso(id));
        modelo.addAttribute("tiposValor", TipoValorI.values());
        modelo.put("idTransaccion", idTransaccion);
        modelo.put("idCaja", idCaja);
        modelo.put("desde", desde);
        modelo.put("hasta", hasta);

        return "ingreso_modificar.html";

    }

    @PostMapping("/modifica")
    public String modifica(@RequestParam Long id, @RequestParam Long idCaja, @RequestParam String desde, 
            @RequestParam String hasta, @RequestParam String fecha,
            @RequestParam(required = false) List<Double> importes, @RequestParam(required = false) List<String> observacionesValores,
            @RequestParam(required = false) List<TipoValorI> tipos, @RequestParam(required = false) String observacion,
            ModelMap modelo, HttpSession session) throws ParseException {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");
        
        List<ValorI> valores = new ArrayList<>();

        if (importes != null) {
            for (int i = 0; i < importes.size(); i++) {

                if (importes.get(i) != null && importes.get(i) > 0) {

                    ValorI v = new ValorI();
                    v.setImporte(importes.get(i));

                    if (tipos != null && tipos.size() > i) {
                        v.setTipo(tipos.get(i));
                    }

                    if (observacionesValores != null && observacionesValores.size() > i) {
                        v.setObservacion(observacionesValores.get(i));
                    }

                    valores.add(v);
                }
            }
        }

        ingresoServicio.modificarIngreso(id, fecha, valores, observacion, logueado);

        return "redirect:/ingreso/modificado?id=" + id + "&idCaja=" + idCaja + "&desde=" + desde + "&hasta=" + hasta;
    }

    @GetMapping("/modificado")
    public String ingresoModificado(@RequestParam Long id, @RequestParam Long idCaja, @RequestParam String desde, 
            @RequestParam String hasta, ModelMap modelo) {

        Ingreso ingreso = ingresoServicio.buscarIngreso(id);

        modelo.put("ingreso", ingreso);
        modelo.put("exito", "Ingreso de Caja MODIFICADO con éxito");
        modelo.put("idCaja", idCaja);
        modelo.put("desde", desde);
        modelo.put("hasta", hasta);

        return "ingreso_modificado.html";
    }
    
    

    @GetMapping("/eliminar")
    public String eliminar(@RequestParam Long id, @RequestParam Long idTransaccion, @RequestParam Long idCaja, 
            @RequestParam String desde, @RequestParam String hasta, ModelMap modelo) {

        modelo.put("ingreso", ingresoServicio.buscarIngreso(id));
        modelo.put("idTransaccion", idTransaccion);
        modelo.put("idCaja", idCaja);
        modelo.put("desde", desde);
        modelo.put("hasta", hasta);

        return "ingreso_eliminar.html";
    }

    @GetMapping("/elimina")
    public String elimina(@RequestParam Long id, @RequestParam Long idCaja, 
            @RequestParam String desde, @RequestParam String hasta, ModelMap modelo) {

        ingresoServicio.eliminarIngreso(id);
        
        return "redirect:/caja/mostrarFiltroAdminTodas?id=" + idCaja + "&desde=" + desde + "&hasta=" + hasta + "&elimina=" + "si";

    }

    @GetMapping("/imprimir/{id}")
    public String imprimir(@PathVariable Long id, ModelMap modelo, HttpSession session) {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");
        Ingreso ingreso = ingresoServicio.buscarIngreso(id);
        
        modelo.addAttribute("flag", false);
        
        if (logueado.getLogo() != null) {

            Long idLogo = logueado.getLogo().getId();
           // Imagen logo = imagenServicio.obtenerImagenPorId(idLogo);
                
            modelo.addAttribute("imagenUrl", "/imagen/img/bytes/" + idLogo);
            modelo.addAttribute("flag", true);
                
        }

        modelo.put("ingreso", ingreso);

        return "ingreso_imprimir.html";
    }
}
