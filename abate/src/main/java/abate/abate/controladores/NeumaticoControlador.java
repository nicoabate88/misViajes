
package abate.abate.controladores;

import abate.abate.dto.AuxilioDTO;
import abate.abate.dto.AuxilioForm;
import abate.abate.dto.PosicionNeumaticoForm;
import abate.abate.entidades.Acoplado;
import abate.abate.entidades.AuxilioNeumatico;
import abate.abate.entidades.Camion;
import abate.abate.entidades.Eje;
import abate.abate.entidades.HistorialNeumatico;
import abate.abate.entidades.HistorialRecapado;
import abate.abate.entidades.Neumatico;
import abate.abate.entidades.PosicionNeumatico;
import abate.abate.entidades.Usuario;
import abate.abate.excepciones.MiException;
import abate.abate.servicios.AcopladoServicio;
import abate.abate.servicios.AuxilioNeumaticoServicio;
import abate.abate.servicios.CamionServicio;
import abate.abate.servicios.CombustibleServicio;
import abate.abate.servicios.ExcelServicio;
import abate.abate.servicios.HistorialRecapadoServicio;
import abate.abate.servicios.NeumaticoMarcaServicio;
import abate.abate.servicios.NeumaticoProveedorServicio;
import abate.abate.servicios.NeumaticoServicio;
import abate.abate.util.NeumaticoComparador;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

@Controller
@RequestMapping("/neumatico")
@PreAuthorize("hasAnyRole('ROLE_ADMIN')")
public class NeumaticoControlador {
    
    @Autowired
    private NeumaticoServicio neumaticoServicio;
    @Autowired
    private CamionServicio camionServicio;
    @Autowired
    private AcopladoServicio acopladoServicio;
    @Autowired
    private CombustibleServicio combustibleServicio;
    @Autowired
    private HistorialRecapadoServicio recapadoServicio;
    @Autowired
    private AuxilioNeumaticoServicio auxilioServicio;
    @Autowired
    private NeumaticoMarcaServicio marcaServicio;
    @Autowired
    private NeumaticoProveedorServicio proveedorServicio;
    @Autowired
    private ExcelServicio excelServicio;
    
     @GetMapping("/registrar")
    public String registrar(ModelMap modelo, HttpSession session) {
        
        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        modelo.addAttribute("aplicaA", Neumatico.AplicaA.values());
        modelo.addAttribute("marcas", marcaServicio.buscarMarcasAsc(logueado.getIdOrg()));
        modelo.addAttribute("proveedores", proveedorServicio.buscarProveedoresAsc(logueado.getIdOrg()));
        modelo.put("numero", neumaticoServicio.obtenerProximoNumero(logueado.getIdOrg()));

        return "neumatico_registrar.html";

    }

    @PostMapping("/registro")
    public String registro(@RequestParam String fecha, @RequestParam Integer numero, @RequestParam Long idMarca, @RequestParam String modelo, @RequestParam Long idProveedor,
            @RequestParam String estado, @RequestParam Integer km, @RequestParam Integer kmEstimado, @RequestParam List<Neumatico.AplicaA> aplicaA,
            @RequestParam String observacion, ModelMap model, HttpSession session) throws ParseException, MiException {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");
        
        try {
            neumaticoServicio.crearNeumatico(numero, idMarca, modelo, idProveedor, km, kmEstimado, fecha, aplicaA, observacion, estado, logueado);

            return "redirect:/neumatico/registrado";

        } catch (MiException ex) {

                    model.addAttribute("aplicaA", Neumatico.AplicaA.values());
        model.addAttribute("marcas", marcaServicio.buscarMarcasAsc(logueado.getIdOrg()));
        model.addAttribute("proveedores", proveedorServicio.buscarProveedoresAsc(logueado.getIdOrg()));
        model.put("numero", neumaticoServicio.obtenerProximoNumero(logueado.getIdOrg()));
            model.put("fecha", fecha);
            model.put("marca", marcaServicio.buscarMarca(idMarca));
            model.put("modelo", modelo);
            model.put("proveedor", proveedorServicio.buscarProveedor(idProveedor));
            model.put("estado", estado);
            model.put("km", km);
            model.put("kmEstimado", kmEstimado);
            model.put("observacion", observacion);
            model.put("error", ex.getMessage());

            return "neumatico_registrar.html";

        }

    }

    @GetMapping("/registrado")
    public String registrado(ModelMap modelo, HttpSession session) {
        
        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        modelo.put("neumatico", neumaticoServicio.ultimoNeumatico(logueado.getIdOrg()));
        modelo.put("exito", "Neumático REGISTRADO con éxito");
        modelo.put("ubicacion", "deposito");
        modelo.put("estado", "todos");

        return "neumatico_mostrar.html";
    }
    
    @GetMapping("/modificar")
    public String modificar(@RequestParam Long idNeumatico, @RequestParam String ubicacion, @RequestParam String estado, ModelMap modelo, HttpSession session) {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");
        
        modelo.addAttribute("marcas", marcaServicio.buscarMarcasAsc(logueado.getIdOrg()));
        modelo.addAttribute("proveedores", proveedorServicio.buscarProveedoresAsc(logueado.getIdOrg()));
        modelo.put("neumatico", neumaticoServicio.buscarNeumatico(idNeumatico));
        modelo.put("flag", neumaticoServicio.verificarHistorialNeumatico(idNeumatico));
        modelo.put("ubicacion", ubicacion);
        modelo.put("estado", estado);
        modelo.addAttribute("aplicaA", Neumatico.AplicaA.values());

        return "neumatico_modificar.html";

    }


