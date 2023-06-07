package app.gymarket_app.servicios;

import app.gymarket_app.entidades.Usuario;
import app.gymarket_app.repositorios.UsuarioRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@RequiredArgsConstructor
@Service
public class UsuarioServicio {

	private final UsuarioRepository repositorio;

	private final PasswordEncoder passwordEncoder;

	public Usuario findById(long id) {
		return repositorio.findById(id).orElse(null);
	}

	public Usuario buscarPorUsername(String username) {
		return repositorio.findFirstByUsername(username);
	}
	public Usuario buscarPorEmail(String email) {
		return repositorio.findFirstByEmail(email);
	}

	public Usuario save(Usuario u) {
		u.setPassword(passwordEncoder.encode(u.getPassword()));
		u.setFechaAlta();
		return repositorio.save(u);
	}


}
