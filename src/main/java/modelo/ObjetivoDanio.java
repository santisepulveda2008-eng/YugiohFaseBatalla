package modelo;

/**
 * Indica qué jugador recibe el daño de un combate.
 */
public enum ObjetivoDanio {
    /** El dueño del monstruo atacante pierde LP. */
    ATACANTE,
    /** El dueño del monstruo atacado (o el rival en un ataque directo) pierde LP. */
    DEFENSOR,
    /** Nadie pierde LP. */
    NINGUNO
}
