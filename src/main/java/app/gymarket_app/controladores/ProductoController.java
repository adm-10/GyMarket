package app.gymarket_app.controladores;

import app.gymarket_app.entidades.Producto;
import app.gymarket_app.entidades.Usuario;
import app.gymarket_app.servicios.ProductoService;
import app.gymarket_app.servicios.UsuarioServicio;
import app.gymarket_app.upload.StorageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import java.util.List;


@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping("/app")
public class ProductoController {

    private final ProductoService productoService;

    private final StorageService storageService;
//le puse el final

    private  Usuario usuario;


   @ModelAttribute("allproductos")
    public List<Producto> allproductos() {
       return productoService.findAll();
    }


// Busca productos de un usuario, con el find all busca todos
    @GetMapping("/allproductos")
    public String productosAdmin(Model model,  @RequestParam(name = "q", required = false) String query)  {
        List<Producto> productos;

        if (query != null) {
            productos = productoService.buscarMisProductos(query, usuario);
        } else {
            productos = productoService.findAll();
        }

        model.addAttribute("allproductos", productos);
        return "app/producto/list-producto";
    }

    //Tiene los productos para comprar
    @GetMapping({ "/tienda"})
    public String tienda(Model model, @RequestParam(name="q", required=false) String query) {
        List<Producto> productos;

        if (query != null) {
            productos = productoService.buscarMisProductos(query, usuario);
        } else {
            productos = productoService.findAll();
        }
        model.addAttribute("allproductos", productos);

        return "tienda";
    }


    @GetMapping("/allproductos/delete/{id}")
    public String deleteProducto(@PathVariable Long id) {
        Producto p = productoService.findById(id);
            productoService.delete(p);
        return "redirect:/app/allproductos";
    }

    @GetMapping("/producto/new")
    public String nuevoProductoForm(Model model) {
        model.addAttribute("producto", new Producto());
        return "app/producto/form-producto";

    }


    @PostMapping("/producto/new/submit")
    public String nuevoProductoSubmit(@Valid @ModelAttribute Producto producto, BindingResult bindingResult,
                                      @RequestParam("file") MultipartFile file) {

        // Verificar si hay errores de validación
        if (bindingResult.hasErrors()) {
            // Mostrar los errores en el formulario
            return "app/producto/form-producto";
        }
        if (!file.isEmpty()) {
            String imagen = storageService.store(file);
            producto.setImagen(MvcUriComponentsBuilder
                    .fromMethodName(FilesController.class, "serveFile", imagen).build().toUriString());
        }
        producto.setUsuario(usuario);
        productoService.save(producto);
        return "redirect:/app/allproductos";
    }



    @GetMapping("/producto/edit/{id}")
    public String editarProducto(@PathVariable Long id, Model model) {

        Producto producto = productoService.findById(id);
        if (producto != null) {
            model.addAttribute("producto", producto);
            return "app/producto/form-producto";
        } else {
            return "redirect:/app/allproductos";
        }
    }

    @PostMapping("/producto/edit/submit")
    public String editarProductoSubmit(@Valid @ModelAttribute Producto producto, BindingResult bindingResult,
                                    @RequestParam("file") MultipartFile file)  {
        // Verificar si hay errores de validación
        if (bindingResult.hasErrors()) {
            // Manejar los errores de validación según tus necesidades
            // Puedes redirigir a una página de error o mostrar los errores en el formulario
            return "app/producto/form-producto";
        }

        if (!file.isEmpty()) {
            String imagen = storageService.store(file);
            producto.setImagen(MvcUriComponentsBuilder
                    .fromMethodName(FilesController.class, "serveFile", imagen).build().toUriString());
        }
        producto.setUsuario(usuario);
        productoService.edit(producto);
        return "redirect:/app/allproductos";


    }




}
