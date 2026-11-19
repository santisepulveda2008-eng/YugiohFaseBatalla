package principal;

import controlador.ControladorBatalla;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import modelo.Batalla;
import modelo.Jugador;
import modelo.Monstruo;
import modelo.ObjetivoDanio;
import modelo.ResultadoCombate;
import persistencia.HistorialArchivo;
import vista.VentanaBatalla;

/**
 * Punto de entrada del simulador y generación de reportes sobre los objetos
 * guardados en las estructuras dinámicas (listas) del duelo.
 */
public class Main {

    public static void main(String[] args) {
        VentanaBatalla.aplicarTema();
        java.awt.EventQueue.invokeLater(() -> {
            VentanaBatalla vista = new VentanaBatalla();
            ControladorBatalla controlador = new ControladorBatalla(vista, new HistorialArchivo());
            controlador.iniciar();
        });
    }

    /**
     * Genera todos los reportes del duelo actual.
     *
     * @param batalla duelo actual
     * @return texto con los reportes
     */
    public static String generarReportes(Batalla batalla) {
        List<Jugador> jugadores = new ArrayList<>();
        jugadores.add(batalla.getJugador1());
        jugadores.add(batalla.getJugador2());
        return reporteMonstruosPorAtaque(jugadores)
                + "\n" + reporteCementerios(jugadores)
                + "\n" + reporteDanioPorJugador(batalla.getRegistros());
    }

    /**
     * Reporte 1: monstruos en el campo ordenados de mayor a menor ATQ.
     */
    private static String reporteMonstruosPorAtaque(List<Jugador> jugadores) {
        StringBuilder sb = new StringBuilder("REPORTE 1 - MONSTRUOS EN EL CAMPO (ordenados por ATQ)\n");
        sb.append("-".repeat(70)).append('\n');
        List<String[]> filas = new ArrayList<>();
        for (Jugador j : jugadores) {
            for (Monstruo m : j.getCampo()) {
                filas.add(new String[]{j.getNombre(), m.getNombre(), m.tipoCarta(),
                    String.valueOf(m.getPuntosAtaque()), String.valueOf(m.getPuntosDefensa()),
                    m.getPosicion().getEtiqueta()});
            }
        }
        if (filas.isEmpty()) {
            return sb.append("No hay monstruos en el campo.\n").toString();
        }
        filas.sort(Comparator.comparingInt((String[] f) -> Integer.parseInt(f[3])).reversed());
        sb.append(String.format("%-14s %-22s %-7s %5s %5s  %s%n", "Jugador", "Monstruo", "Tipo", "ATQ", "DEF", "Posición"));
        for (String[] f : filas) {
            sb.append(String.format("%-14s %-22s %-7s %5s %5s  %s%n", f[0], f[1], f[2], f[3], f[4], f[5]));
        }
        return sb.toString();
    }

    /**
     * Reporte 2: monstruos destruidos (cementerio) de cada jugador.
     */
    private static String reporteCementerios(List<Jugador> jugadores) {
        StringBuilder sb = new StringBuilder("REPORTE 2 - CEMENTERIOS (monstruos destruidos)\n");
        sb.append("-".repeat(70)).append('\n');
        for (Jugador j : jugadores) {
            List<Monstruo> cementerio = j.getCementerio();
            sb.append(j.getNombre()).append(" (").append(cementerio.size()).append("): ");
            if (cementerio.isEmpty()) {
                sb.append("ninguno");
            } else {
                List<String> nombres = new ArrayList<>();
                for (Monstruo m : cementerio) {
                    nombres.add(m.getNombre() + " [ATQ " + m.getPuntosAtaque() + "]");
                }
                sb.append(String.join(", ", nombres));
            }
            sb.append('\n');
        }
        return sb.toString();
    }

    /**
     * Reporte 3: daño total recibido por cada jugador y ataque más fuerte del duelo.
     */
    private static String reporteDanioPorJugador(List<ResultadoCombate> registros) {
        StringBuilder sb = new StringBuilder("REPORTE 3 - DAÑO POR JUGADOR\n");
        sb.append("-".repeat(70)).append('\n');
        if (registros.isEmpty()) {
            return sb.append("Todavía no se ha realizado ningún ataque.\n").toString();
        }
        Map<String, Integer> danioRecibido = new LinkedHashMap<>();
        ResultadoCombate mayor = null;
        for (ResultadoCombate r : registros) {
            String afectado = r.getObjetivoDanio() == ObjetivoDanio.ATACANTE ? r.getJugadorAtacante()
                    : r.getObjetivoDanio() == ObjetivoDanio.DEFENSOR ? r.getJugadorDefensor() : null;
            if (afectado != null) {
                danioRecibido.merge(afectado, r.getDanio(), Integer::sum);
            }
            if (mayor == null || r.getDanio() > mayor.getDanio()) {
                mayor = r;
            }
        }
        sb.append("Ataques realizados: ").append(registros.size()).append('\n');
        for (Map.Entry<String, Integer> e : danioRecibido.entrySet()) {
            sb.append(String.format("  %-16s recibió %5d puntos de daño%n", e.getKey(), e.getValue()));
        }
        sb.append("Ataque con más daño: turno ").append(mayor.getTurno()).append(", ")
                .append(mayor.getAtacante()).append(" -> ").append(mayor.getDefensor())
                .append(" (").append(mayor.getDanio()).append(" de daño)\n");
        return sb.toString();
    }
}
