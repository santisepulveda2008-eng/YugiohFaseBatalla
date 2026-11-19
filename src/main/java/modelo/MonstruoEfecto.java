package modelo;

/**
 * Monstruo que tiene un texto de efecto. El efecto es descriptivo: el cálculo
 * de daño sigue las reglas oficiales de la Fase de Batalla.
 */
public class MonstruoEfecto extends Monstruo {

    private final String efecto;

    public MonstruoEfecto(String nombre, int atq, int def, Posicion posicion, String efecto) {
        super(nombre, atq, def, posicion);
        this.efecto = (efecto == null || efecto.isBlank()) ? "Sin descripción" : efecto.trim();
    }

    public String getEfecto() {
        return efecto;
    }

    @Override
    public String tipoCarta() {
        return "Efecto";
    }

    @Override
    public String descripcion() {
        return super.descripcion() + " | Efecto: " + efecto;
    }
}
