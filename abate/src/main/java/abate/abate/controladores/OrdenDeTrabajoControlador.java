package abate.abate.controladores;

import abate.abate.dto.MantenimientoDTO;
import abate.abate.entidades.Acoplado;
import abate.abate.entidades.Camion;
import abate.abate.entidades.Mantenimiento;
import abate.abate.entidades.OrdenDeTrabajo;
import abate.abate.entidades.TipoMantenimiento;
import abate.abate.entidades.Usuario;
import abate.abate.servicios.AcopladoServicio;
import abate.abate.servicios.CamionServicio;
import abate.abate.servicios.ChoferServicio;
import abate.abate.servicios.CombustibleServicio;
import abate.abate.servicios.MantenimientoServicio;
import abate.abate.servicios.OrdenDeTrabajoServicio;
import abate.abate.servicios.TipoMantenimientoServicio;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
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
@RequestMapping("/ordenDeTrabajo")
@PreAuthorize("hasAnyRole('ROLE_ADMIN')")
public class OrdenDeTrabajoControlador {

    @Autowired
    private MantenimientoServicio mantenimientoServicio;
    @Autowired
    private TipoMantenimientoServicio tipoMantenimientoServicio;
    @Autowired
    private CamionServicio camionServicio;
    @Autowired
    private AcopladoServicio acopladoServicio;
    @Autowired
    private ChoferServicio choferServicio;
    @Autowired
    private OrdenDeTrabajoServicio ordenServicio;
    @Autowired
    private CombustibleServicio combustibleServicio;

    @GetMapping("/registrar")
    public String registrar(ModelMap modelo, HttpSession session) {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        modelo.addAttribute("camiones", camionServicio.buscarCamionesHabAsc(logueado.getIdOrg()));
        modelo.put("camion", null);

        return "ordenDeTrabajo_registrar.html";
    }

    @GetMapping("/registrar1")
    public String registrar1(@RequestParam(required = false) Long idCamion,
            @RequestParam(required = false) Long idAcoplado, ModelMap modelo, HttpSession session) {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        if (idCamion != null) {

            Camion camion = camionServicio.buscarCamion(idCamion);

            OrdenDeTrabajo orden = ordenServicio.buscarOrdenAbiertaCamion(idCamion);

            if (orden != null) {

                return "redirect:/ordenDeTrabajo/modificar?id=" + orden.getId();

            } else {

                modelo.put("camion", camion);

            }

            if (camion.getAcoplado() != null && idAcoplado == null) {

                Acoplado acoplado = camion.getAcoplado();

                OrdenDeTrabajo ordenA = ordenServicio.buscarOrdenAbiertaAcoplado(acoplado.getId());

                if (ordenA != null) {

                    return "redirect:/ordenDeTrabajo/modificar?id=" + ordenA.getId();

                } else {

                    modelo.put("acoplado", acoplado);

                }

            } else {

                modelo.put("acoplado", null);

            }
        }

        if (idAcoplado != null) {

            Acoplado acoplado = acopladoServicio.buscarAcoplado(idAcoplado);

            OrdenDeTrabajo ordenA = ordenServicio.buscarOrdenAbiertaAcoplado(acoplado.getId());

            if (ordenA != null) {

                return "redirect:/ordenDeTrabajo/modificar?id=" + ordenA.getId();

            } else {

                modelo.put("acoplado", acoplado);

            }

        }

        modelo.put("idOrg", logueado.getIdOrg());
        modelo.addAttribute("choferes", choferServicio.bucarChoferesHabNombreAsc(logueado.getIdOrg()));
        modelo.addAttribute("camiones", camionServicio.buscarCamionesHabAsc(logueado.getIdOrg()));
        modelo.addAttribute("acoplados", acopladoServicio.buscarAcopladosHabAsc(logueado.getIdOrg()));
        modelo.addAttribute("tiposCamion", tipoMantenimientoServicio.buscarTiposOrdenAplicaA(logueado.getIdOrg(), TipoMantenimiento.AplicaA.CAMION));
        modelo.addAttribute("tiposAcoplado", tipoMantenimientoServicio.buscarTiposOrdenAplicaA(logueado.getIdOrg(), TipoMantenimiento.AplicaA.ACOPLADO));

        return "ordenDeTrabajo_registrar.html";
    }

