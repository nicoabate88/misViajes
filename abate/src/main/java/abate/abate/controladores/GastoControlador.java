package abate.abate.controladores;

import abate.abate.entidades.Camion;
import abate.abate.entidades.Detalle;
import abate.abate.entidades.Flete;
import abate.abate.entidades.Gasto;
import abate.abate.entidades.Usuario;
import abate.abate.servicios.CamionServicio;
import abate.abate.servicios.ChoferServicio;
import abate.abate.servicios.ExcelServicio;
import abate.abate.servicios.FleteServicio;
import abate.abate.servicios.GastoServicio;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
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
@RequestMapping("/gasto")
@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_CHOFER')")
public class GastoControlador {

    @Autowired
    private GastoServicio gastoServicio;
    @Autowired
    private FleteServicio fleteServicio;
    @Autowired
    private ChoferServicio choferServicio;
    @Autowired
    private CamionServicio camionServicio;
    @Autowired
    private ExcelServicio excelServicio;
    
    
    @GetMapping("/registrarDesdeFlete/{id}")
    public String mostrarFormularioDeGasto(@PathVariable Long id, ModelMap modelo) {
        
        modelo.addAttribute("detalles", new ArrayList<Detalle>());
        modelo.put("idFlete", id);

        return "gasto_registrarFlete.html";
        
    }

    @PostMapping("/registroDesdeFlete")
    public String guardarGasto(@RequestParam Long idFlete, @RequestParam("conceptos[]") List<String> conceptos,
            @RequestParam("cantidades[]") List<Integer> cantidades,
            @RequestParam("precios[]") List<Double> precios, HttpSession session) {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        List<Detalle> detalles = new ArrayList<>();
        for (int i = 0; i < conceptos.size(); i++) {
            Detalle detalle = new Detalle();
            detalle.setConcepto(conceptos.get(i));
            detalle.setCantidad(cantidades.get(i));
            detalle.setPrecio(precios.get(i));
            detalle.setTotal(cantidades.get(i) * precios.get(i));
            detalles.add(detalle);
        }

        gastoServicio.registrarGastoFlete(detalles, idFlete, logueado);

        return "redirect:/gasto/registradoDesdeFlete/" + idFlete;
    }

    @GetMapping("/registradoDesdeFlete/{idFlete}")
    public String gastoRegistrado(@PathVariable Long idFlete, ModelMap modelo, HttpSession session) {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");
        
        Gasto gasto = gastoServicio.buscarUltimoGasto(logueado.getIdOrg());
        
        modelo.put("idFlete", idFlete);
        modelo.put("gasto", gasto);
        modelo.put("exito", "Gasto REGISTRADO con éxito");
        
        if (logueado.getRol().equalsIgnoreCase("CHOFER")) {
            
            return "gasto_mostrarChofer.html";
        
        } else {
            
            return "gasto_mostrarPendiente.html";
            
        }  

    }
    
    @PostMapping("/registroAdmin")
    public String guardarAdmin(@RequestParam Long idFlete, @RequestParam String desde, @RequestParam String hasta, @RequestParam(required = false) Long idChofer,
           @RequestParam(required = false) Long idCamion, @RequestParam(required = false) Long idCliente, @RequestParam("conceptos[]") List<String> conceptos,
            @RequestParam("cantidades[]") List<Integer> cantidades,
            @RequestParam("precios[]") List<Double> precios, HttpSession session) {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        List<Detalle> detalles = new ArrayList<>();
        for (int i = 0; i < conceptos.size(); i++) {
            Detalle detalle = new Detalle();
            detalle.setConcepto(conceptos.get(i));
            detalle.setCantidad(cantidades.get(i));
            detalle.setPrecio(precios.get(i));
            detalle.setTotal(cantidades.get(i) * precios.get(i));
            detalles.add(detalle);
        }

        gastoServicio.registrarGastoFlete(detalles, idFlete, logueado);

        return "redirect:/gasto/registradoAdmin?&idFlete=" + idFlete + "&desde=" + desde + "&hasta=" + hasta +
           (idChofer != null ? "&idChofer=" + idChofer : "") +
           (idCamion != null ? "&idCamion=" + idCamion : "") +
           (idCliente != null ? "&idCliente=" + idCliente : "");
    }
    
