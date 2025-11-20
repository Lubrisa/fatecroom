package br.gov.sp.fatec.fatecroom.persistence;

import br.gov.sp.fatec.fatecroom.auth.Authentication;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Predicate;

import br.gov.sp.fatec.fatecroom.logging.Logger;

final class Repository {
    private Repository() { }

    private static Map<String, String> parseLine(String line, String[] headers) {
        final var values = line.split(",");
        final var entry = new HashMap<String, String>();

        for (int i = 0; i < headers.length; i++)
            entry.put(headers[i], i < values.length ? values[i] : "");
        
        return entry;
    }

    private static Map<String, String> updateEntry(Map<String, String> original, Map<String, String> updated) {
        final var merged = new HashMap<String, String>(original);
        
        for (var entry : updated.entrySet())
            merged.put(entry.getKey(), entry.getValue());
        
        return merged;
    }

    private static String mapToLine(Map<String, String> entry, String[] headers) {
        final var values = new String[headers.length];

        for (int i = 0; i < headers.length; i++)
            values[i] = entry.getOrDefault(headers[i], "");
        
        return String.join(",", values);
    }

    private static String headersOf(Map<String, String> entry) {
        final var values = new String[entry.size()];
        int i = 0;
        for (var key : entry.keySet()) {
            values[i++] = key;
        }
        return String.join(",", values);
    }

    private static File getOrCreate(String filename) throws IOException {
        var file = new File(filename);

        if (!file.exists()) {
            var parent = file.getParentFile();
            if (parent != null && !parent.exists() && !parent.mkdirs())
                throw new IOException("Could not create directories for: " + filename);

            if (!file.createNewFile())
                throw new IOException("Could not create file: " + filename);
        }
        
        return file;
    }