    @PostMapping("/modifica")
    public String modifica(@RequestParam Long id, @RequestParam String ubicacion, @RequestParam String estado, @RequestParam String fecha,
            @RequestParam Integer numero, @RequestParam Long idMarca, @RequestParam String modelo, @RequestParam Long idProveedor, @RequestParam String estadoN,
            @RequestParam Integer km, @RequestParam Integer kmEstimado, @RequestParam(required = false) String ubicacionN, @RequestParam List<Neumatico.AplicaA> aplicaA,
            @RequestParam String observacion, ModelMap model, HttpSession session) throws ParseException {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");
        
        try {

            if (ubicacionN == null) {
                ubicacionN = "";
            }

            neumaticoServicio.modificarNeumatico(id, numero, idMarca, modelo, idProveedor, km, kmEstimado, fecha, aplicaA, observacion, estadoN, ubicacionN, logueado);

            return "redirect:/neumatico/modificado?id=" + id + "&ubicacion=" + ubicacion + "&estado=" + estado;

        } catch (MiException ex) {

            model.addAttribute("marcas", marcaServicio.buscarMarcasAsc(logueado.getIdOrg()));
        model.addAttribute("proveedores", proveedorServicio.buscarProveedoresAsc(logueado.getIdOrg()));
        model.put("neumatico", neumaticoServicio.buscarNeumatico(id));
        model.put("flag", neumaticoServicio.verificarHistorialNeumatico(id));
        model.put("ubicacion", ubicacion);
        model.put("estado", estado);
        model.addAttribute("aplicaA", Neumatico.AplicaA.values());
            model.put("error", ex.getMessage());

            return "neumatico_modificar.html";

        }

    }

    @GetMapping("/modificado")
    public String modificado(@RequestParam Long id, @RequestParam String ubicacion, @RequestParam String estado, ModelMap modelo) {

        modelo.put("neumatico", neumaticoServicio.buscarNeumatico(id));
        modelo.put("exito", "Neumático MODIFICADO con éxito");
        modelo.put("ubicacion", ubicacion);
        modelo.put("estado", estado);

        return "neumatico_mostrar.html";

    }
    
    @GetMapping("/eliminar")
    public String eliminar(@RequestParam Long idNeumatico, @RequestParam String ubicacion, @RequestParam String estado, ModelMap modelo) {

        modelo.put("neumatico", neumaticoServicio.buscarNeumatico(idNeumatico));
        modelo.put("ubicacion", ubicacion);
        modelo.put("estado", estado);

        return "neumatico_eliminar.html";
    }

    @GetMapping("/elimina")
    public String elimina(@RequestParam Long idNeumatico, @RequestParam String ubicacion, @RequestParam String estado, ModelMap modelo) {

        try {

            neumaticoServicio.eliminarNeumatico(idNeumatico);

            return "redirect:/neumatico/eliminado?id=" + idNeumatico + "&ubicacion=" + ubicacion + "&estado=" + estado;

        } catch (MiException ex) {

            modelo.put("neumatico", neumaticoServicio.buscarNeumatico(idNeumatico));
            modelo.put("ubicacion", ubicacion);
            modelo.put("estado", estado);
            modelo.put("error", ex.getMessage());

            return "neumatico_eliminar.html";
        }
    }

    @GetMapping("/eliminado")
    public String eliminado(@RequestParam Long id, @RequestParam String ubicacion, @RequestParam String estado, ModelMap modelo) {

        boolean eliminado = true;

        return "redirect:/neumatico/listarFiltro?ubicacion=" + ubicacion + "&estado=" + estado + "&eliminado=" + eliminado;

    }
    
    @GetMapping("/listarAdmin")
    public String listarAdmin(ModelMap modelo) {

        modelo.put("camion", Neumatico.AplicaA.CAMION);
        modelo.put("acoplado", Neumatico.AplicaA.ACOPLADO);

        return "neumatico_listarAdmin.html";

    }
    
    @GetMapping("/listar")
    public String listar(ModelMap modelo, HttpSession session) {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");
        
        List<Neumatico> neumaticos = neumaticoServicio.buscarNeumaticosDeposito(logueado.getIdOrg());
        Boolean flag = false;
        if (!neumaticos.isEmpty()) {
            flag = true;
        }

        Collections.sort(neumaticos, NeumaticoComparador.ordenarNumAsc);

        modelo.addAttribute("neumaticos", neumaticos);
        modelo.put("cantidad", neumaticos.size());
        modelo.put("ubicacion", "deposito");
        modelo.put("estado", "todos");
        modelo.put("flag", flag);

        return "neumatico_listar.html";

    }

