package persistencia;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import modelo.ResultadoCombate;

/**
 * Guarda el historial de combates en un archivo de texto plano (.txt).
 */
public class HistorialArchivo implements RepositorioHistorial {

    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final Path ruta;

    /** Usa el archivo {@value RepositorioHistorial#ARCHIVO_POR_DEFECTO} en la carpeta del proyecto. */
    public HistorialArchivo() {
        this(Paths.get(ARCHIVO_POR_DEFECTO));
    }

    /**
     * @param ruta ruta del archivo de historial
     */
    public HistorialArchivo(Path ruta) {
        if (ruta == null) {
            throw new IllegalArgumentException("La ruta del historial no puede ser nula.");
        }
        this.ruta = ruta;
    }

    @Override
    public void guardar(ResultadoCombate resultado) throws IOException {
        if (resultado == null) {
            throw new IllegalArgumentException("El resultado no puede ser nulo.");
        }
        guardar("[" + LocalDateTime.now().format(FORMATO_FECHA) + "] " + resultado.aLineaHistorial());
    }

    @Override
    public void guardar(String linea) throws IOException {
        Path carpeta = ruta.toAbsolutePath().getParent();
        if (carpeta != null) {
            Files.createDirectories(carpeta);
        }
        try (BufferedWriter escritor = Files.newBufferedWriter(ruta, StandardCharsets.UTF_8,
                StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
            escritor.write(linea);
            escritor.newLine();
        }
    }

    @Override
    public List<String> leerTodo() throws IOException {
        List<String> lineas = new ArrayList<>();
        if (!Files.exists(ruta)) {
            return lineas;
        }
        try (BufferedReader lector = Files.newBufferedReader(ruta, StandardCharsets.UTF_8)) {
            String linea;
            while ((linea = lector.readLine()) != null) {
                lineas.add(linea);
            }
        }
        return lineas;
    }

    public Path getRuta() {
        return ruta;
    }
}
