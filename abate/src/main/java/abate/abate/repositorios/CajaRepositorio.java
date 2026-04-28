package abate.abate.repositorios;

import abate.abate.entidades.Caja;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CajaRepositorio extends JpaRepository<Caja, Long> {

    @Query("SELECT c FROM Caja c WHERE chofer_id = :id")
    public Caja buscarCajaIdChofer(@Param("id") Long id);
    
    @Query("SELECT c FROM Caja c WHERE c.idOrg = :id")
    public List<Caja> buscarCajas(@Param("id") Long id);
    
    @Query("SELECT c FROM Caja c WHERE c.idOrg = :idOrg AND c.chofer.estado = 'HABILITADO' AND c.chofer.caja = 'SI'")
    List<Caja> buscarCajasChoferHabilitados(@Param("idOrg") Long idOrg);

}
