package app.gymarket_app.repositorios;

import app.gymarket_app.entidades.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Usuario findFirstByUsername(String username);
    Usuario findFirstByEmail(String email);

}
