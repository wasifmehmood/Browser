package com.example.privatebrowser.FileFilter;

import java.io.File;
import java.io.FileFilter;

public class AudioFileFilter implements FileFilter {

    private File file;

    /**
     * Array includes the supported audio file types
     */
    private final String[] okFileExtensions = new String[]{
            "mp3",
            "wav"
    };

    /**
     * Setter for file
     * @param newfile
     */
    public AudioFileFilter(File newfile) {
        this.file = newfile;
    }

    /**
     * Checks if the opened file is supported or not.
     *
     * @param file
     * @return
     */
    public boolean accept(File file) {
        for (String extension : okFileExtensions) {
            if (file.getName().toLowerCase().endsWith(extension)) {
                return true;
            }
        }
        return false;
    }


}
