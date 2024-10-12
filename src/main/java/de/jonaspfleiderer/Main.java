package de.jonaspfleiderer;

import com.formdev.flatlaf.FlatLightLaf;
import de.jonaspfleiderer.logging.Logger;
import de.jonaspfleiderer.ui.ErrorUi;
import de.jonaspfleiderer.ui.MainUi;
import de.jonaspfleiderer.util.FontUtils;
import de.jonaspfleiderer.util.ResourceManager;

import javax.swing.*;
import java.io.File;

public class Main {
    private static final boolean isWindows = System.getProperty("os.name").toLowerCase().contains("windows");
    private static String parserName;
    private static final File logFile = new File("latest.log");
    private static Logger logger;

    public static void main(String[] args) {
        logger = new Logger(logFile);

        ResourceManager.setup();
        FontUtils.setup();

        FlatLightLaf.setup();

        if (isWindows) {
            parserName = "parser.exe";
        } else {
            parserName = "parser";
        }
        if (!new File(parserName).exists()) {
            logger.logError("[Main] The parser '" + parserName + "' does not exist.");
            new ErrorUi(250, 200, "Mandatory program '" + parserName + "' was not found. Please add this application to the same directory as the 'FilePartitioner.jar' file.", WindowConstants.EXIT_ON_CLOSE).setVisible(true);
            return;
        }

        MainUi mainUi = new MainUi(400, 180);
        mainUi.setVisible(true);
    }

    public static String getParserName() {
        return parserName;
    }

    public static Logger getLogger() {
        return logger;
    }
}