package abate.abate.repositorios;

import abate.abate.entidades.Camion;
import abate.abate.entidades.Gasto;
import abate.abate.entidades.Usuario;
import java.util.ArrayList;
import java.util.Date;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface GastoRepositorio extends JpaRepository<Gasto, Long> {

    @Query("SELECT g FROM Gasto g WHERE chofer_id = :id")
    public ArrayList<Gasto> buscarGastoIdChofer(@Param("id") Long id);

    @Query("SELECT MAX(g) FROM Gasto g WHERE g.idOrg = :id")
    public Gasto buscarUltimoGasto(@Param("id") Long id);

    @Query("SELECT g FROM Gasto g WHERE imagen_id = :id")
    public Gasto buscarGastoIdImagen(@Param("id") Long id);

    @Query("SELECT MAX(id) FROM Gasto g WHERE g.idOrg = :id")
    public Long ultimoGasto(@Param("id") Long id);

    Optional<Gasto> findTopByIdOrgOrderByIdGastoDesc(Long idOrg);

    Gasto findTopByCamionOrderByIdDesc(Camion camion); //devuelve ultimo Gasto registrado de camion especifico

    Gasto findTopByChoferOrderByIdDesc(Usuario chofer); //devuelve ultimo Gasto registrado de chofer especifico

    ArrayList<Gasto> findByFechaBetweenAndCamionId(Date desde, Date hasta, Long idCamion);
    
    ArrayList<Gasto> findByFechaBetweenAndChoferId(Date desde, Date hasta, Long idChofer);

    ArrayList<Gasto> findByFechaBetweenAndIdOrg(Date desde, Date hasta, Long idOrg);

}
