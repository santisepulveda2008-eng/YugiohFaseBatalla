package modelo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Coordina el duelo: turnos, validación de ataques y aplicación del daño a los LP.
 */
public class Batalla {

    private final Jugador jugador1;
    private final Jugador jugador2;
    private boolean turnoJugador1;
    private int numeroTurno;
    private final List<ResultadoCombate> registros;

    public Batalla(Jugador jugador1, Jugador jugador2) {
        if (jugador1 == null || jugador2 == null) {
            throw new IllegalArgumentException("Se necesitan dos jugadores.");
        }
        this.jugador1 = jugador1;
        this.jugador2 = jugador2;
        this.turnoJugador1 = true;
        this.numeroTurno = 1;
        this.registros = new ArrayList<>();
    }

    // ------------------------------------------------------------------
    // Sobrecarga: atacar()
    // ------------------------------------------------------------------

    /**
     * El jugador en turno ataca a un monstruo del rival.
     *
     * @param atacante monstruo del jugador actual
     * @param defensor monstruo del rival
     * @return resultado del combate
     * @throws AccionInvalidaException si el ataque no está permitido por las reglas
     */
    public ResultadoCombate atacar(Monstruo atacante, Monstruo defensor) throws AccionInvalidaException {
        validarAtacante(atacante);
        if (defensor == null || !rival().getCampo().contains(defensor)) {
            throw new AccionInvalidaException("Selecciona un monstruo del campo rival como objetivo.");
        }
        ResultadoCombate resultado = CalculadoraDanio.calcular(atacante, defensor);
        aplicar(resultado, atacante, defensor);
        return resultado;
    }

    /**
     * Ataque directo a los LP del rival. Solo se permite si el rival no tiene monstruos.
     *
     * @param atacante monstruo del jugador actual
     * @return resultado del combate
     * @throws AccionInvalidaException si el rival aún tiene monstruos o el ataque no es válido
     */
    public ResultadoCombate atacar(Monstruo atacante) throws AccionInvalidaException {
        validarAtacante(atacante);
        if (rival().tieneMonstruos()) {
            throw new AccionInvalidaException(
                    "No puedes atacar directamente: " + rival().getNombre() + " todavía tiene monstruos.");
        }
        ResultadoCombate resultado = CalculadoraDanio.calcular(atacante);
        aplicar(resultado, atacante, null);
        return resultado;
    }

    /**
     * Pasa el turno al otro jugador y reinicia los ataques de sus monstruos.
     *
     * @throws AccionInvalidaException si el duelo ya terminó
     */
    public void terminarTurno() throws AccionInvalidaException {
        if (haTerminado()) {
            throw new AccionInvalidaException("El duelo ya terminó.");
        }
        turnoJugador1 = !turnoJugador1;
        numeroTurno++;
        jugadorActual().reiniciarAtaques();
    }

    public Jugador jugadorActual() {
        return turnoJugador1 ? jugador1 : jugador2;
    }

    public Jugador rival() {
        return turnoJugador1 ? jugador2 : jugador1;
    }

    public boolean haTerminado() {
        return jugador1.estaDerrotado() || jugador2.estaDerrotado();
    }

    /**
     * @return el ganador, o {@code null} si el duelo no ha terminado
     */
    public Jugador ganador() {
        if (jugador1.estaDerrotado()) {
            return jugador2;
        }
        if (jugador2.estaDerrotado()) {
            return jugador1;
        }
        return null;
    }

    public Jugador getJugador1() {
        return jugador1;
    }

    public Jugador getJugador2() {
        return jugador2;
    }

    public int getNumeroTurno() {
        return numeroTurno;
    }

    public List<ResultadoCombate> getRegistros() {
        return Collections.unmodifiableList(registros);
    }

    // ------------------------------------------------------------------

    private void validarAtacante(Monstruo atacante) throws AccionInvalidaException {
        if (haTerminado()) {
            throw new AccionInvalidaException("El duelo ya terminó. Inicia un nuevo duelo.");
        }
        if (atacante == null || !jugadorActual().getCampo().contains(atacante)) {
            throw new AccionInvalidaException(
                    "Selecciona uno de tus monstruos (" + jugadorActual().getNombre() + ") para atacar.");
        }
        if (atacante.getPosicion() != Posicion.ATAQUE) {
            throw new AccionInvalidaException(atacante.getNombre() + " está en defensa y no puede atacar.");
        }
        if (atacante.isHaAtacado()) {
            throw new AccionInvalidaException(atacante.getNombre() + " ya atacó en este turno.");
        }
    }

    private void aplicar(ResultadoCombate r, Monstruo atacante, Monstruo defensor) {
        Jugador actual = jugadorActual();
        Jugador oponente = rival();
        r.asignarContexto(numeroTurno, actual.getNombre(), oponente.getNombre());
        atacante.setHaAtacado(true);

        switch (r.getObjetivoDanio()) {
            case ATACANTE -> actual.recibirDanio(r.getDanio());
            case DEFENSOR -> oponente.recibirDanio(r.getDanio());
            default -> { /* nadie recibe daño */ }
        }
        if (r.isAtacanteDestruido()) {
            atacante.destruir();
            actual.enviarAlCementerio(atacante);
        }
        if (defensor != null && r.isDefensorDestruido()) {
            defensor.destruir();
            oponente.enviarAlCementerio(defensor);
        }
        registros.add(r);
    }
}
