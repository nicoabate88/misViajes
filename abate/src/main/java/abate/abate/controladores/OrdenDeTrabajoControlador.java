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
import abate.abate.servicios.CombustibleServicio;
import abate.abate.servicios.MantenimientoServicio;
import abate.abate.servicios.OrdenDeTrabajoServicio;
import abate.abate.servicios.ProveedorServicio;
import abate.abate.servicios.TipoMantenimientoServicio;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
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
    private OrdenDeTrabajoServicio ordenServicio;
    @Autowired
    private CombustibleServicio combustibleServicio;
    @Autowired
    private ProveedorServicio proveedorServicio;

    @GetMapping("/registrar")
    public String registrar(ModelMap modelo, HttpSession session) {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        modelo.addAttribute("camiones", camionServicio.buscarCamionesHabAsc(logueado.getIdOrg()));
        modelo.put("flag", true);
        modelo.put("camion", null);

        return "ordenDeTrabajo_registrar.html";
    }

    @GetMapping("/buscarMantenimientos")
    public String buscarMantenimientos(@RequestParam(required = false) int km, @RequestParam Long idCamion, ModelMap modelo, HttpSession session) {

        OrdenDeTrabajo orden = ordenServicio.buscarOrdenAbiertaCamion(idCamion);
        Boolean pendiente = false;
        Long idOt = null;

        if (orden != null && orden.getEstado().equals(OrdenDeTrabajo.Estado.ABIERTO)) {

            return "redirect:/ordenDeTrabajo/modificar?id=" + orden.getId();

        } else if (orden != null && orden.getEstado().equals(OrdenDeTrabajo.Estado.EN_PROCESO)) {

            pendiente = true;
            idOt = orden.getId();

        }

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");
        Camion camion = camionServicio.buscarCamion(idCamion);
        Acoplado acoplado = null;

        List<Mantenimiento> listaCamion = new ArrayList();
        List<Mantenimiento> listaAcoplado = new ArrayList();

        if (orden != null && orden.getEstado().equals(OrdenDeTrabajo.Estado.EN_PROCESO)) {
            listaCamion = mantenimientoServicio.buscarPendientesCamionPorOT(orden.getId());
            for (Mantenimiento m : listaCamion) {
                m.setObservacion("Mantenimiento Pendiente OT N°" + orden.getIdOrden());
            }

            if (camion.getAcoplado() != null && orden.getAcoplado() != null) {
                if (orden.getAcoplado().getId() == camion.getAcoplado().getId()) {
                    listaAcoplado = mantenimientoServicio.buscarPendientesAcopladoPorOT(orden.getId(), orden.getAcoplado().getId());
                    for (Mantenimiento m : listaAcoplado) {
                        m.setObservacion("Mantenimiento Pendiente OT N°" + orden.getIdOrden());
                    }
                }
            }
        }

        List<Mantenimiento> vencidosCamion = mantenimientoServicio.obtenerMantenimientosCamionPorVencer(idCamion, km);

        Set<Long> tiposExistentes = listaCamion.stream()
                .map(m -> m.getTipoMantenimiento().getId())
                .collect(Collectors.toSet());

        for (Mantenimiento m : vencidosCamion) {
            Long tipoId = m.getTipoMantenimiento().getId();
            if (!tiposExistentes.contains(tipoId)) {
                m.setObservacion("Mantenimiento a vencer en próximos " + km + " km");
                listaCamion.add(m);
            }
        }

        if (camion.getAcoplado() != null) {

            acoplado = camion.getAcoplado();

            List<Mantenimiento> vencidosAcoplado = mantenimientoServicio.obtenerMantenimientosAcopladoPorVencer(acoplado.getId(), km);

            Set<Long> tiposExistente = listaAcoplado.stream()
                    .map(m -> m.getTipoMantenimiento().getId())
                    .collect(Collectors.toSet());

            for (Mantenimiento m : vencidosAcoplado) {
                Long tipoId = m.getTipoMantenimiento().getId();
                if (!tiposExistente.contains(tipoId)) {
                    m.setObservacion("Mantenimiento a vencer en próximos " + km + " km");
                    listaAcoplado.add(m);
                }
            }

            modelo.addAttribute("tiposAcoplado", tipoMantenimientoServicio.buscarTiposOrdenAplicaA(logueado.getIdOrg(), TipoMantenimiento.AplicaA.ACOPLADO));

        }

        modelo.put("idOt", idOt);
        modelo.put("pendiente", pendiente);
        modelo.put("camion", camion);
        modelo.put("acoplado", acoplado);
        modelo.put("flag", false);
        modelo.put("idOrg", logueado.getIdOrg());
        modelo.addAttribute("mantenimientoC", listaCamion);
        modelo.addAttribute("mantenimientoA", listaAcoplado);
        modelo.addAttribute("tiposCamion", tipoMantenimientoServicio.buscarTiposOrdenAplicaA(logueado.getIdOrg(), TipoMantenimiento.AplicaA.CAMION));
        modelo.addAttribute("proveedores", proveedorServicio.buscarProveedoresHabAsc(logueado.getIdOrg()));

        return "ordenDeTrabajo_registrar.html";

    }

    @PostMapping("/registro")
    public String registro(@RequestParam("mantenimientosCamionJson") String mantenimientosCamionJson,
            @RequestParam("mantenimientosAcopladoJson") String mantenimientosAcopladoJson, @RequestParam Long idCamion, @RequestParam(required = false) Long idAcoplado,
            @RequestParam(required = false) Long idProveedor, @RequestParam(required = false) String observacion, @RequestParam String fechaInicio, @RequestParam String fechaFin,
            @RequestParam(required = false) boolean pendiente, @RequestParam(required = false) Long idOt, ModelMap modelo, RedirectAttributes redirectAttributes,
            HttpSession session) throws JsonProcessingException, ParseException {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        if (mantenimientosCamionJson == null || mantenimientosCamionJson.trim().isEmpty() || mantenimientosCamionJson.trim().equals("[]")) {
            idCamion = null;
        }

        if (mantenimientosAcopladoJson == null || mantenimientosAcopladoJson.trim().isEmpty() || mantenimientosAcopladoJson.trim().equals("[]")) {
            idAcoplado = null;
        }

        if (pendiente == true) {

            ordenServicio.modificarOtCerrado(idOt);

        }

        OrdenDeTrabajo orden = ordenServicio.crearOrden(idCamion, idAcoplado, idProveedor, observacion, fechaInicio, fechaFin, logueado);

        ObjectMapper mapper = new ObjectMapper();

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
            m.setFecha(new Date());
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

        if (orden.getEstado() == OrdenDeTrabajo.Estado.CERRADO) {

            modelo.addAttribute("orden", orden);
            modelo.addAttribute("mantenimientos", orden.getMantenimientos());

            return "ordenDeTrabajo_mostrarCerrada.html";
        }

        if (orden.getCamion() != null) {
            int kmCamion = combustibleServicio.kmUltimaCarga(orden.getCamion());
            modelo.put("kmCamion", kmCamion);
            modelo.put("kmVigenciaC", 0);
        }

        if (orden.getAcoplado() != null) {
            int kmAcoplado = combustibleServicio.kmAcoplado(orden.getAcoplado(), obtenerFechaFija());
            modelo.put("kmAcoplado", kmAcoplado);
            modelo.put("kmVigenciaA", 0);
        } else {
            modelo.put("kmAcoplado", 10000);
            modelo.put("kmVigenciaA", 10000);
        }

        modelo.put("orden", orden);
        modelo.addAttribute("mantenimientos", orden.getMantenimientos());
        modelo.addAttribute("tiposCamion", tipoMantenimientoServicio.buscarTiposOrdenAplicaA(orden.getIdOrg(), TipoMantenimiento.AplicaA.CAMION));
        modelo.addAttribute("tiposAcoplado", tipoMantenimientoServicio.buscarTiposOrdenAplicaA(orden.getIdOrg(), TipoMantenimiento.AplicaA.ACOPLADO));

        return "ordenDeTrabajo_modificar.html";

    }

    @PostMapping("/modifica")
    public String completarOT(@RequestParam Long id, @RequestParam(required = false) List<Long> ejecutados,
            @RequestParam(required = false) String nuevosCamionJson, @RequestParam(required = false) String nuevosAcopladoJson,
            @RequestParam(required = false) String observacion, @RequestParam String fechaInicio, @RequestParam String fechaFin, HttpSession session, HttpServletRequest request, RedirectAttributes redirectAttributes) {

        try {

            Usuario logueado = (Usuario) session.getAttribute("usuariosession");

            OrdenDeTrabajo ot = ordenServicio.buscarOrden(id);

            List<Mantenimiento> existentes = ot.getMantenimientos();
            if (existentes == null) {
                existentes = new ArrayList<>();
            }

            ObjectMapper mapper = new ObjectMapper();

            // ---------- 1) NUEVOS MANTENIMIENTOS CAMIÓN ----------
            List<MantenimientoDTO> mantenimientosCamionDTO = new ArrayList<>();
            if (nuevosCamionJson != null && !nuevosCamionJson.trim().isEmpty() && !nuevosCamionJson.trim().equals("[]")) {
                MantenimientoDTO[] arrC = mapper.readValue(nuevosCamionJson, MantenimientoDTO[].class);
                if (arrC != null) {
                    mantenimientosCamionDTO = Arrays.asList(arrC);
                }
            }

            for (MantenimientoDTO dto : mantenimientosCamionDTO) {
                if (dto == null) {
                    continue;
                }

                Mantenimiento m = new Mantenimiento();
                TipoMantenimiento tipo = new TipoMantenimiento();
                tipo.setId(dto.getTipoMantenimiento());
                m.setTipoMantenimiento(tipo);

                if (dto.getAplicaA() != null) {
                    m.setAplicaA(TipoMantenimiento.AplicaA.valueOf(dto.getAplicaA()));
                } else {
                    m.setAplicaA(TipoMantenimiento.AplicaA.CAMION); // por las dudas
                }

                String obs = dto.getObservacion();
                if (obs != null) {
                    m.setObservacion(obs.toUpperCase());
                }

                m.setKm(dto.getKm());
                m.setKmVigencia(dto.getKmVigencia());
                m.setUsuario(logueado);
                m.setEstado(Mantenimiento.Estado.PENDIENTE);
                m.setIdOrg(logueado.getIdOrg());
                m.setFecha(new Date());
                m.setCamion(ot.getCamion());
                m.setOrdenDeTrabajo(ot);

                Boolean ejecutadoNuevo = dto.getEjecutado();
                if (Boolean.TRUE.equals(ejecutadoNuevo)) {
                    m.setEstado(Mantenimiento.Estado.VIGENTE);

                    Integer km = m.getKm();
                    Integer kmVig = m.getKmVigencia();
                    if (km != null && kmVig != null && kmVig != 0) {
                        m.setKmProximo(km + kmVig);
                        m.setKmAlarma(km + kmVig - 1000);
                    } else {
                        m.setKmProximo(0);
                        m.setKmAlarma(0);
                    }

                    Mantenimiento existente = mantenimientoServicio.buscarExistenteMasivo(m);
                    if (existente != null) {
                        mantenimientoServicio.modificarVigenteOt(m, existente);
                    }
                }

                existentes.add(m);
                Mantenimiento mantenimientoVigente = null;
                mantenimientoServicio.crearMantenimiento(m, mantenimientoVigente);
            }

            // ---------- 2) NUEVOS MANTENIMIENTOS ACOPLADO ----------
            List<MantenimientoDTO> mantenimientosAcopladoDTO = new ArrayList<>();
            if (nuevosAcopladoJson != null && !nuevosAcopladoJson.trim().isEmpty() && !nuevosAcopladoJson.trim().equals("[]")) {
                MantenimientoDTO[] arrA = mapper.readValue(nuevosAcopladoJson, MantenimientoDTO[].class);
                if (arrA != null) {
                    mantenimientosAcopladoDTO = Arrays.asList(arrA);
                }
            }

            for (MantenimientoDTO dto : mantenimientosAcopladoDTO) {
                if (dto == null) {
                    continue;
                }

                Mantenimiento m = new Mantenimiento();
                TipoMantenimiento tipo = new TipoMantenimiento();
                tipo.setId(dto.getTipoMantenimiento());
                m.setTipoMantenimiento(tipo);

                if (dto.getAplicaA() != null) {
                    m.setAplicaA(TipoMantenimiento.AplicaA.valueOf(dto.getAplicaA()));
                } else {
                    m.setAplicaA(TipoMantenimiento.AplicaA.ACOPLADO); // por las dudas
                }

                String obs = dto.getObservacion();
                if (obs != null) {
                    m.setObservacion(obs.toUpperCase());
                }

                m.setKm(dto.getKm());
                m.setKmVigencia(dto.getKmVigencia());
                m.setUsuario(logueado);
                m.setEstado(Mantenimiento.Estado.PENDIENTE);
                m.setIdOrg(logueado.getIdOrg());
                m.setFecha(new Date());
                m.setAcoplado(ot.getAcoplado());
                m.setOrdenDeTrabajo(ot);

                Boolean ejecutadoNuevo = dto.getEjecutado();
                if (Boolean.TRUE.equals(ejecutadoNuevo)) {
                    m.setEstado(Mantenimiento.Estado.VIGENTE);

                    Integer km = m.getKm();
                    Integer kmVig = m.getKmVigencia();
                    if (km != null && kmVig != null && kmVig != 0) {
                        m.setKmProximo(km + kmVig);
                        m.setKmAlarma(km + kmVig - 1000);
                    } else {
                        m.setKmProximo(0);
                        m.setKmAlarma(0);
                    }

                    Mantenimiento existente = mantenimientoServicio.buscarExistenteMasivoOt(m, id);
                    if (existente != null) {
                        mantenimientoServicio.modificarVigenteOt(m, existente);
                    }
                }

                existentes.add(m);
                Mantenimiento mantenimientoVigente = null;
                mantenimientoServicio.crearMantenimiento(m, mantenimientoVigente);
            }

            // ---------- 3) DATOS DE MANTENIMIENTOS EXISTENTES (FORMULARIO) ----------
            String[] kmCam = request.getParameterValues("kmCamionExistente");
            String[] kmVigCam = request.getParameterValues("kmVigenciaExistente");
            String[] obsCam = request.getParameterValues("obsExistente");

            String[] kmAco = request.getParameterValues("kmAcopladoExistente");
            String[] kmVigAco = request.getParameterValues("kmVigenciaAExistente");
            String[] obsAco = request.getParameterValues("obsAcopladoExistente");

            int iCam = 0;
            int iAco = 0;

            for (Mantenimiento m : existentes) {

                if (m.getAplicaA() == TipoMantenimiento.AplicaA.CAMION) {

                    // SOLO si hay inputs para esa fila (en tu UI: cuando está PENDIENTE)
                    if (m.getEstado() == Mantenimiento.Estado.PENDIENTE) {

                        if (kmCam != null && iCam < kmCam.length && kmCam[iCam] != null && !kmCam[iCam].trim().isEmpty()) {
                            m.setKm(Integer.parseInt(kmCam[iCam].trim()));
                        }
                        if (kmVigCam != null && iCam < kmVigCam.length && kmVigCam[iCam] != null && !kmVigCam[iCam].trim().isEmpty()) {
                            m.setKmVigencia(Integer.parseInt(kmVigCam[iCam].trim()));
                        }

                        if (obsCam != null && iCam < obsCam.length && obsCam[iCam] != null) {
                            String obs = obsCam[iCam].trim();
                            if (!obs.isEmpty()) {
                                m.setObservacion(obs.toUpperCase());
                            }

                        }

                        iCam++;

                    }

                } else if (m.getAplicaA() == TipoMantenimiento.AplicaA.ACOPLADO) {

                    // SOLO si hay inputs para esa fila (en tu UI: cuando está PENDIENTE)
                    if (m.getEstado() == Mantenimiento.Estado.PENDIENTE) {

                        if (kmAco != null && iAco < kmAco.length && kmAco[iAco] != null && !kmAco[iAco].trim().isEmpty()) {
                            m.setKm(Integer.parseInt(kmAco[iAco].trim()));
                        }
                        if (kmVigAco != null && iAco < kmVigAco.length && kmVigAco[iAco] != null && !kmVigAco[iAco].trim().isEmpty()) {
                            m.setKmVigencia(Integer.parseInt(kmVigAco[iAco].trim()));
                        }
                        if (obsAco != null && iAco < obsAco.length && obsAco[iAco] != null) {
                            String obs = obsAco[iAco].trim();
                            if (!obs.isEmpty()) {
                                m.setObservacion(obs.toUpperCase());
                            }
                        }

                        iAco++;

                    }

                }

            }

            // ---------- 4) MARCAR COMO EJECUTADOS ----------
            if (ejecutados != null && !ejecutados.isEmpty()) {
                for (Mantenimiento m : existentes) {
                    if (m.getId() != null && ejecutados.contains(m.getId())) {

                        m.setEstado(Mantenimiento.Estado.VIGENTE);
                        m.setFecha(new Date());
                        m.setUsuario(logueado);

                        Integer km = m.getKm();
                        Integer kmVig = m.getKmVigencia();
                        if (km != null && kmVig != null && kmVig != 0) {
                            m.setKmProximo(km + kmVig);
                            m.setKmAlarma(km + kmVig - 1000);
                        } else {
                            m.setKmProximo(0);
                            m.setKmAlarma(0);
                        }

                        Mantenimiento existente = mantenimientoServicio.buscarExistenteMasivoOt(m, id);
                        if (existente != null) {
                            mantenimientoServicio.modificarVigenteOt(m, existente);
                        }
                    }
                }
            }

            if (observacion != null && !observacion.trim().isEmpty()) {
                ot.setObservacion(observacion.toUpperCase());
            }

            ot.setMantenimientos(existentes);
            Date fechaI = convertirFecha(fechaInicio);
            ot.setFechaInicio(fechaI);
            Date fechaF = convertirFecha(fechaFin);
            ot.setFechaFin(fechaF);

            ordenServicio.modificar(ot);
            ordenServicio.actualizarEstadoOT(ot.getId());

            return "redirect:/ordenDeTrabajo/listar?mensaje=exito";

        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Error al procesar la OT.");
            return "redirect:/ordenDeTrabajo/listar?mensaje=error";
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
        modelo.addAttribute("proveedores", proveedorServicio.buscarProveedoresHabAsc(logueado.getIdOrg()));
        modelo.addAttribute("camiones", camionServicio.buscarCamionesHabAsc(logueado.getIdOrg()));
        modelo.addAttribute("acoplados", acopladoServicio.buscarAcopladosHabAsc(logueado.getIdOrg()));
        modelo.put("estado", "ABIERTO");

        return "ordenDeTrabajo_listar.html";

    }

    @PostMapping("/listarFiltro")
    public String listarFiltro(@RequestParam(required = false) Long idCamion, @RequestParam(required = false) Long idAcoplado,
            @RequestParam(required = false) Long idProveedor, @RequestParam(required = false) String estado, ModelMap modelo, HttpSession session) {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");
        Boolean flag = false;
        List<OrdenDeTrabajo> ordenes = new ArrayList();


        if (idCamion == null && idAcoplado == null && idProveedor == null && estado.equalsIgnoreCase("TODOS")) {

            ordenes = ordenServicio.buscarOrdenes(logueado.getIdOrg());

        } else if (idCamion != null && idAcoplado == null && idProveedor == null && estado.equalsIgnoreCase("TODOS")) {

            ordenes = ordenServicio.buscarOrdenesCamion(idCamion);

        } else if (idCamion == null && idAcoplado != null && idProveedor == null && estado.equalsIgnoreCase("TODOS")) {

            ordenes = ordenServicio.buscarOrdenesAcoplado(idAcoplado);

        } else if (idCamion != null && idAcoplado != null && idProveedor == null && estado.equalsIgnoreCase("TODOS")) {

            ordenes = ordenServicio.buscarOrdenesCamionAcoplado(idCamion, idAcoplado);

        } else if (idCamion == null && idAcoplado == null && idProveedor == null && estado.equalsIgnoreCase("ABIERTO")) {

            ordenes = ordenServicio.buscarOrdenAbiertaProceso(logueado.getIdOrg());

        } else if (idCamion != null && idAcoplado != null && idProveedor == null && estado.equalsIgnoreCase("ABIERTO")) {

            ordenes = ordenServicio.buscarOrdenesCamionAcopladoAbiertaProceso(idCamion, idAcoplado);

        } else if (idCamion != null && idAcoplado == null && idProveedor == null && estado.equalsIgnoreCase("ABIERTO")) {

            ordenes = ordenServicio.buscarOrdenesCamionAbiertaProceso(idCamion);

        } else if (idCamion == null && idAcoplado != null && idProveedor == null && estado.equalsIgnoreCase("ABIERTO")) {

            ordenes = ordenServicio.buscarOrdenesAcopladoAbiertaProceso(idAcoplado);

        } else if (idCamion == null && idAcoplado == null && idProveedor == null && estado.equalsIgnoreCase("CERRADO")) {

            ordenes = ordenServicio.buscarOrdenCerrada(logueado.getIdOrg());

        } else if (idCamion != null && idAcoplado != null && idProveedor == null && estado.equalsIgnoreCase("CERRADO")) {

            ordenes = ordenServicio.buscarOrdenesCamionAcopladoCerrada(idCamion, idAcoplado);

        } else if (idCamion != null && idAcoplado == null && idProveedor == null && estado.equalsIgnoreCase("CERRADO")) {

            ordenes = ordenServicio.buscarOrdenesCamionCerrada(idCamion);

        } else if (idCamion == null && idAcoplado != null && idProveedor == null && estado.equalsIgnoreCase("CERRADO")) {

            ordenes = ordenServicio.buscarOrdenesAcopladoCerrada(idAcoplado);

        } else if (idCamion == null && idAcoplado == null && idProveedor != null && estado.equalsIgnoreCase("TODOS")) {

            ordenes = ordenServicio.buscarOrdenesProveedor(logueado.getIdOrg(), idProveedor);

        } else if (idCamion != null && idAcoplado == null && idProveedor != null && estado.equalsIgnoreCase("TODOS")) {

            ordenes = ordenServicio.buscarOrdenesCamionProveedor(idCamion, idProveedor);

        } else if (idCamion == null && idAcoplado != null && idProveedor != null && estado.equalsIgnoreCase("TODOS")) {

            ordenes = ordenServicio.buscarOrdenesAcopladoProveedor(idAcoplado, idProveedor);

        } else if (idCamion != null && idAcoplado != null && idProveedor != null && estado.equalsIgnoreCase("TODOS")) {

            ordenes = ordenServicio.buscarOrdenesCamionAcopladoProveedor(idCamion, idAcoplado, idProveedor);

        } else if (idCamion == null && idAcoplado == null && idProveedor != null && estado.equalsIgnoreCase("ABIERTO")) {

            ordenes = ordenServicio.buscarOrdenAbiertaProcesoProveedor(logueado.getIdOrg(), idProveedor);

        } else if (idCamion != null && idAcoplado != null && idProveedor != null && estado.equalsIgnoreCase("ABIERTO")) {

            ordenes = ordenServicio.buscarOrdenesCamionAcopladoAbiertaProcesoProveedor(idCamion, idAcoplado, idProveedor);

        } else if (idCamion != null && idAcoplado == null && idProveedor != null && estado.equalsIgnoreCase("ABIERTO")) {

            ordenes = ordenServicio.buscarOrdenesCamionAbiertaProcesoProveedor(idCamion, idProveedor);

        } else if (idCamion == null && idAcoplado != null && idProveedor != null && estado.equalsIgnoreCase("ABIERTO")) {

            ordenes = ordenServicio.buscarOrdenesAcopladoAbiertaProcesoProveedor(idAcoplado, idProveedor);

        } else if (idCamion == null && idAcoplado == null && idProveedor != null && estado.equalsIgnoreCase("CERRADO")) {

            ordenes = ordenServicio.buscarOrdenCerradaProveedor(logueado.getIdOrg(), idProveedor);

        } else if (idCamion != null && idAcoplado != null && idProveedor != null && estado.equalsIgnoreCase("CERRADO")) {

            ordenes = ordenServicio.buscarOrdenesCamionAcopladoCerradaProveedor(idCamion, idAcoplado, idProveedor);

        } else if (idCamion != null && idAcoplado == null && idProveedor != null && estado.equalsIgnoreCase("CERRADO")) {

            ordenes = ordenServicio.buscarOrdenesCamionCerradaProveedor(idCamion, idProveedor);

        } else if (idCamion == null && idAcoplado != null && idProveedor != null && estado.equalsIgnoreCase("CERRADO")) {

            ordenes = ordenServicio.buscarOrdenesAcopladoCerradaProveedor(idAcoplado, idProveedor);
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
        if (idProveedor != null) {
            modelo.put("proveedor", proveedorServicio.buscarProveedor(idProveedor));
        }
        
        modelo.put("estado", estado);
        modelo.put("flag", flag);
        modelo.put("ordenes", ordenes);
        modelo.addAttribute("proveedores", proveedorServicio.buscarProveedoresHabAsc(logueado.getIdOrg()));
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
            return null;
        }
    }

}