    @GetMapping("/listarFiltro")
    public String listarFiltro(@RequestParam String ubicacion, @RequestParam String estado, @RequestParam(required = false) boolean eliminado, 
            ModelMap modelo, HttpSession session) {

        List<Neumatico> neumaticos = new ArrayList();
        
        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        if (ubicacion.equalsIgnoreCase("todos") && estado.equalsIgnoreCase("todos")) {

            neumaticos = neumaticoServicio.buscarNeumaticos(logueado.getIdOrg());

        } else if (ubicacion.equalsIgnoreCase("todos") && estado.equalsIgnoreCase("nuevo")) {

            neumaticos = neumaticoServicio.buscarNeumaticosNuevo(logueado.getIdOrg());

        } else if (ubicacion.equalsIgnoreCase("todos") && estado.equalsIgnoreCase("usado")) {

            neumaticos = neumaticoServicio.buscarNeumaticosUsado(logueado.getIdOrg());

        } else if (ubicacion.equalsIgnoreCase("todos") && estado.equalsIgnoreCase("recapado")) {

            neumaticos = neumaticoServicio.buscarNeumaticosRecapado(logueado.getIdOrg());

        } else if (ubicacion.equalsIgnoreCase("deposito") && estado.equalsIgnoreCase("todos")) {

            neumaticos = neumaticoServicio.buscarNeumaticosDeposito(logueado.getIdOrg());

        } else if (ubicacion.equalsIgnoreCase("deposito") && estado.equalsIgnoreCase("nuevo")) {

            neumaticos = neumaticoServicio.buscarNeumaticosDepositoNuevo(logueado.getIdOrg());

        } else if (ubicacion.equalsIgnoreCase("deposito") && estado.equalsIgnoreCase("usado")) {

            neumaticos = neumaticoServicio.buscarNeumaticosDepositoUsado(logueado.getIdOrg());

        } else if (ubicacion.equalsIgnoreCase("deposito") && estado.equalsIgnoreCase("recapado")) {

            neumaticos = neumaticoServicio.buscarNeumaticosDepositoRecapado(logueado.getIdOrg());

        } else if (ubicacion.equalsIgnoreCase("colocado") && estado.equalsIgnoreCase("todos")) {

            neumaticos = neumaticoServicio.buscarNeumaticosColocado(logueado.getIdOrg());

        } else if (ubicacion.equalsIgnoreCase("colocado") && estado.equalsIgnoreCase("nuevo")) {

            neumaticos = neumaticoServicio.buscarNeumaticosColocadoNuevo(logueado.getIdOrg());

        } else if (ubicacion.equalsIgnoreCase("colocado") && estado.equalsIgnoreCase("usado")) {

            neumaticos = neumaticoServicio.buscarNeumaticosColocadoUsado(logueado.getIdOrg());

        } else if (ubicacion.equalsIgnoreCase("colocado") && estado.equalsIgnoreCase("recapado")) {

            neumaticos = neumaticoServicio.buscarNeumaticosColocadoRecapado(logueado.getIdOrg());

        } else if (ubicacion.equalsIgnoreCase("auxilio") && estado.equalsIgnoreCase("todos")) {

            neumaticos = neumaticoServicio.buscarNeumaticosAuxilio(logueado.getIdOrg());

        } else if (ubicacion.equalsIgnoreCase("auxilio") && estado.equalsIgnoreCase("nuevo")) {

            neumaticos = neumaticoServicio.buscarNeumaticosAuxilioNuevo(logueado.getIdOrg());

        } else if (ubicacion.equalsIgnoreCase("auxilio") && estado.equalsIgnoreCase("usado")) {

            neumaticos = neumaticoServicio.buscarNeumaticosAuxilioUsado(logueado.getIdOrg());

        } else if (ubicacion.equalsIgnoreCase("auxilio") && estado.equalsIgnoreCase("recapado")) {

            neumaticos = neumaticoServicio.buscarNeumaticosAuxilioRecapado(logueado.getIdOrg());

        } else if (ubicacion.equalsIgnoreCase("recapado") && estado.equalsIgnoreCase("todos")) {

            neumaticos = neumaticoServicio.buscarNeumaticosEnRecapado(logueado.getIdOrg());

        } else if (ubicacion.equalsIgnoreCase("recapado") && estado.equalsIgnoreCase("nuevo")) {

            neumaticos = neumaticoServicio.buscarNeumaticosEnRecapadoNuevo(logueado.getIdOrg());

        } else if (ubicacion.equalsIgnoreCase("recapado") && estado.equalsIgnoreCase("usado")) {

            neumaticos = neumaticoServicio.buscarNeumaticosEnRecapadoUsado(logueado.getIdOrg());

        } else if (ubicacion.equalsIgnoreCase("recapado") && estado.equalsIgnoreCase("recapado")) {

            neumaticos = neumaticoServicio.buscarNeumaticosEnRecapadoRecapado(logueado.getIdOrg());

        } else if (ubicacion.equalsIgnoreCase("fueraServicio") && estado.equalsIgnoreCase("todos")) {

            neumaticos = neumaticoServicio.buscarNeumaticosFueraServicio(logueado.getIdOrg());

        } else if (ubicacion.equalsIgnoreCase("fueraServicio") && estado.equalsIgnoreCase("nuevo")) {

            neumaticos = neumaticoServicio.buscarNeumaticosFueraServicioNuevo(logueado.getIdOrg());

        } else if (ubicacion.equalsIgnoreCase("fueraServicio") && estado.equalsIgnoreCase("usado")) {

            neumaticos = neumaticoServicio.buscarNeumaticosFueraServicioUsado(logueado.getIdOrg());

        } else if (ubicacion.equalsIgnoreCase("fueraServicio") && estado.equalsIgnoreCase("recapado")) {

            neumaticos = neumaticoServicio.buscarNeumaticosFueraServicioRecapado(logueado.getIdOrg());

        }

        Boolean flag = false;
        if (!neumaticos.isEmpty()) {
            flag = true;
        }

        Collections.sort(neumaticos, NeumaticoComparador.ordenarNumAsc);

        modelo.addAttribute("neumaticos", neumaticos);
        modelo.put("cantidad", neumaticos.size());
        modelo.put("ubicacion", ubicacion);
        modelo.put("estado", estado);
        modelo.put("flag", flag);
        if (eliminado == true) {
            modelo.put("exito", "Neumático ELIMINADO con éxito");
        }

        return "neumatico_listar.html";

    }
    
    @GetMapping("/asignar")
    public String asignar(@RequestParam Neumatico.AplicaA aplicaA, ModelMap modelo, HttpSession session) {
        
        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        modelo.addAttribute("aplicaA", Neumatico.AplicaA.values());
        modelo.put("aplica", aplicaA);

        if (aplicaA == Neumatico.AplicaA.CAMION) {
            
            modelo.addAttribute("camiones", camionServicio.buscarCamionesHabAsc(logueado.getIdOrg()));
            modelo.put("camion", null);

            return "neumatico_asignar.html";

        } else {
            
            modelo.addAttribute("acoplados", acopladoServicio.buscarAcopladosHabAsc(logueado.getIdOrg()));
            modelo.put("acoplado", null);

            return "neumatico_asignar.html";

        }

    }