    @GetMapping("/registradoAdmin")
    public String gastoRegistradoAdmin(@RequestParam Long idFlete, @RequestParam String desde, @RequestParam String hasta, @RequestParam(required = false) Long idChofer,
           @RequestParam(required = false) Long idCamion, @RequestParam(required = false) Long idCliente, ModelMap modelo, HttpSession session) {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");
        
        modelo.put("gasto", gastoServicio.buscarUltimoGasto(logueado.getIdOrg()));
        modelo.put("idFlete", idFlete);
        modelo.put("desde", desde);
        modelo.put("hasta", hasta);
        modelo.put("idChofer", idChofer);
        modelo.put("idCamion", idCamion);
        modelo.put("idCliente", idCliente);
        modelo.put("exito", "Gasto REGISTRADO con éxito");

        return "gasto_mostrarAdmin.html";

    }

    @GetMapping("/verChofer/{id}")
    public String verChofer(@PathVariable Long id, ModelMap modelo) {

        Flete flete = fleteServicio.buscarFlete(id);
        modelo.put("idFlete", id);

        if (flete.getGasto() != null) {
            modelo.put("gasto", gastoServicio.buscarGasto(flete.getGasto().getId()));

            return "gasto_mostrarChofer.html";

        } else if (flete.getGasto() == null && flete.getEstado().equalsIgnoreCase("PENDIENTE")) {

            modelo.addAttribute("detalles", new ArrayList<Detalle>());

            return "gasto_registrarFlete.html";

        } else {

            return "gasto_mensaje.html";
            
        }
    }
    
    @GetMapping("/modificarAdmin")
    public String modificarAdmin(@RequestParam Long id, @RequestParam Long idFlete, @RequestParam String desde, @RequestParam String hasta, 
        @RequestParam(required = false) Long idChofer, @RequestParam(required = false) Long idCamion, @RequestParam(required = false) Long idCliente, ModelMap modelo) {
        
        modelo.put("gasto", gastoServicio.buscarGasto(id));
        modelo.put("idFlete", idFlete);
        modelo.put("desde", desde);
        modelo.put("hasta", hasta);
        modelo.put("idChofer", idChofer);
        modelo.put("idCamion", idCamion);
        modelo.put("idCliente", idCliente);

        return "gasto_modificarAdmin.html";

    }
    
    @PostMapping("/modificaAdmin")
    public String modificaAdmin(@RequestParam Long idGasto, @RequestParam Long idFlete,  @RequestParam String desde, @RequestParam String hasta, 
        @RequestParam(required = false) Long idChofer, @RequestParam(required = false) Long idCamion, @RequestParam(required = false) Long idCliente,
            @RequestParam("conceptos[]") List<String> conceptos, @RequestParam("cantidades[]") List<Integer> cantidades,
            @RequestParam("precios[]") List<Double> precios, HttpSession session) {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        List<Detalle> detalles = new ArrayList<>();
        for (int i = 0; i < conceptos.size(); i++) {
            Detalle detalle = new Detalle();
            detalle.setConcepto(conceptos.get(i));
            detalle.setCantidad(cantidades.get(i));
            detalle.setPrecio(precios.get(i));
            detalle.setTotal(cantidades.get(i) * precios.get(i));
            detalles.add(detalle);
        }

        gastoServicio.modificarGastoFlete(idGasto, detalles, logueado);

        return "redirect:/gasto/modificadoAdmin?&idGasto=" + idGasto + "&idFlete=" + idFlete + "&desde=" + desde + "&hasta=" + hasta +
           (idChofer != null ? "&idChofer=" + idChofer : "") +
           (idCamion != null ? "&idCamion=" + idCamion : "") +
           (idCliente != null ? "&idCliente=" + idCliente : "");
    }
    
    @GetMapping("/modificadoAdmin")
    public String gastoModificadoAdmin(@RequestParam Long idGasto, @RequestParam Long idFlete,  @RequestParam String desde, @RequestParam String hasta, 
        @RequestParam(required = false) Long idChofer, @RequestParam(required = false) Long idCamion, @RequestParam(required = false) Long idCliente, ModelMap modelo) {

        modelo.put("gasto", gastoServicio.buscarGasto(idGasto));
        modelo.put("idFlete", idFlete);
        modelo.put("desde", desde);
        modelo.put("hasta", hasta);
        modelo.put("idChofer", idChofer);
        modelo.put("idCamion", idCamion);
        modelo.put("idCliente", idCliente);
        modelo.put("exito", "Gasto MODIFICADO con éxito");

        return "gasto_mostrarAdmin.html";

    }
    
