package abate.abate.servicios;

import abate.abate.entidades.Cliente;
import abate.abate.entidades.Cuenta;
import abate.abate.entidades.Transaccion;
import abate.abate.entidades.Usuario;
import abate.abate.repositorios.ClienteRepositorio;
import abate.abate.repositorios.CuentaRepositorio;
import abate.abate.repositorios.TransaccionRepositorio;
import abate.abate.repositorios.UsuarioRepositorio;
import abate.abate.util.CuentaComparador;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CuentaServicio {

    @Autowired
    private ClienteRepositorio clienteRepositorio;
    @Autowired
    private CuentaRepositorio cuentaRepositorio;
    @Autowired
    private UsuarioRepositorio usuarioRepositorio;
    @Autowired
    private TransaccionRepositorio transaccionRepositorio;

    @Transactional
    public void crearCuentaCliente(Long idCliente) {

        Cliente cliente = new Cliente();
        Optional<Cliente> cte = clienteRepositorio.findById(idCliente);
        if (cte.isPresent()) {
            cliente = cte.get();
        }

        Cuenta cuenta = new Cuenta();

        cuenta.setIdOrg(cliente.getIdOrg());
        cuenta.setCliente(cliente);
        cuenta.setSaldo(0.0);
        cuenta.setEstado("INHABILITADA");

        cuentaRepositorio.save(cuenta);

    }

    @Transactional
    public void crearCuentaChofer(Long idChofer) {

        Usuario chofer = new Usuario();
        Optional<Usuario> chof = usuarioRepositorio.findById(idChofer);
        if (chof.isPresent()) {
            chofer = chof.get();
        }

        Cuenta cuenta = new Cuenta();

        cuenta.setIdOrg(chofer.getIdOrg());
        cuenta.setChofer(chofer);
        cuenta.setSaldo(0.0);
        if(chofer.getCuenta().equalsIgnoreCase("SI")){
            cuenta.setEstado("HABILITADA");
        } else {
            cuenta.setEstado("INHABILITADA");
        }

        cuentaRepositorio.save(cuenta);

    }
    
    @Transactional
    public void habilitarCuenta(Long idUsuario) {

        Cuenta cuenta = cuentaRepositorio.buscarCuentaIdChofer(idUsuario);

        cuenta.setEstado("HABILITADA");

        cuentaRepositorio.save(cuenta);

    }

    @Transactional
    public void inhabilitarCuenta(Long idUsuario) {

        Cuenta cuenta = cuentaRepositorio.buscarCuentaIdChofer(idUsuario);

        cuenta.setEstado("INHABILITADA");

        cuentaRepositorio.save(cuenta);

    }

    @Transactional
    public void eliminarCuentaCliente(Long idCliente) {

        Long id = cuentaRepositorio.buscarIdCuentaIdCliente(idCliente);

        cuentaRepositorio.deleteById(id);

    }

    @Transactional
    public void eliminarCuentaChofer(Long idChofer) {

        Long id = cuentaRepositorio.buscarIdCuentaIdChofer(idChofer);

        cuentaRepositorio.deleteById(id);

    }

    public Cuenta buscarCuenta(Long id) {

        return cuentaRepositorio.getById(id);
    }

    public Long buscarIdCuentaCliente(Long idCliente) {

        return cuentaRepositorio.buscarIdCuentaIdCliente(idCliente);

    }

    public Long buscarIdCuentaChofer(Long idChofer) {

        return cuentaRepositorio.buscarIdCuentaIdChofer(idChofer);

    }

    public Cuenta buscarCuentaChofer(Long idChofer) {

        return cuentaRepositorio.buscarCuentaIdChofer(idChofer);

    }

    public Cuenta buscarCuentaCliente(Long idCliente) {

        return cuentaRepositorio.buscarCuentaIdCliente(idCliente);

    }

    public ArrayList<Cuenta> buscarCuentasChofer(Long idOrg) {

        ArrayList<Cuenta> lista = new ArrayList();

        lista = (ArrayList<Cuenta>) cuentaRepositorio.buscarCuentasChofer(idOrg);

        Collections.sort(lista, CuentaComparador.ordenarNombreChoferAsc);

        return lista;
    }
    
    public ArrayList<Cuenta> buscarCuentasChoferHab(Long idOrg) {

        ArrayList<Cuenta> lista = new ArrayList();

        lista = (ArrayList<Cuenta>) cuentaRepositorio.buscarCuentasChofer(idOrg);

        Collections.sort(lista, CuentaComparador.ordenarNombreChoferAsc);

        return lista;
    }

    public ArrayList<Cuenta> buscarCuentasCliente(Long idOrg) {

        ArrayList<Cuenta> lista = new ArrayList();

        lista = (ArrayList<Cuenta>) cuentaRepositorio.buscarCuentasCliente(idOrg);

        Collections.sort(lista, CuentaComparador.ordenarNombreClienteAsc);

        return lista;
    }

    @Transactional
    public void agregarTransaccionCuentaCliente(Long idTransaccion) {

        Double saldo = 0.0;

        Transaccion transaccion = new Transaccion();
        Optional<Transaccion> tran = transaccionRepositorio.findById(idTransaccion);
        if (tran.isPresent()) {
            transaccion = tran.get();
        }

        Long idCliente = transaccion.getCliente().getId();
        Cuenta cuenta = cuentaRepositorio.buscarCuentaIdCliente(idCliente);

        List<Transaccion> transacciones = cuenta.getTransaccion();
        transacciones.add(transaccion);
        cuenta.setTransaccion(transacciones);

        for (Transaccion t : transacciones) {
            saldo = saldo + t.getImporte();
        }

        double saldoRed = Math.round(saldo * 100.0) / 100.0;

        cuenta.setSaldo(saldoRed);

        cuentaRepositorio.save(cuenta);

    }

    @Transactional
    public void agregarTransaccionCuentaChofer(Transaccion transaccion) {

        Double saldo = 0.0;

        Long idChofer = transaccion.getChofer().getId();
        Cuenta cuenta = cuentaRepositorio.buscarCuentaIdChofer(idChofer);

        List<Transaccion> transacciones = cuenta.getTransaccion();
        transacciones.add(transaccion);
        cuenta.setTransaccion(transacciones);

        for (Transaccion t : transacciones) {
            saldo = saldo + t.getImporte();
        }

        double saldoRed = Math.round(saldo * 100.0) / 100.0;

        cuenta.setSaldo(saldoRed);

        cuentaRepositorio.save(cuenta);

    }

    @Transactional
    public void modificarTransaccionCuentaCliente(Transaccion transaccion) {

        Double saldo = 0.0;
        Long idCliente = transaccion.getCliente().getId();
        Cuenta cuenta = cuentaRepositorio.buscarCuentaIdCliente(idCliente);

        List<Transaccion> transacciones = cuenta.getTransaccion();
        for (Transaccion t : transacciones) {
            if (t.getId() == transaccion.getId()) {
                t.setFecha(transaccion.getFecha());
                t.setImporte(transaccion.getImporte());
            }
        }
        cuenta.setTransaccion(transacciones);

        for (Transaccion tr : transacciones) {
            saldo = saldo + tr.getImporte();
        }

        double saldoRed = Math.round(saldo * 100.0) / 100.0;

        cuenta.setSaldo(saldoRed);

        cuentaRepositorio.save(cuenta);

    }

    @Transactional
    public void modificarTransaccionCuentaChofer(Transaccion transaccion) {

        Double saldo = 0.0;
        Long idChofer = transaccion.getChofer().getId();
        Cuenta cuenta = cuentaRepositorio.buscarCuentaIdChofer(idChofer);

        List<Transaccion> transacciones = cuenta.getTransaccion();
        for (Transaccion t : transacciones) {
            if (t.getId() == transaccion.getId()) {
                t.setFecha(transaccion.getFecha());
                t.setImporte(transaccion.getImporte());
            }
        }
        cuenta.setTransaccion(transacciones);

        for (Transaccion tr : transacciones) {
            saldo = saldo + tr.getImporte();
        }

        double saldoRed = Math.round(saldo * 100.0) / 100.0;

        cuenta.setSaldo(saldoRed);

        cuentaRepositorio.save(cuenta);

    }

    @Transactional
    public void eliminarTransaccionCuentaCliente(Transaccion transaccion) {

        Double saldo = 0.0;
        Long idCliente = transaccion.getCliente().getId();

        transaccion.setCliente(null);
        transaccion.setFlete(null);
        transaccion.setRecibo(null);
        transaccionRepositorio.save(transaccion);

        Cuenta cuenta = cuentaRepositorio.buscarCuentaIdCliente(idCliente);
        cuenta.getTransaccion().remove(transaccion);
        List<Transaccion> transacciones = cuenta.getTransaccion();

        for (Transaccion tr : transacciones) {
            saldo = saldo + tr.getImporte();
        }

        double saldoRed = Math.round(saldo * 100.0) / 100.0;

        cuenta.setSaldo(saldoRed);

        cuentaRepositorio.save(cuenta);

        transaccionRepositorio.deleteById(transaccion.getId());

    }

    @Transactional
    public void eliminarTransaccionCuentaChofer(Transaccion transaccion) {

        Double saldo = 0.0;
        Long idChofer = transaccion.getChofer().getId();

        transaccion.setChofer(null);
        transaccion.setFlete(null);
        transaccion.setGasto(null);
        transaccion.setEntrega(null);
        transaccion.setRecibo(null);
        transaccion.setIngreso(null);

        transaccionRepositorio.save(transaccion);

        Cuenta cuenta = cuentaRepositorio.buscarCuentaIdChofer(idChofer);
        cuenta.getTransaccion().remove(transaccion);

        List<Transaccion> transacciones = cuenta.getTransaccion();

        for (Transaccion tr : transacciones) {
            saldo = saldo + tr.getImporte();
        }

        double saldoRed = Math.round(saldo * 100.0) / 100.0;

        cuenta.setSaldo(saldoRed);

        cuentaRepositorio.save(cuenta);

        transaccionRepositorio.deleteById(transaccion.getId());

    }

}
