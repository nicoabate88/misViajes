
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
    
    @Query("SELECT m FROM Mantenimiento m WHERE camion_id = :id AND estado != 'ACTUALIZADO'")
    public List<Mantenimiento> buscarMantenimientoIdCamion(@Param("id") Long id);
    
    @Query("SELECT m FROM Mantenimiento m WHERE acoplado_id = :id AND estado != 'ACTUALIZADO'")
    public List<Mantenimiento> buscarMantenimientoIdAcoplado(@Param("id") Long id);
    
    @Query("SELECT m FROM Mantenimiento m WHERE camion_id = :id AND estado = 'ACTUALIZADO'")
    public List<Mantenimiento> buscarHistorialCamion(@Param("id") Long id);
    
    @Query("SELECT m FROM Mantenimiento m WHERE acoplado_id = :id AND estado = 'ACTUALIZADO'")
    public List<Mantenimiento> buscarHistorialAcoplado(@Param("id") Long id);
    
    List<Mantenimiento> findByCamionIsNotNullAndIdOrgAndEstadoNot(Long idOrg, String estado);
    
    List<Mantenimiento> findByAcopladoIsNotNullAndIdOrgAndEstadoNot(Long idOrg, String estado);
    
    @Query("SELECT m FROM Mantenimiento m WHERE m.idOrg = :id AND m.estado != 'ACTUALIZADO'")
    List<Mantenimiento> findMantenimientosNoActualizados(Long id);
    
    @Query("SELECT m FROM Mantenimiento m WHERE estado != 'ACTUALIZADO' AND camion_id = :id AND tipo_mantenimiento_id = :idTipo")
    Optional<Mantenimiento> buscarMantenimientoCamion(@Param("id") Long id, @Param("idTipo") Long idTipo);
    
    @Query("SELECT m FROM Mantenimiento m WHERE estado != 'ACTUALIZADO' AND acoplado_id = :id AND tipo_mantenimiento_id = :idTipo")
    Optional<Mantenimiento> buscarMantenimientoAcoplado(@Param("id") Long id, @Param("idTipo") Long idTipo);
    
    @Query("SELECT m FROM Mantenimiento m WHERE m.idOrg = :idOrg AND estado != 'ACTUALIZADO' AND camion_id != null AND tipo_mantenimiento_id = :idTipo")
    List<Mantenimiento> buscarMantenimientoCamionesPorTipo(@Param("idOrg") Long idOrg, @Param("idTipo") Long idTipo);
    
    @Query("SELECT m FROM Mantenimiento m WHERE m.idOrg = :idOrg AND estado != 'ACTUALIZADO' AND acoplado_id != null AND tipo_mantenimiento_id = :idTipo")
    List<Mantenimiento> buscarMantenimientoAcopladosPorTipo(@Param("idOrg") Long idOrg, @Param("idTipo") Long idTipo);
    
    @Query("SELECT m FROM Mantenimiento m WHERE estado != 'ACTUALIZADO' AND camion_id = :id AND tipo_mantenimiento_id = :idTipo")
    List<Mantenimiento> buscarMantenimientoCamionPorTipo(@Param("id") Long id, @Param("idTipo") Long idTipo);
    
    @Query("SELECT m FROM Mantenimiento m WHERE estado != 'ACTUALIZADO' AND acoplado_id = :id AND tipo_mantenimiento_id = :idTipo")
    List<Mantenimiento> buscarMantenimientoAcopladoPorTipo(@Param("id") Long id, @Param("idTipo") Long idTipo);
    
}
