package abate.abate.controladores;

import abate.abate.entidades.Combustible;
import abate.abate.entidades.Flete;
import abate.abate.entidades.Gasto;
import abate.abate.entidades.Imagen;
import abate.abate.entidades.Usuario;
import abate.abate.servicios.CombustibleServicio;
import abate.abate.servicios.FleteServicio;
import abate.abate.servicios.GastoServicio;
import abate.abate.servicios.ImagenServicio;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
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

    @GetMapping("/cargadoGasto/{id}")
    public String cargadoGasto(@PathVariable Long id, ModelMap modelo) {

        Flete flete = fleteServicio.buscarFlete(id);

        modelo.put("gasto", gastoServicio.buscarGasto(flete.getGasto().getId()));
        modelo.put("id", id);

        modelo.put("exito", "Imagen de Gasto CARGADA con éxito");

        return "gasto_registradoImg.html";

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

        return "gasto_registradoImg.html";

    }

    @GetMapping("/cargarCombustible/{id}")
    public String registrarCombustible(@PathVariable Long id, ModelMap modelo) {

        modelo.put("carga", combustibleServicio.buscarCombustible(id));

        return "imagen_combustibleCargar.html";
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
        modelo.put("fecha", carga.getFechaCarga());
        modelo.put("exito", "Imagen de Diesel CARGADA con éxito");

        return "combustible_registrado.html";

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
    public String cargarImagenCP(@RequestParam Long id, @RequestParam("file") MultipartFile file, ModelMap modelo, HttpSession session) throws IOException {

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

            Usuario logueado = (Usuario) session.getAttribute("usuariosession");

            if (logueado.getRol().equalsIgnoreCase("CHOFER")) {

                return "redirect:/imagen/cargadoCPChofer/" + id;

            } else {

                return "redirect:/imagen/cargadoCPAdmin/" + id;

            }

        } catch (Exception e) {
            modelo.addAttribute("flete", flete);
            modelo.addAttribute("error", "Ocurrió un error al procesar su imagen. Intente nuevamente o ingrese otro archivo");
            return "imagen_CPcargar.html";
        }

    }

    @GetMapping("/cargadoCPChofer/{id}")
    public String cargadoCPChofer(@PathVariable Long id, ModelMap modelo) {

        Flete flete = fleteServicio.buscarFlete(id);

        modelo.put("flete", flete);
        modelo.put("fecha", flete.getFechaFlete());
        modelo.put("exito", "CP CARGADA con éxito");

        return "flete_modificadoChofer.html";

    }

    @GetMapping("/cargadoCPAdmin/{id}")
    public String cargadoCPAdmin(@PathVariable Long id, ModelMap modelo) {

        Flete flete = fleteServicio.buscarFlete(id);

        modelo.put("flete", flete);
        modelo.put("fecha", flete.getFechaFlete());
        modelo.put("exito", "CP CARGADA con éxito");

        return "flete_modificadoAdmin.html";

    }

    @PostMapping("/cargaDescarga")
    public String cargarImagenDescarga(@RequestParam Long id, @RequestParam("file") MultipartFile file, ModelMap modelo, HttpSession session) throws IOException {
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

            Usuario logueado = (Usuario) session.getAttribute("usuariosession");

            if (logueado.getRol().equalsIgnoreCase("CHOFER")) {

                return "redirect:/imagen/cargadoDescargaChofer/" + id;

            } else {

                return "redirect:/imagen/cargadoDescargaAdmin/" + id;

            }

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
        modelo.put("fecha", flete.getFechaFlete());
        modelo.put("exito", "Ticket de Descarga CARGADO con éxito");

        return "flete_modificadoChofer.html";

    }

    @GetMapping("/cargadoDescargaAdmin/{id}")
    public String cargadoDescargaAdmin(@PathVariable Long id, ModelMap modelo) {

        Flete flete = fleteServicio.buscarFlete(id);

        modelo.put("flete", flete);
        modelo.put("fecha", flete.getFechaFlete());
        modelo.put("exito", "Ticket de Descarga CARGADO con éxito");

        return "flete_modificadoAdmin.html";

    }

    @PostMapping("/cargaDescargaDesdeFlete")
    public String cargarImagenDescargaDesdeFlete(@RequestParam Long id, @RequestParam("file") MultipartFile file, ModelMap modelo) throws IOException {

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

            return "redirect:/imagen/cargadoDesDesdeFlete/" + id;

        } catch (Exception e) {
            modelo.addAttribute("flete", flete);
            modelo.addAttribute("error", "Ocurrió un error al procesar su imagen. Intente nuevamente o ingrese otro archivo");
            return "imagen_descargaCargarDesdeFlete.html";
        }

    }

    @GetMapping("/cargadoDesDesdeFlete/{id}")
    public String gastoRegistrado(@PathVariable Long id, ModelMap modelo) {

        Flete flete = fleteServicio.buscarFlete(id);

        modelo.put("flete", flete);
        modelo.put("exito", "CP y Ticket de Descarga CARGADO con éxito");

        return "flete_agregarGasto.html";

    }

    @GetMapping("/verGasto/{id}")
    public String verGasto(@PathVariable Long id, ModelMap modelo, HttpSession session) {

        Flete flete = fleteServicio.buscarFlete(id);
        Gasto gasto = gastoServicio.buscarGasto(flete.getGasto().getId());

        if (gasto.getImagen() != null) {

            Long idImagen = gasto.getImagen().getId();
            Imagen imagen = imagenServicio.obtenerImagenPorId(idImagen);
            
            Usuario logueado = (Usuario) session.getAttribute("usuariosession");
            
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

                return "imagen_mostrarGastoAdmin.html";
            
                
            } else if ((logueado.getRol().equalsIgnoreCase("CHOFER")) && (imagen.getTipo().equalsIgnoreCase("application/pdf"))) {
                
                modelo.addAttribute("imagenNombre", imagen.getNombre());
                modelo.addAttribute("id", idImagen);
                modelo.addAttribute("estado", gasto.getEstado());

                return "imagen_mostrarGastoPdf.html";
                
            }  else {
                
                modelo.addAttribute("imagenNombre", imagen.getNombre());
                modelo.addAttribute("id", idImagen);

                return "imagen_mostrarGastoPdfAdmin.html";
                
            }

        } else {

            modelo.put("flete", flete);

            return "imagen_gastoCargar.html";

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

        Gasto gasto = gastoServicio.buscarGasto(id);

        if (gasto.getImagen() != null) {

            Long idImagen = gasto.getImagen().getId();
            Imagen imagen = imagenServicio.obtenerImagenPorId(idImagen);
            
            Usuario logueado = (Usuario) session.getAttribute("usuariosession");
            
            if ((logueado.getRol().equalsIgnoreCase("CHOFER")) && (!imagen.getTipo().equalsIgnoreCase("application/pdf"))) {
                
                modelo.addAttribute("imagenUrl", "/imagen/img/bytes/" + idImagen);
                modelo.addAttribute("imagenNombre", imagen.getNombre());
                modelo.addAttribute("id", idImagen);
                modelo.addAttribute("estado", gasto.getEstado());

                return "imagen_mostrarGastoCaja.html";
                
            } else if ((logueado.getRol().equalsIgnoreCase("ADMIN")) && (!imagen.getTipo().equalsIgnoreCase("application/pdf"))) {    
                
                modelo.addAttribute("imagenUrl", "/imagen/img/bytes/" + idImagen);
                modelo.addAttribute("imagenNombre", imagen.getNombre());
                modelo.addAttribute("id", idImagen);

                return "imagen_mostrarGastoCajaAdmin.html";
            
                
           } else if ((logueado.getRol().equalsIgnoreCase("CHOFER")) && (imagen.getTipo().equalsIgnoreCase("application/pdf"))) {

                modelo.addAttribute("imagenNombre", imagen.getNombre());
                modelo.addAttribute("id", idImagen);
                modelo.addAttribute("estado", gasto.getEstado());

                return "imagen_mostrarGastoPdfCaja.html";

            } else {
               
               modelo.addAttribute("imagenNombre", imagen.getNombre());
               modelo.addAttribute("id", idImagen);

               return "imagen_mostrarGastoPdfCajaAdmin.html";

            }

        } else {

            modelo.put("gasto", gastoServicio.buscarGasto(id));

            return "imagen_gastoCargarCaja.html";

        }
    }

    @GetMapping("/descargarGastoPdf/{id}")
    public String descargarGastoPdf(@PathVariable Long id, ModelMap modelo) {

        Imagen imagen = imagenServicio.obtenerImagenPorId(id);

        modelo.addAttribute("imagenUrl", "/imagen/pdf/" + id);
        modelo.addAttribute("imagenNombre", imagen.getNombre());
        modelo.addAttribute("id", id);

        return "imagen_descargarGastoPdf.html";
    }

    @GetMapping("/modificarGasto/{id}") //llega id de Imagen
    public String modificarGasto(@PathVariable Long id, ModelMap modelo) {

        modelo.put("id", id);

        return "imagen_gastoModificar.html";
    }

    @PostMapping("/modificaGasto")
    public String modificaGasto(@RequestParam Long id, @RequestParam("file") MultipartFile file, ModelMap modelo) throws IOException {

        Gasto gasto = gastoServicio.buscarGastoIdImagen(id);
        Flete flete = fleteServicio.buscarFleteIdGasto(gasto.getId());

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

            return "redirect:/imagen/modificadoGasto/" + id;

        } catch (Exception e) {
            modelo.addAttribute("id", id);
            modelo.addAttribute("error", "Ocurrió un error al procesar su imagen. Intente nuevamente o ingrese otro archivo");
            return "imagen_gastoModificar.html";
        }

    }

    @GetMapping("/modificadoGasto/{id}")
    public String modificadoGasto(@PathVariable Long id, ModelMap modelo) {

        modelo.put("gasto", gastoServicio.buscarGastoIdImagen(id));
        modelo.put("exito", "Imagen de Gasto MODIFICADO con éxito");

        return "gasto_registradoImg.html";

    }

    @GetMapping("/modificarGastoCaja/{id}") //llega id de Imagen
    public String modificarGastoCaja(@PathVariable Long id, ModelMap modelo) {

        modelo.put("id", id);

        return "imagen_gastoModificarCaja.html";
    }

    @PostMapping("/modificaGastoCaja")
    public String modificaGastoCaja(@RequestParam Long id, @RequestParam("file") MultipartFile file, ModelMap modelo) throws IOException {

        Gasto gasto = gastoServicio.buscarGastoIdImagen(id);

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

            return "redirect:/imagen/modificadoGastoCaja/" + id;

        } catch (Exception e) {
            modelo.addAttribute("id", id);
            modelo.addAttribute("error", "Ocurrió un error al procesar su imagen. Intente nuevamente o ingrese otro archivo");
            return "imagen_gastoModificar.html";
        }

    }

    @GetMapping("/modificadoGastoCaja/{id}")
    public String modificadoGastoCaja(@PathVariable Long id, ModelMap modelo) {

        modelo.put("gasto", gastoServicio.buscarGastoIdImagen(id));
        modelo.put("exito", "Imagen de Gasto MODIFICADO con éxito");

        return "gasto_registradoImg.html";

    }

    @GetMapping("/eliminarGasto/{id}")
    public String eliminarGasto(@PathVariable Long id, ModelMap model) {

        Imagen imagen = imagenServicio.obtenerImagenPorId(id);

        model.addAttribute("imagenNombre", imagen.getNombre());
        model.addAttribute("id", id);

        return "imagen_eliminarGasto.html";
    }

    @GetMapping("/eliminaGasto/{id}")
    public String eliminaGasto(@PathVariable Long id, HttpSession session, ModelMap modelo) {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        imagenServicio.eliminarImagenGasto(id);

        if (logueado.getRol().equalsIgnoreCase("CHOFER")) {

            return "redirect:/imagen/eliminadoGasto";

        } else {

            return "redirect:/imagen/eliminadoGastoAdmin";

        }
    }

    @GetMapping("/eliminadoGasto")
    public String eliminado(ModelMap modelo, HttpSession session) {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        modelo.put("chofer", logueado);
        modelo.put("exito", "Imagen de Gasto ELIMINADA con éxito");

        return "index_chofer.html";

    }

    @GetMapping("/eliminadoGastoAdmin")
    public String eliminadoGastoAdmin(ModelMap modelo, HttpSession session) {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        modelo.put("id", logueado.getId());
        modelo.put("exito", "Imagen de Gasto ELIMINADA con éxito");

        return "index_admin.html";

    }

    @GetMapping("/verCP/{id}")
    public String verCP(@PathVariable Long id, Model model, HttpSession session) {

        Flete flete = fleteServicio.buscarFlete(id);

        if (flete.getImagenCP() != null) {

            Long idImagen = flete.getImagenCP().getId();
            Imagen imagen = imagenServicio.obtenerImagenPorId(idImagen);
            
            Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        if ((logueado.getRol().equalsIgnoreCase("CHOFER")) && (imagen.getTipo().equalsIgnoreCase("application/pdf"))) {

                model.addAttribute("imagenNombre", imagen.getNombre());
                model.addAttribute("id", idImagen);
                model.addAttribute("estado", flete.getEstado());

                return "imagen_mostrarCPpdf.html";

            } else if ((logueado.getRol().equalsIgnoreCase("ADMIN")) && (imagen.getTipo().equalsIgnoreCase("application/pdf"))) {

                model.addAttribute("imagenNombre", imagen.getNombre());
                model.addAttribute("id", idImagen);

                return "imagen_mostrarCPpdfPendiente.html";
                
            } else if ((logueado.getRol().equalsIgnoreCase("CHOFER")) && (!imagen.getTipo().equalsIgnoreCase("application/pdf"))) {

                model.addAttribute("imagenUrl", "/imagen/img/bytes/" + idImagen);
                model.addAttribute("imagenNombre", imagen.getNombre());
                model.addAttribute("id", idImagen);
                model.addAttribute("estado", flete.getEstado());

                return "imagen_mostrarCP.html";

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

    @GetMapping("/verCPPendiente/{id}")
    public String verCPdesdePendiente(@PathVariable Long id, Model model) {

        Flete flete = fleteServicio.buscarFlete(id);

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

                return "imagen_mostrarCPAdmin.html";
            }

        } else {

            model.addAttribute("flete", flete);

            return "imagen_CPcargar.html";

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

    @GetMapping("/descargarCPpdf/{id}")
    public String descargarCPpdf(@PathVariable Long id, ModelMap modelo) {

        Imagen imagen = imagenServicio.obtenerImagenPorId(id);

        modelo.addAttribute("imagenUrl", "/imagen/pdf/" + id);
        modelo.addAttribute("imagenNombre", imagen.getNombre());
        modelo.addAttribute("id", id);

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
    public String modificarCP(@PathVariable Long id, ModelMap modelo) {

        modelo.put("id", id);

        return "imagen_CPmodificar.html";
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

                return "redirect:/imagen/modificadoCP/" + id;

            } else {

                return "redirect:/imagen/modificadoCPAdmin/" + id;

            }

        } catch (Exception e) {
            modelo.addAttribute("id", id);
            modelo.addAttribute("error", "Ocurrió un error al procesar su imagen. Intente nuevamente o ingrese otro archivo");
            return "imagen_CPmodificar.html";
        }

    }

    @GetMapping("/modificadoCP/{id}")
    public String modificadoCP(@PathVariable Long id, ModelMap modelo) {

        Flete flete = fleteServicio.buscarFleteIdImagenCP(id);

        modelo.put("flete", flete);
        modelo.put("exito", "CP MODIFICADA con éxito");

        return "flete_modificadoChofer.html";

    }

    @GetMapping("/modificadoCPAdmin/{id}")
    public String modificadoCPAdmin(@PathVariable Long id, ModelMap modelo) {

        Flete flete = fleteServicio.buscarFleteIdImagenCP(id);

        modelo.put("flete", flete);
        modelo.put("exito", "CP MODIFICADA con éxito");

        return "flete_modificadoAdmin.html";

    }

    @GetMapping("/eliminarCP/{id}")
    public String eliminarCP(@PathVariable Long id, ModelMap model) {

        Imagen imagen = imagenServicio.obtenerImagenPorId(id);

        model.addAttribute("imagenNombre", imagen.getNombre());
        model.addAttribute("id", id);

        return "imagen_eliminarCP.html";
    }

    @GetMapping("/eliminaCP/{id}")
    public String eliminaCP(@PathVariable Long id, HttpSession session, ModelMap modelo) {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        imagenServicio.eliminarImagenCP(id);

        if (logueado.getRol().equalsIgnoreCase("CHOFER")) {

            return "redirect:/imagen/eliminadoCP";

        } else {

            return "redirect:/imagen/elimiadoCPAdmin";

        }
    }

    @GetMapping("/eliminadoCP")
    public String eliminadoCP(ModelMap modelo, HttpSession session) {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        modelo.put("chofer", logueado);
        modelo.put("exito", "Carta de Porte ELIMINADA con éxito");

        return "index_chofer.html";

    }

    @GetMapping("/eliminadoCPAdmin")
    public String eliminadoCPAdmin(ModelMap modelo, HttpSession session) {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        modelo.put("id", logueado.getId());
        modelo.put("exito", "Carta de Porte ELIMINADA con éxito");

        return "index_admin.html";

    }

    @GetMapping("/verDescarga/{id}")
    public String verDescarga(@PathVariable Long id, Model model, HttpSession session) {

        Flete flete = fleteServicio.buscarFlete(id);

        if (flete.getImagenDescarga() != null) {

            Long idImagen = flete.getImagenDescarga().getId();
            Imagen imagen = imagenServicio.obtenerImagenPorId(idImagen);
            Usuario logueado = (Usuario) session.getAttribute("usuariosession");
            
            if ((logueado.getRol().equalsIgnoreCase("CHOFER")) && (!imagen.getTipo().equalsIgnoreCase("application/pdf"))) {
            
                model.addAttribute("imagenUrl", "/imagen/img/bytes/" + idImagen);
                model.addAttribute("imagenNombre", imagen.getNombre());
                model.addAttribute("id", idImagen);
                model.addAttribute("estado", flete.getEstado());

                return "imagen_mostrarDescarga.html";
            
            } else if ((logueado.getRol().equalsIgnoreCase("ADMIN")) && (!imagen.getTipo().equalsIgnoreCase("application/pdf"))) {
                
                model.addAttribute("imagenUrl", "/imagen/img/bytes/" + idImagen);
                model.addAttribute("imagenNombre", imagen.getNombre());
                model.addAttribute("id", idImagen);

                return "imagen_mostrarDescargaAdmin.html";
                
            } else if ((logueado.getRol().equalsIgnoreCase("CHOFER")) && (imagen.getTipo().equalsIgnoreCase("application/pdf"))) {
                
                model.addAttribute("imagenNombre", imagen.getNombre());
                model.addAttribute("id", idImagen);
                model.addAttribute("estado", flete.getEstado());
                
                return "imagen_mostrarDescargaPdf.html";
                
            } else {
            
                model.addAttribute("imagenNombre", imagen.getNombre());
                model.addAttribute("id", idImagen);
                
                return "imagen_mostrarDescargaPdfPendiente.html";
            
           } 
            
        } else {

            model.addAttribute("flete", flete);

            return "imagen_descargaCargar.html";
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

                return "imagen_mostrarDescargaPdfPendiente.html";

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

        return "imagen_descargarDescargaPdf.html";
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

    @GetMapping("/modificarDescarga/{id}") //llega id de Imagen
    public String modificarDescarga(@PathVariable Long id, ModelMap modelo) {

        modelo.put("id", id);

        return "imagen_descargaModificar.html";
    }

    @PostMapping("/modificaDescarga")
    public String modificaDescarga(@RequestParam Long id, @RequestParam("file") MultipartFile file, ModelMap modelo, HttpSession session) throws IOException {

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

            Usuario logueado = (Usuario) session.getAttribute("usuariosession");

            if (logueado.getRol().equalsIgnoreCase("CHOFER")) {

                return "redirect:/imagen/modificadoDescarga/" + id;

            } else {

                return "redirect:/imagen/modificadoDescargaAdmin/" + id;

            }

        } catch (Exception e) {
            modelo.addAttribute("id", id);
            modelo.addAttribute("error", "Ocurrió un error al procesar su imagen. Intente nuevamente o ingrese otro archivo");
            return "imagen_descargaModificar.html";
        }

    }

    @GetMapping("/modificadoDescarga/{id}")
    public String modificadoDescarga(@PathVariable Long id, ModelMap modelo) {

        Flete flete = fleteServicio.buscarFleteIdImagenDescarga(id);

        modelo.put("flete", flete);
        modelo.put("exito", "Ticket de Descarga MODIFICADO con éxito");

        return "flete_modificadoChofer.html";

    }

    @GetMapping("/modificadoDescargaAdmin/{id}")
    public String modificadoDescargaAdmin(@PathVariable Long id, ModelMap modelo) {

        Flete flete = fleteServicio.buscarFleteIdImagenDescarga(id);

        modelo.put("flete", flete);
        modelo.put("exito", "Ticket de Descarga MODIFICADO con éxito");

        return "flete_modificadoAdmin.html";
        

    }

    @GetMapping("/eliminarDescarga/{id}")
    public String eliminarDescarga(@PathVariable Long id, ModelMap model) {

        Imagen imagen = imagenServicio.obtenerImagenPorId(id);

        model.addAttribute("imagenNombre", imagen.getNombre());
        model.addAttribute("id", id);

        return "imagen_eliminarDescarga.html";
    }

    @GetMapping("/eliminaDescarga/{id}")
    public String eliminaDescarga(@PathVariable Long id, HttpSession session, ModelMap modelo) {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        imagenServicio.eliminarImagenDescarga(id);

        if (logueado.getRol().equalsIgnoreCase("CHOFER")) {

            return "redirect:/imagen/eliminadoDescarga";

        } else {

            return "redirect:/imagen/eliminadoDescargaAdmin";

        }
    }

    @GetMapping("/eliminadoDescarga")
    public String eliminadoDescarga(ModelMap modelo, HttpSession session) {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        modelo.put("chofer", logueado);
        modelo.put("exito", "Ticket de Descarga ELIMINADA con éxito");

        return "index_chofer.html";

    }

    @GetMapping("/eliminadoDescargaAdmin")
    public String eliminadoDescargaAdmin(ModelMap modelo, HttpSession session) {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        modelo.put("id", logueado.getId());
        modelo.put("exito", "Ticket de Descarga ELIMINADA con éxito");

        return "index_admin.html";

    }

    @GetMapping("/verCombustible/{id}")
    public String verCombustible(@PathVariable Long id, Model model, HttpSession session) {

        Combustible carga = combustibleServicio.buscarCombustible(id);

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        if (carga.getImagen() != null) {

            Long idImagen = carga.getImagen().getId();
            Imagen imagen = imagenServicio.obtenerImagenPorId(idImagen);

            if ((logueado.getRol().equalsIgnoreCase("CHOFER")) && (imagen.getTipo().equalsIgnoreCase("application/pdf"))) {

                model.addAttribute("imagenNombre", imagen.getNombre());
                model.addAttribute("id", idImagen);
                model.addAttribute("idUsuario", carga.getChofer().getId());

                return "imagen_mostrarCombustiblePdf.html";

            }
            if ((logueado.getRol().equalsIgnoreCase("ADMIN")) && (imagen.getTipo().equalsIgnoreCase("application/pdf"))) {

                model.addAttribute("imagenNombre", imagen.getNombre());
                model.addAttribute("id", idImagen);
                model.addAttribute("idCamion", carga.getCamion().getId());

                return "imagen_mostrarCombustiblePdfAdmin.html";

            }

            if ((logueado.getRol().equalsIgnoreCase("ADMIN")) && (!imagen.getTipo().equalsIgnoreCase("application/pdf"))) {

                model.addAttribute("imagenUrl", "/imagen/img/bytes/" + idImagen);
                model.addAttribute("imagenNombre", imagen.getNombre());
                model.addAttribute("id", idImagen);
                model.addAttribute("idCamion", carga.getCamion().getId());

                return "imagen_mostrarCombustibleAdmin.html";

            } else {

                model.addAttribute("imagenUrl", "/imagen/img/bytes/" + idImagen);
                model.addAttribute("imagenNombre", imagen.getNombre());
                model.addAttribute("id", idImagen);
                model.addAttribute("idUsuario", carga.getChofer().getId());
                model.addAttribute("estado", carga.getEstado());

                return "imagen_mostrarCombustible.html";
            }

        } else {

            model.addAttribute("carga", combustibleServicio.buscarCombustible(id));

            return "imagen_combustibleCargar.html";

        }
    }

    @GetMapping("/descargarCombustiblePdf/{id}")
    public String descargarCombustiblePdf(@PathVariable Long id, ModelMap modelo) {

        Imagen imagen = imagenServicio.obtenerImagenPorId(id);

        modelo.addAttribute("imagenUrl", "/imagen/pdf/" + id);
        modelo.addAttribute("imagenNombre", imagen.getNombre());
        modelo.addAttribute("id", id);

        return "imagen_descargarCombustiblePdf.html";
    }

    @GetMapping("/modificarCombustible/{id}") //llega id de Imagen
    public String modificarCombustible(@PathVariable Long id, ModelMap modelo) {

        modelo.put("id", id);

        return "imagen_combustibleModificar.html";
    }

    @PostMapping("/modificaCombustible")
    public String modificaCombustible(@RequestParam Long id, @RequestParam("file") MultipartFile file, ModelMap modelo) throws IOException {

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

            return "redirect:/imagen/modificadoCombustible/" + id;

        } catch (Exception e) {
            modelo.addAttribute("id", id);
            modelo.addAttribute("error", "Ocurrió un error al procesar su imagen. Intente nuevamente o ingrese otro archivo");
            return "imagen_combustibleModificar.html";
        }

    }

    @GetMapping("/modificadoCombustible/{id}")
    public String modificadoCombustible(@PathVariable Long id, ModelMap modelo) {

        Combustible carga = combustibleServicio.buscarCombustibleIdImagen(id);

        modelo.put("carga", carga);
        modelo.put("fecha", carga.getFechaCarga());
        modelo.put("exito", "Imagen de Diesel MODIFICADO con éxito");

        return "combustible_modificado.html";

    }

    @GetMapping("/eliminarCombustible/{id}")
    public String eliminarCombustible(@PathVariable Long id, ModelMap model) {

        Imagen imagen = imagenServicio.obtenerImagenPorId(id);

        model.addAttribute("imagenNombre", imagen.getNombre());
        model.addAttribute("id", id);
        model.put("carga", combustibleServicio.buscarCombustibleIdImagen(id));

        return "imagen_eliminarCombustible.html";
    }

    @GetMapping("/eliminaCombustible/{id}")
    public String eliminaCombustible(@PathVariable Long id, HttpSession session, ModelMap modelo) {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        imagenServicio.eliminarImagenCombustible(id);

        if (logueado.getRol().equalsIgnoreCase("CHOFER")) {

            return "redirect:/imagen/eliminadoCombustible";

        } else {

            return "redirect:/imagen/eliminadoCombustibleAdmin";

        }
    }

    @GetMapping("/eliminadoCombustible")
    public String eliminadoCombustible(ModelMap modelo, HttpSession session) {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        modelo.put("chofer", logueado);
        modelo.put("exito", "Imagen de Combustible ELIMINADA con éxito");

        return "index_chofer.html";

    }

    @GetMapping("/eliminadoCombustibleAdmin")
    public String eliminadoCombustibleAdmin(ModelMap modelo, HttpSession session) {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        modelo.put("id", logueado.getId());
        modelo.put("exito", "Imagen de Combustible ELIMINADA con éxito");

        return "index_admin.html";

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
