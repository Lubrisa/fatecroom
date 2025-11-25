package br.gov.sp.fatec.fatecroom.persistence;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class UsersRepositoryTests {

	private static final Path USERS_FILE = Path.of("usuarios.csv");

	private static void deleteUsersFileQuietly() {
		try {
			Files.deleteIfExists(USERS_FILE);
		} catch (IOException ignored) { }
	}

	private static Map<String, String> validUser(String name, String type, String email, String password, String active) {
		var m = new HashMap<String, String>();
		m.put("nome_usuario", name);
		m.put("tipo_usuario", type);
		m.put("email", email);
		m.put("senha", password);
		m.put("ativo", active);
		return m;
	}

	private static void insert_shouldThrowIllegalArgumentException_whenUserDataIsNull() {
		try {
			UsersRepository.insert(null);
			System.err.println("Test failed: Expected IllegalArgumentException was not thrown.");
		} catch (IllegalArgumentException e) {
			if (!e.getMessage().toLowerCase().contains("nulos") && !e.getMessage().toLowerCase().contains("nulo"))
				System.err.println("Test warning: unexpected exception message: " + e.getMessage());
			else
				System.out.println("Test passed: Caught expected IllegalArgumentException for null user data.");
		} catch (Exception e) {
			System.err.println("Test failed: Caught unexpected exception type: " + e);
		}
	}

	private static void insert_shouldThrowIllegalArgumentException_whenIdProvided() {
		try {
			var user = validUser("Test User", "STUDENT", "user1@fatec.sp.gov.br", "0123456789ab", "true");
			user.put("id_usuario", "999");
			UsersRepository.insert(user);
			System.err.println("Test failed: Expected IllegalArgumentException was not thrown.");
		} catch (IllegalArgumentException e) {
			if (!e.getMessage().toLowerCase().contains("id do usuário") && !e.getMessage().toLowerCase().contains("id"))
				System.err.println("Test warning: unexpected exception message: " + e.getMessage());
			else
				System.out.println("Test passed: Caught expected IllegalArgumentException for provided id.");
		} catch (Exception e) {
			System.err.println("Test failed: Caught unexpected exception type: " + e);
		}
	}

	private static void insert_shouldInsertAndBeRetrievable_whenValid() {
		deleteUsersFileQuietly();
		try {
			var email = "valid.user@fatec.sp.gov.br";
			var password = "abcdefghijkl"; // 12 chars
			var user = validUser("Valid User", "STUDENT", email, password, "true");
			var inserted = UsersRepository.insert(user);

			if (!inserted.containsKey("id_usuario") || inserted.get("id_usuario").isBlank()) {
				System.err.println("Test failed: Inserted user did not contain generated id.");
				return;
			}

			Optional<Map<String, String>> found = UsersRepository.getByEmailAndPassword(email, password);
			if (found.isEmpty()) {
				System.err.println("Test failed: Could not retrieve inserted user by email and password.");
			} else {
				var retrieved = found.get();
				if (!retrieved.getOrDefault("email", "").equals(email))
					System.err.println("Test failed: Retrieved user email mismatch.");
			}
		} catch (Exception e) {
			System.err.println("Test failed: Caught unexpected exception: " + e);
		} finally {
			deleteUsersFileQuietly();
		}
	}

	private static void getByEmailAndPassword_shouldReturnEmpty_whenNotFound() {
		deleteUsersFileQuietly();
		try {
			var result = UsersRepository.getByEmailAndPassword("noone@fatec.sp.gov.br", "doesnotmatter");
			if (result.isPresent())
				System.err.println("Test failed: Expected empty result when file is empty.");
		} catch (Exception e) {
			System.err.println("Test failed: Caught unexpected exception: " + e);
		} finally {
			deleteUsersFileQuietly();
		}
	}

	private static void getRange_shouldReturnCorrectRange_whenEntriesExist() {
		deleteUsersFileQuietly();
		try {
			// insert three users
			UsersRepository.insert(validUser("A User", "STUDENT", "a@fatec.sp.gov.br", "password000001", "true"));
			UsersRepository.insert(validUser("B User", "PROFESSOR", "b@fatec.sp.gov.br", "password000002", "true"));
			UsersRepository.insert(validUser("C User", "ADMIN", "c@fatec.sp.gov.br", "password000003", "true"));

			List<Map<String, String>> range = UsersRepository.getRange(1, 1); // should return the second user
			if (range.size() != 1) {
				System.err.println("Test failed: Expected 1 result, got " + range.size());
			} else {
				var user = range.get(0);
				var email = user.getOrDefault("email", "");
				if (!email.equals("b@fatec.sp.gov.br"))
					System.err.println("Test failed: Range returned unexpected user: " + email);
				else
					System.out.println("Test passed: getRange returned the expected user.");
			}
		} catch (Exception e) {
			System.err.println("Test failed: Caught unexpected exception: " + e);
		} finally {
			deleteUsersFileQuietly();
		}
	}

	public static void main(String[] args) {
		insert_shouldThrowIllegalArgumentException_whenUserDataIsNull();
		insert_shouldThrowIllegalArgumentException_whenIdProvided();
		insert_shouldInsertAndBeRetrievable_whenValid();
		getByEmailAndPassword_shouldReturnEmpty_whenNotFound();
		getRange_shouldReturnCorrectRange_whenEntriesExist();
	}
}
