package com.vsmoon.editor;

import javax.swing.JTextArea;
import javax.swing.Timer;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class AutoSaveManager {

    private Timer timer;
    private JTextArea textArea;
    private File draftFile;

    public AutoSaveManager(JTextArea textArea, int intervalMinutes) {
        this.textArea = textArea;
        this.draftFile = new File(System.getProperty("user.home"), ".vsmoon_draft.tmp");

        int delay = intervalMinutes * 60 * 1000;
        this.timer = new Timer(delay, e -> saveDraft());
    }

    public void start() {
        if (!timer.isRunning()) {
            timer.start();
        }
    }

    public void stop() {
        if (timer.isRunning()) {
            timer.stop();
        }
    }

    private void saveDraft() {
        if (textArea.getText().trim().isEmpty()) return;

        try (FileWriter writer = new FileWriter(draftFile)) {
            writer.write(textArea.getText());
        } catch (IOException ignored) {
            // Manejo silencioso de borrador temporal
        }
    }
}