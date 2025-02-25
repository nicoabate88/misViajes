package abate.abate.repositorios;

import abate.abate.entidades.Cuenta;
import java.util.ArrayList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CuentaRepositorio extends JpaRepository<Cuenta, Long> {

    @Query("SELECT id FROM Cuenta c WHERE cliente_id = :id")
    public Long buscarIdCuentaIdCliente(@Param("id") Long id);

    @Query("SELECT id FROM Cuenta c WHERE chofer_id = :id")
    public Long buscarIdCuentaIdChofer(@Param("id") Long id);

    @Query("SELECT c FROM Cuenta c WHERE cliente_id = :id")
    public Cuenta buscarCuentaIdCliente(@Param("id") Long id);

    @Query("SELECT c FROM Cuenta c WHERE chofer_id = :id")
    public Cuenta buscarCuentaIdChofer(@Param("id") Long id);

    @Query("SELECT c FROM Cuenta c WHERE cliente_id = null AND c.idOrg = :id")
    public ArrayList<Cuenta> buscarCuentasChofer(@Param("id") Long id);

    @Query("SELECT c FROM Cuenta c WHERE chofer_id = null AND c.idOrg = :id")
    public ArrayList<Cuenta> buscarCuentasCliente(@Param("id") Long id);

}
