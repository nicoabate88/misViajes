
package abate.abate.repositorios;

import abate.abate.entidades.Banco;
import abate.abate.entidades.Banco.EstadoBanco;
import java.util.ArrayList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BancoRepositorio extends JpaRepository<Banco, Long> {
    
    ArrayList<Banco> findByIdOrg(Long idOrg);

    ArrayList<Banco> findByIdOrgAndEstadoOrderByNombreAsc(Long idOrg, EstadoBanco estado);
    
}
