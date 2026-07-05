package abate.abate.servicios;

import abate.abate.entidades.Banco;
import abate.abate.entidades.Cheque;
import abate.abate.entidades.Cheque.EstadoCheque;
import abate.abate.entidades.CuentaBancaria;
import abate.abate.entidades.MovimientoValor;
import abate.abate.entidades.MovimientoValor.TipoMovimientoValor;
import abate.abate.entidades.TitularCheque;
import abate.abate.repositorios.BancoRepositorio;
import abate.abate.repositorios.ChequeRepositorio;
import abate.abate.repositorios.CuentaBancariaRepositorio;
import abate.abate.repositorios.MovimientoValorRepositorio;
import abate.abate.repositorios.TitularChequeRepositorio;
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
    private TitularChequeRepositorio titularRepositorio;

    @Autowired
    private CuentaBancariaRepositorio cuentaBancariaRepositorio;

    @Autowired
    private MovimientoValorRepositorio movimientoValorRepositorio;

    @Transactional
    public void registrarCheque(Long idOrg, String numeroCheque, Long idBanco, Long idTitular, BigDecimal importe,
            Date fechaEmision, Date fechaVencimiento, Date fechaAcreditacion, String observacion) throws Exception {

        if (numeroCheque == null || numeroCheque.trim().isEmpty()) {
            throw new Exception("Debe ingresar número de cheque.");
        }
        
        if (fechaAcreditacion.before(fechaVencimiento)) { 
            throw new Exception("La fecha de acreditación no puede ser anterior a la fecha de vencimiento.");
        }

        Banco banco = bancoRepositorio.getById(idBanco);

        if (banco == null) {
            throw new Exception("Banco no encontrado.");
        }
        
        TitularCheque titular = titularRepositorio.getById(idTitular);
        
        if (titular == null) {
            throw new Exception("Titular Cheque no encontrado.");
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
        
        cheque.setTitularEmisor(titular);

        cheque.setImporte(importe);

        cheque.setFechaEmision(fechaEmision);

        cheque.setFechaVencimiento(fechaVencimiento);
        
        cheque.setFechaAcreditacion(fechaAcreditacion);

        cheque.setObservacion(observacion);

        cheque.setEstado(EstadoCheque.EN_CARTERA);

        chequeRepositorio.save(cheque);

    }

    @Transactional
    public void actualizarCheque(Long idCheque, String numeroCheque, Long idBanco,
            Long idTitular, BigDecimal importe, Date fechaEmision, Date fechaVencimiento, Date fechaAcreditacion,
            String observacion, EstadoCheque estado) throws Exception {
        
        if (fechaAcreditacion.before(fechaVencimiento)) { 
            throw new Exception("La fecha de acreditación no puede ser anterior a la fecha de vencimiento.");
        }

        Cheque cheque = chequeRepositorio.getById(idCheque);

        if (cheque == null) {
            throw new Exception("No se encontró el cheque");
        }

        // VALIDACION DE ESTADO
        if (cheque.getEstado().equals(EstadoCheque.ACREDITADO) || cheque.getEstado().equals(EstadoCheque.VENDIDO)) {
            throw new Exception("Solo se pueden modificar cheques en cartera");
        }

        // VALIDACION ESTADOS PERMITIDOS
        if (!estado.equals(EstadoCheque.EN_CARTERA)
                && !estado.equals(EstadoCheque.ANULADO)) {

            throw new Exception("Estado inválido");
        }

        // VALIDACION BANCO
        if(cheque.getBancoEmisor().getId() != idBanco){
        Banco banco = bancoRepositorio.getById(idBanco);
        if (banco == null) {
            throw new Exception("Debe seleccionar un banco válido");
        } else {
            cheque.setBancoEmisor(banco);
        }
        }
        
        if(cheque.getTitularEmisor().getId() != idTitular){
        TitularCheque titular = titularRepositorio.getById(idTitular);
        
        if (titular == null) {
            throw new Exception("Titular Cheque no encontrado.");
        } else {
            cheque.setTitularEmisor(titular);
        }
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

        cheque.setImporte(importe);

        cheque.setFechaEmision(fechaEmision);

        cheque.setFechaVencimiento(fechaVencimiento);
        
        cheque.setFechaAcreditacion(fechaAcreditacion);

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
    public void anularVentaCheque(Long idCheque) throws Exception {

    Optional<Cheque> respuestaCheque = chequeRepositorio.findById(idCheque);

    if (!respuestaCheque.isPresent()) {
        throw new Exception("Cheque no encontrado.");
    }

    Cheque cheque = respuestaCheque.get();

    if (cheque.getEstado() != EstadoCheque.VENDIDO) {
        throw new Exception("El cheque Nº " + cheque.getNumeroCheque() + " no se encuentra vendido.");
    }

    if (cheque.getCuentaBancaria() == null) {
        throw new Exception("El cheque no posee una cuenta bancaria asociada.");
    }

    CuentaBancaria cuenta = cheque.getCuentaBancaria();

    BigDecimal saldoAnterior = cuenta.getSaldoDisponible();

    BigDecimal nuevoSaldo = saldoAnterior.add(cheque.getImporte());

    cuenta.setSaldoDisponible(nuevoSaldo);

    cuentaBancariaRepositorio.save(cuenta);

    cheque.setEstado(EstadoCheque.EN_CARTERA);
    cheque.setFechaVenta(null);
    cheque.setCuentaBancaria(null);

    chequeRepositorio.save(cheque);
    
    MovimientoValor movimientoModificar = movimientoValorRepositorio.findByChequeAndTipoMovimiento(cheque, TipoMovimientoValor.VENTA_CHEQUE);
    
    if(movimientoModificar != null){
        movimientoModificar.setTipoMovimiento(TipoMovimientoValor.ANULACION_VENTA_CHEQUE);
        
        movimientoValorRepositorio.save(movimientoModificar);
    }
    
    //crear nuevo movimiento anulado
    MovimientoValor movimiento = new MovimientoValor();

    movimiento.setIdOrg(cheque.getIdOrg());
    movimiento.setCuentaBancaria(cuenta);
    movimiento.setCheque(cheque);
    movimiento.setTipoMovimiento(TipoMovimientoValor.ANULACION_VENTA_CHEQUE);
    movimiento.setImporte(cheque.getImporte());
    movimiento.setSaldoAnterior(saldoAnterior);
    movimiento.setSaldoPosterior(nuevoSaldo);
    movimiento.setDescripcion("Anulación venta cheque Nº " + cheque.getNumeroCheque());

    movimientoValorRepositorio.save(movimiento);
    
    
   
    }
    
    @Transactional
    public void acreditarChequesVencidos() throws Exception {

    ArrayList<Cheque> lista = chequeRepositorio.findByEstadoAndFechaAcreditacionLessThanEqual(EstadoCheque.VENDIDO, new Date());

    for (Cheque cheque : lista) {

        CuentaBancaria cuenta = cheque.getCuentaBancaria();

        if (cuenta == null) {
            continue;
        }

        if (cheque.getFechaAcreditacion() == null) {
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

        movimiento.setDescripcion(
                "Liberación automática cheque Nº " 
                + cheque.getNumeroCheque()
                + " - Fecha acreditación: "
                + cheque.getFechaAcreditacion()
        );

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
