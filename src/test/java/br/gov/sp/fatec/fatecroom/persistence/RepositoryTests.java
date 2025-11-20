package br.gov.sp.fatec.fatecroom.persistence;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;

public class RepositoryTests {
    private static Path createTempCsvFile() throws IOException {
        return Files.createTempFile("test_repo_%d_%s".formatted(System.currentTimeMillis(), UUID.randomUUID().toString()), ".csv");
    }

    private static void deleteTempFile(Path path) throws IOException {
        if (path != null)
            Files.deleteIfExists(path);
    }

    private static void entryEquals(String line, Map<String, String> entry) {
        var values = line.split(",");
        var keys = entry.keySet().toArray(new String[entry.size()]);
        for (int i = 0; i < keys.length; i++) {
            if (!entry.get(keys[i]).equals(values[i])) {
                throw new AssertionError("Entry does not match. Expected: " + entry + ", Found line: " + line);
            }
        }
    }

    private static final Function<Map<String, String>, String> KEY_SELECTOR = entry -> entry.get("key");
    private static final Map<String, String> SAMPLE_DATA = Map.of("key", "1", "name", "Test");
    private static final Map<String, String> UPDATED_DATA = Map.of("key", "1", "name", "NewName");

    private static void insertOrUpdate_shouldThrowIllegalArgumentException_whenFilenameIsNull() {
        try {
            Repository.insertOrUpdate(null, SAMPLE_DATA, KEY_SELECTOR);
            System.err.println("Test failed: Expected IllegalArgumentException was not thrown.");
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("cannot be null")) {
                System.out.println("Test passed: Caught expected IllegalArgumentException for null filename.");
            } else {
                System.err.println("Test failed: Caught IllegalArgumentException, but with unexpected message: " + e.getMessage());
            }
        } catch (Exception e) {
            System.err.println("Test failed: Caught unexpected exception type: " + e);
        }
    }

    private static void insertOrUpdate_shouldThrowIllegalArgumentException_whenDataIsNull() {
        Path tempFile = null;
        try {
            tempFile = createTempCsvFile();
            Repository.insertOrUpdate(tempFile.toString(), null, KEY_SELECTOR);
            System.err.println("Test failed: Expected IllegalArgumentException was not thrown.");
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("cannot be null")) {
                System.out.println("Test passed: Caught expected IllegalArgumentException for null data.");
            } else {
                System.err.println("Test failed: Caught IllegalArgumentException, but with unexpected message: " + e.getMessage());
            }
        } catch (Exception e) {
            System.err.println("Test failed: Caught unexpected exception type: " + e);
        } finally {
            try {
                deleteTempFile(tempFile);
            } catch (IOException e) {
                System.err.println("Warning: Could not delete temporary file: " + e);
            }
        }
    }

    private static void insertOrUpdate_shouldThrowIllegalArgumentException_whenKeySelectorIsNull() {
        Path tempFile = null;
        try {
            tempFile = createTempCsvFile();
            Repository.insertOrUpdate(tempFile.toString(), SAMPLE_DATA, null);
            System.err.println("Test failed: Expected IllegalArgumentException was not thrown.");
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("cannot be null")) {
                System.out.println("Test passed: Caught expected IllegalArgumentException for null keySelector.");
            } else {
                System.err.println("Test failed: Caught IllegalArgumentException, but with unexpected message: " + e.getMessage());
            }
        } catch (Exception e) {
            System.err.println("Test failed: Caught unexpected exception type: " + e);
        } finally {
            try {
                deleteTempFile(tempFile);
            } catch (IOException e) {
                System.err.println("Warning: Could not delete temporary file: " + e);
            }
        }
    }

    private static void insertOrUpdate_shouldInsertHeader_whenFileIsEmpty() {
        Path tempFile = null;
        try {
            tempFile = createTempCsvFile();
            Repository.insertOrUpdate(tempFile.toString(), SAMPLE_DATA, KEY_SELECTOR);
            var lines = Files.readAllLines(tempFile);
            if (lines.size() >= 1) {
                var headerKeySet = Set.of(lines.get(0).split(","));
                if (headerKeySet.equals(SAMPLE_DATA.keySet())) {
                    System.out.println("Test passed: Header inserted successfully.");
                } else {
                    System.err.println("Test failed: Header keys do not match. Expected: " + SAMPLE_DATA.keySet() + ", Found: " + headerKeySet);
                }
            } else {
                System.err.println("Test failed: Header not inserted correctly. Lines: " + lines);
            }
        } catch (Exception e) {
            System.err.println("Test failed: Caught unexpected exception: " + e);
        } finally {
            try {
                deleteTempFile(tempFile);
            } catch (IOException e) {
                System.err.println("Warning: Could not delete temporary file: " + e);
            }
        }
    }

    private static void insertOrUpdate_shouldInsertNewEntry_whenFileIsEmpty() {
        Path tempFile = null;
        try {
            tempFile = createTempCsvFile();
            Repository.insertOrUpdate(tempFile.toString(), SAMPLE_DATA, KEY_SELECTOR);
            var lines = Files.readAllLines(tempFile);
            if (lines.size() == 2) {
                try {
                    entryEquals(lines.get(1), SAMPLE_DATA);
                    System.out.println("Test passed: New entry inserted successfully.");
                } catch (AssertionError e) {
                    System.err.println("Test failed: " + e.getMessage());
                }
            } else {
                System.err.println("Test failed: Entry not inserted correctly. Lines: " + lines);
            }
        } catch (Exception e) {
            System.err.println("Test failed: Caught unexpected exception: " + e);
        } finally {
            try {
                deleteTempFile(tempFile);
            } catch (IOException e) {
                System.err.println("Warning: Could not delete temporary file: " + e);
            }
        }
    }

    private static void insertOrUpdate_shouldUpdateExistingEntry_whenKeyExists() {
        Path tempFile = null;
        try {
            tempFile = createTempCsvFile();
            Repository.insertOrUpdate(tempFile.toString(), SAMPLE_DATA, KEY_SELECTOR);
            Repository.insertOrUpdate(tempFile.toString(), UPDATED_DATA, KEY_SELECTOR);
            var lines = Files.readAllLines(tempFile);
            if (lines.size() == 2) {
                try {
                    entryEquals(lines.get(1), UPDATED_DATA);
                    System.out.println("Test passed: Existing entry updated successfully.");
                } catch (AssertionError e) {
                    System.err.println("Test failed: " + e.getMessage());
                }
            } else {
                System.err.println("Test failed: Entry not updated correctly. Lines: " + lines);
            }
        } catch (Exception e) {
            System.err.println("Test failed: Caught unexpected exception: " + e);
        } finally {
            try {
                deleteTempFile(tempFile);
            } catch (IOException e) {
                System.err.println("Warning: Could not delete temporary file: " + e);
            }
        }
    }

    private static void delete_shouldThrowIllegalArgumentException_whenFilenameIsNull() {
        try {
            Repository.delete(null, "1", entry -> entry.get("key"));
            System.err.println("Test failed: Expected IllegalArgumentException was not thrown.");
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("cannot be null")) {
                System.out.println("Test passed: Caught expected IllegalArgumentException for null filename.");
            } else {
                System.err.println("Test failed: Caught IllegalArgumentException, but with unexpected message: " + e.getMessage());
            }
        } catch (Exception e) {
            System.err.println("Test failed: Caught unexpected exception type: " + e);
        }
    }

    private static void delete_shouldThrowIllegalArgumentException_whenKeyIsNull() {
        Path tempFile = null;
        try {
            tempFile = createTempCsvFile();
            Repository.delete(tempFile.toString(), null, entry -> entry.get("key"));
            System.err.println("Test failed: Expected IllegalArgumentException was not thrown.");
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("cannot be null")) {
                System.out.println("Test passed: Caught expected IllegalArgumentException for null key.");
            } else {
                System.err.println("Test failed: Caught IllegalArgumentException, but with unexpected message: " + e.getMessage());
            }
        } catch (Exception e) {
            System.err.println("Test failed: Caught unexpected exception type: " + e);
        } finally {
            try {
                deleteTempFile(tempFile);
            } catch (IOException e) {
                System.err.println("Warning: Could not delete temporary file: " + e);
            }
        }
    }

    private static void delete_shouldThrowIllegalArgumentException_whenKeyIsBlank() {
        Path tempFile = null;
        try {
            tempFile = createTempCsvFile();
            Repository.delete(tempFile.toString(), "   ", entry -> entry.get("key"));
            System.err.println("Test failed: Expected IllegalArgumentException was not thrown.");
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("blank")) {
                System.out.println("Test passed: Caught expected IllegalArgumentException for blank key.");
            } else {
                System.err.println("Test failed: Caught IllegalArgumentException, but with unexpected message: " + e.getMessage());
            }
        } catch (Exception e) {
            System.err.println("Test failed: Caught unexpected exception type: " + e);
        } finally {
            try {
                deleteTempFile(tempFile);
            } catch (IOException e) {
                System.err.println("Warning: Could not delete temporary file: " + e);
            }
        }
    }

    private static void delete_shouldReturnFalse_whenFileIsEmpty() {
        Path tempFile = null;
        try {
            tempFile = createTempCsvFile();
            var result = Repository.delete(tempFile.toString(), "1", entry -> entry.get("key"));
            if (!result) {
                System.out.println("Test passed: Delete returned false for empty file.");
            } else {
                System.err.println("Test failed: Delete should return false for empty file.");
            }
        } catch (Exception e) {
            System.err.println("Test failed: Caught unexpected exception: " + e);
        } finally {
            try {
                deleteTempFile(tempFile);
            } catch (IOException e) {
                System.err.println("Warning: Could not delete temporary file: " + e);
            }
        }
    }

    private static void delete_shouldThrowIllegalArgumentException_whenKeySelectorIsNull() {
        Path tempFile = null;
        try {
            tempFile = createTempCsvFile();
            Repository.delete(tempFile.toString(), "1", null);
            System.err.println("Test failed: Expected IllegalArgumentException was not thrown.");
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("cannot be null")) {
                System.out.println("Test passed: Caught expected IllegalArgumentException for null keySelector.");
            } else {
                System.err.println("Test failed: Caught IllegalArgumentException, but with unexpected message: " + e.getMessage());
            }
        } catch (Exception e) {
            System.err.println("Test failed: Caught unexpected exception type: " + e);
        } finally {
            try {
                deleteTempFile(tempFile);
            } catch (IOException e) {
                System.err.println("Warning: Could not delete temporary file: " + e);
            }
        }
    }

    private static void delete_shouldReturnFalse_whenEntryDoesNotExist() {
        Path tempFile = null;
        try {
            tempFile = createTempCsvFile();
            Repository.insertOrUpdate(tempFile.toString(), SAMPLE_DATA, KEY_SELECTOR);
            var result = Repository.delete(tempFile.toString(), "2", entry -> entry.get("key"));
            if (!result) {
                System.out.println("Test passed: Delete returned false for non-existing entry.");
            } else {
                System.err.println("Test failed: Delete should return false for non-existing entry.");
            }
        } catch (Exception e) {
            System.err.println("Test failed: Caught unexpected exception: " + e);
        } finally {
            try {
                deleteTempFile(tempFile);
            } catch (IOException e) {
                System.err.println("Warning: Could not delete temporary file: " + e);
            }
        }
    }

    private static void delete_shouldReturnTrue_whenEntryExists() {
        Path tempFile = null;
        try {
            tempFile = createTempCsvFile();
            Repository.insertOrUpdate(tempFile.toString(), SAMPLE_DATA, KEY_SELECTOR);
            var result = Repository.delete(tempFile.toString(), "1", entry -> entry.get("key"));
            if (result) {
                var lines = Files.readAllLines(tempFile);
                if (lines.size() == 1) { // only header
                    System.out.println("Test passed: Entry deleted successfully.");
                } else {
                    System.err.println("Test failed: Entry not deleted. Lines: " + lines);
                }
            } else {
                System.err.println("Test failed: Delete should return true for existing entry.");
            }
        } catch (Exception e) {
            System.err.println("Test failed: Caught unexpected exception: " + e);
        } finally {
            try {
                deleteTempFile(tempFile);
            } catch (IOException e) {
                System.err.println("Warning: Could not delete temporary file: " + e);
            }
        }
    }

    private static void getByKey_shouldThrowIllegalArgumentException_whenFilenameIsNull() {
        try {
            Repository.getByKey(null, "1", entry -> entry.get("key"));
            System.err.println("Test failed: Expected IllegalArgumentException was not thrown.");
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("cannot be null")) {
                System.out.println("Test passed: Caught expected IllegalArgumentException for null filename.");
            } else {
                System.err.println("Test failed: Caught IllegalArgumentException, but with unexpected message: " + e.getMessage());
            }
        } catch (Exception e) {
            System.err.println("Test failed: Caught unexpected exception type: " + e);
        }
    }

    private static void getByKey_shouldThrowIllegalArgumentException_whenKeyIsNull() {
        Path tempFile = null;
        try {
            tempFile = createTempCsvFile();
            Repository.getByKey(tempFile.toString(), null, entry -> entry.get("key"));
            System.err.println("Test failed: Expected IllegalArgumentException was not thrown.");
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("cannot be null")) {
                System.out.println("Test passed: Caught expected IllegalArgumentException for null key.");
            } else {
                System.err.println("Test failed: Caught IllegalArgumentException, but with unexpected message: " + e.getMessage());
            }
        } catch (Exception e) {
            System.err.println("Test failed: Caught unexpected exception type: " + e);
        } finally {
            try {
                deleteTempFile(tempFile);
            } catch (IOException e) {
                System.err.println("Warning: Could not delete temporary file: " + e);
            }
        }
    }

    private static void getByKey_shouldThrowIllegalArgumentException_whenKeyIsBlank() {
        Path tempFile = null;
        try {
            tempFile = createTempCsvFile();
            Repository.getByKey(tempFile.toString(), "   ", entry -> entry.get("key"));
            System.err.println("Test failed: Expected IllegalArgumentException was not thrown.");
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("blank")) {
                System.out.println("Test passed: Caught expected IllegalArgumentException for blank key.");
            } else {
                System.err.println("Test failed: Caught IllegalArgumentException, but with unexpected message: " + e.getMessage());
            }
        } catch (Exception e) {
            System.err.println("Test failed: Caught unexpected exception type: " + e);
        } finally {
            try {
                deleteTempFile(tempFile);
            } catch (IOException e) {
                System.err.println("Warning: Could not delete temporary file: " + e);
            }
        }
    }

    private static void getByKey_shouldThrowIllegalArgumentException_whenKeySelectorIsNull() {
        Path tempFile = null;
        try {
            tempFile = createTempCsvFile();
            Repository.getByKey(tempFile.toString(), "1", null);
            System.err.println("Test failed: Expected IllegalArgumentException was not thrown.");
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("cannot be null")) {
                System.out.println("Test passed: Caught expected IllegalArgumentException for null keySelector.");
            } else {
                System.err.println("Test failed: Caught IllegalArgumentException, but with unexpected message: " + e.getMessage());
            }
        } catch (Exception e) {
            System.err.println("Test failed: Caught unexpected exception type: " + e);
        } finally {
            try {
                deleteTempFile(tempFile);
            } catch (IOException e) {
                System.err.println("Warning: Could not delete temporary file: " + e);
            }
        }
    }

    private static void getByKey_shouldReturnEmpty_whenFileIsEmpty() {
        Path tempFile = null;
        try {
            tempFile = createTempCsvFile();
            var result = Repository.getByKey(tempFile.toString(), "1", entry -> entry.get("key"));
            if (result.isEmpty()) {
                System.out.println("Test passed: getByKey returned empty for empty file.");
            } else {
                System.err.println("Test failed: getByKey should return empty for empty file.");
            }
        } catch (Exception e) {
            System.err.println("Test failed: Caught unexpected exception: " + e);
        } finally {
            try {
                deleteTempFile(tempFile);
            } catch (IOException e) {
                System.err.println("Warning: Could not delete temporary file: " + e);
            }
        }
    }

    private static void getByKey_shouldReturnEmpty_whenKeyDoesNotExist() {
        Path tempFile = null;
        try {
            tempFile = createTempCsvFile();
            Repository.insertOrUpdate(tempFile.toString(), SAMPLE_DATA, KEY_SELECTOR);
            var result = Repository.getByKey(tempFile.toString(), "2", entry -> entry.get("key"));
            if (result.isEmpty()) {
                System.out.println("Test passed: getByKey returned empty for non-existing key.");
            } else {
                System.err.println("Test failed: getByKey should return empty for non-existing key. Result: " + result);
            }
        } catch (Exception e) {
            System.err.println("Test failed: Caught unexpected exception: " + e);
        } finally {
            try {
                deleteTempFile(tempFile);
            } catch (IOException e) {
                System.err.println("Warning: Could not delete temporary file: " + e);
            }
        }
    }

    private static void getByKey_shouldReturnEntry_whenKeyExists() {
        Path tempFile = null;
        try {
            tempFile = createTempCsvFile();
            Repository.insertOrUpdate(tempFile.toString(), SAMPLE_DATA, KEY_SELECTOR);
            var result = Repository.getByKey(tempFile.toString(), "1", entry -> entry.get("key"));
            if (result.isPresent() && "Test".equals(result.get().get("name"))) {
                System.out.println("Test passed: getByKey returned correct entry.");
            } else {
                System.err.println("Test failed: getByKey did not return correct entry. Result: " + result);
            }
        } catch (Exception e) {
            System.err.println("Test failed: Caught unexpected exception: " + e);
        } finally {
            try {
                deleteTempFile(tempFile);
            } catch (IOException e) {
                System.err.println("Warning: Could not delete temporary file: " + e);
            }
        }
    }

    private static void getFirstByPredicate_shouldThrowIllegalArgumentException_whenFilenameIsNull() {
        try {
            Repository.getFirstByPredicate(null, entry -> true);
            System.err.println("Test failed: Expected IllegalArgumentException was not thrown.");
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("cannot be null")) {
                System.out.println("Test passed: Caught expected IllegalArgumentException for null filename.");
            } else {
                System.err.println("Test failed: Caught IllegalArgumentException, but with unexpected message: " + e.getMessage());
            }
        } catch (Exception e) {
            System.err.println("Test failed: Caught unexpected exception type: " + e);
        }
    }

    private static void getFirstByPredicate_shouldThrowIllegalArgumentException_whenPredicateIsNull() {
        Path tempFile = null;
        try {
            tempFile = createTempCsvFile();
            Repository.getFirstByPredicate(tempFile.toString(), null);
            System.err.println("Test failed: Expected IllegalArgumentException was not thrown.");
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("cannot be null")) {
                System.out.println("Test passed: Caught expected IllegalArgumentException for null predicate.");
            } else {
                System.err.println("Test failed: Caught IllegalArgumentException, but with unexpected message: " + e.getMessage());
            }
        } catch (Exception e) {
            System.err.println("Test failed: Caught unexpected exception type: " + e);
        } finally {
            try {
                deleteTempFile(tempFile);
            } catch (IOException e) {
                System.err.println("Warning: Could not delete temporary file: " + e);
            }
        }
    }

    private static void getFirstByPredicate_shouldReturnEmpty_whenNoMatch() {
        Path tempFile = null;
        try {
            tempFile = createTempCsvFile();
            Repository.insertOrUpdate(tempFile.toString(), SAMPLE_DATA, KEY_SELECTOR);
            var result = Repository.getFirstByPredicate(tempFile.toString(), entry -> "NonExistent".equals(entry.get("name")));
            if (result.isEmpty()) {
                System.out.println("Test passed: getByPredicate returned empty when no match.");
            } else {
                System.err.println("Test failed: getByPredicate should return empty when no match. Result: " + result);
            }
        } catch (Exception e) {
            System.err.println("Test failed: Caught unexpected exception: " + e);
        } finally {
            try {
                deleteTempFile(tempFile);
            } catch (IOException e) {
                System.err.println("Warning: Could not delete temporary file: " + e);
            }
        }
    }

    private static void getFirstByPredicate_shouldReturnEntry_whenPredicateMatches() {
        Path tempFile = null;
        try {
            tempFile = createTempCsvFile();
            Repository.insertOrUpdate(tempFile.toString(), SAMPLE_DATA, KEY_SELECTOR);
            var result = Repository.getFirstByPredicate(tempFile.toString(), entry -> "Test".equals(entry.get("name")));
            if (result.isPresent() && result.get().equals(SAMPLE_DATA)) {
                System.out.println("Test passed: getByPredicate returned correct entry.");
            } else {
                System.err.println("Test failed: getByPredicate did not return correct entry. Result: " + result);
            }
        } catch (Exception e) {
            System.err.println("Test failed: Caught unexpected exception: " + e);
        } finally {
            try {
                deleteTempFile(tempFile);
            } catch (IOException e) {
                System.err.println("Warning: Could not delete temporary file: " + e);
            }
        }
    }

    private static void getRange_shouldReturnEmptyList_whenFileIsEmpty() {
        Path tempFile = null;
        try {
            tempFile = createTempCsvFile();
            var result = Repository.getRange(tempFile.toString(), 0, 10);
            if (result.isEmpty()) {
                System.out.println("Test passed: getRange returned empty list for empty file.");
            } else {
                System.err.println("Test failed: getRange should return empty list for empty file.");
            }
        } catch (Exception e) {
            System.err.println("Test failed: Caught unexpected exception: " + e);
        } finally {
            try {
                deleteTempFile(tempFile);
            } catch (IOException e) {
                System.err.println("Warning: Could not delete temporary file: " + e);
            }
        }
    }

    private static void getRange_shouldReturnCorrectRange_whenEntriesExist() {
        Path tempFile = null;
        try {
            tempFile = createTempCsvFile();
            Repository.insertOrUpdate(tempFile.toString(), Map.of("key", "1", "name", "A"), KEY_SELECTOR);
            Repository.insertOrUpdate(tempFile.toString(), Map.of("key", "2", "name", "B"), KEY_SELECTOR);
            Repository.insertOrUpdate(tempFile.toString(), Map.of("key", "3", "name", "C"), KEY_SELECTOR);
            Repository.insertOrUpdate(tempFile.toString(), Map.of("key", "4", "name", "D"), KEY_SELECTOR);
            var result = Repository.getRange(tempFile.toString(), 1, 2);
            if (result.size() == 2 && "B".equals(result.get(0).get("name")) && "C".equals(result.get(1).get("name"))) {
                System.out.println("Test passed: getRange returned correct range.");
            } else {
                System.err.println("Test failed: getRange did not return correct range. Result: " + result);
            }
        } catch (Exception e) {
            System.err.println("Test failed: Caught unexpected exception: " + e);
        } finally {
            try {
                deleteTempFile(tempFile);
            } catch (IOException e) {
                System.err.println("Warning: Could not delete temporary file: " + e);
            }
        }
    }

    private static void getRange_shouldThrowIllegalArgumentException_whenSkipIsNegative() {
        Path tempFile = null;
        try {
            tempFile = createTempCsvFile();
            Repository.getRange(tempFile.toString(), -1, 10);
            System.err.println("Test failed: Expected IllegalArgumentException was not thrown.");
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("must be non-negative")) {
                System.out.println("Test passed: Caught expected IllegalArgumentException for negative skip.");
            } else {
                System.err.println("Test failed: Caught IllegalArgumentException, but with unexpected message: " + e.getMessage());
            }
        } catch (Exception e) {
            System.err.println("Test failed: Caught unexpected exception type: " + e);
        } finally {
            try {
                deleteTempFile(tempFile);
            } catch (IOException e) {
                System.err.println("Warning: Could not delete temporary file: " + e);
            }
        }
    }

    private static void getRange_shouldThrowIllegalArgumentException_whenTakeIsNotPositive() {
        Path tempFile = null;
        try {
            tempFile = createTempCsvFile();
            Repository.getRange(tempFile.toString(), 0, 0);
            System.err.println("Test failed: Expected IllegalArgumentException was not thrown.");
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("must be positive")) {
                System.out.println("Test passed: Caught expected IllegalArgumentException for non-positive take.");
            } else {
                System.err.println("Test failed: Caught IllegalArgumentException, but with unexpected message: " + e.getMessage());
            }
        } catch (Exception e) {
            System.err.println("Test failed: Caught unexpected exception type: " + e);
        } finally {
            try {
                deleteTempFile(tempFile);
            } catch (IOException e) {
                System.err.println("Warning: Could not delete temporary file: " + e);
            }
        }
    }

    public static void main(String[] args) {
        insertOrUpdate_shouldThrowIllegalArgumentException_whenFilenameIsNull();
        insertOrUpdate_shouldThrowIllegalArgumentException_whenDataIsNull();
        insertOrUpdate_shouldThrowIllegalArgumentException_whenKeySelectorIsNull();
        insertOrUpdate_shouldInsertHeader_whenFileIsEmpty();
        insertOrUpdate_shouldInsertNewEntry_whenFileIsEmpty();
        insertOrUpdate_shouldUpdateExistingEntry_whenKeyExists();
        delete_shouldThrowIllegalArgumentException_whenFilenameIsNull();
        delete_shouldThrowIllegalArgumentException_whenKeyIsNull();
        delete_shouldThrowIllegalArgumentException_whenKeyIsBlank();
        delete_shouldReturnFalse_whenFileIsEmpty();
        delete_shouldThrowIllegalArgumentException_whenKeySelectorIsNull();
        delete_shouldReturnFalse_whenEntryDoesNotExist();
        delete_shouldReturnTrue_whenEntryExists();
        getByKey_shouldThrowIllegalArgumentException_whenFilenameIsNull();
        getByKey_shouldThrowIllegalArgumentException_whenKeyIsNull();
        getByKey_shouldThrowIllegalArgumentException_whenKeyIsBlank();
        getByKey_shouldThrowIllegalArgumentException_whenKeySelectorIsNull();
        getByKey_shouldReturnEmpty_whenFileIsEmpty();
        getByKey_shouldReturnEmpty_whenKeyDoesNotExist();
        getByKey_shouldReturnEntry_whenKeyExists();
        getFirstByPredicate_shouldThrowIllegalArgumentException_whenFilenameIsNull();
        getFirstByPredicate_shouldThrowIllegalArgumentException_whenPredicateIsNull();
        getFirstByPredicate_shouldReturnEmpty_whenNoMatch();
        getFirstByPredicate_shouldReturnEntry_whenPredicateMatches();
        getRange_shouldReturnEmptyList_whenFileIsEmpty();
        getRange_shouldReturnCorrectRange_whenEntriesExist();
        getRange_shouldThrowIllegalArgumentException_whenSkipIsNegative();
        getRange_shouldThrowIllegalArgumentException_whenTakeIsNotPositive();
    }
}
