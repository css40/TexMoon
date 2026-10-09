package com.vsmoon.editor;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class EditorFrame extends JFrame {

    private JTextArea textArea;
    private JLabel statusLabel;
    private JLabel brandLabel;
    private JLabel lblWordCount;
    private JLabel lblCharCount;
    private JLabel lblLineCount;
    private JLabel lblResumenTitle;
    private JLabel fileBadge;
    private File currentFile;

    private RoundedPanel headerBar;
    private RoundedPanel sidebar;
    private JPanel editorHolder;
    private JPanel headerOuter;
    private JPanel sidebarOuter;
    private JPanel statusPanel;
    private ThemeSelectorButton btnThemeSelector;
    private ModernScrollBarUI scrollBarUI;

    private final List<PillButton> registeredButtons = new ArrayList<>();
    private final List<JLabel> mutedLabels = new ArrayList<>();

    private ThemeManager.Theme currentTheme = ThemeManager.Theme.DARK;
    private ThemeManager.ColorPalette palette;
    private AutoSaveManager autoSaveManager;
    private int currentFontSize = 15;

    public EditorFrame() {
        setTitle("TxMoon - Edición estándar de texto v1.0");
        setSize(1000, 680);
        setMinimumSize(new Dimension(850, 520));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        palette = ThemeManager.getPalette(currentTheme);
        initUI();
        setupShortcuts();

        // Auto-guardado temporal cada 2 minutos
        autoSaveManager = new AutoSaveManager(textArea, 2);
        autoSaveManager.start();
    }

    private void initUI() {
        getContentPane().setBackground(palette.bgDark);
        setLayout(new BorderLayout(0, 0));

        // --- Área Principal de Texto ---
        textArea = new JTextArea();
        textArea.setFont(new Font("Consolas", Font.PLAIN, currentFontSize));
        textArea.setBackground(palette.editorBg);
        textArea.setForeground(palette.textMain);
        textArea.setCaretColor(palette.accent);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setMargin(new Insets(20, 25, 20, 25));

        textArea.addCaretListener(e -> updateStatusMetrics());

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        // Scroll Bar UI Personalizado
        scrollBarUI = new ModernScrollBarUI(palette.cardBg, palette.editorBg);
        scrollPane.getVerticalScrollBar().setUI(scrollBarUI);
        scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(10, 0));
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        // Evento de Ratón: Scroll fluido + Zoom con CTRL
        textArea.addMouseWheelListener(e -> {
            if (e.isControlDown()) {
                if (e.getWheelRotation() < 0 && currentFontSize < 40) {
                    currentFontSize += 1;
                } else if (e.getWheelRotation() > 0 && currentFontSize > 10) {
                    currentFontSize -= 1;
                }
                textArea.setFont(new Font("Consolas", Font.PLAIN, currentFontSize));
            } else {
                scrollPane.getVerticalScrollBar().dispatchEvent(e);
            }
        });

        editorHolder = new JPanel(new BorderLayout());
        editorHolder.setBackground(palette.bgDark);
        editorHolder.setBorder(new EmptyBorder(10, 15, 10, 0));
        editorHolder.add(scrollPane, BorderLayout.CENTER);

        add(editorHolder, BorderLayout.CENTER);

        // --- Layout Periférico ---
        add(createModernHeader(), BorderLayout.NORTH);
        add(createSidebarPanel(), BorderLayout.EAST);
        add(createStatusPanel(), BorderLayout.SOUTH);
    }

    private JPanel createModernHeader() {
        headerOuter = new JPanel(new BorderLayout());
        headerOuter.setBackground(palette.bgDark);
        headerOuter.setBorder(new EmptyBorder(12, 15, 5, 15));

        headerBar = new RoundedPanel(18, palette.barBg);
        headerBar.setLayout(new BorderLayout());
        headerBar.setBorder(new EmptyBorder(6, 12, 6, 12));

        JPanel leftGroup = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        leftGroup.setOpaque(false);

        fileBadge = new JLabel(" Sin título ");
        fileBadge.setFont(new Font("SansSerif", Font.BOLD, 12));
        fileBadge.setForeground(palette.textMuted);
        fileBadge.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(palette.cardBg, 1, true),
            new EmptyBorder(5, 10, 5, 10)
        ));

        PillButton btnNew = createPillButton("Nuevo", "/assets/nuevo.png", false);
        PillButton btnOpen = createPillButton("Abrir", "/assets/abrir.png", false);
        PillButton btnSave = createPillButton("Guardar", "/assets/guardar.png", true);
        PillButton btnFind = createPillButton("Buscar", "/assets/buscar.png", false);

        btnNew.addActionListener(e -> newDocument());
        btnOpen.addActionListener(e -> openFile());
        btnSave.addActionListener(e -> saveFile());
        btnFind.addActionListener(e -> openFindReplace());

        leftGroup.add(fileBadge);
        leftGroup.add(Box.createRigidArea(new Dimension(8, 0)));
        leftGroup.add(btnNew);
        leftGroup.add(btnOpen);
        leftGroup.add(btnSave);
        leftGroup.add(btnFind);

        JPanel rightGroup = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        rightGroup.setOpaque(false);

        btnThemeSelector = new ThemeSelectorButton();

        PillButton btnAbout = createPillButton("Acerca de", "/assets/info.png", false);
        btnAbout.addActionListener(e -> showAboutDialog());

        rightGroup.add(btnThemeSelector);
        rightGroup.add(btnAbout);

        headerBar.add(leftGroup, BorderLayout.WEST);
        headerBar.add(rightGroup, BorderLayout.EAST);

        headerOuter.add(headerBar, BorderLayout.CENTER);
        return headerOuter;
    }

    private JPanel createSidebarPanel() {
        sidebarOuter = new JPanel(new BorderLayout());
        sidebarOuter.setBackground(palette.bgDark);
        sidebarOuter.setBorder(new EmptyBorder(10, 10, 10, 15));

        sidebar = new RoundedPanel(18, palette.barBg);
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setPreferredSize(new Dimension(170, 0));
        sidebar.setBorder(new EmptyBorder(20, 15, 20, 15));

        lblResumenTitle = new JLabel("RESUMEN");
        lblResumenTitle.setFont(new Font("SansSerif", Font.BOLD, 11));
        lblResumenTitle.setForeground(palette.accent);
        lblResumenTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        sidebar.add(lblResumenTitle);
        sidebar.add(Box.createRigidArea(new Dimension(0, 18)));

        lblWordCount = createMetricLabel("0");
        lblCharCount = createMetricLabel("0");
        lblLineCount = createMetricLabel("1");

        sidebar.add(createMetricBox("Palabras", lblWordCount));
        sidebar.add(Box.createRigidArea(new Dimension(0, 15)));
        sidebar.add(createMetricBox("Caracteres", lblCharCount));
        sidebar.add(Box.createRigidArea(new Dimension(0, 15)));
        sidebar.add(createMetricBox("Líneas", lblLineCount));

        // Empuja la imagen al fondo del panel
        sidebar.add(Box.createVerticalGlue());

        // Imagen del caballero al fondo pegada a la izquierda
        JLabel lblKnight = new JLabel();
        ImageIcon knightIcon = ImageUtils.loadIcon("/assets/caballero.png", 90, 90);
        if (knightIcon != null) {
            lblKnight.setIcon(knightIcon);
        }
        lblKnight.setAlignmentX(Component.LEFT_ALIGNMENT);
        sidebar.add(lblKnight);

        sidebarOuter.add(sidebar, BorderLayout.CENTER);
        return sidebarOuter;
    }

    private JPanel createMetricBox(String title, JLabel valueLabel) {
        JPanel box = new JPanel();
        box.setLayout(new BoxLayout(box, BoxLayout.Y_AXIS));
        box.setOpaque(false);
        box.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblTag = new JLabel(title.toUpperCase());
        lblTag.setFont(new Font("SansSerif", Font.PLAIN, 10));
        lblTag.setForeground(palette.textMuted);
        lblTag.setAlignmentX(Component.LEFT_ALIGNMENT);
        mutedLabels.add(lblTag);

        valueLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        box.add(lblTag);
        box.add(Box.createRigidArea(new Dimension(0, 2)));
        box.add(valueLabel);

        return box;
    }

    private JLabel createMetricLabel(String defaultValue) {
        JLabel label = new JLabel(defaultValue);
        label.setFont(new Font("SansSerif", Font.BOLD, 16));
        label.setForeground(palette.textMain);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    private JPanel createStatusPanel() {
        statusPanel = new JPanel(new BorderLayout());
        statusPanel.setBackground(palette.bgDark);
        statusPanel.setBorder(new EmptyBorder(4, 20, 8, 20));

        statusLabel = new JLabel("Listo | Auto-guardado activo");
        statusLabel.setForeground(palette.textMuted);
        statusLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));

        brandLabel = new JLabel("TxMoon v1.0");
        brandLabel.setForeground(palette.accent);
        brandLabel.setFont(new Font("SansSerif", Font.BOLD, 11));

        statusPanel.add(statusLabel, BorderLayout.WEST);
        statusPanel.add(brandLabel, BorderLayout.EAST);

        return statusPanel;
    }

    private PillButton createPillButton(String text, String iconPath, boolean isPrimary) {
        PillButton btn = new PillButton(text, iconPath, isPrimary);
        registeredButtons.add(btn);
        return btn;
    }

    // --- Aplicación de Temas ---

    public void applyTheme(ThemeManager.Theme theme) {
        this.currentTheme = theme;
        this.palette = ThemeManager.getPalette(theme);

        getContentPane().setBackground(palette.bgDark);
        editorHolder.setBackground(palette.bgDark);
        headerOuter.setBackground(palette.bgDark);
        sidebarOuter.setBackground(palette.bgDark);
        statusPanel.setBackground(palette.bgDark);

        headerBar.setBackgroundColor(palette.barBg);
        sidebar.setBackgroundColor(palette.barBg);

        textArea.setBackground(palette.editorBg);
        textArea.setForeground(palette.textMain);
        textArea.setCaretColor(palette.accent);

        scrollBarUI.setColors(palette.cardBg, palette.editorBg);
        btnThemeSelector.updateThemeState(theme);

        lblWordCount.setForeground(palette.textMain);
        lblCharCount.setForeground(palette.textMain);
        lblLineCount.setForeground(palette.textMain);
        lblResumenTitle.setForeground(palette.accent);
        brandLabel.setForeground(palette.accent);

        fileBadge.setForeground(palette.textMuted);
        statusLabel.setForeground(palette.textMuted);

        for (JLabel lbl : mutedLabels) {
            lbl.setForeground(palette.textMuted);
        }

        for (PillButton btn : registeredButtons) {
            btn.updateThemeColors();
        }

        repaint();
    }

    // --- Componente: Botón Desplegable de Temas ---

    private class ThemeSelectorButton extends JButton {

        public ThemeSelectorButton() {
            setFont(new Font("SansSerif", Font.BOLD, 12));
            setFocusPainted(false);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));

            updateThemeState(currentTheme);

            addActionListener(e -> showThemeMenu());

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) { repaint(); }
                @Override
                public void mouseExited(MouseEvent e) { repaint(); }
            });
        }

        public void updateThemeState(ThemeManager.Theme theme) {
            String iconPath = "/assets/oscuro.png";
            String labelText = "Oscuro ▾";

            if (theme == ThemeManager.Theme.LIGHT) {
                iconPath = "/assets/claro.png";
                labelText = "Claro ▾";
            } else if (theme == ThemeManager.Theme.SEPIA) {
                iconPath = "/assets/sepia.png";
                labelText = "Sepia ▾";
            }

            setText(labelText);
            ImageIcon icon = ImageUtils.loadIcon(iconPath, 16, 16);
            if (icon != null) {
                setIcon(icon);
                setIconTextGap(8);
            }

            setForeground(palette.textMain);
            repaint();
        }

        private void showThemeMenu() {
            JPopupMenu popup = new JPopupMenu();
            popup.setBackground(palette.cardBg);
            popup.setBorder(BorderFactory.createLineBorder(palette.cardHover, 1));

            popup.add(createMenuItem("Oscuro", "/assets/oscuro.png", ThemeManager.Theme.DARK));
            popup.add(createMenuItem("Claro", "/assets/claro.png", ThemeManager.Theme.LIGHT));
            popup.add(createMenuItem("Sepia", "/assets/sepia.png", ThemeManager.Theme.SEPIA));

            popup.show(this, 0, getHeight() + 4);
        }

        private JMenuItem createMenuItem(String text, String iconPath, ThemeManager.Theme theme) {
            JMenuItem item = new JMenuItem(text);
            item.setFont(new Font("SansSerif", Font.BOLD, 12));
            item.setForeground(palette.textMain);
            item.setBackground(palette.cardBg);
            item.setBorder(new EmptyBorder(6, 12, 6, 12));

            ImageIcon icon = ImageUtils.loadIcon(iconPath, 16, 16);
            if (icon != null) {
                item.setIcon(icon);
            }

            item.addActionListener(e -> applyTheme(theme));
            return item;
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            Color currentBg = getModel().isRollover() ? palette.cardHover : palette.cardBg;
            g2.setColor(currentBg);
            g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 12, 12));

            super.paintComponent(g2);
            g2.dispose();
        }
    }

    // --- Componente: Botón Cápsula Estilizado ---

    private class PillButton extends JButton {
        private final boolean isPrimary;

        public PillButton(String text, String iconPath, boolean isPrimary) {
            super(text);
            this.isPrimary = isPrimary;

            ImageIcon icon = ImageUtils.loadIcon(iconPath, 16, 16);
            if (icon != null) {
                setIcon(icon);
                setIconTextGap(8);
            }

            setFont(new Font("SansSerif", Font.BOLD, 12));
            setFocusPainted(false);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));

            updateThemeColors();

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) { repaint(); }
                @Override
                public void mouseExited(MouseEvent e) { repaint(); }
            });
        }

        public void updateThemeColors() {
            if (isPrimary) {
                setForeground(Color.WHITE);
            } else {
                setForeground(palette.textMain);
            }
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            Color currentBg = isPrimary ? palette.accent : palette.cardBg;
            if (getModel().isRollover()) {
                currentBg = isPrimary ? palette.accentHover : palette.cardHover;
            }

            g2.setColor(currentBg);
            g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 12, 12));

            super.paintComponent(g2);
            g2.dispose();
        }
    }

    private class RoundedPanel extends JPanel {
        private final int cornerRadius;
        private Color backgroundColor;

        public RoundedPanel(int radius, Color bgColor) {
            this.cornerRadius = radius;
            this.backgroundColor = bgColor;
            setOpaque(false);
        }

        public void setBackgroundColor(Color color) {
            this.backgroundColor = color;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D graphics = (Graphics2D) g.create();
            graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            graphics.setColor(backgroundColor);
            graphics.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius));
            graphics.dispose();
        }
    }

    // --- Atajos de Teclado ---

    private void setupShortcuts() {
        JRootPane root = getRootPane();
        InputMap im = root.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = root.getActionMap();

        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK), "newDoc");
        am.put("newDoc", new AbstractAction() {
            public void actionPerformed(ActionEvent e) { newDocument(); }
        });

        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK), "openFile");
        am.put("openFile", new AbstractAction() {
            public void actionPerformed(ActionEvent e) { openFile(); }
        });

        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK), "saveFile");
        am.put("saveFile", new AbstractAction() {
            public void actionPerformed(ActionEvent e) { saveFile(); }
        });

        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_F, InputEvent.CTRL_DOWN_MASK), "findReplace");
        am.put("findReplace", new AbstractAction() {
            public void actionPerformed(ActionEvent e) { openFindReplace(); }
        });

        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0), "insertTimestamp");
        am.put("insertTimestamp", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                textArea.insert(TextUtils.getCurrentTimestamp(), textArea.getCaretPosition());
            }
        });
    }

    // --- Diálogos y Operaciones de Archivo ---

    private void openFindReplace() {
        FindReplaceDialog dialog = new FindReplaceDialog(this, textArea, palette);
        dialog.setVisible(true);
    }

    private void showAboutDialog() {
        CustomDialogs.showAboutDialog(this, palette);
    }

    private void newDocument() {
        if (!confirmDiscardChanges()) return;

        textArea.setText("");
        currentFile = null;
        fileBadge.setText(" Sin título ");
        setTitle("TxMoon - Edición estándar de texto v1.0 - Sin título");
        statusLabel.setText("Nuevo documento creado.");
        updateStatusMetrics();
    }

    private void openFile() {
        if (!confirmDiscardChanges()) return;

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("Archivos de Texto (*.txt)", "txt"));

        int option = fileChooser.showOpenDialog(this);
        if (option == JFileChooser.APPROVE_OPTION) {
            currentFile = fileChooser.getSelectedFile();
            try (BufferedReader reader = new BufferedReader(new FileReader(currentFile))) {
                textArea.read(reader, null);
                fileBadge.setText(" " + currentFile.getName() + " ");
                setTitle("TxMoon - Edición estándar de texto v1.0 - " + currentFile.getName());
                statusLabel.setText("Archivo abierto correctamente.");
                updateStatusMetrics();
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error al abrir el archivo.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void saveFile() {
        if (currentFile == null) {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileFilter(new FileNameExtensionFilter("Archivos de Texto (*.txt)", "txt"));
            int option = fileChooser.showSaveDialog(this);

            if (option == JFileChooser.APPROVE_OPTION) {
                currentFile = fileChooser.getSelectedFile();
                if (!currentFile.getName().toLowerCase().endsWith(".txt")) {
                    currentFile = new File(currentFile.getAbsolutePath() + ".txt");
                }
            } else {
                return;
            }
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(currentFile))) {
            textArea.write(writer);
            fileBadge.setText(" " + currentFile.getName() + " ");
            setTitle("TxMoon - Edición estándar de texto v1.0 - " + currentFile.getName());
            statusLabel.setText("Guardado correctamente.");
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error al guardar el archivo.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean confirmDiscardChanges() {
        if (textArea.getText().trim().isEmpty()) return true;

        return CustomDialogs.showConfirmWarning(
            this,
            "¿Deseas continuar? Los cambios no guardados se perderán.",
            palette
        );
    }

    private void updateStatusMetrics() {
        String text = textArea.getText();
        int words = TextUtils.countWords(text);
        int chars = TextUtils.countCharacters(text);
        int lines = textArea.getLineCount();

        lblWordCount.setText(String.valueOf(words));
        lblCharCount.setText(String.valueOf(chars));
        lblLineCount.setText(String.valueOf(lines));
    }
}