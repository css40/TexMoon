package com.vsmoon.editor;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class CustomDialogs {

    /**
     * Muestra la ventana modal "Acerca de TxMoon"
     */
    public static void showAboutDialog(JFrame parent, ThemeManager.ColorPalette palette) {
        JDialog dialog = new JDialog(parent, "Acerca de TxMoon", true);
        dialog.setSize(420, 350);
        dialog.setLocationRelativeTo(parent);
        dialog.setResizable(false);
        dialog.setUndecorated(true);

        // Panel principal redondeado
        JPanel content = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(palette.barBg);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 20, 20));
                g2.setColor(palette.cardHover);
                g2.draw(new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, 20, 20));
                g2.dispose();
            }
        };
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(new EmptyBorder(25, 25, 25, 25));

        // --- PANEL DE LOGOS BIEN ORDENADOS ---
        JPanel logoPanel = new JPanel();
        logoPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 0)); // 20px de separación horizontal
        logoPanel.setOpaque(false);
        logoPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        ImageIcon blogMoonIcon = ImageUtils.loadIcon("/assets/blogmoon.png", 40, 40);
        ImageIcon pixelIcon = ImageUtils.loadIcon("/assets/pixel.png", 40, 40);

        if (blogMoonIcon != null) {
            JLabel lblBlogMoon = new JLabel(blogMoonIcon);
            lblBlogMoon.setHorizontalAlignment(SwingConstants.CENTER);
            logoPanel.add(lblBlogMoon);
        }

        if (pixelIcon != null) {
            JLabel lblPixel = new JLabel(pixelIcon);
            lblPixel.setHorizontalAlignment(SwingConstants.CENTER);
            logoPanel.add(lblPixel);
        }

        // Título principal
        JLabel title = new JLabel("TxMoon");
        title.setFont(new Font("SansSerif", Font.BOLD, 22));
        title.setForeground(palette.accent);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Subtítulo de versión
        JLabel version = new JLabel("Edición Estándar de Texto v1.0");
        version.setFont(new Font("SansSerif", Font.BOLD, 12));
        version.setForeground(palette.textMain);
        version.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Crédito y Descripción
        JLabel desc = new JLabel("<html><center><br>"
                + "Un editor de texto plano enfocado en la escritura limpia, rápida y sin distracciones.<br><br>"
                + "Creador: <b>JHos Amador</b><br>"
                + "<font color='" + toHex(palette.textMuted) + "'>Desarrollado bajo el sello de <b>PixelBlade</b></font>"
                + "</center></html>");
        desc.setFont(new Font("SansSerif", Font.PLAIN, 12));
        desc.setForeground(palette.textMain);
        desc.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Botón Cerrar
        JButton btnClose = createStyledButton("Cerrar", palette);
        btnClose.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnClose.addActionListener(e -> dialog.dispose());

        // Ensamblado
        content.add(logoPanel);
        content.add(Box.createRigidArea(new Dimension(0, 12)));
        content.add(title);
        content.add(Box.createRigidArea(new Dimension(0, 2)));
        content.add(version);
        content.add(desc);
        content.add(Box.createRigidArea(new Dimension(0, 20)));
        content.add(btnClose);

        dialog.add(content);
        dialog.setBackground(new Color(0, 0, 0, 0));
        dialog.setVisible(true);
    }

    /**
     * Muestra un cuadro de confirmación modal (Warning) personalizado.
     */
    public static boolean showConfirmWarning(JFrame parent, String message, ThemeManager.ColorPalette palette) {
        JDialog dialog = new JDialog(parent, "Confirmación", true);
        dialog.setSize(380, 180);
        dialog.setLocationRelativeTo(parent);
        dialog.setResizable(false);
        dialog.setUndecorated(true);

        final boolean[] result = {false};

        JPanel content = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(palette.barBg);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 16, 16));
                g2.setColor(palette.accent);
                g2.draw(new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, 16, 16));
                g2.dispose();
            }
        };
        content.setOpaque(false);
        content.setLayout(new BorderLayout(15, 15));
        content.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel lblMsg = new JLabel("<html><center>" + message + "</center></html>", SwingConstants.CENTER);
        lblMsg.setFont(new Font("SansSerif", Font.BOLD, 13));
        lblMsg.setForeground(palette.textMain);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        btnPanel.setOpaque(false);

        JButton btnYes = createStyledButton("Sí, continuar", palette);
        JButton btnNo = createStyledButton("Cancelar", palette);

        btnYes.addActionListener(e -> {
            result[0] = true;
            dialog.dispose();
        });

        btnNo.addActionListener(e -> {
            result[0] = false;
            dialog.dispose();
        });

        btnPanel.add(btnYes);
        btnPanel.add(btnNo);

        content.add(lblMsg, BorderLayout.CENTER);
        content.add(btnPanel, BorderLayout.SOUTH);

        dialog.add(content);
        dialog.setBackground(new Color(0, 0, 0, 0));
        dialog.setVisible(true);

        return result[0];
    }

    private static JButton createStyledButton(String text, ThemeManager.ColorPalette palette) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("SansSerif", Font.BOLD, 12));
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setForeground(palette.textMain);

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) { btn.repaint(); }
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) { btn.repaint(); }
        });

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

    private static String toHex(Color color) {
        return String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
    }
}