package br.gov.sp.fatec.fatecroom.persistence;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import br.gov.sp.fatec.fatecroom.models.User;

public class UsersRepository {
    private static final String FILE_NAME = "usuarios.csv";

    private static int sequence = 1;

    /**
     * Inserts a new user into the repository.
     * 
     * @param userData A map containing the user data.
     * @return The inserted user data with the generated ID.
     * @throws IllegalArgumentException If the user data is invalid.
     * @throws IOException If an I/O error occurs during the operation.
     */
    public static Map<String, String> insert(Map<String, String> userData) throws IllegalArgumentException, IOException {
        if (userData == null) {
            throw new IllegalArgumentException("Dados do usuário não podem ser nulos.");
        } else if (userData.containsKey(User.ID_FIELD)) {
            throw new IllegalArgumentException("ID do usuário não deve ser fornecido na inserção.");
        }

        User.validate(userData);
        
        var userDataWithId = new HashMap<String, String>(userData);
        userDataWithId.put(User.ID_FIELD, String.valueOf(sequence++));
        
        Repository.insertEnsuringUniqueness(FILE_NAME, userDataWithId, user -> user.get(User.ID_FIELD));
        
        return userDataWithId;
    }

    /**
     * Retrieves a user by email and password.
     * 
     * @param email The email of the user.
     * @param password The password of the user.
     * @return An Optional containing the user data if found, or empty otherwise.
     * @throws IOException If an I/O error occurs during the operation.
     */
    public static Optional<Map<String, String>> getByEmailAndPassword(String email, String password) throws IOException {
        return Repository.getFirstByPredicate(
            FILE_NAME,
            user -> user.getOrDefault(User.EMAIL_FIELD, "").equals(email) &&
                    user.getOrDefault(User.PASSWORD_FIELD, "").equals(password)
        );
    }

    /**
     * Retrieves a range of users from the repository.
     * 
     * @param skip The number of users to skip.
     * @param take The number of users to retrieve.
     * @return A list of user data maps within the specified range.
     * @throws IOException If an I/O error occurs during the operation.
     */
    public static List<Map<String, String>> getRange(int skip, int take) throws IOException {
        return Repository.getRange(
            FILE_NAME,
            skip,
            take
        );
    }
}