    @GetMapping("/eliminarAdmin")
    public String eliminarAdmin(@RequestParam Long id, @RequestParam Long idFlete,  @RequestParam String desde, @RequestParam String hasta, 
        @RequestParam(required = false) Long idChofer, @RequestParam(required = false) Long idCamion, @RequestParam(required = false) Long idCliente, ModelMap modelo) {

        modelo.put("gasto", gastoServicio.buscarGasto(id));
        modelo.put("idFlete", idFlete);
        modelo.put("desde", desde);
        modelo.put("hasta", hasta);
        modelo.put("idChofer", idChofer);
        modelo.put("idCamion", idCamion);
        modelo.put("idCliente", idCliente);

        return "gasto_eliminarAdmin.html";

    }
    
    @GetMapping("/eliminaAdmin")
    public String eliminaAdmin(@RequestParam Long id, @RequestParam Long idFlete,  @RequestParam String desde, @RequestParam String hasta, 
        @RequestParam(required = false) Long idChofer, @RequestParam(required = false) Long idCamion, @RequestParam(required = false) Long idCliente, ModelMap modelo) {

        gastoServicio.eliminarGastoFlete(id, idFlete);

        return "redirect:/flete/mostrarAdmin?&id=" + idFlete + "&desde=" + desde + "&hasta=" + hasta +
           (idChofer != null ? "&idChofer=" + idChofer : "") +
           (idCamion != null ? "&idCamion=" + idCamion : "") +
           (idCliente != null ? "&idCliente=" + idCliente : "") +
                "&gasto=" + "si";
    }

    @GetMapping("/modificarDesdeFlete/{id}")
    public String modificar(@PathVariable Long id, ModelMap modelo) {

        Flete flete = fleteServicio.buscarFlete(id);

        modelo.put("idFlete", id);
        modelo.put("gasto", flete.getGasto());

        return "gasto_modificarDesdeFlete.html";

    }
    
    @GetMapping("/modificarPendiente/{id}")
    public String modificarPendiente(@PathVariable Long id, ModelMap modelo) {

        Flete flete = fleteServicio.buscarFlete(id);

        modelo.put("idFlete", id);
        modelo.put("gasto", gastoServicio.buscarGasto(flete.getGasto().getId()));

        return "gasto_modificarPendiente.html";

    }

    @PostMapping("/modificaDesdeFlete")
    public String modificarGasto(@RequestParam Long idFlete, @RequestParam Long idGasto,
            @RequestParam("conceptos[]") List<String> conceptos, @RequestParam("cantidades[]") List<Integer> cantidades,
            @RequestParam("precios[]") List<Double> precios, HttpSession session) {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        List<Detalle> detalles = new ArrayList<>();
        for (int i = 0; i < conceptos.size(); i++) {
            Detalle detalle = new Detalle();
            detalle.setConcepto(conceptos.get(i));
            detalle.setCantidad(cantidades.get(i));
            detalle.setPrecio(precios.get(i));
            detalle.setTotal(cantidades.get(i) * precios.get(i));
            detalles.add(detalle);
        }

        gastoServicio.modificarGastoFlete(idGasto, detalles, logueado);
        
        if (logueado.getRol().equalsIgnoreCase("CHOFER")) {

        return "redirect:/gasto/modificadoDesdeFlete/" + idFlete;
        
        } else {
        
        return "redirect:/gasto/modificadoPendiente/" + idFlete;
            
        }
  
    }
    
    @GetMapping("/modificadoPendiente/{idFlete}")
    public String gastoModificadoPendiente(@PathVariable Long idFlete, ModelMap modelo) {

        Flete flete = fleteServicio.buscarFlete(idFlete);

        modelo.put("idFlete", idFlete);
        modelo.put("gasto", gastoServicio.buscarGasto(flete.getGasto().getId()));
        modelo.put("exito", "Gasto MODIFICADO con éxito");

        return "gasto_mostrarPendiente.html";

    }

    @GetMapping("/modificadoDesdeFlete/{idFlete}")
    public String gastoModificado(@PathVariable Long idFlete, ModelMap modelo) {

        Flete flete = fleteServicio.buscarFlete(idFlete);

        modelo.put("idFlete", idFlete);
        modelo.put("gasto", flete.getGasto());
        modelo.put("exito", "Gasto MODIFICADO con éxito");

        return "gasto_mostrarChofer.html";

    }
    
    @GetMapping("/eliminarDesdeFlete/{id}")
    public String eliminar(@PathVariable Long id, ModelMap modelo) {

        Flete flete = fleteServicio.buscarFlete(id);

        modelo.put("gasto", flete.getGasto());
        modelo.put("idFlete", id);

        return "gasto_eliminarDesdeFlete.html";

    }
    
