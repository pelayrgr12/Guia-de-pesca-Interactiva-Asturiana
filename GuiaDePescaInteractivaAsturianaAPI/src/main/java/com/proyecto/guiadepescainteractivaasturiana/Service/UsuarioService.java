package com.proyecto.guiadepescainteractivaasturiana.Service;

import com.proyecto.guiadepescainteractivaasturiana.DTo.UsuarioPutDTo;
import com.proyecto.guiadepescainteractivaasturiana.Entities.Rol;
import com.proyecto.guiadepescainteractivaasturiana.Entities.Usuario;
import com.proyecto.guiadepescainteractivaasturiana.Repository.UsuarioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService implements UserDetailsService {
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private RoleService rolService;


    public List<Usuario> findAll() {

        return usuarioRepository.findAll();
    }

    public boolean actualizarHabilitado(int id, boolean habilitado) {
        Optional<Usuario> optionalUsuario = usuarioRepository.findById(id);

        if (optionalUsuario.isEmpty()) {
            return false;
        }

        Usuario usuario = optionalUsuario.get();
        usuario.setHabilitado(habilitado);
        usuarioRepository.save(usuario);

        return true;
    }


    public Optional<Usuario> findByCorreo(String correo){
        return usuarioRepository.findByCorreo(correo);
    }

    public Usuario getUsuarioByCorreo(String correo) {
        return usuarioRepository.findByCorreo(correo).orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con correo: " + correo));
    }


    public Usuario actualizarUsuario(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder, RoleService rolService) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.rolService = rolService;
    }

    public Optional<Usuario> findByUsername(String username) {
        return usuarioRepository.findByNombre(username);
    }

    public boolean validarCredenciales(String correo, String password) {
        Optional<Usuario> usuario = usuarioRepository.findByCorreo(correo);
        return usuario.isPresent() && passwordEncoder.matches(password, usuario.get().getContrasena());
    }

    public Usuario registrarUsuario(Usuario usuario, String password) {
        usuario.setHabilitado(true);
        usuario.setContrasena(passwordEncoder.encode(password));

        Rol rolUsuario;
        try {
            rolUsuario = rolService.obtenerRolPorNombre("USER");
        } catch (RuntimeException e) {

            rolUsuario = new Rol();
            rolUsuario.setRol("USER");
            rolUsuario = rolService.guardarRol(rolUsuario);
            System.out.println("Rol 'USER' creado automáticamente.");
        }

        usuario.setRol(rolUsuario);

        return usuarioRepository.save(usuario);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByNombre(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));

        return new User(usuario.getNombre(), usuario.getContrasena(), Collections.emptyList());
    }

    public void actualizarDatosUsuario(String correo, UsuarioPutDTo dto) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByCorreo(correo);
        if (usuarioOpt.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado");
        }

        Usuario usuario = usuarioOpt.get();

        if (dto.getNuevaContrasena() != null && !dto.getNuevaContrasena().isBlank()) {
            if (dto.getContrasenaActual() == null || !passwordEncoder.matches(dto.getContrasenaActual(), usuario.getContrasena())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La contraseña actual es incorrecta");
            }

            if (!esContrasenaSegura(dto.getNuevaContrasena())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La nueva contraseña no cumple con los requisitos");
            }

            usuario.setContrasena(passwordEncoder.encode(dto.getNuevaContrasena()));
        }

        if (dto.getNombre() != null && !dto.getNombre().isBlank()) {
            usuario.setNombre(dto.getNombre());
        }
        if (dto.getFechaNacimiento() != null) {
            usuario.setFechaNacimiento(dto.getFechaNacimiento());
        }
        if (dto.getCorreo() != null && !dto.getCorreo().isBlank()) {
            usuario.setCorreo(dto.getCorreo());
        }

        usuarioRepository.save(usuario);
    }



    private boolean esContrasenaSegura(String contrasena) {
        return contrasena != null &&
                contrasena.length() >= 8 &&
                contrasena.matches(".*[A-Z].*") &&
                contrasena.matches(".*[a-z].*") &&
                contrasena.matches(".*\\d.*") &&
                contrasena.matches(".*[!@#$%^&*(),.?\":{}|<>].*");
    }





}