package abate.abate.controladores;

import abate.abate.entidades.Caja;
import abate.abate.entidades.Transaccion;
import abate.abate.entidades.Usuario;
import abate.abate.servicios.CajaServicio;
import abate.abate.servicios.ChoferServicio;
import abate.abate.servicios.ExcelServicio;
import abate.abate.servicios.GastoServicio;
import abate.abate.servicios.IngresoServicio;
import abate.abate.servicios.TransaccionServicio;
import java.io.IOException;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
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
@RequestMapping("/caja")
@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_CHOFER')")
public class CajaControlador {

    @Autowired
    private CajaServicio cajaServicio;
    @Autowired
    private TransaccionServicio transaccionServicio;
    @Autowired
    private ChoferServicio choferServicio;
    @Autowired
    private ExcelServicio excelServicio;
    @Autowired
    private GastoServicio gastoServicio;
    @Autowired
    private IngresoServicio ingresoServicio;

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @GetMapping("/mostrarAdmin/{id}")
    public String mostrarIdChoferAdmin(@PathVariable Long id, ModelMap modelo) throws ParseException {

        Caja caja = cajaServicio.buscarCajaChofer(id);
        String desde = obtenerFechaDesde();
        String hasta = obtenerFechaHasta();

        ArrayList<Transaccion> lista = transaccionServicio.buscarTransaccionIdCajaFecha(caja.getId(), desde, hasta);
        Boolean flag = true;
        if (lista.isEmpty()) {
            flag = false;
        }

        modelo.put("flag", flag);
        modelo.put("caja", caja);
        modelo.put("desde", desde);
        modelo.put("hasta", hasta);
        modelo.addAttribute("transacciones", lista);

        return "caja_mostrarAdmin.html";

    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @PostMapping("/mostrarFiltroAdmin")
    public String mostrarFiltroAdmin(@RequestParam Long id, @RequestParam String desde, @RequestParam String hasta, ModelMap modelo) throws ParseException {

        Boolean flag = true;

        ArrayList<Transaccion> lista = transaccionServicio.buscarTransaccionIdCajaFecha(id, desde, hasta);

        if (lista.isEmpty()) {
            flag = false;
        }

        modelo.put("flag", flag);
        modelo.put("caja", cajaServicio.buscarCaja(id));
        modelo.put("desde", desde);
        modelo.put("hasta", hasta);
        modelo.addAttribute("transacciones", lista);

        return "caja_mostrarFiltroAdmin.html";

    }
    
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @GetMapping("/mostrarTodas")
    public String mostrarTodas(ModelMap modelo, HttpSession session) throws ParseException {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        Double saldo = 0.0;
        List<Caja> cajas = cajaServicio.buscarCajasHab(logueado.getIdOrg());
        for (Caja c : cajas) {
            saldo = saldo + c.getSaldo();
        }

        modelo.addAttribute("cajas", cajas);
        modelo.put("saldo", saldo);

        return "caja_mostrarTodas.html";

    }
    
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @GetMapping("/listarChoferFiltro")
    public String listarChoferFiltro(@RequestParam(required = false) Long id, @RequestParam(required = false) Boolean inhabilitado,
            ModelMap modelo, HttpSession session) throws ParseException {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");
        boolean filtrarInhabilitados = Boolean.TRUE.equals(inhabilitado);

        if (filtrarInhabilitados) {
            
        Double saldo = 0.0;
        List<Caja> cajas = cajaServicio.buscarCajas(logueado.getIdOrg());
        for (Caja c : cajas) {
            saldo = saldo + c.getSaldo();
        }

        modelo.addAttribute("cajas", cajas);
        modelo.put("saldo", saldo);
        modelo.put("inhabilitado", Boolean.TRUE.equals(inhabilitado));

        return "caja_mostrarTodas.html";

        } else if (id != null) {
            
        Caja caja = cajaServicio.buscarCaja(id);
        String desde = obtenerFechaDesde();
        String hasta = obtenerFechaHasta();

        ArrayList<Transaccion> lista = transaccionServicio.buscarTransaccionIdCajaFecha(caja.getId(), desde, hasta);
        Boolean flag = true;
        if (lista.isEmpty()) {
            flag = false;
        }

        modelo.put("flag", flag);
        modelo.put("caja", caja);
        modelo.put("desde", desde);
        modelo.put("hasta", hasta);
        modelo.addAttribute("transacciones", lista);

        return "caja_mostrarAdminTodas.html";

        } else {
            
        Double saldo = 0.0;
        List<Caja> cajas = cajaServicio.buscarCajasHab(logueado.getIdOrg());
        for (Caja c : cajas) {
            saldo = saldo + c.getSaldo();
        }

        modelo.addAttribute("cajas", cajas);
        modelo.put("saldo", saldo);

        return "caja_mostrarTodas.html";

        }

    }
    
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @GetMapping("/mostrarAdminTodas/{id}")
    public String mostrarAdminTodas(@PathVariable Long id, ModelMap modelo) throws ParseException {

        Caja caja = cajaServicio.buscarCajaChofer(id);
        String desde = obtenerFechaDesde();
        String hasta = obtenerFechaHasta();

        ArrayList<Transaccion> lista = transaccionServicio.buscarTransaccionIdCajaFecha(caja.getId(), desde, hasta);
        Boolean flag = true;
        if (lista.isEmpty()) {
            flag = false;
        }

        modelo.put("flag", flag);
        modelo.put("caja", caja);
        modelo.put("desde", desde);
        modelo.put("hasta", hasta);
        modelo.addAttribute("transacciones", lista);

        return "caja_mostrarAdminTodas.html";

    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @PostMapping("/mostrarFiltroAdminTodas")
    public String mostrarFiltroAdminTodas(@RequestParam Long id, @RequestParam String desde, @RequestParam String hasta, ModelMap modelo) throws ParseException {

        Boolean flag = true;

        ArrayList<Transaccion> lista = transaccionServicio.buscarTransaccionIdCajaFecha(id, desde, hasta);

        if (lista.isEmpty()) {
            flag = false;
        }

        modelo.put("flag", flag);
        modelo.put("caja", cajaServicio.buscarCaja(id));
        modelo.put("desde", desde);
        modelo.put("hasta", hasta);
        modelo.addAttribute("transacciones", lista);

        return "caja_mostrarFiltroAdminTodas.html";

    }
    

    @GetMapping("/mostrarChofer/{id}")
    public String mostrarChofer(@PathVariable Long id, ModelMap modelo) throws ParseException {

        Caja caja = cajaServicio.buscarCajaChofer(id);
        String desde = obtenerFechaDesde();
        String hasta = obtenerFechaHasta();

        ArrayList<Transaccion> lista = transaccionServicio.buscarTransaccionIdCajaFecha(caja.getId(), desde, hasta);
        Boolean flag = true;
        if (lista.isEmpty()) {
            flag = false;
        }

        modelo.put("flag", flag);
        modelo.put("caja", caja);
        modelo.put("desde", desde);
        modelo.put("hasta", hasta);
        modelo.addAttribute("transacciones", lista);

        return "caja_mostrarChofer.html";

    }

    @PostMapping("/mostrarFiltroChofer")
    public String mostrarFiltroChofer(@RequestParam Long id, @RequestParam String desde, @RequestParam String hasta, ModelMap modelo) throws ParseException {

        Boolean flag = true;

        ArrayList<Transaccion> lista = transaccionServicio.buscarTransaccionIdCajaFecha(id, desde, hasta);

        if (lista.isEmpty()) {
            flag = false;
        }

        modelo.put("flag", flag);
        modelo.put("caja", cajaServicio.buscarCaja(id));
        modelo.put("desde", desde);
        modelo.put("hasta", hasta);
        modelo.addAttribute("transacciones", lista);

        return "caja_mostrarFiltroChofer.html";

    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @GetMapping("/habilitar/{id}")
    public String habilitarCaja(@PathVariable Long id, ModelMap modelo) {

        modelo.put("caja", cajaServicio.buscarCajaChofer(id));

        return "caja_habilitar.html";

    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @GetMapping("/habilita/{id}")
    public String habilitaCaja(@PathVariable Long id, ModelMap modelo) {

        Caja caja = cajaServicio.buscarCaja(id);

        choferServicio.habilitarCajaChofer(caja.getChofer().getId());
        
        return "redirect:/caja/habilitado/" +caja.getId();

    }
    
    @GetMapping("/habilitado/{id}")
    public String habilitado(@PathVariable Long id, ModelMap modelo) {

        modelo.put("caja", cajaServicio.buscarCaja(id));
        modelo.put("exito", "Caja HABILITADA con éxito");

        return "caja_habilitada.html";
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @GetMapping("/inhabilitar/{id}")
    public String inhabilitarCaja(@PathVariable Long id, ModelMap modelo) {

        modelo.put("caja", cajaServicio.buscarCajaChofer(id));

        return "caja_inhabilitar.html";

    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @GetMapping("/inhabilita/{id}")
    public String inhabilitaCaja(@PathVariable Long id, ModelMap modelo) {

        Caja caja = cajaServicio.buscarCaja(id);

        choferServicio.inhabilitarCajaChofer(caja.getChofer().getId());

        return "redirect:/caja/inhabilitado/" +caja.getId();

    }
    
    @GetMapping("/inhabilitado/{id}")
    public String inhabilitado(@PathVariable Long id, ModelMap modelo) {

        modelo.put("caja", cajaServicio.buscarCaja(id));
        modelo.put("exito", "Caja INHABILITADA con éxito");

        return "caja_habilitada.html";
    }

    @GetMapping("/mostrarTransaccionChofer/{id}")
    public String mostrarTransaccionChofer(@PathVariable Long id, ModelMap modelo) {

        Transaccion transaccion = transaccionServicio.buscarTransaccion(id);

        if (transaccion.getIngreso() != null) {

            modelo.put("ingreso", ingresoServicio.buscarIngreso(transaccion.getIngreso().getId()));

            return "transaccion_cajaIngreso.html";

        }
        
        else {

            modelo.put("gasto", gastoServicio.buscarGasto(transaccion.getGasto().getId()));

            return "transaccion_gastoChofer.html";

        }
    }

    @GetMapping("/mostrarTransaccionAdmin/{id}")
    public String mostrarTransaccionAdmin(@PathVariable Long id, ModelMap modelo) {

        Transaccion transaccion = transaccionServicio.buscarTransaccion(id);

        if (transaccion.getIngreso() != null) {

            modelo.put("ingreso", ingresoServicio.buscarIngreso(transaccion.getIngreso().getId()));

            return "transaccion_cajaIngresoAdmin.html";

        } else {

            modelo.put("gasto", gastoServicio.buscarGasto(transaccion.getGasto().getId()));

            return "transaccion_gastoAdmin.html";

        }

    }

    @PostMapping("/exportarAdmin")
    public String exportarAdmin(@RequestParam Long id, ModelMap modelo) throws ParseException {

        Caja caja = cajaServicio.buscarCaja(id);
        String desde = obtenerFechaDesde();
        String hasta = obtenerFechaHasta();

        modelo.put("caja", caja);
        modelo.addAttribute("transacciones", transaccionServicio.buscarTransaccionIdCajaFecha(id, desde, hasta));

        return "caja_exportarAdmin.html";

    }

    @PostMapping("/exportaAdmin")
    public void exportaAdmin(@RequestParam Long id, HttpServletResponse response) throws IOException, ParseException {

        Caja caja = cajaServicio.buscarCaja(id);
        String desde = obtenerFechaDesde();
        String hasta = obtenerFechaHasta();

        ArrayList<Transaccion> myObjects = transaccionServicio.buscarTransaccionIdCajaFecha(id, desde, hasta);
        String htmlContent = generateHtmlFromObjects(myObjects);
        excelServicio.exportHtmlToExcelCaja(htmlContent, response, caja.getChofer().getNombre(), caja.getSaldo());

    }
    
    @GetMapping("/imprimirAdmin")
    public String imprimirAdmin(@RequestParam Long idCaja, ModelMap modelo) throws ParseException {
        
        Caja caja = cajaServicio.buscarCaja(idCaja);
        String desde = obtenerFechaDesde();
        String hasta = obtenerFechaHasta();

        modelo.put("caja", caja);
        modelo.addAttribute("transacciones", transaccionServicio.buscarTransaccionIdCajaFecha(idCaja, desde, hasta));

        return "caja_imprimirAdmin.html";  
        
    }

    @PostMapping("/exportarFiltroChofer")
    public String exportarFiltroChofer(@RequestParam Long id, @RequestParam String desde, @RequestParam String hasta, ModelMap modelo) throws ParseException {

        ArrayList<Transaccion> lista = transaccionServicio.buscarTransaccionIdCajaFecha(id, desde, hasta);

        modelo.put("caja", cajaServicio.buscarCaja(id));
        modelo.put("desde", desde);
        modelo.put("hasta", hasta);
        modelo.addAttribute("transacciones", lista);

        return "caja_exportarFiltroChofer.html";

    }

    @PostMapping("/exportarFiltroAdmin")
    public String exportarFiltroAdmin(@RequestParam Long id, @RequestParam String desde, @RequestParam String hasta, ModelMap modelo) throws ParseException {

        ArrayList<Transaccion> lista = transaccionServicio.buscarTransaccionIdCajaFecha(id, desde, hasta);

        modelo.put("caja", cajaServicio.buscarCaja(id));
        modelo.put("desde", desde);
        modelo.put("hasta", hasta);
        modelo.addAttribute("transacciones", lista);

        return "caja_exportarFiltroAdmin.html";

    }

    @PostMapping("/exportaFiltroAdmin")
    public void exportaFiltroAdmin(@RequestParam Long id, @RequestParam String desde, @RequestParam String hasta, HttpServletResponse response) throws IOException, ParseException {

        Caja caja = cajaServicio.buscarCaja(id);

        ArrayList<Transaccion> lista = transaccionServicio.buscarTransaccionIdCajaFecha(id, desde, hasta);

        ArrayList<Transaccion> myObjects = lista;
        String htmlContent = generateHtmlFromObjects(myObjects);
        excelServicio.exportHtmlToExcelCajaMovimiento(htmlContent, response, caja.getChofer().getNombre(), desde, hasta);

    }
    
    @GetMapping("/imprimirFiltroAdmin")
    public String imprimirFiltroAdmin(@RequestParam Long idCaja, @RequestParam String desde, @RequestParam String hasta, ModelMap modelo) throws ParseException {
        
        ArrayList<Transaccion> lista = transaccionServicio.buscarTransaccionIdCajaFecha(idCaja, desde, hasta);

        modelo.put("caja", cajaServicio.buscarCaja(idCaja));
        modelo.put("desde", desde);
        modelo.put("hasta", hasta);
        modelo.addAttribute("transacciones", lista);

        return "caja_imprimirFiltroAdmin.html";  
        
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
