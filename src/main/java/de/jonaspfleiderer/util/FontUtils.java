package de.jonaspfleiderer.util;

import java.awt.*;
import java.io.File;
import java.io.IOException;

public class FontUtils {
    private static Font defaultFont;
    private static Font monoFont;

    public static void setup() {
        try {
            defaultFont = Font.createFont(Font.TRUETYPE_FONT, ResourceManager.getDefaultFontResource().openStream());
            monoFont = Font.createFont(Font.TRUETYPE_FONT, ResourceManager.getMonoFontResource().openStream());
        } catch (FontFormatException | IOException e) {
            defaultFont = new Font("Arial", Font.PLAIN, 20);
        }
    }

    public static Font getHeaderFont() {
        return defaultFont.deriveFont(20f);
    }

    public static Font getHeaderFontBold() {
        return defaultFont.deriveFont(Font.BOLD, 20f);
    }

    public static Font getNormalFont() {
        return defaultFont.deriveFont(15f);
    }

    public static Font getSmallFont() {
        return defaultFont.deriveFont(13f);
    }

    public static Font getBigButtonFont() {
        return defaultFont.deriveFont(18f);
    }

    public static Font getMonoSpaceFont() {
        return monoFont.deriveFont(Font.BOLD, 12f);
    }
}
