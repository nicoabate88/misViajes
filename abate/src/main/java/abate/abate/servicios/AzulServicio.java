package abate.abate.servicios;

import abate.abate.entidades.Azul;
import abate.abate.entidades.Camion;
import abate.abate.entidades.Usuario;
import abate.abate.repositorios.AzulRepositorio;
import abate.abate.repositorios.CamionRepositorio;
import abate.abate.repositorios.UsuarioRepositorio;
import abate.abate.util.AzulComparador;
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
public class AzulServicio {

    @Autowired
    private CamionRepositorio camionRepositorio;
    @Autowired
    private AzulRepositorio azulRepositorio;
    @Autowired
    private UsuarioRepositorio usuarioRepositorio;

    @Transactional
    public void crearCarga(Long idCamion, String fecha, Double litros, Usuario usuario) throws ParseException {

        Camion camion = new Camion();
        Optional<Camion> cam = camionRepositorio.findById(idCamion);
        if (cam.isPresent()) {
            camion = cam.get();
        }

        Date f = convertirFecha(fecha);

        Azul azul = new Azul();

        azul.setFecha(f);
        azul.setCamion(camion);
        azul.setUsuario(usuario);
        azul.setChofer(usuario);
        azul.setIdOrg(usuario.getIdOrg());
        azul.setLitro(litros);
        azul.setEstado("PENDIENTE");

        azulRepositorio.save(azul);

    }

    @Transactional
    public void crearCargaAdmin(Long idCamion, String fecha, Double litros, Usuario usuario) throws ParseException {

        Camion camion = new Camion();
        Optional<Camion> cam = camionRepositorio.findById(idCamion);
        if (cam.isPresent()) {
            camion = cam.get();
        }

        Date f = convertirFecha(fecha);

        Azul azul = new Azul();

        azul.setFecha(f);
        azul.setCamion(camion);
        azul.setUsuario(usuario);
        azul.setIdOrg(usuario.getIdOrg());
        azul.setLitro(litros);
        azul.setEstado("ACEPTADO");

        azulRepositorio.save(azul);

    }

    public Long buscarUltimo() {

        return azulRepositorio.ultimaCarga();

    }

    public Azul buscarAzul(Long id) {

        return azulRepositorio.getById(id);
    }

    public ArrayList<Azul> buscarCargasIdCamion(Long id, String desde, String hasta) throws ParseException {

        Camion camion = new Camion();
        Optional<Camion> cam = camionRepositorio.findById(id);
        if (cam.isPresent()) {
            camion = cam.get();
        }

        Date d = convertirFecha(desde);
        Date h = convertirFecha(hasta);

        ArrayList<Azul> lista = azulRepositorio.findByFechaBetweenAndCamion(d, h, camion);

        Collections.sort(lista, AzulComparador.ordenarFechaDesc);

        return lista;
    }

    public ArrayList<Azul> buscarCargasIdChofer(Long id, String desde, String hasta) throws ParseException {

        Usuario chofer = new Usuario();
        Optional<Usuario> chf = usuarioRepositorio.findById(id);
        if (chf.isPresent()) {
            chofer = chf.get();
        }

        Date d = convertirFecha(desde);
        Date h = convertirFecha(hasta);

        ArrayList<Azul> lista = azulRepositorio.findByFechaBetweenAndChofer(d, h, chofer);

        Collections.sort(lista, AzulComparador.ordenarFechaDesc);

        return lista;
    }

    public Long buscarIdCamion(Long idCarga) {

        Camion camion = azulRepositorio.findCamionByCargaId(idCarga);

        Long idCamion = camion.getId();

        return idCamion;

    }

    @Transactional
    public void aceptarCarga(Long idCarga, Usuario logueado) {

        Azul azul = new Azul();
        Optional<Azul> cga = azulRepositorio.findById(idCarga);
        if (cga.isPresent()) {
            azul = cga.get();
        }

        azul.setEstado("ACEPTADO");
        azul.setUsuario(logueado);

        azulRepositorio.save(azul);

    }

    @Transactional
    public void volverPendiente(Long idCarga, Usuario logueado) {

        Azul azul = new Azul();
        Optional<Azul> cga = azulRepositorio.findById(idCarga);
        if (cga.isPresent()) {
            azul = cga.get();
        }

        azul.setEstado("PENDIENTE");
        azul.setUsuario(logueado);

        azulRepositorio.save(azul);

    }

    @Transactional
    public void modificarCarga(Long id, String fecha, Long idCamion, Double litros, Usuario logueado) throws ParseException {

        Azul azul = new Azul();
        Optional<Azul> cga = azulRepositorio.findById(id);
        if (cga.isPresent()) {
            azul = cga.get();
        }

        Camion camion = camionRepositorio.getById(idCamion);

        Date f = convertirFecha(fecha);

        azul.setFecha(f);
        azul.setCamion(camion);
        azul.setLitro(litros);
        azul.setUsuario(logueado);

        azulRepositorio.save(azul);

    }

    @Transactional
    public void eliminarCarga(Long id) throws ParseException {

        Azul azul = new Azul();
        Optional<Azul> cga = azulRepositorio.findById(id);
        if (cga.isPresent()) {
            azul = cga.get();
        }

        azul.setChofer(null);
        azul.setCamion(null);
        azul.setUsuario(null);

        azulRepositorio.save(azul);

        azulRepositorio.deleteById(id);

    }

    public Date convertirFecha(String fecha) throws ParseException {
        SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd");
        return formato.parse(fecha);
    }

}
