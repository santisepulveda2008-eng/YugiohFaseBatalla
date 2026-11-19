package modelo;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Pruebas de las reglas oficiales de cálculo de daño.
 */
class CalculadoraDanioTest {

    private static Monstruo enAtaque(String nombre, int atq) {
        return new MonstruoNormal(nombre, atq, 1000, Posicion.ATAQUE);
    }

    private static Monstruo enDefensa(String nombre, int def) {
        return new MonstruoNormal(nombre, 1000, def, Posicion.DEFENSA);
    }

    @Test
    @DisplayName("Ataque vs Ataque: el atacante más fuerte destruye y daña al rival")
    void ataqueVsAtaqueGanaAtacante() {
        ResultadoCombate r = CalculadoraDanio.calcular(enAtaque("A", 2500), enAtaque("D", 1800));
        assertEquals(700, r.getDanio());
        assertEquals(ObjetivoDanio.DEFENSOR, r.getObjetivoDanio());
        assertTrue(r.isDefensorDestruido());
        assertFalse(r.isAtacanteDestruido());
    }

    @Test
    @DisplayName("Ataque vs Ataque: el atacante más débil es destruido y su dueño recibe daño")
    void ataqueVsAtaquePierdeAtacante() {
        ResultadoCombate r = CalculadoraDanio.calcular(enAtaque("A", 1200), enAtaque("D", 2000));
        assertEquals(800, r.getDanio());
        assertEquals(ObjetivoDanio.ATACANTE, r.getObjetivoDanio());
        assertTrue(r.isAtacanteDestruido());
        assertFalse(r.isDefensorDestruido());
    }

    @Test
    @DisplayName("Ataque vs Ataque con empate: ambos destruidos y sin daño")
    void ataqueVsAtaqueEmpate() {
        ResultadoCombate r = CalculadoraDanio.calcular(enAtaque("A", 1500), enAtaque("D", 1500));
        assertEquals(0, r.getDanio());
        assertEquals(ObjetivoDanio.NINGUNO, r.getObjetivoDanio());
        assertTrue(r.isAtacanteDestruido());
        assertTrue(r.isDefensorDestruido());
    }

    @Test
    @DisplayName("Ataque vs Ataque 0 contra 0: ninguno es destruido")
    void ataqueVsAtaqueCeroContraCero() {
        ResultadoCombate r = CalculadoraDanio.calcular(enAtaque("A", 0), enAtaque("D", 0));
        assertFalse(r.isAtacanteDestruido());
        assertFalse(r.isDefensorDestruido());
        assertEquals(0, r.getDanio());
    }

    @Test
    @DisplayName("Ataque vs Defensa: ATQ mayor destruye al defensor sin hacer daño")
    void ataqueVsDefensaGanaAtacante() {
        ResultadoCombate r = CalculadoraDanio.calcular(enAtaque("A", 2000), enDefensa("D", 1500));
        assertEquals(0, r.getDanio());
        assertEquals(ObjetivoDanio.NINGUNO, r.getObjetivoDanio());
        assertTrue(r.isDefensorDestruido());
        assertFalse(r.isAtacanteDestruido());
    }

    @Test
    @DisplayName("Ataque vs Defensa: DEF mayor daña al atacante sin destruir a nadie")
    void ataqueVsDefensaPierdeAtacante() {
        ResultadoCombate r = CalculadoraDanio.calcular(enAtaque("A", 1000), enDefensa("D", 2100));
        assertEquals(1100, r.getDanio());
        assertEquals(ObjetivoDanio.ATACANTE, r.getObjetivoDanio());
        assertFalse(r.isDefensorDestruido());
        assertFalse(r.isAtacanteDestruido());
    }

    @Test
    @DisplayName("Ataque vs Defensa con empate: no pasa nada")
    void ataqueVsDefensaEmpate() {
        ResultadoCombate r = CalculadoraDanio.calcular(enAtaque("A", 1600), enDefensa("D", 1600));
        assertEquals(0, r.getDanio());
        assertFalse(r.isDefensorDestruido());
        assertFalse(r.isAtacanteDestruido());
    }

    @Test
    @DisplayName("Ataque directo: el daño es igual al ATQ del atacante")
    void ataqueDirecto() {
        ResultadoCombate r = CalculadoraDanio.calcular(enAtaque("A", 3000));
        assertEquals(3000, r.getDanio());
        assertEquals(ObjetivoDanio.DEFENSOR, r.getObjetivoDanio());
        assertEquals(CalculadoraDanio.ATAQUE_DIRECTO, r.getDefensor());
    }

    @Test
    @DisplayName("Un monstruo en defensa no puede atacar")
    void atacanteEnDefensaLanzaExcepcion() {
        Monstruo enDefensa = enDefensa("A", 2000);
        assertThrows(IllegalArgumentException.class, () -> CalculadoraDanio.calcular(enDefensa));
        assertThrows(IllegalArgumentException.class,
                () -> CalculadoraDanio.calcular(enDefensa, enAtaque("D", 100)));
    }

    @Test
    @DisplayName("La calculadora no modifica a los monstruos")
    void noModificaMonstruos() {
        Monstruo a = enAtaque("A", 1000);
        Monstruo d = enAtaque("D", 3000);
        CalculadoraDanio.calcular(a, d);
        assertFalse(a.estaDestruido());
        assertFalse(a.isHaAtacado());
    }
}
