package modelo;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Pruebas de la jerarquía de cartas (herencia, polimorfismo y validaciones).
 */
class MonstruoTest {

    @Test
    @DisplayName("El constructor guarda nombre, ATQ, DEF y posición")
    void constructor() {
        Monstruo m = new MonstruoNormal("Dragón de Obsidiana", 2800, 2100, Posicion.DEFENSA);
        assertEquals("Dragón de Obsidiana", m.getNombre());
        assertEquals(2800, m.getPuntosAtaque());
        assertEquals(2100, m.getPuntosDefensa());
        assertEquals(Posicion.DEFENSA, m.getPosicion());
        assertFalse(m.estaDestruido());
    }

    @Test
    @DisplayName("No se aceptan puntos negativos ni mayores a 9999")
    void validaPuntos() {
        assertThrows(IllegalArgumentException.class, () -> new MonstruoNormal("X", -1, 0, Posicion.ATAQUE));
        assertThrows(IllegalArgumentException.class, () -> new MonstruoNormal("X", 0, 10000, Posicion.ATAQUE));
    }

    @Test
    @DisplayName("No se acepta un nombre vacío")
    void validaNombre() {
        assertThrows(IllegalArgumentException.class, () -> new MonstruoNormal(" ", 100, 100, Posicion.ATAQUE));
    }

    @Test
    @DisplayName("cambiarPosicion() alterna y cambiarPosicion(Posicion) asigna (sobrecarga)")
    void cambiarPosicionSobrecarga() {
        Monstruo m = new MonstruoNormal("X", 100, 200, Posicion.ATAQUE);
        m.cambiarPosicion();
        assertEquals(Posicion.DEFENSA, m.getPosicion());
        m.cambiarPosicion();
        assertEquals(Posicion.ATAQUE, m.getPosicion());
        m.cambiarPosicion(Posicion.DEFENSA);
        assertEquals(Posicion.DEFENSA, m.getPosicion());
    }

    @Test
    @DisplayName("valorDeCombate usa ATQ en ataque y DEF en defensa")
    void valorDeCombate() {
        Monstruo m = new MonstruoNormal("X", 1700, 900, Posicion.ATAQUE);
        assertEquals(1700, m.valorDeCombate());
        m.cambiarPosicion();
        assertEquals(900, m.valorDeCombate());
    }

    @Test
    @DisplayName("puedeAtacar solo en ataque, sin haber atacado y sin estar destruido")
    void puedeAtacar() {
        Monstruo m = new MonstruoNormal("X", 1000, 1000, Posicion.ATAQUE);
        assertTrue(m.puedeAtacar());
        m.setHaAtacado(true);
        assertFalse(m.puedeAtacar());
        m.setHaAtacado(false);
        m.cambiarPosicion();
        assertFalse(m.puedeAtacar());
        m.cambiarPosicion();
        m.destruir();
        assertFalse(m.puedeAtacar());
    }

    @Test
    @DisplayName("Polimorfismo: cada subclase sobreescribe tipoCarta() y descripcion()")
    void polimorfismo() {
        Carta normal = new MonstruoNormal("Caballero Rúnico", 1800, 1600, Posicion.ATAQUE);
        Carta efecto = new MonstruoEfecto("Hechicera Lunar", 1500, 1200, Posicion.ATAQUE, "Roba una carta");
        assertEquals("Normal", normal.tipoCarta());
        assertEquals("Efecto", efecto.tipoCarta());
        assertTrue(efecto.descripcion().contains("Roba una carta"));
        assertFalse(normal.descripcion().contains("Efecto:"));
    }

    @Test
    @DisplayName("El contador estático aumenta con cada carta creada")
    void contadorEstatico() {
        int antes = Carta.totalCartasCreadas();
        new MonstruoNormal("A", 1, 1, Posicion.ATAQUE);
        new MonstruoEfecto("B", 1, 1, Posicion.ATAQUE, "");
        assertEquals(antes + 2, Carta.totalCartasCreadas());
    }

    @Test
    @DisplayName("Posicion.desdeTexto convierte el texto del combo")
    void posicionDesdeTexto() {
        assertEquals(Posicion.ATAQUE, Posicion.desdeTexto("Ataque"));
        assertEquals(Posicion.DEFENSA, Posicion.desdeTexto("defensa"));
        assertThrows(IllegalArgumentException.class, () -> Posicion.desdeTexto("Volteado"));
    }
}
