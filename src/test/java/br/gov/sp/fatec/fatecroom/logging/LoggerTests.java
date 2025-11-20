package br.gov.sp.fatec.fatecroom.logging;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

public class LoggerTests {
    private static void setSinkFileFromPath_shouldThrowIllegalArgumentException_whenPathIsNull() {
        try {
            Logger.setSinkFileFromPath(null);
            System.err.println("Test failed: Expected IllegalArgumentException for null path.");
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("cannot be null")) {
                System.out.println("Test passed: Caught expected IllegalArgumentException for null path.");
            } else {
                System.err.println("Test failed: Caught IllegalArgumentException, but with unexpected message: " + e.getMessage());
            }
        } catch (Exception e) {
            System.err.println("Test failed: Caught unexpected exception type: " + e);
        }
    }

    private static void setSinkFileFromPath_shouldThrowIllegalArgumentException_whenPathEndsWithSeparator() {
        try {
            Logger.setSinkFileFromPath(Paths.get("\\"));
            System.err.println("Test failed: Expected IllegalArgumentException for directory path.");
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("must point to a file")) {
                System.out.println("Test passed: Caught expected IllegalArgumentException for directory path.");
            } else {
                System.err.println("Test failed: Caught IllegalArgumentException, but with unexpected message: " + e.getMessage());
            }
        } catch (Exception e) {
            System.err.println("Test failed: Caught unexpected exception type: " + e);
        }
    }

    private static void setSinkFileFromPath_shouldThrowIllegalArgumentException_whenPathIsDirectory() {
        Path pathToDirectory = null;
        try {
            var baseDir = Files.createTempDirectory("test_dir.%d.%s".formatted(System.currentTimeMillis(), UUID.randomUUID().toString()));
            pathToDirectory = Files.createDirectory(baseDir.resolve("subdir.csv"));
            Logger.setSinkFileFromPath(pathToDirectory);
            System.err.println("Test failed: Expected IllegalArgumentException for directory path.");
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("cannot be a directory")) {
                System.out.println("Test passed: Caught expected IllegalArgumentException for directory path.");
            } else {
                System.err.println("Test failed: Caught IllegalArgumentException, but with unexpected message: " + e.getMessage());
            }
        } catch (IOException e) {
            System.err.println("Test failed: Caught unexpected exception type: " + e);
        } finally {
            try {
                if (pathToDirectory != null)
                    Files.deleteIfExists(pathToDirectory);
            } catch (Exception e) {
                System.err.println("Warning: Could not delete temporary directory: " + e);
            }
        }
    }

    private static void setSinkFileFromPath_shouldThrowIllegalArgumentException_whenFileExtensionIsNotCsv() {
        try {
            Logger.setSinkFileFromPath(Path.of("logfile.txt"));
            System.err.println("Test failed: Expected IllegalArgumentException for non-CSV file extension.");
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("must have a .csv extension")) {
                System.out.println("Test passed: Caught expected IllegalArgumentException for non-CSV file extension.");
            } else {
                System.err.println("Test failed: Caught IllegalArgumentException, but with unexpected message: " + e.getMessage());
            }
        } catch (Exception e) {
            System.err.println("Test failed: Caught unexpected exception type: " + e);
        }
    }

    private static void setSinkFileFromPath_shouldThrowIllegalArgumentException_whenFileIsNotWritable() {
        Path tempFilePath = null;
        try {
            tempFilePath = Files.createTempFile("test_file_%d_%s".formatted(System.currentTimeMillis(), UUID.randomUUID().toString()), ".csv");
            tempFilePath.toFile().setWritable(false);
            Logger.setSinkFileFromPath(tempFilePath);
            System.err.println("Test failed: Expected IllegalArgumentException for non-writable file.");
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("is not writable")) {
                System.out.println("Test passed: Caught expected IllegalArgumentException for non-writable file.");
            } else {
                System.err.println("Test failed: Caught IllegalArgumentException, but with unexpected message: " + e.getMessage());
            }
        } catch (Exception e) {
            System.err.println("Test failed: Caught unexpected exception type: " + e);
        }
    }

    private static void setSinkFileFromPath_shouldSetSinkFileSuccessfully_whenPathIsValid() {
        Path tempFilePath = null;
        try {
            tempFilePath = Files.createTempFile("test_file_%d_%s".formatted(System.currentTimeMillis(), UUID.randomUUID().toString()), ".csv");
            Logger.setSinkFileFromPath(tempFilePath);
            System.out.println("Test passed: Sink file set successfully for valid path.");
        } catch (Exception e) {
            System.err.println("Test failed: Caught unexpected exception: " + e);
        } finally {
            try {
                if (tempFilePath != null)
                    Files.deleteIfExists(tempFilePath);
            } catch (Exception e) {
                System.err.println("Warning: Could not delete temporary file: " + e);
            }
        }
    }

    private static void setSinkFileFromPath_shouldBackupAndReplaceFile_whenFileAlreadyExistsAndHeaderIsDifferent() {
        Path tempFilePath = null;
        Path backupPath = null;
        try {
            tempFilePath = Files.createTempFile("test_file_%d_%s".formatted(System.currentTimeMillis(), UUID.randomUUID().toString()), ".csv");
            Files.writeString(tempFilePath, "Different,Header,Line\nData1,Data2,Data3\n");
            backupPath = Logger.setSinkFileFromPath(tempFilePath);
            if (backupPath == null || !Files.exists(backupPath)) {
                System.err.println("Test failed: Expected backup file to be created.");
                return;
            }
            System.out.println("Test passed: Existing file backed up and replaced successfully.");
        } catch (IOException e) {
            System.err.println("Test failed: Caught unexpected exception: " + e);
        } finally {
            try {
                if (tempFilePath != null)
                    Files.deleteIfExists(tempFilePath);
                if (backupPath != null)
                    Files.deleteIfExists(backupPath);
            } catch (IOException e) {
                System.err.println("Warning: Could not delete temporary files: " + e);
            }
        }
    }

    private static void logNow_shouldLogMessageSuccessfully_whenCalled() {
        Path tempFilePath = null;
        try {
            tempFilePath = Files.createTempFile("test_log_%d_%s".formatted(System.currentTimeMillis(), UUID.randomUUID().toString()), ".csv");
            Logger.setSinkFileFromPath(tempFilePath);
            Logger.logNow("test_user", "test", "test");
            String fileContent = Files.readString(tempFilePath);
            if (fileContent.contains("test_user")) {
                System.out.println("Test passed: Log message written successfully.");
            } else {
                System.err.println("Test failed: Log message not found in file.");
            }
        } catch (IOException e) {
            System.err.println("Test failed: Caught unexpected exception: " + e);
        } finally {
            try {
                if (tempFilePath != null)
                    Files.deleteIfExists(tempFilePath);
            } catch (IOException e) {
                System.err.println("Warning: Could not delete temporary file: " + e);
            }
        }
    }

    public static void main(String[] args) {
        setSinkFileFromPath_shouldThrowIllegalArgumentException_whenPathIsNull();
        setSinkFileFromPath_shouldThrowIllegalArgumentException_whenPathEndsWithSeparator();
        setSinkFileFromPath_shouldThrowIllegalArgumentException_whenPathIsDirectory();
        setSinkFileFromPath_shouldThrowIllegalArgumentException_whenFileExtensionIsNotCsv();
        setSinkFileFromPath_shouldThrowIllegalArgumentException_whenFileIsNotWritable();
        setSinkFileFromPath_shouldSetSinkFileSuccessfully_whenPathIsValid();
        setSinkFileFromPath_shouldBackupAndReplaceFile_whenFileAlreadyExistsAndHeaderIsDifferent();
        logNow_shouldLogMessageSuccessfully_whenCalled();
    }
}
