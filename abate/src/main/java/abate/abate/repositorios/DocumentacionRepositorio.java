
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
    
    @Query("SELECT d FROM Documentacion d WHERE chofer_id = :id")
    public List<Documentacion> buscarDocumentacionIdChofer(@Param("id") Long id);
    
    @Query("SELECT d FROM Documentacion d WHERE camion_id = :id")
    public List<Documentacion> buscarDocumentacionIdCamion(@Param("id") Long id);
    
    @Query("SELECT d FROM Documentacion d WHERE acoplado_id = :id")
    public List<Documentacion> buscarDocumentacionIdAcoplado(@Param("id") Long id);
    
    List<Documentacion> findByCamionIsNotNullAndIdOrg(Long idOrg);
    
    List<Documentacion> findByAcopladoIsNotNullAndIdOrg(Long idOrg);
    
    List<Documentacion> findByChoferIsNotNullAndIdOrg(Long idOrg);
    
    @Query("SELECT d FROM Documentacion d WHERE imagen_id = :id")
    public Documentacion buscarDocumentacionIdImagen(@Param("id") Long id);
    
    @Query("SELECT d FROM Documentacion d WHERE d.idOrg = :id AND d.fechaVencimiento <= :fechaLimite")
    List<Documentacion> findDocumentacionesPorVencer(@Param("id") Long id, @Param("fechaLimite") Date fechaLimite);
    
    @Query("SELECT d FROM Documentacion d WHERE d.idOrg = :idOrg AND camion_id != null AND tipo_documentacion_id = :idTipo")
    List<Documentacion> buscarDocumentacionCamionesPorTipo(@Param("idOrg") Long idOrg, @Param("idTipo") Long idTipo);
    
    @Query("SELECT d FROM Documentacion d WHERE camion_id = :id AND tipo_documentacion_id = :idTipo")
    List<Documentacion> buscarDocumentacionCamionPorTipo(@Param("id") Long id, @Param("idTipo") Long idTipo);
    
    @Query("SELECT d FROM Documentacion d WHERE d.idOrg = :idOrg AND acoplado_id != null AND tipo_documentacion_id = :idTipo")
    List<Documentacion> buscarDocumentacionAcopladosPorTipo(@Param("idOrg") Long idOrg, @Param("idTipo") Long idTipo);
    
    @Query("SELECT d FROM Documentacion d WHERE acoplado_id = :id AND tipo_documentacion_id = :idTipo")
    List<Documentacion> buscarDocumentacionAcopladoPorTipo(@Param("id") Long id, @Param("idTipo") Long idTipo);
    
    @Query("SELECT d FROM Documentacion d WHERE d.idOrg = :idOrg AND chofer_id != null AND tipo_documentacion_id = :idTipo")
    List<Documentacion> buscarDocumentacionChoferesPorTipo(@Param("idOrg") Long idOrg, @Param("idTipo") Long idTipo);
    
    @Query("SELECT d FROM Documentacion d WHERE chofer_id = :id AND tipo_documentacion_id = :idTipo")
    List<Documentacion> buscarDocumentacionChoferPorTipo(@Param("id") Long id, @Param("idTipo") Long idTipo);

    
}
