package app.gymarket_app.controladores;


import app.gymarket_app.entidades.Usuario;
import app.gymarket_app.servicios.UsuarioServicio;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;



@Slf4j
@RequiredArgsConstructor
@Controller
public class LoginController {

    private final UsuarioServicio usuarioServicio;


// Si accden direcatmante a la raiz, lo enviamos a la ruta public(listado de libros)
    @GetMapping("/")
    public String welcome(){
        return "redirect:/public/";
    }

    // Show de form login

    // Mostrar formulario de registro de usuario
    @GetMapping("/auth/register")
    public String mostrarFormularioRegistro(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "registro";
    }

    @PostMapping("/auth/login")
    public String iniciarSesion(@ModelAttribute Usuario usuario) {
        // Lógica para manejar la autenticación y el inicio de sesión
        // ...

        // Redirigir a la página principal después de iniciar sesión exitosamente
        return "redirect:/";
    }
    // Registrar un nuevo usuario
    @PostMapping("/auth/register")
    public String registrarUsuario( @Valid @ModelAttribute Usuario usuario, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            // Manejar los errores de validación
            return "registro";
        }
        usuarioServicio.save(usuario);
        return "redirect:/auth/login";
    }

    // Mostrar formulario de inicio de sesión
    @GetMapping("/auth/login")
    public String mostrarFormularioLogin(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "login";
    }
    }

