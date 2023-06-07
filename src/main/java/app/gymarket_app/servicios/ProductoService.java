package app.gymarket_app.servicios;

import app.gymarket_app.entidades.Compra;
import app.gymarket_app.entidades.Producto;
import app.gymarket_app.entidades.Usuario;
import app.gymarket_app.repositorios.ProductoRepository;
import app.gymarket_app.upload.StorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;


import java.util.List;

@RequiredArgsConstructor
@Service
public class ProductoService {
   private final StorageService storageService;

    private  final ProductoRepository repositorio;
    public Producto findById(Long id) {
        return repositorio.findById(id).orElse(null);
    }
    public List<Producto> findAll() {
        return repositorio.findAll();
    }
    public Producto save(Producto p) {
        return repositorio.save(p);
    }


    public void delete(Producto p) {
        if (!p.getImagen().isEmpty())
            storageService.delete(p.getImagen());
        repositorio.delete(p);
    }

    public List<Producto> variosPorId(List<Long> ids) {
        return repositorio.findAllById(ids);
    }

    public Producto edit(Producto p) {
        return repositorio.save(p);
    }


    public List<Producto> productosDeUnaCompra(Compra c) {
        return repositorio.findByCompra(c);
    }

    public List<Producto> productosDeUnUsuario(Usuario u) {
        return repositorio.findByUsuario(u);
    }

    public List<Producto> buscarMisProductos(String query, Usuario u) {
        return repositorio.findByNombreContainsIgnoreCaseAndUsuario(query,u);
    }

    public List<Producto> buscar(String query) {
        return repositorio.findByNombreContainsIgnoreCaseAndCompraIsNull(query);
    }
    public List<Producto> findTop3() {
        // Utilizamos el método findAll() de Spring Data JPA con una instancia de PageRequest para obtener los tres primeros registros
        Pageable pageable = PageRequest.of(0, 3);
        Page<Producto> page = repositorio.findAll(pageable);

        // Devolvemos la lista de productos obtenidos de la página
        return page.getContent();
    }

}