    @GetMapping("/eliminarPendiente/{id}")
    public String eliminarPendiente(@PathVariable Long id, ModelMap modelo) {

        Flete flete = fleteServicio.buscarFlete(id);

        modelo.put("gasto", gastoServicio.buscarGasto(flete.getGasto().getId()));
        modelo.put("idFlete", id);

        return "gasto_eliminarPendiente.html";

    }

    @GetMapping("/eliminaDesdeFlete/{idFlete}")
    public String elimina(@PathVariable Long idFlete, ModelMap modelo) {

        Flete flete = fleteServicio.buscarFlete(idFlete);

        gastoServicio.eliminarGastoFlete(flete.getGasto().getId(), idFlete);

        return "redirect:/gasto/eliminadoDesdeFlete/" +idFlete;

    }

    @GetMapping("/eliminadoDesdeFlete/{idFlete}")
    public String gastoEliminado(@PathVariable Long idFlete, ModelMap modelo, HttpSession session) {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        if (logueado.getRol().equalsIgnoreCase("CHOFER")) {

            modelo.put("flete", fleteServicio.buscarFlete(idFlete));
            modelo.put("exito", "Gasto ELIMINADO con éxito");

            return "flete_mostrarChofer.html";

        } else {

            modelo.put("flete", fleteServicio.buscarFlete(idFlete));
            modelo.put("exito", "Gasto ELIMINADO con éxito");

            return "flete_mostrarPendiente.html";
        }

    }
    
    @GetMapping("/registrarDesdeCajaAdmin/{idChofer}")
    public String registrarCaja(@PathVariable Long idChofer, ModelMap modelo) {
       
        Usuario chofer = choferServicio.buscarChofer(idChofer);
        
        modelo.addAttribute("detalles", new ArrayList<Detalle>());
        modelo.put("chofer", chofer);
        modelo.addAttribute("camiones", camionServicio.buscarCamionesAsc(chofer.getIdOrg()));

        return "gasto_registrarCajaAdmin.html";
        
    }
    
    @PostMapping("/registroDesdeCajaAdmin")
    public String registroCajaAdmin(@RequestParam Long idChofer, @RequestParam String fecha, @RequestParam Long idCamion, 
            @RequestParam("conceptos[]") List<String> conceptos, @RequestParam("cantidades[]") List<Integer> cantidades,
            @RequestParam("precios[]") List<Double> precios, HttpSession session) throws ParseException {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        List<Detalle> detalles = new ArrayList<>();
        for (int i = 0; i < conceptos.size(); i++) {
            Detalle detalle = new Detalle();
            detalle.setConcepto(conceptos.get(i));
            detalle.setCantidad(cantidades.get(i));
            detalle.setPrecio(precios.get(i));
            detalle.setTotal(cantidades.get(i) * precios.get(i));
            detalles.add(detalle);
        }

        gastoServicio.registrarGastoCaja(idChofer, fecha, idCamion, detalles, logueado);

        return "redirect:/gasto/registradoDesdeCajaAdmin";
    }
    
    @GetMapping("/registradoDesdeCajaAdmin")
    public String registradoCajaAdmin(ModelMap modelo, HttpSession session) {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");
        
        Gasto gasto = gastoServicio.buscarUltimoGasto(logueado.getIdOrg());
        
        modelo.put("gasto", gasto);
        modelo.put("exito", "Gasto REGISTRADO con éxito");

        return "gasto_mostrarAdminCaja.html";

    }
    
    @GetMapping("/registrarDesdeCaja/{id}")
    public String registrarCaja(@PathVariable Long id, ModelMap modelo, HttpSession session) {
        
        Usuario logueado = (Usuario) session.getAttribute("usuariosession");
        
        if (logueado.getRol().equalsIgnoreCase("CHOFER")) {
        
        modelo.addAttribute("detalles", new ArrayList<Detalle>());
        modelo.put("chofer", logueado);
        modelo.addAttribute("camiones", camionServicio.buscarCamionesAsc(logueado.getIdOrg()));

        return "gasto_registrarCajaChofer.html";
        
        } else {
            
        modelo.addAttribute("detalles", new ArrayList<Detalle>());
        modelo.put("chofer", choferServicio.buscarChofer(id));
        modelo.addAttribute("camiones", camionServicio.buscarCamionesAsc(logueado.getIdOrg()));

        return "gasto_registrarCajaAdmin.html";
            
        }
        
    }

