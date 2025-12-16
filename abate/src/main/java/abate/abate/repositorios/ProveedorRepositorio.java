
package abate.abate.repositorios;

import abate.abate.entidades.Proveedor;
import java.util.ArrayList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProveedorRepositorio extends JpaRepository<Proveedor, Long> {
    
    @Query("SELECT MAX(id) FROM Proveedor p WHERE p.idOrg = :id")
    public Long ultimoProveedor(@Param("id") Long id);

    @Query("SELECT p FROM Proveedor p WHERE p.idOrg = :id")
    public ArrayList<Proveedor> buscarProveedores(@Param("id") Long id);
    
    @Query("SELECT p FROM Proveedor p WHERE p.idOrg = :id AND p.estado = 'HABILITADO'")
    public ArrayList<Proveedor> buscarProveedoresHab(@Param("id") Long id);
   
}
