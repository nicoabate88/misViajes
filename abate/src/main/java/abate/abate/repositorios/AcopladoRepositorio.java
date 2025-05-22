
package abate.abate.repositorios;

import abate.abate.entidades.Acoplado;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AcopladoRepositorio extends JpaRepository<Acoplado, Long> {
    
    @Query("SELECT MAX(id) FROM Acoplado a WHERE a.idOrg = :id")
    public Long ultimoIdAcoplado(@Param("id") Long id);
    
    @Query("SELECT MAX(a) FROM Acoplado a WHERE a.idOrg = :id")
    public Acoplado ultimoAcoplado(@Param("id") Long id);

    @Query("SELECT a FROM Acoplado a WHERE a.idOrg = :id")
    public List<Acoplado> buscarAcoplados(@Param("id") Long id);
    
}
