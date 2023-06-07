package app.gymarket_app.servicios;

import app.gymarket_app.entidades.Compra;
import app.gymarket_app.entidades.Producto;
import app.gymarket_app.entidades.Usuario;
import app.gymarket_app.repositorios.CompraRepository;

import app.gymarket_app.repositorios.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class CompraServicio {

    private final CompraRepository repositorio;

    private final ProductoService productoService;


    public Compra insertar(Compra c, Usuario u) {
        c.setUsuario(u);
        c.setFechaCompra();
        return repositorio.save(c);
    }

   public Compra insertar(Compra c) {
        return repositorio.save(c);
    }

    public Producto addProductoCompra(Producto p, Compra c) {
        p.setCompra(c);
        return productoService.edit(p);
    }

    public Compra buscarPorId(long id) {
        return repositorio.findById(id).orElse(null);
    }

    public List<Compra> todas() {
        return repositorio.findAll();
    }


    public List<Compra> porUsuario(Usuario u) {
        return repositorio.findByUsuario(u);
    }
}
