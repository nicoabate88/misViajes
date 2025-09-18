package abate.abate.controladores;

import abate.abate.entidades.Cliente;
import abate.abate.entidades.Flete;
import abate.abate.entidades.Imagen;
import abate.abate.entidades.Usuario;
import abate.abate.servicios.AcopladoServicio;
import abate.abate.servicios.CamionServicio;
import abate.abate.servicios.ChoferServicio;
import abate.abate.servicios.ClienteServicio;
import abate.abate.servicios.ExcelServicio;
import abate.abate.servicios.FleteServicio;
import abate.abate.servicios.ImagenServicio;
import abate.abate.servicios.ProductoServicio;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/flete")
@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_CHOFER')")
public class FleteControlador {

    @Autowired
    private FleteServicio fleteServicio;
    @Autowired
    private ClienteServicio clienteServicio;
    @Autowired
    private ProductoServicio productoServicio;
    @Autowired
    private ChoferServicio choferServicio;
    @Autowired
    private ExcelServicio excelServicio;
    @Autowired
    private CamionServicio camionServicio;
    @Autowired
    private AcopladoServicio acopladoServicio;
    @Autowired
    private ImagenServicio imagenServicio;
    

    @GetMapping("/registrar")
    public String registrarFlete(ModelMap modelo, HttpSession session) {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");
        
        if(logueado.getIdOrg() != 3){
        
        if (logueado.getRol().equalsIgnoreCase("CHOFER")) {

            modelo.put("chofer", logueado);
            modelo.addAttribute("clientes", clienteServicio.buscarClientesHabNombreAsc(logueado.getIdOrg()));
            modelo.addAttribute("camiones", camionServicio.buscarCamionesHabAsc(logueado.getIdOrg()));
            modelo.addAttribute("acoplados", acopladoServicio.buscarAcopladosHabAsc(logueado.getIdOrg()));
            modelo.addAttribute("productos", productoServicio.buscarProductosHabAsc(logueado.getIdOrg()));

            return "flete_registrarChofer.html";

        } else {

            modelo.addAttribute("choferes", choferServicio.bucarChoferesHabNombreAsc(logueado.getIdOrg()));

            return "flete_registrarAdmin.html";
        }
        
        } else { //obligatorio cargar imagen de CP y Descarga
            
            if (logueado.getRol().equalsIgnoreCase("CHOFER")) {
            
            modelo.put("chofer", logueado);
            modelo.addAttribute("clientes", clienteServicio.buscarClientesHabNombreAsc(logueado.getIdOrg()));
            modelo.addAttribute("camiones", camionServicio.buscarCamionesAsc(logueado.getIdOrg()));
            modelo.addAttribute("acoplados", acopladoServicio.buscarAcopladosHabAsc(logueado.getIdOrg()));
            modelo.addAttribute("productos", productoServicio.buscarProductosHabAsc(logueado.getIdOrg()));

            return "flete_registrarChoferImagen.html";
            
        } else {
                
            modelo.addAttribute("choferes", choferServicio.bucarChoferesNombreAsc(logueado.getIdOrg()));

            return "flete_registrarAdmin.html";
                
            }
        }
    }
    
    @GetMapping("/registrarAdmin")
    public String registrarAdmin(@RequestParam("idChofer") Long idChofer, ModelMap modelo, HttpSession session) {
        
            Usuario logueado = (Usuario) session.getAttribute("usuariosession");
            modelo.addAttribute("clientes", clienteServicio.buscarClientesHabNombreAsc(logueado.getIdOrg()));
            modelo.addAttribute("choferes", choferServicio.bucarChoferesHabNombreAsc(logueado.getIdOrg()));
            modelo.addAttribute("camiones", camionServicio.buscarCamionesHabAsc(logueado.getIdOrg()));
            modelo.addAttribute("acoplados", acopladoServicio.buscarAcopladosHabAsc(logueado.getIdOrg()));
            modelo.addAttribute("productos", productoServicio.buscarProductosHabAsc(logueado.getIdOrg()));
            modelo.put("chofer", choferServicio.buscarChofer(idChofer));

            return "flete_registrarAdmin1.html";

    }

    @PostMapping("/registroChofer")
    public String registroFlete(@RequestParam Long idCamion, @RequestParam(required = false) Long idAcoplado, @RequestParam Long idCliente,
            @RequestParam String fechaCarga, @RequestParam String fechaFlete,
            @RequestParam String origen, @RequestParam String destino, @RequestParam Double km, @RequestParam Long idProducto,
            @RequestParam String cPorte, @RequestParam String ctg, @RequestParam Double tarifa, @RequestParam Double kg,
            @RequestParam(required = false) String observacion, ModelMap modelo, HttpSession session) throws ParseException {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        fleteServicio.crearFleteChofer(logueado.getIdOrg(), fechaCarga, idCliente, idCamion, idAcoplado, origen, fechaFlete, destino, km, 
                idProducto, tarifa, cPorte, ctg, kg, observacion, logueado.getId());

        return "redirect:/flete/registradoChofer";

    }
    
