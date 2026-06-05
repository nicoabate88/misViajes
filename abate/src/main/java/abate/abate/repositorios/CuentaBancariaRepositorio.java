
package abate.abate.repositorios;

import abate.abate.entidades.CuentaBancaria;
import abate.abate.entidades.CuentaBancaria.EstadoCuentaBancaria;
import java.util.ArrayList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CuentaBancariaRepositorio extends JpaRepository<CuentaBancaria, Long> {

    ArrayList<CuentaBancaria> findByIdOrgOrderByBancoAsc(Long idOrg);

    ArrayList<CuentaBancaria> findByIdOrgAndEstadoOrderByBancoAsc(Long idOrg, EstadoCuentaBancaria estado);
    
    ArrayList<CuentaBancaria> findByIdOrgOrderByLimiteOperativoDesc(Long idOrg);

    ArrayList<CuentaBancaria> findByIdOrgOrderBySaldoDisponibleDesc(Long idOrg);
    
    ArrayList<CuentaBancaria> findByIdOrgAndEstadoOrderByLimiteOperativoDesc(Long idOrg, EstadoCuentaBancaria estado);

    ArrayList<CuentaBancaria> findByIdOrgAndEstadoOrderBySaldoDisponibleDesc(Long idOrg, EstadoCuentaBancaria estado);
    
}
