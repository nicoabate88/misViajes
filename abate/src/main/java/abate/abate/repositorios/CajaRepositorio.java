package abate.abate.repositorios;

import abate.abate.entidades.Caja;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CajaRepositorio extends JpaRepository<Caja, Long> {

    @Query("SELECT c FROM Caja c WHERE chofer_id = :id")
    public Caja buscarCajaIdChofer(@Param("id") Long id);

}
