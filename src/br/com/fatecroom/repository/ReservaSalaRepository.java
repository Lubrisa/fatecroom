package br.com.fatecroom.repository;

import br.com.fatecroom.model.ReservaSala;

import java.util.ArrayList;
import java.util.List;

/**
 * STUB TEMPORÁRIO para permitir compilar e testar a UI.
 * Seu colega depois pode substituir por uma implementação real com CSV.
 */
public class ReservaSalaRepository {

    private final List<ReservaSala> reservas = new ArrayList<>();
    private int nextId = 1;

    public List<ReservaSala> findAll() {
        return new ArrayList<>(reservas);
    }

    public List<ReservaSala> findBySalaAndData(int idSala, String data) {
        List<ReservaSala> list = new ArrayList<>();
        for (ReservaSala r : reservas) {
            if (r.getIdSala() == idSala && data.equals(r.getDataReserva())) {
                list.add(r);
            }
        }
        return list;
    }

    public int getNextId() {
        return nextId++;
    }

    public void save(ReservaSala reserva) {
        if (reserva.getIdReserva() == 0) {
            reserva.setIdReserva(getNextId());
        }
        reservas.add(reserva);
    }
}
