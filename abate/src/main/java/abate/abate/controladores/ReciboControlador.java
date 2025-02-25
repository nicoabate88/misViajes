package abate.abate.controladores;

import abate.abate.entidades.Cuenta;
import abate.abate.entidades.Recibo;
import abate.abate.entidades.Usuario;
import abate.abate.servicios.ClienteServicio;
import abate.abate.servicios.CuentaServicio;
import abate.abate.servicios.ReciboServicio;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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

        return "recibo_registrar.html";
    }

    @GetMapping("/registrarId/{id}")
    public String registrarReciboId(@PathVariable Long id, ModelMap modelo) {

        Cuenta cuenta = cuentaServicio.buscarCuentaCliente(id);

        modelo.put("cuenta", cuenta);

        return "recibo_registrarId.html";
    }

    @PostMapping("/registro")
    public String registroUsuario(@RequestParam Long idCliente, @RequestParam String fecha,
            @RequestParam Double importe, @RequestParam(required = false) String observacion,
            ModelMap modelo, HttpSession session) throws ParseException {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        reciboServicio.crearRecibo(logueado.getIdOrg(), idCliente, fecha, importe, observacion, logueado.getId());

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

        modelo.addAttribute("recibos", lista);
        modelo.put("desde", desde);
        modelo.put("hasta", hasta);
        modelo.put("id", logueado.getIdOrg());
        modelo.put("total", total);

        return "recibo_listar.html";
    }

    @PostMapping("/listarFiltro")
    public String listar(@RequestParam Long id, @RequestParam String desde, @RequestParam String hasta, ModelMap modelo, HttpSession session) throws ParseException {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");
        ArrayList<Recibo> lista = reciboServicio.buscarRecibos(logueado.getIdOrg(), desde, hasta);
        Double total = 0.0;
        for (Recibo r : lista) {
            total = r.getImporte() + total;
        }

        modelo.addAttribute("recibos", lista);
        modelo.put("desde", desde);
        modelo.put("hasta", hasta);
        modelo.put("id", logueado.getIdOrg());
        modelo.put("total", total);

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

    @GetMapping("/modificar/{id}")
    public String modificar(@PathVariable Long id, ModelMap modelo, HttpSession session) {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        modelo.put("recibo", reciboServicio.buscarRecibo(id));
        modelo.addAttribute("clientes", clienteServicio.buscarClientesNombreAsc(logueado.getIdOrg()));

        return "recibo_modificar.html";

    }

    @PostMapping("/modifica/{id}")
    public String modifica(@RequestParam Long id, @RequestParam Long idCliente, @RequestParam String fecha,
            @RequestParam Double importe, @RequestParam(required = false) String observacion,
            ModelMap modelo, HttpSession session) throws ParseException {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        reciboServicio.modificarRecibo(id, idCliente, fecha, importe, observacion, logueado.getId());

        return "redirect:/recibo/modificado/" + id;
    }

    @GetMapping("/modificado/{id}")
    public String reciboModificado(@PathVariable Long id, ModelMap modelo) {

        Recibo recibo = reciboServicio.buscarRecibo(id);

        modelo.put("recibo", recibo);
        modelo.put("exito", "Recibo MODIFICADO con éxito");

        return "recibo_registrado.html";
    }

    @GetMapping("/modificarDesdeCliente/{id}")
    public String modificarDesdeCliente(@PathVariable Long id, ModelMap modelo, HttpSession session) {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        modelo.put("recibo", reciboServicio.buscarRecibo(id));
        modelo.addAttribute("clientes", clienteServicio.buscarClientesNombreAsc(logueado.getIdOrg()));

        return "recibo_modificarDesdeCliente.html";

    }

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id, ModelMap modelo) {

        modelo.put("recibo", reciboServicio.buscarRecibo(id));

        return "recibo_eliminar.html";
    }

    @GetMapping("/elimina/{id}")
    public String elimina(@PathVariable Long id, HttpSession session, ModelMap modelo) {

        reciboServicio.eliminarRecibo(id);

        return "redirect:/recibo/eliminado";

    }
    
    @GetMapping("/eliminado")
    public String eliminado(ModelMap modelo, HttpSession session) {
        
        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        modelo.put("id", logueado.getId());
        modelo.put("exito", "Recibo ELIMINADO con éxito");

        return "index_admin.html";      

    }

    @GetMapping("/imprimir/{id}")
    public String imprimir(@PathVariable Long id, ModelMap modelo) {

        Recibo recibo = reciboServicio.buscarRecibo(id);

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
