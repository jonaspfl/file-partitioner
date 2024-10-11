package de.jonaspfleiderer.util;

import java.io.IOException;
import java.net.URL;

public class ResourceManager {
    private static final ResourceManager instance = new ResourceManager();
    private static URL defaultFontResource;
    private static URL monoFontResource;

    private void loadResources() throws IOException {
        defaultFontResource = getClass().getClassLoader().getResource("Fonts/font0.otf");
        monoFontResource = getClass().getClassLoader().getResource("Fonts/font1.otf");
    }

    public static void setup() {
        try {
            instance.loadResources();
        } catch (IOException e) {
            System.err.println("Failed to load resources: " + e.getMessage());
        }
    }

    public static URL getDefaultFontResource() {
        return defaultFontResource;
    }

    public static URL getMonoFontResource() {
        return monoFontResource;
    }
}
