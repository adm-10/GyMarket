package app.gymarket_app.controladores;

import app.gymarket_app.entidades.Actividad;


import app.gymarket_app.entidades.Producto;
import app.gymarket_app.entidades.Usuario;
import app.gymarket_app.servicios.ActividadService;
import app.gymarket_app.upload.StorageService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
public class ActividadController {

    private Usuario usuario;

    private final HttpSession session;
    private final ActividadService actividadService;
    private final StorageService storageService;
    //le puse el final


    @ModelAttribute("listaActividades")
    public List<Actividad> listaActividades() {

        return actividadService.findAll();
    }

    @GetMapping("/list")
    public String listado(@RequestParam(name="q", required=false) String query, Model model) {
        List<Actividad> actividades;

        if (query != null) {
            actividades = actividadService.buscarMisActividades(query, usuario);
        } else {
            actividades = actividadService.findAll();
        }

        model.addAttribute("listaActividades",actividades);
        return "app/actividad/list-actividades";
    }


    @GetMapping("/admin-actividades")
    public String actividadesAdmin(@RequestParam(name="q", required=false) String query, Model model) {
        model.addAttribute("listaActividades", (query == null) ? actividadService.findAll() : actividadService.buscador(query));
        return "app/actividad/actividades-admin";
    }


    @GetMapping("/actividad/new")
    public String nuevaActividadForm(Model model) {
        model.addAttribute("actividad", new Actividad());
        return "app/actividad/form-actividad";

    }

    @PostMapping("/actividad/new/submit")
    public String nuevaActividadSubmit(@Valid @ModelAttribute Actividad actividad, BindingResult bindingResult,
                                       @RequestParam("file") MultipartFile file) {
        // Verificar si hay errores de validación
        if (bindingResult.hasErrors()) {
            // Mostrar los errores en el formulario
            return "app/actividad/form-actividad";
        }
            if (!file.isEmpty()) {
                String imagen = storageService.store(file);
                actividad.setImagen(MvcUriComponentsBuilder
                        .fromMethodName(FilesController.class, "serveFile", imagen).build().toUriString());
            }
            actividadService.save(actividad);
            return "redirect:/app/admin-actividades";

    }



    @GetMapping("/list/delete/{id}")
    public String deleteActividad(@PathVariable Long id) {
        Actividad a = actividadService.findById(id);
        actividadService.delete(a);
        return "redirect:/app/admin-actividades";
    }



    @GetMapping("/actividad/edit/{id}")
    public String editarActividad(@PathVariable Long id, Model model) {

        Actividad actividad = actividadService.findById(id);
        if (actividad != null) {
            model.addAttribute("actividad", actividad);
            return "app/actividad/form-actividad";
        } else {
            return "redirect:/app/list";
        }
    }

    @PostMapping("/actividad/edit/submit")
    public String editarActividadSubmit(@Valid @ModelAttribute Actividad actividad,BindingResult bindingResult,
                                       @RequestParam("file") MultipartFile file )  {
        // Verificar si hay errores de validación
        if (bindingResult.hasErrors()) {
            // Manejar los errores de validación según tus necesidades
            // Puedes redirigir a una página de error o mostrar los errores en el formulario
            return "app/actividad/form-actividad";
        }

        if (!file.isEmpty()) {
            String imagen = storageService.store(file);
            actividad.setImagen(MvcUriComponentsBuilder
                    .fromMethodName(FilesController.class, "serveFile", imagen).build().toUriString());
        }
        actividadService.edit(actividad);
        return "redirect:/app/admin-actividades";

    }


    @GetMapping("/search")
    public String buscar(Model model){
        model.addAttribute("actividad", new Actividad());
        return "app/actividad/search-form";
    }

    @PostMapping("/search/list")
    public String list(@ModelAttribute("actividad") Actividad actividad, Model model) {
        List<Actividad> actividadList = actividadService.search(
                actividad.getNombre(), actividad.getId(), actividad.getDificultad(),
                actividad.getObjetivo(), actividad.getDias(), actividad.getHoraActividad()
        );
        model.addAttribute("listaSearch", actividadList);
        model.addAttribute("idActividad", actividad.getId()); // Agrega la ID de la actividad al modelo
        return "app/actividad/search-list";
    }


}
