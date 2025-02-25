package abate.abate.repositorios;

import abate.abate.entidades.Camion;
import java.util.ArrayList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CamionRepositorio extends JpaRepository<Camion, Long> {

    @Query("SELECT MAX(id) FROM Camion c WHERE c.idOrg = :id")
    public Long ultimoCamion(@Param("id") Long id);

    @Query("SELECT c FROM Camion c WHERE c.idOrg = :id")
    public ArrayList<Camion> buscarCamiones(@Param("id") Long id);

}
