package abate.abate.controladores;

import abate.abate.entidades.Combustible;
import abate.abate.entidades.Documentacion;
import abate.abate.entidades.Flete;
import abate.abate.entidades.Gasto;
import abate.abate.entidades.Imagen;
import abate.abate.entidades.TipoDocumentacion;
import abate.abate.entidades.Usuario;
import abate.abate.servicios.CombustibleServicio;
import abate.abate.servicios.DocumentacionServicio;
import abate.abate.servicios.FleteServicio;
import abate.abate.servicios.GastoServicio;
import abate.abate.servicios.ImagenServicio;
import abate.abate.servicios.UsuarioServicio;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import javax.servlet.http.HttpSession;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/imagen")
@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_CHOFER')")
public class ImagenControlador {

    @Autowired
    private ImagenServicio imagenServicio;
    @Autowired
    private GastoServicio gastoServicio;
    @Autowired
    private FleteServicio fleteServicio;
    @Autowired
    private CombustibleServicio combustibleServicio;
    @Autowired
    private DocumentacionServicio documentacionServicio;
    @Autowired
    private UsuarioServicio usuarioServicio;

    @GetMapping("/cargarGasto/{id}") //llega id de Flete
    public String registrar(@PathVariable Long id, ModelMap modelo) {

        modelo.put("flete", fleteServicio.buscarFlete(id));

        return "imagen_gastoCargar.html";
    }

    @PostMapping("/cargaGasto")
    public String crearImagen(@RequestParam Long id, @RequestParam("file") MultipartFile file, ModelMap modelo) throws IOException {

        Flete flete = fleteServicio.buscarFlete(id);

        try {

            Imagen imagen = new Imagen();
            imagen.setNombre("Gasto VJE ID" + flete.getIdFlete());
            imagen.setTipo(file.getContentType());

            if (file.getContentType().equals("application/pdf")) {
                imagen.setDatos(file.getBytes());
            } else {
                imagen.setDatos(optimizeImage(file));
            }

            imagenServicio.crearImagenGasto(id, imagen);

            return "redirect:/imagen/cargadoGasto/" + id;

        } catch (Exception e) {
            modelo.addAttribute("flete", flete);
            modelo.addAttribute("error", "Ocurrió un error al procesar su imagen. Intente nuevamente o ingrese otro archivo");
            return "imagen_gastoCargar.html";
        }

    }
    
    @PostMapping("/cargaGastoPendiente")
    public String crearImagenPendiente(@RequestParam Long id, @RequestParam("file") MultipartFile file, ModelMap modelo) throws IOException {

        Flete flete = fleteServicio.buscarFlete(id);

        try {

            Imagen imagen = new Imagen();
            imagen.setNombre("Gasto VJE ID" + flete.getIdFlete());
            imagen.setTipo(file.getContentType());

            if (file.getContentType().equals("application/pdf")) {
                imagen.setDatos(file.getBytes());
            } else {
                imagen.setDatos(optimizeImage(file));
            }

            imagenServicio.crearImagenGasto(id, imagen);

            return "redirect:/imagen/cargadoGastoPendiente/" + id;

        } catch (Exception e) {
            modelo.addAttribute("flete", flete);
            modelo.addAttribute("error", "Ocurrió un error al procesar su imagen. Intente nuevamente o ingrese otro archivo");
            return "imagen_gastoCargarPendiente.html";
        }

    }
    
    @GetMapping("/cargadoGastoPendiente/{id}")
    public String cargadoGastoPendiente(@PathVariable Long id, ModelMap modelo) {

        Flete flete = fleteServicio.buscarFlete(id);

        modelo.put("gasto", gastoServicio.buscarGasto(flete.getGasto().getId()));
        modelo.put("idFlete", id);
        modelo.put("exito", "Imagen de Gasto CARGADA con éxito");

        return "gasto_mostrarPendiente.html";

    }

    @GetMapping("/cargadoGasto/{id}")
    public String cargadoGasto(@PathVariable Long id, ModelMap modelo) {

        Flete flete = fleteServicio.buscarFlete(id);

        modelo.put("gasto", flete.getGasto());
        modelo.put("idFlete", id);
        modelo.put("exito", "Imagen de Gasto CARGADA con éxito");

        return "gasto_mostrarChofer.html";

    }
    
    @PostMapping("/cargaGastoAdmin")
    public String crearImagenAdmin(@RequestParam Long id,  @RequestParam String desde, @RequestParam String hasta, @RequestParam(required = false) Long idChofer,
           @RequestParam(required = false) Long idCamion, @RequestParam(required = false) Long idCliente, 
           @RequestParam("file") MultipartFile file, ModelMap modelo) throws IOException {

        Flete flete = fleteServicio.buscarFlete(id);

        try {

            Imagen imagen = new Imagen();
            imagen.setNombre("Gasto VJE ID" + flete.getIdFlete());
            imagen.setTipo(file.getContentType());

            if (file.getContentType().equals("application/pdf")) {
                imagen.setDatos(file.getBytes());
            } else {
                imagen.setDatos(optimizeImage(file));
            }

            imagenServicio.crearImagenGasto(id, imagen);

            return "redirect:/imagen/cargadoGastoAdmin?id=" + id +"&desde=" + desde + "&hasta=" + hasta +
           (idChofer != null ? "&idChofer=" + idChofer : "") +
           (idCamion != null ? "&idCamion=" + idCamion : "") +
           (idCliente != null ? "&idCliente=" + idCliente : "");

        } catch (Exception e) {
            
            modelo.addAttribute("flete", flete);
            modelo.put("desde", desde);
            modelo.put("hasta", hasta);
            modelo.put("idChofer", idChofer);
            modelo.put("idCamion", idCamion);
            modelo.put("idCliente", idCliente);
            modelo.addAttribute("error", "Ocurrió un error al procesar su imagen. Intente nuevamente o ingrese otro archivo");
            
            return "imagen_gastoCargarAdmin.html";
        }

    }
    
    @GetMapping("/cargadoGastoAdmin")
    public String cargadoGastoAdmin(@RequestParam Long id, @RequestParam String desde, @RequestParam String hasta, @RequestParam(required = false) Long idChofer,
           @RequestParam(required = false) Long idCamion, @RequestParam(required = false) Long idCliente, ModelMap modelo) {

        Flete flete = fleteServicio.buscarFlete(id);
        
        modelo.put("idFlete", id);
        modelo.put("desde", desde);
        modelo.put("hasta", hasta);
        modelo.put("idChofer", idChofer);
        modelo.put("idCamion", idCamion);
        modelo.put("idCliente", idCliente);
        modelo.put("gasto", gastoServicio.buscarGasto(flete.getGasto().getId()));
        modelo.put("exito", "Imagen de Gasto CARGADA con éxito");

        return "gasto_mostrarAdmin.html";

    }

    @GetMapping("/cargarGastoCaja/{id}") //llega id de Gasto
    public String registrarDesdeCaja(@PathVariable Long id, ModelMap modelo) {

        modelo.put("gasto", gastoServicio.buscarGasto(id));

        return "imagen_gastoCargarCaja.html";
    }

    @PostMapping("/cargaGastoCaja")
    public String crearImagenCaja(@RequestParam Long id, @RequestParam("file") MultipartFile file, ModelMap modelo) throws IOException {

        Gasto gasto = gastoServicio.buscarGasto(id);

        try {

            Imagen imagen = new Imagen();
            imagen.setNombre("Gasto ID" + gasto.getIdGasto());
            imagen.setTipo(file.getContentType());

            if (file.getContentType().equals("application/pdf")) {
                imagen.setDatos(file.getBytes());

            } else {
                imagen.setDatos(optimizeImage(file));
            }

            if (!gasto.getNombre().startsWith("GASTO VJE")) {

                imagenServicio.crearImagenGastoCaja(id, imagen);

            } else {

                Long idFlete = fleteServicio.buscarIdFleteIdGasto(id);
                Flete flete = fleteServicio.buscarFlete(idFlete);
                imagen.setNombre("Gasto VJE ID" + flete.getIdFlete());
                imagenServicio.crearImagenGasto(idFlete, imagen);

            }

            return "redirect:/imagen/cargadoCaja/" + id;

        } catch (Exception e) {
            modelo.addAttribute("gasto", gasto);
            modelo.addAttribute("error", "Ocurrió un error al procesar su imagen. Intente nuevamente o ingrese otro archivo");
            return "imagen_gastoCargarCaja.html";
        }

    }

    @GetMapping("/cargadoCaja/{id}")
    public String cargadoCaja(@PathVariable Long id, ModelMap modelo) {

        modelo.put("gasto", gastoServicio.buscarGasto(id));
        modelo.put("exito", "Imagen de Gasto CARGADA con éxito");

        return "gasto_mostrarChoferCaja.html";

    }
    
    @PostMapping("/cargaGastoCajaAdmin")
    public String crearImagenCajaAdmin(@RequestParam Long id, @RequestParam("file") MultipartFile file, ModelMap modelo) throws IOException {

        Gasto gasto = gastoServicio.buscarGasto(id);

        try {

            Imagen imagen = new Imagen();
            imagen.setNombre("Gasto ID" + gasto.getIdGasto());
            imagen.setTipo(file.getContentType());

            if (file.getContentType().equals("application/pdf")) {
                imagen.setDatos(file.getBytes());

            } else {
                imagen.setDatos(optimizeImage(file));
            }

            if (!gasto.getNombre().startsWith("GASTO VJE")) {

                imagenServicio.crearImagenGastoCaja(id, imagen);

            } else {

                Long idFlete = fleteServicio.buscarIdFleteIdGasto(id);
                Flete flete = fleteServicio.buscarFlete(idFlete);
                imagen.setNombre("Gasto VJE ID" + flete.getIdFlete());
                imagenServicio.crearImagenGasto(idFlete, imagen);

            }

            return "redirect:/imagen/cargadoCajaAdmin/" + id;

        } catch (Exception e) {
            modelo.addAttribute("gasto", gasto);
            modelo.addAttribute("error", "Ocurrió un error al procesar su imagen. Intente nuevamente o ingrese otro archivo");
            return "imagen_gastoCargarCajaAdmin.html";
        }

    }
    
    @GetMapping("/cargadoCajaAdmin/{id}")
    public String cargadoCajaAdmin(@PathVariable Long id, ModelMap modelo) {

        modelo.put("gasto", gastoServicio.buscarGasto(id));
        modelo.put("exito", "Imagen de Gasto CARGADA con éxito");

        return "gasto_mostrarAdminCaja.html";

    }

    @GetMapping("/cargarCombustible/{id}")
    public String registrarCombustible(@PathVariable Long id, ModelMap modelo) {

        modelo.put("carga", combustibleServicio.buscarCombustible(id));

        return "imagen_combustibleCargar.html";
    }
    
    @PostMapping("/cargaCombustibleAdmin")
    public String cargarImagenCombustibleADmin(@RequestParam Long idCarga, @RequestParam String desde, @RequestParam String hasta, @RequestParam(required = false) Long idCamion,  
            @RequestParam(required = false) Long idChofer, @RequestParam("file") MultipartFile file, ModelMap modelo) throws IOException {

        Combustible carga = combustibleServicio.buscarCombustible(idCarga);

        try {

            Imagen imagen = new Imagen();
            imagen.setNombre("Carga de Diesel " + carga.getFechaCarga());
            imagen.setTipo(file.getContentType());
            if (file.getContentType().equals("application/pdf")) {
                imagen.setDatos(file.getBytes());
            } else {
                imagen.setDatos(optimizeImage(file));
            }

            imagenServicio.crearImagenCombustible(idCarga, imagen);

            return "redirect:/imagen/cargadoCombustibleAdmin?idCarga=" + idCarga +"&desde=" + desde + "&hasta=" + hasta +
           (idCamion != null ? "&idCamion=" + idCamion : "") +
           (idChofer != null ? "&idChofer=" + idChofer : "");

        } catch (Exception e) {
            
            modelo.addAttribute("carga", carga);
            modelo.put("desde", desde);
            modelo.put("hasta", hasta);
            modelo.put("idChofer", idChofer);
            modelo.put("idCamion", idCamion);
            modelo.addAttribute("error", "Ocurrió un error al procesar su imagen. Intente nuevamente o ingrese otro archivo");
           
            return "imagen_combustibleCargarAdmin.html";
        }
    }
    
    @GetMapping("/cargadoCombustibleAdmin")
    public String cargadoCombustible(@RequestParam Long idCarga, @RequestParam String desde, @RequestParam String hasta, @RequestParam(required = false) Long idCamion,  
            @RequestParam(required = false) Long idChofer, ModelMap modelo) {

        modelo.put("carga", combustibleServicio.buscarCombustible(idCarga));
        modelo.put("exito", "Imagen de Combustible CARGADA con éxito");
        modelo.put("desde", desde);
        modelo.put("hasta", hasta);
        modelo.put("idChofer", idChofer);
        modelo.put("idCamion", idCamion);

        return "combustible_mostrarAdmin.html";

    }

    @PostMapping("/cargaCombustible")
    public String cargarImagenCombustible(@RequestParam Long id, @RequestParam("file") MultipartFile file, ModelMap modelo) throws IOException {

        Combustible carga = combustibleServicio.buscarCombustible(id);

        try {

            Imagen imagen = new Imagen();
            imagen.setNombre("Carga de Diesel " + carga.getFechaCarga());
            imagen.setTipo(file.getContentType());
            if (file.getContentType().equals("application/pdf")) {
                imagen.setDatos(file.getBytes());
            } else {
                imagen.setDatos(optimizeImage(file));
            }

            imagenServicio.crearImagenCombustible(id, imagen);

            return "redirect:/imagen/cargadoCombustible/" + id;

        } catch (Exception e) {
            modelo.addAttribute("carga", carga);
            modelo.addAttribute("error", "Ocurrió un error al procesar su imagen. Intente nuevamente o ingrese otro archivo");
            return "imagen_combustibleCargar.html";
        }
    }

    @GetMapping("/cargadoCombustible/{id}")
    public String cargadoCombustible(@PathVariable Long id, ModelMap modelo) {

        Combustible carga = combustibleServicio.buscarCombustible(id);

        modelo.put("carga", carga);
        modelo.put("exito", "Imagen de Combustible CARGADA con éxito");

        return "combustible_mostrar.html";

    }

    @GetMapping("/cargarCPdesdeFlete/{id}") //llega id de Flete
    public String registrarCPdesdeFlete(@PathVariable Long id, ModelMap modelo) {

        Flete flete = fleteServicio.buscarFlete(id);

        modelo.put("flete", flete);

        return "imagen_CPcargarDesdeFlete.html";
    }

    @PostMapping("/cargaCPdesdeFlete")
    public String cargarImagenCPdesdeFlete(@RequestParam Long id, @RequestParam("file") MultipartFile file, ModelMap modelo) throws IOException {

        Flete flete = fleteServicio.buscarFlete(id);

        try {

            Imagen imagen = new Imagen();
            imagen.setNombre("CP VIAJE ID" + flete.getIdFlete());
            imagen.setTipo(file.getContentType());
            if (file.getContentType().equals("application/pdf")) {
                imagen.setDatos(file.getBytes());
            } else {
                imagen.setDatos(optimizeImage(file));
            }

            imagenServicio.crearImagenCP(id, imagen);

            return "redirect:/imagen/cargadoCPFlete/" + id;

        } catch (Exception e) {
            modelo.addAttribute("flete", flete);
            modelo.addAttribute("error", "Ocurrió un error al procesar su imagen. Intente nuevamente o ingrese otro archivo");
            return "imagen_CPcargarDesdeFlete.html";
        }

    }

    @GetMapping("/cargadoCPFlete/{id}")
    public String cargadoCPFlete(@PathVariable Long id, ModelMap modelo) {

        modelo.put("flete", fleteServicio.buscarFlete(id));

        return "imagen_descargaCargarDesdeFlete.html";

    }

    @PostMapping("/cargaCP")
    public String cargarImagenCP(@RequestParam Long id, @RequestParam("file") MultipartFile file, ModelMap modelo) throws IOException {

        Flete flete = fleteServicio.buscarFlete(id);

        try {

            Imagen imagen = new Imagen();
            imagen.setNombre("CP Viaje ID" + flete.getIdFlete());
            imagen.setTipo(file.getContentType());
            if (file.getContentType().equals("application/pdf")) {
                imagen.setDatos(file.getBytes());
            } else {
                imagen.setDatos(optimizeImage(file));
            }

            imagenServicio.crearImagenCP(id, imagen);

            return "redirect:/imagen/cargadoCPChofer/" + id;

        } catch (Exception e) {
            
            modelo.addAttribute("flete", flete);
            modelo.addAttribute("error", "Ocurrió un error al procesar su imagen. Intente nuevamente o ingrese otro archivo");
            return "imagen_CPcargar.html";
            
        }

    }
    
