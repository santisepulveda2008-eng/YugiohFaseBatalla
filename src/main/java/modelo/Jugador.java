package modelo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Jugador del duelo: tiene Puntos de Vida (LP), un campo con monstruos y un cementerio.
 */
public class Jugador {

    /** Puntos de Vida con los que inicia cada jugador. */
    public static final int LP_INICIALES = 8000;
    /** Cantidad máxima de monstruos en el campo. */
    public static final int MAX_MONSTRUOS = 5;

    private final String nombre;
    private int puntosVida;
    private final List<Monstruo> campo;
    private final List<Monstruo> cementerio;

    public Jugador(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre del jugador no puede estar vacío.");
        }
        this.nombre = nombre.trim();
        this.puntosVida = LP_INICIALES;
        this.campo = new ArrayList<>();
        this.cementerio = new ArrayList<>();
    }

    /**
     * Coloca un monstruo en el campo del jugador.
     *
     * @param monstruo monstruo a invocar
     * @throws AccionInvalidaException si el campo ya tiene {@link #MAX_MONSTRUOS} monstruos
     */
    public void invocar(Monstruo monstruo) throws AccionInvalidaException {
        if (monstruo == null) {
            throw new IllegalArgumentException("El monstruo no puede ser nulo.");
        }
        if (campo.size() >= MAX_MONSTRUOS) {
            throw new AccionInvalidaException(
                    nombre + " ya tiene " + MAX_MONSTRUOS + " monstruos en el campo.");
        }
        campo.add(monstruo);
    }

    /**
     * Resta daño a los LP. Los LP nunca quedan por debajo de 0.
     *
     * @param danio cantidad de daño (no negativa)
     */
    public void recibirDanio(int danio) {
        if (danio < 0) {
            throw new IllegalArgumentException("El daño no puede ser negativo.");
        }
        puntosVida = Math.max(0, puntosVida - danio);
    }

    /**
     * Retira un monstruo del campo y lo envía al cementerio.
     *
     * @param monstruo monstruo destruido
     */
    public void enviarAlCementerio(Monstruo monstruo) {
        if (campo.remove(monstruo)) {
            cementerio.add(monstruo);
        }
    }

    public boolean tieneMonstruos() {
        return !campo.isEmpty();
    }

    public boolean estaDerrotado() {
        return puntosVida <= 0;
    }

    /**
     * Permite que todos los monstruos del campo vuelvan a atacar (inicio de turno).
     */
    public void reiniciarAtaques() {
        for (Monstruo m : campo) {
            m.setHaAtacado(false);
        }
    }

    public String getNombre() {
        return nombre;
    }

    public int getPuntosVida() {
        return puntosVida;
    }

    /** @return vista de solo lectura del campo */
    public List<Monstruo> getCampo() {
        return Collections.unmodifiableList(campo);
    }

    /** @return vista de solo lectura del cementerio */
    public List<Monstruo> getCementerio() {
        return Collections.unmodifiableList(cementerio);
    }
}
