package abate.abate.controladores;

import abate.abate.entidades.Cuenta;
import abate.abate.entidades.Flete;
import abate.abate.entidades.Gasto;
import abate.abate.entidades.Transaccion;
import abate.abate.entidades.Usuario;
import abate.abate.servicios.ChoferServicio;
import abate.abate.servicios.CuentaServicio;
import abate.abate.servicios.EntregaServicio;
import abate.abate.servicios.ExcelServicio;
import abate.abate.servicios.FleteServicio;
import abate.abate.servicios.GastoServicio;
import abate.abate.servicios.ReciboServicio;
import abate.abate.servicios.TransaccionServicio;
import java.io.IOException;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
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
@RequestMapping("/cuenta")
@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_CHOFER')")
public class CuentaControlador {

    @Autowired
    private CuentaServicio cuentaServicio;
    @Autowired
    private TransaccionServicio transaccionServicio;
    @Autowired
    private FleteServicio fleteServicio;
    @Autowired
    private ReciboServicio reciboServicio;
    @Autowired
    private GastoServicio gastoServicio;
    @Autowired
    private EntregaServicio entregaServicio;
    @Autowired
    private ExcelServicio excelServicio;
    @Autowired
    private ChoferServicio choferServicio;

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @GetMapping("/listarChofer")
    public String listarChofer(ModelMap modelo, HttpSession session) {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");
        Boolean flag = false;

        Double saldo = 0.0;
        ArrayList<Cuenta> cuentas = cuentaServicio.buscarCuentasChoferHab(logueado.getIdOrg());
        for (Cuenta c : cuentas) {
            saldo = saldo + c.getSaldo();
        }
        if(!cuentas.isEmpty()){
            flag = true;
        }

        modelo.put("flag", flag);

        modelo.addAttribute("cuentas", cuentas);
        modelo.put("saldo", saldo);

        return "cuenta_listarChofer.html";
    }
    
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @GetMapping("/listarChoferFiltro")
    public String listarChoferFiltro(@RequestParam(required = false) Long id, @RequestParam(required = false) Boolean inhabilitado,
            ModelMap modelo, HttpSession session) {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");
        boolean filtrarInhabilitados = Boolean.TRUE.equals(inhabilitado);
        Boolean flag = false;

        if (filtrarInhabilitados) {
        Double saldo = 0.0;
        ArrayList<Cuenta> cuentas = cuentaServicio.buscarCuentasChofer(logueado.getIdOrg());
        for (Cuenta c : cuentas) {
            saldo = saldo + c.getSaldo();
        }
        if(!cuentas.isEmpty()){
            flag = true;
        }

        modelo.put("flag", flag);
        modelo.addAttribute("cuentas", cuentas);
        modelo.put("saldo", saldo);
        modelo.put("inhabilitado", Boolean.TRUE.equals(inhabilitado));

        return "cuenta_listarChofer.html";

        } else if (id != null) {

            modelo.addAttribute("cuentas", cuentaServicio.buscarCuentasChoferHab(logueado.getIdOrg()));
            modelo.addAttribute("cuenta", cuentaServicio.buscarCuenta(id));

            return "cuenta_listarChoferFiltro.html";

        } else {
            
        Double saldo = 0.0;
        ArrayList<Cuenta> cuentas = cuentaServicio.buscarCuentasChoferHab(logueado.getIdOrg());
        for (Cuenta c : cuentas) {
            saldo = saldo + c.getSaldo();
        }
        if(!cuentas.isEmpty()){
            flag = true;
        }

        modelo.put("flag", flag);
        modelo.addAttribute("cuentas", cuentas);
        modelo.put("saldo", saldo);

        return "cuenta_listarChofer.html";

        }

    }

    @GetMapping("/mostrarChofer/{id}")
    public String mostrarChofer(@PathVariable Long id, ModelMap modelo) throws ParseException {

        Cuenta cuenta = cuentaServicio.buscarCuenta(id);
        String desde = obtenerFechaDesde();
        String hasta = obtenerFechaHasta();

        ArrayList<Transaccion> lista = transaccionServicio.buscarTransaccionIdCuentaFecha(id, desde, hasta);
        Boolean flag = true;
        if (lista.isEmpty()) {
            flag = false;
        }

        modelo.put("flag", flag);
        modelo.put("cuenta", cuenta);
        modelo.put("desde", desde);
        modelo.put("hasta", hasta);
        modelo.addAttribute("transacciones", lista);

        return "cuenta_mostrarChoferAdmin.html";

    }

