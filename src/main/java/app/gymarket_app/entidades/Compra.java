package app.gymarket_app.entidades;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;


@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@ToString
@Entity
public class Compra {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @CreatedDate
    private LocalDateTime fechaCompra;

    @ManyToOne
    private Usuario usuario;

    public void setFechaCompra() {
        this.fechaCompra = LocalDateTime.now();
    }
}