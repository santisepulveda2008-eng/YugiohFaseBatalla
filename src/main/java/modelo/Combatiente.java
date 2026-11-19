package modelo;

/**
 * Contrato que debe cumplir cualquier carta capaz de participar en un combate.
 */
public interface Combatiente {

    /**
     * @return los puntos que usa la carta en el combate: ATQ si está en
     * posición de ataque, DEF si está en posición de defensa.
     */
    int valorDeCombate();

    /**
     * @return true si la carta puede declarar un ataque en este turno.
     */
    boolean puedeAtacar();

    /**
     * Marca la carta como destruida.
     */
    void destruir();

    /**
     * @return true si la carta fue destruida en batalla.
     */
    boolean estaDestruido();
}
