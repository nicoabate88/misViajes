
package abate.abate.servicios;

import abate.abate.dto.AuxilioDTO;
import abate.abate.dto.PosicionDTO;
import abate.abate.entidades.Acoplado;
import abate.abate.entidades.AuxilioNeumatico;
import abate.abate.entidades.Camion;
import abate.abate.entidades.Eje;
import abate.abate.entidades.HistorialNeumatico;
import abate.abate.entidades.HistorialRecapado;
import abate.abate.entidades.Neumatico;
import abate.abate.entidades.NeumaticoMarca;
import abate.abate.entidades.NeumaticoProveedor;
import abate.abate.entidades.PosicionNeumatico;
import abate.abate.entidades.Usuario;
import abate.abate.excepciones.MiException;
import abate.abate.repositorios.AcopladoRepositorio;
import abate.abate.repositorios.AuxilioNeumaticoRepositorio;
import abate.abate.repositorios.CamionRepositorio;
import abate.abate.repositorios.EjeRepositorio;
import abate.abate.repositorios.HistorialNeumaticoRepositorio;
import abate.abate.repositorios.HistorialRecapadoRepositorio;
import abate.abate.repositorios.NeumaticoMarcaRepositorio;
import abate.abate.repositorios.NeumaticoProveedorRepositorio;
import abate.abate.repositorios.NeumaticoRepositorio;
import abate.abate.repositorios.PosicionNeumaticoRepositorio;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NeumaticoServicio {
    
    @Autowired
    private NeumaticoRepositorio neumaticoRepositorio;
    @Autowired
    private NeumaticoMarcaRepositorio marcaRepositorio;
    @Autowired
    private NeumaticoProveedorRepositorio proveedorRepositorio;
    @Autowired
    private CombustibleServicio combustibleServicio;
    @Autowired
    private AcopladoServicio acopladoServicio;
    @Autowired
    private AuxilioNeumaticoServicio auxilioServicio;
    @Autowired
    private HistorialNeumaticoRepositorio historialRepositorio;
    @Autowired
    private HistorialRecapadoRepositorio recapadoRepositorio;
    @Autowired
    private PosicionNeumaticoRepositorio posicionRepositorio;
    @Autowired
    private EjeRepositorio ejeRepositorio;
    @Autowired
    private AuxilioNeumaticoRepositorio auxilioRepositorio;
    @Autowired
    private CamionRepositorio camionRepositorio;
    @Autowired
    private AcopladoRepositorio acopladoRepositorio;
    
    
    @Transactional
    public void crearNeumatico(Integer numero, Long idMarca, String modelo, Long idProveedor, Integer km, Integer kmEstimado,
            String fechaIngreso, List<Neumatico.AplicaA> aplicaA, Integer cantidad, String observacion, String estado, Usuario usuario) throws ParseException, MiException {
        
        validarDatos(numero, usuario.getIdOrg());

        Date fecha = convertirFecha(fechaIngreso);
        NeumaticoMarca marca = marcaRepositorio.getById(idMarca);
        NeumaticoProveedor proveedor = proveedorRepositorio.getById(idProveedor);
        
        if(cantidad == 1){

        Neumatico neumatico = new Neumatico();

        neumatico.setNumero(numero);
        neumatico.setIdOrg(usuario.getIdOrg());
        neumatico.setMarca(marca);
        neumatico.setModelo(modelo.toUpperCase());
        neumatico.setProveedor(proveedor);
        neumatico.setKm(km);
        neumatico.setKmIngreso(km);
        neumatico.setKmEstimado(kmEstimado);
        neumatico.setKmUtil(kmEstimado - km);
        neumatico.setEstado(estado);
        neumatico.setUbicacion("DEPOSITO");
        neumatico.setUsuario(usuario);
        neumatico.setFechaIngreso(fecha);
        neumatico.setObservacion(observacion.toUpperCase());
        neumatico.setAplicaA(aplicaA);
        
        neumaticoRepositorio.save(neumatico);
        
        } else {    
        
        for (int i = 0; i < cantidad; i++) {
            
        int numeroNeumatico = numero + i;    
        
        Neumatico neumatico = new Neumatico();

        neumatico.setNumero(numeroNeumatico);
        neumatico.setIdOrg(usuario.getIdOrg());
        neumatico.setMarca(marca);
        neumatico.setModelo(modelo.toUpperCase());
        neumatico.setProveedor(proveedor);
        neumatico.setKm(km);
        neumatico.setKmIngreso(km);
        neumatico.setKmEstimado(kmEstimado);
        neumatico.setKmUtil(kmEstimado - km);
        neumatico.setEstado(estado);
        neumatico.setUbicacion("DEPOSITO");
        neumatico.setUsuario(usuario);
        neumatico.setFechaIngreso(fecha);
        neumatico.setObservacion(observacion.toUpperCase());
        neumatico.setAplicaA(aplicaA);
        
        neumaticoRepositorio.save(neumatico); 
        
        }
            
        }
        
    }
    
    @Transactional
    public void modificarNeumatico(Long idNeumatico, Integer numero, Long idMarca, String modelo, Long idProveedor, Integer km, Integer kmEstimado, 
            String fechaIngreso, List<Neumatico.AplicaA> aplicaA, String observacion, String estado, String ubicacion, Usuario usuario) throws ParseException, MiException{
        
        Neumatico neumatico = neumaticoRepositorio.getById(idNeumatico);
        
        Boolean flag = verificarHistorialNeumatico(idNeumatico);
        
        if(!neumatico.getNumero().equals(numero)){
            
                validarDatos(numero, usuario.getIdOrg());
            
        }   
        
        Date fecha = convertirFecha(fechaIngreso);
        NeumaticoMarca marca = marcaRepositorio.getById(idMarca);
        NeumaticoProveedor proveedor = proveedorRepositorio.getById(idProveedor);
        neumatico.setNumero(numero);
        neumatico.setMarca(marca);
        neumatico.setModelo(modelo.toUpperCase());
        neumatico.setProveedor(proveedor);
        neumatico.setKm(km);
        if(flag == false){
        neumatico.setKmIngreso(km);
        }
        neumatico.setKmEstimado(kmEstimado);
        neumatico.setKmUtil(kmEstimado - km);
        neumatico.setEstado(estado);
        neumatico.setUsuario(usuario);
        neumatico.setFechaIngreso(fecha);
        neumatico.setObservacion(observacion.toUpperCase());
        neumatico.setAplicaA(aplicaA);
        
        if(ubicacion.equalsIgnoreCase("FUERA_DE_SERVICIO")){
            neumatico.setUbicacion("FUERA DE SERVICIO");
            neumatico.setFechaEgreso(new Date());
            List<HistorialRecapado> recapados = neumatico.getRecapados();
            if(!recapados.isEmpty()){
                for(HistorialRecapado r : recapados){
                    if(r.getEstado().equalsIgnoreCase("VIGENTE")){
                        r.setEstado("FINALIZADO");
                        r.setKmFinalRecapado(km);
                        r.setKmRecapado(km - r.getKmAlRecapar());
                        recapadoRepositorio.save(r);
                    }
            }
            }
        }
        
        if(neumatico.getUbicacion().equalsIgnoreCase("FUERA DE SERVICIO") && ubicacion.equalsIgnoreCase("DEPOSITO")){
            neumatico.setUbicacion("DEPOSITO");
            neumatico.setFechaEgreso(null);
            List<HistorialRecapado> recapados = neumatico.getRecapados();
            if(!recapados.isEmpty()){
                for(HistorialRecapado r : recapados){
                    if(r.getEstado().equalsIgnoreCase("FINALIZADO")){
                        r.setEstado("VIGENTE");
                        r.setKmFinalRecapado(null);
                        r.setKmRecapado(null);
                        recapadoRepositorio.save(r);
                    }
            }
            }
        }
        
        neumaticoRepositorio.save(neumatico);
        
    }
    
    @Transactional
    public void eliminarNeumatico(Long id) throws MiException{
        
       boolean flag = posicionRepositorio.existsByNeumaticoId(id);
       boolean flag1 = auxilioRepositorio.existsByNeumaticoId(id);
       
        if (flag == false && flag1 == false) {
            
            neumaticoRepositorio.deleteById(id);

        } else {

            throw new MiException("El Neumático no puede ser eliminado, tiene historial registrado.");
        }
        
    }
    
    @Transactional
    public void modificarHistorial(Long id, String fecha, Integer km, Usuario logueado) throws ParseException {

        HistorialNeumatico historial = historialRepositorio.getById(id);

        Date fechaColocacion = convertirFecha(fecha);

        historial.setFechaColocacion(fechaColocacion);
        historial.setKmColocacion(km);
        historial.setUsuario(logueado);

        historialRepositorio.save(historial);

    }
    
    @Transactional
    public void eliminarHistorial(Long id)  {

        HistorialNeumatico historial = historialRepositorio.getById(id);

        Neumatico neumatico = historial.getNeumatico();
        
        PosicionNeumatico posicion = historial.getPosicion();
        
        historial.setPosicion(null);
        historialRepositorio.save(historial); 
         
        posicionRepositorio.delete(posicion);
        neumatico.getHistorial().remove(historial);
        
        neumatico.setUbicacion("DEPOSITO");
        
                      
        neumaticoRepositorio.save(neumatico);

    }
    
    
    public Neumatico buscarNeumatico(Long id){
        
        return neumaticoRepositorio.getById(id);
        
    }
    
    public void validarDatos(Integer numero, Long idOrg) throws MiException {
        
        boolean existe = neumaticoRepositorio.existsByNumeroAndIdOrg(numero, idOrg);

        if (existe) {
            throw new MiException("El Número de Neumático '"+numero+"' ya está registrado. Por favor, ingrese otro");
        } 
    } 
    
    public Integer obtenerProximoNumero(Long idOrg) {

           return neumaticoRepositorio.findTopByIdOrgOrderByNumeroDesc(idOrg)
            .map(neumatico -> neumatico.getNumero() + 1)
            .orElse(1);
           
    }
    
    public HistorialNeumatico buscarHistorial(Long id){
        
        return historialRepositorio.getById(id);
        
    }
    
    public Boolean verificarHistorialNeumatico(Long id){
        
        return historialRepositorio.existsByNeumaticoId(id);
        
    }
    
    public Neumatico ultimoNeumatico(Long idOrg){
        
        return neumaticoRepositorio.ultimoNeumatico(idOrg);
        
    }
    
    public List<Neumatico> buscarNeumaticosAplicaA(Neumatico.AplicaA aplicaA, Long idOrg) {

        return neumaticoRepositorio.findByAplicaA(aplicaA, idOrg);

    }
    
    
    public List<Neumatico> buscarNeumaticosDeposito(Long idOrg){
        
        return neumaticoRepositorio.findByDeposito(idOrg);
        
    }
    
    public List<HistorialRecapado> buscarRecapadoEnRecapado(Long idOrg){
        
        return recapadoRepositorio.findByEnRecapado(idOrg);
        
    }
        
    public List<Neumatico> buscarNeumaticos(Long idOrg){
        
        List<Neumatico> neumaticos = neumaticoRepositorio.buscarNeumaticosIdOrg(idOrg);
        
        for (Neumatico n : neumaticos) {
            if(n.getUbicacion().equalsIgnoreCase("COLOCADO")){
            List<HistorialNeumatico> historialList = n.getHistorial();
            if (historialList != null && !historialList.isEmpty()) {
                HistorialNeumatico historial = historialList.get(historialList.size() - 1);
                if (historial.getCamion() != null) {
                    int km = combustibleServicio.kmUltimaCarga(historial.getCamion());
                    Integer kmRecorrido = km - historial.getKmColocacion();
                    Integer kmNeumatico = n.getKm() + kmRecorrido;
                    if(historial.getPosicion().getEje().getElevable() == true){  
                     kmRecorrido = (kmRecorrido *  historial.getPosicion().getEje().getPorcentaje()) / 100;
                     kmNeumatico = n.getKm() + kmRecorrido;   
                    }
                    n.setKm(kmNeumatico);
                    n.setKmUtil(n.getKmEstimado() - kmNeumatico);
                    n.setUbicacion("COLOCADO (Camión " + historial.getCamion().getDominio() + " " + historial.getCamion().getMarca() + " " + historial.getCamion().getModelo()+ ")");
                } else {
                    int km = combustibleServicio.kmAcoplado(historial.getAcoplado(), obtenerFechaFija());
                    Integer kmRecorrido = km - historial.getKmColocacion();
                    Integer kmNeumatico = n.getKm() + kmRecorrido;
                    if(historial.getPosicion().getEje().getElevable() == true){  
                     kmRecorrido = (kmRecorrido *  historial.getPosicion().getEje().getPorcentaje()) / 100;
                     kmNeumatico = n.getKm() + kmRecorrido;   
                    }
                    n.setKm(kmNeumatico);
                    n.setKmUtil(n.getKmEstimado() - kmNeumatico);
                    n.setUbicacion("COLOCADO (Acoplado " + historial.getAcoplado().getDominio() + " " + historial.getAcoplado().getMarca() + " " + historial.getAcoplado().getModelo()+ ")");
                } 

            }
            } if(n.getUbicacion().equalsIgnoreCase("AUXILIO")){
            
           AuxilioNeumatico auxilio = auxilioServicio.buscarAuxilioIdNeumatico(n.getId());
           
           if(auxilio.getCamion() != null){
              
               n.setUbicacion("AUXILIO (Camión " + auxilio.getCamion().getDominio() + " " + auxilio.getCamion().getMarca() + " " + auxilio.getCamion().getModelo()+ ")");
           
           } else{
               
               n.setUbicacion("AUXILIO (Acoplado " + auxilio.getAcoplado().getDominio() + " " + auxilio.getAcoplado().getMarca() + " " + auxilio.getAcoplado().getModelo()+ ")");
           
           } 
            
        }
        }

        return neumaticos;
        
    }    
    
    public List<Neumatico> buscarNeumaticosDepositoNuevo(Long idOrg){
        
        return neumaticoRepositorio.findByDepositoNuevo(idOrg);
        
    }
    
    public List<Neumatico> buscarNeumaticosDepositoUsado(Long idOrg){
        
        return neumaticoRepositorio.findByDepositoUsado(idOrg);
        
    }
    
    public List<Neumatico> buscarNeumaticosDepositoRecapado(Long idOrg){
        
        return neumaticoRepositorio.findByDepositoRecapado(idOrg);
        
    }
    
    public List<Neumatico> buscarNeumaticosColocado(Long idOrg) {

        List<Neumatico> neumaticos = neumaticoRepositorio.findByColocado(idOrg);

            for (Neumatico n : neumaticos) {
            if(n.getUbicacion().equalsIgnoreCase("COLOCADO")){
            List<HistorialNeumatico> historialList = n.getHistorial();
            if (historialList != null && !historialList.isEmpty()) {
                HistorialNeumatico historial = historialList.get(historialList.size() - 1);
                if (historial.getCamion() != null) {
                    int km = combustibleServicio.kmUltimaCarga(historial.getCamion());
                    Integer kmRecorrido = km - historial.getKmColocacion();
                    Integer kmNeumatico = n.getKm() + kmRecorrido;
                    if(historial.getPosicion().getEje().getElevable() == true){  
                     kmRecorrido = (kmRecorrido *  historial.getPosicion().getEje().getPorcentaje()) / 100;
                     kmNeumatico = n.getKm() + kmRecorrido;   
                    }
                    n.setKm(kmNeumatico);
                    n.setKmUtil(n.getKmEstimado() - kmNeumatico);
                    n.setUbicacion("COLOCADO (Camión " + historial.getCamion().getDominio() + " " + historial.getCamion().getMarca() + " " + historial.getCamion().getModelo()+ ")");
                } else {
                    int km = combustibleServicio.kmAcoplado(historial.getAcoplado(), obtenerFechaFija());
                    Integer kmRecorrido = km - historial.getKmColocacion();
                    Integer kmNeumatico = n.getKm() + kmRecorrido;
                    if(historial.getPosicion().getEje().getElevable() == true){  
                     kmRecorrido = (kmRecorrido *  historial.getPosicion().getEje().getPorcentaje()) / 100;
                     kmNeumatico = n.getKm() + kmRecorrido;   
                    }
                    n.setKm(kmNeumatico);
                    n.setKmUtil(n.getKmEstimado() - kmNeumatico);
                    n.setUbicacion("COLOCADO (Acoplado " + historial.getAcoplado().getDominio() + " " + historial.getAcoplado().getMarca() + " " + historial.getAcoplado().getModelo()+ ")");
                } 

            }
            } if(n.getUbicacion().equalsIgnoreCase("AUXILIO")){
            
           AuxilioNeumatico auxilio = auxilioServicio.buscarAuxilioIdNeumatico(n.getId());
           
           if(auxilio.getCamion() != null){
              
               n.setUbicacion("AUXILIO (Camión " + auxilio.getCamion().getDominio() + " " + auxilio.getCamion().getMarca() + " " + auxilio.getCamion().getModelo()+ ")");
           
           } else{
               
               n.setUbicacion("AUXILIO (Acoplado " + auxilio.getAcoplado().getDominio() + " " + auxilio.getAcoplado().getMarca() + " " + auxilio.getAcoplado().getModelo()+ ")");
           
           } 
            
        }
        }

        return neumaticos;

    }
    
    public List<Neumatico> buscarNeumaticosColocadoNuevo(Long idOrg){
        
        List<Neumatico> neumaticos = neumaticoRepositorio.findByColocadoNuevo(idOrg);

        for (Neumatico n : neumaticos) {
            if(n.getUbicacion().equalsIgnoreCase("COLOCADO")){
            List<HistorialNeumatico> historialList = n.getHistorial();
            if (historialList != null && !historialList.isEmpty()) {
                HistorialNeumatico historial = historialList.get(historialList.size() - 1);
                if (historial.getCamion() != null) {
                    int km = combustibleServicio.kmUltimaCarga(historial.getCamion());
                    Integer kmRecorrido = km - historial.getKmColocacion();
                    Integer kmNeumatico = n.getKm() + kmRecorrido;
                    if(historial.getPosicion().getEje().getElevable() == true){  
                     kmRecorrido = (kmRecorrido *  historial.getPosicion().getEje().getPorcentaje()) / 100;
                     kmNeumatico = n.getKm() + kmRecorrido;   
                    }
                    n.setKm(kmNeumatico);
                    n.setKmUtil(n.getKmEstimado() - kmNeumatico);
                    n.setUbicacion("COLOCADO (Camión " + historial.getCamion().getDominio() + " " + historial.getCamion().getMarca() + " " + historial.getCamion().getModelo()+ ")");
                } else {
                    int km = combustibleServicio.kmAcoplado(historial.getAcoplado(), obtenerFechaFija());
                    Integer kmRecorrido = km - historial.getKmColocacion();
                    Integer kmNeumatico = n.getKm() + kmRecorrido;
                    if(historial.getPosicion().getEje().getElevable() == true){  
                     kmRecorrido = (kmRecorrido *  historial.getPosicion().getEje().getPorcentaje()) / 100;
                     kmNeumatico = n.getKm() + kmRecorrido;   
                    }
                    n.setKm(kmNeumatico);
                    n.setKmUtil(n.getKmEstimado() - kmNeumatico);
                    n.setUbicacion("COLOCADO (Acoplado " + historial.getAcoplado().getDominio() + " " + historial.getAcoplado().getMarca() + " " + historial.getAcoplado().getModelo()+ ")");
                } 

            }
            } if(n.getUbicacion().equalsIgnoreCase("AUXILIO")){
            
           AuxilioNeumatico auxilio = auxilioServicio.buscarAuxilioIdNeumatico(n.getId());
           
           if(auxilio.getCamion() != null){
              
               n.setUbicacion("AUXILIO (Camión " + auxilio.getCamion().getDominio() + " " + auxilio.getCamion().getMarca() + " " + auxilio.getCamion().getModelo()+ ")");
           
           } else{
               
               n.setUbicacion("AUXILIO (Acoplado " + auxilio.getAcoplado().getDominio() + " " + auxilio.getAcoplado().getMarca() + " " + auxilio.getAcoplado().getModelo()+ ")");
           
           } 
            
        }
        }

        return neumaticos;
        
    }
    
    public List<Neumatico> buscarNeumaticosColocadoUsado(Long idOrg){
        
        List<Neumatico> neumaticos = neumaticoRepositorio.findByColocadoUsado(idOrg);

            for (Neumatico n : neumaticos) {
            if(n.getUbicacion().equalsIgnoreCase("COLOCADO")){
            List<HistorialNeumatico> historialList = n.getHistorial();
            if (historialList != null && !historialList.isEmpty()) {
                HistorialNeumatico historial = historialList.get(historialList.size() - 1);
                if (historial.getCamion() != null) {
                    int km = combustibleServicio.kmUltimaCarga(historial.getCamion());
                    Integer kmRecorrido = km - historial.getKmColocacion();
                    Integer kmNeumatico = n.getKm() + kmRecorrido;
                    if(historial.getPosicion().getEje().getElevable() == true){  
                     kmRecorrido = (kmRecorrido *  historial.getPosicion().getEje().getPorcentaje()) / 100;
                     kmNeumatico = n.getKm() + kmRecorrido;   
                    }
                    n.setKm(kmNeumatico);
                    n.setKmUtil(n.getKmEstimado() - kmNeumatico);
                    n.setUbicacion("COLOCADO (Camión " + historial.getCamion().getDominio() + " " + historial.getCamion().getMarca() + " " + historial.getCamion().getModelo()+ ")");
                } else {
                    int km = combustibleServicio.kmAcoplado(historial.getAcoplado(), obtenerFechaFija());
                    Integer kmRecorrido = km - historial.getKmColocacion();
                    Integer kmNeumatico = n.getKm() + kmRecorrido;
                    if(historial.getPosicion().getEje().getElevable() == true){  
                     kmRecorrido = (kmRecorrido *  historial.getPosicion().getEje().getPorcentaje()) / 100;
                     kmNeumatico = n.getKm() + kmRecorrido;   
                    }
                    n.setKm(kmNeumatico);
                    n.setKmUtil(n.getKmEstimado() - kmNeumatico);
                    n.setUbicacion("COLOCADO (Acoplado " + historial.getAcoplado().getDominio() + " " + historial.getAcoplado().getMarca() + " " + historial.getAcoplado().getModelo()+ ")");
                } 

            }
            } if(n.getUbicacion().equalsIgnoreCase("AUXILIO")){
            
           AuxilioNeumatico auxilio = auxilioServicio.buscarAuxilioIdNeumatico(n.getId());
           
           if(auxilio.getCamion() != null){
              
               n.setUbicacion("AUXILIO (Camión " + auxilio.getCamion().getDominio() + " " + auxilio.getCamion().getMarca() + " " + auxilio.getCamion().getModelo()+ ")");
           
           } else{
               
               n.setUbicacion("AUXILIO (Acoplado " + auxilio.getAcoplado().getDominio() + " " + auxilio.getAcoplado().getMarca() + " " + auxilio.getAcoplado().getModelo()+ ")");
           
           } 
            
        }
        }

        return neumaticos;
        
    }
    
    public List<Neumatico> buscarNeumaticosColocadoRecapado(Long idOrg){
        
        List<Neumatico> neumaticos = neumaticoRepositorio.findByColocadoRecapado(idOrg);

            for (Neumatico n : neumaticos) {
            if(n.getUbicacion().equalsIgnoreCase("COLOCADO")){
            List<HistorialNeumatico> historialList = n.getHistorial();
            if (historialList != null && !historialList.isEmpty()) {
                HistorialNeumatico historial = historialList.get(historialList.size() - 1);
                if (historial.getCamion() != null) {
                    int km = combustibleServicio.kmUltimaCarga(historial.getCamion());
                    Integer kmRecorrido = km - historial.getKmColocacion();
                    Integer kmNeumatico = n.getKm() + kmRecorrido;
                    if(historial.getPosicion().getEje().getElevable() == true){  
                     kmRecorrido = (kmRecorrido *  historial.getPosicion().getEje().getPorcentaje()) / 100;
                     kmNeumatico = n.getKm() + kmRecorrido;   
                    }
                    n.setKm(kmNeumatico);
                    n.setKmUtil(n.getKmEstimado() - kmNeumatico);
                    n.setUbicacion("COLOCADO (Camión " + historial.getCamion().getDominio() + " " + historial.getCamion().getMarca() + " " + historial.getCamion().getModelo()+ ")");
                } else {
                    int km = combustibleServicio.kmAcoplado(historial.getAcoplado(), obtenerFechaFija());
                    Integer kmRecorrido = km - historial.getKmColocacion();
                    Integer kmNeumatico = n.getKm() + kmRecorrido;
                    if(historial.getPosicion().getEje().getElevable() == true){  
                     kmRecorrido = (kmRecorrido *  historial.getPosicion().getEje().getPorcentaje()) / 100;
                     kmNeumatico = n.getKm() + kmRecorrido;   
                    }
                    n.setKm(kmNeumatico);
                    n.setKmUtil(n.getKmEstimado() - kmNeumatico);
                    n.setUbicacion("COLOCADO (Acoplado " + historial.getAcoplado().getDominio() + " " + historial.getAcoplado().getMarca() + " " + historial.getAcoplado().getModelo()+ ")");
                } 

            }
            } if(n.getUbicacion().equalsIgnoreCase("AUXILIO")){
            
           AuxilioNeumatico auxilio = auxilioServicio.buscarAuxilioIdNeumatico(n.getId());
           
           if(auxilio.getCamion() != null){
              
               n.setUbicacion("AUXILIO (Camión " + auxilio.getCamion().getDominio() + " " + auxilio.getCamion().getMarca() + " " + auxilio.getCamion().getModelo()+ ")");
           
           } else{
               
               n.setUbicacion("AUXILIO (Acoplado " + auxilio.getAcoplado().getDominio() + " " + auxilio.getAcoplado().getMarca() + " " + auxilio.getAcoplado().getModelo()+ ")");
           
           } 
            
        }
        }

        return neumaticos;
        
    }
    
    public List<Neumatico> buscarNeumaticosAuxilio(Long idOrg){
        
        List<Neumatico> neumaticos = neumaticoRepositorio.findByAuxilio(idOrg);
        
        for(Neumatico n : neumaticos){
            
           AuxilioNeumatico auxilio = auxilioServicio.buscarAuxilioIdNeumatico(n.getId());
           
           if(auxilio.getCamion() != null){
              
               n.setUbicacion("AUXILIO (Camión " + auxilio.getCamion().getDominio() + " " + auxilio.getCamion().getMarca() + " " + auxilio.getCamion().getModelo()+ ")");
           
           } else{
               
               n.setUbicacion("AUXILIO (Acoplado " + auxilio.getAcoplado().getDominio() + " " + auxilio.getAcoplado().getMarca() + " " + auxilio.getAcoplado().getModelo()+ ")");
           
           } 
            
        }
        
        return neumaticos;
        
        
    }
    
    public List<Neumatico> buscarNeumaticosAuxilioNuevo(Long idOrg){
        
        List<Neumatico> neumaticos = neumaticoRepositorio.findByAuxilioNuevo(idOrg);
        
        for(Neumatico n : neumaticos){
            
           AuxilioNeumatico auxilio = auxilioServicio.buscarAuxilioIdNeumatico(n.getId());
           
           if(auxilio.getCamion() != null){
              
               n.setUbicacion("AUXILIO (Camión " + auxilio.getCamion().getDominio() + " " + auxilio.getCamion().getMarca() + " " + auxilio.getCamion().getModelo()+ ")");
           
           } else{
               
               n.setUbicacion("AUXILIO (Acoplado " + auxilio.getAcoplado().getDominio() + " " + auxilio.getAcoplado().getMarca() + " " + auxilio.getAcoplado().getModelo()+ ")");
           
           } 
            
        }
        
        return neumaticos;
        
    }
    
    public List<Neumatico> buscarNeumaticosAuxilioUsado(Long idOrg){
        
        List<Neumatico> neumaticos = neumaticoRepositorio.findByAuxilioUsado(idOrg);
        
        for(Neumatico n : neumaticos){
            
           AuxilioNeumatico auxilio = auxilioServicio.buscarAuxilioIdNeumatico(n.getId());
           
           if(auxilio.getCamion() != null){
              
               n.setUbicacion("AUXILIO (Camión " + auxilio.getCamion().getDominio() + " " + auxilio.getCamion().getMarca() + " " + auxilio.getCamion().getModelo()+ ")");
           
           } else{
               
               n.setUbicacion("AUXILIO (Acoplado " + auxilio.getAcoplado().getDominio() + " " + auxilio.getAcoplado().getMarca() + " " + auxilio.getAcoplado().getModelo()+ ")");
           
           } 
            
        }
        
        return neumaticos;
        
    }
    
    public List<Neumatico> buscarNeumaticosAuxilioRecapado(Long idOrg){
        
        List<Neumatico> neumaticos = neumaticoRepositorio.findByAuxilioRecapado(idOrg);
        
        for(Neumatico n : neumaticos){
            
           AuxilioNeumatico auxilio = auxilioServicio.buscarAuxilioIdNeumatico(n.getId());
           
           if(auxilio.getCamion() != null){
              
               n.setUbicacion("AUXILIO (Camión " + auxilio.getCamion().getDominio() + " " + auxilio.getCamion().getMarca() + " " + auxilio.getCamion().getModelo()+ ")");
           
           } else{
               
               n.setUbicacion("AUXILIO (Acoplado " + auxilio.getAcoplado().getDominio() + " " + auxilio.getAcoplado().getMarca() + " " + auxilio.getAcoplado().getModelo()+ ")");
           
           } 
            
        }
        
        return neumaticos;
        
    }
    
    public List<Neumatico> buscarNeumaticosEnRecapado(Long idOrg){
        
        return neumaticoRepositorio.findByEnRecapado(idOrg);
        
    }
    
    public List<Neumatico> buscarNeumaticosEnRecapadoNuevo(Long idOrg){
        
        return neumaticoRepositorio.findByEnRecapadoNuevo(idOrg);
        
    }
    
    public List<Neumatico> buscarNeumaticosEnRecapadoUsado(Long idOrg){
        
        return neumaticoRepositorio.findByEnRecapadoUsado(idOrg);
        
    }
    
    public List<Neumatico> buscarNeumaticosEnRecapadoRecapado(Long idOrg){
        
        return neumaticoRepositorio.findByEnRecapadoRecapado(idOrg);
        
    }
    
    public List<Neumatico> buscarNeumaticosFueraServicio(Long idOrg){
        
        return neumaticoRepositorio.findByFueraServicio(idOrg);
        
    }
    
    public List<Neumatico> buscarNeumaticosFueraServicioNuevo(Long idOrg){
        
        return neumaticoRepositorio.findByFueraServicioNuevo(idOrg);
        
    }
    
    public List<Neumatico> buscarNeumaticosFueraServicioUsado(Long idOrg){
        
        return neumaticoRepositorio.findByFueraServicioUsado(idOrg);
        
    }
    
    public List<Neumatico> buscarNeumaticosFueraServicioRecapado(Long idOrg){
        
        return neumaticoRepositorio.findByFueraServicioRecapado(idOrg);
        
    }
    
    public List<Neumatico> buscarNeumaticosNuevo(Long idOrg){
        
        List<Neumatico> neumaticos = neumaticoRepositorio.findByNuevo(idOrg);
        
            for (Neumatico n : neumaticos) {
            if(n.getUbicacion().equalsIgnoreCase("COLOCADO")){
            List<HistorialNeumatico> historialList = n.getHistorial();
            if (historialList != null && !historialList.isEmpty()) {
                HistorialNeumatico historial = historialList.get(historialList.size() - 1);
                if (historial.getCamion() != null) {
                    int km = combustibleServicio.kmUltimaCarga(historial.getCamion());
                    Integer kmRecorrido = km - historial.getKmColocacion();
                    Integer kmNeumatico = n.getKm() + kmRecorrido;
                    if(historial.getPosicion().getEje().getElevable() == true){  
                     kmRecorrido = (kmRecorrido *  historial.getPosicion().getEje().getPorcentaje()) / 100;
                     kmNeumatico = n.getKm() + kmRecorrido;   
                    }
                    n.setKm(kmNeumatico);
                    n.setKmUtil(n.getKmEstimado() - kmNeumatico);
                    n.setUbicacion("COLOCADO (Camión " + historial.getCamion().getDominio() + " " + historial.getCamion().getMarca() + " " + historial.getCamion().getModelo()+ ")");
                } else {
                    int km = combustibleServicio.kmAcoplado(historial.getAcoplado(), obtenerFechaFija());
                    Integer kmRecorrido = km - historial.getKmColocacion();
                    Integer kmNeumatico = n.getKm() + kmRecorrido;
                    if(historial.getPosicion().getEje().getElevable() == true){  
                     kmRecorrido = (kmRecorrido *  historial.getPosicion().getEje().getPorcentaje()) / 100;
                     kmNeumatico = n.getKm() + kmRecorrido;   
                    }
                    n.setKm(kmNeumatico);
                    n.setKmUtil(n.getKmEstimado() - kmNeumatico);
                    n.setUbicacion("COLOCADO (Acoplado " + historial.getAcoplado().getDominio() + " " + historial.getAcoplado().getMarca() + " " + historial.getAcoplado().getModelo()+ ")");
                } 

            }
            } if(n.getUbicacion().equalsIgnoreCase("AUXILIO")){
            
           AuxilioNeumatico auxilio = auxilioServicio.buscarAuxilioIdNeumatico(n.getId());
           
           if(auxilio.getCamion() != null){
              
               n.setUbicacion("AUXILIO (Camión " + auxilio.getCamion().getDominio() + " " + auxilio.getCamion().getMarca() + " " + auxilio.getCamion().getModelo()+ ")");
           
           } else{
               
               n.setUbicacion("AUXILIO (Acoplado " + auxilio.getAcoplado().getDominio() + " " + auxilio.getAcoplado().getMarca() + " " + auxilio.getAcoplado().getModelo()+ ")");
           
           } 
            
        }
        }

        return neumaticos;
        
    }
    
    public List<Neumatico> buscarNeumaticosUsado(Long idOrg){
        
            List<Neumatico> neumaticos = neumaticoRepositorio.findByUsado(idOrg);
        
            for (Neumatico n : neumaticos) {
            if(n.getUbicacion().equalsIgnoreCase("COLOCADO")){
            List<HistorialNeumatico> historialList = n.getHistorial();
            if (historialList != null && !historialList.isEmpty()) {
                HistorialNeumatico historial = historialList.get(historialList.size() - 1);
                if (historial.getCamion() != null) {
                    int km = combustibleServicio.kmUltimaCarga(historial.getCamion());
                    Integer kmRecorrido = km - historial.getKmColocacion();
                    Integer kmNeumatico = n.getKm() + kmRecorrido;
                    if(historial.getPosicion().getEje().getElevable() == true){  
                     kmRecorrido = (kmRecorrido *  historial.getPosicion().getEje().getPorcentaje()) / 100;
                     kmNeumatico = n.getKm() + kmRecorrido;   
                    }
                    n.setKm(kmNeumatico);
                    n.setKmUtil(n.getKmEstimado() - kmNeumatico);
                    n.setUbicacion("COLOCADO (Camión " + historial.getCamion().getDominio() + " " + historial.getCamion().getMarca() + " " + historial.getCamion().getModelo()+ ")");
                } else {
                    int km = combustibleServicio.kmAcoplado(historial.getAcoplado(), obtenerFechaFija());
                    Integer kmRecorrido = km - historial.getKmColocacion();
                    Integer kmNeumatico = n.getKm() + kmRecorrido;
                    if(historial.getPosicion().getEje().getElevable() == true){  
                     kmRecorrido = (kmRecorrido *  historial.getPosicion().getEje().getPorcentaje()) / 100;
                     kmNeumatico = n.getKm() + kmRecorrido;   
                    }
                    n.setKm(kmNeumatico);
                    n.setKmUtil(n.getKmEstimado() - kmNeumatico);
                    n.setUbicacion("COLOCADO (Acoplado " + historial.getAcoplado().getDominio() + " " + historial.getAcoplado().getMarca() + " " + historial.getAcoplado().getModelo()+ ")");
                } 

            }
            } if(n.getUbicacion().equalsIgnoreCase("AUXILIO")){
            
           AuxilioNeumatico auxilio = auxilioServicio.buscarAuxilioIdNeumatico(n.getId());
           
           if(auxilio.getCamion() != null){
              
               n.setUbicacion("AUXILIO (Camión " + auxilio.getCamion().getDominio() + " " + auxilio.getCamion().getMarca() + " " + auxilio.getCamion().getModelo()+ ")");
           
           } else{
               
               n.setUbicacion("AUXILIO (Acoplado " + auxilio.getAcoplado().getDominio() + " " + auxilio.getAcoplado().getMarca() + " " + auxilio.getAcoplado().getModelo()+ ")");
           
           } 
            
        }
        }
        
        return neumaticos;
        
    }
    
    public List<Neumatico> buscarNeumaticosRecapado(Long idOrg){
        
            List<Neumatico> neumaticos = neumaticoRepositorio.findByRecapado(idOrg);
        
            for (Neumatico n : neumaticos) {
            if(n.getUbicacion().equalsIgnoreCase("COLOCADO")){
            List<HistorialNeumatico> historialList = n.getHistorial();
            if (historialList != null && !historialList.isEmpty()) {
                HistorialNeumatico historial = historialList.get(historialList.size() - 1);
                if (historial.getCamion() != null) {
                    int km = combustibleServicio.kmUltimaCarga(historial.getCamion());
                    Integer kmRecorrido = km - historial.getKmColocacion();
                    Integer kmNeumatico = n.getKm() + kmRecorrido;
                    if(historial.getPosicion().getEje().getElevable() == true){  
                     kmRecorrido = (kmRecorrido *  historial.getPosicion().getEje().getPorcentaje()) / 100;
                     kmNeumatico = n.getKm() + kmRecorrido;   
                    }
                    n.setKm(kmNeumatico);
                    n.setKmUtil(n.getKmEstimado() - kmNeumatico);
                    n.setUbicacion("COLOCADO (Camión " + historial.getCamion().getDominio() + " " + historial.getCamion().getMarca() + " " + historial.getCamion().getModelo()+ ")");
                } else {
                    int km = combustibleServicio.kmAcoplado(historial.getAcoplado(), obtenerFechaFija());
                    Integer kmRecorrido = km - historial.getKmColocacion();
                    Integer kmNeumatico = n.getKm() + kmRecorrido;
                    if(historial.getPosicion().getEje().getElevable() == true){  
                     kmRecorrido = (kmRecorrido *  historial.getPosicion().getEje().getPorcentaje()) / 100;
                     kmNeumatico = n.getKm() + kmRecorrido;   
                    }
                    n.setKm(kmNeumatico);
                    n.setKmUtil(n.getKmEstimado() - kmNeumatico);
                    n.setUbicacion("COLOCADO (Acoplado " + historial.getAcoplado().getDominio() + " " + historial.getAcoplado().getMarca() + " " + historial.getAcoplado().getModelo()+ ")");
                } 

            }
            } if(n.getUbicacion().equalsIgnoreCase("AUXILIO")){
            
           AuxilioNeumatico auxilio = auxilioServicio.buscarAuxilioIdNeumatico(n.getId());
           
           if(auxilio.getCamion() != null){
              
               n.setUbicacion("AUXILIO (Camión " + auxilio.getCamion().getDominio() + " " + auxilio.getCamion().getMarca() + " " + auxilio.getCamion().getModelo()+ ")");
           
           } else{
               
               n.setUbicacion("AUXILIO (Acoplado " + auxilio.getAcoplado().getDominio() + " " + auxilio.getAcoplado().getMarca() + " " + auxilio.getAcoplado().getModelo()+ ")");
           
           } 
            
        }
        }

        return neumaticos;
        
    }
    
    @Transactional
    public void guardarAsignacionNeumaticos(Neumatico.AplicaA aplicaA, Long idEntidad, String fecha, Integer km, List<PosicionDTO> posicionesDTO,
            List<AuxilioDTO> auxiliosDTO, Usuario usuario) throws ParseException {

        if(aplicaA == Neumatico.AplicaA.CAMION){
        
        Camion camion = camionRepositorio.getById(idEntidad);
        Date fechaColocacion = convertirFecha(fecha);

        for (PosicionDTO dto : posicionesDTO) {

            if (dto.getNeumaticoId() != null) {

                Eje eje = ejeRepositorio.getById(dto.getEjeId());

                Neumatico nuevoNeumatico = neumaticoRepositorio.getById(dto.getNeumaticoId());

                // Buscar si ya existe PosicionNeumatico para ese eje y posicion
                Optional<PosicionNeumatico> posicionExistenteOpt = posicionRepositorio.findByEjeAndPosicionAndEstado(eje, dto.getPosicion(), "VIGENTE");

                if (posicionExistenteOpt.isPresent()) {
                    PosicionNeumatico posicionExistente = posicionExistenteOpt.get();
                    Neumatico neumaticoActual = posicionExistente.getNeumatico();

                    if (neumaticoActual.getId().equals(nuevoNeumatico.getId())) {
                        // Mismo neumático, no hacer nada
                        continue;

                    } else {
                        // Neumático diferente 1. Actualizar historial del neumático que se retira
                        Optional<HistorialNeumatico> historialViejoOpt = historialRepositorio.findByNeumaticoAndEstado(neumaticoActual, "VIGENTE");

                        if (historialViejoOpt.isPresent()) {
                            HistorialNeumatico historialViejo = historialViejoOpt.get();

                            Integer kmRecorrido = km - historialViejo.getKmColocacion();

                            if (kmRecorrido.equals(0)) {

                                // Quitar el historial de la lista
                                neumaticoActual.getHistorial().remove(historialViejo);
                                // Guardar el neumático para que se aplique orphanRemoval
                                neumaticoRepositorio.save(neumaticoActual);

                            } else {

                                if (eje.getElevable() == false) {

                                    historialViejo.setKmRecorrido(kmRecorrido);
                                    Integer kmActual = neumaticoActual.getKm() + kmRecorrido;
                                    neumaticoActual.setKm(kmActual);
                                    neumaticoActual.setKmUtil(neumaticoActual.getKmEstimado() - kmActual);
                                
                                } else {
                                    
                                    kmRecorrido = (kmRecorrido *  eje.getPorcentaje()) / 100;
                                    historialViejo.setKmRecorrido(kmRecorrido);
                                    Integer kmActual = neumaticoActual.getKm() + kmRecorrido;
                                    neumaticoActual.setKm(kmActual);
                                    neumaticoActual.setKmUtil(neumaticoActual.getKmEstimado() - kmActual);

                                }

                                historialViejo.setFechaRetiro(fechaColocacion);
                                historialViejo.setKmRetiro(km);
                                historialViejo.setEstado("FINALIZADO");
                                
                                historialRepositorio.save(historialViejo);

                            }
                                neumaticoActual.setUbicacion("DEPOSITO");
                                neumaticoActual.setUsuario(usuario);

                                neumaticoRepositorio.save(neumaticoActual);
                        }

                        // 2. Eliminar posicion existente
                        posicionExistente.setEstado("FINALIZADO");
                        posicionRepositorio.save(posicionExistente);
                        //3. Actualizar km y estado de nuematico que se retira

                    }
                }

                // Crear nueva PosicionNeumatico
                PosicionNeumatico nuevaPosicion = new PosicionNeumatico();
                nuevaPosicion.setEje(eje);
                nuevaPosicion.setEstado("VIGENTE");
                nuevaPosicion.setNeumatico(nuevoNeumatico);
                nuevaPosicion.setPosicion(dto.getPosicion());
                posicionRepositorio.save(nuevaPosicion);

                // Crear nuevo historial para el nuevo neumático
                HistorialNeumatico nuevoHistorial = new HistorialNeumatico();
                nuevoHistorial.setFechaColocacion(fechaColocacion);
                nuevoHistorial.setKmColocacion(km);
                nuevoHistorial.setCamion(camion);
                nuevoHistorial.setNeumatico(nuevoNeumatico);
                nuevoHistorial.setPosicion(nuevaPosicion);
                nuevoHistorial.setEstado("VIGENTE");
                nuevoHistorial.setUsuario(usuario);

                historialRepositorio.save(nuevoHistorial);

                // Actualizar ubicación del nuevo neumático
                nuevoNeumatico.setUbicacion("COLOCADO");
                if (nuevoNeumatico.getEstado().equalsIgnoreCase("NUEVO")) {
                    nuevoNeumatico.setEstado("USADO");
                }
                nuevoNeumatico.setUsuario(usuario);
                neumaticoRepositorio.save(nuevoNeumatico);

            } else {

                if (dto.getEjeId() != null) {

                    Eje eje = ejeRepositorio.getById(dto.getEjeId());

                    Optional<PosicionNeumatico> posicionExistenteOpt = posicionRepositorio.findByEjeAndPosicionAndEstado(eje, dto.getPosicion(), "VIGENTE");

                    if (posicionExistenteOpt.isPresent()) {

                        PosicionNeumatico posicionExistente = posicionExistenteOpt.get();

                        if (posicionExistente.getPosicion().equals(dto.getPosicion())) {

                            Neumatico neumaticoActual = posicionExistente.getNeumatico();

                            Optional<HistorialNeumatico> historialViejoOpt = historialRepositorio.findByNeumaticoAndEstado(neumaticoActual, "VIGENTE");

                            Integer kmRecorrido = 0;
                            if (historialViejoOpt.isPresent()) {
                                
                                HistorialNeumatico historialViejo = historialViejoOpt.get();

                                kmRecorrido = km - historialViejo.getKmColocacion();

                                if (kmRecorrido.equals(0)) {

                                    // Quitar el historial de la lista
                                    neumaticoActual.getHistorial().remove(historialViejo);
                                    // Guardar el neumático para que se aplique orphanRemoval
                                    neumaticoRepositorio.save(neumaticoActual);

                                } else {

                                if (eje.getElevable() == false) {

                                    historialViejo.setKmRecorrido(kmRecorrido);
                                    Integer kmActual = neumaticoActual.getKm() + kmRecorrido;
                                    neumaticoActual.setKm(kmActual);
                                    neumaticoActual.setKmUtil(neumaticoActual.getKmEstimado() - kmActual);
                                
                                } else {
                                    
                                    kmRecorrido = (kmRecorrido *  eje.getPorcentaje()) / 100;
                                    historialViejo.setKmRecorrido(kmRecorrido);
                                    Integer kmActual = neumaticoActual.getKm() + kmRecorrido;
                                    neumaticoActual.setKm(kmActual);
                                    neumaticoActual.setKmUtil(neumaticoActual.getKmEstimado() - kmActual);

                                }

                                    historialViejo.setKmRetiro(km);
                                    historialViejo.setFechaRetiro(fechaColocacion);
                                    historialViejo.setEstado("FINALIZADO");

                                    historialRepositorio.save(historialViejo);

                                }

                            }
                            neumaticoActual.setUbicacion("DEPOSITO");
                            neumaticoActual.setUsuario(usuario);

                            neumaticoRepositorio.save(neumaticoActual);

                        }

                        // 2. Eliminar posicion existente
                        posicionExistente.setEstado("FINALIZADO");
                        posicionRepositorio.save(posicionExistente);

                    }
                }

            }

        }

        //Auxilio
        if (auxiliosDTO != null) {
            for (AuxilioDTO auxilio : auxiliosDTO) {

                List<AuxilioNeumatico> existentes = auxilioRepositorio.findByCamionAndEstado(camion, "VIGENTE");

                if (auxilio.getNeumaticoId() != null) {

                    if (existentes == null) {
                        existentes = new ArrayList<>();
                    }

                    if (auxiliosDTO == null) {
                        auxiliosDTO = new ArrayList<>();
                    }

                    // Convertir listas a AuxilioDTO para comparación (solo si existen)
                    List<AuxilioDTO> existentesDTO = existentes.stream()
                            .map(a -> new AuxilioDTO(a.getPosicion(), a.getNeumatico().getId()))
                            .sorted(Comparator.comparing(AuxilioDTO::getPosicion))
                            .collect(Collectors.toList());

                    List<AuxilioDTO> nuevosOrdenados = auxiliosDTO.stream()
                            .filter(dto -> dto.getNeumaticoId() != null)
                            .sorted(Comparator.comparing(AuxilioDTO::getPosicion))
                            .collect(Collectors.toList());

                    // Si las listas son iguales, no hacer nada
                    if (existentesDTO.equals(nuevosOrdenados)) {

                        return;
                    }
                    // Si son distintos, modificar
                    for (AuxilioNeumatico auxilioExistente : existentes) {

                        Neumatico neumatico = auxilioExistente.getNeumatico();
                        neumatico.setUbicacion("DEPOSITO");
                        neumaticoRepositorio.save(neumatico);

                        auxilioExistente.setEstado("FINALIZADO");
                        auxilioExistente.setFechaRetiro(fechaColocacion);
                        auxilioRepositorio.save(auxilioExistente);
                    }

                    for (AuxilioDTO dto : nuevosOrdenados) {

                        Neumatico neumatico = neumaticoRepositorio.getById(dto.getNeumaticoId());

                        AuxilioNeumatico auxilioNuevo = new AuxilioNeumatico();
                        auxilioNuevo.setNeumatico(neumatico);
                        auxilioNuevo.setCamion(camion);
                        auxilioNuevo.setFechaColocacion(fechaColocacion);
                        auxilioNuevo.setEstado("VIGENTE");
                        auxilioNuevo.setPosicion(dto.getPosicion());
                        auxilioNuevo.setUsuario(usuario);

                        auxilioRepositorio.save(auxilioNuevo);

                        // (Opcional) Actualizar ubicación del neumático
                        neumatico.setUbicacion("AUXILIO");
                        neumaticoRepositorio.save(neumatico);
                    }

                } else {

                    if (existentes != null) {

                        for (AuxilioNeumatico auxilioExistente : existentes) {

                            if (auxilioExistente.getPosicion() == auxilio.getPosicion()) {

                                auxilioExistente.setFechaRetiro(fechaColocacion);
                                auxilioExistente.setEstado("FINALIZADO");
                                auxilioExistente.setUsuario(usuario);

                                auxilioRepositorio.save(auxilioExistente);

                                Neumatico neumatico = auxilioExistente.getNeumatico();

                                neumatico.setUbicacion("DEPOSITO");
                                neumatico.setUsuario(usuario);

                                neumaticoRepositorio.save(neumatico);

                            }

                        }

                    }

                }

            }
        }
        } else {
            
        Acoplado acoplado = acopladoRepositorio.getById(idEntidad);
        Date fechaColocacion = convertirFecha(fecha);

        for (PosicionDTO dto : posicionesDTO) {

            if (dto.getNeumaticoId() != null) {

                Eje eje = ejeRepositorio.getById(dto.getEjeId());

                Neumatico nuevoNeumatico = neumaticoRepositorio.getById(dto.getNeumaticoId());

                // Buscar si ya existe PosicionNeumatico para ese eje y posicion
                Optional<PosicionNeumatico> posicionExistenteOpt = posicionRepositorio.findByEjeAndPosicionAndEstado(eje, dto.getPosicion(), "VIGENTE");

                if (posicionExistenteOpt.isPresent()) {
                    PosicionNeumatico posicionExistente = posicionExistenteOpt.get();
                    Neumatico neumaticoActual = posicionExistente.getNeumatico();

                    if (neumaticoActual.getId().equals(nuevoNeumatico.getId())) {
                        // Mismo neumático, no hacer nada
                        continue;

                    } else {
                        // Neumático diferente 1. Actualizar historial del neumático que se retira
                        Optional<HistorialNeumatico> historialViejoOpt = historialRepositorio.findByNeumaticoAndEstado(neumaticoActual, "VIGENTE");

                        if (historialViejoOpt.isPresent()) {
                            HistorialNeumatico historialViejo = historialViejoOpt.get();

                            Integer kmRecorrido = km - historialViejo.getKmColocacion();

                            if (kmRecorrido.equals(0)) {

                                // Quitar el historial de la lista
                                neumaticoActual.getHistorial().remove(historialViejo);
                                // Guardar el neumático para que se aplique orphanRemoval
                                neumaticoRepositorio.save(neumaticoActual);

                            } else {

                                if (eje.getElevable() == false) {

                                    historialViejo.setKmRecorrido(kmRecorrido);
                                    Integer kmActual = neumaticoActual.getKm() + kmRecorrido;
                                    neumaticoActual.setKm(kmActual);
                                    neumaticoActual.setKmUtil(neumaticoActual.getKmEstimado() - kmActual);
                                
                                } else {
                                    
                                    kmRecorrido = (kmRecorrido *  eje.getPorcentaje()) / 100;
                                    historialViejo.setKmRecorrido(kmRecorrido);
                                    Integer kmActual = neumaticoActual.getKm() + kmRecorrido;
                                    neumaticoActual.setKm(kmActual);
                                    neumaticoActual.setKmUtil(neumaticoActual.getKmEstimado() - kmActual);

                                }

                                historialViejo.setFechaRetiro(fechaColocacion);
                                historialViejo.setKmRetiro(km);
                                historialViejo.setEstado("FINALIZADO");
                                
                                historialRepositorio.save(historialViejo);

                            }
                                neumaticoActual.setUbicacion("DEPOSITO");
                                neumaticoActual.setUsuario(usuario);

                                neumaticoRepositorio.save(neumaticoActual);
                        }

                        // 2. Eliminar posicion existente
                        posicionExistente.setEstado("FINALIZADO");
                        posicionRepositorio.save(posicionExistente);
                        //3. Actualizar km y estado de nuematico que se retira

                    }
                }

                // Crear nueva PosicionNeumatico
                PosicionNeumatico nuevaPosicion = new PosicionNeumatico();
                nuevaPosicion.setEje(eje);
                nuevaPosicion.setEstado("VIGENTE");
                nuevaPosicion.setNeumatico(nuevoNeumatico);
                nuevaPosicion.setPosicion(dto.getPosicion());
                posicionRepositorio.save(nuevaPosicion);

                // Crear nuevo historial para el nuevo neumático
                HistorialNeumatico nuevoHistorial = new HistorialNeumatico();
                nuevoHistorial.setFechaColocacion(fechaColocacion);
                nuevoHistorial.setKmColocacion(km);
                nuevoHistorial.setAcoplado(acoplado);
                nuevoHistorial.setNeumatico(nuevoNeumatico);
                nuevoHistorial.setPosicion(nuevaPosicion);
                nuevoHistorial.setEstado("VIGENTE");
                nuevoHistorial.setUsuario(usuario);

                historialRepositorio.save(nuevoHistorial);

                // Actualizar ubicación del nuevo neumático
                nuevoNeumatico.setUbicacion("COLOCADO");
                if (nuevoNeumatico.getEstado().equalsIgnoreCase("NUEVO")) {
                    nuevoNeumatico.setEstado("USADO");
                }
                nuevoNeumatico.setUsuario(usuario);
                neumaticoRepositorio.save(nuevoNeumatico);

            } else {

                if (dto.getEjeId() != null) {

                    Eje eje = ejeRepositorio.getById(dto.getEjeId());

                    Optional<PosicionNeumatico> posicionExistenteOpt = posicionRepositorio.findByEjeAndPosicionAndEstado(eje, dto.getPosicion(), "VIGENTE");

                    if (posicionExistenteOpt.isPresent()) {

                        PosicionNeumatico posicionExistente = posicionExistenteOpt.get();

                        if (posicionExistente.getPosicion().equals(dto.getPosicion())) {

                            Neumatico neumaticoActual = posicionExistente.getNeumatico();

                            Optional<HistorialNeumatico> historialViejoOpt = historialRepositorio.findByNeumaticoAndEstado(neumaticoActual, "VIGENTE");

                            Integer kmRecorrido = 0;
                            if (historialViejoOpt.isPresent()) {
                                HistorialNeumatico historialViejo = historialViejoOpt.get();

                                kmRecorrido = km - historialViejo.getKmColocacion();

                                if (kmRecorrido.equals(0)) {

                                    // Quitar el historial de la lista
                                    neumaticoActual.getHistorial().remove(historialViejo);
                                    // Guardar el neumático para que se aplique orphanRemoval
                                    neumaticoRepositorio.save(neumaticoActual);

                                } else {

                                if (eje.getElevable() == false) {

                                    historialViejo.setKmRecorrido(kmRecorrido);
                                    Integer kmActual = neumaticoActual.getKm() + kmRecorrido;
                                    neumaticoActual.setKm(kmActual);
                                    neumaticoActual.setKmUtil(neumaticoActual.getKmEstimado() - kmActual);
                                
                                } else {
                                    
                                    kmRecorrido = (kmRecorrido *  eje.getPorcentaje()) / 100;
                                    historialViejo.setKmRecorrido(kmRecorrido);
                                    Integer kmActual = neumaticoActual.getKm() + kmRecorrido;
                                    neumaticoActual.setKm(kmActual);
                                    neumaticoActual.setKmUtil(neumaticoActual.getKmEstimado() - kmActual);

                                }

                                    historialViejo.setKmRetiro(km);
                                    historialViejo.setFechaRetiro(fechaColocacion);
                                    historialViejo.setEstado("FINALIZADO");

                                    historialRepositorio.save(historialViejo);

                                }

                            }
                            neumaticoActual.setUbicacion("DEPOSITO");
                            neumaticoActual.setUsuario(usuario);

                            neumaticoRepositorio.save(neumaticoActual);

                        }

                        // 2. Eliminar posicion existente
                        posicionExistente.setEstado("FINALIZADO");
                        posicionRepositorio.save(posicionExistente);

                    }
                }

            }

        }

        //Auxilio
        if (auxiliosDTO != null) {
            for (AuxilioDTO auxilio : auxiliosDTO) {

                List<AuxilioNeumatico> existentes = auxilioRepositorio.findByAcopladoAndEstado(acoplado, "VIGENTE");

                if (auxilio.getNeumaticoId() != null) {

                    if (existentes == null) {
                        existentes = new ArrayList<>();
                    }

                    if (auxiliosDTO == null) {
                        auxiliosDTO = new ArrayList<>();
                    }

                    // Convertir listas a AuxilioDTO para comparación (solo si existen)
                    List<AuxilioDTO> existentesDTO = existentes.stream()
                            .map(a -> new AuxilioDTO(a.getPosicion(), a.getNeumatico().getId()))
                            .sorted(Comparator.comparing(AuxilioDTO::getPosicion))
                            .collect(Collectors.toList());

                    List<AuxilioDTO> nuevosOrdenados = auxiliosDTO.stream()
                            .filter(dto -> dto.getNeumaticoId() != null)
                            .sorted(Comparator.comparing(AuxilioDTO::getPosicion))
                            .collect(Collectors.toList());

                    // Si las listas son iguales, no hacer nada
                    if (existentesDTO.equals(nuevosOrdenados)) {

                        return;
                    }
                    // Si son distintos, modificar
                    for (AuxilioNeumatico auxilioExistente : existentes) {

                        Neumatico neumatico = auxilioExistente.getNeumatico();
                        neumatico.setUbicacion("DEPOSITO");
                        neumaticoRepositorio.save(neumatico);

                        auxilioExistente.setEstado("FINALIZADO");
                        auxilioExistente.setFechaRetiro(fechaColocacion);
                        auxilioRepositorio.save(auxilioExistente);
                    }

                    for (AuxilioDTO dto : nuevosOrdenados) {

                        Neumatico neumatico = neumaticoRepositorio.getById(dto.getNeumaticoId());

                        AuxilioNeumatico auxilioNuevo = new AuxilioNeumatico();
                        auxilioNuevo.setNeumatico(neumatico);
                        auxilioNuevo.setAcoplado(acoplado);
                        auxilioNuevo.setFechaColocacion(fechaColocacion);
                        auxilioNuevo.setEstado("VIGENTE");
                        auxilioNuevo.setPosicion(dto.getPosicion());
                        auxilioNuevo.setUsuario(usuario);

                        auxilioRepositorio.save(auxilioNuevo);

                        // (Opcional) Actualizar ubicación del neumático
                        neumatico.setUbicacion("AUXILIO");
                        neumaticoRepositorio.save(neumatico);
                    }

                } else {

                    if (existentes != null) {

                        for (AuxilioNeumatico auxilioExistente : existentes) {

                            if (auxilioExistente.getPosicion() == auxilio.getPosicion()) {

                                auxilioExistente.setFechaRetiro(fechaColocacion);
                                auxilioExistente.setEstado("FINALIZADO");
                                auxilioExistente.setUsuario(usuario);

                                auxilioRepositorio.save(auxilioExistente);

                                Neumatico neumatico = auxilioExistente.getNeumatico();

                                neumatico.setUbicacion("DEPOSITO");
                                neumatico.setUsuario(usuario);

                                neumaticoRepositorio.save(neumatico);

                            }

                        }

                    }

                }

            }
        }
        }
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
