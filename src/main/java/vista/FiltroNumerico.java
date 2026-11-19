package vista;

import java.awt.Toolkit;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

/**
 * Filtro para campos de texto que solo deja escribir dígitos y limita la cantidad.
 * Evita que el usuario ingrese letras en los campos de ATQ y DEF.
 */
public class FiltroNumerico extends DocumentFilter {

    private final int maxDigitos;

    public FiltroNumerico(int maxDigitos) {
        this.maxDigitos = maxDigitos;
    }

    @Override
    public void insertString(FilterBypass fb, int offset, String texto, AttributeSet attr)
            throws BadLocationException {
        replace(fb, offset, 0, texto, attr);
    }

    @Override
    public void replace(FilterBypass fb, int offset, int largo, String texto, AttributeSet attr)
            throws BadLocationException {
        if (texto == null) {
            return;
        }
        int largoFinal = fb.getDocument().getLength() - largo + texto.length();
        if (texto.chars().allMatch(Character::isDigit) && largoFinal <= maxDigitos) {
            super.replace(fb, offset, largo, texto, attr);
        } else {
            Toolkit.getDefaultToolkit().beep();
        }
    }
}
