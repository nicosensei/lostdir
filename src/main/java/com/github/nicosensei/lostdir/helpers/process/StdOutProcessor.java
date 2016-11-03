package com.github.nicosensei.lostdir.helpers.process;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by nicos on 11/3/2016.
 */
public abstract class StdOutProcessor<T> {

    public final T processStdOut(final Process process) throws IOException {
        final T result = initResult();
        final InputStream is = process.getInputStream();
        final InputStreamReader isr = new InputStreamReader(is);
        final BufferedReader br = new BufferedReader(isr);
        String line;
        while ((line = br.readLine()) != null) {
            processStdOutLine(line, result);
        }
        return result;
    }

    protected abstract T initResult();

    protected abstract void processStdOutLine(final String line, final T result);

}
