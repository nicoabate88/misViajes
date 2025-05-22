
package abate.abate.servicios;

import abate.abate.entidades.Neumatico;
import abate.abate.entidades.NeumaticoMarca;
import abate.abate.entidades.Usuario;
import abate.abate.excepciones.MiException;
import abate.abate.repositorios.NeumaticoMarcaRepositorio;
import abate.abate.repositorios.NeumaticoRepositorio;
import abate.abate.util.NeumaticoMarcaComparador;
import java.util.Collections;
import java.util.List;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NeumaticoMarcaServicio {
    
    @Autowired
    private NeumaticoMarcaRepositorio marcaRepositorio;
    @Autowired
    private NeumaticoRepositorio neumaticoRepositorio;
    
    @Transactional
    public void crearMarca(String nombre, Usuario usuario) throws MiException{
        
        validarDatos(nombre, usuario.getIdOrg());
        
        NeumaticoMarca marca = new NeumaticoMarca();
        
        marca.setIdOrg(usuario.getIdOrg());
        marca.setMarca(nombre.toUpperCase());
        marca.setUsuario(usuario);
        
        marcaRepositorio.save(marca);
        
    }
    
    @Transactional
    public void modificarMarca(Long id, String nombre, Usuario usuario) throws MiException {
        
        NeumaticoMarca marca = marcaRepositorio.getById(id);
        
        validarDatosModificar(marca, nombre);

        marca.setMarca(nombre.toUpperCase());
        marca.setUsuario(usuario);
                
        marcaRepositorio.save(marca);

    }
    
    @Transactional
    public void eliminarMarca(Long id, Long idOrg) throws MiException{
        
       Neumatico neumatico = neumaticoRepositorio.findTopByMarcaIdAndIdOrgOrderByIdDesc(id, idOrg);
        
       if (neumatico == null) {
            
            marcaRepositorio.deleteById(id);

        } else {

            throw new MiException("La Marca no puede ser eliminada, existe Neúmatico registrado con esta marca.");
        }
        
    }
    
    public NeumaticoMarca buscarUltimo(Long idOrg) {

        return marcaRepositorio.ultimaMarca(idOrg);

    }
    
    public NeumaticoMarca buscarMarca(Long id) {

        return marcaRepositorio.getById(id);
    }
    
   public List<NeumaticoMarca> buscarMarcasAsc(Long idOrg) {

        List<NeumaticoMarca> lista = marcaRepositorio.buscarNeumaticoMarca(idOrg);

        Collections.sort(lista, NeumaticoMarcaComparador.ordenarNombreAsc); 

        return lista;

    }
    
    public void validarDatos(String nombre, Long idOrg) throws MiException {

        List<NeumaticoMarca> lista = marcaRepositorio.buscarNeumaticoMarca(idOrg);

        for (NeumaticoMarca m : lista) {
            if (m.getMarca().equalsIgnoreCase(nombre)) {
                throw new MiException("La marca '"+nombre+"' ya está registrada.");
            }
        }
    }    
    
    
    public void validarDatosModificar(NeumaticoMarca marca, String nombre) throws MiException {

        List<NeumaticoMarca> lista = marcaRepositorio.buscarNeumaticoMarca(marca.getIdOrg());

        if(!marca.getMarca().equalsIgnoreCase(nombre)){
        for (NeumaticoMarca m : lista) {
            if (m.getMarca().equalsIgnoreCase(nombre)) {
                throw new MiException("La marca '"+nombre+"' ya está registrada.");
            }
        }
    } 
    }
    
}
