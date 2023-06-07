package app.gymarket_app.repositorios;

import app.gymarket_app.entidades.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ActividadRepository  extends JpaRepository<Actividad, Long> {

    List<Actividad> findByNombreContainsIgnoreCase(String filtro);

    List<Actividad> findByReserva(Reserva reserva);

    @Query("SELECT a FROM Actividad a WHERE (:nombre is null or lower(a.nombre) like %:nombre%) " +
            "and ((:id is null) or (a.id = :id)) " +
            "and (:dificultad is null or a.dificultad = :dificultad) " +
            "and (:objetivo is null or a.objetivo = :objetivo) " +
            "and (:dias is null or a.dias = :dias) " +
            "and (:horaActividad is null or a.horaActividad = :horaActividad)")
    List<Actividad> searchActividad(@Param("nombre") String nombre,
                                    @Param("id") Long id,
                                    @Param("dificultad") String dificultad,
                                    @Param("objetivo") String objetivo,
                                    @Param("dias") String dias,
                                    @Param("horaActividad") String horaActividad);



    List<Actividad> findByNombre(String nombre);
    List<Actividad> findByDificultad(String dificultad);
    List<Actividad> findByObjetivo(String objetivo);
    List<Actividad> findByDias(String dias);
    List<Actividad> findByHoraActividad(String horaActividad);

    List<Actividad> findByNombreContainsIgnoreCaseAndUsuario(String nombre, Usuario usuario);

}