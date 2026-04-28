
package abate.abate.repositorios;

import abate.abate.entidades.Valor;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ValorRepositorio extends JpaRepository<Valor, Long> {

    @Query("SELECT MAX(id) FROM Valor")
    public Long ultimoValor();
    
    List<Valor> findByReciboId(Long reciboId);

    @Query("SELECT COALESCE(SUM(v.importe),0) FROM Valor v WHERE v.recibo.id = :reciboId")
    Double sumImportesByRecibo(@Param("reciboId") Long reciboId);

    void deleteByReciboId(Long reciboId);
    
}
