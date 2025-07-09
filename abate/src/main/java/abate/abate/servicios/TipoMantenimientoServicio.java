
package abate.abate.servicios;

import abate.abate.entidades.Mantenimiento;
import abate.abate.entidades.TipoMantenimiento;
import abate.abate.entidades.Usuario;
import abate.abate.excepciones.MiException;
import abate.abate.repositorios.MantenimientoRepositorio;
import abate.abate.repositorios.TipoMantenimientoRepositorio;
import abate.abate.util.TipoMantenimientoComparador;
import java.util.Collections;
import java.util.List;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TipoMantenimientoServicio {
    
    @Autowired
    private TipoMantenimientoRepositorio tipoMantenimientoRepositorio;
    @Autowired
    private MantenimientoRepositorio mantenimientoRepositorio;
    
    @Transactional
    public void crearTipoMantenimiento(String nombre, List<TipoMantenimiento.AplicaA> aplicaA, Usuario usuario) throws MiException {

        validarDatos(nombre, usuario.getIdOrg());
        
        String nombreMayusculas = nombre.toUpperCase();
        
        TipoMantenimiento tipoMantenimiento = new TipoMantenimiento();
        tipoMantenimiento.setNombre(nombreMayusculas); 
        tipoMantenimiento.setAplicaA(aplicaA);
        tipoMantenimiento.setIdOrg(usuario.getIdOrg());
        tipoMantenimiento.setUsuario(usuario);

        tipoMantenimientoRepositorio.save(tipoMantenimiento);

    }
    
    @Transactional
    public void modificarTipoMantenimiento(Long id, String nombre, List<TipoMantenimiento.AplicaA> aplicaA, Usuario usuario) throws MiException {

        TipoMantenimiento tipo = tipoMantenimientoRepositorio.getById(id);
        
        validarDatosModificar(tipo, nombre, usuario.getIdOrg());
        
        String nombreMayusculas = nombre.toUpperCase();

        tipo.setNombre(nombreMayusculas); 
        tipo.setUsuario(usuario);

        tipo.getAplicaA().clear();

        tipo.getAplicaA().addAll(aplicaA);
   
        tipoMantenimientoRepositorio.save(tipo);
        
    }
    
    @Transactional
    public void eliminarTipoMantenimiento(Long id) throws MiException {
        
        TipoMantenimiento tipo = tipoMantenimientoRepositorio.getById(id);
        
        Mantenimiento mantenimiento = mantenimientoRepositorio.findTopByTipoMantenimientoAndIdOrgOrderByIdDesc(tipo, tipo.getIdOrg());

        if ( mantenimiento == null ) {
            
            tipo.getAplicaA().clear();
            tipo.setUsuario(null);
            
            tipoMantenimientoRepositorio.save(tipo);

            tipoMantenimientoRepositorio.deleteById(id);

        } else {

            throw new MiException("El Tipo de Mantenimiento no puede ser eliminado, tiene Mantenimiento asociado.");
        }

    }
    
    public TipoMantenimiento buscarUltimo(Long idOrg) {

        return tipoMantenimientoRepositorio.ultimoTipo(idOrg);
    }
    
    public TipoMantenimiento buscarTipo(Long id) {

        return tipoMantenimientoRepositorio.getById(id);
    }
    
    public List<TipoMantenimiento> buscarTiposAsc(Long idOrg) {

        List<TipoMantenimiento> lista = tipoMantenimientoRepositorio.buscarTipos(idOrg);

        Collections.sort(lista, TipoMantenimientoComparador.ordenarNombreAsc); 

        return lista;

    }
    
    public List<TipoMantenimiento> buscarTiposAplicaA(Long idOrg, TipoMantenimiento.AplicaA aplicaA) {

        List<TipoMantenimiento> lista = tipoMantenimientoRepositorio.findByAplicaA(idOrg, aplicaA);

        return lista;

    }
    
    public void validarDatos(String nombre, Long idOrg) throws MiException {

        List<TipoMantenimiento> lista = tipoMantenimientoRepositorio.buscarTipos(idOrg);
        
        for (TipoMantenimiento u : lista) {
            if (u.getNombre().equalsIgnoreCase(nombre)) {
                throw new MiException("El Tipo de Mantenimiento '"+nombre+"' ya está registrado.");
            }
    }
    
}
        
       public void validarDatosModificar(TipoMantenimiento tipo, String nombre, Long idOrg) throws MiException {

        List<TipoMantenimiento> lista = tipoMantenimientoRepositorio.buscarTipos(idOrg);

        if(!tipo.getNombre().equalsIgnoreCase(nombre)){
        for (TipoMantenimiento t : lista) {
            if (t.getNombre().equalsIgnoreCase(nombre)) {
                throw new MiException("El Tipo de Mantenimiento '"+nombre+"' ya está registrado.");
            }
        }
    } 
    }
        
    
}
