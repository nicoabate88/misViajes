package abate.abate.repositorios;

import abate.abate.entidades.Recibo;
import java.util.ArrayList;
import java.util.Date;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ReciboRepositorio extends JpaRepository<Recibo, Long> {

    @Query("SELECT MAX(id) FROM Recibo WHERE idOrg = :id")
    public Long ultimoRecibo(@Param("id") Long id);

    Optional<Recibo> findTopByIdOrgOrderByIdDesc(Long idOrg);

    @Query("SELECT r FROM Recibo r WHERE cliente_id = :id")
    public ArrayList<Recibo> buscarRecibosIdCliente(@Param("id") Long id);

    @Query("SELECT r FROM Recibo r WHERE r.idOrg = :id")
    public ArrayList<Recibo> buscarRecibos(@Param("id") Long id);

    ArrayList<Recibo> findByFechaBetweenAndIdOrg(Date desde, Date hasta, Long idOrg);

    ArrayList<Recibo> findByFechaBetweenAndClienteId(Date desde, Date hasta, Long idCliente);

}
