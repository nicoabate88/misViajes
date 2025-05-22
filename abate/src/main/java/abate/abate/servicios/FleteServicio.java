package abate.abate.servicios;

import abate.abate.entidades.Acoplado;
import abate.abate.entidades.Camion;
import abate.abate.entidades.Cliente;
import abate.abate.entidades.Flete;
import abate.abate.entidades.Producto;
import abate.abate.entidades.Usuario;
import abate.abate.repositorios.AcopladoRepositorio;
import abate.abate.repositorios.CamionRepositorio;
import abate.abate.repositorios.ClienteRepositorio;
import abate.abate.repositorios.FleteRepositorio;
import abate.abate.repositorios.ProductoRepositorio;
import abate.abate.repositorios.UsuarioRepositorio;
import abate.abate.util.FleteComparador;
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
public class FleteServicio {

    @Autowired
    private FleteRepositorio fleteRepositorio;
    @Autowired
    private ClienteRepositorio clienteRepositorio;
    @Autowired
    private UsuarioRepositorio usuarioRepositorio;
    @Autowired
    private TransaccionServicio transaccionServicio;
    @Autowired
    private GastoServicio gastoServicio;
    @Autowired
    private CamionRepositorio camionRepositorio;
    @Autowired
    private AcopladoRepositorio acopladoRepositorio;
    @Autowired
    private ProductoRepositorio productoRepositorio;
    @Autowired
    private ImagenServicio imagenServicio;

    @Transactional
    public void crearFleteChofer(Long idOrg, String fechaCarga, Long idCliente, Long idCamion, Long idAcoplado, String origen, String fechaViaje, String destino, Double km,
            Long idProducto, Double tarifa, String cPorte, String ctg, Double kg, String observacion, Long idChofer) throws ParseException {

        Flete flete = new Flete();

        Cliente cliente = new Cliente();
        Optional<Cliente> cte = clienteRepositorio.findById(idCliente);
        if (cte.isPresent()) {
            cliente = cte.get();
        }

        Usuario chofer = new Usuario();
        Optional<Usuario> chof = usuarioRepositorio.findById(idChofer);
        if (chof.isPresent()) {
            chofer = chof.get();
        }

        Camion camion = new Camion();
        Optional<Camion> cam = camionRepositorio.findById(idCamion);
        if (cam.isPresent()) {
            camion = cam.get();
        }
        
        if(idAcoplado != null){
        Acoplado acoplado = new Acoplado();
        Optional<Acoplado> acop = acopladoRepositorio.findById(idAcoplado);
        if (acop.isPresent()) {
            acoplado = acop.get();
        }
        flete.setAcoplado(acoplado);
        } else {
            flete.setAcoplado(null);
        }
        
        Producto producto = productoRepositorio.getById(idProducto);

        Long ifFlete = buscarUltimoIdOrg(idOrg);
        String origenM = origen.toUpperCase();
        String destinoM = destino.toUpperCase();
        if(!observacion.isEmpty()){
            String obsM = observacion.toUpperCase();
            flete.setObservacion(obsM);
        }
        Date carga = convertirFecha(fechaCarga);
        Date viaje = convertirFecha(fechaViaje);

        Double neto = (kg / 1000) * tarifa;
        Double netoR = Math.round(neto * 100.0) / 100.0;
        Double iva = neto * 0.21;
        Double ivaR = Math.round(iva * 100.0) / 100.0;
        Double total = neto + iva;
        Double totalR = Math.round(total * 100.0) / 100.0;
        Double por = chofer.getPorcentaje() / 100;
        Double porcentaje = (double) Math.round(neto * por);

        flete.setIdOrg(idOrg);
        flete.setFechaCarga(carga);
        flete.setCliente(cliente);
        flete.setOrigenFlete(origenM);
        flete.setFechaFlete(viaje);
        flete.setDestinoFlete(destinoM);
        flete.setKmFlete(km);
        flete.setProducto(producto);
        flete.setTarifa(tarifa);
        flete.setCartaPorte(cPorte);
        flete.setCtg(ctg);
        flete.setKgFlete(kg);
        flete.setNeto(netoR);
        flete.setIva(ivaR);
        flete.setTotal(totalR);
        flete.setPorcientoChofer(chofer.getPorcentaje());
        flete.setPorcentajeChofer(porcentaje);
        flete.setChofer(chofer);
        flete.setUsuario(chofer);
        flete.setEstado("PENDIENTE");
        flete.setComisionTpte(0.0);
        flete.setComisionTpteValor(0.0);
        flete.setComisionTpteChofer("NO");
        flete.setCamion(camion);
        flete.setIdFlete(ifFlete + 1);

        fleteRepositorio.save(flete);

    }