    @PostMapping("/mostrarChoferFiltroAdmin")
    public String mostrarChoferFiltroAdmin(@RequestParam Long id, @RequestParam String desde, @RequestParam String hasta, ModelMap modelo) throws ParseException {

        Boolean flag = true;

        ArrayList<Transaccion> lista = transaccionServicio.buscarTransaccionIdCuentaFecha(id, desde, hasta);

        if (lista.isEmpty()) {
            flag = false;
        }

        modelo.put("flag", flag);
        modelo.put("cuenta", cuentaServicio.buscarCuenta(id));
        modelo.put("desde", desde);
        modelo.put("hasta", hasta);
        modelo.addAttribute("transacciones", lista);

        return "cuenta_mostrarChoferFiltroAdmin.html";

    }

    @PostMapping("/mostrarIdChoferFiltroAdmin")
    public String mostrarIdChoferFiltroAdmin(@RequestParam Long id, @RequestParam String desde, @RequestParam String hasta, ModelMap modelo) throws ParseException {

        Boolean flag = true;

        ArrayList<Transaccion> lista = transaccionServicio.buscarTransaccionIdCuentaFecha(id, desde, hasta);

        if (lista.isEmpty()) {
            flag = false;
        }

        modelo.put("flag", flag);
        modelo.put("cuenta", cuentaServicio.buscarCuenta(id));
        modelo.put("desde", desde);
        modelo.put("hasta", hasta);
        modelo.addAttribute("transacciones", lista);

        return "cuenta_mostrarIdChoferFiltroAdmin.html";

    }

    @PostMapping("/mostrarChoferFiltro")
    public String mostrarChoferFiltro(@RequestParam Long id, @RequestParam String desde, @RequestParam String hasta, ModelMap modelo) throws ParseException {

        Boolean flag = true;

        ArrayList<Transaccion> lista = transaccionServicio.buscarTransaccionIdCuentaFecha(id, desde, hasta);

        if (lista.isEmpty()) {
            flag = false;
        }

        modelo.put("flag", flag);
        modelo.put("cuenta", cuentaServicio.buscarCuenta(id));
        modelo.put("desde", desde);
        modelo.put("hasta", hasta);
        modelo.addAttribute("transacciones", lista);

        return "cuenta_mostrarChoferFiltro.html";
    }

    @GetMapping("/mostrarIdChofer/{id}")
    public String mostrarIdChofer(@PathVariable Long id, ModelMap modelo) throws ParseException {

        Long idCuenta = cuentaServicio.buscarIdCuentaChofer(id);
        Cuenta cuenta = cuentaServicio.buscarCuenta(idCuenta);
        Boolean flag = true;
        String desde = obtenerFechaDesde();
        String hasta = obtenerFechaHasta();

        ArrayList<Transaccion> lista = transaccionServicio.buscarTransaccionIdCuentaFecha(idCuenta, desde, hasta);
        if (lista.isEmpty()) {
            flag = false;
        }

        modelo.put("chofer", choferServicio.buscarChofer(id));
        modelo.put("flag", flag);
        modelo.put("cuenta", cuenta);
        modelo.put("desde", desde);
        modelo.put("hasta", hasta);
        modelo.addAttribute("transacciones", lista);

        return "cuenta_mostrarChofer.html";

    }

