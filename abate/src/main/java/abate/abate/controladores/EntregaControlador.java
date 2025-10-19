package abate.abate.controladores;

import abate.abate.entidades.Cuenta;
import abate.abate.entidades.Entrega;
import abate.abate.entidades.Imagen;
import abate.abate.entidades.Usuario;
import abate.abate.servicios.ChoferServicio;
import abate.abate.servicios.CuentaServicio;
import abate.abate.servicios.EntregaServicio;
import abate.abate.servicios.ImagenServicio;
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
@RequestMapping("/entrega")
@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_CHOFER')")
public class EntregaControlador {

    @Autowired
    private EntregaServicio entregaServicio;
    @Autowired
    private ChoferServicio choferServicio;
    @Autowired
    private CuentaServicio cuentaServicio;
    @Autowired
    private ImagenServicio imagenServicio;

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @GetMapping("/registrar")
    public String registrarEntrega(ModelMap modelo, HttpSession session) {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        modelo.addAttribute("cuentas", cuentaServicio.buscarCuentasChofer(logueado.getIdOrg()));

        return "entrega_registrar.html";
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @GetMapping("/registrarId/{id}")
    public String registrarEntrega(@PathVariable Long id, ModelMap modelo) {

        Cuenta cuenta = cuentaServicio.buscarCuentaChofer(id);

        modelo.put("cuenta", cuenta);

        return "entrega_registrarId.html";
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @PostMapping("/registro")
    public String registroEntrega(@RequestParam Long idChofer, @RequestParam String fecha,
            @RequestParam Double importe, @RequestParam(required = false) String observacion,
            ModelMap modelo, HttpSession session) throws ParseException {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        entregaServicio.crearEntrega(logueado.getIdOrg(), idChofer, fecha, importe, observacion, logueado.getId());

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
    public String listar(ModelMap modelo, HttpSession session) throws ParseException {

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
        modelo.put("desde", desde);
        modelo.put("hasta", hasta);
        modelo.put("id", logueado.getIdOrg());
        modelo.put("total", total);
        modelo.put("chofer", null);

        return "entrega_listar.html";
    }

    @PostMapping("/listarFiltro")
    public String listarFiltro(Long id, String desde, String hasta, @RequestParam(required = false) Long idChofer,
            ModelMap modelo, HttpSession session) throws ParseException {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");
        Double total = 0.0;
        ArrayList<Entrega> lista = new ArrayList();
        
        if(idChofer == null) {
        
        lista = entregaServicio.buscarEntregas(id, desde, hasta);
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
        modelo.put("desde", desde);
        modelo.put("hasta", hasta);
        modelo.put("id", id);
        modelo.put("total", total);

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
    @GetMapping("/modificar/{id}")
    public String modificar(@PathVariable Long id, ModelMap modelo, HttpSession session) {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        modelo.put("entrega", entregaServicio.buscarEntrega(id));
        modelo.addAttribute("choferes", choferServicio.bucarChoferesHabNombreAsc(logueado.getIdOrg()));

        return "entrega_modificar.html";

    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @PostMapping("/modifica")
    public String modifica(@RequestParam Long id, @RequestParam Long idChofer, @RequestParam String fecha,
            @RequestParam Double importe, @RequestParam String observacion,
            ModelMap modelo, HttpSession session) throws ParseException {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        entregaServicio.modificarEntrega(id, idChofer, fecha, importe, observacion, logueado.getId());

        return "redirect:/entrega/modificado/" + id;

    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @GetMapping("/modificado/{id}")
    public String entregaModificado(@PathVariable Long id, ModelMap modelo) {

        Entrega entrega = entregaServicio.buscarEntrega(id);

        modelo.put("entrega", entrega);
        modelo.put("exito", "Entrega MODIFICADA con éxito");

        return "entrega_registrado.html";
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @GetMapping("/modificarDesdeChofer/{id}")
    public String modificarDesdeChofer(@PathVariable Long id, ModelMap modelo, HttpSession session) {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        modelo.put("entrega", entregaServicio.buscarEntrega(id));
        modelo.addAttribute("choferes", choferServicio.bucarChoferesHabNombreAsc(logueado.getIdOrg()));

        return "entrega_modificarDesdeChofer.html";

    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id, ModelMap modelo) {

        modelo.put("entrega", entregaServicio.buscarEntrega(id));

        return "entrega_eliminar.html";
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @GetMapping("/elimina/{id}")
    public String elimina(@PathVariable Long id, HttpSession session, ModelMap modelo) {

        entregaServicio.eliminarEntrega(id);

        return "redirect:/entrega/eliminado";
        
    }
    
    @GetMapping("/eliminado")
    public String eliminado(ModelMap modelo, HttpSession session) {
        
        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        modelo.put("id", logueado.getId());
        modelo.put("exito", "Entrega ELIMINADA con éxito");

        return "index_admin.html";    

    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @GetMapping("/imprimir/{id}")
    public String imprimir(@PathVariable Long id, ModelMap modelo, HttpSession session) {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");
        Entrega entrega = entregaServicio.buscarEntrega(id);
        
        modelo.addAttribute("flag", false);
        
        if (logueado.getLogo() != null) {

            Long idLogo = logueado.getLogo().getId();
            Imagen logo = imagenServicio.obtenerImagenPorId(idLogo);
                
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
