package br.gov.sp.fatec.fatecroom.auth;

/**
 * Handles user authentication by validating and storing the email of the logged-in user.
 */
public class AuthenticationService {
    private static final String FATEC_DOMAIN = "@fatec.sp.gov.br";

    private static String loggedInUserEmail = null;

    /**
     * Validates if the provided email belongs to the FATEC domain.
     * @param email The email to validate.
     * @throws IllegalArgumentException if the email is invalid.
     */
    private static void validateEmail(String email) throws IllegalArgumentException {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email cannot be null or empty.");
        }

        if (!email.toLowerCase().endsWith(FATEC_DOMAIN)) {
            throw new IllegalArgumentException("Email must belong to the FATEC domain.");
        }

        if (email.equals(FATEC_DOMAIN)) {
            throw new IllegalArgumentException("Email local part cannot be empty.");
        }
    }

    /**
     * Sets the email of the currently logged-in user after validation.
     * @param email The email to set as the logged-in user.
     * @return The validated email of the logged-in user.
     * @throws IllegalArgumentException if the email is invalid.
     */
    public static String setLoggedInUserEmail(String email) throws IllegalArgumentException {
        validateEmail(email);
        loggedInUserEmail = email;
        return loggedInUserEmail;
    }

    /**
     * Gets the email of the currently logged-in user.
     * @return The email of the logged-in user. If no user is logged in, returns null.
     */
    public static String getLoggedInUserEmail() {
        return loggedInUserEmail;
    }
}
