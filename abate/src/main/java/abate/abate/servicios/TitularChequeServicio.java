
package abate.abate.servicios;

import abate.abate.entidades.TitularCheque;
import abate.abate.excepciones.MiException;
import abate.abate.repositorios.TitularChequeRepositorio;
import java.util.ArrayList;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TitularChequeServicio {
    
    @Autowired
    private TitularChequeRepositorio titularRepositorio;

    public TitularCheque buscarPorId(Long id) throws Exception {

        return titularRepositorio.getById(id);

    }

    public ArrayList<TitularCheque> buscarTitulares(Long idOrg) {

        ArrayList<TitularCheque> lista = titularRepositorio.findByIdOrgOrderByNombreAsc(idOrg);

        return lista;

    }

    public ArrayList<TitularCheque> buscarTitularesHabilitados(Long idOrg) {

        ArrayList<TitularCheque> lista = titularRepositorio.findByIdOrgAndEstadoOrderByNombreAsc(idOrg, TitularCheque.EstadoTitular.HABILITADA);

        return lista;

    }

    @Transactional
    public void registrarTitular(TitularCheque titular) {

        titularRepositorio.save(titular);

    }

    public void validarDatos(Long idOrg, String nombre) throws MiException {

        ArrayList<TitularCheque> lista = titularRepositorio.findByIdOrg(idOrg);

        for (TitularCheque b : lista) {
            if (b.getNombre().equalsIgnoreCase(nombre)) {
                throw new MiException("El Titular '" + nombre + "' ya está registrado.");
            }
        }
    }

    public void validarDatosModificar(TitularCheque titular, String nombre) throws MiException {

        ArrayList<TitularCheque> lista = titularRepositorio.findByIdOrg(titular.getIdOrg());

        if (!titular.getNombre().equalsIgnoreCase(nombre)) {
            for (TitularCheque b : lista) {
                if (b.getNombre().equalsIgnoreCase(nombre)) {
                    throw new MiException("El Titular '" + nombre + "' ya está registrado.");
                }
            }
        }
    }
    
}