    @PostMapping("/cargaCPPendiente")
    public String cargarCPPendiente(@RequestParam Long id, @RequestParam("file") MultipartFile file, ModelMap modelo) throws IOException {

        Flete flete = fleteServicio.buscarFlete(id);

        try {

            Imagen imagen = new Imagen();
            imagen.setNombre("CP Viaje ID" + flete.getIdFlete());
            imagen.setTipo(file.getContentType());
            if (file.getContentType().equals("application/pdf")) {
                imagen.setDatos(file.getBytes());
            } else {
                imagen.setDatos(optimizeImage(file));
            }

            imagenServicio.crearImagenCP(id, imagen);

            return "redirect:/imagen/cargadoCPPendiente/" + id;


        } catch (Exception e) {
            
            modelo.addAttribute("flete", flete);
            modelo.addAttribute("error", "Ocurrió un error al procesar su imagen. Intente nuevamente o ingrese otro archivo");
            return "imagen_CPcargarPendiente.html";
        }

    }
    
    @PostMapping("/cargaCPAdmin")
    public String cargarImagenCPAdmin(@RequestParam Long id, @RequestParam String desde, @RequestParam String hasta, @RequestParam(required = false) Long idChofer,
           @RequestParam(required = false) Long idCamion, @RequestParam(required = false) Long idCliente, 
           @RequestParam("file") MultipartFile file, ModelMap modelo) throws IOException {

        Flete flete = fleteServicio.buscarFlete(id);

        try {

            Imagen imagen = new Imagen();
            imagen.setNombre("CP Viaje ID" + flete.getIdFlete());
            imagen.setTipo(file.getContentType());
            if (file.getContentType().equals("application/pdf")) {
                imagen.setDatos(file.getBytes());
            } else {
                imagen.setDatos(optimizeImage(file));
            }

            imagenServicio.crearImagenCP(id, imagen);

            return "redirect:/imagen/cargadoCP?id=" + id +"&desde=" + desde + "&hasta=" + hasta +
           (idChofer != null ? "&idChofer=" + idChofer : "") +
           (idCamion != null ? "&idCamion=" + idCamion : "") +
           (idCliente != null ? "&idCliente=" + idCliente : "");

        } catch (Exception e) {
            modelo.addAttribute("flete", flete);
            modelo.put("desde", desde);
            modelo.put("hasta", hasta);
            modelo.put("idChofer", idChofer);
            modelo.put("idCamion", idCamion);
            modelo.put("idCliente", idCliente);
            modelo.addAttribute("error", "Ocurrió un error al procesar su imagen. Intente nuevamente o ingrese otro archivo");
            
            return "imagen_CPcargarAdmin.html";
        }

    }
    
    @GetMapping("/cargadoCP")
    public String cargadoCP(@RequestParam Long id, @RequestParam String desde, @RequestParam String hasta, 
            @RequestParam(required = false) Long idChofer,  @RequestParam(required = false) Long idCamion, 
            @RequestParam(required = false) Long idCliente, ModelMap modelo) {

        modelo.put("flete", fleteServicio.buscarFlete(id));
        modelo.put("desde", desde);
        modelo.put("hasta", hasta);
        modelo.put("idChofer", idChofer);
        modelo.put("idCamion", idCamion);
        modelo.put("idCliente", idCliente);
        modelo.put("exito", "CP CARGADA con éxito");

        return "flete_mostrarAdmin.html";

    }

    @GetMapping("/cargadoCPChofer/{id}")
    public String cargadoCPChofer(@PathVariable Long id, ModelMap modelo) {

        Flete flete = fleteServicio.buscarFlete(id);

        modelo.put("flete", flete);
        modelo.put("exito", "CP CARGADA con éxito");

        return "flete_mostrarChofer.html";

    }

    @GetMapping("/cargadoCPAdmin/{id}")
    public String cargadoCPAdmin(@PathVariable Long id, ModelMap modelo) {

        Flete flete = fleteServicio.buscarFlete(id);

        modelo.put("flete", flete);
        modelo.put("exito", "CP CARGADA con éxito");

        return "flete_mostrarPendiente.html";

    }
    
    @GetMapping("/cargadoCPPendiente/{id}")
    public String cargadoCPPendiente(@PathVariable Long id, ModelMap modelo) {

        Flete flete = fleteServicio.buscarFlete(id);

        modelo.put("flete", flete);
        modelo.put("exito", "CP CARGADA con éxito");

        return "flete_mostrarPendiente.html";

    }

    @PostMapping("/cargaDescarga")
    public String cargarImagenDescarga(@RequestParam Long id, @RequestParam("file") MultipartFile file, ModelMap modelo) throws IOException {
        
        Flete flete = fleteServicio.buscarFlete(id);

        try {

            Imagen imagen = new Imagen();
            imagen.setNombre("Descarga Viaje ID" + flete.getIdFlete());
            imagen.setTipo(file.getContentType());
            if (file.getContentType().equals("application/pdf")) {
                imagen.setDatos(file.getBytes());
            } else {
                imagen.setDatos(optimizeImage(file));
            }

            imagenServicio.crearImagenDescarga(id, imagen);

                return "redirect:/imagen/cargadoDescargaChofer/" + id;

        } catch (Exception e) {
            modelo.addAttribute("flete", flete);
            modelo.addAttribute("error", "Ocurrió un error al procesar su imagen. Intente nuevamente o ingrese otro archivo");
            return "imagen_descargaCargar.html";
        }
    }

    @GetMapping("/cargadoDescargaChofer/{id}")
    public String cargadoDescargaChofer(@PathVariable Long id, ModelMap modelo) {

        Flete flete = fleteServicio.buscarFlete(id);

        modelo.put("flete", flete);
        modelo.put("exito", "Ticket de Descarga CARGADO con éxito");

        return "flete_mostrarChofer.html";

    }

    @GetMapping("/cargadoDescargaAdmin/{id}")
    public String cargadoDescargaAdmin(@PathVariable Long id, ModelMap modelo) {

        Flete flete = fleteServicio.buscarFlete(id);

        modelo.put("flete", flete);
        modelo.put("fecha", flete.getFechaFlete());
        modelo.put("exito", "Ticket de Descarga CARGADO con éxito");

        return "flete_modificadoAdmin.html";

    }
    
    @PostMapping("/cargaDescargaAdmin")
    public String cargarImagenDescargaAdmin(@RequestParam Long id, @RequestParam String desde, @RequestParam String hasta, @RequestParam(required = false) Long idChofer,
           @RequestParam(required = false) Long idCamion, @RequestParam(required = false) Long idCliente, 
           @RequestParam("file") MultipartFile file, ModelMap modelo) throws IOException {
        
        Flete flete = fleteServicio.buscarFlete(id);

        try {

            Imagen imagen = new Imagen();
            imagen.setNombre("Descarga Viaje ID" + flete.getIdFlete());
            imagen.setTipo(file.getContentType());
            if (file.getContentType().equals("application/pdf")) {
                imagen.setDatos(file.getBytes());
            } else {
                imagen.setDatos(optimizeImage(file));
            }

            imagenServicio.crearImagenDescarga(id, imagen);

                return "redirect:/imagen/cargadoDescarga?id=" + id +"&desde=" + desde + "&hasta=" + hasta +
           (idChofer != null ? "&idChofer=" + idChofer : "") +
           (idCamion != null ? "&idCamion=" + idCamion : "") +
           (idCliente != null ? "&idCliente=" + idCliente : "");

        } catch (Exception e) {
            
            modelo.addAttribute("flete", flete);
            modelo.put("desde", desde);
            modelo.put("hasta", hasta);
            modelo.put("idChofer", idChofer);
            modelo.put("idCamion", idCamion);
            modelo.put("idCliente", idCliente);
            modelo.addAttribute("error", "Ocurrió un error al procesar su imagen. Intente nuevamente o ingrese otro archivo");
            
            return "imagen_descargaCargarAdmin.html";
        }
    }
    
    @GetMapping("/cargadoDescarga")
    public String cargadoDescarga(@RequestParam Long id, @RequestParam String desde, @RequestParam String hasta, 
            @RequestParam(required = false) Long idChofer,  @RequestParam(required = false) Long idCamion, 
            @RequestParam(required = false) Long idCliente, ModelMap modelo) {
        
        modelo.put("flete", fleteServicio.buscarFlete(id));
        modelo.put("desde", desde);
        modelo.put("hasta", hasta);
        modelo.put("idChofer", idChofer);
        modelo.put("idCamion", idCamion);
        modelo.put("idCliente", idCliente);
        modelo.put("exito", "Ticket de Descarga CARGADO con éxito");

        return "flete_mostrarAdmin.html";

    }

    @PostMapping("/cargaDescargaPendiente")
    public String cargarImagenDescargaPendiente(@RequestParam Long id, @RequestParam("file") MultipartFile file, ModelMap modelo) throws IOException {

        Flete flete = fleteServicio.buscarFlete(id);

        try {

            Imagen imagen = new Imagen();
            imagen.setNombre("Descarga Viaje ID" + flete.getIdFlete());
            imagen.setTipo(file.getContentType());
            if (file.getContentType().equals("application/pdf")) {
                imagen.setDatos(file.getBytes());
            } else {
                imagen.setDatos(optimizeImage(file));
            }

            imagenServicio.crearImagenDescarga(id, imagen);

            return "redirect:/imagen/cargadoDescargaPendiente/" + id;

        } catch (Exception e) {
            
            modelo.addAttribute("flete", flete);
            modelo.addAttribute("error", "Ocurrió un error al procesar su imagen. Intente nuevamente o ingrese otro archivo");
            
            return "imagen_descargaCargarPendiente.html";
        }

    }

    @GetMapping("/cargadoDescargaPendiente/{id}")
    public String descargaPendiente(@PathVariable Long id, ModelMap modelo) {

        Flete flete = fleteServicio.buscarFlete(id);

        modelo.put("flete", flete);
        modelo.put("exito", "Ticket de Descarga CARGADO con éxito");

        return "flete_mostrarPendiente.html";

    }

    @GetMapping("/verGasto/{id}")
    public String verGasto(@PathVariable Long id, ModelMap modelo, HttpSession session) {

        Flete flete = fleteServicio.buscarFlete(id);
        Gasto gasto = flete.getGasto();
        modelo.put("idGasto", gasto.getId());
        modelo.put("flete", flete);
        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        if (gasto.getImagen() != null) {

            Long idImagen = gasto.getImagen().getId();
            Imagen imagen = imagenServicio.obtenerImagenPorId(idImagen);
            
            if ((logueado.getRol().equalsIgnoreCase("CHOFER")) && (!imagen.getTipo().equalsIgnoreCase("application/pdf"))) {
                
                modelo.addAttribute("imagenUrl", "/imagen/img/bytes/" + idImagen);
                modelo.addAttribute("imagenNombre", imagen.getNombre());
                modelo.addAttribute("id", idImagen);
                modelo.addAttribute("estado", gasto.getEstado());

                return "imagen_mostrarGasto.html";
                
            }  else if ((logueado.getRol().equalsIgnoreCase("ADMIN")) && (!imagen.getTipo().equalsIgnoreCase("application/pdf"))) {
                
                modelo.addAttribute("imagenUrl", "/imagen/img/bytes/" + idImagen);
                modelo.addAttribute("imagenNombre", imagen.getNombre());
                modelo.addAttribute("id", idImagen);

                return "imagen_mostrarGastoPendiente.html";
            
                
            } else if ((logueado.getRol().equalsIgnoreCase("CHOFER")) && (imagen.getTipo().equalsIgnoreCase("application/pdf"))) {
                
                modelo.addAttribute("imagenNombre", imagen.getNombre());
                modelo.addAttribute("id", idImagen);
                modelo.addAttribute("estado", gasto.getEstado());

                return "imagen_mostrarGastoPdf.html";
                
            }  else {
                
                modelo.addAttribute("imagenNombre", imagen.getNombre());
                modelo.addAttribute("id", idImagen);

                return "imagen_mostrarGastoPdfPendiente.html";
                
            }

        } else {
            
            if (logueado.getRol().equalsIgnoreCase("CHOFER")) {

            return "imagen_gastoCargar.html";
            
            } else {
                
            return "imagen_gastoCargarPendiente.html";
                
            }

        }
    }

    @GetMapping("/verGastoAdmin")
    public String verGastoAdmin(@RequestParam Long id, @RequestParam Long idFlete, @RequestParam String desde, @RequestParam String hasta, 
           @RequestParam(required = false) Long idChofer, @RequestParam(required = false) Long idCamion, @RequestParam(required = false) Long idCliente, ModelMap modelo) {

        Gasto gasto = gastoServicio.buscarGasto(id);
        
        modelo.put("flete", fleteServicio.buscarFlete(idFlete));
        modelo.put("desde", desde);
        modelo.put("hasta", hasta);
        modelo.put("idChofer", idChofer);
        modelo.put("idCamion", idCamion);
        modelo.put("idCliente", idCliente);

        if (gasto.getImagen() != null) {

            Long idImagen = gasto.getImagen().getId();
            Imagen imagen = imagenServicio.obtenerImagenPorId(idImagen);
            
                
            if (!imagen.getTipo().equalsIgnoreCase("application/pdf")) {
                
                modelo.addAttribute("imagenUrl", "/imagen/img/bytes/" + idImagen);
                modelo.addAttribute("imagenNombre", imagen.getNombre());
                modelo.addAttribute("id", idImagen);

                return "imagen_mostrarGastoAdmin.html";
                
            }  else {
                
                modelo.addAttribute("imagenNombre", imagen.getNombre());
                modelo.addAttribute("id", idImagen);

                return "imagen_mostrarGastoPdfAdmin.html";
                
            }

        } else {

            return "imagen_gastoCargarAdmin.html";

        }
    }

    @GetMapping("/verGastoDesdeCamion/{id}")
    public String verGastoDesdeCamion(@PathVariable Long id, ModelMap modelo) {

        Flete flete = fleteServicio.buscarFleteIdGasto(id);
        Gasto gasto = gastoServicio.buscarGasto(id);

        if (gasto.getImagen() != null) {

            Long idImagen = gasto.getImagen().getId();
            Imagen imagen = imagenServicio.obtenerImagenPorId(idImagen);

            if (imagen.getTipo().equalsIgnoreCase("application/pdf")) {

                modelo.addAttribute("imagenNombre", imagen.getNombre());
                modelo.addAttribute("id", idImagen);

                return "imagen_mostrarGastoPdfAdmin.html";

            } else {

                modelo.addAttribute("imagenUrl", "/imagen/img/bytes/" + idImagen);
                modelo.addAttribute("imagenNombre", imagen.getNombre());
                modelo.addAttribute("id", idImagen);

                return "imagen_mostrarGastoAdmin.html";

            }

        } else {

            modelo.put("flete", flete);

            return "imagen_gastoCargar.html";

        }
    }

    @GetMapping("/verGastoCaja/{id}")
    public String verGastoCaja(@PathVariable Long id, ModelMap modelo, HttpSession session) {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");
        Gasto gasto = gastoServicio.buscarGasto(id);
        modelo.put("gasto", gasto);

        if (gasto.getImagen() != null) {

            Long idImagen = gasto.getImagen().getId();
            Imagen imagen = imagenServicio.obtenerImagenPorId(idImagen);
            modelo.addAttribute("imagenUrl", "/imagen/img/bytes/" + idImagen);
            modelo.addAttribute("imagenNombre", imagen.getNombre());
            modelo.addAttribute("id", idImagen);
            modelo.addAttribute("estado", gasto.getEstado());
            
            
            
            if ((logueado.getRol().equalsIgnoreCase("CHOFER")) && (!imagen.getTipo().equalsIgnoreCase("application/pdf"))) {

                return "imagen_mostrarGastoCaja.html";
                
            } else if ((logueado.getRol().equalsIgnoreCase("ADMIN")) && (!imagen.getTipo().equalsIgnoreCase("application/pdf"))) {    

                return "imagen_mostrarGastoCajaAdmin.html";
            
                
           } else if ((logueado.getRol().equalsIgnoreCase("CHOFER")) && (imagen.getTipo().equalsIgnoreCase("application/pdf"))) {

                return "imagen_mostrarGastoPdfCaja.html";

            } else {

               return "imagen_mostrarGastoPdfCajaAdmin.html";

            }

        } else {
            
            if (logueado.getRol().equalsIgnoreCase("CHOFER")) {


            return "imagen_gastoCargarCaja.html";
            
            } else {

            return "imagen_gastoCargarCajaAdmin.html";    
            }
                

        }
    }
    
    @GetMapping("/descargarGastoPdfAdmin")
    public String descargarGastoPdfAdmin(@RequestParam Long id, @RequestParam Long idFlete, @RequestParam String desde, @RequestParam String hasta, 
           @RequestParam(required = false) Long idChofer, @RequestParam(required = false) Long idCamion, @RequestParam(required = false) Long idCliente, ModelMap modelo) {

        Imagen imagen = imagenServicio.obtenerImagenPorId(id);

        modelo.addAttribute("imagenUrl", "/imagen/pdf/" + id);
        modelo.addAttribute("imagenNombre", imagen.getNombre());
        modelo.addAttribute("id", id);
        modelo.put("idFlete", idFlete);
        modelo.put("desde", desde);
        modelo.put("hasta", hasta);
        modelo.put("idChofer", idChofer);
        modelo.put("idCamion", idCamion);
        modelo.put("idCliente", idCliente);

        return "imagen_descargarGastoPdfAdmin.html";
    }
    
