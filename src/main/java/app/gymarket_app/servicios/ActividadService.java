package app.gymarket_app.servicios;

import app.gymarket_app.entidades.*;
import app.gymarket_app.repositorios.ActividadRepository;
import app.gymarket_app.upload.StorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.Collections;
import java.util.List;
@RequiredArgsConstructor
@Service
public class ActividadService {
    private final ActividadRepository repositorio;
    private final StorageService storageService;

    public List<Actividad> findAll() {
        return repositorio.findAll();
    }
//Busca por nombre actividad
    public List<Actividad> buscador(String filtro) {
        return repositorio.findByNombreContainsIgnoreCase(filtro);
    }

    //Guarda Actividad
    public Actividad save(Actividad a) {
        return repositorio.save(a);
    }

    //Borrar Producto
    public void delete(Actividad a) {
        if (!a.getImagen().isEmpty())
            storageService.delete(a.getImagen());
        repositorio.delete(a);
    }

    //Buscar por ID
    public Actividad findById(Long id) {
        return repositorio.findById(id).orElse(null);
    }


    //Editar
    public Actividad edit(Actividad a) {
        return repositorio.save(a);
    }

    public List<Actividad> actividadDeReserva(Reserva r) {
        return repositorio.findByReserva(r);
    }

    public List<Actividad> variosPorId(List<Long> ids) {
        return repositorio.findAllById(ids);
    }

    public List<Actividad> search(String nombre, Long id, String dificultad, String objetivo, String dias, String horaActividad) {
        // Implementa la lógica de búsqueda según los criterios proporcionados
        // Puedes utilizar el repositorio JPA y sus métodos findByXxx() para buscar por cada campo

        if (nombre != null && !nombre.isEmpty()) {
            return repositorio.findByNombre(nombre);
        } else if (id != null) {
            return repositorio.findById(id).map(Collections::singletonList).orElse(Collections.emptyList());
        } else if (dificultad != null && !dificultad.isEmpty()) {
            return repositorio.findByDificultad(dificultad);
        } else if (objetivo != null && !objetivo.isEmpty()) {
            return repositorio.findByObjetivo(objetivo);
        } else if (dias != null && !dias.isEmpty()) {
            return repositorio.findByDias(dias);
        } else if (horaActividad != null && !horaActividad.isEmpty()) {
            return repositorio.findByHoraActividad(horaActividad);
        }

        // Si no se proporciona ningún criterio de búsqueda, devuelve todas las actividades
        return repositorio.findAll();
    }

    public List<Actividad> findTop3() {
        // Utilizamos el método findAll() de Spring Data JPA con una instancia de PageRequest para obtener los tres primeros registros
        Pageable pageable = PageRequest.of(0, 3);
        Page<Actividad> page = repositorio.findAll(pageable);

        // Devolvemos la lista de productos obtenidos de la página
        return page.getContent();
    }

    public List<Actividad> buscarMisActividades(String query, Usuario u) {
        return repositorio.findByNombreContainsIgnoreCaseAndUsuario(query,u);
    }

    public List<Actividad> actividadesDeUnaReserva(Reserva r) {
        return repositorio.findByReserva(r);
    }


}