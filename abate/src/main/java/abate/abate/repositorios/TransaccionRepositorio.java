package abate.abate.repositorios;

import abate.abate.entidades.Cliente;
import abate.abate.entidades.Transaccion;
import abate.abate.entidades.Usuario;
import java.util.ArrayList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TransaccionRepositorio extends JpaRepository<Transaccion, Long> {

    @Query("SELECT t FROM Transaccion t WHERE cliente_id = :id")
    public ArrayList<Transaccion> buscarTransaccionIdCliente(@Param("id") Long id);

    @Query("SELECT t FROM Transaccion t WHERE chofer_id = :id")
    public ArrayList<Transaccion> buscarTransaccionIdChofer(@Param("id") Long id);

    @Query("SELECT t FROM Transaccion t WHERE recibo_id = :id")
    public Transaccion buscarTransaccionIdRecibo(@Param("id") Long id);

    @Query("SELECT t FROM Transaccion t WHERE entrega_id = :id")
    public Transaccion buscarTransaccionIdEntrega(@Param("id") Long id);

    @Query("SELECT t FROM Transaccion t WHERE ingreso_id = :id")
    public Transaccion buscarTransaccionIdIngreso(@Param("id") Long id);

    @Query("SELECT t FROM Transaccion t WHERE gasto_id = :id")
    public Transaccion buscarTransaccionIdGasto(@Param("id") Long id);

    @Query("SELECT t FROM Transaccion t WHERE flete_id = :id")
    public ArrayList<Transaccion> buscarTransaccionesIdFlete(@Param("id") Long id);

    @Query("SELECT MAX(id) FROM Transaccion")
    public Long ultimoTransaccion();

    @Query(value = "SELECT * FROM transaccion t "
            + "INNER JOIN cuenta_transaccion ct ON t.id = ct.transaccion_id "
            + "INNER JOIN cuenta c ON ct.cuenta_id = c.id "
            + "WHERE c.id = :id", nativeQuery = true)
    public ArrayList<Transaccion> buscarTransaccionCuenta(@Param("id") Long id);

    @Query(value = "SELECT * FROM transaccion t "
            + "INNER JOIN caja_transaccion ct ON t.id = ct.transaccion_id "
            + "INNER JOIN caja c ON ct.caja_id = c.id "
            + "WHERE c.id = :id", nativeQuery = true)
    public ArrayList<Transaccion> buscarTransaccionCaja(@Param("id") Long id);

    Transaccion findTopByChoferOrderByIdDesc(Usuario chofer);

    Transaccion findTopByClienteOrderByIdDesc(Cliente cliente);

    @Query(value = "SELECT * FROM transaccion t "
            + "INNER JOIN cuenta_transaccion ct ON t.id = ct.transaccion_id "
            + "INNER JOIN cuenta c ON ct.cuenta_id = c.id "
            + "WHERE c.id = :id "
            + "AND t.fecha >= :fechaDesde AND t.fecha <= :fechaHasta", nativeQuery = true)
    public ArrayList<Transaccion> buscarTransaccionCuentaPorRangoFechas(@Param("id") Long id,
            @Param("fechaDesde") java.sql.Date fechaDesde,
            @Param("fechaHasta") java.sql.Date fechaHasta);

    @Query(value = "SELECT * FROM transaccion t "
            + "INNER JOIN caja_transaccion ct ON t.id = ct.transaccion_id "
            + "INNER JOIN caja c ON ct.caja_id = c.id "
            + "WHERE c.id = :id "
            + "AND t.fecha >= :fechaDesde AND t.fecha <= :fechaHasta", nativeQuery = true)
    public ArrayList<Transaccion> buscarTransaccionCajaPorRangoFechas(@Param("id") Long id,
            @Param("fechaDesde") java.sql.Date fechaDesde,
            @Param("fechaHasta") java.sql.Date fechaHasta);

}
