package abate.abate.controladores;

import abate.abate.entidades.Flete;
import abate.abate.entidades.Usuario;
import abate.abate.servicios.CamionServicio;
import abate.abate.servicios.ChoferServicio;
import abate.abate.servicios.ClienteServicio;
import abate.abate.servicios.ExcelServicio;
import abate.abate.servicios.FleteServicio;
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
@RequestMapping("/flete")
@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_CHOFER')")
public class FleteControlador {

    @Autowired
    private FleteServicio fleteServicio;
    @Autowired
    private ClienteServicio clienteServicio;
    @Autowired
    private ChoferServicio choferServicio;
    @Autowired
    private ExcelServicio excelServicio;
    @Autowired
    private CamionServicio camionServicio;

    @GetMapping("/registrar")
    public String registrarFlete(ModelMap modelo, HttpSession session) {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");
        if (logueado.getRol().equalsIgnoreCase("CHOFER")) {

            modelo.put("chofer", logueado);
            modelo.addAttribute("clientes", clienteServicio.buscarClientesNombreAsc(logueado.getIdOrg()));
            modelo.addAttribute("camiones", camionServicio.buscarCamionesAsc(logueado.getIdOrg()));

            return "flete_registrarChofer.html";

        } else {

            modelo.addAttribute("clientes", clienteServicio.buscarClientesNombreAsc(logueado.getIdOrg()));
            modelo.addAttribute("choferes", choferServicio.bucarChoferesNombreAsc(logueado.getIdOrg()));
            modelo.addAttribute("camiones", camionServicio.buscarCamionesAsc(logueado.getIdOrg()));

            return "flete_registrarAdmin.html";
        }
    }

    @PostMapping("/registroChofer")
    public String registroFlete(@RequestParam Long idCliente, @RequestParam Long idCamion, @RequestParam String fechaCarga, @RequestParam String fechaFlete,
            @RequestParam String origen, @RequestParam String destino, @RequestParam Double km, @RequestParam String tipoCereal,
            @RequestParam String cPorte, @RequestParam String ctg, @RequestParam Double tarifa, @RequestParam Double kg,
            ModelMap modelo, HttpSession session) throws ParseException {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        fleteServicio.crearFleteChofer(logueado.getIdOrg(), fechaCarga, idCliente, idCamion, origen, fechaFlete, destino, km, tipoCereal, tarifa, cPorte, ctg, kg, logueado.getId());

        return "redirect:/flete/registradoChofer";

    }