    @Transactional
    public void crearFleteAdmin(Long idOrg, Long idChofer, Long idCamion, Long idAcoplado, String fechaCarga, Long idCliente, String origen, String fechaViaje, String destino, Double km,
            Long idProducto, Double tarifa, String cPorte, String ctg, Double kg, Double comisionTpte, String comisionTpteChofer, String ivaN, String observacion, Long idUsuario) throws ParseException {

        Flete flete = new Flete();

        Cliente cliente = new Cliente();
        Optional<Cliente> cte = clienteRepositorio.findById(idCliente);
        if (cte.isPresent()) {
            cliente = cte.get();
        }

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

        Camion camion = new Camion();
        Optional<Camion> cam = camionRepositorio.findById(idCamion);
        if (cam.isPresent()) {
            camion = cam.get();
        }
        
        if(idAcoplado != null){
        Acoplado acoplado = new Acoplado();
        Optional<Acoplado> acop = acopladoRepositorio.findById(idAcoplado);
        if (acop.isPresent()) {
            acoplado = acop.get();
        }
        flete.setAcoplado(acoplado);
        } else {
            flete.setAcoplado(null);
        }
        
        Producto producto = productoRepositorio.getById(idProducto);

        Long ifFlete = buscarUltimoIdOrg(idOrg);
        String origenM = origen.toUpperCase();
        String destinoM = destino.toUpperCase();
        if(!observacion.isEmpty()){
            String obsM = observacion.toUpperCase();
            flete.setObservacion(obsM);
        } 
        Date carga = convertirFecha(fechaCarga);
        Date viaje = convertirFecha(fechaViaje);
        Double neto = (kg / 1000) * tarifa;
        Double netoR = Math.round(neto * 100.0) / 100.0;
        Double por = chofer.getPorcentaje() / 100;
        Double porcentaje = (double) Math.round(neto * por);

        if (comisionTpte == 0.0) {
            flete.setNeto(netoR);
            flete.setComisionTpte(0.0);
            flete.setComisionTpteValor(0.0);
            flete.setComisionTpteChofer("NO");
            flete.setPorcentajeChofer(porcentaje);
            if (ivaN.equalsIgnoreCase("SI")) {
                Double iva = neto * 0.21;
                Double ivaR = Math.round(iva * 100.0) / 100.0;
                Double total = neto + iva;
                Double totalR = Math.round(total * 100.0) / 100.0;
                flete.setIva(ivaR);
                flete.setTotal(totalR);
            } else {
                flete.setIva(0.0);
                flete.setTotal(netoR);
            }
        } else {
            Double comision = ((comisionTpte / 100) * netoR);
            Double comisionR = Math.round(comision * 100.0) / 100.0;
            Double netoFlete = (netoR - comisionR);
            flete.setNeto(netoFlete);
            flete.setComisionTpte(comisionTpte);
            flete.setComisionTpteValor(comisionR);
            if (ivaN.equalsIgnoreCase("SI")) {
                Double iva = netoFlete * 0.21;
                Double ivaR = Math.round(iva * 100.0) / 100.0;
                Double total = netoFlete + iva;
                Double totalR = Math.round(total * 100.0) / 100.0;
                flete.setIva(ivaR);
                flete.setTotal(totalR);
            } else {
                flete.setIva(0.0);
                flete.setTotal(netoFlete);
            }
            if (comisionTpteChofer.equalsIgnoreCase("NO")) {
                flete.setPorcentajeChofer(porcentaje);
                flete.setComisionTpteChofer("NO");
            } else {
                Double comisionChofer = ((comisionTpte / 100) * porcentaje);
                Double comisionChoferR = (double) Math.round(porcentaje - comisionChofer);

                flete.setPorcentajeChofer(comisionChoferR);
                flete.setComisionTpteChofer("SI");
            }
        }

        flete.setIdOrg(idOrg);
        flete.setFechaCarga(carga);
        flete.setCliente(cliente);
        flete.setOrigenFlete(origenM);
        flete.setFechaFlete(viaje);
        flete.setDestinoFlete(destinoM);
        flete.setKmFlete(km);
        flete.setProducto(producto);
        flete.setTarifa(tarifa);
        flete.setCartaPorte(cPorte);
        flete.setCtg(ctg);
        flete.setKgFlete(kg);
        flete.setPorcientoChofer(chofer.getPorcentaje());
        flete.setChofer(chofer);
        flete.setUsuario(usuario);
        flete.setEstado("ACEPTADO");
        flete.setCamion(camion);
        flete.setIdFlete(ifFlete + 1);

        fleteRepositorio.save(flete);

        transaccionServicio.crearTransaccionFleteChofer(buscarUltimo(idOrg));
        transaccionServicio.crearTransaccionFleteCliente(buscarUltimo(idOrg));

    }

