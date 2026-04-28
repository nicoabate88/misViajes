package abate.abate.servicios;

import abate.abate.entidades.Cliente;
import abate.abate.entidades.ClienteEstadistica;
import abate.abate.entidades.ClientesEstadistica;
import abate.abate.entidades.Flete;
import abate.abate.entidades.Transaccion;
import abate.abate.excepciones.MiException;
import abate.abate.repositorios.ClienteRepositorio;
import abate.abate.repositorios.FleteRepositorio;
import abate.abate.repositorios.TransaccionRepositorio;
import abate.abate.util.ClienteComparador;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
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
    @Autowired
    private FleteRepositorio fleteRepositorio;

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

    public Map<Cliente, ClientesEstadistica> estadisticaClientes(String desde, String hasta, Long idOrg) throws ParseException {

        Date d = convertirFecha(desde);
        Date h = convertirFecha(hasta);

        List<Flete> fletes = fleteRepositorio.findByFechaFleteBetweenAndIdOrg(d, h, idOrg);

        Map<Cliente, ClientesEstadistica> estadisticasPorCliente = new HashMap<>();
        ClientesEstadistica totalGeneral = new ClientesEstadistica();

        // Pre-cargar todos los clientes
        ArrayList<Cliente> lista = clienteRepositorio.buscarClientesHab(idOrg);
        for (Cliente cliente : lista) {
            estadisticasPorCliente.put(cliente, new ClientesEstadistica());

        }

        for (Flete flete : fletes) {
            Cliente cliente = flete.getCliente();
            estadisticasPorCliente.putIfAbsent(cliente, new ClientesEstadistica());
            ClientesEstadistica resumen = estadisticasPorCliente.get(cliente);

            resumen.setFlete(resumen.getFlete() + 1);
            resumen.setNeto(resumen.getNeto() + flete.getNeto());
            resumen.setKg(resumen.getKg() + flete.getKgFlete());
            resumen.setKm(resumen.getKm() + flete.getKmFlete());
            if (resumen.getKg() > 0) {
                resumen.setTarifa(resumen.getNeto() / resumen.getKg() * 1000);
                totalGeneral.setTarifa(totalGeneral.getNeto() / totalGeneral.getKg() * 1000);
            } else {
                resumen.setTarifa(0.0);
                totalGeneral.setTarifa(0.0);
            }
            if (resumen.getKm() > 0) {
                resumen.setRentabilidad(resumen.getNeto() / resumen.getKm());
                totalGeneral.setRentabilidad(totalGeneral.getNeto() / totalGeneral.getKm());
            } else {
                resumen.setRentabilidad(0.0);
                totalGeneral.setRentabilidad(0.0);
            }

            totalGeneral.setFlete(totalGeneral.getFlete() + 1);
            totalGeneral.setNeto(totalGeneral.getNeto() + flete.getNeto());
            totalGeneral.setKg(totalGeneral.getKg() + flete.getKgFlete());
            totalGeneral.setKm(totalGeneral.getKm() + flete.getKmFlete());

        }

        Cliente totalKey = new Cliente();
        totalKey.setNombre("TOTAL");
        estadisticasPorCliente.put(totalKey, totalGeneral);
        // Ordenar el mapa por el dominio del camión
        return estadisticasPorCliente.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByKey(Comparator.comparing(Cliente::getNombre)))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));

    }

    public ArrayList<ClienteEstadistica> estadisticaCliente(String desde, String hasta, Long idCliente) throws ParseException {

        Date d = convertirFecha(desde);
        Date h = convertirFecha(hasta);

        ArrayList<Flete> fletes = fleteRepositorio.buscarFleteCliente(d, h, idCliente);

        Map<String, ClienteEstadistica> resumenMap = new HashMap<>();

        int totalFletes = 0;
        int totalKm = 0;
        double totalKg = 0;
        int totalTarifa = 0;
        int totalNeto = 0;

        for (Flete flete : fletes) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(flete.getFechaFlete());
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH) + 1;

            String key = year + "-" + month;

            if (resumenMap.containsKey(key)) {
                ClienteEstadistica resumen = resumenMap.get(key);
                resumen.setFlete(resumen.getFlete() + 1);
                resumen.setNeto(resumen.getNeto() + flete.getNeto());
                resumen.setKm(resumen.getKm() + flete.getKmFlete());
                resumen.setKg(resumen.getKg() + flete.getKgFlete());
                if (resumen.getKg() > 0) {
                    resumen.setTarifa(resumen.getNeto() / resumen.getKg() * 1000);
                } else {
                    resumen.setTarifa(0.0);
                }
                if (resumen.getKm() > 0) {
                    resumen.setRentabilidad(resumen.getNeto() / resumen.getKm());
                } else {
                    resumen.setRentabilidad(0.0);
                }
            } else {
                ClienteEstadistica nuevoResumen = new ClienteEstadistica(year, month, 1, flete.getNeto(), flete.getKmFlete(), flete.getKgFlete());
                resumenMap.put(key, nuevoResumen);
            }

            totalFletes++;
            totalNeto += flete.getNeto();
            totalKg += flete.getKgFlete();
            totalKm += flete.getKmFlete();
        }

        ArrayList<ClienteEstadistica> resultado = new ArrayList<>(resumenMap.values());

        ClienteEstadistica totalGeneral = new ClienteEstadistica(0, 0, totalFletes);
        totalGeneral.setFlete(totalFletes);
        totalGeneral.setKm(totalKm);
        totalGeneral.setKg(totalKg);
        totalGeneral.setTarifa(totalTarifa);
        totalGeneral.setNeto(totalNeto);
        if (totalKg > 0) {
            totalGeneral.setTarifa(totalGeneral.getNeto() / totalGeneral.getKg() * 1000);
        }
        if (totalKm > 0) {
            totalGeneral.setRentabilidad(totalNeto / totalKm);
        }

        resultado.add(totalGeneral);

        return resultado;

    }

    public Date convertirFecha(String fecha) throws ParseException { //convierte fecha String a fecha Date
        SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd");
        return formato.parse(fecha);
    }

}
