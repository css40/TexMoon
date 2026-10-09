package com.vsmoon.editor;

import javax.swing.*;
import javax.swing.plaf.basic.BasicComboBoxUI;
import java.awt.*;

public class CustomComboBoxUI extends BasicComboBoxUI {

    private final Color bgColor;
    private final Color textColor;

    public CustomComboBoxUI(Color bgColor, Color textColor) {
        this.bgColor = bgColor;
        this.textColor = textColor;
    }

    @Override
    protected JButton createArrowButton() {
        JButton btn = new JButton("▾");
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setForeground(textColor);
        btn.setFont(new Font("SansSerif", Font.BOLD, 12));
        return btn;
    }

    @Override
    public void paintCurrentValueBackground(Graphics g, Rectangle bounds, boolean hasFocus) {
        // Mantiene el fondo limpio sin bordes cuadrados por defecto
    }
}