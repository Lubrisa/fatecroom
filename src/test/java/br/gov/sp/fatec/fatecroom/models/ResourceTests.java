package br.gov.sp.fatec.fatecroom.models;

import java.util.HashMap;
import java.util.Map;

public class ResourceTests {

    private static Map<String, String> validResource(String name) {
        var m = new HashMap<String, String>();
        m.put("nome_recurso", name);
        m.put("tipo_recurso", "PROJETOR");
        m.put("patrimonio", "12345");
        m.put("local_padrao", "Sala 1");
        m.put("observacao", "ok");
        return m;
    }

    private static void validate_shouldThrow_whenNameMissing() {
        try {
            var r = validResource("Projector");
            r.remove("nome_recurso");
            Resource.validate(r);
            System.err.println("Test failed: Expected IllegalArgumentException was not thrown (name missing).");
        } catch (IllegalArgumentException e) {
            System.out.println("Test passed: Caught expected IllegalArgumentException for missing name.");
        } catch (Exception e) {
            System.err.println("Test failed: Caught unexpected exception type: " + e);
        }
    }

    private static void validate_shouldThrow_whenNameTooShort() {
        try {
            var r = validResource("AB");
            Resource.validate(r);
            System.err.println("Test failed: Expected IllegalArgumentException was not thrown (name too short).");
        } catch (IllegalArgumentException e) {
            System.out.println("Test passed: Caught expected IllegalArgumentException for name too short.");
        }
    }

    private static void validate_shouldThrow_whenTypeMissingOrInvalid() {
        try {
            var r = validResource("Projector");
            r.remove("tipo_recurso");
            Resource.validate(r);
            System.err.println("Test failed: Expected IllegalArgumentException was not thrown (type missing).");
        } catch (IllegalArgumentException e) {
            System.out.println("Test passed: Caught expected IllegalArgumentException for missing type.");
        }

        try {
            var r2 = validResource("Projector");
            r2.put("tipo_recurso", "UNKNOWN");
            Resource.validate(r2);
            System.err.println("Test failed: Expected IllegalArgumentException was not thrown (type invalid).");
        } catch (IllegalArgumentException e) {
            System.out.println("Test passed: Caught expected IllegalArgumentException for invalid type.");
        }
    }

    private static void validate_shouldThrow_whenPatrimonyMissingOrInvalid() {
        try {
            var r = validResource("Projector");
            r.remove("patrimonio");
            Resource.validate(r);
            System.err.println("Test failed: Expected IllegalArgumentException was not thrown (patrimonio missing).");
        } catch (IllegalArgumentException e) {
            System.out.println("Test passed: Caught expected IllegalArgumentException for missing patrimonio.");
        }

        try {
            var r2 = validResource("Projector");
            r2.put("patrimonio", "");
            Resource.validate(r2);
            System.err.println("Test failed: Expected IllegalArgumentException was not thrown (patrimonio empty).");
        } catch (IllegalArgumentException e) {
            System.out.println("Test passed: Caught expected IllegalArgumentException for empty patrimonio.");
        }

        try {
            var r3 = validResource("Projector");
            r3.put("patrimonio", "abc");
            Resource.validate(r3);
            System.err.println("Test failed: Expected IllegalArgumentException was not thrown (patrimonio non-numeric).");
        } catch (IllegalArgumentException e) {
            System.out.println("Test passed: Caught expected IllegalArgumentException for non-numeric patrimonio.");
        }
    }

    private static void validate_shouldThrow_whenDefaultLocationMissingOrEmpty() {
        try {
            var r = validResource("Projector");
            r.remove("local_padrao");
            Resource.validate(r);
            System.err.println("Test failed: Expected IllegalArgumentException was not thrown (local_padrao missing).");
        } catch (IllegalArgumentException e) {
            System.out.println("Test passed: Caught expected IllegalArgumentException for missing local_padrao.");
        }

        try {
            var r2 = validResource("Projector");
            r2.put("local_padrao", "");
            Resource.validate(r2);
            System.err.println("Test failed: Expected IllegalArgumentException was not thrown (local_padrao empty).");
        } catch (IllegalArgumentException e) {
            System.out.println("Test passed: Caught expected IllegalArgumentException for empty local_padrao.");
        }
    }

    private static void validate_shouldThrow_whenQuantityNotNumberOrActiveInvalid() {
        // quantity and active fields were removed from the Resource model; nothing to test here.
    }

    private static void validate_shouldThrow_whenIdProvidedButInvalid() {
        try {
            var r = validResource("Projector");
            r.put("id_recurso", "");
            Resource.validate(r);
            System.err.println("Test failed: Expected IllegalArgumentException was not thrown (id empty).");
        } catch (IllegalArgumentException e) {
            System.out.println("Test passed: Caught expected IllegalArgumentException for empty id.");
        }

        try {
            var r2 = validResource("Projector");
            r2.put("id_recurso", "abc");
            Resource.validate(r2);
            System.err.println("Test failed: Expected IllegalArgumentException was not thrown (id non-numeric).");
        } catch (IllegalArgumentException e) {
            System.out.println("Test passed: Caught expected IllegalArgumentException for non-numeric id.");
        }
    }

    private static void validate_shouldThrow_whenQuantityNotNumber() {
        // no-op: quantity field removed
    }

    private static void validate_shouldPass_whenValid() {
        try {
            var r = validResource("Speaker");
            Resource.validate(r);
            System.out.println("Test passed: Valid resource data did not throw.");
        } catch (Exception e) {
            System.err.println("Test failed: Caught unexpected exception for valid data: " + e);
        }
    }

    public static void main(String[] args) {
        validate_shouldThrow_whenNameMissing();
        validate_shouldThrow_whenNameTooShort();
        validate_shouldThrow_whenTypeMissingOrInvalid();
        validate_shouldThrow_whenPatrimonyMissingOrInvalid();
        validate_shouldThrow_whenDefaultLocationMissingOrEmpty();
        // removed quantity/active tests
        validate_shouldThrow_whenIdProvidedButInvalid();
        // removed quantity tests
        validate_shouldPass_whenValid();
    }
}
