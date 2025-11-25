package br.gov.sp.fatec.fatecroom.persistence;

import br.gov.sp.fatec.fatecroom.models.RoomReservation;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RoomReservationsRepositoryTests {

    private static final Path FILE = Path.of("reservas_salas.csv");

    private static void deleteFileQuietly() {
        try { Files.deleteIfExists(FILE); } catch (IOException ignored) { }
    }

    private static Map<String, String> validReservation(String roomId, String userId, String date, String start, String end) {
        var m = new HashMap<String, String>();
        m.put(RoomReservation.ROOM_ID_FIELD, roomId);
        m.put(RoomReservation.USER_ID_FIELD, userId);
        m.put(RoomReservation.DATE_FIELD, date);
        m.put(RoomReservation.START_TIME_FIELD, start);
        m.put(RoomReservation.END_TIME_FIELD, end);
        m.put(RoomReservation.STATUS_FIELD, RoomReservation.ACTIVE_STATUS);
        m.put(RoomReservation.OBSERVATION_FIELD, "ok");
        return m;
    }

    private static void insert_shouldThrowIllegalArgumentException_whenNull() {
        try {
            RoomReservationsRepository.insert(null);
            System.err.println("Test failed: Expected IllegalArgumentException was not thrown.");
        } catch (IllegalArgumentException e) {
            if (!e.getMessage().toLowerCase().contains("nulo"))
                System.err.println("Test warning: unexpected exception message: " + e.getMessage());
            else
                System.out.println("Test passed: Caught expected IllegalArgumentException for null reservation data.");
        } catch (Exception e) {
            System.err.println("Test failed: Caught unexpected exception type: " + e);
        }
    }

    private static void insert_update_delete_flow() {
        deleteFileQuietly();
        try {
            var inserted = RoomReservationsRepository.insert(validReservation("1","2","2025-11-24","09:00","10:00"));
            if (!inserted.containsKey(RoomReservation.ID_FIELD) || inserted.get(RoomReservation.ID_FIELD).isBlank()) {
                System.err.println("Test failed: Inserted reservation did not contain generated id.");
                return;
            }
            var id = inserted.get(RoomReservation.ID_FIELD);

            // update status
            var toUpdate = new HashMap<String,String>(inserted);
            toUpdate.put(RoomReservation.STATUS_FIELD, RoomReservation.CANCELLED_STATUS);
            RoomReservationsRepository.update(toUpdate);

            List<Map<String,String>> range = RoomReservationsRepository.getRange(0, 10);
            boolean updated = range.stream().anyMatch(m -> id.equals(m.get(RoomReservation.ID_FIELD)) && RoomReservation.CANCELLED_STATUS.equals(m.get(RoomReservation.STATUS_FIELD)));
            if (!updated) {
                System.err.println("Test failed: Updated reservation not found or not updated.");
                return;
            }

            boolean deleted = RoomReservationsRepository.delete(id);
            if (!deleted) {
                System.err.println("Test failed: Delete returned false for existing reservation.");
            }

            List<Map<String,String>> after = RoomReservationsRepository.getRange(0,10);
            boolean stillExists = after.stream().anyMatch(m -> id.equals(m.get(RoomReservation.ID_FIELD)));
            if (stillExists) System.err.println("Test failed: Reservation still found after delete.");
        } catch (Exception e) {
            System.err.println("Test failed: Caught unexpected exception: " + e);
        } finally {
            deleteFileQuietly();
        }
    }

    private static void delete_shouldThrow_whenIdNullOrBlank() {
        try {
            RoomReservationsRepository.delete(null);
            System.err.println("Test failed: Expected IllegalArgumentException was not thrown for null id.");
        } catch (IllegalArgumentException e) {
            System.out.println("Test passed: Caught expected IllegalArgumentException for null id.");
        } catch (Exception e) {
            System.err.println("Test failed: Caught unexpected exception type: " + e);
        }

        try {
            RoomReservationsRepository.delete("   ");
            System.err.println("Test failed: Expected IllegalArgumentException was not thrown for blank id.");
        } catch (IllegalArgumentException e) {
            System.out.println("Test passed: Caught expected IllegalArgumentException for blank id.");
        } catch (Exception e) {
            System.err.println("Test failed: Caught unexpected exception type: " + e);
        }
    }

    public static void main(String[] args) {
        insert_shouldThrowIllegalArgumentException_whenNull();
        insert_update_delete_flow();
        delete_shouldThrow_whenIdNullOrBlank();
    }
}
