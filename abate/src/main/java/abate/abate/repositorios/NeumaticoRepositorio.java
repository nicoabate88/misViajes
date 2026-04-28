
package abate.abate.repositorios;

import abate.abate.entidades.Neumatico;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface NeumaticoRepositorio extends JpaRepository<Neumatico, Long> {
    
    @Query("SELECT MAX(n) FROM Neumatico n WHERE n.idOrg = :id")
    public Neumatico ultimoNeumatico(@Param("id") Long id); 
    
    boolean existsByNumeroAndIdOrg(Integer numero, Long idOrg);
    
    Optional<Neumatico> findTopByIdOrgOrderByNumeroDesc(Long idOrg);
    
    @Query("SELECT n FROM Neumatico n WHERE :aplicaA MEMBER OF n.aplicaA AND n.idOrg = :id")
    List<Neumatico> findByAplicaA(@Param("aplicaA") Neumatico.AplicaA aplicaA, @Param("id") Long id);
    
    @Query("SELECT n FROM Neumatico n WHERE n.ubicacion = 'DEPOSITO' AND :aplicaA MEMBER OF n.aplicaA")
    List<Neumatico> findByDepositoAndAplicaA(@Param("aplicaA") Neumatico.AplicaA aplicaA);
    
    @Query("SELECT n FROM Neumatico n WHERE n.idOrg = :id")
    List<Neumatico> buscarNeumaticosIdOrg(@Param("id") Long id);
    
    @Query("SELECT n FROM Neumatico n WHERE n.ubicacion = 'DEPOSITO' AND n.idOrg = :id")
    List<Neumatico> findByDeposito(@Param("id") Long id);
    
    @Query("SELECT n FROM Neumatico n WHERE n.ubicacion = 'DEPOSITO' AND n.estado = 'NUEVO' AND n.idOrg = :id")
    List<Neumatico> findByDepositoNuevo(@Param("id") Long id);
    
    @Query("SELECT n FROM Neumatico n WHERE n.ubicacion = 'DEPOSITO' AND n.estado = 'USADO' AND n.idOrg = :id")
    List<Neumatico> findByDepositoUsado(@Param("id") Long id);
    
    @Query("SELECT n FROM Neumatico n WHERE n.ubicacion = 'DEPOSITO' AND n.estado IN ('RECAPADO', '+RECAPADO') AND n.idOrg = :id")
    List<Neumatico> findByDepositoRecapado(@Param("id") Long id);
    
    @Query("SELECT n FROM Neumatico n WHERE n.ubicacion = 'COLOCADO' AND n.idOrg = :id")
    List<Neumatico> findByColocado(@Param("id") Long id);
    
    @Query("SELECT n FROM Neumatico n WHERE n.ubicacion = 'COLOCADO' AND n.estado = 'NUEVO' AND n.idOrg = :id")
    List<Neumatico> findByColocadoNuevo(@Param("id") Long id);
    
    @Query("SELECT n FROM Neumatico n WHERE n.ubicacion = 'COLOCADO' AND n.estado = 'USADO' AND n.idOrg = :id")
    List<Neumatico> findByColocadoUsado(@Param("id") Long id);
    
    @Query("SELECT n FROM Neumatico n WHERE n.ubicacion = 'COLOCADO' AND n.estado IN ('RECAPADO', '+RECAPADO') AND n.idOrg = :id")
    List<Neumatico> findByColocadoRecapado(@Param("id") Long id);
    
    @Query("SELECT n FROM Neumatico n WHERE n.ubicacion = 'AUXILIO' AND n.idOrg = :id")
    List<Neumatico> findByAuxilio(@Param("id") Long id);
    
    @Query("SELECT n FROM Neumatico n WHERE n.ubicacion = 'AUXILIO' AND n.estado = 'NUEVO' AND n.idOrg = :id")
    List<Neumatico> findByAuxilioNuevo(@Param("id") Long id);
    
    @Query("SELECT n FROM Neumatico n WHERE n.ubicacion = 'AUXILIO' AND n.estado = 'USADO' AND n.idOrg = :id")
    List<Neumatico> findByAuxilioUsado(@Param("id") Long id);
    
    @Query("SELECT n FROM Neumatico n WHERE n.ubicacion = 'AUXILIO' AND n.estado IN ('RECAPADO', '+RECAPADO') AND n.idOrg = :id")
    List<Neumatico> findByAuxilioRecapado(@Param("id") Long id);
    
    @Query("SELECT n FROM Neumatico n WHERE n.ubicacion = 'EN RECAPADO' AND n.idOrg = :id")
    List<Neumatico> findByEnRecapado(@Param("id") Long id);
    
    @Query("SELECT n FROM Neumatico n WHERE n.ubicacion = 'EN RECAPADO' AND n.estado = 'NUEVO' AND n.idOrg = :id")
    List<Neumatico> findByEnRecapadoNuevo(@Param("id") Long id);
    
    @Query("SELECT n FROM Neumatico n WHERE n.ubicacion = 'EN RECAPADO' AND n.estado = 'USADO' AND n.idOrg = :id")
    List<Neumatico> findByEnRecapadoUsado(@Param("id") Long id);
    
    @Query("SELECT n FROM Neumatico n WHERE n.ubicacion = 'EN RECAPADO' AND n.estado IN ('RECAPADO', '+RECAPADO') AND n.idOrg = :id")
    List<Neumatico> findByEnRecapadoRecapado(@Param("id") Long id);
    
    @Query("SELECT n FROM Neumatico n WHERE n.ubicacion = 'FUERA DE SERVICIO' AND n.idOrg = :id")
    List<Neumatico> findByFueraServicio(@Param("id") Long id);
    
    @Query("SELECT n FROM Neumatico n WHERE n.ubicacion = 'FUERA DE SERVICIO' AND n.estado = 'NUEVO' AND n.idOrg = :id")
    List<Neumatico> findByFueraServicioNuevo(@Param("id") Long id);
    
    @Query("SELECT n FROM Neumatico n WHERE n.ubicacion = 'FUERA DE SERVICIO' AND n.estado = 'USADO' AND n.idOrg = :id")
    List<Neumatico> findByFueraServicioUsado(@Param("id") Long id);
    
    @Query("SELECT n FROM Neumatico n WHERE n.ubicacion = 'FUERA DE SERVICIO' AND n.estado IN ('RECAPADO', '+RECAPADO') AND n.idOrg = :id")
    List<Neumatico> findByFueraServicioRecapado(@Param("id") Long id);
    
    @Query("SELECT n FROM Neumatico n WHERE n.estado IN ('RECAPADO', '+RECAPADO') AND n.idOrg = :id")
    List<Neumatico> findByRecapado(@Param("id") Long id);
    
    @Query("SELECT n FROM Neumatico n WHERE n.estado = 'NUEVO' AND n.idOrg = :id")
    List<Neumatico> findByNuevo(@Param("id") Long id);
    
    @Query("SELECT n FROM Neumatico n WHERE n.estado = 'USADO' AND n.idOrg = :id")
    List<Neumatico> findByUsado(@Param("id") Long id);
    
    Neumatico findTopByMarcaIdAndIdOrgOrderByIdDesc(Long marcaId, Long idOrg);
    
    Neumatico findTopByProveedorIdAndIdOrgOrderByIdDesc(Long proveedorId, Long idOrg);
   
}