    @PostMapping("/registroDesdeCaja")
    public String registroCaja(@RequestParam Long idChofer, @RequestParam String fecha, @RequestParam Long idCamion, 
            @RequestParam("conceptos[]") List<String> conceptos, @RequestParam("cantidades[]") List<Integer> cantidades,
            @RequestParam("precios[]") List<Double> precios, HttpSession session) throws ParseException {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        List<Detalle> detalles = new ArrayList<>();
        for (int i = 0; i < conceptos.size(); i++) {
            Detalle detalle = new Detalle();
            detalle.setConcepto(conceptos.get(i));
            detalle.setCantidad(cantidades.get(i));
            detalle.setPrecio(precios.get(i));
            detalle.setTotal(cantidades.get(i) * precios.get(i));
            detalles.add(detalle);
        }

        gastoServicio.registrarGastoCaja(idChofer, fecha, idCamion, detalles, logueado);

        return "redirect:/gasto/registradoDesdeCaja";
    }

    @GetMapping("/registradoDesdeCaja")
    public String registradoCaja(ModelMap modelo, HttpSession session) {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");
        
        Gasto gasto = gastoServicio.buscarUltimoGasto(logueado.getIdOrg());
        
        modelo.put("gasto", gasto);
        modelo.put("exito", "Gasto REGISTRADO con éxito");

        return "gasto_mostrarChoferCaja.html";

    }
    
    @GetMapping("/mostrarChoferCaja/{id}")
    public String mostrarGastoCaja(@PathVariable Long id, ModelMap modelo){
        
        modelo.put("gasto", gastoServicio.buscarGasto(id));

        return "gasto_mostrarChoferCaja.html";
        
    }
    
    @GetMapping("/mostrarAdminCaja/{id}")
    public String mostrarGastoAdmin(@PathVariable Long id, ModelMap modelo){
        
        modelo.put("gasto", gastoServicio.buscarGasto(id));

        return "gasto_mostrarAdminCaja.html";
        
    }
    
    @GetMapping("/modificarDesdeCajaAdmin/{id}")
    public String modificarCajaAdmin(@PathVariable Long id, ModelMap modelo) {

        Gasto gasto = gastoServicio.buscarGasto(id);
        
        modelo.put("gasto", gasto);
        modelo.addAttribute("camiones", camionServicio.buscarCamionesAsc(gasto.getIdOrg()));

        return "gasto_modificarDesdeCajaAdmin.html";

    }
    
    @PostMapping("/modificaDesdeCajaAdmin")
    public String modificaCajaAdmin(@RequestParam Long idGasto, @RequestParam String fecha, @RequestParam Long idCamion, 
            @RequestParam("conceptos[]") List<String> conceptos, @RequestParam("cantidades[]") List<Integer> cantidades,
            @RequestParam("precios[]") List<Double> precios, HttpSession session) throws ParseException {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        List<Detalle> detalles = new ArrayList<>();
        for (int i = 0; i < conceptos.size(); i++) {
            Detalle detalle = new Detalle();
            detalle.setConcepto(conceptos.get(i));
            detalle.setCantidad(cantidades.get(i));
            detalle.setPrecio(precios.get(i));
            detalle.setTotal(cantidades.get(i) * precios.get(i));
            detalles.add(detalle);
        }

        gastoServicio.modificarGastoCaja(idGasto, fecha, idCamion, detalles, logueado);

        return "redirect:/gasto/modificadoDesdeCajaAdmin/" + idGasto;
    }
        
    @GetMapping("/modificadoDesdeCajaAdmin/{idGasto}")
    public String modificadoCajaAdmin(@PathVariable Long idGasto, ModelMap modelo) {
        
        modelo.put("gasto", gastoServicio.buscarGasto(idGasto));
        modelo.put("exito", "Gasto MODIFICADO con éxito");

        return "transaccion_gastoAdmin.html";

    }
    
    @GetMapping("/modificarDesdeCaja/{id}")
    public String modificarCaja(@PathVariable Long id, ModelMap modelo) {

        Gasto gasto = gastoServicio.buscarGasto(id);
        
        modelo.put("gasto", gasto);
        modelo.addAttribute("camiones", camionServicio.buscarCamionesAsc(gasto.getIdOrg()));

        return "gasto_modificarDesdeCaja.html";

    }