    @PostMapping("/registroChoferImagen")
    public String registroFleteImagen(@RequestParam Long idCamion, @RequestParam(required = false) Long idAcoplado, @RequestParam Long idCliente,
            @RequestParam String fechaCarga, @RequestParam String fechaFlete,
            @RequestParam String origen, @RequestParam String destino, @RequestParam Double km, @RequestParam Long idProducto,
            @RequestParam String cPorte, @RequestParam String ctg, @RequestParam Double tarifa, @RequestParam Double kg,
            @RequestParam(required = false) String observacion, @RequestParam("cp") MultipartFile cp, @RequestParam("descarga") MultipartFile descarga,
            ModelMap modelo, HttpSession session) throws ParseException {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");
        
        Long idCP;
        Long idDescarga;
        
        try {

            Imagen imagen = new Imagen();
            imagen.setNombre("CP");
            imagen.setTipo(cp.getContentType());
            if (cp.getContentType().equals("application/pdf")) {
                imagen.setDatos(cp.getBytes());
            } else {
                imagen.setDatos(optimizeImage(cp));
            }

           idCP = imagenServicio.crearImagenCPObligatorio(imagen);

        } catch (Exception e) {

            modelo.addAttribute("error", "Ocurrió un error al procesar su imagen CP. Intente nuevamente o ingrese otro archivo");
            modelo.put("chofer", logueado);
            modelo.put("destino", destino);
            modelo.put("origen", origen);
            modelo.put("km", km);
            modelo.put("tarifa", tarifa);
            modelo.put("kg", kg);
            modelo.put("cPorte", cPorte);
            modelo.put("ctg", ctg);
            modelo.addAttribute("clientes", clienteServicio.buscarClientesHabNombreAsc(logueado.getIdOrg()));
            modelo.addAttribute("camiones", camionServicio.buscarCamionesHabAsc(logueado.getIdOrg()));
            modelo.addAttribute("acoplados", acopladoServicio.buscarAcopladosHabAsc(logueado.getIdOrg()));
            modelo.addAttribute("productos", productoServicio.buscarProductosHabAsc(logueado.getIdOrg()));

            return "flete_registrarChoferImagen.html";
            
        }
        
        try {

            Imagen imagen = new Imagen();
            imagen.setNombre("Descarga");
            imagen.setTipo(descarga.getContentType());
            if (descarga.getContentType().equals("application/pdf")) {
                imagen.setDatos(descarga.getBytes());
            } else {
                imagen.setDatos(optimizeImage(descarga));
            }

            idDescarga = imagenServicio.crearImagenDescargaObligatorio(imagen);

        } catch (Exception e) {

            modelo.addAttribute("error", "Ocurrió un error al procesar su imagen Ticket de Descarga. Intente nuevamente o ingrese otro archivo");
            modelo.put("chofer", logueado);
            modelo.put("destino", destino);
            modelo.put("origen", origen);
            modelo.put("km", km);
            modelo.put("tarifa", tarifa);
            modelo.put("kg", kg);
            modelo.put("cPorte", cPorte);
            modelo.put("ctg", ctg);
            modelo.addAttribute("clientes", clienteServicio.buscarClientesHabNombreAsc(logueado.getIdOrg()));
            modelo.addAttribute("camiones", camionServicio.buscarCamionesHabAsc(logueado.getIdOrg()));
            modelo.addAttribute("acoplados", acopladoServicio.buscarAcopladosHabAsc(logueado.getIdOrg()));
            modelo.addAttribute("productos", productoServicio.buscarProductosHabAsc(logueado.getIdOrg()));

            return "flete_registrarChoferImagen.html";
        }

        fleteServicio.crearFleteChoferImagenObligatorio(logueado.getIdOrg(), fechaCarga, idCliente, idCamion, idAcoplado, origen, fechaFlete, destino, km, 
                idProducto, tarifa, cPorte, ctg, kg, observacion, idCP, idDescarga, logueado.getId());

        return "redirect:/flete/registradoChofer";

    }

