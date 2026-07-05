
package abate.abate.repositorios;

import abate.abate.entidades.TitularCheque;
import abate.abate.entidades.TitularCheque.EstadoTitular;
import java.util.ArrayList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TitularChequeRepositorio extends JpaRepository<TitularCheque, Long> {
    
    ArrayList<TitularCheque> findByIdOrg(Long idOrg);
    
    ArrayList<TitularCheque> findByIdOrgOrderByNombreAsc(Long idOrg);

    ArrayList<TitularCheque> findByIdOrgAndEstadoOrderByNombreAsc(Long idOrg, EstadoTitular estado);
    
}
