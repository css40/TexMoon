package com.vsmoon.editor;

import java.awt.Color;

public class ThemeManager {

    public enum Theme {
        DARK, LIGHT, SEPIA
    }

    public static class ColorPalette {
        public Color bgDark;
        public Color editorBg;
        public Color barBg;
        public Color cardBg;
        public Color cardHover;
        public Color textMain;
        public Color textMuted;
        public Color accent;
        public Color accentHover;
    }

    public static ColorPalette getPalette(Theme theme) {
        ColorPalette palette = new ColorPalette();

        switch (theme) {
            case LIGHT:
                palette.bgDark      = new Color(0xE6, 0xE8, 0xEE); // Fondo ventana
                palette.editorBg    = new Color(0xFF, 0xFF, 0xFF); // Fondo editor
                palette.barBg       = new Color(0xD8, 0xDE, 0xE9); // Fondo barras
                palette.cardBg      = new Color(0xC8, 0xD0, 0xE0); // Botones secundarios
                palette.cardHover   = new Color(0xB8, 0xC2, 0xD4); // Hover
                palette.textMain    = new Color(0x2E, 0x34, 0x40); // Texto oscuro legible
                palette.textMuted   = new Color(0x5E, 0x81, 0xAC); // Subtítulos
                palette.accent      = new Color(0x7C, 0x3A, 0xED); // Púrpura acento
                palette.accentHover = new Color(0x6D, 0x28, 0xD9);
                break;

            case SEPIA:
                palette.bgDark      = new Color(0xE8, 0xE0, 0xD0);
                palette.editorBg    = new Color(0xF9, 0xF5, 0xEB);
                palette.barBg       = new Color(0xDC, 0xD3, 0xC1);
                palette.cardBg      = new Color(0xCE, 0xC3, 0xAD);
                palette.cardHover   = new Color(0xBF, 0xB2, 0x9B);
                palette.textMain    = new Color(0x43, 0x3B, 0x32);
                palette.textMuted   = new Color(0x8F, 0x7A, 0x66);
                palette.accent      = new Color(0xB5, 0x59, 0x00);
                palette.accentHover = new Color(0x94, 0x48, 0x00);
                break;

            case DARK:
            default:
                palette.bgDark      = new Color(0x11, 0x11, 0x1B);
                palette.editorBg    = new Color(0x1E, 0x1E, 0x2E);
                palette.barBg       = new Color(0x18, 0x18, 0x25);
                palette.cardBg      = new Color(0x31, 0x32, 0x44);
                palette.cardHover   = new Color(0x45, 0x47, 0x5A);
                palette.textMain    = new Color(0xCD, 0xD6, 0xF4);
                palette.textMuted   = new Color(0xA6, 0xAD, 0xC8);
                palette.accent      = new Color(0xCB, 0xA6, 0xF7);
                palette.accentHover = new Color(0xB4, 0x8E, 0xAD);
                break;
        }

        return palette;
    }
}