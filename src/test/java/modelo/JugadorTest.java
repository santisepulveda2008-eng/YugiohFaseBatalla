package modelo;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Pruebas de la gestión de LP y del campo de un jugador.
 */
class JugadorTest {

    private Jugador jugador;

    @BeforeEach
    void setUp() {
        jugador = new Jugador("Yami");
    }

    @Test
    @DisplayName("El jugador inicia con 8000 LP y sin monstruos")
    void estadoInicial() {
        assertEquals(Jugador.LP_INICIALES, jugador.getPuntosVida());
        assertEquals(8000, jugador.getPuntosVida());
        assertFalse(jugador.tieneMonstruos());
        assertFalse(jugador.estaDerrotado());
    }

    @Test
    @DisplayName("recibirDanio resta los puntos indicados")
    void recibirDanioRestaLp() {
        jugador.recibirDanio(1500);
        assertEquals(6500, jugador.getPuntosVida());
    }

    @Test
    @DisplayName("Los LP nunca bajan de 0 y el jugador queda derrotado")
    void lpNoNegativos() {
        jugador.recibirDanio(9000);
        assertEquals(0, jugador.getPuntosVida());
        assertTrue(jugador.estaDerrotado());
    }

    @Test
    @DisplayName("No se acepta daño negativo")
    void danioNegativo() {
        assertThrows(IllegalArgumentException.class, () -> jugador.recibirDanio(-100));
    }

    @Test
    @DisplayName("Se pueden invocar hasta 5 monstruos; el sexto lanza AccionInvalidaException")
    void limiteDeCampo() throws AccionInvalidaException {
        for (int i = 0; i < Jugador.MAX_MONSTRUOS; i++) {
            jugador.invocar(new MonstruoNormal("M" + i, 1000, 1000, Posicion.ATAQUE));
        }
        assertEquals(5, jugador.getCampo().size());
        assertThrows(AccionInvalidaException.class,
                () -> jugador.invocar(new MonstruoNormal("Extra", 1, 1, Posicion.ATAQUE)));
    }

    @Test
    @DisplayName("enviarAlCementerio mueve el monstruo del campo al cementerio")
    void enviarAlCementerio() throws AccionInvalidaException {
        Monstruo m = new MonstruoNormal("Guerrero", 1500, 1200, Posicion.ATAQUE);
        jugador.invocar(m);
        jugador.enviarAlCementerio(m);
        assertFalse(jugador.getCampo().contains(m));
        assertTrue(jugador.getCementerio().contains(m));
    }

    @Test
    @DisplayName("reiniciarAtaques permite volver a atacar")
    void reiniciarAtaques() throws AccionInvalidaException {
        Monstruo m = new MonstruoNormal("Guerrero", 1500, 1200, Posicion.ATAQUE);
        jugador.invocar(m);
        m.setHaAtacado(true);
        jugador.reiniciarAtaques();
        assertFalse(m.isHaAtacado());
    }

    @Test
    @DisplayName("El campo devuelto no se puede modificar desde afuera")
    void campoSoloLectura() {
        assertThrows(UnsupportedOperationException.class,
                () -> jugador.getCampo().add(new MonstruoNormal("X", 1, 1, Posicion.ATAQUE)));
    }

    @Test
    @DisplayName("Un jugador sin nombre no es válido")
    void nombreVacio() {
        assertThrows(IllegalArgumentException.class, () -> new Jugador("  "));
    }
}