    @PostMapping("/modificaDesdeCaja")
    public String modificaCaja(@RequestParam Long idGasto, @RequestParam String fecha, @RequestParam Long idCamion, 
            @RequestParam("conceptos[]") List<String> conceptos, @RequestParam("cantidades[]") List<Integer> cantidades,
            @RequestParam("precios[]") List<Double> precios, HttpSession session) throws ParseException {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        List<Detalle> detalles = new ArrayList<>();
        for (int i = 0; i < conceptos.size(); i++) {
            Detalle detalle = new Detalle();
            detalle.setConcepto(conceptos.get(i));
            detalle.setCantidad(cantidades.get(i));
            detalle.setPrecio(precios.get(i));
            detalle.setTotal(cantidades.get(i) * precios.get(i));
            detalles.add(detalle);
        }

        gastoServicio.modificarGastoCaja(idGasto, fecha, idCamion, detalles, logueado);

        return "redirect:/gasto/modificadoDesdeCaja/" + idGasto;
    }

    @GetMapping("/modificadoDesdeCaja/{idGasto}")
    public String modificadoCaja(@PathVariable Long idGasto, ModelMap modelo) {
        modelo.put("gasto", gastoServicio.buscarGasto(idGasto));
        modelo.put("exito", "Gasto MODIFICADO con éxito");

        return "gasto_mostrarChoferCaja.html";

    }
    
    @GetMapping("/eliminarDesdeCaja/{id}")
    public String eliminarCaja(@PathVariable Long id, ModelMap modelo, HttpSession session) {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");
        
        if (logueado.getRol().equalsIgnoreCase("CHOFER")) {
            
        modelo.put("gasto", gastoServicio.buscarGasto(id));

        return "gasto_eliminarDesdeCajaChofer.html";
        
        } else {
            
        modelo.put("gasto", gastoServicio.buscarGasto(id));

        return "gasto_eliminarDesdeCajaAdmin.html";
            
        }

    }
    
    @GetMapping("/eliminaDesdeCajaAdmin")
    public String eliminaCajaAdmin(@RequestParam Long idGasto, @RequestParam Long idChofer, ModelMap modelo) {

        gastoServicio.eliminarGastoCaja(idGasto);

        return "redirect:/caja/mostrarAdmin/" +idChofer;

    }

    @GetMapping("/eliminaDesdeCaja/{idGasto}")
    public String eliminaCaja(@PathVariable Long idGasto, ModelMap modelo) {

        gastoServicio.eliminarGastoCaja(idGasto);

        return "redirect:/gasto/eliminadoDesdeCaja/";

    }

    @GetMapping("/eliminadoDesdeCaja")
    public String gastoEliminadoCaja(ModelMap modelo, HttpSession session) {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");
            
            modelo.put("chofer", logueado);
            modelo.put("exito", "Gasto ELIMINADO con éxito");

            return "index_chofer.html";

    }
    
    @GetMapping("/modificarDesdeCuenta/{id}")
    public String modificarCuenta(@PathVariable Long id, ModelMap modelo) {
        
        modelo.put("gasto", gastoServicio.buscarGasto(id));

        return "gasto_modificarDesdeCuenta.html";

    }

    @PostMapping("/modificaDesdeCuenta")
    public String modificaCuenta(@RequestParam Long idGasto, 
            @RequestParam("conceptos[]") List<String> conceptos, @RequestParam("cantidades[]") List<Integer> cantidades,
            @RequestParam("precios[]") List<Double> precios, HttpSession session) throws ParseException {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        List<Detalle> detalles = new ArrayList<>();
        for (int i = 0; i < conceptos.size(); i++) {
            Detalle detalle = new Detalle();
            detalle.setConcepto(conceptos.get(i));
            detalle.setCantidad(cantidades.get(i));
            detalle.setPrecio(precios.get(i));
            detalle.setTotal(cantidades.get(i) * precios.get(i));
            detalles.add(detalle);
        }

        gastoServicio.modificarGastoFlete(idGasto, detalles, logueado);

        return "redirect:/gasto/modificadoDesdeCuenta/" + idGasto;
    }

    @GetMapping("/modificadoDesdeCuenta/{idGasto}")
    public String modificadoCuenta(@PathVariable Long idGasto, ModelMap modelo, HttpSession session) {
        
        Usuario logueado = (Usuario) session.getAttribute("usuariosession");
        
        if (logueado.getRol().equalsIgnoreCase("CHOFER")) {
            
        modelo.put("gasto", gastoServicio.buscarGasto(idGasto));
        modelo.put("exito", "Gasto MODIFICADO con éxito");

        return "transaccion_gastoChoferCuenta.html";
        
        } else {
            
        modelo.put("gasto", gastoServicio.buscarGasto(idGasto));
        modelo.put("exito", "Gasto MODIFICADO con éxito");

        return "transaccion_gastoAdminCuenta.html";
            
        }

    }
    
