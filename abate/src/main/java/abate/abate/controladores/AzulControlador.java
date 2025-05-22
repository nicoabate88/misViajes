
package abate.abate.controladores;

import abate.abate.entidades.Azul;
import abate.abate.entidades.Combustible;
import abate.abate.servicios.AzulServicio;
import abate.abate.servicios.CamionServicio;
import abate.abate.servicios.ChoferServicio;
import abate.abate.servicios.CombustibleServicio;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
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
@RequestMapping("/azul")
@PreAuthorize("hasAnyRole('ROLE_ADMIN')")
public class AzulControlador {
    
    @Autowired
    private CamionServicio camionServicio;
    @Autowired
    private ChoferServicio choferServicio;
    @Autowired
    private AzulServicio azulServicio;
    @Autowired
    private CombustibleServicio combustibleServicio;
    
    @GetMapping("/listarAdmin/{id}")
    public String listarAdmin(@PathVariable Long id, ModelMap modelo) throws ParseException {

        String desde = obtenerFechaDesde();
        String hasta = obtenerFechaHasta();
        List<Azul> lista = azulServicio.buscarCargasIdCamion(id, desde, hasta);
        
        Double litros = 0.0;

        for (Azul a : lista) {
            litros = litros + a.getLitro();
        }

        modelo.put("camion", camionServicio.buscarCamion(id));
        modelo.put("desde", desde);
        modelo.put("hasta", hasta);
        modelo.put("litros", litros);
        modelo.put("cantidad", lista.size());
        modelo.addAttribute("cargas", lista);

        return "azul_listarCargasAdmin.html";
    }

    @PostMapping("/listarAdminFiltro")
    public String listarAdminFiltro(@RequestParam Long id, @RequestParam String desde, @RequestParam String hasta, ModelMap modelo) throws ParseException {
        
        List<Azul> lista = azulServicio.buscarCargasIdCamion(id, desde, hasta);
        
        Double litros = 0.0;

        for (Azul a : lista) {
            litros = litros + a.getLitro();
        }

        modelo.put("camion", camionServicio.buscarCamion(id));
        modelo.put("desde", desde);
        modelo.put("hasta", hasta);
        modelo.put("litros", litros);
        modelo.put("cantidad", lista.size());
        modelo.addAttribute("cargas", lista);

        return "azul_listarCargasAdmin.html";
    }
    
    @GetMapping("/modificarAdmin/{id}")
    public String modificarAdmin(@PathVariable Long id, ModelMap modelo) {
            
        Combustible carga = combustibleServicio.buscarCombustibleIdAzul(id);
        
            Combustible cargaAnterior = combustibleServicio.cargaAnteriorPorId(carga.getId(), carga.getCamion().getId());

            modelo.put("fechaAnterior", cargaAnterior.getFechaCarga());
            modelo.put("carga", carga);
            modelo.addAttribute("choferes", choferServicio.bucarChoferesNombreAsc(carga.getIdOrg()));
            modelo.put("flag", combustibleServicio.ultimaCarga(carga.getCamion(), carga.getId()));

            return "combustible_modificarDesdeAzulAdmin.html";

    }
    
    public String obtenerFechaDesde() {

        LocalDate now = LocalDate.now();

        LocalDate firstDayOfMonth = now.withDayOfMonth(1);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        String formattedDate = firstDayOfMonth.format(formatter);

        return formattedDate;

    }

    public String obtenerFechaHasta() {

        LocalDate now = LocalDate.now();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        String formattedToday = now.format(formatter);

        return formattedToday;

    }

    
}
