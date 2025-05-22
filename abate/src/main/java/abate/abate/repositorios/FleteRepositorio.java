package abate.abate.repositorios;

import abate.abate.entidades.Acoplado;
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
    
    @Query("SELECT f FROM Flete f WHERE f.fechaFlete BETWEEN :desde AND :hasta AND chofer_id = :id")
    public ArrayList<Flete> buscarFleteChofer(@Param("desde") Date desde, @Param("hasta") Date hasta, @Param("id") Long id);
    
    @Query("SELECT f FROM Flete f WHERE f.fechaFlete BETWEEN :desde AND :hasta AND camion_id = :id")
    public ArrayList<Flete> buscarFleteCamion(@Param("desde") Date desde, @Param("hasta") Date hasta, @Param("id") Long id);
    
    @Query("SELECT f FROM Flete f WHERE f.fechaFlete BETWEEN :desde AND :hasta AND acoplado_id = :id")
    public ArrayList<Flete> buscarFleteAcoplado(@Param("desde") Date desde, @Param("hasta") Date hasta, @Param("id") Long id);
    
    @Query("SELECT f FROM Flete f WHERE f.fechaFlete BETWEEN :desde AND :hasta AND cliente_id = :id")
    public ArrayList<Flete> buscarFleteCliente(@Param("desde") Date desde, @Param("hasta") Date hasta, @Param("id") Long id);
    
    @Query("SELECT f FROM Flete f WHERE f.fechaFlete BETWEEN :desde AND :hasta AND chofer_id = :idChofer AND cliente_id = :idCliente")
    public ArrayList<Flete> buscarFleteChoferCliente(@Param("desde") Date desde, @Param("hasta") Date hasta, @Param("idChofer") Long idChofer, @Param("idCliente") Long idCliente);

    @Query("SELECT f FROM Flete f WHERE f.fechaFlete BETWEEN :desde AND :hasta AND chofer_id = :idChofer AND camion_id = :idCamion")
    public ArrayList<Flete> buscarFleteChoferCamion(@Param("desde") Date desde, @Param("hasta") Date hasta, @Param("idChofer") Long idChofer, @Param("idCamion") Long idCamion);
    
    @Query("SELECT f FROM Flete f WHERE f.fechaFlete BETWEEN :desde AND :hasta AND cliente_id = :idCliente AND camion_id = :idCamion")
    public ArrayList<Flete> buscarFleteClienteCamion(@Param("desde") Date desde, @Param("hasta") Date hasta, @Param("idCliente") Long idCliente, @Param("idCamion") Long idCamion);
    
    @Query("SELECT f FROM Flete f WHERE f.fechaFlete BETWEEN :desde AND :hasta AND chofer_id = :idChofer AND cliente_id = :idCliente AND camion_id = :idCamion")
    public ArrayList<Flete> buscarFleteChoferClienteCamion(@Param("desde") Date desde, @Param("hasta") Date hasta, @Param("idChofer") Long idChofer, @Param("idCliente") Long idCliente, @Param("idCamion") Long idCamion);
    
    ArrayList<Flete> findByFechaFleteBetweenAndIdOrg(Date desde, Date hasta, Long idOrg);

    //ArrayList<Flete> findByFechaFleteBetweenAndChofer(Date desde, Date hasta, Usuario chofer);

    //ArrayList<Flete> findByFechaFleteBetweenAndCamion(Date desde, Date hasta, Camion camion);

    //ArrayList<Flete> findByFechaFleteBetweenAndCliente(Date desde, Date hasta, Cliente cliente);

    //ArrayList<Flete> findByFechaFleteBetweenAndChoferAndCliente(Date desde, Date hasta, Usuario chofer, Cliente cliente);
    
    //ArrayList<Flete> findByFechaFleteBetweenAndChoferAndCamion(Date desde, Date hasta, Usuario chofer, Camion camion);
    
    //ArrayList<Flete> findByFechaFleteBetweenAndClienteAndCamion(Date desde, Date hasta, Cliente cliente, Camion camion);
    
    //ArrayList<Flete> findByFechaFleteBetweenAndChoferAndClienteAndCamion(Date desde, Date hasta, Usuario chofer, Cliente cliente, Camion camion);

    Flete findTopByCamionOrderByIdDesc(Camion camion);
    
    Flete findTopByAcopladoOrderByIdDesc(Acoplado acoplado);
    
    Flete findTopByProductoOrderByIdDesc(Producto producto);

    Flete findTopByChoferOrderByIdDesc(Usuario chofer);

    @Query("SELECT f.id FROM Flete f WHERE gasto_id = :idGasto")
    Long findFleteIdByIdGasto(@Param("idGasto") Long idGasto);

}