    @GetMapping("/registradoChofer")
    public String registradoChofer(HttpSession session, ModelMap modelo) {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        Long id = fleteServicio.buscarUltimo(logueado.getIdOrg());

        modelo.put("flete", fleteServicio.buscarFlete(id));
        modelo.put("exito", "Viaje REGISTRADO con éxito");

        return "flete_mostrarChofer.html";

    }  
        
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @PostMapping("/registroAdmin")
    public String registroFleteAdmin(@RequestParam Long idChofer, @RequestParam Long idCamion, @RequestParam(required = false) Long idAcoplado, 
            @RequestParam Long idCliente, @RequestParam String fechaCarga, @RequestParam String fechaFlete,
            @RequestParam String origen, @RequestParam String destino, @RequestParam Double km, @RequestParam Long idProducto, @RequestParam String cPorte,
            @RequestParam String ctg, @RequestParam Double tarifa, @RequestParam Double kg, @RequestParam Double comisionTpte, @RequestParam Double comisionTpteValor, 
            @RequestParam String comisionTpteChofer, @RequestParam Double neto, @RequestParam Double iva, @RequestParam Double total, @RequestParam Double porcentaje, 
            @RequestParam Double gananciaChofer, @RequestParam(required = false) String observacion, ModelMap modelo, HttpSession session) throws ParseException {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        fleteServicio.crearFleteAdmin(logueado.getIdOrg(), idChofer, idCamion, idAcoplado, fechaCarga, idCliente, origen, fechaFlete, destino, km, idProducto, tarifa, 
                cPorte, ctg, kg, comisionTpte, comisionTpteValor, comisionTpteChofer, neto, iva, total, porcentaje, gananciaChofer, observacion, logueado.getId());

        return "redirect:/flete/registradoAdmin";

    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @GetMapping("/registradoAdmin")
    public String registradoAdmin(HttpSession session, ModelMap modelo) {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        Long id = fleteServicio.buscarUltimo(logueado.getIdOrg());

        modelo.put("flete", fleteServicio.buscarFlete(id));
        modelo.put("exito", "Viaje REGISTRADO con éxito");
        modelo.put("desde", obtenerFechaDesde());
        modelo.put("hasta", obtenerFechaHasta());
        modelo.put("idChofer", null);
        modelo.put("idCamion", null);
        modelo.put("idCliente", null);

        return "flete_mostrarAdmin.html";

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
            modelo.put("chofer", logueado);
            modelo.put("desde", desde);
            modelo.put("hasta", hasta);

            return "flete_listarChofer.html";

        } else {

            List<Flete> fletes = fleteServicio.buscarFletesPendiente(logueado.getIdOrg());
            
            if(!fletes.isEmpty()){
            
            modelo.addAttribute("fletes", fletes);
            modelo.put("cantidad", fletes.size());

            return "flete_listarPendiente.html";
            
            } else {
                
                return "redirect:/flete/listarTodo";
                
            }
            
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

        Cliente cliente = clienteServicio.buscarCliente(id);
        String desde = obtenerFechaDesde();
        String hasta = obtenerFechaHasta();
        ArrayList<Flete> fletes = fleteServicio.buscarFletesIdClienteFecha(id, desde, hasta);
        Boolean flag = true;
        if (fletes.isEmpty()) {
            flag = false;
        }

        modelo.addAttribute("fletes", fletes);
        modelo.put("flag", flag);
        modelo.put("idCliente", id);
        modelo.put("chofer", null);
        modelo.put("camion", null);
        modelo.put("cliente", cliente);
        modelo.put("cliente", clienteServicio.buscarCliente(id));
        modelo.addAttribute("choferes", choferServicio.bucarChoferesHabNombreAsc(cliente.getIdOrg()));
        modelo.addAttribute("clientes", clienteServicio.buscarClientesHabNombreAsc(cliente.getIdOrg()));
        modelo.addAttribute("camiones", camionServicio.buscarCamionesHabAsc(cliente.getIdOrg()));
        modelo.put("desde", desde);
        modelo.put("hasta", hasta);
        modelo.put("cantidad", fletes.size());

        return "flete_listarTodoFiltrado.html";
    }

    @GetMapping("/listarIdChofer/{id}")
    public String listarIdChofer(@PathVariable Long id, ModelMap modelo) throws ParseException {

        Usuario chofer = choferServicio.buscarChofer(id);
        String desde = obtenerFechaDesde();
        String hasta = obtenerFechaHasta();
        ArrayList<Flete> fletes = fleteServicio.buscarFletesIdChoferFecha(id, desde, hasta);
        Boolean flag = true;
        if (fletes.isEmpty()) {
            flag = false;
        }
        
        modelo.addAttribute("fletes", fletes);
        modelo.put("flag", flag);
        modelo.put("idChofer", id);
        modelo.put("chofer", chofer);
        modelo.put("camion", null);
        modelo.put("cliente", null);
        modelo.addAttribute("choferes", choferServicio.bucarChoferesHabNombreAsc(chofer.getIdOrg()));
        modelo.addAttribute("clientes", clienteServicio.buscarClientesHabNombreAsc(chofer.getIdOrg()));
        modelo.addAttribute("camiones", camionServicio.buscarCamionesHabAsc(chofer.getIdOrg()));
        modelo.put("desde", desde);
        modelo.put("hasta", hasta);
        modelo.put("cantidad", fletes.size());

        return "flete_listarTodoFiltrado.html";
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

        modelo.addAttribute("fletes", fletes);
        modelo.put("flag", flag);
        modelo.addAttribute("choferes", choferServicio.bucarChoferesHabNombreAsc(logueado.getIdOrg()));
        modelo.addAttribute("clientes", clienteServicio.buscarClientesHabNombreAsc(logueado.getIdOrg()));
        modelo.addAttribute("camiones", camionServicio.buscarCamionesHabAsc(logueado.getIdOrg()));
        modelo.put("desde", desde);
        modelo.put("hasta", hasta);
        modelo.put("cantidad", fletes.size());

        return "flete_listarTodo.html";
    }

    @PostMapping("/listarTodoFiltrado")
    public String listarTodoFiltrado(@RequestParam String desde, @RequestParam String hasta, @RequestParam(required = false) Long idChofer,
           @RequestParam(required = false) Long idCamion, @RequestParam(required = false) Long idCliente, ModelMap modelo, HttpSession session) throws ParseException {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        if (idChofer == null && idCliente == null && idCamion == null) {

            ArrayList<Flete> fletes = fleteServicio.buscarFletesRangoFecha(logueado.getIdOrg(), desde, hasta);
            Boolean flag = true;
            if (fletes.isEmpty()) {
                flag = false;
            }

            modelo.addAttribute("fletes", fletes);
            modelo.put("flag", flag);
            modelo.put("cantidad", fletes.size());
            modelo.put("chofer", null);
            modelo.put("camion", null);
            modelo.put("cliente", null);

        } else if (idChofer != null && idCliente == null && idCamion == null) {

            ArrayList<Flete> fletes = fleteServicio.buscarFletesIdChoferFecha(idChofer, desde, hasta);
            Boolean flag = true;
            if (fletes.isEmpty()) {
                flag = false;
            }

            modelo.addAttribute("fletes", fletes);
            modelo.put("flag", flag);
            modelo.put("chofer", choferServicio.buscarChofer(idChofer));
            modelo.put("camion", null);
            modelo.put("cliente", null);
            modelo.put("cantidad", fletes.size());

        } else if (idChofer == null && idCliente != null && idCamion == null) {
            
            ArrayList<Flete> fletes = fleteServicio.buscarFletesIdClienteFecha(idCliente, desde, hasta);
            Boolean flag = true;
            if (fletes.isEmpty()) {
                flag = false;
            }

            modelo.addAttribute("fletes", fletes);
            modelo.put("flag", flag);
            modelo.put("cliente", clienteServicio.buscarCliente(idCliente));
            modelo.put("camion", null);
            modelo.put("chofer", null);
            modelo.put("cantidad", fletes.size());
            
        } else if (idChofer == null && idCliente == null && idCamion != null) {
            
            ArrayList<Flete> fletes = fleteServicio.buscarFletesIdCamionFecha(idCamion, desde, hasta);
            Boolean flag = true;
            if (fletes.isEmpty()) {
                flag = false;
            }

            modelo.addAttribute("fletes", fletes);
            modelo.put("flag", flag);
            modelo.put("cliente", null);
            modelo.put("chofer", null);
            modelo.put("camion", camionServicio.buscarCamion(idCamion));
            modelo.put("cantidad", fletes.size());
        
        }  else if (idChofer != null && idCliente != null && idCamion == null) {
            
            ArrayList<Flete> fletes = fleteServicio.buscarFletesIdChoferClienteFecha(idChofer, idCliente, desde, hasta);
            Boolean flag = true;
            if (fletes.isEmpty()) {
                flag = false;
            }

            modelo.addAttribute("fletes", fletes);
            modelo.put("flag", flag);
            modelo.put("cliente", clienteServicio.buscarCliente(idCliente));
            modelo.put("chofer", choferServicio.buscarChofer(idChofer));
            modelo.put("camion", null);
            modelo.put("cantidad", fletes.size());
            
        } else if (idChofer != null && idCliente == null && idCamion != null) {
            
            ArrayList<Flete> fletes = fleteServicio.buscarFletesIdChoferCamionFecha(idChofer, idCamion, desde, hasta);
            Boolean flag = true;
            if (fletes.isEmpty()) {
                flag = false;
            }

            modelo.addAttribute("fletes", fletes);
            modelo.put("flag", flag);
            modelo.put("cliente", null);
            modelo.put("chofer", choferServicio.buscarChofer(idChofer));
            modelo.put("camion", camionServicio.buscarCamion(idCamion));
            modelo.put("cantidad", fletes.size());
            
        } else if (idChofer == null && idCliente != null && idCamion != null) {
            
            ArrayList<Flete> fletes = fleteServicio.buscarFletesIdClienteCamionFecha(idCliente, idCamion, desde, hasta);
            Boolean flag = true;
            if (fletes.isEmpty()) {
                flag = false;
            }

            modelo.addAttribute("fletes", fletes);
            modelo.put("flag", flag);
            modelo.put("cliente", clienteServicio.buscarCliente(idCliente));
            modelo.put("chofer", null);
            modelo.put("camion", camionServicio.buscarCamion(idCamion));
            modelo.put("cantidad", fletes.size());
            
        } else {
            
            ArrayList<Flete> fletes = fleteServicio.buscarFletesIdChoferClienteCamionFecha(idChofer, idCliente, idCamion, desde, hasta);
            Boolean flag = true;
            if (fletes.isEmpty()) {
                flag = false;
            }

            modelo.addAttribute("fletes", fletes);
            modelo.put("flag", flag);
            modelo.put("cliente", clienteServicio.buscarCliente(idCliente));
            modelo.put("chofer", choferServicio.buscarChofer(idChofer));
            modelo.put("camion", camionServicio.buscarCamion(idCamion));
            modelo.put("cantidad", fletes.size());
            
        }
        
        modelo.put("desde", desde);
        modelo.put("hasta", hasta);
        modelo.put("idChofer", idChofer);
        modelo.put("idCliente", idCliente);
        modelo.put("idCamion", idCamion);
        modelo.addAttribute("choferes", choferServicio.bucarChoferesHabNombreAsc(logueado.getIdOrg()));
        modelo.addAttribute("clientes", clienteServicio.buscarClientesHabNombreAsc(logueado.getIdOrg()));
        modelo.addAttribute("camiones", camionServicio.buscarCamionesHabAsc(logueado.getIdOrg()));
        
        return "flete_listarTodoFiltrado.html";
    }

    @GetMapping("/mostrarChofer/{id}")
    public String mostrarChofer(@PathVariable Long id, ModelMap modelo) {

            modelo.put("flete", fleteServicio.buscarFlete(id));

            return "flete_mostrarChofer.html";

    }

    @GetMapping("/mostrarPendiente/{id}")
    public String mostrarPendiente(@PathVariable Long id, ModelMap modelo) {

        modelo.put("flete", fleteServicio.buscarFlete(id));

        return "flete_mostrarPendiente.html";
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

    @GetMapping("/mostrarAdmin")
    public String mostrarAdmin(@RequestParam Long id, @RequestParam String desde, @RequestParam String hasta, @RequestParam(required = false) Long idChofer,
           @RequestParam(required = false) Long idCamion, @RequestParam(required = false) Long idCliente, @RequestParam(required = false) String gasto,
           ModelMap modelo, HttpSession session) {

        modelo.put("flete", fleteServicio.buscarFlete(id));
        modelo.put("desde", desde);
        modelo.put("hasta", hasta);
        modelo.put("idChofer", idChofer);
        modelo.put("idCamion", idCamion);
        modelo.put("idCliente", idCliente);
        if(gasto != null){
            modelo.put("exito", "Gasto ELIMINADO con éxito");
        }

        return "flete_mostrarAdmin.html";

    }
    
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @GetMapping("/aceptarAdmin")
    public String aceptarAdmin(@RequestParam Long id, @RequestParam String desde, @RequestParam String hasta, @RequestParam(required = false) Long idChofer,
           @RequestParam(required = false) Long idCamion, @RequestParam(required = false) Long idCliente, ModelMap modelo, HttpSession session) {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        fleteServicio.aceptarFlete(id, logueado);

        return "redirect:/flete/listarTodoFiltradoGet?&desde=" + desde + "&hasta=" + hasta +
           (idChofer != null ? "&idChofer=" + idChofer : "") +
           (idCamion != null ? "&idCamion=" + idCamion : "") +
           (idCliente != null ? "&idCliente=" + idCliente : "");

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

        List<Flete> fletes = fleteServicio.buscarFletesPendiente(logueado.getIdOrg());
        
        modelo.addAttribute("fletes", fletes);
        modelo.put("cantidad", fletes.size());
        modelo.put("exito", "Viaje CONFIRMADO con éxito");

        return "flete_listarPendiente.html";

    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @GetMapping("/volverPendiente")
    public String volverPendiente(@RequestParam Long id, @RequestParam String desde, @RequestParam String hasta, @RequestParam(required = false) Long idChofer,
           @RequestParam(required = false) Long idCamion, @RequestParam(required = false) Long idCliente, ModelMap modelo) {

        modelo.put("flete", fleteServicio.buscarFlete(id));
        modelo.put("desde", desde);
        modelo.put("hasta", hasta);
        modelo.put("idChofer", idChofer);
        modelo.put("idCamion", idCamion);
        modelo.put("idCliente", idCliente);

        return "flete_volverPendiente.html";

    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @GetMapping("/pendiente")
    public String pendienteFlete(@RequestParam Long id, @RequestParam String desde, @RequestParam String hasta, @RequestParam(required = false) Long idChofer,
           @RequestParam(required = false) Long idCamion, @RequestParam(required = false) Long idCliente, ModelMap modelo, HttpSession session) {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        fleteServicio.pendienteFlete(id, logueado);

        return "redirect:/flete/listarTodoFiltradoGet?desde=" + desde + "&hasta=" + hasta +
           (idChofer != null ? "&idChofer=" + idChofer : "") +
           (idCamion != null ? "&idCamion=" + idCamion : "") +
           (idCliente != null ? "&idCliente=" + idCliente : "") +
            "&pendiente=" + "si";
    }

    @GetMapping("/modificar/{id}")
    public String modificar(@PathVariable Long id, ModelMap modelo, HttpSession session) {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");
        if (logueado.getRol().equalsIgnoreCase("CHOFER")) {

            modelo.put("flete", fleteServicio.buscarFlete(id));
            modelo.addAttribute("clientes", clienteServicio.buscarClientesHabNombreAsc(logueado.getIdOrg()));
            modelo.addAttribute("camiones", camionServicio.buscarCamionesHabAsc(logueado.getIdOrg()));
            modelo.addAttribute("acoplados", acopladoServicio.buscarAcopladosHabAsc(logueado.getIdOrg()));
            modelo.addAttribute("productos", productoServicio.buscarProductosHabAsc(logueado.getIdOrg()));

            return "flete_modificarChofer.html";

        } else {

            modelo.put("flete", fleteServicio.buscarFlete(id));
            modelo.addAttribute("clientes", clienteServicio.buscarClientesHabNombreAsc(logueado.getIdOrg()));
            modelo.addAttribute("choferes", choferServicio.bucarChoferesHabNombreAsc(logueado.getIdOrg()));
            modelo.addAttribute("camiones", camionServicio.buscarCamionesHabAsc(logueado.getIdOrg()));
            modelo.addAttribute("acoplados", acopladoServicio.buscarAcopladosHabAsc(logueado.getIdOrg()));
            modelo.addAttribute("productos", productoServicio.buscarProductosHabAsc(logueado.getIdOrg()));

            return "flete_modificarAdmin.html";
        }
    }

    @PostMapping("/modificaChofer/{id}")
    public String modificaChofer(@RequestParam Long id, @RequestParam Long idCamion, @RequestParam(required = false) Long idAcoplado, 
            @RequestParam Long idCliente, @RequestParam String fechaCarga, @RequestParam String fechaFlete, @RequestParam String origen, 
            @RequestParam String destino, @RequestParam Double km, @RequestParam Long idProducto, @RequestParam String cPorte, @RequestParam String ctg, 
            @RequestParam Double tarifa, @RequestParam Double kg, @RequestParam(required = false) String observacion, ModelMap modelo) throws ParseException {

        fleteServicio.modificarFleteChofer(id, idCamion, idAcoplado, fechaCarga, idCliente, origen, fechaFlete, destino, km, idProducto, tarifa, cPorte, ctg, kg, observacion);

        return "redirect:/flete/modificadoChofer/" + id;

    }
    
    @GetMapping("/modificadoChofer/{id}")
    public String modificadoChofer(@PathVariable Long id, ModelMap modelo) {

        modelo.put("flete", fleteServicio.buscarFlete(id));
        modelo.put("exito", "Viaje MODIFICADO con éxito");

        return "flete_mostrarChofer.html";      

    }

    @PostMapping("/modificaAdmin/{id}")
    public String modificaAdmin(@RequestParam Long id, @RequestParam Long idChofer, @RequestParam Long idCamion, @RequestParam(required = false) Long idAcoplado, 
            @RequestParam Long idCliente, @RequestParam String fechaCarga, @RequestParam String fechaFlete, @RequestParam String origen, 
            @RequestParam String destino, @RequestParam Double km, @RequestParam Long idProducto, @RequestParam String cPorte, @RequestParam String ctg, 
            @RequestParam Double tarifa, @RequestParam Double kg, @RequestParam Double comisionTpte, @RequestParam Double comisionTpteValor,
            @RequestParam String comisionTpteChofer, @RequestParam Double neto, @RequestParam Double iva, @RequestParam Double total, @RequestParam Double porcentaje, 
            @RequestParam Double gananciaChofer, @RequestParam(required = false) String observacion, ModelMap modelo, HttpSession session) throws ParseException {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");
        Flete flete = fleteServicio.buscarFlete(id);

        if (flete.getEstado().equalsIgnoreCase("PENDIENTE")) {

            fleteServicio.modificarFleteAdmin(id, idChofer, idCamion, idAcoplado, fechaCarga, idCliente, origen, fechaFlete, destino, km, idProducto, tarifa, cPorte, ctg,
            kg, neto, iva, total, porcentaje, gananciaChofer, comisionTpte, comisionTpteValor, comisionTpteChofer, observacion, logueado.getId());

            return "redirect:/flete/modificadoPendiente/" + id;

        } else {
            
            fleteServicio.modificarFleteAdmin(id, idChofer, idCamion, idAcoplado, fechaCarga, idCliente, origen, fechaFlete, destino, km, idProducto, tarifa, cPorte, ctg,
                    kg, neto, iva, total, porcentaje, gananciaChofer, comisionTpte, comisionTpteValor, comisionTpteChofer, observacion, logueado.getId());

            return "redirect:/flete/modificadoAdmin/" + id;

        }
    }
    
    @GetMapping("/modificadoPendiente/{id}")
    public String modificadoPendiente(@PathVariable Long id, ModelMap modelo) {

            modelo.put("flete", fleteServicio.buscarFlete(id));
            modelo.put("exito", "Viaje MODIFICADO con éxito");

            return "flete_mostrarPendiente.html";        

    }
    
    @GetMapping("/modificadoAdmin/{id}")
    public String modificadoAdmin(@PathVariable Long id, ModelMap modelo) {

            modelo.put("flete", fleteServicio.buscarFlete(id));
            modelo.put("exito", "Viaje MODIFICADO con éxito");

            return "flete_modificadoAdmin.html";        

    }
    
    @GetMapping("/modificarAdmin")
    public String modificarAdmin(@RequestParam Long id, @RequestParam String desde, @RequestParam String hasta, @RequestParam(required = false) Long idChofer,
           @RequestParam(required = false) Long idCamion, @RequestParam(required = false) Long idCliente, ModelMap modelo, HttpSession session) {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

            modelo.put("flete", fleteServicio.buscarFlete(id));
            modelo.addAttribute("clientes", clienteServicio.buscarClientesHabNombreAsc(logueado.getIdOrg()));
            modelo.addAttribute("choferes", choferServicio.bucarChoferesHabNombreAsc(logueado.getIdOrg()));
            modelo.addAttribute("camiones", camionServicio.buscarCamionesHabAsc(logueado.getIdOrg()));
            modelo.addAttribute("acoplados", acopladoServicio.buscarAcopladosHabAsc(logueado.getIdOrg()));
            modelo.addAttribute("productos", productoServicio.buscarProductosHabAsc(logueado.getIdOrg()));
            modelo.put("desde", desde);
        modelo.put("hasta", hasta);
        modelo.put("idChofer", idChofer);
        modelo.put("idCamion", idCamion);
        modelo.put("idCliente", idCliente);

            return "flete_modificar.html";
            
    }
    
    @PostMapping("/modifica")
    public String modifica(@RequestParam Long id, @RequestParam String desde, @RequestParam String hasta, @RequestParam(required = false) Long chofer,
           @RequestParam(required = false) Long camion, @RequestParam(required = false) Long cliente, @RequestParam Long idChofer, @RequestParam Long idCamion, 
           @RequestParam(required = false) Long idAcoplado, @RequestParam Long idCliente, @RequestParam String fechaCarga, @RequestParam String fechaFlete, 
           @RequestParam String origen, @RequestParam String destino, @RequestParam Double km, @RequestParam Long idProducto, @RequestParam String cPorte, 
           @RequestParam String ctg, @RequestParam Double tarifa, @RequestParam Double kg, @RequestParam Double comisionTpte, @RequestParam Double comisionTpteValor, 
           @RequestParam String comisionTpteChofer, @RequestParam Double neto, @RequestParam Double iva, @RequestParam Double total, @RequestParam Double porcentaje, 
           @RequestParam Double gananciaChofer, @RequestParam(required = false) String observacion, ModelMap modelo, HttpSession session) throws ParseException {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");
        
        fleteServicio.modificarFleteAdmin(id, idChofer, idCamion, idAcoplado, fechaCarga, idCliente, origen, fechaFlete, destino, km, idProducto, tarifa, cPorte, ctg,
                    kg, neto, iva, total, porcentaje, gananciaChofer, comisionTpte, comisionTpteValor, comisionTpteChofer, observacion, logueado.getId());

        return "redirect:/flete/modificado?id=" + id +"&desde=" + desde + "&hasta=" + hasta +
           (chofer != null ? "&chofer=" + chofer : "") +
           (camion != null ? "&camion=" + camion : "") +
           (cliente != null ? "&cliente=" + cliente : "");

    }
    
    @GetMapping("/modificado")
    public String modificado(@RequestParam Long id, @RequestParam String desde, @RequestParam String hasta, @RequestParam(required = false) Long chofer,
           @RequestParam(required = false) Long camion, @RequestParam(required = false) Long cliente, ModelMap modelo) {
        
        modelo.put("flete", fleteServicio.buscarFlete(id));
        modelo.put("desde", desde);
        modelo.put("hasta", hasta);
        modelo.put("idChofer", chofer);
        modelo.put("idCamion", camion);
        modelo.put("idCliente", cliente);
         modelo.put("exito", "Viaje MODIFICADO con éxito");

        return "flete_mostrarAdmin.html";   

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
    public String eliminado(ModelMap modelo, HttpSession session) throws ParseException {
        
        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

            String desde = obtenerFechaDesde();
            String hasta = obtenerFechaHasta();
            ArrayList<Flete> fletes = fleteServicio.buscarFletesIdChoferFecha(logueado.getId(), desde, hasta);
            Boolean flag = true;
            if (fletes.isEmpty()) {
                flag = false;
            }

            modelo.addAttribute("fletes", fletes);
            modelo.put("flag", flag);
            modelo.put("chofer", logueado);
            modelo.put("desde", desde);
            modelo.put("hasta", hasta);
            modelo.put("exito", "Viaje ELIMINADO con éxito");

            return "flete_listarChofer.html";     

    }
    
    @GetMapping("/eliminadoAdmin")
    public String eliminadoAdmin(ModelMap modelo, HttpSession session) {
        
        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

            modelo.put("id", logueado.getId());
            modelo.put("exito", "Viaje ELIMINADO con éxito");

            return "index_admin.html";    

    }
    
    
    @GetMapping("/eliminarAdmin")
    public String eliminarAdmin(@RequestParam Long id, @RequestParam String desde, @RequestParam String hasta, @RequestParam(required = false) Long idChofer,
           @RequestParam(required = false) Long idCamion, @RequestParam(required = false) Long idCliente, ModelMap modelo) {

            modelo.put("flete", fleteServicio.buscarFlete(id));
            modelo.put("desde", desde);
            modelo.put("hasta", hasta);
            modelo.put("idChofer", idChofer);
            modelo.put("idCamion", idCamion);
            modelo.put("idCliente", idCliente);
            
            return "flete_eliminar.html";

    }
    
    @GetMapping("/eliminaAdmin")
    public String eliminaAdmin(@RequestParam Long id, @RequestParam String desde, @RequestParam String hasta, @RequestParam(required = false) Long idChofer,
           @RequestParam(required = false) Long idCamion, @RequestParam(required = false) Long idCliente, HttpSession session, ModelMap modelo) {

        fleteServicio.eliminarFlete(id);

            return "redirect:/flete/listarTodoFiltradoGet?&desde=" + desde + "&hasta=" + hasta +
           (idChofer != null ? "&idChofer=" + idChofer : "") +
           (idCamion != null ? "&idCamion=" + idCamion : "") +
           (idCliente != null ? "&idCliente=" + idCliente : "") +
                    "&elimina=" + "si";
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
    public String exportarTodos(@RequestParam String desde, @RequestParam String hasta, @RequestParam(required = false) Long idChofer,
           @RequestParam(required = false) Long idCamion, @RequestParam(required = false) Long idCliente, ModelMap modelo, HttpSession session) throws ParseException {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");
        
         if (idChofer == null && idCliente == null && idCamion == null) {

            modelo.addAttribute("fletes", fleteServicio.buscarFletesRangoFechaAsc(logueado.getIdOrg(), desde, hasta));

        } else if (idChofer != null && idCliente == null && idCamion == null) {

            modelo.addAttribute("fletes", fleteServicio.buscarFletesIdChoferFechaAsc(idChofer, desde, hasta));

        } else if (idChofer == null && idCliente != null && idCamion == null) {

            modelo.addAttribute("fletes", fleteServicio.buscarFletesIdClienteFechaAsc(idCliente, desde, hasta));
            
        } else if (idChofer == null && idCliente == null && idCamion != null) {

            modelo.addAttribute("fletes", fleteServicio.buscarFletesIdCamionFechaAsc(idCamion, desde, hasta));
        
        }  else if (idChofer != null && idCliente != null && idCamion == null) {

            modelo.addAttribute("fletes", fleteServicio.buscarFletesIdChoferClienteFechaAsc(idChofer, idCliente, desde, hasta));
            
        } else if (idChofer != null && idCliente == null && idCamion != null) {

            modelo.addAttribute("fletes", fleteServicio.buscarFletesIdChoferCamionFechaAsc(idChofer, idCamion, desde, hasta));
            
        } else if (idChofer == null && idCliente != null && idCamion != null) {

            modelo.addAttribute("fletes", fleteServicio.buscarFletesIdClienteCamionFechaAsc(idCliente, idCamion, desde, hasta));
            
        } else {

            modelo.addAttribute("fletes", fleteServicio.buscarFletesIdChoferClienteCamionFechaAsc(idChofer, idCliente, idCamion, desde, hasta));
            
        }
        
        modelo.put("idOrg", logueado.getIdOrg());
        modelo.put("desde", desde);
        modelo.put("hasta", hasta);
        modelo.put("idChofer", idChofer);
        modelo.put("idCliente", idCliente);
        modelo.put("idCamion", idCamion);

        return "flete_exportarAdminTodos.html";

    }

    @PostMapping("/exportaAdminTodos")
    public void exportaTodos(@RequestParam(required = false) Long idOrg, @RequestParam String desde, @RequestParam String hasta, @RequestParam(required = false) Long idChofer,
           @RequestParam(required = false) Long idCamion, @RequestParam(required = false) Long idCliente, HttpServletResponse response) throws IOException, ParseException {

        ArrayList<Flete> myObjects = new ArrayList();
        
        if (idChofer == null && idCliente == null && idCamion == null) {

            myObjects = fleteServicio.buscarFletesRangoFechaAsc(idOrg, desde, hasta);

        } else if (idChofer != null && idCliente == null && idCamion == null) {

            myObjects = fleteServicio.buscarFletesIdChoferFechaAsc(idChofer, desde, hasta);

        } else if (idChofer == null && idCliente != null && idCamion == null) {

            myObjects = fleteServicio.buscarFletesIdClienteFechaAsc(idCliente, desde, hasta);
            
        } else if (idChofer == null && idCliente == null && idCamion != null) {

            myObjects = fleteServicio.buscarFletesIdCamionFechaAsc(idCamion, desde, hasta);
        
        }  else if (idChofer != null && idCliente != null && idCamion == null) {

            myObjects = fleteServicio.buscarFletesIdChoferClienteFechaAsc(idChofer, idCliente, desde, hasta);
            
        } else if (idChofer != null && idCliente == null && idCamion != null) {

            myObjects = fleteServicio.buscarFletesIdChoferCamionFechaAsc(idChofer, idCamion, desde, hasta);
            
        } else if (idChofer == null && idCliente != null && idCamion != null) {

            myObjects = fleteServicio.buscarFletesIdClienteCamionFechaAsc(idCliente, idCamion, desde, hasta);
            
        } else {

            myObjects = fleteServicio.buscarFletesIdChoferClienteCamionFechaAsc(idChofer, idCliente, idCamion, desde, hasta);
            
        }
        
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
                + "<th>Acoplado</th>"
                + "<th>KM</th>"
                + "<th>Producto</th>"
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
                    + "<td>").append(flete.getAcoplado() != null ? flete.getAcoplado().getDominio() : "-").append("</td>"
                    + "<td>").append(flete.getKmFlete()).append("</td>"
                    + "<td>").append(flete.getProducto().getNombre()).append("</td>"
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
                + "<th>Producto</th>"
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
                    + "<td>").append(flete.getProducto().getNombre()).append("</td>"
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
    
        @GetMapping("/listarTodoFiltradoGet")
    public String listarTodoFiltradoGet(@RequestParam String desde, @RequestParam String hasta, @RequestParam(required = false) Long idChofer,
           @RequestParam(required = false) Long idCamion, @RequestParam(required = false) Long idCliente, @RequestParam(required = false) String elimina,
           @RequestParam(required = false) String pendiente, ModelMap modelo, HttpSession session) throws ParseException {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        if (idChofer == null && idCliente == null && idCamion == null) {

            ArrayList<Flete> fletes = fleteServicio.buscarFletesRangoFecha(logueado.getIdOrg(), desde, hasta);
            Boolean flag = true;
            if (fletes.isEmpty()) {
                flag = false;
            }

            modelo.addAttribute("fletes", fletes);
            modelo.put("flag", flag);
            modelo.put("cantidad", fletes.size());
            modelo.put("chofer", null);
            modelo.put("camion", null);
            modelo.put("cliente", null);

        } else if (idChofer != null && idCliente == null && idCamion == null) {

            ArrayList<Flete> fletes = fleteServicio.buscarFletesIdChoferFecha(idChofer, desde, hasta);
            Boolean flag = true;
            if (fletes.isEmpty()) {
                flag = false;
            }

            modelo.addAttribute("fletes", fletes);
            modelo.put("flag", flag);
            modelo.put("chofer", choferServicio.buscarChofer(idChofer));
            modelo.put("camion", null);
            modelo.put("cliente", null);
            modelo.put("cantidad", fletes.size());

        } else if (idChofer == null && idCliente != null && idCamion == null) {
            
            ArrayList<Flete> fletes = fleteServicio.buscarFletesIdClienteFecha(idCliente, desde, hasta);
            Boolean flag = true;
            if (fletes.isEmpty()) {
                flag = false;
            }

            modelo.addAttribute("fletes", fletes);
            modelo.put("flag", flag);
            modelo.put("cliente", clienteServicio.buscarCliente(idCliente));
            modelo.put("camion", null);
            modelo.put("chofer", null);
            modelo.put("cantidad", fletes.size());
            
        } else if (idChofer == null && idCliente == null && idCamion != null) {
            
            ArrayList<Flete> fletes = fleteServicio.buscarFletesIdCamionFecha(idCamion, desde, hasta);
            Boolean flag = true;
            if (fletes.isEmpty()) {
                flag = false;
            }

            modelo.addAttribute("fletes", fletes);
            modelo.put("flag", flag);
            modelo.put("cliente", null);
            modelo.put("chofer", null);
            modelo.put("camion", camionServicio.buscarCamion(idCamion));
            modelo.put("cantidad", fletes.size());
        
        }  else if (idChofer != null && idCliente != null && idCamion == null) {
            
            ArrayList<Flete> fletes = fleteServicio.buscarFletesIdChoferClienteFecha(idChofer, idCliente, desde, hasta);
            Boolean flag = true;
            if (fletes.isEmpty()) {
                flag = false;
            }

            modelo.addAttribute("fletes", fletes);
            modelo.put("flag", flag);
            modelo.put("cliente", clienteServicio.buscarCliente(idCliente));
            modelo.put("chofer", choferServicio.buscarChofer(idChofer));
            modelo.put("camion", null);
            modelo.put("cantidad", fletes.size());
            
        } else if (idChofer != null && idCliente == null && idCamion != null) {
            
            ArrayList<Flete> fletes = fleteServicio.buscarFletesIdChoferCamionFecha(idChofer, idCamion, desde, hasta);
            Boolean flag = true;
            if (fletes.isEmpty()) {
                flag = false;
            }

            modelo.addAttribute("fletes", fletes);
            modelo.put("flag", flag);
            modelo.put("cliente", null);
            modelo.put("chofer", choferServicio.buscarChofer(idChofer));
            modelo.put("camion", camionServicio.buscarCamion(idCamion));
            modelo.put("cantidad", fletes.size());
            
        } else if (idChofer == null && idCliente != null && idCamion != null) {
            
            ArrayList<Flete> fletes = fleteServicio.buscarFletesIdClienteCamionFecha(idCliente, idCamion, desde, hasta);
            Boolean flag = true;
            if (fletes.isEmpty()) {
                flag = false;
            }

            modelo.addAttribute("fletes", fletes);
            modelo.put("flag", flag);
            modelo.put("cliente", clienteServicio.buscarCliente(idCliente));
            modelo.put("chofer", null);
            modelo.put("camion", camionServicio.buscarCamion(idCamion));
            modelo.put("cantidad", fletes.size());
            
        } else {
            
            ArrayList<Flete> fletes = fleteServicio.buscarFletesIdChoferClienteCamionFecha(idChofer, idCliente, idCamion, desde, hasta);
            Boolean flag = true;
            if (fletes.isEmpty()) {
                flag = false;
            }

            modelo.addAttribute("fletes", fletes);
            modelo.put("flag", flag);
            modelo.put("cliente", clienteServicio.buscarCliente(idCliente));
            modelo.put("chofer", choferServicio.buscarChofer(idChofer));
            modelo.put("camion", camionServicio.buscarCamion(idCamion));
            modelo.put("cantidad", fletes.size());
            
        }
        
        modelo.put("desde", desde);
        modelo.put("hasta", hasta);
        modelo.put("idChofer", idChofer);
        modelo.put("idCliente", idCliente);
        modelo.put("idCamion", idCamion);
        modelo.addAttribute("choferes", choferServicio.bucarChoferesNombreAsc(logueado.getIdOrg()));
        modelo.addAttribute("clientes", clienteServicio.buscarClientesNombreAsc(logueado.getIdOrg()));
        modelo.addAttribute("camiones", camionServicio.buscarCamionesAsc(logueado.getIdOrg()));
        if(elimina != null){
        modelo.put("exito", "Viaje ELIMINADO con éxito");
        }
        if(pendiente != null){
        modelo.put("exito", "Viaje RETORNADO A PENDIENTE con éxito");
        }
        
        return "flete_listarTodoFiltrado.html";
    }
    
        public byte[] optimizeImage(MultipartFile file) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Thumbnails.of(file.getInputStream())
                .size(1024, 768) // Ajusta el tamaño según tus necesidades
                .outputQuality(0.99) // Ajusta la calidad según tus necesidades
                .toOutputStream(outputStream);
        return outputStream.toByteArray();
    }

}
