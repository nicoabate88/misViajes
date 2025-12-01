package abate.abate.servicios;

import abate.abate.entidades.Acoplado;
import abate.abate.entidades.Camion;
import abate.abate.entidades.Mantenimiento;
import abate.abate.entidades.OrdenDeTrabajo;
import abate.abate.entidades.Usuario;
import abate.abate.repositorios.AcopladoRepositorio;
import abate.abate.repositorios.CamionRepositorio;
import abate.abate.repositorios.OrdenDeTrabajoRepositorio;
import abate.abate.repositorios.UsuarioRepositorio;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrdenDeTrabajoServicio {

    @Autowired
    private CamionRepositorio camionRepositorio;
    @Autowired
    private AcopladoRepositorio acopladoRepositorio;
    @Autowired
    private UsuarioRepositorio usuarioRepositorio;
    @Autowired
    private OrdenDeTrabajoRepositorio ordenRepositorio;

    @Transactional
    public OrdenDeTrabajo crearOrden(String fecha, Long idCamion, Long idAcoplado, String lugar, String proveedor, String responsable, Long idChofer,
            String observacion, Usuario usuario) throws ParseException {

        Date fechaAlta = convertirFecha(fecha);
        String obsMayusculas = observacion.toUpperCase();
        Long idOrden = buscarUltimoIdOrg(usuario.getIdOrg());

        OrdenDeTrabajo orden = new OrdenDeTrabajo();

        orden.setObservacion(obsMayusculas);
        orden.setFechaAlta(fechaAlta);
        orden.setUsuario(usuario);
        orden.setLugar(lugar);
        orden.setProveedor(proveedor);
        orden.setResponsable(responsable);
        orden.setIdOrg(usuario.getIdOrg());
        orden.setIdOrden(idOrden + 1);
        orden.setEstado(OrdenDeTrabajo.Estado.ABIERTA);
        Usuario chofer = usuarioRepositorio.getById(idChofer);
        orden.setChofer(chofer);
        if (idCamion != null) {
            Camion camion = camionRepositorio.getById(idCamion);
            orden.setCamion(camion);
        }
        if (idAcoplado != null) {
            Acoplado acoplado = acopladoRepositorio.getById(idAcoplado);
            orden.setAcoplado(acoplado);
        }

        orden = ordenRepositorio.save(orden);

        return orden;

    }

    @Transactional
    public void modificar(OrdenDeTrabajo orden) {

        ordenRepositorio.save(orden);

    }

    @Transactional
    public void actualizarEstadoOT(Long id) {

        OrdenDeTrabajo orden = ordenRepositorio.getById(id);

        List<Mantenimiento> mantenimientos = orden.getMantenimientos();

        long total = mantenimientos.size();
        long ejecutados = mantenimientos.stream()
                .filter(m -> m.getEstado() == Mantenimiento.Estado.VIGENTE)
                .count();

        long pendientes = total - ejecutados;

        if (ejecutados == 0) {
            orden.setEstado(OrdenDeTrabajo.Estado.ABIERTA);
            orden.setFechaCierre(null);
        } else if (pendientes > 0) {
            orden.setEstado(OrdenDeTrabajo.Estado.EN_PROCESO);
            orden.setFechaCierre(null);
        } else {
            orden.setEstado(OrdenDeTrabajo.Estado.CERRADA);
            orden.setFechaCierre(new Date());
        }

        ordenRepositorio.save(orden);
    }

    public Long buscarUltimo(Long idOrg) {

        return ordenRepositorio.ultimaOrden(idOrg);

    }

    public OrdenDeTrabajo buscarOrden(Long id) {

        return ordenRepositorio.getById(id);
    }

    public OrdenDeTrabajo buscarOrdenAbiertaCamion(Long idCamion) {

        List<OrdenDeTrabajo.Estado> estados = Arrays.asList(
                OrdenDeTrabajo.Estado.ABIERTA,
                OrdenDeTrabajo.Estado.EN_PROCESO
        );

        Optional<OrdenDeTrabajo> orn = ordenRepositorio.findByEstadoInAndCamionId(estados, idCamion);

        if (orn.isPresent()) {

            OrdenDeTrabajo orden = orn.get();

            return orden;

        } else {

            return null;

        }
    }

    public OrdenDeTrabajo buscarOrdenAbiertaAcoplado(Long idAcoplado) {

        List<OrdenDeTrabajo.Estado> estados = Arrays.asList(
                OrdenDeTrabajo.Estado.ABIERTA,
                OrdenDeTrabajo.Estado.EN_PROCESO
        );

        Optional<OrdenDeTrabajo> orn = ordenRepositorio.findByEstadoInAndAcopladoId(estados, idAcoplado);

        if (orn.isPresent()) {

            OrdenDeTrabajo orden = orn.get();

            return orden;

        } else {

            return null;

        }
    }

    public List<OrdenDeTrabajo> buscarOrdenes(Long id) {

        List<OrdenDeTrabajo> ordenes = ordenRepositorio.findByIdOrg(id);

        return ordenes;

    }

    public List<OrdenDeTrabajo> buscarOrdenAbiertaProceso(Long id) {

        List<OrdenDeTrabajo.Estado> estados = Arrays.asList(
                OrdenDeTrabajo.Estado.ABIERTA,
                OrdenDeTrabajo.Estado.EN_PROCESO
        );

        List<OrdenDeTrabajo> ordenes = ordenRepositorio.findByIdOrgAndEstadoIn(id, estados);

        return ordenes;
    }

    public List<OrdenDeTrabajo> buscarOrdenCerrada(Long id) {

        List<OrdenDeTrabajo> ordenes = ordenRepositorio.findByIdOrgAndEstado(id, OrdenDeTrabajo.Estado.CERRADA);

        return ordenes;
    }

    public List<OrdenDeTrabajo> buscarOrdenesCamion(Long idCamion) {

        List<OrdenDeTrabajo> ordenes = ordenRepositorio.findByCamionId(idCamion);

        return ordenes;
    }

    public List<OrdenDeTrabajo> buscarOrdenesAcoplado(Long idAcoplado) {

        List<OrdenDeTrabajo> ordenes = ordenRepositorio.findByAcopladoId(idAcoplado);

        return ordenes;
    }

    public List<OrdenDeTrabajo> buscarOrdenesCamionAcoplado(Long idCamion, Long idAcoplado) {

        List<OrdenDeTrabajo> ordenes = ordenRepositorio.findByCamionIdAndAcopladoId(idCamion, idAcoplado);

        return ordenes;

    }

    public List<OrdenDeTrabajo> buscarOrdenesCamionAcopladoAbiertaProceso(Long idCamion, Long idAcoplado) {

        List<OrdenDeTrabajo.Estado> estados = Arrays.asList(
                OrdenDeTrabajo.Estado.ABIERTA,
                OrdenDeTrabajo.Estado.EN_PROCESO
        );

        List<OrdenDeTrabajo> ordenes = ordenRepositorio.findByCamionIdAndAcopladoIdAndEstadoIn(idCamion, idAcoplado, estados);

        return ordenes;

    }

    public List<OrdenDeTrabajo> buscarOrdenesCamionAbiertaProceso(Long idCamion) {

        List<OrdenDeTrabajo.Estado> estados = Arrays.asList(
                OrdenDeTrabajo.Estado.ABIERTA,
                OrdenDeTrabajo.Estado.EN_PROCESO
        );

        List<OrdenDeTrabajo> ordenes = ordenRepositorio.findByCamionIdAndEstadoIn(idCamion, estados);

        return ordenes;

    }

    public List<OrdenDeTrabajo> buscarOrdenesAcopladoAbiertaProceso(Long idAcoplado) {

        List<OrdenDeTrabajo.Estado> estados = Arrays.asList(
                OrdenDeTrabajo.Estado.ABIERTA,
                OrdenDeTrabajo.Estado.EN_PROCESO
        );

        List<OrdenDeTrabajo> ordenes = ordenRepositorio.findByAcopladoIdAndEstadoIn(idAcoplado, estados);

        return ordenes;

    }

    public List<OrdenDeTrabajo> buscarOrdenesCamionCerrada(Long idCamion) {

        List<OrdenDeTrabajo> ordenes = ordenRepositorio.findByCamionIdAndEstado(idCamion, OrdenDeTrabajo.Estado.CERRADA);

        return ordenes;
    }

    public List<OrdenDeTrabajo> buscarOrdenesAcopladoCerrada(Long idAcoplado) {

        List<OrdenDeTrabajo> ordenes = ordenRepositorio.findByAcopladoIdAndEstado(idAcoplado, OrdenDeTrabajo.Estado.CERRADA);

        return ordenes;
    }

    public List<OrdenDeTrabajo> buscarOrdenesCamionAcopladoCerrada(Long idCamion, Long idAcoplado) {

        List<OrdenDeTrabajo> ordenes = ordenRepositorio.findByCamionIdAndAcopladoIdAndEstado(idCamion, idAcoplado, OrdenDeTrabajo.Estado.CERRADA);

        return ordenes;
    }

    public Long buscarUltimoIdOrg(Long idOrg) {

        Optional<OrdenDeTrabajo> orn = ordenRepositorio.findTopByIdOrgOrderByIdDesc(idOrg);
        if (orn.isPresent()) {
            OrdenDeTrabajo orden = orn.get();
            return orden.getIdOrden();

        } else {

            int ultimo = 0;
            Long primero = Long.valueOf(ultimo);

            return primero;

        }

    }

    public Date convertirFecha(String fecha) throws ParseException {
        SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd");
        return formato.parse(fecha);
    }

}
