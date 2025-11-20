package br.gov.sp.fatec.fatecroom.auth;

public class AuthenticationTests {
    private static void setLoggedInUserEmail_shouldThrowIllegalArgumentException_whenEmailIsNull() {
        try {
            Authentication.setLoggedInUserEmail(null);
            System.err.println("Test failed: Expected IllegalArgumentException was not thrown.");
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("null")) {
                System.out.println("Test passed: Caught expected IllegalArgumentException for null email.");
            } else {
                System.err.println("Test failed: Caught IllegalArgumentException, but with unexpected message: " + e.getMessage());
            }
        }
    }

    private static void setLoggedInUserEmail_shouldThrowIllegalArgumentException_whenEmailIsBlank() {
        try {
            Authentication.setLoggedInUserEmail("   ");
            System.err.println("Test failed: Expected IllegalArgumentException was not thrown.");
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("empty")) {
                System.out.println("Test passed: Caught expected IllegalArgumentException for blank email.");
            } else {
                System.err.println("Test failed: Caught IllegalArgumentException, but with unexpected message: " + e.getMessage());
            }
        }
    }

    private static void setLoggedInUserEmail_shouldThrowIllegalArgumentException_whenEmailIsNotInFatecDomain() {
        try {
            Authentication.setLoggedInUserEmail("user@example.com");
            System.err.println("Test failed: Expected IllegalArgumentException was not thrown.");
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("FATEC domain")) {
                System.out.println("Test passed: Caught expected IllegalArgumentException for non-FATEC domain email.");
            } else {
                System.err.println("Test failed: Caught IllegalArgumentException, but with unexpected message: " + e.getMessage());
            }
        }
    }

    private static void setLoggedInUserEmail_shouldThrowIllegalArgumentException_whenEmailLocalPartIsEmpty() {
        try {
            Authentication.setLoggedInUserEmail("@fatec.sp.gov.br");
            System.err.println("Test failed: Expected IllegalArgumentException was not thrown.");
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("local part cannot be empty")) {
                System.out.println("Test passed: Caught expected IllegalArgumentException for empty local part email.");
            } else {
                System.err.println("Test failed: Caught IllegalArgumentException, but with unexpected message: " + e.getMessage());
            }
        }
    }

    private static void setLoggedInUserEmail_shouldSetEmailSuccessfully_whenEmailIsValid() {
        String validEmail = "user@fatec.sp.gov.br";
        try {
            Authentication.setLoggedInUserEmail(validEmail);
            System.out.println("Test passed: Email set successfully for valid email.");
        } catch (IllegalArgumentException e) {
            System.err.println("Test failed: Unexpected IllegalArgumentException for valid email: " + e.getMessage());
        }
    }

    private static void getLoggedInUserEmail_shouldReturnNull_whenNoUserIsLoggedIn() {
        String email = Authentication.getLoggedInUserEmail();
        if (email == null) {
            System.out.println("Test passed: getLoggedInUserEmail returned null when no user is logged in.");
        } else {
            System.err.println("Test failed: getLoggedInUserEmail did not return null when no user is logged in.");
        }
    }

    private static void getLoggedInUserEmail_shouldReturnEmail_whenUserIsLoggedIn() {
        String validEmail = "user@fatec.sp.gov.br";
        try {
            Authentication.setLoggedInUserEmail(validEmail);
            String email = Authentication.getLoggedInUserEmail();
            if (validEmail.equals(email)) {
                System.out.println("Test passed: getLoggedInUserEmail returned the correct email when user is logged in.");
            } else {
                System.err.println("Test failed: getLoggedInUserEmail did not return the correct email when user is logged in.");
            }
        } catch (IllegalArgumentException e) {
            System.err.println("Test failed: Unexpected IllegalArgumentException while setting valid email: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        getLoggedInUserEmail_shouldReturnNull_whenNoUserIsLoggedIn(); // precisa executar primeiro, pois os outros testes setam o email
        setLoggedInUserEmail_shouldThrowIllegalArgumentException_whenEmailIsNull();
        setLoggedInUserEmail_shouldThrowIllegalArgumentException_whenEmailIsBlank();
        setLoggedInUserEmail_shouldThrowIllegalArgumentException_whenEmailIsNotInFatecDomain();
        setLoggedInUserEmail_shouldThrowIllegalArgumentException_whenEmailLocalPartIsEmpty();
        setLoggedInUserEmail_shouldSetEmailSuccessfully_whenEmailIsValid();
        getLoggedInUserEmail_shouldReturnEmail_whenUserIsLoggedIn();
    }
}
