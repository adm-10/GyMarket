package app.gymarket_app.entidades;

import jakarta.persistence.*;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import java.time.LocalDateTime;
import lombok.Data;


@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@ToString
@Entity
public class Usuario {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotBlank(message = "El nombre es obligatorio")
	private String nombre;

	@Column(nullable = false)
	@NotBlank(message = "El nombre de usuario es obligatorio")
	@Size(max = 10, message = "El nombre de usuario debe tener como máximo 10 caracteres")
	private String username;

	@Email(message = "El formato del correo electrónico es inválido")
	@NotBlank(message = "El correo electrónico es obligatorio")
	@Size(max = 100, message = "El correo electrónico debe tener como máximo 100 caracteres")
	private String email;

	@NotBlank(message = "La contraseña es obligatoria")
	@Size(min = 5, message = "La contraseña debe tener al menos 5 caracteres")
	private String password;

	private String role;

	private LocalDateTime fechaAlta;

	public void setFechaAlta() {
		this.fechaAlta = LocalDateTime.now();
	}


}
