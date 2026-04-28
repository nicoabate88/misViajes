package abate.abate.controladores;

import abate.abate.dto.MantenimientoDTO;
import abate.abate.entidades.Acoplado;
import abate.abate.entidades.Camion;
import abate.abate.entidades.Mantenimiento;
import abate.abate.entidades.TipoMantenimiento;
import abate.abate.entidades.Usuario;
import abate.abate.servicios.AcopladoServicio;
import abate.abate.servicios.CamionServicio;
import abate.abate.servicios.CombustibleServicio;
import abate.abate.servicios.ExcelServicio;
import abate.abate.servicios.MantenimientoServicio;
import abate.abate.servicios.TipoMantenimientoServicio;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/mantenimiento")
@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_CHOFER')")
public class MantenimientoControlador {

    @Autowired
    private MantenimientoServicio mantenimientoServicio;
    @Autowired
    private TipoMantenimientoServicio tipoMantenimientoServicio;
    @Autowired
    private CamionServicio camionServicio;
    @Autowired
    private AcopladoServicio acopladoServicio;
    @Autowired
    private CombustibleServicio combustibleServicio;
    @Autowired
    private ExcelServicio excelServicio;

    @GetMapping("/registrarChofer/{aplicaA}")
    public String registrarChofer(@PathVariable TipoMantenimiento.AplicaA aplicaA, ModelMap modelo, HttpSession session) {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        modelo.addAttribute("tipos", tipoMantenimientoServicio.buscarTiposAplicaA(logueado.getIdOrg(), aplicaA));
        modelo.put("aplica", aplicaA);
        modelo.put("idOrg", logueado.getIdOrg());

        if (aplicaA == TipoMantenimiento.AplicaA.CAMION) {

            if (logueado.getCamion() != null) {

                Camion camion = logueado.getCamion();
                int km = combustibleServicio.kmUltimaCarga(camion);
                modelo.addAttribute("camion", camion);
                modelo.put("km", km);
                modelo.put("kmProximo", km + 10000);
                modelo.put("kmAlarma", km + 9000);
                modelo.put("kmVigencia", 10000);
            } else {
                modelo.addAttribute("camion", null);
            }

            return "mantenimiento_registrarCamionChofer.html";

        } else {

            if (logueado.getAcoplado() != null) {

                Acoplado acoplado = logueado.getAcoplado();
                int km = combustibleServicio.kmAcoplado(acoplado, obtenerFechaFija());
                modelo.addAttribute("acoplado", acoplado);
                modelo.put("km", km);
                modelo.put("kmProximo", km + 10000);
                modelo.put("kmAlarma", km + 9000);
                modelo.put("kmVigencia", 10000);
            } else {
                modelo.addAttribute("acoplado", null);
            }

            return "mantenimiento_registrarAcopladoChofer.html";

        }

    }

    @GetMapping("/registrar/{aplicaA}")
    public String registrar(@PathVariable TipoMantenimiento.AplicaA aplicaA, ModelMap modelo, HttpSession session) {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        modelo.addAttribute("tipos", tipoMantenimientoServicio.buscarTiposAplicaA(logueado.getIdOrg(), aplicaA));
        modelo.addAttribute("aplicaA", TipoMantenimiento.AplicaA.values());
        modelo.put("aplica", aplicaA);
        modelo.put("tipo", null);
        modelo.put("idOrg", logueado.getIdOrg());

        if (aplicaA == TipoMantenimiento.AplicaA.CAMION) {
            if (logueado.getRol().equalsIgnoreCase("ADMIN")) {
                modelo.addAttribute("camiones", camionServicio.buscarCamionesHabAsc(logueado.getIdOrg()));
            } else {
                modelo.addAttribute("camiones", logueado.getCamion());
            }

            modelo.put("camion", null);

            return "mantenimiento_registrarCamion.html";

        } else {
            if (logueado.getRol().equalsIgnoreCase("ADMIN")) {
                modelo.addAttribute("acoplados", acopladoServicio.buscarAcopladosHabAsc(logueado.getIdOrg()));
            } else {
                modelo.addAttribute("acoplados", logueado.getAcoplado());
            }

            return "mantenimiento_registrarAcoplado.html";
        }

    }

    @GetMapping("/registrar1")
    public String registrar1(@RequestParam Long idOrg, @RequestParam TipoMantenimiento.AplicaA aplicaA, ModelMap modelo, HttpSession session) {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        modelo.put("aplica", aplicaA);
        modelo.addAttribute("tipos", tipoMantenimientoServicio.buscarTiposAplicaA(idOrg, aplicaA));
        modelo.addAttribute("aplicaA", TipoMantenimiento.AplicaA.values());
        modelo.put("tipo", null);
        modelo.put("idOrg", idOrg);

        if (aplicaA == TipoMantenimiento.AplicaA.CAMION) {
            if (logueado.getRol().equalsIgnoreCase("ADMIN")) {
                modelo.addAttribute("camiones", camionServicio.buscarCamionesHabAsc(idOrg));
            } else {
                modelo.addAttribute("camiones", logueado.getCamion());
            }
            modelo.put("camion", null);

            return "mantenimiento_registrarCamion.html";

        } else {
            if (logueado.getRol().equalsIgnoreCase("ADMIN")) {
                modelo.addAttribute("acoplados", acopladoServicio.buscarAcopladosHabAsc(idOrg));
            } else {
                modelo.addAttribute("acoplados", logueado.getAcoplado());
            }

            return "mantenimiento_registrarAcoplado.html";
        }
    }

    @GetMapping("/registrar2")
    public String registrar2(@RequestParam Long idOrg, @RequestParam TipoMantenimiento.AplicaA aplicaA, @RequestParam(required = false) Long idTipo,
            @RequestParam Long idEntidad, ModelMap modelo, HttpSession session) {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");
        modelo.addAttribute("tipos", tipoMantenimientoServicio.buscarTiposAplicaA(idOrg, aplicaA));
        if (idTipo != null) {
            modelo.put("tipo", tipoMantenimientoServicio.buscarTipo(idTipo));
        } else {
            modelo.put("tipo", null);
        }
        modelo.addAttribute("aplicaA", TipoMantenimiento.AplicaA.values());
        modelo.put("aplica", aplicaA);
        modelo.put("idOrg", idOrg);

        if (aplicaA == TipoMantenimiento.AplicaA.CAMION) {

            Camion camion = camionServicio.buscarCamion(idEntidad);
            int km = combustibleServicio.kmUltimaCarga(camion);

            if (logueado.getRol().equalsIgnoreCase("ADMIN")) {
                modelo.addAttribute("camiones", camionServicio.buscarCamionesHabAsc(idOrg));
            } else {
                modelo.addAttribute("camiones", logueado.getCamion());
            }
            modelo.put("camion", camion);
            modelo.put("km", km);
            modelo.put("kmProximo", km + 10000);
            modelo.put("kmAlarma", km + 9000);
            modelo.put("kmVigencia", 10000);

            return "mantenimiento_registrarCamion.html";

        } else {

            Acoplado acoplado = acopladoServicio.buscarAcoplado(idEntidad);
            int km = combustibleServicio.kmAcoplado(acoplado, obtenerFechaFija());

            if (logueado.getRol().equalsIgnoreCase("ADMIN")) {
                modelo.addAttribute("acoplados", acopladoServicio.buscarAcopladosHabAsc(idOrg));
            } else {
                modelo.addAttribute("acoplados", logueado.getAcoplado());
            }
            modelo.put("acoplado", acoplado);
            modelo.put("km", km);
            modelo.put("kmProximo", km + 10000);
            modelo.put("kmAlarma", km + 9000);
            modelo.put("kmVigencia", 10000);

            return "mantenimiento_registrarAcoplado.html";

        }

    }

    @PostMapping("/registro")
    public String registro(@RequestParam TipoMantenimiento.AplicaA aplicaA, @RequestParam Long idTipo, @RequestParam Long idEntidad,
            @RequestParam String observacion, @RequestParam String fecha, @RequestParam Integer km, @RequestParam Integer kmProximo,
            @RequestParam Integer kmAlarma, ModelMap modelo, HttpSession session) throws ParseException {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        if (km == null) {
            km = 0;
        }

        TipoMantenimiento tipo = tipoMantenimientoServicio.buscarTipo(idTipo);
        Date fechaMantenimiento = convertirFecha(fecha);
        String obsMayusculas = observacion.toUpperCase();
        Mantenimiento mantenimiento = new Mantenimiento();

        mantenimiento.setTipoMantenimiento(tipo);
        mantenimiento.setAplicaA(aplicaA);
        mantenimiento.setObservacion(obsMayusculas);
        mantenimiento.setFecha(fechaMantenimiento);
        mantenimiento.setKm(km);
        mantenimiento.setKmProximo(kmProximo);
        mantenimiento.setKmAlarma(kmAlarma);
        mantenimiento.setEstado(Mantenimiento.Estado.VIGENTE);
        mantenimiento.setUsuario(logueado);
        mantenimiento.setIdOrg(logueado.getIdOrg());
        if (aplicaA == TipoMantenimiento.AplicaA.CAMION) {
            Camion camion = camionServicio.buscarCamion(idEntidad);
            mantenimiento.setCamion(camion);
        } else {
            Acoplado acoplado = acopladoServicio.buscarAcoplado(idEntidad);
            mantenimiento.setAcoplado(acoplado);
        }

        Mantenimiento buscar = mantenimientoServicio.buscarExistente(mantenimiento);

        if (buscar != null) {

            modelo.put("mantenimientoExistente", buscar);
            modelo.put("mantenimiento", mantenimiento);
            modelo.put("fecha", fecha);

            if (logueado.getRol().equalsIgnoreCase("ADMIN")) {

                return "mantenimiento_registrarConfirmar.html";

            } else {

                return "mantenimiento_registrarConfirmarChofer.html";

            }

        } else {

            mantenimientoServicio.crearMantenimiento(mantenimiento, null);

            return "redirect:/mantenimiento/registrado";

        }

    }

    @PostMapping("/confirmarRegistro")
    public String confirmarRegistro(@RequestParam TipoMantenimiento.AplicaA aplicaA, @RequestParam Long idTipo, @RequestParam Long idEntidad,
            @RequestParam String observacion, @RequestParam String fecha, @RequestParam(required = false) Integer km, @RequestParam Integer kmProximo,
            @RequestParam Integer kmAlarma, @RequestParam Long idMantenimientoExistente, ModelMap modelo, HttpSession session) throws ParseException {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        if (km == null) {
            km = 0;
        }

        TipoMantenimiento tipo = tipoMantenimientoServicio.buscarTipo(idTipo);
        Date fechaMantenimiento = convertirFecha(fecha);
        String obsMayusculas = observacion.toUpperCase();
        Mantenimiento mantenimiento = new Mantenimiento();

        mantenimiento.setTipoMantenimiento(tipo);
        mantenimiento.setAplicaA(aplicaA);
        mantenimiento.setObservacion(obsMayusculas);
        mantenimiento.setFecha(fechaMantenimiento);
        mantenimiento.setKm(km);
        mantenimiento.setKmProximo(kmProximo);
        mantenimiento.setKmAlarma(kmAlarma);
        mantenimiento.setEstado(Mantenimiento.Estado.VIGENTE);
        mantenimiento.setUsuario(logueado);
        mantenimiento.setIdOrg(logueado.getIdOrg());
        if (aplicaA == TipoMantenimiento.AplicaA.CAMION) {
            Camion camion = camionServicio.buscarCamion(idEntidad);
            mantenimiento.setCamion(camion);
        } else {
            Acoplado acoplado = acopladoServicio.buscarAcoplado(idEntidad);
            mantenimiento.setAcoplado(acoplado);
        }

        Mantenimiento mantenimientoExistente = mantenimientoServicio.buscarMantenimiento(idMantenimientoExistente);

        mantenimientoServicio.crearMantenimiento(mantenimiento, mantenimientoExistente);

        return "redirect:/mantenimiento/registrado";

    }

    @GetMapping("/registrado")
    public String registrado(ModelMap modelo, HttpSession session) {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        Long id = mantenimientoServicio.buscarUltimo(logueado.getIdOrg());

        modelo.put("mantenimiento", mantenimientoServicio.buscarMantenimientoDiasVigencia(id));
        modelo.put("exito", "Mantenimiento REGISTRADO con éxito");

        if (logueado.getRol().equalsIgnoreCase("ADMIN")) {

            return "mantenimiento_mostrar.html";

        } else {

            return "mantenimiento_mostrarChofer.html";

        }
    }

    @GetMapping("/modificar/{id}")
    public String modificar(@PathVariable Long id, ModelMap modelo) {

        Mantenimiento mantenimiento = mantenimientoServicio.buscarMantenimientoDiasVigencia(id);

        modelo.put("mantenimiento", mantenimiento);

        if (mantenimiento.getAplicaA() != TipoMantenimiento.AplicaA.ACOPLADO) {
            
            modelo.put("kmActual", combustibleServicio.kmUltimaCarga(mantenimiento.getCamion()));

            return "mantenimiento_modificarCamion.html";

        } else {
            
            modelo.put("kmActual", combustibleServicio.kmAcoplado(mantenimiento.getAcoplado(), obtenerFechaFija()));

            return "mantenimiento_modificarAcoplado.html";

        }

    }

    @PostMapping("/modifica")
    public String modifica(@RequestParam Long id, @RequestParam String observacion, @RequestParam String fecha,
            @RequestParam Integer kmProximo, @RequestParam Integer kmAlarma, ModelMap modelo, HttpSession session) throws ParseException {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        mantenimientoServicio.modificarMantenimiento(id, fecha, kmProximo, kmAlarma, observacion, logueado);

        return "redirect:/mantenimiento/modificado/" + id;

    }