    @PostMapping("/registro")
    public String registro(@RequestParam String fecha, @RequestParam("mantenimientosCamionJson") String mantenimientosCamionJson,
            @RequestParam("mantenimientosAcopladoJson") String mantenimientosAcopladoJson,
            @RequestParam(required = false) Long idCamion, @RequestParam(required = false) Long idAcoplado, @RequestParam(required = false) String lugar,
            @RequestParam(required = false) String proveedor, @RequestParam String responsable, @RequestParam Long idChofer,
            @RequestParam(required = false) String observacion, ModelMap modelo, RedirectAttributes redirectAttributes,
            HttpSession session) throws JsonProcessingException, ParseException {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        if (mantenimientosCamionJson == null || mantenimientosCamionJson.trim().isEmpty() || mantenimientosCamionJson.trim().equals("[]")) {
            idCamion = null;
        }

        if (mantenimientosAcopladoJson == null || mantenimientosAcopladoJson.trim().isEmpty() || mantenimientosAcopladoJson.trim().equals("[]")) {
            idAcoplado = null;
        }

        OrdenDeTrabajo orden = ordenServicio.crearOrden(fecha, idCamion, idAcoplado, lugar, proveedor, responsable, idChofer, observacion, logueado);

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
            m.setObservacion(dto.getObservacion().toUpperCase());
            mantenimientosAcoplado.add(m);
        }

        List<Mantenimiento> mantenimientosTotales = new ArrayList<>();
        mantenimientosTotales.addAll(mantenimientosCamion);
        mantenimientosTotales.addAll(mantenimientosAcoplado);

        for (Mantenimiento m : mantenimientosTotales) {
            m.setEstado(Mantenimiento.Estado.PENDIENTE);
            m.setIdOrg(logueado.getIdOrg());
            m.setUsuario(logueado);
            m.setFecha(fechaOt);
            m.setOrdenDeTrabajo(orden);
            if (m.getAplicaA().equals(TipoMantenimiento.AplicaA.CAMION)) {
                Camion camion = camionServicio.buscarCamion(idCamion);
                m.setCamion(camion);
            } else if (m.getAplicaA().equals(TipoMantenimiento.AplicaA.ACOPLADO)) {
                Acoplado acoplado = acopladoServicio.buscarAcoplado(idAcoplado);
                m.setAcoplado(acoplado);
            }

            Mantenimiento mantenimientoVigente = null;

            mantenimientoServicio.crearMantenimiento(m, mantenimientoVigente);

        }

        redirectAttributes.addFlashAttribute("mantenimientosC", mantenimientosCamionDTO);
        redirectAttributes.addFlashAttribute("mantenimientosA", mantenimientosAcopladoDTO);

