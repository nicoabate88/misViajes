package abate.abate.servicios;

import abate.abate.entidades.Cliente;
import abate.abate.entidades.Recibo;
import abate.abate.entidades.Usuario;
import abate.abate.repositorios.ClienteRepositorio;
import abate.abate.repositorios.ReciboRepositorio;
import abate.abate.repositorios.UsuarioRepositorio;
import abate.abate.util.ReciboComparador;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Optional;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReciboServicio {

    @Autowired
    private ClienteRepositorio clienteRepositorio;
    @Autowired
    private UsuarioRepositorio usuarioRepositorio;
    @Autowired
    private ReciboRepositorio reciboRepositorio;
    @Autowired
    private TransaccionServicio transaccionServicio;

    @Transactional
    public void crearRecibo(Long idOrg, Long idCliente, String fecha, Double importe, String observacion, Long idUsuario) throws ParseException {

        Cliente cliente = new Cliente();
        Optional<Cliente> cte = clienteRepositorio.findById(idCliente);
        if (cte.isPresent()) {
            cliente = cte.get();
        }

        Usuario usuario = new Usuario();
        Optional<Usuario> user = usuarioRepositorio.findById(idUsuario);
        if (user.isPresent()) {
            usuario = user.get();
        }

        String obsMayusculas = observacion.toUpperCase();
        Date f = convertirFecha(fecha);
        Long idRecibo = buscarUltimoIdOrg(idOrg);

        Recibo recibo = new Recibo();

        recibo.setIdOrg(idOrg);
        recibo.setCliente(cliente);
        recibo.setFecha(f);
        recibo.setObservacion(obsMayusculas);
        recibo.setImporte(importe);
        recibo.setUsuario(usuario);
        recibo.setIdRecibo(idRecibo + 1);

        reciboRepositorio.save(recibo);

        transaccionServicio.crearTransaccionRecibo(buscarUltimo(idOrg));

    }

    @Transactional
    public void modificarRecibo(Long idRecibo, Long idCliente, String fecha, Double importe, String observacion, Long idUsuario) throws ParseException { //modificar Cliente u observacion de Recibo

        Recibo recibo = new Recibo();
        Optional<Recibo> rec = reciboRepositorio.findById(idRecibo);
        if (rec.isPresent()) {
            recibo = rec.get();
        }

        Cliente cliente = new Cliente();
        Optional<Cliente> cte = clienteRepositorio.findById(idCliente);
        if (cte.isPresent()) {
            cliente = cte.get();
        }

        Usuario usuario = new Usuario();
        Optional<Usuario> user = usuarioRepositorio.findById(idUsuario);
        if (user.isPresent()) {
            usuario = user.get();
        }

        String obsMayusculas = observacion.toUpperCase();
        Date f = convertirFecha(fecha);

        recibo.setCliente(cliente);
        recibo.setFecha(f);
        recibo.setImporte(importe);
        recibo.setObservacion(obsMayusculas);
        recibo.setUsuario(usuario);

        reciboRepositorio.save(recibo);

        transaccionServicio.modificarTransaccionRecibo(idRecibo);

    }

    @Transactional
    public void eliminarRecibo(Long idRecibo) {

        Recibo recibo = new Recibo();
        Optional<Recibo> rec = reciboRepositorio.findById(idRecibo);
        if (rec.isPresent()) {
            recibo = rec.get();
        }

        transaccionServicio.eliminarTransaccionRecibo(idRecibo);

        recibo.setCliente(null);
        recibo.setUsuario(null);

        reciboRepositorio.save(recibo);

        reciboRepositorio.deleteById(idRecibo);

    }

    public ArrayList<Recibo> buscarRecibosIdCliente(Long id, String desde, String hasta) throws ParseException {

        Date d = convertirFecha(desde);
        Date h = convertirFecha(hasta);

        ArrayList<Recibo> lista = reciboRepositorio.findByFechaBetweenAndClienteId(d, h, id);

        Collections.sort(lista, ReciboComparador.ordenarFechaDesc); //ordena por nombre alfabetico los nombres de clientes

        return lista;

    }

    public ArrayList<Recibo> buscarRecibos(Long idOrg, String desde, String hasta) throws ParseException {

        Date d = convertirFecha(desde);
        Date h = convertirFecha(hasta);

        ArrayList<Recibo> lista = reciboRepositorio.findByFechaBetweenAndIdOrg(d, h, idOrg);

        Collections.sort(lista, ReciboComparador.ordenarFechaDesc); //ordena por nombre alfabetico los nombres de clientes

        return lista;

    }

    public Long buscarUltimoIdOrg(Long idOrg) {

        Recibo recibo = new Recibo();
        Optional<Recibo> rbo = reciboRepositorio.findTopByIdOrgOrderByIdDesc(idOrg);
        if (rbo.isPresent()) {
            recibo = rbo.get();

            return recibo.getIdRecibo();

        } else {

            int ultimo = 0;
            Long primero = Long.valueOf(ultimo);

            return primero;

        }

    }

    public Long buscarUltimo(Long idOrg) {

        return reciboRepositorio.ultimoRecibo(idOrg);

    }

    public Recibo buscarRecibo(Long id) {

        return reciboRepositorio.getById(id);
    }

    public Date convertirFecha(String fecha) throws ParseException {
        SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd");
        return formato.parse(fecha);
    }

}
