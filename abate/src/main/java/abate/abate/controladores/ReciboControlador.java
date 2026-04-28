package abate.abate.controladores;

import abate.abate.entidades.Cuenta;
import abate.abate.entidades.Recibo;
import abate.abate.entidades.Usuario;
import abate.abate.entidades.Valor;
import abate.abate.entidades.Valor.TipoValor;
import abate.abate.servicios.ClienteServicio;
import abate.abate.servicios.CuentaServicio;
import abate.abate.servicios.ReciboServicio;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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
@RequestMapping("/recibo")
@PreAuthorize("hasAnyRole('ROLE_ADMIN')")
public class ReciboControlador {

    @Autowired
    private ClienteServicio clienteServicio;
    @Autowired
    private ReciboServicio reciboServicio;
    @Autowired
    private CuentaServicio cuentaServicio;

    @GetMapping("/registrar")
    public String registrarRecibo(ModelMap modelo, HttpSession session) {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        modelo.addAttribute("cuentas", cuentaServicio.buscarCuentasCliente(logueado.getIdOrg()));
        modelo.addAttribute("tiposValor", TipoValor.values());

        return "recibo_registrar.html";
    }

    @GetMapping("/registrarId/{id}")
    public String registrarReciboId(@PathVariable Long id, ModelMap modelo) {

        Cuenta cuenta = cuentaServicio.buscarCuentaCliente(id);

        modelo.put("cuenta", cuenta);
        modelo.addAttribute("tiposValor", TipoValor.values());

        return "recibo_registrarId.html";
    }

