package app.gymarket_app.entidades;

import jakarta.persistence.*;
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

public class Actividad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @NotEmpty(message = "El nombre no puede estar vacío")
    private String nombre;

    @NotEmpty(message = "La dificultad no puede estar vacía")
    private String dificultad;

    @NotEmpty(message = "El objetivo no puede estar vacío")
    private String objetivo;

    @NotBlank(message = "La descripción es obligatoria")
    private String descripcion;

    @NotEmpty
    private String dias;

    @NotEmpty
    private String horaActividad;

    @URL
    private String imagen;

    @ManyToOne
    private Usuario usuario;

    @ManyToOne
    private Reserva reserva;


}