    @GetMapping("/eliminarDesdeCuenta/{id}")
    public String eliminarCuenta(@PathVariable Long id, ModelMap modelo, HttpSession session) {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");
        
        if (logueado.getRol().equalsIgnoreCase("CHOFER")) {
        modelo.put("gasto", gastoServicio.buscarGasto(id));

        return "gasto_eliminarDesdeCuentaChofer.html";
        
        } else {
            
        modelo.put("gasto", gastoServicio.buscarGasto(id));

        return "gasto_eliminarDesdeCuentaAdmin.html";
            
        }

    }

    @GetMapping("/eliminaDesdeCuenta/{idGasto}")
    public String eliminaCuenta(@PathVariable Long idGasto, ModelMap modelo) {
        
        Long idFlete = fleteServicio.buscarIdFleteIdGasto(idGasto);

        gastoServicio.eliminarGastoFlete(idGasto, idFlete);

        return "redirect:/gasto/eliminadoDesdeCuenta/";

    }

    @GetMapping("/eliminadoDesdeCuenta")
    public String gastoEliminadoCuenta(ModelMap modelo, HttpSession session) {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        if (logueado.getRol().equalsIgnoreCase("CHOFER")) {
            
            modelo.put("chofer", logueado);
            modelo.put("exito", "Gasto ELIMINADO con éxito");

            return "index_chofer.html";

        } else {

            modelo.put("id", logueado.getId());
            modelo.put("exito", "Gasto ELIMINADO con éxito");

            return "index_admin.html";
        }

    }
    
    @GetMapping("/verAdmin")
    public String verAdmin(@RequestParam Long id, @RequestParam String desde, @RequestParam String hasta, @RequestParam(required = false) Long idChofer,
           @RequestParam(required = false) Long idCamion, @RequestParam(required = false) Long idCliente, ModelMap modelo) {

        Flete flete = fleteServicio.buscarFlete(id);
        modelo.put("idFlete", id);
        modelo.put("desde", desde);
        modelo.put("hasta", hasta);
        modelo.put("idChofer", idChofer);
        modelo.put("idCamion", idCamion);
        modelo.put("idCliente", idCliente);

        if (flete.getGasto() != null) {

            modelo.put("gasto", gastoServicio.buscarGasto(flete.getGasto().getId()));

            return "gasto_mostrarAdmin.html";

        } else {

            modelo.addAttribute("detalles", new ArrayList<Detalle>());

            return "gasto_registrarAdmin.html";

        }
    }
    
    @GetMapping("/verPendiente/{id}")
    public String verPendiente(@PathVariable Long id, ModelMap modelo) {

        Flete flete = fleteServicio.buscarFlete(id);
        modelo.put("idFlete", id);

        if (flete.getGasto() != null) {

            modelo.put("gasto", gastoServicio.buscarGasto(flete.getGasto().getId()));

            return "gasto_mostrarPendiente.html";

        } else {

            modelo.addAttribute("detalles", new ArrayList<Detalle>());

            return "gasto_registrarPendiente.html";

        }
    }
    
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @GetMapping("/aceptar/{id}")
    public String aceptar(@PathVariable Long id, ModelMap modelo, HttpSession session) {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        gastoServicio.aceptarGastoCaja(id, logueado);
        
        return "redirect:/gasto/aceptado/" +id;

    }
    
