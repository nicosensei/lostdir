package com.github.nicosensei.lostdir.helpers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Collection;

/**
 * Text and string manipulation utilities.
 *
 * @author Nicolas Giraud
 *
 */
public final class TextUtils {

    public static final Charset UTF8 = Charset.forName("UTF-8");

    public static final String PLATFORM_EOL = System.lineSeparator();

    private TextUtils() {
        // prevent instantiation
    }

    public static String getPlatformLineSeparator() {
        return PLATFORM_EOL;
    }

    /**
     * Produces the concatenation of the given values separated by the given separator.
     * @param values the values to join
     * @param separator the separator to use
     * @return the concatenated values
     */
    public static String conjoin(
            final String[] values,
            final String separator) {
        return conjoin(values, separator);
    }

    /**
     * Produces the concatenation of the given values separated by the given separator.
     * @param values the values to join
     * @param separator the separator to use
     * @return the concatenated values
     */
    public static String conjoin(
            final Collection<String> values,
            final String separator) {
        StringBuilder sb = new StringBuilder(values.size());
        for (Object v : values) {
            sb.append(v + separator);
        }
        return sb.substring(0, sb.lastIndexOf(separator));
    }

    /**
     * Reads a text file from the given input stream.
     * @param inStream the input stream to read from.
     * @return a {@link StringBuilder} populated with the contents.
     * @throws IOException if an errors occurs
     */
    public static final StringBuilder readTextFile(final InputStream inStream) throws IOException {
        StringBuilder sb = new StringBuilder(1000);
        BufferedReader br = new BufferedReader(new InputStreamReader(inStream));
        String line = null;
        while ((line = br.readLine()) != null) {
            sb.append(line).append(PLATFORM_EOL);
        }
        br.close();
        return sb;
    }

}
