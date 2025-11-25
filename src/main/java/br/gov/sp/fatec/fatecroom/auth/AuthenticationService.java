package br.gov.sp.fatec.fatecroom.auth;

import br.gov.sp.fatec.fatecroom.persistence.UsersRepository;
import java.io.IOException;

/**
 * Handles user authentication by validating and storing the email of the logged-in user.
 */
public class AuthenticationService {
    private static String loggedInUserEmail = null;

    /**
     * Gets the email of the currently logged-in user.
     * @return The email of the logged-in user. If no user is logged in, returns null.
     */
    public static String getLoggedInUserEmail() {
        return loggedInUserEmail;
    }

    /**
     * Clears the logged-in user session.
     */
    public static void clearSession() {
        loggedInUserEmail = null;
    }

    public static void authenticateUser(String email, String password) throws IllegalArgumentException, IOException {
        var user = UsersRepository.getByEmailAndPassword(email, password);

        if (user.isEmpty()) {
            throw new IllegalArgumentException("Invalid email or password.");
        }

        loggedInUserEmail = email;
    }
}
