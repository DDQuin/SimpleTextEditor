package com.ddquin.simpletexteditor;

import java.io.File;

public class TreeFile {

    private File file;

    public TreeFile(File file) {
        this.file = file;
    }

    public File getFile() {
        return file;
    }

    @Override
    public String toString() {
        return file.getName();
    }
}
