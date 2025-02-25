package abate.abate.servicios;

import abate.abate.entidades.Usuario;
import abate.abate.excepciones.MiException;
import abate.abate.repositorios.UsuarioRepositorio;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Service
public class UsuarioServicio implements UserDetailsService {

    @Autowired
    private UsuarioRepositorio usuarioRepositorio;

    @Transactional
    public void crearUsuario(String nombre, String nombreUsuario, String password, String password2, Usuario usuario) throws MiException {

        String nombreMay = nombre.toUpperCase();
        String nombreUsuarioMin = nombreUsuario.toLowerCase();

        validarDatos(usuario.getCuil(), nombreMay, nombreUsuarioMin, password, password2);

        Usuario user = new Usuario();

        user.setNombre(nombreMay);
        user.setCuil(usuario.getCuil());
        user.setUsuario(nombreUsuarioMin);
        user.setIdOrg(usuario.getIdOrg());
        user.setEmpresa(usuario.getEmpresa());
        user.setDireccion(usuario.getDireccion());
        user.setLocalidad(usuario.getLocalidad());
        user.setTelefono(usuario.getTelefono());
        user.setPassword(new BCryptPasswordEncoder().encode(password));
        user.setRol("ADMIN");
        user.setCaja("NO");

        usuarioRepositorio.save(user);

    }

    @Transactional
    public void crearUsuarioAdmin(Long idOrg, String nombre, String nombreUsuario, Long cuil, String empresa,
            String direccion, String localidad, String telefono, String password, String password2) throws MiException {

        String nombreMay = nombre.toUpperCase();
        String empresaMay = empresa.toUpperCase();
        String nombreUsuarioMin = nombreUsuario.toLowerCase();

        validarDatos(idOrg, nombreMay, nombreUsuarioMin, password, password2);

        Usuario user = new Usuario();

        user.setNombre(nombreMay);
        user.setCuil(cuil);
        user.setUsuario(nombreUsuarioMin);
        user.setIdOrg(idOrg);
        user.setEmpresa(empresaMay);
        user.setDireccion(direccion);
        user.setLocalidad(localidad);
        user.setTelefono(telefono);
        user.setPassword(new BCryptPasswordEncoder().encode(password));
        user.setRol("ADMIN");
        user.setCaja("NO");

        usuarioRepositorio.save(user);

    }

    @Transactional
    public void modificarUsuario(Long id, String nombre, String nombreUsuario) throws MiException {

        String nombreMay = nombre.toUpperCase();
        String nombreUsuarioMin = nombreUsuario.toLowerCase();

        Usuario user = usuarioRepositorio.getById(id);

        validarDatosModifica(user, nombreMay, nombreUsuarioMin);

        user.setNombre(nombreMay);
        user.setUsuario(nombreUsuarioMin);

        usuarioRepositorio.save(user);

    }

    @Transactional
    public void modificarPswUsuario(Long id, String password) {

        Usuario user = new Usuario();
        Optional<Usuario> u = usuarioRepositorio.findById(id);
        if (u.isPresent()) {
            user = u.get();
        }

        user.setPassword(new BCryptPasswordEncoder().encode(password));

        usuarioRepositorio.save(user);

    }

    public Usuario buscarUsuario(Long idUsuario) {

        return usuarioRepositorio.getById(idUsuario);
    }

    public ArrayList<Usuario> buscarUsuarios(Long idOrg) {

        ArrayList<Usuario> lista = usuarioRepositorio.buscarUsuariosAdmin(idOrg);

        return lista;

    }

    public ArrayList<Usuario> buscarTodosUsuarios() {

        ArrayList<Usuario> lista = (ArrayList<Usuario>) usuarioRepositorio.findAll();

        return lista;

    }

    public Long buscarUltimoUsuario(Long idOrg) {

        return usuarioRepositorio.ultimoUsuario(idOrg);
    }

    public Long buscarUltimoUsuarioCeo() {

        return usuarioRepositorio.ultimoUsuarioCeo();

    }

    public void validarDatos(Long idOrg, String nombre, String nombreUsuario, String password, String password2) throws MiException {

        ArrayList<Usuario> listaAdmin = usuarioRepositorio.buscarUsuariosAdmin(idOrg);
        for (Usuario u : listaAdmin) {
            if (u.getNombre().equalsIgnoreCase(nombre)) {
                throw new MiException("Apellido y Nombre ya está registrado.");
            }
        }

        ArrayList<Usuario> lista = buscarTodosUsuarios();
        for (Usuario u : lista) {
            if (u.getUsuario().equals(nombreUsuario)) {
                throw new MiException("El Nombre de Usuario no está disponible, por favor ingrese otro.");
            }
        }

        if (!password.equals(password2)) {
            throw new MiException("Las Contraseñas ingresadas deben ser iguales.");
        }
    }

    public void validarDatosModifica(Usuario user, String nombre, String nombreUsuario) throws MiException {

        ArrayList<Usuario> listaAdmin = usuarioRepositorio.buscarUsuariosAdmin(user.getIdOrg());
        if (!user.getNombre().equalsIgnoreCase(nombre)) {
            for (Usuario u : listaAdmin) {
                if (u.getNombre().equals(nombre)) {
                    throw new MiException("Apellido y Nombre ya está registrado.");
                }
            }
        }

        ArrayList<Usuario> lista = buscarTodosUsuarios();
        if (!user.getUsuario().equalsIgnoreCase(nombreUsuario)) {
            for (Usuario u : lista) {
                if (u.getUsuario().equalsIgnoreCase(nombreUsuario)) {
                    throw new MiException("El Nombre de Usuario no está disponible, por favor ingrese otro.");
                }
            }
        }
    }

    @Transactional
    public void crearUsuarioCeo(String nombre, String nombreUsuario, String password, String password2) {

        String nombreM = nombre.toUpperCase();

        Usuario user = new Usuario();

        user.setNombre(nombreM);
        user.setUsuario(nombreUsuario);
        user.setPassword(new BCryptPasswordEncoder().encode(password));
        user.setRol("CEO");

        usuarioRepositorio.save(user);

    }

    public ArrayList<Usuario> buscarUsuariosAdmin() {

        ArrayList<Usuario> lista = usuarioRepositorio.findFirstByIdOrg();

        return lista;

    }

    @Override
    public UserDetails loadUserByUsername(String nombreUsuario) throws UsernameNotFoundException {

        Usuario usuario = usuarioRepositorio.buscarUsuarioPorUsuario(nombreUsuario);

        if (usuario != null) {

            List<GrantedAuthority> permisos = new ArrayList();

            GrantedAuthority p = new SimpleGrantedAuthority("ROLE_" + usuario.getRol().toString());

            permisos.add(p);

            ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();

            HttpSession session = attr.getRequest().getSession(true);

            session.setAttribute("usuariosession", usuario);

            return new User(usuario.getUsuario(), usuario.getPassword(), permisos);

        } else {

            return null;

        }

    }

}
