package br.gov.sp.fatec.fatecroom.models;

import java.util.HashMap;
import java.util.Map;

public class UserTests {

    private static Map<String, String> validUser(String name, String type, String email, String password, String active) {
        var m = new HashMap<String, String>();
        m.put("nome_usuario", name);
        m.put("tipo_usuario", type);
        m.put("email", email);
        m.put("senha", password);
        m.put("ativo", active);
        return m;
    }

    private static void validate_shouldThrow_whenIdProvidedButInvalid() {
        try {
            var u = validUser("Joao", "STUDENT", "joao@fatec.sp.gov.br", "longpassword12", "true");
            u.put("id_usuario", "");
            User.validate(u);
            System.err.println("Test failed: Expected IllegalArgumentException was not thrown (id empty).");
        } catch (IllegalArgumentException e) {
            System.out.println("Test passed: Caught expected IllegalArgumentException for empty id.");
        }

        try {
            var u2 = validUser("Joao", "STUDENT", "joao@fatec.sp.gov.br", "longpassword12", "true");
            u2.put("id_usuario", "abc");
            User.validate(u2);
            System.err.println("Test failed: Expected IllegalArgumentException was not thrown (id non-numeric).");
        } catch (IllegalArgumentException e) {
            System.out.println("Test passed: Caught expected IllegalArgumentException for non-numeric id.");
        }
    }

    private static void validate_shouldThrow_whenNameMissingOrInvalidLength() {
        try {
            var u = validUser("Joao", "STUDENT", "joao@fatec.sp.gov.br", "longpassword12", "true");
            u.remove("nome_usuario");
            User.validate(u);
            System.err.println("Test failed: Expected IllegalArgumentException was not thrown (name missing).");
        } catch (IllegalArgumentException e) {
            System.out.println("Test passed: Caught expected IllegalArgumentException for missing name.");
        }

        try {
            var u2 = validUser("Al", "STUDENT", "al@fatec.sp.gov.br", "longpassword12", "true");
            User.validate(u2);
            System.err.println("Test failed: Expected IllegalArgumentException was not thrown (name too short).");
        } catch (IllegalArgumentException e) {
            System.out.println("Test passed: Caught expected IllegalArgumentException for name too short.");
        }

        try {
            var longName = "A".repeat(101);
            var u3 = validUser(longName, "STUDENT", "x@fatec.sp.gov.br", "longpassword12", "true");
            User.validate(u3);
            System.err.println("Test failed: Expected IllegalArgumentException was not thrown (name too long).");
        } catch (IllegalArgumentException e) {
            System.out.println("Test passed: Caught expected IllegalArgumentException for name too long.");
        }
    }

    private static void validate_shouldThrow_whenTypeMissingOrInvalid() {
        try {
            var u = validUser("Joao", "STUDENT", "joao@fatec.sp.gov.br", "longpassword12", "true");
            u.remove("tipo_usuario");
            User.validate(u);
            System.err.println("Test failed: Expected IllegalArgumentException was not thrown (type missing).");
        } catch (IllegalArgumentException e) {
            System.out.println("Test passed: Caught expected IllegalArgumentException for missing type.");
        }

        try {
            var u2 = validUser("Joao", "UNKNOWN", "joao@fatec.sp.gov.br", "longpassword12", "true");
            User.validate(u2);
            System.err.println("Test failed: Expected IllegalArgumentException was not thrown (type invalid).");
        } catch (IllegalArgumentException e) {
            System.out.println("Test passed: Caught expected IllegalArgumentException for invalid type.");
        }
    }

    private static void validate_shouldThrow_whenEmailMissingOrInvalid() {
        try {
            var u = validUser("Joao", "STUDENT", "joao@fatec.sp.gov.br", "longpassword12", "true");
            u.remove("email");
            User.validate(u);
            System.err.println("Test failed: Expected IllegalArgumentException was not thrown (email missing).");
        } catch (IllegalArgumentException e) {
            System.out.println("Test passed: Caught expected IllegalArgumentException for missing email.");
        }

        try {
            var u2 = validUser("Joao", "STUDENT", "joao@example.com", "longpassword12", "true");
            User.validate(u2);
            System.err.println("Test failed: Expected IllegalArgumentException was not thrown (email wrong domain).");
        } catch (IllegalArgumentException e) {
            System.out.println("Test passed: Caught expected IllegalArgumentException for wrong domain.");
        }

        try {
            var u3 = validUser("Joao", "STUDENT", "@fatec.sp.gov.br", "longpassword12", "true");
            User.validate(u3);
            System.err.println("Test failed: Expected IllegalArgumentException was not thrown (email local part empty).");
        } catch (IllegalArgumentException e) {
            System.out.println("Test passed: Caught expected IllegalArgumentException for empty local part of email.");
        }
    }

    private static void validate_shouldThrow_whenPasswordMissingOrInvalidLength() {
        try {
            var u = validUser("Joao", "STUDENT", "joao@fatec.sp.gov.br", "longpassword12", "true");
            u.remove("senha");
            User.validate(u);
            System.err.println("Test failed: Expected IllegalArgumentException was not thrown (password missing).");
        } catch (IllegalArgumentException e) {
            System.out.println("Test passed: Caught expected IllegalArgumentException for missing password.");
        }

        try {
            var u2 = validUser("Joao", "STUDENT", "joao@fatec.sp.gov.br", "short", "true");
            User.validate(u2);
            System.err.println("Test failed: Expected IllegalArgumentException was not thrown (password too short).");
        } catch (IllegalArgumentException e) {
            System.out.println("Test passed: Caught expected IllegalArgumentException for short password.");
        }
    }

    private static void validate_shouldThrow_whenActiveMissingOrInvalid() {
        try {
            var u = validUser("Joao", "STUDENT", "joao@fatec.sp.gov.br", "longpassword12", "true");
            u.remove("ativo");
            User.validate(u);
            System.err.println("Test failed: Expected IllegalArgumentException was not thrown (active missing).");
        } catch (IllegalArgumentException e) {
            System.out.println("Test passed: Caught expected IllegalArgumentException for missing active.");
        }

        try {
            var u2 = validUser("Joao", "STUDENT", "joao@fatec.sp.gov.br", "longpassword12", "yes");
            User.validate(u2);
            System.err.println("Test failed: Expected IllegalArgumentException was not thrown (active invalid).");
        } catch (IllegalArgumentException e) {
            System.out.println("Test passed: Caught expected IllegalArgumentException for invalid active value.");
        }
    }

    private static void validate_shouldPass_whenValid() {
        try {
            var u = validUser("Maria Silva", "PROFESSOR", "maria@fatec.sp.gov.br", "averylongpassword", "false");
            User.validate(u);
            System.out.println("Test passed: Valid user data did not throw.");
        } catch (Exception e) {
            System.err.println("Test failed: Caught unexpected exception for valid data: " + e);
        }
    }

    public static void main(String[] args) {
        validate_shouldThrow_whenIdProvidedButInvalid();
        validate_shouldThrow_whenNameMissingOrInvalidLength();
        validate_shouldThrow_whenTypeMissingOrInvalid();
        validate_shouldThrow_whenEmailMissingOrInvalid();
        validate_shouldThrow_whenPasswordMissingOrInvalidLength();
        validate_shouldThrow_whenActiveMissingOrInvalid();
        validate_shouldPass_whenValid();
    }
}