        return "redirect:/ordenDeTrabajo/registrado";

    }

    @GetMapping("/registrado")
    public String registrado(@ModelAttribute("mantenimientosC") List<Mantenimiento> mantenimientosC,
            @ModelAttribute("mantenimientosA") List<Mantenimiento> mantenimientosA, ModelMap modelo, HttpSession session) {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        Long id = ordenServicio.buscarUltimo(logueado.getIdOrg());

        modelo.put("exito", "La carga de Orden de Trabajo se ha REGISTRADO con éxito");
        modelo.addAttribute("mantenimientosC", mantenimientosC);
        modelo.addAttribute("mantenimientosA", mantenimientosA);
        modelo.addAttribute("orden", ordenServicio.buscarOrden(id));

        return "ordenDeTrabajo_mostrar.html";

    }

    @GetMapping("/modificar")
    public String modificar(@RequestParam Long id, ModelMap modelo) {

        OrdenDeTrabajo orden = ordenServicio.buscarOrden(id);

        if (orden.getEstado() == OrdenDeTrabajo.Estado.CERRADA) {

            modelo.addAttribute("orden", orden);
            modelo.addAttribute("mantenimientos", orden.getMantenimientos());

            return "ordenDeTrabajo_mostrarCerrada.html";
        }

        if (orden.getCamion() != null) {
            int kmCamion = combustibleServicio.kmUltimaCarga(orden.getCamion());
            modelo.put("kmCamion", kmCamion);
            modelo.put("kmVigenciaC", 10000);
        }

        if (orden.getAcoplado() != null) {
            int kmAcoplado = combustibleServicio.kmAcoplado(orden.getAcoplado(), obtenerFechaFija());
            modelo.put("kmAcoplado", kmAcoplado);
            modelo.put("kmVigenciaA", 10000);
        } else {
            modelo.put("kmAcoplado", 10000);
            modelo.put("kmVigenciaA", 10000);
        }

        modelo.put("orden", orden);
        modelo.addAttribute("mantenimientos", orden.getMantenimientos());
        modelo.addAttribute("choferes", choferServicio.bucarChoferesHabNombreAsc(orden.getIdOrg()));
        modelo.addAttribute("tiposCamion", tipoMantenimientoServicio.buscarTiposOrdenAplicaA(orden.getIdOrg(), TipoMantenimiento.AplicaA.CAMION));
        modelo.addAttribute("tiposAcoplado", tipoMantenimientoServicio.buscarTiposOrdenAplicaA(orden.getIdOrg(), TipoMantenimiento.AplicaA.ACOPLADO));

        return "ordenDeTrabajo_modificar.html";

    }

    @PostMapping("/modifica")
    public String completarOT(@RequestParam Long id, @RequestParam(required = false) List<Long> ejecutados,
            @RequestParam(required = false) String nuevosCamionJson, @RequestParam(required = false) String nuevosAcopladoJson,
            @RequestParam(required = false) String lugar, @RequestParam(required = false) String proveedor, @RequestParam String responsable,
            @RequestParam Long idChofer, @RequestParam String observacion, HttpSession session, HttpServletRequest request, RedirectAttributes redirectAttributes) {

        try {

            Usuario logueado = (Usuario) session.getAttribute("usuariosession");

            OrdenDeTrabajo ot = ordenServicio.buscarOrden(id);
            List<Mantenimiento> existentes = ot.getMantenimientos();

            ObjectMapper mapper = new ObjectMapper();
            List<MantenimientoDTO> mantenimientosCamionDTO = Arrays.asList(mapper.readValue(nuevosCamionJson, MantenimientoDTO[].class));
            List<MantenimientoDTO> mantenimientosAcopladoDTO = Arrays.asList(mapper.readValue(nuevosAcopladoJson, MantenimientoDTO[].class));

            for (MantenimientoDTO dto : mantenimientosCamionDTO) {
                Mantenimiento m = new Mantenimiento();
                TipoMantenimiento tipo = new TipoMantenimiento();
                tipo.setId(dto.getTipoMantenimiento());
                m.setTipoMantenimiento(tipo);
                m.setAplicaA(TipoMantenimiento.AplicaA.valueOf(dto.getAplicaA()));
                m.setObservacion(dto.getObservacion().toUpperCase());
                m.setKm(dto.getKm());
                m.setKmVigencia(dto.getKmVigencia());
                m.setUsuario(logueado);
                m.setEstado(Mantenimiento.Estado.PENDIENTE);
                m.setIdOrg(logueado.getIdOrg());
                m.setFecha(new Date());
                m.setCamion(ot.getCamion());
                m.setOrdenDeTrabajo(ot);

                Boolean ejecutado = dto.getEjecutado();
                if (ejecutado == true) {
                    m.setEstado(Mantenimiento.Estado.VIGENTE);
                    m.setKmProximo(m.getKm() + m.getKmVigencia());
                    m.setKmAlarma(m.getKm() + m.getKmVigencia() - 1000);

                    Mantenimiento existente = mantenimientoServicio.buscarExistenteMasivo(m);
                    if (existente != null) {
                        mantenimientoServicio.modificarVigenteOt(m, existente);
                    }

                }

                existentes.add(m);

                Mantenimiento mantenimientoVigente = null;

                mantenimientoServicio.crearMantenimiento(m, mantenimientoVigente);

            }

            for (MantenimientoDTO dto : mantenimientosAcopladoDTO) {
                Mantenimiento m = new Mantenimiento();
                TipoMantenimiento tipo = new TipoMantenimiento();
                tipo.setId(dto.getTipoMantenimiento());
                m.setTipoMantenimiento(tipo);
                m.setAplicaA(TipoMantenimiento.AplicaA.valueOf(dto.getAplicaA()));
                m.setObservacion(dto.getObservacion().toUpperCase());
                m.setKm(dto.getKm());
                m.setKmVigencia(dto.getKmVigencia());
                m.setUsuario(logueado);
                m.setEstado(Mantenimiento.Estado.PENDIENTE);
                m.setIdOrg(logueado.getIdOrg());
                m.setFecha(new Date());
                m.setAcoplado(ot.getAcoplado());
                m.setOrdenDeTrabajo(ot);

                Boolean ejecutado = dto.getEjecutado();
                if (ejecutado == true) {
                    m.setEstado(Mantenimiento.Estado.VIGENTE);
                    m.setKmProximo(m.getKm() + m.getKmVigencia());
                    m.setKmAlarma(m.getKm() + m.getKmVigencia() - 1000);

                    Mantenimiento existente = mantenimientoServicio.buscarExistenteMasivo(m);
                    if (existente != null) {
                        mantenimientoServicio.modificarVigenteOt(m, existente);
                    }

                }

                existentes.add(m);

                Mantenimiento mantenimientoVigente = null;

                mantenimientoServicio.crearMantenimiento(m, mantenimientoVigente);
            }

            // 2️⃣ Obtener datos enviados por el formulario (en arrays separados)
            String[] kmCam = request.getParameterValues("kmCamionExistente");
            String[] kmVigCam = request.getParameterValues("kmVigenciaExistente");
            String[] obsCam = request.getParameterValues("obsExistente");

            String[] kmAco = request.getParameterValues("kmAcopladoExistente");
            String[] kmVigAco = request.getParameterValues("kmVigenciaAExistente");
            String[] obsAco = request.getParameterValues("obsAcopladoExistente");

            // 3️⃣ Contadores independientes
            int iCam = 0;
            int iAco = 0;

            // 4️⃣ Procesar mantenimientos existentes
            for (Mantenimiento m : existentes) {

                // 🔹 CAMIÓN EXISTENTE
                if (m.getAplicaA() == TipoMantenimiento.AplicaA.CAMION) {

                    if (m.getEstado() == Mantenimiento.Estado.PENDIENTE) {

                        if (kmCam != null && iCam < kmCam.length && !kmCam[iCam].isEmpty()) {
                            m.setKm(Integer.parseInt(kmCam[iCam]));
                        }
                        if (kmVigCam != null && iCam < kmVigCam.length && !kmVigCam[iCam].isEmpty()) {
                            m.setKmVigencia(Integer.parseInt(kmVigCam[iCam]));
                        }
                        if (obsCam != null && iCam < obsCam.length) {
                            m.setObservacion(obsCam[iCam].toUpperCase());
                        }
                    }

                    iCam++;
                } // 🔸 ACOPLADO EXISTENTE
                else if (m.getAplicaA() == TipoMantenimiento.AplicaA.ACOPLADO) {

                    if (m.getEstado() == Mantenimiento.Estado.PENDIENTE) {

                        if (kmAco != null && iAco < kmAco.length && !kmAco[iAco].isEmpty()) {
                            m.setKm(Integer.parseInt(kmAco[iAco]));
                        }
                        if (kmVigAco != null && iAco < kmVigAco.length && !kmVigAco[iAco].isEmpty()) {
                            m.setKmVigencia(Integer.parseInt(kmVigAco[iAco]));
                        }
                        if (obsAco != null && iAco < obsAco.length) {
                            m.setObservacion(obsAco[iAco].toUpperCase());
                        }
                    }

                    iAco++;
                }
            }

            // 5️⃣ Marcar ejecutados (si vienen tildados)
            if (ejecutados != null) {
                for (Mantenimiento m : existentes) {
                    if (ejecutados.contains(m.getId())) {
                        m.setEstado(Mantenimiento.Estado.VIGENTE);
                        m.setFecha(new Date());
                        m.setKmProximo(m.getKm() + m.getKmVigencia());
                        m.setKmAlarma(m.getKm() + m.getKmVigencia() - 1000);
                        m.setUsuario(logueado);

                        Mantenimiento existente = mantenimientoServicio.buscarExistenteMasivo(m);
                        if (existente != null) {
                            mantenimientoServicio.modificarVigenteOt(m, existente);
                        }
                    }
                }
            }

            ot.setObservacion(observacion.toUpperCase());
            ot.setMantenimientos(existentes);
            ordenServicio.modificar(ot);

            ordenServicio.actualizarEstadoOT(ot.getId());

            return "redirect:/ordenDeTrabajo/listar?mensaje=" + "exito";

        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Error al procesar la OT.");
            return "redirect:/ordenDeTrabajo/listar?mensaje=" + "error";
        }
    }

    @GetMapping("/imprimir/{id}")
    public String imprimir(@PathVariable Long id, ModelMap modelo, HttpSession session) {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");
        OrdenDeTrabajo orden = ordenServicio.buscarOrden(id);
        modelo.addAttribute("flag", false);

        if (logueado.getLogo() != null) {

            Long idLogo = logueado.getLogo().getId();

            modelo.addAttribute("imagenUrl", "/imagen/img/bytes/" + idLogo);
            modelo.addAttribute("flag", true);

        }

        modelo.put("orden", orden);
        modelo.addAttribute("mantenimientos", orden.getMantenimientos());

        return "ordenDeTrabajo_imprimir.html";

    }

    @GetMapping("/imprimirCerrada/{id}")
    public String imprimirCerrada(@PathVariable Long id, ModelMap modelo, HttpSession session) {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");
        OrdenDeTrabajo orden = ordenServicio.buscarOrden(id);
        modelo.addAttribute("flag", false);

        if (logueado.getLogo() != null) {

            Long idLogo = logueado.getLogo().getId();

            modelo.addAttribute("imagenUrl", "/imagen/img/bytes/" + idLogo);
            modelo.addAttribute("flag", true);

        }

        modelo.put("orden", orden);
        modelo.addAttribute("mantenimientos", orden.getMantenimientos());

        return "ordenDeTrabajo_imprimirCerrada.html";

    }

    @GetMapping("/listar")
    public String listar(@RequestParam(required = false) String mensaje, ModelMap modelo, HttpSession session) {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        if (mensaje != null) {
            if (mensaje.equalsIgnoreCase("exito")) {
                modelo.put("exito", "Orden de Trabajo actualizada correctamente.");
            } else if (mensaje.equalsIgnoreCase("error")) {
                modelo.put("error", "Error al procesar la Orden de Trabajo");
            }
        }

        modelo.put("ordenes", ordenServicio.buscarOrdenAbiertaProceso(logueado.getIdOrg()));
        modelo.addAttribute("camiones", camionServicio.buscarCamionesHabAsc(logueado.getIdOrg()));
        modelo.addAttribute("acoplados", acopladoServicio.buscarAcopladosHabAsc(logueado.getIdOrg()));
        modelo.put("estado", "ABIERTA");

        return "ordenDeTrabajo_listar.html";

    }

    @PostMapping("/listarFiltro")
    public String listarFiltro(@RequestParam(required = false) Long idCamion, @RequestParam(required = false) Long idAcoplado,
            @RequestParam(required = false) String estado, ModelMap modelo, HttpSession session) {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");
        Boolean flag = false;
        List<OrdenDeTrabajo> ordenes = new ArrayList();

        if (idCamion == null && idAcoplado == null && estado.equalsIgnoreCase("TODOS")) {

            ordenes = ordenServicio.buscarOrdenes(logueado.getIdOrg());

        }
        if (idCamion != null && idAcoplado == null && estado.equalsIgnoreCase("TODOS")) {

            ordenes = ordenServicio.buscarOrdenesCamion(idCamion);

        } else if (idCamion == null && idAcoplado != null && estado.equalsIgnoreCase("TODOS")) {

            ordenes = ordenServicio.buscarOrdenesAcoplado(idAcoplado);

        } else if (idCamion != null && idAcoplado != null && estado.equalsIgnoreCase("TODOS")) {

            ordenes = ordenServicio.buscarOrdenesCamionAcoplado(idCamion, idAcoplado);

        } else if (idCamion == null && idAcoplado == null && estado.equalsIgnoreCase("ABIERTA")) {

            ordenes = ordenServicio.buscarOrdenAbiertaProceso(idCamion);

        } else if (idCamion != null && idAcoplado != null && estado.equalsIgnoreCase("ABIERTA")) {

            ordenes = ordenServicio.buscarOrdenesCamionAcopladoAbiertaProceso(idCamion, idAcoplado);

        } else if (idCamion != null && idAcoplado == null && estado.equalsIgnoreCase("ABIERTA")) {

            ordenes = ordenServicio.buscarOrdenesCamionAbiertaProceso(idCamion);

        } else if (idCamion == null && idAcoplado != null && estado.equalsIgnoreCase("ABIERTA")) {

            ordenes = ordenServicio.buscarOrdenesAcopladoAbiertaProceso(idAcoplado);

        } else if (idCamion == null && idAcoplado == null && estado.equalsIgnoreCase("CERRADA")) {

            ordenes = ordenServicio.buscarOrdenCerrada(logueado.getIdOrg());

        } else if (idCamion != null && idAcoplado != null && estado.equalsIgnoreCase("CERRADA")) {

            ordenes = ordenServicio.buscarOrdenesCamionAcopladoCerrada(idCamion, idAcoplado);

        } else if (idCamion != null && idAcoplado == null && estado.equalsIgnoreCase("CERRADA")) {

            ordenes = ordenServicio.buscarOrdenesCamionCerrada(idCamion);

        } else if (idCamion == null && idAcoplado != null && estado.equalsIgnoreCase("CERRADA")) {

            ordenes = ordenServicio.buscarOrdenesAcopladoCerrada(idAcoplado);
        }

        if (!ordenes.isEmpty()) {
            flag = true;
        }

        if (idCamion != null) {
            modelo.put("camion", camionServicio.buscarCamion(idCamion));
        }
        if (idAcoplado != null) {
            modelo.put("acoplado", acopladoServicio.buscarAcoplado(idAcoplado));
        }
        modelo.put("estado", estado);
        modelo.put("flag", flag);
        modelo.put("ordenes", ordenes);
        modelo.addAttribute("camiones", camionServicio.buscarCamionesHabAsc(logueado.getIdOrg()));
        modelo.addAttribute("acoplados", acopladoServicio.buscarAcopladosHabAsc(logueado.getIdOrg()));

        return "ordenDeTrabajo_listar.html";

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
