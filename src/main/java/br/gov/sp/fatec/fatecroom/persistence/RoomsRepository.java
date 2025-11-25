package br.gov.sp.fatec.fatecroom.persistence;

import br.gov.sp.fatec.fatecroom.models.Room;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class RoomsRepository {
    private static final String FILE_NAME = "salas.csv";

    private static int sequence = 1;

    /**
     * Inserts a new room into the repository.
     * 
     * @param roomData A map containing the room data.
     * @return The inserted room data with the generated ID.
     * @throws IllegalArgumentException If the room data is invalid.
     * @throws IOException If an I/O error occurs during the operation.
     */
    public static Map<String, String> insert(Map<String, String> roomData) throws IllegalArgumentException, IOException {

        if (roomData == null) {
            throw new IllegalArgumentException("Dados da sala não podem ser nulos.");
        } else if (roomData.containsKey(Room.ID_FIELD)) {
            throw new IllegalArgumentException("ID da sala não deve ser fornecido na inserção.");
        }

        Room.validate(roomData);

        Map<String, String> roomDataWithId = new java.util.HashMap<>(roomData);
        roomDataWithId.put(Room.ID_FIELD, String.valueOf(sequence++));
        Repository.insertEnsuringUniqueness(FILE_NAME, roomDataWithId, room -> room.get(Room.ID_FIELD));

        return roomDataWithId;
    }

    /**
     * Retrieves a range of rooms from the repository.
     * 
     * @param skip The number of rooms to skip.
     * @param take The number of rooms to retrieve.
     * @return A list of room data maps within the specified range.
     * @throws IOException If an I/O error occurs during the operation.
     */
    public static List<Map<String, String>> getRange(int skip, int take) throws IOException {
        return Repository.getRange(
            FILE_NAME,
            skip,
            take
        );
    }
}
