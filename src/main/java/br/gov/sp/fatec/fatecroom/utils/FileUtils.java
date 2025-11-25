package br.gov.sp.fatec.fatecroom.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.Instant;
import java.util.UUID;

/**
 * Utility class for file operations.
 */
public class FileUtils {
    private FileUtils() { }

    /**
     * Creates a backup of the specified file by renaming it.
     * @param originalPath the path of the original file
     * @return the path of the created backup file
     * @throws IllegalArgumentException if the originalPath is null
     * @throws IOException if an I/O error occurs during the move operation
     */
    public static Path createBackup(Path originalPath) throws IllegalArgumentException, IOException {
        if (originalPath == null)
            throw new IllegalArgumentException("Original file cannot be null.");

        if (Files.notExists(originalPath))
            throw new IllegalArgumentException("Original file does not exist.");

        final String DEFAULT_BACKUP_BASE_FILE_NAME = "backup.bak";

        var originalFileName = originalPath.getFileName();
        var backupBaseFileName = (originalFileName != null)
            ? originalFileName.toString()
            : DEFAULT_BACKUP_BASE_FILE_NAME;

        // O caminho do backup vai combinar:
        //
        // - o nome do arquivo original
        // - um timestamp (milisegundos desde o epoch)
        // - um UUID para garantir unicidade mesmo em operações rápidas
        //
        // Isso torna quase impossível a colisão de nomes de arquivos de backup.
        final String BACKUP_PATH_FORMAT = "%s.%d.%s.bak";

        var backupPath = Path.of(
            BACKUP_PATH_FORMAT.formatted(
                backupBaseFileName,
                Instant.now().toEpochMilli(),
                UUID.randomUUID().toString()
            )
        );

        Files.copy(originalPath, backupPath, StandardCopyOption.COPY_ATTRIBUTES);

        return backupPath;
    }

    public static void appendLineTo(File file, String line) throws IOException {
        if (file == null)
            throw new IllegalArgumentException("File cannot be null.");

        try (var writer = new BufferedWriter(new FileWriter(file, true))) {
            writer.write(line);
            writer.newLine();
        }
    }
}
