package app.gymarket_app.servicios;

import app.gymarket_app.entidades.*;
import app.gymarket_app.repositorios.ReservaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
@RequiredArgsConstructor
@Service
public class ReservaService {

    private final ReservaRepository repositorio;

    private final ActividadService actividadService;


    public List<Reserva> porUsuario(Usuario u) {
        return repositorio.findByUsuario(u);
    }


    public Reserva save(Reserva r) {
        return repositorio.save(r);
    }

    public Reserva edit(Reserva r) {
        return repositorio.save(r);
    }
    public void delete(Reserva r) {
        repositorio.delete(r);
    }

    public Reserva findById(Long id) {
        return repositorio.findById(id).orElse(null);
    }

    public Reserva insertar(Reserva r, Usuario u) {
        r.setUsuario(u);
        r.setFechaReserva();
        return repositorio.save(r);
    }

    public Actividad addActividadReserva(Actividad a, Reserva r) {
        a.setReserva(r);
        return actividadService.edit(a);
    }

    public Reserva buscarPorId(long id) {
        return repositorio.findById(id).orElse(null);
    }



}
