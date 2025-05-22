
package abate.abate.repositorios;

import abate.abate.entidades.Eje;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EjeRepositorio extends JpaRepository<Eje, Long> {
    
}
