package com.vsmoon.editor;

import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            EditorFrame editor = new EditorFrame();
            editor.setVisible(true);
        });
    }
}