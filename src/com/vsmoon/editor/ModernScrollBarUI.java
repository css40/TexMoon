package com.vsmoon.editor;

import javax.swing.*;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;

public class ModernScrollBarUI extends BasicScrollBarUI {

    private Color thumbColor;
    private Color trackColor;

    public ModernScrollBarUI(Color thumbColor, Color trackColor) {
        this.thumbColor = thumbColor;
        this.trackColor = trackColor;
    }

    public void setColors(Color thumbColor, Color trackColor) {
        this.thumbColor = thumbColor;
        this.trackColor = trackColor;
    }

    @Override
    protected void configureScrollBarColors() {
        this.thumbColor = (thumbColor != null) ? thumbColor : new Color(0x45, 0x47, 0x5A);
        this.trackColor = (trackColor != null) ? trackColor : new Color(0x1E, 0x1E, 0x2E);
    }

    @Override
    protected JButton createDecreaseButton(int orientation) {
        return createZeroButton();
    }

    @Override
    protected JButton createIncreaseButton(int orientation) {
        return createZeroButton();
    }

    private JButton createZeroButton() {
        JButton button = new JButton();
        button.setPreferredSize(new Dimension(0, 0));
        button.setMinimumSize(new Dimension(0, 0));
        button.setMaximumSize(new Dimension(0, 0));
        return button;
    }

    @Override
    protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(trackColor);
        g2.fillRect(trackBounds.x, trackBounds.y, trackBounds.width, trackBounds.height);
        g2.dispose();
    }

    @Override
    protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
        if (thumbBounds.isEmpty() || !scrollbar.isEnabled()) {
            return;
        }

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        Color color = isThumbRollover() ? thumbColor.brighter() : thumbColor;
        g2.setColor(color);
        g2.fillRoundRect(thumbBounds.x + 2, thumbBounds.y + 2, thumbBounds.width - 4, thumbBounds.height - 4, 10, 10);

        g2.dispose();
    }
}