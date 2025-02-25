package abate.abate.repositorios;

import abate.abate.entidades.Camion;
import abate.abate.entidades.Combustible;
import abate.abate.entidades.Usuario;
import java.util.ArrayList;
import java.util.Date;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CombustibleRepositorio extends JpaRepository<Combustible, Long> {

    @Query("SELECT MAX(id) FROM Combustible c WHERE c.idOrg = :id")
    public Long ultimaCarga(@Param("id") Long id);

    @Query("SELECT c.camion FROM Combustible c WHERE c.id = :idCarga")
    Camion findCamionByCargaId(@Param("idCarga") Long idCarga);

    Optional<Combustible> findFirstByIdGreaterThanAndCamionIdOrderByIdAsc(Long id, Long camionId);  //devuelve la carga posterior a la recibida por parametro

    Combustible findFirstByIdLessThanAndCamionIdOrderByIdDesc(Long id, Long camionId);   //devuelve la carga anterior a la recibida por parametro

    Optional<Combustible> findFirstByCamionOrderByIdAsc(Camion camion);  //devuelve primer registro buscado por camion

    Combustible findTopByUsuarioOrderByIdDesc(Usuario chofer); //devuelve ultimo Combustible registrado de chofer especifico

    Combustible findTopByCamionOrderByIdDesc(Camion camion); //devuelve ultimo Combustible registrado de camion especifico

    ArrayList<Combustible> findTop2ByCamionOrderByIdDesc(Camion camion); //devuelve ultimos 2 Combustible registrado de camion especifico

    ArrayList<Combustible> findAllByOrderByIdDesc();  //devuelve la lista de combustibles de forma descendente

    ArrayList<Combustible> findAllByUsuarioOrderByIdDesc(Usuario chofer);

    ArrayList<Combustible> findAllByCamionOrderByIdDesc(Camion camion);

    @Query("SELECT c FROM Combustible c WHERE camion_id = :id ORDER BY c.id DESC")  // Método para obtener el anteúltimo registro
    ArrayList<Combustible> findTop2ByCamionOrderByIdDesc(@Param("id") Long id);

    ArrayList<Combustible> findByFechaCargaBetweenAndChofer(Date desde, Date hasta, Usuario chofer);

    ArrayList<Combustible> findByFechaCargaBetweenAndIdOrg(Date desde, Date hasta, Long idOrg);

    ArrayList<Combustible> findByFechaCargaBetweenAndCamion(Date desde, Date hasta, Camion camion);

    @Query("SELECT c FROM Combustible c WHERE imagen_id = :id")
    public Combustible buscarCombustibleIdImagen(@Param("id") Long id);

    @Query("SELECT c.id FROM Combustible c WHERE azul_id = :id")
    public Long buscarIdCombustibleAzul(@Param("id") Long id);

}
                