package abate.abate.servicios;

import abate.abate.entidades.Acoplado;
import abate.abate.entidades.Camion;
import abate.abate.entidades.Mantenimiento;
import abate.abate.entidades.OrdenDeTrabajo;
import abate.abate.entidades.Proveedor;
import abate.abate.entidades.Usuario;
import abate.abate.repositorios.AcopladoRepositorio;
import abate.abate.repositorios.CamionRepositorio;
import abate.abate.repositorios.MantenimientoRepositorio;
import abate.abate.repositorios.OrdenDeTrabajoRepositorio;
import abate.abate.repositorios.ProveedorRepositorio;
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
    private OrdenDeTrabajoRepositorio ordenRepositorio;
    @Autowired
    private ProveedorRepositorio proveedorRepositorio;
    @Autowired
    private MantenimientoRepositorio mantenimientoRepositorio;

    @Transactional
    public OrdenDeTrabajo crearOrden(Long idCamion, Long idAcoplado, Long idProveedor, String observacion,
            String fechaInicio, String fechaFin, Usuario usuario) throws ParseException {

        String obsMayusculas = observacion.toUpperCase();
        Long idOrden = buscarUltimoIdOrg(usuario.getIdOrg());

        OrdenDeTrabajo orden = new OrdenDeTrabajo();

        Date fechaI = convertirFecha(fechaInicio);
        orden.setFechaInicio(fechaI);

        Date fechaF = convertirFecha(fechaFin);
        orden.setFechaFin(fechaF);

        orden.setObservacion(obsMayusculas);
        orden.setFechaAlta(new Date());
        orden.setUsuario(usuario);
        orden.setIdOrg(usuario.getIdOrg());
        orden.setIdOrden(idOrden + 1);
        orden.setEstado(OrdenDeTrabajo.Estado.ABIERTO);

        Proveedor proveedor = proveedorRepositorio.getById(idProveedor);
        orden.setProveedor(proveedor);

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
    public void modificarOtCerrado(Long idOt) {

        List<Mantenimiento> lista = mantenimientoRepositorio.findByOrdenDeTrabajoIdAndEstado(idOt, Mantenimiento.Estado.PENDIENTE);

        if (lista != null) {

            for (Mantenimiento mantenimiento : lista) {
                mantenimiento.setCamion(null);
                mantenimiento.setAcoplado(null);
                mantenimiento.setUsuario(null);
                mantenimiento.setTipoMantenimiento(null);

                mantenimientoRepositorio.save(mantenimiento);

                mantenimientoRepositorio.deleteById(mantenimiento.getId());
            }

        }

        OrdenDeTrabajo orden = ordenRepositorio.getById(idOt);

        orden.setEstado(OrdenDeTrabajo.Estado.CERRADO);
        orden.setFechaCierre(new Date());

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
            orden.setEstado(OrdenDeTrabajo.Estado.ABIERTO);
            orden.setFechaCierre(null);
        } else if (pendientes > 0) {
            orden.setEstado(OrdenDeTrabajo.Estado.EN_PROCESO);
            orden.setFechaCierre(null);
        } else {
            orden.setEstado(OrdenDeTrabajo.Estado.CERRADO);
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
                OrdenDeTrabajo.Estado.ABIERTO,
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
                OrdenDeTrabajo.Estado.ABIERTO,
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

    public List<OrdenDeTrabajo> buscarOrdenAbiertaProcesoProveedor(Long id, Long idProveedor) {

        List<OrdenDeTrabajo.Estado> estados = Arrays.asList(
                OrdenDeTrabajo.Estado.ABIERTO,
                OrdenDeTrabajo.Estado.EN_PROCESO
        );

        List<OrdenDeTrabajo> ordenes = ordenRepositorio.findByIdOrgAndProveedorIdAndEstadoIn(id, idProveedor, estados);

        return ordenes;
    }

    public List<OrdenDeTrabajo> buscarOrdenesCamionAcopladoAbiertaProcesoProveedor(Long idCamion, Long idAcoplado, Long idProveedor) {

        List<OrdenDeTrabajo.Estado> estados = Arrays.asList(
                OrdenDeTrabajo.Estado.ABIERTO,
                OrdenDeTrabajo.Estado.EN_PROCESO
        );

        List<OrdenDeTrabajo> ordenes = ordenRepositorio.findByCamionIdAndAcopladoIdAndProveedorIdAndEstadoIn(idCamion, idAcoplado, idProveedor, estados);

        return ordenes;

    }

    public List<OrdenDeTrabajo> buscarOrdenesCamionAbiertaProcesoProveedor(Long idCamion, Long idProveedor) {

        List<OrdenDeTrabajo.Estado> estados = Arrays.asList(
                OrdenDeTrabajo.Estado.ABIERTO,
                OrdenDeTrabajo.Estado.EN_PROCESO
        );

        List<OrdenDeTrabajo> ordenes = ordenRepositorio.findByCamionIdAndProveedorIdAndEstadoIn(idCamion, idProveedor, estados);

        return ordenes;

    }

    public List<OrdenDeTrabajo> buscarOrdenesAcopladoAbiertaProcesoProveedor(Long idAcoplado, Long idProveedor) {

        List<OrdenDeTrabajo.Estado> estados = Arrays.asList(
                OrdenDeTrabajo.Estado.ABIERTO,
                OrdenDeTrabajo.Estado.EN_PROCESO
        );

        List<OrdenDeTrabajo> ordenes = ordenRepositorio.findByAcopladoIdAndProveedorIdAndEstadoIn(idAcoplado, idProveedor, estados);

        return ordenes;

    }

    public List<OrdenDeTrabajo> buscarOrdenes(Long id) {

        List<OrdenDeTrabajo> ordenes = ordenRepositorio.findByIdOrg(id);

        return ordenes;

    }

    public List<OrdenDeTrabajo> buscarOrdenesProveedor(Long id, Long idProveedor) {

        List<OrdenDeTrabajo> ordenes = ordenRepositorio.findByIdOrgAndProveedorId(id, idProveedor);

        return ordenes;

    }

    public List<OrdenDeTrabajo> buscarOrdenAbiertaProceso(Long id) {

        List<OrdenDeTrabajo.Estado> estados = Arrays.asList(
                OrdenDeTrabajo.Estado.ABIERTO,
                OrdenDeTrabajo.Estado.EN_PROCESO
        );

        List<OrdenDeTrabajo> ordenes = ordenRepositorio.findByIdOrgAndEstadoIn(id, estados);

        return ordenes;
    }

    public List<OrdenDeTrabajo> buscarOrdenCerrada(Long id) {

        List<OrdenDeTrabajo> ordenes = ordenRepositorio.findByIdOrgAndEstado(id, OrdenDeTrabajo.Estado.CERRADO);

        return ordenes;
    }

    public List<OrdenDeTrabajo> buscarOrdenCerradaProveedor(Long id, Long idProveedor) {

        List<OrdenDeTrabajo> ordenes = ordenRepositorio.findByIdOrgAndProveedorIdAndEstado(id, idProveedor, OrdenDeTrabajo.Estado.CERRADO);

        return ordenes;
    }

    public List<OrdenDeTrabajo> buscarOrdenesCamionCerradaProveedor(Long idCamion, Long idProveedor) {

        List<OrdenDeTrabajo> ordenes = ordenRepositorio.findByCamionIdAndProveedorIdAndEstado(idCamion, idProveedor, OrdenDeTrabajo.Estado.CERRADO);

        return ordenes;
    }

    public List<OrdenDeTrabajo> buscarOrdenesAcopladoCerradaProveedor(Long idAcoplado, Long idProveedor) {

        List<OrdenDeTrabajo> ordenes = ordenRepositorio.findByAcopladoIdAndProveedorIdAndEstado(idAcoplado, idProveedor, OrdenDeTrabajo.Estado.CERRADO);

        return ordenes;
    }

    public List<OrdenDeTrabajo> buscarOrdenesCamion(Long idCamion) {

        List<OrdenDeTrabajo> ordenes = ordenRepositorio.findByCamionId(idCamion);

        return ordenes;
    }

    public List<OrdenDeTrabajo> buscarOrdenesCamionProveedor(Long idCamion, Long idProveedor) {

        List<OrdenDeTrabajo> ordenes = ordenRepositorio.findByCamionIdAndProveedorId(idCamion, idProveedor);

        return ordenes;
    }

    public List<OrdenDeTrabajo> buscarOrdenesAcopladoProveedor(Long idAcoplado, Long idProveedor) {

        List<OrdenDeTrabajo> ordenes = ordenRepositorio.findByAcopladoIdAndProveedorId(idAcoplado, idProveedor);

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
                OrdenDeTrabajo.Estado.ABIERTO,
                OrdenDeTrabajo.Estado.EN_PROCESO
        );

        List<OrdenDeTrabajo> ordenes = ordenRepositorio.findByCamionIdAndAcopladoIdAndEstadoIn(idCamion, idAcoplado, estados);

        return ordenes;

    }

    public List<OrdenDeTrabajo> buscarOrdenesCamionAcopladoCerradaProveedor(Long idCamion, Long idAcoplado, Long idProveedor) {

        List<OrdenDeTrabajo> ordenes = ordenRepositorio.findByCamionIdAndAcopladoIdAndProveedorIdAndEstado(idCamion, idAcoplado, idProveedor, OrdenDeTrabajo.Estado.CERRADO);

        return ordenes;
    }

    public List<OrdenDeTrabajo> buscarOrdenesCamionAcopladoProveedor(Long idCamion, Long idAcoplado, Long idProveedor) {

        List<OrdenDeTrabajo> ordenes = ordenRepositorio.findByCamionIdAndAcopladoIdAndProveedorId(idCamion, idAcoplado, idProveedor);

        return ordenes;

    }

    public List<OrdenDeTrabajo> buscarOrdenesCamionAbiertaProceso(Long idCamion) {

        List<OrdenDeTrabajo.Estado> estados = Arrays.asList(
                OrdenDeTrabajo.Estado.ABIERTO,
                OrdenDeTrabajo.Estado.EN_PROCESO
        );

        List<OrdenDeTrabajo> ordenes = ordenRepositorio.findByCamionIdAndEstadoIn(idCamion, estados);

        return ordenes;

    }

    public List<OrdenDeTrabajo> buscarOrdenesAcopladoAbiertaProceso(Long idAcoplado) {

        List<OrdenDeTrabajo.Estado> estados = Arrays.asList(
                OrdenDeTrabajo.Estado.ABIERTO,
                OrdenDeTrabajo.Estado.EN_PROCESO
        );

        List<OrdenDeTrabajo> ordenes = ordenRepositorio.findByAcopladoIdAndEstadoIn(idAcoplado, estados);

        return ordenes;

    }

    public List<OrdenDeTrabajo> buscarOrdenesCamionCerrada(Long idCamion) {

        List<OrdenDeTrabajo> ordenes = ordenRepositorio.findByCamionIdAndEstado(idCamion, OrdenDeTrabajo.Estado.CERRADO);

        return ordenes;
    }

    public List<OrdenDeTrabajo> buscarOrdenesAcopladoCerrada(Long idAcoplado) {

        List<OrdenDeTrabajo> ordenes = ordenRepositorio.findByAcopladoIdAndEstado(idAcoplado, OrdenDeTrabajo.Estado.CERRADO);

        return ordenes;
    }

    public List<OrdenDeTrabajo> buscarOrdenesCamionAcopladoCerrada(Long idCamion, Long idAcoplado) {

        List<OrdenDeTrabajo> ordenes = ordenRepositorio.findByCamionIdAndAcopladoIdAndEstado(idCamion, idAcoplado, OrdenDeTrabajo.Estado.CERRADO);

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
