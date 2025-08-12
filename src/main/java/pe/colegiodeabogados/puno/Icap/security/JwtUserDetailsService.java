package pe.colegiodeabogados.puno.Icap.security;



import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import pe.colegiodeabogados.puno.Icap.model.Usuario;
import pe.colegiodeabogados.puno.Icap.model.UsuarioRol;
import pe.colegiodeabogados.puno.Icap.repository.IUsuarioRepository;
import pe.colegiodeabogados.puno.Icap.repository.IUsuarioRolRepository;


import java.util.ArrayList;
import java.util.List;

//Clase S4
@Service
@RequiredArgsConstructor
public class JwtUserDetailsService implements UserDetailsService {

    private final IUsuarioRolRepository repo;
    private final IUsuarioRepository repoU;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario u=repoU.findOneByUsers(username).orElse(null);
        List<UsuarioRol> user = repo.findOneByUsuarioUsers(username);

        if (user == null) {
            throw new UsernameNotFoundException("Username not found: " + username);
        }

        List<GrantedAuthority> roles = new ArrayList<>();
        user.forEach(rol -> {
            roles.add(new SimpleGrantedAuthority(rol.getRol().getNombre().name()));
        });

        return new org.springframework.security.core.userdetails.User(u.getUsers(), u.getClave(), roles);
    }
}
