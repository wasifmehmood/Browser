package com.example.privatebrowser.FileFilter;

import java.io.File;
import java.io.FileFilter;

public class DocFileFilter implements FileFilter {

    File file;
    private final String[] okFileExtensions = new String[] {
            "pdf",
            "doc",
            "txt"
    };

    public DocFileFilter(File newfile) {
        this.file = newfile;
    }

    public boolean accept(File file) {
        for (String extension: okFileExtensions) {
            if (file.getName().toLowerCase().endsWith(extension)) {
                return true;
            }
        }
        return false;
    }
}
