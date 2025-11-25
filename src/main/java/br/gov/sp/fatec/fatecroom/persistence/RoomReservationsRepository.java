package br.gov.sp.fatec.fatecroom.persistence;

import br.gov.sp.fatec.fatecroom.models.RoomReservation;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class RoomReservationsRepository {
    private static final String FILE_NAME = "reservas_salas.csv";

    private static int sequence = 1;

    /**
     * Inserts a new room reservation into the repository.
     *
     * @param reservationData A map containing the reservation data.
     * @return The inserted reservation data with the generated ID.
     * @throws IllegalArgumentException If the reservation data is invalid.
     * @throws IOException If an I/O error occurs during the operation.
     */
    public static Map<String, String> insert(Map<String, String> reservationData) throws IllegalArgumentException, IOException {
        if (reservationData == null) {
            throw new IllegalArgumentException("Dados da reserva não podem ser nulos.");
        } else if (reservationData.containsKey(RoomReservation.ID_FIELD)) {
            throw new IllegalArgumentException("ID da reserva não deve ser fornecido na inserção.");
        }

        RoomReservation.validate(reservationData);

        Map<String, String> reservationDataWithId = new java.util.HashMap<>(reservationData);
        reservationDataWithId.put(RoomReservation.ID_FIELD, String.valueOf(sequence++));
        Repository.insertEnsuringUniqueness(FILE_NAME, reservationDataWithId, reservation -> reservation.get(RoomReservation.ID_FIELD));

        return reservationDataWithId;
    }

    /**
     * Updates an existing room reservation.
     *
     * @param reservationData A map containing the reservation data (must include the ID).
     * @throws IllegalArgumentException If the reservation data is invalid.
     * @throws IOException If an I/O error occurs during the operation.
     */
    public static void update(Map<String, String> reservationData) throws IllegalArgumentException, IOException {
        if (reservationData == null) {
            throw new IllegalArgumentException("Dados da reserva não podem ser nulos.");
        } else if (!reservationData.containsKey(RoomReservation.ID_FIELD)) {
            throw new IllegalArgumentException("ID da reserva é obrigatório para atualização.");
        }

        RoomReservation.validate(reservationData);

        Repository.insertOrUpdate(FILE_NAME, reservationData, reservation -> reservation.get(RoomReservation.ID_FIELD));
    }
    
    /**
     * Deletes a room reservation by id.
     *
     * @param reservationId the id of the reservation to delete
     * @return true if an entry was found and deleted, false otherwise
     * @throws IllegalArgumentException if the id is null or blank
     * @throws IOException if an I/O error occurs while modifying storage
     */
    public static boolean delete(String reservationId) throws IllegalArgumentException, IOException {
        if (reservationId == null || reservationId.isBlank()) {
            throw new IllegalArgumentException("ID da reserva não pode ser nulo ou vazio.");
        }

        return Repository.delete(FILE_NAME, reservationId, reservation -> reservation.get(RoomReservation.ID_FIELD));
    }
    
    /**
     * Returns a paginated range of room reservations.
     *
     * @param skip number of entries to skip from the beginning
     * @param take maximum number of entries to return
     * @return list of reservation maps (each map represents a reservation row)
     * @throws IOException if an I/O error occurs while reading storage
     */
    public static List<Map<String, String>> getRange(int skip, int take) throws IOException {
        return Repository.getRange(
            FILE_NAME,
            skip,
            take
        );
    }
}
