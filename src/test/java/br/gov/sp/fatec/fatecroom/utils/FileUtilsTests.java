package br.gov.sp.fatec.fatecroom.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

public class FileUtilsTests {
    private static File createTempFile() throws IOException {
        return File.createTempFile(
            "test_file_%d_%s".formatted(System.currentTimeMillis(), UUID.randomUUID().toString()),
            ".txt"
        );
    }

    private static Path createTempPath() throws IOException {
        return createTempFile().toPath();
    }

    private static void deleteTempPath(Path path) throws IOException {
        if (path != null)
            Files.deleteIfExists(path);
    }

    private static void deleteTempFile(File file) throws IOException {
        if (file != null)
            Files.deleteIfExists(file.toPath());
    }

    private static void createBackup_shouldThrowIllegalArgumentException_whenFileIsNull() {
        try {
            FileUtils.createBackup(null);
            System.err.println("Test failed: Expected IllegalArgumentException was not thrown.");
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("cannot be null")) {
                System.out.println("Test passed: Caught expected IllegalArgumentException for null file.");
            } else {
                System.err.println("Test failed: Caught IllegalArgumentException, but with unexpected message: " + e.getMessage());
            }
        } catch (Exception e) {
            System.err.println("Test failed: Caught unexpected exception type: " + e);
        }
    }

    private static void createBackup_shouldThrowIllegalArgumentException_whenFileNotExists() {
        var nonExistentPath = Path.of("non_existent_file.txt");

        try {
            FileUtils.createBackup(nonExistentPath);
            System.err.println("Test failed: Expected IllegalArgumentException was not thrown.");
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("not exist")) {
                System.out.println("Test passed: Caught expected IllegalArgumentException for non-existent file.");
            } else {
                System.err.println("Test failed: Caught IllegalArgumentException, but with unexpected message: " + e.getMessage());
            }
        } catch (Exception e) {
            System.err.println("Test failed: Caught unexpected exception type: " + e);
        }
    }

    private static void createBackup_shouldCreateBackupFile_whenFileExists() {
        Path tempFilePath;
        
        try {
            tempFilePath = createTempPath();
        } catch (IOException e) {
            System.err.println("Test failed: error creating temporary file: " + e);
            return;
        }

        Path backupPath = null;
        try {
            backupPath = FileUtils.createBackup(tempFilePath);
            if (Files.exists(backupPath)) {
                System.out.println("Test passed: Backup file created successfully at " + backupPath.getFileName());
            } else {
                System.err.println("Test failed: Backup file was not created.");
            }
        } catch (Exception e) {
            System.err.println("Test failed: Caught unexpected exception: " + e);
        } finally {
            try {
                deleteTempPath(tempFilePath);
                deleteTempPath(backupPath);
            } catch (IOException e) {
                System.err.println("Warning: Could not delete temporary files: " + e);
            }
        }
    }

    private static void appendLineTo_shouldThrowIllegalArgumentException_whenFileIsNull() {
        try {
            FileUtils.appendLineTo(null, "Sample line");
            System.err.println("Test failed: Expected IllegalArgumentException was not thrown.");
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("cannot be null")) {
                System.out.println("Test passed: Caught expected IllegalArgumentException for null file.");
            } else {
                System.err.println("Test failed: Caught IllegalArgumentException, but with unexpected message: " + e.getMessage());
            }
        } catch (Exception e) {
            System.err.println("Test failed: Caught unexpected exception type: " + e);
        }
    }

    private static void appendLineTo_shouldAppendLine_whenFileIsValid() {
        File tempFile;
        
        try {
            tempFile = createTempFile();
        } catch (IOException e) {
            System.err.println("Test failed: error creating temporary file: " + e);
            return;
        }

        final String LINE_TO_APPEND = "This is a test line.";

        try {
            FileUtils.appendLineTo(tempFile, LINE_TO_APPEND);
            var lines = Files.readAllLines(tempFile.toPath());
            if (lines.contains(LINE_TO_APPEND)) {
                System.out.println("Test passed: Line appended successfully.");
            } else {
                System.err.println("Test failed: Line was not appended.");
            }
        } catch (IOException e) {
            System.err.println("Test failed: Caught unexpected exception: " + e);
        } finally {
            try {
                deleteTempFile(tempFile);
            } catch (IOException e) {
                System.err.println("Warning: Could not delete temporary file: " + e);
            }
        }
    }

    public static void main(String[] args) {
        createBackup_shouldThrowIllegalArgumentException_whenFileIsNull();
        createBackup_shouldThrowIllegalArgumentException_whenFileNotExists();
        createBackup_shouldCreateBackupFile_whenFileExists();
        appendLineTo_shouldThrowIllegalArgumentException_whenFileIsNull();
        appendLineTo_shouldAppendLine_whenFileIsValid();
    }
}
