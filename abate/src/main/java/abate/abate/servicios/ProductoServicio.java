
package abate.abate.servicios;

import abate.abate.entidades.Flete;
import abate.abate.entidades.Producto;
import abate.abate.excepciones.MiException;
import abate.abate.repositorios.FleteRepositorio;
import abate.abate.repositorios.ProductoRepositorio;
import abate.abate.util.ProductoComparador;
import java.util.ArrayList;
import java.util.Collections;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProductoServicio {
    
    @Autowired
    private ProductoRepositorio productoRepositorio;
    @Autowired
    private FleteRepositorio fleteRepositorio;
    
    @Transactional
    public void crearProducto(Long idOrg, String nombre, String estado) throws MiException {

        validarDatos(idOrg, nombre);

        Producto producto = new Producto();

        String nombreMayusculas = nombre.toUpperCase();

        producto.setIdOrg(idOrg);
        producto.setNombre(nombreMayusculas);
        producto.setEstado(estado);

        productoRepositorio.save(producto);

    }
    
    @Transactional
    public void modificarProducto(Long id, String nombre, String estado) throws MiException {

        Producto producto = productoRepositorio.getById(id);
        
        validarDatosModificar(producto, nombre);

        String nombreMayusculas = nombre.toUpperCase();

        producto.setNombre(nombreMayusculas);
        producto.setEstado(estado);

        productoRepositorio.save(producto);

    }

    @Transactional
    public void eliminarProducto(Long id) throws MiException {

        Producto producto = productoRepositorio.getById(id);

        Flete flete = fleteRepositorio.findTopByProductoOrderByIdDesc(producto);

        if (flete == null) {

            productoRepositorio.deleteById(id);

        } else {

            throw new MiException("El Producto no puede ser eliminado, existen Viajes asociado.");
        }

    }

    public ArrayList<Producto> buscarProductosAsc(Long idOrg) {

        ArrayList<Producto> lista = productoRepositorio.buscarProductos(idOrg);

        Collections.sort(lista, ProductoComparador.ordenarNombreAsc);

        return lista;

    }
    
    public ArrayList<Producto> buscarProductosHabAsc(Long idOrg) {

        ArrayList<Producto> lista = productoRepositorio.buscarProductosHab(idOrg);

        Collections.sort(lista, ProductoComparador.ordenarNombreAsc);

        return lista;

    }

    public Producto buscarProducto(Long id) {

        return productoRepositorio.getById(id);
    }

    public Long buscarUltimo(Long idOrg) {

        return productoRepositorio.ultimoProducto(idOrg);

    }
    
    public void validarDatos(Long idOrg, String nombre) throws MiException {

        ArrayList<Producto> lista = productoRepositorio.buscarProductos(idOrg);

        for (Producto p : lista) {
            if (p.getNombre().equalsIgnoreCase(nombre)) {
                throw new MiException("El NOMBRE de Producto ya está registrado.");
            }
        }
    }

    public void validarDatosModificar(Producto producto, String nombre) throws MiException {

        ArrayList<Producto> lista = productoRepositorio.buscarProductos(producto.getIdOrg());

        if (!producto.getNombre().equalsIgnoreCase(nombre)) {
            for (Producto p : lista) {
                if (p.getNombre().equalsIgnoreCase(nombre)) {
                    throw new MiException("El NOMBRE de Producto ya está registrado.");
                }
            }
        }
    }
    
}
