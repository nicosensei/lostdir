package com.github.nicosensei.lostdir.trid;

import com.github.nicosensei.lostdir.helpers.process.StdOutProcessor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by nicos on 11/3/2016.
 */
public final class TridOuputProcessor extends StdOutProcessor<FileDiagnostic> {

    private static final String REGEX_WHITESPACE = "\\s+";

    private static final Pattern PATTERN_EXT = 	Pattern.compile("(\\d+\\.\\d+)%\\s+\\(\\.(\\w+)\\)\\s+(.*)");

    private final String analyzedFile;

    public TridOuputProcessor(final String analyzedFile) {
        this.analyzedFile = analyzedFile;
    }

    private boolean readFileName = false;

    @Override
    protected FileDiagnostic initResult() {
        return new FileDiagnostic(analyzedFile);
    }

    @Override
    protected void processStdOutLine(final String line, final FileDiagnostic result) {
        if (readFileName) {
            final Matcher m = PATTERN_EXT.matcher(line);
            if (m.find()) {
                result.add(new Extension(m.group(2), Double.parseDouble(m.group(1)), m.group(3)));
            }
        } else if (line.endsWith(analyzedFile)) {
            readFileName = true;
        }
    }
}