    @Transactional
    public void aceptarFlete(Long idFlete, Usuario logueado) {

        Flete flete = new Flete();
        Optional<Flete> fte = fleteRepositorio.findById(idFlete);
        if (fte.isPresent()) {
            flete = fte.get();
        }

        flete.setEstado("ACEPTADO");
        flete.setUsuario(logueado);

        fleteRepositorio.save(flete);

        transaccionServicio.crearTransaccionFleteChofer(idFlete);
        transaccionServicio.crearTransaccionFleteCliente(idFlete);
        if (flete.getGasto() != null && flete.getGasto().getEstado().equalsIgnoreCase("PENDIENTE")) {
            gastoServicio.aceptarGastoCaja(flete.getGasto().getId(), logueado);
            transaccionServicio.crearTransaccionGasto(flete.getGasto().getId());
        }

    }

    @Transactional
    public void pendienteFlete(Long idFlete, Usuario usuario) {

        Flete flete = new Flete();
        Optional<Flete> fte = fleteRepositorio.findById(idFlete);
        if (fte.isPresent()) {
            flete = fte.get();
        }

        transaccionServicio.eliminarTransaccionFlete(idFlete);

        if (flete.getGasto() != null) {

            transaccionServicio.eliminarTransaccionGasto(flete.getGasto().getId());

            gastoServicio.volverPendienteGasto(flete.getGasto().getId(), usuario);

        }

        flete.setEstado("PENDIENTE");
        flete.setUsuario(usuario);

        fleteRepositorio.save(flete);

    }

    public Long buscarUltimoIdOrg(Long idOrg) {

        Flete flete = new Flete();
        Optional<Flete> fte = fleteRepositorio.findTopByIdOrgOrderByIdDesc(idOrg);
        if (fte.isPresent()) {
            flete = fte.get();

            return flete.getIdFlete();

        } else {

            int ultimo = 0;
            Long primero = Long.valueOf(ultimo);

            return primero;

        }

    }

    public Long buscarUltimo(Long idOrg) {

        return fleteRepositorio.ultimoFlete(idOrg);
    }

    public Long buscarIdFleteIdGasto(Long idGasto) {

        return fleteRepositorio.findFleteIdByIdGasto(idGasto);
    }

    public ArrayList<Flete> buscarFletesPendiente(Long idOrg) {

        ArrayList<Flete> lista = (ArrayList<Flete>) fleteRepositorio.buscarFletePendiente(idOrg);

       // Collections.sort(lista, FleteComparador.ordenarFechaAsc);

        return lista;
    }

    public ArrayList<Flete> buscarFletesRangoFecha(Long idOrg, String desde, String hasta) throws ParseException {

        Date d = convertirFecha(desde);
        Date h = convertirFecha(hasta);

        ArrayList<Flete> lista = fleteRepositorio.findByFechaFleteBetweenAndIdOrg(d, h, idOrg);
        Collections.sort(lista, FleteComparador.ordenarIdDesc);

        return lista;
    }

    public ArrayList<Flete> buscarFletesRangoFechaAsc(Long idOrg, String desde, String hasta) throws ParseException {

        Date d = convertirFecha(desde);
        Date h = convertirFecha(hasta);

        ArrayList<Flete> lista = fleteRepositorio.findByFechaFleteBetweenAndIdOrg(d, h, idOrg);
        Collections.sort(lista, FleteComparador.ordenarFechaAsc);

        return lista;
    }

