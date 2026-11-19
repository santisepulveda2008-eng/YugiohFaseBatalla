package persistencia;

import java.io.IOException;
import java.util.List;
import modelo.ResultadoCombate;

/**
 * Contrato para guardar y leer el historial de combates.
 */
public interface RepositorioHistorial {

    /** Nombre del archivo de texto donde se guarda el historial. */
    String ARCHIVO_POR_DEFECTO = "historial_combate.txt";

    /**
     * Guarda el registro de un ataque.
     *
     * @param resultado resultado a guardar
     * @throws IOException si no se puede escribir
     */
    void guardar(ResultadoCombate resultado) throws IOException;

    /**
     * Guarda una línea libre (por ejemplo, el encabezado de un duelo).
     *
     * @param linea texto a guardar
     * @throws IOException si no se puede escribir
     */
    void guardar(String linea) throws IOException;

    /**
     * @return todas las líneas guardadas (lista vacía si aún no existe el archivo)
     * @throws IOException si no se puede leer
     */
    List<String> leerTodo() throws IOException;
}