    @PostMapping("/registro")
    public String registroUsuario(@RequestParam Long idCliente, @RequestParam String fecha, @RequestParam(required = false) List<Double> importes,
            @RequestParam(required = false) List<String> observacionesValores, @RequestParam(required = false) List<TipoValor> tipos,
            @RequestParam(required = false) String observacion, ModelMap modelo, HttpSession session) throws ParseException {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        List<Valor> valores = new ArrayList<>();

        if (importes != null) {
            for (int i = 0; i < importes.size(); i++) {

                if (importes.get(i) != null && importes.get(i) > 0) {

                    Valor v = new Valor();
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

        reciboServicio.crearRecibo(logueado.getIdOrg(), idCliente, fecha, valores, observacion, logueado);

        return "redirect:/recibo/registrado";
    }

    @GetMapping("/registrado")
    public String reciboRegistrado(HttpSession session, ModelMap modelo) {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        Long id = reciboServicio.buscarUltimo(logueado.getIdOrg());
        Recibo recibo = reciboServicio.buscarRecibo(id);

        modelo.put("recibo", recibo);
        modelo.put("exito", "Recibo REGISTRADO con éxito");

        return "recibo_registrado.html";

    }

    @GetMapping("/listar")
    public String listar(ModelMap modelo, HttpSession session) throws ParseException {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");
        String desde = obtenerFechaDesde();
        String hasta = obtenerFechaHasta();
        ArrayList<Recibo> lista = reciboServicio.buscarRecibos(logueado.getIdOrg(), desde, hasta);
        Double total = 0.0;
        for (Recibo r : lista) {
            total = r.getImporte() + total;
        }

        modelo.addAttribute("clientes", clienteServicio.buscarClientesHabNombreAsc(logueado.getIdOrg()));
        modelo.addAttribute("recibos", lista);
        modelo.put("idCliente", null);
        modelo.put("desde", desde);
        modelo.put("hasta", hasta);
        modelo.put("total", total);
        modelo.put("cliente", null);

        return "recibo_listar.html";
    }

    @GetMapping("/listarFiltro")
    public String listar(@RequestParam String desde, @RequestParam String hasta, @RequestParam(required = false) Long idCliente,
            @RequestParam(required = false) String elimina, ModelMap modelo, HttpSession session) throws ParseException {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");
        Double total = 0.0;
        ArrayList<Recibo> lista = new ArrayList();

        if (idCliente == null || idCliente == 0) {

            lista = reciboServicio.buscarRecibos(logueado.getIdOrg(), desde, hasta);
            modelo.put("cliente", null);

        } else {

            lista = reciboServicio.buscarRecibosIdCliente(idCliente, desde, hasta);
            modelo.put("cliente", clienteServicio.buscarCliente(idCliente));

        }

        for (Recibo r : lista) {
            total = r.getImporte() + total;
        }

        modelo.addAttribute("clientes", clienteServicio.buscarClientesHabNombreAsc(logueado.getIdOrg()));
        modelo.addAttribute("recibos", lista);
        modelo.put("idCliente", idCliente);
        modelo.put("desde", desde);
        modelo.put("hasta", hasta);
        modelo.put("id", logueado.getIdOrg());
        modelo.put("total", total);
        if (elimina != null) {
            modelo.put("exito", "Recibo ELIMINADO con éxito");
        }

        return "recibo_listar.html";
    }

    @GetMapping("/listarIdCliente/{id}")
    public String listarIdCliente(@PathVariable Long id, ModelMap modelo) throws ParseException {

        String desde = obtenerFechaDesde();
        String hasta = obtenerFechaHasta();
        ArrayList<Recibo> lista = reciboServicio.buscarRecibosIdCliente(id, desde, hasta);
        Double total = 0.0;
        for (Recibo r : lista) {
            total = r.getImporte() + total;
        }

        modelo.addAttribute("recibos", lista);
        modelo.put("cliente", clienteServicio.buscarCliente(id));
        modelo.put("desde", desde);
        modelo.put("hasta", hasta);
        modelo.put("total", total);

        return "recibo_listarCliente.html";
    }

    @PostMapping("/listarIdClienteFiltro")
    public String listarIdClienteFiltro(@RequestParam Long id, @RequestParam String desde, @RequestParam String hasta, ModelMap modelo) throws ParseException {

        ArrayList<Recibo> lista = reciboServicio.buscarRecibosIdCliente(id, desde, hasta);
        Double total = 0.0;
        for (Recibo r : lista) {
            total = r.getImporte() + total;
        }

        modelo.addAttribute("recibos", lista);
        modelo.put("cliente", clienteServicio.buscarCliente(id));
        modelo.put("desde", desde);
        modelo.put("hasta", hasta);
        modelo.put("total", total);

        return "recibo_listarCliente.html";
    }

    @GetMapping("/modificar")
    public String modificar(@RequestParam Long id, @RequestParam String desde, @RequestParam String hasta, 
            @RequestParam(required = false) Long idCliente, ModelMap modelo, HttpSession session) {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        modelo.put("recibo", reciboServicio.buscarRecibo(id));
        modelo.addAttribute("clientes", clienteServicio.buscarClientesNombreAsc(logueado.getIdOrg()));
        modelo.addAttribute("tiposValor", TipoValor.values());
        modelo.put("idCliente", idCliente);
        modelo.put("desde", desde);
        modelo.put("hasta", hasta);

        return "recibo_modificar.html";

    }

    @PostMapping("/modifica")
    public String modifica(@RequestParam Long id, @RequestParam String desde, @RequestParam String hasta, @RequestParam(required = false) Long idClient,
            @RequestParam Long idCliente, @RequestParam String fecha,
            @RequestParam(required = false) List<Double> importes, @RequestParam(required = false) List<String> observacionesValores,
            @RequestParam(required = false) List<TipoValor> tipos, @RequestParam(required = false) String observacion,
            ModelMap modelo, HttpSession session) throws ParseException {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        List<Valor> valores = new ArrayList<>();

        if (importes != null) {
            for (int i = 0; i < importes.size(); i++) {

                if (importes.get(i) != null && importes.get(i) > 0) {

                    Valor v = new Valor();
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

        reciboServicio.modificarRecibo(id, idCliente, fecha, valores, observacion, logueado);
        
        if(idClient == null){
            idClient = 0L;
        }

        return "redirect:/recibo/modificado?id=" + id + "&desde=" + desde + "&hasta=" + hasta + "&idCliente=" + idClient;
    }

    @GetMapping("/modificado")
    public String reciboModificado(@RequestParam Long id, @RequestParam String desde, @RequestParam String hasta, @RequestParam(required = false) Long idCliente, ModelMap modelo) {

        Recibo recibo = reciboServicio.buscarRecibo(id);
        
        if(idCliente == 0){
            idCliente = null;
        }

        modelo.put("recibo", recibo);
        modelo.put("exito", "Recibo MODIFICADO con éxito");
        modelo.put("idCliente", idCliente);
        modelo.put("desde", desde);
        modelo.put("hasta", hasta);

        return "recibo_mostrar.html";
    }
    
       @GetMapping("/modificarCuenta")
    public String modificarCuenta(@RequestParam Long id, @RequestParam String desde, @RequestParam String hasta, 
            @RequestParam Long idCuenta, ModelMap modelo, HttpSession session) {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        modelo.put("recibo", reciboServicio.buscarRecibo(id));
        modelo.addAttribute("clientes", clienteServicio.buscarClientesNombreAsc(logueado.getIdOrg()));
        modelo.addAttribute("tiposValor", TipoValor.values());
        modelo.put("idCuenta", idCuenta);
        modelo.put("desde", desde);
        modelo.put("hasta", hasta);

        return "recibo_modificarCuenta.html";

    }
    
        @PostMapping("/modificaCuenta")
    public String modificaCuenta(@RequestParam Long id, @RequestParam String desde, @RequestParam String hasta, @RequestParam(required = false) Long idCuenta,
            @RequestParam Long idCliente, @RequestParam String fecha,
            @RequestParam(required = false) List<Double> importes, @RequestParam(required = false) List<String> observacionesValores,
            @RequestParam(required = false) List<TipoValor> tipos, @RequestParam(required = false) String observacion,
            ModelMap modelo, HttpSession session) throws ParseException {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        List<Valor> valores = new ArrayList<>();

        if (importes != null) {
            for (int i = 0; i < importes.size(); i++) {

                if (importes.get(i) != null && importes.get(i) > 0) {

                    Valor v = new Valor();
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

        reciboServicio.modificarRecibo(id, idCliente, fecha, valores, observacion, logueado);

        return "redirect:/recibo/modificadoCuenta?id=" + id + "&desde=" + desde + "&hasta=" + hasta + "&idCuenta=" + idCuenta;
    }

    @GetMapping("/modificadoCuenta")
    public String reciboModificadoCuenta(@RequestParam Long id, @RequestParam String desde, @RequestParam String hasta, @RequestParam(required = false) Long idCuenta, ModelMap modelo) {

        Recibo recibo = reciboServicio.buscarRecibo(id);

        modelo.put("recibo", recibo);
        modelo.put("exito", "Recibo MODIFICADO con éxito");
        modelo.put("idCuenta", idCuenta);
        modelo.put("desde", desde);
        modelo.put("hasta", hasta);

        return "recibo_mostrarCuenta.html";
    }

    @GetMapping("/modificarDesdeCliente/{id}")
    public String modificarDesdeCliente(@PathVariable Long id, ModelMap modelo, HttpSession session) {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        modelo.addAttribute("tiposValor", TipoValor.values());
        modelo.put("recibo", reciboServicio.buscarRecibo(id));
        modelo.addAttribute("clientes", clienteServicio.buscarClientesNombreAsc(logueado.getIdOrg()));

        return "recibo_modificarDesdeCliente.html";

    }

    @GetMapping("/eliminar")
    public String eliminar(@RequestParam Long id, String desde, String hasta, @RequestParam(required = false) Long idCliente, ModelMap modelo) {

        modelo.put("recibo", reciboServicio.buscarRecibo(id));
        modelo.put("idCliente", idCliente);
        modelo.put("desde", desde);
        modelo.put("hasta", hasta);

        return "recibo_eliminar.html";
    }

    @GetMapping("/elimina")
    public String elimina(@RequestParam Long id, String desde, String hasta, @RequestParam(required = false) Long idCliente, HttpSession session, ModelMap modelo) {

        reciboServicio.eliminarRecibo(id);

        return "redirect:/recibo/listarFiltro?&desde=" + desde + "&hasta=" + hasta + "&idCliente=" + idCliente + "&elimina=" + "si";

    }
    
        @GetMapping("/eliminarCuenta")
    public String eliminarCuenta(@RequestParam Long id, String desde, String hasta, @RequestParam(required = false) Long idCuenta, ModelMap modelo) {

        modelo.put("recibo", reciboServicio.buscarRecibo(id));
        modelo.put("idCuenta", idCuenta);
        modelo.put("desde", desde);
        modelo.put("hasta", hasta);

        return "recibo_eliminarCuenta.html";
    }

    @GetMapping("/eliminaCuenta")
    public String eliminaCuenta(@RequestParam Long id, String desde, String hasta, @RequestParam(required = false) Long idCuenta, HttpSession session, ModelMap modelo) {

        reciboServicio.eliminarRecibo(id);
        
        if(idCuenta == null){
            idCuenta = 0L;
        }

        return "redirect:/cuenta/mostrarClienteFiltro?&id=" + idCuenta + "&desde=" + desde + "&hasta=" + hasta + "&elimina=" + "si";

    }

    @GetMapping("/imprimir/{id}")
    public String imprimir(@PathVariable Long id, ModelMap modelo, HttpSession session) {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        Recibo recibo = reciboServicio.buscarRecibo(id);
        modelo.addAttribute("flag", false);

        if (logueado.getLogo() != null) {

            Long idLogo = logueado.getLogo().getId();
            //  Imagen logo = imagenServicio.obtenerImagenPorId(idLogo);

            modelo.addAttribute("imagenUrl", "/imagen/img/bytes/" + idLogo);
            modelo.addAttribute("flag", true);

        }

        modelo.put("recibo", recibo);

        return "recibo_imprimir.html";
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

}
