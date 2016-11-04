package com.github.nicosensei.lostdir.scan;

import com.github.nicosensei.lostdir.helpers.GlobalConstants;

/**
 * Created by nicos on 11/3/2016.
 */
public final class Extension {

    private final String extension;

    private final String description;

    private final double rate;

    public Extension(final String extension, final double rate, final String description) {
        this.extension = extension;
        this.rate = rate;
        this.description = description;
    }

    public String getExtension() {
        return extension;
    }

    public double getRate() {
        return rate;
    }

    @Override
    public String toString() {
        return new StringBuilder(extension)
                .append(GlobalConstants.CHAR_SPACE).append(rate)
                .append(GlobalConstants.CHAR_SPACE).append(description)
                .toString();
    }
}