    @GetMapping("/descargarGastoPdfPendiente")
    public String descargarGastoPdfPendiente(@RequestParam Long id, @RequestParam Long idFlete, ModelMap modelo) {

        Imagen imagen = imagenServicio.obtenerImagenPorId(id);

        modelo.addAttribute("imagenUrl", "/imagen/pdf/" + id);
        modelo.addAttribute("imagenNombre", imagen.getNombre());
        modelo.addAttribute("id", id);
        modelo.put("idFlete", idFlete);

        return "imagen_descargarGastoPdfPendiente.html";
    }

    @GetMapping("/descargarGastoPdf")
    public String descargarGastoPdf(@RequestParam Long id, @RequestParam Long idFlete, ModelMap modelo) {

        Imagen imagen = imagenServicio.obtenerImagenPorId(id);

        modelo.addAttribute("imagenUrl", "/imagen/pdf/" + id);
        modelo.addAttribute("imagenNombre", imagen.getNombre());
        modelo.addAttribute("id", id);
        modelo.addAttribute("idFlete", idFlete);

        return "imagen_descargarGastoPdf.html";
    }
    
    @GetMapping("/descargarGastoPdfCaja")
    public String descargarGastoPdfCaja(@RequestParam Long id, @RequestParam Long idGasto, ModelMap modelo) {

        Imagen imagen = imagenServicio.obtenerImagenPorId(id);

        modelo.addAttribute("imagenUrl", "/imagen/pdf/" + id);
        modelo.addAttribute("imagenNombre", imagen.getNombre());
        modelo.addAttribute("id", id);
        modelo.addAttribute("idGasto", idGasto);

        return "imagen_descargarGastoPdfCaja.html";
    }
    
    @GetMapping("/descargarGastoPdfCajaAdmin")
    public String descargarGastoPdfCajaAdmin(@RequestParam Long id, @RequestParam Long idGasto, ModelMap modelo) {

        Imagen imagen = imagenServicio.obtenerImagenPorId(id);

        modelo.addAttribute("imagenUrl", "/imagen/pdf/" + id);
        modelo.addAttribute("imagenNombre", imagen.getNombre());
        modelo.addAttribute("id", id);
        modelo.addAttribute("idGasto", idGasto);

        return "imagen_descargarGastoPdfCajaAdmin.html";
    }
    
    @GetMapping("/modificarGastoAdmin") //llega id de Imagen
    public String modificarGastoAdmin(@RequestParam Long id, @RequestParam Long idFlete, @RequestParam String desde, @RequestParam String hasta, 
           @RequestParam(required = false) Long idChofer, @RequestParam(required = false) Long idCamion, @RequestParam(required = false) Long idCliente, ModelMap modelo) {

        modelo.put("id", id);
        modelo.put("idFlete", idFlete);
        modelo.put("desde", desde);
        modelo.put("hasta", hasta);
        modelo.put("idChofer", idChofer);
        modelo.put("idCamion", idCamion);
        modelo.put("idCliente", idCliente);

        return "imagen_gastoModificarAdmin.html";
    }
    
    @PostMapping("/modificaGastoAdmin")
    public String modificaGastoAdmin(@RequestParam Long id, @RequestParam Long idFlete, @RequestParam String desde, @RequestParam String hasta, 
           @RequestParam(required = false) Long idChofer, @RequestParam(required = false) Long idCamion, @RequestParam(required = false) Long idCliente, 
           @RequestParam("file") MultipartFile file, ModelMap modelo) throws IOException {

        Flete flete = fleteServicio.buscarFlete(idFlete);

        try {

            Imagen imagen = new Imagen();
            imagen.setNombre("Gasto VJE ID" + flete.getIdFlete());
            imagen.setTipo(file.getContentType());
            if (file.getContentType().equals("application/pdf")) {
                imagen.setDatos(file.getBytes());
            } else {
                imagen.setDatos(optimizeImage(file));
            }

            imagenServicio.modificarImagen(id, imagen);

            return "redirect:/imagen/modificadoGastoAdmin?idFlete=" + idFlete +"&desde=" + desde + "&hasta=" + hasta +
           (idChofer != null ? "&idChofer=" + idChofer : "") +
           (idCamion != null ? "&idCamion=" + idCamion : "") +
           (idCliente != null ? "&idCliente=" + idCliente : "");

        } catch (Exception e) {
            
        modelo.addAttribute("id", id);
        modelo.put("idFlete", idFlete);
        modelo.put("desde", desde);
        modelo.put("hasta", hasta);
        modelo.put("idChofer", idChofer);
        modelo.put("idCamion", idCamion);
        modelo.put("idCliente", idCliente);
            modelo.addAttribute("error", "Ocurrió un error al procesar su imagen. Intente nuevamente o ingrese otro archivo");
            
            return "imagen_gastoModificarAdmin.html";
        }
    }
    
    @GetMapping("/modificadoGastoAdmin")
    public String modificadoGastoAdmin(@RequestParam Long idFlete, @RequestParam String desde, @RequestParam String hasta, 
           @RequestParam(required = false) Long idChofer, @RequestParam(required = false) Long idCamion, 
           @RequestParam(required = false) Long idCliente, ModelMap modelo) {

        Flete flete = fleteServicio.buscarFlete(idFlete);
        modelo.put("idFlete", idFlete);
        modelo.put("desde", desde);
        modelo.put("hasta", hasta);
        modelo.put("idChofer", idChofer);
        modelo.put("idCamion", idCamion);
        modelo.put("idCliente", idCliente);
        modelo.put("gasto", gastoServicio.buscarGasto(flete.getGasto().getId()));
        modelo.put("exito", "Imagen de Gasto MODIFICADA con éxito");

        return "gasto_mostrarAdmin.html";

    }
    
    @GetMapping("/modificarGastoPendiente") //llega id de Imagen
    public String modificarGastoPendiente(@RequestParam Long id, @RequestParam Long idFlete, ModelMap modelo) {

        modelo.put("id", id);
        modelo.put("idFlete", idFlete);

        return "imagen_gastoModificarPendiente.html";
    }
    
    @PostMapping("/modificaGastoPendiente")
    public String modificaGastoPendiente(@RequestParam Long id, @RequestParam Long idFlete, @RequestParam("file") MultipartFile file, ModelMap modelo) throws IOException {

        Flete flete = fleteServicio.buscarFlete(idFlete);

        try {

            Imagen imagen = new Imagen();
            imagen.setNombre("Gasto VJE ID" + flete.getIdFlete());
            imagen.setTipo(file.getContentType());
            if (file.getContentType().equals("application/pdf")) {
                imagen.setDatos(file.getBytes());
            } else {
                imagen.setDatos(optimizeImage(file));
            }

            imagenServicio.modificarImagen(id, imagen);

            return "redirect:/imagen/modificadoGastoPendiente/" + idFlete;

        } catch (Exception e) {
            modelo.addAttribute("id", id);
            modelo.put("flete", fleteServicio.buscarFleteIdGasto(id));
            modelo.addAttribute("error", "Ocurrió un error al procesar su imagen. Intente nuevamente o ingrese otro archivo");
            
            return "imagen_gastoModificarPendiente.html";
        }

    }
    
    @GetMapping("/modificadoGastoPendiente/{id}")
    public String modificadoGastoPendiente(@PathVariable Long id, ModelMap modelo) {
        
        Flete flete = fleteServicio.buscarFlete(id);
        modelo.put("gasto", flete.getGasto());
        modelo.put("idFlete", id);
        modelo.put("exito", "Imagen de Gasto MODIFICADO con éxito");

        return "gasto_mostrarPendiente.html";

    }
    
    @GetMapping("/modificarGasto") //llega id de Imagen
    public String modificarGasto(@RequestParam Long id, @RequestParam Long idFlete, ModelMap modelo) {

        modelo.put("id", id);
        modelo.put("idFlete", idFlete);

        return "imagen_gastoModificar.html";
    }

    @PostMapping("/modificaGasto")
    public String modificaGasto(@RequestParam Long id, @RequestParam Long idFlete, @RequestParam("file") MultipartFile file, ModelMap modelo) throws IOException {

        Flete flete = fleteServicio.buscarFlete(idFlete);

        try {

            Imagen imagen = new Imagen();
            imagen.setNombre("Gasto VJE ID" + flete.getIdFlete());
            imagen.setTipo(file.getContentType());
            if (file.getContentType().equals("application/pdf")) {
                imagen.setDatos(file.getBytes());
            } else {
                imagen.setDatos(optimizeImage(file));
            }

            imagenServicio.modificarImagen(id, imagen);

            return "redirect:/imagen/modificadoGasto/" + idFlete;

        } catch (Exception e) {
            modelo.addAttribute("id", id);
            modelo.put("idFlete", idFlete);
            modelo.addAttribute("error", "Ocurrió un error al procesar su imagen. Intente nuevamente o ingrese otro archivo");
            return "imagen_gastoModificar.html";
        }

    }

    @GetMapping("/modificadoGasto/{idFlete}")
    public String modificadoGasto(@PathVariable Long idFlete, ModelMap modelo) {

        Flete flete = fleteServicio.buscarFlete(idFlete);
        
        modelo.put("idFlete", idFlete);
        modelo.put("gasto", flete.getGasto());
        modelo.put("exito", "Imagen de Gasto MODIFICADA con éxito");

        return "gasto_mostrarChofer.html";

    }

    @GetMapping("/modificarGastoCaja") //llega id de Imagen
    public String modificarGastoCaja(@RequestParam Long id, @RequestParam Long idGasto, ModelMap modelo) {

        modelo.put("id", id);
        modelo.put("idGasto", idGasto);

        return "imagen_gastoModificarCaja.html";
    }

    @PostMapping("/modificaGastoCaja")
    public String modificaGastoCaja(@RequestParam Long id, @RequestParam Long idGasto, @RequestParam("file") MultipartFile file, ModelMap modelo) throws IOException {

        Gasto gasto = gastoServicio.buscarGasto(idGasto);

        try {

            Imagen imagen = new Imagen();

            imagen.setTipo(file.getContentType());
            if (file.getContentType().equals("application/pdf")) {
                imagen.setDatos(file.getBytes());
            } else {
                imagen.setDatos(optimizeImage(file));
            }
            imagen.setNombre("GASTO ID" + gasto.getIdGasto());
            imagenServicio.modificarImagen(id, imagen);

            return "redirect:/imagen/modificadoGastoCaja/" + idGasto;

        } catch (Exception e) {
            modelo.addAttribute("id", id);
            modelo.put("idGasto", idGasto);
            modelo.addAttribute("error", "Ocurrió un error al procesar su imagen. Intente nuevamente o ingrese otro archivo");
            return "imagen_gastoModificarCaja.html";
        }

    }

    @GetMapping("/modificadoGastoCaja/{idGasto}")
    public String modificadoGastoCaja(@PathVariable Long idGasto, ModelMap modelo) {

        modelo.put("gasto", gastoServicio.buscarGasto(idGasto));
        modelo.put("exito", "Imagen de Gasto MODIFICADO con éxito");

        return "gasto_mostrarChoferCaja.html";

    }
    
    @GetMapping("/modificarGastoCajaAdmin") //llega id de Imagen
    public String modificarGastoCajaAdmin(@RequestParam Long id, @RequestParam Long idGasto, ModelMap modelo) {

        modelo.put("id", id);
        modelo.put("idGasto", idGasto);

        return "imagen_gastoModificarCajaAdmin.html";
    }

    @PostMapping("/modificaGastoCajaAdmin")
    public String modificaGastoCajaAdmin(@RequestParam Long id, @RequestParam Long idGasto, @RequestParam("file") MultipartFile file, ModelMap modelo) throws IOException {

        Gasto gasto = gastoServicio.buscarGasto(idGasto);

        try {

            Imagen imagen = new Imagen();

            imagen.setTipo(file.getContentType());
            if (file.getContentType().equals("application/pdf")) {
                imagen.setDatos(file.getBytes());
            } else {
                imagen.setDatos(optimizeImage(file));
            }
            imagen.setNombre("GASTO ID" + gasto.getIdGasto());
            imagenServicio.modificarImagen(id, imagen);

            return "redirect:/imagen/modificadoGastoCajaAdmin/" + idGasto;

        } catch (Exception e) {
            modelo.addAttribute("id", id);
            modelo.put("idGasto", idGasto);
            modelo.addAttribute("error", "Ocurrió un error al procesar su imagen. Intente nuevamente o ingrese otro archivo");
            return "imagen_gastoModificarCajaAdmin.html";
        }

    }

    @GetMapping("/modificadoGastoCajaAdmin/{idGasto}")
    public String modificadoGastoCajaAdmin(@PathVariable Long idGasto, ModelMap modelo) {

        modelo.put("gasto", gastoServicio.buscarGasto(idGasto));
        modelo.put("exito", "Imagen de Gasto MODIFICADO con éxito");

        return "gasto_mostrarAdminCaja.html";

    }
    
    @GetMapping("/eliminarGastoAdmin")
    public String eliminarGastoAdmin(@RequestParam Long id, @RequestParam Long idFlete, @RequestParam String desde, @RequestParam String hasta, 
           @RequestParam(required = false) Long idChofer, @RequestParam(required = false) Long idCamion, @RequestParam(required = false) Long idCliente, ModelMap model) {

        Imagen imagen = imagenServicio.obtenerImagenPorId(id);

        model.addAttribute("imagenNombre", imagen.getNombre());
        model.addAttribute("id", id);
        model.put("idFlete", idFlete);
        model.put("desde", desde);
        model.put("hasta", hasta);
        model.put("idChofer", idChofer);
        model.put("idCamion", idCamion);
        model.put("idCliente", idCliente);

        return "imagen_eliminarGastoAdmin.html";
    }
    
    @GetMapping("/eliminaGastoAdmin")
    public String eliminaGastoAdmin(@RequestParam Long id, @RequestParam Long idFlete, @RequestParam String desde, @RequestParam String hasta, 
           @RequestParam(required = false) Long idChofer, @RequestParam(required = false) Long idCamion, @RequestParam(required = false) Long idCliente, ModelMap modelo) {

        imagenServicio.eliminarImagenGasto(id);

        return "redirect:/imagen/eliminadoGasto?idFlete=" + idFlete +"&desde=" + desde + "&hasta=" + hasta +
           (idChofer != null ? "&idChofer=" + idChofer : "") +
           (idCamion != null ? "&idCamion=" + idCamion : "") +
           (idCliente != null ? "&idCliente=" + idCliente : "");

    }
    
    @GetMapping("/eliminadoGasto")
    public String eliminadoGasto(@RequestParam Long idFlete, @RequestParam String desde, @RequestParam String hasta, 
           @RequestParam(required = false) Long idChofer, @RequestParam(required = false) Long idCamion, 
           @RequestParam(required = false) Long idCliente,ModelMap modelo) {

        Flete flete = fleteServicio.buscarFlete(idFlete);
        modelo.put("idFlete", idFlete);
        modelo.put("desde", desde);
        modelo.put("hasta", hasta);
        modelo.put("idChofer", idChofer);
        modelo.put("idCamion", idCamion);
        modelo.put("idCliente", idCliente);
        modelo.put("gasto", gastoServicio.buscarGasto(flete.getGasto().getId()));
        modelo.put("exito", "Imagen de Gasto ELIMINADA con éxito");

        return "gasto_mostrarAdmin.html";

    }
    
    @GetMapping("/eliminarGastoPendiente")
    public String eliminarGastoPendiente(@RequestParam Long id, @RequestParam Long idFlete, ModelMap model) {

        Imagen imagen = imagenServicio.obtenerImagenPorId(id);

        model.addAttribute("imagenNombre", imagen.getNombre());
        model.addAttribute("id", id);
        model.addAttribute("idFlete", idFlete);

        return "imagen_eliminarGastoPendiente.html";
    }
    
    @GetMapping("/eliminaGastoPendiente")
    public String eliminaGastoPendiente(@RequestParam Long id,@RequestParam Long idFlete, ModelMap modelo) {

        imagenServicio.eliminarImagenGasto(id);

        return "redirect:/imagen/eliminadoGastoPendiente/" +idFlete;

    }

    @GetMapping("/eliminarGasto")
    public String eliminarGasto(@RequestParam Long id, @RequestParam Long idFlete, ModelMap model) {

        Imagen imagen = imagenServicio.obtenerImagenPorId(id);

        model.addAttribute("imagenNombre", imagen.getNombre());
        model.addAttribute("id", id);
        model.put("idFlete", idFlete);

        return "imagen_eliminarGasto.html";
    }
    
    @GetMapping("/eliminarGastoCaja")
    public String eliminarGastoCaja(@RequestParam Long id, @RequestParam Long idGasto, ModelMap model) {

        Imagen imagen = imagenServicio.obtenerImagenPorId(id);

        model.addAttribute("imagenNombre", imagen.getNombre());
        model.addAttribute("id", id);
        model.put("idGasto", idGasto);

        return "imagen_eliminarGastoCaja.html";
    }
    
    @GetMapping("/eliminaGastoCaja")
    public String eliminaGastoCaja(@RequestParam Long id, @RequestParam Long idGasto, ModelMap modelo) {

        imagenServicio.eliminarImagenGasto(id);

            return "redirect:/imagen/eliminadoGastoCaja/" +idGasto;
    }
    
