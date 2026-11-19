package modelo;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Pruebas de la batalla: turnos, aplicación de daño y validaciones de reglas.
 */
class BatallaTest {

    private Jugador j1;
    private Jugador j2;
    private Batalla batalla;

    @BeforeEach
    void setUp() {
        j1 = new Jugador("Ana");
        j2 = new Jugador("Luis");
        batalla = new Batalla(j1, j2);
    }

    @Test
    @DisplayName("Inicia el jugador 1 en el turno 1")
    void estadoInicial() {
        assertSame(j1, batalla.jugadorActual());
        assertSame(j2, batalla.rival());
        assertEquals(1, batalla.getNumeroTurno());
        assertFalse(batalla.haTerminado());
        assertNull(batalla.ganador());
    }

    @Test
    @DisplayName("Ataque vs Ataque reduce LP del rival y manda al defensor al cementerio")
    void ataqueAplicaDanioYDestruye() throws AccionInvalidaException {
        Monstruo a = new MonstruoNormal("Dragón", 3000, 2500, Posicion.ATAQUE);
        Monstruo d = new MonstruoNormal("Soldado", 1200, 1000, Posicion.ATAQUE);
        j1.invocar(a);
        j2.invocar(d);

        ResultadoCombate r = batalla.atacar(a, d);

        assertEquals(1800, r.getDanio());
        assertEquals(8000 - 1800, j2.getPuntosVida());
        assertEquals(8000, j1.getPuntosVida());
        assertTrue(j2.getCementerio().contains(d));
        assertFalse(j2.getCampo().contains(d));
        assertTrue(a.isHaAtacado());
        assertEquals(1, batalla.getRegistros().size());
        assertEquals("Ana", r.getJugadorAtacante());
        assertEquals("Luis", r.getJugadorDefensor());
    }

    @Test
    @DisplayName("Atacar contra una defensa mayor daña al atacante")
    void ataqueContraDefensaMayor() throws AccionInvalidaException {
        Monstruo a = new MonstruoNormal("Lobo", 1000, 800, Posicion.ATAQUE);
        Monstruo d = new MonstruoNormal("Muro", 0, 2000, Posicion.DEFENSA);
        j1.invocar(a);
        j2.invocar(d);

        batalla.atacar(a, d);

        assertEquals(7000, j1.getPuntosVida());
        assertEquals(8000, j2.getPuntosVida());
        assertTrue(j1.getCampo().contains(a));
        assertTrue(j2.getCampo().contains(d));
    }

    @Test
    @DisplayName("El ataque directo solo se permite si el rival no tiene monstruos")
    void ataqueDirecto() throws AccionInvalidaException {
        Monstruo a = new MonstruoNormal("Dragón", 2500, 2000, Posicion.ATAQUE);
        j1.invocar(a);
        Monstruo d = new MonstruoNormal("Escudo", 100, 100, Posicion.DEFENSA);
        j2.invocar(d);
        assertThrows(AccionInvalidaException.class, () -> batalla.atacar(a));

        j2.enviarAlCementerio(d);
        batalla.atacar(a);
        assertEquals(5500, j2.getPuntosVida());
    }

    @Test
    @DisplayName("Un monstruo no puede atacar dos veces en el mismo turno")
    void noAtacaDosVeces() throws AccionInvalidaException {
        Monstruo a = new MonstruoNormal("Dragón", 2500, 2000, Posicion.ATAQUE);
        j1.invocar(a);
        batalla.atacar(a);
        AccionInvalidaException ex = assertThrows(AccionInvalidaException.class, () -> batalla.atacar(a));
        assertTrue(ex.getMessage().contains("ya atacó"));
    }

    @Test
    @DisplayName("Un monstruo en defensa no puede atacar")
    void enDefensaNoAtaca() throws AccionInvalidaException {
        Monstruo a = new MonstruoNormal("Muro", 0, 2000, Posicion.DEFENSA);
        j1.invocar(a);
        assertThrows(AccionInvalidaException.class, () -> batalla.atacar(a));
    }

    @Test
    @DisplayName("No se puede atacar con monstruos del rival")
    void noAtacaConMonstruoAjeno() throws AccionInvalidaException {
        Monstruo ajeno = new MonstruoNormal("Ajeno", 1000, 1000, Posicion.ATAQUE);
        j2.invocar(ajeno);
        assertThrows(AccionInvalidaException.class, () -> batalla.atacar(ajeno));
    }

    @Test
    @DisplayName("terminarTurno cambia de jugador, suma turno y reinicia ataques")
    void terminarTurno() throws AccionInvalidaException {
        Monstruo m2 = new MonstruoNormal("Lobo", 1000, 800, Posicion.ATAQUE);
        j2.invocar(m2);
        m2.setHaAtacado(true);

        batalla.terminarTurno();

        assertSame(j2, batalla.jugadorActual());
        assertEquals(2, batalla.getNumeroTurno());
        assertFalse(m2.isHaAtacado());
    }

    @Test
    @DisplayName("Cuando un jugador llega a 0 LP el duelo termina y hay ganador")
    void finDelDuelo() throws AccionInvalidaException {
        Monstruo a = new MonstruoNormal("Titán", 9999, 0, Posicion.ATAQUE);
        j1.invocar(a);
        batalla.atacar(a);
        assertEquals(0, j2.getPuntosVida());
        assertTrue(batalla.haTerminado());
        assertSame(j1, batalla.ganador());
        assertThrows(AccionInvalidaException.class, () -> batalla.terminarTurno());
    }

    @Test
    @DisplayName("La línea de historial contiene atacante, defensor, daño y resultado")
    void lineaHistorial() throws AccionInvalidaException {
        Monstruo a = new MonstruoNormal("Dragón", 3000, 2500, Posicion.ATAQUE);
        Monstruo d = new MonstruoNormal("Soldado", 1200, 1000, Posicion.ATAQUE);
        j1.invocar(a);
        j2.invocar(d);
        String linea = batalla.atacar(a, d).aLineaHistorial();
        assertTrue(linea.contains("Dragón"));
        assertTrue(linea.contains("Soldado"));
        assertTrue(linea.contains("Daño: 1800"));
        assertTrue(linea.contains("Soldado fue destruido"));
    }
}
