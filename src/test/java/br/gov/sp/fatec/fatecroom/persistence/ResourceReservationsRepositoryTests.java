package br.gov.sp.fatec.fatecroom.persistence;

import br.gov.sp.fatec.fatecroom.models.ResourceReservation;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ResourceReservationsRepositoryTests {

    private static final Path FILE = Path.of("reservas_recursos.csv");

    private static void deleteFileQuietly() {
        try { Files.deleteIfExists(FILE); } catch (IOException ignored) { }
    }

    private static Map<String, String> validReservation(String resourceId, String userId, String date, String start, String end) {
        var m = new HashMap<String, String>();
        m.put(ResourceReservation.RESOURCE_ID_FIELD, resourceId);
        m.put(ResourceReservation.USER_ID_FIELD, userId);
        m.put(ResourceReservation.DATE_FIELD, date);
        m.put(ResourceReservation.START_TIME_FIELD, start);
        m.put(ResourceReservation.END_TIME_FIELD, end);
        m.put(ResourceReservation.STATUS_FIELD, ResourceReservation.ACTIVE_STATUS);
        m.put(ResourceReservation.OBSERVATION_FIELD, "ok");
        return m;
    }

    private static void insert_shouldThrowIllegalArgumentException_whenNull() {
        try {
            ResourceReservationsRepository.insert(null);
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

    private static void insert_shouldThrowIllegalArgumentException_whenIdProvided() {
        try {
            var r = validReservation("1","1","2025-11-24","09:00","10:00");
            r.put(ResourceReservation.ID_FIELD, "10");
            ResourceReservationsRepository.insert(r);
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

    private static void insert_update_delete_flow() {
        deleteFileQuietly();
        try {
            var inserted = ResourceReservationsRepository.insert(validReservation("1","2","2025-11-24","09:00","10:00"));
            if (!inserted.containsKey(ResourceReservation.ID_FIELD) || inserted.get(ResourceReservation.ID_FIELD).isBlank()) {
                System.err.println("Test failed: Inserted reservation did not contain generated id.");
                return;
            }
            var id = inserted.get(ResourceReservation.ID_FIELD);

            // update status
            var toUpdate = new HashMap<String,String>(inserted);
            toUpdate.put(ResourceReservation.STATUS_FIELD, ResourceReservation.CANCELLED_STATUS);
            ResourceReservationsRepository.update(toUpdate);

            List<Map<String,String>> range = ResourceReservationsRepository.getRange(0, 10);
            boolean updated = range.stream().anyMatch(m -> id.equals(m.get(ResourceReservation.ID_FIELD)) && ResourceReservation.CANCELLED_STATUS.equals(m.get(ResourceReservation.STATUS_FIELD)));
            if (!updated) {
                System.err.println("Test failed: Updated reservation not found or not updated.");
                return;
            }

            boolean deleted = ResourceReservationsRepository.delete(id);
            if (!deleted) {
                System.err.println("Test failed: Delete returned false for existing reservation.");
            }

            List<Map<String,String>> after = ResourceReservationsRepository.getRange(0,10);
            boolean stillExists = after.stream().anyMatch(m -> id.equals(m.get(ResourceReservation.ID_FIELD)));
            if (stillExists) System.err.println("Test failed: Reservation still found after delete.");
        } catch (Exception e) {
            System.err.println("Test failed: Caught unexpected exception: " + e);
        } finally {
            deleteFileQuietly();
        }
    }

    private static void delete_shouldThrow_whenIdNullOrBlank() {
        try {
            ResourceReservationsRepository.delete(null);
            System.err.println("Test failed: Expected IllegalArgumentException was not thrown for null id.");
        } catch (IllegalArgumentException e) {
            System.out.println("Test passed: Caught expected IllegalArgumentException for null id.");
        } catch (Exception e) {
            System.err.println("Test failed: Caught unexpected exception type: " + e);
        }

        try {
            ResourceReservationsRepository.delete("   ");
            System.err.println("Test failed: Expected IllegalArgumentException was not thrown for blank id.");
        } catch (IllegalArgumentException e) {
            System.out.println("Test passed: Caught expected IllegalArgumentException for blank id.");
        } catch (Exception e) {
            System.err.println("Test failed: Caught unexpected exception type: " + e);
        }
    }

    private static void delete_shouldReturnFalse_whenNotFound() {
        deleteFileQuietly();
        try {
            boolean r = ResourceReservationsRepository.delete("99999");
            if (!r) System.out.println("Test passed: delete returned false for non-existing id.");
            else System.err.println("Test failed: delete should return false for non-existing id.");
        } catch (Exception e) {
            System.err.println("Test failed: Caught unexpected exception: " + e);
        } finally { deleteFileQuietly(); }
    }

    public static void main(String[] args) {
        insert_shouldThrowIllegalArgumentException_whenNull();
        insert_shouldThrowIllegalArgumentException_whenIdProvided();
        insert_update_delete_flow();
        delete_shouldThrow_whenIdNullOrBlank();
        delete_shouldReturnFalse_whenNotFound();
    }
}
