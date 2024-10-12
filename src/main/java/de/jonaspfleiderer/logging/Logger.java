package de.jonaspfleiderer.logging;

import de.jonaspfleiderer.ui.ErrorUi;

import javax.swing.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Logger {
    private final File logFile;
    private boolean enabled;

    public Logger(File logFile) {
        this.logFile = logFile;
        if (logFile.exists()) {
            if (!logFile.delete()) {
                enabled = false;
                return;
            }
        }

        try {
            if (!logFile.createNewFile()) {
                enabled = false;
                return;
            }
            enabled = true;
        } catch (IOException e) {
            enabled = false;
        }
    }

    public void log(String message) {
        if (!enabled) return;

        try {
            FileWriter fw = new FileWriter(logFile, true);
            fw.write("[" + LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) + "] " + message + "\n");
            fw.close();
        } catch (IOException e) {
            ErrorUi ui = new ErrorUi(250, 250, e.getMessage(), WindowConstants.HIDE_ON_CLOSE);
            ui.setVisible(true);
        }
    }

    public void logError(String message) {
        if (!enabled) return;

        log("ERROR:\n   -> " + message);
    }
}