    @GetMapping("/asignar1")
    public String asignar1(@RequestParam Neumatico.AplicaA aplicaA, @RequestParam Long idEntidad, @RequestParam(required = false) boolean eliminado, 
            @RequestParam(required = false) boolean historialModificado, @RequestParam(required = false) boolean historialEliminado, ModelMap modelo, HttpSession session) {
        
        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        if (aplicaA == Neumatico.AplicaA.CAMION) {

            Camion camion = camionServicio.buscarCamion(idEntidad);
            int km = combustibleServicio.kmUltimaCarga(camion);

        // Crear el mapa de asignaciones
        Map<Long, Map<Integer, Neumatico>> mapaAsignaciones = new HashMap<>();

        for (Eje eje : camion.getEjes()) {
            Map<Integer, Neumatico> posicionesMap = new HashMap<>();
            for (PosicionNeumatico posicion : eje.getPosiciones()) {
                if (!"FINALIZADO".equalsIgnoreCase(posicion.getEstado()) && posicion.getNeumatico() != null) {
                    Neumatico neumatico = posicion.getNeumatico();
                    List<HistorialNeumatico> historialList = neumatico.getHistorial();
                    if (historialList != null && !historialList.isEmpty()) {
                    HistorialNeumatico historial = historialList.get(historialList.size() - 1);
                    Integer kmRecorrido = km - historial.getKmColocacion();
                    Integer kmNeumatico = neumatico.getKm() + kmRecorrido;
                    if(eje.getElevable() == true){
                        kmRecorrido = (kmRecorrido *  historial.getPosicion().getEje().getPorcentaje()) / 100;
                        kmNeumatico = neumatico.getKm() + kmRecorrido;
                    }
                    neumatico.setKm(kmNeumatico);
                    posicion.setNeumatico(neumatico);
                    }
                    posicionesMap.put(posicion.getPosicion(), posicion.getNeumatico());
                }
            }
            mapaAsignaciones.put(eje.getId(), posicionesMap);
        }

        List<AuxilioNeumatico> auxiliosActuales = auxilioServicio.buscarAuxiliosVigenteCamion(idEntidad);

        // Crear una lista de DTOs con los valores actuales para prellenar el formulario
        List<AuxilioDTO> auxilios = new ArrayList<>();

        for (int i = 1; i <= camion.getCantidadAuxilio(); i++) {
            AuxilioDTO dto = new AuxilioDTO();
            dto.setPosicion(i);
            // Ver si hay algún auxilio guardado con esta posición
            for (AuxilioNeumatico a : auxiliosActuales) {
                if (a.getPosicion() != null && a.getPosicion().equals(i)) {
                    dto.setNeumaticoId(a.getNeumatico().getId());
                    break;
                }
            }
            auxilios.add(dto);
        }

            modelo.addAttribute("mapaAsignaciones", mapaAsignaciones);
            modelo.addAttribute("auxilioForm", new AuxilioForm(auxilios));
            modelo.addAttribute("camiones", camionServicio.buscarCamionesHabAsc(logueado.getIdOrg()));
            modelo.put("camion", camion);
            modelo.put("km", km);

        } else {

            Acoplado acoplado = acopladoServicio.buscarAcoplado(idEntidad);
            
            int km = combustibleServicio.kmAcoplado(acoplado, obtenerFechaFija());
            
        Map<Long, Map<Integer, Neumatico>> mapaAsignaciones = new HashMap<>();

        for (Eje eje : acoplado.getEjes()) {
            Map<Integer, Neumatico> posicionesMap = new HashMap<>();
            for (PosicionNeumatico posicion : eje.getPosiciones()) {
                if (!"FINALIZADO".equalsIgnoreCase(posicion.getEstado()) && posicion.getNeumatico() != null) {
                    Neumatico neumatico = posicion.getNeumatico();
                    List<HistorialNeumatico> historialList = neumatico.getHistorial();
                    if (historialList != null && !historialList.isEmpty()) {
                    HistorialNeumatico historial = historialList.get(historialList.size() - 1);
                    Integer kmRecorrido = km - historial.getKmColocacion();
                    Integer kmNeumatico = neumatico.getKm() + kmRecorrido;
                    if(eje.getElevable() == true){
                        kmRecorrido = (kmRecorrido *  historial.getPosicion().getEje().getPorcentaje()) / 100;
                        kmNeumatico = neumatico.getKm() + kmRecorrido;
                    }
                    neumatico.setKm(kmNeumatico);
                    posicion.setNeumatico(neumatico);
                    }
                    posicionesMap.put(posicion.getPosicion(), posicion.getNeumatico());
                }
            }
            mapaAsignaciones.put(eje.getId(), posicionesMap);
        }

        List<AuxilioNeumatico> auxiliosActuales = auxilioServicio.buscarAuxiliosVigenteAcoplado(idEntidad);

        // Crear una lista de DTOs con los valores actuales para prellenar el formulario
        List<AuxilioDTO> auxilios = new ArrayList<>();

        for (int i = 1; i <= acoplado.getCantidadAuxilio(); i++) {
            AuxilioDTO dto = new AuxilioDTO();
            dto.setPosicion(i);
            // Ver si hay algún auxilio guardado con esta posición
            for (AuxilioNeumatico a : auxiliosActuales) {
                if (a.getPosicion() != null && a.getPosicion().equals(i)) {
                    dto.setNeumaticoId(a.getNeumatico().getId());
                    break;
                }
            }
            auxilios.add(dto);
        }
        
            modelo.addAttribute("mapaAsignaciones", mapaAsignaciones);
            modelo.addAttribute("auxilioForm", new AuxilioForm(auxilios));
            modelo.addAttribute("acoplados", acopladoServicio.buscarAcopladosHabAsc(logueado.getIdOrg()));
            modelo.put("acoplado", acoplado);
            modelo.put("km", km);

        }
        // Obtener lista de neumáticos disponibles
        List<Neumatico> neumaticos = neumaticoServicio.buscarNeumaticosAplicaA(aplicaA, logueado.getIdOrg());
        Collections.sort(neumaticos, NeumaticoComparador.ordenarNumAsc);

        modelo.addAttribute("aplicaA", Neumatico.AplicaA.values());
        modelo.put("aplica", aplicaA);
        modelo.addAttribute("neumaticos", neumaticos);
        if (eliminado == true) {
        modelo.put("exito", "Neumático ASIGNADO con éxito");
        }
        if(historialModificado == true){
        modelo.put("exito", "Historial de Neumático MODIFICADO con éxito");
        }
        if(historialEliminado == true){
        modelo.put("exito", "Historial de Neumático ELIMINADO con éxito");
        }

        return "neumatico_asignar.html";

    }

    @PostMapping("/asignaNeumatico")
    public String guardarAsignacionNeumaticos(@RequestParam Neumatico.AplicaA aplicaA, @RequestParam Long idEntidad, @RequestParam("fecha") String fecha, 
            @RequestParam("km") Integer km, @ModelAttribute PosicionNeumaticoForm posicionNeumaticoForm, @ModelAttribute AuxilioForm auxilioForm,
            ModelMap modelo, HttpSession session) throws ParseException {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        neumaticoServicio.guardarAsignacionNeumaticos(aplicaA, idEntidad, fecha, km, posicionNeumaticoForm.getPosiciones(), auxilioForm.getAuxilios(), logueado);

        Boolean eliminado = true;
        
        return "redirect:/neumatico/asignar1?aplicaA=" + aplicaA + "&idEntidad=" + idEntidad + "&eliminado=" + eliminado;

    }
    
