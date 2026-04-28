
package abate.abate.repositorios;

import abate.abate.entidades.ValorE;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ValorERepositorio extends JpaRepository<ValorE, Long> {

    @Query("SELECT MAX(id) FROM ValorE")
    public Long ultimoValor();
    
    List<ValorE> findByEntregaId(Long entregaId);

    @Query("SELECT COALESCE(SUM(v.importe),0) FROM ValorE v WHERE v.entrega.id = :entregaId")
    Double sumImportesByEntrega(@Param("entregaId") Long entregaId);

    void deleteByEntregaId(Long entregaId);
    
}