    public ArrayList<Flete> buscarFletesIdChoferFecha(Long idChofer, String desde, String hasta) throws ParseException {

        Date d = convertirFecha(desde);
        Date h = convertirFecha(hasta);

        ArrayList<Flete> lista = fleteRepositorio.buscarFleteChofer(d, h, idChofer);

        Collections.sort(lista, FleteComparador.ordenarFechaDesc);

        return lista;
    }

    public ArrayList<Flete> buscarFletesIdChoferFechaAsc(Long idChofer, String desde, String hasta) throws ParseException {

        Date d = convertirFecha(desde);
        Date h = convertirFecha(hasta);

        ArrayList<Flete> lista = fleteRepositorio.buscarFleteChofer(d, h, idChofer);

        Collections.sort(lista, FleteComparador.ordenarFechaAsc);

        return lista;
    }

    public ArrayList<Flete> buscarFletesIdClienteFecha(Long idCliente, String desde, String hasta) throws ParseException {

        Date d = convertirFecha(desde);
        Date h = convertirFecha(hasta);

        ArrayList<Flete> lista = fleteRepositorio.buscarFleteCliente(d, h, idCliente);
        Collections.sort(lista, FleteComparador.ordenarIdDesc);

        return lista;
    }

    public ArrayList<Flete> buscarFletesIdClienteFechaAsc(Long idCliente, String desde, String hasta) throws ParseException {

        Date d = convertirFecha(desde);
        Date h = convertirFecha(hasta);

        ArrayList<Flete> lista = fleteRepositorio.buscarFleteCliente(d, h, idCliente);
        Collections.sort(lista, FleteComparador.ordenarFechaAsc);

        return lista;
    }
    
    public ArrayList<Flete> buscarFletesIdCamionFecha(Long idCamion, String desde, String hasta) throws ParseException {

        Date d = convertirFecha(desde);
        Date h = convertirFecha(hasta);

        ArrayList<Flete> lista = fleteRepositorio.buscarFleteCamion(d, h, idCamion);
        Collections.sort(lista, FleteComparador.ordenarIdDesc);

        return lista;
    }
    
    public ArrayList<Flete> buscarFletesIdCamionFechaAsc(Long idCamion, String desde, String hasta) throws ParseException {

        Date d = convertirFecha(desde);
        Date h = convertirFecha(hasta);

        ArrayList<Flete> lista = fleteRepositorio.buscarFleteCamion(d, h, idCamion);
        Collections.sort(lista, FleteComparador.ordenarFechaAsc);

        return lista;
    }

    public ArrayList<Flete> buscarFletesIdChoferClienteFecha(Long idChofer, Long idCliente, String desde, String hasta) throws ParseException {

        Date d = convertirFecha(desde);
        Date h = convertirFecha(hasta);

        ArrayList<Flete> lista = fleteRepositorio.buscarFleteChoferCliente(d, h, idChofer, idCliente);
        Collections.sort(lista, FleteComparador.ordenarIdDesc);

        return lista;
    }

    public ArrayList<Flete> buscarFletesIdChoferClienteFechaAsc(Long idChofer, Long idCliente, String desde, String hasta) throws ParseException {

        Date d = convertirFecha(desde);
        Date h = convertirFecha(hasta);

        ArrayList<Flete> lista = fleteRepositorio.buscarFleteChoferCliente(d, h, idChofer, idCliente);

        Collections.sort(lista, FleteComparador.ordenarFechaAsc);

        return lista;
    }
    
    public ArrayList<Flete> buscarFletesIdChoferCamionFecha(Long idChofer, Long idCamion, String desde, String hasta) throws ParseException {

        Date d = convertirFecha(desde);
        Date h = convertirFecha(hasta);

        ArrayList<Flete> lista = fleteRepositorio.buscarFleteChoferCamion(d, h, idChofer, idCamion);
        Collections.sort(lista, FleteComparador.ordenarIdDesc);

        return lista;
    }
    
