
package abate.abate.repositorios;

import abate.abate.entidades.ValorI;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ValorIRepositorio extends JpaRepository<ValorI, Long> {

    @Query("SELECT MAX(id) FROM ValorI")
    public Long ultimoValor();
    
    List<ValorI> findByIngresoId(Long ingresoId);

    @Query("SELECT COALESCE(SUM(v.importe),0) FROM ValorI v WHERE v.ingreso.id = :ingresoId")
    Double sumImportesByIngreso(@Param("ingresoId") Long ingresoId);

    void deleteByIngresoId(Long ingresoId);
    
}
