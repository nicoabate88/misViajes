package abate.abate.servicios;

import abate.abate.entidades.Acoplado;
import abate.abate.entidades.Camion;
import abate.abate.entidades.CamionEstadistica;
import abate.abate.entidades.CamionesEstadistica;
import abate.abate.entidades.Combustible;
import abate.abate.entidades.Eje;
import abate.abate.entidades.Flete;
import abate.abate.entidades.Gasto;
import abate.abate.entidades.Usuario;
import abate.abate.excepciones.MiException;
import abate.abate.repositorios.AcopladoRepositorio;
import abate.abate.repositorios.CamionRepositorio;
import abate.abate.repositorios.CombustibleRepositorio;
import abate.abate.repositorios.EjeRepositorio;
import abate.abate.repositorios.FleteRepositorio;
import abate.abate.repositorios.GastoRepositorio;
import abate.abate.repositorios.PosicionNeumaticoRepositorio;
import abate.abate.repositorios.UsuarioRepositorio;
import abate.abate.util.CamionComparador;
import java.util.ArrayList;
import java.util.Collections;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CamionServicio {

    @Autowired
    private CamionRepositorio camionRepositorio;
    @Autowired
    private AcopladoRepositorio acopladoRepositorio;
    @Autowired
    private CombustibleRepositorio combustibleRepositorio;
    @Autowired
    private FleteRepositorio fleteRepositorio;
    @Autowired
    private UsuarioRepositorio usuarioRepositorio;
    @Autowired
    private GastoRepositorio gastoRepositorio;
    @Autowired
    private PosicionNeumaticoRepositorio posicionNeumaticoRepositorio;
    @Autowired
    private EjeRepositorio ejeRepositorio;

    @Transactional
    public void crearCamion(Camion camion, Usuario logueado) throws MiException {
        
        validarDatos(logueado.getIdOrg(), camion.getDominio());
        
        // Asociar cada eje con el Camion antes de persistir
        if (camion.getEjes() != null) {
            for (Eje eje : camion.getEjes()) {
                eje.setCamion(camion);
                eje.setEstado("HABILITADO");
                eje.setUsuario(logueado);
            }
        }
        
        camion.setMarca(camion.getMarca().toUpperCase());
        camion.setModelo(camion.getModelo().toUpperCase());
        camion.setDominio(camion.getDominio().toUpperCase());
        camion.setIdOrg(logueado.getIdOrg());
        camion.setAzul(camion.getAzul());
        camion.setEstado(camion.getEstado());
        camion.setUsuario(logueado);
        
        camionRepositorio.save(camion);
    
    }
    
    @Transactional
    public void modificarCamion(Camion camionModificado, Long idAcoplado, Usuario usuario) throws MiException {
    
    Camion camionOriginal = camionRepositorio.getById(camionModificado.getId());

    String dominioMay = camionModificado.getDominio().toUpperCase();
    
    if(camionModificado.getEstado().equalsIgnoreCase("HABILITADO")){
        
        validarDatosModificar(camionOriginal, dominioMay);
    
    } else {
        
         validarDatosModificarInhabilitado(camionOriginal, dominioMay);
    }
    
    String marcaMay = camionModificado.getMarca().toUpperCase();
    String modeloMay = camionModificado.getModelo().toUpperCase();
    
    if (idAcoplado != 0) {
            Acoplado acoplado = new Acoplado();
            Optional<Acoplado> acop = acopladoRepositorio.findById(idAcoplado);
            if (acop.isPresent()) {
                acoplado = acop.get();
            }
            camionOriginal.setAcoplado(acoplado);
        } else {
            camionOriginal.setAcoplado(null);
        }
    
    camionOriginal.setDominio(dominioMay);
    camionOriginal.setMarca(marcaMay);
    camionOriginal.setModelo(modeloMay);
    camionOriginal.setEstado(camionModificado.getEstado());
    camionOriginal.setAzul(camionModificado.getAzul());
    camionOriginal.setCantidadAuxilio(camionModificado.getCantidadAuxilio());
    camionOriginal.setUsuario(usuario);

    // Mapear ejes enviados desde el formulario
    List<Eje> nuevosEjes = camionModificado.getEjes();

    // Mapear ejes existentes en la base de datos
    List<Eje> ejesOriginales = camionOriginal.getEjes();

    // Detectar ejes eliminados
    List<Eje> ejesAEliminar = new ArrayList<>();
    for (Eje original : ejesOriginales) {
        boolean sigueExistiendo = nuevosEjes.stream()
            .anyMatch(e -> e.getId() != null && e.getId().equals(original.getId()));
        if (!sigueExistiendo) {
            boolean tienePosiciones = posicionNeumaticoRepositorio.existsByEje(original);
            if (tienePosiciones) {
                throw new MiException("No se puede eliminar el Eje N° " + original.getNumero() + ", tiene historial de neumáticos asociado.");
            }
            ejesAEliminar.add(original);
        }
    }

    // Eliminar los ejes permitidos
    ejesAEliminar.forEach(e -> {
        camionOriginal.getEjes().remove(e);
        ejeRepositorio.delete(e);
    });

    // Procesar ejes nuevos o actualizados
    for (Eje eje : nuevosEjes) {
        eje.setCamion(camionOriginal);
        if (eje.getId() == null) {
            // Nuevo eje
            camionOriginal.getEjes().add(eje);
        } else {
            // Actualizar eje existente
            for (Eje existente : camionOriginal.getEjes()) {
                if (existente.getId().equals(eje.getId())) {
                    existente.setNombre(eje.getNombre());
                    existente.setElevable(eje.getElevable()); 
                    existente.setPorcentaje(eje.getPorcentaje());
                    existente.setCantidadNeumatico(eje.getCantidadNeumatico());
                    break;
                }
            }
        }
    }

    // Renumerar todos los ejes del camión secuencialmente
    int contador = 1;
    for (Eje eje : camionOriginal.getEjes()) {
        eje.setUsuario(usuario);
        eje.setNumero(contador++);
    }

    camionRepositorio.save(camionOriginal);
    
   }
    
    @Transactional
    public void eliminarCamion(Long id) throws MiException {
        
        Camion camion = camionRepositorio.getById(id);
        
        Combustible combustible = combustibleRepositorio.findTopByCamionOrderByIdDesc(camion);
        Flete flete = fleteRepositorio.findTopByCamionOrderByIdDesc(camion);
        Gasto gasto = gastoRepositorio.findTopByCamionOrderByIdDesc(camion);
        Usuario usuario = usuarioRepositorio.findTopByCamionOrderByIdDesc(camion);

        if (combustible == null && gasto == null && flete == null && usuario == null) {
            
            camion.setUsuario(null);
            
            camionRepositorio.save(camion);

            camionRepositorio.deleteById(id);

        } else {

            throw new MiException("El Camión no puede ser eliminado, tiene Viaje / Gasto y/o Combustible asociado.");
        }

    }
    
    public ArrayList<Camion> buscarCamionesAsc(Long idOrg) {

        ArrayList<Camion> lista = camionRepositorio.buscarCamiones(idOrg);

        Collections.sort(lista, CamionComparador.ordenarDominioAsc);

        return lista;

    }
    
    public ArrayList<Camion> buscarCamionesHabAsc(Long idOrg) {

        ArrayList<Camion> lista = camionRepositorio.buscarCamionesHab(idOrg);

        Collections.sort(lista, CamionComparador.ordenarDominioAsc);

        return lista;

    }

    public Camion buscarCamion(Long id) {

        return camionRepositorio.getById(id);
    }

    public Long buscarUltimo(Long idOrg) {

        return camionRepositorio.ultimoCamion(idOrg);

    }

    public ArrayList<CamionEstadistica> estadisticaCamion(String desde, String hasta, Long idCamion) throws ParseException {

        Date d = convertirFecha(desde);
        Date h = convertirFecha(hasta);

        ArrayList<Flete> fletes = fleteRepositorio.buscarFleteCamion(d, h, idCamion);
        ArrayList<Combustible> cargas = combustibleRepositorio.buscarCombustibleIdCamion(d, h, idCamion);
        ArrayList<Gasto> gastos = gastoRepositorio.findByFechaBetweenAndCamionId(d, h, idCamion);

        // Paso 1: Procesar los fletes
        Map<String, CamionEstadistica> resumenMap = new HashMap<>();
        
        int totalFletes = 0;
    int totalKm = 0;
    double totalLitros = 0;
    int totalGastos = 0;
    int totalNeto = 0;

        for (Flete flete : fletes) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(flete.getFechaFlete());
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH) + 1;

            String key = year + "-" + month;

            if (resumenMap.containsKey(key)) {
                CamionEstadistica resumen = resumenMap.get(key);
                resumen.setFlete(resumen.getFlete() + 1);
                resumen.setNeto(resumen.getNeto() + flete.getNeto());
            } else {
                CamionEstadistica nuevoResumen = new CamionEstadistica(year, month, 1, flete.getNeto());
                resumenMap.put(key, nuevoResumen);
            }
            
            totalFletes++;
            totalNeto += flete.getNeto();
        }

        // Paso 2: Procesar los combustibles y actualizar ResumenFletesMensual
        for (Combustible combustible : cargas) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(combustible.getFechaCarga());
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH) + 1;

            String key = year + "-" + month;

            if (resumenMap.containsKey(key)) {
                CamionEstadistica resumen = resumenMap.get(key);
                resumen.setKmRecorrido(resumen.getKmRecorrido() + combustible.getKmRecorrido());
                resumen.setLitro(resumen.getLitro() + combustible.getLitro());
            } else {
                // Si no existe un resumen para ese mes y año, lo creamos (aunque esto no debería ocurrir si los fletes y combustibles coinciden en fechas)
                CamionEstadistica nuevoResumen = new CamionEstadistica(year, month, 0, 0.0);
                nuevoResumen.setKmRecorrido(combustible.getKmRecorrido());
                nuevoResumen.setLitro(combustible.getLitro());
                resumenMap.put(key, nuevoResumen);
            }
                    totalKm += combustible.getKmRecorrido();
        totalLitros += combustible.getLitro();
        }
        // Paso 3: Procesar los gastos y actualizar ResumenFletesMensual
        for (Gasto gasto : gastos) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(gasto.getFecha());
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH) + 1;

            String key = year + "-" + month;

            if (resumenMap.containsKey(key)) {
                CamionEstadistica resumen = resumenMap.get(key);
                resumen.setGasto(resumen.getGasto() + gasto.getImporte());
            } else {
                // Si no existe un resumen para ese mes y año, lo creamos (aunque esto no debería ocurrir si los fletes y combustibles coinciden en fechas)
                CamionEstadistica nuevoResumen = new CamionEstadistica(year, month, 0.0);
                nuevoResumen.setGasto(gasto.getImporte());
                resumenMap.put(key, nuevoResumen);
            }
            totalGastos += gasto.getImporte();
        }

            ArrayList<CamionEstadistica> resultado = new ArrayList<>(resumenMap.values());

    // Agregar fila de totales generales
    CamionEstadistica totalGeneral = new CamionEstadistica(0, 0, totalFletes);
    totalGeneral.setFlete(totalFletes);
    totalGeneral.setKmRecorrido(totalKm);
    totalGeneral.setLitro(totalLitros);
    totalGeneral.setGasto(totalGastos);
    totalGeneral.setNeto(totalNeto);
    if(totalKm > 0){
    totalGeneral.setRentabilidad(totalNeto / totalKm);
    }
    
    resultado.add(totalGeneral);

    return resultado;

    }

    public Map<Camion, CamionesEstadistica> estadisticaCamiones(String desde, String hasta, Long idOrg) throws ParseException {

        Date d = convertirFecha(desde);
        Date h = convertirFecha(hasta);

        List<Camion> todasLosCamiones = camionRepositorio.buscarCamionesHab(idOrg);
        List<Flete> fletes = fleteRepositorio.findByFechaFleteBetweenAndIdOrg(d, h, idOrg);
        List<Combustible> cargas = combustibleRepositorio.findByFechaCargaBetweenAndIdOrg(d, h, idOrg);
        List<Gasto> gastos = gastoRepositorio.findByFechaBetweenAndIdOrg(d, h, idOrg);

        // Mapa para almacenar estadísticas por camión
        Map<Camion, CamionesEstadistica> estadisticasPorCamion = new HashMap<>();
        CamionesEstadistica totalGeneral = new CamionesEstadistica();
        
        for (Camion camion : todasLosCamiones) {
            CamionesEstadistica estadistica = new CamionesEstadistica();
            estadistica.setFlete(0);
            estadistica.setKmRecorrido(0);
            estadistica.setLitro(0.0);
            estadistica.setConsumo(0.0);
            estadistica.setAzul(0);
            estadistica.setLubricante(0.0);
            estadistica.setGasto(0);
            estadistica.setCamion(camion);
            estadisticasPorCamion.put(camion, estadistica);
        }
        
        // Procesar los fletes
        for (Flete flete : fletes) {
            Camion camion = flete.getCamion();
            estadisticasPorCamion.putIfAbsent(camion, new CamionesEstadistica());
            CamionesEstadistica resumen = estadisticasPorCamion.get(camion);
            resumen.setFlete(resumen.getFlete() + 1);
            resumen.setNeto(resumen.getNeto() + flete.getNeto());
            totalGeneral.setFlete(totalGeneral.getFlete() + 1);
            totalGeneral.setNeto(totalGeneral.getNeto() + flete.getNeto());
        }

        // Procesar los combustibles
        for (Combustible combustible : cargas) {
            Camion camion = combustible.getCamion();
            estadisticasPorCamion.putIfAbsent(camion, new CamionesEstadistica());
            CamionesEstadistica resumen = estadisticasPorCamion.get(camion);
            resumen.setKmRecorrido(resumen.getKmRecorrido() + combustible.getKmRecorrido());
            resumen.setLitro(resumen.getLitro() + combustible.getLitro());
            totalGeneral.setKmRecorrido(totalGeneral.getKmRecorrido() + combustible.getKmRecorrido());
            totalGeneral.setLitro(totalGeneral.getLitro() + combustible.getLitro());

        }

        // Procesar los gastos
        for (Gasto gasto : gastos) {
            Camion camion = gasto.getCamion();
            estadisticasPorCamion.putIfAbsent(camion, new CamionesEstadistica());
            CamionesEstadistica resumen = estadisticasPorCamion.get(camion);
            resumen.setGasto(resumen.getGasto() + gasto.getImporte());
            totalGeneral.setGasto(totalGeneral.getGasto() + gasto.getImporte());
        }
        
        if(totalGeneral.getKmRecorrido() > 0){
        totalGeneral.setRentabilidad(totalGeneral.getNeto() / totalGeneral.getKmRecorrido());
        }
        
        Camion totalKey = new Camion();
        totalKey.setDominio("TOTAL");
        estadisticasPorCamion.put(totalKey, totalGeneral);
        // Ordenar el mapa por el dominio del camión
        return estadisticasPorCamion.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByKey(Comparator.comparing(Camion::getDominio)))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));
    }

    public void validarDatos(Long idOrg, String dominio) throws MiException {

        ArrayList<Camion> lista = camionRepositorio.buscarCamiones(idOrg);

        for (Camion c : lista) {
            if (c.getDominio().equalsIgnoreCase(dominio)) {
                throw new MiException("El DOMINIO '"+dominio+"' ya está registrado.");
            }
        }
    }

    public void validarDatosModificar(Camion camion, String dominio) throws MiException {

        ArrayList<Camion> lista = camionRepositorio.buscarCamiones(camion.getIdOrg());

        if (!camion.getDominio().equalsIgnoreCase(dominio)) {
            for (Camion c : lista) {
                if (c.getDominio().equalsIgnoreCase(dominio)) {
                    throw new MiException("El DOMINIO '"+dominio+"' ya está registrado.");
                }
            }
        }
    }
    
    public void validarDatosModificarInhabilitado(Camion camion, String dominio) throws MiException {
        
        List<Usuario> usuarios = usuarioRepositorio.findByCamion_Id(camion.getId());

       if (!usuarios.isEmpty()) {
           for(Usuario u : usuarios){
               
               throw new MiException("No puede INHABILITAR este camión porque está asociado al chofer '"+u.getNombre()+"'. Modifique la configuración del chofer y vuelva a ejecutar esta operación.");
               
           }
        } 

        ArrayList<Camion> lista = camionRepositorio.buscarCamiones(camion.getIdOrg());

        if (!camion.getDominio().equalsIgnoreCase(dominio)) {
            for (Camion c : lista) {
                if (c.getDominio().equalsIgnoreCase(dominio)) {
                    throw new MiException("El DOMINIO '"+dominio+"' ya está registrado.");
                }
            }
        }
    }

    public Date convertirFecha(String fecha) throws ParseException { //convierte fecha String a fecha Date
        SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd");
        return formato.parse(fecha);
    }

}
