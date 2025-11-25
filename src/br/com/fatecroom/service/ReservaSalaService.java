package br.com.fatecroom.service;

import br.com.fatecroom.model.ReservaSala;
import br.com.fatecroom.repository.ReservaSalaRepository;

import java.util.ArrayList;
import java.util.List;

public class ReservaSalaService {

    private final ReservaSalaRepository repository;

    public ReservaSalaService(ReservaSalaRepository repository) {
        this.repository = repository;
    }

    public boolean isDisponivel(int idSala, String data, String horaInicio, String horaFim) {
        List<ReservaSala> existentes = repository.findBySalaAndData(idSala, data);
        for (ReservaSala r : existentes) {
            if (!"CANCELADA".equalsIgnoreCase(r.getStatus())) {
                if (intervaloConflita(horaInicio, horaFim, r.getHoraInicio(), r.getHoraFim())) {
                    return false;
                }
            }
        }
        return true;
    }

    protected boolean intervaloConflita(String inicio1, String fim1,
                                        String inicio2, String fim2) {
        return inicio1.compareTo(fim2) < 0 && fim1.compareTo(inicio2) > 0;
    }

    public void registrarReserva(ReservaSala reserva) {
        int novoId = repository.getNextId();
        reserva.setIdReserva(novoId);

        if (reserva.getStatus() == null || reserva.getStatus().trim().isEmpty()) {
            reserva.setStatus("PENDENTE");
        }

        repository.save(reserva);
    }

    public List<ReservaSala> buscarReservas(Integer idSala, Integer idUsuario, String data) {
        List<ReservaSala> todas = repository.findAll();
        List<ReservaSala> filtradas = new ArrayList<>();

        for (ReservaSala r : todas) {
            if (idSala != null && r.getIdSala() != idSala) continue;
            if (idUsuario != null && r.getIdUsuario() != idUsuario) continue;
            if (data != null && !data.isEmpty() && !data.equals(r.getDataReserva())) continue;

            filtradas.add(r);
        }
        return filtradas;
    }
}
