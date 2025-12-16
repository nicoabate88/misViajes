
package abate.abate.servicios;

import abate.abate.entidades.OrdenDeTrabajo;
import abate.abate.entidades.Proveedor;
import abate.abate.excepciones.MiException;
import abate.abate.repositorios.OrdenDeTrabajoRepositorio;
import abate.abate.repositorios.ProveedorRepositorio;
import abate.abate.util.ProveedorComparador;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;
import java.util.Optional;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProveedorServicio {
    
    @Autowired
    private ProveedorRepositorio proveedorRepositorio;
    @Autowired
    private OrdenDeTrabajoRepositorio ordenRepositorio;
    
    @Transactional
    public void crearProveedor(Long idOrg, String nombre, Long cuit) throws MiException {

        validarDatos(idOrg, nombre, cuit);

        Proveedor proveedor = new Proveedor();

        String nombreMayusculas = nombre.toUpperCase();

        proveedor.setIdOrg(idOrg);
        proveedor.setNombre(nombreMayusculas);
        proveedor.setCuit(cuit);
        proveedor.setEstado("HABILITADO");

        proveedorRepositorio.save(proveedor);

    }
    
    @Transactional
    public void modificarProveedor(Long id, String nombre, Long cuit, String estado) throws MiException {

        Proveedor proveedor = new Proveedor();

        Optional<Proveedor> pro = proveedorRepositorio.findById(id);
        if (pro.isPresent()) {
            proveedor = pro.get();
        }
        
        validarDatosModificar(proveedor, nombre, cuit);

        String nombreMayusculas = nombre.toUpperCase();

        proveedor.setNombre(nombreMayusculas);
        proveedor.setCuit(cuit);
        proveedor.setEstado(estado);

        proveedorRepositorio.save(proveedor);

    }
    
    @Transactional
    public void eliminarProveedor(Long id) throws MiException {

        Proveedor proveedor = proveedorRepositorio.getById(id);

        OrdenDeTrabajo orden = ordenRepositorio.findTopByProveedorOrderByIdDesc(proveedor);

        if (orden == null) {

            proveedorRepositorio.deleteById(id);

        } else {

            throw new MiException("El Proveedor no puede ser eliminado, tiene OT asociada.");
        }

    }
    
    public ArrayList<Proveedor> buscarProveedoresAsc(Long idOrg) {

        ArrayList<Proveedor> lista = proveedorRepositorio.buscarProveedores(idOrg);

        Collections.sort(lista, ProveedorComparador.ordenarNombreAsc);

        return lista;

    }
    
    public ArrayList<Proveedor> buscarProveedoresHabAsc(Long idOrg) {

        ArrayList<Proveedor> lista = proveedorRepositorio.buscarProveedoresHab(idOrg);

        Collections.sort(lista, ProveedorComparador.ordenarNombreAsc);

        return lista;

    }

    public Proveedor buscarProveedor(Long id) {

        return proveedorRepositorio.getById(id);
    }

    public Long buscarUltimo(Long idOrg) {

        return proveedorRepositorio.ultimoProveedor(idOrg);

    }
    
    public void validarDatos(Long idOrg, String nombre, Long cuit) throws MiException {

        ArrayList<Proveedor> lista = proveedorRepositorio.buscarProveedores(idOrg);

        if (lista != null) {
            for (Proveedor p : lista) {
                if (p.getNombre().equalsIgnoreCase(nombre)) {
                    throw new MiException("El NOMBRE de Proveedor ya está registrado.");
                }
                if (Objects.equals(p.getCuit(), cuit)) {
                    throw new MiException("El CUIT de Proveedor ya está registrado.");
                }
            }
        }
    }

    public void validarDatosModificar(Proveedor proveedor, String nombre, Long cuit) throws MiException {

        ArrayList<Proveedor> lista = proveedorRepositorio.buscarProveedores(proveedor.getIdOrg());

            if (lista != null) {
            if (!proveedor.getNombre().equalsIgnoreCase(nombre)) {
                for (Proveedor p : lista) {
                    if (p.getNombre().equalsIgnoreCase(nombre)) {
                        throw new MiException("El NOMBRE de Proveedor ya está registrado.");
                    }
                }
            }

            if (!proveedor.getCuit().equals(cuit)) {
                for (Proveedor p : lista) {
                    if (Objects.equals(p.getCuit(), cuit)) {
                        throw new MiException("El CUIT de Proveedor ya está registrado.");
                    }
                }
            }
        }
    }
    
    
    
}
