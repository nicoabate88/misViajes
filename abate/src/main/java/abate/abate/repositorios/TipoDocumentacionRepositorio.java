
package abate.abate.repositorios;

import abate.abate.entidades.TipoDocumentacion;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TipoDocumentacionRepositorio extends JpaRepository<TipoDocumentacion, Long> {
    
    @Query("SELECT MAX(t) FROM TipoDocumentacion t WHERE t.idOrg = :id")
    public TipoDocumentacion ultimoTipo(@Param("id") Long id);  
    
    @Query("SELECT t FROM TipoDocumentacion t WHERE t.idOrg = :id") 
    public List<TipoDocumentacion> buscarTipos(@Param("id") Long id);
    
    @Query("SELECT t FROM TipoDocumentacion t WHERE t.idOrg = :id AND :aplicaA MEMBER OF t.aplicaA")
    List<TipoDocumentacion> findByAplicaA(@Param("id") Long id, @Param("aplicaA") TipoDocumentacion.AplicaA aplicaA);
    
    @Query("SELECT COUNT(td) > 0 FROM TipoDocumentacion td JOIN td.aplicaA a WHERE td.idOrg = :id AND td.id = :idTipo AND a = :aplicaA")
    boolean existsByIdAndAplicaA(@Param("id") Long id, @Param("idTipo") Long idTipo, @Param("aplicaA") TipoDocumentacion.AplicaA aplicaA);
    
}
