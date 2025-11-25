package br.com.fatecroom.service;

import br.com.fatecroom.model.ReservaSala;

import java.util.ArrayList;
import java.util.List;

public class ReservaSalaServiceMock extends ReservaSalaService {

    private final List<ReservaSala> reservas = new ArrayList<>();
    private int nextId = 1;

    public ReservaSalaServiceMock() {
        super(null); // não usamos o repository do pai aqui

        ReservaSala r1 = new ReservaSala(
                nextId++, 101, 1,
                "2025-11-23", "19:00", "21:00",
                "CONFIRMADA", "Aula de POO"
        );
        ReservaSala r2 = new ReservaSala(
                nextId++, 102, 2,
                "2025-11-23", "18:00", "19:30",
                "PENDENTE", "Apresentação de projeto"
        );
        ReservaSala r3 = new ReservaSala(
                nextId++, 101, 3,
                "2025-11-24", "08:00", "10:00",
                "CONFIRMADA", "Laboratório de Redes"
        );

        reservas.add(r1);
        reservas.add(r2);
        reservas.add(r3);
    }

    @Override
    public boolean isDisponivel(int idSala, String data, String horaInicio, String horaFim) {
        for (ReservaSala r : reservas) {
            if (r.getIdSala() == idSala &&
                    data.equals(r.getDataReserva()) &&
                    !"CANCELADA".equalsIgnoreCase(r.getStatus())) {

                if (intervaloConflita(horaInicio, horaFim, r.getHoraInicio(), r.getHoraFim())) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void registrarReserva(ReservaSala reserva) {
        reserva.setIdReserva(nextId++);
        if (reserva.getStatus() == null || reserva.getStatus().trim().isEmpty()) {
            reserva.setStatus("PENDENTE");
        }
        reservas.add(reserva);
    }

    @Override
    public List<ReservaSala> buscarReservas(Integer idSala, Integer idUsuario, String data) {
        List<ReservaSala> filtradas = new ArrayList<>();

        for (ReservaSala r : reservas) {
            if (idSala != null && r.getIdSala() != idSala) continue;
            if (idUsuario != null && r.getIdUsuario() != idUsuario) continue;
            if (data != null && !data.isEmpty() && !data.equals(r.getDataReserva())) continue;

            filtradas.add(r);
        }

        return filtradas;
    }
}