    @GetMapping("/mostrarIdChoferAdmin/{id}")
    public String mostrarIdChoferAdmin(@PathVariable Long id, ModelMap modelo) throws ParseException {

        Long idCuenta = cuentaServicio.buscarIdCuentaChofer(id);
        Cuenta cuenta = cuentaServicio.buscarCuenta(idCuenta);
        String desde = obtenerFechaDesde();
        String hasta = obtenerFechaHasta();

        ArrayList<Transaccion> lista = transaccionServicio.buscarTransaccionIdCuentaFecha(idCuenta, desde, hasta);
        Boolean flag = true;
        if (lista.isEmpty()) {
            flag = false;
        }

        modelo.put("flag", flag);
        modelo.put("cuenta", cuenta);
        modelo.put("desde", desde);
        modelo.put("hasta", hasta);
        modelo.addAttribute("transacciones", lista);

        return "cuenta_mostrarIdChoferAdmin.html";

    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @GetMapping("/listarCliente")
    public String listarCliente(ModelMap modelo, HttpSession session) {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");
        Boolean flag = false;

        Double saldo = 0.0;
        ArrayList<Cuenta> cuentas = cuentaServicio.buscarCuentasClienteHab(logueado.getIdOrg());
        for (Cuenta c : cuentas) {
            saldo = saldo + c.getSaldo();
        }
        if(!cuentas.isEmpty()){
            flag = true;
        }

        modelo.put("flag", flag);
        modelo.addAttribute("cuentas", cuentas);
        modelo.put("saldo", saldo);

        return "cuenta_listarCliente.html";
    }
    
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @GetMapping("/listarClienteFiltro")
    public String listarClienteFiltro(@RequestParam(required = false) Long id, @RequestParam(required = false) Boolean inhabilitado,
            ModelMap modelo, HttpSession session) {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");
        boolean filtrarInhabilitados = Boolean.TRUE.equals(inhabilitado);
        Boolean flag = false;

        if (filtrarInhabilitados) {
        Double saldo = 0.0;
        ArrayList<Cuenta> cuentas = cuentaServicio.buscarCuentasCliente(logueado.getIdOrg());
        for (Cuenta c : cuentas) {
            saldo = saldo + c.getSaldo();
        }
        if(!cuentas.isEmpty()){
            flag = true;
        }

        modelo.put("flag", flag);
        modelo.addAttribute("cuentas", cuentas);
        modelo.put("saldo", saldo);
        modelo.put("inhabilitado", Boolean.TRUE.equals(inhabilitado));

        return "cuenta_listarCliente.html";

        } else if (id != null) {

            modelo.addAttribute("cuentas", cuentaServicio.buscarCuentasClienteHab(logueado.getIdOrg()));
            modelo.addAttribute("cuenta", cuentaServicio.buscarCuenta(id));

            return "cuenta_listarClienteFiltro.html";

        } else {
            
        Double saldo = 0.0;
        ArrayList<Cuenta> cuentas = cuentaServicio.buscarCuentasClienteHab(logueado.getIdOrg());
        for (Cuenta c : cuentas) {
            saldo = saldo + c.getSaldo();
        }
        if(!cuentas.isEmpty()){
            flag = true;
        }

        modelo.put("flag", flag);
        modelo.addAttribute("cuentas", cuentas);
        modelo.put("saldo", saldo);

        return "cuenta_listarCliente.html";

        }

    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @GetMapping("/mostrarCliente/{id}")
    public String mostrarCliente(@PathVariable Long id, ModelMap modelo) throws ParseException {

        Cuenta cuenta = cuentaServicio.buscarCuenta(id);
        String desde = obtenerFechaDesde();
        String hasta = obtenerFechaHasta();

        ArrayList<Transaccion> lista = transaccionServicio.buscarTransaccionIdCuentaFecha(id, desde, hasta);
        Boolean flag = true;
        if (lista.isEmpty()) {
            flag = false;
        }

        modelo.put("flag", flag);
        modelo.put("cuenta", cuenta);
        modelo.put("desde", desde);
        modelo.put("hasta", hasta);
        modelo.addAttribute("transacciones", lista);

        return "cuenta_mostrarCliente.html";

    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @GetMapping("/mostrarIdCliente/{id}")
    public String mostrarIdCliente(@PathVariable Long id, ModelMap modelo) throws ParseException {

        Long idCuenta = cuentaServicio.buscarIdCuentaCliente(id);
        Cuenta cuenta = cuentaServicio.buscarCuenta(idCuenta);
        String desde = obtenerFechaDesde();
        String hasta = obtenerFechaHasta();

        ArrayList<Transaccion> lista = transaccionServicio.buscarTransaccionIdCuentaFecha(idCuenta, desde, hasta);
        Boolean flag = true;
        if (lista.isEmpty()) {
            flag = false;
        }

        modelo.put("flag", flag);
        modelo.put("cuenta", cuenta);
        modelo.put("desde", desde);
        modelo.put("hasta", hasta);
        modelo.addAttribute("transacciones", lista);

        return "cuenta_mostrarIdCliente.html";

    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @PostMapping("/mostrarClienteFiltro")
    public String mostrarClienteFiltro(@RequestParam Long id, @RequestParam String desde, @RequestParam String hasta, ModelMap modelo) throws ParseException {

        Boolean flag = true;

        ArrayList<Transaccion> lista = transaccionServicio.buscarTransaccionIdCuentaFecha(id, desde, hasta);

        if (lista.isEmpty()) {
            flag = false;
        }

        modelo.put("flag", flag);
        modelo.put("cuenta", cuentaServicio.buscarCuenta(id));
        modelo.put("desde", desde);
        modelo.put("hasta", hasta);
        modelo.addAttribute("transacciones", lista);

        return "cuenta_mostrarClienteFiltro.html";

    }

    @PostMapping("/mostrarIdClienteFiltro")
    public String mostrarIdClienteFiltro(@RequestParam Long id, @RequestParam String desde, @RequestParam String hasta, ModelMap modelo) throws ParseException {

        Boolean flag = true;

        ArrayList<Transaccion> lista = transaccionServicio.buscarTransaccionIdCuentaFecha(id, desde, hasta);

        if (lista.isEmpty()) {
            flag = false;
        }

        modelo.put("flag", flag);
        modelo.put("cuenta", cuentaServicio.buscarCuenta(id));
        modelo.put("desde", desde);
        modelo.put("hasta", hasta);
        modelo.addAttribute("transacciones", lista);

        return "cuenta_mostrarIdClienteFiltro.html";

    }

    @GetMapping("/mostrarTransaccionCliente/{id}")
    public String mostrarTransaccion(@PathVariable Long id, ModelMap modelo) {

        Transaccion transaccion = transaccionServicio.buscarTransaccion(id);

        if (transaccion.getFlete() != null) {

            modelo.put("flete", fleteServicio.buscarFlete(transaccion.getFlete().getId()));

            return "transaccion_fleteCliente.html";

        } else {

            Double valorAbsoluto = Math.abs(transaccion.getImporte());

            modelo.put("importe", valorAbsoluto);
            modelo.put("recibo", reciboServicio.buscarRecibo(transaccion.getRecibo().getId()));

            return "transaccion_recibo.html";
        }

    }

    @GetMapping("/mostrarTransaccionClienteDesdeCuenta/{id}")
    public String mostrarTransaccionCliente(@PathVariable Long id, ModelMap modelo) {

        Transaccion transaccion = transaccionServicio.buscarTransaccion(id);

        if (transaccion.getFlete() != null) {

            modelo.put("idCuenta", cuentaServicio.buscarIdCuentaCliente(transaccion.getCliente().getId()));
            modelo.put("flete", fleteServicio.buscarFlete(transaccion.getFlete().getId()));

            return "transaccion_fleteClienteCuenta.html";

        } else {

            Double valorAbsoluto = Math.abs(transaccion.getImporte());
            modelo.put("idCuenta", cuentaServicio.buscarIdCuentaCliente(transaccion.getCliente().getId()));
            modelo.put("importe", valorAbsoluto);
            modelo.put("recibo", reciboServicio.buscarRecibo(transaccion.getRecibo().getId()));

            return "transaccion_reciboCuenta.html";
        }

    }

    @GetMapping("/mostrarTransaccionChoferAdmin/{id}")
    public String mostrarTransaccionChoferAdmin(@PathVariable Long id, ModelMap modelo) {

        Transaccion transaccion = transaccionServicio.buscarTransaccion(id);

        if (transaccion.getFlete() != null) {

            modelo.put("flete", fleteServicio.buscarFlete(transaccion.getFlete().getId()));

            return "transaccion_fleteChoferAdmin.html";

        }

        if (transaccion.getEntrega() != null) {


            modelo.put("entrega", entregaServicio.buscarEntrega(transaccion.getEntrega().getId()));
            modelo.put("importe", Math.abs(transaccion.getImporte()));

            return "transaccion_entregaAdmin.html";

        } else {

            Gasto gasto = gastoServicio.buscarGasto(transaccion.getGasto().getId());
            Flete flete = fleteServicio.buscarFleteIdGasto(gasto.getId());

            modelo.put("idFlete", flete.getId());
            modelo.put("gasto", gasto);

            return "transaccion_gastoAdminCuenta.html";

        }

    }

    @GetMapping("/mostrarTransaccionDesdeCuenta/{id}")
    public String mostrarTransaccionChoferAdminDesdeCuenta(@PathVariable Long id, ModelMap modelo) {

        Transaccion transaccion = transaccionServicio.buscarTransaccion(id);
        Long idCuenta = cuentaServicio.buscarIdCuentaChofer(transaccion.getChofer().getId());

        if (transaccion.getFlete() != null) {

            modelo.put("flete", fleteServicio.buscarFlete(transaccion.getFlete().getId()));
            modelo.put("idCuenta", idCuenta);

            return "transaccion_fleteCuenta.html";

        }

        if (transaccion.getEntrega() != null) {

            modelo.put("entrega", entregaServicio.buscarEntrega(transaccion.getEntrega().getId()));
            modelo.put("importe", Math.abs(transaccion.getImporte()));
            modelo.put("idCuenta", idCuenta);

            return "transaccion_entregaCuenta.html";

        } else {

            Gasto gasto = gastoServicio.buscarGasto(transaccion.getGasto().getId());
            Flete flete = fleteServicio.buscarFleteIdGasto(gasto.getId());

            modelo.put("idFlete", flete.getId());
            modelo.put("gasto", gasto);
            modelo.put("idCuenta", idCuenta);

            return "transaccion_gastoCuenta.html";

        }

    }

    @GetMapping("/mostrarTransaccionChofer/{id}")
    public String mostrarTransaccionChofer(@PathVariable Long id, ModelMap modelo) {

        Transaccion transaccion = transaccionServicio.buscarTransaccion(id);

        if (transaccion.getFlete() != null) {

            modelo.put("flete", fleteServicio.buscarFlete(transaccion.getFlete().getId()));

            return "transaccion_fleteChofer.html";

        }

        if (transaccion.getEntrega() != null) {

            modelo.put("entrega", entregaServicio.buscarEntrega(transaccion.getEntrega().getId()));
            modelo.put("importe", Math.abs(transaccion.getImporte()));

            return "transaccion_entrega.html";

        } else {

            Gasto gasto = gastoServicio.buscarGasto(transaccion.getGasto().getId());
            Flete flete = fleteServicio.buscarFleteIdGasto(gasto.getId());

            modelo.put("idFlete", flete.getId());
            modelo.put("gasto", gasto);

            return "transaccion_gastoChoferCuenta.html";

        }

    }
    
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @GetMapping("/habilitar/{id}")
    public String habilitarCuenta(@PathVariable Long id, ModelMap modelo) {

        modelo.put("cuenta", cuentaServicio.buscarCuentaChofer(id));

        return "cuenta_habilitar.html";

    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @GetMapping("/habilita/{id}")
    public String habilitaCaja(@PathVariable Long id, ModelMap modelo) {

        Cuenta cuenta = cuentaServicio.buscarCuenta(id);

        choferServicio.habilitarCuentaChofer(cuenta.getChofer().getId());
        
        return "redirect:/cuenta/habilitado/" +id;

    }
    
    @GetMapping("/habilitado/{id}")
    public String habilitado(@PathVariable Long id, ModelMap modelo) {

        modelo.put("cuenta", cuentaServicio.buscarCuenta(id));
        modelo.put("exito", "Cuenta de Chofer HABILITADA con éxito");

        return "cuenta_habilitada.html";
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @GetMapping("/inhabilitar/{id}")
    public String inhabilitarCuenta(@PathVariable Long id, ModelMap modelo) {

        modelo.put("cuenta", cuentaServicio.buscarCuentaChofer(id));

        return "cuenta_inhabilitar.html";

    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @GetMapping("/inhabilita/{id}")
    public String inhabilitaCuenta(@PathVariable Long id, ModelMap modelo) {

        Cuenta cuenta = cuentaServicio.buscarCuenta(id);

        choferServicio.inhabilitarCuentaChofer(cuenta.getChofer().getId());

        return "redirect:/cuenta/inhabilitado/" +id;

    }
    
    @GetMapping("/inhabilitado/{id}")
    public String inhabilitado(@PathVariable Long id, ModelMap modelo) {

        modelo.put("cuenta", cuentaServicio.buscarCuenta(id));
        modelo.put("exito", "Cuenta de Chofer INHABILITADA con éxito");

        return "cuenta_habilitada.html";
    }

    @PostMapping("/exportarFiltro")
    public String exportarFiltro(@RequestParam Long id, @RequestParam String desde, @RequestParam String hasta, ModelMap modelo) throws ParseException {

        ArrayList<Transaccion> lista = transaccionServicio.buscarTransaccionIdCuentaFecha(id, desde, hasta);

        modelo.put("cuenta", cuentaServicio.buscarCuenta(id));
        modelo.put("desde", desde);
        modelo.put("hasta", hasta);
        modelo.addAttribute("transacciones", lista);

        return "cuenta_exportarFiltro.html";

    }

    @PostMapping("/exportarTodoAdmin")
    public String exportarTodoAdmin(@RequestParam Long id, ModelMap modelo) throws ParseException {

        Cuenta cuenta = cuentaServicio.buscarCuenta(id);
        String desde = obtenerFechaDesde();
        String hasta = obtenerFechaHasta();

        modelo.put("cuenta", cuenta);
        modelo.addAttribute("transacciones", transaccionServicio.buscarTransaccionIdCuentaFecha(id, desde, hasta));

        return "cuenta_exportarTodoAdmin.html";

    }

    @PostMapping("/exportaTodoAdmin")
    public void exportaTodoAdmin(@RequestParam Long id, HttpServletResponse response) throws IOException, ParseException {

        Cuenta cuenta = cuentaServicio.buscarCuenta(id);
        String desde = obtenerFechaDesde();
        String hasta = obtenerFechaHasta();

        ArrayList<Transaccion> myObjects = transaccionServicio.buscarTransaccionIdCuentaFecha(id, desde, hasta);
        String htmlContent = generateHtmlFromObjects(myObjects);
        excelServicio.exportHtmlToExcelCuenta(htmlContent, response, cuenta.getChofer().getNombre(), cuenta.getSaldo());

    }
    
    @GetMapping("/imprimirTodoAdmin")
    public String imprimirTodoAdmin(@RequestParam Long idCuenta, ModelMap modelo) throws ParseException {
        
        Cuenta cuenta = cuentaServicio.buscarCuenta(idCuenta);
        String desde = obtenerFechaDesde();
        String hasta = obtenerFechaHasta();

        modelo.put("cuenta", cuenta);
        modelo.addAttribute("transacciones", transaccionServicio.buscarTransaccionIdCuentaFecha(idCuenta, desde, hasta));

        return "cuenta_imprimirTodoAdmin.html";   
        
    }

    @PostMapping("/exportarFiltroAdmin")
    public String exportarFiltroAdmin(@RequestParam Long id, @RequestParam String desde, @RequestParam String hasta, ModelMap modelo) throws ParseException {

        ArrayList<Transaccion> lista = transaccionServicio.buscarTransaccionIdCuentaFecha(id, desde, hasta);

        modelo.put("cuenta", cuentaServicio.buscarCuenta(id));
        modelo.put("desde", desde);
        modelo.put("hasta", hasta);
        modelo.addAttribute("transacciones", lista);

        return "cuenta_exportarFiltroAdmin.html";

    }

    @PostMapping("/exportarIdFiltroAdmin")
    public String exportarIdFiltroAdmin(@RequestParam Long id, @RequestParam String desde, @RequestParam String hasta, ModelMap modelo) throws ParseException {

        ArrayList<Transaccion> lista = transaccionServicio.buscarTransaccionIdCuentaFecha(id, desde, hasta);

        modelo.put("cuenta", cuentaServicio.buscarCuenta(id));
        modelo.put("desde", desde);
        modelo.put("hasta", hasta);
        modelo.addAttribute("transacciones", lista);

        return "cuenta_exportarIdFiltroAdmin.html";

    }

    @PostMapping("/exportaFiltroAdmin")
    public void exportaFiltroAdmin(@RequestParam Long id, @RequestParam String desde, @RequestParam String hasta, HttpServletResponse response) throws IOException, ParseException {

        Cuenta cuenta = cuentaServicio.buscarCuenta(id);

        ArrayList<Transaccion> lista = transaccionServicio.buscarTransaccionIdCuentaFecha(id, desde, hasta);

        ArrayList<Transaccion> myObjects = lista;
        String htmlContent = generateHtmlFromObjects(myObjects);
        excelServicio.exportHtmlToExcelCuentaMovimiento(htmlContent, response, cuenta.getChofer().getNombre(), desde, hasta);

    }
    
    @GetMapping("/imprimirFiltroAdmin")
    public String imprimirFiltroAdmin(@RequestParam Long idCuenta, @RequestParam String desde, @RequestParam String hasta, ModelMap modelo) throws ParseException {
        
        Cuenta cuenta = cuentaServicio.buscarCuenta(idCuenta);

        ArrayList<Transaccion> lista = transaccionServicio.buscarTransaccionIdCuentaFecha(idCuenta, desde, hasta);

        modelo.put("cuenta", cuenta);
        modelo.addAttribute("transacciones", lista);
        modelo.put("desde", desde);
        modelo.put("hasta", hasta);

        return "cuenta_imprimirFiltroAdmin.html";   
        
    }

    @PostMapping("/exportarTodoCliente")
    public String exportarTodoCliente(@RequestParam Long id, ModelMap modelo) throws ParseException {

        Cuenta cuenta = cuentaServicio.buscarCuenta(id);
        String desde = obtenerFechaDesde();
        String hasta = obtenerFechaHasta();

        modelo.put("cuenta", cuenta);
        modelo.addAttribute("transacciones", transaccionServicio.buscarTransaccionIdCuentaFecha(id, desde, hasta));

        return "cuenta_exportarTodoCliente.html";

    }

    @PostMapping("/exportaTodoCliente")
    public void exportaTodoCliente(@RequestParam Long id, HttpServletResponse response) throws IOException, ParseException {

        Cuenta cuenta = cuentaServicio.buscarCuenta(id);
        String desde = obtenerFechaDesde();
        String hasta = obtenerFechaHasta();

        ArrayList<Transaccion> myObjects = transaccionServicio.buscarTransaccionIdCuentaFecha(id, desde, hasta);
        String htmlContent = generateHtmlFromObjects(myObjects);
        excelServicio.exportHtmlToExcelCuenta(htmlContent, response, cuenta.getCliente().getNombre(), cuenta.getSaldo());

    }
    
    @GetMapping("/imprimirTodoCliente")
    public String imprimirTodoCliente(@RequestParam Long idCuenta, ModelMap modelo) throws ParseException {
        
        Cuenta cuenta = cuentaServicio.buscarCuenta(idCuenta);
        String desde = obtenerFechaDesde();
        String hasta = obtenerFechaHasta();

        modelo.put("cuenta", cuenta);
        modelo.addAttribute("transacciones", transaccionServicio.buscarTransaccionIdCuentaFecha(idCuenta, desde, hasta));

        return "cuenta_imprimirTodoCliente.html";   
        
    }

    @PostMapping("/exportarFiltroCliente")
    public String exportarFiltroCliente(@RequestParam Long id, @RequestParam String desde, @RequestParam String hasta, ModelMap modelo) throws ParseException {

        ArrayList<Transaccion> lista = transaccionServicio.buscarTransaccionIdCuentaFecha(id, desde, hasta);

        modelo.put("cuenta", cuentaServicio.buscarCuenta(id));
        modelo.put("desde", desde);
        modelo.put("hasta", hasta);
        modelo.addAttribute("transacciones", lista);

        return "cuenta_exportarFiltroCliente.html";

    }

    @PostMapping("/exportarIdFiltroCliente")
    public String exportarIdFiltroCliente(@RequestParam Long id, @RequestParam String desde, @RequestParam String hasta, ModelMap modelo) throws ParseException {

        ArrayList<Transaccion> lista = transaccionServicio.buscarTransaccionIdCuentaFecha(id, desde, hasta);

        modelo.put("cuenta", cuentaServicio.buscarCuenta(id));
        modelo.put("desde", desde);
        modelo.put("hasta", hasta);
        modelo.addAttribute("transacciones", lista);

        return "cuenta_exportarIdFiltroCliente.html";

    }

    @PostMapping("/exportaFiltroCliente")
    public void exportaFiltroCliente(@RequestParam Long id, @RequestParam String desde, @RequestParam String hasta, HttpServletResponse response) throws IOException, ParseException {

        Cuenta cuenta = cuentaServicio.buscarCuenta(id);

        ArrayList<Transaccion> lista = transaccionServicio.buscarTransaccionIdCuentaFecha(id, desde, hasta);

        ArrayList<Transaccion> myObjects = lista;
        String htmlContent = generateHtmlFromObjects(myObjects);
        excelServicio.exportHtmlToExcelCuentaMovimiento(htmlContent, response, cuenta.getCliente().getNombre(), desde, hasta);

    }
    
    @GetMapping("/imprimirFiltroCliente")
    public String imprimirFIltroCliente(@RequestParam Long idCuenta, @RequestParam String desde, @RequestParam String hasta, ModelMap modelo) throws ParseException {
        
        ArrayList<Transaccion> lista = transaccionServicio.buscarTransaccionIdCuentaFecha(idCuenta, desde, hasta);

        modelo.put("cuenta", cuentaServicio.buscarCuenta(idCuenta));
        modelo.put("desde", desde);
        modelo.put("hasta", hasta);
        modelo.addAttribute("transacciones", lista);

        return "cuenta_imprimirFiltroCliente.html";   
        
    }
    
    @PostMapping("/exportarCliente")
    public String exportarCliente(ModelMap modelo, HttpSession session) throws ParseException {
        
        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        ArrayList<Cuenta> cuentas = cuentaServicio.buscarCuentasCliente(logueado.getIdOrg());

        modelo.addAttribute("cuentas", cuentas);

        return "cuenta_exportarCliente.html";

    }

    @PostMapping("/exportaCliente")
    public void exportaCliente(HttpServletResponse response, HttpSession session) throws IOException, ParseException {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        ArrayList<Cuenta> cuentas = cuentaServicio.buscarCuentasCliente(logueado.getIdOrg());
        
        String htmlContent = generateHtmlFromObjectsClientes(cuentas);
        excelServicio.exportHtmlToExcelClientes(htmlContent, response);

    }
    
    @GetMapping("/imprimirCliente")
    public String imprimirCliente(ModelMap modelo, HttpSession session) throws ParseException {
        
        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        ArrayList<Cuenta> cuentas = cuentaServicio.buscarCuentasCliente(logueado.getIdOrg());

        modelo.addAttribute("cuentas", cuentas);

        return "cuenta_imprimirCliente.html";
        
    }
    
    @PostMapping("/exportarChofer")
    public String exportarChofer(ModelMap modelo, HttpSession session) throws ParseException {
        
        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        ArrayList<Cuenta> cuentas = cuentaServicio.buscarCuentasChofer(logueado.getIdOrg());

        modelo.addAttribute("cuentas", cuentas);

        return "cuenta_exportarChofer.html";

    }

    @PostMapping("/exportaChofer")
    public void exportaChofer(HttpServletResponse response, HttpSession session) throws IOException, ParseException {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        ArrayList<Cuenta> cuentas = cuentaServicio.buscarCuentasChofer(logueado.getIdOrg());
        
        String htmlContent = generateHtmlFromObjectsChoferes(cuentas);
        excelServicio.exportHtmlToExcelChoferes(htmlContent, response);

    }
    
    @GetMapping("/imprimirChofer")
    public String imprimirChofer(ModelMap modelo, HttpSession session) throws ParseException {
        
        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        ArrayList<Cuenta> cuentas = cuentaServicio.buscarCuentasChofer(logueado.getIdOrg());

        modelo.addAttribute("cuentas", cuentas);

        return "cuenta_imprimirChofer.html";
        
    }

    private String generateHtmlFromObjects(ArrayList<Transaccion> objects) {
        StringBuilder sb = new StringBuilder();
        sb.append("<table>");
        sb.append("<thead><tr>"
                + "<th>Fecha</th>"
                + "<th>Concepto</th>"
                + "<th>Importe</th>"
                + "<th>Saldo</th>"
                + "</tr></thead>");
        sb.append("<tbody>");
        for (Transaccion transaccion : objects) {
            sb.append("<tr><td>").append(transaccion.getFecha()).append("</td>"
                    + "<td>").append(transaccion.getObservacion()).append("</td>"
                    + "<td>").append(transaccion.getImporte()).append("</td>"
                    + "<td>").append(transaccion.getSaldoAcumulado()).append("</td>"
                    + "</tr>");
        }
        sb.append("</tbody></table>");
        return sb.toString();
    }
    
    private String generateHtmlFromObjectsClientes(ArrayList<Cuenta> objects) {
        StringBuilder sb = new StringBuilder();
        sb.append("<table>");
        sb.append("<thead><tr>"
                + "<th>Cliente</th>"
                + "<th>Saldo</th>"
                + "</tr></thead>");
        sb.append("<tbody>");
        for (Cuenta cuenta : objects) {
            sb.append("<tr><td>").append(cuenta.getCliente().getNombre()).append("</td>"
                    + "<td>").append(cuenta.getSaldo()).append("</td>"
                    + "</tr>");
        }
        sb.append("</tbody></table>");
        return sb.toString();
    }
    
    private String generateHtmlFromObjectsChoferes(ArrayList<Cuenta> objects) {
        StringBuilder sb = new StringBuilder();
        sb.append("<table>");
        sb.append("<thead><tr>"
                + "<th>Chofer</th>"
                + "<th>Saldo</th>"
                + "</tr></thead>");
        sb.append("<tbody>");
        for (Cuenta cuenta : objects) {
            sb.append("<tr><td>").append(cuenta.getChofer().getNombre()).append("</td>"
                    + "<td>").append(cuenta.getSaldo()).append("</td>"
                    + "</tr>");
        }
        sb.append("</tbody></table>");
        return sb.toString();
    }
        
    public String obtenerFechaDesde() {
        
    LocalDate now = LocalDate.now();

    LocalDate firstDayTwoMonthsAgo = now.minusMonths(2).withDayOfMonth(1);

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    String formattedDate = firstDayTwoMonthsAgo.format(formatter);

    return formattedDate;
}

    public String obtenerFechaHasta() {

        LocalDate now = LocalDate.now();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        String formattedToday = now.format(formatter);

        return formattedToday;

    }

}
