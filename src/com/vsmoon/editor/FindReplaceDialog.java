package com.vsmoon.editor;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class FindReplaceDialog extends JDialog {

    private JTextField txtFind;
    private JTextField txtReplace;

    public FindReplaceDialog(JFrame parent, JTextArea textArea, ThemeManager.ColorPalette palette) {
        super(parent, "Buscar y Reemplazar", false);
        setSize(400, 230);
        setLocationRelativeTo(parent);
        setResizable(false);
        setUndecorated(true);

        JPanel content = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(palette.barBg);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 18, 18));
                g2.setColor(palette.cardHover);
                g2.draw(new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, 18, 18));
                g2.dispose();
            }
        };
        content.setOpaque(false);
        content.setLayout(new BorderLayout(10, 10));
        content.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Título de la ventana flotante
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        JLabel lblTitle = new JLabel("🔍 Buscar y Reemplazar");
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 14));
        lblTitle.setForeground(palette.accent);

        JButton btnClose = new JButton("✕");
        btnClose.setFont(new Font("SansSerif", Font.BOLD, 12));
        btnClose.setForeground(palette.textMuted);
        btnClose.setContentAreaFilled(false);
        btnClose.setBorderPainted(false);
        btnClose.setFocusPainted(false);
        btnClose.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnClose.addActionListener(e -> dispose());

        header.add(lblTitle, BorderLayout.WEST);
        header.add(btnClose, BorderLayout.EAST);

        // Formulario
        JPanel form = new JPanel(new GridLayout(2, 2, 10, 12));
        form.setOpaque(false);

        JLabel lblFind = new JLabel("Buscar:");
        lblFind.setForeground(palette.textMain);
        lblFind.setFont(new Font("SansSerif", Font.BOLD, 12));

        JLabel lblReplace = new JLabel("Reemplazar:");
        lblReplace.setForeground(palette.textMain);
        lblReplace.setFont(new Font("SansSerif", Font.BOLD, 12));

        txtFind = createStyledTextField(palette);
        txtReplace = createStyledTextField(palette);

        form.add(lblFind);
        form.add(txtFind);
        form.add(lblReplace);
        form.add(txtReplace);

        // Botones de acción
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        btnPanel.setOpaque(false);

        JButton btnNext = createDialogButton("Buscar", palette);
        JButton btnReplace = createDialogButton("Reemplazar", palette);

        btnNext.addActionListener(e -> {
            String target = txtFind.getText();
            if (target.isEmpty()) return;
            String text = textArea.getText();
            int caretPos = textArea.getCaretPosition();
            int index = text.indexOf(target, caretPos);

            if (index != -1) {
                textArea.select(index, index + target.length());
                textArea.requestFocus();
            } else {
                index = text.indexOf(target, 0);
                if (index != -1) {
                    textArea.select(index, index + target.length());
                    textArea.requestFocus();
                }
            }
        });

        btnReplace.addActionListener(e -> {
            if (textArea.getSelectedText() != null && textArea.getSelectedText().equals(txtFind.getText())) {
                textArea.replaceSelection(txtReplace.getText());
            }
        });

        btnPanel.add(btnNext);
        btnPanel.add(btnReplace);

        content.add(header, BorderLayout.NORTH);
        content.add(form, BorderLayout.CENTER);
        content.add(btnPanel, BorderLayout.SOUTH);

        add(content);
        setBackground(new Color(0, 0, 0, 0));
    }

    private JTextField createStyledTextField(ThemeManager.ColorPalette palette) {
        JTextField field = new JTextField();
        field.setBackground(palette.editorBg);
        field.setForeground(palette.textMain);
        field.setCaretColor(palette.accent);
        field.setFont(new Font("SansSerif", Font.PLAIN, 12));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(palette.cardHover, 1, true),
            new EmptyBorder(4, 8, 4, 8)
        ));
        return field;
    }

    private JButton createDialogButton(String text, ThemeManager.ColorPalette palette) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("SansSerif", Font.BOLD, 12));
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setForeground(palette.textMain);

        btn.setUI(new javax.swing.plaf.basic.BasicButtonUI() {
            @Override
            public void paint(Graphics g, JComponent c) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color bg = btn.getModel().isRollover() ? palette.cardHover : palette.cardBg;
                g2.setColor(bg);
                g2.fill(new RoundRectangle2D.Float(0, 0, c.getWidth(), c.getHeight(), 10, 10));
                super.paint(g2, c);
                g2.dispose();
            }
        });

        return btn;
    }
}