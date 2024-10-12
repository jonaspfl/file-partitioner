package de.jonaspfleiderer.util;

import de.jonaspfleiderer.Main;

import java.io.IOException;
import java.net.URL;

public class ResourceManager {
    private static final ResourceManager instance = new ResourceManager();
    private static URL defaultFontResource;
    private static URL monoFontResource;

    private void loadResources() throws IOException {
        Main.getLogger().log("[ResourceManager] Loading resources...");
        defaultFontResource = getClass().getClassLoader().getResource("Fonts/font0.otf");
        monoFontResource = getClass().getClassLoader().getResource("Fonts/font1.otf");
    }

    public static void setup() {
        try {
            instance.loadResources();
        } catch (IOException e) {
            Main.getLogger().logError("[ResourceManager] Failed to load resources: " + e.getMessage());
        }
    }

    public static URL getDefaultFontResource() {
        return defaultFontResource;
    }

    public static URL getMonoFontResource() {
        return monoFontResource;
    }
}
