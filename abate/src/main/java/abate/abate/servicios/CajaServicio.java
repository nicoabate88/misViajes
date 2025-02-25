package abate.abate.servicios;

import abate.abate.entidades.Caja;
import abate.abate.entidades.Transaccion;
import abate.abate.entidades.Usuario;
import abate.abate.repositorios.CajaRepositorio;
import abate.abate.repositorios.TransaccionRepositorio;
import abate.abate.repositorios.UsuarioRepositorio;
import java.util.List;
import java.util.Optional;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CajaServicio {

    @Autowired
    private UsuarioRepositorio usuarioRepositorio;
    @Autowired
    private CajaRepositorio cajaRepositorio;
    @Autowired
    private TransaccionRepositorio transaccionRepositorio;

    @Transactional
    public void crearCajaChofer(Long idChofer) {

        Usuario chofer = new Usuario();
        Optional<Usuario> chof = usuarioRepositorio.findById(idChofer);
        if (chof.isPresent()) {
            chofer = chof.get();
        }

        Caja caja = new Caja();

        caja.setIdOrg(chofer.getIdOrg());
        caja.setChofer(chofer);
        caja.setSaldo(0.0);
        if(chofer.getCaja().equalsIgnoreCase("SI")){
        caja.setEstado("HABILITADA");
        } else {
            caja.setEstado("INHABILITADA");
        }

        cajaRepositorio.save(caja);

    }

    @Transactional
    public void habilitarCaja(Long idUsuario) {

        Caja caja = cajaRepositorio.buscarCajaIdChofer(idUsuario);

        caja.setEstado("HABILITADA");

        cajaRepositorio.save(caja);

    }

    @Transactional
    public void inhabilitarCaja(Long idUsuario) {

        Caja caja = cajaRepositorio.buscarCajaIdChofer(idUsuario);

        caja.setEstado("INHABILITADA");

        cajaRepositorio.save(caja);

    }

    @Transactional
    public void agregarTransaccionCajaChofer(Long idTransaccion) {

        Double saldo = 0.0;

        Transaccion transaccion = new Transaccion();
        Optional<Transaccion> tran = transaccionRepositorio.findById(idTransaccion);
        if (tran.isPresent()) {
            transaccion = tran.get();
        }

        Long idChofer = transaccion.getChofer().getId();
        Caja caja = cajaRepositorio.buscarCajaIdChofer(idChofer);

        List<Transaccion> transacciones = caja.getTransaccion();
        transacciones.add(transaccion);
        caja.setTransaccion(transacciones);

        for (Transaccion t : transacciones) {
            saldo = saldo + t.getImporte();
        }

        double saldoRed = Math.round(saldo * 100.0) / 100.0;

        caja.setSaldo(saldoRed);

        cajaRepositorio.save(caja);

    }

    @Transactional
    public void modificarTransaccionCajaChofer(Transaccion transaccion) {

        Double saldo = 0.0;
        Long idChofer = transaccion.getChofer().getId();
        Caja caja = cajaRepositorio.buscarCajaIdChofer(idChofer);

        List<Transaccion> transacciones = caja.getTransaccion();
        for (Transaccion t : transacciones) {
            if (t.getId() == transaccion.getId()) {
                t.setFecha(transaccion.getFecha());
                t.setImporte(transaccion.getImporte());
            }
        }
        caja.setTransaccion(transacciones);

        for (Transaccion tr : transacciones) {
            saldo = saldo + tr.getImporte();
        }

        double saldoRed = Math.round(saldo * 100.0) / 100.0;

        caja.setSaldo(saldoRed);

        cajaRepositorio.save(caja);

    }

    @Transactional
    public void eliminarTransaccionCajaChofer(Transaccion transaccion) {

        Double saldo = 0.0;
        Long idChofer = transaccion.getChofer().getId();

        transaccion.setChofer(null);
        transaccion.setGasto(null);
        transaccion.setIngreso(null);
        transaccionRepositorio.save(transaccion);

        Caja caja = cajaRepositorio.buscarCajaIdChofer(idChofer);
        caja.getTransaccion().remove(transaccion);

        List<Transaccion> transacciones = caja.getTransaccion();

        for (Transaccion tr : transacciones) {
            saldo = saldo + tr.getImporte();
        }

        double saldoRed = Math.round(saldo * 100.0) / 100.0;

        caja.setSaldo(saldoRed);

        cajaRepositorio.save(caja);

        transaccionRepositorio.deleteById(transaccion.getId());

    }

    public Caja buscarCajaChofer(Long idChofer) {

        return cajaRepositorio.buscarCajaIdChofer(idChofer);

    }

    public Caja buscarCaja(Long idCaja) {

        return cajaRepositorio.getById(idCaja);

    }

}