    private static void replaceFile(File original, File replacement) throws IOException {
        try {
            Files.move(replacement.toPath(), original.toPath(), StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
        } catch (AtomicMoveNotSupportedException e) {
            Files.move(replacement.toPath(), original.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }
    }

    /**
     * Inserts a new entry or updates an existing one based on the key selector.
     * 
     * @param filename The name of the file where the entry will be inserted or updated.
     * @param data The entry data to insert or update.
     * @param keySelector A function to select the key from the entry data.
     * @throws IOException If an I/O error occurs during the operation.
     */
    public static void insertOrUpdate(String filename, Map<String, String> data, Function<Map<String, String>, String> keySelector) throws IOException {
        if (filename == null || filename.isBlank())
            throw new IllegalArgumentException("Filename cannot be null or blank.");
        if (data == null || data.isEmpty())
            throw new IllegalArgumentException("Data cannot be null or empty.");
        if (keySelector == null)
            throw new IllegalArgumentException("Key selector cannot be null.");

        Logger.logNow(Authentication.getLoggedInUserEmail(), "INSERT_OR_UPDATE", "Inserting or updating entry with key %s into %s".formatted(keySelector.apply(data), filename));

        var file = getOrCreate(filename);
        var temp = File.createTempFile("repo_" + UUID.randomUUID().toString(), ".tmp");

        Logger.logNow(Authentication.getLoggedInUserEmail(), "INSERT_OR_UPDATE", "Using temp file %s".formatted(temp.getAbsolutePath()));

        String headerLine;
        var updated = false;

        try (var reader = new BufferedReader(new FileReader(file));
            var writer = new BufferedWriter(new FileWriter(temp))) {
            headerLine = reader.readLine();

            if (headerLine == null)
                headerLine = headersOf(data);

            writer.write(headerLine);
            writer.newLine();

            var headers = headerLine.split(",");

            String line;
            while ((line = reader.readLine()) != null) {
                var entry = parseLine(line, headers);
                if (keySelector.apply(entry).equals(keySelector.apply(data))) {
                    var merged = updateEntry(entry, data);
                    writer.write(mapToLine(merged, headers));
                    updated = true;
                } else {
                    writer.write(line);
                }
                writer.newLine();
            }

            if (!updated) {
                writer.write(mapToLine(data, headers));
                writer.newLine();
            }

            writer.flush();

            Logger.logNow(Authentication.getLoggedInUserEmail(), "INSERT_OR_UPDATE", "Insert or update entry with key %s completed successfully.".formatted(keySelector.apply(data)));
        } catch (Exception e) {
            Logger.logNow(Authentication.getLoggedInUserEmail(), "INSERT_OR_UPDATE", "Error during insert or update entry with key %s: %s".formatted(keySelector.apply(data), e.getMessage()));
            throw e;
        } finally {
            replaceFile(file, temp);
        }
    }

    /**
     * Deletes an entry by key.
     * 
     * @param filename The name of the file from which the entry will be deleted.
     * @param key The key of the entry to delete.
     * @param keySelector A function to select the key from the entry data.
     * @return True if the entry was found and deleted, false otherwise.
     * @throws IOException If an I/O error occurs during the operation.
     */
    public static boolean delete(String filename, String key, Function<Map<String, String>, String> keySelector) throws IOException {
        if (filename == null || filename.isBlank())
            throw new IllegalArgumentException("Filename cannot be null or blank.");
        if (key == null || key.isBlank())
            throw new IllegalArgumentException("Key cannot be null or blank.");
        if (keySelector == null)
            throw new IllegalArgumentException("Key selector cannot be null.");

        boolean foundAndDeleted = false;

        Logger.logNow(Authentication.getLoggedInUserEmail(), "DELETE", "Deleting entry with key %s from %s".formatted(key, filename));
        
        var file = getOrCreate(filename);
        var temp = File.createTempFile("repo_" + UUID.randomUUID().toString(), ".tmp");

        Logger.logNow(Authentication.getLoggedInUserEmail(), "DELETE", "Using temp file %s".formatted(temp.getAbsolutePath()));

        try (var reader = new BufferedReader(new FileReader(file));
             var writer = new BufferedWriter(new FileWriter(temp))) {
            var headerLine = reader.readLine();

            if (headerLine == null)
                return false;

            var headers = headerLine.split(",");
            writer.write(headerLine);
            writer.newLine();

            String line;
            while ((line = reader.readLine()) != null) {
                var entry = parseLine(line, headers);
                if (keySelector.apply(entry).equals(key)) {
                    foundAndDeleted = true;
                    continue; // skip writing this line to delete it
                }
                writer.write(line);
                writer.newLine();
            }

            writer.flush();

            Logger.logNow(Authentication.getLoggedInUserEmail(), "DELETE", "Delete entry with key %s completed successfully.".formatted(key));
        } catch (Exception e) {
            Logger.logNow(Authentication.getLoggedInUserEmail(), "DELETE", "Error during delete entry with key %s: %s".formatted(key, e.getMessage()));
            throw e;
        } finally {
            replaceFile(file, temp);
        }

        return foundAndDeleted;
    }

    /**
     * Gets an entry by key.
     * 
     * @param fileName The name of the file to search.
     * @param key The key of the entry to find.
     * @param keySelector A function to select the key from the entry data.
     * @return An Optional containing the found entry, or empty if not found.
     * @throws IOException If an I/O error occurs during the operation.
     */
    public static Optional<Map<String, String>> getByKey(String fileName, String key, Function<Map<String, String>, String> keySelector) throws IOException {
        if (key == null || key.isBlank())
            throw new IllegalArgumentException("Key cannot be null or blank.");
        if (keySelector == null)
            throw new IllegalArgumentException("Key selector cannot be null.");

        return getFirstByPredicate(fileName, entry -> keySelector.apply(entry).equals(key));
    }

    /**
     * Gets the first entry matching a predicate.
     * 
     * @param fileName The name of the file to search.
     * @param predicate A predicate to test entries.
     * @return An Optional containing the found entry, or empty if not found.
     * @throws IOException If an I/O error occurs during the operation.
     */
    public static Optional<Map<String, String>> getFirstByPredicate(String fileName, Predicate<Map<String, String>> predicate) throws IOException {
        if (fileName == null || fileName.isBlank())
            throw new IllegalArgumentException("Filename cannot be null or blank.");
        if (predicate == null)
            throw new IllegalArgumentException("Predicate cannot be null.");

        var file = getOrCreate(fileName);

        if (file.length() == 0)
            return Optional.empty();

        try (var reader = new BufferedReader(new FileReader(file))) {
            var headerLine = reader.readLine();

            if (headerLine == null)
                return Optional.empty();

            var headers = headerLine.split(",");

            String line;
            while ((line = reader.readLine()) != null) {
                var entry = parseLine(line, headers);
                if (predicate.test(entry)) {
                    return Optional.of(entry);
                }
            }
        }

        return Optional.empty();
    }

    /**
     * Gets a range of entries matching a predicate.
     * @param filename The name of the file to search.
     * @param predicate A predicate to test entries.
     * @param skip The number of entries to skip.
     * @param take The number of entries to take.
     * @return A list of entries within the specified range that match the predicate.
     * @throws IOException If an I/O error occurs during the operation.
     */
    public static List<Map<String, String>> getRangeByPredicate(String filename, Predicate<Map<String, String>> predicate, int skip, int take) throws IOException {
        if (filename == null || filename.isBlank())
            throw new IllegalArgumentException("Filename cannot be null or blank.");
        if (predicate == null)
            throw new IllegalArgumentException("Predicate cannot be null.");
        if (skip < 0 || take <= 0)
            throw new IllegalArgumentException("Skip must be non-negative and take must be positive.");

        var file = getOrCreate(filename);

        if (file.length() == 0)
            return new ArrayList<>();

        var results = new ArrayList<Map<String, String>>();

        try (var reader = new BufferedReader(new FileReader(file))) {
            var headerLine = reader.readLine();

            if (headerLine == null)
                return results;

            var headers = headerLine.split(",");

            String line;
            int index = 0;
            while ((line = reader.readLine()) != null) {
                var entry = parseLine(line, headers);
                if (predicate.test(entry)) {
                    if (index >= skip && results.size() < take) {
                        results.add(entry);
                    }
                    index++;
                    if (results.size() >= take)
                        break;
                }
            }
        }

        return results;
    }

    /**
     * Gets a range of entries.
     * 
     * @param filename The name of the file to search.
     * @param skip The number of entries to skip.
     * @param take The number of entries to take.
     * @return A list of entries within the specified range.
     * @throws IOException If an I/O error occurs during the operation.
     */
    public static List<Map<String, String>> getRange(String filename, int skip, int take) throws IOException {
        return getRangeByPredicate(filename, entry -> true, skip, take);
    }
}
