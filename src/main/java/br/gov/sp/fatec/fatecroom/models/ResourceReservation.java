package br.gov.sp.fatec.fatecroom.models;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;

public class ResourceReservation {
    private ResourceReservation() {}

    public static final String ID_FIELD = "id_reserva_recurso";
    public static final String RESOURCE_ID_FIELD = "id_recurso";
    public static final String USER_ID_FIELD = "id_usuario";
    public static final String DATE_FIELD = "data_reserva"; // YYYY-MM-DD
    public static final String START_TIME_FIELD = "hora_inicio"; // HH:MM
    public static final String END_TIME_FIELD = "hora_fim"; // HH:MM
    public static final String STATUS_FIELD = "status";
    public static final String OBSERVATION_FIELD = "observacao";

    public static final String ACTIVE_STATUS = "ATIVA";
    public static final String CANCELLED_STATUS = "CANCELADA";
    public static final List<String> VALID_STATUSES = List.of(
        ACTIVE_STATUS,
        CANCELLED_STATUS
    );

    /**
     * Validates resource reservation data from a map.
     *
     * @param reservationData Map containing reservation data to validate.
     * @throws IllegalArgumentException if any validation rule is violated.
     */
    public static void validate(Map<String, String> reservationData) throws IllegalArgumentException {
        if (reservationData.containsKey(ID_FIELD)) {
            if (reservationData.get(ID_FIELD).isEmpty())
                throw new IllegalArgumentException("ID da reserva de recurso não pode estar vazio.");
            else if (!reservationData.get(ID_FIELD).matches("\\d+"))
                throw new IllegalArgumentException("ID da reserva de recurso deve ser um número inteiro positivo.");
        }

        if (!reservationData.containsKey(RESOURCE_ID_FIELD))
            throw new IllegalArgumentException("ID do recurso é obrigatório.");
        else if (!reservationData.get(RESOURCE_ID_FIELD).matches("\\d+"))
            throw new IllegalArgumentException("ID do recurso deve ser um número inteiro positivo.");

        if (!reservationData.containsKey(USER_ID_FIELD))
            throw new IllegalArgumentException("ID do usuário é obrigatório.");
        else if (!reservationData.get(USER_ID_FIELD).matches("\\d+"))
            throw new IllegalArgumentException("ID do usuário deve ser um número inteiro positivo.");

        if (!reservationData.containsKey(DATE_FIELD))
            throw new IllegalArgumentException("Data da reserva é obrigatória.");
        else {
            try {
                LocalDate.parse(reservationData.get(DATE_FIELD), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            } catch (DateTimeParseException e) {
                throw new IllegalArgumentException("Data da reserva deve estar no formato yyyy-MM-dd.");
            }
        }

        LocalTime startTime;
        if (!reservationData.containsKey(START_TIME_FIELD))
            throw new IllegalArgumentException("Hora de início é obrigatória.");
        else {
            try {
                startTime = LocalTime.parse(reservationData.get(START_TIME_FIELD), DateTimeFormatter.ofPattern("HH:mm"));
            } catch (DateTimeParseException e) {
                throw new IllegalArgumentException("Hora de início deve estar no formato HH:mm.");
            }
        }

        LocalTime endTime;
        if (!reservationData.containsKey(END_TIME_FIELD))
            throw new IllegalArgumentException("Hora de fim é obrigatória.");
        else {
            try {
                endTime = LocalTime.parse(reservationData.get(END_TIME_FIELD), DateTimeFormatter.ofPattern("HH:mm"));
            } catch (DateTimeParseException e) {
                throw new IllegalArgumentException("Hora de fim deve estar no formato HH:mm.");
            }
        }

        if (endTime.isBefore(startTime) || endTime.equals(startTime)) {
            throw new IllegalArgumentException("Hora de fim não pode ser anterior ou igual à hora de início.");
        }

        if (!reservationData.containsKey(STATUS_FIELD))
            throw new IllegalArgumentException("Status da reserva de recurso é obrigatório.");
        else if (!VALID_STATUSES.contains(reservationData.get(STATUS_FIELD)))
            throw new IllegalArgumentException("Status da reserva inválido. Status válidos: %s.".formatted(String.join(", ", VALID_STATUSES)));

        // Optional observation length check
        if (reservationData.containsKey(OBSERVATION_FIELD)) {
            var obs = reservationData.get(OBSERVATION_FIELD);
            if (obs.length() > 500)
                throw new IllegalArgumentException("Observação da reserva de recurso deve ter até %d caracteres.".formatted(500));
        }
    }
}
