
package abate.abate.repositorios;

import abate.abate.entidades.Documentacion;
import abate.abate.entidades.TipoDocumentacion;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface DocumentacionRepositorio extends JpaRepository<Documentacion, Long> {
    
    Documentacion findTopByTipoDocumentacionOrderByIdDesc(TipoDocumentacion tipo);
    
    @Query("SELECT MAX(id) FROM Documentacion d WHERE d.idOrg = :id")
    public Long ultimoDocumento(@Param("id") Long id);
    
    Optional<Documentacion> findByTipoDocumentacionIdAndCamionId(Long tipoDocumentacionId, Long camionId);
    
    Optional<Documentacion> findByTipoDocumentacionIdAndAcopladoId(Long tipoDocumentacionId, Long acopladoId);
    
    Optional<Documentacion> findByTipoDocumentacionIdAndChoferId(Long tipoDocumentacionId, Long choferId);
    
    @Query("SELECT d FROM Documentacion d WHERE chofer_id = :id AND d.estado = 'VIGENTE'")
    public List<Documentacion> buscarDocumentacionIdChofer(@Param("id") Long id);
    
    @Query("SELECT d FROM Documentacion d WHERE chofer_id = :id AND d.estado = 'INHABILITADO'")
    public List<Documentacion> buscarDocumentacionInhabilitadoIdChofer(@Param("id") Long id);
    
    @Query("SELECT d FROM Documentacion d WHERE camion_id = :id AND d.estado = 'VIGENTE'")
    public List<Documentacion> buscarDocumentacionIdCamion(@Param("id") Long id);
    
    @Query("SELECT d FROM Documentacion d WHERE camion_id = :id AND d.estado = 'INHABILITADO'")
    public List<Documentacion> buscarDocumentacionInhabilitadoIdCamion(@Param("id") Long id);
    
    @Query("SELECT d FROM Documentacion d WHERE acoplado_id = :id AND d.estado = 'VIGENTE'")
    public List<Documentacion> buscarDocumentacionIdAcoplado(@Param("id") Long id);
    
    @Query("SELECT d FROM Documentacion d WHERE acoplado_id = :id AND d.estado = 'INHABILITADO'")
    public List<Documentacion> buscarDocumentacionInhabilitadoIdAcoplado(@Param("id") Long id);
    
    List<Documentacion> findByCamionIsNotNullAndIdOrgAndEstado(Long idOrg, String estado);
    
    List<Documentacion> findByAcopladoIsNotNullAndIdOrgAndEstado(Long idOrg, String estado);
    
    List<Documentacion> findByChoferIsNotNullAndIdOrgAndEstado(Long idOrg, String estado);
    
    @Query("SELECT d FROM Documentacion d WHERE imagen_id = :id")
    public Documentacion buscarDocumentacionIdImagen(@Param("id") Long id);
    
    @Query("SELECT d FROM Documentacion d WHERE d.idOrg = :id AND d.estado = 'VIGENTE' AND d.fechaVencimiento <= :fechaLimite")
    List<Documentacion> findDocumentacionesPorVencer(@Param("id") Long id, @Param("fechaLimite") Date fechaLimite);
    
    @Query("SELECT d FROM Documentacion d WHERE d.idOrg = :idOrg AND d.estado = 'VIGENTE' AND camion_id != null AND tipo_documentacion_id = :idTipo")
    List<Documentacion> buscarDocumentacionCamionesPorTipo(@Param("idOrg") Long idOrg, @Param("idTipo") Long idTipo);
    
    @Query("SELECT d FROM Documentacion d WHERE camion_id = :id AND tipo_documentacion_id = :idTipo")
    List<Documentacion> buscarDocumentacionCamionPorTipo(@Param("id") Long id, @Param("idTipo") Long idTipo);
    
    @Query("SELECT d FROM Documentacion d WHERE d.idOrg = :idOrg AND d.estado = 'VIGENTE' AND acoplado_id != null AND tipo_documentacion_id = :idTipo")
    List<Documentacion> buscarDocumentacionAcopladosPorTipo(@Param("idOrg") Long idOrg, @Param("idTipo") Long idTipo);
    
    @Query("SELECT d FROM Documentacion d WHERE acoplado_id = :id AND tipo_documentacion_id = :idTipo")
    List<Documentacion> buscarDocumentacionAcopladoPorTipo(@Param("id") Long id, @Param("idTipo") Long idTipo);
    
    @Query("SELECT d FROM Documentacion d WHERE d.idOrg = :idOrg AND d.estado = 'VIGENTE' AND chofer_id != null AND tipo_documentacion_id = :idTipo")
    List<Documentacion> buscarDocumentacionChoferesPorTipo(@Param("idOrg") Long idOrg, @Param("idTipo") Long idTipo);
    
    @Query("SELECT d FROM Documentacion d WHERE chofer_id = :id AND tipo_documentacion_id = :idTipo")
    List<Documentacion> buscarDocumentacionChoferPorTipo(@Param("id") Long id, @Param("idTipo") Long idTipo);
    
    @Query("SELECT d FROM Documentacion d WHERE camion_id = :id AND tipo_documentacion_id = :idTipo")
    Optional<Documentacion> buscarDocumentacionCamion(@Param("id") Long id, @Param("idTipo") Long idTipo);
    
    @Query("SELECT d FROM Documentacion d WHERE acoplado_id = :id AND tipo_documentacion_id = :idTipo")
    Optional<Documentacion> buscarDocumentacionAcoplado(@Param("id") Long id, @Param("idTipo") Long idTipo);
    
    @Query("SELECT d FROM Documentacion d WHERE chofer_id = :id AND tipo_documentacion_id = :idTipo")
    Optional<Documentacion> buscarDocumentacionChofer(@Param("id") Long id, @Param("idTipo") Long idTipo);

    boolean existsByCamionIdAndEstado(Long camionId, String estado);
    
    boolean existsByAcopladoIdAndEstado(Long acopladoId, String estado);
    
    boolean existsByChoferIdAndEstado(Long choferId, String estado);
    
    @Query("SELECT d FROM Documentacion d WHERE d.idOrg = :id AND d.estado = 'VIGENTE' AND d.fechaVencimiento <= :fechaLimite AND ((:idCamion IS NULL) OR (:idCamion = 0L AND d.camion IS NULL) OR (:idCamion > 0 AND d.camion.id = :idCamion)) AND ((:idAcoplado IS NULL) OR (:idAcoplado = 0L AND d.acoplado IS NULL) OR (:idAcoplado > 0 AND d.acoplado.id = :idAcoplado)) AND ((:idChofer IS NULL) OR (:idChofer = 0L AND d.chofer IS NULL) OR (:idChofer > 0 AND d.chofer.id = :idChofer))")
    List<Documentacion> findDocumentacionesPorVencerFiltrado(@Param("id") Long id, @Param("fechaLimite") Date fechaLimite, @Param("idCamion") Long idCamion, @Param("idAcoplado") Long idAcoplado, @Param("idChofer") Long idChofer);
    
    @Query("SELECT d FROM Documentacion d WHERE camion_id = :id AND d.estado = 'VIGENTE' AND d.fechaVencimiento <= :fechaLimite")
    List<Documentacion> findDocumentacionesPorVencerCamion(@Param("id") Long id, @Param("fechaLimite") Date fechaLimite);
    
    @Query("SELECT d FROM Documentacion d WHERE acoplado_id = :id AND d.estado = 'VIGENTE' AND d.fechaVencimiento <= :fechaLimite")
    List<Documentacion> findDocumentacionesPorVencerAcoplado(@Param("id") Long id, @Param("fechaLimite") Date fechaLimite);
    
    @Query("SELECT d FROM Documentacion d WHERE chofer_id = :id AND d.estado = 'VIGENTE' AND d.fechaVencimiento <= :fechaLimite")
    List<Documentacion> findDocumentacionesPorVencerChofer(@Param("id") Long id, @Param("fechaLimite") Date fechaLimite);

    
}
