package abate.abate.repositorios;

import abate.abate.entidades.Entrega;
import abate.abate.entidades.Usuario;
import java.util.ArrayList;
import java.util.Date;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface EntregaRepositorio extends JpaRepository<Entrega, Long> {

    @Query("SELECT MAX(id) FROM Entrega e WHERE e.idOrg = :id")
    public Long ultimoEntrega(@Param("id") Long id);

    Optional<Entrega> findTopByIdOrgOrderByIdDesc(Long idOrg);

    @Query("SELECT e FROM Entrega e WHERE chofer_id = :id")
    public ArrayList<Entrega> buscarEntregasIdChofer(@Param("id") Long id);

    @Query("SELECT e FROM Entrega e WHERE e.idOrg = :id")
    public ArrayList<Entrega> buscarEntregas(@Param("id") Long id);

    Entrega findTopByChoferOrderByIdDesc(Usuario chofer);

    ArrayList<Entrega> findByFechaBetweenAndIdOrg(Date desde, Date hasta, Long idOrg);

    ArrayList<Entrega> findByFechaBetweenAndChoferId(Date desde, Date hasta, Long idChofer);

}
