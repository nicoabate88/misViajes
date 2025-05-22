package abate.abate.repositorios;

import abate.abate.entidades.Azul;
import abate.abate.entidades.Camion;
import abate.abate.entidades.Usuario;
import java.util.ArrayList;
import java.util.Date;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AzulRepositorio extends JpaRepository<Azul, Long> {
    
    @Query("SELECT MAX(id) FROM Azul a WHERE a.idOrg = :id")
    public Long ultimaCarga(@Param("id") Long id);

    @Query("SELECT MAX(a) FROM Azul a WHERE a.idOrg = :id")
    public Azul ultimaCargaAzul(@Param("id") Long id);

    ArrayList<Azul> findByFechaBetweenAndCamion(Date desde, Date hasta, Camion camion);

    ArrayList<Azul> findByFechaBetweenAndChofer(Date desde, Date hasta, Usuario chofer);

    ArrayList<Azul> findByFechaBetween(Date desde, Date hasta);

    @Query("SELECT a.camion FROM Azul a WHERE a.id = :idCarga")
    Camion findCamionByCargaId(@Param("idCarga") Long idCarga);

    Azul findTopByCamionOrderByIdDesc(Camion camion);

}
