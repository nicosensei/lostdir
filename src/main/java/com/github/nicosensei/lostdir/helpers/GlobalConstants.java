package com.github.nicosensei.lostdir.helpers;

import java.io.File;
import java.nio.file.Paths;

/**
 * Created by nicos on 11/3/2016.
 */
public final class GlobalConstants {

    public static final String NEWLINE = System.lineSeparator();

    public static final String FILE_SEPARATOR = File.separator;

    public static final String CURRENT_DIR = Paths.get(".").toAbsolutePath().normalize().toString();

    public static final String CHAR_SPACE = " ";
    public static final String CHAR_DOT = ".";
}
