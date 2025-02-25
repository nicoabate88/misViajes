package abate.abate.servicios;

import abate.abate.entidades.Ingreso;
import abate.abate.entidades.Usuario;
import abate.abate.repositorios.IngresoRepositorio;
import abate.abate.repositorios.UsuarioRepositorio;
import abate.abate.util.IngresoComparador;
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
public class IngresoServicio {

    @Autowired
    private UsuarioRepositorio usuarioRepositorio;
    @Autowired
    private TransaccionServicio transaccionServicio;
    @Autowired
    private IngresoRepositorio ingresoRepositorio;

    @Transactional
    public void crearIngreso(Long idOrg, Long idChofer, String fecha, Double importe, String observacion, Long idUsuario) throws ParseException {

        Usuario chofer = new Usuario();
        Optional<Usuario> chof = usuarioRepositorio.findById(idChofer);
        if (chof.isPresent()) {
            chofer = chof.get();
        }

        Usuario usuario = new Usuario();
        Optional<Usuario> user = usuarioRepositorio.findById(idUsuario);
        if (user.isPresent()) {
            usuario = user.get();
        }

        String obsMayusculas = observacion.toUpperCase();
        Date f = convertirFecha(fecha);
        Long idIngreso = buscarUltimoIdOrg(idOrg);

        Ingreso ingreso = new Ingreso();

        ingreso.setIdOrg(idOrg);
        ingreso.setChofer(chofer);
        ingreso.setFecha(f);
        ingreso.setObservacion(obsMayusculas);
        ingreso.setImporte(importe);
        ingreso.setUsuario(usuario);
        ingreso.setIdIngreso(idIngreso + 1);

        ingresoRepositorio.save(ingreso);

        transaccionServicio.crearTransaccionIngreso(buscarUltimo(idOrg));

    }

    @Transactional
    public void modificarIngreso(Long idIngreso, String fecha, Double importe, String observacion, Long idUsuario) throws ParseException {

        Ingreso ingreso = new Ingreso();
        Optional<Ingreso> ing = ingresoRepositorio.findById(idIngreso);
        if (ing.isPresent()) {
            ingreso = ing.get();
        }

        Usuario usuario = new Usuario();
        Optional<Usuario> user = usuarioRepositorio.findById(idUsuario);
        if (user.isPresent()) {
            usuario = user.get();
        }

        String obsMayusculas = observacion.toUpperCase();
        Date f = convertirFecha(fecha);

        ingreso.setFecha(f);
        ingreso.setImporte(importe);
        ingreso.setObservacion(obsMayusculas);
        ingreso.setUsuario(usuario);

        ingresoRepositorio.save(ingreso);

        transaccionServicio.modificarTransaccionIngreso(idIngreso);

    }

    @Transactional
    public void eliminarIngreso(Long idIngreso) {

        Ingreso ingreso = new Ingreso();
        Optional<Ingreso> ing = ingresoRepositorio.findById(idIngreso);
        if (ing.isPresent()) {
            ingreso = ing.get();
        }

        transaccionServicio.eliminarTransaccionIngreso(idIngreso);

        ingreso.setChofer(null);
        ingreso.setUsuario(null);

        ingresoRepositorio.save(ingreso);

        ingresoRepositorio.deleteById(idIngreso);

    }

    public Long buscarUltimoIdOrg(Long idOrg) {

        Ingreso ingreso = new Ingreso();
        Optional<Ingreso> ing = ingresoRepositorio.findTopByIdOrgOrderByIdDesc(idOrg);
        if (ing.isPresent()) {
            ingreso = ing.get();

            return ingreso.getIdIngreso();

        } else {

            int ultimo = 0;
            Long primero = Long.valueOf(ultimo);

            return primero;

        }

    }

    public Long buscarUltimo(Long idOrg) {

        return ingresoRepositorio.ultimoIngreso(idOrg);
    }

    public Ingreso buscarIngreso(Long id) {

        return ingresoRepositorio.getById(id);
    }

    public ArrayList<Ingreso> buscarIngresos(Long id) {

        ArrayList<Ingreso> lista = ingresoRepositorio.buscarIngresosIdChofer(id);

        Collections.sort(lista, IngresoComparador.ordenarFechaDesc);

        return lista;

    }

    public Date convertirFecha(String fecha) throws ParseException {
        SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd");
        return formato.parse(fecha);
    }

}
