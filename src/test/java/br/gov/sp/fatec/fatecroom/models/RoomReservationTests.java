package br.gov.sp.fatec.fatecroom.models;

import java.util.HashMap;
import java.util.Map;

public class RoomReservationTests {

    private static Map<String, String> validReservation(String roomId, String userId, String date, String start, String end) {
        var m = new HashMap<String, String>();
        m.put("id_sala", roomId);
        m.put("id_usuario", userId);
        m.put("data_reserva", date);
        m.put("hora_inicio", start);
        m.put("hora_fim", end);
        m.put("status", "ATIVA");
        m.put("observacao", "Aula");
        return m;
    }

    private static void validate_shouldThrow_whenRoomIdMissing() {
        try {
            var r = validReservation("1", "2", "2025-10-10", "09:00", "10:00");
            r.remove("id_sala");
            RoomReservation.validate(r);
            System.err.println("Test failed: Expected IllegalArgumentException was not thrown (room id missing).");
        } catch (IllegalArgumentException e) {
            System.out.println("Test passed: Caught expected IllegalArgumentException for missing room id.");
        } catch (Exception e) {
            System.err.println("Test failed: Caught unexpected exception type: " + e);
        }
    }

    private static void validate_shouldThrow_whenIdProvidedButInvalid() {
        try {
            var r = validReservation("1", "2", "2025-10-10", "09:00", "10:00");
            r.put("id_reserva", "");
            RoomReservation.validate(r);
            System.err.println("Test failed: Expected IllegalArgumentException was not thrown (id empty).");
        } catch (IllegalArgumentException e) {
            System.out.println("Test passed: Caught expected IllegalArgumentException for empty id.");
        }

        try {
            var r2 = validReservation("1", "2", "2025-10-10", "09:00", "10:00");
            r2.put("id_reserva", "abc");
            RoomReservation.validate(r2);
            System.err.println("Test failed: Expected IllegalArgumentException was not thrown (id non-numeric).");
        } catch (IllegalArgumentException e) {
            System.out.println("Test passed: Caught expected IllegalArgumentException for non-numeric id.");
        }
    }

    private static void validate_shouldThrow_whenUserIdMissingOrInvalid() {
        try {
            var r = validReservation("1", "2", "2025-10-10", "09:00", "10:00");
            r.remove("id_usuario");
            RoomReservation.validate(r);
            System.err.println("Test failed: Expected IllegalArgumentException was not thrown (user id missing).");
        } catch (IllegalArgumentException e) {
            System.out.println("Test passed: Caught expected IllegalArgumentException for missing user id.");
        }

        try {
            var r2 = validReservation("1", "x", "2025-10-10", "09:00", "10:00");
            RoomReservation.validate(r2);
            System.err.println("Test failed: Expected IllegalArgumentException was not thrown (user id non-numeric).");
        } catch (IllegalArgumentException e) {
            System.out.println("Test passed: Caught expected IllegalArgumentException for non-numeric user id.");
        }
    }

    private static void validate_shouldThrow_whenTimeFormatInvalid() {
        try {
            var r = validReservation("1", "2", "2025-10-10", "9am", "10:00");
            RoomReservation.validate(r);
            System.err.println("Test failed: Expected IllegalArgumentException was not thrown (start format invalid).");
        } catch (IllegalArgumentException e) {
            System.out.println("Test passed: Caught expected IllegalArgumentException for invalid start format.");
        }

        try {
            var r2 = validReservation("1", "2", "2025-10-10", "09:00", "10am");
            RoomReservation.validate(r2);
            System.err.println("Test failed: Expected IllegalArgumentException was not thrown (end format invalid).");
        } catch (IllegalArgumentException e) {
            System.out.println("Test passed: Caught expected IllegalArgumentException for invalid end format.");
        }
    }

    private static void validate_shouldThrow_whenReasonTooLong() {
        try {
            var longReason = "A".repeat(501);
            var r = validReservation("1", "2", "2025-10-10", "09:00", "10:00");
            r.put("observacao", longReason);
            RoomReservation.validate(r);
            System.err.println("Test failed: Expected IllegalArgumentException was not thrown (reason too long).");
        } catch (IllegalArgumentException e) {
            System.out.println("Test passed: Caught expected IllegalArgumentException for too long reason.");
        }
    }

    private static void validate_shouldThrow_whenDateInvalid() {
        try {
            var r = validReservation("1", "2", "10-10-2025", "09:00", "10:00");
            RoomReservation.validate(r);
            System.err.println("Test failed: Expected IllegalArgumentException was not thrown (invalid date).");
        } catch (IllegalArgumentException e) {
            System.out.println("Test passed: Caught expected IllegalArgumentException for invalid date format.");
        } catch (Exception e) {
            System.err.println("Test failed: Caught unexpected exception type: " + e);
        }
    }

    private static void validate_shouldThrow_whenTimeOrderInvalid() {
        try {
            var r = validReservation("1", "2", "2025-10-10", "10:00", "09:00");
            RoomReservation.validate(r);
            System.err.println("Test failed: Expected IllegalArgumentException was not thrown (start >= end).");
        } catch (IllegalArgumentException e) {
            System.out.println("Test passed: Caught expected IllegalArgumentException for start >= end.");
        } catch (Exception e) {
            System.err.println("Test failed: Caught unexpected exception type: " + e);
        }
    }

    private static void validate_shouldPass_whenValid() {
        try {
            var r = validReservation("3", "4", "2025-11-01", "08:30", "09:30");
            RoomReservation.validate(r);
            System.out.println("Test passed: Valid room reservation did not throw.");
        } catch (Exception e) {
            System.err.println("Test failed: Caught unexpected exception for valid data: " + e);
        }
    }

    public static void main(String[] args) {
        validate_shouldThrow_whenRoomIdMissing();
        validate_shouldThrow_whenIdProvidedButInvalid();
        validate_shouldThrow_whenUserIdMissingOrInvalid();
        validate_shouldThrow_whenTimeFormatInvalid();
        validate_shouldThrow_whenReasonTooLong();
        validate_shouldThrow_whenDateInvalid();
        validate_shouldThrow_whenTimeOrderInvalid();
        validate_shouldPass_whenValid();
    }
}
