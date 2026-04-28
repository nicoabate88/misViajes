package abate.abate.servicios;

import abate.abate.entidades.Acoplado;
import abate.abate.entidades.Caja;
import abate.abate.entidades.Camion;
import abate.abate.entidades.ChoferEstadistica;
import abate.abate.entidades.ChoferesEstadistica;
import abate.abate.entidades.Combustible;
import abate.abate.entidades.Entrega;
import abate.abate.entidades.Flete;
import abate.abate.entidades.Gasto;
import abate.abate.entidades.Ingreso;
import abate.abate.entidades.Usuario;
import abate.abate.excepciones.MiException;
import abate.abate.repositorios.AcopladoRepositorio;
import abate.abate.repositorios.CajaRepositorio;
import abate.abate.repositorios.CamionRepositorio;
import abate.abate.repositorios.CombustibleRepositorio;
import abate.abate.repositorios.DocumentacionRepositorio;
import abate.abate.repositorios.EntregaRepositorio;
import abate.abate.repositorios.FleteRepositorio;
import abate.abate.repositorios.GastoRepositorio;
import abate.abate.repositorios.IngresoRepositorio;
import abate.abate.repositorios.UsuarioRepositorio;
import abate.abate.util.ChoferComparador;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class ChoferServicio {

    @Autowired
    private UsuarioRepositorio usuarioRepositorio;
    @Autowired
    private CuentaServicio cuentaServicio;
    @Autowired
    private CajaServicio cajaServicio;
    @Autowired
    private CamionRepositorio camionRepositorio;
    @Autowired
    private CombustibleRepositorio combustibleRepositorio;
    @Autowired
    private FleteRepositorio fleteRepositorio;
    @Autowired
    private EntregaRepositorio entregaRepositorio;
    @Autowired
    private IngresoRepositorio ingresoRepositorio;
    @Autowired
    private CajaRepositorio cajaRepositorio;
    @Autowired
    private GastoRepositorio gastoRepositorio;
    @Autowired
    private AcopladoRepositorio acopladoRepositorio;
    @Autowired
    private DocumentacionRepositorio documentacionRepositorio;
    @Autowired
    private DocumentacionServicio documentacionServicio;

    @Transactional
    public void crearChofer(Long idOrg, String nombre, Long cuil, Long idCamion, String caja, String cuenta,String verDocumentacion, String documentacion,
            String verMantenimiento, String mantenimiento, String nombreUsuario, Double porcentaje, String estado, String password, String password2) throws MiException {

        String nombreUsuarioMin = nombreUsuario.toLowerCase();
        String nombreM = nombre.toUpperCase();

        validarDatos(idOrg, nombreM, nombreUsuarioMin, cuil, password, password2);

        Usuario user = new Usuario();
        if (idCamion != null) {
            Camion camion = new Camion();
            Optional<Camion> cam = camionRepositorio.findById(idCamion);
            if (cam.isPresent()) {
                camion = cam.get();
            }
            user.setCamion(camion);
        }

        user.setIdOrg(idOrg);
        user.setNombre(nombreM);
        user.setCuil(cuil);
        user.setUsuario(nombreUsuarioMin);
        user.setPassword(new BCryptPasswordEncoder().encode(password));
        user.setRol("CHOFER");
        user.setCaja(caja);
        user.setCuenta(cuenta);
        user.setVerDocumentacion(verDocumentacion);
        user.setDocumentacion(documentacion);
        user.setVerMantenimiento(verMantenimiento);
        user.setMantenimiento(mantenimiento);
        user.setPorcentaje(porcentaje);
        user.setEstado(estado);

        usuarioRepositorio.save(user);

        Long idUsuario = buscarUltimo(idOrg);
        cuentaServicio.crearCuentaChofer(idUsuario);
        cajaServicio.crearCajaChofer(idUsuario);

    }

    @Transactional
    public void modificarChofer(Long id, String nombre, Long cuil, Long idCamion, String verDocumentacion,  String documentacion, 
            String verMantenimiento, String mantenimiento, String nombreUsuario, Double porcentaje, String estado) throws MiException {

        String nombreM = nombre.toUpperCase();
        String nombreUsuarioMin = nombreUsuario.toLowerCase();

        Usuario user = new Usuario();
        Optional<Usuario> u = usuarioRepositorio.findById(id);
        if (u.isPresent()) {
            user = u.get();
        }

        validarDatosModificar(user, nombreM, nombreUsuarioMin, cuil);
        
                if (!user.getEstado().equalsIgnoreCase(estado)) {
            if (estado.equalsIgnoreCase("INHABILITADO")) {
                Boolean flag = false;
                flag = documentacionRepositorio.existsByChoferIdAndEstado(id, "VIGENTE");
                if (flag == true) {
                    documentacionServicio.inhabilitarDocumentacionIdChofer(id);
                }
            } else if (estado.equalsIgnoreCase("HABILITADO")) {
                Boolean flag = false;
                flag = documentacionRepositorio.existsByChoferIdAndEstado(id, "INHABILITADO");
                if (flag == true) {
                    documentacionServicio.habilitarDocumentacionIdChofer(id);
                }
            }
        }

        if (idCamion != 0) {
            Camion camion = new Camion();
            Optional<Camion> cam = camionRepositorio.findById(idCamion);
            if (cam.isPresent()) {
                camion = cam.get();
            }
            user.setCamion(camion);
        } else {
            user.setCamion(null);
        }

        user.setNombre(nombreM);
        user.setCuil(cuil);
        user.setUsuario(nombreUsuario);
        user.setPorcentaje(porcentaje);
        user.setVerDocumentacion(verDocumentacion);
        user.setDocumentacion(documentacion);
        user.setVerMantenimiento(verMantenimiento);
        user.setMantenimiento(mantenimiento);
        user.setEstado(estado);

        usuarioRepositorio.save(user);

    }

    @Transactional
    public void habilitarCajaChofer(Long idUsuario) {

        Usuario chofer = new Usuario();
        Optional<Usuario> user = usuarioRepositorio.findById(idUsuario);
        if (user.isPresent()) {
            chofer = user.get();
        }

        chofer.setCaja("SI");

        usuarioRepositorio.save(chofer);

        cajaServicio.habilitarCaja(idUsuario);

    }

    @Transactional
    public void inhabilitarCajaChofer(Long idUsuario) {

        Usuario chofer = new Usuario();
        Optional<Usuario> user = usuarioRepositorio.findById(idUsuario);
        if (user.isPresent()) {
            chofer = user.get();
        }

        chofer.setCaja("NO");

        usuarioRepositorio.save(chofer);

        cajaServicio.inhabilitarCaja(idUsuario);

    }
    
    @Transactional
    public void habilitarCuentaChofer(Long idUsuario) {

        Usuario chofer = new Usuario();
        Optional<Usuario> user = usuarioRepositorio.findById(idUsuario);
        if (user.isPresent()) {
            chofer = user.get();
        }

        chofer.setCuenta("SI");

        usuarioRepositorio.save(chofer);

        cuentaServicio.habilitarCuenta(idUsuario);

    }

    @Transactional
    public void inhabilitarCuentaChofer(Long idUsuario) {

        Usuario chofer = new Usuario();
        Optional<Usuario> user = usuarioRepositorio.findById(idUsuario);
        if (user.isPresent()) {
            chofer = user.get();
        }

        chofer.setCuenta("NO");

        usuarioRepositorio.save(chofer);

        cuentaServicio.inhabilitarCuenta(idUsuario);

    }

    @Transactional
    public void modificarPswChofer(Long id, String password) {

        Usuario user = new Usuario();
        Optional<Usuario> u = usuarioRepositorio.findById(id);
        if (u.isPresent()) {
            user = u.get();
        }

        user.setPassword(new BCryptPasswordEncoder().encode(password));

        usuarioRepositorio.save(user);

    }

    @Transactional
    public void eliminarChofer(Long id) throws MiException {

        Usuario chofer = usuarioRepositorio.getById(id);

        Flete flete = fleteRepositorio.findTopByChoferOrderByIdDesc(chofer);
        Entrega entrega = entregaRepositorio.findTopByChoferOrderByIdDesc(chofer);
        Combustible combustible = combustibleRepositorio.findTopByUsuarioOrderByIdDesc(chofer);
        Ingreso ingreso = ingresoRepositorio.findTopByChoferOrderByIdDesc(chofer);
        Caja caja = cajaServicio.buscarCajaChofer(id);
        Gasto gasto = gastoRepositorio.findTopByChoferOrderByIdDesc(chofer);

        if (flete == null && entrega == null && combustible == null && ingreso == null && gasto == null) {

            cajaRepositorio.deleteById(caja.getId());
            usuarioRepositorio.deleteById(id);
            cuentaServicio.eliminarCuentaChofer(id);

        } else {

            throw new MiException("El Chofer no puede ser eliminado, tiene Viaje / Gasto / Entrega y/o Combustible asociado.");
        }

    }

    public Usuario buscarChofer(Long id) {

        return usuarioRepositorio.getById(id);

    }

    public ArrayList<Usuario> bucarChoferesNombreAsc(Long idOrg) {

        ArrayList<Usuario> lista = usuarioRepositorio.buscarUsuariosChofer(idOrg);

        Collections.sort(lista, ChoferComparador.ordenarNombreAsc);

        return lista;
    }
    
    public ArrayList<Usuario> bucarChoferesHabNombreAsc(Long idOrg) {

        ArrayList<Usuario> lista = usuarioRepositorio.buscarUsuariosChoferHab(idOrg);

        Collections.sort(lista, ChoferComparador.ordenarNombreAsc);

        return lista;
    }

    public ArrayList<Usuario> bucarUsuarios(Long idOrg) {

        ArrayList<Usuario> lista = usuarioRepositorio.buscarUsuarios(idOrg);

        return lista;
    }

    public ArrayList<Usuario> bucarTodosUsuarios() {

        ArrayList<Usuario> lista = (ArrayList<Usuario>) usuarioRepositorio.findAll();

        return lista;
    }

    public Long buscarUltimo(Long idOrg) {

        return usuarioRepositorio.ultimoUsuario(idOrg);
    }
    
    public ArrayList<ChoferEstadistica> estadisticaChofer(String desde, String hasta, Long idChofer) throws ParseException {

        Date d = convertirFecha(desde);
        Date h = convertirFecha(hasta);

        ArrayList<Flete> fletes = fleteRepositorio.buscarFleteChofer(d, h, idChofer);
        ArrayList<Combustible> cargas = combustibleRepositorio.buscarCombustibleIdChofer(d, h, idChofer);
        ArrayList<Gasto> gastos = gastoRepositorio.findByFechaBetweenAndChoferId(d, h, idChofer);

        Map<String, ChoferEstadistica> resumenMap = new HashMap<>();
        
        int totalFletes = 0;
    int totalKm = 0;
    double totalLitros = 0;
    int totalGastos = 0;
    int totalNeto = 0;

        for (Flete flete : fletes) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(flete.getFechaFlete());
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH) + 1;

            String key = year + "-" + month;

            if (resumenMap.containsKey(key)) {
                ChoferEstadistica resumen = resumenMap.get(key);
                resumen.setFlete(resumen.getFlete() + 1);
                resumen.setNeto(resumen.getNeto() + flete.getNeto());
            } else {
                ChoferEstadistica nuevoResumen = new ChoferEstadistica(year, month, 1, flete.getNeto());
                resumenMap.put(key, nuevoResumen);
            }
            
            totalFletes++;
            totalNeto += flete.getNeto();
        }

        for (Combustible combustible : cargas) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(combustible.getFechaCarga());
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH) + 1;

            String key = year + "-" + month;

            if (resumenMap.containsKey(key)) {
                ChoferEstadistica resumen = resumenMap.get(key);
                resumen.setKmRecorrido(resumen.getKmRecorrido() + combustible.getKmRecorrido());
                resumen.setLitro(resumen.getLitro() + combustible.getLitro());
            } else {

                ChoferEstadistica nuevoResumen = new ChoferEstadistica(year, month, 0, 0.0);
                nuevoResumen.setKmRecorrido(combustible.getKmRecorrido());
                nuevoResumen.setLitro(combustible.getLitro());
                resumenMap.put(key, nuevoResumen);
            }
                    totalKm += combustible.getKmRecorrido();
        totalLitros += combustible.getLitro();
        }

        for (Gasto gasto : gastos) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(gasto.getFecha());
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH) + 1;

            String key = year + "-" + month;

            if (resumenMap.containsKey(key)) {
                ChoferEstadistica resumen = resumenMap.get(key);
                resumen.setGasto(resumen.getGasto() + gasto.getImporte());
            } else {

                ChoferEstadistica nuevoResumen = new ChoferEstadistica(year, month, 0.0);
                nuevoResumen.setGasto(gasto.getImporte());
                resumenMap.put(key, nuevoResumen);
            }
            totalGastos += gasto.getImporte();
        }

            ArrayList<ChoferEstadistica> resultado = new ArrayList<>(resumenMap.values());

    ChoferEstadistica totalGeneral = new ChoferEstadistica(0, 0, totalFletes);
    totalGeneral.setFlete(totalFletes);
    totalGeneral.setKmRecorrido(totalKm);
    totalGeneral.setLitro(totalLitros);
    totalGeneral.setGasto(totalGastos);
    totalGeneral.setNeto(totalNeto);
    if(totalKm > 0){
    totalGeneral.setRentabilidad(totalNeto / totalKm);
    }
    
    resultado.add(totalGeneral);

    return resultado;

    }

    public Map<Usuario, ChoferesEstadistica> estadisticaChoferes(String desde, String hasta, Long idOrg) throws ParseException {

        Date d = convertirFecha(desde);
        Date h = convertirFecha(hasta);

        List<Flete> fletes = fleteRepositorio.findByFechaFleteBetweenAndIdOrg(d, h, idOrg);
        List<Combustible> cargas = combustibleRepositorio.findByFechaCargaBetweenAndIdOrg(d, h, idOrg);
        List<Gasto> gastos = gastoRepositorio.findByFechaBetweenAndIdOrg(d, h, idOrg);

        Map<Usuario, ChoferesEstadistica> estadisticasPorChofer = new HashMap<>();
        ChoferesEstadistica totalGeneral = new ChoferesEstadistica();
        
        // Pre-cargar todos los choferes
        ArrayList<Usuario> lista = usuarioRepositorio.buscarUsuariosChoferHab(idOrg);
        for (Usuario chofer : lista) {
        estadisticasPorChofer.put(chofer, new ChoferesEstadistica());
        }

        for (Flete flete : fletes) {
            Usuario chofer = flete.getChofer();
            estadisticasPorChofer.putIfAbsent(chofer, new ChoferesEstadistica());
            ChoferesEstadistica resumen = estadisticasPorChofer.get(chofer);
            resumen.setFlete(resumen.getFlete() + 1);
            resumen.setNeto(resumen.getNeto() + flete.getNeto());
            totalGeneral.setFlete(totalGeneral.getFlete() + 1);
            totalGeneral.setNeto(totalGeneral.getNeto() + flete.getNeto());
        }

        for (Combustible combustible : cargas) {
            if(combustible.getChofer() != null && combustible.getConsumo() != null){
            Usuario chofer = combustible.getChofer();
            estadisticasPorChofer.putIfAbsent(chofer, new ChoferesEstadistica());
            ChoferesEstadistica resumen = estadisticasPorChofer.get(chofer);
            resumen.setKmRecorrido(resumen.getKmRecorrido() + combustible.getKmRecorrido());
            resumen.setLitro(resumen.getLitro() + combustible.getLitro());
            totalGeneral.setKmRecorrido(totalGeneral.getKmRecorrido() + combustible.getKmRecorrido());
            totalGeneral.setLitro(totalGeneral.getLitro() + combustible.getLitro());
        }
        }

        for (Gasto gasto : gastos) {
            Usuario chofer = gasto.getChofer();
            estadisticasPorChofer.putIfAbsent(chofer, new ChoferesEstadistica());
            ChoferesEstadistica resumen = estadisticasPorChofer.get(chofer);
            resumen.setGasto(resumen.getGasto() + gasto.getImporte());
            totalGeneral.setGasto(totalGeneral.getGasto() + gasto.getImporte());
        }
        
        if(totalGeneral.getKmRecorrido() > 0){
        totalGeneral.setRentabilidad(totalGeneral.getNeto() / totalGeneral.getKmRecorrido());
        }
        
        Usuario totalKey = new Usuario();
        totalKey.setNombre("TOTAL");
        estadisticasPorChofer.put(totalKey, totalGeneral);
        // Ordenar el mapa por el dominio del camión
        return estadisticasPorChofer.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByKey(Comparator.comparing(Usuario::getNombre)))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));

    }

    public void validarDatos(Long idOrg, String nombre, String nombreUsuario, Long cuil, String password, String password2) throws MiException {

        ArrayList<Usuario> lista = bucarTodosUsuarios();

        if (lista != null) {
            for (Usuario u : lista) {
                if (u.getUsuario().equalsIgnoreCase(nombreUsuario)) {
                    throw new MiException("El Nombre de Usuario no es válido, por favor ingrese otro.");
                }
            }
        }

        ArrayList<Usuario> listaChoferes = usuarioRepositorio.buscarUsuariosChofer(idOrg);

        if (listaChoferes != null) {
            for (Usuario u : listaChoferes) {
                if (u.getNombre().equalsIgnoreCase(nombre)) {
                    throw new MiException("Apellido y Nombre ya está registrado.");
                }
                if (Objects.equals(u.getCuil(), cuil)) {
                    throw new MiException("El CUIL ya está registrado.");
                }
            }
            if (!password.equals(password2)) {
                throw new MiException("Las Contraseñas ingresadas deben ser iguales.");
            }
        }
    }

    public void validarDatosModificar(Usuario user, String nombre, String nombreUsuario, Long cuil) throws MiException {

        ArrayList<Usuario> listaChoferes = usuarioRepositorio.buscarUsuariosChofer(user.getIdOrg());

        if (listaChoferes != null) {
            if (!user.getNombre().equalsIgnoreCase(nombre)) {
                for (Usuario u : listaChoferes) {
                    if (u.getNombre().equalsIgnoreCase(nombre)) {
                        throw new MiException("Apellido y Nombre ya está registrado.");
                    }
                }
            }
            if (!user.getCuil().equals(cuil)) {
                for (Usuario u : listaChoferes) {
                    if (Objects.equals(u.getCuil(), cuil)) {
                        throw new MiException("El CUIL ya está registrado.");
                    }
                }
            }
        }

        ArrayList<Usuario> lista = bucarTodosUsuarios();
        if (lista != null) {
            if (!user.getUsuario().equalsIgnoreCase(nombreUsuario)) {
                for (Usuario u : lista) {
                    if (u.getUsuario().equalsIgnoreCase(nombreUsuario)) {
                        throw new MiException("El Nombre de Usuario no es válido, por favor ingrese otro.");
                    }
                }
            }
        }
        
    }

    public Date convertirFecha(String fecha) throws ParseException { //convierte fecha String a fecha Date
        SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd");
        return formato.parse(fecha);
    }

}
