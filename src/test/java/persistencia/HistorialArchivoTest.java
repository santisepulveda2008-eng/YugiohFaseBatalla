package persistencia;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import modelo.CalculadoraDanio;
import modelo.MonstruoNormal;
import modelo.Posicion;
import modelo.ResultadoCombate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Pruebas de lectura/escritura del historial en archivo de texto.
 */
class HistorialArchivoTest {

    @TempDir
    Path carpetaTemporal;

    @Test
    @DisplayName("Si el archivo no existe, leerTodo devuelve una lista vacía")
    void archivoInexistente() throws IOException {
        HistorialArchivo historial = new HistorialArchivo(carpetaTemporal.resolve("no_existe.txt"));
        assertTrue(historial.leerTodo().isEmpty());
    }

    @Test
    @DisplayName("Las líneas guardadas se leen en el mismo orden")
    void guardarYLeer() throws IOException {
        HistorialArchivo historial = new HistorialArchivo(carpetaTemporal.resolve("historial.txt"));
        historial.guardar("Primera línea");
        historial.guardar("Segunda línea con tildes: daño");

        List<String> lineas = historial.leerTodo();
        assertEquals(List.of("Primera línea", "Segunda línea con tildes: daño"), lineas);
    }

    @Test
    @DisplayName("guardar(ResultadoCombate) escribe atacante, defensor, daño y resultado")
    void guardarResultado() throws IOException {
        HistorialArchivo historial = new HistorialArchivo(carpetaTemporal.resolve("historial.txt"));
        ResultadoCombate r = CalculadoraDanio.calcular(
                new MonstruoNormal("Dragón", 2500, 2000, Posicion.ATAQUE),
                new MonstruoNormal("Soldado", 1000, 1000, Posicion.ATAQUE));

        historial.guardar(r);

        String linea = historial.leerTodo().get(0);
        assertTrue(linea.contains("Dragón"));
        assertTrue(linea.contains("Soldado"));
        assertTrue(linea.contains("Daño: 1500"));
        assertTrue(linea.contains("Resultado:"));
    }

    @Test
    @DisplayName("El archivo se abre en modo agregar: no borra lo anterior")
    void modoAgregar() throws IOException {
        Path ruta = carpetaTemporal.resolve("historial.txt");
        new HistorialArchivo(ruta).guardar("Duelo 1");
        new HistorialArchivo(ruta).guardar("Duelo 2");
        assertEquals(2, new HistorialArchivo(ruta).leerTodo().size());
    }

    @Test
    @DisplayName("El constructor por defecto usa historial_combate.txt")
    void rutaPorDefecto() {
        assertEquals(RepositorioHistorial.ARCHIVO_POR_DEFECTO,
                new HistorialArchivo().getRuta().getFileName().toString());
    }
}
