
package abate.abate.repositorios;

import abate.abate.entidades.NeumaticoProveedor;
import java.util.ArrayList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface NeumaticoProveedorRepositorio extends JpaRepository<NeumaticoProveedor, Long> {
    
    @Query("SELECT MAX(p) FROM NeumaticoProveedor p WHERE p.idOrg = :id")
    public NeumaticoProveedor ultimoProveedor(@Param("id") Long id);
    
    @Query("SELECT p FROM NeumaticoProveedor p WHERE p.idOrg = :id")
    public ArrayList<NeumaticoProveedor> buscarNeumaticoProveedor(@Param("id") Long id);
    
}
