
package abate.abate.repositorios;

import abate.abate.entidades.NeumaticoMarca;
import java.util.ArrayList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface NeumaticoMarcaRepositorio extends JpaRepository<NeumaticoMarca, Long> {
    
    @Query("SELECT MAX(m) FROM NeumaticoMarca m WHERE m.idOrg = :id")
    public NeumaticoMarca ultimaMarca(@Param("id") Long id);
    
    @Query("SELECT n FROM NeumaticoMarca n WHERE n.idOrg = :id")
    public ArrayList<NeumaticoMarca> buscarNeumaticoMarca(@Param("id") Long id);
    
}
