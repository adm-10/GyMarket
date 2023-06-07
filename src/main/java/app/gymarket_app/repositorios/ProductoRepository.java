package app.gymarket_app.repositorios;

import app.gymarket_app.entidades.Compra;
import app.gymarket_app.entidades.Producto;
import app.gymarket_app.entidades.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface ProductoRepository  extends JpaRepository<Producto, Long> {


    List<Producto> findByCompra(Compra compra);

    List<Producto> findByUsuario(Usuario usuario);


    List<Producto> findByCompraIsNull();

    List<Producto> findByNombreContainsIgnoreCaseAndCompraIsNull(String nombre);

    List<Producto> findByNombreContainsIgnoreCaseAndUsuario(String nombre, Usuario usuario);



}