    @GetMapping("/eliminadoGastoCaja/{idGasto}")
    public String eliminadoGastoCaja(@PathVariable Long idGasto, ModelMap modelo, HttpSession session) {
        
        modelo.put("gasto", gastoServicio.buscarGasto(idGasto));
        modelo.put("exito", "Imagen de Gasto ELIMINADA con éxito");

        return "gasto_mostrarChoferCaja.html";

    }
    
    @GetMapping("/eliminarGastoCajaAdmin")
    public String eliminarGastoCajaAdmin(@RequestParam Long id, @RequestParam Long idGasto, ModelMap model) {

        Imagen imagen = imagenServicio.obtenerImagenPorId(id);

        model.addAttribute("imagenNombre", imagen.getNombre());
        model.addAttribute("id", id);
        model.put("idGasto", idGasto);

        return "imagen_eliminarGastoCajaAdmin.html";
    }
    
    @GetMapping("/eliminaGastoCajaAdmin")
    public String eliminaGastoCajaAdmin(@RequestParam Long id, @RequestParam Long idGasto, ModelMap modelo) {

        imagenServicio.eliminarImagenGasto(id);

            return "redirect:/imagen/eliminadoGastoCajaAdmin/" +idGasto;
    }
    
    @GetMapping("/eliminadoGastoCajaAdmin/{idGasto}")
    public String eliminadoGastoCajaAdmin(@PathVariable Long idGasto, ModelMap modelo, HttpSession session) {
        
        modelo.put("gasto", gastoServicio.buscarGasto(idGasto));
        modelo.put("exito", "Imagen de Gasto ELIMINADA con éxito");

        return "gasto_mostrarChoferCaja.html";

    }
    
    @GetMapping("/eliminadoGastoPendiente/{id}")
    public String eliminadoGastoPendiente(@PathVariable Long id, ModelMap modelo) {

        Flete flete = fleteServicio.buscarFlete(id);
        modelo.put("gasto", flete.getGasto());
        modelo.put("idFlete", id);
        modelo.put("exito", "Imagen de Gasto ELIMINADA con éxito");

        return "gasto_mostrarPendiente.html";
        

    }

    @GetMapping("/eliminaGasto")
    public String eliminaGasto(@RequestParam Long id, @RequestParam Long idFlete, ModelMap modelo) {

        imagenServicio.eliminarImagenGasto(id);

            return "redirect:/imagen/eliminadoGastoChofer/" +idFlete;
    }

    @GetMapping("/eliminadoGastoChofer/{idFlete}")
    public String eliminadoGastoChofer(@PathVariable Long idFlete, ModelMap modelo, HttpSession session) {

        Flete flete = fleteServicio.buscarFlete(idFlete);
        
        modelo.put("idFlete", idFlete);
        modelo.put("gasto", flete.getGasto());
        modelo.put("exito", "Imagen de Gasto ELIMINADA con éxito");

        return "gasto_mostrarChofer.html";

    }

    @GetMapping("/eliminadoGastoAdmin")
    public String eliminadoGastoAdmin(ModelMap modelo, HttpSession session) {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        modelo.put("id", logueado.getId());
        modelo.put("exito", "Imagen de Gasto ELIMINADA con éxito");

        return "index_admin.html";

    }

    @GetMapping("/verCP/{id}")
    public String verCP(@PathVariable Long id, Model model) {

        Flete flete = fleteServicio.buscarFlete(id);
        model.addAttribute("flete", flete);

        if (flete.getImagenCP() != null) {

            Long idImagen = flete.getImagenCP().getId();
            Imagen imagen = imagenServicio.obtenerImagenPorId(idImagen);

            if (imagen.getTipo().equalsIgnoreCase("application/pdf")) {

                model.addAttribute("imagenNombre", imagen.getNombre());
                model.addAttribute("id", idImagen);
                model.addAttribute("estado", flete.getEstado());

                return "imagen_mostrarCPpdf.html";

            } else {

                model.addAttribute("imagenUrl", "/imagen/img/bytes/" + idImagen);
                model.addAttribute("imagenNombre", imagen.getNombre());
                model.addAttribute("id", idImagen);
                model.addAttribute("estado", flete.getEstado());

                return "imagen_mostrarCP.html";

            }

        } else {

            return "imagen_CPcargar.html";

        }
    }
    
    @GetMapping("/verCPAdmin")
    public String verCPAdmin(@RequestParam Long id, @RequestParam String desde, @RequestParam String hasta, @RequestParam(required = false) Long idChofer,
    @RequestParam(required = false) Long idCamion, @RequestParam(required = false) Long idCliente, Model model) {

        Flete flete = fleteServicio.buscarFlete(id);
        
        model.addAttribute("flete", flete);
        model.addAttribute("desde", desde);
        model.addAttribute("hasta", hasta);
        model.addAttribute("idChofer", idChofer);
        model.addAttribute("idCamion", idCamion);
        model.addAttribute("idCliente", idCliente);

        if (flete.getImagenCP() != null) {

            Long idImagen = flete.getImagenCP().getId();
            Imagen imagen = imagenServicio.obtenerImagenPorId(idImagen);

         if (imagen.getTipo().equalsIgnoreCase("application/pdf")) {

                model.addAttribute("imagenNombre", imagen.getNombre());
                model.addAttribute("id", idImagen);

                return "imagen_mostrarCPpdfAdmin.html";

        } else {
                
                model.addAttribute("imagenUrl", "/imagen/img/bytes/" + idImagen);
                model.addAttribute("imagenNombre", imagen.getNombre());
                model.addAttribute("id", idImagen);

                return "imagen_mostrarCPAdmin.html";
                
            }
        
    } else {

            return "imagen_CPcargarAdmin.html";
            
        }
    }

    @GetMapping("/verCPPendiente/{id}")
    public String verCPdesdePendiente(@PathVariable Long id, Model model) {

        Flete flete = fleteServicio.buscarFlete(id);
        
        model.addAttribute("idFlete", id);

        if (flete.getImagenCP() != null) {

            Long idImagen = flete.getImagenCP().getId();
            Imagen imagen = imagenServicio.obtenerImagenPorId(idImagen);

            if (imagen.getTipo().equalsIgnoreCase("application/pdf")) {

                model.addAttribute("imagenNombre", imagen.getNombre());
                model.addAttribute("id", idImagen);

                return "imagen_mostrarCPpdfPendiente.html";

            } else {

                model.addAttribute("imagenUrl", "/imagen/img/bytes/" + idImagen);
                model.addAttribute("imagenNombre", imagen.getNombre());
                model.addAttribute("id", idImagen);

                return "imagen_mostrarCPpendiente.html";
            }

        } else {

            model.addAttribute("flete", flete);

            return "imagen_CPcargarPendiente.html";

        }
    }

    @GetMapping("/verCPChofer/{id}")
    public String verCPdesdeChofer(@PathVariable Long id, Model model) {

        Flete flete = fleteServicio.buscarFlete(id);

        if (flete.getImagenCP() != null) {

            Long idImagen = flete.getImagenCP().getId();
            Imagen imagen = imagenServicio.obtenerImagenPorId(idImagen);

            if (imagen.getTipo().equalsIgnoreCase("application/pdf")) {

                model.addAttribute("imagenNombre", imagen.getNombre());
                model.addAttribute("id", idImagen);

                return "imagen_mostrarCPpdfChofer.html";

            } else {

                model.addAttribute("imagenUrl", "/imagen/img/bytes/" + idImagen);
                model.addAttribute("imagenNombre", imagen.getNombre());
                model.addAttribute("id", idImagen);

                return "imagen_mostrarCPAdmin.html";
            }

        } else {

            model.addAttribute("flete", flete);

            return "imagen_CPcargar.html";

        }
    }

    @GetMapping("/verCPCliente/{id}")
    public String verCPdesdeCliente(@PathVariable Long id, Model model) {

        Flete flete = fleteServicio.buscarFlete(id);

        if (flete.getImagenCP() != null) {

            Long idImagen = flete.getImagenCP().getId();
            Imagen imagen = imagenServicio.obtenerImagenPorId(idImagen);

            if (imagen.getTipo().equalsIgnoreCase("application/pdf")) {

                model.addAttribute("imagenNombre", imagen.getNombre());
                model.addAttribute("id", idImagen);

                return "imagen_mostrarCPpdfCliente.html";

            } else {

                model.addAttribute("imagenUrl", "/imagen/img/bytes/" + idImagen);
                model.addAttribute("imagenNombre", imagen.getNombre());
                model.addAttribute("id", idImagen);

                return "imagen_mostrarCPAdmin.html";
            }

        } else {

            model.addAttribute("flete", flete);

            return "imagen_CPcargar.html";

        }
    }

    @GetMapping("/verCPFiltrado/{id}")
    public String verCPdesdeFiltrado(@PathVariable Long id, Model model) {

        Flete flete = fleteServicio.buscarFlete(id);

        if (flete.getImagenCP() != null) {

            Long idImagen = flete.getImagenCP().getId();
            Imagen imagen = imagenServicio.obtenerImagenPorId(idImagen);

            if (imagen.getTipo().equalsIgnoreCase("application/pdf")) {

                model.addAttribute("imagenNombre", imagen.getNombre());
                model.addAttribute("id", idImagen);

                return "imagen_mostrarCPpdfFiltrado.html";

            } else {

                model.addAttribute("imagenUrl", "/imagen/img/bytes/" + idImagen);
                model.addAttribute("imagenNombre", imagen.getNombre());
                model.addAttribute("id", idImagen);

                return "imagen_mostrarCPAdmin.html";
            }

        } else {

            model.addAttribute("flete", flete);

            return "imagen_CPcargar.html";

        }
    }
    
    @GetMapping("/descargarCPpdfAdmin")
    public String descargarCPpdfAdmin(@RequestParam Long id, @RequestParam Long idFlete, @RequestParam String desde, @RequestParam String hasta, 
            @RequestParam(required = false) Long idChofer, @RequestParam(required = false) Long idCamion, @RequestParam(required = false) Long idCliente, ModelMap modelo) {

        Imagen imagen = imagenServicio.obtenerImagenPorId(id);

        modelo.addAttribute("imagenUrl", "/imagen/pdf/" + id);
        modelo.addAttribute("imagenNombre", imagen.getNombre());
        modelo.addAttribute("id", id);
        modelo.addAttribute("idFlete", idFlete);
        modelo.addAttribute("desde", desde);
        modelo.addAttribute("hasta", hasta);
        modelo.addAttribute("idChofer", idChofer);
        modelo.addAttribute("idCamion", idCamion);
        modelo.addAttribute("idCliente", idCliente);

        return "imagen_descargarCPpdfAdmin.html";
    }

    @GetMapping("/descargarCPpdf/{id}")
    public String descargarCPpdf(@PathVariable Long id, ModelMap modelo) {

        Imagen imagen = imagenServicio.obtenerImagenPorId(id);

        modelo.addAttribute("imagenUrl", "/imagen/pdf/" + id);
        modelo.addAttribute("imagenNombre", imagen.getNombre());
        modelo.addAttribute("id", id);
        modelo.put("flete", fleteServicio.buscarFleteIdImagenCP(id));

        return "imagen_descargarCPpdf.html";
    }

    @GetMapping("/descargarCPpdfPendiente/{id}")
    public String descargarCPpdfPendiente(@PathVariable Long id, ModelMap modelo) {

        Imagen imagen = imagenServicio.obtenerImagenPorId(id);

        modelo.addAttribute("imagenUrl", "/imagen/pdf/" + id);
        modelo.addAttribute("imagenNombre", imagen.getNombre());
        modelo.addAttribute("id", id);
        modelo.addAttribute("flete", fleteServicio.buscarFleteIdImagenCP(id));

        return "imagen_descargarCPpdfPendiente.html";
    }

    @GetMapping("/descargarCPpdfChofer/{id}")
    public String descargarCPpdfChofer(@PathVariable Long id, ModelMap modelo) {

        Imagen imagen = imagenServicio.obtenerImagenPorId(id);

        modelo.addAttribute("imagenUrl", "/imagen/pdf/" + id);
        modelo.addAttribute("imagenNombre", imagen.getNombre());
        modelo.addAttribute("id", id);
        modelo.addAttribute("flete", fleteServicio.buscarFleteIdImagenCP(id));

        return "imagen_descargarCPpdfChofer.html";
    }

    @GetMapping("/descargarCPpdfCliente/{id}")
    public String descargarCPpdfCliente(@PathVariable Long id, ModelMap modelo) {

        Imagen imagen = imagenServicio.obtenerImagenPorId(id);

        modelo.addAttribute("imagenUrl", "/imagen/pdf/" + id);
        modelo.addAttribute("imagenNombre", imagen.getNombre());
        modelo.addAttribute("id", id);
        modelo.addAttribute("flete", fleteServicio.buscarFleteIdImagenCP(id));

        return "imagen_descargarCPpdfCliente.html";
    }

    @GetMapping("/descargarCPpdfFiltrado/{id}")
    public String descargarCPpdfFiltrado(@PathVariable Long id, ModelMap modelo) {

        Imagen imagen = imagenServicio.obtenerImagenPorId(id);

        modelo.addAttribute("imagenUrl", "/imagen/pdf/" + id);
        modelo.addAttribute("imagenNombre", imagen.getNombre());
        modelo.addAttribute("id", id);
        modelo.addAttribute("flete", fleteServicio.buscarFleteIdImagenCP(id));

        return "imagen_descargarCPpdfFiltrado.html";
    }

    @GetMapping("/modificarCP/{id}") //llega id de Imagen
    public String modificarCP(@PathVariable Long id, ModelMap modelo, HttpSession session) {

        modelo.put("id", id);
        modelo.addAttribute("flete", fleteServicio.buscarFleteIdImagenCP(id));
        
        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

            if (logueado.getRol().equalsIgnoreCase("CHOFER")) {

        return "imagen_CPmodificar.html";
        
            } else {
                
        return "imagen_CPmodificarPendiente.html";
                
            }
    }

    @PostMapping("/modificaCP")
    public String modificaCP(@RequestParam Long id, @RequestParam("file") MultipartFile file, ModelMap modelo, HttpSession session) throws IOException {

        Flete flete = fleteServicio.buscarFleteIdImagenCP(id);

        try {

            Imagen imagen = new Imagen();
            imagen.setNombre("CP Viaje ID" + flete.getIdFlete());
            imagen.setTipo(file.getContentType());
            if (file.getContentType().equals("application/pdf")) {
                imagen.setDatos(file.getBytes());
            } else {
                imagen.setDatos(optimizeImage(file));
            }

            imagenServicio.modificarImagen(id, imagen);

            Usuario logueado = (Usuario) session.getAttribute("usuariosession");

            if (logueado.getRol().equalsIgnoreCase("CHOFER")) {

                return "redirect:/imagen/modificadoCPChofer/" + id;

            } else {

                return "redirect:/imagen/modificadoCPAdmin/" + id;

            }

        } catch (Exception e) {
            modelo.addAttribute("id", id);
            modelo.addAttribute("error", "Ocurrió un error al procesar su imagen. Intente nuevamente o ingrese otro archivo");
            return "imagen_CPmodificar.html";
        }

    }

    @GetMapping("/modificadoCPChofer/{id}")
    public String modificadoCPChofer(@PathVariable Long id, ModelMap modelo) {

        Flete flete = fleteServicio.buscarFleteIdImagenCP(id);

        modelo.put("flete", flete);
        modelo.put("exito", "CP MODIFICADA con éxito");

        return "flete_mostrarChofer.html";

    }

    @GetMapping("/modificadoCPAdmin/{id}")
    public String modificadoCPAdmin(@PathVariable Long id, ModelMap modelo) {

        Flete flete = fleteServicio.buscarFleteIdImagenCP(id);

        modelo.put("flete", flete);
        modelo.put("exito", "CP MODIFICADA con éxito");

        return "flete_mostrarPendiente.html";

    }
    
    @GetMapping("/modificarCPAdmin") //llega id de Imagen
    public String modificarCPAdmin(@RequestParam Long id, @RequestParam Long idFlete, @RequestParam String desde, @RequestParam String hasta, 
    @RequestParam(required = false) Long idChofer, @RequestParam(required = false) Long idCamion, @RequestParam(required = false) Long idCliente, ModelMap modelo) {

        modelo.put("id", id);
        modelo.put("idFlete", idFlete);
        modelo.put("desde", desde);
        modelo.put("hasta", hasta);
        modelo.put("idChofer", idChofer);
        modelo.put("idCamion", idCamion);
        modelo.put("idCliente", idCliente);

        return "imagen_CPmodificarAdmin.html";
    }
    
