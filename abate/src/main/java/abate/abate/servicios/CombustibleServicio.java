package abate.abate.servicios;

import abate.abate.entidades.Acoplado;
import abate.abate.entidades.Azul;
import abate.abate.entidades.Camion;
import abate.abate.entidades.Combustible;
import abate.abate.entidades.Usuario;
import abate.abate.repositorios.AcopladoRepositorio;
import abate.abate.repositorios.AzulRepositorio;
import abate.abate.repositorios.CamionRepositorio;
import abate.abate.repositorios.CombustibleRepositorio;
import abate.abate.repositorios.UsuarioRepositorio;
import abate.abate.util.CombustibleComparador;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Optional;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CombustibleServicio {

    @Autowired
    private UsuarioRepositorio usuarioRepositorio;
    @Autowired
    private CombustibleRepositorio combustibleRepositorio;
    @Autowired
    private CamionRepositorio camionRepositorio;
    @Autowired
    private AcopladoRepositorio acopladoRepositorio;
    @Autowired
    private AzulServicio azulServicio;
    @Autowired
    private AzulRepositorio azulRepositorio;

    @Transactional
    public void crearPrimerCarga(Long idOrg, String fecha, Double km, Long idCamion, Usuario usuario) throws ParseException {

        Camion camion = new Camion();
        Optional<Camion> cam = camionRepositorio.findById(idCamion);
        if (cam.isPresent()) {
            camion = cam.get();
        }

        Date f = convertirFecha(fecha);

        Combustible carga = new Combustible();

        carga.setIdOrg(idOrg);
        carga.setFechaCarga(f);
        carga.setKmCarga(km);
        carga.setKmRecorrido(0.0);
        carga.setLitro(0.0);
        carga.setCompleto("SI");
        carga.setEstado("ACEPTADO");
        carga.setConsumoPromedio(0.0);
        carga.setCamion(camion);
        carga.setUsuario(usuario);
        carga.setChofer(usuario);

        combustibleRepositorio.save(carga);

    }

    @Transactional
    public void crearCarga(Long idOrg, Long idCamion, Long idAcoplado, String fecha, Double kmAnterior, Double kmCarga, Double litros, String completo, Double azul, Usuario usuario) throws ParseException {

        Camion camion = new Camion();
        Optional<Camion> cam = camionRepositorio.findById(idCamion);
        if (cam.isPresent()) {
            camion = cam.get();
        }

        Date f = convertirFecha(fecha);
        Double kmRecorrido = kmCarga - kmAnterior;
        Double consumo = ((100 * litros) / kmRecorrido);
        Double consumoRed = Math.round(consumo * 100.0) / 100.0;

        Combustible carga = new Combustible();

        if (azul != null && azul != 0) {
            azulServicio.crearCarga(idCamion, fecha, azul, usuario);
            Azul ultimoAzul = azulRepositorio.ultimaCargaAzul(idOrg);
            carga.setAzul(ultimoAzul);
        }
        if (idAcoplado != null){
            Acoplado acoplado = acopladoRepositorio.getById(idAcoplado);
            carga.setAcoplado(acoplado);
        } 

        carga.setIdOrg(idOrg);
        carga.setFechaCarga(f);
        carga.setLitro(litros);
        carga.setUsuario(usuario);
        carga.setChofer(usuario);
        carga.setKmCarga(kmCarga);
        carga.setKmAnterior(kmAnterior);
        carga.setKmRecorrido(kmRecorrido);
        carga.setConsumo(consumoRed);
        carga.setEstado("PENDIENTE");
        carga.setCamion(camion);

        if (completo.equalsIgnoreCase("NO")) {
            carga.setCompleto("NO");
            carga.setConsumoPromedio(0.0);
        } else {
            carga.setCompleto("SI");
            Double consumoPromedio = consumoPromedioTanque(consumoRed, kmRecorrido, litros, camion);
            carga.setConsumoPromedio(consumoPromedio);
        }

        combustibleRepositorio.save(carga);

    }

    @Transactional
    public void crearCargaAdmin(Long idOrg, Long idCamion, String fecha, Long idChofer, Long idAcoplado, Double kmAnterior, Double kmCarga, Double litros, String completo, Double azul, Usuario usuario) throws ParseException {

        Camion camion = new Camion();
        Optional<Camion> cam = camionRepositorio.findById(idCamion);
        if (cam.isPresent()) {
            camion = cam.get();
        }

        Usuario chofer = new Usuario();
        Optional<Usuario> user = usuarioRepositorio.findById(idChofer);
        if (user.isPresent()) {
            chofer = user.get();
        }

        Date f = convertirFecha(fecha);
        Double kmRecorrido = kmCarga - kmAnterior;
        Double consumo = ((100 * litros) / kmRecorrido);
        Double consumoRed = Math.round(consumo * 100.0) / 100.0;

        Combustible carga = new Combustible();

        if (azul != null && azul != 0) {
            azulServicio.crearCargaAdmin(idCamion, fecha, azul, usuario);
            Azul ultimoAzul = azulRepositorio.ultimaCargaAzul(idOrg);
            carga.setAzul(ultimoAzul);
        }
        if (idAcoplado != null){
            Acoplado acoplado = acopladoRepositorio.getById(idAcoplado);
            carga.setAcoplado(acoplado);
        } 

        carga.setIdOrg(idOrg);
        carga.setFechaCarga(f);
        carga.setLitro(litros);
        carga.setUsuario(usuario);
        carga.setChofer(chofer);
        carga.setKmCarga(kmCarga);
        carga.setKmAnterior(kmAnterior);
        carga.setKmRecorrido(kmRecorrido);
        carga.setConsumo(consumoRed);
        carga.setEstado("ACEPTADO");
        carga.setCamion(camion);

        if (completo.equalsIgnoreCase("NO")) {
            carga.setCompleto("NO");
            carga.setConsumoPromedio(0.0);
        } else {
            carga.setCompleto("SI");
            Double consumoPromedio = consumoPromedioTanque(consumoRed, kmRecorrido, litros, camion);
            carga.setConsumoPromedio(consumoPromedio);
        }

        combustibleRepositorio.save(carga);

    }

    @Transactional
    public void aceptarCarga(Long idCarga, Usuario usuario) {

        Combustible carga = new Combustible();
        Optional<Combustible> comb = combustibleRepositorio.findById(idCarga);
        if (comb.isPresent()) {
            carga = comb.get();
        }

        carga.setEstado("ACEPTADO");
        carga.setUsuario(usuario);

        combustibleRepositorio.save(carga);

    }

    @Transactional
    public void volverPendiente(Long idCarga, Usuario usaurio) {

        Combustible carga = new Combustible();
        Optional<Combustible> comb = combustibleRepositorio.findById(idCarga);
        if (comb.isPresent()) {
            carga = comb.get();
        }

        carga.setEstado("PENDIENTE");
        carga.setUsuario(usaurio);

        combustibleRepositorio.save(carga);

    }

    public Long buscaridCamion(Long idCarga) {

        Camion camion = combustibleRepositorio.findCamionByCargaId(idCarga);

        Long idCamion = camion.getId();

        return idCamion;

    }

    @Transactional
    public void modificarPrimerCarga(Long id, String fecha, Double kmCarga) throws ParseException {

        Combustible carga = new Combustible();
        Optional<Combustible> cga = combustibleRepositorio.findById(id);
        if (cga.isPresent()) {
            carga = cga.get();
        }

        Date f = convertirFecha(fecha);

        carga.setFechaCarga(f);
        carga.setKmCarga(kmCarga);

        combustibleRepositorio.save(carga);

    }

    @Transactional
    public void modificarCarga(Long id, Long idAcoplado, String fecha, Double kmCarga, Double litros, String completo, Double azul, Usuario logueado) throws ParseException {

        Combustible carga = new Combustible();
        Optional<Combustible> cga = combustibleRepositorio.findById(id);
        if (cga.isPresent()) {
            carga = cga.get();
        }

        boolean modificakm = false;
        if (carga.getKmCarga() != kmCarga) {
            modificakm = true;
        }

        Date f = convertirFecha(fecha);
        Double kmRecorrido = kmCarga - carga.getKmAnterior();
        Double consumo = ((100 * litros) / kmRecorrido);
        Double consumoRed = Math.round(consumo * 100.0) / 100.0;
        
        if (idAcoplado != null){
            Acoplado acoplado = acopladoRepositorio.getById(idAcoplado);
            carga.setAcoplado(acoplado);
        } else {
            carga.setAcoplado(null);
        }

        carga.setFechaCarga(f);
        carga.setKmCarga(kmCarga);
        carga.setLitro(litros);
        carga.setKmRecorrido(kmRecorrido);
        carga.setConsumo(consumoRed);

        if (completo.equalsIgnoreCase("NO")) {
            carga.setCompleto("NO");
            carga.setConsumoPromedio(0.0);
        } else {
            carga.setCompleto("SI");
            Double consumoPromedio = consumoPromedioTanqueModifica(consumoRed, kmRecorrido, litros, carga.getCamion(), carga.getCamion().getId());
            carga.setConsumoPromedio(consumoPromedio);
        }

        if (carga.getAzul() == null && azul != null && azul != 0) {
            azulServicio.crearCarga(carga.getCamion().getId(), fecha, azul, logueado);
            Azul ultimoAzul = azulRepositorio.ultimaCargaAzul(logueado.getIdOrg());
            carga.setAzul(ultimoAzul);
        }

        Long idAzul = null;

        if (carga.getAzul() != null && (azul == null || azul == 0)) {
            idAzul = carga.getAzul().getId();
            carga.setAzul(null);
        }

        combustibleRepositorio.save(carga);

        if (carga.getAzul() != null && (azul != null || azul != 0)) {
            azulServicio.modificarCarga(carga.getAzul().getId(), fecha, carga.getCamion().getId(), azul, logueado);
        }

        if (idAzul != null) {

            azulServicio.eliminarCarga(idAzul);
        }

        //Si modificó km y no es última Carga, busca carga posterior para modificar km y consumo
        Combustible ultimaCarga = combustibleRepositorio.findTopByCamionOrderByIdDesc(carga.getCamion());
        if (ultimaCarga.getId() != carga.getId() && modificakm == true) {

            Combustible posterior = new Combustible();
            Optional<Combustible> comb = combustibleRepositorio.findFirstByIdGreaterThanAndCamionIdOrderByIdAsc(carga.getId(), carga.getCamion().getId());
            if (comb.isPresent()) {
                posterior = comb.get();
            }
            if (posterior != null) {
                posterior.setKmAnterior(carga.getKmCarga());
                Double kmRecorridoP = posterior.getKmCarga() - carga.getKmCarga();
                posterior.setKmRecorrido(kmRecorridoP);
                Double consumoP = ((100.0 * posterior.getLitro()) / kmRecorridoP);
                Double consumoRedP = Math.round(consumoP * 100.0) / 100.0;
                posterior.setConsumo(consumoRedP);
                if (posterior.getCompleto().equalsIgnoreCase("SI")) {
                    Double consumoPromedioP = consumoPromedioTanqueModifica(consumoRedP, kmRecorridoP, posterior.getLitro(), posterior.getCamion(), posterior.getCamion().getId());
                    posterior.setConsumoPromedio(consumoPromedioP);
                }

                combustibleRepositorio.save(posterior);
            }
        }

    }

    @Transactional
    public void eliminarCarga(Long id) throws ParseException {

        Combustible carga = new Combustible();
        Optional<Combustible> cga = combustibleRepositorio.findById(id);
        if (cga.isPresent()) {
            carga = cga.get();
        }

        Long idAzul = null;

        if (carga.getAzul() != null) {
            idAzul = carga.getAzul().getId();
            carga.setAzul(null);
        }

        carga.setImagen(null);
        carga.setUsuario(null);
        carga.setChofer(null);

        combustibleRepositorio.save(carga);

        combustibleRepositorio.deleteById(id);

        if (idAzul != null) {
            azulServicio.eliminarCarga(idAzul);
        }

    }

    public ArrayList<Combustible> buscarCargasCamion(Long idCamion) {

        Camion camion = camionRepositorio.getById(idCamion);

        ArrayList<Combustible> lista = combustibleRepositorio.findAllByCamionOrderByIdDesc(camion);

        return lista;

    }

    public ArrayList<Combustible> buscarCargasIdCamion(Long id, String desde, String hasta) throws ParseException {

        Date d = convertirFecha(desde);
        Date h = convertirFecha(hasta);

        ArrayList<Combustible> lista = combustibleRepositorio.buscarCombustibleIdCamion(d, h, id);

        Collections.sort(lista, CombustibleComparador.ordenarIdDesc);

        return lista;
    }
    
    public ArrayList<Combustible> buscarCargasIdCamionFechaAsc(Long id, String desde, String hasta) throws ParseException {

        Date d = convertirFecha(desde);
        Date h = convertirFecha(hasta);

        ArrayList<Combustible> lista = combustibleRepositorio.buscarCombustibleIdCamion(d, h, id);

        Collections.sort(lista, CombustibleComparador.ordenarFechaAsc);

        return lista;
    }

    public ArrayList<Combustible> buscarCargasIdChofer(Long id, String desde, String hasta) throws ParseException {

        Date d = convertirFecha(desde);
        Date h = convertirFecha(hasta);

        ArrayList<Combustible> lista = combustibleRepositorio.buscarCombustibleIdChofer(d, h, id);

        Collections.sort(lista, CombustibleComparador.ordenarIdDesc);

        return lista;
    }
    
    public ArrayList<Combustible> buscarCargasIdChoferFechaAsc(Long id, String desde, String hasta) throws ParseException {

        Date d = convertirFecha(desde);
        Date h = convertirFecha(hasta);

        ArrayList<Combustible> lista = combustibleRepositorio.buscarCombustibleIdChofer(d, h, id);

        Collections.sort(lista, CombustibleComparador.ordenarFechaAsc);

        return lista;
    }
    
    public ArrayList<Combustible> buscarCargas(Long id, String desde, String hasta) throws ParseException {

        Date d = convertirFecha(desde);
        Date h = convertirFecha(hasta);

        ArrayList<Combustible> lista = combustibleRepositorio.findByFechaCargaBetweenAndIdOrg(d, h, id);

        Collections.sort(lista, CombustibleComparador.ordenarIdDesc);

        return lista;
    }
    
    public ArrayList<Combustible> buscarCargasFechaAsc(Long id, String desde, String hasta) throws ParseException {

        Date d = convertirFecha(desde);
        Date h = convertirFecha(hasta);

        ArrayList<Combustible> lista = combustibleRepositorio.findByFechaCargaBetweenAndIdOrg(d, h, id);

        Collections.sort(lista, CombustibleComparador.ordenarFechaAsc);

        return lista;
    }
    
    public ArrayList<Combustible> buscarCargasCamionChofer(Long idCamion, Long idChofer, String desde, String hasta) throws ParseException {

        Date d = convertirFecha(desde);
        Date h = convertirFecha(hasta);

        ArrayList<Combustible> lista = combustibleRepositorio.buscarCombustibleIdCamionIdChofer(d, h, idCamion, idChofer);

        Collections.sort(lista, CombustibleComparador.ordenarIdDesc);

        return lista;
    }
    
    public ArrayList<Combustible> buscarCargasCamionChoferFechaAsc(Long idCamion, Long idChofer, String desde, String hasta) throws ParseException {

        Date d = convertirFecha(desde);
        Date h = convertirFecha(hasta);

        ArrayList<Combustible> lista = combustibleRepositorio.buscarCombustibleIdCamionIdChofer(d, h, idCamion, idChofer);

        Collections.sort(lista, CombustibleComparador.ordenarFechaAsc);

        return lista;
    }

    public Double consumoPromedioCamion(Long idCamion) {

        Camion camion = camionRepositorio.getById(idCamion);
        Double litros = 0.0;
        Double km = 0.0;
        Double redondeado = 0.0;

        ArrayList<Combustible> listaCargas = combustibleRepositorio.findAllByCamionOrderByIdDesc(camion);

        if (!listaCargas.isEmpty()) {

            int lastIndex = listaCargas.size() - 1;
            listaCargas.remove(lastIndex);  //elimino ultimo registro de la listra porque es la carga inicial de KM

            for (Combustible c : listaCargas) {
                litros = litros + c.getLitro();
                km = km + c.getKmRecorrido();
            }

            Double consumoPromedioCamion = (100 * litros) / km;
            redondeado = Math.round(consumoPromedioCamion * 100.0) / 100.0;

            return redondeado;

        } else {

            return redondeado;

        }

    }

    public Double consumoPromedioTanque(Double consumo, Double km, Double litros, Camion camion) {

        ArrayList<Combustible> listaPromedio = new ArrayList();

        Combustible ultimaCarga = combustibleRepositorio.findTopByCamionOrderByIdDesc(camion);

        if (ultimaCarga.getCompleto().equalsIgnoreCase("NO")) {

            ArrayList<Combustible> listaCargas = combustibleRepositorio.findAllByCamionOrderByIdDesc(camion);

            for (Combustible c : listaCargas) {
                if (c.getCompleto().equalsIgnoreCase("SI") || c.getConsumo() == null) {
                    break;
                }
                listaPromedio.add(c);
            }
        }

        if (!listaPromedio.isEmpty()) {  //si listaPromedio no está vacía

            km = km;
            litros = litros;

            for (Combustible c : listaPromedio) {
                km = km + c.getKmRecorrido();
                litros = litros + c.getLitro();

            }

            Double consumoPromedio = ((100 * litros) / km);
            consumo = Math.round(consumoPromedio * 100.0) / 100.0;

        }

        return consumo;
    }

    public Double consumoPromedioTanqueModifica(Double consumo, Double km, Double litros, Camion camion, Long id) {

        ArrayList<Combustible> listaPromedio = new ArrayList();

        ArrayList<Combustible> ultimasCargas = combustibleRepositorio.findTop2ByCamionOrderByIdDesc(id);  //se obtienen ultimas 2 cargas
        Combustible anteultimaCarga = ultimasCargas.get(1);  //se obtiene anteultima carga

        if (anteultimaCarga.getCompleto().equalsIgnoreCase("NO")) {

            ArrayList<Combustible> listaCargas = combustibleRepositorio.findAllByCamionOrderByIdDesc(camion);
            listaCargas.remove(0);

            for (Combustible c : listaCargas) {
                if (c.getCompleto().equalsIgnoreCase("SI") || c.getConsumo() == null) {

                    break;
                }

                listaPromedio.add(c);
            }
        }

        if (!listaPromedio.isEmpty()) {  //si listaPromedio no está vacía

            km = km;
            litros = litros;

            for (Combustible c : listaPromedio) {
                km = km + c.getKmRecorrido();
                litros = litros + c.getLitro();

            }

            Double consumoPromedio = ((100 * litros) / km);
            consumo = Math.round(consumoPromedio * 100.0) / 100.0;

        }

        return consumo;
    }
/*
    public Long buscarUltimo(Long idOrg) {

        return combustibleRepositorio.ultimaCarga(idOrg);

    }
    */
    public Combustible buscarUltimo(Long idOrg) {

        return combustibleRepositorio.findTopByIdOrgOrderByIdDesc(idOrg);

    }

    public Combustible buscarCombustibleIdImagen(Long id) {

        return combustibleRepositorio.buscarCombustibleIdImagen(id);
    }
    
    public Combustible buscarCombustibleIdAzul(Long id) {

        return combustibleRepositorio.buscarCombustibleIdAzul(id);
    }

    public Combustible buscarCombustible(Long id) {

        return combustibleRepositorio.getById(id);
    }

    public Combustible cargaAnterior(Camion camion) {

        return combustibleRepositorio.findTopByCamionOrderByIdDesc(camion);

    }

    public Combustible cargaAnteriorPorId(Long idCarga, Long idCamion) {

        return combustibleRepositorio.findFirstByIdLessThanAndCamionIdOrderByIdDesc(idCarga, idCamion);

    }

    public Boolean ultimaCarga(Camion camion, Long idCarga) {

        Boolean flag = false;

        Combustible combustible = combustibleRepositorio.findTopByCamionOrderByIdDesc(camion);
        if (combustible.getId() == idCarga) {
            flag = true;
        }

        return flag;
    }
    
    public int kmUltimaCarga(Camion camion) {

        Combustible carga = combustibleRepositorio.findTopByCamionOrderByIdDesc(camion);
        
        if(carga != null){
            
        Double km = carga.getKmCarga();
        
        return km.intValue();
        
        } else {
            
            return 0;
            
        }

    }

    public Combustible cargaAnteultimo(Camion camion) {

        ArrayList<Combustible> ultimosRegistros = combustibleRepositorio.findTop2ByCamionOrderByIdDesc(camion);

        if (ultimosRegistros.size() >= 2) {
            return ultimosRegistros.get(1);
        } else {
            return ultimosRegistros.get(0);
        }
    }

    public boolean kmIniciales(Camion camion) {

        Optional<Combustible> iniciales = combustibleRepositorio.findFirstByCamionOrderByIdAsc(camion);
        if (iniciales.isPresent()) {

            boolean flag = true;

            return flag;

        } else {

            boolean flag = false;

            return flag;

        }

    }
    
    public int kmAcoplado(Acoplado acoplado, Date fechaDesde){
        
        Date fechaActual = new Date();
        
        ArrayList<Combustible> cargas = combustibleRepositorio.findByFechaCargaBetweenAndAcoplado(fechaDesde, fechaActual, acoplado);
        
        Double km = 0.0;
        
        if(!cargas.isEmpty()){
            
            for(Combustible carga : cargas){
            
                km = km + carga.getKmRecorrido();
                
        }
        }
        
        return km.intValue();
        
    }

    public Date convertirFecha(String fecha) throws ParseException {
        SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd");
        return formato.parse(fecha);
    }

}
