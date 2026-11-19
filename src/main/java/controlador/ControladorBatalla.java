package controlador;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import modelo.AccionInvalidaException;
import modelo.Batalla;
import modelo.Carta;
import modelo.Jugador;
import modelo.Monstruo;
import modelo.MonstruoEfecto;
import modelo.MonstruoNormal;
import modelo.Posicion;
import modelo.ResultadoCombate;
import persistencia.RepositorioHistorial;
import principal.Main;
import vista.VentanaBatalla;

/**
 * Controlador del patrón MVC: recibe los eventos de la vista, invoca al modelo
 * y actualiza la vista con el nuevo estado.
 */
public class ControladorBatalla implements ActionListener {

    private final VentanaBatalla vista;
    private Batalla batalla;
    private final RepositorioHistorial historial;

    public ControladorBatalla(VentanaBatalla vista, RepositorioHistorial historial) {
        this.vista = vista;
        this.historial = historial;
    }

    /**
     * Conecta los eventos, carga el historial guardado y abre el primer duelo.
     */
    public void iniciar() {
        vista.setControlador(this);
        cargarHistorialGuardado();
        vista.setVisible(true);
        if (!crearDuelo()) {
            batalla = new Batalla(new Jugador("Jugador 1"), new Jugador("Jugador 2"));
            refrescarVista();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        switch (e.getActionCommand()) {
            case "INVOCAR_1" -> invocarMonstruo(1);
            case "INVOCAR_2" -> invocarMonstruo(2);
            case "POSICION_1" -> cambiarPosicion(1);
            case "POSICION_2" -> cambiarPosicion(2);
            case "TIPO_1" -> vista.habilitarEfecto(1, "Efecto".equals(vista.leerTipo(1)));
            case "TIPO_2" -> vista.habilitarEfecto(2, "Efecto".equals(vista.leerTipo(2)));
            case "ATACAR" -> atacar();
            case "DIRECTO" -> ataqueDirecto();
            case "TERMINAR" -> terminarTurno();
            case "REPORTES" -> mostrarReportes();
            case "NUEVO" -> nuevoDuelo();
            default -> { /* evento no manejado */ }
        }
    }

    // ------------------------------------------------------------------
    // Acciones
    // ------------------------------------------------------------------

    private void invocarMonstruo(int numeroJugador) {
        try {
            Monstruo monstruo = construirMonstruo(numeroJugador);
            jugador(numeroJugador).invocar(monstruo);
            vista.limpiarFormulario(numeroJugador);
            vista.mostrarResultado(jugador(numeroJugador).getNombre() + " invoca a " + monstruo.getNombre()
                    + " en posición de " + monstruo.getPosicion().getEtiqueta() + ".");
            refrescarVista();
        } catch (NumberFormatException ex) {
            vista.mostrarError("ATQ y DEF deben ser números enteros entre 0 y " + Monstruo.PUNTOS_MAXIMOS + ".");
        } catch (IllegalArgumentException | AccionInvalidaException ex) {
            vista.mostrarError(ex.getMessage());
        }
    }

    private void cambiarPosicion(int numeroJugador) {
        Monstruo m = monstruoSeleccionado(numeroJugador);
        if (m == null) {
            vista.mostrarError("Selecciona en la tabla el monstruo que quieres cambiar de posición.");
            return;
        }
        if (m.isHaAtacado()) {
            vista.mostrarError(m.getNombre() + " ya atacó en este turno y no puede cambiar de posición.");
            return;
        }
        m.cambiarPosicion();
        vista.mostrarResultado(m.getNombre() + " pasa a posición de " + m.getPosicion().getEtiqueta() + ".");
        refrescarVista();
    }

    private void atacar() {
        int actual = numeroJugadorActual();
        Monstruo atacante = monstruoSeleccionado(actual);
        if (!batalla.rival().tieneMonstruos()) {
            vista.mostrarError(batalla.rival().getNombre()
                    + " no tiene monstruos. Usa ATAQUE DIRECTO.");
            return;
        }
        Monstruo defensor = monstruoSeleccionado(actual == 1 ? 2 : 1);
        try {
            registrar(batalla.atacar(atacante, defensor));
        } catch (AccionInvalidaException ex) {
            vista.mostrarError(ex.getMessage());
        }
    }

    private void ataqueDirecto() {
        Monstruo atacante = monstruoSeleccionado(numeroJugadorActual());
        try {
            registrar(batalla.atacar(atacante));
        } catch (AccionInvalidaException ex) {
            vista.mostrarError(ex.getMessage());
        }
    }

    private void terminarTurno() {
        try {
            batalla.terminarTurno();
            vista.mostrarResultado("Turno " + batalla.getNumeroTurno() + ": es el turno de "
                    + batalla.jugadorActual().getNombre() + ".");
            refrescarVista();
        } catch (AccionInvalidaException ex) {
            vista.mostrarError(ex.getMessage());
        }
    }

    private void mostrarReportes() {
        vista.mostrarReporte(Main.generarReportes(batalla));
    }

    private void nuevoDuelo() {
        if (vista.confirmar("¿Iniciar un nuevo duelo? Se perderá el estado actual (el historial se conserva).")) {
            crearDuelo();
        }
    }

    // ------------------------------------------------------------------
    // Apoyo
    // ------------------------------------------------------------------

    /**
     * Pide los nombres y crea una nueva batalla.
     *
     * @return false si el usuario canceló
     */
    private boolean crearDuelo() {
        String nombre1 = vista.pedirNombreJugador(1, "Jugador 1");
        if (nombre1 == null) {
            return false;
        }
        String nombre2 = vista.pedirNombreJugador(2, "Jugador 2");
        if (nombre2 == null) {
            return false;
        }
        try {
            batalla = new Batalla(new Jugador(nombre1), new Jugador(nombre2));
        } catch (IllegalArgumentException ex) {
            vista.mostrarError(ex.getMessage());
            return crearDuelo();
        }
        String fecha = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        String encabezado = "===== Nuevo duelo (" + fecha + "): " + nombre1.trim() + " vs " + nombre2.trim()
                + " | LP iniciales: " + Jugador.LP_INICIALES + " =====";
        guardarLinea(encabezado);
        vista.agregarAlHistorial(encabezado);
        vista.mostrarResultado("¡Que comience el duelo! Invoca monstruos para cada jugador.");
        refrescarVista();
        return true;
    }

    private Monstruo construirMonstruo(int n) {
        String nombre = vista.leerNombre(n);
        int atq = Integer.parseInt(vista.leerAtaque(n));
        int def = Integer.parseInt(vista.leerDefensa(n));
        Posicion posicion = Posicion.desdeTexto(vista.leerPosicion(n));
        if ("Efecto".equals(vista.leerTipo(n))) {
            return new MonstruoEfecto(nombre, atq, def, posicion, vista.leerEfecto(n));
        }
        return new MonstruoNormal(nombre, atq, def, posicion);
    }

    private void registrar(ResultadoCombate resultado) {
        guardarResultado(resultado);
        vista.agregarAlHistorial(resultado.aLineaHistorial());
        vista.mostrarResultado(resultado.resumen());
        refrescarVista();
        if (batalla.haTerminado()) {
            String fin = "*** " + batalla.ganador().getNombre() + " gana el duelo en el turno "
                    + batalla.getNumeroTurno() + " ***";
            guardarLinea(fin);
            vista.agregarAlHistorial(fin);
            vista.mostrarResultado(fin);
            vista.mostrarInformacion("Fin del duelo", "¡" + batalla.ganador().getNombre() + " gana el duelo!");
        }
    }

    private void guardarResultado(ResultadoCombate resultado) {
        try {
            historial.guardar(resultado);
        } catch (IOException ex) {
            vista.mostrarError("No se pudo guardar en el historial: " + ex.getMessage());
        }
    }

    private void guardarLinea(String linea) {
        try {
            historial.guardar(linea);
        } catch (IOException ex) {
            vista.mostrarError("No se pudo guardar en el historial: " + ex.getMessage());
        }
    }

    private void cargarHistorialGuardado() {
        try {
            List<String> lineas = historial.leerTodo();
            vista.limpiarHistorial();
            for (String linea : lineas) {
                vista.agregarAlHistorial(linea);
            }
        } catch (IOException ex) {
            vista.mostrarError("No se pudo leer el historial: " + ex.getMessage());
        }
    }

    private void refrescarVista() {
        boolean turnoJ1 = batalla.jugadorActual() == batalla.getJugador1();
        vista.actualizarJugador(1, batalla.getJugador1(), turnoJ1 && !batalla.haTerminado());
        vista.actualizarJugador(2, batalla.getJugador2(), !turnoJ1 && !batalla.haTerminado());
        vista.actualizarTurno(batalla.getNumeroTurno(), batalla.jugadorActual().getNombre());
        vista.habilitarAtaqueDirecto(!batalla.rival().tieneMonstruos() && !batalla.haTerminado());
        vista.actualizarEstado("Cartas creadas: " + Carta.totalCartasCreadas()
                + "   •   Ataques en este duelo: " + batalla.getRegistros().size()
                + "   •   Historial: " + RepositorioHistorial.ARCHIVO_POR_DEFECTO);
    }

    private Jugador jugador(int n) {
        return n == 1 ? batalla.getJugador1() : batalla.getJugador2();
    }

    private int numeroJugadorActual() {
        return batalla.jugadorActual() == batalla.getJugador1() ? 1 : 2;
    }

    private Monstruo monstruoSeleccionado(int n) {
        int fila = vista.filaSeleccionada(n);
        List<Monstruo> campo = jugador(n).getCampo();
        return (fila >= 0 && fila < campo.size()) ? campo.get(fila) : null;
    }
}