    public ArrayList<Flete> buscarFletesIdChoferCamionFechaAsc(Long idChofer, Long idCamion, String desde, String hasta) throws ParseException {

        Date d = convertirFecha(desde);
        Date h = convertirFecha(hasta);

        ArrayList<Flete> lista = fleteRepositorio.buscarFleteChoferCamion(d, h, idChofer, idCamion);

        Collections.sort(lista, FleteComparador.ordenarFechaAsc);

        return lista;
    }
    
    public ArrayList<Flete> buscarFletesIdClienteCamionFecha(Long idCliente, Long idCamion, String desde, String hasta) throws ParseException {

        Date d = convertirFecha(desde);
        Date h = convertirFecha(hasta);

        ArrayList<Flete> lista = fleteRepositorio.buscarFleteClienteCamion(d, h, idCliente, idCamion);
        Collections.sort(lista, FleteComparador.ordenarIdDesc);

        return lista;
    }
    
    public ArrayList<Flete> buscarFletesIdClienteCamionFechaAsc(Long idCliente, Long idCamion, String desde, String hasta) throws ParseException {

        Date d = convertirFecha(desde);
        Date h = convertirFecha(hasta);

        ArrayList<Flete> lista = fleteRepositorio.buscarFleteClienteCamion(d, h, idCliente, idCamion);

        Collections.sort(lista, FleteComparador.ordenarFechaAsc);

        return lista;
    }
    
    public ArrayList<Flete> buscarFletesIdChoferClienteCamionFecha(Long idChofer, Long idCliente, Long idCamion, String desde, String hasta) throws ParseException {

        Date d = convertirFecha(desde);
        Date h = convertirFecha(hasta);

        ArrayList<Flete> lista = fleteRepositorio.buscarFleteChoferClienteCamion(d, h, idChofer, idCliente, idCamion);
        Collections.sort(lista, FleteComparador.ordenarIdDesc);

        return lista;
    }
        
    public ArrayList<Flete> buscarFletesIdChoferClienteCamionFechaAsc(Long idChofer, Long idCliente, Long idCamion, String desde, String hasta) throws ParseException {

        Date d = convertirFecha(desde);
        Date h = convertirFecha(hasta);

        ArrayList<Flete> lista = fleteRepositorio.buscarFleteChoferClienteCamion(d, h, idChofer, idCliente, idCamion);

        Collections.sort(lista, FleteComparador.ordenarFechaAsc);

        return lista;
    }

    public Flete buscarFlete(Long id) {

        Flete flete = fleteRepositorio.getById(id);

        return flete;
    }

    public Flete buscarFleteIdImagenCP(Long id) {

        return fleteRepositorio.buscarFleteIdImagenCP(id);
    }

    public Flete buscarFleteIdImagenDescarga(Long id) {

        return fleteRepositorio.buscarFleteIdImagenDescarga(id);
    }

    public Flete buscarFleteIdGasto(Long id) {

        return fleteRepositorio.buscarFleteIdGasto(id);
    }

