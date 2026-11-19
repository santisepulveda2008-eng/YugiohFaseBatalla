package modelo;

/**
 * Posición en la que se encuentra un monstruo dentro del campo.
 */
public enum Posicion {
    ATAQUE("Ataque"),
    DEFENSA("Defensa");

    private final String etiqueta;

    Posicion(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }

    /**
     * Convierte un texto ("Ataque" / "Defensa") en la posición correspondiente.
     *
     * @param texto texto a convertir, sin importar mayúsculas
     * @return la posición encontrada
     * @throws IllegalArgumentException si el texto no corresponde a ninguna posición
     */
    public static Posicion desdeTexto(String texto) {
        for (Posicion p : values()) {
            if (p.etiqueta.equalsIgnoreCase(texto) || p.name().equalsIgnoreCase(texto)) {
                return p;
            }
        }
        throw new IllegalArgumentException("Posición desconocida: " + texto);
    }

    @Override
    public String toString() {
        return etiqueta;
    }
}
