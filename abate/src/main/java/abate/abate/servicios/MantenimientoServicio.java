package abate.abate.servicios;

import abate.abate.entidades.Mantenimiento;
import abate.abate.entidades.TipoMantenimiento;
import abate.abate.entidades.Usuario;
import abate.abate.repositorios.MantenimientoRepositorio;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MantenimientoServicio {

    @Autowired
    private MantenimientoRepositorio mantenimientoRepositorio;
    @Autowired
    private CombustibleServicio combustibleServicio;

    @Transactional
    public void crearMantenimiento(Mantenimiento mantenimiento, Mantenimiento mantenimientoExistente) {

        if (mantenimientoExistente != null) {

            mantenimientoExistente.setEstado(Mantenimiento.Estado.ACTUALIZADO);
            mantenimientoExistente.setKmActual(mantenimiento.getKm());
            mantenimientoExistente.setKmVigencia(mantenimiento.getKm() - mantenimientoExistente.getKm());
            mantenimientoExistente.setFechaActualizado(mantenimiento.getFecha());

            mantenimientoRepositorio.save(mantenimientoExistente);

        }

        mantenimientoRepositorio.save(mantenimiento);

    }

    public Mantenimiento buscarExistente(Mantenimiento mantenimiento) {

        Mantenimiento buscar = null;

        if (mantenimiento.getAplicaA() == TipoMantenimiento.AplicaA.CAMION) {
            Optional<Mantenimiento> mant = mantenimientoRepositorio.buscarMantenimientoVigenteCamion(Mantenimiento.Estado.VIGENTE, mantenimiento.getCamion().getId(), mantenimiento.getTipoMantenimiento().getId());
            if (mant.isPresent()) {
                buscar = mant.get();

                int kmActual = combustibleServicio.kmUltimaCarga(buscar.getCamion());
                int kmAlarma = buscar.getKmAlarma();
                int kmProximo = buscar.getKmProximo();

                buscar.setKmVigencia(kmProximo - kmActual);
                buscar.setKmActual(kmActual);

                if (kmAlarma <= kmActual && kmProximo > kmActual) {
                    buscar.setEstado(Mantenimiento.Estado.PROXIMO_A_VENCER);
                }
                if (kmProximo <= kmActual) {
                    buscar.setEstado(Mantenimiento.Estado.VENCIDO);
                }
            }
        } else {

            Optional<Mantenimiento> mant = mantenimientoRepositorio.buscarMantenimientoVigenteAcoplado(Mantenimiento.Estado.VIGENTE, mantenimiento.getAcoplado().getId(), mantenimiento.getTipoMantenimiento().getId());
            if (mant.isPresent()) {
                buscar = mant.get();

                int kmActual = combustibleServicio.kmAcoplado(mantenimiento.getAcoplado(), obtenerFechaFija());
                int kmAlarma = buscar.getKmAlarma();
                int kmProximo = buscar.getKmProximo();

                buscar.setKmActual(kmActual);
                buscar.setKmVigencia(kmProximo - kmActual);

                if (kmAlarma <= kmActual && kmProximo > kmActual) {
                    mantenimiento.setEstado(Mantenimiento.Estado.PROXIMO_A_VENCER);
                }
                if (kmProximo <= kmActual) {
                    mantenimiento.setEstado(Mantenimiento.Estado.VENCIDO);
                }
            }
        }

        return buscar;

    }

    public Mantenimiento buscarExistenteMasivo(Mantenimiento mantenimiento) {

        Mantenimiento buscar = null;

        if (mantenimiento.getAplicaA() == TipoMantenimiento.AplicaA.CAMION) {
            Optional<Mantenimiento> mant = mantenimientoRepositorio.buscarMantenimientoVigenteCamion(Mantenimiento.Estado.VIGENTE, mantenimiento.getCamion().getId(), mantenimiento.getTipoMantenimiento().getId());
            if (mant.isPresent()) {

                buscar = mant.get();

            }
        } else {

            Optional<Mantenimiento> mant = mantenimientoRepositorio.buscarMantenimientoVigenteAcoplado(Mantenimiento.Estado.VIGENTE, mantenimiento.getAcoplado().getId(), mantenimiento.getTipoMantenimiento().getId());
            if (mant.isPresent()) {

                buscar = mant.get();

            }
        }

        return buscar;
    }

    @Transactional
    public void modificarVigenteOt(Mantenimiento mantenimiento, Mantenimiento mantenimientoVigente) {

        mantenimientoVigente.setEstado(Mantenimiento.Estado.ACTUALIZADO);
        mantenimientoVigente.setKmActual(mantenimiento.getKm());
        mantenimientoVigente.setKmVigencia(mantenimiento.getKm() - mantenimientoVigente.getKm());
        mantenimientoVigente.setFechaActualizado(mantenimiento.getFecha());
        mantenimientoVigente.setUsuario(mantenimiento.getUsuario());

        mantenimientoRepositorio.save(mantenimientoVigente);

    }

    @Transactional
    public void modificarMantenimiento(Long id, String fecha, Integer km, Integer kmProximo, Integer kmAlarma, String observacion, Usuario usuario) throws ParseException {

        Mantenimiento mantenimiento = mantenimientoRepositorio.getById(id);

        String obsMayusculas = observacion.toUpperCase();
        Date fechaMantenimiento = convertirFecha(fecha);

        mantenimiento.setFecha(fechaMantenimiento);
        mantenimiento.setKm(km);
        mantenimiento.setKmProximo(kmProximo);
        mantenimiento.setKmAlarma(kmAlarma);
        mantenimiento.setObservacion(obsMayusculas);
        mantenimiento.setUsuario(usuario);

        mantenimientoRepositorio.save(mantenimiento);

    }

    @Transactional
    public void eliminarMantenimiento(Long id) {

        Mantenimiento mantenimiento = mantenimientoRepositorio.getById(id);

        mantenimiento.setCamion(null);
        mantenimiento.setAcoplado(null);
        mantenimiento.setUsuario(null);
        mantenimiento.setTipoMantenimiento(null);

        mantenimientoRepositorio.save(mantenimiento);

        mantenimientoRepositorio.deleteById(id);

    }

    public Long buscarUltimo(Long idOrg) {

        return mantenimientoRepositorio.ultimoMantenimiento(idOrg);

    }

    public Mantenimiento buscarMantenimiento(Long id) {

        return mantenimientoRepositorio.getById(id);
    }

    public Mantenimiento buscarMantenimientoDiasVigencia(Long id) {

        Mantenimiento mantenimiento = mantenimientoRepositorio.getById(id);
        int kmActual;
        int kmAlarma = mantenimiento.getKmAlarma();
        int kmProximo = mantenimiento.getKmProximo();

        if (mantenimiento.getAplicaA() == TipoMantenimiento.AplicaA.CAMION) {
            kmActual = combustibleServicio.kmUltimaCarga(mantenimiento.getCamion());
        } else {
            kmActual = combustibleServicio.kmAcoplado(mantenimiento.getAcoplado(), obtenerFechaFija());
        }

        mantenimiento.setKmActual(kmActual);
        mantenimiento.setKmVigencia(kmProximo - kmActual);

        if (kmAlarma <= kmActual && kmProximo > kmActual) {
            mantenimiento.setEstado(Mantenimiento.Estado.PROXIMO_A_VENCER);
        }
        if (kmProximo <= kmActual) {
            mantenimiento.setEstado(Mantenimiento.Estado.VENCIDO);
        }

        return mantenimiento;
    }

    public List<Mantenimiento> buscarMantenimientoVigenteIdCamion(Long id) {

        List<Mantenimiento> lista = mantenimientoRepositorio.buscarMantenimientoVigenteIdCamion(id, Mantenimiento.Estado.VIGENTE);

        if (!lista.isEmpty()) {
            for (Mantenimiento mantenimiento : lista) {

                int kmActual = combustibleServicio.kmUltimaCarga(mantenimiento.getCamion());
                int kmAlarma = mantenimiento.getKmAlarma();
                int kmProximo = mantenimiento.getKmProximo();

                mantenimiento.setKmVigencia(kmProximo - kmActual);
                mantenimiento.setKmActual(kmActual);

                if (kmAlarma <= kmActual && kmProximo > kmActual) {
                    mantenimiento.setEstado(Mantenimiento.Estado.PROXIMO_A_VENCER);
                }
                if (kmProximo <= kmActual) {
                    mantenimiento.setEstado(Mantenimiento.Estado.VENCIDO);
                }
            }
        }

        return lista;
    }

    public List<Mantenimiento> buscarMantenimientoVigenteIdCamionPreventivo(Long id) {

        List<Mantenimiento> lista = mantenimientoRepositorio.buscarMantenimientoVigenteIdCamion(id, Mantenimiento.Estado.VIGENTE, TipoMantenimiento.Clase.PREVENTIVO);

        if (!lista.isEmpty()) {
            for (Mantenimiento mantenimiento : lista) {

                int kmActual = combustibleServicio.kmUltimaCarga(mantenimiento.getCamion());
                int kmAlarma = mantenimiento.getKmAlarma();
                int kmProximo = mantenimiento.getKmProximo();

                mantenimiento.setKmVigencia(kmProximo - kmActual);
                mantenimiento.setKmActual(kmActual);

                if (kmAlarma <= kmActual && kmProximo > kmActual) {
                    mantenimiento.setEstado(Mantenimiento.Estado.PROXIMO_A_VENCER);
                }
                if (kmProximo <= kmActual) {
                    mantenimiento.setEstado(Mantenimiento.Estado.VENCIDO);
                }
            }
        }

        return lista;
    }

    public List<Mantenimiento> buscarMantenimientoVigenteIdCamionCorrectivo(Long id) {

        List<Mantenimiento> lista = mantenimientoRepositorio.buscarMantenimientoVigenteIdCamion(id, Mantenimiento.Estado.VIGENTE, TipoMantenimiento.Clase.CORRECTIVO);

        if (!lista.isEmpty()) {
            for (Mantenimiento mantenimiento : lista) {

                int kmActual = combustibleServicio.kmUltimaCarga(mantenimiento.getCamion());
                int kmAlarma = mantenimiento.getKmAlarma();
                int kmProximo = mantenimiento.getKmProximo();

                mantenimiento.setKmVigencia(kmProximo - kmActual);
                mantenimiento.setKmActual(kmActual);

                if (kmAlarma <= kmActual && kmProximo > kmActual) {
                    mantenimiento.setEstado(Mantenimiento.Estado.PROXIMO_A_VENCER);
                }
                if (kmProximo <= kmActual) {
                    mantenimiento.setEstado(Mantenimiento.Estado.VENCIDO);
                }
            }
        }

        return lista;
    }

    public List<Mantenimiento> buscarMantenimientoVigenteIdAcoplado(Long id) {

        List<Mantenimiento> lista = mantenimientoRepositorio.buscarMantenimientoVigenteIdAcoplado(id, Mantenimiento.Estado.VIGENTE);

        if (!lista.isEmpty()) {
            for (Mantenimiento mantenimiento : lista) {

                int kmActual = combustibleServicio.kmAcoplado(mantenimiento.getAcoplado(), obtenerFechaFija());
                int kmAlarma = mantenimiento.getKmAlarma();
                int kmProximo = mantenimiento.getKmProximo();

                mantenimiento.setKmVigencia(kmProximo - kmActual);
                mantenimiento.setKmActual(kmActual);

                if (kmAlarma <= kmActual && kmProximo > kmActual) {
                    mantenimiento.setEstado(Mantenimiento.Estado.PROXIMO_A_VENCER);
                }
                if (kmProximo <= kmActual) {
                    mantenimiento.setEstado(Mantenimiento.Estado.VENCIDO);
                }
            }
        }

        return lista;
    }

    public List<Mantenimiento> buscarMantenimientoVigenteIdAcopladoPreventivo(Long id) {

        List<Mantenimiento> lista = mantenimientoRepositorio.buscarMantenimientoVigenteIdAcoplado(id, Mantenimiento.Estado.VIGENTE, TipoMantenimiento.Clase.PREVENTIVO);

        if (!lista.isEmpty()) {
            for (Mantenimiento mantenimiento : lista) {

                int kmActual = combustibleServicio.kmAcoplado(mantenimiento.getAcoplado(), obtenerFechaFija());
                int kmAlarma = mantenimiento.getKmAlarma();
                int kmProximo = mantenimiento.getKmProximo();

                mantenimiento.setKmVigencia(kmProximo - kmActual);
                mantenimiento.setKmActual(kmActual);

                if (kmAlarma <= kmActual && kmProximo > kmActual) {
                    mantenimiento.setEstado(Mantenimiento.Estado.PROXIMO_A_VENCER);
                }
                if (kmProximo <= kmActual) {
                    mantenimiento.setEstado(Mantenimiento.Estado.VENCIDO);
                }
            }
        }

        return lista;
    }

    public List<Mantenimiento> buscarMantenimientoVigenteIdAcopladoCorrectivo(Long id) {

        List<Mantenimiento> lista = mantenimientoRepositorio.buscarMantenimientoVigenteIdAcoplado(id, Mantenimiento.Estado.VIGENTE, TipoMantenimiento.Clase.CORRECTIVO);

        if (!lista.isEmpty()) {
            for (Mantenimiento mantenimiento : lista) {

                int kmActual = combustibleServicio.kmAcoplado(mantenimiento.getAcoplado(), obtenerFechaFija());
                int kmAlarma = mantenimiento.getKmAlarma();
                int kmProximo = mantenimiento.getKmProximo();

                mantenimiento.setKmVigencia(kmProximo - kmActual);
                mantenimiento.setKmActual(kmActual);

                if (kmAlarma <= kmActual && kmProximo > kmActual) {
                    mantenimiento.setEstado(Mantenimiento.Estado.PROXIMO_A_VENCER);
                }
                if (kmProximo <= kmActual) {
                    mantenimiento.setEstado(Mantenimiento.Estado.VENCIDO);
                }
            }
        }

        return lista;
    }

    public List<Mantenimiento> obtenerMantenimientosPorVencer(Long idOrg, int km) {

        List<Mantenimiento> lista = mantenimientoRepositorio.findMantenimientosNoActualizados(idOrg, Mantenimiento.Estado.VIGENTE);
        boolean flag = false;

        if (!lista.isEmpty()) {

            for (Mantenimiento mantenimiento : lista) {

                int kmActual = 0;
                int kmProximo = mantenimiento.getKmProximo();

                if (mantenimiento.getAplicaA() == TipoMantenimiento.AplicaA.CAMION) {
                    if(mantenimiento.getCamion().getEstado().equalsIgnoreCase("INHABILITADO")){
                        flag = true;
                    } else {
                    kmActual = combustibleServicio.kmUltimaCarga(mantenimiento.getCamion());
                    }
                } else {
                    if(mantenimiento.getAcoplado().getEstado().equalsIgnoreCase("INHABILITADO")){
                        flag = true;
                    } else {
                    kmActual = combustibleServicio.kmAcoplado(mantenimiento.getAcoplado(), obtenerFechaFija());
                    }
                }

                mantenimiento.setKmActual(kmActual);
                mantenimiento.setKmVigencia(kmProximo - kmActual);

            }
        }
        
        Iterator<Mantenimiento> iterator = lista.iterator();
        while (iterator.hasNext()) {
            Mantenimiento mantenimiento = iterator.next();
            if (mantenimiento.getKmVigencia() > km && flag == true) {
                iterator.remove();
            }
        }

        if (!lista.isEmpty()) {
            for (Mantenimiento mantenimiento : lista) {
                if (mantenimiento.getKmAlarma() <= mantenimiento.getKmActual() && mantenimiento.getKmProximo() > mantenimiento.getKmActual()) {
                    mantenimiento.setEstado(Mantenimiento.Estado.PROXIMO_A_VENCER);
                }
                if (mantenimiento.getKmProximo() <= mantenimiento.getKmActual()) {
                    mantenimiento.setEstado(Mantenimiento.Estado.VENCIDO);
                }
            }
        }
        return lista;

    }
    
    public List<Mantenimiento> obtenerMantenimientosCamionPorVencer(Long idCamion, int km) {

        List<Mantenimiento> lista = mantenimientoRepositorio.findMantenimientosCamionPorVencer(idCamion, Mantenimiento.Estado.VIGENTE);

        if (!lista.isEmpty()) {

            for (Mantenimiento mantenimiento : lista) {

                int kmProximo = mantenimiento.getKmProximo();
                int kmActual = combustibleServicio.kmUltimaCarga(mantenimiento.getCamion());

                mantenimiento.setKmActual(kmActual);
                mantenimiento.setKmVigencia(kmProximo - kmActual);

            }
        }

        Iterator<Mantenimiento> iterator = lista.iterator();
        while (iterator.hasNext()) {
            Mantenimiento mantenimiento = iterator.next();
            if (mantenimiento.getKmVigencia() > km) {
                iterator.remove();
            }
        }

        return lista;

    }
    
    public List<Mantenimiento> obtenerMantenimientosAcopladoPorVencer(Long idAcoplado, int km) {

        List<Mantenimiento> lista = mantenimientoRepositorio.findMantenimientosAcopladoPorVencer(idAcoplado, Mantenimiento.Estado.VIGENTE);

        if (!lista.isEmpty()) {

            for (Mantenimiento mantenimiento : lista) {

                int kmProximo = mantenimiento.getKmProximo();
                int kmActual = combustibleServicio.kmAcoplado(mantenimiento.getAcoplado(), obtenerFechaFija());
                
                mantenimiento.setKmActual(kmActual);
                mantenimiento.setKmVigencia(kmProximo - kmActual);

            }
        }

        Iterator<Mantenimiento> iterator = lista.iterator();
        while (iterator.hasNext()) {
            Mantenimiento mantenimiento = iterator.next();
            if (mantenimiento.getKmVigencia() > km) {
                iterator.remove();
            }
        }

        return lista;

    }

    public List<Mantenimiento> buscarMantenimientosVigenteCamiones(Long idOrg) {

        List<Mantenimiento> lista = mantenimientoRepositorio.findByCamionIsNotNullAndIdOrgAndEstado(idOrg, Mantenimiento.Estado.VIGENTE);
        
        if (!lista.isEmpty()) {

        Iterator<Mantenimiento> iterator = lista.iterator();

        while (iterator.hasNext()) {
            
            Mantenimiento mantenimiento = iterator.next();

            if (mantenimiento.getCamion() != null && "INHABILITADO".equalsIgnoreCase(mantenimiento.getCamion().getEstado())) {
                iterator.remove();
                continue;
            }

            int kmActual = combustibleServicio.kmUltimaCarga(mantenimiento.getCamion());
            int kmAlarma = mantenimiento.getKmAlarma();
            int kmProximo = mantenimiento.getKmProximo();

            mantenimiento.setKmVigencia(kmProximo - kmActual);
            mantenimiento.setKmActual(kmActual);

            if (kmAlarma <= kmActual && kmProximo > kmActual) {
                mantenimiento.setEstado(Mantenimiento.Estado.PROXIMO_A_VENCER);
            }

            if (kmProximo <= kmActual) {
                mantenimiento.setEstado(Mantenimiento.Estado.VENCIDO);
            }
        }
    }

        return lista;
    }

    public List<Mantenimiento> buscarMantenimientosVigenteCamionesPreventivo(Long idOrg) {

        List<Mantenimiento> lista = mantenimientoRepositorio.findByCamionIsNotNullAndIdOrgAndEstadoAndTipoMantenimiento_Clase(idOrg, Mantenimiento.Estado.VIGENTE, TipoMantenimiento.Clase.PREVENTIVO);

        if (!lista.isEmpty()) {

        Iterator<Mantenimiento> iterator = lista.iterator();

        while (iterator.hasNext()) {
            
            Mantenimiento mantenimiento = iterator.next();

            if (mantenimiento.getCamion() != null && "INHABILITADO".equalsIgnoreCase(mantenimiento.getCamion().getEstado())) {
                iterator.remove();
                continue;
            }

            int kmActual = combustibleServicio.kmUltimaCarga(mantenimiento.getCamion());
            int kmAlarma = mantenimiento.getKmAlarma();
            int kmProximo = mantenimiento.getKmProximo();

            mantenimiento.setKmVigencia(kmProximo - kmActual);
            mantenimiento.setKmActual(kmActual);

            if (kmAlarma <= kmActual && kmProximo > kmActual) {
                mantenimiento.setEstado(Mantenimiento.Estado.PROXIMO_A_VENCER);
            }

            if (kmProximo <= kmActual) {
                mantenimiento.setEstado(Mantenimiento.Estado.VENCIDO);
            }
        }
    }

        return lista;
    }

    public List<Mantenimiento> buscarMantenimientosVigenteCamionesCorrectivo(Long idOrg) {

        List<Mantenimiento> lista = mantenimientoRepositorio.findByCamionIsNotNullAndIdOrgAndEstadoAndTipoMantenimiento_Clase(idOrg, Mantenimiento.Estado.VIGENTE, TipoMantenimiento.Clase.CORRECTIVO);

        if (!lista.isEmpty()) {

        Iterator<Mantenimiento> iterator = lista.iterator();

        while (iterator.hasNext()) {
            
            Mantenimiento mantenimiento = iterator.next();

            if (mantenimiento.getCamion() != null && "INHABILITADO".equalsIgnoreCase(mantenimiento.getCamion().getEstado())) {
                iterator.remove();
                continue;
            }

            int kmActual = combustibleServicio.kmUltimaCarga(mantenimiento.getCamion());
            int kmAlarma = mantenimiento.getKmAlarma();
            int kmProximo = mantenimiento.getKmProximo();

            mantenimiento.setKmVigencia(kmProximo - kmActual);
            mantenimiento.setKmActual(kmActual);

            if (kmAlarma <= kmActual && kmProximo > kmActual) {
                mantenimiento.setEstado(Mantenimiento.Estado.PROXIMO_A_VENCER);
            }

            if (kmProximo <= kmActual) {
                mantenimiento.setEstado(Mantenimiento.Estado.VENCIDO);
            }
        }
    }

        return lista;
    }

    public List<Mantenimiento> buscarMantenimientosVigenteAcoplados(Long idOrg) {

        List<Mantenimiento> lista = mantenimientoRepositorio.findByAcopladoIsNotNullAndIdOrgAndEstado(idOrg, Mantenimiento.Estado.VIGENTE);
        
        if (!lista.isEmpty()) {

        Iterator<Mantenimiento> iterator = lista.iterator();

        while (iterator.hasNext()) {
            
            Mantenimiento mantenimiento = iterator.next();

            if (mantenimiento.getAcoplado() != null && "INHABILITADO".equalsIgnoreCase(mantenimiento.getAcoplado().getEstado())) {
                iterator.remove();
                continue;
            }

            int kmActual = combustibleServicio.kmAcoplado(mantenimiento.getAcoplado(), obtenerFechaFija());
                int kmAlarma = mantenimiento.getKmAlarma();
                int kmProximo = mantenimiento.getKmProximo();

                mantenimiento.setKmVigencia(kmProximo - kmActual);
                mantenimiento.setKmActual(kmActual);

                if (kmAlarma <= kmActual && kmProximo > kmActual) {
                    mantenimiento.setEstado(Mantenimiento.Estado.PROXIMO_A_VENCER);
                }
                if (kmProximo <= kmActual) {
                    mantenimiento.setEstado(Mantenimiento.Estado.VENCIDO);
                }
        }
    }

        return lista;
    }

    public List<Mantenimiento> buscarMantenimientosVigenteAcopladosPreventivo(Long idOrg) {

        List<Mantenimiento> lista = mantenimientoRepositorio.findByAcopladoIsNotNullAndIdOrgAndEstadoAndTipoMantenimiento_Clase(idOrg, Mantenimiento.Estado.VIGENTE, TipoMantenimiento.Clase.PREVENTIVO);

        if (!lista.isEmpty()) {

        Iterator<Mantenimiento> iterator = lista.iterator();

        while (iterator.hasNext()) {
            
            Mantenimiento mantenimiento = iterator.next();

            if (mantenimiento.getAcoplado() != null && "INHABILITADO".equalsIgnoreCase(mantenimiento.getAcoplado().getEstado())) {
                iterator.remove();
                continue;
            }

            int kmActual = combustibleServicio.kmAcoplado(mantenimiento.getAcoplado(), obtenerFechaFija());
                int kmAlarma = mantenimiento.getKmAlarma();
                int kmProximo = mantenimiento.getKmProximo();

                mantenimiento.setKmVigencia(kmProximo - kmActual);
                mantenimiento.setKmActual(kmActual);

                if (kmAlarma <= kmActual && kmProximo > kmActual) {
                    mantenimiento.setEstado(Mantenimiento.Estado.PROXIMO_A_VENCER);
                }
                if (kmProximo <= kmActual) {
                    mantenimiento.setEstado(Mantenimiento.Estado.VENCIDO);
                }
        }
    }

        return lista;
    }

    public List<Mantenimiento> buscarMantenimientosVigenteAcopladosCorrectivo(Long idOrg) {

        List<Mantenimiento> lista = mantenimientoRepositorio.findByAcopladoIsNotNullAndIdOrgAndEstadoAndTipoMantenimiento_Clase(idOrg, Mantenimiento.Estado.VIGENTE, TipoMantenimiento.Clase.CORRECTIVO);

        if (!lista.isEmpty()) {

        Iterator<Mantenimiento> iterator = lista.iterator();

        while (iterator.hasNext()) {
            
            Mantenimiento mantenimiento = iterator.next();

            if (mantenimiento.getAcoplado() != null && "INHABILITADO".equalsIgnoreCase(mantenimiento.getAcoplado().getEstado())) {
                iterator.remove();
                continue;
            }

            int kmActual = combustibleServicio.kmAcoplado(mantenimiento.getAcoplado(), obtenerFechaFija());
                int kmAlarma = mantenimiento.getKmAlarma();
                int kmProximo = mantenimiento.getKmProximo();

                mantenimiento.setKmVigencia(kmProximo - kmActual);
                mantenimiento.setKmActual(kmActual);

                if (kmAlarma <= kmActual && kmProximo > kmActual) {
                    mantenimiento.setEstado(Mantenimiento.Estado.PROXIMO_A_VENCER);
                }
                if (kmProximo <= kmActual) {
                    mantenimiento.setEstado(Mantenimiento.Estado.VENCIDO);
                }
        }
    }

        return lista;
    }

    public List<Mantenimiento> buscarMantenimientosVigenteCamionesPorTipo(Long idOrg, Long idTipo) {

        List<Mantenimiento> lista = mantenimientoRepositorio.buscarMantenimientoVigenteCamionesPorTipo(idOrg, Mantenimiento.Estado.VIGENTE, idTipo);
        
        if (!lista.isEmpty()) {

        Iterator<Mantenimiento> iterator = lista.iterator();

        while (iterator.hasNext()) {
            
            Mantenimiento mantenimiento = iterator.next();

            if (mantenimiento.getCamion() != null && "INHABILITADO".equalsIgnoreCase(mantenimiento.getCamion().getEstado())) {
                iterator.remove();
                continue;
            }

            int kmActual = combustibleServicio.kmUltimaCarga(mantenimiento.getCamion());
            int kmAlarma = mantenimiento.getKmAlarma();
            int kmProximo = mantenimiento.getKmProximo();

            mantenimiento.setKmVigencia(kmProximo - kmActual);
            mantenimiento.setKmActual(kmActual);

            if (kmAlarma <= kmActual && kmProximo > kmActual) {
                mantenimiento.setEstado(Mantenimiento.Estado.PROXIMO_A_VENCER);
            }

            if (kmProximo <= kmActual) {
                mantenimiento.setEstado(Mantenimiento.Estado.VENCIDO);
            }
        }
    }

        return lista;
    }

    public List<Mantenimiento> buscarMantenimientosVigenteCamionesPorTipoPreventivo(Long idOrg, Long idTipo) {

        List<Mantenimiento> lista = mantenimientoRepositorio.buscarMantenimientoVigenteCamionesPorTipoClase(idOrg, Mantenimiento.Estado.VIGENTE, idTipo, TipoMantenimiento.Clase.PREVENTIVO);

        if (!lista.isEmpty()) {

        Iterator<Mantenimiento> iterator = lista.iterator();

        while (iterator.hasNext()) {
            
            Mantenimiento mantenimiento = iterator.next();

            if (mantenimiento.getCamion() != null && "INHABILITADO".equalsIgnoreCase(mantenimiento.getCamion().getEstado())) {
                iterator.remove();
                continue;
            }

            int kmActual = combustibleServicio.kmUltimaCarga(mantenimiento.getCamion());
            int kmAlarma = mantenimiento.getKmAlarma();
            int kmProximo = mantenimiento.getKmProximo();

            mantenimiento.setKmVigencia(kmProximo - kmActual);
            mantenimiento.setKmActual(kmActual);

            if (kmAlarma <= kmActual && kmProximo > kmActual) {
                mantenimiento.setEstado(Mantenimiento.Estado.PROXIMO_A_VENCER);
            }

            if (kmProximo <= kmActual) {
                mantenimiento.setEstado(Mantenimiento.Estado.VENCIDO);
            }
        }
    }

        return lista;
    }

    public List<Mantenimiento> buscarMantenimientosVigenteCamionesPorTipoCorrectivo(Long idOrg, Long idTipo) {

        List<Mantenimiento> lista = mantenimientoRepositorio.buscarMantenimientoVigenteCamionesPorTipoClase(idOrg, Mantenimiento.Estado.VIGENTE, idTipo, TipoMantenimiento.Clase.CORRECTIVO);

        if (!lista.isEmpty()) {

        Iterator<Mantenimiento> iterator = lista.iterator();

        while (iterator.hasNext()) {
            
            Mantenimiento mantenimiento = iterator.next();

            if (mantenimiento.getCamion() != null && "INHABILITADO".equalsIgnoreCase(mantenimiento.getCamion().getEstado())) {
                iterator.remove();
                continue;
            }

            int kmActual = combustibleServicio.kmUltimaCarga(mantenimiento.getCamion());
            int kmAlarma = mantenimiento.getKmAlarma();
            int kmProximo = mantenimiento.getKmProximo();

            mantenimiento.setKmVigencia(kmProximo - kmActual);
            mantenimiento.setKmActual(kmActual);

            if (kmAlarma <= kmActual && kmProximo > kmActual) {
                mantenimiento.setEstado(Mantenimiento.Estado.PROXIMO_A_VENCER);
            }

            if (kmProximo <= kmActual) {
                mantenimiento.setEstado(Mantenimiento.Estado.VENCIDO);
            }
        }
    }

        return lista;
    }

    public List<Mantenimiento> buscarMantenimientosVigenteCamionPorTipo(Long idCamion, Long idTipo) {

        List<Mantenimiento> lista = mantenimientoRepositorio.buscarMantenimientoVigenteCamionPorTipo(Mantenimiento.Estado.VIGENTE, idCamion, idTipo);

        if (!lista.isEmpty()) {
            for (Mantenimiento mantenimiento : lista) {

                int kmActual = combustibleServicio.kmUltimaCarga(mantenimiento.getCamion());
                int kmAlarma = mantenimiento.getKmAlarma();
                int kmProximo = mantenimiento.getKmProximo();

                mantenimiento.setKmVigencia(kmProximo - kmActual);
                mantenimiento.setKmActual(kmActual);

                if (kmAlarma <= kmActual && kmProximo > kmActual) {
                    mantenimiento.setEstado(Mantenimiento.Estado.PROXIMO_A_VENCER);
                }
                if (kmProximo <= kmActual) {
                    mantenimiento.setEstado(Mantenimiento.Estado.VENCIDO);
                }
            }

        }

        return lista;
    }

    public List<Mantenimiento> buscarMantenimientosVigenteCamionPorTipoPreventivo(Long idCamion, Long idTipo) {

        List<Mantenimiento> lista = mantenimientoRepositorio.buscarMantenimientoVigenteCamionPorTipoClase(Mantenimiento.Estado.VIGENTE, idCamion, idTipo, TipoMantenimiento.Clase.PREVENTIVO);

        if (!lista.isEmpty()) {
            for (Mantenimiento mantenimiento : lista) {

                int kmActual = combustibleServicio.kmUltimaCarga(mantenimiento.getCamion());
                int kmAlarma = mantenimiento.getKmAlarma();
                int kmProximo = mantenimiento.getKmProximo();

                mantenimiento.setKmVigencia(kmProximo - kmActual);
                mantenimiento.setKmActual(kmActual);

                if (kmAlarma <= kmActual && kmProximo > kmActual) {
                    mantenimiento.setEstado(Mantenimiento.Estado.PROXIMO_A_VENCER);
                }
                if (kmProximo <= kmActual) {
                    mantenimiento.setEstado(Mantenimiento.Estado.VENCIDO);
                }
            }

        }

        return lista;
    }

    public List<Mantenimiento> buscarMantenimientosVigenteCamionPorTipoCorrectivo(Long idCamion, Long idTipo) {

        List<Mantenimiento> lista = mantenimientoRepositorio.buscarMantenimientoVigenteCamionPorTipoClase(Mantenimiento.Estado.VIGENTE, idCamion, idTipo, TipoMantenimiento.Clase.CORRECTIVO);

        if (!lista.isEmpty()) {
            for (Mantenimiento mantenimiento : lista) {

                int kmActual = combustibleServicio.kmUltimaCarga(mantenimiento.getCamion());
                int kmAlarma = mantenimiento.getKmAlarma();
                int kmProximo = mantenimiento.getKmProximo();

                mantenimiento.setKmVigencia(kmProximo - kmActual);
                mantenimiento.setKmActual(kmActual);

                if (kmAlarma <= kmActual && kmProximo > kmActual) {
                    mantenimiento.setEstado(Mantenimiento.Estado.PROXIMO_A_VENCER);
                }
                if (kmProximo <= kmActual) {
                    mantenimiento.setEstado(Mantenimiento.Estado.VENCIDO);
                }
            }

        }

        return lista;
    }

    public List<Mantenimiento> buscarMantenimientosVigenteAcopladosPorTipo(Long idOrg, Long idTipo) {

        List<Mantenimiento> lista = mantenimientoRepositorio.buscarMantenimientoVigenteAcopladosPorTipo(idOrg, Mantenimiento.Estado.VIGENTE, idTipo);

        if (!lista.isEmpty()) {

        Iterator<Mantenimiento> iterator = lista.iterator();

        while (iterator.hasNext()) {
            
            Mantenimiento mantenimiento = iterator.next();

            if (mantenimiento.getAcoplado() != null && "INHABILITADO".equalsIgnoreCase(mantenimiento.getAcoplado().getEstado())) {
                iterator.remove();
                continue;
            }

            int kmActual = combustibleServicio.kmAcoplado(mantenimiento.getAcoplado(), obtenerFechaFija());
                int kmAlarma = mantenimiento.getKmAlarma();
                int kmProximo = mantenimiento.getKmProximo();

                mantenimiento.setKmVigencia(kmProximo - kmActual);
                mantenimiento.setKmActual(kmActual);

                if (kmAlarma <= kmActual && kmProximo > kmActual) {
                    mantenimiento.setEstado(Mantenimiento.Estado.PROXIMO_A_VENCER);
                }
                if (kmProximo <= kmActual) {
                    mantenimiento.setEstado(Mantenimiento.Estado.VENCIDO);
                }
        }
    }

        return lista;
    }

    public List<Mantenimiento> buscarMantenimientosVigenteAcopladosPorTipoPreventivo(Long idOrg, Long idTipo) {

        List<Mantenimiento> lista = mantenimientoRepositorio.buscarMantenimientoVigenteAcopladosPorTipoClase(idOrg, Mantenimiento.Estado.VIGENTE, idTipo, TipoMantenimiento.Clase.PREVENTIVO);

        if (!lista.isEmpty()) {

        Iterator<Mantenimiento> iterator = lista.iterator();

        while (iterator.hasNext()) {
            
            Mantenimiento mantenimiento = iterator.next();

            if (mantenimiento.getAcoplado() != null && "INHABILITADO".equalsIgnoreCase(mantenimiento.getAcoplado().getEstado())) {
                iterator.remove();
                continue;
            }

            int kmActual = combustibleServicio.kmAcoplado(mantenimiento.getAcoplado(), obtenerFechaFija());
                int kmAlarma = mantenimiento.getKmAlarma();
                int kmProximo = mantenimiento.getKmProximo();

                mantenimiento.setKmVigencia(kmProximo - kmActual);
                mantenimiento.setKmActual(kmActual);

                if (kmAlarma <= kmActual && kmProximo > kmActual) {
                    mantenimiento.setEstado(Mantenimiento.Estado.PROXIMO_A_VENCER);
                }
                if (kmProximo <= kmActual) {
                    mantenimiento.setEstado(Mantenimiento.Estado.VENCIDO);
                }
        }
    }

        return lista;
    }

    public List<Mantenimiento> buscarMantenimientosVigenteAcopladosPorTipoCorrectivo(Long idOrg, Long idTipo) {

        List<Mantenimiento> lista = mantenimientoRepositorio.buscarMantenimientoVigenteAcopladosPorTipoClase(idOrg, Mantenimiento.Estado.VIGENTE, idTipo, TipoMantenimiento.Clase.CORRECTIVO);

        if (!lista.isEmpty()) {

        Iterator<Mantenimiento> iterator = lista.iterator();

        while (iterator.hasNext()) {
            
            Mantenimiento mantenimiento = iterator.next();

            if (mantenimiento.getAcoplado() != null && "INHABILITADO".equalsIgnoreCase(mantenimiento.getAcoplado().getEstado())) {
                iterator.remove();
                continue;
            }

            int kmActual = combustibleServicio.kmAcoplado(mantenimiento.getAcoplado(), obtenerFechaFija());
                int kmAlarma = mantenimiento.getKmAlarma();
                int kmProximo = mantenimiento.getKmProximo();

                mantenimiento.setKmVigencia(kmProximo - kmActual);
                mantenimiento.setKmActual(kmActual);

                if (kmAlarma <= kmActual && kmProximo > kmActual) {
                    mantenimiento.setEstado(Mantenimiento.Estado.PROXIMO_A_VENCER);
                }
                if (kmProximo <= kmActual) {
                    mantenimiento.setEstado(Mantenimiento.Estado.VENCIDO);
                }
        }
    }

        return lista;
    }

    public List<Mantenimiento> buscarMantenimientosVigenteAcopladoPorTipo(Long idAcoplado, Long idTipo) {

        List<Mantenimiento> lista = mantenimientoRepositorio.buscarMantenimientoVigenteAcopladoPorTipo(Mantenimiento.Estado.VIGENTE, idAcoplado, idTipo);

        if (!lista.isEmpty()) {

        Iterator<Mantenimiento> iterator = lista.iterator();

        while (iterator.hasNext()) {
            
            Mantenimiento mantenimiento = iterator.next();

            if (mantenimiento.getAcoplado() != null && "INHABILITADO".equalsIgnoreCase(mantenimiento.getAcoplado().getEstado())) {
                iterator.remove();
                continue;
            }

            int kmActual = combustibleServicio.kmAcoplado(mantenimiento.getAcoplado(), obtenerFechaFija());
                int kmAlarma = mantenimiento.getKmAlarma();
                int kmProximo = mantenimiento.getKmProximo();

                mantenimiento.setKmVigencia(kmProximo - kmActual);
                mantenimiento.setKmActual(kmActual);

                if (kmAlarma <= kmActual && kmProximo > kmActual) {
                    mantenimiento.setEstado(Mantenimiento.Estado.PROXIMO_A_VENCER);
                }
                if (kmProximo <= kmActual) {
                    mantenimiento.setEstado(Mantenimiento.Estado.VENCIDO);
                }
        }
    }

        return lista;
    }

    public List<Mantenimiento> buscarMantenimientosVigenteAcopladoPorTipoPreventivo(Long idAcoplado, Long idTipo) {

        List<Mantenimiento> lista = mantenimientoRepositorio.buscarMantenimientoVigenteAcopladoPorTipoClase(Mantenimiento.Estado.VIGENTE, idAcoplado, idTipo, TipoMantenimiento.Clase.PREVENTIVO);

        if (!lista.isEmpty()) {

            for (Mantenimiento mantenimiento : lista) {

                int kmActual = combustibleServicio.kmAcoplado(mantenimiento.getAcoplado(), obtenerFechaFija());
                int kmAlarma = mantenimiento.getKmAlarma();
                int kmProximo = mantenimiento.getKmProximo();

                mantenimiento.setKmVigencia(kmProximo - kmActual);
                mantenimiento.setKmActual(kmActual);

                if (kmAlarma <= kmActual && kmProximo > kmActual) {
                    mantenimiento.setEstado(Mantenimiento.Estado.PROXIMO_A_VENCER);
                }
                if (kmProximo <= kmActual) {
                    mantenimiento.setEstado(Mantenimiento.Estado.VENCIDO);
                }
            }
        }

        return lista;
    }

    public List<Mantenimiento> buscarMantenimientosVigenteAcopladoPorTipoCorrectivo(Long idAcoplado, Long idTipo) {

        List<Mantenimiento> lista = mantenimientoRepositorio.buscarMantenimientoVigenteAcopladoPorTipoClase(Mantenimiento.Estado.VIGENTE, idAcoplado, idTipo, TipoMantenimiento.Clase.CORRECTIVO);

        if (!lista.isEmpty()) {

            for (Mantenimiento mantenimiento : lista) {

                int kmActual = combustibleServicio.kmAcoplado(mantenimiento.getAcoplado(), obtenerFechaFija());
                int kmAlarma = mantenimiento.getKmAlarma();
                int kmProximo = mantenimiento.getKmProximo();

                mantenimiento.setKmVigencia(kmProximo - kmActual);
                mantenimiento.setKmActual(kmActual);

                if (kmAlarma <= kmActual && kmProximo > kmActual) {
                    mantenimiento.setEstado(Mantenimiento.Estado.PROXIMO_A_VENCER);
                }
                if (kmProximo <= kmActual) {
                    mantenimiento.setEstado(Mantenimiento.Estado.VENCIDO);
                }
            }
        }

        return lista;
    }

    public List<Mantenimiento> buscarHistorialCamion(Long id) {

        List<Mantenimiento> lista = mantenimientoRepositorio.buscarHistorialCamion(id, Mantenimiento.Estado.ACTUALIZADO);

        return lista;
    }

    public List<Mantenimiento> buscarHistorialAcoplado(Long id) {

        List<Mantenimiento> lista = mantenimientoRepositorio.buscarHistorialAcoplado(id, Mantenimiento.Estado.ACTUALIZADO);

        return lista;
    }

    public Date convertirFecha(String fecha) throws ParseException {
        SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd");
        return formato.parse(fecha);
    }

    public static Date obtenerFechaFija() {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            return sdf.parse("01-01-2024");
        } catch (ParseException e) {
            e.printStackTrace();
            return null; // o lanzar una excepción personalizada
        }
    }

}
