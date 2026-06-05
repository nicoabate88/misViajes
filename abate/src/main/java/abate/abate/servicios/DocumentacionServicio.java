
package abate.abate.servicios;

import abate.abate.entidades.Acoplado;
import abate.abate.entidades.Camion;
import abate.abate.entidades.Documentacion;
import abate.abate.entidades.TipoDocumentacion;
import abate.abate.entidades.Usuario;
import abate.abate.excepciones.MiException;
import abate.abate.repositorios.AcopladoRepositorio;
import abate.abate.repositorios.CamionRepositorio;
import abate.abate.repositorios.DocumentacionRepositorio;
import abate.abate.repositorios.TipoDocumentacionRepositorio;
import abate.abate.repositorios.UsuarioRepositorio;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DocumentacionServicio {
    
    @Autowired
    private DocumentacionRepositorio documentacionRepositorio;
    @Autowired
    private TipoDocumentacionRepositorio tipoDocumentacionRepositorio;
    @Autowired
    private CamionRepositorio camionRepositorio;
    @Autowired
    private AcopladoRepositorio acopladoRepositorio;
    @Autowired
    private UsuarioRepositorio usuarioRepositorio;
    @Autowired
    private ImagenServicio imagenServicio;
    
    @Transactional
    public void crearDocumentacion(Long tipoId, TipoDocumentacion.AplicaA aplicaA, Long idEntidad, String fechaAlta, String fechaVencimiento, 
            String observacion, Usuario usuario) throws ParseException, MiException {
        
        validarDatos(tipoId, aplicaA, idEntidad);

        TipoDocumentacion tipo = tipoDocumentacionRepositorio.getById(tipoId); 
        
        Date alta= convertirFecha(fechaAlta);
        Date vencimiento = convertirFecha(fechaVencimiento);
        
        String obsMayusculas = observacion.toUpperCase();
        
        Documentacion documentacion = new Documentacion();
        
        documentacion.setTipoDocumentacion(tipo);
        documentacion.setAplicaA(aplicaA);
        documentacion.setObservacion(obsMayusculas); 
        documentacion.setFechaAlta(alta);
        documentacion.setFechaVencimiento(vencimiento);
        documentacion.setUsuario(usuario);
        documentacion.setIdOrg(usuario.getIdOrg());
        documentacion.setEstado("VIGENTE");
        if(aplicaA == TipoDocumentacion.AplicaA.CAMION){
            Camion camion = camionRepositorio.getById(idEntidad);
            documentacion.setCamion(camion);
        } else if(aplicaA == TipoDocumentacion.AplicaA.ACOPLADO){
            Acoplado acoplado = acopladoRepositorio.getById(idEntidad);
            documentacion.setAcoplado(acoplado);
        } else {
            Usuario chofer = usuarioRepositorio.getById(idEntidad);
            documentacion.setChofer(chofer);
        }

        documentacionRepositorio.save(documentacion);

    }
    
    @Transactional
    public Documentacion crearDocumentacionMasivo(Documentacion documentacion) {
        
        Documentacion buscarVigente = null;

        if (documentacion.getAplicaA() == TipoDocumentacion.AplicaA.CAMION) {
            Optional<Documentacion> doc = documentacionRepositorio.buscarDocumentacionCamion(documentacion.getCamion().getId(), documentacion.getTipoDocumentacion().getId());
            if (doc.isPresent()) {
                buscarVigente = doc.get();
            }
            
        } else if (documentacion.getAplicaA() == TipoDocumentacion.AplicaA.ACOPLADO) {
            Optional<Documentacion> doc = documentacionRepositorio.buscarDocumentacionAcoplado(documentacion.getAcoplado().getId(), documentacion.getTipoDocumentacion().getId());
            if (doc.isPresent()) {
                buscarVigente = doc.get();
            }
        } else {
            Optional<Documentacion> doc = documentacionRepositorio.buscarDocumentacionChofer(documentacion.getChofer().getId(), documentacion.getTipoDocumentacion().getId());
            if (doc.isPresent()) {
                buscarVigente = doc.get();
            }
        }
        
        if(buscarVigente != null) {
            
            buscarVigente.setFechaAlta(documentacion.getFechaAlta());
            buscarVigente.setFechaVencimiento(documentacion.getFechaVencimiento());
            buscarVigente.setUsuario(documentacion.getUsuario());
            buscarVigente.setObservacion(documentacion.getObservacion());
            buscarVigente.setImagen(null);
            
            documentacionRepositorio.save(buscarVigente);
            
            return buscarVigente;
            
        } else {
            
            documentacionRepositorio.save(documentacion);
            
            return documentacion;
               
        }

    }
    
    @Transactional
    public Documentacion crearDocumentacionMasivoTipo(Documentacion documentacion) {
        
        Documentacion buscarVigente = null;

        if (documentacion.getAplicaA() == TipoDocumentacion.AplicaA.CAMION) {
            Optional<Documentacion> doc = documentacionRepositorio.buscarDocumentacionCamion(documentacion.getCamion().getId(), documentacion.getTipoDocumentacion().getId());
            if (doc.isPresent()) {
                buscarVigente = doc.get();
            }
            
        } else if (documentacion.getAplicaA() == TipoDocumentacion.AplicaA.ACOPLADO) {
            Optional<Documentacion> doc = documentacionRepositorio.buscarDocumentacionAcoplado(documentacion.getAcoplado().getId(), documentacion.getTipoDocumentacion().getId());
            if (doc.isPresent()) {
                buscarVigente = doc.get();
            }
        } else {
            Optional<Documentacion> doc = documentacionRepositorio.buscarDocumentacionChofer(documentacion.getChofer().getId(), documentacion.getTipoDocumentacion().getId());
            if (doc.isPresent()) {
                buscarVigente = doc.get();
            }
        }
        
        if(buscarVigente != null) {
            
            buscarVigente.setFechaAlta(documentacion.getFechaAlta());
            buscarVigente.setFechaVencimiento(documentacion.getFechaVencimiento());
            buscarVigente.setUsuario(documentacion.getUsuario());
            buscarVigente.setObservacion(documentacion.getObservacion());
            buscarVigente.setImagen(null);

            documentacionRepositorio.save(buscarVigente);
            
            return buscarVigente;
            
        } else {
            
            documentacionRepositorio.save(documentacion);
            
            return documentacion;
               
        }

    }

    
    @Transactional
    public void modificarDocumentacion(Long id, Long tipoId, TipoDocumentacion.AplicaA aplicaA, Long idEntidad, String fechaAlta, 
            String fechaVencimiento, String observacion, Usuario usuario) throws ParseException, MiException {
        
        validarDatosModificar(id, tipoId, aplicaA, idEntidad);

        Documentacion documentacion = documentacionRepositorio.getById(id);
        
        if(documentacion.getTipoDocumentacion().getId() != tipoId){
        TipoDocumentacion tipo = tipoDocumentacionRepositorio.getById(tipoId);
        documentacion.setTipoDocumentacion(tipo);
        }
        if(documentacion.getAplicaA() != aplicaA){
            documentacion.setAplicaA(aplicaA);
        }
        if(aplicaA == TipoDocumentacion.AplicaA.CAMION){
            Camion camion = camionRepositorio.getById(idEntidad);
            documentacion.setCamion(camion);
        } else if(aplicaA == TipoDocumentacion.AplicaA.ACOPLADO){
            Acoplado acoplado = acopladoRepositorio.getById(idEntidad);
            documentacion.setAcoplado(acoplado);
        } else {
            Usuario chofer = usuarioRepositorio.getById(idEntidad);
            documentacion.setChofer(chofer);
        } 
        
        Date alta = convertirFecha(fechaAlta);
        Date vencimiento = convertirFecha(fechaVencimiento);
        String obsMayusculas = observacion.toUpperCase();
        documentacion.setEstado("VIGENTE");
        documentacion.setObservacion(obsMayusculas); 
        documentacion.setFechaAlta(alta);
        documentacion.setFechaVencimiento(vencimiento);
        documentacion.setUsuario(usuario);

        documentacionRepositorio.save(documentacion);

    }
    
    @Transactional
    public void eliminarDocumentacion(Long id) throws MiException {

        Documentacion documentacion = documentacionRepositorio.getById(id);

        if (documentacion.getImagen() != null) {
            imagenServicio.eliminarImagenDocu(documentacion.getImagen().getId(), id);
        }

        documentacion.setCamion(null);
        documentacion.setChofer(null);
        documentacion.setAcoplado(null);
        documentacion.setUsuario(null);
        documentacion.setTipoDocumentacion(null);

        documentacionRepositorio.save(documentacion);

        documentacionRepositorio.deleteById(id);

    }
    
    public Long buscarUltimo(Long idOrg) {

        return documentacionRepositorio.ultimoDocumento(idOrg);
        
    }
    
    public Documentacion buscarDocumentacionIdImagen(Long id) {

        return documentacionRepositorio.buscarDocumentacionIdImagen(id);
    }
    
    public Documentacion buscarDocumentacion(Long id) {

        return documentacionRepositorio.getById(id);
    }
    
    public Documentacion buscarDocumentacionDiasVigencia(Long id) {

        Documentacion documentacion = documentacionRepositorio.getById(id);
        
        Date fechaActual = new Date();
        long diferenciaMillis = documentacion.getFechaVencimiento().getTime() - fechaActual.getTime();
        int diasVigencia = (int) TimeUnit.MILLISECONDS.toDays(diferenciaMillis);  
        documentacion.setDiasVigencia(diasVigencia);
        
        if(diasVigencia <= 30 && diasVigencia > 0){
            documentacion.setEstado("PRÓXIMO A VENCER");
        } if(diasVigencia <= 0){
            documentacion.setEstado("VENCIDO");
        }
        
        return documentacion;
    }
    
    public List<Documentacion> buscarDocumentacionIdChofer(Long id) {
        
        List<Documentacion> lista = documentacionRepositorio.buscarDocumentacionIdChofer(id);
        
        if(!lista.isEmpty()){
        for(Documentacion d : lista){
            
        Date fechaActual = new Date();
        long diferenciaMillis = d.getFechaVencimiento().getTime() - fechaActual.getTime();
        int diasVigencia = (int) TimeUnit.MILLISECONDS.toDays(diferenciaMillis);  
        d.setDiasVigencia(diasVigencia);
        
        if(diasVigencia <= 30 && diasVigencia > 0){
            d.setEstado("PRÓXIMO A VENCER");
        } if(diasVigencia <= 0){
            d.setEstado("VENCIDO");
        }
        
        }    
        }

        return lista;
    }
    
    public List<Documentacion> buscarDocumentacionIdCamion(Long id) {
        
        List<Documentacion> lista = documentacionRepositorio.buscarDocumentacionIdCamion(id);
        
        if(!lista.isEmpty()){
        for(Documentacion d : lista){
            
        Date fechaActual = new Date();
        long diferenciaMillis = d.getFechaVencimiento().getTime() - fechaActual.getTime();
        int diasVigencia = (int) TimeUnit.MILLISECONDS.toDays(diferenciaMillis);  
        d.setDiasVigencia(diasVigencia);
        
        if(diasVigencia <= 30 && diasVigencia > 0){
            d.setEstado("PRÓXIMO A VENCER");
        } if(diasVigencia <= 0){
            d.setEstado("VENCIDO");
        }
        }    
        }

        return lista;
    }
    
    public List<Documentacion> buscarDocumentacionCamionesIdTipo(Long idOrg, Long idTipo) {
        
        List<Documentacion> lista = documentacionRepositorio.buscarDocumentacionCamionesPorTipo(idOrg, idTipo);
        
        if(!lista.isEmpty()){
        for(Documentacion d : lista){
            
        Date fechaActual = new Date();
        long diferenciaMillis = d.getFechaVencimiento().getTime() - fechaActual.getTime();
        int diasVigencia = (int) TimeUnit.MILLISECONDS.toDays(diferenciaMillis);  
        d.setDiasVigencia(diasVigencia);
        
        if(diasVigencia <= 30 && diasVigencia > 0){
            d.setEstado("PRÓXIMO A VENCER");
        } if(diasVigencia <= 0){
            d.setEstado("VENCIDO");
        }
        }    
        }

        return lista;
    }
    
    public List<Documentacion> buscarDocumentacionCamionIdTipo(Long idCamion, Long idTipo) {
        
        List<Documentacion> lista = documentacionRepositorio.buscarDocumentacionCamionPorTipo(idCamion, idTipo);
        
        if(!lista.isEmpty()){
        for(Documentacion d : lista){
            
        Date fechaActual = new Date();
        long diferenciaMillis = d.getFechaVencimiento().getTime() - fechaActual.getTime();
        int diasVigencia = (int) TimeUnit.MILLISECONDS.toDays(diferenciaMillis);  
        d.setDiasVigencia(diasVigencia);
        
        if(diasVigencia <= 30 && diasVigencia > 0){
            d.setEstado("PRÓXIMO A VENCER");
        } if(diasVigencia <= 0){
            d.setEstado("VENCIDO");
        }
        }    
        }

        return lista;
    }
    
    public List<Documentacion> buscarDocumentacionIdAcoplado(Long id) {
        
        List<Documentacion> lista = documentacionRepositorio.buscarDocumentacionIdAcoplado(id);
        
        if(!lista.isEmpty()){
        for(Documentacion d : lista){
            
        Date fechaActual = new Date();
        long diferenciaMillis = d.getFechaVencimiento().getTime() - fechaActual.getTime();
        int diasVigencia = (int) TimeUnit.MILLISECONDS.toDays(diferenciaMillis);  
        d.setDiasVigencia(diasVigencia);
        
        if(diasVigencia <= 30 && diasVigencia > 0){
            d.setEstado("PRÓXIMO A VENCER");
        } if(diasVigencia <= 0){
            d.setEstado("VENCIDO");
        }
        
        }    
        }

        return lista;
    }
    
    public List<Documentacion> buscarDocumentacionAcopladosIdTipo(Long idOrg, Long idTipo) {
        
        List<Documentacion> lista = documentacionRepositorio.buscarDocumentacionAcopladosPorTipo(idOrg, idTipo);
        
        if(!lista.isEmpty()){
        for(Documentacion d : lista){
            
        Date fechaActual = new Date();
        long diferenciaMillis = d.getFechaVencimiento().getTime() - fechaActual.getTime();
        int diasVigencia = (int) TimeUnit.MILLISECONDS.toDays(diferenciaMillis);  
        d.setDiasVigencia(diasVigencia);
        
        if(diasVigencia <= 30 && diasVigencia > 0){
            d.setEstado("PRÓXIMO A VENCER");
        } if(diasVigencia <= 0){
            d.setEstado("VENCIDO");
        }
        
        }    
        }

        return lista;
    }
    
    public List<Documentacion> buscarDocumentacionAcopladoIdTipo(Long idAcoplado, Long idTipo) {
        
        List<Documentacion> lista = documentacionRepositorio.buscarDocumentacionAcopladoPorTipo(idAcoplado, idTipo);
            
        if(!lista.isEmpty()){
        for(Documentacion d : lista){
            
        Date fechaActual = new Date();
        long diferenciaMillis = d.getFechaVencimiento().getTime() - fechaActual.getTime();
        int diasVigencia = (int) TimeUnit.MILLISECONDS.toDays(diferenciaMillis);  
        d.setDiasVigencia(diasVigencia);
        
        if(diasVigencia <= 30 && diasVigencia > 0){
            d.setEstado("PRÓXIMO A VENCER");
        } if(diasVigencia <= 0){
            d.setEstado("VENCIDO");
        }
        
        }    
        }

        return lista;
    }
    
    public List<Documentacion> buscarDocumentacionChoferesIdTipo(Long idOrg, Long idTipo) {
        
        List<Documentacion> lista = documentacionRepositorio.buscarDocumentacionChoferesPorTipo(idOrg, idTipo);
        
        if(!lista.isEmpty()){
        for(Documentacion d : lista){
            
        Date fechaActual = new Date();
        long diferenciaMillis = d.getFechaVencimiento().getTime() - fechaActual.getTime();
        int diasVigencia = (int) TimeUnit.MILLISECONDS.toDays(diferenciaMillis);  
        d.setDiasVigencia(diasVigencia);
        
        if(diasVigencia <= 30 && diasVigencia > 0){
            d.setEstado("PRÓXIMO A VENCER");
        } if(diasVigencia <= 0){
            d.setEstado("VENCIDO");
        }
        
        }    
        }

        return lista;
    }
    
    public List<Documentacion> buscarDocumentacionChoferIdTipo(Long idChofer, Long idTipo) {
        
        List<Documentacion> lista = documentacionRepositorio.buscarDocumentacionChoferPorTipo(idChofer, idTipo);
            
        if(!lista.isEmpty()){
        for(Documentacion d : lista){
            
        Date fechaActual = new Date();
        long diferenciaMillis = d.getFechaVencimiento().getTime() - fechaActual.getTime();
        int diasVigencia = (int) TimeUnit.MILLISECONDS.toDays(diferenciaMillis);  
        d.setDiasVigencia(diasVigencia);
        
        if(diasVigencia <= 30 && diasVigencia > 0){
            d.setEstado("PRÓXIMO A VENCER");
        } if(diasVigencia <= 0){
            d.setEstado("VENCIDO");
        }
        
        }    
        }

        return lista;
    }
    
     public List<Documentacion> obtenerDocumentacionesPorVencer(Long idOrg, int dias) {
        
        Calendar calendar = Calendar.getInstance();

        calendar.add(Calendar.DAY_OF_MONTH, dias); // Sumamos 30 días
        Date fechaLimite = calendar.getTime(); // Fecha límite
        
        List<Documentacion> lista = documentacionRepositorio.findDocumentacionesPorVencer(idOrg, fechaLimite);
        
        if(!lista.isEmpty()){
        for(Documentacion d : lista){
            
        Date fechaActual = new Date();
        long diferenciaMillis = d.getFechaVencimiento().getTime() - fechaActual.getTime();
        int diasVigencia = (int) TimeUnit.MILLISECONDS.toDays(diferenciaMillis);  
        d.setDiasVigencia(diasVigencia);
        
        if(diasVigencia <= 30 && diasVigencia > 0){
            d.setEstado("PRÓXIMO A VENCER");
        } if(diasVigencia <= 0){
            d.setEstado("VENCIDO");
        }
        
        }    
        }

        return lista;
        
    }
     
    public List<Documentacion> obtenerDocumentacionesPorVencerFiltro(Long idOrg, int dias, Long idCamion, Long idAcoplado, Long idChofer) {

        Calendar calendar = Calendar.getInstance();

        calendar.add(Calendar.DAY_OF_MONTH, dias); // Sumamos 30 días
        Date fechaLimite = calendar.getTime(); // Fecha límite

        List<Documentacion> lista = documentacionRepositorio.findDocumentacionesPorVencerFiltrado(idOrg, fechaLimite, idCamion, idAcoplado, idChofer);

        if (!lista.isEmpty()) {
            for (Documentacion d : lista) {

                Date fechaActual = new Date();
                long diferenciaMillis = d.getFechaVencimiento().getTime() - fechaActual.getTime();
                int diasVigencia = (int) TimeUnit.MILLISECONDS.toDays(diferenciaMillis);
                d.setDiasVigencia(diasVigencia);

                if (diasVigencia <= 30 && diasVigencia > 0) {
                    d.setEstado("PRÓXIMO A VENCER");
                }
                if (diasVigencia <= 0) {
                    d.setEstado("VENCIDO");
                }

            }
        }

        return lista;

    }

    public List<Documentacion> obtenerDocumentacionesPorVencerCamion(Long idCamion, int dias) {

        Calendar calendar = Calendar.getInstance();

        calendar.add(Calendar.DAY_OF_MONTH, dias); // Sumamos 30 días
        Date fechaLimite = calendar.getTime(); // Fecha límite

        List<Documentacion> lista = documentacionRepositorio.findDocumentacionesPorVencerCamion(idCamion, fechaLimite);

        if (!lista.isEmpty()) {
            for (Documentacion d : lista) {

                Date fechaActual = new Date();
                long diferenciaMillis = d.getFechaVencimiento().getTime() - fechaActual.getTime();
                int diasVigencia = (int) TimeUnit.MILLISECONDS.toDays(diferenciaMillis);
                d.setDiasVigencia(diasVigencia);

                if (diasVigencia <= 30 && diasVigencia > 0) {
                    d.setEstado("PRÓXIMO A VENCER");
                }
                if (diasVigencia <= 0) {
                    d.setEstado("VENCIDO");
                }

            }
        }

        return lista;

    }

    public List<Documentacion> obtenerDocumentacionesPorVencerAcoplado(Long idAcoplado, int dias) {

        Calendar calendar = Calendar.getInstance();

        calendar.add(Calendar.DAY_OF_MONTH, dias); // Sumamos 30 días
        Date fechaLimite = calendar.getTime(); // Fecha límite

        List<Documentacion> lista = documentacionRepositorio.findDocumentacionesPorVencerAcoplado(idAcoplado, fechaLimite);

        if (!lista.isEmpty()) {
            for (Documentacion d : lista) {

                Date fechaActual = new Date();
                long diferenciaMillis = d.getFechaVencimiento().getTime() - fechaActual.getTime();
                int diasVigencia = (int) TimeUnit.MILLISECONDS.toDays(diferenciaMillis);
                d.setDiasVigencia(diasVigencia);

                if (diasVigencia <= 30 && diasVigencia > 0) {
                    d.setEstado("PRÓXIMO A VENCER");
                }
                if (diasVigencia <= 0) {
                    d.setEstado("VENCIDO");
                }

            }
        }

        return lista;

    }

    public List<Documentacion> obtenerDocumentacionesPorVencerChofer(Long idChofer, int dias) {

        Calendar calendar = Calendar.getInstance();

        calendar.add(Calendar.DAY_OF_MONTH, dias); // Sumamos 30 días
        Date fechaLimite = calendar.getTime(); // Fecha límite

        List<Documentacion> lista = documentacionRepositorio.findDocumentacionesPorVencerChofer(idChofer, fechaLimite);

        if (!lista.isEmpty()) {
            for (Documentacion d : lista) {

                Date fechaActual = new Date();
                long diferenciaMillis = d.getFechaVencimiento().getTime() - fechaActual.getTime();
                int diasVigencia = (int) TimeUnit.MILLISECONDS.toDays(diferenciaMillis);
                d.setDiasVigencia(diasVigencia);

                if (diasVigencia <= 30 && diasVigencia > 0) {
                    d.setEstado("PRÓXIMO A VENCER");
                }
                if (diasVigencia <= 0) {
                    d.setEstado("VENCIDO");
                }

            }
        }

        return lista;

    }
 
    
    public List<Documentacion> buscarDocumentacionCamiones(Long idOrg) {
          
        List<Documentacion> lista = documentacionRepositorio.findByCamionIsNotNullAndIdOrgAndEstado(idOrg, "VIGENTE");
        
        if(!lista.isEmpty()){
        for(Documentacion d : lista){
            
        Date fechaActual = new Date();
        long diferenciaMillis = d.getFechaVencimiento().getTime() - fechaActual.getTime();
        int diasVigencia = (int) TimeUnit.MILLISECONDS.toDays(diferenciaMillis);  
        d.setDiasVigencia(diasVigencia);
        
        if(diasVigencia <= 30 && diasVigencia > 0){
            d.setEstado("PRÓXIMO A VENCER");
        } if(diasVigencia <= 0){
            d.setEstado("VENCIDO");
        }
        
        }    
        }
          
        return lista;
    }
      
    public List<Documentacion> buscarDocumentacionAcoplados(Long idOrg) {
          
        List<Documentacion> lista = documentacionRepositorio.findByAcopladoIsNotNullAndIdOrgAndEstado(idOrg, "VIGENTE");
        
        if(!lista.isEmpty()){
        for(Documentacion d : lista){
            
        Date fechaActual = new Date();
        long diferenciaMillis = d.getFechaVencimiento().getTime() - fechaActual.getTime();
        int diasVigencia = (int) TimeUnit.MILLISECONDS.toDays(diferenciaMillis);  
        d.setDiasVigencia(diasVigencia);
        
        if(diasVigencia <= 30 && diasVigencia > 0){
            d.setEstado("PRÓXIMO A VENCER");
        } if(diasVigencia <= 0){
            d.setEstado("VENCIDO");
        }
        
        }    
        }
          
        return lista;
    }
        
    public List<Documentacion> buscarDocumentacionChoferes(Long idOrg) {
          
        List<Documentacion> lista = documentacionRepositorio.findByChoferIsNotNullAndIdOrgAndEstado(idOrg, "VIGENTE");
        
        if(!lista.isEmpty()){
        for(Documentacion d : lista){
            
        Date fechaActual = new Date();
        long diferenciaMillis = d.getFechaVencimiento().getTime() - fechaActual.getTime();
        int diasVigencia = (int) TimeUnit.MILLISECONDS.toDays(diferenciaMillis);  
        d.setDiasVigencia(diasVigencia);
        
        if(diasVigencia <= 30 && diasVigencia > 0){
            d.setEstado("PRÓXIMO A VENCER");
        } if(diasVigencia <= 0){
            d.setEstado("VENCIDO");
        }
        
        }    
        }
          
        return lista;
    }
    
        public void inhabilitarDocumentacionIdCamion(Long idCamion) {

        List<Documentacion> lista = documentacionRepositorio.buscarDocumentacionIdCamion(idCamion);
        if (!lista.isEmpty()) {
            for (Documentacion d : lista) {
                d.setEstado("INHABILITADO");
                documentacionRepositorio.save(d);

            }
        }
    }

    public void habilitarDocumentacionIdCamion(Long idCamion) {

        List<Documentacion> lista = documentacionRepositorio.buscarDocumentacionInhabilitadoIdCamion(idCamion);
        if (!lista.isEmpty()) {
            for (Documentacion d : lista) {
                d.setEstado("VIGENTE");
                documentacionRepositorio.save(d);

            }
        }
    }

    public void inhabilitarDocumentacionIdAcoplado(Long idAcoplado) {

        List<Documentacion> lista = documentacionRepositorio.buscarDocumentacionIdAcoplado(idAcoplado);
        if (!lista.isEmpty()) {
            for (Documentacion d : lista) {
                d.setEstado("INHABILITADO");
                documentacionRepositorio.save(d);

            }
        }
    }

    public void habilitarDocumentacionIdAcoplado(Long idAcoplado) {

        List<Documentacion> lista = documentacionRepositorio.buscarDocumentacionInhabilitadoIdAcoplado(idAcoplado);
        if (!lista.isEmpty()) {
            for (Documentacion d : lista) {
                d.setEstado("VIGENTE");
                documentacionRepositorio.save(d);

            }
        }
    }

    public void inhabilitarDocumentacionIdChofer(Long idChofer) {

        List<Documentacion> lista = documentacionRepositorio.buscarDocumentacionIdChofer(idChofer);
        if (!lista.isEmpty()) {
            for (Documentacion d : lista) {
                d.setEstado("INHABILITADO");
                documentacionRepositorio.save(d);

            }
        }
    }

    public void habilitarDocumentacionIdChofer(Long idChofer) {

        List<Documentacion> lista = documentacionRepositorio.buscarDocumentacionInhabilitadoIdChofer(idChofer);
        if (!lista.isEmpty()) {
            for (Documentacion d : lista) {
                d.setEstado("VIGENTE");
                documentacionRepositorio.save(d);

            }
        }
    }
    
    public void validarDatos(Long tipoId, TipoDocumentacion.AplicaA aplicaA, Long idEntidad) throws MiException {
        
        TipoDocumentacion tipo = tipoDocumentacionRepositorio.getById(tipoId);

        if(aplicaA == TipoDocumentacion.AplicaA.CAMION){
            Camion camion = camionRepositorio.getById(idEntidad);
            Optional<Documentacion> docu = documentacionRepositorio.findByTipoDocumentacionIdAndCamionId(tipoId, camion.getId());
            if(docu.isPresent()){
                throw new MiException("El Camión '"+camion.getDominio()+"' ya tiene '"+tipo.getNombre()+"' asociado.");
            }
            
        } else if(aplicaA == TipoDocumentacion.AplicaA.ACOPLADO){
            Acoplado acoplado = acopladoRepositorio.getById(idEntidad);
            Optional<Documentacion> docu = documentacionRepositorio.findByTipoDocumentacionIdAndAcopladoId(tipoId, acoplado.getId());
            if(docu.isPresent()){
                throw new MiException("El Acoplado '"+acoplado.getDominio()+"' ya tiene '"+tipo.getNombre()+"' asociado.");
            }
            
        } else { 
            Usuario chofer = usuarioRepositorio.getById(idEntidad);
             Optional<Documentacion> docu = documentacionRepositorio.findByTipoDocumentacionIdAndChoferId(tipoId, chofer.getId());
            if(docu.isPresent()){
                throw new MiException("El Chofer '"+chofer.getNombre()+"' ya tiene '"+tipo.getNombre()+"' asociado.");
        }   
        }
    }
    
    
    public void validarDatosModificar(Long id, Long tipoId, TipoDocumentacion.AplicaA aplicaA, Long idEntidad) throws MiException {
        
        TipoDocumentacion tipo = tipoDocumentacionRepositorio.getById(tipoId);

        Documentacion documentacion = documentacionRepositorio.getById(id);
        
        if(aplicaA == TipoDocumentacion.AplicaA.CAMION){
            Camion camion = camionRepositorio.getById(idEntidad);
            Optional<Documentacion> docu = documentacionRepositorio.findByTipoDocumentacionIdAndCamionId(tipoId, camion.getId());
            if(docu.isPresent()){
                Documentacion doc = docu.get();
                if(doc.getId() != documentacion.getId()){
                throw new MiException("El Camión '"+camion.getDominio()+"' ya tiene '"+tipo.getNombre()+"' asociado.");
                }
            }

        } else if(aplicaA == TipoDocumentacion.AplicaA.ACOPLADO){
            Acoplado acoplado = acopladoRepositorio.getById(idEntidad);
            Optional<Documentacion> docu = documentacionRepositorio.findByTipoDocumentacionIdAndAcopladoId(tipoId, acoplado.getId());
            if(docu.isPresent()){
                Documentacion doc = docu.get();
                if(doc.getId() != documentacion.getId()){
                throw new MiException("El Acoplado '"+acoplado.getDominio()+"' ya tiene '"+tipo.getNombre()+"' asociado.");
                }
            }
        } else {
            Usuario chofer = usuarioRepositorio.getById(idEntidad);
             Optional<Documentacion> docu = documentacionRepositorio.findByTipoDocumentacionIdAndChoferId(tipoId, chofer.getId());
            if(docu.isPresent()){
                Documentacion doc = docu.get();
                if(doc.getId() != documentacion.getId()){
                throw new MiException("El Chofer '"+chofer.getNombre()+"' ya tiene '"+tipo.getNombre()+"' asociado.");
                }
        } 
    }
    }
    
    public Date convertirFecha(String fecha) throws ParseException {
        SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd");
        return formato.parse(fecha);
    }
    
}
