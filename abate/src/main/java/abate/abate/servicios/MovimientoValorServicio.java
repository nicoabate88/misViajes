package abate.abate.servicios;

import abate.abate.entidades.Cheque;
import abate.abate.entidades.Cheque.EstadoCheque;
import abate.abate.entidades.CuentaBancaria;
import abate.abate.entidades.MovimientoValor;
import abate.abate.entidades.MovimientoValor.TipoMovimientoValor;
import abate.abate.entidades.TitularCheque;
import abate.abate.repositorios.MovimientoValorRepositorio;
import java.math.BigDecimal;
import java.util.ArrayList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MovimientoValorServicio {

    @Autowired
    private MovimientoValorRepositorio movimientoValorRepositorio;

    public void generarMovimiento(Long idOrg, CuentaBancaria cuenta, Cheque cheque,
            TipoMovimientoValor tipo, BigDecimal importe, BigDecimal saldoAnterior,
            BigDecimal saldoPosterior, String descripcion) {

        MovimientoValor movimiento = new MovimientoValor();

        movimiento.setIdOrg(idOrg);
        movimiento.setCuentaBancaria(cuenta);
        movimiento.setCheque(cheque);
        movimiento.setTipoMovimiento(tipo);
        movimiento.setImporte(importe);
        movimiento.setSaldoAnterior(saldoAnterior);
        movimiento.setSaldoPosterior(saldoPosterior);
        movimiento.setDescripcion(descripcion);

        movimientoValorRepositorio.save(movimiento);

    }

    public ArrayList<MovimientoValor> buscarMovimientosCuenta(CuentaBancaria cuentaBancaria) {

        ArrayList<MovimientoValor> lista = movimientoValorRepositorio.findByCuentaBancariaOrderByFechaDesc(cuentaBancaria);

        return lista;

    }
    
    public MovimientoValor buscarMovimientoVentaCheque(Cheque cheque) {

        MovimientoValor movimiento = movimientoValorRepositorio.findByChequeAndTipoMovimiento(cheque, TipoMovimientoValor.VENTA_CHEQUE);

        return movimiento;

    }
    
    public ArrayList<MovimientoValor> buscarVentasPendientesPorCuentaYTitular(CuentaBancaria cuenta, TitularCheque titular) {

    return movimientoValorRepositorio
            .findByCuentaBancariaAndTipoMovimientoAndChequeEstadoAndChequeTitularEmisorOrderByFechaDesc(
                    cuenta,
                    TipoMovimientoValor.VENTA_CHEQUE,
                    EstadoCheque.VENDIDO,
                    titular
            );
}

}
