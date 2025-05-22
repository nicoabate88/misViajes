package abate.abate.repositorios;

import abate.abate.entidades.Acoplado;
import abate.abate.entidades.Camion;
import abate.abate.entidades.Usuario;
import java.util.ArrayList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioRepositorio extends JpaRepository<Usuario, Long> {

    @Query("SELECT u FROM Usuario u WHERE u.usuario = :usuario")
    public Usuario buscarUsuarioPorUsuario(@Param("usuario") String usuario);

    @Query("SELECT MAX(id) FROM Usuario u WHERE u.idOrg = :id")
    public Long ultimoUsuario(@Param("id") Long id);

    @Query("SELECT MAX(id) FROM Usuario u WHERE u.rol = 'CEO'")
    public Long ultimoUsuarioCeo();

    @Query("SELECT u FROM Usuario u WHERE u.rol = 'CHOFER' AND u.idOrg = :id")
    public ArrayList<Usuario> buscarUsuariosChofer(@Param("id") Long id);

    @Query("SELECT u FROM Usuario u WHERE u.rol = 'ADMIN' AND u.idOrg = :id")
    public ArrayList<Usuario> buscarUsuariosAdmin(@Param("id") Long id);

    @Query("SELECT u FROM Usuario u WHERE u.idOrg = :id")
    public ArrayList<Usuario> buscarUsuarios(@Param("id") Long id);

    Usuario findTopByCamionOrderByIdDesc(Camion camion);
    
    Usuario findTopByAcopladoOrderByIdDesc(Acoplado acoplado);

    @Query("SELECT u FROM Usuario u WHERE u.id IN (SELECT MIN(u1.id) FROM Usuario u1 GROUP BY u1.idOrg)")
    public ArrayList<Usuario> findFirstByIdOrg();

}
