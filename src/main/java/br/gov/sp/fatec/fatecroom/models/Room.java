package br.gov.sp.fatec.fatecroom.models;

import java.util.List;
import java.util.Map;

public class Room {
    private Room() {}

    public static final String ID_FIELD = "id_sala";
    public static final String NAME_FIELD = "nome_sala";
    public static final String ROOM_TYPE_FIELD = "tipo_sala";
    public static final String CAPACITY_FIELD = "capacidade";
    public static final String BLOCK_FIELD = "bloco";
    public static final String OBSERVATION_FIELD = "observacao";

    public static final String LAB_TYPE = "LABORATORIO";
    public static final String CLASSROOM_TYPE = "SALA_DE_AULA";
    public static final String AUDITORIUM_TYPE = "AUDITORIO";
    public static final List<String> VALID_ROOM_TYPES = List.of(
        LAB_TYPE,
        CLASSROOM_TYPE,
        AUDITORIUM_TYPE
    );

    private static final int MIN_NAME_LENGTH = 3;
    private static final int MAX_NAME_LENGTH = 100;
    private static final int MIN_CAPACITY = 1;
    private static final int MAX_CAPACITY = 500;

    /**
     * Validates room data from a map.
     *
     * @param roomData Map containing room data to validate.
     * @throws IllegalArgumentException if any validation rule is violated.
     */
    public static void validate(Map<String, String> roomData) throws IllegalArgumentException {
        if (roomData.containsKey(ID_FIELD)) {
            if (roomData.get(ID_FIELD).isEmpty())
                throw new IllegalArgumentException("ID da sala não pode estar vazio.");
            else if (!roomData.get(ID_FIELD).matches("\\d+"))
                throw new IllegalArgumentException("ID da sala deve ser um número inteiro positivo.");
        }

        if (!roomData.containsKey(NAME_FIELD))
            throw new IllegalArgumentException("Nome da sala é obrigatório.");
        else if (roomData.get(NAME_FIELD).length() < MIN_NAME_LENGTH || roomData.get(NAME_FIELD).length() > MAX_NAME_LENGTH)
            throw new IllegalArgumentException("Nome da sala deve ter entre %d e %d caracteres.".formatted(MIN_NAME_LENGTH, MAX_NAME_LENGTH));

        if (!roomData.containsKey(ROOM_TYPE_FIELD))
            throw new IllegalArgumentException("Tipo da sala é obrigatório.");
        else if (!VALID_ROOM_TYPES.contains(roomData.get(ROOM_TYPE_FIELD)))
            throw new IllegalArgumentException("Tipo da sala inválido. Tipos válidos: %s.".formatted(String.join(", ", VALID_ROOM_TYPES)));

        if (!roomData.containsKey(CAPACITY_FIELD))
            throw new IllegalArgumentException("Capacidade da sala é obrigatória.");
        else {
            try {
                int capacity = Integer.parseInt(roomData.get(CAPACITY_FIELD));
                if (capacity < MIN_CAPACITY || capacity > MAX_CAPACITY)
                    throw new IllegalArgumentException("Capacidade da sala deve estar entre %d e %d.".formatted(MIN_CAPACITY, MAX_CAPACITY));
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Capacidade da sala deve ser um número inteiro válido.");
            }
        }

        if (!roomData.containsKey(BLOCK_FIELD))
            throw new IllegalArgumentException("Bloco da sala é obrigatório.");
        else if (roomData.get(BLOCK_FIELD).isEmpty())
            throw new IllegalArgumentException("Bloco da sala não pode estar vazio.");

        // Optional observation length check
        if (roomData.containsKey(OBSERVATION_FIELD)) {
            var obs = roomData.get(OBSERVATION_FIELD);
            if (obs.length() > 500)
                throw new IllegalArgumentException("Observação da sala deve ter até %d caracteres.".formatted(500));
        }
    }
}
