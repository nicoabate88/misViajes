package abate.abate.controladores;

import abate.abate.entidades.Cuenta;
import abate.abate.entidades.Entrega;
import abate.abate.entidades.Usuario;
import abate.abate.entidades.ValorE;
import abate.abate.entidades.ValorE.TipoValorE;
import abate.abate.servicios.ChoferServicio;
import abate.abate.servicios.CuentaServicio;
import abate.abate.servicios.EntregaServicio;
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
@RequestMapping("/entrega")
@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_CHOFER')")
public class EntregaControlador {

    @Autowired
    private EntregaServicio entregaServicio;
    @Autowired
    private ChoferServicio choferServicio;
    @Autowired
    private CuentaServicio cuentaServicio;

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @GetMapping("/registrar")
    public String registrarEntrega(ModelMap modelo, HttpSession session) {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        modelo.addAttribute("cuentas", cuentaServicio.buscarCuentasChofer(logueado.getIdOrg()));
        modelo.addAttribute("tiposValor", TipoValorE.values());

        return "entrega_registrar.html";
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @GetMapping("/registrarId/{id}")
    public String registrarEntrega(@PathVariable Long id, ModelMap modelo) {

        Cuenta cuenta = cuentaServicio.buscarCuentaChofer(id);

        modelo.put("cuenta", cuenta);
        modelo.addAttribute("tiposValor", TipoValorE.values());

        return "entrega_registrarId.html";
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @PostMapping("/registro")
    public String registroEntrega(@RequestParam Long idChofer, @RequestParam String fecha, @RequestParam(required = false) List<Double> importes,
            @RequestParam(required = false) List<String> observacionesValores, @RequestParam(required = false) List<TipoValorE> tipos,
            @RequestParam(required = false) String observacion, ModelMap modelo, HttpSession session) throws ParseException {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        List<ValorE> valores = new ArrayList<>();

        if (importes != null) {
            for (int i = 0; i < importes.size(); i++) {

                if (importes.get(i) != null && importes.get(i) > 0) {

                    ValorE v = new ValorE();
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

        entregaServicio.crearEntrega(logueado.getIdOrg(), idChofer, fecha, valores, observacion, logueado);

        return "redirect:/entrega/registrado";

    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @GetMapping("/registrado")
    public String entregaRegistrado(HttpSession session, ModelMap modelo) {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        Long id = entregaServicio.buscarUltimo(logueado.getIdOrg());
        Entrega entrega = entregaServicio.buscarEntrega(id);

        modelo.put("entrega", entrega);
        modelo.put("exito", "Entrega REGISTRADA con éxito");

        return "entrega_registrado.html";
    }

    @GetMapping("/listar")
    public String listar(@RequestParam(required = false) String elimina, ModelMap modelo, HttpSession session) throws ParseException {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");
        String desde = obtenerFechaDesde();
        String hasta = obtenerFechaHasta();
        ArrayList<Entrega> lista = entregaServicio.buscarEntregas(logueado.getIdOrg(), desde, hasta);
        Double total = 0.0;
        for (Entrega e : lista) {
            total = e.getImporte() + total;
        }

        modelo.addAttribute("choferes", choferServicio.bucarChoferesHabNombreAsc(logueado.getIdOrg()));
        modelo.addAttribute("entregas", lista);
        modelo.put("idChofer", null);
        modelo.put("desde", desde);
        modelo.put("hasta", hasta);
        modelo.put("total", total);
        modelo.put("chofer", null);
        if (elimina != null) {
            modelo.put("exito", "Entrega ELIMINADA con éxito");
        }

        return "entrega_listar.html";
    }

    @GetMapping("/listarFiltro")
    public String listarFiltro(String desde, String hasta, @RequestParam(required = false) Long idChofer,
            @RequestParam(required = false) String elimina, ModelMap modelo, HttpSession session) throws ParseException {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");
        Double total = 0.0;
        ArrayList<Entrega> lista = new ArrayList();

        if (idChofer == null || idChofer == 0) {

            lista = entregaServicio.buscarEntregas(logueado.getIdOrg(), desde, hasta);
            modelo.put("chofer", null);

        } else {

            lista = entregaServicio.buscarEntregasIdChofer(idChofer, desde, hasta);
            modelo.put("chofer", choferServicio.buscarChofer(idChofer));

        }

        for (Entrega e : lista) {
            total = e.getImporte() + total;
        }

        modelo.addAttribute("choferes", choferServicio.bucarChoferesHabNombreAsc(logueado.getIdOrg()));
        modelo.addAttribute("entregas", lista);
        modelo.put("idChofer", idChofer);
        modelo.put("desde", desde);
        modelo.put("hasta", hasta);
        modelo.put("total", total);
        if (elimina != null) {
            modelo.put("exito", "Entrega ELIMINADA con éxito");
        }

        return "entrega_listar.html";

    }

    @GetMapping("/listarIdChofer/{id}")
    public String listarIdChofer(@PathVariable Long id, HttpSession session, ModelMap modelo) throws ParseException {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");
        String desde = obtenerFechaDesde();
        String hasta = obtenerFechaHasta();

        ArrayList<Entrega> lista = entregaServicio.buscarEntregasIdChofer(id, desde, hasta);
        Double total = 0.0;
        for (Entrega e : lista) {
            total = e.getImporte() + total;
        }

        if (logueado.getRol().equalsIgnoreCase("CHOFER")) {

            modelo.addAttribute("entregas", lista);
            modelo.put("id", id);
            modelo.put("desde", desde);
            modelo.put("hasta", hasta);
            modelo.put("total", total);

            return "entrega_listarIdChofer.html";

        } else {

            modelo.addAttribute("entregas", lista);
            modelo.put("chofer", choferServicio.buscarChofer(id));
            modelo.put("id", id);
            modelo.put("desde", desde);
            modelo.put("hasta", hasta);
            modelo.put("total", total);

            return "entrega_listarIdChoferAdmin.html";

        }
    }

    @PostMapping("/listarIdChoferFiltro")
    public String listarIdChoferFiltro(@RequestParam Long id, @RequestParam String desde, @RequestParam String hasta, HttpSession session, ModelMap modelo) throws ParseException {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        ArrayList<Entrega> lista = entregaServicio.buscarEntregasIdChofer(id, desde, hasta);
        Double total = 0.0;
        for (Entrega e : lista) {
            total = e.getImporte() + total;
        }

        if (logueado.getRol().equalsIgnoreCase("CHOFER")) {

            modelo.addAttribute("entregas", lista);
            modelo.put("id", id);
            modelo.put("desde", desde);
            modelo.put("hasta", hasta);
            modelo.put("total", total);

            return "entrega_listarIdChofer.html";

        } else {

            modelo.addAttribute("entregas", lista);
            modelo.put("chofer", choferServicio.buscarChofer(id));
            modelo.put("id", id);
            modelo.put("desde", desde);
            modelo.put("hasta", hasta);
            modelo.put("total", total);

            return "entrega_listarIdChoferAdmin.html";

        }
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @GetMapping("/modificar")
    public String modificar(@RequestParam Long id, @RequestParam String desde, @RequestParam String hasta, 
            @RequestParam(required = false) Long idChofer, ModelMap modelo, HttpSession session) {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        modelo.put("entrega", entregaServicio.buscarEntrega(id));
        modelo.addAttribute("choferes", choferServicio.bucarChoferesHabNombreAsc(logueado.getIdOrg()));
        modelo.addAttribute("tiposValor", TipoValorE.values());
        modelo.put("idChofer", idChofer);
        modelo.put("desde", desde);
        modelo.put("hasta", hasta);

        return "entrega_modificar.html";

    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @PostMapping("/modifica")
    public String modifica(@RequestParam Long id, @RequestParam String desde, @RequestParam String hasta, @RequestParam(required = false) Long idChof,
            @RequestParam Long idChofer, @RequestParam String fecha,
            @RequestParam(required = false) List<Double> importes, @RequestParam(required = false) List<String> observacionesValores,
            @RequestParam(required = false) List<TipoValorE> tipos, @RequestParam String observacion,
            ModelMap modelo, HttpSession session) throws ParseException {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        List<ValorE> valores = new ArrayList<>();

        if (importes != null) {
            for (int i = 0; i < importes.size(); i++) {

                if (importes.get(i) != null && importes.get(i) > 0) {

                    ValorE v = new ValorE();
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

        entregaServicio.modificarEntrega(id, idChofer, fecha, valores, observacion, logueado);
        
        if(idChof == null){
            idChof = 0L;
        }

        return "redirect:/entrega/modificado?id=" + id + "&desde=" + desde + "&hasta=" + hasta + "&idChofer=" + idChof;

    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @GetMapping("/modificado")
    public String entregaModificado(@RequestParam Long id, @RequestParam String desde, @RequestParam String hasta, @RequestParam(required = false) Long idChofer, ModelMap modelo) {

        Entrega entrega = entregaServicio.buscarEntrega(id);
        
        if(idChofer == 0){
            idChofer = null;
        }

        modelo.put("entrega", entrega);
        modelo.put("exito", "Entrega MODIFICADA con éxito");
        modelo.put("idChofer", idChofer);
        modelo.put("desde", desde);
        modelo.put("hasta", hasta);

        return "entrega_mostrar.html";
        
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @GetMapping("/modificarCuenta")
    public String modificarCuenta(@RequestParam Long id, @RequestParam Long idCuenta, @RequestParam(required = false) String desde,
            @RequestParam(required = false) String hasta, ModelMap modelo, HttpSession session) {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        modelo.put("entrega", entregaServicio.buscarEntrega(id));
        modelo.addAttribute("choferes", choferServicio.bucarChoferesHabNombreAsc(logueado.getIdOrg()));
        modelo.addAttribute("tiposValor", TipoValorE.values());
        modelo.put("id", idCuenta);
        modelo.put("desde", desde);
        modelo.put("hasta", hasta);

        return "entrega_modificarDesdeCuenta.html";

    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @PostMapping("/modificaCuenta")
    public String modificaCuenta(@RequestParam Long id, @RequestParam Long idCuenta, @RequestParam(required = false) String desde,
            @RequestParam(required = false) String hasta, @RequestParam Long idChofer, @RequestParam String fecha,
            @RequestParam(required = false) List<Double> importes, @RequestParam(required = false) List<String> observacionesValores,
            @RequestParam(required = false) List<TipoValorE> tipos, @RequestParam String observacion,
            ModelMap modelo, HttpSession session) throws ParseException {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        List<ValorE> valores = new ArrayList<>();

        if (importes != null) {
            for (int i = 0; i < importes.size(); i++) {

                if (importes.get(i) != null && importes.get(i) > 0) {

                    ValorE v = new ValorE();
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

        entregaServicio.modificarEntrega(id, idChofer, fecha, valores, observacion, logueado);

        return "redirect:/entrega/modificadoCuenta?id=" + id + "&idCuenta=" + idCuenta + "&desde=" + desde + "&hasta=" + hasta;

    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @GetMapping("/modificadoCuenta")
    public String entregaModificadoCuenta(@RequestParam Long id, @RequestParam(required = false) Long idCuenta,
            @RequestParam(required = false) String desde, @RequestParam(required = false) String hasta, ModelMap modelo) {

        Entrega entrega = entregaServicio.buscarEntrega(id);

        modelo.put("entrega", entrega);
        modelo.put("exito", "Entrega MODIFICADA con éxito");
        modelo.put("id", entrega.getChofer());
        modelo.put("idCuenta", idCuenta);
        modelo.put("desde", desde);
        modelo.put("hasta", hasta);

        return "entrega_modificado.html";

    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @GetMapping("/modificarDesdeChofer/{id}")
    public String modificarDesdeChofer(@PathVariable Long id, ModelMap modelo, HttpSession session) {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        modelo.put("entrega", entregaServicio.buscarEntrega(id));
        modelo.addAttribute("choferes", choferServicio.bucarChoferesHabNombreAsc(logueado.getIdOrg()));
        modelo.addAttribute("tiposValor", TipoValorE.values());

        return "entrega_modificarDesdeChofer.html";

    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @GetMapping("/eliminar")
    public String eliminar(@RequestParam Long id, String desde, String hasta, @RequestParam(required = false) Long idChofer, ModelMap modelo) {

        modelo.put("entrega", entregaServicio.buscarEntrega(id));
        modelo.put("idChofer", idChofer);
        modelo.put("desde", desde);
        modelo.put("hasta", hasta);

        return "entrega_eliminar.html";
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @GetMapping("/elimina")
    public String elimina(@RequestParam Long id,String desde, String hasta, @RequestParam(required = false) Long idChofer, HttpSession session, ModelMap modelo) {

        entregaServicio.eliminarEntrega(id);
        
        if(idChofer == null){
            idChofer = 0L;
        }

        return "redirect:/entrega/listarFiltro?&desde=" + desde + "&hasta=" + hasta + "&idChofer=" + idChofer + "&elimina=" + "si";

    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @GetMapping("/eliminarCuenta")
    public String eliminarCuenta(@RequestParam Long id, @RequestParam Long idCuenta, @RequestParam(required = false) String desde,
            @RequestParam(required = false) String hasta, ModelMap modelo) {

        modelo.put("entrega", entregaServicio.buscarEntrega(id));
        modelo.put("id", idCuenta);
        modelo.put("desde", desde);
        modelo.put("hasta", hasta);

        return "entrega_eliminarCuenta.html";
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @GetMapping("/eliminaCuenta")
    public String eliminaCuenta(@RequestParam Long id, @RequestParam Long idCuenta, @RequestParam(required = false) String desde,
            @RequestParam(required = false) String hasta, HttpSession session, ModelMap modelo) {

        entregaServicio.eliminarEntrega(id);

        return "redirect:/entrega/eliminadoCuenta?id=" + idCuenta + "&idCuenta=" + idCuenta + "&desde=" + desde + "&hasta=" + hasta;

    }

    @GetMapping("/eliminadoCuenta")
    public String eliminadoCuenta(@RequestParam Long id, @RequestParam(required = false) Long idCuenta,
            @RequestParam(required = false) String desde, @RequestParam(required = false) String hasta) {

        return "redirect:/cuenta/mostrarChoferFiltroAdmin?&id=" + id + "&desde=" + desde + "&hasta=" + hasta + "&elimina=" + "si";

    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @GetMapping("/imprimir/{id}")
    public String imprimir(@PathVariable Long id, ModelMap modelo, HttpSession session) {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");
        Entrega entrega = entregaServicio.buscarEntrega(id);

        modelo.addAttribute("flag", false);

        if (logueado.getLogo() != null) {

            Long idLogo = logueado.getLogo().getId();
            // Imagen logo = imagenServicio.obtenerImagenPorId(idLogo);

            modelo.addAttribute("imagenUrl", "/imagen/img/bytes/" + idLogo);
            modelo.addAttribute("flag", true);

        }

        modelo.put("entrega", entrega);

        return "entrega_imprimir.html";
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
