package abate.abate.servicios;

import abate.abate.entidades.Banco;
import abate.abate.entidades.Cheque;
import abate.abate.entidades.Cheque.EstadoCheque;
import abate.abate.entidades.CuentaBancaria;
import abate.abate.entidades.MovimientoValor;
import abate.abate.entidades.MovimientoValor.TipoMovimientoValor;
import abate.abate.repositorios.BancoRepositorio;
import abate.abate.repositorios.ChequeRepositorio;
import abate.abate.repositorios.CuentaBancariaRepositorio;
import abate.abate.repositorios.MovimientoValorRepositorio;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ChequeServicio {

    @Autowired
    private ChequeRepositorio chequeRepositorio;

    @Autowired
    private BancoRepositorio bancoRepositorio;

    @Autowired
    private CuentaBancariaRepositorio cuentaBancariaRepositorio;

    @Autowired
    private MovimientoValorRepositorio movimientoValorRepositorio;

    @Transactional
    public void registrarCheque(Long idOrg, String numeroCheque,
            Long idBanco, String titular, BigDecimal importe,
            Date fechaEmision, Date fechaVencimiento, String observacion) throws Exception {

        if (numeroCheque == null || numeroCheque.trim().isEmpty()) {
            throw new Exception("Debe ingresar número de cheque.");
        }

        Banco banco = bancoRepositorio.getById(idBanco);

        if (banco == null) {
            throw new Exception("Banco no encontrado.");
        }

        if (importe == null || importe.compareTo(BigDecimal.ZERO) <= 0) {
            throw new Exception("Debe ingresar importe válido.");
        }

        Boolean existe = chequeRepositorio.existsByBancoEmisorAndNumeroCheque(banco, numeroCheque);

        if (existe) {
            throw new Exception("Ya existe un cheque con ese número para el banco seleccionado.");
        }

        Cheque cheque = new Cheque();

        cheque.setIdOrg(idOrg);

        cheque.setNumeroCheque(numeroCheque);

        cheque.setBancoEmisor(banco);

        cheque.setTitular(titular.toUpperCase());

        cheque.setImporte(importe);

        cheque.setFechaEmision(fechaEmision);

        cheque.setFechaVencimiento(fechaVencimiento);

        cheque.setObservacion(observacion);

        cheque.setEstado(EstadoCheque.EN_CARTERA);

        chequeRepositorio.save(cheque);

    }

    @Transactional
    public void actualizarCheque(Long idCheque, String numeroCheque, Long idBanco,
            String titular, BigDecimal importe, Date fechaEmision, Date fechaVencimiento,
            String observacion, EstadoCheque estado) throws Exception {

        Cheque cheque = chequeRepositorio.getById(idCheque);

        if (cheque == null) {
            throw new Exception("No se encontró el cheque");
        }

        // VALIDACION DE ESTADO
        if (!cheque.getEstado().equals(EstadoCheque.EN_CARTERA)) {
            throw new Exception("Solo se pueden modificar cheques en cartera");
        }

        // VALIDACION ESTADOS PERMITIDOS
        if (!estado.equals(EstadoCheque.EN_CARTERA)
                && !estado.equals(EstadoCheque.ANULADO)) {

            throw new Exception("Estado inválido");
        }

        // VALIDACION BANCO
        Banco banco = bancoRepositorio.getById(idBanco);

        if (banco == null) {
            throw new Exception("Debe seleccionar un banco válido");
        }

        // VALIDACIONES BASICAS
        if (numeroCheque == null || numeroCheque.isEmpty()) {
            throw new Exception("Debe ingresar número de cheque");
        }

        if (importe == null || importe.compareTo(BigDecimal.ZERO) <= 0) {
            throw new Exception("Debe ingresar un importe válido");
        }

        if (fechaEmision == null) {
            throw new Exception("Debe ingresar fecha de emisión");
        }

        if (fechaVencimiento == null) {
            throw new Exception("Debe ingresar fecha de vencimiento");
        }

        // ACTUALIZACION
        cheque.setNumeroCheque(numeroCheque);

        cheque.setBancoEmisor(banco);

        cheque.setTitular(titular);

        cheque.setImporte(importe);

        cheque.setFechaEmision(fechaEmision);

        cheque.setFechaVencimiento(fechaVencimiento);

        cheque.setObservacion(observacion);

        cheque.setEstado(estado);

        chequeRepositorio.save(cheque);

    }

    public Cheque buscarPorId(Long id) {

        return chequeRepositorio.getById(id);

    }

    public ArrayList<Cheque> buscarChequesPorEstado(Long idOrg, EstadoCheque estado) {

        return chequeRepositorio.findByIdOrgAndEstadoOrderByFechaVencimientoAsc(idOrg, estado);

    }

    @Transactional
    public void venderCheques(List<Long> idsCheques, Long idCuentaBancaria) throws Exception {

        if (idsCheques == null || idsCheques.isEmpty()) {
            throw new Exception("Debe seleccionar al menos un cheque.");
        }

        Optional<CuentaBancaria> respuestaCuenta = cuentaBancariaRepositorio.findById(idCuentaBancaria);

        if (!respuestaCuenta.isPresent()) {
            throw new Exception("Cuenta bancaria no encontrada.");
        }

        CuentaBancaria cuenta = respuestaCuenta.get();

        for (Long idCheque : idsCheques) {

            Optional<Cheque> respuestaCheque = chequeRepositorio.findById(idCheque);

            if (!respuestaCheque.isPresent()) {
                throw new Exception("Cheque no encontrado.");
            }

            Cheque cheque = respuestaCheque.get();

            if (cheque.getEstado() != EstadoCheque.EN_CARTERA) {
                throw new Exception("El cheque Nº " + cheque.getNumeroCheque() + " no se encuentra en cartera.");
            }

            BigDecimal saldoAnterior = cuenta.getSaldoDisponible();

            BigDecimal nuevoSaldo = saldoAnterior.subtract(cheque.getImporte());

            cuenta.setSaldoDisponible(nuevoSaldo);

            cuentaBancariaRepositorio.save(cuenta);

            cheque.setEstado(EstadoCheque.VENDIDO);
            cheque.setFechaVenta(new Date());
            cheque.setCuentaBancaria(cuenta);

            chequeRepositorio.save(cheque);

            MovimientoValor movimiento = new MovimientoValor();

            movimiento.setIdOrg(cheque.getIdOrg());
            movimiento.setCuentaBancaria(cuenta);
            movimiento.setCheque(cheque);
            movimiento.setTipoMovimiento(TipoMovimientoValor.VENTA_CHEQUE);
            movimiento.setImporte(cheque.getImporte());
            movimiento.setSaldoAnterior(saldoAnterior);
            movimiento.setSaldoPosterior(nuevoSaldo);
            movimiento.setDescripcion("Venta de cheque Nº " + cheque.getNumeroCheque());

            movimientoValorRepositorio.save(movimiento);
        }
    }
    
    @Transactional
    public void acreditarChequesVencidos() throws Exception {

    ArrayList<Cheque> lista = chequeRepositorio.findByEstadoAndFechaVencimientoLessThanEqual(EstadoCheque.VENDIDO, new Date());

    for (Cheque cheque : lista) {

        CuentaBancaria cuenta = cheque.getCuentaBancaria();

        if (cuenta == null) {
            continue;
        }

        BigDecimal saldoAnterior = cuenta.getSaldoDisponible();

        BigDecimal nuevoSaldo = saldoAnterior.add(cheque.getImporte());

        cuenta.setSaldoDisponible(nuevoSaldo);

        cuentaBancariaRepositorio.save(cuenta);

        cheque.setEstado(EstadoCheque.ACREDITADO);

        chequeRepositorio.save(cheque);

        MovimientoValor movimiento = new MovimientoValor();

        movimiento.setIdOrg(cheque.getIdOrg());

        movimiento.setCuentaBancaria(cuenta);

        movimiento.setCheque(cheque);

        movimiento.setTipoMovimiento(TipoMovimientoValor.LIBERACION_CHEQUE);

        movimiento.setImporte(cheque.getImporte());

        movimiento.setSaldoAnterior(saldoAnterior);

        movimiento.setSaldoPosterior(nuevoSaldo);

        movimiento.setDescripcion("Liberación automática cheque Nº " + cheque.getNumeroCheque());

        movimientoValorRepositorio.save(movimiento);

    }

}
    
    public ArrayList<Cheque> buscarChequesPorNumero(Long idOrg, String numeroCheque) {
    
    return chequeRepositorio.buscarPorNumero(
        idOrg,
        numeroCheque
);

}

}
