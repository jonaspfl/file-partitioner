package de.jonaspfleiderer;

import de.jonaspfleiderer.ui.ErrorUi;
import de.jonaspfleiderer.ui.MainUi;
import de.jonaspfleiderer.util.FontUtils;
import de.jonaspfleiderer.util.ResourceManager;

import java.io.File;

public class Main {
    private static final boolean isWindows = System.getProperty("os.name").toLowerCase().contains("windows");
    private static String parserName;

    public static void main(String[] args) {
        ResourceManager.setup();
        FontUtils.setup();

        if (isWindows) {
            parserName = "parser.exe";
        } else {
            parserName = "parser";
        }
        if (!new File(parserName).exists()) {
            new ErrorUi(250, 200, "Mandatory program '" + parserName + "' was not found. Please add this application to the same directory as the 'FilePartitioner.jar' file.").setVisible(true);
            return;
        }

        MainUi mainUi = new MainUi(400, 180);
        mainUi.setVisible(true);
    }

    public static String getParserName() {
        return parserName;
    }
}