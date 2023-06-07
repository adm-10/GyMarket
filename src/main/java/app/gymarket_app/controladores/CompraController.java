package app.gymarket_app.controladores;

import app.gymarket_app.entidades.Compra;
import app.gymarket_app.entidades.Producto;
import app.gymarket_app.entidades.Usuario;
import app.gymarket_app.servicios.CompraServicio;
import app.gymarket_app.servicios.ProductoService;
import app.gymarket_app.servicios.UsuarioServicio;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.util.ByteArrayDataSource;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.io.ByteArrayOutputStream;



import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Controller
@RequestMapping("/app")
public class CompraController {

    private final HttpSession session;
    private Usuario usuario;
    private final UsuarioServicio usuarioServicio;
    private final CompraServicio compraServicio;
    private final ProductoService productoService;

    @Autowired
    private JavaMailSender javaMailSender;





    @ModelAttribute("carrito")
    public List<Producto> productosCarrito() {
        List<Long> contenido = (List<Long>) session.getAttribute("carrito");
        return (contenido == null) ? null : productoService.variosPorId(contenido);
    }

    @ModelAttribute("total_carrito")
    public Double totalCarrito() {
        List<Producto> productosCarrito = productosCarrito();
        if (productosCarrito != null)
            return productosCarrito.stream()
                    .mapToDouble(p -> p.getPvp())
                    .sum();
        return 0.0;
    }

    @ModelAttribute("mis_compras")
    public List<Compra> misCompras() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        usuario = usuarioServicio.buscarPorUsername(username);
        return compraServicio.porUsuario(usuario);
    }


    @GetMapping("/carrito")
    public String verCarrito(Model model) {
        return "app/compra/carrito";
    }

    @GetMapping("/carrito/add/{id}")
    public String addCarrito(Model model, @PathVariable Long id) {
        List<Long> contenido = (List<Long>) session.getAttribute("carrito");
        if (contenido == null)
            contenido = new ArrayList<>();
        if (!contenido.contains(id))
            contenido.add(id);
        session.setAttribute("carrito", contenido);
        return "redirect:/app/carrito";
    }

    @GetMapping("/carrito/eliminar/{id}")
    public String borrarDeCarrito(Model model, @PathVariable Long id) {
        List<Long> contenido = (List<Long>) session.getAttribute("carrito");
        if (contenido == null)
            return "redirect:/public";
        contenido.remove(id);
        if (contenido.isEmpty())
            session.removeAttribute("carrito");
        else
            session.setAttribute("carrito", contenido);
        return "redirect:/app/carrito";

    }

    @GetMapping("/carrito/finalizar")
    public String checkout() {
        List<Long> contenido = (List<Long>) session.getAttribute("carrito");
        if (contenido == null)
            return "redirect:/public";

        List<Producto> productos = productosCarrito();

        Compra c = compraServicio.insertar(new Compra(), usuario);

        productos.forEach(p -> compraServicio.addProductoCompra(p, c));
        session.removeAttribute("carrito");

        return "redirect:/app/compra/factura/" + c.getId();

    }


    @GetMapping("/miscompras")
    public String verMisCompras(Model model) {
        return "/app/compra/listado";
    }

    @GetMapping("/compra/factura/{id}")
    public String factura(Model model, @PathVariable Long id) {
        Compra c = compraServicio.buscarPorId(id);
        List<Producto> productos = productoService.productosDeUnaCompra(c);
        model.addAttribute("productos", productos);
        model.addAttribute("compra", c);
        model.addAttribute("total_compra", productos.stream().mapToDouble(p -> p.getPvp()).sum());
        return "/app/compra/factura";
    }


    @GetMapping("/compra/factura/{id}/pdf")
    public ResponseEntity<byte[]> descargarFacturaPDF(@PathVariable Long id) {
        byte[] pdfContent = generarContenidoPDF(id);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "factura.pdf");
        headers.setContentLength(pdfContent.length);

        return new ResponseEntity<>(pdfContent, headers, HttpStatus.OK);
    }

    @PostMapping("/compra/factura/{id}/enviar-correo")
    public String enviarCorreoFactura(@PathVariable Long id, @RequestParam("email") String email) {
        try {
            Compra c = compraServicio.buscarPorId(id);
            List<Producto> productos = productoService.productosDeUnaCompra(c);
            byte[] pdfContent = generarContenidoPDF(id);

            // Crear el mensaje de correo
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            // Configurar los detalles del correo
            helper.setTo(email);
            helper.setSubject("Factura adjunta");
            helper.setText("Adjunto encontrarás la factura de tu compra.");

            // Adjuntar el archivo PDF
            ByteArrayDataSource dataSource = new ByteArrayDataSource(pdfContent, "application/pdf");
            helper.addAttachment("factura.pdf", dataSource);

            // Enviar el correo
            javaMailSender.send(message);

            // Redirigir a una página de éxito o mostrar un mensaje de éxito
            return "redirect:/correo-enviado";
        } catch (Exception e) {
            e.printStackTrace();
            // Redirigir a una página de error o mostrar un mensaje de error
            return "redirect:/error-correo";
        }
    }

    @Controller
    public class CorreoEnviadoController {

        @GetMapping("/correo-enviado")
        public String mostrarCorreoEnviado() {
            return "correo-enviado";
        }

    }

    private byte[] generarContenidoPDF(Long id) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Document document = new Document();

        try {
            PdfWriter writer = PdfWriter.getInstance(document, outputStream);
            document.open();

            // Obtener la compra y los productos
            Compra compra = compraServicio.buscarPorId(id);
            List<Producto> productos = productoService.productosDeUnaCompra(compra);

            // Agregar encabezado de la factura
            Paragraph encabezado = new Paragraph("Factura de compra #" + compra.getId());
            encabezado.setAlignment(Element.ALIGN_CENTER);
            document.add(encabezado);

            // Agregar detalles de los productos
            for (Producto producto : productos) {
                Paragraph detalleProducto = new Paragraph(producto.getNombre() + " - Precio: " + producto.getPvp());
                document.add(detalleProducto);
            }

            // Agregar total de la compra
            double totalCompra = productos.stream().mapToDouble(Producto::getPvp).sum();
            Paragraph total = new Paragraph("Total de la compra: " + totalCompra);
            document.add(total);

            document.close();
            writer.close(); // Cerrar el writer para asegurar que los cambios se escriban en el outputStream
        } catch (DocumentException e) {
            e.printStackTrace();
        }

        return outputStream.toByteArray();
    }
}
