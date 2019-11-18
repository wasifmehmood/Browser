package com.example.privatebrowser.FileFilter;

import java.io.File;
import java.io.FileFilter;

public class VideoFileFilter implements FileFilter {

    private File file;


    private final String[] okFileExtensions = new String[] {
            "mp4",
            "avi",
            "wmv",
            "flv",
    };

    public VideoFileFilter(File newfile) {
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