    @PostMapping("/modificaCPAdmin")
    public String modificaCPAdmin(@RequestParam Long id, @RequestParam Long idFlete, @RequestParam String desde, @RequestParam String hasta, 
    @RequestParam(required = false) Long idChofer, @RequestParam(required = false) Long idCamion, @RequestParam(required = false) Long idCliente, 
    @RequestParam("file") MultipartFile file, ModelMap modelo) throws IOException {

        Flete flete = fleteServicio.buscarFlete(idFlete);

        try {

            Imagen imagen = new Imagen();
            imagen.setNombre("CP Viaje ID" + flete.getIdFlete());
            imagen.setTipo(file.getContentType());
            if (file.getContentType().equals("application/pdf")) {
                imagen.setDatos(file.getBytes());
            } else {
                imagen.setDatos(optimizeImage(file));
            }

            imagenServicio.modificarImagen(id, imagen);

                return "redirect:/imagen/modificadoCP?idFlete=" + idFlete +"&desde=" + desde + "&hasta=" + hasta +
           (idChofer != null ? "&idChofer=" + idChofer : "") +
           (idCamion != null ? "&idCamion=" + idCamion : "") +
           (idCliente != null ? "&idCliente=" + idCliente : "");

        } catch (Exception e) {
            
        modelo.addAttribute("id", id);
        modelo.put("idFlete", idFlete);
        modelo.put("desde", desde);
        modelo.put("hasta", hasta);
        modelo.put("idChofer", idChofer);
        modelo.put("idCamion", idCamion);
        modelo.put("idCliente", idCliente);
        modelo.addAttribute("error", "Ocurrió un error al procesar su imagen. Intente nuevamente o ingrese otro archivo");
            
        return "imagen_CPmodificarAdmin.html";
        
        }

    }
    
    @GetMapping("/modificadoCP")
    public String modificadoCP(@RequestParam Long idFlete, @RequestParam String desde, @RequestParam String hasta, 
    @RequestParam(required = false) Long idChofer, @RequestParam(required = false) Long idCamion, @RequestParam(required = false) Long idCliente, ModelMap modelo) {

        modelo.put("flete", fleteServicio.buscarFlete(idFlete));
        modelo.put("desde", desde);
        modelo.put("hasta", hasta);
        modelo.put("idChofer", idChofer);
        modelo.put("idCamion", idCamion);
        modelo.put("idCliente", idCliente);
        modelo.put("exito", "CP MODIFICADA con éxito");

        return "flete_mostrarAdmin.html";

    }

    @GetMapping("/eliminarCP/{id}")
    public String eliminarCP(@PathVariable Long id, ModelMap model, HttpSession session) {
        
        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        Imagen imagen = imagenServicio.obtenerImagenPorId(id);

        model.addAttribute("imagenNombre", imagen.getNombre());
        model.addAttribute("id", id);
        model.addAttribute("flete", fleteServicio.buscarFleteIdImagenCP(id));

        if (logueado.getRol().equalsIgnoreCase("CHOFER")) {

        return "imagen_eliminarCP.html";
        
        } else {
            
        return "imagen_eliminarCPPendiente.html";
            
        }
    }

    @GetMapping("/eliminaCP/{id}")
    public String eliminaCP(@PathVariable Long id, HttpSession session, ModelMap modelo) {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");
        
        Flete flete = fleteServicio.buscarFleteIdImagenCP(id);

        imagenServicio.eliminarImagenCP(id);

        if (logueado.getRol().equalsIgnoreCase("CHOFER")) {

            return "redirect:/imagen/eliminadoCPChofer/" +flete.getId();

        } else {

            return "redirect:/imagen/eliminadoCPAdmin/" + flete.getId();

        }
    }

    @GetMapping("/eliminadoCPChofer/{id}")
    public String eliminadoCPChofer(@PathVariable Long id, ModelMap modelo, HttpSession session) {

        modelo.put("exito", "CP ELIMINADA con éxito");
        modelo.put("flete", fleteServicio.buscarFlete(id));

        return "flete_mostrarChofer.html";
        
    }

    @GetMapping("/eliminadoCPAdmin/{id}")
    public String eliminadoCPAdmin(@PathVariable Long id, ModelMap modelo) {

        Flete flete = fleteServicio.buscarFlete(id);

        modelo.put("flete", flete);
        modelo.put("exito", "CP ELIMINADA con éxito");

        return "flete_mostrarPendiente.html";

    }
    
    @GetMapping("/eliminarCPAdmin")
    public String eliminarCPAdmin(@RequestParam Long id, @RequestParam Long idFlete, @RequestParam String desde, @RequestParam String hasta, 
    @RequestParam(required = false) Long idChofer, @RequestParam(required = false) Long idCamion, @RequestParam(required = false) Long idCliente, ModelMap model) {

        Imagen imagen = imagenServicio.obtenerImagenPorId(id);

        model.addAttribute("imagenNombre", imagen.getNombre());
        model.addAttribute("id", id);
        model.put("idFlete", idFlete);
        model.put("desde", desde);
        model.put("hasta", hasta);
        model.put("idChofer", idChofer);
        model.put("idCamion", idCamion);
        model.put("idCliente", idCliente);

        return "imagen_eliminarCPAdmin.html";
    }
    
    @GetMapping("/eliminaCPAdmin")
    public String eliminaCPAdmin(@RequestParam Long id, @RequestParam Long idFlete, @RequestParam String desde, @RequestParam String hasta, 
    @RequestParam(required = false) Long idChofer, @RequestParam(required = false) Long idCamion, @RequestParam(required = false) Long idCliente, ModelMap modelo) {

        imagenServicio.eliminarImagenCP(id);

            return "redirect:/imagen/eliminadoCP?idFlete=" + idFlete +"&desde=" + desde + "&hasta=" + hasta +
           (idChofer != null ? "&idChofer=" + idChofer : "") +
           (idCamion != null ? "&idCamion=" + idCamion : "") +
           (idCliente != null ? "&idCliente=" + idCliente : "");
    }
    
    @GetMapping("/eliminadoCP")
    public String eliminadoCP(@RequestParam Long idFlete, @RequestParam String desde, @RequestParam String hasta, 
    @RequestParam(required = false) Long idChofer, @RequestParam(required = false) Long idCamion, 
    @RequestParam(required = false) Long idCliente,ModelMap modelo) {

        modelo.put("flete", fleteServicio.buscarFlete(idFlete));
        modelo.put("desde", desde);
        modelo.put("hasta", hasta);
        modelo.put("idChofer", idChofer);
        modelo.put("idCamion", idCamion);
        modelo.put("idCliente", idCliente);
        modelo.put("exito", "CP ELIMINADA con éxito");

        return "flete_mostrarAdmin.html";

    }

    @GetMapping("/verDescarga/{id}")
    public String verDescarga(@PathVariable Long id, Model model) {

        Flete flete = fleteServicio.buscarFlete(id);
        model.addAttribute("flete", flete);

        if (flete.getImagenDescarga() != null) {

            Long idImagen = flete.getImagenDescarga().getId();
            Imagen imagen = imagenServicio.obtenerImagenPorId(idImagen);
            
            if (!imagen.getTipo().equalsIgnoreCase("application/pdf")) {
            
                model.addAttribute("imagenUrl", "/imagen/img/bytes/" + idImagen);
                model.addAttribute("imagenNombre", imagen.getNombre());
                model.addAttribute("id", idImagen);
                model.addAttribute("estado", flete.getEstado());

                return "imagen_mostrarDescarga.html";
                
            } else {
                
                model.addAttribute("imagenNombre", imagen.getNombre());
                model.addAttribute("id", idImagen);
                model.addAttribute("estado", flete.getEstado());
                
                return "imagen_mostrarDescargaPdf.html";
            
        }
            
        } else {

            return "imagen_descargaCargar.html";
        }
    }
    
    @GetMapping("/verDescargaAdmin")
    public String verDescargaAdmin(@RequestParam Long id, @RequestParam String desde, @RequestParam String hasta, @RequestParam(required = false) Long idChofer,
    @RequestParam(required = false) Long idCamion, @RequestParam(required = false) Long idCliente, Model model) {

        Flete flete = fleteServicio.buscarFlete(id);
        
        model.addAttribute("flete", flete);
        model.addAttribute("desde", desde);
        model.addAttribute("hasta", hasta);
        model.addAttribute("idChofer", idChofer);
        model.addAttribute("idCamion", idCamion);
        model.addAttribute("idCliente", idCliente);

        if (flete.getImagenDescarga() != null) {

            Long idImagen = flete.getImagenDescarga().getId();
            Imagen imagen = imagenServicio.obtenerImagenPorId(idImagen);

            if (imagen.getTipo().equalsIgnoreCase("application/pdf")) {

                model.addAttribute("imagenNombre", imagen.getNombre());
                model.addAttribute("id", idImagen);

                return "imagen_mostrarDescargaPdfAdmin.html";

            } else {

                model.addAttribute("imagenUrl", "/imagen/img/bytes/" + idImagen);
                model.addAttribute("imagenNombre", imagen.getNombre());
                model.addAttribute("id", idImagen);

                return "imagen_mostrarDescargaAdmin.html";

            }

        } else {

            return "imagen_descargaCargarAdmin.html";
        }
    }

    @GetMapping("/verDescargaPendiente/{id}")
    public String verDescargaPendiente(@PathVariable Long id, Model model) {

        Flete flete = fleteServicio.buscarFlete(id);
        

        if (flete.getImagenDescarga() != null) {

            Long idImagen = flete.getImagenDescarga().getId();
            Imagen imagen = imagenServicio.obtenerImagenPorId(idImagen);

            if (imagen.getTipo().equalsIgnoreCase("application/pdf")) {

                model.addAttribute("imagenNombre", imagen.getNombre());
                model.addAttribute("id", idImagen);
                model.addAttribute("idFlete", flete.getId());

                return "imagen_mostrarDescargaPdfPendiente.html";

            } else {

                model.addAttribute("imagenUrl", "/imagen/img/bytes/" + idImagen);
                model.addAttribute("imagenNombre", imagen.getNombre());
                model.addAttribute("id", idImagen);
                model.addAttribute("idFlete", flete.getId());

                return "imagen_mostrarDescargaPendiente.html";

            }

        } else {

            model.addAttribute("flete", flete);

            return "imagen_descargaCargarPendiente.html";
        }
    }

    @GetMapping("/verDescargaChofer/{id}")
    public String verDescargaChofer(@PathVariable Long id, Model model) {

        Flete flete = fleteServicio.buscarFlete(id);

        if (flete.getImagenDescarga() != null) {

            Long idImagen = flete.getImagenDescarga().getId();
            Imagen imagen = imagenServicio.obtenerImagenPorId(idImagen);

            if (imagen.getTipo().equalsIgnoreCase("application/pdf")) {

                model.addAttribute("imagenNombre", imagen.getNombre());
                model.addAttribute("id", idImagen);

                return "imagen_mostrarDescargaPdfChofer.html";

            } else {

                model.addAttribute("imagenUrl", "/imagen/img/bytes/" + idImagen);
                model.addAttribute("imagenNombre", imagen.getNombre());
                model.addAttribute("id", idImagen);

                return "imagen_mostrarDescargaAdmin.html";

            }

        } else {

            model.addAttribute("flete", flete);

            return "imagen_descargaCargar.html";
        }
    }

    @GetMapping("/verDescargaCliente/{id}")
    public String verDescargaCliente(@PathVariable Long id, Model model) {

        Flete flete = fleteServicio.buscarFlete(id);

        if (flete.getImagenDescarga() != null) {

            Long idImagen = flete.getImagenDescarga().getId();
            Imagen imagen = imagenServicio.obtenerImagenPorId(idImagen);

            if (imagen.getTipo().equalsIgnoreCase("application/pdf")) {

                model.addAttribute("imagenNombre", imagen.getNombre());
                model.addAttribute("id", idImagen);

                return "imagen_mostrarDescargaPdfCliente.html";

            } else {

                model.addAttribute("imagenUrl", "/imagen/img/bytes/" + idImagen);
                model.addAttribute("imagenNombre", imagen.getNombre());
                model.addAttribute("id", idImagen);

                return "imagen_mostrarDescargaAdmin.html";

            }

        } else {

            model.addAttribute("flete", flete);

            return "imagen_descargaCargar.html";
        }
    }

    @GetMapping("/verDescargaFiltrado/{id}")
    public String verDescargaFiltrado(@PathVariable Long id, Model model) {

        Flete flete = fleteServicio.buscarFlete(id);

        if (flete.getImagenDescarga() != null) {

            Long idImagen = flete.getImagenDescarga().getId();
            Imagen imagen = imagenServicio.obtenerImagenPorId(idImagen);

            if (imagen.getTipo().equalsIgnoreCase("application/pdf")) {

                model.addAttribute("imagenNombre", imagen.getNombre());
                model.addAttribute("id", idImagen);

                return "imagen_mostrarDescargaPdfFiltrado.html";

            } else {

                model.addAttribute("imagenUrl", "/imagen/img/bytes/" + idImagen);
                model.addAttribute("imagenNombre", imagen.getNombre());
                model.addAttribute("id", idImagen);

                return "imagen_mostrarDescargaAdmin.html";

            }

        } else {

            model.addAttribute("flete", flete);

            return "imagen_descargaCargar.html";
        }
    }

    @GetMapping("/descargarDescargaPdf/{id}")
    public String descargarDescargaPdf(@PathVariable Long id, ModelMap modelo) {

        Imagen imagen = imagenServicio.obtenerImagenPorId(id);

        modelo.addAttribute("imagenUrl", "/imagen/pdf/" + id);
        modelo.addAttribute("imagenNombre", imagen.getNombre());
        modelo.addAttribute("id", id);
        modelo.addAttribute("flete", fleteServicio.buscarFleteIdImagenDescarga(id));

        return "imagen_descargarDescargaPdf.html";
    }
    
    @GetMapping("/descargarDescargaPdfAdmin")
    public String descargarDescargaPdfAdmin(@RequestParam Long id, @RequestParam Long idFlete, @RequestParam String desde, @RequestParam String hasta, 
            @RequestParam(required = false) Long idChofer, @RequestParam(required = false) Long idCamion, 
            @RequestParam(required = false) Long idCliente, ModelMap modelo) {

        Imagen imagen = imagenServicio.obtenerImagenPorId(id);

        modelo.addAttribute("imagenUrl", "/imagen/pdf/" + id);
        modelo.addAttribute("imagenNombre", imagen.getNombre());
        modelo.addAttribute("id", id);
        modelo.addAttribute("idFlete", idFlete);
        modelo.addAttribute("desde", desde);
        modelo.addAttribute("hasta", hasta);
        modelo.addAttribute("idChofer", idChofer);
        modelo.addAttribute("idCamion", idCamion);
        modelo.addAttribute("idCliente", idCliente);

        return "imagen_descargarDescargaPdfAdmin.html";
    }

    @GetMapping("/descargarDescargaPdfPendiente/{id}")
    public String descargarDescargaPdfPendiente(@PathVariable Long id, ModelMap modelo) {

        Imagen imagen = imagenServicio.obtenerImagenPorId(id);

        modelo.addAttribute("imagenUrl", "/imagen/pdf/" + id);
        modelo.addAttribute("imagenNombre", imagen.getNombre());
        modelo.addAttribute("id", id);
        modelo.addAttribute("flete", fleteServicio.buscarFleteIdImagenDescarga(id));

        return "imagen_descargarDescargaPdfPendiente.html";
    }

    @GetMapping("/descargarDescargaPdfChofer/{id}")
    public String descargarDescargaPdfChofer(@PathVariable Long id, ModelMap modelo) {

        Imagen imagen = imagenServicio.obtenerImagenPorId(id);

        modelo.addAttribute("imagenUrl", "/imagen/pdf/" + id);
        modelo.addAttribute("imagenNombre", imagen.getNombre());
        modelo.addAttribute("id", id);
        modelo.addAttribute("flete", fleteServicio.buscarFleteIdImagenDescarga(id));

        return "imagen_descargarDescargaPdfChofer.html";
    }

    @GetMapping("/descargarDescargaPdfCliente/{id}")
    public String descargarDescargaPdfCliente(@PathVariable Long id, ModelMap modelo) {

        Imagen imagen = imagenServicio.obtenerImagenPorId(id);

        modelo.addAttribute("imagenUrl", "/imagen/pdf/" + id);
        modelo.addAttribute("imagenNombre", imagen.getNombre());
        modelo.addAttribute("id", id);
        modelo.addAttribute("flete", fleteServicio.buscarFleteIdImagenDescarga(id));

        return "imagen_descargarDescargaPdfCliente.html";
    }

    @GetMapping("/descargarDescargaPdfFiltrado/{id}")
    public String descargarDescargaPdfFiltrado(@PathVariable Long id, ModelMap modelo) {

        Imagen imagen = imagenServicio.obtenerImagenPorId(id);

        modelo.addAttribute("imagenUrl", "/imagen/pdf/" + id);
        modelo.addAttribute("imagenNombre", imagen.getNombre());
        modelo.addAttribute("id", id);
        modelo.addAttribute("flete", fleteServicio.buscarFleteIdImagenDescarga(id));

        return "imagen_descargarDescargaPdfFiltrado.html";
    }

    @GetMapping("/modificarDescarga") //llega id de Imagen
    public String modificarDescarga(@RequestParam Long id, @RequestParam Long idFlete, ModelMap modelo) {

        modelo.put("id", id);
        modelo.put("idFlete", idFlete);

        return "imagen_descargaModificar.html";
    }