    @Transactional
    public void modificarFleteChofer(Long idFlete, Long idCamion, Long idAcoplado, String fechaCarga, Long idCliente, String origen, String fechaViaje, String destino, Double km,
            Long idProducto, Double tarifa, String cPorte, String ctg, Double kg, String observacion) throws ParseException {

        Flete flete = new Flete();
        Optional<Flete> fte = fleteRepositorio.findById(idFlete);
        if (fte.isPresent()) {
            flete = fte.get();
        }

        if(flete.getCliente().getId() != idCliente){
        Cliente cliente = new Cliente();
        Optional<Cliente> cte = clienteRepositorio.findById(idCliente);
        if (cte.isPresent()) {
            cliente = cte.get();
        }
        flete.setCliente(cliente);
        }

        if(flete.getCamion().getId() != idCamion){
        Camion camion = new Camion();
        Optional<Camion> cam = camionRepositorio.findById(idCamion);
        if (cam.isPresent()) {
            camion = cam.get();
        }
        flete.setCamion(camion);
        }
        
        if(idAcoplado != null) {
        if(flete.getAcoplado() != null){    
        if(flete.getAcoplado().getId() != idAcoplado){
        Acoplado acoplado = new Acoplado();
        Optional<Acoplado> acop = acopladoRepositorio.findById(idAcoplado);
        if (acop.isPresent()) {
            acoplado = acop.get();
        }
        flete.setAcoplado(acoplado);
            }
        } else {
        Acoplado acoplado = new Acoplado();
        Optional<Acoplado> acop = acopladoRepositorio.findById(idAcoplado);
        if (acop.isPresent()) {
            acoplado = acop.get();
        }
        flete.setAcoplado(acoplado);
            
        }
        } else {
            flete.setAcoplado(null);
        }
        
        if(flete.getProducto().getId() != idProducto){
        Producto producto = productoRepositorio.getById(idProducto);
        flete.setProducto(producto);
        }
        
        String origenM = origen.toUpperCase();
        String destinoM = destino.toUpperCase();
        if(!observacion.isEmpty()){
            String obsM = observacion.toUpperCase();
            flete.setObservacion(obsM);
        } else {
            flete.setObservacion(null);
        }
        Date carga = convertirFecha(fechaCarga);
        Date viaje = convertirFecha(fechaViaje);
        Double neto = (kg / 1000) * tarifa;
        Double iva = neto * 0.21;

        Double netoR = Math.round(neto * 100.0) / 100.0;
        Double por = flete.getPorcientoChofer() / 100;
        Double porcentaje = (double) Math.round(netoR * por);

        Double ivaR = Math.round(iva * 100.0) / 100.0;

        Double total = neto + iva;
        Double totalR = Math.round(total * 100.0) / 100.0;

        flete.setFechaCarga(carga);
        flete.setOrigenFlete(origenM);
        flete.setFechaFlete(viaje);
        flete.setDestinoFlete(destinoM);
        flete.setKmFlete(km);
        flete.setTarifa(tarifa);
        flete.setCartaPorte(cPorte);
        flete.setCtg(ctg);
        flete.setKgFlete(kg);
        flete.setNeto(netoR);
        flete.setIva(ivaR);
        flete.setTotal(totalR);
        flete.setPorcentajeChofer(porcentaje);

        fleteRepositorio.save(flete);

    }

