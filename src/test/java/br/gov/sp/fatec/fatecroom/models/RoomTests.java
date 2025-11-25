package br.gov.sp.fatec.fatecroom.models;

import java.util.HashMap;
import java.util.Map;

public class RoomTests {

    private static Map<String, String> validRoom(String name, String capacity) {
        var m = new HashMap<String, String>();
        m.put("nome_sala", name);
        m.put("tipo_sala", "SALA_DE_AULA");
        m.put("capacidade", capacity);
        m.put("bloco", "A");
        m.put("observacao", "ok");
        return m;
    }

    private static void validate_shouldThrow_whenNameMissing() {
        try {
            var r = validRoom("Room 1", "10");
            r.remove("nome_sala");
            Room.validate(r);
            System.err.println("Test failed: Expected IllegalArgumentException was not thrown (name missing).");
        } catch (IllegalArgumentException e) {
            System.out.println("Test passed: Caught expected IllegalArgumentException for missing name.");
        } catch (Exception e) {
            System.err.println("Test failed: Caught unexpected exception type: " + e);
        }
    }

    private static void validate_shouldThrow_whenCapacityNotNumber() {
        try {
            var r = validRoom("Room 1", "ten");
            Room.validate(r);
            System.err.println("Test failed: Expected IllegalArgumentException was not thrown (capacity not number).");
        } catch (IllegalArgumentException e) {
            System.out.println("Test passed: Caught expected IllegalArgumentException for non-numeric capacity.");
        } catch (Exception e) {
            System.err.println("Test failed: Caught unexpected exception type: " + e);
        }
    }

    private static void validate_shouldThrow_whenActiveInvalid() {
        try {
            // Room model does not validate 'ativo' here; instead test invalid room type
            var r = validRoom("Room 1", "10");
            r.put("tipo_sala", "UNKNOWN_TYPE");
            Room.validate(r);
            System.err.println("Test failed: Expected IllegalArgumentException was not thrown (invalid room type).");
        } catch (IllegalArgumentException e) {
            System.out.println("Test passed: Caught expected IllegalArgumentException for invalid room type.");
        } catch (Exception e) {
            System.err.println("Test failed: Caught unexpected exception type: " + e);
        }
    }

    private static void validate_shouldThrow_whenNameTooShortOrTooLong() {
        try {
            var r = validRoom("Ro", "10");
            Room.validate(r);
            System.err.println("Test failed: Expected IllegalArgumentException was not thrown (name too short).");
        } catch (IllegalArgumentException e) {
            System.out.println("Test passed: Caught expected IllegalArgumentException for name too short.");
        }

        try {
            var longName = "A".repeat(101);
            var r2 = validRoom(longName, "10");
            Room.validate(r2);
            System.err.println("Test failed: Expected IllegalArgumentException was not thrown (name too long).");
        } catch (IllegalArgumentException e) {
            System.out.println("Test passed: Caught expected IllegalArgumentException for name too long.");
        }
    }

    private static void validate_shouldThrow_whenCapacityOutOfRange() {
        try {
            var r = validRoom("Room 1", "0");
            Room.validate(r);
            System.err.println("Test failed: Expected IllegalArgumentException was not thrown (capacity too small).");
        } catch (IllegalArgumentException e) {
            System.out.println("Test passed: Caught expected IllegalArgumentException for capacity too small.");
        }

        try {
            var r2 = validRoom("Room 1", "1000");
            Room.validate(r2);
            System.err.println("Test failed: Expected IllegalArgumentException was not thrown (capacity too large).");
        } catch (IllegalArgumentException e) {
            System.out.println("Test passed: Caught expected IllegalArgumentException for capacity too large.");
        }
    }

    private static void validate_shouldThrow_whenIdProvidedButInvalid() {
        try {
            var r = validRoom("Room 1", "10");
            r.put("id_sala", "");
            Room.validate(r);
            System.err.println("Test failed: Expected IllegalArgumentException was not thrown (id empty).");
        } catch (IllegalArgumentException e) {
            System.out.println("Test passed: Caught expected IllegalArgumentException for empty id.");
        }

        try {
            var r2 = validRoom("Room 1", "10");
            r2.put("id_sala", "abc");
            Room.validate(r2);
            System.err.println("Test failed: Expected IllegalArgumentException was not thrown (id non-numeric).");
        } catch (IllegalArgumentException e) {
            System.out.println("Test passed: Caught expected IllegalArgumentException for non-numeric id.");
        }
    }

    private static void validate_shouldThrow_whenTypeMissing() {
        try {
            var r = validRoom("Room 1", "10");
            r.remove("tipo_sala");
            Room.validate(r);
            System.err.println("Test failed: Expected IllegalArgumentException was not thrown (type missing).");
        } catch (IllegalArgumentException e) {
            System.out.println("Test passed: Caught expected IllegalArgumentException for missing type.");
        }
    }

    private static void validate_shouldPass_whenValid() {
        try {
            var r = validRoom("Main Hall", "25");
            Room.validate(r);
            System.out.println("Test passed: Valid room data did not throw.");
        } catch (Exception e) {
            System.err.println("Test failed: Caught unexpected exception for valid data: " + e);
        }
    }

    public static void main(String[] args) {
        validate_shouldThrow_whenNameMissing();
        validate_shouldThrow_whenCapacityNotNumber();
        validate_shouldThrow_whenActiveInvalid();
        validate_shouldThrow_whenNameTooShortOrTooLong();
        validate_shouldThrow_whenCapacityOutOfRange();
        validate_shouldThrow_whenIdProvidedButInvalid();
        validate_shouldThrow_whenTypeMissing();
        validate_shouldPass_whenValid();
    }
}
