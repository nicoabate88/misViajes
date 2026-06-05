
package abate.abate.repositorios;

import abate.abate.entidades.Cheque;
import abate.abate.entidades.CuentaBancaria;
import abate.abate.entidades.MovimientoValor;
import java.util.ArrayList;
import java.util.Date;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MovimientoValorRepositorio extends JpaRepository<MovimientoValor, Long> {

    ArrayList<MovimientoValor> findByCuentaBancariaOrderByFechaDesc(CuentaBancaria cuentaBancaria);

    ArrayList<MovimientoValor> findByChequeOrderByFechaDesc(Cheque cheque);

    ArrayList<MovimientoValor> findByFechaBetween(Date desde, Date hasta);
    
}
