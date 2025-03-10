package abate.abate.servicios;

import abate.abate.entidades.Camion;
import abate.abate.entidades.Detalle;
import abate.abate.entidades.Flete;
import abate.abate.entidades.Gasto;
import abate.abate.entidades.Transaccion;
import abate.abate.entidades.Usuario;
import abate.abate.repositorios.CamionRepositorio;
import abate.abate.repositorios.DetalleRepositorio;
import abate.abate.repositorios.FleteRepositorio;
import abate.abate.repositorios.GastoRepositorio;
import abate.abate.repositorios.TransaccionRepositorio;
import abate.abate.repositorios.UsuarioRepositorio;
import abate.abate.util.GastoComparador;
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
public class GastoServicio {

    @Autowired
    private UsuarioRepositorio usuarioRepositorio;
    @Autowired
    private TransaccionServicio transaccionServicio;
    @Autowired
    private GastoRepositorio gastoRepositorio;
    @Autowired
    private FleteRepositorio fleteRepositorio;
    @Autowired
    private CamionRepositorio camionRepositorio;
    @Autowired
    private TransaccionRepositorio transaccionRepositorio;
    @Autowired
    private DetalleRepositorio detalleRepositorio;
    @Autowired
    private ImagenServicio imagenServicio;

    @Transactional
    public void registrarGastoFlete(List<Detalle> detalles, Long idFlete, Usuario logueado) {

        Gasto gasto = new Gasto();
        Flete flete = new Flete();
        Optional<Flete> fte = fleteRepositorio.findById(idFlete);
        if (fte.isPresent()) {
            flete = fte.get();
        }

        double importe = 0;
        for (Detalle detalle : detalles) {
            detalle.setGasto(gasto);
            importe += detalle.getTotal();
        }

        detalleRepositorio.saveAll(detalles);

        String nombre = "GASTO VIAJE ID" + flete.getIdFlete();
        gasto.setIdOrg(logueado.getIdOrg());
        gasto.setChofer(flete.getChofer());
        gasto.setFecha(flete.getFechaFlete());
        gasto.setNombre(nombre);
        gasto.setImporte(importe);
        gasto.setUsuario(logueado);
        gasto.setCamion(flete.getCamion());
        gasto.setDetalles(detalles);
        if (logueado.getRol().equalsIgnoreCase("CHOFER")) {
            gasto.setEstado("PENDIENTE");
        } else {
            gasto.setEstado("ACEPTADO");
        }

        gastoRepositorio.save(gasto);

        Gasto nuevoGasto = gastoRepositorio.buscarUltimoGasto(logueado.getIdOrg());

        flete.setGasto(nuevoGasto);
        fleteRepositorio.save(flete);   //persiste Gasto en el flete correspondiente

        if (logueado.getRol().equalsIgnoreCase("ADMIN")) {
            transaccionServicio.crearTransaccionGasto(nuevoGasto.getId());
        }

    }

    public Gasto buscarUltimoGasto(Long idOrg) {

        return gastoRepositorio.buscarUltimoGasto(idOrg);
    }

    @Transactional
    public void aceptarGastoCaja(Long idGasto, Usuario logueado) {

        Gasto gasto = new Gasto();
        Optional<Gasto> gto = gastoRepositorio.findById(idGasto);
        if (gto.isPresent()) {
            gasto = gto.get();
        }

        gasto.setEstado("ACEPTADO");
        gasto.setUsuario(logueado);

        gastoRepositorio.save(gasto);

    }

    @Transactional
    public void volverPendienteGasto(Long idGasto, Usuario logueado) {

        Gasto gasto = new Gasto();
        Optional<Gasto> gto = gastoRepositorio.findById(idGasto);
        if (gto.isPresent()) {
            gasto = gto.get();
        }
        gasto.setEstado("PENDIENTE");
        gasto.setUsuario(logueado);

        gastoRepositorio.save(gasto);

    }

    @Transactional
    public void modificarChoferGasto(Gasto gasto, Usuario chofer) {

        gasto.setChofer(chofer);

        gastoRepositorio.save(gasto);

    }

    public Long buscarUltimo(Long idOrg) {

        return gastoRepositorio.ultimoGasto(idOrg);
    }

    public Gasto buscarGasto(Long id) {

        return gastoRepositorio.getById(id);
    }

    public Gasto buscarGastoIdImagen(Long id) {

        return gastoRepositorio.buscarGastoIdImagen(id);
    }

    public Long buscarUltimoIdOrg(Long idOrg) {

        Gasto gasto = new Gasto();
        Optional<Gasto> gto = gastoRepositorio.findTopByIdOrgOrderByIdGastoDesc(idOrg);
        if (gto.isPresent() && gto.get().getIdGasto() != null) {
            gasto = gto.get();

            return gasto.getIdGasto();

        } else {

            int ultimo = 0;
            Long primero = Long.valueOf(ultimo);

            return primero;

        }

    }

    @Transactional
    public void modificarGastoFlete(Long idGasto, List<Detalle> nuevosDetalles, Usuario usuario) {

        Gasto gasto = new Gasto();
        Optional<Gasto> gto = gastoRepositorio.findById(idGasto);
        if (gto.isPresent()) {
            gasto = gto.get();
        }

        detalleRepositorio.deleteAll(gasto.getDetalles());
        gasto.getDetalles().clear();

        double importeTotal = 0;
        for (Detalle detalle : nuevosDetalles) {
            detalle.setGasto(gasto);
            importeTotal += detalle.getTotal();
            gasto.getDetalles().add(detalle);
        }

        gasto.setImporte(importeTotal);
        gasto.setUsuario(usuario);
        gastoRepositorio.save(gasto);

        Transaccion transaccion = transaccionRepositorio.buscarTransaccionIdGasto(idGasto);
        if (transaccion != null) {
            transaccionServicio.modificarTransaccionGasto(gasto.getId());
        }
    }

