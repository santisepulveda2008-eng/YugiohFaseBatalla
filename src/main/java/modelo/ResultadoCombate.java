package modelo;

/**
 * Resultado inmutable (salvo el contexto del turno) de un ataque.
 */
public class ResultadoCombate {

    private int turno;
    private String jugadorAtacante;
    private String jugadorDefensor;
    private final String atacante;
    private final String defensor;
    private final int danio;
    private final ObjetivoDanio objetivoDanio;
    private final boolean atacanteDestruido;
    private final boolean defensorDestruido;

    public ResultadoCombate(String atacante, String defensor, int danio, ObjetivoDanio objetivoDanio,
            boolean atacanteDestruido, boolean defensorDestruido) {
        this.atacante = atacante;
        this.defensor = defensor;
        this.danio = danio;
        this.objetivoDanio = objetivoDanio;
        this.atacanteDestruido = atacanteDestruido;
        this.defensorDestruido = defensorDestruido;
        this.turno = 0;
        this.jugadorAtacante = "-";
        this.jugadorDefensor = "-";
    }

    /**
     * Agrega la información del turno y de los jugadores. Solo la usa {@link Batalla}.
     */
    void asignarContexto(int turno, String jugadorAtacante, String jugadorDefensor) {
        this.turno = turno;
        this.jugadorAtacante = jugadorAtacante;
        this.jugadorDefensor = jugadorDefensor;
    }

    /**
     * @return texto legible con el resultado del ataque
     */
    public String resumen() {
        StringBuilder sb = new StringBuilder();
        if (defensorDestruido && atacanteDestruido) {
            sb.append("Ambos monstruos fueron destruidos");
        } else if (defensorDestruido) {
            sb.append(nombreSinStats(defensor)).append(" fue destruido");
        } else if (atacanteDestruido) {
            sb.append(nombreSinStats(atacante)).append(" fue destruido");
        } else {
            sb.append("Ningún monstruo fue destruido");
        }
        switch (objetivoDanio) {
            case ATACANTE -> sb.append("; ").append(jugadorAtacante).append(" recibe ").append(danio).append(" de daño");
            case DEFENSOR -> sb.append("; ").append(jugadorDefensor).append(" recibe ").append(danio).append(" de daño");
            default -> sb.append("; nadie recibe daño");
        }
        return sb.append('.').toString();
    }

    /**
     * @return la línea que se escribe en historial_combate.txt
     */
    public String aLineaHistorial() {
        return String.format("Turno %d | Atacante: %s (%s) | Defensor: %s (%s) | Daño: %d | Resultado: %s",
                turno, atacante, jugadorAtacante, defensor, jugadorDefensor, danio, resumen());
    }

    private static String nombreSinStats(String texto) {
        int i = texto.indexOf(" (");
        return i > 0 ? texto.substring(0, i) : texto;
    }

    public int getTurno() {
        return turno;
    }

    public String getJugadorAtacante() {
        return jugadorAtacante;
    }

    public String getJugadorDefensor() {
        return jugadorDefensor;
    }

    public String getAtacante() {
        return atacante;
    }

    public String getDefensor() {
        return defensor;
    }

    public int getDanio() {
        return danio;
    }

    public ObjetivoDanio getObjetivoDanio() {
        return objetivoDanio;
    }

    public boolean isAtacanteDestruido() {
        return atacanteDestruido;
    }

    public boolean isDefensorDestruido() {
        return defensorDestruido;
    }
}