    @GetMapping("/aceptado/{id}")
    public String gastoAceptado(@PathVariable Long id, ModelMap modelo) {

        Gasto gasto = gastoServicio.buscarGasto(id);

        modelo.put("gasto", gasto);

        return "transaccion_gastoAdmin.html";
        
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @GetMapping("/volverPendiente/{id}")
    public String volverPendiente(@PathVariable Long id, ModelMap modelo) {

        Gasto gasto = gastoServicio.buscarGasto(id);

        modelo.put("gasto", gasto);

        return "gasto_volverPendiente.html";

    }
    
    @GetMapping("/vuelvePendiente/{id}")
    public String vuelvePendiente(@PathVariable Long id, ModelMap modelo, HttpSession session) {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        gastoServicio.volverPendienteGasto(id, logueado);
        
        return "redirect:/gasto/pendiente/" +id;

    }
    
    @GetMapping("/pendiente/{id}")
    public String gastoPendiente(@PathVariable Long id, ModelMap modelo) {

        modelo.put("gasto", gastoServicio.buscarGasto(id));
        modelo.put("exito", "Gasto RETORNADO a Pendiente");

        return "transaccion_gastoAdmin.html";
        
    }

    @GetMapping("/listarCamion/{idCamion}")
    public String listarCamion(@PathVariable Long idCamion, ModelMap modelo) throws ParseException {

        String desde = obtenerFechaDesde();
        String hasta = obtenerFechaHasta();

        ArrayList<Gasto> lista = gastoServicio.buscarGastosCamion(idCamion, desde, hasta);
        Double total = 0.0;
        for (Gasto g : lista) {
            total = total + g.getImporte();
        }

        Boolean flag = true;
        if (lista.isEmpty()) {
            flag = false;
        }

        modelo.put("camion", camionServicio.buscarCamion(idCamion));
        modelo.addAttribute("gastos", lista);
        modelo.put("total", total);
        modelo.put("desde", desde);
        modelo.put("hasta", hasta);
        modelo.put("flag", flag);

        return "gasto_listarCamion.html";
    }

    @PostMapping("/listarCamionFiltro")
    public String listarCamionFiltro(@RequestParam Long idCamion, @RequestParam String desde,
            @RequestParam String hasta, ModelMap modelo) throws ParseException {

        ArrayList<Gasto> lista = gastoServicio.buscarGastosCamion(idCamion, desde, hasta);
        Double total = 0.0;
        for (Gasto g : lista) {
            total = total + g.getImporte();
        }

        Boolean flag = true;
        if (lista.isEmpty()) {
            flag = false;
        }

        modelo.put("camion", camionServicio.buscarCamion(idCamion));
        modelo.addAttribute("gastos", lista);
        modelo.put("total", total);
        modelo.put("desde", desde);
        modelo.put("hasta", hasta);
        modelo.put("flag", flag);

        return "gasto_listarCamion.html";
    }
    
    @GetMapping("/detalleCamion/{id}")
    public String detalleCamion(@PathVariable Long id, ModelMap modelo) throws ParseException {

            modelo.put("gasto", gastoServicio.buscarGasto(id));

            return "gasto_mostrarDesdeCamion.html";

    }

    @PostMapping("/exportar")
    public String exportar(@RequestParam Long idCamion, @RequestParam String desde,
            @RequestParam String hasta, ModelMap modelo) throws ParseException {

        modelo.addAttribute("gastos", gastoServicio.buscarGastosCamion(idCamion, desde, hasta));
        modelo.put("camion", camionServicio.buscarCamion(idCamion));
        modelo.put("desde", desde);
        modelo.put("hasta", hasta);

        return "gasto_exportar.html";

    }

    @PostMapping("/exporta")
    public void exporta(@RequestParam String desde, @RequestParam String hasta, @RequestParam Long idCamion,
            HttpServletResponse response) throws IOException, ParseException {

        ArrayList<Gasto> myObjects = gastoServicio.buscarGastosCamion(idCamion, desde, hasta);
        Camion camion = camionServicio.buscarCamion(idCamion);

        String htmlContent = generateHtmlFromObjects(myObjects);
        excelServicio.exportHtmlToExcelGasto(htmlContent, response, camion);

    }

    private String generateHtmlFromObjects(ArrayList<Gasto> objects) {
        StringBuilder sb = new StringBuilder();
        sb.append("<table>");
        sb.append("<thead><tr>"
                + "<th>Fecha</th>"
                + "<th>Concepto</th>"
                + "<th>Chofer</th>"
                + "<th>Importe</th>"
                + "</tr></thead>");
        sb.append("<tbody>");
        for (Gasto g : objects) {
            sb.append("<tr><td>").append(g.getFecha()).append("</td>"
                    + "<td>").append(g.getNombre()).append("</td>"
                    + "<td>").append(g.getChofer().getNombre()).append("</td>"
                    + "<td>").append(g.getImporte()).append("</td>"
                    + "</tr>");
        }
        sb.append("</tbody></table>");
        return sb.toString();
    }

        public String obtenerFechaDesde() {

        LocalDate now = LocalDate.now();

         LocalDate firstDayOfPreviousMonth = now.minusMonths(1).withDayOfMonth(1);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        String formattedDate = firstDayOfPreviousMonth.format(formatter);

        return formattedDate;

    }

    public String obtenerFechaHasta() {

        LocalDate now = LocalDate.now();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        String formattedToday = now.format(formatter);

        return formattedToday;

    }

    public Date convertirFecha(String fecha) throws ParseException {
        SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd");
        return formato.parse(fecha);
    }

}