    @PostMapping("/modificaDescarga")
    public String modificaDescarga(@RequestParam Long id, @RequestParam Long idFlete, @RequestParam("file") MultipartFile file, ModelMap modelo) throws IOException {

        Flete flete = fleteServicio.buscarFlete(idFlete);

        try {

            Imagen imagen = new Imagen();
            imagen.setNombre("Descarga Viaje ID" + flete.getIdFlete());
            imagen.setTipo(file.getContentType());
            if (file.getContentType().equals("application/pdf")) {
                imagen.setDatos(file.getBytes());
            } else {
                imagen.setDatos(optimizeImage(file));
            }

            imagenServicio.modificarImagen(id, imagen);

                return "redirect:/imagen/modificadoDescargaChofer/" + idFlete;

        } catch (Exception e) {
            modelo.addAttribute("id", id);
            modelo.put("idFlete", idFlete);
            modelo.addAttribute("error", "Ocurrió un error al procesar su imagen. Intente nuevamente o ingrese otro archivo");
            return "imagen_descargaModificar.html";
        }

    }
    
    @GetMapping("/modificarDescargaPendiente/{id}") //llega id de Imagen
    public String modificarDescargaPendiente(@PathVariable Long id, ModelMap modelo) {

        modelo.put("id", id);
        modelo.put("flete", fleteServicio.buscarFleteIdImagenDescarga(id));

        return "imagen_descargaModificarPendiente.html";
    }
    
    @PostMapping("/modificaDescargaPendiente")
    public String modificaDescargaPendiente(@RequestParam Long id, @RequestParam("file") MultipartFile file, ModelMap modelo) throws IOException {

        Flete flete = fleteServicio.buscarFleteIdImagenDescarga(id);

        try {

            Imagen imagen = new Imagen();
            imagen.setNombre("Descarga Viaje ID" + flete.getIdFlete());
            imagen.setTipo(file.getContentType());
            if (file.getContentType().equals("application/pdf")) {
                imagen.setDatos(file.getBytes());
            } else {
                imagen.setDatos(optimizeImage(file));
            }

            imagenServicio.modificarImagen(id, imagen);

            return "redirect:/imagen/modificadoDescargaPendiente/" + id;

        } catch (Exception e) {
            modelo.addAttribute("id", id);
            modelo.addAttribute("flete", flete);
            modelo.addAttribute("error", "Ocurrió un error al procesar su imagen. Intente nuevamente o ingrese otro archivo");
            
            return "imagen_descargaModificarPendiente.html";
        }

    }
    
    @GetMapping("/modificadoDescargaPendiente/{id}")
    public String modificadoDescargaPendiente(@PathVariable Long id, ModelMap modelo) {

        Flete flete = fleteServicio.buscarFleteIdImagenDescarga(id);

        modelo.put("flete", flete);
        modelo.put("exito", "Ticket de Descarga MODIFICADO con éxito");

        return "flete_mostrarPendiente.html";

    }

    @GetMapping("/modificadoDescargaChofer/{id}")
    public String modificadoDescargaChofer(@PathVariable Long id, ModelMap modelo) {

        modelo.put("flete", fleteServicio.buscarFlete(id));
        modelo.put("exito", "Ticket de Descarga MODIFICADO con éxito");

        return "flete_mostrarChofer.html";

    }

    @GetMapping("/modificadoDescargaAdmin/{id}")
    public String modificadoDescargaAdmin(@PathVariable Long id, ModelMap modelo) {

        Flete flete = fleteServicio.buscarFleteIdImagenDescarga(id);

        modelo.put("flete", flete);
        modelo.put("exito", "Ticket de Descarga MODIFICADO con éxito");

        return "flete_modificadoAdmin.html";
        
    }
    
    @GetMapping("/modificarDescargaAdmin") //llega id de Imagen
    public String modificarDescargaAdmin(@RequestParam Long id, @RequestParam Long idFlete, @RequestParam String desde, @RequestParam String hasta, 
    @RequestParam(required = false) Long idChofer, @RequestParam(required = false) Long idCamion, @RequestParam(required = false) Long idCliente, ModelMap modelo) {

        modelo.put("id", id);
        modelo.put("idFlete", idFlete);
        modelo.put("desde", desde);
        modelo.put("hasta", hasta);
        modelo.put("idChofer", idChofer);
        modelo.put("idCamion", idCamion);
        modelo.put("idCliente", idCliente);

        return "imagen_descargaModificarAdmin.html";
    }
    
    @PostMapping("/modificaDescargaAdmin")
    public String modificaDescargaAdmin(@RequestParam Long id, @RequestParam Long idFlete, @RequestParam String desde, @RequestParam String hasta, 
    @RequestParam(required = false) Long idChofer, @RequestParam(required = false) Long idCamion, @RequestParam(required = false) Long idCliente,
    @RequestParam("file") MultipartFile file, ModelMap modelo) throws IOException {

        Flete flete = fleteServicio.buscarFlete(idFlete);

        try {

            Imagen imagen = new Imagen();
            imagen.setNombre("Descarga Viaje ID" + flete.getIdFlete());
            imagen.setTipo(file.getContentType());
            if (file.getContentType().equals("application/pdf")) {
                imagen.setDatos(file.getBytes());
            } else {
                imagen.setDatos(optimizeImage(file));
            }

            imagenServicio.modificarImagen(id, imagen);

                return "redirect:/imagen/modificadoDescarga?idFlete=" + idFlete +"&desde=" + desde + "&hasta=" + hasta +
           (idChofer != null ? "&idChofer=" + idChofer : "") +
           (idCamion != null ? "&idCamion=" + idCamion : "") +
           (idCliente != null ? "&idCliente=" + idCliente : "");

        } catch (Exception e) {
            
            modelo.addAttribute("id", id);
            modelo.put("idFlete", idFlete);
        modelo.put("desde", desde);
        modelo.put("hasta", hasta);
        modelo.put("idChofer", idChofer);
        modelo.put("idCamion", idCamion);
        modelo.put("idCliente", idCliente);
            modelo.addAttribute("error", "Ocurrió un error al procesar su imagen. Intente nuevamente o ingrese otro archivo");
            
            return "imagen_descargaModificarAdmin.html";
        }

    }
    
    @GetMapping("/modificadoDescarga")
    public String modificadoDescarga(@RequestParam Long idFlete, @RequestParam String desde, @RequestParam String hasta, 
    @RequestParam(required = false) Long idChofer, @RequestParam(required = false) Long idCamion, @RequestParam(required = false) Long idCliente, ModelMap modelo) {

        modelo.put("flete", fleteServicio.buscarFlete(idFlete));
        modelo.put("desde", desde);
        modelo.put("hasta", hasta);
        modelo.put("idChofer", idChofer);
        modelo.put("idCamion", idCamion);
        modelo.put("idCliente", idCliente);
        modelo.put("exito", "Ticket de Descarga MODIFICADO con éxito");

        return "flete_mostrarAdmin.html";
        
    }

    @GetMapping("/eliminarDescarga")
    public String eliminarDescarga(@RequestParam Long id, @RequestParam Long idFlete, ModelMap model) {

        Imagen imagen = imagenServicio.obtenerImagenPorId(id);

        model.addAttribute("imagenNombre", imagen.getNombre());
        model.addAttribute("id", id);
        model.put("idFlete", idFlete);

        return "imagen_eliminarDescarga.html";
    }

    @GetMapping("/eliminaDescarga")
    public String eliminaDescarga(@RequestParam Long id,@RequestParam Long idFlete, ModelMap modelo) {

        imagenServicio.eliminarImagenDescarga(id);

            return "redirect:/imagen/eliminadoDescargaChofer/" +idFlete;
    }

    @GetMapping("/eliminadoDescargaChofer/{idFlete}")
    public String eliminadoDescargaChofer(@PathVariable Long idFlete, ModelMap modelo, HttpSession session) {

        modelo.put("flete", fleteServicio.buscarFlete(idFlete));
        modelo.put("exito", "Ticket de Descarga ELIMINADO con éxito");

        return "flete_mostrarChofer.html";

    }

    @GetMapping("/eliminadoDescargaAdmin")
    public String eliminadoDescargaAdmin(ModelMap modelo, HttpSession session) {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        modelo.put("id", logueado.getId());
        modelo.put("exito", "Ticket de Descarga ELIMINADA con éxito");

        return "index_admin.html";

    }
    
    @GetMapping("/eliminarDescargaPendiente/{id}")
    public String eliminarDescargaPendiente(@PathVariable Long id, ModelMap model) {

        Imagen imagen = imagenServicio.obtenerImagenPorId(id);

        model.addAttribute("imagenNombre", imagen.getNombre());
        model.addAttribute("id", id);
        model.addAttribute("flete", fleteServicio.buscarFleteIdImagenDescarga(id));

        return "imagen_eliminarDescargaPendiente.html";
    }
    
    @GetMapping("/eliminaDescargaPendiente/{id}")
    public String eliminaDescargaPendiente(@PathVariable Long id, ModelMap modelo) {

        Flete flete = fleteServicio.buscarFleteIdImagenDescarga(id);
        
        imagenServicio.eliminarImagenDescarga(id);


        return "redirect:/imagen/eliminadoDescargaPendiente/" + flete.getId();

    }
    
    @GetMapping("/eliminadoDescargaPendiente/{id}")
    public String eliminadoDescargaPendiente(@PathVariable Long id, ModelMap modelo) {

        modelo.put("flete", fleteServicio.buscarFlete(id));
        modelo.put("exito", "Ticket de Descarga ELIMINADO con éxito");

        return "flete_mostrarPendiente.html";

    }
    
    @GetMapping("/eliminarDescargaAdmin")
    public String eliminarDescargaAdmin(@RequestParam Long id, @RequestParam Long idFlete, @RequestParam String desde, @RequestParam String hasta, 
    @RequestParam(required = false) Long idChofer, @RequestParam(required = false) Long idCamion, @RequestParam(required = false) Long idCliente, ModelMap modelo) {

        Imagen imagen = imagenServicio.obtenerImagenPorId(id);
        
        modelo.put("idFlete", idFlete);
        modelo.put("desde", desde);
        modelo.put("hasta", hasta);
        modelo.put("idChofer", idChofer);
        modelo.put("idCamion", idCamion);
        modelo.put("idCliente", idCliente);
        modelo.put("imagenNombre", imagen.getNombre());
        modelo.put("id", id);

        return "imagen_eliminarDescargaAdmin.html";
    }
    
    @GetMapping("/eliminaDescargaAdmin")
    public String eliminaDescargaAdmin(@RequestParam Long id, @RequestParam Long idFlete, @RequestParam String desde, @RequestParam String hasta, 
    @RequestParam(required = false) Long idChofer, @RequestParam(required = false) Long idCamion, @RequestParam(required = false) Long idCliente, ModelMap modelo) {

        imagenServicio.eliminarImagenDescarga(id);

            return "redirect:/imagen/eliminadoDescarga?idFlete=" + idFlete +"&desde=" + desde + "&hasta=" + hasta +
           (idChofer != null ? "&idChofer=" + idChofer : "") +
           (idCamion != null ? "&idCamion=" + idCamion : "") +
           (idCliente != null ? "&idCliente=" + idCliente : "");

    }
    
    
    @GetMapping("/eliminadoDescarga")
    public String eliminadoDescarga(@RequestParam Long idFlete, @RequestParam String desde, @RequestParam String hasta, 
    @RequestParam(required = false) Long idChofer, @RequestParam(required = false) Long idCamion, @RequestParam(required = false) Long idCliente, ModelMap modelo) {

        modelo.put("flete", fleteServicio.buscarFlete(idFlete));
        modelo.put("desde", desde);
        modelo.put("hasta", hasta);
        modelo.put("idChofer", idChofer);
        modelo.put("idCamion", idCamion);
        modelo.put("idCliente", idCliente);
        modelo.put("exito", "Ticket de Descarga ELIMINADO con éxito");

        return "flete_mostrarAdmin.html";

    }

    @GetMapping("/verCombustible/{id}")
    public String verCombustible(@PathVariable Long id, Model model) {

        Combustible carga = combustibleServicio.buscarCombustible(id);
        model.addAttribute("carga", carga);

        if (carga.getImagen() != null) {

            Long idImagen = carga.getImagen().getId();
            Imagen imagen = imagenServicio.obtenerImagenPorId(idImagen);
            model.addAttribute("imagenUrl", "/imagen/img/bytes/" + idImagen);
            model.addAttribute("imagenNombre", imagen.getNombre());
            model.addAttribute("id", idImagen);

            if (imagen.getTipo().equalsIgnoreCase("application/pdf")) {

                return "imagen_mostrarCombustiblePdf.html";

            } else {

                return "imagen_mostrarCombustible.html";
            }

        } else {

            return "imagen_combustibleCargar.html";

        }
    }
    
    @GetMapping("/verCombustibleAdmin")
    public String verCombustibleAdmin(@RequestParam Long idCarga, @RequestParam String desde, @RequestParam String hasta, @RequestParam(required = false) Long idCamion,
            @RequestParam(required = false) Long idChofer, ModelMap model) {

        Combustible carga = combustibleServicio.buscarCombustible(idCarga);
        model.addAttribute("carga", carga);
        model.put("desde", desde);
        model.put("hasta", hasta);
        model.put("idChofer", idChofer);
        model.put("idCamion", idCamion);

        if (carga.getImagen() != null) {

            Long idImagen = carga.getImagen().getId();
            Imagen imagen = imagenServicio.obtenerImagenPorId(idImagen);
            model.addAttribute("imagenUrl", "/imagen/img/bytes/" + idImagen);
            model.addAttribute("imagenNombre", imagen.getNombre());
            model.addAttribute("id", idImagen);

            if (imagen.getTipo().equalsIgnoreCase("application/pdf")) {

                return "imagen_mostrarCombustiblePdfAdmin.html";

            } else {

                return "imagen_mostrarCombustibleAdmin.html";

        }
        } else {
                
            return "imagen_combustibleCargarAdmin.html";
                
            }
    
    }

    @GetMapping("/descargarCombustiblePdf")
    public String descargarCombustiblePdf(@RequestParam Long id, @RequestParam Long idCarga, ModelMap modelo) {

        Imagen imagen = imagenServicio.obtenerImagenPorId(id);

        modelo.addAttribute("imagenUrl", "/imagen/pdf/" + id);
        modelo.addAttribute("imagenNombre", imagen.getNombre());
        modelo.addAttribute("id", id);
        modelo.put("idCarga", idCarga);

        return "imagen_descargarCombustiblePdf.html";
    }
    
    @GetMapping("/descargarCombustiblePdfAdmin")
    public String descargarCombustiblePdfAdmin(@RequestParam Long id, @RequestParam Long idCarga, @RequestParam String desde, @RequestParam String hasta, 
            @RequestParam(required = false) Long idCamion, @RequestParam(required = false) Long idChofer, ModelMap modelo) {

        Imagen imagen = imagenServicio.obtenerImagenPorId(id);

        modelo.addAttribute("imagenUrl", "/imagen/pdf/" + id);
        modelo.addAttribute("imagenNombre", imagen.getNombre());
        modelo.addAttribute("id", id);
        modelo.put("idCarga", idCarga);
        modelo.put("desde", desde);
        modelo.put("hasta", hasta);
        modelo.put("idChofer", idChofer);
        modelo.put("idCamion", idCamion);

        return "imagen_descargarCombustiblePdfAdmin.html";
    }
    
    @GetMapping("/modificarCombustibleAdmin") //llega id de Imagen
    public String modificarCombustibleAdmin(@RequestParam Long id, @RequestParam Long idCarga, @RequestParam String desde, @RequestParam String hasta, 
            @RequestParam(required = false) Long idCamion, @RequestParam(required = false) Long idChofer, ModelMap modelo) {

        modelo.put("id", id);
        modelo.put("idCarga", idCarga);
        modelo.put("desde", desde);
        modelo.put("hasta", hasta);
        modelo.put("idChofer", idChofer);
        modelo.put("idCamion", idCamion);

        return "imagen_combustibleModificarAdmin.html";
    }
    
    @PostMapping("/modificaCombustibleAdmin")
    public String modificaCombustibleAdmin(@RequestParam Long id, @RequestParam Long idCarga, @RequestParam String desde, @RequestParam String hasta, 
            @RequestParam(required = false) Long idCamion, @RequestParam(required = false) Long idChofer, @RequestParam("file") MultipartFile file, ModelMap modelo) throws IOException {

        Combustible carga = combustibleServicio.buscarCombustibleIdImagen(id);

        try {

            Imagen imagen = new Imagen();
            imagen.setNombre("Carga de Diesel " + carga.getFechaCarga());
            imagen.setTipo(file.getContentType());
            if (file.getContentType().equals("application/pdf")) {
                imagen.setDatos(file.getBytes());
            } else {
                imagen.setDatos(optimizeImage(file));
            }

            imagenServicio.modificarImagen(id, imagen);

            return "redirect:/imagen/modificadoCombustibleAdmin?idCarga=" + idCarga +"&desde=" + desde + "&hasta=" + hasta +
           (idCamion != null ? "&idCamion=" + idCamion : "") +
           (idChofer != null ? "&idChofer=" + idChofer : "");

        } catch (Exception e) {
            modelo.addAttribute("id", id);
            modelo.put("idCarga", idCarga);
            modelo.put("desde", desde);
        modelo.put("hasta", hasta);
        modelo.put("idChofer", idChofer);
        modelo.put("idCamion", idCamion);
            modelo.addAttribute("error", "Ocurrió un error al procesar su imagen. Intente nuevamente o ingrese otro archivo");
            return "imagen_combustibleModificarAdmin.html";
        }

    }
    
