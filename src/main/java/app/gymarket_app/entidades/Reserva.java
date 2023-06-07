package app.gymarket_app.entidades;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@ToString
@Entity

public class Reserva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Usuario usuario;

    private LocalDateTime fechaReserva;

    public void setFechaReserva() {
        this.fechaReserva = LocalDateTime.now();
    }

    // Constructor, getters y setters, etc.
}