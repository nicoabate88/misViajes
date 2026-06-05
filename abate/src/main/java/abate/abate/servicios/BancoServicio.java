package abate.abate.servicios;

import abate.abate.entidades.Banco;
import abate.abate.excepciones.MiException;
import abate.abate.repositorios.BancoRepositorio;
import java.util.ArrayList;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BancoServicio {

    @Autowired
    private BancoRepositorio bancoRepositorio;

    public Banco buscarPorId(Long id) throws Exception {

        return bancoRepositorio.getById(id);

    }

    public ArrayList<Banco> buscarBancos(Long idOrg) {

        ArrayList<Banco> lista = bancoRepositorio.findByIdOrg(idOrg);

        return lista;

    }

    public ArrayList<Banco> buscarBancosHabilitados(Long idOrg) {

        ArrayList<Banco> lista = bancoRepositorio.findByIdOrgAndEstadoOrderByNombreAsc(idOrg, Banco.EstadoBanco.HABILITADA);

        return lista;

    }

    @Transactional
    public void registrarBanco(Banco banco) {

        bancoRepositorio.save(banco);

    }

    public void validarDatos(Long idOrg, String nombre) throws MiException {

        ArrayList<Banco> lista = bancoRepositorio.findByIdOrg(idOrg);

        for (Banco b : lista) {
            if (b.getNombre().equalsIgnoreCase(nombre)) {
                throw new MiException("El Banco '" + nombre + "' ya está registrado.");
            }
        }
    }

    public void validarDatosModificar(Banco banco, String nombre) throws MiException {

        ArrayList<Banco> lista = bancoRepositorio.findByIdOrg(banco.getIdOrg());

        if (!banco.getNombre().equalsIgnoreCase(nombre)) {
            for (Banco b : lista) {
                if (b.getNombre().equalsIgnoreCase(nombre)) {
                    throw new MiException("El Banco '" + nombre + "' ya está registrado.");
                }
            }
        }
    }

}
