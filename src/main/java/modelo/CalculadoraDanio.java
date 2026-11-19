package modelo;

/**
 * Reglas oficiales de cálculo de daño de la Fase de Batalla.
 * <p>
 * Es una clase final de utilidad: no guarda estado ni modifica los monstruos,
 * solo calcula y devuelve un {@link ResultadoCombate}. Por eso puede probarse
 * de forma independiente con JUnit.
 */
public final class CalculadoraDanio {

    public static final String ATAQUE_DIRECTO = "ATAQUE DIRECTO";

    private CalculadoraDanio() {
        // No se instancia
    }

    /**
     * Calcula el resultado de un ataque contra un monstruo.
     *
     * @param atacante monstruo que ataca (debe estar en posición de ataque)
     * @param defensor monstruo atacado
     * @return resultado del combate
     */
    public static ResultadoCombate calcular(Monstruo atacante, Monstruo defensor) {
        if (atacante == null || defensor == null) {
            throw new IllegalArgumentException("El atacante y el defensor son obligatorios.");
        }
        if (atacante.getPosicion() != Posicion.ATAQUE) {
            throw new IllegalArgumentException("Un monstruo en defensa no puede atacar.");
        }
        return defensor.getPosicion() == Posicion.ATAQUE
                ? ataqueContraAtaque(atacante, defensor)
                : ataqueContraDefensa(atacante, defensor);
    }

    /**
     * Calcula un ataque directo: el rival pierde LP igual al ATQ del atacante.
     *
     * @param atacante monstruo que ataca directamente
     * @return resultado del combate
     */
    public static ResultadoCombate calcular(Monstruo atacante) {
        if (atacante == null) {
            throw new IllegalArgumentException("El atacante es obligatorio.");
        }
        if (atacante.getPosicion() != Posicion.ATAQUE) {
            throw new IllegalArgumentException("Un monstruo en defensa no puede atacar.");
        }
        int danio = atacante.getPuntosAtaque();
        return new ResultadoCombate(etiqueta(atacante), ATAQUE_DIRECTO, danio,
                danio > 0 ? ObjetivoDanio.DEFENSOR : ObjetivoDanio.NINGUNO, false, false);
    }

    /**
     * Ataque vs Ataque: el de menor ATQ se destruye y su dueño recibe la diferencia.
     * Si empatan, ambos se destruyen y nadie recibe daño (salvo 0 vs 0: no se destruye ninguno).
     */
    private static ResultadoCombate ataqueContraAtaque(Monstruo a, Monstruo d) {
        int atq = a.getPuntosAtaque();
        int rival = d.getPuntosAtaque();
        int diferencia = Math.abs(atq - rival);
        if (atq > rival) {
            return new ResultadoCombate(etiqueta(a), etiqueta(d), diferencia, ObjetivoDanio.DEFENSOR, false, true);
        }
        if (atq < rival) {
            return new ResultadoCombate(etiqueta(a), etiqueta(d), diferencia, ObjetivoDanio.ATACANTE, true, false);
        }
        boolean ambosDestruidos = atq > 0;
        return new ResultadoCombate(etiqueta(a), etiqueta(d), 0, ObjetivoDanio.NINGUNO,
                ambosDestruidos, ambosDestruidos);
    }

    /**
     * Ataque vs Defensa: si ATQ &gt; DEF el defensor se destruye sin daño;
     * si ATQ &lt; DEF el dueño del atacante recibe la diferencia; si empatan no pasa nada.
     */
    private static ResultadoCombate ataqueContraDefensa(Monstruo a, Monstruo d) {
        int atq = a.getPuntosAtaque();
        int def = d.getPuntosDefensa();
        if (atq > def) {
            return new ResultadoCombate(etiqueta(a), etiqueta(d), 0, ObjetivoDanio.NINGUNO, false, true);
        }
        if (atq < def) {
            return new ResultadoCombate(etiqueta(a), etiqueta(d), def - atq, ObjetivoDanio.ATACANTE, false, false);
        }
        return new ResultadoCombate(etiqueta(a), etiqueta(d), 0, ObjetivoDanio.NINGUNO, false, false);
    }

    private static String etiqueta(Monstruo m) {
        return m.getPosicion() == Posicion.ATAQUE
                ? m.getNombre() + " (ATQ " + m.getPuntosAtaque() + ")"
                : m.getNombre() + " (DEF " + m.getPuntosDefensa() + ")";
    }
}
