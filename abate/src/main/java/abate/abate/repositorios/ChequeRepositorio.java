package abate.abate.repositorios;

import abate.abate.entidades.Banco;
import abate.abate.entidades.Cheque;
import abate.abate.entidades.Cheque.EstadoCheque;
import java.util.ArrayList;
import java.util.Date;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ChequeRepositorio extends JpaRepository<Cheque, Long> {

    ArrayList<Cheque> findByIdOrg(Long idOrg);

    ArrayList<Cheque> findByIdOrgAndEstado(Long idOrg, EstadoCheque estado);

    ArrayList<Cheque> findByIdOrgAndEstadoOrderByFechaVencimientoAsc(Long idOrg, EstadoCheque estado);

    ArrayList<Cheque> findByEstadoAndFechaVencimientoLessThanEqual(EstadoCheque estado, Date fecha);

    ArrayList<Cheque> findByEstadoAndFechaAcreditacionLessThanEqual(EstadoCheque estado, Date fechaAcreditacion);

    Boolean existsByBancoEmisorAndNumeroCheque(Banco bancoEmisor, String numeroCheque);

    // ArrayList<Cheque> findByIdOrgAndNumeroChequeContainingIgnoreCase(Long idOrg, String numeroCheque);
    @Query("SELECT c "
            + "FROM Cheque c "
            + "WHERE c.idOrg = :idOrg "
            + "AND c.numeroCheque LIKE %:numeroCheque%")
    ArrayList<Cheque> buscarPorNumero(@Param("idOrg") Long idOrg, @Param("numeroCheque") String numeroCheque);

}
