package vista;

import java.awt.Color;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.plaf.metal.MetalLookAndFeel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.text.AbstractDocument;
import modelo.Jugador;
import modelo.Monstruo;

/**
 * Vista principal del simulador, diseñada con el editor visual (JFrame Form) de NetBeans.
 * <p>
 * Solo muestra información y lee lo que escribe el usuario: no contiene lógica de negocio.
 * Los eventos de los botones los atiende el {@code ControladorBatalla}.
 */
public class VentanaBatalla extends javax.swing.JFrame {

    private static final String[] COLUMNAS = {"#", "Nombre", "Tipo", "ATQ", "DEF", "Posición", "Estado"};
    private static final Color COLOR_TURNO = new Color(255, 213, 79);
    private static final Color AZUL = new Color(41, 121, 255);
    private static final Color ROJO = new Color(229, 57, 53);

    /**
     * Crea la ventana.
     */
    public VentanaBatalla() {
        initComponents();
        configurarTabla(tablaCampoJ1);
        configurarTabla(tablaCampoJ2);
        instalarFiltroNumerico(txtAtaqueJ1, txtDefensaJ1, txtAtaqueJ2, txtDefensaJ2);
        usarCursorDeMano(btnInvocarJ1, btnInvocarJ2, btnCambiarPosicionJ1, btnCambiarPosicionJ2,
                btnAtacar, btnAtaqueDirecto, btnTerminarTurno, btnReportes, btnNuevoDuelo);
        txtEfectoJ1.setEnabled(false);
        txtEfectoJ2.setEnabled(false);
        setIconImage(new ImageIcon(getClass().getResource("/vista/iconos/carta.png")).getImage());
    }

    /**
     * Aplica el tema visual de la aplicación. Se llama antes de crear la ventana.
     */
    public static void aplicarTema() {
        try {
            UIManager.setLookAndFeel(new MetalLookAndFeel());
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            // Si falla se usa el tema por defecto
        }
        UIManager.put("ComboBox.selectionBackground", new Color(124, 77, 255));
        UIManager.put("ComboBox.selectionForeground", Color.WHITE);
        UIManager.put("ToolTip.background", new Color(30, 26, 46));
        UIManager.put("ToolTip.foreground", new Color(236, 239, 241));
        UIManager.put("ToolTip.border", BorderFactory.createLineBorder(new Color(255, 213, 79)));
        UIManager.put("TextField.inactiveBackground", new Color(34, 30, 50));
        UIManager.put("TextField.inactiveForeground", new Color(110, 104, 140));
    }

    // ------------------------------------------------------------------
    // Conexión con el controlador
    // ------------------------------------------------------------------

    /**
     * Registra el controlador como oyente de todos los botones y combos.
     * Cada componente tiene un actionCommand definido en el diseñador.
     *
     * @param controlador oyente de eventos
     */
    public void setControlador(ActionListener controlador) {
        JButton[] botones = {btnInvocarJ1, btnInvocarJ2, btnCambiarPosicionJ1, btnCambiarPosicionJ2,
            btnAtacar, btnAtaqueDirecto, btnTerminarTurno, btnReportes, btnNuevoDuelo};
        for (JButton b : botones) {
            b.addActionListener(controlador);
        }
        cmbTipoJ1.addActionListener(controlador);
        cmbTipoJ2.addActionListener(controlador);
    }

    // ------------------------------------------------------------------
    // Mostrar información
    // ------------------------------------------------------------------

    /**
     * Muestra nombre, LP, barra de vida y campo de un jugador.
     *
     * @param n       número del jugador (1 o 2)
     * @param jugador jugador a mostrar
     * @param enTurno true si es el jugador que tiene el turno
     */
    public void actualizarJugador(int n, Jugador jugador, boolean enTurno) {
        JLabel lblNombre = n == 1 ? lblNombreJ1 : lblNombreJ2;
        JLabel lblLp = n == 1 ? lblLpJ1 : lblLpJ2;
        JProgressBar barra = n == 1 ? barraVidaJ1 : barraVidaJ2;
        JTable tabla = tablaDe(n);

        lblNombre.setText((enTurno ? "» " : "") + jugador.getNombre());
        lblNombre.setForeground(enTurno ? COLOR_TURNO : new Color(236, 239, 241));
        lblLp.setText("LP " + jugador.getPuntosVida());
        barra.setMaximum(Jugador.LP_INICIALES);
        barra.setValue(jugador.getPuntosVida());
        barra.setForeground(colorVida(jugador.getPuntosVida()));

        Color acento = n == 1 ? AZUL : ROJO;
        (n == 1 ? panelJugador1 : panelJugador2).setBorder(
                BorderFactory.createLineBorder(enTurno ? COLOR_TURNO : acento, enTurno ? 3 : 2));

        int seleccion = tabla.getSelectedRow();
        DefaultTableModel modelo = (DefaultTableModel) tabla.getModel();
        modelo.setRowCount(0);
        List<Monstruo> campo = jugador.getCampo();
        for (int i = 0; i < campo.size(); i++) {
            Monstruo m = campo.get(i);
            String estado = m.puedeAtacar() ? "Listo" : (m.isHaAtacado() ? "Ya atacó" : "—");
            modelo.addRow(new Object[]{i + 1, m.getNombre(), m.tipoCarta(), m.getPuntosAtaque(),
                m.getPuntosDefensa(), m.getPosicion().getEtiqueta(), estado});
        }
        if (seleccion >= 0 && seleccion < campo.size()) {
            tabla.setRowSelectionInterval(seleccion, seleccion);
        }
    }

    public void actualizarTurno(int numeroTurno, String jugador) {
        lblTurno.setText("Turno " + numeroTurno + "  •  Juega " + jugador);
    }

    public void mostrarResultado(String texto) {
        lblResultado.setText("<html><div style=\"text-align:center\">" + escaparHtml(texto) + "</div></html>");
    }

    public void agregarAlHistorial(String linea) {
        txtHistorial.append(linea + System.lineSeparator());
        txtHistorial.setCaretPosition(txtHistorial.getDocument().getLength());
    }

    public void limpiarHistorial() {
        txtHistorial.setText("");
    }