    @GetMapping("/historial/{id}")
    public String obtenerHistorial(@PathVariable Long id, ModelMap model) {

        Neumatico neumatico = neumaticoServicio.buscarNeumatico(id);
        List<HistorialNeumatico> historial = neumatico.getHistorial();
        List<HistorialRecapado> recapados = neumatico.getRecapados();

        for (HistorialNeumatico h : historial) {
            if (h.getFechaRetiro() == null) {
                h.setFechaRetiro(new Date());
            }
            
            if (h.getKmRecorrido() == null) {
                if (h.getCamion() != null) {
                    Integer km = combustibleServicio.kmUltimaCarga(h.getCamion());
                    h.setKmRetiro(km);
                    if (h.getPosicion().getEje().getElevable() == false) {
                        Integer kmRecorrido = km - h.getKmColocacion();
                        h.setKmRecorrido(kmRecorrido);
                        Integer kmNeumatico = neumatico.getKm() + kmRecorrido;
                        neumatico.setKm(kmNeumatico);
                        neumatico.setKmUtil(neumatico.getKmEstimado() - kmNeumatico);
                    } else { 
                        Integer kmRec = km - h.getKmColocacion();
                        Integer kmRecorrido = (kmRec * h.getPosicion().getEje().getPorcentaje()) / 100;   
                        h.setKmRecorrido(kmRecorrido);
                        Integer kmNeumatico = neumatico.getKm() + kmRecorrido;
                        neumatico.setKm(kmNeumatico);
                        neumatico.setKmUtil(neumatico.getKmEstimado() - kmNeumatico);
                    }
                } else {
                    int km = combustibleServicio.kmAcoplado(h.getAcoplado(), obtenerFechaFija());
                    h.setKmRetiro(km);
                    if (h.getPosicion().getEje().getElevable() == false) {
                        Integer kmRecorrido = km - h.getKmColocacion();
                        h.setKmRecorrido(kmRecorrido);
                        Integer kmNeumatico = neumatico.getKm() + kmRecorrido;
                        neumatico.setKm(kmNeumatico);
                        neumatico.setKmUtil(neumatico.getKmEstimado() - kmNeumatico);
                    } else { //si eje es elevable, divide los KM en 2
                        Integer kmRec = km - h.getKmColocacion();
                        Integer kmRecorrido = (kmRec * h.getPosicion().getEje().getPorcentaje()) / 100;  
                        h.setKmRecorrido(kmRecorrido);
                        Integer kmNeumatico = neumatico.getKm() + kmRecorrido;
                        neumatico.setKm(kmNeumatico);
                        neumatico.setKmUtil(neumatico.getKmEstimado() - kmNeumatico);
                    }
                }
            }

        }

        Collections.reverse(historial);
        
        HistorialNeumatico posicion = historial.isEmpty() ? null : historial.get(0);
        Boolean flag = true;
        if(neumatico.getUbicacion().equalsIgnoreCase("COLOCADO") || neumatico.getUbicacion().equalsIgnoreCase("AUXILIO")){
            flag = false;
        }
        
         if(!recapados.isEmpty()){
            HistorialRecapado recapado = recapados.get(recapados.size() - 1);
            if(recapado.getEstado().equalsIgnoreCase("VIGENTE")){
            recapado.setKmRecapado(neumatico.getKm() - recapado.getKmAlRecapar());
            }
        }
        
        model.put("flag", flag);
        model.put("auxilio", auxilioServicio.buscarAuxilioIdNeumatico(id));
        model.put("posicion", posicion);
        model.addAttribute("neumatico", neumatico);
        model.addAttribute("historial", historial);
        model.addAttribute("recapados", recapados);

        return "fragmentos/historial_neumatico :: historialFragment";

    }
    
     @GetMapping("/modificarHistorial/{id}")
    public String modificarHistorial(@PathVariable Long id, ModelMap modelo){
        
        modelo.put("historial", neumaticoServicio.buscarHistorial(id));
        modelo.put("camion", Neumatico.AplicaA.CAMION);
        modelo.put("acoplado", Neumatico.AplicaA.ACOPLADO);
        
        return "neumatico_modificarHistorial.html";
        
    }
    
    @PostMapping("/modificaHistorial")
    public String modificaHistorial(@RequestParam Long id, @RequestParam String fecha, @RequestParam Integer km, 
            @RequestParam Neumatico.AplicaA aplicaA, @RequestParam Long idEntidad, ModelMap modelo, HttpSession session) throws ParseException{
        
        Usuario logueado = (Usuario) session.getAttribute("usuariosession");
        
        neumaticoServicio.modificarHistorial(id, fecha, km, logueado);
        
        Boolean historialModificado = true;
        
        return "redirect:/neumatico/asignar1?aplicaA=" + aplicaA + "&idEntidad=" + idEntidad + "&historialModificado=" + historialModificado;
        
    }
    
    @GetMapping("/eliminarHistorial/{id}")
    public String eliminarHistorial(@PathVariable Long id, ModelMap modelo){
        
        modelo.put("historial", neumaticoServicio.buscarHistorial(id));
        modelo.put("camion", Neumatico.AplicaA.CAMION);
        modelo.put("acoplado", Neumatico.AplicaA.ACOPLADO);
        
        return "neumatico_eliminarHistorial.html";
        
    }
    
    @GetMapping("/eliminaHistorial")
    public String modificaHistorial(@RequestParam Long id, @RequestParam Neumatico.AplicaA aplicaA, @RequestParam Long idEntidad, ModelMap modelo) {
        
        neumaticoServicio.eliminarHistorial(id);
        
        Boolean historialEliminado = true;
        
        return "redirect:/neumatico/asignar1?aplicaA=" + aplicaA + "&idEntidad=" + idEntidad + "&historialEliminado=" + historialEliminado;
        
    }
    
