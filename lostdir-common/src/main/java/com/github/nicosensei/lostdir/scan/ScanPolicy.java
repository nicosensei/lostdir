package com.github.nicosensei.lostdir.scan;

import java.util.Arrays;
import java.util.HashSet;

/**
 * Created by nicos on 11/16/2016.
 */
public final class ScanPolicy {

    public static final HashSet<String> LOOK_FOR_EXT = new HashSet<>(Arrays.asList(new String[] {
            "JPG", "MOV", "MP4", "M4V"
    }));

    public static final double DEFAULT_KEEP_THRESHOLD = 30.0d;

    private final double keepThreshold;

    public ScanPolicy(double keepThreshold) {
        this.keepThreshold = keepThreshold;
    }

    public double getKeepThreshold() {
        return keepThreshold;
    }
}