    @Transactional
    public void eliminarGastoFlete(Long idGasto, Long idFlete) {

        Flete flete = new Flete();
        Optional<Flete> fte = fleteRepositorio.findById(idFlete);
        if (fte.isPresent()) {
            flete = fte.get();
        }

        flete.setGasto(null);

        fleteRepositorio.save(flete);

        Gasto gasto = new Gasto();
        Optional<Gasto> gto = gastoRepositorio.findById(idGasto);
        if (gto.isPresent()) {
            gasto = gto.get();
        }

        if (gasto.getImagen() != null) {
            imagenServicio.eliminarImagenGasto(gasto.getImagen().getId());
        }

        Transaccion transaccion = transaccionRepositorio.buscarTransaccionIdGasto(idGasto);
        if (transaccion != null) {
            transaccionServicio.eliminarTransaccionGasto(idGasto);
        }

        List<Detalle> lista = gasto.getDetalles();

        for (Detalle d : lista) {

            d.setGasto(null);
            detalleRepositorio.save(d);

            detalleRepositorio.deleteById(d.getId());

        }

        gasto.setCamion(null);
        gasto.setChofer(null);
        gasto.setUsuario(null);
        gasto.setDetalles(null);

        gastoRepositorio.save(gasto);

        gastoRepositorio.deleteById(idGasto);

    }

    @Transactional
    public void registrarGastoCaja(Long idChofer, String fecha, Long idCamion, List<Detalle> detalles, Usuario logueado) throws ParseException {

        Gasto gasto = new Gasto();

        Usuario chofer = usuarioRepositorio.getById(idChofer);
        Camion camion = camionRepositorio.getById(idCamion);
        Date f = convertirFecha(fecha);
        Long idGasto = buscarUltimoIdOrg(logueado.getIdOrg());

        double importe = 0;
        for (Detalle detalle : detalles) {
            detalle.setGasto(gasto);
            importe += detalle.getTotal();
        }

        detalleRepositorio.saveAll(detalles);

        gasto.setNombre("GASTO ID" + (idGasto + 1));
        gasto.setIdOrg(logueado.getIdOrg());
        gasto.setIdGasto(idGasto + 1);
        gasto.setChofer(chofer);
        gasto.setFecha(f);
        gasto.setImporte(importe);
        gasto.setUsuario(logueado);
        gasto.setCamion(camion);
        gasto.setDetalles(detalles);
        if (logueado.getRol().equalsIgnoreCase("CHOFER")) {
            gasto.setEstado("PENDIENTE");
        } else {
            gasto.setEstado("ACEPTADO");
        }

        gastoRepositorio.save(gasto);

        Gasto nuevoGasto = gastoRepositorio.buscarUltimoGasto(logueado.getIdOrg());

        transaccionServicio.crearTransaccionGasto(nuevoGasto.getId());

    }

    @Transactional
    public void modificarGastoCaja(Long idGasto, String fecha, Long idCamion, List<Detalle> nuevosDetalles, Usuario usuario) throws ParseException {

        Gasto gasto = new Gasto();
        Optional<Gasto> gto = gastoRepositorio.findById(idGasto);
        if (gto.isPresent()) {
            gasto = gto.get();
        }

        Camion camion = camionRepositorio.getById(idCamion);
        Date f = convertirFecha(fecha);

        detalleRepositorio.deleteAll(gasto.getDetalles());
        gasto.getDetalles().clear();

        double importeTotal = 0;
        for (Detalle detalle : nuevosDetalles) {
            detalle.setGasto(gasto);
            importeTotal += detalle.getTotal();
            gasto.getDetalles().add(detalle);
        }

        gasto.setImporte(importeTotal);
        gasto.setUsuario(usuario);
        gasto.setCamion(camion);
        gasto.setFecha(f);
        gastoRepositorio.save(gasto);

        Transaccion transaccion = transaccionRepositorio.buscarTransaccionIdGasto(idGasto);
        if (transaccion != null) {
            transaccionServicio.modificarTransaccionGasto(gasto.getId());
        }
    }

    @Transactional
    public void eliminarGastoCaja(Long idGasto) {

        Gasto gasto = new Gasto();
        Optional<Gasto> gto = gastoRepositorio.findById(idGasto);
        if (gto.isPresent()) {
            gasto = gto.get();
        }

        if (gasto.getImagen() != null) {
            imagenServicio.eliminarImagenGasto(gasto.getImagen().getId());
        }

        Transaccion transaccion = transaccionRepositorio.buscarTransaccionIdGasto(idGasto);
        if (transaccion != null) {
            transaccionServicio.eliminarTransaccionGasto(idGasto);
        }

        List<Detalle> lista = gasto.getDetalles();

        for (Detalle d : lista) {

            d.setGasto(null);
            detalleRepositorio.save(d);

            detalleRepositorio.deleteById(d.getId());

        }

        gasto.setCamion(null);
        gasto.setChofer(null);
        gasto.setUsuario(null);
        gasto.setDetalles(null);

        gastoRepositorio.save(gasto);

        gastoRepositorio.deleteById(idGasto);

    }

    public ArrayList<Gasto> buscarGastosCamion(Long idCamion, String desde, String hasta) throws ParseException {

        Date d = convertirFecha(desde);
        Date h = convertirFecha(hasta);

        ArrayList<Gasto> lista = gastoRepositorio.findByFechaBetweenAndCamionId(d, h, idCamion);

        Collections.sort(lista, GastoComparador.ordenarFechaDesc);

        return lista;

    }

    public Date convertirFecha(String fecha) throws ParseException {
        SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd");
        return formato.parse(fecha);
    }
}