    public void actualizarEstado(String texto) {
        lblEstado.setText(texto);
    }

    public void habilitarAtaqueDirecto(boolean habilitado) {
        btnAtaqueDirecto.setEnabled(habilitado);
    }

    public void habilitarEfecto(int n, boolean habilitado) {
        JTextField txt = n == 1 ? txtEfectoJ1 : txtEfectoJ2;
        txt.setEnabled(habilitado);
        if (!habilitado) {
            txt.setText("");
        }
    }

    // ------------------------------------------------------------------
    // Leer datos del usuario
    // ------------------------------------------------------------------

    public String leerNombre(int n) {
        return (n == 1 ? txtNombreJ1 : txtNombreJ2).getText().trim();
    }

    public String leerAtaque(int n) {
        return (n == 1 ? txtAtaqueJ1 : txtAtaqueJ2).getText().trim();
    }

    public String leerDefensa(int n) {
        return (n == 1 ? txtDefensaJ1 : txtDefensaJ2).getText().trim();
    }

    public String leerPosicion(int n) {
        return String.valueOf((n == 1 ? cmbPosicionJ1 : cmbPosicionJ2).getSelectedItem());
    }

    public String leerTipo(int n) {
        return String.valueOf((n == 1 ? cmbTipoJ1 : cmbTipoJ2).getSelectedItem());
    }

    public String leerEfecto(int n) {
        return (n == 1 ? txtEfectoJ1 : txtEfectoJ2).getText().trim();
    }

    /**
     * @param n número del jugador
     * @return índice del monstruo seleccionado en la tabla del jugador, o -1 si no hay selección
     */
    public int filaSeleccionada(int n) {
        return tablaDe(n).getSelectedRow();
    }

    public void limpiarFormulario(int n) {
        (n == 1 ? txtNombreJ1 : txtNombreJ2).setText("");
        (n == 1 ? txtAtaqueJ1 : txtAtaqueJ2).setText("");
        (n == 1 ? txtDefensaJ1 : txtDefensaJ2).setText("");
        (n == 1 ? txtEfectoJ1 : txtEfectoJ2).setText("");
        JComboBox<String> pos = n == 1 ? cmbPosicionJ1 : cmbPosicionJ2;
        pos.setSelectedIndex(0);
        (n == 1 ? txtNombreJ1 : txtNombreJ2).requestFocusInWindow();
    }

    // ------------------------------------------------------------------
    // Diálogos
    // ------------------------------------------------------------------

    public String pedirNombreJugador(int n, String porDefecto) {
        Object valor = JOptionPane.showInputDialog(this, "Nombre del Jugador " + n + ":",
                "Nuevo duelo", JOptionPane.QUESTION_MESSAGE, null, null, porDefecto);
        return valor == null ? null : valor.toString();
    }

