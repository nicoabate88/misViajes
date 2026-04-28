package abate.abate.servicios;

import abate.abate.entidades.Cliente;
import abate.abate.entidades.Recibo;
import abate.abate.entidades.Usuario;
import abate.abate.entidades.Valor;
import abate.abate.repositorios.ClienteRepositorio;
import abate.abate.repositorios.ReciboRepositorio;
import abate.abate.util.ReciboComparador;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReciboServicio {

    @Autowired
    private ClienteRepositorio clienteRepositorio;
    @Autowired
    private ReciboRepositorio reciboRepositorio;
    @Autowired
    private TransaccionServicio transaccionServicio;

    @Transactional
    public void crearRecibo(Long idOrg, Long idCliente, String fecha, List<Valor> valores, String observacion, Usuario usuario) throws ParseException {

        Cliente cliente = clienteRepositorio.getById(idCliente);

        String obsMayusculas = observacion.toUpperCase();
        Date f = convertirFecha(fecha);
        Long idRecibo = buscarUltimoIdOrg(idOrg);

        Recibo recibo = new Recibo();
        recibo.setIdOrg(idOrg);
        recibo.setCliente(cliente);
        recibo.setFecha(f);
        recibo.setObservacion(obsMayusculas);
        recibo.setUsuario(usuario);
        recibo.setIdRecibo(idRecibo + 1);

        Double total = 0.0;

        for (Valor v : valores) {

            recibo.addValor(v);   // mantiene relación bidireccional
            total += v.getImporte();
        }

        recibo.setImporte(total);

        reciboRepositorio.save(recibo);

        transaccionServicio.crearTransaccionRecibo(buscarUltimo(idOrg));

    }

    @Transactional
    public void modificarRecibo(Long idRecibo, Long idCliente, String fecha, List<Valor> nuevosValores, String observacion, Usuario usuario) throws ParseException {

        Recibo recibo = reciboRepositorio.getById(idRecibo);

        Cliente cliente = clienteRepositorio.getById(idCliente);

        String obsMayusculas = observacion.toUpperCase();
        Date f = convertirFecha(fecha);

        recibo.setCliente(cliente);
        recibo.setFecha(f);
        recibo.setObservacion(obsMayusculas);
        recibo.setUsuario(usuario);

        recibo.getValores().clear();

        Double total = 0.0;

        for (Valor v : nuevosValores) {
            recibo.addValor(v);
            total += v.getImporte();
        }

        recibo.setImporte(total);

        reciboRepositorio.save(recibo);

        transaccionServicio.modificarTransaccionRecibo(idRecibo);
    }

    @Transactional
    public void eliminarRecibo(Long idRecibo) {

        Recibo recibo = reciboRepositorio.getById(idRecibo);

        transaccionServicio.eliminarTransaccionRecibo(idRecibo);

        reciboRepositorio.delete(recibo);
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

        return reciboRepositorio.findByIdWithValores(id);

    }

    public Date convertirFecha(String fecha) throws ParseException {
        SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd");
        return formato.parse(fecha);
    }

}
