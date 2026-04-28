package abate.abate.servicios;

import abate.abate.entidades.Entrega;
import abate.abate.entidades.Usuario;
import abate.abate.entidades.ValorE;
import abate.abate.repositorios.UsuarioRepositorio;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import abate.abate.repositorios.EntregaRepositorio;
import abate.abate.util.EntregaComparador;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class EntregaServicio {

    @Autowired
    private UsuarioRepositorio usuarioRepositorio;
    @Autowired
    private EntregaRepositorio entregaRepositorio;
    @Autowired
    private TransaccionServicio transaccionServicio;

    @Transactional
    public void crearEntrega(Long idOrg, Long idChofer, String fecha, List<ValorE> valores, String observacion, Usuario usuario) throws ParseException {

        Usuario chofer = new Usuario();
        Optional<Usuario> chof = usuarioRepositorio.findById(idChofer);
        if (chof.isPresent()) {
            chofer = chof.get();
        }

        String obsMayusculas = observacion.toUpperCase();
        Date f = convertirFecha(fecha);
        Long idEntrega = buscarUltimoIdOrg(idOrg);

        Entrega entrega = new Entrega();

        entrega.setIdOrg(idOrg);
        entrega.setChofer(chofer);
        entrega.setFecha(f);
        entrega.setObservacion(obsMayusculas);
        entrega.setUsuario(usuario);
        entrega.setIdEntrega(idEntrega + 1);

        Double total = 0.0;

        for (ValorE v : valores) {

            entrega.addValor(v);   // mantiene relación bidireccional
            total += v.getImporte();
        }

        entrega.setImporte(total);

        entregaRepositorio.save(entrega);

        transaccionServicio.crearTransaccionEntrega(entrega);

    }

    @Transactional
    public void modificarEntrega(Long idEntrega, Long idChofer, String fecha, List<ValorE> nuevosValores, String observacion, Usuario usuario) throws ParseException {

        Entrega entrega = new Entrega();
        Optional<Entrega> ent = entregaRepositorio.findById(idEntrega);
        if (ent.isPresent()) {
            entrega = ent.get();
        }

        Usuario chofer = new Usuario();
        Optional<Usuario> chof = usuarioRepositorio.findById(idChofer);
        if (chof.isPresent()) {
            chofer = chof.get();
        }

        String obsMayusculas = observacion.toUpperCase();
        Date f = convertirFecha(fecha);

        entrega.setChofer(chofer);
        entrega.setFecha(f);
        entrega.setObservacion(obsMayusculas);
        entrega.setUsuario(usuario);

        entrega.getValores().clear();

        Double total = 0.0;

        for (ValorE v : nuevosValores) {
            entrega.addValor(v);
            total += v.getImporte();
        }

        entrega.setImporte(total);

        entregaRepositorio.save(entrega);

        transaccionServicio.modificarTransaccionEntrega(entrega);

    }

    @Transactional
    public void eliminarEntrega(Long idEntrega) {

        Entrega entrega = entregaRepositorio.getById(idEntrega);

        transaccionServicio.eliminarTransaccionEntrega(idEntrega);

        entregaRepositorio.delete(entrega);

    }

    public Long buscarUltimoIdOrg(Long idOrg) {

        Entrega entrega = new Entrega();
        Optional<Entrega> etga = entregaRepositorio.findTopByIdOrgOrderByIdDesc(idOrg);
        if (etga.isPresent()) {
            entrega = etga.get();

            return entrega.getIdEntrega();

        } else {

            int ultimo = 0;
            Long primero = Long.valueOf(ultimo);

            return primero;

        }

    }

    public Long buscarUltimo(Long idOrg) {

        return entregaRepositorio.ultimoEntrega(idOrg);

    }

    public Entrega buscarEntrega(Long id) {

        return entregaRepositorio.findByIdWithValores(id);

    }

    public ArrayList<Entrega> buscarEntregas(Long idOrg, String desde, String hasta) throws ParseException {

        Date d = convertirFecha(desde);
        Date h = convertirFecha(hasta);

        ArrayList<Entrega> lista = entregaRepositorio.findByFechaBetweenAndIdOrg(d, h, idOrg);

        Collections.sort(lista, EntregaComparador.ordenarFechaDesc);

        return lista;

    }

    public ArrayList<Entrega> buscarEntregasIdChofer(Long id, String desde, String hasta) throws ParseException {

        Date d = convertirFecha(desde);
        Date h = convertirFecha(hasta);

        ArrayList<Entrega> lista = entregaRepositorio.findByFechaBetweenAndChoferId(d, h, id);

        Collections.sort(lista, EntregaComparador.ordenarFechaDesc);

        return lista;

    }

    public Date convertirFecha(String fecha) throws ParseException {
        SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd");
        return formato.parse(fecha);
    }

}
