package abate.abate.servicios;

import abate.abate.entidades.Cliente;
import abate.abate.entidades.Transaccion;
import abate.abate.excepciones.MiException;
import abate.abate.repositorios.ClienteRepositorio;
import abate.abate.repositorios.TransaccionRepositorio;
import abate.abate.util.ClienteComparador;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;
import java.util.Optional;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ClienteServicio {

    @Autowired
    private ClienteRepositorio clienteRepositorio;
    @Autowired
    private TransaccionRepositorio transaccionRepositorio;
    @Autowired
    private CuentaServicio cuentaServicio;

    @Transactional
    public void crearCliente(Long idOrg, String nombre, Long cuit, String localidad, String direccion, Long telefono, String email, String estado) throws MiException {

        validarDatos(idOrg, nombre, cuit);

        Cliente cliente = new Cliente();

        String nombreMayusculas = nombre.toUpperCase();
        String localidadMayusculas = localidad.toUpperCase();
        String direccionMayusculas = direccion.toUpperCase();

        cliente.setIdOrg(idOrg);
        cliente.setNombre(nombreMayusculas);
        cliente.setCuit(cuit);
        cliente.setLocalidad(localidadMayusculas);
        cliente.setDireccion(direccionMayusculas);
        cliente.setTelefono(telefono);
        cliente.setEmail(email);
        cliente.setEstado(estado);

        clienteRepositorio.save(cliente);

        cuentaServicio.crearCuentaCliente(buscarUltimo(idOrg));

    }

    @Transactional
    public void modificarCliente(Long id, String nombre, Long cuit, String localidad, String direccion, Long telefono, String email, String estado) throws MiException {

        Cliente cliente = new Cliente();

        Optional<Cliente> cte = clienteRepositorio.findById(id);
        if (cte.isPresent()) {
            cliente = cte.get();
        }
        validarDatosModificar(cliente, nombre, cuit);

        String nombreMayusculas = nombre.toUpperCase();
        String localidadMayusculas = localidad.toUpperCase();
        String direccionMayusculas = direccion.toUpperCase();

        cliente.setNombre(nombreMayusculas);
        cliente.setCuit(cuit);
        cliente.setLocalidad(localidadMayusculas);
        cliente.setDireccion(direccionMayusculas);
        cliente.setTelefono(telefono);
        cliente.setEmail(email);
        cliente.setEstado(estado);

        clienteRepositorio.save(cliente);

    }

    @Transactional
    public void eliminarCliente(Long id) throws MiException {

        Cliente cliente = clienteRepositorio.getById(id);

        Transaccion transaccion = transaccionRepositorio.findTopByClienteOrderByIdDesc(cliente);

        if (transaccion == null) {

            clienteRepositorio.deleteById(id);

            cuentaServicio.eliminarCuentaCliente(id);

        } else {

            throw new MiException("El Cliente no puede ser eliminado, tiene Viaje y/o Recibo asociado.");
        }

    }

    public Cliente buscarCliente(Long id) {

        return clienteRepositorio.getById(id);
    }

    public ArrayList<Cliente> buscarClientesNombreAsc(Long idOrg) {

        ArrayList<Cliente> lista = clienteRepositorio.buscarClientes(idOrg);

        Collections.sort(lista, ClienteComparador.ordenarNombreAsc); //ordena por nombre alfabetico los nombres de clientes

        return lista;

    }
    
    public ArrayList<Cliente> buscarClientesHabNombreAsc(Long idOrg) {

        ArrayList<Cliente> lista = clienteRepositorio.buscarClientesHab(idOrg);

        Collections.sort(lista, ClienteComparador.ordenarNombreAsc); //ordena por nombre alfabetico los nombres de clientes

        return lista;

    }

    public Long buscarUltimo(Long idOrg) {

        return clienteRepositorio.ultimoCliente(idOrg);

    }

    public void validarDatos(Long idOrg, String nombre, Long cuit) throws MiException {

        ArrayList<Cliente> lista = clienteRepositorio.buscarClientes(idOrg);

        if (lista != null) {
            for (Cliente c : lista) {
                if (c.getNombre().equalsIgnoreCase(nombre)) {
                    throw new MiException("El NOMBRE de Cliente ya está registrado.");
                }
                if (Objects.equals(c.getCuit(), cuit)) {
                    throw new MiException("El CUIT de Cliente ya está registrado.");
                }
            }
        }
    }

    public void validarDatosModificar(Cliente cliente, String nombre, Long cuit) throws MiException {

        ArrayList<Cliente> lista = clienteRepositorio.buscarClientes(cliente.getIdOrg());

        if (lista != null) {
            if (!cliente.getNombre().equalsIgnoreCase(nombre)) {
                for (Cliente c : lista) {
                    if (c.getNombre().equalsIgnoreCase(nombre)) {
                        throw new MiException("El NOMBRE de Cliente ya está registrado.");
                    }
                }
            }

            if (!cliente.getCuit().equals(cuit)) {
                for (Cliente c : lista) {
                    if (Objects.equals(c.getCuit(), cuit)) {
                        throw new MiException("El CUIT de Cliente ya está registrado.");
                    }
                }
            }
        }

    }

}
