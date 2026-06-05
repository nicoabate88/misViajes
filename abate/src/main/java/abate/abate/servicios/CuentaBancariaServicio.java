package abate.abate.servicios;

import abate.abate.entidades.CuentaBancaria;
import abate.abate.entidades.CuentaBancaria.EstadoCuentaBancaria;
import abate.abate.entidades.MovimientoValor;
import abate.abate.entidades.MovimientoValor.TipoMovimientoValor;
import abate.abate.excepciones.MiException;
import abate.abate.repositorios.CuentaBancariaRepositorio;
import abate.abate.repositorios.MovimientoValorRepositorio;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Optional;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CuentaBancariaServicio {

    @Autowired
    private CuentaBancariaRepositorio cuentaBancariaRepositorio;

    @Autowired
    private MovimientoValorRepositorio movimientoValorRepositorio;

    public CuentaBancaria buscarPorId(Long id) throws Exception {

        return cuentaBancariaRepositorio.getById(id);

    }

    public ArrayList<CuentaBancaria> buscarCuentasBancarias(Long idOrg, String orden) {

        if ("limite".equals(orden)) {
            return cuentaBancariaRepositorio.findByIdOrgOrderByLimiteOperativoDesc(idOrg);
        }

        if ("saldo".equals(orden)) {
            return cuentaBancariaRepositorio.findByIdOrgOrderBySaldoDisponibleDesc(idOrg);
        }

        return cuentaBancariaRepositorio.findByIdOrgOrderByBancoAsc(idOrg);

    }

    public ArrayList<CuentaBancaria> buscarCuentasBancariasEstado(Long idOrg, String orden, String estado) {

        EstadoCuentaBancaria estadoCuenta = EstadoCuentaBancaria.HABILITADA;
        if (estado.equalsIgnoreCase("INHABILITADA")) {
            estadoCuenta = EstadoCuentaBancaria.INHABILITADA;
        }

        if ("limite".equals(orden)) {
            return cuentaBancariaRepositorio.findByIdOrgAndEstadoOrderByLimiteOperativoDesc(idOrg, estadoCuenta);
        }

        if ("saldo".equals(orden)) {
            return cuentaBancariaRepositorio.findByIdOrgAndEstadoOrderBySaldoDisponibleDesc(idOrg, estadoCuenta);
        }

        return cuentaBancariaRepositorio.findByIdOrgAndEstadoOrderByBancoAsc(idOrg, estadoCuenta);

    }

    public ArrayList<CuentaBancaria> buscarCuentasBancariasHabilitadas(Long idOrg) {

        ArrayList<CuentaBancaria> lista = cuentaBancariaRepositorio.findByIdOrgAndEstadoOrderByBancoAsc(idOrg, CuentaBancaria.EstadoCuentaBancaria.HABILITADA);

        return lista;

    }

    @Transactional
    public void registrarCuentaBancaria(CuentaBancaria cuenta) {

        cuentaBancariaRepositorio.save(cuenta);

    }

    public void disminuirSaldo(CuentaBancaria cuenta, BigDecimal importe) {

        BigDecimal nuevoSaldo = cuenta.getSaldoDisponible().subtract(importe);

        cuenta.setSaldoDisponible(nuevoSaldo);

        cuentaBancariaRepositorio.save(cuenta);

    }

    public void aumentarSaldo(CuentaBancaria cuenta, BigDecimal importe) {

        BigDecimal nuevoSaldo = cuenta.getSaldoDisponible().add(importe);

        cuenta.setSaldoDisponible(nuevoSaldo);

        cuentaBancariaRepositorio.save(cuenta);

    }

    @Transactional
    public void modificarCuentaBancaria(Long id, String banco, String numeroCuenta,
            BigDecimal nuevoLimiteOperativo, EstadoCuentaBancaria estado) throws Exception {

        Optional<CuentaBancaria> respuesta = cuentaBancariaRepositorio.findById(id);

        if (!respuesta.isPresent()) {
            throw new Exception("Cuenta bancaria no encontrada.");
        }

        if (nuevoLimiteOperativo == null || nuevoLimiteOperativo.compareTo(BigDecimal.ZERO) < 0) {
            throw new Exception("El límite operativo no puede ser menor a cero.");
        }

        CuentaBancaria cuenta = respuesta.get();

        validarDatosModificar(cuenta, banco, numeroCuenta);

        BigDecimal limiteAnterior = cuenta.getLimiteOperativo();
        BigDecimal saldoAnterior = cuenta.getSaldoDisponible();

        BigDecimal diferencia = nuevoLimiteOperativo.subtract(limiteAnterior);

        BigDecimal saldoPosterior = saldoAnterior.add(diferencia);

        cuenta.setBanco(banco.toUpperCase());
        cuenta.setNumeroCuenta(numeroCuenta.toUpperCase());
        cuenta.setLimiteOperativo(nuevoLimiteOperativo);
        cuenta.setSaldoDisponible(saldoPosterior);
        cuenta.setEstado(estado);

        cuentaBancariaRepositorio.save(cuenta);

        if (diferencia.compareTo(BigDecimal.ZERO) != 0) {

            MovimientoValor movimiento = new MovimientoValor();

            movimiento.setIdOrg(cuenta.getIdOrg());
            movimiento.setCuentaBancaria(cuenta);
            movimiento.setCheque(null);
            movimiento.setTipoMovimiento(TipoMovimientoValor.MODIFICACION_LIMITE_OPERATIVO);
            movimiento.setImporte(diferencia.abs());
            movimiento.setSaldoAnterior(saldoAnterior);
            movimiento.setSaldoPosterior(saldoPosterior);

            movimiento.setDescripcion(
                    "Modificación de límite operativo. Límite anterior: $ "
                    + limiteAnterior
                    + " - Nuevo límite: $ "
                    + nuevoLimiteOperativo
            );

            movimientoValorRepositorio.save(movimiento);
        }
    }

    public void validarDatos(Long idOrg, String banco, String cuenta) throws MiException {

        ArrayList<CuentaBancaria> lista = cuentaBancariaRepositorio.findByIdOrgOrderByBancoAsc(idOrg);

        for (CuentaBancaria c : lista) {
            if (c.getBanco().equalsIgnoreCase(banco) && c.getNumeroCuenta().equalsIgnoreCase(cuenta)) {
                throw new MiException("La cuenta '" + banco + "' '" + cuenta + "'  ya está registrado.");
            }
        }
    }

    public void validarDatosModificar(CuentaBancaria cuenta, String nombreBanco, String numeroCuenta) throws MiException {

        ArrayList<CuentaBancaria> lista = cuentaBancariaRepositorio.findByIdOrgOrderByBancoAsc(cuenta.getIdOrg());

        if (!cuenta.getBanco().equalsIgnoreCase(nombreBanco) && !cuenta.getNumeroCuenta().equalsIgnoreCase(numeroCuenta)) {
            for (CuentaBancaria c : lista) {
                if (c.getBanco().equalsIgnoreCase(nombreBanco) && c.getNumeroCuenta().equalsIgnoreCase(numeroCuenta)) {
                    throw new MiException("La cuenta '" + nombreBanco + "' '" + numeroCuenta + "'  ya está registrado.");
                }
            }
        }
    }

}