    @GetMapping("/recapar")
    public String recapar(@RequestParam(required = false) Long idNeumatico, @RequestParam(required = false) String fecha,
            ModelMap modelo, HttpSession session) {
        
        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        if (idNeumatico != null) {
            modelo.put("neumatico", neumaticoServicio.buscarNeumatico(idNeumatico));
        } else {
            modelo.put("neumatico", null);
        }
        if (fecha != null) {
            modelo.put("fecha", fecha);
        }
        
        modelo.addAttribute("proveedores", proveedorServicio.buscarProveedoresAsc(logueado.getIdOrg()));
        modelo.addAttribute("neumaticos", neumaticoServicio.buscarNeumaticosDeposito(logueado.getIdOrg()));

        return "neumatico_recapar.html";

    }

    @PostMapping("/recapa")
    public String recapa(@RequestParam String fecha, @RequestParam Long idNeumatico,
            @RequestParam Integer km, @RequestParam Integer kmEstimado,
            @RequestParam Long idProveedor, @RequestParam String observacion, ModelMap model, HttpSession session) throws ParseException {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        recapadoServicio.crearRecapado(fecha, idNeumatico, km, kmEstimado, idProveedor, observacion, logueado);

        return "redirect:/neumatico/recapado?&idOrg=" + logueado.getIdOrg();
    }

    @GetMapping("/recapado")
    public String recapado(@RequestParam Long idOrg, ModelMap modelo) {

        HistorialRecapado recapado = recapadoServicio.buscarUltimo(idOrg);

        modelo.put("recapado", recapado);
        modelo.put("exito", "Neumático enviado a RECAPAR");

        return "neumatico_mostrarRecapado.html";
    }

    @GetMapping("/modificarRecapado")
    public String modificarRecapado(@RequestParam Long id, ModelMap modelo, HttpSession session) {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");
        
        modelo.addAttribute("proveedores", proveedorServicio.buscarProveedoresAsc(logueado.getIdOrg()));
        modelo.put("recapado", recapadoServicio.buscarRecapado(id));

        return "neumatico_recaparModificar.html";

    }

    @PostMapping("/modificaRecapado")
    public String modificaRecapado(@RequestParam Long id, @RequestParam String fecha, @RequestParam Integer km,
            @RequestParam Integer kmEstimado, @RequestParam Long idProveedor, @RequestParam String observacion,
            ModelMap model, HttpSession session) throws ParseException {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        recapadoServicio.modificarRecapado(id, fecha, km, kmEstimado, idProveedor, observacion, logueado);

        return "redirect:/neumatico/modificadoRecapado?id=" + id;

    }

    @GetMapping("/modificadoRecapado")
    public String modificadoRecapado(@RequestParam Long id, ModelMap modelo) {

        HistorialRecapado recapado = recapadoServicio.buscarRecapado(id);

        modelo.put("recapado", recapado);
        modelo.put("neumatico", recapado.getNeumatico());
        modelo.put("exito", "Neumático en Recapado MODIFICADO con éxito");

        return "neumatico_mostrarRecapado.html";

    }

    @GetMapping("/eliminarRecapado")
    public String eliminarRecapado(@RequestParam Long id, ModelMap modelo) {

        modelo.put("recapado", recapadoServicio.buscarRecapado(id));

        return "neumatico_recaparEliminar.html";
    }

    @GetMapping("/eliminaRecapado")
    public String eliminaRecapado(@RequestParam Long id, ModelMap modelo) {

        recapadoServicio.eliminarRecapado(id);

        return "redirect:/neumatico/eliminadoRecapado";

    }

    @GetMapping("/eliminadoRecapado")
    public String eliminadoRecapado(ModelMap modelo, HttpSession session) {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");
        
        modelo.put("exito", "Recapado de Neumático ELIMINADO con éxito");
        modelo.addAttribute("recapados", neumaticoServicio.buscarRecapadoEnRecapado(logueado.getIdOrg()));

        return "neumatico_listarRecapado.html";

    }

    @GetMapping("/listarRecapado")
    public String listarRecapado(ModelMap modelo, HttpSession session) {
        
        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        modelo.addAttribute("recapados", neumaticoServicio.buscarRecapadoEnRecapado(logueado.getIdOrg()));

        return "neumatico_listarRecapado.html";

    }

