
package abate.abate.repositorios;

import abate.abate.entidades.Producto;
import java.util.ArrayList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductoRepositorio extends JpaRepository<Producto, Long> {
    
    @Query("SELECT MAX(id) FROM Producto p WHERE p.idOrg = :id")
    public Long ultimoProducto(@Param("id") Long id);

    @Query("SELECT p FROM Producto p WHERE p.idOrg = :id")
    public ArrayList<Producto> buscarProductos(@Param("id") Long id);
    
    @Query("SELECT p FROM Producto p WHERE p.idOrg = :id AND p.estado = 'HABILITADO'")
    public ArrayList<Producto> buscarProductosHab(@Param("id") Long id);
    
}