    @Transactional
    public void modificarFleteAdmin(Long idFlete, Long idChofer, Long idCamion, Long idAcoplado, String fechaCarga, Long idCliente, String origen, String fechaViaje, String destino, Double km,
            Long idProducto, Double tarifa, String cPorte, String ctg, Double kg, Double ivaM, Double porciento, Double porcentajeChofer,
            Double comisionTpte, String comisionTpteChofer, String observacion, Long idUsuario) throws ParseException {

        Flete flete = new Flete();
        Optional<Flete> fte = fleteRepositorio.findById(idFlete);
        if (fte.isPresent()) {
            flete = fte.get();
        }

        Usuario chofer = new Usuario();
        Optional<Usuario> chof = usuarioRepositorio.findById(idChofer);
        if (chof.isPresent()) {
            chofer = chof.get();
        flete.setChofer(chofer);    
        }
        
        Usuario usuario = new Usuario();
        Optional<Usuario> user = usuarioRepositorio.findById(idUsuario);
        if (user.isPresent()) {
            usuario = user.get();
        }
        
        if(flete.getCliente().getId() != idCliente){
        Cliente cliente = new Cliente();
        Optional<Cliente> cte = clienteRepositorio.findById(idCliente);
        if (cte.isPresent()) {
            cliente = cte.get();
        }
        flete.setCliente(cliente);
        }

        if(flete.getCamion().getId() != idCamion){
        Camion camion = new Camion();
        Optional<Camion> cam = camionRepositorio.findById(idCamion);
        if (cam.isPresent()) {
            camion = cam.get();
        }
        flete.setCamion(camion);
        }
        
        if(idAcoplado != null) {
        if(flete.getAcoplado() != null){    
        if(flete.getAcoplado().getId() != idAcoplado){
        Acoplado acoplado = new Acoplado();
        Optional<Acoplado> acop = acopladoRepositorio.findById(idAcoplado);
        if (acop.isPresent()) {
            acoplado = acop.get();
        }
        flete.setAcoplado(acoplado);
            }
        } else {
        Acoplado acoplado = new Acoplado();
        Optional<Acoplado> acop = acopladoRepositorio.findById(idAcoplado);
        if (acop.isPresent()) {
            acoplado = acop.get();
        }
        flete.setAcoplado(acoplado);
            
        }
        } else {
            flete.setAcoplado(null);
        }
        
        if(flete.getProducto().getId() != idProducto){
        Producto producto = productoRepositorio.getById(idProducto);
        flete.setProducto(producto);
        }

        String origenM = origen.toUpperCase();
        String destinoM = destino.toUpperCase();
        if(!observacion.isEmpty()){
            String obsM = observacion.toUpperCase();
            flete.setObservacion(obsM);
        } else {
            flete.setObservacion(null);
        }
        Date carga = convertirFecha(fechaCarga);
        Date viaje = convertirFecha(fechaViaje);
        Double neto = (kg / 1000) * tarifa;
        Double iva;

        if (ivaM == 0.0) {
            iva = ivaM;
        } else {
            iva = neto * 0.21;
        }

        if (comisionTpte != 0.0) {
            Double tpte = ((comisionTpte / 100) * neto);
            Double tpteR = Math.round(tpte * 100.0) / 100.0;
            neto = neto - tpteR;
            flete.setComisionTpte(comisionTpte);
            flete.setComisionTpteValor(tpteR);
            if (ivaM != 0.0) {
                iva = neto * 0.21;
            }
        } else {
            flete.setComisionTpte(0.0);
            flete.setComisionTpteValor(0.0);
        }

        Double netoR = Math.round(neto * 100.0) / 100.0;
        Double ivaR = Math.round(iva * 100.0) / 100.0;

        Double total = neto + iva;
        Double totalR = Math.round(total * 100.0) / 100.0;

        if (porciento != 0) {

            porcentajeChofer = (double) Math.round(neto * (porciento / 100));

            if (comisionTpteChofer.equalsIgnoreCase("SI")) {

                porcentajeChofer = (double) Math.round(netoR * (porciento / 100));
                flete.setComisionTpteChofer("SI");

            } else {
                porcentajeChofer = (double) Math.round(((kg * tarifa) / 1000) * (porciento / 100));
                flete.setComisionTpteChofer("NO");
            }
        }

        flete.setFechaCarga(carga);
        flete.setOrigenFlete(origenM);
        flete.setFechaFlete(viaje);
        flete.setDestinoFlete(destinoM);
        flete.setKmFlete(km);
        flete.setTarifa(tarifa);
        flete.setCartaPorte(cPorte);
        flete.setCtg(ctg);
        flete.setKgFlete(kg);
        flete.setNeto(netoR);
        flete.setIva(ivaR);
        flete.setTotal(totalR);
        flete.setPorcientoChofer(porciento);
        flete.setPorcentajeChofer(porcentajeChofer);
        flete.setUsuario(usuario);

        fleteRepositorio.save(flete);

        if (flete.getGasto() != null) {
            gastoServicio.modificarChoferGasto(flete.getGasto(), chofer);
        }
        
        
        
        if (flete.getEstado().equalsIgnoreCase("ACEPTADO")) {
                    
            transaccionServicio.modificarTransaccionFlete(flete);
        }

    }

    @Transactional
    public void eliminarFlete(Long idFlete) {

        Flete flete = new Flete();
        Optional<Flete> fte = fleteRepositorio.findById(idFlete);
        if (fte.isPresent()) {
            flete = fte.get();
        }

        if (flete.getEstado().equalsIgnoreCase("ACEPTADO")) {
            transaccionServicio.eliminarTransaccionFlete(idFlete);
        }

        if (flete.getImagenCP() != null) {
            imagenServicio.eliminarImagenCP(flete.getImagenCP().getId());
        }

        if (flete.getImagenDescarga() != null) {
            imagenServicio.eliminarImagenDescarga(flete.getImagenDescarga().getId());
        }

        if (flete.getGasto() != null) {
            gastoServicio.eliminarGastoFlete(flete.getGasto().getId(), idFlete);
        }

        flete.setChofer(null);
        flete.setCliente(null);
        flete.setUsuario(null);
        flete.setCamion(null);
        flete.setAcoplado(null);
        flete.setProducto(null);

        fleteRepositorio.save(flete);

        fleteRepositorio.deleteById(idFlete);

    }

    public Date convertirFecha(String fecha) throws ParseException { //convierte fecha String a fecha Date
        SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd");
        return formato.parse(fecha);
    }

}
