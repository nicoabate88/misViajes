package abate.abate.repositorios;

import abate.abate.entidades.Mantenimiento;
import abate.abate.entidades.TipoMantenimiento;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MantenimientoRepositorio extends JpaRepository<Mantenimiento, Long> {

    Mantenimiento findTopByTipoMantenimientoAndIdOrgOrderByIdDesc(TipoMantenimiento tipo, Long idOrg);

    @Query("SELECT MAX(id) FROM Mantenimiento m WHERE m.idOrg = :id")
    public Long ultimoMantenimiento(@Param("id") Long id);

    @Query("SELECT m FROM Mantenimiento m WHERE camion_id = :id AND m.estado = :estado")
    public List<Mantenimiento> buscarMantenimientoVigenteIdCamion(@Param("id") Long id, @Param("estado") Mantenimiento.Estado estado);

    @Query("SELECT m FROM Mantenimiento m WHERE m.camion.id = :id AND m.estado = :estado AND m.tipoMantenimiento.clase = :clase")
    List<Mantenimiento> buscarMantenimientoVigenteIdCamion(@Param("id") Long id, @Param("estado") Mantenimiento.Estado estado, @Param("clase") TipoMantenimiento.Clase clase);
    
    @Query("SELECT m FROM Mantenimiento m WHERE m.estado = :estado AND m.camion.id = :idCamion AND m.tipoMantenimiento.id = :idTipo AND (m.ordenDeTrabajo IS NULL OR m.ordenDeTrabajo.id <> :idOt)")
    Optional<Mantenimiento> buscarMantenimientoVigenteCamionOt(@Param("estado") Mantenimiento.Estado estado, @Param("idCamion") Long idCamion, @Param("idTipo") Long idTipo, @Param("idOt") Long idOt);

    @Query("SELECT m FROM Mantenimiento m WHERE acoplado_id = :id AND m.estado = :estado")
    public List<Mantenimiento> buscarMantenimientoVigenteIdAcoplado(@Param("id") Long id, @Param("estado") Mantenimiento.Estado estado);

    @Query("SELECT m FROM Mantenimiento m WHERE m.acoplado.id = :id AND m.estado = :estado AND m.tipoMantenimiento.clase = :clase")
    List<Mantenimiento> buscarMantenimientoVigenteIdAcoplado(@Param("id") Long id, @Param("estado") Mantenimiento.Estado estado, @Param("clase") TipoMantenimiento.Clase clase);
    
    @Query("SELECT m FROM Mantenimiento m WHERE m.estado = :estado AND m.acoplado.id = :idAcoplado AND m.tipoMantenimiento.id = :idTipo AND (m.ordenDeTrabajo IS NULL OR m.ordenDeTrabajo.id <> :idOt)")
    Optional<Mantenimiento> buscarMantenimientoVigenteAcopladoOt(@Param("estado") Mantenimiento.Estado estado, @Param("idAcoplado") Long idAcoplado, @Param("idTipo") Long idTipo, @Param("idOt") Long idOt);

    @Query("SELECT m FROM Mantenimiento m WHERE camion_id = :id AND m.estado = :estado")
    public List<Mantenimiento> buscarHistorialCamion(@Param("id") Long id, @Param("estado") Mantenimiento.Estado estado);
    
    @Query("SELECT m FROM Mantenimiento m WHERE camion_id = :id AND m.estado = :estado AND tipo_mantenimiento_id = :idTipo")
    public List<Mantenimiento> buscarHistorialCamionTipo(@Param("id") Long id, @Param("estado") Mantenimiento.Estado estado, @Param("idTipo") Long idTipo);

    @Query("SELECT m FROM Mantenimiento m WHERE acoplado_id = :id AND m.estado = :estado")
    public List<Mantenimiento> buscarHistorialAcoplado(@Param("id") Long id, @Param("estado") Mantenimiento.Estado estado);
    
    @Query("SELECT m FROM Mantenimiento m WHERE acoplado_id = :id AND m.estado = :estado AND tipo_mantenimiento_id = :idTipo")
    public List<Mantenimiento> buscarHistorialAcopladoTipo(@Param("id") Long id, @Param("estado") Mantenimiento.Estado estado, @Param("idTipo") Long idTipo);
    
    @Query("SELECT m FROM Mantenimiento m WHERE m.idOrg = :id AND m.estado = :estado AND ((:idCamion IS NULL) OR (:idCamion = 0L AND m.camion IS NULL) OR (:idCamion > 0 AND m.camion.id = :idCamion)) AND ((:idAcoplado IS NULL) OR (:idAcoplado = 0L AND m.acoplado IS NULL) OR (:idAcoplado > 0 AND m.acoplado.id = :idAcoplado))")
    List<Mantenimiento> findMantenimientosFiltrados(@Param("id") Long id, @Param("estado") Mantenimiento.Estado estado, @Param("idCamion") Long idCamion, @Param("idAcoplado") Long idAcoplado);

    List<Mantenimiento> findByCamionIsNotNullAndIdOrgAndEstado(Long idOrg, Mantenimiento.Estado estado);

    List<Mantenimiento> findByCamionIsNotNullAndIdOrgAndEstadoAndTipoMantenimiento_Clase(Long idOrg, Mantenimiento.Estado estado, TipoMantenimiento.Clase clase);

    List<Mantenimiento> findByAcopladoIsNotNullAndIdOrgAndEstado(Long idOrg, Mantenimiento.Estado estado);

    List<Mantenimiento> findByAcopladoIsNotNullAndIdOrgAndEstadoAndTipoMantenimiento_Clase(Long idOrg, Mantenimiento.Estado estado, TipoMantenimiento.Clase clase);

    @Query("SELECT m FROM Mantenimiento m WHERE m.idOrg = :id AND m.estado = :estado")
    List<Mantenimiento> findMantenimientosNoActualizados(@Param("id") Long id, @Param("estado") Mantenimiento.Estado estado);
    
    @Query("SELECT m FROM Mantenimiento m WHERE camion_id = :id AND m.estado = :estado")
    List<Mantenimiento> findMantenimientosCamionPorVencer(@Param("id") Long id, @Param("estado") Mantenimiento.Estado estado);
    
    @Query("SELECT m FROM Mantenimiento m WHERE acoplado_id = :id AND m.estado = :estado")
    List<Mantenimiento> findMantenimientosAcopladoPorVencer(@Param("id") Long id, @Param("estado") Mantenimiento.Estado estado);

    @Query("SELECT m FROM Mantenimiento m WHERE m.estado = :estado AND camion_id = :id AND tipo_mantenimiento_id = :idTipo")
    Optional<Mantenimiento> buscarMantenimientoVigenteCamion(@Param("estado") Mantenimiento.Estado estado, @Param("id") Long id, @Param("idTipo") Long idTipo);

    @Query("SELECT m FROM Mantenimiento m WHERE m.estado = :estado AND acoplado_id = :id AND tipo_mantenimiento_id = :idTipo")
    Optional<Mantenimiento> buscarMantenimientoVigenteAcoplado(@Param("estado") Mantenimiento.Estado estado, @Param("id") Long id, @Param("idTipo") Long idTipo);

    @Query("SELECT m FROM Mantenimiento m WHERE m.idOrg = :idOrg AND m.estado = :estado AND camion_id != null AND tipo_mantenimiento_id = :idTipo")
    List<Mantenimiento> buscarMantenimientoVigenteCamionesPorTipo(@Param("idOrg") Long idOrg, @Param("estado") Mantenimiento.Estado estado, @Param("idTipo") Long idTipo);

    @Query("SELECT m FROM Mantenimiento m WHERE m.idOrg = :idOrg AND m.estado = :estado AND camion_id != null AND tipo_mantenimiento_id = :idTipo AND m.tipoMantenimiento.clase = :clase")
    List<Mantenimiento> buscarMantenimientoVigenteCamionesPorTipoClase(@Param("idOrg") Long idOrg, @Param("estado") Mantenimiento.Estado estado, @Param("idTipo") Long idTipo, @Param("clase") TipoMantenimiento.Clase clase);

    @Query("SELECT m FROM Mantenimiento m WHERE m.idOrg = :idOrg AND m.estado = :estado AND acoplado_id != null AND tipo_mantenimiento_id = :idTipo AND m.tipoMantenimiento.clase = :clase")
    List<Mantenimiento> buscarMantenimientoVigenteAcopladosPorTipoClase(@Param("idOrg") Long idOrg, @Param("estado") Mantenimiento.Estado estado, @Param("idTipo") Long idTipo, @Param("clase") TipoMantenimiento.Clase clase);

    @Query("SELECT m FROM Mantenimiento m WHERE m.idOrg = :idOrg AND m.estado = :estado AND acoplado_id != null AND tipo_mantenimiento_id = :idTipo")
    List<Mantenimiento> buscarMantenimientoVigenteAcopladosPorTipo(@Param("idOrg") Long idOrg, @Param("estado") Mantenimiento.Estado estado, @Param("idTipo") Long idTipo);

    @Query("SELECT m FROM Mantenimiento m WHERE m.estado = :estado AND camion_id = :id AND tipo_mantenimiento_id = :idTipo")
    List<Mantenimiento> buscarMantenimientoVigenteCamionPorTipo(@Param("estado") Mantenimiento.Estado estado, @Param("id") Long id, @Param("idTipo") Long idTipo);

    @Query("SELECT m FROM Mantenimiento m WHERE m.estado = :estado AND camion_id = :id AND tipo_mantenimiento_id = :idTipo AND m.tipoMantenimiento.clase = :clase")
    List<Mantenimiento> buscarMantenimientoVigenteCamionPorTipoClase(@Param("estado") Mantenimiento.Estado estado, @Param("id") Long id, @Param("idTipo") Long idTipo, @Param("clase") TipoMantenimiento.Clase clase);

    @Query("SELECT m FROM Mantenimiento m WHERE m.estado = :estado AND acoplado_id = :id AND tipo_mantenimiento_id = :idTipo")
    List<Mantenimiento> buscarMantenimientoVigenteAcopladoPorTipo(@Param("estado") Mantenimiento.Estado estado, @Param("id") Long id, @Param("idTipo") Long idTipo);

    @Query("SELECT m FROM Mantenimiento m WHERE m.estado = :estado AND acoplado_id = :id AND tipo_mantenimiento_id = :idTipo AND m.tipoMantenimiento.clase = :clase")
    List<Mantenimiento> buscarMantenimientoVigenteAcopladoPorTipoClase(@Param("estado") Mantenimiento.Estado estado, @Param("id") Long id, @Param("idTipo") Long idTipo, @Param("clase") TipoMantenimiento.Clase clase);

    List<Mantenimiento> findByOrdenDeTrabajoIdAndEstadoAndCamionIsNotNull(Long ordenDeTrabajoId, Mantenimiento.Estado estado);
    
    List<Mantenimiento> findByOrdenDeTrabajoIdAndEstadoAndAcopladoId(Long ordenDeTrabajoId, Mantenimiento.Estado estado, Long idAcoplado);
    
    List<Mantenimiento> findByOrdenDeTrabajoIdAndEstado(Long ordenDeTrabajoId, Mantenimiento.Estado estado);
    
    Optional<Mantenimiento> findTopByCamionIdAndTipoMantenimientoIdAndEstadoOrderByIdDesc(Long idCamion, Long idTipoMantenimiento, Mantenimiento.Estado estado);
    
    Optional<Mantenimiento> findTopByAcopladoIdAndTipoMantenimientoIdAndEstadoOrderByIdDesc(Long idAcoplado, Long idTipoMantenimiento, Mantenimiento.Estado estado);
    
}
