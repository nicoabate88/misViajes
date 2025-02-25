package abate.abate.repositorios;

import abate.abate.entidades.Detalle;
import java.util.ArrayList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface DetalleRepositorio extends JpaRepository<Detalle, Long> {

    @Query("SELECT MAX(id) FROM Detalle")
    public Long ultimoDetalle();

    @Query("SELECT d FROM Detalle d WHERE gasto_id = :id")
    public ArrayList<Detalle> buscarDetallesGasto(@Param("id") Long id);

}
