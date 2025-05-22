
package abate.abate.servicios;

import abate.abate.entidades.Documentacion;
import abate.abate.entidades.TipoDocumentacion;
import abate.abate.entidades.Usuario;
import abate.abate.excepciones.MiException;
import abate.abate.repositorios.DocumentacionRepositorio;
import abate.abate.repositorios.TipoDocumentacionRepositorio;
import abate.abate.util.TipoDocumentacionComparador;
import java.util.Collections;
import java.util.List;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TipoDocumentacionServicio {
    
    @Autowired
    private TipoDocumentacionRepositorio tipoDocumentacionRepositorio;
    @Autowired
    private DocumentacionRepositorio documentacionRepositorio;
    
    @Transactional
    public void crearTipoDocumentacion(String nombre, List<TipoDocumentacion.AplicaA> aplicaA, Usuario usuario) throws MiException {

        validarDatos(nombre, usuario.getIdOrg());
        
        String nombreMayusculas = nombre.toUpperCase();
        
        TipoDocumentacion tipoDocumentacion = new TipoDocumentacion();
        tipoDocumentacion.setNombre(nombreMayusculas); 
        tipoDocumentacion.setAplicaA(aplicaA);
        tipoDocumentacion.setUsuario(usuario);
        tipoDocumentacion.setIdOrg(usuario.getIdOrg());

        tipoDocumentacionRepositorio.save(tipoDocumentacion);

    }
    
    @Transactional
    public void modificarTipoDocumentacion(Long id, String nombre, List<TipoDocumentacion.AplicaA> aplicaA, Usuario usuario) throws MiException {

        TipoDocumentacion tipo = tipoDocumentacionRepositorio.getById(id);
        
        validarDatosModificar(tipo, nombre, usuario.getIdOrg());
        
        String nombreMayusculas = nombre.toUpperCase();

        tipo.setNombre(nombreMayusculas); 
        tipo.setUsuario(usuario);

        tipo.getAplicaA().clear();

        tipo.getAplicaA().addAll(aplicaA);
   
        tipoDocumentacionRepositorio.save(tipo);
        
    }
    
    @Transactional
    public void eliminarTipoDocumentacion(Long id) throws MiException {
        
        TipoDocumentacion tipo = tipoDocumentacionRepositorio.getById(id);
        
        Documentacion documentacion = documentacionRepositorio.findTopByTipoDocumentacionOrderByIdDesc(tipo);

        if ( documentacion == null ) {
            
            tipo.getAplicaA().clear();
            tipo.setUsuario(null);
            
            tipoDocumentacionRepositorio.save(tipo);

            tipoDocumentacionRepositorio.deleteById(id);

        } else {

            throw new MiException("El Tipo de Documentación no puede ser eliminado, tiene Documentación asociado.");
        }

    }
    
    public TipoDocumentacion buscarUltimo(Long idOrg) {

        return tipoDocumentacionRepositorio.ultimoTipo(idOrg);
    }
    
    public TipoDocumentacion buscarTipo(Long id) {

        return tipoDocumentacionRepositorio.getById(id);
    }
    
    public List<TipoDocumentacion> buscarTiposAsc(Long idOrg) {

        List<TipoDocumentacion> lista = tipoDocumentacionRepositorio.buscarTipos(idOrg);

        Collections.sort(lista, TipoDocumentacionComparador.ordenarNombreAsc); 

        return lista;

    }
    
    public List<TipoDocumentacion> buscarTiposAplicaA(Long idOrg, TipoDocumentacion.AplicaA aplicaA) {

        List<TipoDocumentacion> lista = tipoDocumentacionRepositorio.findByAplicaA(idOrg, aplicaA);

        return lista;

    }
    
    public void validarDatos(String nombre, Long idOrg) throws MiException {

        List<TipoDocumentacion> lista = tipoDocumentacionRepositorio.buscarTipos(idOrg);
        
        for (TipoDocumentacion u : lista) {
            if (u.getNombre().equalsIgnoreCase(nombre)) {
                throw new MiException("El Nombre de Tipo de Documentación no es válido, por favor ingrese otro.");
            }
    }
    
}
        
       public void validarDatosModificar(TipoDocumentacion tipo, String nombre, Long idOrg) throws MiException {

        List<TipoDocumentacion> lista = tipoDocumentacionRepositorio.buscarTipos(idOrg);

        if(!tipo.getNombre().equalsIgnoreCase(nombre)){
        for (TipoDocumentacion t : lista) {
            if (t.getNombre().equalsIgnoreCase(nombre)) {
                throw new MiException("El NOMBRE '"+nombre+"' ya está registrado.");
            }
        }
    } 
    }
    
}