    @GetMapping("/modificado/{id}")
    public String modificado(@PathVariable Long id, ModelMap modelo, HttpSession session) {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        modelo.put("mantenimiento", mantenimientoServicio.buscarMantenimientoDiasVigencia(id));
        modelo.put("exito", "Mantenimiento MODIFICADO con éxito");

        if (logueado.getRol().equalsIgnoreCase("ADMIN")) {

            return "mantenimiento_mostrar.html";

        } else {

            return "mantenimiento_mostrarChofer.html";

        }

    }

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id, ModelMap modelo) {

        modelo.put("mantenimiento", mantenimientoServicio.buscarMantenimientoDiasVigencia(id));

        return "mantenimiento_eliminar.html";
    }

    @GetMapping("/elimina/{id}")
    public String elimina(@PathVariable Long id, ModelMap modelo) {

        mantenimientoServicio.eliminarMantenimiento(id);

        return "redirect:/mantenimiento/eliminado";
    }

    @GetMapping("/eliminado")
    public String eliminado(ModelMap modelo, HttpSession session) {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        modelo.put("exito", "Mantenimiento ELIMINADO con éxito");

        if (logueado.getRol().equalsIgnoreCase("ADMIN")) {

            return "mantenimiento_listarAdmin.html";

        } else {

            return "mantenimiento_listarChofer.html";

        }
    }

    @GetMapping("/listarChofer")
    public String listarChofer(ModelMap modelo) {

        return "mantenimiento_listarChofer.html";

    }

    @GetMapping("/listarChoferCamiones")
    public String camionChofer(ModelMap modelo, HttpSession session) {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        Boolean flag = false;

        if (logueado.getCamion() != null) {
            if (logueado.getMantenimiento().equalsIgnoreCase("SI")) {
                flag = true;
            }

            modelo.put("flag", flag);
            modelo.put("aplica", TipoMantenimiento.AplicaA.CAMION);
            modelo.put("camion", logueado.getCamion());
            modelo.addAttribute("camiones", camionServicio.buscarCamionesHabAsc(logueado.getIdOrg()));
            modelo.addAttribute("mantenimientos", mantenimientoServicio.buscarMantenimientoVigenteIdCamion(logueado.getCamion().getId()));

            return "mantenimiento_listarChoferCamion.html";

        } else {

            modelo.put("flag", flag);
            modelo.put("aplica", TipoMantenimiento.AplicaA.CAMION);
            modelo.addAttribute("camiones", camionServicio.buscarCamionesHabAsc(logueado.getIdOrg()));

            return "mantenimiento_listarChoferCamiones.html";

        }

    }

    @GetMapping("/listarChoferCamion")
    public String listarChoferCamion(@RequestParam Long idCamion, ModelMap modelo, HttpSession session) {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        Camion camion = camionServicio.buscarCamion(idCamion);

        Boolean flag = false;
        if (logueado.getMantenimiento().equalsIgnoreCase("SI") && logueado.getCamion() != null) {
            if (logueado.getCamion().getId() == camion.getId()) {
                flag = true;
            }
        }

        modelo.put("flag", flag);
        modelo.put("camion", camionServicio.buscarCamion(idCamion));
        modelo.put("aplica", TipoMantenimiento.AplicaA.CAMION);
        modelo.addAttribute("camiones", camionServicio.buscarCamionesHabAsc(logueado.getIdOrg()));
        modelo.addAttribute("mantenimientos", mantenimientoServicio.buscarMantenimientoVigenteIdCamion(idCamion));

        return "mantenimiento_listarChoferCamion.html";

    }

    @GetMapping("/listarHistorialChoferCamion/{id}")
    public String listarHistorialChoferCamion(@PathVariable Long id, ModelMap modelo) {

        List<Mantenimiento> mantenimientos = mantenimientoServicio.buscarHistorialCamion(id);

        Map<String, List<Mantenimiento>> mantenimientoPorTipo = mantenimientos.stream()
                .sorted(Comparator.comparing(Mantenimiento::getFecha).reversed())
                .collect(Collectors.groupingBy(doc -> doc.getTipoMantenimiento().getNombre()));

        modelo.put("mantenimientos", mantenimientoPorTipo);
        modelo.put("camion", camionServicio.buscarCamion(id));

        return "mantenimiento_listarChoferHistorialCamion.html";

    }

    @GetMapping("/listarChoferAcoplados")
    public String acopladosChofer(ModelMap modelo, HttpSession session) {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        Boolean flag = false;

        if (logueado.getCamion() != null && logueado.getCamion().getAcoplado() != null) {
            if (logueado.getMantenimiento().equalsIgnoreCase("SI")) {
                flag = true;
            }

            modelo.put("flag", flag);
            modelo.put("aplica", TipoMantenimiento.AplicaA.ACOPLADO);
            modelo.put("acoplado", logueado.getCamion().getAcoplado());
            modelo.addAttribute("acoplados", acopladoServicio.buscarAcopladosHabAsc(logueado.getIdOrg()));
            modelo.addAttribute("mantenimientos", mantenimientoServicio.buscarMantenimientoVigenteIdAcoplado(logueado.getCamion().getAcoplado().getId()));

            return "mantenimiento_listarChoferAcoplado.html";

        } else {

            modelo.put("flag", flag);
            modelo.put("aplica", TipoMantenimiento.AplicaA.ACOPLADO);
            modelo.addAttribute("acoplados", acopladoServicio.buscarAcopladosHabAsc(logueado.getIdOrg()));

            return "mantenimiento_listarChoferAcoplados.html";

        }

    }

    @GetMapping("/listarChoferAcoplado")
    public String listarChoferAcoplado(@RequestParam Long idAcoplado, ModelMap modelo, HttpSession session) {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        Acoplado acoplado = acopladoServicio.buscarAcoplado(idAcoplado);

        Boolean flag = false;
        if (logueado.getMantenimiento().equalsIgnoreCase("SI") && logueado.getAcoplado() != null) {
            if (logueado.getAcoplado().getId() == acoplado.getId()) {
                flag = true;
            }
        }

        modelo.put("flag", flag);
        modelo.put("aplica", TipoMantenimiento.AplicaA.ACOPLADO);
        modelo.put("acoplado", acopladoServicio.buscarAcoplado(idAcoplado));
        modelo.addAttribute("acoplados", acopladoServicio.buscarAcopladosHabAsc(logueado.getIdOrg()));
        modelo.addAttribute("mantenimientos", mantenimientoServicio.buscarMantenimientoVigenteIdAcoplado(idAcoplado));

        return "mantenimiento_listarChoferAcoplado.html";

    }

    @GetMapping("/listarHistorialChoferAcoplado/{id}")
    public String listarHistorialChoferSemi(@PathVariable Long id, ModelMap modelo) {

        List<Mantenimiento> mantenimientos = mantenimientoServicio.buscarHistorialAcoplado(id);

        Map<String, List<Mantenimiento>> mantenimientoPorTipo = mantenimientos.stream()
                .sorted(Comparator.comparing(Mantenimiento::getFecha).reversed())
                .collect(Collectors.groupingBy(doc -> doc.getTipoMantenimiento().getNombre()));

        modelo.put("mantenimientos", mantenimientoPorTipo);
        modelo.put("acoplado", acopladoServicio.buscarAcoplado(id));

        return "mantenimiento_listarChoferHistorialAcoplado.html";

    }

    @GetMapping("/listarAdmin")
    public String listarAdmin() {

        return "mantenimiento_listarAdmin.html";

    }

    @GetMapping("/listarAdminVencimiento")
    public String listarAdminVencimiento(ModelMap modelo, HttpSession session) {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");
        Boolean flag = false;
        int km = 1000;

        List<Mantenimiento> mantenimientos = mantenimientoServicio.obtenerMantenimientosPorVencer(logueado.getIdOrg(), km);

        Map<String, List<Mantenimiento>> mantenimientoPorTipo = mantenimientos.stream()
                .sorted(Comparator.comparing(Mantenimiento::getKmVigencia))
                .collect(Collectors.groupingBy(doc -> doc.getTipoMantenimiento().getNombre()));

        if (!mantenimientoPorTipo.isEmpty()) {
            flag = true;
        }

        modelo.put("mantenimientos", mantenimientoPorTipo);
        modelo.put("km", km);
        modelo.put("flag", flag);
        modelo.put("ninguno", 0L);
        modelo.addAttribute("camiones", camionServicio.buscarCamionesHabAsc(logueado.getIdOrg()));
        modelo.addAttribute("acoplados", acopladoServicio.buscarAcopladosHabAsc(logueado.getIdOrg()));

        return "mantenimiento_listarAdminVencimiento.html";

    }

    @PostMapping("/listarAdminVencimientoFiltro")
    public String listarAdminVencimientoFiltro(@RequestParam int km, @RequestParam(required = false) Long idCamion, @RequestParam(required = false) Long idAcoplado,
            ModelMap modelo, HttpSession session) {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");
        Boolean flag = false;
        List<Mantenimiento> mantenimientos = new ArrayList<>();
        
        if((idCamion != null && idCamion == 0L) || (idAcoplado != null && idAcoplado == 0L)){
            
            mantenimientos = mantenimientoServicio.obtenerMantenimientosPorVencerFiltro(logueado.getIdOrg(), km, idCamion, idAcoplado);
            
        } else if (idCamion == null && idAcoplado == null) {

            mantenimientos = mantenimientoServicio.obtenerMantenimientosPorVencer(logueado.getIdOrg(), km);

        } else if (idCamion != null || idAcoplado != null) {

            if (idCamion != null) {

                List<Mantenimiento> mantenimientosC = mantenimientoServicio.obtenerMantenimientosCamionPorVencer(idCamion, km);
                mantenimientos.addAll(mantenimientosC);
                modelo.put("camion", camionServicio.buscarCamion(idCamion));

            }

            if (idAcoplado != null) {

                List<Mantenimiento> mantenimientosA = mantenimientoServicio.obtenerMantenimientosAcopladoPorVencer(idAcoplado, km);
                mantenimientos.addAll(mantenimientosA);
                modelo.put("acoplado", acopladoServicio.buscarAcoplado(idAcoplado));

            }

        }

        Map<String, List<Mantenimiento>> mantenimientoPorTipo = mantenimientos.stream()
                .sorted(Comparator.comparing(Mantenimiento::getKmVigencia))
                .collect(Collectors.groupingBy(doc -> doc.getTipoMantenimiento().getNombre()));

        if (!mantenimientoPorTipo.isEmpty()) {
            flag = true;
        }

        modelo.put("idCamion", idCamion);
        modelo.put("idAcoplado", idAcoplado);
        modelo.put("mantenimientos", mantenimientoPorTipo);
        modelo.put("km", km);
        modelo.put("flag", flag);
        modelo.addAttribute("camiones", camionServicio.buscarCamionesHabAsc(logueado.getIdOrg()));
        modelo.addAttribute("acoplados", acopladoServicio.buscarAcopladosHabAsc(logueado.getIdOrg()));
        modelo.put("ninguno", 0L);

        return "mantenimiento_listarAdminVencimiento.html";

    }

    @GetMapping("/listarAdminCamiones")
    public String listarAdminCamiones(ModelMap modelo, HttpSession session) {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");
        Boolean flag = false;

        List<Mantenimiento> mantenimientos = new ArrayList();

        Map<String, List<Mantenimiento>> mantenimientoPorTipo = mantenimientos.stream()
                .sorted(Comparator.comparing(Mantenimiento::getKmVigencia))
                .collect(Collectors.groupingBy(doc -> doc.getTipoMantenimiento().getNombre()));

        modelo.put("mantenimientos", mantenimientoPorTipo);
        modelo.put("aplica", TipoMantenimiento.AplicaA.CAMION);
        modelo.addAttribute("camiones", camionServicio.buscarCamionesHabAsc(logueado.getIdOrg()));
        modelo.addAttribute("tipos", tipoMantenimientoServicio.buscarTiposAplicaA(logueado.getIdOrg(), TipoMantenimiento.AplicaA.CAMION));
        modelo.put("flag", flag);
        modelo.put("idCamion", null);
        modelo.put("camion", null);
        modelo.put("idTipo", null);
        modelo.put("tipo", null);
        modelo.put("seleccioneC", "seleccione");
        modelo.put("seleccioneT", "seleccione");
        modelo.put("clase", "PREVENTIVO");

        return "mantenimiento_listarAdminCamiones.html";

    }

    @PostMapping("/listarAdminCamionFiltro")
    public String listarAdminCamionFiltro(@RequestParam(required = false) Long idCamion, @RequestParam(required = false) Long idTipo,
            @RequestParam String clase, ModelMap modelo, HttpSession session) {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");
        Boolean flag = false;
        List<Mantenimiento> mantenimientos = new ArrayList();

        if (idCamion != null && idTipo == null && clase.equalsIgnoreCase("PREVENTIVO")) {

            mantenimientos = mantenimientoServicio.buscarMantenimientoVigenteIdCamionPreventivo(idCamion);

            Map<String, List<Mantenimiento>> mantenimientoPorTipo = mantenimientos.stream()
                    .sorted(Comparator.comparing(Mantenimiento::getKmVigencia))
                    .collect(Collectors.groupingBy(doc -> doc.getTipoMantenimiento().getNombre()));
            if (!mantenimientos.isEmpty()) {
                flag = true;
            }

            modelo.put("mantenimientos", mantenimientoPorTipo);
            modelo.put("camion", camionServicio.buscarCamion(idCamion));
            modelo.put("idCamion", idCamion);
            modelo.put("idTipo", null);
            modelo.put("tipo", null);

        } else if (idCamion != null && idTipo == null && clase.equalsIgnoreCase("TODOS")) {

            mantenimientos = mantenimientoServicio.buscarMantenimientoVigenteIdCamion(idCamion);

            Map<String, List<Mantenimiento>> mantenimientoPorTipo = mantenimientos.stream()
                    .sorted(Comparator.comparing(Mantenimiento::getKmVigencia))
                    .collect(Collectors.groupingBy(doc -> doc.getTipoMantenimiento().getNombre()));
            if (!mantenimientos.isEmpty()) {
                flag = true;
            }

            modelo.put("mantenimientos", mantenimientoPorTipo);
            modelo.put("camion", camionServicio.buscarCamion(idCamion));
            modelo.put("idCamion", idCamion);
            modelo.put("idTipo", null);
            modelo.put("tipo", null);

        } else if (idCamion != null && idTipo == null && clase.equalsIgnoreCase("CORRECTIVO")) {

            mantenimientos = mantenimientoServicio.buscarMantenimientoVigenteIdCamionCorrectivo(idCamion);

            Map<String, List<Mantenimiento>> mantenimientoPorTipo = mantenimientos.stream()
                    .sorted(Comparator.comparing(Mantenimiento::getKmVigencia))
                    .collect(Collectors.groupingBy(doc -> doc.getTipoMantenimiento().getNombre()));
            if (!mantenimientos.isEmpty()) {
                flag = true;
            }

            modelo.put("mantenimientos", mantenimientoPorTipo);
            modelo.put("camion", camionServicio.buscarCamion(idCamion));
            modelo.put("idCamion", idCamion);
            modelo.put("idTipo", null);
            modelo.put("tipo", null);

        } else if (idCamion == null && idTipo != null && clase.equalsIgnoreCase("PREVENTIVO")) {

            mantenimientos = mantenimientoServicio.buscarMantenimientosVigenteCamionesPorTipoPreventivo(logueado.getIdOrg(), idTipo);

            Map<String, List<Mantenimiento>> mantenimientoPorTipo = mantenimientos.stream()
                    .sorted(Comparator.comparing(Mantenimiento::getKmVigencia))
                    .collect(Collectors.groupingBy(doc -> doc.getTipoMantenimiento().getNombre()));
            if (!mantenimientos.isEmpty()) {
                flag = true;
            }

            modelo.put("mantenimientos", mantenimientoPorTipo);
            modelo.put("camion", null);
            modelo.put("tipo", tipoMantenimientoServicio.buscarTipo(idTipo));
            modelo.put("idCamion", null);
            modelo.put("idTipo", idTipo);

        } else if (idCamion == null && idTipo != null && clase.equalsIgnoreCase("CORRECTIVO")) {

            mantenimientos = mantenimientoServicio.buscarMantenimientosVigenteCamionesPorTipoCorrectivo(logueado.getIdOrg(), idTipo);

            Map<String, List<Mantenimiento>> mantenimientoPorTipo = mantenimientos.stream()
                    .sorted(Comparator.comparing(Mantenimiento::getKmVigencia))
                    .collect(Collectors.groupingBy(doc -> doc.getTipoMantenimiento().getNombre()));
            if (!mantenimientos.isEmpty()) {
                flag = true;
            }

            modelo.put("mantenimientos", mantenimientoPorTipo);
            modelo.put("camion", null);
            modelo.put("tipo", tipoMantenimientoServicio.buscarTipo(idTipo));
            modelo.put("idCamion", null);
            modelo.put("idTipo", idTipo);

        } else if (idCamion == null && idTipo != null && clase.equalsIgnoreCase("TODOS")) {

            mantenimientos = mantenimientoServicio.buscarMantenimientosVigenteCamionesPorTipo(logueado.getIdOrg(), idTipo);

            Map<String, List<Mantenimiento>> mantenimientoPorTipo = mantenimientos.stream()
                    .sorted(Comparator.comparing(Mantenimiento::getKmVigencia))
                    .collect(Collectors.groupingBy(doc -> doc.getTipoMantenimiento().getNombre()));
            if (!mantenimientos.isEmpty()) {
                flag = true;
            }

            modelo.put("mantenimientos", mantenimientoPorTipo);
            modelo.put("camion", null);
            modelo.put("tipo", tipoMantenimientoServicio.buscarTipo(idTipo));
            modelo.put("idCamion", null);
            modelo.put("idTipo", idTipo);

        } else if (idCamion != null && idTipo != null && clase.equalsIgnoreCase("PREVENTIVO")) {

            mantenimientos = mantenimientoServicio.buscarMantenimientosVigenteCamionPorTipoPreventivo(idCamion, idTipo);

            Map<String, List<Mantenimiento>> mantenimientoPorTipo = mantenimientos.stream()
                    .sorted(Comparator.comparing(Mantenimiento::getKmVigencia))
                    .collect(Collectors.groupingBy(doc -> doc.getTipoMantenimiento().getNombre()));
            if (!mantenimientos.isEmpty()) {
                flag = true;
            }

            modelo.put("mantenimientos", mantenimientoPorTipo);
            modelo.put("camion", camionServicio.buscarCamion(idCamion));
            modelo.put("tipo", tipoMantenimientoServicio.buscarTipo(idTipo));
            modelo.put("idCamion", idCamion);
            modelo.put("idTipo", idTipo);

        } else if (idCamion != null && idTipo != null && clase.equalsIgnoreCase("CORRECTIVO")) {

            mantenimientos = mantenimientoServicio.buscarMantenimientosVigenteCamionPorTipoCorrectivo(idCamion, idTipo);

            Map<String, List<Mantenimiento>> mantenimientoPorTipo = mantenimientos.stream()
                    .sorted(Comparator.comparing(Mantenimiento::getKmVigencia))
                    .collect(Collectors.groupingBy(doc -> doc.getTipoMantenimiento().getNombre()));
            if (!mantenimientos.isEmpty()) {
                flag = true;
            }

            modelo.put("mantenimientos", mantenimientoPorTipo);
            modelo.put("camion", camionServicio.buscarCamion(idCamion));
            modelo.put("tipo", tipoMantenimientoServicio.buscarTipo(idTipo));
            modelo.put("idCamion", idCamion);
            modelo.put("idTipo", idTipo);

        } else if (idCamion != null && idTipo != null && clase.equalsIgnoreCase("TODOS")) {

            mantenimientos = mantenimientoServicio.buscarMantenimientosVigenteCamionPorTipo(idCamion, idTipo);

            Map<String, List<Mantenimiento>> mantenimientoPorTipo = mantenimientos.stream()
                    .sorted(Comparator.comparing(Mantenimiento::getKmVigencia))
                    .collect(Collectors.groupingBy(doc -> doc.getTipoMantenimiento().getNombre()));
            if (!mantenimientos.isEmpty()) {
                flag = true;
            }

            modelo.put("mantenimientos", mantenimientoPorTipo);
            modelo.put("camion", camionServicio.buscarCamion(idCamion));
            modelo.put("tipo", tipoMantenimientoServicio.buscarTipo(idTipo));
            modelo.put("idCamion", idCamion);
            modelo.put("idTipo", idTipo);

        } else if (idCamion == null && idTipo == null && clase.equalsIgnoreCase("TODOS")) {

            mantenimientos = mantenimientoServicio.buscarMantenimientosVigenteCamiones(logueado.getIdOrg());
            Map<String, List<Mantenimiento>> mantenimientoPorTipo = mantenimientos.stream()
                    .sorted(Comparator.comparing(Mantenimiento::getKmVigencia))
                    .collect(Collectors.groupingBy(doc -> doc.getTipoMantenimiento().getNombre()));

            if (!mantenimientos.isEmpty()) {
                flag = true;
            }

            modelo.put("mantenimientos", mantenimientoPorTipo);
            modelo.put("camion", null);
            modelo.put("idCamion", null);
            modelo.put("tipo", null);
            modelo.put("idTipo", null);
            modelo.put("seleccioneC", null);
            modelo.put("seleccioneT", null);

        } else if (idCamion == null && idTipo == null && clase.equalsIgnoreCase("PREVENTIVO")) {

            mantenimientos = mantenimientoServicio.buscarMantenimientosVigenteCamionesPreventivo(logueado.getIdOrg());
            Map<String, List<Mantenimiento>> mantenimientoPorTipo = mantenimientos.stream()
                    .sorted(Comparator.comparing(Mantenimiento::getKmVigencia))
                    .collect(Collectors.groupingBy(doc -> doc.getTipoMantenimiento().getNombre()));

            if (!mantenimientos.isEmpty()) {
                flag = true;
            }

            modelo.put("mantenimientos", mantenimientoPorTipo);
            modelo.put("camion", null);
            modelo.put("idCamion", null);
            modelo.put("tipo", null);
            modelo.put("idTipo", null);
            modelo.put("seleccioneC", null);
            modelo.put("seleccioneT", null);

        } else if (idCamion == null && idTipo == null && clase.equalsIgnoreCase("CORRECTIVO")) {

            mantenimientos = mantenimientoServicio.buscarMantenimientosVigenteCamionesCorrectivo(logueado.getIdOrg());
            Map<String, List<Mantenimiento>> mantenimientoPorTipo = mantenimientos.stream()
                    .sorted(Comparator.comparing(Mantenimiento::getKmVigencia))
                    .collect(Collectors.groupingBy(doc -> doc.getTipoMantenimiento().getNombre()));

            if (!mantenimientos.isEmpty()) {
                flag = true;
            }

            modelo.put("mantenimientos", mantenimientoPorTipo);
            modelo.put("camion", null);
            modelo.put("idCamion", null);
            modelo.put("tipo", null);
            modelo.put("idTipo", null);
            modelo.put("seleccioneC", null);
            modelo.put("seleccioneT", null);

        }

        modelo.put("aplica", TipoMantenimiento.AplicaA.CAMION);
        modelo.addAttribute("camiones", camionServicio.buscarCamionesHabAsc(logueado.getIdOrg()));
        modelo.addAttribute("tipos", tipoMantenimientoServicio.buscarTiposAplicaA(logueado.getIdOrg(), TipoMantenimiento.AplicaA.CAMION));
        modelo.put("flag", flag);
        modelo.put("clase", clase);

        return "mantenimiento_listarAdminCamiones.html";

    }

    @GetMapping("/listarAdminCamion")
    public String listarAdminCamion(@RequestParam Long id, @RequestParam(required = false) Long idCamion, @RequestParam(required = false) Long idTipo,
            @RequestParam String clase, ModelMap modelo) {

        modelo.put("mantenimiento", mantenimientoServicio.buscarMantenimientoDiasVigencia(id));
        modelo.put("idCamion", idCamion);
        modelo.put("idTipo", idTipo);
        modelo.put("clase", clase);

        return "mantenimiento_listarAdminCamion.html";

    }

    @GetMapping("/listarAdminAcoplados")
    public String listarAdminSemis(ModelMap modelo, HttpSession session) {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        Boolean flag = false;

        List<Mantenimiento> mantenimientos = new ArrayList();

        Map<String, List<Mantenimiento>> mantenimientoPorTipo = mantenimientos.stream()
                .sorted(Comparator.comparing(Mantenimiento::getKmVigencia))
                .collect(Collectors.groupingBy(doc -> doc.getTipoMantenimiento().getNombre()));

        modelo.put("mantenimientos", mantenimientoPorTipo);
        modelo.put("aplica", TipoMantenimiento.AplicaA.ACOPLADO);
        modelo.addAttribute("acoplados", acopladoServicio.buscarAcopladosHabAsc(logueado.getIdOrg()));
        modelo.addAttribute("tipos", tipoMantenimientoServicio.buscarTiposAplicaA(logueado.getIdOrg(), TipoMantenimiento.AplicaA.ACOPLADO));
        modelo.put("flag", flag);
        modelo.put("idAcoplado", null);
        modelo.put("acoplado", null);
        modelo.put("idTipo", null);
        modelo.put("tipo", null);
        modelo.put("seleccioneA", "seleccione");
        modelo.put("seleccioneT", "seleccione");
        modelo.put("clase", "PREVENTIVO");

        return "mantenimiento_listarAdminAcoplados.html";

    }

    @PostMapping("/listarAdminAcopladoFiltro")
    public String listarAdminAcopladoFiltro(@RequestParam(required = false) Long idAcoplado, @RequestParam(required = false) Long idTipo,
            @RequestParam String clase, ModelMap modelo, HttpSession session) {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");
        Boolean flag = false;
        List<Mantenimiento> mantenimientos = new ArrayList();

        if (idAcoplado != null && idTipo == null && clase.equalsIgnoreCase("PREVENTIVO")) {

            mantenimientos = mantenimientoServicio.buscarMantenimientoVigenteIdAcopladoPreventivo(idAcoplado);

            Map<String, List<Mantenimiento>> mantenimientoPorTipo = mantenimientos.stream()
                    .sorted(Comparator.comparing(Mantenimiento::getKmVigencia))
                    .collect(Collectors.groupingBy(doc -> doc.getTipoMantenimiento().getNombre()));
            if (!mantenimientos.isEmpty()) {
                flag = true;
            }

            modelo.put("mantenimientos", mantenimientoPorTipo);
            modelo.put("acoplado", acopladoServicio.buscarAcoplado(idAcoplado));
            modelo.put("idAcoplado", idAcoplado);
            modelo.put("idTipo", null);
            modelo.put("tipo", null);

        } else if (idAcoplado != null && idTipo == null && clase.equalsIgnoreCase("CORRECTIVO")) {

            mantenimientos = mantenimientoServicio.buscarMantenimientoVigenteIdAcopladoCorrectivo(idAcoplado);

            Map<String, List<Mantenimiento>> mantenimientoPorTipo = mantenimientos.stream()
                    .sorted(Comparator.comparing(Mantenimiento::getKmVigencia))
                    .collect(Collectors.groupingBy(doc -> doc.getTipoMantenimiento().getNombre()));
            if (!mantenimientos.isEmpty()) {
                flag = true;
            }

            modelo.put("mantenimientos", mantenimientoPorTipo);

            modelo.put("acoplado", acopladoServicio.buscarAcoplado(idAcoplado));
            modelo.put("idAcoplado", idAcoplado);
            modelo.put("idTipo", null);
            modelo.put("tipo", null);

        } else if (idAcoplado != null && idTipo == null && clase.equalsIgnoreCase("TODOS")) {

            mantenimientos = mantenimientoServicio.buscarMantenimientoVigenteIdAcoplado(idAcoplado);

            Map<String, List<Mantenimiento>> mantenimientoPorTipo = mantenimientos.stream()
                    .sorted(Comparator.comparing(Mantenimiento::getKmVigencia))
                    .collect(Collectors.groupingBy(doc -> doc.getTipoMantenimiento().getNombre()));
            if (!mantenimientos.isEmpty()) {
                flag = true;
            }

            modelo.put("mantenimientos", mantenimientoPorTipo);

            modelo.put("acoplado", acopladoServicio.buscarAcoplado(idAcoplado));
            modelo.put("idAcoplado", idAcoplado);
            modelo.put("idTipo", null);
            modelo.put("tipo", null);

        } else if (idAcoplado == null && idTipo != null && clase.equalsIgnoreCase("PREVENTIVO")) {

            mantenimientos = mantenimientoServicio.buscarMantenimientosVigenteAcopladosPorTipoPreventivo(logueado.getIdOrg(), idTipo);

            Map<String, List<Mantenimiento>> mantenimientoPorTipo = mantenimientos.stream()
                    .sorted(Comparator.comparing(Mantenimiento::getKmVigencia))
                    .collect(Collectors.groupingBy(doc -> doc.getTipoMantenimiento().getNombre()));
            if (!mantenimientos.isEmpty()) {
                flag = true;
            }

            modelo.put("mantenimientos", mantenimientoPorTipo);

            modelo.put("acoplado", null);
            modelo.put("tipo", tipoMantenimientoServicio.buscarTipo(idTipo));
            modelo.put("idAcoplado", null);
            modelo.put("idTipo", idTipo);

        } else if (idAcoplado == null && idTipo != null && clase.equalsIgnoreCase("CORRECTIVO")) {

            mantenimientos = mantenimientoServicio.buscarMantenimientosVigenteAcopladosPorTipoCorrectivo(logueado.getIdOrg(), idTipo);

            Map<String, List<Mantenimiento>> mantenimientoPorTipo = mantenimientos.stream()
                    .sorted(Comparator.comparing(Mantenimiento::getKmVigencia))
                    .collect(Collectors.groupingBy(doc -> doc.getTipoMantenimiento().getNombre()));
            if (!mantenimientos.isEmpty()) {
                flag = true;
            }

            modelo.put("mantenimientos", mantenimientoPorTipo);

            modelo.put("acoplado", null);
            modelo.put("tipo", tipoMantenimientoServicio.buscarTipo(idTipo));
            modelo.put("idAcoplado", null);
            modelo.put("idTipo", idTipo);

        } else if (idAcoplado == null && idTipo != null && clase.equalsIgnoreCase("TODOS")) {

            mantenimientos = mantenimientoServicio.buscarMantenimientosVigenteAcopladosPorTipo(logueado.getIdOrg(), idTipo);

            Map<String, List<Mantenimiento>> mantenimientoPorTipo = mantenimientos.stream()
                    .sorted(Comparator.comparing(Mantenimiento::getKmVigencia))
                    .collect(Collectors.groupingBy(doc -> doc.getTipoMantenimiento().getNombre()));
            if (!mantenimientos.isEmpty()) {
                flag = true;
            }

            modelo.put("mantenimientos", mantenimientoPorTipo);

            modelo.put("acoplado", null);
            modelo.put("tipo", tipoMantenimientoServicio.buscarTipo(idTipo));
            modelo.put("idAcoplado", null);
            modelo.put("idTipo", idTipo);

        } else if (idAcoplado != null && idTipo != null && clase.equalsIgnoreCase("PREVENTIVO")) {

            mantenimientos = mantenimientoServicio.buscarMantenimientosVigenteAcopladoPorTipoPreventivo(idAcoplado, idTipo);

            Map<String, List<Mantenimiento>> mantenimientoPorTipo = mantenimientos.stream()
                    .sorted(Comparator.comparing(Mantenimiento::getKmVigencia))
                    .collect(Collectors.groupingBy(doc -> doc.getTipoMantenimiento().getNombre()));
            if (!mantenimientos.isEmpty()) {
                flag = true;
            }

            modelo.put("mantenimientos", mantenimientoPorTipo);

            modelo.put("acoplado", acopladoServicio.buscarAcoplado(idAcoplado));
            modelo.put("tipo", tipoMantenimientoServicio.buscarTipo(idTipo));
            modelo.put("idAcoplado", idAcoplado);
            modelo.put("idTipo", idTipo);

        } else if (idAcoplado != null && idTipo != null && clase.equalsIgnoreCase("CORRECTIVO")) {

            mantenimientos = mantenimientoServicio.buscarMantenimientosVigenteAcopladoPorTipoCorrectivo(idAcoplado, idTipo);

            Map<String, List<Mantenimiento>> mantenimientoPorTipo = mantenimientos.stream()
                    .sorted(Comparator.comparing(Mantenimiento::getKmVigencia))
                    .collect(Collectors.groupingBy(doc -> doc.getTipoMantenimiento().getNombre()));
            if (!mantenimientos.isEmpty()) {
                flag = true;
            }

            modelo.put("mantenimientos", mantenimientoPorTipo);

            modelo.put("acoplado", acopladoServicio.buscarAcoplado(idAcoplado));
            modelo.put("tipo", tipoMantenimientoServicio.buscarTipo(idTipo));
            modelo.put("idAcoplado", idAcoplado);
            modelo.put("idTipo", idTipo);

        } else if (idAcoplado != null && idTipo != null && clase.equalsIgnoreCase("TODOS")) {

            mantenimientos = mantenimientoServicio.buscarMantenimientosVigenteAcopladoPorTipo(idAcoplado, idTipo);

            Map<String, List<Mantenimiento>> mantenimientoPorTipo = mantenimientos.stream()
                    .sorted(Comparator.comparing(Mantenimiento::getKmVigencia))
                    .collect(Collectors.groupingBy(doc -> doc.getTipoMantenimiento().getNombre()));
            if (!mantenimientos.isEmpty()) {
                flag = true;
            }

            modelo.put("mantenimientos", mantenimientoPorTipo);

            modelo.put("acoplado", acopladoServicio.buscarAcoplado(idAcoplado));
            modelo.put("tipo", tipoMantenimientoServicio.buscarTipo(idTipo));
            modelo.put("idAcoplado", idAcoplado);
            modelo.put("idTipo", idTipo);

        } else if (idAcoplado == null && idTipo == null && clase.equalsIgnoreCase("TODOS")) {

            mantenimientos = mantenimientoServicio.buscarMantenimientosVigenteAcoplados(logueado.getIdOrg());

            Map<String, List<Mantenimiento>> mantenimientoPorTipo = mantenimientos.stream()
                    .sorted(Comparator.comparing(Mantenimiento::getKmVigencia))
                    .collect(Collectors.groupingBy(doc -> doc.getTipoMantenimiento().getNombre()));

            if (!mantenimientos.isEmpty()) {
                flag = true;
            }

            modelo.put("mantenimientos", mantenimientoPorTipo);
            modelo.put("acoplado", null);
            modelo.put("idAcoplado", null);
            modelo.put("tipo", null);
            modelo.put("idTipo", null);
            modelo.put("seleccioneA", null);
            modelo.put("seleccioneT", null);

        } else if (idAcoplado == null && idTipo == null && clase.equalsIgnoreCase("PREVENTIVO")) {

            mantenimientos = mantenimientoServicio.buscarMantenimientosVigenteAcopladosPreventivo(logueado.getIdOrg());

            Map<String, List<Mantenimiento>> mantenimientoPorTipo = mantenimientos.stream()
                    .sorted(Comparator.comparing(Mantenimiento::getKmVigencia))
                    .collect(Collectors.groupingBy(doc -> doc.getTipoMantenimiento().getNombre()));

            if (!mantenimientos.isEmpty()) {
                flag = true;
            }

            modelo.put("mantenimientos", mantenimientoPorTipo);
            modelo.put("acoplado", null);
            modelo.put("idAcoplado", null);
            modelo.put("tipo", null);
            modelo.put("idTipo", null);
            modelo.put("seleccioneA", null);
            modelo.put("seleccioneT", null);

        } else if (idAcoplado == null && idTipo == null && clase.equalsIgnoreCase("CORRECTIVO")) {

            mantenimientos = mantenimientoServicio.buscarMantenimientosVigenteAcopladosCorrectivo(logueado.getIdOrg());

            Map<String, List<Mantenimiento>> mantenimientoPorTipo = mantenimientos.stream()
                    .sorted(Comparator.comparing(Mantenimiento::getKmVigencia))
                    .collect(Collectors.groupingBy(doc -> doc.getTipoMantenimiento().getNombre()));

            if (!mantenimientos.isEmpty()) {
                flag = true;
            }

            modelo.put("mantenimientos", mantenimientoPorTipo);
            modelo.put("acoplado", null);
            modelo.put("idAcoplado", null);
            modelo.put("tipo", null);
            modelo.put("idTipo", null);
            modelo.put("seleccioneA", null);
            modelo.put("seleccioneT", null);

        }

        modelo.put("aplica", TipoMantenimiento.AplicaA.ACOPLADO);
        modelo.addAttribute("acoplados", acopladoServicio.buscarAcopladosHabAsc(logueado.getIdOrg()));
        modelo.addAttribute("tipos", tipoMantenimientoServicio.buscarTiposAplicaA(logueado.getIdOrg(), TipoMantenimiento.AplicaA.ACOPLADO));
        modelo.put("flag", flag);
        modelo.put("clase", clase);

        return "mantenimiento_listarAdminAcoplados.html";

    }

    @GetMapping("/listarAdminAcoplado")
    public String listarAdminAcoplado(@RequestParam Long id, @RequestParam(required = false) Long idAcoplado, @RequestParam(required = false) Long idTipo,
            @RequestParam String clase, ModelMap modelo) {

        modelo.addAttribute("mantenimiento", mantenimientoServicio.buscarMantenimientoDiasVigencia(id));
        modelo.put("idAcoplado", idAcoplado);
        modelo.put("idTipo", idTipo);
        modelo.put("clase", clase);

        return "mantenimiento_listarAdminAcoplado.html";

    }

    @GetMapping("/listarHistorialCamionAdmin")
    public String listarHistorialCamionAdmin(@RequestParam Long id, @RequestParam(required = false) Long idTipo,
            @RequestParam String clase, ModelMap modelo, HttpSession session) {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");
        Boolean flag = false;

        List<Mantenimiento> mantenimientos = mantenimientoServicio.buscarHistorialCamion(id);

        Map<String, List<Mantenimiento>> mantenimientoPorTipo = mantenimientos.stream()
                .sorted(Comparator.comparing(Mantenimiento::getFecha).reversed())
                .collect(Collectors.groupingBy(doc -> doc.getTipoMantenimiento().getNombre()));

        if (!mantenimientos.isEmpty()) {
            flag = true;
        }

        modelo.addAttribute("tipos", tipoMantenimientoServicio.buscarTiposAplicaA(logueado.getIdOrg(), TipoMantenimiento.AplicaA.CAMION));
        modelo.put("mantenimientos", mantenimientoPorTipo);
        modelo.put("camion", camionServicio.buscarCamion(id));
        modelo.put("idCamion", id);
        modelo.put("idTipo", idTipo);
        modelo.put("clase", clase);
        modelo.put("flag", flag);

        return "mantenimiento_listarHistorialCamionAdmin.html";

    }
    
    @PostMapping("/listarHistorialCamionAdminFiltro")
    public String listarHistorialCamionAdminFiltro(@RequestParam Long idCamion, @RequestParam(required = false) Long tipo, @RequestParam String clase, 
            @RequestParam(required = false) Long idTipo, ModelMap modelo, HttpSession session) {
        
        Usuario logueado = (Usuario) session.getAttribute("usuariosession");
        Boolean flag = false;
        List<Mantenimiento> mantenimientos = new ArrayList();
        
        if(tipo == null){
            
            mantenimientos = mantenimientoServicio.buscarHistorialCamion(idCamion);
            
        } else {
            
            mantenimientos = mantenimientoServicio.buscarHistorialCamionTipoMantenimiento(idCamion, tipo);
            modelo.put("tipo", tipoMantenimientoServicio.buscarTipo(tipo));
            modelo.put("tipoId", tipo);
            
        }

        Map<String, List<Mantenimiento>> mantenimientoPorTipo = mantenimientos.stream()
                .sorted(Comparator.comparing(Mantenimiento::getFecha).reversed())
                .collect(Collectors.groupingBy(doc -> doc.getTipoMantenimiento().getNombre()));
        if (!mantenimientos.isEmpty()) {
            flag = true;
        }

        modelo.addAttribute("tipos", tipoMantenimientoServicio.buscarTiposAplicaA(logueado.getIdOrg(), TipoMantenimiento.AplicaA.CAMION));
        modelo.put("mantenimientos", mantenimientoPorTipo);
        modelo.put("camion", camionServicio.buscarCamion(idCamion));
        modelo.put("idCamion", idCamion);
        modelo.put("idTipo", idTipo);
        modelo.put("clase", clase);
        modelo.put("flag", flag);

        return "mantenimiento_listarHistorialCamionAdmin.html";

    }

    @GetMapping("/listarHistorialAcopladoAdmin")
    public String listarHistorialAcopladoAdmin(@RequestParam Long id, @RequestParam(required = false) Long idTipo,
            @RequestParam String clase, ModelMap modelo, HttpSession session) {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");
        Boolean flag = false;

        List<Mantenimiento> mantenimientos = mantenimientoServicio.buscarHistorialAcoplado(id);

        Map<String, List<Mantenimiento>> mantenimientoPorTipo = mantenimientos.stream()
                .sorted(Comparator.comparing(Mantenimiento::getFecha).reversed())
                .collect(Collectors.groupingBy(doc -> doc.getTipoMantenimiento().getNombre()));

        if (!mantenimientos.isEmpty()) {
            flag = true;
        }

        modelo.addAttribute("tipos", tipoMantenimientoServicio.buscarTiposAplicaA(logueado.getIdOrg(), TipoMantenimiento.AplicaA.ACOPLADO));
        modelo.put("mantenimientos", mantenimientoPorTipo);
        modelo.put("acoplado", acopladoServicio.buscarAcoplado(id));
        modelo.put("idAcoplado", id);
        modelo.put("idTipo", idTipo);
        modelo.put("clase", clase);
        modelo.put("flag", flag);

        return "mantenimiento_listarHistorialAcopladoAdmin.html";

    }
    
    @PostMapping("/listarHistorialAcopladoAdminFiltro")
    public String listarHistorialAcopladoAdminFiltro(@RequestParam Long idAcoplado, @RequestParam(required = false) Long tipo, @RequestParam String clase, 
            @RequestParam(required = false) Long idTipo, ModelMap modelo, HttpSession session) {
        
        Usuario logueado = (Usuario) session.getAttribute("usuariosession");
        Boolean flag = false;
        List<Mantenimiento> mantenimientos = new ArrayList();
        
        if(tipo == null){
            
            mantenimientos = mantenimientoServicio.buscarHistorialAcoplado(idAcoplado);
            
        } else {
            
            mantenimientos = mantenimientoServicio.buscarHistorialAcopladoTipoMantenimiento(idAcoplado, tipo);
            modelo.put("tipo", tipoMantenimientoServicio.buscarTipo(tipo));
            modelo.put("tipoId", tipo);
            
        }

        Map<String, List<Mantenimiento>> mantenimientoPorTipo = mantenimientos.stream()
                .sorted(Comparator.comparing(Mantenimiento::getFecha).reversed())
                .collect(Collectors.groupingBy(doc -> doc.getTipoMantenimiento().getNombre()));
        if (!mantenimientos.isEmpty()) {
            flag = true;
        }

        modelo.addAttribute("tipos", tipoMantenimientoServicio.buscarTiposAplicaA(logueado.getIdOrg(), TipoMantenimiento.AplicaA.ACOPLADO));
        modelo.put("mantenimientos", mantenimientoPorTipo);
        modelo.put("acoplado", acopladoServicio.buscarAcoplado(idAcoplado));
        modelo.put("idAcoplado", idAcoplado);
        modelo.put("idTipo", idTipo);
        modelo.put("clase", clase);
        modelo.put("flag", flag);

        return "mantenimiento_listarHistorialAcopladoAdmin.html";

    }

    @GetMapping("/registrarMasivoCamion")
    public String registrarMasivoCamion(ModelMap modelo, HttpSession session) {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        modelo.addAttribute("camiones", camionServicio.buscarCamionesHabAsc(logueado.getIdOrg()));
        modelo.put("camion", null);
        modelo.put("idOrg", logueado.getIdOrg());

        return "mantenimiento_registrarMasivoCamion.html";
    }

    @GetMapping("/registrarMasivo1Camion")
    public String registrarMasivo1Camion(@RequestParam Long idOrg, @RequestParam Long idCamion, ModelMap modelo) {

        Camion camion = camionServicio.buscarCamion(idCamion);
        int kmCamion = combustibleServicio.kmUltimaCarga(camion);

        modelo.put("kmCamion", kmCamion);
        modelo.put("kmProximoCamion", 0);
        modelo.put("kmAlarmaCamion", 0);
        modelo.put("kmVigenciaCamion", 0);
        modelo.put("camion", camion);
        modelo.addAttribute("camiones", camionServicio.buscarCamionesHabAsc(idOrg));
        modelo.addAttribute("tiposCamion", tipoMantenimientoServicio.buscarTiposAplicaA(idOrg, TipoMantenimiento.AplicaA.CAMION));
        modelo.put("idOrg", idOrg);

        return "mantenimiento_registrarMasivoCamion.html";
    }

    @PostMapping("/registroMasivoCamion")
    public String registroMasivoCamion(@RequestParam("mantenimientosCamionJson") String mantenimientosCamionJson,
            @RequestParam Long idCamion, @RequestParam String fecha,
            ModelMap modelo, RedirectAttributes redirectAttributes, HttpSession session) throws JsonProcessingException, ParseException {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");
        ObjectMapper mapper = new ObjectMapper();
        Date fechaOt = convertirFecha(fecha);

        List<MantenimientoDTO> mantenimientosCamionDTO = Arrays.asList(mapper.readValue(mantenimientosCamionJson, MantenimientoDTO[].class));

        for (MantenimientoDTO dto : mantenimientosCamionDTO) {
            Mantenimiento m = new Mantenimiento();
            TipoMantenimiento tipo = new TipoMantenimiento();
            tipo.setId(dto.getTipoMantenimiento());
            m.setTipoMantenimiento(tipo);
            m.setAplicaA(TipoMantenimiento.AplicaA.valueOf(dto.getAplicaA()));
            m.setKm(dto.getKm());
            m.setKmVigencia(dto.getKmVigencia());
            m.setKmProximo(dto.getKmProximo());
            m.setKmAlarma(dto.getKmAlarma());
            m.setObservacion(dto.getObservacion().toUpperCase());
            m.setEstado(Mantenimiento.Estado.VIGENTE);
            m.setIdOrg(logueado.getIdOrg());
            m.setUsuario(logueado);
            m.setFecha(fechaOt);
            Camion camion = camionServicio.buscarCamion(idCamion);
            m.setCamion(camion);

            Mantenimiento mantenimientoVigente = mantenimientoServicio.buscarExistenteMasivo(m);
            mantenimientoServicio.crearMantenimiento(m, mantenimientoVigente);
        }

        redirectAttributes.addFlashAttribute("mantenimientosC", mantenimientosCamionDTO);

        return "redirect:/mantenimiento/registradoMasivoCamion";

    }

    @GetMapping("/registradoMasivoCamion")
    public String registradoMasivoCamion(@ModelAttribute("mantenimientosC") List<Mantenimiento> mantenimientosC, ModelMap modelo) {

        modelo.put("exito", "La carga de Mantenimientos se ha REGISTRADO con éxito");
        modelo.addAttribute("mantenimientosC", mantenimientosC);

        return "mantenimiento_mostrarMasivoCamion.html";
    }

    @GetMapping("/registrarMasivoAcoplado")
    public String registrarMasivoAcoplado(ModelMap modelo, HttpSession session) {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        modelo.addAttribute("acoplados", acopladoServicio.buscarAcopladosHabAsc(logueado.getIdOrg()));
        modelo.put("acoplado", null);
        modelo.put("idOrg", logueado.getIdOrg());

        return "mantenimiento_registrarMasivoAcoplado.html";
    }

    @GetMapping("/registrarMasivo1Acoplado")
    public String registrarMasivo1Acoplado(@RequestParam Long idOrg, @RequestParam Long idAcoplado, ModelMap modelo) {

        Acoplado acoplado = acopladoServicio.buscarAcoplado(idAcoplado);
        int km = combustibleServicio.kmAcoplado(acoplado, obtenerFechaFija());
        modelo.put("acoplado", acoplado);
        modelo.put("kmAcoplado", km);
        modelo.put("kmProximoAcoplado", 0);
        modelo.put("kmAlarmaAcoplado", 0);
        modelo.put("kmVigenciaAcoplado", 0);
        modelo.addAttribute("acoplados", acopladoServicio.buscarAcopladosHabAsc(idOrg));
        modelo.addAttribute("tiposAcoplado", tipoMantenimientoServicio.buscarTiposAplicaA(idOrg, TipoMantenimiento.AplicaA.ACOPLADO));
        modelo.put("idOrg", idOrg);

        return "mantenimiento_registrarMasivoAcoplado.html";
    }

    @PostMapping("/registroMasivoAcoplado")
    public String registroMasivoAcoplado(@RequestParam("mantenimientosAcopladoJson") String mantenimientosAcopladoJson,
            @RequestParam Long idAcoplado, @RequestParam String fecha,
            ModelMap modelo, RedirectAttributes redirectAttributes, HttpSession session) throws JsonProcessingException, ParseException {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");
        ObjectMapper mapper = new ObjectMapper();
        Date fechaOt = convertirFecha(fecha);

        List<MantenimientoDTO> mantenimientosAcopladoDTO = Arrays.asList(mapper.readValue(mantenimientosAcopladoJson, MantenimientoDTO[].class));

        for (MantenimientoDTO dto : mantenimientosAcopladoDTO) {
            Mantenimiento m = new Mantenimiento();
            TipoMantenimiento tipo = new TipoMantenimiento();
            tipo.setId(dto.getTipoMantenimiento());
            m.setTipoMantenimiento(tipo);
            m.setAplicaA(TipoMantenimiento.AplicaA.valueOf(dto.getAplicaA()));
            m.setKm(dto.getKm());
            m.setKmVigencia(dto.getKmVigencia());
            m.setKmProximo(dto.getKmProximo());
            m.setKmAlarma(dto.getKmAlarma());
            m.setObservacion(dto.getObservacion().toUpperCase());
            m.setEstado(Mantenimiento.Estado.VIGENTE);
            m.setIdOrg(logueado.getIdOrg());
            m.setUsuario(logueado);
            m.setFecha(fechaOt);
            Acoplado acoplado = acopladoServicio.buscarAcoplado(idAcoplado);
            m.setAcoplado(acoplado);

            Mantenimiento mantenimientoVigente = mantenimientoServicio.buscarExistenteMasivo(m);
            mantenimientoServicio.crearMantenimiento(m, mantenimientoVigente);

        }

        redirectAttributes.addFlashAttribute("mantenimientosA", mantenimientosAcopladoDTO);

        return "redirect:/mantenimiento/registradoMasivoAcoplado";

    }

    @GetMapping("/registradoMasivoAcoplado")
    public String registradoMasivoAcoplado(@ModelAttribute("mantenimientosA") List<Mantenimiento> mantenimientosA, ModelMap modelo) {

        modelo.put("exito", "La carga de Mantenimientos se ha REGISTRADO con éxito");
        modelo.addAttribute("mantenimientosA", mantenimientosA);

        return "mantenimiento_mostrarMasivoAcoplado.html";
    }

    @GetMapping("/registrarMasivoChofer")
    public String registrarMasivoChofer(ModelMap modelo, HttpSession session) {

        Usuario chofer = (Usuario) session.getAttribute("usuariosession");

        Camion camion = null;
        Acoplado acoplado = null;

        if (chofer.getCamion() != null) {

            camion = chofer.getCamion();
            int kmCamion = combustibleServicio.kmUltimaCarga(camion);
            modelo.put("kmCamion", kmCamion);
            modelo.put("kmProximoCamion", kmCamion + 10000);
            modelo.put("kmAlarmaCamion", kmCamion + 9000);
            modelo.put("kmVigenciaCamion", 10000);

        }
        if (chofer.getAcoplado() != null) {

            acoplado = chofer.getAcoplado();
            int km = combustibleServicio.kmAcoplado(acoplado, obtenerFechaFija());
            modelo.put("kmAcoplado", km);
            modelo.put("kmProximoAcoplado", km + 10000);
            modelo.put("kmAlarmaAcoplado", km + 9000);
            modelo.put("kmVigenciaAcoplado", 10000);

        }

        modelo.put("camion", camion);
        modelo.put("acoplado", acoplado);
        modelo.addAttribute("tiposCamion", tipoMantenimientoServicio.buscarTiposAplicaA(chofer.getIdOrg(), TipoMantenimiento.AplicaA.CAMION));
        modelo.addAttribute("tiposAcoplado", tipoMantenimientoServicio.buscarTiposAplicaA(chofer.getIdOrg(), TipoMantenimiento.AplicaA.ACOPLADO));

        return "mantenimiento_registrarMasivoChofer.html";
    }

    @PostMapping("/registroMasivoChofer")
    public String registroMasivoChofer(@RequestParam("mantenimientosCamionJson") String mantenimientosCamionJson,
            @RequestParam("mantenimientosAcopladoJson") String mantenimientosAcopladoJson, @RequestParam String fecha,
            ModelMap modelo, HttpSession session, RedirectAttributes redirectAttributes)
            throws JsonProcessingException, ParseException {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");
        ObjectMapper mapper = new ObjectMapper();
        Date fechaOt = convertirFecha(fecha);

        List<MantenimientoDTO> mantenimientosCamionDTO = Arrays.asList(mapper.readValue(mantenimientosCamionJson, MantenimientoDTO[].class));
        List<MantenimientoDTO> mantenimientosAcopladoDTO = Arrays.asList(mapper.readValue(mantenimientosAcopladoJson, MantenimientoDTO[].class));

        List<Mantenimiento> mantenimientosCamion = new ArrayList<>();
        for (MantenimientoDTO dto : mantenimientosCamionDTO) {
            Mantenimiento m = new Mantenimiento();
            TipoMantenimiento tipo = new TipoMantenimiento();
            tipo.setId(dto.getTipoMantenimiento());
            m.setTipoMantenimiento(tipo);
            m.setAplicaA(TipoMantenimiento.AplicaA.valueOf(dto.getAplicaA()));
            m.setKm(dto.getKm());
            m.setKmVigencia(dto.getKmVigencia());
            m.setKmProximo(dto.getKmProximo());
            m.setKmAlarma(dto.getKmAlarma());
            m.setObservacion(dto.getObservacion().toUpperCase());
            mantenimientosCamion.add(m);
        }

        List<Mantenimiento> mantenimientosAcoplado = new ArrayList<>();
        for (MantenimientoDTO dto : mantenimientosAcopladoDTO) {
            Mantenimiento m = new Mantenimiento();
            TipoMantenimiento tipo = new TipoMantenimiento();
            tipo.setId(dto.getTipoMantenimiento());
            m.setTipoMantenimiento(tipo);
            m.setAplicaA(TipoMantenimiento.AplicaA.valueOf(dto.getAplicaA()));
            m.setKm(dto.getKm());
            m.setKmVigencia(dto.getKmVigencia());
            m.setKmProximo(dto.getKmProximo());
            m.setKmAlarma(dto.getKmAlarma());
            m.setObservacion(dto.getObservacion().toUpperCase());
            mantenimientosCamion.add(m);
        }

        List<Mantenimiento> mantenimientosTotales = new ArrayList<>();
        mantenimientosTotales.addAll(mantenimientosCamion);
        mantenimientosTotales.addAll(mantenimientosAcoplado);

        for (Mantenimiento m : mantenimientosTotales) {
            m.setEstado(Mantenimiento.Estado.VIGENTE);
            m.setIdOrg(logueado.getIdOrg());
            m.setUsuario(logueado);
            m.setFecha(fechaOt);

            if (m.getAplicaA().equals(TipoMantenimiento.AplicaA.CAMION)) {
                Camion camion = logueado.getCamion();
                m.setCamion(camion);
            } else if (m.getAplicaA().equals(TipoMantenimiento.AplicaA.ACOPLADO)) {
                Acoplado acoplado = logueado.getAcoplado();
                m.setAcoplado(acoplado);
            }

            Mantenimiento mantenimientoVigente = mantenimientoServicio.buscarExistenteMasivo(m);

            mantenimientoServicio.crearMantenimiento(m, mantenimientoVigente);

        }

        redirectAttributes.addFlashAttribute("mantenimientosC", mantenimientosCamionDTO);
        redirectAttributes.addFlashAttribute("mantenimientosA", mantenimientosAcopladoDTO);

        return "redirect:/mantenimiento/registradoMasivoChofer";

    }

    @GetMapping("/registradoMasivoChofer")
    public String registradoMasivoChofer(@ModelAttribute("mantenimientosC") List<Mantenimiento> mantenimientosC,
            @ModelAttribute("mantenimientosA") List<Mantenimiento> mantenimientosA, ModelMap modelo) {

        modelo.put("exito", "La carga de Mantenimientos se ha REGISTRADO con éxito");
        modelo.addAttribute("mantenimientosC", mantenimientosC);
        modelo.addAttribute("mantenimientosA", mantenimientosA);

        return "mantenimiento_mostrarMasivoChofer.html";
    }

    @PostMapping("/exportarCamiones")
    public String exportarCamiones(@RequestParam(required = false) Long idCamion, @RequestParam(required = false) Long idTipo,
            @RequestParam String clase, ModelMap modelo, HttpSession session) throws ParseException {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");
        List<Mantenimiento> mantenimientos = new ArrayList();

        if (idCamion != null && idTipo == null && clase.equalsIgnoreCase("PREVENTIVO")) {

            mantenimientos = mantenimientoServicio.buscarMantenimientoVigenteIdCamionPreventivo(idCamion);

            Map<String, List<Mantenimiento>> mantenimientoPorTipo = mantenimientos.stream()
                    .sorted(Comparator.comparing(Mantenimiento::getKmVigencia))
                    .collect(Collectors.groupingBy(doc -> doc.getTipoMantenimiento().getNombre()));

            modelo.put("mantenimientos", mantenimientoPorTipo);

        } else if (idCamion != null && idTipo == null && clase.equalsIgnoreCase("TODOS")) {

            mantenimientos = mantenimientoServicio.buscarMantenimientoVigenteIdCamion(idCamion);

            Map<String, List<Mantenimiento>> mantenimientoPorTipo = mantenimientos.stream()
                    .sorted(Comparator.comparing(Mantenimiento::getKmVigencia))
                    .collect(Collectors.groupingBy(doc -> doc.getTipoMantenimiento().getNombre()));

            modelo.put("mantenimientos", mantenimientoPorTipo);

        } else if (idCamion != null && idTipo == null && clase.equalsIgnoreCase("CORRECTIVO")) {

            mantenimientos = mantenimientoServicio.buscarMantenimientoVigenteIdCamionCorrectivo(idCamion);

            Map<String, List<Mantenimiento>> mantenimientoPorTipo = mantenimientos.stream()
                    .sorted(Comparator.comparing(Mantenimiento::getKmVigencia))
                    .collect(Collectors.groupingBy(doc -> doc.getTipoMantenimiento().getNombre()));

            modelo.put("mantenimientos", mantenimientoPorTipo);

        } else if (idCamion == null && idTipo != null && clase.equalsIgnoreCase("PREVENTIVO")) {

            mantenimientos = mantenimientoServicio.buscarMantenimientosVigenteCamionesPorTipoPreventivo(logueado.getIdOrg(), idTipo);

            Map<String, List<Mantenimiento>> mantenimientoPorTipo = mantenimientos.stream()
                    .sorted(Comparator.comparing(Mantenimiento::getKmVigencia))
                    .collect(Collectors.groupingBy(doc -> doc.getTipoMantenimiento().getNombre()));

            modelo.put("mantenimientos", mantenimientoPorTipo);

        } else if (idCamion == null && idTipo != null && clase.equalsIgnoreCase("CORRECTIVO")) {

            mantenimientos = mantenimientoServicio.buscarMantenimientosVigenteCamionesPorTipoCorrectivo(logueado.getIdOrg(), idTipo);

            Map<String, List<Mantenimiento>> mantenimientoPorTipo = mantenimientos.stream()
                    .sorted(Comparator.comparing(Mantenimiento::getKmVigencia))
                    .collect(Collectors.groupingBy(doc -> doc.getTipoMantenimiento().getNombre()));

            modelo.put("mantenimientos", mantenimientoPorTipo);

        } else if (idCamion == null && idTipo != null && clase.equalsIgnoreCase("TODOS")) {

            mantenimientos = mantenimientoServicio.buscarMantenimientosVigenteCamionesPorTipo(logueado.getIdOrg(), idTipo);

            Map<String, List<Mantenimiento>> mantenimientoPorTipo = mantenimientos.stream()
                    .sorted(Comparator.comparing(Mantenimiento::getKmVigencia))
                    .collect(Collectors.groupingBy(doc -> doc.getTipoMantenimiento().getNombre()));

            modelo.put("mantenimientos", mantenimientoPorTipo);

        } else if (idCamion != null && idTipo != null && clase.equalsIgnoreCase("PREVENTIVO")) {

            mantenimientos = mantenimientoServicio.buscarMantenimientosVigenteCamionPorTipoPreventivo(idCamion, idTipo);

            Map<String, List<Mantenimiento>> mantenimientoPorTipo = mantenimientos.stream()
                    .sorted(Comparator.comparing(Mantenimiento::getKmVigencia))
                    .collect(Collectors.groupingBy(doc -> doc.getTipoMantenimiento().getNombre()));

            modelo.put("mantenimientos", mantenimientoPorTipo);

        } else if (idCamion != null && idTipo != null && clase.equalsIgnoreCase("CORRECTIVO")) {

            mantenimientos = mantenimientoServicio.buscarMantenimientosVigenteCamionPorTipoCorrectivo(idCamion, idTipo);

            Map<String, List<Mantenimiento>> mantenimientoPorTipo = mantenimientos.stream()
                    .sorted(Comparator.comparing(Mantenimiento::getKmVigencia))
                    .collect(Collectors.groupingBy(doc -> doc.getTipoMantenimiento().getNombre()));

            modelo.put("mantenimientos", mantenimientoPorTipo);

        } else if (idCamion != null && idTipo != null && clase.equalsIgnoreCase("TODOS")) {

            mantenimientos = mantenimientoServicio.buscarMantenimientosVigenteCamionPorTipo(idCamion, idTipo);

            Map<String, List<Mantenimiento>> mantenimientoPorTipo = mantenimientos.stream()
                    .sorted(Comparator.comparing(Mantenimiento::getKmVigencia))
                    .collect(Collectors.groupingBy(doc -> doc.getTipoMantenimiento().getNombre()));

            modelo.put("mantenimientos", mantenimientoPorTipo);

        } else if (idCamion == null && idTipo == null && clase.equalsIgnoreCase("TODOS")) {

            mantenimientos = mantenimientoServicio.buscarMantenimientosVigenteCamiones(logueado.getIdOrg());

            Map<String, List<Mantenimiento>> mantenimientoPorTipo = mantenimientos.stream()
                    .sorted(Comparator.comparing(Mantenimiento::getKmVigencia))
                    .collect(Collectors.groupingBy(doc -> doc.getTipoMantenimiento().getNombre()));

            modelo.put("mantenimientos", mantenimientoPorTipo);

        } else if (idCamion == null && idTipo == null && clase.equalsIgnoreCase("PREVENTIVO")) {

            mantenimientos = mantenimientoServicio.buscarMantenimientosVigenteCamionesPreventivo(logueado.getIdOrg());

            Map<String, List<Mantenimiento>> mantenimientoPorTipo = mantenimientos.stream()
                    .sorted(Comparator.comparing(Mantenimiento::getKmVigencia))
                    .collect(Collectors.groupingBy(doc -> doc.getTipoMantenimiento().getNombre()));

            modelo.put("mantenimientos", mantenimientoPorTipo);

        } else if (idCamion == null && idTipo == null && clase.equalsIgnoreCase("CORRECTIVO")) {

            mantenimientos = mantenimientoServicio.buscarMantenimientosVigenteCamionesCorrectivo(logueado.getIdOrg());

            Map<String, List<Mantenimiento>> mantenimientoPorTipo = mantenimientos.stream()
                    .sorted(Comparator.comparing(Mantenimiento::getKmVigencia))
                    .collect(Collectors.groupingBy(doc -> doc.getTipoMantenimiento().getNombre()));

            modelo.put("mantenimientos", mantenimientoPorTipo);

        }

        modelo.put("idCamion", idCamion);
        modelo.put("idTipo", idTipo);
        modelo.put("clase", clase);

        return "mantenimiento_exportarCamiones.html";

    }

    @PostMapping("/exportaCamiones")
    public void exportaCamion(@RequestParam(required = false) Long idCamion, @RequestParam(required = false) Long idTipo,
            @RequestParam String clase, HttpSession session, HttpServletResponse response) throws IOException, ParseException {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        if (idCamion != null && idTipo == null && clase.equalsIgnoreCase("PREVENTIVO")) {

            List<Mantenimiento> mantenimientos = mantenimientoServicio.buscarMantenimientoVigenteIdCamionPreventivo(idCamion);

            Map<String, List<Mantenimiento>> mantenimientoPorTipo = mantenimientos.stream()
                    .sorted(Comparator.comparing(Mantenimiento::getKmVigencia))
                    .collect(Collectors.groupingBy(doc -> doc.getTipoMantenimiento().getNombre()));

            String htmlContent = generateHtmlFromObjects(mantenimientoPorTipo);
            excelServicio.exportHtmlToExcelMantenimiento(htmlContent, response);

        } else if (idCamion != null && idTipo == null && clase.equalsIgnoreCase("TODOS")) {

            List<Mantenimiento> mantenimientos = mantenimientoServicio.buscarMantenimientoVigenteIdCamion(idCamion);

            Map<String, List<Mantenimiento>> mantenimientoPorTipo = mantenimientos.stream()
                    .sorted(Comparator.comparing(Mantenimiento::getKmVigencia))
                    .collect(Collectors.groupingBy(doc -> doc.getTipoMantenimiento().getNombre()));

            String htmlContent = generateHtmlFromObjects(mantenimientoPorTipo);
            excelServicio.exportHtmlToExcelMantenimiento(htmlContent, response);

        } else if (idCamion != null && idTipo == null && clase.equalsIgnoreCase("CORRECTIVO")) {

            List<Mantenimiento> mantenimientos = mantenimientoServicio.buscarMantenimientoVigenteIdCamionCorrectivo(idCamion);

            Map<String, List<Mantenimiento>> mantenimientoPorTipo = mantenimientos.stream()
                    .sorted(Comparator.comparing(Mantenimiento::getKmVigencia))
                    .collect(Collectors.groupingBy(doc -> doc.getTipoMantenimiento().getNombre()));

            String htmlContent = generateHtmlFromObjects(mantenimientoPorTipo);
            excelServicio.exportHtmlToExcelMantenimiento(htmlContent, response);

        } else if (idCamion == null && idTipo != null && clase.equalsIgnoreCase("PREVENTIVO")) {

            List<Mantenimiento> mantenimientos = mantenimientoServicio.buscarMantenimientosVigenteCamionesPorTipoPreventivo(logueado.getIdOrg(), idTipo);

            Map<String, List<Mantenimiento>> mantenimientoPorTipo = mantenimientos.stream()
                    .sorted(Comparator.comparing(Mantenimiento::getKmVigencia))
                    .collect(Collectors.groupingBy(doc -> doc.getTipoMantenimiento().getNombre()));

            String htmlContent = generateHtmlFromObjects(mantenimientoPorTipo);
            excelServicio.exportHtmlToExcelMantenimiento(htmlContent, response);

        } else if (idCamion == null && idTipo != null && clase.equalsIgnoreCase("CORRECTIVO")) {

            List<Mantenimiento> mantenimientos = mantenimientoServicio.buscarMantenimientosVigenteCamionesPorTipoCorrectivo(logueado.getIdOrg(), idTipo);

            Map<String, List<Mantenimiento>> mantenimientoPorTipo = mantenimientos.stream()
                    .sorted(Comparator.comparing(Mantenimiento::getKmVigencia))
                    .collect(Collectors.groupingBy(doc -> doc.getTipoMantenimiento().getNombre()));

            String htmlContent = generateHtmlFromObjects(mantenimientoPorTipo);
            excelServicio.exportHtmlToExcelMantenimiento(htmlContent, response);

        } else if (idCamion == null && idTipo != null && clase.equalsIgnoreCase("TODOS")) {

            List<Mantenimiento> mantenimientos = mantenimientoServicio.buscarMantenimientosVigenteCamionesPorTipo(logueado.getIdOrg(), idTipo);

            Map<String, List<Mantenimiento>> mantenimientoPorTipo = mantenimientos.stream()
                    .sorted(Comparator.comparing(Mantenimiento::getKmVigencia))
                    .collect(Collectors.groupingBy(doc -> doc.getTipoMantenimiento().getNombre()));

            String htmlContent = generateHtmlFromObjects(mantenimientoPorTipo);
            excelServicio.exportHtmlToExcelMantenimiento(htmlContent, response);

        } else if (idCamion != null && idTipo != null && clase.equalsIgnoreCase("PREVENTIVO")) {

            List<Mantenimiento> mantenimientos = mantenimientoServicio.buscarMantenimientosVigenteCamionPorTipoPreventivo(idCamion, idTipo);

            Map<String, List<Mantenimiento>> mantenimientoPorTipo = mantenimientos.stream()
                    .sorted(Comparator.comparing(Mantenimiento::getKmVigencia))
                    .collect(Collectors.groupingBy(doc -> doc.getTipoMantenimiento().getNombre()));

            String htmlContent = generateHtmlFromObjects(mantenimientoPorTipo);
            excelServicio.exportHtmlToExcelMantenimiento(htmlContent, response);

        } else if (idCamion != null && idTipo != null && clase.equalsIgnoreCase("CORRECTIVO")) {

            List<Mantenimiento> mantenimientos = mantenimientoServicio.buscarMantenimientosVigenteCamionPorTipoCorrectivo(idCamion, idTipo);

            Map<String, List<Mantenimiento>> mantenimientoPorTipo = mantenimientos.stream()
                    .sorted(Comparator.comparing(Mantenimiento::getKmVigencia))
                    .collect(Collectors.groupingBy(doc -> doc.getTipoMantenimiento().getNombre()));

            String htmlContent = generateHtmlFromObjects(mantenimientoPorTipo);
            excelServicio.exportHtmlToExcelMantenimiento(htmlContent, response);

        } else if (idCamion != null && idTipo != null && clase.equalsIgnoreCase("TODOS")) {

            List<Mantenimiento> mantenimientos = mantenimientoServicio.buscarMantenimientosVigenteCamionPorTipo(idCamion, idTipo);

            Map<String, List<Mantenimiento>> mantenimientoPorTipo = mantenimientos.stream()
                    .sorted(Comparator.comparing(Mantenimiento::getKmVigencia))
                    .collect(Collectors.groupingBy(doc -> doc.getTipoMantenimiento().getNombre()));

            String htmlContent = generateHtmlFromObjects(mantenimientoPorTipo);
            excelServicio.exportHtmlToExcelMantenimiento(htmlContent, response);

        } else if (idCamion == null && idTipo == null && clase.equalsIgnoreCase("TODOS")) {

            List<Mantenimiento> mantenimientos = mantenimientoServicio.buscarMantenimientosVigenteCamiones(logueado.getIdOrg());

            Map<String, List<Mantenimiento>> mantenimientoPorTipo = mantenimientos.stream()
                    .sorted(Comparator.comparing(Mantenimiento::getKmVigencia))
                    .collect(Collectors.groupingBy(doc -> doc.getTipoMantenimiento().getNombre()));

            String htmlContent = generateHtmlFromObjects(mantenimientoPorTipo);
            excelServicio.exportHtmlToExcelMantenimiento(htmlContent, response);

        } else if (idCamion == null && idTipo == null && clase.equalsIgnoreCase("PREVENTIVO")) {

            List<Mantenimiento> mantenimientos = mantenimientoServicio.buscarMantenimientosVigenteCamionesPreventivo(logueado.getIdOrg());

            Map<String, List<Mantenimiento>> mantenimientoPorTipo = mantenimientos.stream()
                    .sorted(Comparator.comparing(Mantenimiento::getKmVigencia))
                    .collect(Collectors.groupingBy(doc -> doc.getTipoMantenimiento().getNombre()));

            String htmlContent = generateHtmlFromObjects(mantenimientoPorTipo);
            excelServicio.exportHtmlToExcelMantenimiento(htmlContent, response);

        } else if (idCamion == null && idTipo == null && clase.equalsIgnoreCase("CORRECTIVO")) {

            List<Mantenimiento> mantenimientos = mantenimientoServicio.buscarMantenimientosVigenteCamionesCorrectivo(logueado.getIdOrg());

            Map<String, List<Mantenimiento>> mantenimientoPorTipo = mantenimientos.stream()
                    .sorted(Comparator.comparing(Mantenimiento::getKmVigencia))
                    .collect(Collectors.groupingBy(doc -> doc.getTipoMantenimiento().getNombre()));

            String htmlContent = generateHtmlFromObjects(mantenimientoPorTipo);
            excelServicio.exportHtmlToExcelMantenimiento(htmlContent, response);
        }

    }

    @PostMapping("/exportarHistorialCamion")
    public String exportarHistorialCamion(@RequestParam Long idCamion, ModelMap modelo) {

        List<Mantenimiento> mantenimientos = mantenimientoServicio.buscarHistorialCamion(idCamion);

        Map<String, List<Mantenimiento>> mantenimientoPorTipo = mantenimientos.stream()
                .sorted(Comparator.comparing(Mantenimiento::getFecha).reversed())
                .collect(Collectors.groupingBy(doc -> doc.getTipoMantenimiento().getNombre()));

        modelo.put("mantenimientos", mantenimientoPorTipo);
        modelo.put("camion", camionServicio.buscarCamion(idCamion));
        modelo.put("idCamion", idCamion);

        return "mantenimiento_exportarHistorialCamion.html";

    }

    @PostMapping("/exportaHistorialCamion")
    public void exportaHistorialCamion(@RequestParam Long idCamion, @RequestParam(required = false) Long tipo, 
            HttpSession session, HttpServletResponse response) throws IOException, ParseException {

        List<Mantenimiento> mantenimientos = new ArrayList();
        
        if(tipo == null){
            
            mantenimientos = mantenimientoServicio.buscarHistorialCamion(idCamion);
            
        } else {
            
            mantenimientos = mantenimientoServicio.buscarHistorialCamionTipoMantenimiento(idCamion, tipo);
            
        }

        Map<String, List<Mantenimiento>> mantenimientoPorTipo = mantenimientos.stream()
                .sorted(Comparator.comparing(Mantenimiento::getFecha).reversed())
                .collect(Collectors.groupingBy(doc -> doc.getTipoMantenimiento().getNombre()));

        Camion camion = camionServicio.buscarCamion(idCamion);
        Acoplado acoplado = null;

        String htmlContent = generateHtmlFromObjectsHistorial(mantenimientoPorTipo);
        excelServicio.exportHtmlToExcelMantenimientoHistorial(htmlContent, response, camion, acoplado);

    }

    @PostMapping("/exportarAcoplados")
    public String exportarAcoplados(@RequestParam(required = false) Long idAcoplado, @RequestParam(required = false) Long idTipo,
            @RequestParam String clase, ModelMap modelo, HttpSession session) throws ParseException {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");
        List<Mantenimiento> mantenimientos = new ArrayList();

        if (idAcoplado != null && idTipo == null && clase.equalsIgnoreCase("PREVENTIVO")) {

            mantenimientos = mantenimientoServicio.buscarMantenimientoVigenteIdAcopladoPreventivo(idAcoplado);

            Map<String, List<Mantenimiento>> mantenimientoPorTipo = mantenimientos.stream()
                    .sorted(Comparator.comparing(Mantenimiento::getKmVigencia))
                    .collect(Collectors.groupingBy(doc -> doc.getTipoMantenimiento().getNombre()));

            modelo.put("mantenimientos", mantenimientoPorTipo);

        } else if (idAcoplado != null && idTipo == null && clase.equalsIgnoreCase("CORRECTIVO")) {

            mantenimientos = mantenimientoServicio.buscarMantenimientoVigenteIdAcopladoCorrectivo(idAcoplado);

            Map<String, List<Mantenimiento>> mantenimientoPorTipo = mantenimientos.stream()
                    .sorted(Comparator.comparing(Mantenimiento::getKmVigencia))
                    .collect(Collectors.groupingBy(doc -> doc.getTipoMantenimiento().getNombre()));

            modelo.put("mantenimientos", mantenimientoPorTipo);

        } else if (idAcoplado != null && idTipo == null && clase.equalsIgnoreCase("TODOS")) {

            mantenimientos = mantenimientoServicio.buscarMantenimientoVigenteIdAcoplado(idAcoplado);

            Map<String, List<Mantenimiento>> mantenimientoPorTipo = mantenimientos.stream()
                    .sorted(Comparator.comparing(Mantenimiento::getKmVigencia))
                    .collect(Collectors.groupingBy(doc -> doc.getTipoMantenimiento().getNombre()));

            modelo.put("mantenimientos", mantenimientoPorTipo);

        } else if (idAcoplado == null && idTipo != null && clase.equalsIgnoreCase("PREVENTIVO")) {

            mantenimientos = mantenimientoServicio.buscarMantenimientosVigenteAcopladosPorTipoPreventivo(logueado.getIdOrg(), idTipo);

            Map<String, List<Mantenimiento>> mantenimientoPorTipo = mantenimientos.stream()
                    .sorted(Comparator.comparing(Mantenimiento::getKmVigencia))
                    .collect(Collectors.groupingBy(doc -> doc.getTipoMantenimiento().getNombre()));

            modelo.put("mantenimientos", mantenimientoPorTipo);

        } else if (idAcoplado == null && idTipo != null && clase.equalsIgnoreCase("CORRECTIVO")) {

            mantenimientos = mantenimientoServicio.buscarMantenimientosVigenteAcopladosPorTipoCorrectivo(logueado.getIdOrg(), idTipo);

            Map<String, List<Mantenimiento>> mantenimientoPorTipo = mantenimientos.stream()
                    .sorted(Comparator.comparing(Mantenimiento::getKmVigencia))
                    .collect(Collectors.groupingBy(doc -> doc.getTipoMantenimiento().getNombre()));

            modelo.put("mantenimientos", mantenimientoPorTipo);

        } else if (idAcoplado == null && idTipo != null && clase.equalsIgnoreCase("TODOS")) {

            mantenimientos = mantenimientoServicio.buscarMantenimientosVigenteAcopladosPorTipo(logueado.getIdOrg(), idTipo);

            Map<String, List<Mantenimiento>> mantenimientoPorTipo = mantenimientos.stream()
                    .sorted(Comparator.comparing(Mantenimiento::getKmVigencia))
                    .collect(Collectors.groupingBy(doc -> doc.getTipoMantenimiento().getNombre()));

            modelo.put("mantenimientos", mantenimientoPorTipo);

        } else if (idAcoplado != null && idTipo != null && clase.equalsIgnoreCase("PREVENTIVO")) {

            mantenimientos = mantenimientoServicio.buscarMantenimientosVigenteAcopladoPorTipoPreventivo(idAcoplado, idTipo);

            Map<String, List<Mantenimiento>> mantenimientoPorTipo = mantenimientos.stream()
                    .sorted(Comparator.comparing(Mantenimiento::getKmVigencia))
                    .collect(Collectors.groupingBy(doc -> doc.getTipoMantenimiento().getNombre()));

            modelo.put("mantenimientos", mantenimientoPorTipo);

        } else if (idAcoplado != null && idTipo != null && clase.equalsIgnoreCase("CORRECTIVO")) {

            mantenimientos = mantenimientoServicio.buscarMantenimientosVigenteAcopladoPorTipoCorrectivo(idAcoplado, idTipo);

            Map<String, List<Mantenimiento>> mantenimientoPorTipo = mantenimientos.stream()
                    .sorted(Comparator.comparing(Mantenimiento::getKmVigencia))
                    .collect(Collectors.groupingBy(doc -> doc.getTipoMantenimiento().getNombre()));

            modelo.put("mantenimientos", mantenimientoPorTipo);

        } else if (idAcoplado != null && idTipo != null && clase.equalsIgnoreCase("TODOS")) {

            mantenimientos = mantenimientoServicio.buscarMantenimientosVigenteAcopladoPorTipo(idAcoplado, idTipo);

            Map<String, List<Mantenimiento>> mantenimientoPorTipo = mantenimientos.stream()
                    .sorted(Comparator.comparing(Mantenimiento::getKmVigencia))
                    .collect(Collectors.groupingBy(doc -> doc.getTipoMantenimiento().getNombre()));

            modelo.put("mantenimientos", mantenimientoPorTipo);

        } else if (idAcoplado == null && idTipo == null && clase.equalsIgnoreCase("TODOS")) {

            mantenimientos = mantenimientoServicio.buscarMantenimientosVigenteAcoplados(logueado.getIdOrg());

            Map<String, List<Mantenimiento>> mantenimientoPorTipo = mantenimientos.stream()
                    .sorted(Comparator.comparing(Mantenimiento::getKmVigencia))
                    .collect(Collectors.groupingBy(doc -> doc.getTipoMantenimiento().getNombre()));

            modelo.put("mantenimientos", mantenimientoPorTipo);

        } else if (idAcoplado == null && idTipo == null && clase.equalsIgnoreCase("PREVENTIVO")) {

            mantenimientos = mantenimientoServicio.buscarMantenimientosVigenteAcopladosPreventivo(logueado.getIdOrg());

            Map<String, List<Mantenimiento>> mantenimientoPorTipo = mantenimientos.stream()
                    .sorted(Comparator.comparing(Mantenimiento::getKmVigencia))
                    .collect(Collectors.groupingBy(doc -> doc.getTipoMantenimiento().getNombre()));

            modelo.put("mantenimientos", mantenimientoPorTipo);

        } else if (idAcoplado == null && idTipo == null && clase.equalsIgnoreCase("CORRECTIVO")) {

            mantenimientos = mantenimientoServicio.buscarMantenimientosVigenteAcopladosCorrectivo(logueado.getIdOrg());

            Map<String, List<Mantenimiento>> mantenimientoPorTipo = mantenimientos.stream()
                    .sorted(Comparator.comparing(Mantenimiento::getKmVigencia))
                    .collect(Collectors.groupingBy(doc -> doc.getTipoMantenimiento().getNombre()));

            modelo.put("mantenimientos", mantenimientoPorTipo);

        }

        modelo.put("idAcoplado", idAcoplado);
        modelo.put("idTipo", idTipo);
        modelo.put("clase", clase);

        return "mantenimiento_exportarAcoplados.html";

    }

    @PostMapping("/exportaAcoplados")
    public void exportaAcoplados(@RequestParam(required = false) Long idAcoplado, @RequestParam(required = false) Long idTipo,
            @RequestParam String clase, HttpSession session, HttpServletResponse response) throws IOException, ParseException {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");
        List<Mantenimiento> mantenimientos = new ArrayList();

        if (idAcoplado != null && idTipo == null && clase.equalsIgnoreCase("PREVENTIVO")) {

            mantenimientos = mantenimientoServicio.buscarMantenimientoVigenteIdAcopladoPreventivo(idAcoplado);

            Map<String, List<Mantenimiento>> mantenimientoPorTipo = mantenimientos.stream()
                    .sorted(Comparator.comparing(Mantenimiento::getKmVigencia))
                    .collect(Collectors.groupingBy(doc -> doc.getTipoMantenimiento().getNombre()));

            String htmlContent = generateHtmlFromObjects(mantenimientoPorTipo);
            excelServicio.exportHtmlToExcelMantenimiento(htmlContent, response);

        } else if (idAcoplado != null && idTipo == null && clase.equalsIgnoreCase("CORRECTIVO")) {

            mantenimientos = mantenimientoServicio.buscarMantenimientoVigenteIdAcopladoCorrectivo(idAcoplado);

            Map<String, List<Mantenimiento>> mantenimientoPorTipo = mantenimientos.stream()
                    .sorted(Comparator.comparing(Mantenimiento::getKmVigencia))
                    .collect(Collectors.groupingBy(doc -> doc.getTipoMantenimiento().getNombre()));

            String htmlContent = generateHtmlFromObjects(mantenimientoPorTipo);
            excelServicio.exportHtmlToExcelMantenimiento(htmlContent, response);

        } else if (idAcoplado != null && idTipo == null && clase.equalsIgnoreCase("TODOS")) {

            mantenimientos = mantenimientoServicio.buscarMantenimientoVigenteIdAcoplado(idAcoplado);

            Map<String, List<Mantenimiento>> mantenimientoPorTipo = mantenimientos.stream()
                    .sorted(Comparator.comparing(Mantenimiento::getKmVigencia))
                    .collect(Collectors.groupingBy(doc -> doc.getTipoMantenimiento().getNombre()));

            String htmlContent = generateHtmlFromObjects(mantenimientoPorTipo);
            excelServicio.exportHtmlToExcelMantenimiento(htmlContent, response);

        } else if (idAcoplado == null && idTipo != null && clase.equalsIgnoreCase("PREVENTIVO")) {

            mantenimientos = mantenimientoServicio.buscarMantenimientosVigenteAcopladosPorTipoPreventivo(logueado.getIdOrg(), idTipo);

            Map<String, List<Mantenimiento>> mantenimientoPorTipo = mantenimientos.stream()
                    .sorted(Comparator.comparing(Mantenimiento::getKmVigencia))
                    .collect(Collectors.groupingBy(doc -> doc.getTipoMantenimiento().getNombre()));

            String htmlContent = generateHtmlFromObjects(mantenimientoPorTipo);
            excelServicio.exportHtmlToExcelMantenimiento(htmlContent, response);

        } else if (idAcoplado == null && idTipo != null && clase.equalsIgnoreCase("CORRECTIVO")) {

            mantenimientos = mantenimientoServicio.buscarMantenimientosVigenteAcopladosPorTipoCorrectivo(logueado.getIdOrg(), idTipo);

            Map<String, List<Mantenimiento>> mantenimientoPorTipo = mantenimientos.stream()
                    .sorted(Comparator.comparing(Mantenimiento::getKmVigencia))
                    .collect(Collectors.groupingBy(doc -> doc.getTipoMantenimiento().getNombre()));

            String htmlContent = generateHtmlFromObjects(mantenimientoPorTipo);
            excelServicio.exportHtmlToExcelMantenimiento(htmlContent, response);

        } else if (idAcoplado == null && idTipo != null && clase.equalsIgnoreCase("TODOS")) {

            mantenimientos = mantenimientoServicio.buscarMantenimientosVigenteAcopladosPorTipo(logueado.getIdOrg(), idTipo);

            Map<String, List<Mantenimiento>> mantenimientoPorTipo = mantenimientos.stream()
                    .sorted(Comparator.comparing(Mantenimiento::getKmVigencia))
                    .collect(Collectors.groupingBy(doc -> doc.getTipoMantenimiento().getNombre()));

            String htmlContent = generateHtmlFromObjects(mantenimientoPorTipo);
            excelServicio.exportHtmlToExcelMantenimiento(htmlContent, response);

        } else if (idAcoplado != null && idTipo != null && clase.equalsIgnoreCase("PREVENTIVO")) {

            mantenimientos = mantenimientoServicio.buscarMantenimientosVigenteAcopladoPorTipoPreventivo(idAcoplado, idTipo);

            Map<String, List<Mantenimiento>> mantenimientoPorTipo = mantenimientos.stream()
                    .sorted(Comparator.comparing(Mantenimiento::getKmVigencia))
                    .collect(Collectors.groupingBy(doc -> doc.getTipoMantenimiento().getNombre()));

            String htmlContent = generateHtmlFromObjects(mantenimientoPorTipo);
            excelServicio.exportHtmlToExcelMantenimiento(htmlContent, response);

        } else if (idAcoplado != null && idTipo != null && clase.equalsIgnoreCase("CORRECTIVO")) {

            mantenimientos = mantenimientoServicio.buscarMantenimientosVigenteAcopladoPorTipoCorrectivo(idAcoplado, idTipo);

            Map<String, List<Mantenimiento>> mantenimientoPorTipo = mantenimientos.stream()
                    .sorted(Comparator.comparing(Mantenimiento::getKmVigencia))
                    .collect(Collectors.groupingBy(doc -> doc.getTipoMantenimiento().getNombre()));

            String htmlContent = generateHtmlFromObjects(mantenimientoPorTipo);
            excelServicio.exportHtmlToExcelMantenimiento(htmlContent, response);

        } else if (idAcoplado != null && idTipo != null && clase.equalsIgnoreCase("TODOS")) {

            mantenimientos = mantenimientoServicio.buscarMantenimientosVigenteAcopladoPorTipo(idAcoplado, idTipo);

            Map<String, List<Mantenimiento>> mantenimientoPorTipo = mantenimientos.stream()
                    .sorted(Comparator.comparing(Mantenimiento::getKmVigencia))
                    .collect(Collectors.groupingBy(doc -> doc.getTipoMantenimiento().getNombre()));

            String htmlContent = generateHtmlFromObjects(mantenimientoPorTipo);
            excelServicio.exportHtmlToExcelMantenimiento(htmlContent, response);

        } else if (idAcoplado == null && idTipo == null && clase.equalsIgnoreCase("TODOS")) {

            mantenimientos = mantenimientoServicio.buscarMantenimientosVigenteAcoplados(logueado.getIdOrg());

            Map<String, List<Mantenimiento>> mantenimientoPorTipo = mantenimientos.stream()
                    .sorted(Comparator.comparing(Mantenimiento::getKmVigencia))
                    .collect(Collectors.groupingBy(doc -> doc.getTipoMantenimiento().getNombre()));

            String htmlContent = generateHtmlFromObjects(mantenimientoPorTipo);
            excelServicio.exportHtmlToExcelMantenimiento(htmlContent, response);

        } else if (idAcoplado == null && idTipo == null && clase.equalsIgnoreCase("PREVENTIVO")) {

            mantenimientos = mantenimientoServicio.buscarMantenimientosVigenteAcopladosPreventivo(logueado.getIdOrg());

            Map<String, List<Mantenimiento>> mantenimientoPorTipo = mantenimientos.stream()
                    .sorted(Comparator.comparing(Mantenimiento::getKmVigencia))
                    .collect(Collectors.groupingBy(doc -> doc.getTipoMantenimiento().getNombre()));

            String htmlContent = generateHtmlFromObjects(mantenimientoPorTipo);
            excelServicio.exportHtmlToExcelMantenimiento(htmlContent, response);

        } else if (idAcoplado == null && idTipo == null && clase.equalsIgnoreCase("CORRECTIVO")) {

            mantenimientos = mantenimientoServicio.buscarMantenimientosVigenteAcopladosCorrectivo(logueado.getIdOrg());

            Map<String, List<Mantenimiento>> mantenimientoPorTipo = mantenimientos.stream()
                    .sorted(Comparator.comparing(Mantenimiento::getKmVigencia))
                    .collect(Collectors.groupingBy(doc -> doc.getTipoMantenimiento().getNombre()));

            String htmlContent = generateHtmlFromObjects(mantenimientoPorTipo);
            excelServicio.exportHtmlToExcelMantenimiento(htmlContent, response);

        }

    }

    @PostMapping("/exportarHistorialAcoplado")
    public String exportarHistorialAcoplado(@RequestParam Long idAcoplado, @RequestParam(required = false) Long tipo, ModelMap modelo) {

        List<Mantenimiento> mantenimientos = new ArrayList();
        
        if(tipo == null){
            
            mantenimientos = mantenimientoServicio.buscarHistorialAcoplado(idAcoplado);
            
        } else {
            
            mantenimientos = mantenimientoServicio.buscarHistorialAcopladoTipoMantenimiento(idAcoplado, tipo);
            
        }

        Map<String, List<Mantenimiento>> mantenimientoPorTipo = mantenimientos.stream()
                .sorted(Comparator.comparing(Mantenimiento::getFecha).reversed())
                .collect(Collectors.groupingBy(doc -> doc.getTipoMantenimiento().getNombre()));

        modelo.put("mantenimientos", mantenimientoPorTipo);
        modelo.put("acoplado", acopladoServicio.buscarAcoplado(idAcoplado));
        modelo.put("idAcoplado", idAcoplado);

        return "mantenimiento_exportarHistorialAcoplado.html";

    }

    @PostMapping("/exportaHistorialAcoplado")
    public void exportaHistorialAcoplado(@RequestParam Long idAcoplado, @RequestParam(required = false) Long tipo, HttpSession session, HttpServletResponse response) throws IOException, ParseException {

        List<Mantenimiento> mantenimientos = new ArrayList();
        
        if(tipo == null){
            
            mantenimientos = mantenimientoServicio.buscarHistorialAcoplado(idAcoplado);
            
        } else {
            
            mantenimientos = mantenimientoServicio.buscarHistorialAcopladoTipoMantenimiento(idAcoplado, tipo);
            
        }

        Map<String, List<Mantenimiento>> mantenimientoPorTipo = mantenimientos.stream()
                .sorted(Comparator.comparing(Mantenimiento::getFecha).reversed())
                .collect(Collectors.groupingBy(doc -> doc.getTipoMantenimiento().getNombre()));

        Acoplado acoplado = acopladoServicio.buscarAcoplado(idAcoplado);
        Camion camion = null;

        String htmlContent = generateHtmlFromObjectsHistorial(mantenimientoPorTipo);
        excelServicio.exportHtmlToExcelMantenimientoHistorial(htmlContent, response, camion, acoplado);

    }

    @PostMapping("/exportaVencimiento")
    public void exportaVencimiento(@RequestParam Integer km, @RequestParam(required = false) Long idCamion, @RequestParam(required = false) Long idAcoplado,
            HttpSession session, HttpServletResponse response) throws IOException, ParseException {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");
        List<Mantenimiento> mantenimientos = new ArrayList<>();

        if((idCamion != null && idCamion == 0L) || (idAcoplado != null && idAcoplado == 0L)){
            
            mantenimientos = mantenimientoServicio.obtenerMantenimientosPorVencerFiltro(logueado.getIdOrg(), km, idCamion, idAcoplado);
            
        } else if (idCamion == null && idAcoplado == null) {

            mantenimientos = mantenimientoServicio.obtenerMantenimientosPorVencer(logueado.getIdOrg(), km);

        } else if (idCamion != null || idAcoplado != null) {

            if (idCamion != null) {

                List<Mantenimiento> mantenimientosC = mantenimientoServicio.obtenerMantenimientosCamionPorVencer(idCamion, km);
                mantenimientos.addAll(mantenimientosC);

            }

            if (idAcoplado != null) {

                List<Mantenimiento> mantenimientosA = mantenimientoServicio.obtenerMantenimientosAcopladoPorVencer(idAcoplado, km);
                mantenimientos.addAll(mantenimientosA);

            }

        }

        Map<String, List<Mantenimiento>> mantenimientoPorTipo = mantenimientos.stream()
                .sorted(Comparator.comparing(Mantenimiento::getKmVigencia))
                .collect(Collectors.groupingBy(doc -> doc.getTipoMantenimiento().getNombre()));

        String htmlContent = generateHtmlFromObjects(mantenimientoPorTipo);
        excelServicio.exportHtmlToExcelMantenimiento(htmlContent, response);
    }

    private String generateHtmlFromObjects(Map<String, List<Mantenimiento>> objects) {
        StringBuilder sb = new StringBuilder();
        sb.append("<table>");
        sb.append("<thead><tr>"
                + "<th>Vehículo</th>"
                + "<th>Tipo Mantenimiento</th>"
                + "<th>Fecha</th>"
                + "<th>Km Mantenimiento</th>"
                + "<th>Km Próximo</th>"
                + "<th>Km Actual</th>"
                + "<th>Km Vigencia</th>"
                + "<th>Estado</th>"
                + "<th>Observación</th>"
                + "</tr></thead>");
        sb.append("<tbody>");
        for (Map.Entry<String, List<Mantenimiento>> entry : objects.entrySet()) {
            List<Mantenimiento> mantenimientos = entry.getValue(); // lista de valores
            for (Mantenimiento mantenimiento : mantenimientos) {
                String dominio = "";
                if (mantenimiento.getAcoplado() != null) {
                    dominio = mantenimiento.getAcoplado().getDominio() + ' ' + mantenimiento.getAcoplado().getMarca() + ' ' + mantenimiento.getAcoplado().getModelo();
                } else if (mantenimiento.getCamion() != null) {
                    dominio = mantenimiento.getCamion().getDominio() + ' ' + mantenimiento.getCamion().getMarca() + ' ' + mantenimiento.getCamion().getModelo();
                }
                sb.append("<tr><td>").append(dominio).append("</td>"
                        + "<td>").append(mantenimiento.getTipoMantenimiento().getNombre()).append("</td>"
                        + "<td>").append(mantenimiento.getFecha()).append("</td>"
                        + "<td>").append(mantenimiento.getKm()).append("</td>"
                        + "<td>").append(mantenimiento.getKmProximo()).append("</td>"
                        + "<td>").append(mantenimiento.getKmActual()).append("</td>"
                        + "<td>").append(mantenimiento.getKmVigencia()).append("</td>"
                        + "<td>").append(mantenimiento.getEstado()).append("</td>"
                        + "<td>").append(mantenimiento.getObservacion()).append("</td>"
                        + "</tr>");
            }
        }
        sb.append("</tbody></table>");
        return sb.toString();
    }

    private String generateHtmlFromObjectsHistorial(Map<String, List<Mantenimiento>> objects) {
        StringBuilder sb = new StringBuilder();
        sb.append("<table>");
        sb.append("<thead><tr>"
                + "<th>Tipo Mantenimiento</th>"
                + "<th>Fecha</th>"
                + "<th>Km Mantenimiento</th>"
                + "<th>Fecha Actualizado</th>"
                + "<th>Km Actualizado</th>"
                + "<th>Km Recorrido</th>"
                + "<th>Observación</th>"
                + "</tr></thead>");
        sb.append("<tbody>");
        for (Map.Entry<String, List<Mantenimiento>> entry : objects.entrySet()) {
            List<Mantenimiento> mantenimientos = entry.getValue(); // lista de valores
            for (Mantenimiento mantenimiento : mantenimientos) {
                sb.append("<tr><td>").append(mantenimiento.getTipoMantenimiento().getNombre()).append("</td>"
                        + "<td>").append(mantenimiento.getFecha()).append("</td>"
                        + "<td>").append(mantenimiento.getKm()).append("</td>"
                        + "<td>").append(mantenimiento.getFechaActualizado()).append("</td>"
                        + "<td>").append(mantenimiento.getKmActual()).append("</td>"
                        + "<td>").append(mantenimiento.getKmVigencia()).append("</td>"
                        + "<td>").append(mantenimiento.getObservacion()).append("</td>"
                        + "</tr>");
            }
        }
        sb.append("</tbody></table>");
        return sb.toString();
    }

    @GetMapping("/imprimirVencimiento")
    public String imprimirVencimiento(@RequestParam Integer km, @RequestParam(required = false) Long idCamion,
            @RequestParam(required = false) Long idAcoplado, ModelMap modelo, HttpSession session) {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");
        List<Mantenimiento> mantenimientos = new ArrayList<>();
        
        if((idCamion != null && idCamion == 0L) || (idAcoplado != null && idAcoplado == 0L)){
            
            mantenimientos = mantenimientoServicio.obtenerMantenimientosPorVencerFiltro(logueado.getIdOrg(), km, idCamion, idAcoplado);
            
        } else if (idCamion == null && idAcoplado == null) {

            mantenimientos = mantenimientoServicio.obtenerMantenimientosPorVencer(logueado.getIdOrg(), km);

        } else if (idCamion != null || idAcoplado != null) {

            if (idCamion != null) {

                List<Mantenimiento> mantenimientosC = mantenimientoServicio.obtenerMantenimientosCamionPorVencer(idCamion, km);
                mantenimientos.addAll(mantenimientosC);

            }

            if (idAcoplado != null) {

                List<Mantenimiento> mantenimientosA = mantenimientoServicio.obtenerMantenimientosAcopladoPorVencer(idAcoplado, km);
                mantenimientos.addAll(mantenimientosA);

            }

        }

        Map<String, List<Mantenimiento>> mantenimientoPorTipo = mantenimientos.stream()
                .sorted(Comparator.comparing(Mantenimiento::getKmVigencia))
                .collect(Collectors.groupingBy(doc -> doc.getTipoMantenimiento().getNombre()));

        modelo.put("mantenimientos", mantenimientoPorTipo);
        modelo.put("km", km);

        return "mantenimiento_imprimirVencimiento.html";
        
    }

    @GetMapping("/imprimirCamiones")
    public String imprimirCamiones(@RequestParam(required = false) Long idCamion, @RequestParam(required = false) Long idTipo,
            @RequestParam String clase, ModelMap modelo, HttpSession session) {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");
        List<Mantenimiento> mantenimientos = new ArrayList();

        if (idCamion != null && idTipo == null && clase.equalsIgnoreCase("PREVENTIVO")) {

            mantenimientos = mantenimientoServicio.buscarMantenimientoVigenteIdCamionPreventivo(idCamion);

            Map<String, List<Mantenimiento>> mantenimientoPorTipo = mantenimientos.stream()
                    .sorted(Comparator.comparing(Mantenimiento::getKmVigencia))
                    .collect(Collectors.groupingBy(doc -> doc.getTipoMantenimiento().getNombre()));

            modelo.put("mantenimientos", mantenimientoPorTipo);
            modelo.put("camion", camionServicio.buscarCamion(idCamion));

        } else if (idCamion != null && idTipo == null && clase.equalsIgnoreCase("TODOS")) {

            mantenimientos = mantenimientoServicio.buscarMantenimientoVigenteIdCamion(idCamion);

            Map<String, List<Mantenimiento>> mantenimientoPorTipo = mantenimientos.stream()
                    .sorted(Comparator.comparing(Mantenimiento::getKmVigencia))
                    .collect(Collectors.groupingBy(doc -> doc.getTipoMantenimiento().getNombre()));

            modelo.put("mantenimientos", mantenimientoPorTipo);
            modelo.put("camion", camionServicio.buscarCamion(idCamion));

        } else if (idCamion != null && idTipo == null && clase.equalsIgnoreCase("CORRECTIVO")) {

            mantenimientos = mantenimientoServicio.buscarMantenimientoVigenteIdCamionCorrectivo(idCamion);

            Map<String, List<Mantenimiento>> mantenimientoPorTipo = mantenimientos.stream()
                    .sorted(Comparator.comparing(Mantenimiento::getKmVigencia))
                    .collect(Collectors.groupingBy(doc -> doc.getTipoMantenimiento().getNombre()));

            modelo.put("mantenimientos", mantenimientoPorTipo);
            modelo.put("camion", camionServicio.buscarCamion(idCamion));

        } else if (idCamion == null && idTipo != null && clase.equalsIgnoreCase("PREVENTIVO")) {

            mantenimientos = mantenimientoServicio.buscarMantenimientosVigenteCamionesPorTipoPreventivo(logueado.getIdOrg(), idTipo);

            Map<String, List<Mantenimiento>> mantenimientoPorTipo = mantenimientos.stream()
                    .sorted(Comparator.comparing(Mantenimiento::getKmVigencia))
                    .collect(Collectors.groupingBy(doc -> doc.getTipoMantenimiento().getNombre()));

            modelo.put("mantenimientos", mantenimientoPorTipo);

        } else if (idCamion == null && idTipo != null && clase.equalsIgnoreCase("CORRECTIVO")) {

            mantenimientos = mantenimientoServicio.buscarMantenimientosVigenteCamionesPorTipoCorrectivo(logueado.getIdOrg(), idTipo);

            Map<String, List<Mantenimiento>> mantenimientoPorTipo = mantenimientos.stream()
                    .sorted(Comparator.comparing(Mantenimiento::getKmVigencia))
                    .collect(Collectors.groupingBy(doc -> doc.getTipoMantenimiento().getNombre()));

            modelo.put("mantenimientos", mantenimientoPorTipo);

        } else if (idCamion == null && idTipo != null && clase.equalsIgnoreCase("TODOS")) {

            mantenimientos = mantenimientoServicio.buscarMantenimientosVigenteCamionesPorTipo(logueado.getIdOrg(), idTipo);

            Map<String, List<Mantenimiento>> mantenimientoPorTipo = mantenimientos.stream()
                    .sorted(Comparator.comparing(Mantenimiento::getKmVigencia))
                    .collect(Collectors.groupingBy(doc -> doc.getTipoMantenimiento().getNombre()));

            modelo.put("mantenimientos", mantenimientoPorTipo);

        } else if (idCamion != null && idTipo != null && clase.equalsIgnoreCase("PREVENTIVO")) {

            mantenimientos = mantenimientoServicio.buscarMantenimientosVigenteCamionPorTipoPreventivo(idCamion, idTipo);

            Map<String, List<Mantenimiento>> mantenimientoPorTipo = mantenimientos.stream()
                    .sorted(Comparator.comparing(Mantenimiento::getKmVigencia))
                    .collect(Collectors.groupingBy(doc -> doc.getTipoMantenimiento().getNombre()));

            modelo.put("mantenimientos", mantenimientoPorTipo);

        } else if (idCamion != null && idTipo != null && clase.equalsIgnoreCase("CORRECTIVO")) {

            mantenimientos = mantenimientoServicio.buscarMantenimientosVigenteCamionPorTipoCorrectivo(idCamion, idTipo);

            Map<String, List<Mantenimiento>> mantenimientoPorTipo = mantenimientos.stream()
                    .sorted(Comparator.comparing(Mantenimiento::getKmVigencia))
                    .collect(Collectors.groupingBy(doc -> doc.getTipoMantenimiento().getNombre()));

            modelo.put("mantenimientos", mantenimientoPorTipo);
            modelo.put("camion", camionServicio.buscarCamion(idCamion));

        } else if (idCamion != null && idTipo != null && clase.equalsIgnoreCase("TODOS")) {

            mantenimientos = mantenimientoServicio.buscarMantenimientosVigenteCamionPorTipo(idCamion, idTipo);

            Map<String, List<Mantenimiento>> mantenimientoPorTipo = mantenimientos.stream()
                    .sorted(Comparator.comparing(Mantenimiento::getKmVigencia))
                    .collect(Collectors.groupingBy(doc -> doc.getTipoMantenimiento().getNombre()));

            modelo.put("mantenimientos", mantenimientoPorTipo);
            modelo.put("camion", camionServicio.buscarCamion(idCamion));

        } else if (idCamion == null && idTipo == null && clase.equalsIgnoreCase("TODOS")) {

            mantenimientos = mantenimientoServicio.buscarMantenimientosVigenteCamiones(logueado.getIdOrg());

            Map<String, List<Mantenimiento>> mantenimientoPorTipo = mantenimientos.stream()
                    .sorted(Comparator.comparing(Mantenimiento::getKmVigencia))
                    .collect(Collectors.groupingBy(doc -> doc.getTipoMantenimiento().getNombre()));

            modelo.put("mantenimientos", mantenimientoPorTipo);

        } else if (idCamion == null && idTipo == null && clase.equalsIgnoreCase("PREVENTIVO")) {

            mantenimientos = mantenimientoServicio.buscarMantenimientosVigenteCamionesPreventivo(logueado.getIdOrg());

            Map<String, List<Mantenimiento>> mantenimientoPorTipo = mantenimientos.stream()
                    .sorted(Comparator.comparing(Mantenimiento::getKmVigencia))
                    .collect(Collectors.groupingBy(doc -> doc.getTipoMantenimiento().getNombre()));

            modelo.put("mantenimientos", mantenimientoPorTipo);

        } else if (idCamion == null && idTipo == null && clase.equalsIgnoreCase("CORRECTIVO")) {

            mantenimientos = mantenimientoServicio.buscarMantenimientosVigenteCamionesCorrectivo(logueado.getIdOrg());

            Map<String, List<Mantenimiento>> mantenimientoPorTipo = mantenimientos.stream()
                    .sorted(Comparator.comparing(Mantenimiento::getKmVigencia))
                    .collect(Collectors.groupingBy(doc -> doc.getTipoMantenimiento().getNombre()));

            modelo.put("mantenimientos", mantenimientoPorTipo);

        }

        modelo.put("idCamion", idCamion);
        modelo.put("idTipo", idTipo);
        modelo.put("clase", clase);

        return "mantenimiento_imprimirCamiones.html";
    }

    @GetMapping("/imprimirHistorialCamion")
    public String imprimirHistorialCamion(@RequestParam Long idCamion, @RequestParam(required = false) Long tipo, ModelMap modelo, HttpSession session) {

        List<Mantenimiento> mantenimientos = new ArrayList();
        
        if(tipo == null){
            
            mantenimientos = mantenimientoServicio.buscarHistorialCamion(idCamion);
            
        } else {
            
            mantenimientos = mantenimientoServicio.buscarHistorialCamionTipoMantenimiento(idCamion, tipo);
            
        }

        Map<String, List<Mantenimiento>> mantenimientoPorTipo = mantenimientos.stream()
                .sorted(Comparator.comparing(Mantenimiento::getFecha).reversed())
                .collect(Collectors.groupingBy(doc -> doc.getTipoMantenimiento().getNombre()));

        modelo.put("mantenimientos", mantenimientoPorTipo);
        modelo.put("camion", camionServicio.buscarCamion(idCamion));

        return "mantenimiento_imprimirHistorialCamion.html";
    }

    @GetMapping("/imprimirAcoplados")
    public String imprimirAcoplados(@RequestParam(required = false) Long idAcoplado, @RequestParam(required = false) Long idTipo,
            @RequestParam String clase, ModelMap modelo, HttpSession session) {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");
        List<Mantenimiento> mantenimientos = new ArrayList();

        if (idAcoplado != null && idTipo == null && clase.equalsIgnoreCase("PREVENTIVO")) {

            mantenimientos = mantenimientoServicio.buscarMantenimientoVigenteIdAcopladoPreventivo(idAcoplado);

            Map<String, List<Mantenimiento>> mantenimientoPorTipo = mantenimientos.stream()
                    .sorted(Comparator.comparing(Mantenimiento::getKmVigencia))
                    .collect(Collectors.groupingBy(doc -> doc.getTipoMantenimiento().getNombre()));

            modelo.put("mantenimientos", mantenimientoPorTipo);
            modelo.put("acoplado", acopladoServicio.buscarAcoplado(idAcoplado));

        } else if (idAcoplado != null && idTipo == null && clase.equalsIgnoreCase("CORRECTIVO")) {

            mantenimientos = mantenimientoServicio.buscarMantenimientoVigenteIdAcopladoCorrectivo(idAcoplado);

            Map<String, List<Mantenimiento>> mantenimientoPorTipo = mantenimientos.stream()
                    .sorted(Comparator.comparing(Mantenimiento::getKmVigencia))
                    .collect(Collectors.groupingBy(doc -> doc.getTipoMantenimiento().getNombre()));

            modelo.put("mantenimientos", mantenimientoPorTipo);
            modelo.put("acoplado", acopladoServicio.buscarAcoplado(idAcoplado));

        } else if (idAcoplado != null && idTipo == null && clase.equalsIgnoreCase("TODOS")) {

            mantenimientos = mantenimientoServicio.buscarMantenimientoVigenteIdAcoplado(idAcoplado);

            Map<String, List<Mantenimiento>> mantenimientoPorTipo = mantenimientos.stream()
                    .sorted(Comparator.comparing(Mantenimiento::getKmVigencia))
                    .collect(Collectors.groupingBy(doc -> doc.getTipoMantenimiento().getNombre()));

            modelo.put("mantenimientos", mantenimientoPorTipo);
            modelo.put("acoplado", acopladoServicio.buscarAcoplado(idAcoplado));

        } else if (idAcoplado == null && idTipo != null && clase.equalsIgnoreCase("PREVENTIVO")) {

            mantenimientos = mantenimientoServicio.buscarMantenimientosVigenteAcopladosPorTipoPreventivo(logueado.getIdOrg(), idTipo);

            Map<String, List<Mantenimiento>> mantenimientoPorTipo = mantenimientos.stream()
                    .sorted(Comparator.comparing(Mantenimiento::getKmVigencia))
                    .collect(Collectors.groupingBy(doc -> doc.getTipoMantenimiento().getNombre()));

            modelo.put("mantenimientos", mantenimientoPorTipo);

        } else if (idAcoplado == null && idTipo != null && clase.equalsIgnoreCase("CORRECTIVO")) {

            mantenimientos = mantenimientoServicio.buscarMantenimientosVigenteAcopladosPorTipoCorrectivo(logueado.getIdOrg(), idTipo);

            Map<String, List<Mantenimiento>> mantenimientoPorTipo = mantenimientos.stream()
                    .sorted(Comparator.comparing(Mantenimiento::getKmVigencia))
                    .collect(Collectors.groupingBy(doc -> doc.getTipoMantenimiento().getNombre()));

            modelo.put("mantenimientos", mantenimientoPorTipo);

        } else if (idAcoplado == null && idTipo != null && clase.equalsIgnoreCase("TODOS")) {

            mantenimientos = mantenimientoServicio.buscarMantenimientosVigenteAcopladosPorTipo(logueado.getIdOrg(), idTipo);

            Map<String, List<Mantenimiento>> mantenimientoPorTipo = mantenimientos.stream()
                    .sorted(Comparator.comparing(Mantenimiento::getKmVigencia))
                    .collect(Collectors.groupingBy(doc -> doc.getTipoMantenimiento().getNombre()));

            modelo.put("mantenimientos", mantenimientoPorTipo);

        } else if (idAcoplado != null && idTipo != null && clase.equalsIgnoreCase("PREVENTIVO")) {

            mantenimientos = mantenimientoServicio.buscarMantenimientosVigenteAcopladoPorTipoPreventivo(idAcoplado, idTipo);

            Map<String, List<Mantenimiento>> mantenimientoPorTipo = mantenimientos.stream()
                    .sorted(Comparator.comparing(Mantenimiento::getKmVigencia))
                    .collect(Collectors.groupingBy(doc -> doc.getTipoMantenimiento().getNombre()));

            modelo.put("mantenimientos", mantenimientoPorTipo);
            modelo.put("acoplado", acopladoServicio.buscarAcoplado(idAcoplado));

        } else if (idAcoplado != null && idTipo != null && clase.equalsIgnoreCase("CORRECTIVO")) {

            mantenimientos = mantenimientoServicio.buscarMantenimientosVigenteAcopladoPorTipoCorrectivo(idAcoplado, idTipo);

            Map<String, List<Mantenimiento>> mantenimientoPorTipo = mantenimientos.stream()
                    .sorted(Comparator.comparing(Mantenimiento::getKmVigencia))
                    .collect(Collectors.groupingBy(doc -> doc.getTipoMantenimiento().getNombre()));

            modelo.put("mantenimientos", mantenimientoPorTipo);
            modelo.put("acoplado", acopladoServicio.buscarAcoplado(idAcoplado));

        } else if (idAcoplado != null && idTipo != null && clase.equalsIgnoreCase("TODOS")) {

            mantenimientos = mantenimientoServicio.buscarMantenimientosVigenteAcopladoPorTipo(idAcoplado, idTipo);

            Map<String, List<Mantenimiento>> mantenimientoPorTipo = mantenimientos.stream()
                    .sorted(Comparator.comparing(Mantenimiento::getKmVigencia))
                    .collect(Collectors.groupingBy(doc -> doc.getTipoMantenimiento().getNombre()));

            modelo.put("mantenimientos", mantenimientoPorTipo);
            modelo.put("acoplado", acopladoServicio.buscarAcoplado(idAcoplado));

        } else if (idAcoplado == null && idTipo == null && clase.equalsIgnoreCase("TODOS")) {

            mantenimientos = mantenimientoServicio.buscarMantenimientosVigenteAcoplados(logueado.getIdOrg());

            Map<String, List<Mantenimiento>> mantenimientoPorTipo = mantenimientos.stream()
                    .sorted(Comparator.comparing(Mantenimiento::getKmVigencia))
                    .collect(Collectors.groupingBy(doc -> doc.getTipoMantenimiento().getNombre()));

            modelo.put("mantenimientos", mantenimientoPorTipo);

        } else if (idAcoplado == null && idTipo == null && clase.equalsIgnoreCase("PREVENTIVO")) {

            mantenimientos = mantenimientoServicio.buscarMantenimientosVigenteAcopladosPreventivo(logueado.getIdOrg());

            Map<String, List<Mantenimiento>> mantenimientoPorTipo = mantenimientos.stream()
                    .sorted(Comparator.comparing(Mantenimiento::getKmVigencia))
                    .collect(Collectors.groupingBy(doc -> doc.getTipoMantenimiento().getNombre()));

            modelo.put("mantenimientos", mantenimientoPorTipo);

        } else if (idAcoplado == null && idTipo == null && clase.equalsIgnoreCase("CORRECTIVO")) {

            mantenimientos = mantenimientoServicio.buscarMantenimientosVigenteAcopladosCorrectivo(logueado.getIdOrg());

            Map<String, List<Mantenimiento>> mantenimientoPorTipo = mantenimientos.stream()
                    .sorted(Comparator.comparing(Mantenimiento::getKmVigencia))
                    .collect(Collectors.groupingBy(doc -> doc.getTipoMantenimiento().getNombre()));

            modelo.put("mantenimientos", mantenimientoPorTipo);

        }

        modelo.put("idAcoplado", idAcoplado);
        modelo.put("idTipo", idTipo);
        modelo.put("clase", clase);

        return "mantenimiento_imprimirAcoplados.html";
    }

    @GetMapping("/imprimirHistorialAcoplado")
    public String imprimirHistorialAcoplado(@RequestParam Long idAcoplado, @RequestParam(required = false) Long tipo, ModelMap modelo, HttpSession session) {

        List<Mantenimiento> mantenimientos = new ArrayList();
        
        if(tipo == null){
            
            mantenimientos = mantenimientoServicio.buscarHistorialAcoplado(idAcoplado);
            
        } else {
            
            mantenimientos = mantenimientoServicio.buscarHistorialAcopladoTipoMantenimiento(idAcoplado, tipo);
            
        }

        Map<String, List<Mantenimiento>> mantenimientoPorTipo = mantenimientos.stream()
                .sorted(Comparator.comparing(Mantenimiento::getFecha).reversed())
                .collect(Collectors.groupingBy(doc -> doc.getTipoMantenimiento().getNombre()));

        modelo.put("mantenimientos", mantenimientoPorTipo);
        modelo.put("acoplado", acopladoServicio.buscarAcoplado(idAcoplado));

        return "mantenimiento_imprimirHistorialAcoplado.html";
    }

    public Date convertirFecha(String fecha) throws ParseException {
        SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd");
        return formato.parse(fecha);
    }

    public static Date obtenerFechaFija() {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            return sdf.parse("01-01-2024");
        } catch (ParseException e) {
            e.printStackTrace();
            return null; // o lanzar una excepción personalizada
        }
    }

}