    @GetMapping("/modificadoCombustibleAdmin")
    public String modificadoCombustibleAdmin(@RequestParam Long idCarga, @RequestParam String desde, @RequestParam String hasta, 
            @RequestParam(required = false) Long idCamion, @RequestParam(required = false) Long idChofer, ModelMap modelo) {

        modelo.put("carga", combustibleServicio.buscarCombustible(idCarga));
        modelo.put("exito", "Imagen de Combustible MODIFICADA con éxito");
        modelo.put("desde", desde);
        modelo.put("hasta", hasta);
        modelo.put("idChofer", idChofer);
        modelo.put("idCamion", idCamion);

        return "combustible_mostrarAdmin.html";

    }
    
    @GetMapping("/eliminarCombustibleAdmin")
    public String eliminarCombustibleAdmin(@RequestParam Long id, @RequestParam Long idCarga, @RequestParam String desde, @RequestParam String hasta, 
            @RequestParam(required = false) Long idCamion, @RequestParam(required = false) Long idChofer, ModelMap model) {

        Imagen imagen = imagenServicio.obtenerImagenPorId(id);

        model.addAttribute("imagenNombre", imagen.getNombre());
        model.addAttribute("id", id);
        model.put("idCarga", idCarga);
        model.put("desde", desde);
        model.put("hasta", hasta);
        model.put("idChofer", idChofer);
        model.put("idCamion", idCamion);

        return "imagen_eliminarCombustibleAdmin.html";
    }

    @GetMapping("/eliminaCombustibleAdmin")
    public String eliminaCombustible(@RequestParam Long id, @RequestParam Long idCarga, @RequestParam String desde, @RequestParam String hasta, 
            @RequestParam(required = false) Long idCamion, @RequestParam(required = false) Long idChofer, ModelMap modelo) {

        imagenServicio.eliminarImagenCombustible(id);

            return "redirect:/imagen/eliminadoCombustibleAdmin?idCarga=" + idCarga +"&desde=" + desde + "&hasta=" + hasta +
           (idCamion != null ? "&idCamion=" + idCamion : "") +
           (idChofer != null ? "&idChofer=" + idChofer : "");
    }

    @GetMapping("/eliminadoCombustibleAdmin")
    public String eliminadoCombustibleAdmin(@RequestParam Long idCarga, @RequestParam String desde, @RequestParam String hasta, 
            @RequestParam(required = false) Long idCamion, @RequestParam(required = false) Long idChofer, ModelMap modelo) {

        modelo.put("carga", combustibleServicio.buscarCombustible(idCarga));
        modelo.put("exito", "Imagen de Combustible ELIMINADA con éxito");
        modelo.put("desde", desde);
        modelo.put("hasta", hasta);
        modelo.put("idChofer", idChofer);
        modelo.put("idCamion", idCamion);

        return "combustible_mostrarAdmin.html";

    }

    @GetMapping("/modificarCombustible") //llega id de Imagen
    public String modificarCombustible(@RequestParam Long id, @RequestParam Long idCarga, ModelMap modelo) {

        modelo.put("id", id);
        modelo.put("idCarga", idCarga);

        return "imagen_combustibleModificar.html";
    }

    @PostMapping("/modificaCombustible")
    public String modificaCombustible(@RequestParam Long id, @RequestParam Long idCarga, @RequestParam("file") MultipartFile file, ModelMap modelo) throws IOException {

        Combustible carga = combustibleServicio.buscarCombustibleIdImagen(id);

        try {

            Imagen imagen = new Imagen();
            imagen.setNombre("Carga de Diesel " + carga.getFechaCarga());
            imagen.setTipo(file.getContentType());
            if (file.getContentType().equals("application/pdf")) {
                imagen.setDatos(file.getBytes());
            } else {
                imagen.setDatos(optimizeImage(file));
            }

            imagenServicio.modificarImagen(id, imagen);

            return "redirect:/imagen/modificadoCombustible/" + idCarga;

        } catch (Exception e) {
            modelo.addAttribute("id", id);
            modelo.put("idCarga", idCarga);
            modelo.addAttribute("error", "Ocurrió un error al procesar su imagen. Intente nuevamente o ingrese otro archivo");
            return "imagen_combustibleModificar.html";
        }

    }

    @GetMapping("/modificadoCombustible/{idCarga}")
    public String modificadoCombustible(@PathVariable Long idCarga, ModelMap modelo) {

        modelo.put("carga", combustibleServicio.buscarCombustible(idCarga));
        modelo.put("exito", "Imagen de Combustible MODIFICADO con éxito");

        return "combustible_mostrar.html";

    }

    @GetMapping("/eliminarCombustible")
    public String eliminarCombustible(@RequestParam Long id, @RequestParam Long idCarga, ModelMap model) {

        Imagen imagen = imagenServicio.obtenerImagenPorId(id);

        model.addAttribute("imagenNombre", imagen.getNombre());
        model.addAttribute("id", id);
        model.put("idCarga", idCarga);

        return "imagen_eliminarCombustible.html";
    }

    @GetMapping("/eliminaCombustible")
    public String eliminaCombustible(@RequestParam Long id, @RequestParam Long idCarga, ModelMap modelo) {

        imagenServicio.eliminarImagenCombustible(id);

            return "redirect:/imagen/eliminadoCombustible/" +idCarga;
    }

    @GetMapping("/eliminadoCombustible/{idCarga}")
    public String eliminadoCombustible(@PathVariable Long idCarga, ModelMap modelo) {

        modelo.put("carga", combustibleServicio.buscarCombustible(idCarga));
        modelo.put("exito", "Imagen de Combustible ELIMINADA con éxito");

        return "combustible_mostrar.html";

    }

    
    @GetMapping("/documentacionChofer/{id}")
    public String documentacionChofer(@PathVariable Long id, Model model, HttpSession session) {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");
        Documentacion documentacion = documentacionServicio.buscarDocumentacion(id);
        Boolean flag = false;
        
        if(documentacion.getAplicaA() == TipoDocumentacion.AplicaA.CAMION){
            if(logueado.getDocumentacion().equalsIgnoreCase("SI") && logueado.getCamion() != null ) {
                if(logueado.getCamion().getDominio().equalsIgnoreCase(documentacion.getCamion().getDominio())){
            flag = true;
                }
            }
        } else if(documentacion.getAplicaA() == TipoDocumentacion.AplicaA.ACOPLADO){
            if(logueado.getDocumentacion().equalsIgnoreCase("SI") && logueado.getAcoplado() != null ) {
                if(logueado.getAcoplado().getDominio().equalsIgnoreCase(documentacion.getAcoplado().getDominio())){
            flag = true;
                }
            }
        } else {
            if(logueado.getDocumentacion().equalsIgnoreCase("SI")) {
            flag = true;
            }
        }
        
        if (documentacion.getImagen() != null) {

            Long idImagen = documentacion.getImagen().getId();
            Imagen imagen = imagenServicio.obtenerImagenPorId(idImagen);

            if (imagen.getTipo().equalsIgnoreCase("application/pdf")) {

                model.addAttribute("imagenNombre", imagen.getNombre());
                model.addAttribute("id", idImagen);
                model.addAttribute("flag", flag);

                return "imagen_mostrarDocumentacionPdf.html";

            } else {

                model.addAttribute("imagenUrl", "/imagen/img/bytes/" + idImagen);
                model.addAttribute("imagenNombre", imagen.getNombre());
                model.addAttribute("id", idImagen);
                model.addAttribute("flag", flag);

                return "imagen_mostrarDocumentacion.html";
                
            }
            
    } else {
            
            if(flag == true){

            model.addAttribute("documentacion", documentacion);

            return "imagen_documentacionCargar.html";
            
            } else {
                
            return "imagen_documentacionMensaje.html";
                
            }

        }
    }
    
    @GetMapping("/verDocumentacion/{id}")
    public String verDocumentacion(@PathVariable Long id, Model model) {

        Documentacion documentacion = documentacionServicio.buscarDocumentacion(id);

        if (documentacion.getImagen() != null) {

            Long idImagen = documentacion.getImagen().getId();
            Imagen imagen = imagenServicio.obtenerImagenPorId(idImagen);

            if (imagen.getTipo().equalsIgnoreCase("application/pdf")) {

                model.addAttribute("imagenNombre", imagen.getNombre());
                model.addAttribute("id", idImagen);
                model.addAttribute("flag", true);

                return "imagen_mostrarDocumentacionPdf.html";

            } else {

                model.addAttribute("imagenUrl", "/imagen/img/bytes/" + idImagen);
                model.addAttribute("imagenNombre", imagen.getNombre());
                model.addAttribute("id", idImagen);
                model.addAttribute("flag", true);

                return "imagen_mostrarDocumentacion.html";
            }

        } else {

            model.addAttribute("documentacion", documentacion);

            return "imagen_documentacionCargar.html";

        }
    }
    
    @PostMapping("/subirImagenesDocumentacion")
    public String subirImagenesDocumentacion(@RequestParam(value = "imagenesCamion", required = false) List<MultipartFile> imagenesCamion,
        @RequestParam(value = "idsCamion", required = false) List<Long> idsCamion, ModelMap modelo) {
        
        Boolean flag = false;
        
    try {
    
        if (idsCamion != null && !idsCamion.isEmpty()) {
            for (int i = 0; i < idsCamion.size(); i++) {
                Long idDoc = idsCamion.get(i);

                MultipartFile archivo = null;
              
                if (imagenesCamion != null && i < imagenesCamion.size()) {
                    archivo = imagenesCamion.get(i);
                }

                if (archivo == null || archivo.isEmpty()) {
                    continue;
                }

                Documentacion documentacion = documentacionServicio.buscarDocumentacion(idDoc);

                Imagen imagen = new Imagen();
                imagen.setNombre(documentacion.getTipoDocumentacion().getNombre());
                imagen.setTipo(archivo.getContentType());

                if ("application/pdf".equals(archivo.getContentType())) {
                    imagen.setDatos(archivo.getBytes());
                } else {
                    imagen.setDatos(optimizeImage(archivo));
                }

                imagenServicio.crearImagenDocumentacion(documentacion.getId(), imagen);
                
                flag = true;
            }
        }
        
        if(flag == true){
            
        return "redirect:/documentacion/listarAdmin?mensaje=exito";
        
        } else {
            
        return "redirect:/documentacion/listarAdmin";
        
        }

    } catch (Exception e) {
        e.printStackTrace();
        return "redirect:/documentacion/listarAdmin?mensaje=error";
    }
}
    
    @PostMapping("/subirImagenesDocumentacionA")
    public String subirImagenesDocumentacionA(@RequestParam(value = "imagenesAcoplado", required = false) List<MultipartFile> imagenesAcoplado,
        @RequestParam(value = "idsAcoplado", required = false) List<Long> idsAcoplado, ModelMap modelo) {
        
        Boolean flag = false;
        
    try {

        if (idsAcoplado != null && !idsAcoplado.isEmpty()) {
            for (int i = 0; i < idsAcoplado.size(); i++) {
                Long idDocA = idsAcoplado.get(i);

                MultipartFile archivoA = null;
                if (imagenesAcoplado != null && i < imagenesAcoplado.size()) {
                    archivoA = imagenesAcoplado.get(i);
                }

                if (archivoA == null || archivoA.isEmpty()) {
                    continue;
                }

                Documentacion documentacionA = documentacionServicio.buscarDocumentacion(idDocA);

                Imagen imagenA = new Imagen();
                imagenA.setNombre(documentacionA.getTipoDocumentacion().getNombre());
                imagenA.setTipo(archivoA.getContentType());

                if ("application/pdf".equals(archivoA.getContentType())) {
                    imagenA.setDatos(archivoA.getBytes());
                } else {
                    imagenA.setDatos(optimizeImage(archivoA));
                }

                imagenServicio.crearImagenDocumentacion(documentacionA.getId(), imagenA);
                
                flag = true;
                
            }
        }
        
        if(flag == true){
            
        return "redirect:/documentacion/listarAdmin?mensaje=exito";
        
        } else {
            
        return "redirect:/documentacion/listarAdmin";
        
        }

    } catch (Exception e) {
        e.printStackTrace();
        return "redirect:/documentacion/listarAdmin?mensaje=error";
    }
}
    
    @PostMapping("/subirImagenesDocumentacionTipo") 
    public String subirImagenesDocumentacionTipo(@RequestParam("documentacionCamionJson") String documentacionCamionJson,
        @RequestParam(required = false) MultipartFile imagenCamion,
        @RequestParam(required = false) Long tipoCamion, ModelMap modelo) throws JsonProcessingException {
        
    ObjectMapper mapper = new ObjectMapper();
    List<Long> ids = mapper.readValue(documentacionCamionJson,
        new com.fasterxml.jackson.core.type.TypeReference<List<Long>>() {});
    
    Boolean flag = false;

    try {
  
        if (imagenCamion != null && !imagenCamion.isEmpty()) {

                MultipartFile archivo = null;

                archivo = imagenCamion;
                
                Imagen imagen = new Imagen();
                imagen.setTipo(archivo.getContentType());

                if ("application/pdf".equals(archivo.getContentType())) {
                    imagen.setDatos(archivo.getBytes());
                } else {
                    imagen.setDatos(optimizeImage(archivo));
                }

                imagenServicio.crearImagenDocumentacionTipo(ids, imagen);
                
                flag = true;

        }

        if(flag == true){
            
        return "redirect:/documentacion/listarAdmin?mensaje=exito";
        
        } else {
            
        return "redirect:/documentacion/listarAdmin";
        
        }

    } catch (Exception e) {
        e.printStackTrace();
        return "redirect:/documentacion/listarAdmin?mensaje=error";
    }

    }
    
     @PostMapping("/subirImagenesDocumentacionTipoA") 
    public String subirImagenesDocumentacionTipoA(@RequestParam("documentacionAcopladoJson") String documentacionAcopladoJson,
        @RequestParam(required = false) MultipartFile imagenAcoplado,
        @RequestParam(required = false) Long tipoAcoplado, ModelMap modelo) throws JsonProcessingException {
        
    ObjectMapper mapper = new ObjectMapper();
    List<Long> idsA = mapper.readValue(documentacionAcopladoJson,
        new com.fasterxml.jackson.core.type.TypeReference<List<Long>>() {});
    
    Boolean flag = false;

    try {
        
        if (imagenAcoplado != null && !imagenAcoplado.isEmpty()) {

                MultipartFile archivo = null;
                
                    archivo = imagenAcoplado;
                
                Imagen imagen = new Imagen();
                imagen.setTipo(archivo.getContentType());

                if ("application/pdf".equals(archivo.getContentType())) {
                    imagen.setDatos(archivo.getBytes());
                } else {
                    imagen.setDatos(optimizeImage(archivo));
                }

                imagenServicio.crearImagenDocumentacionTipo(idsA, imagen);
                
                flag = true;
            
        }

        if(flag == true){
            
        return "redirect:/documentacion/listarAdmin?mensaje=exito";
        
        } else {
            
        return "redirect:/documentacion/listarAdmin";
        
        }

    } catch (Exception e) {
        e.printStackTrace();
        return "redirect:/documentacion/listarAdmin?mensaje=error";
    }

    }
    
    @PostMapping("/subirImagenesDocumentacionTipoChofer") 
    public String subirImagenesDocumentacionTipoChofer(@RequestParam("documentacionChoferJson") String documentacionChoferJson,
        @RequestParam(required = false) MultipartFile imagenChofer,
        @RequestParam(required = false) Long tipoChofer, ModelMap modelo) throws JsonProcessingException {
        
    ObjectMapper mapper = new ObjectMapper();
    List<Long> ids = mapper.readValue(documentacionChoferJson,
        new com.fasterxml.jackson.core.type.TypeReference<List<Long>>() {});
    
    Boolean flag = false;

    try {
        // === Procesar imágenes de Camión ===
        if (imagenChofer != null && !imagenChofer.isEmpty()) {

                MultipartFile archivo = null;

                    archivo = imagenChofer;

                Imagen imagen = new Imagen();
                imagen.setTipo(archivo.getContentType());

                if ("application/pdf".equals(archivo.getContentType())) {
                    imagen.setDatos(archivo.getBytes());
                } else {
                    imagen.setDatos(optimizeImage(archivo));
                }

                imagenServicio.crearImagenDocumentacionTipo(ids, imagen);
                
                flag = true;
            
        }

        if(flag == true){
            
        return "redirect:/documentacion/listarAdmin?mensaje=exito";
        
        } else {
            
        return "redirect:/documentacion/listarAdmin";
        
        }

    } catch (Exception e) {
        e.printStackTrace();
        return "redirect:/documentacion/listarAdmin?mensaje=error";
    }

    }
    
