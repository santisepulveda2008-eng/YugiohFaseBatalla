package modelo;

/**
 * Clase base abstracta de todas las cartas del juego.
 */
public abstract class Carta {

    /** Cantidad de cartas creadas durante la ejecución (compartido por todas). */
    private static int contadorCartas = 0;

    private final int id;
    private final String nombre;

    protected Carta(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre de la carta no puede estar vacío.");
        }
        contadorCartas++;
        this.id = contadorCartas;
        this.nombre = nombre.trim();
    }

    /**
     * @return el tipo de carta (por ejemplo "Normal" o "Efecto").
     */
    public abstract String tipoCarta();

    /**
     * @return una descripción corta de la carta. Las subclases pueden sobreescribirla.
     */
    public String descripcion() {
        return nombre + " [" + tipoCarta() + "]";
    }

    /**
     * @return el total de cartas creadas desde que inició el programa.
     */
    public static int totalCartasCreadas() {
        return contadorCartas;
    }

    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }
}
