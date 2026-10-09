package com.vsmoon.editor;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;

public class ThemeSelectorButton extends JButton {

    private ThemeManager.Theme currentTheme = ThemeManager.Theme.DARK;
    private final EditorFrame mainFrame;
    private ThemeManager.ColorPalette palette;

    public ThemeSelectorButton(EditorFrame mainFrame, ThemeManager.ColorPalette palette) {
        this.mainFrame = mainFrame;
        this.palette = palette;

        setFont(new Font("SansSerif", Font.BOLD, 12));
        setFocusPainted(false);
        setContentAreaFilled(false);
        setBorderPainted(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));

        updateThemeState(ThemeManager.Theme.DARK, palette);

        addActionListener(e -> showThemeMenu());

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) { repaint(); }
            @Override
            public void mouseExited(MouseEvent e) { repaint(); }
        });
    }

    public void updateThemeState(ThemeManager.Theme theme, ThemeManager.ColorPalette palette) {
        this.currentTheme = theme;
        this.palette = palette;

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

        item.addActionListener(e -> mainFrame.applyTheme(theme));
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