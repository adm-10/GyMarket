package app.gymarket_app.controladores;

import app.gymarket_app.entidades.Actividad;
import app.gymarket_app.entidades.Producto;
import app.gymarket_app.servicios.ActividadService;
import app.gymarket_app.servicios.ProductoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RequiredArgsConstructor
@Controller
@RequestMapping("/public")
public class ZonaPublicaController {

    private final ProductoService productoService;
    private final ActividadService actividadService;

    @ModelAttribute("productos")
    public List<Producto> todosLosProductos() {
        return productoService.findAll();
    }

    @ModelAttribute("actividades")
    public List<Actividad> todasLasActividades() {
        return actividadService.findAll();
    }


    // Tiene Productos y servicios
    @GetMapping({"/", "/index"})
    public String index(Model model, @RequestParam(name="q", required=false) String query) {
        if (query != null) {
            model.addAttribute("productos", productoService.buscar(query));
        } else {
            List<Producto> topProductos = productoService.findTop3();
            model.addAttribute("topProductos", topProductos);
            List<Actividad> topActividades= actividadService.findTop3();
            model.addAttribute("topActividades", topActividades);
        }

        return "index";
    }

    @GetMapping("/producto/{id}")
    public String showProduct(Model model, @PathVariable Long id) {
        Producto result = productoService.findById(id);
        if (result != null) {
            model.addAttribute("producto", result);
            return "producto";
        }
        return "redirect:/public";
    }


    @GetMapping("/actividad/{id}")
    public String mostrarActividades(Model model, @PathVariable Long id)  {
        Actividad result = actividadService.findById(id);
        if (result != null) {
            model.addAttribute("actividad", result);
            return "actividad";
        }
        return "redirect:/public";
    }
}