    @GetMapping("/registradoChofer")
    public String registradoChofer(HttpSession session, ModelMap modelo) {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        Long id = fleteServicio.buscarUltimo(logueado.getIdOrg());

        modelo.put("flete", fleteServicio.buscarFlete(id));
        modelo.put("exito", "Viaje REGISTRADO con éxito");

        return "flete_registradoChofer.html";

    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @PostMapping("/registroAdmin")
    public String registroFleteAdmin(@RequestParam Long idChofer, @RequestParam Long idCamion, @RequestParam Long idCliente, @RequestParam String fechaCarga, @RequestParam String fechaFlete,
            @RequestParam String origen, @RequestParam String destino, @RequestParam Double km, @RequestParam String tipoCereal, @RequestParam String cPorte,
            @RequestParam String ctg, @RequestParam Double tarifa, @RequestParam Double kg, @RequestParam Double comisionTpte, @RequestParam String comisionTpteChofer,
            @RequestParam String iva, ModelMap modelo, HttpSession session) throws ParseException {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        fleteServicio.crearFleteAdmin(logueado.getIdOrg(), idChofer, idCamion, fechaCarga, idCliente, origen, fechaFlete, destino, km, tipoCereal, tarifa, cPorte, ctg, kg, comisionTpte, comisionTpteChofer, iva, logueado.getId());

        return "redirect:/flete/registradoAdmin";

    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @GetMapping("/registradoAdmin")
    public String registradoAdmin(HttpSession session, ModelMap modelo) {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        Long id = fleteServicio.buscarUltimo(logueado.getIdOrg());

        modelo.put("flete", fleteServicio.buscarFlete(id));
        modelo.put("exito", "Viaje REGISTRADO con éxito");

        return "flete_registradoAdmin.html";

    }

    @GetMapping("/listar")
    public String listar(ModelMap modelo, HttpSession session) throws ParseException {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        if (logueado.getRol().equalsIgnoreCase("CHOFER")) {

            String desde = obtenerFechaDesde();
            String hasta = obtenerFechaHasta();
            ArrayList<Flete> fletes = fleteServicio.buscarFletesIdChoferFecha(logueado.getId(), desde, hasta);
            Boolean flag = true;
            if (fletes.isEmpty()) {
                flag = false;
            }

            modelo.addAttribute("fletes", fletes);
            modelo.put("flag", flag);
            modelo.put("chofer", choferServicio.buscarChofer(logueado.getId()));
            modelo.put("desde", desde);
            modelo.put("hasta", hasta);

            return "flete_listarChofer.html";

        } else {

            modelo.addAttribute("fletes", fleteServicio.buscarFletesPendiente(logueado.getIdOrg()));

            return "flete_listarPendiente.html";
        }
    }

    @PostMapping("/listarChoferFiltro")
    public String listarChoferFiltro(@RequestParam Long id, @RequestParam String desde, @RequestParam String hasta,
            ModelMap modelo) throws ParseException {

        ArrayList<Flete> fletes = fleteServicio.buscarFletesIdChoferFecha(id, desde, hasta);
        Boolean flag = true;
        if (fletes.isEmpty()) {
            flag = false;
        }

        modelo.addAttribute("fletes", fletes);
        modelo.put("flag", flag);
        modelo.put("chofer", choferServicio.buscarChofer(id));
        modelo.put("desde", desde);
        modelo.put("hasta", hasta);

        return "flete_listarChofer.html";
    }

    @GetMapping("/listarIdCliente/{id}")
    public String listarIdCliente(@PathVariable Long id, ModelMap modelo) throws ParseException {

        String desde = obtenerFechaDesde();
        String hasta = obtenerFechaHasta();
        ArrayList<Flete> fletes = fleteServicio.buscarFletesIdClienteFecha(id, desde, hasta);
        Boolean flag = true;
        if (fletes.isEmpty()) {
            flag = false;
        }
        
        Double neto = 0.0;
        
        for(Flete f : fletes){
            neto = neto + f.getNeto();
        }

        modelo.addAttribute("fletes", fletes);
        modelo.put("flag", flag);
        modelo.put("cliente", clienteServicio.buscarCliente(id));
        modelo.put("desde", desde);
        modelo.put("hasta", hasta);
        modelo.put("cantidad", fletes.size());
        modelo.put("neto", neto);

        return "flete_listarIdCliente";
    }

    @PostMapping("/listarIdClienteFiltro")
    public String listarIdClienteFiltro(@RequestParam Long id, @RequestParam String desde, @RequestParam String hasta, ModelMap modelo) throws ParseException {

        ArrayList<Flete> fletes = fleteServicio.buscarFletesIdClienteFecha(id, desde, hasta);
        Boolean flag = true;
        if (fletes.isEmpty()) {
            flag = false;
        }
        
        Double neto = 0.0;
        
        for(Flete f : fletes){
            neto = neto + f.getNeto();
        }

        modelo.addAttribute("fletes", fletes);
        modelo.put("flag", flag);
        modelo.put("cliente", clienteServicio.buscarCliente(id));
        modelo.put("desde", desde);
        modelo.put("hasta", hasta);
        modelo.put("cantidad", fletes.size());
        modelo.put("neto", neto);

        return "flete_listarIdCliente";
    }

    @GetMapping("/listarIdChofer/{id}")
    public String listarIdChofer(@PathVariable Long id, ModelMap modelo) throws ParseException {

        String desde = obtenerFechaDesde();
        String hasta = obtenerFechaHasta();
        ArrayList<Flete> fletes = fleteServicio.buscarFletesIdChoferFecha(id, desde, hasta);
        Boolean flag = true;
        if (fletes.isEmpty()) {
            flag = false;
        }
        
        Double neto = 0.0;
        Double ganancia = 0.0;
        
        for(Flete f : fletes){
            neto = neto + f.getNeto();
            ganancia = ganancia + f.getPorcentajeChofer();
        }
        
        modelo.addAttribute("fletes", fletes);
        modelo.put("flag", flag);
        modelo.put("chofer", choferServicio.buscarChofer(id));
        modelo.put("desde", desde);
        modelo.put("hasta", hasta);
        modelo.put("cantidad", fletes.size());
        modelo.put("neto", neto);
        modelo.put("ganancia", ganancia);

        return "flete_listarIdChofer";
    }

    @PostMapping("/listarIdChoferFiltro")
    public String listarIdChoferFiltro(@RequestParam Long id, @RequestParam String desde, @RequestParam String hasta,
            ModelMap modelo) throws ParseException {

        ArrayList<Flete> fletes = fleteServicio.buscarFletesIdChoferFecha(id, desde, hasta);
        Boolean flag = true;
        if (fletes.isEmpty()) {
            flag = false;
        }
        
        Double neto = 0.0;
        Double ganancia = 0.0;
        
        
        for(Flete f : fletes){
            neto = neto + f.getNeto();
            ganancia = ganancia + f.getPorcentajeChofer();
        }

        modelo.addAttribute("fletes", fletes);
        modelo.put("flag", flag);
        modelo.put("chofer", choferServicio.buscarChofer(id));
        modelo.put("desde", desde);
        modelo.put("hasta", hasta);
        modelo.put("cantidad", fletes.size());
        modelo.put("neto", neto);
        modelo.put("ganancia", ganancia);

        return "flete_listarIdChofer";
    }

    @GetMapping("/listarTodo")
    public String listarMes(ModelMap modelo, HttpSession session) throws ParseException {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");
        String desde = obtenerFechaDesde();
        String hasta = obtenerFechaHasta();
        ArrayList<Flete> fletes = fleteServicio.buscarFletesRangoFecha(logueado.getIdOrg(), desde, hasta);
        Boolean flag = true;
        if (fletes.isEmpty()) {
            flag = false;
        }
        
        Double neto = 0.0;
        Double ganancia = 0.0;
        
        for(Flete f : fletes){
            neto = neto + f.getNeto();
            ganancia = ganancia + f.getPorcentajeChofer();
        }

        modelo.addAttribute("fletes", fletes);
        modelo.put("flag", flag);
        modelo.addAttribute("choferes", choferServicio.bucarChoferesNombreAsc(logueado.getIdOrg()));
        modelo.addAttribute("clientes", clienteServicio.buscarClientesNombreAsc(logueado.getIdOrg()));
        modelo.put("desde", desde);
        modelo.put("hasta", hasta);
        modelo.put("cantidad", fletes.size());
        modelo.put("neto", neto);
        modelo.put("ganancia", ganancia);

        return "flete_listarTodo";
    }

    @PostMapping("/listarXfechaAdmin")
    public String listarXfechaAdmin(@RequestParam String desde, @RequestParam String hasta, @RequestParam(required = false) Long idChofer,
            @RequestParam(required = false) Long idCliente, ModelMap modelo, HttpSession session) throws ParseException {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        if (idChofer == null && idCliente == null) {

            ArrayList<Flete> fletes = fleteServicio.buscarFletesRangoFecha(logueado.getIdOrg(), desde, hasta);
            Boolean flag = true;
            if (fletes.isEmpty()) {
                flag = false;
            }
            
            Double neto = 0.0;
            Double ganancia = 0.0;
        
            for(Flete f : fletes){
            neto = neto + f.getNeto();
            ganancia = ganancia + f.getPorcentajeChofer();
            }

            modelo.addAttribute("fletes", fletes);
            modelo.put("flag", flag);
            modelo.addAttribute("choferes", choferServicio.bucarChoferesNombreAsc(logueado.getIdOrg()));
            modelo.addAttribute("clientes", clienteServicio.buscarClientesNombreAsc(logueado.getIdOrg()));
            modelo.put("desde", desde);
            modelo.put("hasta", hasta);
            modelo.put("cantidad", fletes.size());
            modelo.put("neto", neto);
            modelo.put("ganancia", ganancia);

            return "flete_listarTodoFiltrado";

        } else if (idChofer != null && idCliente == null) {

            ArrayList<Flete> fletes = fleteServicio.buscarFletesIdChoferFecha(idChofer, desde, hasta);
            Boolean flag = true;
            if (fletes.isEmpty()) {
                flag = false;
            }
            
            Double neto = 0.0;
            Double ganancia = 0.0;
        
            for(Flete f : fletes){
            neto = neto + f.getNeto();
            ganancia = ganancia + f.getPorcentajeChofer();
            }

            modelo.addAttribute("fletes", fletes);
            modelo.put("flag", flag);
            modelo.put("chofer", choferServicio.buscarChofer(idChofer));
            modelo.put("desde", desde);
            modelo.put("hasta", hasta);
            modelo.put("cantidad", fletes.size());
            modelo.put("neto", neto);
            modelo.put("ganancia", ganancia);

            return "flete_listarTodoIdChofer.html";

        } else if (idChofer == null && idCliente != null) {
            
            ArrayList<Flete> fletes = fleteServicio.buscarFletesIdClienteFecha(idCliente, desde, hasta);
            Boolean flag = true;
            if (fletes.isEmpty()) {
                flag = false;
            }
            
            Double neto = 0.0;
            
            for(Flete f : fletes){
            neto = neto + f.getNeto();
            }

            modelo.addAttribute("fletes", fletes);
            modelo.put("flag", flag);
            modelo.put("cliente", clienteServicio.buscarCliente(idCliente));
            modelo.put("desde", desde);
            modelo.put("hasta", hasta);
            modelo.put("cantidad", fletes.size());
            modelo.put("neto", neto);

            return "flete_listarTodoIdCliente.html";
            
        } else {
            
            ArrayList<Flete> fletes = fleteServicio.buscarFletesIdChoferClienteFecha(idChofer, idCliente, desde, hasta);
            Boolean flag = true;
            if (fletes.isEmpty()) {
                flag = false;
            }
            
            Double neto = 0.0;
            Double ganancia = 0.0;
        
            for(Flete f : fletes){
            neto = neto + f.getNeto();
            ganancia = ganancia + f.getPorcentajeChofer();
            
            }

            modelo.addAttribute("fletes", fletes);
            modelo.put("flag", flag);
            modelo.put("cliente", clienteServicio.buscarCliente(idCliente));
            modelo.put("chofer", choferServicio.buscarChofer(idChofer));
            modelo.put("desde", desde);
            modelo.put("hasta", hasta);
            modelo.put("cantidad", fletes.size());
            modelo.put("neto", neto);
            modelo.put("ganancia", ganancia);

            return "flete_listarTodoIdChoferCliente.html";
            
        }
    }

    @GetMapping("/mostrar/{id}")
    public String mostrar(@PathVariable Long id, ModelMap modelo, HttpSession session) {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        if (logueado.getRol().equalsIgnoreCase("CHOFER")) {

            modelo.put("flete", fleteServicio.buscarFlete(id));
            modelo.put("idUsuario", logueado.getId());

            return "flete_mostrarChofer.html";

        } else {

            modelo.put("flete", fleteServicio.buscarFlete(id));

            return "flete_mostrarAdmin.html";
        }

    }

    @GetMapping("/mostrarPendiente/{id}")
    public String mostrarPendiente(@PathVariable Long id, ModelMap modelo) {

        modelo.put("flete", fleteServicio.buscarFlete(id));

        return "flete_mostrarPendienteAdmin.html";
    }

    @GetMapping("/mostrarDesdeCtaChofer/{id}")
    public String mostrarDesdeCtaChofer(@PathVariable Long id, ModelMap modelo) {

        modelo.put("flete", fleteServicio.buscarFlete(id));

        return "flete_mostrarAdminCtaChofer.html";

    }

    @GetMapping("/mostrarDesdeCtaCliente/{id}")
    public String mostrarDesdeCtaCliente(@PathVariable Long id, ModelMap modelo) {

        modelo.put("flete", fleteServicio.buscarFlete(id));

        return "flete_mostrarAdminCtaCliente.html";

    }

    @GetMapping("/mostrarFiltrado/{id}")
    public String mostrarFiltrado(@PathVariable Long id, ModelMap modelo, HttpSession session) {

        modelo.put("flete", fleteServicio.buscarFlete(id));

        return "flete_mostrarFiltrado.html";

    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @GetMapping("/aceptar/{id}")
    public String aceptarFlete(@PathVariable Long id, ModelMap modelo, HttpSession session) {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        fleteServicio.aceptarFlete(id, logueado);

        return "redirect:/flete/aceptado";

    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @GetMapping("/aceptado")
    public String fleteAceptado(HttpSession session, ModelMap modelo) {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        modelo.addAttribute("fletes", fleteServicio.buscarFletesPendiente(logueado.getIdOrg()));
        modelo.put("exito", "Viaje CONFIRMADO con éxito");

        return "flete_listarPendiente.html";

    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @GetMapping("/volverPendiente/{id}")
    public String volverPendiente(@PathVariable Long id, ModelMap modelo) {

        modelo.put("flete", fleteServicio.buscarFlete(id));

        return "flete_volverPendiente.html";

    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @GetMapping("/pendiente/{id}")
    public String pendienteFlete(@PathVariable Long id, ModelMap modelo, HttpSession session) {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        fleteServicio.pendienteFlete(id, logueado);

        return "redirect:/flete/fletePendiente";

    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @GetMapping("/fletePendiente")
    public String fletePendiente(HttpSession session, ModelMap modelo) {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        modelo.put("id", logueado.getId());
        modelo.put("exito", "Viaje RETORNADO a Pendiente");

        return "index_admin.html";

    }

    @GetMapping("/modificar/{id}")
    public String modificar(@PathVariable Long id, ModelMap modelo, HttpSession session) {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");
        if (logueado.getRol().equalsIgnoreCase("CHOFER")) {

            modelo.put("flete", fleteServicio.buscarFlete(id));
            modelo.addAttribute("clientes", clienteServicio.buscarClientesNombreAsc(logueado.getIdOrg()));
            modelo.addAttribute("camiones", camionServicio.buscarCamionesAsc(logueado.getIdOrg()));

            return "flete_modificarChofer.html";

        } else {

            modelo.put("flete", fleteServicio.buscarFlete(id));
            modelo.addAttribute("clientes", clienteServicio.buscarClientesNombreAsc(logueado.getIdOrg()));
            modelo.addAttribute("choferes", choferServicio.bucarChoferesNombreAsc(logueado.getIdOrg()));
            modelo.addAttribute("camiones", camionServicio.buscarCamionesAsc(logueado.getIdOrg()));

            return "flete_modificarAdmin.html";
        }
    }

    @PostMapping("/modificaChofer/{id}")
    public String modificaChofer(@RequestParam Long id, @RequestParam Long idCamion, @RequestParam Long idCliente, @RequestParam String fechaCarga,
            @RequestParam String fechaFlete, @RequestParam String origen, @RequestParam String destino, @RequestParam Double km, @RequestParam String tipoCereal,
            @RequestParam String cPorte, @RequestParam String ctg, @RequestParam Double tarifa, @RequestParam Double kg, ModelMap modelo) throws ParseException {

        fleteServicio.modificarFleteChofer(id, idCamion, fechaCarga, idCliente, origen, fechaFlete, destino, km, tipoCereal, tarifa, cPorte, ctg, kg);

        return "redirect:/flete/modificado/" + id;

    }
    
    @GetMapping("/modificado/{id}")
    public String modificado(@PathVariable Long id, ModelMap modelo) {

        modelo.put("flete", fleteServicio.buscarFlete(id));
        modelo.put("exito", "Viaje MODIFICADO con éxito");

        return "flete_modificadoChofer.html";      

    }

    @PostMapping("/modificaAdmin/{id}")
    public String modificaAdmin(@RequestParam Long id, @RequestParam Long idChofer, @RequestParam Long idCamion, @RequestParam Long idCliente, @RequestParam String fechaCarga,
            @RequestParam String fechaFlete, @RequestParam String origen, @RequestParam String destino, @RequestParam Double km, @RequestParam String tipoCereal,
            @RequestParam String cPorte, @RequestParam String ctg, @RequestParam Double tarifa, @RequestParam Double kg, @RequestParam Double comisionTpte,
            @RequestParam String comisionTpteChofer, @RequestParam Double iva, @RequestParam Double porcentaje, @RequestParam Double porcentajeChofer,
            ModelMap modelo, HttpSession session) throws ParseException {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");
        Flete flete = fleteServicio.buscarFlete(id);

        if (flete.getEstado().equalsIgnoreCase("PENDIENTE")) {

            fleteServicio.modificarFleteAdmin(id, idChofer, idCamion, fechaCarga, idCliente, origen, fechaFlete, destino, km, tipoCereal, tarifa, cPorte, ctg,
            kg, iva, porcentaje, porcentajeChofer, comisionTpte, comisionTpteChofer, logueado.getId());

            return "redirect:/flete/modificadoPendiente/" + id;

        } else {

            fleteServicio.modificarFleteAdmin(id, idChofer, idCamion, fechaCarga, idCliente, origen, fechaFlete, destino, km, tipoCereal, tarifa, cPorte, ctg,
                    kg, iva, porcentaje, porcentajeChofer, comisionTpte, comisionTpteChofer, logueado.getId());

            return "redirect:/flete/modificadoAdmin/" + id;

        }
    }
    
    @GetMapping("/modificadoPendiente/{id}")
    public String modificadoPendiente(@PathVariable Long id, ModelMap modelo) {

            modelo.put("flete", fleteServicio.buscarFlete(id));
            modelo.put("exito", "Viaje MODIFICADO con éxito");

            return "flete_modificadoAdminPendiente.html";        

    }
    
    @GetMapping("/modificadoAdmin/{id}")
    public String modificadoAdmin(@PathVariable Long id, ModelMap modelo) {

            modelo.put("flete", fleteServicio.buscarFlete(id));
            modelo.put("exito", "Flete MODIFICADO con éxito");

            return "flete_modificadoAdmin.html";        

    }

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id, HttpSession session, ModelMap modelo) {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");
        if (logueado.getRol().equalsIgnoreCase("CHOFER")) {

            modelo.put("flete", fleteServicio.buscarFlete(id));
            return "flete_eliminarChofer.html";

        } else {

            modelo.put("flete", fleteServicio.buscarFlete(id));
            return "flete_eliminarAdmin.html";
        }
    }

    @GetMapping("/elimina/{id}")
    public String elimina(@PathVariable Long id, HttpSession session, ModelMap modelo) {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");
        fleteServicio.eliminarFlete(id);

        if (logueado.getRol().equalsIgnoreCase("CHOFER")) {
            
            return "redirect:/flete/eliminado";

        } else {

            return "redirect:/flete/eliminadoAdmin";
            
        }

    }
    
    @GetMapping("/eliminado")
    public String eliminado(ModelMap modelo, HttpSession session) {
        
        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

            modelo.put("chofer", logueado);
            modelo.put("exito", "Viaje ELIMINADO con éxito");

            return "index_chofer.html";      

    }
    
    @GetMapping("/eliminadoAdmin")
    public String eliminadoAdmin(ModelMap modelo, HttpSession session) {
        
        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

            modelo.put("id", logueado.getId());
            modelo.put("exito", "Viaje ELIMINADO con éxito");

            return "index_admin.html";    

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

    @PostMapping("/exportarAdminIdChofer")
    public String exportarIdChofer(@RequestParam String desde, @RequestParam String hasta, @RequestParam Long idChofer, ModelMap modelo) throws ParseException {

        modelo.addAttribute("fletes", fleteServicio.buscarFletesIdChoferFechaAsc(idChofer, desde, hasta));
        modelo.put("desde", desde);
        modelo.put("hasta", hasta);
        modelo.put("id", idChofer);

        return "flete_exportarAdminIdChofer.html";

    }

    @PostMapping("/exportaAdminIdChofer")
    public void exporta(@RequestParam Long idChofer, @RequestParam String desde, @RequestParam String hasta, HttpServletResponse response) throws IOException, ParseException {

        ArrayList<Flete> myObjects = fleteServicio.buscarFletesIdChoferFechaAsc(idChofer, desde, hasta);
        String htmlContent = generateHtmlFromObjects(myObjects);
        excelServicio.exportHtmlToExcel(htmlContent, response);

    }
    
    @PostMapping("/exportarAdminIdCliente")
    public String exportarIdCliente(@RequestParam String desde, @RequestParam String hasta, @RequestParam Long idCliente, ModelMap modelo) throws ParseException {

        modelo.addAttribute("fletes", fleteServicio.buscarFletesIdClienteFechaAsc(idCliente, desde, hasta));
        modelo.put("desde", desde);
        modelo.put("hasta", hasta);
        modelo.put("id", idCliente);

        return "flete_exportarAdminIdCliente.html";

    }

    @PostMapping("/exportaAdminIdCliente")
    public void exportaIdCliente(@RequestParam Long idCliente, @RequestParam String desde, @RequestParam String hasta, HttpServletResponse response) throws IOException, ParseException {

        ArrayList<Flete> myObjects = fleteServicio.buscarFletesIdClienteFechaAsc(idCliente, desde, hasta);
        String htmlContent = generateHtmlFromObjects(myObjects);
        excelServicio.exportHtmlToExcel(htmlContent, response);

    }
    
    @PostMapping("/exportarAdminIdChoferCliente")
    public String exportarIdChoferCliente(@RequestParam String desde, @RequestParam String hasta, @RequestParam Long idChofer, @RequestParam Long idCliente, ModelMap modelo) throws ParseException {

        modelo.addAttribute("fletes", fleteServicio.buscarFletesIdChoferClienteFechaAsc(idChofer, idCliente, desde, hasta));
        modelo.put("desde", desde);
        modelo.put("hasta", hasta);
        modelo.put("idChofer", idChofer);
        modelo.put("idCliente", idCliente);

        return "flete_exportarAdminIdChoferCliente.html";

    }

    @PostMapping("/exportaAdminIdChoferCliente")
    public void exportaIdChoferCliente(@RequestParam Long idChofer, @RequestParam Long idCliente, @RequestParam String desde, @RequestParam String hasta, HttpServletResponse response) throws IOException, ParseException {

        ArrayList<Flete> myObjects = fleteServicio.buscarFletesIdChoferClienteFechaAsc(idChofer, idCliente, desde, hasta);
        String htmlContent = generateHtmlFromObjects(myObjects);
        excelServicio.exportHtmlToExcel(htmlContent, response);

    }

    @PostMapping("/exportarAdminTodos")
    public String exportarTodos(@RequestParam String desde, @RequestParam String hasta, ModelMap modelo, HttpSession session) throws ParseException {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        modelo.addAttribute("fletes", fleteServicio.buscarFletesRangoFechaAsc(logueado.getIdOrg(), desde, hasta));
        modelo.put("desde", desde);
        modelo.put("hasta", hasta);
        modelo.put("id", logueado.getIdOrg());

        return "flete_exportarAdminTodos.html";

    }

    @PostMapping("/exportaAdminTodos")
    public void exportaTodos(@RequestParam Long idOrg, @RequestParam String desde, @RequestParam String hasta, HttpServletResponse response) throws IOException, ParseException {

        ArrayList<Flete> myObjects = fleteServicio.buscarFletesRangoFechaAsc(idOrg, desde, hasta);
        String htmlContent = generateHtmlFromObjects(myObjects);
        excelServicio.exportHtmlToExcel(htmlContent, response);

    }

    @PostMapping("/exportarAdminDesdeChofer")
    public String exportarDesdeChofer(@RequestParam String desde, @RequestParam String hasta, @RequestParam Long idChofer, ModelMap modelo) throws ParseException {

        modelo.addAttribute("fletes", fleteServicio.buscarFletesIdChoferFechaAsc(idChofer, desde, hasta));
        modelo.put("desde", desde);
        modelo.put("hasta", hasta);
        modelo.put("id", idChofer);

        return "flete_exportarAdminDesdeChofer.html";

    }

    @PostMapping("/exportaAdminDesdeChofer")
    public void exportaAdminDesdeChofer(@RequestParam Long idChofer, @RequestParam String desde, @RequestParam String hasta, HttpServletResponse response) throws IOException, ParseException {

        ArrayList<Flete> myObjects = fleteServicio.buscarFletesIdChoferFechaAsc(idChofer, desde, hasta);
        String htmlContent = generateHtmlFromObjects(myObjects);
        excelServicio.exportHtmlToExcel(htmlContent, response);

    }

    @PostMapping("/exportarAdminDesdeCliente")
    public String exportarAdminDesdeCliente(@RequestParam String desde, @RequestParam String hasta, @RequestParam Long idCliente, ModelMap modelo) throws ParseException {

        modelo.addAttribute("fletes", fleteServicio.buscarFletesIdClienteFechaAsc(idCliente, desde, hasta));
        modelo.put("desde", desde);
        modelo.put("hasta", hasta);
        modelo.put("id", idCliente);

        return "flete_exportarAdminDesdeCliente.html";

    }

    @PostMapping("/exportaAdminDesdeCliente")
    public void exportaAdminDesdeCliente(@RequestParam Long idCliente, @RequestParam String desde, @RequestParam String hasta, HttpServletResponse response) throws IOException, ParseException {

        ArrayList<Flete> myObjects = fleteServicio.buscarFletesIdClienteFechaAsc(idCliente, desde, hasta);
        String htmlContent = generateHtmlFromObjects(myObjects);
        excelServicio.exportHtmlToExcel(htmlContent, response);

    }

    @PostMapping("/exportarChofer")
    public String exportarFletes(@RequestParam String desde, @RequestParam String hasta, @RequestParam Long id,
            ModelMap modelo) throws ParseException {

        modelo.addAttribute("fletes", fleteServicio.buscarFletesIdChoferFechaAsc(id, desde, hasta));
        modelo.put("desde", desde);
        modelo.put("hasta", hasta);
        modelo.put("id", id);

        return "flete_exportarChofer.html";
    }

    @PostMapping("/exportaChofer")
    public void exportToExcel(@RequestParam String desde, @RequestParam String hasta, @RequestParam Long idChofer, HttpServletResponse response) throws IOException, ParseException {

        ArrayList<Flete> myObjects = fleteServicio.buscarFletesIdChoferFechaAsc(idChofer, desde, hasta);
        String htmlContent = generateHtmlFromObjectsChofer(myObjects);
        excelServicio.exportHtmlToExcel(htmlContent, response);

    }

    private String generateHtmlFromObjects(ArrayList<Flete> objects) {
        StringBuilder sb = new StringBuilder();
        sb.append("<table>");
        sb.append("<thead><tr>"
                + "<th>ID</th>"
                + "<th>Fecha Carga</th>"
                + "<th>Fecha Viaje</th>"
                + "<th>Cliente</th>"
                + "<th>Lugar de Carga</th>"
                + "<th>Destino de Carga</th>"
                + "<th>Chofer</th>"
                + "<th>Porcentaje</th>"
                + "<th>Camión</th>"
                + "<th>KM</th>"
                + "<th>Tipo Carga</th>"
                + "<th>CP</th>"
                + "<th>CTG</th>"
                + "<th>Tarifa</th>"
                + "<th>Kg</th>"
                + "<th>Neto</th>"
                + "<th>IVA</th>"
                + "<th>Total</th>"
                + "</tr></thead>");
        sb.append("<tbody>");
        for (Flete flete : objects) {
            sb.append("<tr><td>").append(flete.getIdFlete()).append("</td>"
                    + "<td>").append(flete.getFechaCarga()).append("</td>"
                    + "<td>").append(flete.getFechaFlete()).append("</td>"
                    + "<td>").append(flete.getCliente().getNombre()).append("</td>"
                    + "<td>").append(flete.getOrigenFlete()).append("</td>"
                    + "<td>").append(flete.getDestinoFlete()).append("</td>"
                    + "<td>").append(flete.getChofer().getNombre()).append("</td>"
                    + "<td>").append(flete.getPorcentajeChofer()).append("</td>"
                    + "<td>").append(flete.getCamion().getDominio()).append("</td>"
                    + "<td>").append(flete.getKmFlete()).append("</td>"
                    + "<td>").append(flete.getTipoCereal()).append("</td>"
                    + "<td>").append(flete.getCartaPorte()).append("</td>"
                    + "<td>").append(flete.getCtg()).append("</td>"
                    + "<td>").append(flete.getTarifa()).append("</td>"
                    + "<td>").append(flete.getKgFlete()).append("</td>"
                    + "<td>").append(flete.getNeto()).append("</td>"
                    + "<td>").append(flete.getIva()).append("</td>"
                    + "<td>").append(flete.getTotal()).append("</td>"
                    + "</tr>");
        }
        sb.append("</tbody></table>");
        return sb.toString();
    }

    private String generateHtmlFromObjectsChofer(ArrayList<Flete> objects) {
        StringBuilder sb = new StringBuilder();
        sb.append("<table>");
        sb.append("<thead><tr>"
                + "<th>ID</th>"
                + "<th>Fecha Carga</th>"
                + "<th>Fecha Viaje</th>"
                + "<th>Cliente</th>"
                + "<th>Lugar de Carga</th>"
                + "<th>Destino de Carga</th>"
                + "<th>KM</th>"
                + "<th>Tipo Carga</th>"
                + "<th>CP</th>"
                + "<th>CTG</th>"
                + "<th>Tarifa</th>"
                + "<th>Kg</th>"
                + "<th>Neto</th>"
                + "<th>Porcentaje</th>"
                + "</tr></thead>");
        sb.append("<tbody>");
        for (Flete flete : objects) {
            sb.append("<tr><td>").append(flete.getIdFlete()).append("</td>"
                    + "<td>").append(flete.getFechaCarga()).append("</td>"
                    + "<td>").append(flete.getFechaFlete()).append("</td>"
                    + "<td>").append(flete.getCliente().getNombre()).append("</td>"
                    + "<td>").append(flete.getOrigenFlete()).append("</td>"
                    + "<td>").append(flete.getDestinoFlete()).append("</td>"
                    + "<td>").append(flete.getKmFlete()).append("</td>"
                    + "<td>").append(flete.getTipoCereal()).append("</td>"
                    + "<td>").append(flete.getCartaPorte()).append("</td>"
                    + "<td>").append(flete.getCtg()).append("</td>"
                    + "<td>").append(flete.getTarifa()).append("</td>"
                    + "<td>").append(flete.getKgFlete()).append("</td>"
                    + "<td>").append(flete.getNeto()).append("</td>"
                    + "<td>").append(flete.getPorcentajeChofer()).append("</td>"
                    + "</tr>");
        }
        sb.append("</tbody></table>");
        return sb.toString();
    }

}
