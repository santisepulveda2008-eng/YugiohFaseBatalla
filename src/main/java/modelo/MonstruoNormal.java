package modelo;

/**
 * Monstruo sin efecto especial.
 */
public class MonstruoNormal extends Monstruo {

    public MonstruoNormal(String nombre, int atq, int def, Posicion posicion) {
        super(nombre, atq, def, posicion);
    }

    @Override
    public String tipoCarta() {
        return "Normal";
    }
}
