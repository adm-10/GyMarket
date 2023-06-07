package app.gymarket_app.controladores;

import app.gymarket_app.entidades.*;
import app.gymarket_app.servicios.ActividadService;
import app.gymarket_app.servicios.ReservaService;
import app.gymarket_app.servicios.UsuarioServicio;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.util.ByteArrayDataSource;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.itextpdf.text.pdf.PdfWriter;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.io.ByteArrayOutputStream;


import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Controller
@RequestMapping("/app")
public class ReservaController {

    private final HttpSession session;
    private Usuario usuario;
    private final UsuarioServicio usuarioServicio;
    @Autowired
    private JavaMailSender javaMailSender;

    private final ActividadService actividadService;

    private final ReservaService reservaService;



    @ModelAttribute("carritoreserva")
    public List<Actividad> actividadesCarrito() {
        List<Long> contenido = (List<Long>) session.getAttribute("carritoreserva");
        return (contenido == null) ? null : actividadService.variosPorId(contenido);
    }


    @ModelAttribute("mis_reservas")
    public List<Reserva> misReservas() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        usuario = usuarioServicio.buscarPorUsername(username);
        return reservaService.porUsuario(usuario);
    }

    @GetMapping("/carritoreserva")
    public String viewCarrito(Model model) {
        return "app/reserva/carritoreserva";
    }

    @GetMapping("/misreservas")
    public String verMisReservas(Model model) {

        return "/app/reserva/list";
    }


    @GetMapping("/carritoreserva/add/{id}")
    public String agregarCarrito(Model model, @PathVariable Long id) {
        List<Long> contenido = (List<Long>) session.getAttribute("carritoreserva");
        if (contenido == null)
            contenido = new ArrayList<>();
        if (!contenido.contains(id))
            contenido.add(id);
        session.setAttribute("carritoreserva", contenido);
        return "redirect:/app/carritoreserva";
    }

    @GetMapping("/carritoreserva/eliminar/{id}")
    public String quitarDeCarrito(Model model, @PathVariable Long id) {
        List<Long> contenido = (List<Long>) session.getAttribute("carritoreserva");
        if (contenido == null)
            return "redirect:/public";
        contenido.remove(id);
        if (contenido.isEmpty())
            session.removeAttribute("carritoreserva");
        else
            session.setAttribute("carritoreserva", contenido);
        return "redirect:/app/carritoreserva";

    }
    //Busqueda actividad






    @GetMapping("/carritoreserva/finalizar")
    public String reservarActividad() {
        List<Long> contenido = (List<Long>) session.getAttribute("carritoreserva");
        if (contenido == null)
            return "redirect:/public";

        List<Actividad> actividades = actividadesCarrito();

        Reserva reserva = reservaService.insertar(new Reserva(), usuario);

        actividades.forEach(a -> reservaService.addActividadReserva(a, reserva));
        session.removeAttribute("carrito");

        return "redirect:/app/reserva/confirmacion/" + reserva.getId();
    }

    @GetMapping("/reserva/confirmacion/{id}")
    public String confirmacion(Model model, @PathVariable Long id) {
        Reserva reserva = reservaService.buscarPorId(id);
        List<Actividad> actividades = actividadService.actividadDeReserva(reserva);
        model.addAttribute("actividades", actividades);
        model.addAttribute("reserva", reserva);
        return "/app/reserva/confirm-reserva";
    }


    @GetMapping("/reserva/confirmacion/{id}/pdf")
    public ResponseEntity<byte[]> descargarConfirmacionPDF(@PathVariable Long id) {
        byte[] pdfContent = generarContenidoPDF(id);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "confirmacion.pdf");
        headers.setContentLength(pdfContent.length);

        return new ResponseEntity<>(pdfContent, headers, HttpStatus.OK);
    }
    private byte[] generarContenidoPDF(Long id) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Document document = new Document();

        try {
            PdfWriter writer = PdfWriter.getInstance(document, outputStream);
            document.open();

            // Obtener la reserva y las actividades
            Reserva reserva = reservaService.buscarPorId(id);
            List<Actividad> actividades = actividadService.actividadDeReserva(reserva);

            // Crear la tabla
            PdfPTable table = new PdfPTable(6);
            table.setWidthPercentage(100);
            table.setSpacingBefore(10f);
            table.setSpacingAfter(10f);

            // Establecer colores de encabezado y contenido
            BaseColor headerColor = new BaseColor(51, 102, 204); // Azul claro
            BaseColor contentColor = BaseColor.WHITE;

            // Agregar encabezados de la tabla
            addTableCell(table, "Nombre", headerColor);
            addTableCell(table, "Dificultad", headerColor);
            addTableCell(table, "Objetivo", headerColor);
            addTableCell(table, "Descripción", headerColor);
            addTableCell(table, "Días", headerColor);
            addTableCell(table, "Hora de la actividad", headerColor);

            // Agregar filas con los detalles de las actividades
            for (Actividad actividad : actividades) {
                addTableCell(table, actividad.getNombre(), contentColor);
                addTableCell(table, actividad.getDificultad(), contentColor);
                addTableCell(table, actividad.getObjetivo(), contentColor);
                addTableCell(table, actividad.getDescripcion(), contentColor);
                addTableCell(table, actividad.getDias().toString(), contentColor);
                addTableCell(table, actividad.getHoraActividad().toString(), contentColor);
            }

            // Agregar el texto de agradecimiento
            Paragraph agradecimiento = new Paragraph("Gracias por tu reserva. Aquí tienes los detalles de tu reserva:");
            agradecimiento.setAlignment(Element.ALIGN_CENTER);
            agradecimiento.setSpacingAfter(10f);
            document.add(agradecimiento);

            // Agregar los detalles de la reserva
            Paragraph detalleReserva = new Paragraph("ID de reserva: " + reserva.getId());
            detalleReserva.setSpacingAfter(10f);
            document.add(detalleReserva);

            // Agregar la tabla al documento
            document.add(table);

            document.close();
            writer.close(); // Cerrar el writer para asegurar que los cambios se escriban en el outputStream
        } catch (DocumentException e) {
            e.printStackTrace();
        }

        return outputStream.toByteArray();
    }

    private void addTableCell(PdfPTable table, String content, BaseColor color) {
        PdfPCell cell = new PdfPCell(new Phrase(content));
        cell.setBackgroundColor(color);
        cell.setPadding(5);
        table.addCell(cell);
    }



    @Controller
    public class CorreoEnviadoReservaController {

        @GetMapping("/correo-reserva")
        public String mostrarCorreoEnviado() {
            return "correo-enviado";
        }

    }


    @GetMapping("/reserva/delete/{id}")
    public String deleteReserva(@PathVariable Long id) {
        Reserva r = reservaService.findById(id);
        reservaService.delete(r);
        return "redirect:/borrar-reserva";
    }


    @Controller
    public class CancelacionReserva {

        @GetMapping("/borrar-reserva")
        public String mostrarCorreoEnviado() {
            return "/app/reserva/borrar-reserva";
        }

    }


}


