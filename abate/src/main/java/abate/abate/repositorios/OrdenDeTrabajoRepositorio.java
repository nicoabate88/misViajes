package abate.abate.repositorios;

import abate.abate.entidades.OrdenDeTrabajo;
import abate.abate.entidades.Proveedor;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface OrdenDeTrabajoRepositorio extends JpaRepository<OrdenDeTrabajo, Long> {

    @Query("SELECT MAX(id) FROM OrdenDeTrabajo o WHERE o.idOrg = :id")
    public Long ultimaOrden(@Param("id") Long id);

    Optional<OrdenDeTrabajo> findByEstadoInAndCamionId(List<OrdenDeTrabajo.Estado> estados, Long idCamion);

    Optional<OrdenDeTrabajo> findByEstadoInAndAcopladoId(List<OrdenDeTrabajo.Estado> estados, Long idAcoplado);

    Optional<OrdenDeTrabajo> findTopByIdOrgOrderByIdDesc(Long idOrg);

    List<OrdenDeTrabajo> findByIdOrgAndEstadoIn(Long idOrg, List<OrdenDeTrabajo.Estado> estados);
    
    List<OrdenDeTrabajo> findByIdOrgAndProveedorIdAndEstadoIn(Long idOrg, Long idProveedor, List<OrdenDeTrabajo.Estado> estados);

    List<OrdenDeTrabajo> findByIdOrgAndEstado(Long idOrg, OrdenDeTrabajo.Estado estado);
    
    List<OrdenDeTrabajo> findByIdOrgAndProveedorIdAndEstado(Long idOrg, Long idProveedor, OrdenDeTrabajo.Estado estado);

    List<OrdenDeTrabajo> findByIdOrg(Long idOrg);
    
    List<OrdenDeTrabajo> findByIdOrgAndProveedorId(Long idOrg, Long idProveedor);

    List<OrdenDeTrabajo> findByCamionId(Long idCamion);
    
    List<OrdenDeTrabajo> findByCamionIdAndProveedorId(Long idCamion, Long idProveedor);

    List<OrdenDeTrabajo> findByAcopladoId(Long idAcoplado);
    
    List<OrdenDeTrabajo> findByAcopladoIdAndProveedorId(Long idAcoplado, Long idProveedor);

    List<OrdenDeTrabajo> findByCamionIdAndAcopladoId(Long idCamion, Long idAcoplado);
    
    List<OrdenDeTrabajo> findByCamionIdAndAcopladoIdAndProveedorId(Long idCamion, Long idAcoplado, Long idProveedor);

    List<OrdenDeTrabajo> findByCamionIdAndAcopladoIdAndEstadoIn(Long idCamion, Long idAcoplado, List<OrdenDeTrabajo.Estado> estados);
    
    List<OrdenDeTrabajo> findByCamionIdAndAcopladoIdAndProveedorIdAndEstadoIn(Long idCamion, Long idAcoplado, Long idProveedor, List<OrdenDeTrabajo.Estado> estados);

    List<OrdenDeTrabajo> findByCamionIdAndEstadoIn(Long idCamion, List<OrdenDeTrabajo.Estado> estados);
    
    List<OrdenDeTrabajo> findByCamionIdAndProveedorIdAndEstadoIn(Long idCamion, Long idProveedor, List<OrdenDeTrabajo.Estado> estados);

    List<OrdenDeTrabajo> findByAcopladoIdAndEstadoIn(Long idAcoplado, List<OrdenDeTrabajo.Estado> estados);
    
    List<OrdenDeTrabajo> findByAcopladoIdAndProveedorIdAndEstadoIn(Long idAcoplado, Long idProveedor, List<OrdenDeTrabajo.Estado> estados);

    List<OrdenDeTrabajo> findByCamionIdAndEstado(Long idCamion, OrdenDeTrabajo.Estado estado);
    
    List<OrdenDeTrabajo> findByCamionIdAndProveedorIdAndEstado(Long idCamion, Long idProveedor, OrdenDeTrabajo.Estado estado);

    List<OrdenDeTrabajo> findByAcopladoIdAndEstado(Long idAcoplado, OrdenDeTrabajo.Estado estado);
    
    List<OrdenDeTrabajo> findByAcopladoIdAndProveedorIdAndEstado(Long idAcoplado, Long idProveedor, OrdenDeTrabajo.Estado estado);

    List<OrdenDeTrabajo> findByCamionIdAndAcopladoIdAndEstado(Long idCamion, Long idAcoplado, OrdenDeTrabajo.Estado estado);
    
     List<OrdenDeTrabajo> findByCamionIdAndAcopladoIdAndProveedorIdAndEstado(Long idCamion, Long idAcoplado, Long idProveedor, OrdenDeTrabajo.Estado estado);
    
    OrdenDeTrabajo findTopByProveedorOrderByIdDesc(Proveedor proveedor);

}
