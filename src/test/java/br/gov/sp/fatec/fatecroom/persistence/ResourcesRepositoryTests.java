package br.gov.sp.fatec.fatecroom.persistence;

import br.gov.sp.fatec.fatecroom.models.Resource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ResourcesRepositoryTests {

    private static final Path FILE = Path.of(ResourcesRepository.FILE_NAME);

    private static void deleteFileQuietly() {
        try { Files.deleteIfExists(FILE); } catch (IOException ignored) { }
    }

    private static Map<String, String> validResource(String name, String patrimony, String location) {
        var m = new HashMap<String, String>();
        m.put(Resource.NAME_FIELD, name);
        m.put(Resource.RESOURCE_TYPE_FIELD, Resource.PROJECTOR_TYPE);
        m.put(Resource.PATRIMONY_FIELD, patrimony);
        m.put(Resource.DEFAULT_LOCATION_FIELD, location);
        m.put(Resource.OBSERVATION_FIELD, "ok");
        return m;
    }

    private static void insert_shouldThrowIllegalArgumentException_whenResourceIsNull() {
        try {
            ResourcesRepository.insert(null);
            System.err.println("Test failed: Expected IllegalArgumentException was not thrown.");
        } catch (IllegalArgumentException e) {
            if (!e.getMessage().toLowerCase().contains("nulo"))
                System.err.println("Test warning: unexpected exception message: " + e.getMessage());
            else
                System.out.println("Test passed: Caught expected IllegalArgumentException for null resource data.");
        } catch (Exception e) {
            System.err.println("Test failed: Caught unexpected exception type: " + e);
        }
    }

    private static void insert_shouldThrowIllegalArgumentException_whenIdProvided() {
        try {
            var r = validResource("R1", "123", "B1");
            r.put(Resource.ID_FIELD, "999");
            ResourcesRepository.insert(r);
            System.err.println("Test failed: Expected IllegalArgumentException was not thrown.");
        } catch (IllegalArgumentException e) {
            if (!e.getMessage().toLowerCase().contains("id"))
                System.err.println("Test warning: unexpected exception message: " + e.getMessage());
            else
                System.out.println("Test passed: Caught expected IllegalArgumentException for provided id.");
        } catch (Exception e) {
            System.err.println("Test failed: Caught unexpected exception type: " + e);
        }
    }

    private static void insert_shouldInsertAndBeRetrievable_whenValid() {
        deleteFileQuietly();
        try {
            var inserted = ResourcesRepository.insert(validResource("Valid", "1000", "B2"));
            if (!inserted.containsKey(Resource.ID_FIELD) || inserted.get(Resource.ID_FIELD).isBlank()) {
                System.err.println("Test failed: Inserted resource did not contain generated id.");
                return;
            }

            List<Map<String, String>> range = ResourcesRepository.getRange(0, 10);
            if (range.isEmpty()) {
                System.err.println("Test failed: getRange returned empty after insert.");
            } else {
                boolean found = range.stream().anyMatch(m -> "Valid".equals(m.get(Resource.NAME_FIELD)));
                if (!found) System.err.println("Test failed: Inserted resource not found in range.");
            }
        } catch (Exception e) {
            System.err.println("Test failed: Caught unexpected exception: " + e);
        } finally {
            deleteFileQuietly();
        }
    }

    private static void getRange_shouldReturnCorrectRange_whenEntriesExist() {
        deleteFileQuietly();
        try {
            ResourcesRepository.insert(validResource("Aaa", "1", "X"));
            ResourcesRepository.insert(validResource("Bbb", "2", "X"));
            ResourcesRepository.insert(validResource("Ccc", "3", "X"));

            List<Map<String, String>> range = ResourcesRepository.getRange(1, 1);
            if (range.size() != 1) {
                System.err.println("Test failed: Expected 1 result, got " + range.size());
            } else if (!"Bbb".equals(range.get(0).get(Resource.NAME_FIELD))) {
                System.err.println("Test failed: Range returned unexpected resource: " + range.get(0));
            } else {
                System.out.println("Test passed: getRange returned the expected resource.");
            }
        } catch (Exception e) {
            System.err.println("Test failed: Caught unexpected exception: " + e);
        } finally {
            deleteFileQuietly();
        }
    }

    public static void main(String[] args) {
        insert_shouldThrowIllegalArgumentException_whenResourceIsNull();
        insert_shouldThrowIllegalArgumentException_whenIdProvided();
        insert_shouldInsertAndBeRetrievable_whenValid();
        getRange_shouldReturnCorrectRange_whenEntriesExist();
    }
}
