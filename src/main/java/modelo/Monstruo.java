package modelo;

/**
 * Carta de monstruo: tiene puntos de ataque, puntos de defensa y una posición.
 * Es abstracta porque cada tipo de monstruo define su propio {@link #tipoCarta()}.
 */
public abstract class Monstruo extends Carta implements Combatiente {

    /** Valor máximo permitido para ATQ y DEF. */
    public static final int PUNTOS_MAXIMOS = 9999;

    private int puntosAtaque;
    private int puntosDefensa;
    private Posicion posicion;
    private boolean destruido;
    private boolean haAtacado;

    protected Monstruo(String nombre, int puntosAtaque, int puntosDefensa, Posicion posicion) {
        super(nombre);
        setPuntosAtaque(puntosAtaque);
        setPuntosDefensa(puntosDefensa);
        setPosicion(posicion);
        this.destruido = false;
        this.haAtacado = false;
    }

    // ------------------------------------------------------------------
    // Getters y setters
    // ------------------------------------------------------------------

    public int getPuntosAtaque() {
        return puntosAtaque;
    }

    public void setPuntosAtaque(int puntos) {
        validarPuntos(puntos, "ATQ");
        this.puntosAtaque = puntos;
    }

    public int getPuntosDefensa() {
        return puntosDefensa;
    }

    public void setPuntosDefensa(int puntos) {
        validarPuntos(puntos, "DEF");
        this.puntosDefensa = puntos;
    }

    public Posicion getPosicion() {
        return posicion;
    }

    public void setPosicion(Posicion posicion) {
        if (posicion == null) {
            throw new IllegalArgumentException("La posición no puede ser nula.");
        }
        this.posicion = posicion;
    }

    public boolean isHaAtacado() {
        return haAtacado;
    }

    public void setHaAtacado(boolean haAtacado) {
        this.haAtacado = haAtacado;
    }

    // ------------------------------------------------------------------
    // Sobrecarga: cambiarPosicion()
    // ------------------------------------------------------------------

    /**
     * Alterna la posición actual: Ataque pasa a Defensa y viceversa.
     */
    public void cambiarPosicion() {
        cambiarPosicion(posicion == Posicion.ATAQUE ? Posicion.DEFENSA : Posicion.ATAQUE);
    }

    /**
     * Coloca al monstruo en la posición indicada.
     *
     * @param nueva nueva posición
     */
    public void cambiarPosicion(Posicion nueva) {
        setPosicion(nueva);
    }

    // ------------------------------------------------------------------
    // Implementación de Combatiente
    // ------------------------------------------------------------------

    @Override
    public int valorDeCombate() {
        return posicion == Posicion.ATAQUE ? puntosAtaque : puntosDefensa;
    }

    @Override
    public boolean puedeAtacar() {
        return !destruido && !haAtacado && posicion == Posicion.ATAQUE;
    }

    @Override
    public void destruir() {
        this.destruido = true;
    }

    @Override
    public boolean estaDestruido() {
        return destruido;
    }

    // ------------------------------------------------------------------
    // Sobreescritura
    // ------------------------------------------------------------------

    @Override
    public String descripcion() {
        return super.descripcion() + " ATQ " + puntosAtaque + " / DEF " + puntosDefensa;
    }

    @Override
    public String toString() {
        return getNombre() + " (" + tipoCarta() + ") ATQ:" + puntosAtaque
                + " DEF:" + puntosDefensa + " - " + posicion.getEtiqueta();
    }

    private static void validarPuntos(int puntos, String campo) {
        if (puntos < 0 || puntos > PUNTOS_MAXIMOS) {
            throw new IllegalArgumentException(
                    "Los puntos de " + campo + " deben estar entre 0 y " + PUNTOS_MAXIMOS + ".");
        }
    }
}
