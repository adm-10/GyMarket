package app.gymarket_app.repositorios;
import app.gymarket_app.entidades.Compra;
import app.gymarket_app.entidades.Usuario;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface CompraRepository extends JpaRepository<Compra, Long> {


    List<Compra> findByUsuario(Usuario usuario);

}
