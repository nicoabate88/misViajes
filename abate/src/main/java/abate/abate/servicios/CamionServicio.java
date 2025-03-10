package abate.abate.servicios;

import abate.abate.entidades.Camion;
import abate.abate.entidades.CamionEstadistica;
import abate.abate.entidades.CamionesEstadistica;
import abate.abate.entidades.Combustible;
import abate.abate.entidades.Flete;
import abate.abate.entidades.Gasto;
import abate.abate.entidades.Usuario;
import abate.abate.excepciones.MiException;
import abate.abate.repositorios.CamionRepositorio;
import abate.abate.repositorios.CombustibleRepositorio;
import abate.abate.repositorios.FleteRepositorio;
import abate.abate.repositorios.GastoRepositorio;
import abate.abate.repositorios.UsuarioRepositorio;
import abate.abate.util.CamionComparador;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;
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
import java.util.stream.Collectors;

@Service
public class CamionServicio {

    @Autowired
    private CamionRepositorio camionRepositorio;
    @Autowired
    private CombustibleRepositorio combustibleRepositorio;
    @Autowired
    private FleteRepositorio fleteRepositorio;
    @Autowired
    private UsuarioRepositorio usuarioRepositorio;
    @Autowired
    private GastoRepositorio gastoRepositorio;

    @Transactional
    public void crearCamion(Long idOrg, String marca, String modelo, String dominio, String azul) throws MiException {

        validarDatos(idOrg, dominio);

        Camion camion = new Camion();

        String marcaMayusculas = marca.toUpperCase();
        String modeloMayusculas = modelo.toUpperCase();
        String dominioMayusculas = dominio.toUpperCase();

        camion.setIdOrg(idOrg);
        camion.setMarca(marcaMayusculas);
        camion.setModelo(modeloMayusculas);
        camion.setDominio(dominioMayusculas);
        camion.setAzul(azul);

        camionRepositorio.save(camion);

    }

    @Transactional
    public void modificarCamion(Long id, String marca, String modelo, String dominio, String azul) throws MiException {

        Camion camion = new Camion();
        Optional<Camion> cam = camionRepositorio.findById(id);
        if (cam.isPresent()) {
            camion = cam.get();
        }
        validarDatosModificar(camion, dominio);

        String marcaMayusculas = marca.toUpperCase();
        String modeloMayusculas = modelo.toUpperCase();
        String dominioMayusculas = dominio.toUpperCase();

        camion.setMarca(marcaMayusculas);
        camion.setModelo(modeloMayusculas);
        camion.setDominio(dominioMayusculas);
        camion.setAzul(azul);

        camionRepositorio.save(camion);

    }

    @Transactional
    public void eliminarCamion(Long id) throws MiException {

        Camion camion = camionRepositorio.getById(id);

        Combustible combustible = combustibleRepositorio.findTopByCamionOrderByIdDesc(camion);
        Flete flete = fleteRepositorio.findTopByCamionOrderByIdDesc(camion);
        Gasto gasto = gastoRepositorio.findTopByCamionOrderByIdDesc(camion);
        Usuario usuario = usuarioRepositorio.findTopByCamionOrderByIdDesc(camion);

        if (combustible == null && gasto == null && flete == null && usuario == null) {

            camionRepositorio.deleteById(id);

        } else {

            throw new MiException("El Camión no puede ser eliminado, tiene Viaje / Gasto / Combustible y/o Chofer asociado.");
        }

    }

    public ArrayList<Camion> buscarCamionesAsc(Long idOrg) {

        ArrayList<Camion> lista = camionRepositorio.buscarCamiones(idOrg);

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

        Camion camion = camionRepositorio.getById(idCamion);
        Date d = convertirFecha(desde);
        Date h = convertirFecha(hasta);

        ArrayList<Flete> fletes = fleteRepositorio.findByFechaFleteBetweenAndCamion(d, h, camion);
        ArrayList<Combustible> cargas = combustibleRepositorio.findByFechaCargaBetweenAndCamion(d, h, camion);
        ArrayList<Gasto> gastos = gastoRepositorio.findByFechaBetweenAndCamionId(d, h, camion.getId());

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

        List<Flete> fletes = fleteRepositorio.findByFechaFleteBetweenAndIdOrg(d, h, idOrg);
        List<Combustible> cargas = combustibleRepositorio.findByFechaCargaBetweenAndIdOrg(d, h, idOrg);
        List<Gasto> gastos = gastoRepositorio.findByFechaBetweenAndIdOrg(d, h, idOrg);

        // Mapa para almacenar estadísticas por camión
        Map<Camion, CamionesEstadistica> estadisticasPorCamion = new HashMap<>();
        CamionesEstadistica totalGeneral = new CamionesEstadistica();
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
                throw new MiException("El DOMINIO de Camión ya está registrado.");
            }
        }
    }

    public void validarDatosModificar(Camion camion, String dominio) throws MiException {

        ArrayList<Camion> lista = camionRepositorio.buscarCamiones(camion.getIdOrg());

        if (!camion.getDominio().equalsIgnoreCase(dominio)) {
            for (Camion c : lista) {
                if (c.getDominio().equalsIgnoreCase(dominio)) {
                    throw new MiException("El DOMINIO de Camión ya está registrado.");
                }
            }
        }
    }

    public Date convertirFecha(String fecha) throws ParseException { //convierte fecha String a fecha Date
        SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd");
        return formato.parse(fecha);
    }

}
