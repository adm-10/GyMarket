package app.gymarket_app.config;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@ControllerAdvice
public class CustomErrorController {

    @ExceptionHandler(Exception.class)
    public ModelAndView handleError(HttpServletRequest request, Exception ex) {
        // Lógica para manejar el error y devolver una vista personalizada
        ModelAndView modelAndView = new ModelAndView("error");
        modelAndView.addObject("errorMessage", "Ha ocurrido un error inesperado");
        return modelAndView;
    }

    @RequestMapping("/error")
    public ModelAndView handleError(HttpServletRequest request) {
        // Obtiene el código de estado del error
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);

        // Lógica para manejar diferentes códigos de estado y devolver vistas personalizadas
        ModelAndView modelAndView = new ModelAndView("error");

        if (status != null) {
            Integer statusCode = Integer.valueOf(status.toString());

            if (statusCode == HttpStatus.NOT_FOUND.value()) {
                modelAndView.addObject("errorMessage", "La página solicitada no existe");
            } else if (statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
                modelAndView.addObject("errorMessage", "Ha ocurrido un error interno en el servidor");
            } else {
                modelAndView.addObject("errorMessage", "Ha ocurrido un error");
            }
        } else {
            modelAndView.addObject("errorMessage", "Ha ocurrido un error");
        }

        return modelAndView;
    }
}