    @GetMapping("/reingresarRecapado")
    public String aceptar(@RequestParam Long id, ModelMap modelo, HttpSession session) {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        recapadoServicio.reingresarRecapado(id, logueado);

        modelo.addAttribute("recapados", neumaticoServicio.buscarRecapadoEnRecapado(logueado.getIdOrg()));
        modelo.put("exito", "Neumático recapado REINGRESADO a Depósito");

        return "neumatico_listarRecapado.html";

    }

    
    @PostMapping("/exportar")
    public String exportarNeumaticos(@RequestParam String ubicacion, @RequestParam String estado, ModelMap modelo, HttpSession session) throws ParseException {
        
        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        List<Neumatico> neumaticos = new ArrayList();

        if (ubicacion.equalsIgnoreCase("todos") && estado.equalsIgnoreCase("todos")) {

            neumaticos = neumaticoServicio.buscarNeumaticos(logueado.getIdOrg());

        } else if (ubicacion.equalsIgnoreCase("todos") && estado.equalsIgnoreCase("nuevo")) {

            neumaticos = neumaticoServicio.buscarNeumaticosNuevo(logueado.getIdOrg());

        } else if (ubicacion.equalsIgnoreCase("todos") && estado.equalsIgnoreCase("usado")) {

            neumaticos = neumaticoServicio.buscarNeumaticosUsado(logueado.getIdOrg());

        } else if (ubicacion.equalsIgnoreCase("todos") && estado.equalsIgnoreCase("recapado")) {

            neumaticos = neumaticoServicio.buscarNeumaticosRecapado(logueado.getIdOrg());

        } else if (ubicacion.equalsIgnoreCase("deposito") && estado.equalsIgnoreCase("todos")) {

            neumaticos = neumaticoServicio.buscarNeumaticosDeposito(logueado.getIdOrg());

        } else if (ubicacion.equalsIgnoreCase("deposito") && estado.equalsIgnoreCase("nuevo")) {

            neumaticos = neumaticoServicio.buscarNeumaticosDepositoNuevo(logueado.getIdOrg());

        } else if (ubicacion.equalsIgnoreCase("deposito") && estado.equalsIgnoreCase("usado")) {

            neumaticos = neumaticoServicio.buscarNeumaticosDepositoUsado(logueado.getIdOrg());

        } else if (ubicacion.equalsIgnoreCase("deposito") && estado.equalsIgnoreCase("recapado")) {

            neumaticos = neumaticoServicio.buscarNeumaticosDepositoRecapado(logueado.getIdOrg());

        } else if (ubicacion.equalsIgnoreCase("colocado") && estado.equalsIgnoreCase("todos")) {

            neumaticos = neumaticoServicio.buscarNeumaticosColocado(logueado.getIdOrg());

        } else if (ubicacion.equalsIgnoreCase("colocado") && estado.equalsIgnoreCase("nuevo")) {

            neumaticos = neumaticoServicio.buscarNeumaticosColocadoNuevo(logueado.getIdOrg());

        } else if (ubicacion.equalsIgnoreCase("colocado") && estado.equalsIgnoreCase("usado")) {

            neumaticos = neumaticoServicio.buscarNeumaticosColocadoUsado(logueado.getIdOrg());

        } else if (ubicacion.equalsIgnoreCase("colocado") && estado.equalsIgnoreCase("recapado")) {

            neumaticos = neumaticoServicio.buscarNeumaticosColocadoRecapado(logueado.getIdOrg());

        } else if (ubicacion.equalsIgnoreCase("auxilio") && estado.equalsIgnoreCase("todos")) {

            neumaticos = neumaticoServicio.buscarNeumaticosAuxilio(logueado.getIdOrg());

        } else if (ubicacion.equalsIgnoreCase("auxilio") && estado.equalsIgnoreCase("nuevo")) {

            neumaticos = neumaticoServicio.buscarNeumaticosAuxilioNuevo(logueado.getIdOrg());

        } else if (ubicacion.equalsIgnoreCase("auxilio") && estado.equalsIgnoreCase("usado")) {

            neumaticos = neumaticoServicio.buscarNeumaticosAuxilioUsado(logueado.getIdOrg());

        } else if (ubicacion.equalsIgnoreCase("auxilio") && estado.equalsIgnoreCase("recapado")) {

            neumaticos = neumaticoServicio.buscarNeumaticosAuxilioRecapado(logueado.getIdOrg());

        } else if (ubicacion.equalsIgnoreCase("recapado") && estado.equalsIgnoreCase("todos")) {

            neumaticos = neumaticoServicio.buscarNeumaticosEnRecapado(logueado.getIdOrg());

        } else if (ubicacion.equalsIgnoreCase("recapado") && estado.equalsIgnoreCase("nuevo")) {

            neumaticos = neumaticoServicio.buscarNeumaticosEnRecapadoNuevo(logueado.getIdOrg());

        } else if (ubicacion.equalsIgnoreCase("recapado") && estado.equalsIgnoreCase("usado")) {

            neumaticos = neumaticoServicio.buscarNeumaticosEnRecapadoUsado(logueado.getIdOrg());

        } else if (ubicacion.equalsIgnoreCase("recapado") && estado.equalsIgnoreCase("recapado")) {

            neumaticos = neumaticoServicio.buscarNeumaticosEnRecapadoRecapado(logueado.getIdOrg());

        } else if (ubicacion.equalsIgnoreCase("fueraServicio") && estado.equalsIgnoreCase("todos")) {

            neumaticos = neumaticoServicio.buscarNeumaticosFueraServicio(logueado.getIdOrg());

        } else if (ubicacion.equalsIgnoreCase("fueraServicio") && estado.equalsIgnoreCase("nuevo")) {

            neumaticos = neumaticoServicio.buscarNeumaticosFueraServicioNuevo(logueado.getIdOrg());

        } else if (ubicacion.equalsIgnoreCase("fueraServicio") && estado.equalsIgnoreCase("usado")) {

            neumaticos = neumaticoServicio.buscarNeumaticosFueraServicioUsado(logueado.getIdOrg());

        } else if (ubicacion.equalsIgnoreCase("fueraServicio") && estado.equalsIgnoreCase("recapado")) {

            neumaticos = neumaticoServicio.buscarNeumaticosFueraServicioRecapado(logueado.getIdOrg());

        }

        Collections.sort(neumaticos, NeumaticoComparador.ordenarNumAsc);

        modelo.addAttribute("neumaticos", neumaticos);
        modelo.put("ubicacion", ubicacion);
        modelo.put("estado", estado);

        return "neumatico_exportar.html";

    }

