package com.ddquin.simpletexteditor;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.File;

@AllArgsConstructor
public class TreeFile {

    private @Getter File file;

    @Override
    public String toString() {
        return file.getName();
    }
}
