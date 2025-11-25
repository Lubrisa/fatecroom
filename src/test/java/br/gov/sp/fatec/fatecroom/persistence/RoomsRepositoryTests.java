package br.gov.sp.fatec.fatecroom.persistence;

import br.gov.sp.fatec.fatecroom.models.Room;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RoomsRepositoryTests {

    private static final Path FILE = Path.of("salas.csv");

    private static void deleteFileQuietly() {
        try { Files.deleteIfExists(FILE); } catch (IOException ignored) { }
    }

    private static Map<String, String> validRoom(String name, String type, String capacity, String block) {
        var m = new HashMap<String, String>();
        m.put(Room.NAME_FIELD, name);
        m.put(Room.ROOM_TYPE_FIELD, type);
        m.put(Room.CAPACITY_FIELD, capacity);
        m.put(Room.BLOCK_FIELD, block);
        m.put(Room.OBSERVATION_FIELD, "obs");
        return m;
    }

    private static void insert_shouldThrowIllegalArgumentException_whenRoomIsNull() {
        try {
            RoomsRepository.insert(null);
            System.err.println("Test failed: Expected IllegalArgumentException was not thrown.");
        } catch (IllegalArgumentException e) {
            if (!e.getMessage().toLowerCase().contains("nulo"))
                System.err.println("Test warning: unexpected exception message: " + e.getMessage());
            else
                System.out.println("Test passed: Caught expected IllegalArgumentException for null room data.");
        } catch (Exception e) {
            System.err.println("Test failed: Caught unexpected exception type: " + e);
        }
    }

    private static void insert_shouldThrowIllegalArgumentException_whenIdProvided() {
        try {
            var r = validRoom("R1", Room.CLASSROOM_TYPE, "10", "B1");
            r.put(Room.ID_FIELD, "5");
            RoomsRepository.insert(r);
            System.err.println("Test failed: Expected IllegalArgumentException was not thrown.");
        } catch (IllegalArgumentException e) {
            if (!e.getMessage().toLowerCase().contains("id"))
                System.err.println("Test warning: unexpected exception message: " + e.getMessage());
            else
                System.out.println("Test passed: Caught expected IllegalArgumentException for provided id.");
        } catch (Exception e) {
            System.err.println("Test failed: Caught unexpected exception type: " + e);
        }
    }

    private static void insert_and_getRange_shouldWork() {
        deleteFileQuietly();
        try {
            var inserted = RoomsRepository.insert(validRoom("ValidRoom", Room.LAB_TYPE, "20", "B2"));
            if (!inserted.containsKey(Room.ID_FIELD) || inserted.get(Room.ID_FIELD).isBlank()) {
                System.err.println("Test failed: Inserted room did not contain generated id.");
                return;
            }

            List<Map<String, String>> range = RoomsRepository.getRange(0, 10);
            if (range.isEmpty()) {
                System.err.println("Test failed: getRange returned empty after insert.");
            } else {
                boolean found = range.stream().anyMatch(m -> "ValidRoom".equals(m.get(Room.NAME_FIELD)));
                if (!found) System.err.println("Test failed: Inserted room not found in range.");
            }
        } catch (Exception e) {
            System.err.println("Test failed: Caught unexpected exception: " + e);
        } finally {
            deleteFileQuietly();
        }
    }

    public static void main(String[] args) {
        insert_shouldThrowIllegalArgumentException_whenRoomIsNull();
        insert_shouldThrowIllegalArgumentException_whenIdProvided();
        insert_and_getRange_shouldWork();
    }
}
