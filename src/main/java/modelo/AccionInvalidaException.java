package modelo;

/**
 * Se lanza cuando un jugador intenta una acción que las reglas no permiten
 * (atacar dos veces, atacar en defensa, invocar con el campo lleno, etc.).
 */
public class AccionInvalidaException extends Exception {

    public AccionInvalidaException(String mensaje) {
        super(mensaje);
    }
}
