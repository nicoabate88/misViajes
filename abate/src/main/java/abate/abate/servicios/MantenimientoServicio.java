
package abate.abate.servicios;

import abate.abate.entidades.Mantenimiento;
import abate.abate.entidades.TipoMantenimiento;
import abate.abate.entidades.Usuario;
import abate.abate.repositorios.MantenimientoRepositorio;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MantenimientoServicio {
    
    @Autowired
    private MantenimientoRepositorio mantenimientoRepositorio;
    @Autowired
    private CombustibleServicio combustibleServicio;
    
    @Transactional
    public void crearMantenimiento(Mantenimiento mantenimiento, Mantenimiento mantenimientoExistente) {

        if (mantenimientoExistente != null) {
            
            mantenimientoExistente.setEstado("ACTUALIZADO");
            mantenimientoExistente.setKmActual(mantenimiento.getKm());
            mantenimientoExistente.setKmVigencia(mantenimiento.getKm() - mantenimientoExistente.getKm());
            mantenimientoExistente.setFechaActualizado(mantenimiento.getFecha());
            
            mantenimientoRepositorio.save(mantenimientoExistente);
            
        }
       
        mantenimientoRepositorio.save(mantenimiento);
        
    }
  
    public Mantenimiento buscarExistente(Mantenimiento mantenimiento){
        
        Mantenimiento buscar = null;
        
        if(mantenimiento.getAplicaA() == TipoMantenimiento.AplicaA.CAMION){
        Optional<Mantenimiento> mant = mantenimientoRepositorio.buscarMantenimientoCamion(mantenimiento.getCamion().getId(), mantenimiento.getTipoMantenimiento().getId());
        if(mant.isPresent()){
            buscar = mant.get();
            
        int kmActual = combustibleServicio.kmUltimaCarga(buscar.getCamion());
        int kmAlarma = buscar.getKmAlarma();
        int kmProximo = buscar.getKmProximo();
        
        buscar.setKmVigencia(kmProximo - kmActual);
        buscar.setKmActual(kmActual);
        
        if(kmAlarma <= kmActual && kmProximo > kmActual){
            buscar.setEstado("PRÓXIMO A VENCER");
        } if(kmProximo <= kmActual ){
            buscar.setEstado("VENCIDO");
        }   
        }
        }
        
        else {
            
        Optional<Mantenimiento> mant = mantenimientoRepositorio.buscarMantenimientoAcoplado(mantenimiento.getAcoplado().getId(), mantenimiento.getTipoMantenimiento().getId());
        if(mant.isPresent()){
            buscar = mant.get();
            
        int kmActual = combustibleServicio.kmAcoplado(mantenimiento.getAcoplado(), obtenerFechaFija());
        int kmAlarma = buscar.getKmAlarma();
        int kmProximo = buscar.getKmProximo();
        
        buscar.setKmActual(kmActual);
        buscar.setKmVigencia(kmProximo - kmActual);

        if(kmAlarma <= kmActual && kmProximo > kmActual){
            mantenimiento.setEstado("PRÓXIMO A VENCER");
        } if(kmProximo <= kmActual ){
            mantenimiento.setEstado("VENCIDO");
        }
        }
        }
        
        return buscar;
            
        }
    
    @Transactional
    public void modificarMantenimiento(Long id, String fecha, Integer km, Integer kmProximo, Integer kmAlarma, String observacion, Usuario usuario) throws ParseException {

        Mantenimiento mantenimiento = mantenimientoRepositorio.getById(id);

        String obsMayusculas = observacion.toUpperCase();
        Date fechaMantenimiento = convertirFecha(fecha);

        mantenimiento.setFecha(fechaMantenimiento);
        mantenimiento.setKm(km);
        mantenimiento.setKmProximo(kmProximo);
        mantenimiento.setKmAlarma(kmAlarma);
        mantenimiento.setObservacion(obsMayusculas); 
        mantenimiento.setUsuario(usuario);

        mantenimientoRepositorio.save(mantenimiento);

    }
    
    @Transactional
    public void eliminarMantenimiento(Long id)  {

        Mantenimiento mantenimiento = mantenimientoRepositorio.getById(id);
        
        mantenimiento.setCamion(null);
        mantenimiento.setAcoplado(null);
        mantenimiento.setUsuario(null);
        mantenimiento.setTipoMantenimiento(null);

        mantenimientoRepositorio.save(mantenimiento);
        
        mantenimientoRepositorio.deleteById(id);

    }
    
    public Long buscarUltimo(Long idOrg) {

        return mantenimientoRepositorio.ultimoMantenimiento(idOrg);
        
    }
    
    public Mantenimiento buscarMantenimiento(Long id) {

        return mantenimientoRepositorio.getById(id);
    }
    
    public Mantenimiento buscarMantenimientoDiasVigencia(Long id) {

        Mantenimiento mantenimiento = mantenimientoRepositorio.getById(id);
        int kmActual;
        int kmAlarma = mantenimiento.getKmAlarma();
        int kmProximo = mantenimiento.getKmProximo();
        
        if(mantenimiento.getAplicaA() == TipoMantenimiento.AplicaA.CAMION){
             kmActual = combustibleServicio.kmUltimaCarga(mantenimiento.getCamion());
        } else {
             kmActual = combustibleServicio.kmAcoplado(mantenimiento.getAcoplado(), obtenerFechaFija());
        }
        
        mantenimiento.setKmActual(kmActual);
        mantenimiento.setKmVigencia(kmProximo - kmActual);

        if(kmAlarma <= kmActual && kmProximo > kmActual){
            mantenimiento.setEstado("PRÓXIMO A VENCER");
        } if( kmProximo <= kmActual ){
            mantenimiento.setEstado("VENCIDO");
        } 
        
        return mantenimiento;
    }
    
    public List<Mantenimiento> buscarMantenimientoIdCamion(Long id) {
        
        List<Mantenimiento> lista = mantenimientoRepositorio.buscarMantenimientoIdCamion(id);
        
        if(!lista.isEmpty()){
        for(Mantenimiento mantenimiento : lista){
            
        int kmActual = combustibleServicio.kmUltimaCarga(mantenimiento.getCamion());
        int kmAlarma = mantenimiento.getKmAlarma();
        int kmProximo = mantenimiento.getKmProximo();
        
        mantenimiento.setKmVigencia(kmProximo - kmActual);
        mantenimiento.setKmActual(kmActual);

        if(kmAlarma <= kmActual && kmProximo > kmActual){
            mantenimiento.setEstado("PRÓXIMO A VENCER");
        } if(kmProximo <= kmActual ){
            mantenimiento.setEstado("VENCIDO");
        } 
        }    
        }

        return lista;
    }
    
    public List<Mantenimiento> buscarMantenimientoIdAcoplado(Long id) {
        
        List<Mantenimiento> lista = mantenimientoRepositorio.buscarMantenimientoIdAcoplado(id);
        
        if(!lista.isEmpty()){
        for(Mantenimiento mantenimiento : lista){
            
        int kmActual = combustibleServicio.kmAcoplado(mantenimiento.getAcoplado(), obtenerFechaFija());
        int kmAlarma = mantenimiento.getKmAlarma();
        int kmProximo = mantenimiento.getKmProximo();
        
        mantenimiento.setKmVigencia(kmProximo - kmActual);
        mantenimiento.setKmActual(kmActual);

        if(kmAlarma <= kmActual && kmProximo > kmActual){
            mantenimiento.setEstado("PRÓXIMO A VENCER");
        } if(kmProximo <= kmActual ){
            mantenimiento.setEstado("VENCIDO");
        } 
        }    
        }

        return lista;
    }
    
    public List<Mantenimiento> obtenerMantenimientosPorVencer(Long idOrg, int km) {
        
        List<Mantenimiento> lista = mantenimientoRepositorio.findMantenimientosNoActualizados(idOrg);
        
        if(!lista.isEmpty()){
            
        for(Mantenimiento mantenimiento : lista){
            
        int kmActual;
        int kmProximo = mantenimiento.getKmProximo();
        
        if(mantenimiento.getAplicaA() == TipoMantenimiento.AplicaA.CAMION){
            kmActual = combustibleServicio.kmUltimaCarga(mantenimiento.getCamion());
        }
        else {
           kmActual = combustibleServicio.kmAcoplado(mantenimiento.getAcoplado(), obtenerFechaFija());
        }
        
        mantenimiento.setKmActual(kmActual);
        mantenimiento.setKmVigencia(kmProximo - kmActual);
        
        }    
        }

        Iterator<Mantenimiento> iterator = lista.iterator();
        while (iterator.hasNext()) {
        Mantenimiento mantenimiento = iterator.next();
        if (mantenimiento.getKmVigencia() > km) {
        iterator.remove(); 
        }
        }
        
        if(!lista.isEmpty()){
        for(Mantenimiento mantenimiento : lista){
        if(mantenimiento.getKmAlarma() <= mantenimiento.getKmActual() && mantenimiento.getKmProximo() > mantenimiento.getKmActual()){
            mantenimiento.setEstado("PRÓXIMO A VENCER");
        } if(mantenimiento.getKmProximo() <= mantenimiento.getKmActual()){
            mantenimiento.setEstado("VENCIDO");
        } 
        }
        }
        return lista;

    }
    
    public List<Mantenimiento> buscarMantenimientosCamiones(Long idOrg) {
          
        List<Mantenimiento> lista = mantenimientoRepositorio.findByCamionIsNotNullAndIdOrgAndEstadoNot(idOrg, "ACTUALIZADO");
        
        if(!lista.isEmpty()){
        for(Mantenimiento mantenimiento : lista){
            
        int kmActual = combustibleServicio.kmUltimaCarga(mantenimiento.getCamion());
        int kmAlarma = mantenimiento.getKmAlarma();
        int kmProximo = mantenimiento.getKmProximo();
        
        mantenimiento.setKmVigencia(kmProximo - kmActual);
        mantenimiento.setKmActual(kmActual);

        if(kmAlarma <= kmActual && kmProximo > kmActual){
            mantenimiento.setEstado("PRÓXIMO A VENCER");
        } if(kmProximo <= kmActual ){
            mantenimiento.setEstado("VENCIDO");
        } 
        }  
              
        }
          
        return lista;
    }
      
    public List<Mantenimiento> buscarMantenimientosAcoplados(Long idOrg) {
          
        List<Mantenimiento> lista = mantenimientoRepositorio.findByAcopladoIsNotNullAndIdOrgAndEstadoNot(idOrg, "ACTUALIZADO");
        
        if(!lista.isEmpty()){
            
        for(Mantenimiento mantenimiento : lista){
            
        int kmActual = combustibleServicio.kmAcoplado(mantenimiento.getAcoplado(), obtenerFechaFija());
        int kmAlarma = mantenimiento.getKmAlarma();
        int kmProximo = mantenimiento.getKmProximo();
        
        mantenimiento.setKmVigencia(kmProximo - kmActual);
        mantenimiento.setKmActual(kmActual);

        if(kmAlarma <= kmActual && kmProximo > kmActual){
            mantenimiento.setEstado("PRÓXIMO A VENCER");
        } if(kmProximo <= kmActual ){
            mantenimiento.setEstado("VENCIDO");
        } 
        } 
        }
          
        return lista;
    }
    
    public List<Mantenimiento> buscarMantenimientosCamionesPorTipo(Long idOrg, Long idTipo) {

        List<Mantenimiento> lista = mantenimientoRepositorio.buscarMantenimientoCamionesPorTipo(idOrg, idTipo);

        if (!lista.isEmpty()) {
            for (Mantenimiento mantenimiento : lista) {

                int kmActual = combustibleServicio.kmUltimaCarga(mantenimiento.getCamion());
                int kmAlarma = mantenimiento.getKmAlarma();
                int kmProximo = mantenimiento.getKmProximo();

                mantenimiento.setKmVigencia(kmProximo - kmActual);
                mantenimiento.setKmActual(kmActual);

                if (kmAlarma <= kmActual && kmProximo > kmActual) {
                    mantenimiento.setEstado("PRÓXIMO A VENCER");
                }
                if (kmProximo <= kmActual) {
                    mantenimiento.setEstado("VENCIDO");
                }
            }

        }

        return lista;
    }
    
    public List<Mantenimiento> buscarMantenimientosCamionPorTipo(Long idCamion, Long idTipo) {

        List<Mantenimiento> lista = mantenimientoRepositorio.buscarMantenimientoCamionPorTipo(idCamion, idTipo);

        if (!lista.isEmpty()) {
            for (Mantenimiento mantenimiento : lista) {

                int kmActual = combustibleServicio.kmUltimaCarga(mantenimiento.getCamion());
                int kmAlarma = mantenimiento.getKmAlarma();
                int kmProximo = mantenimiento.getKmProximo();

                mantenimiento.setKmVigencia(kmProximo - kmActual);
                mantenimiento.setKmActual(kmActual);

                if (kmAlarma <= kmActual && kmProximo > kmActual) {
                    mantenimiento.setEstado("PRÓXIMO A VENCER");
                }
                if (kmProximo <= kmActual) {
                    mantenimiento.setEstado("VENCIDO");
                }
            }

        }

        return lista;
    }
    
        public List<Mantenimiento> buscarMantenimientosAcopladosPorTipo(Long idOrg, Long idTipo) {

        List<Mantenimiento> lista = mantenimientoRepositorio.buscarMantenimientoAcopladosPorTipo(idOrg, idTipo);
        
        if(!lista.isEmpty()){
            
        for(Mantenimiento mantenimiento : lista){
            
        int kmActual = combustibleServicio.kmAcoplado(mantenimiento.getAcoplado(), obtenerFechaFija());
        int kmAlarma = mantenimiento.getKmAlarma();
        int kmProximo = mantenimiento.getKmProximo();
        
        mantenimiento.setKmVigencia(kmProximo - kmActual);
        mantenimiento.setKmActual(kmActual);

        if(kmAlarma <= kmActual && kmProximo > kmActual){
            mantenimiento.setEstado("PRÓXIMO A VENCER");
        } if(kmProximo <= kmActual ){
            mantenimiento.setEstado("VENCIDO");
        } 
        } 
        }

        return lista;
    }
        
        public List<Mantenimiento> buscarMantenimientosAcopladoPorTipo(Long idAcoplado, Long idTipo) {

        List<Mantenimiento> lista = mantenimientoRepositorio.buscarMantenimientoAcopladoPorTipo(idAcoplado, idTipo);

        if(!lista.isEmpty()){
            
        for(Mantenimiento mantenimiento : lista){
            
        int kmActual = combustibleServicio.kmAcoplado(mantenimiento.getAcoplado(), obtenerFechaFija());
        int kmAlarma = mantenimiento.getKmAlarma();
        int kmProximo = mantenimiento.getKmProximo();
        
        mantenimiento.setKmVigencia(kmProximo - kmActual);
        mantenimiento.setKmActual(kmActual);

        if(kmAlarma <= kmActual && kmProximo > kmActual){
            mantenimiento.setEstado("PRÓXIMO A VENCER");
        } if(kmProximo <= kmActual ){
            mantenimiento.setEstado("VENCIDO");
        } 
        } 
        }

        return lista;
    }
        
    public List<Mantenimiento> buscarHistorialCamion(Long id) {
        
        List<Mantenimiento> lista = mantenimientoRepositorio.buscarHistorialCamion(id);
        
        return lista;
    }     
    
    public List<Mantenimiento> buscarHistorialAcoplado(Long id) {
        
        List<Mantenimiento> lista = mantenimientoRepositorio.buscarHistorialAcoplado(id);
        
        return lista;
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
