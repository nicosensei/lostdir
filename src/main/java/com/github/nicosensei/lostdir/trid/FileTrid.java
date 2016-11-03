package com.github.nicosensei.lostdir.trid;

import com.github.nicosensei.lostdir.helpers.GlobalConstants;

import java.util.ArrayList;

/**
 * Created by nicos on 11/3/2016.
 */
public final class FileTrid extends ArrayList<Extension> {

    private final String path;

    public FileTrid(final String path) {
        super(3);
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(path).append(": [");
        for (Extension ext : this) {
            sb.append(GlobalConstants.NEWLINE).append(ext.toString());
        }
        return sb.toString();
    }
}
