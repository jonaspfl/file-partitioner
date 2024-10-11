package de.jonaspfleiderer.util;

import javax.swing.filechooser.FileFilter;
import java.io.File;

public class ExtentionlessFileFilter extends FileFilter {
    @Override
    public boolean accept(File f) {
        if (f.isDirectory()) return true;
        return !f.getName().contains(".");
    }

    @Override
    public String getDescription() {
        return "";
    }
}
