package br.gov.sp.fatec.fatecroom.persistence;

import br.gov.sp.fatec.fatecroom.models.ResourceReservation;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class ResourceReservationsRepository {
    private static final String FILE_NAME = "reservas_recursos.csv";

    private static int sequence = 1;

    /**
     * Inserts a new resource reservation into the repository.
     *
     * @param reservationData A map containing the reservation data.
     * @return The inserted reservation data with the generated ID.
     * @throws IllegalArgumentException If the reservation data is invalid.
     * @throws IOException If an I/O error occurs during the operation.
     */
    public static Map<String, String> insert(Map<String, String> reservationData) throws IllegalArgumentException, IOException {
        if (reservationData == null) {
            throw new IllegalArgumentException("Dados da reserva de recurso não podem ser nulos.");
        } else if (reservationData.containsKey(ResourceReservation.ID_FIELD)) {
            throw new IllegalArgumentException("ID da reserva de recurso não deve ser fornecido na inserção.");
        }

        ResourceReservation.validate(reservationData);

        Map<String, String> reservationDataWithId = new java.util.HashMap<>(reservationData);
        reservationDataWithId.put(ResourceReservation.ID_FIELD, String.valueOf(sequence++));
        Repository.insertEnsuringUniqueness(FILE_NAME, reservationDataWithId, reservation -> reservation.get(ResourceReservation.ID_FIELD));

        return reservationDataWithId;
    }

    /**
     * Updates an existing resource reservation.
     *
     * @param reservationData A map containing the reservation data (must include the ID).
     * @throws IllegalArgumentException If the reservation data is invalid.
     * @throws IOException If an I/O error occurs during the operation.
     */
    public static void update(Map<String, String> reservationData) throws IllegalArgumentException, IOException {
        if (reservationData == null) {
            throw new IllegalArgumentException("Dados da reserva de recurso não podem ser nulos.");
        } else if (!reservationData.containsKey(ResourceReservation.ID_FIELD)) {
            throw new IllegalArgumentException("ID da reserva de recurso é obrigatório para atualização.");
        }

        ResourceReservation.validate(reservationData);

        Repository.insertOrUpdate(FILE_NAME, reservationData, reservation -> reservation.get(ResourceReservation.ID_FIELD));
    }

    /**
     * Deletes a resource reservation by id.
     *
     * @param reservationId the id of the reservation to delete
     * @return true if an entry was found and deleted, false otherwise
     * @throws IllegalArgumentException if the id is null or blank
     * @throws IOException if an I/O error occurs while modifying storage
     */
    public static boolean delete(String reservationId) throws IllegalArgumentException, IOException {
        if (reservationId == null || reservationId.isBlank()) {
            throw new IllegalArgumentException("ID da reserva de recurso não pode ser nulo ou vazio.");
        }

        return Repository.delete(FILE_NAME, reservationId, reservation -> reservation.get(ResourceReservation.ID_FIELD));
    }

    /**
     * Returns a paginated range of resource reservations.
     *
     * @param skip number of entries to skip from the beginning
     *     * @param take maximum number of entries to return
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