    public void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Acción no válida", JOptionPane.WARNING_MESSAGE);
    }

    public void mostrarInformacion(String titulo, String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, titulo, JOptionPane.INFORMATION_MESSAGE);
    }

    public boolean confirmar(String mensaje) {
        return JOptionPane.showConfirmDialog(this, mensaje, "Confirmar",
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
    }

    public void mostrarReporte(String texto) {
        JTextArea area = new JTextArea(texto, 24, 70);
        area.setEditable(false);
        area.setFont(new java.awt.Font("Consolas", java.awt.Font.PLAIN, 13));
        area.setCaretPosition(0);
        JOptionPane.showMessageDialog(this, new JScrollPane(area), "Reportes del duelo",
                JOptionPane.PLAIN_MESSAGE);
    }

    // ------------------------------------------------------------------
    // Configuración visual auxiliar
    // ------------------------------------------------------------------

    private JTable tablaDe(int n) {
        return n == 1 ? tablaCampoJ1 : tablaCampoJ2;
    }

    private static void configurarTabla(JTable tabla) {
        tabla.setModel(new DefaultTableModel(COLUMNAS, 0) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        });
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JTableHeader encabezado = tabla.getTableHeader();
        encabezado.setReorderingAllowed(false);
        encabezado.setBackground(new Color(44, 39, 66));
        encabezado.setForeground(new Color(255, 213, 79));
        encabezado.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 12));
        DefaultTableCellRenderer centrado = new DefaultTableCellRenderer();
        centrado.setHorizontalAlignment(JLabel.CENTER);
        int[] anchos = {24, 170, 56, 46, 46, 64, 66};
        for (int i = 0; i < anchos.length; i++) {
            tabla.getColumnModel().getColumn(i).setPreferredWidth(anchos[i]);
            if (i != 1) {
                tabla.getColumnModel().getColumn(i).setCellRenderer(centrado);
            }
        }
    }

    private static void instalarFiltroNumerico(JTextField... campos) {
        for (JTextField campo : campos) {
            ((AbstractDocument) campo.getDocument()).setDocumentFilter(new FiltroNumerico(4));
        }
    }

    private static void usarCursorDeMano(JButton... botones) {
        for (JButton b : botones) {
            b.setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.HAND_CURSOR));
        }
    }

    private static Color colorVida(int lp) {
        double porcentaje = lp / (double) Jugador.LP_INICIALES;
        if (porcentaje > 0.5) {
            return new Color(102, 187, 106);
        }
        if (porcentaje > 0.25) {
            return new Color(255, 202, 40);
        }
        return new Color(239, 83, 80);
    }

    private static String escaparHtml(String texto) {
        return texto.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panelFondo = new javax.swing.JPanel();
        panelEncabezado = new javax.swing.JPanel();
        lblTitulo = new javax.swing.JLabel();
        lblTurno = new javax.swing.JLabel();
        panelBotonesSuperiores = new javax.swing.JPanel();
        btnReportes = new javax.swing.JButton();
        btnNuevoDuelo = new javax.swing.JButton();
        panelCentro = new javax.swing.JPanel();
        panelJugador1 = new javax.swing.JPanel();
        panelInfoJ1 = new javax.swing.JPanel();
        lblNombreJ1 = new javax.swing.JLabel();
        lblLpJ1 = new javax.swing.JLabel();
        barraVidaJ1 = new javax.swing.JProgressBar();
        scrollCampoJ1 = new javax.swing.JScrollPane();
        tablaCampoJ1 = new javax.swing.JTable();
        panelSurJ1 = new javax.swing.JPanel();
        panelFormJ1 = new javax.swing.JPanel();
        lblNombreMonstruoJ1 = new javax.swing.JLabel();
        txtNombreJ1 = new javax.swing.JTextField();
        lblAtaqueJ1 = new javax.swing.JLabel();
        txtAtaqueJ1 = new javax.swing.JTextField();
        lblDefensaJ1 = new javax.swing.JLabel();
        txtDefensaJ1 = new javax.swing.JTextField();
        lblPosicionJ1 = new javax.swing.JLabel();
        cmbPosicionJ1 = new javax.swing.JComboBox<>();
        lblTipoJ1 = new javax.swing.JLabel();
        cmbTipoJ1 = new javax.swing.JComboBox<>();
        lblEfectoJ1 = new javax.swing.JLabel();
        txtEfectoJ1 = new javax.swing.JTextField();
        panelBotonesJ1 = new javax.swing.JPanel();
        btnInvocarJ1 = new javax.swing.JButton();
        btnCambiarPosicionJ1 = new javax.swing.JButton();
        panelBatalla = new javax.swing.JPanel();
        panelAcciones = new javax.swing.JPanel();
        panelBotonesBatalla = new javax.swing.JPanel();
        lblInstruccion = new javax.swing.JLabel();
        btnAtacar = new javax.swing.JButton();
        btnAtaqueDirecto = new javax.swing.JButton();
        btnTerminarTurno = new javax.swing.JButton();
        lblResultado = new javax.swing.JLabel();
        scrollHistorial = new javax.swing.JScrollPane();
        txtHistorial = new javax.swing.JTextArea();
        panelJugador2 = new javax.swing.JPanel();
        panelInfoJ2 = new javax.swing.JPanel();
        lblNombreJ2 = new javax.swing.JLabel();
        lblLpJ2 = new javax.swing.JLabel();
        barraVidaJ2 = new javax.swing.JProgressBar();
        scrollCampoJ2 = new javax.swing.JScrollPane();
        tablaCampoJ2 = new javax.swing.JTable();
        panelSurJ2 = new javax.swing.JPanel();
        panelFormJ2 = new javax.swing.JPanel();
        lblNombreMonstruoJ2 = new javax.swing.JLabel();
        txtNombreJ2 = new javax.swing.JTextField();
        lblAtaqueJ2 = new javax.swing.JLabel();
        txtAtaqueJ2 = new javax.swing.JTextField();
        lblDefensaJ2 = new javax.swing.JLabel();
        txtDefensaJ2 = new javax.swing.JTextField();
        lblPosicionJ2 = new javax.swing.JLabel();
        cmbPosicionJ2 = new javax.swing.JComboBox<>();
        lblTipoJ2 = new javax.swing.JLabel();
        cmbTipoJ2 = new javax.swing.JComboBox<>();
        lblEfectoJ2 = new javax.swing.JLabel();
        txtEfectoJ2 = new javax.swing.JTextField();
        panelBotonesJ2 = new javax.swing.JPanel();
        btnInvocarJ2 = new javax.swing.JButton();
        btnCambiarPosicionJ2 = new javax.swing.JButton();
        lblEstado = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Yu-Gi-Oh! - Simulador de Fase de Batalla");
        setMinimumSize(new java.awt.Dimension(1320, 800));

        panelFondo.setBackground(new java.awt.Color(30, 26, 46));
        panelFondo.setBackground(new java.awt.Color(18, 16, 28));
        panelFondo.setLayout(new java.awt.BorderLayout(0, 12));

        panelEncabezado.setBackground(new java.awt.Color(30, 26, 46));
        panelEncabezado.setBackground(new java.awt.Color(12, 10, 20));
        panelEncabezado.setBorder(javax.swing.BorderFactory.createEmptyBorder(12, 18, 12, 18));
        panelEncabezado.setLayout(new java.awt.BorderLayout(16, 0));

        lblTitulo.setText("FASE DE BATALLA");
        lblTitulo.setFont(new java.awt.Font("Segoe UI", 1, 26)); // NOI18N
        lblTitulo.setForeground(new java.awt.Color(255, 213, 79));
        lblTitulo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/vista/iconos/titulo.png"))); // NOI18N
        lblTitulo.setIconTextGap(12);
        panelEncabezado.add(lblTitulo, java.awt.BorderLayout.LINE_START);

        lblTurno.setText("Turno 1");
        lblTurno.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        lblTurno.setForeground(new java.awt.Color(236, 239, 241));
        lblTurno.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        panelEncabezado.add(lblTurno, java.awt.BorderLayout.CENTER);

        panelBotonesSuperiores.setBackground(new java.awt.Color(30, 26, 46));
        panelBotonesSuperiores.setBackground(new java.awt.Color(12, 10, 20));
        panelBotonesSuperiores.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 10, 0));

        btnReportes.setBackground(new java.awt.Color(58, 52, 86));
        btnReportes.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        btnReportes.setForeground(new java.awt.Color(236, 239, 241));
        btnReportes.setIcon(new javax.swing.ImageIcon(getClass().getResource("/vista/iconos/reporte.png"))); // NOI18N
        btnReportes.setText("Reportes");
        btnReportes.setToolTipText("Muestra los reportes del duelo");
        btnReportes.setActionCommand("REPORTES"); // NOI18N
        btnReportes.setBorder(javax.swing.BorderFactory.createEmptyBorder(8, 14, 8, 14));
        btnReportes.setFocusPainted(false);
        btnReportes.setIconTextGap(10);
        panelBotonesSuperiores.add(btnReportes);

        btnNuevoDuelo.setBackground(new java.awt.Color(94, 53, 177));
        btnNuevoDuelo.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        btnNuevoDuelo.setForeground(new java.awt.Color(255, 255, 255));
        btnNuevoDuelo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/vista/iconos/nuevo.png"))); // NOI18N
        btnNuevoDuelo.setText("Nuevo duelo");
        btnNuevoDuelo.setToolTipText("Reinicia el duelo con nuevos jugadores");
        btnNuevoDuelo.setActionCommand("NUEVO"); // NOI18N
        btnNuevoDuelo.setBorder(javax.swing.BorderFactory.createEmptyBorder(8, 14, 8, 14));
        btnNuevoDuelo.setFocusPainted(false);
        btnNuevoDuelo.setIconTextGap(10);
        panelBotonesSuperiores.add(btnNuevoDuelo);

        panelEncabezado.add(panelBotonesSuperiores, java.awt.BorderLayout.LINE_END);

        panelFondo.add(panelEncabezado, java.awt.BorderLayout.PAGE_START);

        panelCentro.setBackground(new java.awt.Color(30, 26, 46));
        panelCentro.setBackground(new java.awt.Color(18, 16, 28));
        panelCentro.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 14, 0, 14));
        panelCentro.setLayout(new java.awt.BorderLayout(14, 0));

        panelJugador1.setBackground(new java.awt.Color(30, 26, 46));
        panelJugador1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(41, 121, 255), 2));
        panelJugador1.setPreferredSize(new java.awt.Dimension(440, 640));
        panelJugador1.setLayout(new java.awt.BorderLayout(0, 8));

        panelInfoJ1.setBackground(new java.awt.Color(30, 26, 46));
        panelInfoJ1.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 14, 4, 14));
        panelInfoJ1.setLayout(new java.awt.GridLayout(3, 1, 0, 4));

        lblNombreJ1.setText("Jugador 1");
        lblNombreJ1.setFont(new java.awt.Font("Segoe UI", 1, 20)); // NOI18N
        lblNombreJ1.setForeground(new java.awt.Color(236, 239, 241));
        panelInfoJ1.add(lblNombreJ1);

        lblLpJ1.setText("LP 8000");
        lblLpJ1.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        lblLpJ1.setForeground(new java.awt.Color(255, 213, 79));
        lblLpJ1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/vista/iconos/corazon.png"))); // NOI18N
        lblLpJ1.setIconTextGap(8);
        panelInfoJ1.add(lblLpJ1);

        barraVidaJ1.setBackground(new java.awt.Color(24, 21, 38));
        barraVidaJ1.setForeground(new java.awt.Color(102, 187, 106));
        barraVidaJ1.setMaximum(8000);
        barraVidaJ1.setValue(8000);
        barraVidaJ1.setToolTipText("Puntos de Vida restantes");
        barraVidaJ1.setBorderPainted(false);
        barraVidaJ1.setPreferredSize(new java.awt.Dimension(146, 14));
        panelInfoJ1.add(barraVidaJ1);

        panelJugador1.add(panelInfoJ1, java.awt.BorderLayout.PAGE_START);

        scrollCampoJ1.setBackground(new java.awt.Color(30, 26, 46));
        scrollCampoJ1.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 14, 0, 14));

        tablaCampoJ1.setBackground(new java.awt.Color(24, 21, 38));
        tablaCampoJ1.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        tablaCampoJ1.setForeground(new java.awt.Color(236, 239, 241));
        tablaCampoJ1.setFillsViewportHeight(true);
        tablaCampoJ1.setGridColor(new java.awt.Color(60, 54, 90));
        tablaCampoJ1.setRowHeight(28);
        tablaCampoJ1.setSelectionBackground(new java.awt.Color(124, 77, 255));
        tablaCampoJ1.setSelectionForeground(new java.awt.Color(255, 255, 255));
        tablaCampoJ1.setShowVerticalLines(false);
        scrollCampoJ1.setViewportView(tablaCampoJ1);

        panelJugador1.add(scrollCampoJ1, java.awt.BorderLayout.CENTER);

        panelSurJ1.setBackground(new java.awt.Color(30, 26, 46));
        panelSurJ1.setBorder(javax.swing.BorderFactory.createEmptyBorder(6, 14, 14, 14));
        panelSurJ1.setLayout(new java.awt.BorderLayout(0, 10));

        panelFormJ1.setBackground(new java.awt.Color(30, 26, 46));
        panelFormJ1.setLayout(new java.awt.GridLayout(6, 2, 10, 6));

        lblNombreMonstruoJ1.setText("Nombre del monstruo");
        lblNombreMonstruoJ1.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        lblNombreMonstruoJ1.setForeground(new java.awt.Color(176, 170, 200));
        panelFormJ1.add(lblNombreMonstruoJ1);

        txtNombreJ1.setBackground(new java.awt.Color(44, 39, 66));
        txtNombreJ1.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        txtNombreJ1.setForeground(new java.awt.Color(236, 239, 241));
        txtNombreJ1.setToolTipText("Nombre de la carta");
        txtNombreJ1.setBorder(javax.swing.BorderFactory.createEmptyBorder(4, 8, 4, 8));
        txtNombreJ1.setCaretColor(new java.awt.Color(236, 239, 241));
        panelFormJ1.add(txtNombreJ1);

        lblAtaqueJ1.setText("ATQ");
        lblAtaqueJ1.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        lblAtaqueJ1.setForeground(new java.awt.Color(176, 170, 200));
        panelFormJ1.add(lblAtaqueJ1);

        txtAtaqueJ1.setBackground(new java.awt.Color(44, 39, 66));
        txtAtaqueJ1.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        txtAtaqueJ1.setForeground(new java.awt.Color(236, 239, 241));
        txtAtaqueJ1.setToolTipText("Puntos de ataque (0 - 9999), solo números");
        txtAtaqueJ1.setBorder(javax.swing.BorderFactory.createEmptyBorder(4, 8, 4, 8));
        txtAtaqueJ1.setCaretColor(new java.awt.Color(236, 239, 241));
        panelFormJ1.add(txtAtaqueJ1);

        lblDefensaJ1.setText("DEF");
        lblDefensaJ1.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        lblDefensaJ1.setForeground(new java.awt.Color(176, 170, 200));
        panelFormJ1.add(lblDefensaJ1);

        txtDefensaJ1.setBackground(new java.awt.Color(44, 39, 66));
        txtDefensaJ1.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        txtDefensaJ1.setForeground(new java.awt.Color(236, 239, 241));
        txtDefensaJ1.setToolTipText("Puntos de defensa (0 - 9999), solo números");
        txtDefensaJ1.setBorder(javax.swing.BorderFactory.createEmptyBorder(4, 8, 4, 8));
        txtDefensaJ1.setCaretColor(new java.awt.Color(236, 239, 241));
        panelFormJ1.add(txtDefensaJ1);

        lblPosicionJ1.setText("Posición");
        lblPosicionJ1.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        lblPosicionJ1.setForeground(new java.awt.Color(176, 170, 200));
        panelFormJ1.add(lblPosicionJ1);

        cmbPosicionJ1.setBackground(new java.awt.Color(44, 39, 66));
        cmbPosicionJ1.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        cmbPosicionJ1.setForeground(new java.awt.Color(236, 239, 241));
        cmbPosicionJ1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Ataque", "Defensa" }));
        cmbPosicionJ1.setToolTipText("Posición en la que se invoca");
        panelFormJ1.add(cmbPosicionJ1);

        lblTipoJ1.setText("Tipo");
        lblTipoJ1.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        lblTipoJ1.setForeground(new java.awt.Color(176, 170, 200));
        panelFormJ1.add(lblTipoJ1);

        cmbTipoJ1.setBackground(new java.awt.Color(44, 39, 66));
        cmbTipoJ1.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        cmbTipoJ1.setForeground(new java.awt.Color(236, 239, 241));
        cmbTipoJ1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Normal", "Efecto" }));
        cmbTipoJ1.setToolTipText("Monstruo Normal o de Efecto");
        cmbTipoJ1.setActionCommand("TIPO_1"); // NOI18N
        panelFormJ1.add(cmbTipoJ1);

        lblEfectoJ1.setText("Texto del efecto");
        lblEfectoJ1.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        lblEfectoJ1.setForeground(new java.awt.Color(176, 170, 200));
        panelFormJ1.add(lblEfectoJ1);

        txtEfectoJ1.setBackground(new java.awt.Color(44, 39, 66));
        txtEfectoJ1.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        txtEfectoJ1.setForeground(new java.awt.Color(236, 239, 241));
        txtEfectoJ1.setToolTipText("Descripción del efecto (solo monstruos de Efecto)");
        txtEfectoJ1.setBorder(javax.swing.BorderFactory.createEmptyBorder(4, 8, 4, 8));
        txtEfectoJ1.setCaretColor(new java.awt.Color(236, 239, 241));
        panelFormJ1.add(txtEfectoJ1);

        panelSurJ1.add(panelFormJ1, java.awt.BorderLayout.CENTER);

        panelBotonesJ1.setBackground(new java.awt.Color(30, 26, 46));
        panelBotonesJ1.setLayout(new java.awt.GridLayout(1, 2, 10, 0));

        btnInvocarJ1.setBackground(new java.awt.Color(41, 121, 255));
        btnInvocarJ1.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        btnInvocarJ1.setForeground(new java.awt.Color(255, 255, 255));
        btnInvocarJ1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/vista/iconos/invocar.png"))); // NOI18N
        btnInvocarJ1.setText("Invocar");
        btnInvocarJ1.setToolTipText("Coloca el monstruo en el campo");
        btnInvocarJ1.setActionCommand("INVOCAR_1"); // NOI18N
        btnInvocarJ1.setBorder(javax.swing.BorderFactory.createEmptyBorder(8, 14, 8, 14));
        btnInvocarJ1.setFocusPainted(false);
        btnInvocarJ1.setIconTextGap(10);
        panelBotonesJ1.add(btnInvocarJ1);

        btnCambiarPosicionJ1.setBackground(new java.awt.Color(58, 52, 86));
        btnCambiarPosicionJ1.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        btnCambiarPosicionJ1.setForeground(new java.awt.Color(236, 239, 241));
        btnCambiarPosicionJ1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/vista/iconos/cambiar.png"))); // NOI18N
        btnCambiarPosicionJ1.setText("Cambiar posición");
        btnCambiarPosicionJ1.setToolTipText("Cambia el monstruo seleccionado entre Ataque y Defensa");
        btnCambiarPosicionJ1.setActionCommand("POSICION_1"); // NOI18N
        btnCambiarPosicionJ1.setBorder(javax.swing.BorderFactory.createEmptyBorder(8, 14, 8, 14));
        btnCambiarPosicionJ1.setFocusPainted(false);
        btnCambiarPosicionJ1.setIconTextGap(10);
        panelBotonesJ1.add(btnCambiarPosicionJ1);

        panelSurJ1.add(panelBotonesJ1, java.awt.BorderLayout.PAGE_END);

        panelJugador1.add(panelSurJ1, java.awt.BorderLayout.PAGE_END);

        panelCentro.add(panelJugador1, java.awt.BorderLayout.LINE_START);

        panelBatalla.setBackground(new java.awt.Color(30, 26, 46));
        panelBatalla.setBackground(new java.awt.Color(18, 16, 28));
        panelBatalla.setLayout(new java.awt.BorderLayout(0, 12));

        panelAcciones.setBackground(new java.awt.Color(30, 26, 46));
        panelAcciones.setBackground(new java.awt.Color(18, 16, 28));
        panelAcciones.setLayout(new java.awt.BorderLayout(0, 10));

        panelBotonesBatalla.setBackground(new java.awt.Color(30, 26, 46));
        panelBotonesBatalla.setBackground(new java.awt.Color(18, 16, 28));
        panelBotonesBatalla.setLayout(new java.awt.GridLayout(4, 1, 0, 10));

        lblInstruccion.setText("Elige tu monstruo y un objetivo rival");
        lblInstruccion.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        lblInstruccion.setForeground(new java.awt.Color(176, 170, 200));
        lblInstruccion.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        panelBotonesBatalla.add(lblInstruccion);

        btnAtacar.setBackground(new java.awt.Color(198, 40, 40));
        btnAtacar.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        btnAtacar.setForeground(new java.awt.Color(255, 255, 255));
        btnAtacar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/vista/iconos/espada.png"))); // NOI18N
        btnAtacar.setText("ATACAR");
        btnAtacar.setToolTipText("Ataca al monstruo rival seleccionado");
        btnAtacar.setActionCommand("ATACAR"); // NOI18N
        btnAtacar.setBorder(javax.swing.BorderFactory.createEmptyBorder(8, 14, 8, 14));
        btnAtacar.setFocusPainted(false);
        btnAtacar.setIconTextGap(10);
        panelBotonesBatalla.add(btnAtacar);

        btnAtaqueDirecto.setBackground(new java.awt.Color(255, 179, 0));
        btnAtaqueDirecto.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        btnAtaqueDirecto.setForeground(new java.awt.Color(33, 22, 0));
        btnAtaqueDirecto.setIcon(new javax.swing.ImageIcon(getClass().getResource("/vista/iconos/rayo.png"))); // NOI18N
        btnAtaqueDirecto.setText("ATAQUE DIRECTO");
        btnAtaqueDirecto.setToolTipText("Solo si el rival no tiene monstruos en el campo");
        btnAtaqueDirecto.setActionCommand("DIRECTO"); // NOI18N
        btnAtaqueDirecto.setBorder(javax.swing.BorderFactory.createEmptyBorder(8, 14, 8, 14));
        btnAtaqueDirecto.setFocusPainted(false);
        btnAtaqueDirecto.setIconTextGap(10);
        panelBotonesBatalla.add(btnAtaqueDirecto);

        btnTerminarTurno.setBackground(new java.awt.Color(94, 53, 177));
        btnTerminarTurno.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        btnTerminarTurno.setForeground(new java.awt.Color(255, 255, 255));
        btnTerminarTurno.setIcon(new javax.swing.ImageIcon(getClass().getResource("/vista/iconos/turno.png"))); // NOI18N
        btnTerminarTurno.setText("Terminar turno");
        btnTerminarTurno.setToolTipText("Pasa el turno al otro jugador");
        btnTerminarTurno.setActionCommand("TERMINAR"); // NOI18N
        btnTerminarTurno.setBorder(javax.swing.BorderFactory.createEmptyBorder(8, 14, 8, 14));
        btnTerminarTurno.setFocusPainted(false);
        btnTerminarTurno.setIconTextGap(10);
        panelBotonesBatalla.add(btnTerminarTurno);

        panelAcciones.add(panelBotonesBatalla, java.awt.BorderLayout.CENTER);

        lblResultado.setText("<html><div style=\"text-align:center\">Que comience el duelo.</div></html>");
        lblResultado.setBackground(new java.awt.Color(30, 26, 46));
        lblResultado.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblResultado.setForeground(new java.awt.Color(255, 213, 79));
        lblResultado.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblResultado.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(60, 54, 90), 1));
        lblResultado.setOpaque(true);
        lblResultado.setPreferredSize(new java.awt.Dimension(300, 76));
        panelAcciones.add(lblResultado, java.awt.BorderLayout.PAGE_END);

        panelBatalla.add(panelAcciones, java.awt.BorderLayout.PAGE_START);

        scrollHistorial.setBackground(new java.awt.Color(18, 16, 28));
        scrollHistorial.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Historial de combate", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 13), new java.awt.Color(255, 213, 79))); // NOI18N

        txtHistorial.setEditable(false);
        txtHistorial.setBackground(new java.awt.Color(24, 21, 38));
        txtHistorial.setColumns(20);
        txtHistorial.setFont(new java.awt.Font("Consolas", 0, 12)); // NOI18N
        txtHistorial.setForeground(new java.awt.Color(236, 239, 241));
        txtHistorial.setLineWrap(true);
        txtHistorial.setRows(5);
        txtHistorial.setWrapStyleWord(true);
        txtHistorial.setBorder(javax.swing.BorderFactory.createEmptyBorder(6, 8, 6, 8));
        scrollHistorial.setViewportView(txtHistorial);

        panelBatalla.add(scrollHistorial, java.awt.BorderLayout.CENTER);

        panelCentro.add(panelBatalla, java.awt.BorderLayout.CENTER);

        panelJugador2.setBackground(new java.awt.Color(30, 26, 46));
        panelJugador2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(229, 57, 53), 2));
        panelJugador2.setPreferredSize(new java.awt.Dimension(440, 640));
        panelJugador2.setLayout(new java.awt.BorderLayout(0, 8));

        panelInfoJ2.setBackground(new java.awt.Color(30, 26, 46));
        panelInfoJ2.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 14, 4, 14));
        panelInfoJ2.setLayout(new java.awt.GridLayout(3, 1, 0, 4));

        lblNombreJ2.setText("Jugador 2");
        lblNombreJ2.setFont(new java.awt.Font("Segoe UI", 1, 20)); // NOI18N
        lblNombreJ2.setForeground(new java.awt.Color(236, 239, 241));
        panelInfoJ2.add(lblNombreJ2);

        lblLpJ2.setText("LP 8000");
        lblLpJ2.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        lblLpJ2.setForeground(new java.awt.Color(255, 213, 79));
        lblLpJ2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/vista/iconos/corazon.png"))); // NOI18N
        lblLpJ2.setIconTextGap(8);
        panelInfoJ2.add(lblLpJ2);

        barraVidaJ2.setBackground(new java.awt.Color(24, 21, 38));
        barraVidaJ2.setForeground(new java.awt.Color(102, 187, 106));
        barraVidaJ2.setMaximum(8000);
        barraVidaJ2.setValue(8000);
        barraVidaJ2.setToolTipText("Puntos de Vida restantes");
        barraVidaJ2.setBorderPainted(false);
        barraVidaJ2.setPreferredSize(new java.awt.Dimension(146, 14));
        panelInfoJ2.add(barraVidaJ2);

        panelJugador2.add(panelInfoJ2, java.awt.BorderLayout.PAGE_START);

        scrollCampoJ2.setBackground(new java.awt.Color(30, 26, 46));
        scrollCampoJ2.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 14, 0, 14));

        tablaCampoJ2.setBackground(new java.awt.Color(24, 21, 38));
        tablaCampoJ2.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        tablaCampoJ2.setForeground(new java.awt.Color(236, 239, 241));
        tablaCampoJ2.setFillsViewportHeight(true);
        tablaCampoJ2.setGridColor(new java.awt.Color(60, 54, 90));
        tablaCampoJ2.setRowHeight(28);
        tablaCampoJ2.setSelectionBackground(new java.awt.Color(124, 77, 255));
        tablaCampoJ2.setSelectionForeground(new java.awt.Color(255, 255, 255));
        tablaCampoJ2.setShowVerticalLines(false);
        scrollCampoJ2.setViewportView(tablaCampoJ2);

        panelJugador2.add(scrollCampoJ2, java.awt.BorderLayout.CENTER);

        panelSurJ2.setBackground(new java.awt.Color(30, 26, 46));
        panelSurJ2.setBorder(javax.swing.BorderFactory.createEmptyBorder(6, 14, 14, 14));
        panelSurJ2.setLayout(new java.awt.BorderLayout(0, 10));

        panelFormJ2.setBackground(new java.awt.Color(30, 26, 46));
        panelFormJ2.setLayout(new java.awt.GridLayout(6, 2, 10, 6));

        lblNombreMonstruoJ2.setText("Nombre del monstruo");
        lblNombreMonstruoJ2.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        lblNombreMonstruoJ2.setForeground(new java.awt.Color(176, 170, 200));
        panelFormJ2.add(lblNombreMonstruoJ2);

        txtNombreJ2.setBackground(new java.awt.Color(44, 39, 66));
        txtNombreJ2.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        txtNombreJ2.setForeground(new java.awt.Color(236, 239, 241));
        txtNombreJ2.setToolTipText("Nombre de la carta");
        txtNombreJ2.setBorder(javax.swing.BorderFactory.createEmptyBorder(4, 8, 4, 8));
        txtNombreJ2.setCaretColor(new java.awt.Color(236, 239, 241));
        panelFormJ2.add(txtNombreJ2);

        lblAtaqueJ2.setText("ATQ");
        lblAtaqueJ2.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        lblAtaqueJ2.setForeground(new java.awt.Color(176, 170, 200));
        panelFormJ2.add(lblAtaqueJ2);

        txtAtaqueJ2.setBackground(new java.awt.Color(44, 39, 66));
        txtAtaqueJ2.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        txtAtaqueJ2.setForeground(new java.awt.Color(236, 239, 241));
        txtAtaqueJ2.setToolTipText("Puntos de ataque (0 - 9999), solo números");
        txtAtaqueJ2.setBorder(javax.swing.BorderFactory.createEmptyBorder(4, 8, 4, 8));
        txtAtaqueJ2.setCaretColor(new java.awt.Color(236, 239, 241));
        panelFormJ2.add(txtAtaqueJ2);

        lblDefensaJ2.setText("DEF");
        lblDefensaJ2.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        lblDefensaJ2.setForeground(new java.awt.Color(176, 170, 200));
        panelFormJ2.add(lblDefensaJ2);

        txtDefensaJ2.setBackground(new java.awt.Color(44, 39, 66));
        txtDefensaJ2.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        txtDefensaJ2.setForeground(new java.awt.Color(236, 239, 241));
        txtDefensaJ2.setToolTipText("Puntos de defensa (0 - 9999), solo números");
        txtDefensaJ2.setBorder(javax.swing.BorderFactory.createEmptyBorder(4, 8, 4, 8));
        txtDefensaJ2.setCaretColor(new java.awt.Color(236, 239, 241));
        panelFormJ2.add(txtDefensaJ2);

        lblPosicionJ2.setText("Posición");
        lblPosicionJ2.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        lblPosicionJ2.setForeground(new java.awt.Color(176, 170, 200));
        panelFormJ2.add(lblPosicionJ2);

        cmbPosicionJ2.setBackground(new java.awt.Color(44, 39, 66));
        cmbPosicionJ2.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        cmbPosicionJ2.setForeground(new java.awt.Color(236, 239, 241));
        cmbPosicionJ2.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Ataque", "Defensa" }));
        cmbPosicionJ2.setToolTipText("Posición en la que se invoca");
        panelFormJ2.add(cmbPosicionJ2);

        lblTipoJ2.setText("Tipo");
        lblTipoJ2.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        lblTipoJ2.setForeground(new java.awt.Color(176, 170, 200));
        panelFormJ2.add(lblTipoJ2);

        cmbTipoJ2.setBackground(new java.awt.Color(44, 39, 66));
        cmbTipoJ2.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        cmbTipoJ2.setForeground(new java.awt.Color(236, 239, 241));
        cmbTipoJ2.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Normal", "Efecto" }));
        cmbTipoJ2.setToolTipText("Monstruo Normal o de Efecto");
        cmbTipoJ2.setActionCommand("TIPO_2"); // NOI18N
        panelFormJ2.add(cmbTipoJ2);

        lblEfectoJ2.setText("Texto del efecto");
        lblEfectoJ2.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        lblEfectoJ2.setForeground(new java.awt.Color(176, 170, 200));
        panelFormJ2.add(lblEfectoJ2);

        txtEfectoJ2.setBackground(new java.awt.Color(44, 39, 66));
        txtEfectoJ2.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        txtEfectoJ2.setForeground(new java.awt.Color(236, 239, 241));
        txtEfectoJ2.setToolTipText("Descripción del efecto (solo monstruos de Efecto)");
        txtEfectoJ2.setBorder(javax.swing.BorderFactory.createEmptyBorder(4, 8, 4, 8));
        txtEfectoJ2.setCaretColor(new java.awt.Color(236, 239, 241));
        panelFormJ2.add(txtEfectoJ2);

        panelSurJ2.add(panelFormJ2, java.awt.BorderLayout.CENTER);

        panelBotonesJ2.setBackground(new java.awt.Color(30, 26, 46));
        panelBotonesJ2.setLayout(new java.awt.GridLayout(1, 2, 10, 0));

        btnInvocarJ2.setBackground(new java.awt.Color(229, 57, 53));
        btnInvocarJ2.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        btnInvocarJ2.setForeground(new java.awt.Color(255, 255, 255));
        btnInvocarJ2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/vista/iconos/invocar.png"))); // NOI18N
        btnInvocarJ2.setText("Invocar");
        btnInvocarJ2.setToolTipText("Coloca el monstruo en el campo");
        btnInvocarJ2.setActionCommand("INVOCAR_2"); // NOI18N
        btnInvocarJ2.setBorder(javax.swing.BorderFactory.createEmptyBorder(8, 14, 8, 14));
        btnInvocarJ2.setFocusPainted(false);
        btnInvocarJ2.setIconTextGap(10);
        panelBotonesJ2.add(btnInvocarJ2);

        btnCambiarPosicionJ2.setBackground(new java.awt.Color(58, 52, 86));
        btnCambiarPosicionJ2.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        btnCambiarPosicionJ2.setForeground(new java.awt.Color(236, 239, 241));
        btnCambiarPosicionJ2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/vista/iconos/cambiar.png"))); // NOI18N
        btnCambiarPosicionJ2.setText("Cambiar posición");
        btnCambiarPosicionJ2.setToolTipText("Cambia el monstruo seleccionado entre Ataque y Defensa");
        btnCambiarPosicionJ2.setActionCommand("POSICION_2"); // NOI18N
        btnCambiarPosicionJ2.setBorder(javax.swing.BorderFactory.createEmptyBorder(8, 14, 8, 14));
        btnCambiarPosicionJ2.setFocusPainted(false);
        btnCambiarPosicionJ2.setIconTextGap(10);
        panelBotonesJ2.add(btnCambiarPosicionJ2);

        panelSurJ2.add(panelBotonesJ2, java.awt.BorderLayout.PAGE_END);

        panelJugador2.add(panelSurJ2, java.awt.BorderLayout.PAGE_END);

        panelCentro.add(panelJugador2, java.awt.BorderLayout.LINE_END);

        panelFondo.add(panelCentro, java.awt.BorderLayout.CENTER);

        lblEstado.setText("Listo");
        lblEstado.setBackground(new java.awt.Color(12, 10, 20));
        lblEstado.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        lblEstado.setForeground(new java.awt.Color(176, 170, 200));
        lblEstado.setBorder(javax.swing.BorderFactory.createEmptyBorder(6, 18, 6, 18));
        lblEstado.setOpaque(true);
        panelFondo.add(lblEstado, java.awt.BorderLayout.PAGE_END);

        getContentPane().add(panelFondo, java.awt.BorderLayout.CENTER);

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JProgressBar barraVidaJ1;
    private javax.swing.JProgressBar barraVidaJ2;
    private javax.swing.JButton btnAtacar;
    private javax.swing.JButton btnAtaqueDirecto;
    private javax.swing.JButton btnCambiarPosicionJ1;
    private javax.swing.JButton btnCambiarPosicionJ2;
    private javax.swing.JButton btnInvocarJ1;
    private javax.swing.JButton btnInvocarJ2;
    private javax.swing.JButton btnNuevoDuelo;
    private javax.swing.JButton btnReportes;
    private javax.swing.JButton btnTerminarTurno;
    private javax.swing.JComboBox<String> cmbPosicionJ1;
    private javax.swing.JComboBox<String> cmbPosicionJ2;
    private javax.swing.JComboBox<String> cmbTipoJ1;
    private javax.swing.JComboBox<String> cmbTipoJ2;
    private javax.swing.JLabel lblAtaqueJ1;
    private javax.swing.JLabel lblAtaqueJ2;
    private javax.swing.JLabel lblDefensaJ1;
    private javax.swing.JLabel lblDefensaJ2;
    private javax.swing.JLabel lblEfectoJ1;
    private javax.swing.JLabel lblEfectoJ2;
    private javax.swing.JLabel lblEstado;
    private javax.swing.JLabel lblInstruccion;
    private javax.swing.JLabel lblLpJ1;
    private javax.swing.JLabel lblLpJ2;
    private javax.swing.JLabel lblNombreJ1;
    private javax.swing.JLabel lblNombreJ2;
    private javax.swing.JLabel lblNombreMonstruoJ1;
    private javax.swing.JLabel lblNombreMonstruoJ2;
    private javax.swing.JLabel lblPosicionJ1;
    private javax.swing.JLabel lblPosicionJ2;
    private javax.swing.JLabel lblResultado;
    private javax.swing.JLabel lblTipoJ1;
    private javax.swing.JLabel lblTipoJ2;
    private javax.swing.JLabel lblTitulo;
    private javax.swing.JLabel lblTurno;
    private javax.swing.JPanel panelAcciones;
    private javax.swing.JPanel panelBatalla;
    private javax.swing.JPanel panelBotonesBatalla;
    private javax.swing.JPanel panelBotonesJ1;
    private javax.swing.JPanel panelBotonesJ2;
    private javax.swing.JPanel panelBotonesSuperiores;
    private javax.swing.JPanel panelCentro;
    private javax.swing.JPanel panelEncabezado;
    private javax.swing.JPanel panelFondo;
    private javax.swing.JPanel panelFormJ1;
    private javax.swing.JPanel panelFormJ2;
    private javax.swing.JPanel panelInfoJ1;
    private javax.swing.JPanel panelInfoJ2;
    private javax.swing.JPanel panelJugador1;
    private javax.swing.JPanel panelJugador2;
    private javax.swing.JPanel panelSurJ1;
    private javax.swing.JPanel panelSurJ2;
    private javax.swing.JScrollPane scrollCampoJ1;
    private javax.swing.JScrollPane scrollCampoJ2;
    private javax.swing.JScrollPane scrollHistorial;
    private javax.swing.JTable tablaCampoJ1;
    private javax.swing.JTable tablaCampoJ2;
    private javax.swing.JTextField txtAtaqueJ1;
    private javax.swing.JTextField txtAtaqueJ2;
    private javax.swing.JTextField txtDefensaJ1;
    private javax.swing.JTextField txtDefensaJ2;
    private javax.swing.JTextField txtEfectoJ1;
    private javax.swing.JTextField txtEfectoJ2;
    private javax.swing.JTextArea txtHistorial;
    private javax.swing.JTextField txtNombreJ1;
    private javax.swing.JTextField txtNombreJ2;
    // End of variables declaration//GEN-END:variables
}
