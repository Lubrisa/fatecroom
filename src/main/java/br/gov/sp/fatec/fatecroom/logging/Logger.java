package br.gov.sp.fatec.fatecroom.logging;

import br.gov.sp.fatec.fatecroom.utils.FileUtils;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;

/**
 * Logger utility for logging operations to a CSV file.
 */
public final class Logger {
    private Logger() { }

    private static final String DEFAULT_SINK_PATH = "logs_operacoes.csv";
    private static final String HEADER = "data_hora;id_usuario;acao;detalhe";

    private static File sinkFile;

    /**
     * Converts a Path to a File after validating it as a proper sink file.
     * @param sinkPath the path to the sink file
     * @return a File object representing the sink file
     * @throws IllegalArgumentException if the path is invalid
     * @throws IOException if an I/O error occurs during validation
     */
    private static File toSinkFile(Path sinkPath) throws IllegalArgumentException, IOException {
        if (sinkPath == null)
            throw new IllegalArgumentException("Sink path cannot be null.");

        // Se `sinkPath` for um caminho composto, por exemplo
        // "diretorio/subdiretorio/arquivo.csv", `getFileName()` retornará outro
        // `Path` representando apenas "arquivo.csv". Se `sinkPath` for a raiz
        // do sistema de arquivos, ou terminar com um separador de diretório
        // ("\" para Windows, "/" para SOs baseados em Unix), `getFileName()`
        // retornará `null`.
        var fileName = sinkPath.getFileName();
        if (fileName == null)
            throw new IllegalArgumentException("Sink path must point to a file.");

        if (!fileName.toString().toLowerCase().endsWith(".csv"))
            throw new IllegalArgumentException("Sink file must have a .csv extension.");

        // As validações anteriores não conseguem garantir que o caminho não
        // aponte para um diretório, pois um caminho como "logs.csv" pode ser um
        // diretório se o sistema de arquivos permitir. `Files.isDirectory`
        // garante que, se o caminho se referir a um diretório, uma exceção será
        // lançada.
        //
        // `Files.isDirectory` realiza operações de I/O para verificar o tipo do
        // arquivo, fazemos a validação com o `fileName` para evitar operações
        // desnecessárias em casos em que o caminho claramente não é um
        // diretório.
        if (Files.isDirectory(sinkPath))
            throw new IllegalArgumentException("Sink path cannot be a directory.");

        if (Files.exists(sinkPath) && !Files.isWritable(sinkPath))
            throw new IllegalArgumentException("Sink file is not writable.");

        return sinkPath.toFile();
    }

    /**
     * Ensures the sink file exists; creates it if it does not.
     * @param sinkFile the sink file
     * @throws IOException if an I/O error occurs during creation
     */
    private static void ensureSinkFileExists(File sinkFile) throws IOException {
        var parentDir = sinkFile.getAbsoluteFile().getParentFile();

        if (parentDir == null)
            throw new IOException("Sink file is the root directory of a file system. ");

        if (!parentDir.exists() && !parentDir.mkdirs())
            throw new IOException("Failed to create parent directories for sink file.");

        if (!sinkFile.exists() && !sinkFile.createNewFile())
            throw new IOException("Failed to create sink file.");
    }

    /**
     * Ensures the sink file contains the required header; adds it if missing,
     * or replaces the content of the file creating a backup if the header is
     * incorrect.
     * @param sinkFile the sink file
     * @throws IOException if an I/O error occurs during validation or writing
     */
    private static Path ensureSinkFileContainsHeader(File sinkFile) throws IOException {
        Path backupPath = null;
        
        if (sinkFile.length() == 0) {
            FileUtils.appendLineTo(sinkFile, HEADER);
        } else {
            var shouldReplace = false;
            try (var reader = new BufferedReader(new FileReader(sinkFile))) {
                String firstLine = reader.readLine();
                // Apesar de já termos verificado que o arquivo não está vazio,
                // é possível que a leitura da primeira linha retorne `null` se
                // outro processo apagar o conteúdo do arquivo entre a primeira
                // verificação e a leitura.
                if (firstLine == null || !firstLine.equals(HEADER))
                    shouldReplace = true;
            }

            if (shouldReplace) {
                backupPath = FileUtils.createBackup(sinkFile.toPath());
                FileUtils.appendLineTo(sinkFile, HEADER);
            }
        }

        return backupPath;
    }

    /**
     * Sets the sink file from the given path, ensuring it exists and contains
     * the correct header.
     * @param newSinkPath the path to set as the sink file
     * @return the path to the backup file if the original file was replaced, or null otherwise
     * @throws IllegalArgumentException if the path is invalid
     * @throws IOException if an I/O error occurs during validation or writing
     */
    public static Path setSinkFileFromPath(Path newSinkPath) throws IllegalArgumentException, IOException {
        var newSinkFile = toSinkFile(newSinkPath);
        
        ensureSinkFileExists(newSinkFile);
        Path backupPath = ensureSinkFileContainsHeader(newSinkFile);
        
        Logger.sinkFile = newSinkFile;
        
        return backupPath;  
    }

    /**
     * Gets the current sink file, setting it to the default if not already set.
     * @return the sink file
     * @throws IllegalArgumentException if the default sink path is invalid
     * @throws IOException if an I/O error occurs during setting the default sink file
     */
    private static File getSinkFile() throws IllegalArgumentException, IOException {
        if (sinkFile == null) {
            try {
                setSinkFileFromPath(Path.of(DEFAULT_SINK_PATH));
            } catch (IOException e) {
                throw new RuntimeException("Failed to set default sink file.", e);
            }
        }

        return sinkFile;
    }

    /**
     * Logs an entry immediately to the sink file.
     * @param userId the ID of the user performing the action
     * @param action the action performed
     * @param detail additional details about the action
     */
    public static void logNow(String userId, String action, String detail) throws RuntimeException {
        try {
            FileUtils.appendLineTo(
                getSinkFile(),
                "%s;%s;%s;%s".formatted(
                    Instant.now(),
                    userId,
                    action,
                    detail
                )
            );
        } catch (IOException e) {
            throw new RuntimeException("Failed to write to sink file.", e);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid sink file configuration.", e);
        }
    }
}