    @PostMapping("/exporta")
    public void exporta(@RequestParam String ubicacion, @RequestParam String estado, HttpSession session, HttpServletResponse response) throws IOException, ParseException {
        
        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        List<Neumatico> neumaticos = new ArrayList();

        if (ubicacion.equalsIgnoreCase("todos") && estado.equalsIgnoreCase("todos")) {

            neumaticos = neumaticoServicio.buscarNeumaticos(logueado.getIdOrg());

        } else if (ubicacion.equalsIgnoreCase("todos") && estado.equalsIgnoreCase("nuevo")) {

            neumaticos = neumaticoServicio.buscarNeumaticosNuevo(logueado.getIdOrg());

        } else if (ubicacion.equalsIgnoreCase("todos") && estado.equalsIgnoreCase("usado")) {

            neumaticos = neumaticoServicio.buscarNeumaticosUsado(logueado.getIdOrg());

        } else if (ubicacion.equalsIgnoreCase("todos") && estado.equalsIgnoreCase("recapado")) {

            neumaticos = neumaticoServicio.buscarNeumaticosRecapado(logueado.getIdOrg());

        } else if (ubicacion.equalsIgnoreCase("deposito") && estado.equalsIgnoreCase("todos")) {

            neumaticos = neumaticoServicio.buscarNeumaticosDeposito(logueado.getIdOrg());

        } else if (ubicacion.equalsIgnoreCase("deposito") && estado.equalsIgnoreCase("nuevo")) {

            neumaticos = neumaticoServicio.buscarNeumaticosDepositoNuevo(logueado.getIdOrg());

        } else if (ubicacion.equalsIgnoreCase("deposito") && estado.equalsIgnoreCase("usado")) {

            neumaticos = neumaticoServicio.buscarNeumaticosDepositoUsado(logueado.getIdOrg());

        } else if (ubicacion.equalsIgnoreCase("deposito") && estado.equalsIgnoreCase("recapado")) {

            neumaticos = neumaticoServicio.buscarNeumaticosDepositoRecapado(logueado.getIdOrg());

        } else if (ubicacion.equalsIgnoreCase("colocado") && estado.equalsIgnoreCase("todos")) {

            neumaticos = neumaticoServicio.buscarNeumaticosColocado(logueado.getIdOrg());

        } else if (ubicacion.equalsIgnoreCase("colocado") && estado.equalsIgnoreCase("nuevo")) {

            neumaticos = neumaticoServicio.buscarNeumaticosColocadoNuevo(logueado.getIdOrg());

        } else if (ubicacion.equalsIgnoreCase("colocado") && estado.equalsIgnoreCase("usado")) {

            neumaticos = neumaticoServicio.buscarNeumaticosColocadoUsado(logueado.getIdOrg());

        } else if (ubicacion.equalsIgnoreCase("colocado") && estado.equalsIgnoreCase("recapado")) {

            neumaticos = neumaticoServicio.buscarNeumaticosColocadoRecapado(logueado.getIdOrg());

        } else if (ubicacion.equalsIgnoreCase("auxilio") && estado.equalsIgnoreCase("todos")) {

            neumaticos = neumaticoServicio.buscarNeumaticosAuxilio(logueado.getIdOrg());

        } else if (ubicacion.equalsIgnoreCase("auxilio") && estado.equalsIgnoreCase("nuevo")) {

            neumaticos = neumaticoServicio.buscarNeumaticosAuxilioNuevo(logueado.getIdOrg());

        } else if (ubicacion.equalsIgnoreCase("auxilio") && estado.equalsIgnoreCase("usado")) {

            neumaticos = neumaticoServicio.buscarNeumaticosAuxilioUsado(logueado.getIdOrg());

        } else if (ubicacion.equalsIgnoreCase("auxilio") && estado.equalsIgnoreCase("recapado")) {

            neumaticos = neumaticoServicio.buscarNeumaticosAuxilioRecapado(logueado.getIdOrg());

        } else if (ubicacion.equalsIgnoreCase("recapado") && estado.equalsIgnoreCase("todos")) {

            neumaticos = neumaticoServicio.buscarNeumaticosEnRecapado(logueado.getIdOrg());

        } else if (ubicacion.equalsIgnoreCase("recapado") && estado.equalsIgnoreCase("nuevo")) {

            neumaticos = neumaticoServicio.buscarNeumaticosEnRecapadoNuevo(logueado.getIdOrg());

        } else if (ubicacion.equalsIgnoreCase("recapado") && estado.equalsIgnoreCase("usado")) {

            neumaticos = neumaticoServicio.buscarNeumaticosEnRecapadoUsado(logueado.getIdOrg());

        } else if (ubicacion.equalsIgnoreCase("recapado") && estado.equalsIgnoreCase("recapado")) {

            neumaticos = neumaticoServicio.buscarNeumaticosEnRecapadoRecapado(logueado.getIdOrg());

        } else if (ubicacion.equalsIgnoreCase("fueraServicio") && estado.equalsIgnoreCase("todos")) {

            neumaticos = neumaticoServicio.buscarNeumaticosFueraServicio(logueado.getIdOrg());

        } else if (ubicacion.equalsIgnoreCase("fueraServicio") && estado.equalsIgnoreCase("nuevo")) {

            neumaticos = neumaticoServicio.buscarNeumaticosFueraServicioNuevo(logueado.getIdOrg());

        } else if (ubicacion.equalsIgnoreCase("fueraServicio") && estado.equalsIgnoreCase("usado")) {

            neumaticos = neumaticoServicio.buscarNeumaticosFueraServicioUsado(logueado.getIdOrg());

        } else if (ubicacion.equalsIgnoreCase("fueraServicio") && estado.equalsIgnoreCase("recapado")) {

            neumaticos = neumaticoServicio.buscarNeumaticosFueraServicioRecapado(logueado.getIdOrg());

        }

        Collections.sort(neumaticos, NeumaticoComparador.ordenarNumAsc);

        String htmlContent = generateHtmlFromObjects(neumaticos);
        excelServicio.exportHtmlToExcelNeumaticos(htmlContent, response);

    }

    private String generateHtmlFromObjects(List<Neumatico> objects) {
        StringBuilder sb = new StringBuilder();
        sb.append("<table>");
        sb.append("<thead><tr>"
                + "<th>Número</th>"
                + "<th>Vehículo</th>"
                + "<th>Marca</th>"
                + "<th>Modelo</th>"
                + "<th>Proveedor</th>"
                + "<th>Fecha Ingreso</th>"
                + "<th>Estado</th>"
                + "<th>Ubicación</th>"
                + "<th>KM</th>"
                + "<th>KM Estimado</th>"
                + "<th>KM Útil</th>"
                + "<th>Observación</th>"
                + "<th>Fecha Egreso</th>"
                + "</tr></thead>");
        sb.append("<tbody>");
        for (Neumatico neumatico : objects) {
            sb.append("<tr><td>").append(neumatico.getNumero()).append("</td>"
                    + "<td>").append(neumatico.getAplicaA()).append("</td>"
                    + "<td>").append(neumatico.getMarca().getMarca()).append("</td>"
                    + "<td>").append(neumatico.getModelo()).append("</td>"
                    + "<td>").append(neumatico.getProveedor().getNombre()).append("</td>"
                    + "<td>").append(neumatico.getFechaIngreso()).append("</td>"
                    + "<td>").append(neumatico.getEstado()).append("</td>"
                    + "<td>").append(neumatico.getUbicacion()).append("</td>"
                    + "<td>").append(neumatico.getKm()).append("</td>"
                    + "<td>").append(neumatico.getKmEstimado()).append("</td>"
                    + "<td>").append(neumatico.getKmUtil()).append("</td>"
                    + "<td>").append(neumatico.getObservacion()).append("</td>"
                    + "<td>").append(neumatico.getFechaEgreso()).append("</td>"
                    + "</tr>");
        }
        sb.append("</tbody></table>");
        return sb.toString();
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
