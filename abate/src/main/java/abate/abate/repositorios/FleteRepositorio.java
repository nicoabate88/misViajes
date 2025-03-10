package abate.abate.repositorios;

import abate.abate.entidades.Camion;
import abate.abate.entidades.Cliente;
import abate.abate.entidades.Flete;
import abate.abate.entidades.Producto;
import abate.abate.entidades.Usuario;
import java.util.ArrayList;
import java.util.Date;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface FleteRepositorio extends JpaRepository<Flete, Long> {

    @Query("SELECT MAX(id) FROM Flete f WHERE f.idOrg = :id")
    public Long ultimoFlete(@Param("id") Long id);

    Optional<Flete> findTopByIdOrgOrderByIdDesc(Long idOrg);

    @Query("SELECT f FROM Flete f WHERE chofer_id = :id")
    public ArrayList<Flete> buscarFletesIdChofer(@Param("id") Long id);

    @Query("SELECT f FROM Flete f WHERE cliente_id = :id")
    public ArrayList<Flete> buscarFletesIdCliente(@Param("id") Long id);

    @Query("SELECT f FROM Flete f WHERE f.estado = 'PENDIENTE' AND f.idOrg = :id")
    public ArrayList<Flete> buscarFletePendiente(@Param("id") Long id);

    @Query("SELECT f FROM Flete f WHERE imagenCP_id = :id")
    public Flete buscarFleteIdImagenCP(@Param("id") Long id);

    @Query("SELECT f FROM Flete f WHERE imagen_descarga_id = :id")
    public Flete buscarFleteIdImagenDescarga(@Param("id") Long id);

    @Query("SELECT f FROM Flete f WHERE gasto_id = :id")
    public Flete buscarFleteIdGasto(@Param("id") Long id);

    ArrayList<Flete> findByFechaFleteBetweenAndIdOrg(Date desde, Date hasta, Long idOrg);

    ArrayList<Flete> findByFechaFleteBetweenAndChofer(Date desde, Date hasta, Usuario chofer);

    ArrayList<Flete> findByFechaFleteBetweenAndCamion(Date desde, Date hasta, Camion camion);

    ArrayList<Flete> findByFechaFleteBetweenAndCliente(Date desde, Date hasta, Cliente cliente);

    ArrayList<Flete> findByFechaFleteBetweenAndChoferAndCliente(Date desde, Date hasta, Usuario chofer, Cliente cliente);

    Flete findTopByCamionOrderByIdDesc(Camion camion);
    
    Flete findTopByProductoOrderByIdDesc(Producto producto);

    Flete findTopByChoferOrderByIdDesc(Usuario chofer);

    @Query("SELECT f.id FROM Flete f WHERE gasto_id = :idGasto")
    Long findFleteIdByIdGasto(@Param("idGasto") Long idGasto);

}
