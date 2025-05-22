
package abate.abate.servicios;

import abate.abate.entidades.Neumatico;
import abate.abate.entidades.NeumaticoProveedor;
import abate.abate.entidades.Usuario;
import abate.abate.excepciones.MiException;
import abate.abate.repositorios.NeumaticoProveedorRepositorio;
import abate.abate.repositorios.NeumaticoRepositorio;
import abate.abate.util.NeumaticoProveedorComparador;
import java.util.Collections;
import java.util.List;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NeumaticoProveedorServicio {
    
    @Autowired
    private NeumaticoProveedorRepositorio proveedorRepositorio;
    @Autowired
    private NeumaticoRepositorio neumaticoRepositorio;
    
    @Transactional
    public void crearProveedor(String nombre, Usuario usuario) throws MiException{
        
        validarDatos(nombre, usuario.getIdOrg());
        
        NeumaticoProveedor proveedor = new NeumaticoProveedor();
        
        proveedor.setNombre(nombre.toUpperCase());
        proveedor.setIdOrg(usuario.getIdOrg());
        proveedor.setUsuario(usuario);
        
        proveedorRepositorio.save(proveedor);
        
    }
    
    @Transactional
    public void modificarProveedor(Long id, String nombre, Usuario usuario) throws MiException {
        
        NeumaticoProveedor proveedor = proveedorRepositorio.getById(id);
        
        validarDatosModificar(proveedor, nombre);

        proveedor.setNombre(nombre.toUpperCase());
        proveedor.setUsuario(usuario);
                
        proveedorRepositorio.save(proveedor);

    }
    
    @Transactional
    public void eliminarProveedor(Long id, Long idOrg) throws MiException{
        
       Neumatico neumatico = neumaticoRepositorio.findTopByProveedorIdAndIdOrgOrderByIdDesc(id, idOrg);
       
        if (neumatico == null) {
            
            proveedorRepositorio.deleteById(id);

        } else {

            throw new MiException("El Proveedor no puede ser eliminado, tiene neumáticos registrados.");
        }
        
    }
    
    public NeumaticoProveedor buscarUltimo(Long idOrg) {

        return proveedorRepositorio.ultimoProveedor(idOrg);

    }
    
    public NeumaticoProveedor buscarProveedor(Long id) {

        return proveedorRepositorio.getById(id);
    }
    
   public List<NeumaticoProveedor> buscarProveedoresAsc(Long idOrg) {

        List<NeumaticoProveedor> lista = proveedorRepositorio.buscarNeumaticoProveedor(idOrg);

        Collections.sort(lista, NeumaticoProveedorComparador.ordenarNombreAsc); 

        return lista;

    }
    
    
    public void validarDatos(String nombre, Long idOrg) throws MiException {

        List<NeumaticoProveedor> lista = proveedorRepositorio.buscarNeumaticoProveedor(idOrg);

        for (NeumaticoProveedor p : lista) {
            if (p.getNombre().equalsIgnoreCase(nombre)) {
                throw new MiException("El Proveedor '"+nombre+"' ya está registrado.");
            }
        }
    }    
    
    
    public void validarDatosModificar(NeumaticoProveedor proveedor, String nombre) throws MiException {

        List<NeumaticoProveedor> lista = proveedorRepositorio.buscarNeumaticoProveedor(proveedor.getIdOrg());

        if(!proveedor.getNombre().equalsIgnoreCase(nombre)){
        for (NeumaticoProveedor p : lista) {
            if (p.getNombre().equalsIgnoreCase(nombre)) {
                throw new MiException("El Proveedor '"+nombre+"' ya está registrado.");
            }
        }
    } 
    }
    
    
}
