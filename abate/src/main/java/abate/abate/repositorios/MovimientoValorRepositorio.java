
package abate.abate.repositorios;

import abate.abate.entidades.Cheque;
import abate.abate.entidades.Cheque.EstadoCheque;
import abate.abate.entidades.CuentaBancaria;
import abate.abate.entidades.MovimientoValor;
import abate.abate.entidades.MovimientoValor.TipoMovimientoValor;
import abate.abate.entidades.TitularCheque;
import java.util.ArrayList;
import java.util.Date;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MovimientoValorRepositorio extends JpaRepository<MovimientoValor, Long> {

    ArrayList<MovimientoValor> findByCuentaBancariaOrderByFechaDesc(CuentaBancaria cuentaBancaria);

    ArrayList<MovimientoValor> findByChequeOrderByFechaDesc(Cheque cheque);
    
    MovimientoValor findByChequeAndTipoMovimiento(Cheque cheque, TipoMovimientoValor tipoMovimiento);

    ArrayList<MovimientoValor> findByFechaBetween(Date desde, Date hasta);
    
    ArrayList<MovimientoValor> findByCuentaBancariaAndTipoMovimientoAndChequeEstadoAndChequeTitularEmisorOrderByFechaDesc(
        CuentaBancaria cuentaBancaria, TipoMovimientoValor tipoMovimiento, EstadoCheque estado, TitularCheque titularEmisor);
    
}
