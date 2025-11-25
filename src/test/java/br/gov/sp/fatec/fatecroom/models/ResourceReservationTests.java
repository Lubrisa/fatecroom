package br.gov.sp.fatec.fatecroom.models;

import java.util.HashMap;
import java.util.Map;

public class ResourceReservationTests {

    private static Map<String, String> validReservation(String resourceId, String userId, String date) {
        var m = new HashMap<String, String>();
        m.put("id_recurso", resourceId);
        m.put("id_usuario", userId);
        m.put("data_reserva", date);
        m.put("hora_inicio", "10:00");
        m.put("hora_fim", "11:00");
        m.put("status", "ATIVA");
        return m;
    }

    // quantity field removed from ResourceReservation model; no tests for it

    private static void validate_shouldThrow_whenIdProvidedButInvalid() {
        try {
            var r = validReservation("1", "2", "2025-12-01");
            r.put("id_reserva_recurso", "");
            ResourceReservation.validate(r);
            System.err.println("Test failed: Expected IllegalArgumentException was not thrown (id empty).");
        } catch (IllegalArgumentException e) {
            System.out.println("Test passed: Caught expected IllegalArgumentException for empty id.");
        }

        try {
            var r2 = validReservation("1", "2", "2025-12-01");
            r2.put("id_reserva_recurso", "abc");
            ResourceReservation.validate(r2);
            System.err.println("Test failed: Expected IllegalArgumentException was not thrown (id non-numeric).");
        } catch (IllegalArgumentException e) {
            System.out.println("Test passed: Caught expected IllegalArgumentException for non-numeric id.");
        }
    }

    private static void validate_shouldThrow_whenResourceOrUserIdMissingOrInvalid() {
        try {
            var r = validReservation("1", "2", "2025-12-01");
            r.remove("id_recurso");
            ResourceReservation.validate(r);
            System.err.println("Test failed: Expected IllegalArgumentException was not thrown (resource id missing).");
        } catch (IllegalArgumentException e) {
            System.out.println("Test passed: Caught expected IllegalArgumentException for missing resource id.");
        }

        try {
            var r2 = validReservation("x", "2", "2025-12-01");
            ResourceReservation.validate(r2);
            System.err.println("Test failed: Expected IllegalArgumentException was not thrown (resource id non-numeric).");
        } catch (IllegalArgumentException e) {
            System.out.println("Test passed: Caught expected IllegalArgumentException for non-numeric resource id.");
        }

        try {
            var r3 = validReservation("1", "2", "2025-12-01");
            r3.remove("id_usuario");
            ResourceReservation.validate(r3);
            System.err.println("Test failed: Expected IllegalArgumentException was not thrown (user id missing).");
        } catch (IllegalArgumentException e) {
            System.out.println("Test passed: Caught expected IllegalArgumentException for missing user id.");
        }

        try {
            var r4 = validReservation("1", "y", "2025-12-01");
            ResourceReservation.validate(r4);
            System.err.println("Test failed: Expected IllegalArgumentException was not thrown (user id non-numeric).");
        } catch (IllegalArgumentException e) {
            System.out.println("Test passed: Caught expected IllegalArgumentException for non-numeric user id.");
        }
    }

    // quantity field removed from ResourceReservation model; no tests for it

    private static void validate_shouldThrow_whenStartEndOrStatusMissingOrInvalid() {
        try {
            var r = validReservation("1", "2", "2025-12-01");
            r.remove("hora_inicio");
            ResourceReservation.validate(r);
            System.err.println("Test failed: Expected IllegalArgumentException was not thrown (hora_inicio missing).");
        } catch (IllegalArgumentException e) {
            System.out.println("Test passed: Caught expected IllegalArgumentException for missing hora_inicio.");
        }

        try {
            var r2 = validReservation("1", "2", "2025-12-01");
            r2.remove("hora_fim");
            ResourceReservation.validate(r2);
            System.err.println("Test failed: Expected IllegalArgumentException was not thrown (hora_fim missing).");
        } catch (IllegalArgumentException e) {
            System.out.println("Test passed: Caught expected IllegalArgumentException for missing hora_fim.");
        }

        try {
            var r3 = validReservation("1", "2", "2025-12-01");
            r3.put("hora_inicio", "9am");
            ResourceReservation.validate(r3);
            System.err.println("Test failed: Expected IllegalArgumentException was not thrown (hora_inicio invalid format).");
        } catch (IllegalArgumentException e) {
            System.out.println("Test passed: Caught expected IllegalArgumentException for invalid hora_inicio format.");
        }

        try {
            var r4 = validReservation("1", "2", "2025-12-01");
            r4.put("status", "UNKNOWN");
            ResourceReservation.validate(r4);
            System.err.println("Test failed: Expected IllegalArgumentException was not thrown (status invalid).");
        } catch (IllegalArgumentException e) {
            System.out.println("Test passed: Caught expected IllegalArgumentException for invalid status.");
        }
    }

    private static void validate_shouldThrow_whenDateMissing() {
        try {
            var r = validReservation("1", "2", "2025-12-01");
            r.remove("data_reserva");
            ResourceReservation.validate(r);
            System.err.println("Test failed: Expected IllegalArgumentException was not thrown (date missing).");
        } catch (IllegalArgumentException e) {
            System.out.println("Test passed: Caught expected IllegalArgumentException for missing date.");
        }
    }

    private static void validate_shouldThrow_whenDateInvalid() {
        try {
            var r = validReservation("1", "2", "01-12-2025");
            ResourceReservation.validate(r);
            System.err.println("Test failed: Expected IllegalArgumentException was not thrown (invalid date).");
        } catch (IllegalArgumentException e) {
            System.out.println("Test passed: Caught expected IllegalArgumentException for invalid date format.");
        } catch (Exception e) {
            System.err.println("Test failed: Caught unexpected exception type: " + e);
        }
    }

    private static void validate_shouldPass_whenValid() {
        try {
            var r = validReservation("5", "6", "2025-12-01");
            ResourceReservation.validate(r);
            System.out.println("Test passed: Valid resource reservation did not throw.");
        } catch (Exception e) {
            System.err.println("Test failed: Caught unexpected exception for valid data: " + e);
        }
    }

    public static void main(String[] args) {
        validate_shouldThrow_whenIdProvidedButInvalid();
        validate_shouldThrow_whenResourceOrUserIdMissingOrInvalid();
        // removed quantity tests
        validate_shouldThrow_whenStartEndOrStatusMissingOrInvalid();
        validate_shouldThrow_whenDateMissing();
        validate_shouldThrow_whenDateInvalid();
        validate_shouldPass_whenValid();
    }
}