     @PostMapping("/subirImagenesDocumentacionAdmin")
      public String subirImagenesDocumentacionAdmin(@RequestParam(value = "imagenesChofer", required = false) List<MultipartFile> imagenesChofer,
        @RequestParam(value = "idsChofer", required = false) List<Long> idsChofer, ModelMap modelo) {
          
          Boolean flag = false;

    try {

        if (idsChofer != null && !idsChofer.isEmpty()) {
            for (int i = 0; i < idsChofer.size(); i++) {
                Long idDoc = idsChofer.get(i);

                MultipartFile archivo = null;
                // Verificar si existe un archivo correspondiente
                if (imagenesChofer != null && i < imagenesChofer.size()) {
                    archivo = imagenesChofer.get(i);
                }

                // Si no se subió archivo, continuar sin error
                if (archivo == null || archivo.isEmpty()) {
                    continue;
                }

                Documentacion documentacion = documentacionServicio.buscarDocumentacion(idDoc);

                Imagen imagen = new Imagen();
                imagen.setNombre(documentacion.getTipoDocumentacion().getNombre());
                imagen.setTipo(archivo.getContentType());

                if ("application/pdf".equals(archivo.getContentType())) {
                    imagen.setDatos(archivo.getBytes());
                } else {
                    imagen.setDatos(optimizeImage(archivo));
                }

                imagenServicio.crearImagenDocumentacion(documentacion.getId(), imagen);
                
                flag = true;
                
            }
        }

        if(flag == true){
            
        return "redirect:/documentacion/listarAdmin?mensaje=exito";
        
        } else {
            
        return "redirect:/documentacion/listarAdmin";
        
        }

    } catch (Exception e) {
        e.printStackTrace();
        return "redirect:/documentacion/listarAdmin?mensaje=error";
    }
}
      
       @PostMapping("/subirImagenesDocumentacionChofer")
      public String subirImagenesDocumentacionChofer(@RequestParam(value = "imagenesChofer", required = false) List<MultipartFile> imagenesChofer,
        @RequestParam(value = "idsChofer", required = false) List<Long> idsChofer, ModelMap modelo) {
          
          Boolean flag = false;

    try {

        if (idsChofer != null && !idsChofer.isEmpty()) {
            for (int i = 0; i < idsChofer.size(); i++) {
                Long idDoc = idsChofer.get(i);

                MultipartFile archivo = null;
                // Verificar si existe un archivo correspondiente
                if (imagenesChofer != null && i < imagenesChofer.size()) {
                    archivo = imagenesChofer.get(i);
                }

                // Si no se subió archivo, continuar sin error
                if (archivo == null || archivo.isEmpty()) {
                    continue;
                }

                Documentacion documentacion = documentacionServicio.buscarDocumentacion(idDoc);

                Imagen imagen = new Imagen();
                imagen.setNombre(documentacion.getTipoDocumentacion().getNombre());
                imagen.setTipo(archivo.getContentType());

                if ("application/pdf".equals(archivo.getContentType())) {
                    imagen.setDatos(archivo.getBytes());
                } else {
                    imagen.setDatos(optimizeImage(archivo));
                }

                imagenServicio.crearImagenDocumentacion(documentacion.getId(), imagen);
                
                flag = true;
                
            }
        }
        
        if(flag == true){
            
        return "redirect:/index?mensaje=exito";
        
        } else {
            
        return "redirect:/index";
        
        }

    } catch (Exception e) {
        e.printStackTrace();
        return "redirect:/index?mensaje=error";
    }
}
      
      @PostMapping("/subirImagenesDocumentacionChoferVehiculo")
    public String subirImagenesDocumentacionChoferVehiculo(@RequestParam(value = "imagenesCamion", required = false) List<MultipartFile> imagenesCamion,
        @RequestParam(value = "idsCamion", required = false) List<Long> idsCamion,
        @RequestParam(value = "imagenesAcoplado", required = false) List<MultipartFile> imagenesAcoplado,
        @RequestParam(value = "idsAcoplado", required = false) List<Long> idsAcoplado,
        ModelMap modelo) {
        
        Boolean flag = false;

    try {
        // === Procesar imágenes de Camión ===
        if (idsCamion != null && !idsCamion.isEmpty()) {
            for (int i = 0; i < idsCamion.size(); i++) {
                Long idDoc = idsCamion.get(i);

                MultipartFile archivo = null;
                // Verificar si existe un archivo correspondiente
                if (imagenesCamion != null && i < imagenesCamion.size()) {
                    archivo = imagenesCamion.get(i);
                }

                // Si no se subió archivo, continuar sin error
                if (archivo == null || archivo.isEmpty()) {
                    continue;
                }

                Documentacion documentacion = documentacionServicio.buscarDocumentacion(idDoc);

                Imagen imagen = new Imagen();
                imagen.setNombre(documentacion.getTipoDocumentacion().getNombre());
                imagen.setTipo(archivo.getContentType());

                if ("application/pdf".equals(archivo.getContentType())) {
                    imagen.setDatos(archivo.getBytes());
                } else {
                    imagen.setDatos(optimizeImage(archivo));
                }

                imagenServicio.crearImagenDocumentacion(documentacion.getId(), imagen);
                
                
                flag = true;
                
            }
        }

        // === Procesar imágenes de Acoplado ===
        if (idsAcoplado != null && !idsAcoplado.isEmpty()) {
            for (int i = 0; i < idsAcoplado.size(); i++) {
                Long idDocA = idsAcoplado.get(i);

                MultipartFile archivoA = null;
                if (imagenesAcoplado != null && i < imagenesAcoplado.size()) {
                    archivoA = imagenesAcoplado.get(i);
                }

                if (archivoA == null || archivoA.isEmpty()) {
                    continue;
                }

                Documentacion documentacionA = documentacionServicio.buscarDocumentacion(idDocA);

                Imagen imagenA = new Imagen();
                imagenA.setNombre(documentacionA.getTipoDocumentacion().getNombre());
                imagenA.setTipo(archivoA.getContentType());

                if ("application/pdf".equals(archivoA.getContentType())) {
                    imagenA.setDatos(archivoA.getBytes());
                } else {
                    imagenA.setDatos(optimizeImage(archivoA));
                }

                imagenServicio.crearImagenDocumentacion(documentacionA.getId(), imagenA);
                
                flag = true;
                
            }
        }

        if(flag == true){
            
        return "redirect:/index?mensaje=exito";
        
        } else {
            
        return "redirect:/index";
        
        }

    } catch (Exception e) {
        e.printStackTrace();
        return "redirect:/index?mensaje=error";
    }
}

    @PostMapping("/cargaDocumentacion")
    public String cargaDocumentacion(@RequestParam Long id, @RequestParam("file") MultipartFile file, ModelMap modelo, HttpSession session) throws IOException {

        Documentacion documentacion = documentacionServicio.buscarDocumentacion(id);

        try {
            Imagen imagen = new Imagen();
            imagen.setNombre(documentacion.getTipoDocumentacion().getNombre());
            imagen.setTipo(file.getContentType());
            
            if (file.getContentType().equals("application/pdf")) {
                imagen.setDatos(file.getBytes());
            } else {
                imagen.setDatos(optimizeImage(file));
            }

            imagenServicio.crearImagenDocumentacion(id, imagen);
            
            return "redirect:/imagen/cargadoDocumentacion/" + id;

        } catch (Exception e) {
            modelo.addAttribute("documentacion", documentacion);
            modelo.addAttribute("error", "Ocurrió un error al procesar su imagen. Intente nuevamente o ingrese otro archivo");
            return "imagen_documentacionCargar.html";
        }
    }
    
    @GetMapping("/cargadoDocumentacion/{id}")
    public String cargadoDocumentacion(@PathVariable Long id, ModelMap modelo, HttpSession session) {
        
        Usuario logueado = (Usuario) session.getAttribute("usuariosession");
            
            if(logueado.getRol().equalsIgnoreCase("CHOFER")){
                
            modelo.put("documentacion", documentacionServicio.buscarDocumentacionDiasVigencia(id));
            modelo.put("exito", "Imagen de Documentacion CARGADA con éxito");
            
            return "documentacion_mostrarChofer.html";
                
                
            } else {
            
            modelo.put("documentacion", documentacionServicio.buscarDocumentacionDiasVigencia(id));
            modelo.put("exito", "Imagen de Documentación CARGADA con éxito");
            
            return "documentacion_mostrar.html"; 
            
            }

    }
    
    @GetMapping("/descargarDocumentacionPdf/{id}")
    public String descargarDocumentacionPdf(@PathVariable Long id, ModelMap modelo) {

        Imagen imagen = imagenServicio.obtenerImagenPorId(id);

        modelo.addAttribute("imagenUrl", "/imagen/pdf/" + id);
        modelo.addAttribute("imagenNombre", imagen.getNombre());
        modelo.addAttribute("id", id);

        return "imagen_descargarDocumentacionPdf.html";
        
    }
    
    @GetMapping("/modificarDocumentacion/{id}") //llega id de Imagen
    public String modificarDocumentacion(@PathVariable Long id, ModelMap modelo) {
        
        modelo.put("id", id);

        return "imagen_documentacionModificar.html";
    }

    @PostMapping("/modificaDocumentacion")
    public String modificaDocumentacion(@RequestParam Long id, @RequestParam("file") MultipartFile file, ModelMap modelo) throws IOException {

        Documentacion documentacion = documentacionServicio.buscarDocumentacionIdImagen(id);

        try {

            Imagen imagen = new Imagen();
            imagen.setNombre(documentacion.getTipoDocumentacion().getNombre());
            imagen.setTipo(file.getContentType());
            if (file.getContentType().equals("application/pdf")) {
                imagen.setDatos(file.getBytes());
            } else {
                imagen.setDatos(optimizeImage(file));
            }

            imagenServicio.modificarImagen(id, imagen);
            
            return "redirect:/imagen/modificadoDocumentacion/" + documentacion.getId(); 

        } catch (Exception e) {
            modelo.addAttribute("id", id);
            modelo.addAttribute("error", "Ocurrió un error al procesar su imagen. Intente nuevamente o ingrese otro archivo");
            return "imagen_documentacionModificar.html";
        }

    }
    
    @GetMapping("/modificadoDocumentacion/{id}")
    public String modificadoDocumentacion(@PathVariable Long id, ModelMap modelo, HttpSession session) {

            Usuario logueado = (Usuario) session.getAttribute("usuariosession");
            
            if(logueado.getRol().equalsIgnoreCase("CHOFER")){
                
            modelo.put("documentacion", documentacionServicio.buscarDocumentacionDiasVigencia(id));
            modelo.put("exito", "Imagen de Documentacion MODIFICADA con éxito");
            
            return "documentacion_mostrarChofer.html";
                
                
            } else {
        
            modelo.put("documentacion", documentacionServicio.buscarDocumentacionDiasVigencia(id));
            modelo.put("exito", "Imagen de Documentacion MODIFICADA con éxito");
            
            return "documentacion_mostrar.html";
            
            }

    }
    
    @GetMapping("/eliminarDocumentacion/{id}")
    public String eliminarDocumentacion(@PathVariable Long id, ModelMap model) {
        
        Imagen imagen = imagenServicio.obtenerImagenPorId(id); 
           
        model.addAttribute("imagenNombre", imagen.getNombre());
        model.addAttribute("id", id);

        return "imagen_eliminarDocumentacion.html";

    }

    @GetMapping("/eliminaDocumentacion/{id}")
    public String eliminaDocumentacion(@PathVariable Long id, ModelMap modelo) {

        Documentacion documentacion = documentacionServicio.buscarDocumentacionIdImagen(id);
        
        imagenServicio.eliminarImagenDocumentacion(id, documentacion.getId());
        
        return "redirect:/imagen/eliminadoDocumentacion/" +documentacion.getId(); 
        
    }
    
    @GetMapping("/eliminadoDocumentacion/{id}")
    public String eliminadoDocumentacion(@PathVariable Long id, ModelMap modelo, HttpSession session) {
        
        Usuario logueado = (Usuario) session.getAttribute("usuariosession");
            
            if(logueado.getRol().equalsIgnoreCase("CHOFER")){
                
            modelo.put("documentacion", documentacionServicio.buscarDocumentacionDiasVigencia(id));
            modelo.put("exito", "Imagen de Documentacion ELIMINADA con éxito");
            
            return "documentacion_mostrarChofer.html";
                
            } else {

            modelo.put("documentacion", documentacionServicio.buscarDocumentacionDiasVigencia(id));
            modelo.put("exito", "Imagen de Documentacion ELIMINADA con éxito");
            
            return "documentacion_mostrar.html";
            
            }

    }
    
    @GetMapping("/verLogo/{id}")
    public String verLogo(@PathVariable Long id, ModelMap modelo, HttpSession session) {

        Usuario usuario = usuarioServicio.buscarUsuario(id);

        modelo.put("idUsuario", id);

        if (usuario.getLogo() != null) {

            Long idLogo = usuario.getLogo().getId();
            Imagen imagen = imagenServicio.obtenerImagenPorId(idLogo);
                
                modelo.addAttribute("imagenUrl", "/imagen/img/bytes/" + idLogo);
                modelo.addAttribute("imagenNombre", imagen.getNombre());
                modelo.addAttribute("id", idLogo);

                return "imagen_logoMostrar.html";
                
            }  else {

            return "imagen_logoCargar.html";

        }
    }
    
    @PostMapping("/cargaLogo")
    public String cargaLogo(@RequestParam("file") MultipartFile file, ModelMap modelo, HttpSession session) throws IOException {

        Usuario usuario = (Usuario) session.getAttribute("usuariosession");

        try {
            
            Imagen logo = new Imagen();
            
            logo.setNombre(usuario.getEmpresa());
            logo.setTipo(file.getContentType());
            
            logo.setDatos(optimizeImage(file));

            imagenServicio.crearImagenLogo(usuario.getIdOrg(), logo);
            
            return "redirect:/imagen/logoCargado";

        } catch (Exception e) {
            
            modelo.addAttribute("idUsuario", usuario.getId());
            modelo.addAttribute("error", "Ocurrió un error al procesar su imagen. Intente nuevamente o ingrese otro archivo");
            
            return "imagen_logoCargar.html";
        }
    }
    
    @GetMapping("/logoCargado")
    public String logoCargado(ModelMap modelo, HttpSession session) {
        
        Usuario logueado = (Usuario) session.getAttribute("usuariosession");
            
            modelo.put("usuario", logueado);
            modelo.put("exito", "Logo CARGADO con éxito");
            
            return "usuario_mostrar.html"; 

    }
    
    @GetMapping("/logoModificar") //llega id de Imagen
    public String logoModificar(@RequestParam Long id, @RequestParam Long idUsuario, ModelMap modelo) {
        
        modelo.put("id", id);
        modelo.put("idUsuario", idUsuario);

        return "imagen_logoModificar.html";
        
    }

    @PostMapping("/logoModifica")
    public String logoModifica(@RequestParam Long id, @RequestParam("file") MultipartFile file, ModelMap modelo, HttpSession session) throws IOException {

        try {
            
            Usuario logueado = (Usuario) session.getAttribute("usuariosession");

            Imagen logo = new Imagen();
            
            logo.setNombre(logueado.getEmpresa());
            logo.setTipo(file.getContentType());

            logo.setDatos(optimizeImage(file));
            
            imagenServicio.modificarImagen(id, logo);
            
            return "redirect:/imagen/logoCargado";

        } catch (Exception e) {
            
            modelo.addAttribute("id", id);
            modelo.addAttribute("error", "Ocurrió un error al procesar su imagen. Intente nuevamente o ingrese otro archivo");
            
            return "imagen_logoModificar.html";
        }

    }
    
    @GetMapping("/logoEliminar")
    public String logoEliminar(@RequestParam Long id, @RequestParam Long idUsuario, ModelMap model) {
        
        Imagen imagen = imagenServicio.obtenerImagenPorId(id); 
           
        model.addAttribute("imagenNombre", imagen.getNombre());
        model.addAttribute("id", id);
        model.addAttribute("idUsuario", idUsuario);

        return "imagen_logoEliminar.html";

    }

    @GetMapping("/logoElimina")
    public String logoElimina(@RequestParam Long id, ModelMap modelo, HttpSession session) {
        
        Usuario logueado = (Usuario) session.getAttribute("usuariosession");
        
        imagenServicio.eliminarLogo(id, logueado.getIdOrg());
        
        return "redirect:/imagen/logoEliminado"; 
        
    }
    
    @GetMapping("/logoEliminado")
    public String logoEliminado( ModelMap modelo, HttpSession session) {
        
        Usuario logueado = (Usuario) session.getAttribute("usuariosession");
            
            modelo.put("usuario", logueado);
            modelo.put("exito", "Logo ELIMINADO con éxito");
            
            return "usuario_mostrar.html";   

    }

    public byte[] optimizeImage(MultipartFile file) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Thumbnails.of(file.getInputStream())
                .size(1024, 768) // Ajusta el tamaño según tus necesidades
                .outputQuality(0.99) // Ajusta la calidad según tus necesidades
                .toOutputStream(outputStream);
        return outputStream.toByteArray();
    }

    @GetMapping("/img/bytes/{id}")
    public ResponseEntity<byte[]> obtenerImagen(@PathVariable Long id) {
        Imagen imagen = imagenServicio.obtenerImagenPorId(id);
        if (imagen != null) {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.valueOf(imagen.getTipo()));
            return new ResponseEntity<>(imagen.getDatos(), headers, HttpStatus.OK);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/pdf/{id}")
    public ResponseEntity<byte[]> getPdf(@PathVariable Long id) {
        Imagen imagen = imagenServicio.obtenerImagenPorId(id);
        byte[] pdfContent = imagen.getDatos();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("inline", imagen.getNombre() + ".pdf");
        return new ResponseEntity<>(pdfContent, headers, HttpStatus.OK);
    }

}
