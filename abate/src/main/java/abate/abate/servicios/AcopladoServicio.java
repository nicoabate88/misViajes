
package abate.abate.servicios;

import abate.abate.entidades.Acoplado;
import abate.abate.entidades.AcopladoEstadistica;
import abate.abate.entidades.AcopladosEstadistica;
import abate.abate.entidades.Combustible;
import abate.abate.entidades.Eje;
import abate.abate.entidades.Flete;
import abate.abate.entidades.Usuario;
import abate.abate.excepciones.MiException;
import abate.abate.repositorios.AcopladoRepositorio;
import abate.abate.repositorios.CombustibleRepositorio;
import abate.abate.repositorios.EjeRepositorio;
import abate.abate.repositorios.FleteRepositorio;
import abate.abate.repositorios.PosicionNeumaticoRepositorio;
import abate.abate.repositorios.UsuarioRepositorio;
import abate.abate.util.AcopladoComparador;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AcopladoServicio {
    
    @Autowired
    private AcopladoRepositorio acopladoRepositorio;
    @Autowired
    private FleteRepositorio fleteRepositorio;
    @Autowired
    private CombustibleRepositorio combustibleRepositorio;
    @Autowired
    private UsuarioRepositorio usuarioRepositorio;
    @Autowired
    private PosicionNeumaticoRepositorio posicionNeumaticoRepositorio;
     @Autowired
    private EjeRepositorio ejeRepositorio;
    
    @Transactional
    public void crearAcoplado(Acoplado acoplado, Usuario logueado) throws MiException {
        
        validarDatos(logueado.getIdOrg(), acoplado.getDominio());
        
        // Asociar cada eje con el Camion antes de persistir
        if (acoplado.getEjes() != null) {
            for (Eje eje : acoplado.getEjes()) {
                eje.setAcoplado(acoplado);
                eje.setEstado("HABILITADO");
                eje.setUsuario(logueado);
            }
        }
        
        acoplado.setMarca(acoplado.getMarca().toUpperCase());
        acoplado.setModelo(acoplado.getModelo().toUpperCase());
        acoplado.setDominio(acoplado.getDominio().toUpperCase());
        acoplado.setEstado(acoplado.getEstado());
        acoplado.setCantidadAuxilio(acoplado.getCantidadAuxilio());
        acoplado.setUsuario(logueado);
        acoplado.setIdOrg(logueado.getIdOrg());
        
        acopladoRepositorio.save(acoplado);
    
    }   
    
    @Transactional
    public void modificarAcoplado(Acoplado acopladoModificado, Usuario usuario) throws MiException {
    
    Acoplado acopladoOriginal = acopladoRepositorio.getById(acopladoModificado.getId());

    String dominioMay = acopladoModificado.getDominio().toUpperCase();
    
    validarDatosModificar(acopladoOriginal, dominioMay);
    
    String marcaMay = acopladoModificado.getMarca().toUpperCase();
    String modeloMay = acopladoModificado.getModelo().toUpperCase();
    
    acopladoOriginal.setDominio(dominioMay);
    acopladoOriginal.setMarca(marcaMay);
    acopladoOriginal.setModelo(modeloMay);
    acopladoOriginal.setEstado(acopladoModificado.getEstado());
    acopladoOriginal.setCantidadAuxilio(acopladoModificado.getCantidadAuxilio());
    acopladoOriginal.setUsuario(usuario);

    // Mapear ejes enviados desde el formulario
    List<Eje> nuevosEjes = acopladoModificado.getEjes();

    // Mapear ejes existentes en la base de datos
    List<Eje> ejesOriginales = acopladoOriginal.getEjes();

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
        acopladoOriginal.getEjes().remove(e);
        ejeRepositorio.delete(e);
    });

    // Procesar ejes nuevos o actualizados
    for (Eje eje : nuevosEjes) {
        eje.setAcoplado(acopladoOriginal);
        if (eje.getId() == null) {
            // Nuevo eje
            acopladoOriginal.getEjes().add(eje);
        } else {
            // Actualizar eje existente
            for (Eje existente : acopladoOriginal.getEjes()) {
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
    for (Eje eje : acopladoOriginal.getEjes()) {
        eje.setUsuario(usuario);
        eje.setNumero(contador++);
    }

    // Guardar cambios
       acopladoRepositorio.save(acopladoOriginal);
   }

    @Transactional
    public void eliminarAcoplado(Long id) throws MiException {

        Acoplado acoplado = acopladoRepositorio.getById(id);

        Combustible combustible = combustibleRepositorio.findTopByAcopladoOrderByIdDesc(acoplado);
        Flete flete = fleteRepositorio.findTopByAcopladoOrderByIdDesc(acoplado);
        Usuario usuario = usuarioRepositorio.findTopByAcopladoOrderByIdDesc(acoplado);

        if (combustible == null && flete == null && usuario == null) {
            
            acoplado.setUsuario(null);
            
            acopladoRepositorio.save(acoplado);

            acopladoRepositorio.deleteById(id);

        } else {

            throw new MiException("El Acoplado no puede ser eliminado, tiene Viaje, Combustible y/o Chofer asociado.");
        }

    }
    
    public List<Acoplado> buscarAcopladosAsc(Long idOrg) {

        List<Acoplado> lista = acopladoRepositorio.buscarAcoplados(idOrg);

        Collections.sort(lista, AcopladoComparador.ordenarDominioAsc);

        return lista;

    }

    public Acoplado buscarAcoplado(Long id) {

        return acopladoRepositorio.getById(id);
    }
    
    public Acoplado buscarUltimoAcoplado(Long idOrg) {

        return acopladoRepositorio.ultimoAcoplado(idOrg);
    }

    public Long buscarUltimo(Long idOrg) {

        return acopladoRepositorio.ultimoIdAcoplado(idOrg);

    }
    
   public void validarDatos(Long idOrg, String dominio) throws MiException {

        List<Acoplado> lista = acopladoRepositorio.buscarAcoplados(idOrg);

        for (Acoplado a : lista) {
            if (a.getDominio().equalsIgnoreCase(dominio)) {
                 throw new MiException("El DOMINIO '"+dominio+"' ya está registrado.");
            }
        }
    }
   
    public void validarDatosModificar(Acoplado acoplado, String dominio) throws MiException {

        List<Acoplado> lista = acopladoRepositorio.buscarAcoplados(acoplado.getIdOrg());

        if (!acoplado.getDominio().equalsIgnoreCase(dominio)) {
            for (Acoplado a : lista) {
                if (a.getDominio().equalsIgnoreCase(dominio)) {
                     throw new MiException("El DOMINIO '"+dominio+"' ya está registrado.");
                }
            }
        }
    }
   
    public Map<Acoplado, AcopladosEstadistica> estadisticaAcoplados(String desde, String hasta, Long idOrg) throws ParseException {

        Date d = convertirFecha(desde);
        Date h = convertirFecha(hasta);

        List<Acoplado> todasLosAcoplados = acopladoRepositorio.buscarAcoplados(idOrg);
        List<Flete> fletes = fleteRepositorio.findByFechaFleteBetweenAndIdOrg(d, h, idOrg);
        List<Combustible> cargas = combustibleRepositorio.findByFechaCargaBetweenAndIdOrg(d, h, idOrg);

        // Mapa para almacenar estadísticas por camión
        Map<Acoplado, AcopladosEstadistica> estadisticasPorAcoplado = new HashMap<>();
        AcopladosEstadistica totalGeneral = new AcopladosEstadistica();
        
        for (Acoplado acoplado : todasLosAcoplados) {
            AcopladosEstadistica estadistica = new AcopladosEstadistica();
            estadistica.setFlete(0);
            estadistica.setKmRecorrido(0);
            estadistica.setAcoplado(acoplado);
            estadisticasPorAcoplado.put(acoplado, estadistica);
        }
        
        // Procesar los fletes
        for (Flete flete : fletes) {
            if(flete.getAcoplado() != null){
            Acoplado acoplado = flete.getAcoplado();
            estadisticasPorAcoplado.putIfAbsent(acoplado, new AcopladosEstadistica());
            AcopladosEstadistica resumen = estadisticasPorAcoplado.get(acoplado);
            resumen.setFlete(resumen.getFlete() + 1);
            totalGeneral.setFlete(totalGeneral.getFlete() + 1);
            }
        }

        // Procesar los combustibles
        for (Combustible combustible : cargas) {
            if(combustible.getAcoplado() != null){
            Acoplado acoplado = combustible.getAcoplado();
            estadisticasPorAcoplado.putIfAbsent(acoplado, new AcopladosEstadistica());
            AcopladosEstadistica resumen = estadisticasPorAcoplado.get(acoplado);
            resumen.setKmRecorrido(resumen.getKmRecorrido() + combustible.getKmRecorrido());
            totalGeneral.setKmRecorrido(totalGeneral.getKmRecorrido() + combustible.getKmRecorrido());
            }
            
        }
        
        Acoplado totalKey = new Acoplado();
        totalKey.setDominio("TOTAL");
        estadisticasPorAcoplado.put(totalKey, totalGeneral);
        // Ordenar el mapa por el dominio del camión
        return estadisticasPorAcoplado.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByKey(Comparator.comparing(Acoplado::getDominio)))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));
    }
    
    public ArrayList<AcopladoEstadistica> estadisticaAcoplado(String desde, String hasta, Long idAcoplado) throws ParseException {

        Date d = convertirFecha(desde);
        Date h = convertirFecha(hasta);

        ArrayList<Flete> fletes = fleteRepositorio.buscarFleteAcoplado(d, h, idAcoplado);
        ArrayList<Combustible> cargas = combustibleRepositorio.buscarCombustibleIdAcoplado(d, h, idAcoplado);

        // Paso 1: Procesar los fletes
        Map<String, AcopladoEstadistica> resumenMap = new HashMap<>();
        
        int totalFletes = 0;
        int totalKm = 0;

        for (Flete flete : fletes) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(flete.getFechaFlete());
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH) + 1;

            String key = year + "-" + month;

            if (resumenMap.containsKey(key)) {
                AcopladoEstadistica resumen = resumenMap.get(key);
                resumen.setFlete(resumen.getFlete() + 1);
            } else {
                AcopladoEstadistica nuevoResumen = new AcopladoEstadistica(year, month, 1);
                resumenMap.put(key, nuevoResumen);
            }
            
            totalFletes++;
        }

        // Paso 2: Procesar los combustibles y actualizar ResumenFletesMensual
        for (Combustible combustible : cargas) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(combustible.getFechaCarga());
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH) + 1;

            String key = year + "-" + month;

            if (resumenMap.containsKey(key)) {
                AcopladoEstadistica resumen = resumenMap.get(key);
                resumen.setKmRecorrido(resumen.getKmRecorrido() + combustible.getKmRecorrido());
            } else {
                // Si no existe un resumen para ese mes y año, lo creamos (aunque esto no debería ocurrir si los fletes y combustibles coinciden en fechas)
                AcopladoEstadistica nuevoResumen = new AcopladoEstadistica(year, month, 0.0);
                nuevoResumen.setKmRecorrido(combustible.getKmRecorrido());
                resumenMap.put(key, nuevoResumen);
            }
            totalKm += combustible.getKmRecorrido();
        }

        ArrayList<AcopladoEstadistica> resultado = new ArrayList<>(resumenMap.values());

    // Agregar fila de totales generales
    AcopladoEstadistica totalGeneral = new AcopladoEstadistica(0, 0, totalFletes);
    totalGeneral.setFlete(totalFletes);
    totalGeneral.setKmRecorrido(totalKm);
    
    resultado.add(totalGeneral);

    return resultado;

    }
        
        public Date convertirFecha(String fecha) throws ParseException { //convierte fecha String a fecha Date
        SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd");
        return formato.parse(fecha);
    }
    
}
