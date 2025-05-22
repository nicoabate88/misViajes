
package abate.abate.servicios;

import abate.abate.entidades.HistorialRecapado;
import abate.abate.entidades.Neumatico;
import abate.abate.entidades.NeumaticoProveedor;
import abate.abate.entidades.Usuario;
import abate.abate.repositorios.HistorialRecapadoRepositorio;
import abate.abate.repositorios.NeumaticoProveedorRepositorio;
import abate.abate.repositorios.NeumaticoRepositorio;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class HistorialRecapadoServicio {
    
    @Autowired
    private NeumaticoRepositorio neumaticoRepositorio;
    @Autowired
    private HistorialRecapadoRepositorio recapadoRepositorio;
    @Autowired
    private NeumaticoProveedorRepositorio proveedorRepositorio;
    
    @Transactional
    public void crearRecapado(String fecha, Long idNeumatico, Integer km, Integer kmEstimado, Long idProveedor, String observacion, Usuario usuario) throws ParseException{
        
        HistorialRecapado recapado = new HistorialRecapado();
        
        Neumatico neumatico = neumaticoRepositorio.getById(idNeumatico);
        
        Date f = convertirFecha(fecha);
        NeumaticoProveedor proveedor = proveedorRepositorio.getById(idProveedor);
        
        recapado.setFechaEnvio(f);
        recapado.setIdOrg(usuario.getIdOrg());
        recapado.setKmAlRecapar(km);
        recapado.setKmEstimado(kmEstimado);
        recapado.setObservacion(observacion.toUpperCase());
        recapado.setNeumatico(neumatico);
        recapado.setEstado("EN RECAPADO");
        recapado.setProveedor(proveedor);
        recapado.setUsuario(usuario);
        
        recapadoRepositorio.save(recapado);
        
        neumatico.setUbicacion("EN RECAPADO");
        neumatico.setUsuario(usuario);
        neumaticoRepositorio.save(neumatico);
        
    }
    
    @Transactional
    public void modificarRecapado(Long id, String fecha, Integer km, Integer kmEstimado, Long idProveedor, String observacion, Usuario usuario) throws ParseException{
        
        HistorialRecapado recapado = recapadoRepositorio.getById(id);
        
        Date f = convertirFecha(fecha);
        String obsMayusculas = observacion.toUpperCase();
        NeumaticoProveedor proveedor = proveedorRepositorio.getById(idProveedor);
        
        recapado.setFechaEnvio(f);
        recapado.setKmAlRecapar(km);
        recapado.setKmEstimado(kmEstimado);
        recapado.setObservacion(obsMayusculas);
        recapado.setProveedor(proveedor);
        recapado.setUsuario(usuario);
        
        recapadoRepositorio.save(recapado);
        
    }
    
    @Transactional
    public void eliminarRecapado(Long idRecapado){
        
        HistorialRecapado recapado = recapadoRepositorio.getById(idRecapado);
        
        Neumatico neumatico = recapado.getNeumatico();
        neumatico.setUbicacion("DEPOSITO");
        
        neumaticoRepositorio.save(neumatico);
        
        recapadoRepositorio.deleteById(idRecapado);
        
    }
    
    @Transactional
    public void reingresarRecapado(Long idRecapado, Usuario usuario) {
        
        HistorialRecapado recapado = recapadoRepositorio.getById(idRecapado);
        Neumatico neumatico = recapado.getNeumatico();
        
        List<HistorialRecapado> recapados = neumatico.getRecapados();
            if(!recapados.isEmpty()){
                for(HistorialRecapado r : recapados){
                    if(r.getEstado().equalsIgnoreCase("VIGENTE")){
                        r.setEstado("FINALIZADO");
                        r.setKmFinalRecapado(recapado.getKmAlRecapar());
                        r.setKmRecapado(recapado.getKmAlRecapar() - r.getKmAlRecapar());
                        recapadoRepositorio.save(r);
                    }
            }
            }
        
        recapado.setFechaReingreso(new Date());
        recapado.setEstado("VIGENTE");
        recapado.setUsuario(usuario);
        
        recapadoRepositorio.save(recapado);

        neumatico.setUbicacion("DEPOSITO");
        neumatico.setEstado("RECAPADO");
        Integer kmEstimado = neumatico.getKmEstimado() + recapado.getKmEstimado();
        neumatico.setKmEstimado(kmEstimado);
        neumatico.setKmUtil(kmEstimado - neumatico.getKm());
        neumatico.setUsuario(usuario);
        neumatico.getRecapados().add(recapado);
        neumaticoRepositorio.save(neumatico);
        
    }
    
    public HistorialRecapado buscarUltimo(Long idOrg){
        
        return recapadoRepositorio.ultimoRecapado(idOrg);
        
    }
    
    public HistorialRecapado buscarRecapado(Long idRecapado){
        
        return recapadoRepositorio.getById(idRecapado);
        
    }
    
    public Date convertirFecha(String fecha) throws ParseException {
        SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd");
        return formato.parse(fecha);
    }
    
    
}
