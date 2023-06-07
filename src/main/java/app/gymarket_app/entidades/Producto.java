package app.gymarket_app.entidades;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;
import org.hibernate.validator.constraints.URL;


@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@ToString
@Entity
public class Producto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @NotEmpty(message = "El nombre no puede estar vacío")
    private String nombre;

    @NotBlank(message = "La descripción es obligatoria")
    private String descripcion;

    @DecimalMin(value = "0.0", inclusive = false, message = "El PVP debe ser mayor que 0")
    private float pvp;

    @URL
    private String imagen;

    @ManyToOne
    private Usuario usuario;

    @ManyToOne
    private Compra compra;


}
