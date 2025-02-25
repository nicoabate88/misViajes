package abate.abate.repositorios;

import abate.abate.entidades.Cliente;
import java.util.ArrayList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ClienteRepositorio extends JpaRepository<Cliente, Long> {

    @Query("SELECT MAX(id) FROM Cliente c WHERE c.idOrg = :id")
    public Long ultimoCliente(@Param("id") Long id);

    @Query("SELECT c FROM Cliente c WHERE c.idOrg = :id")
    public ArrayList<Cliente> buscarClientes(@Param("id") Long id);

}
