package com.github.nicosensei.lostdir.rename.config;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Created by nicos on 11/24/2016.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public final class Task {

    private String ext;
    private String filePattern;

    public String getExt() {
        return ext;
    }

    public void setExt(String ext) {
        this.ext = ext;
    }

    public String getFilePattern() {
        return filePattern;
    }

    public void setFilePattern(String filePattern) {
        this.filePattern = filePattern;
    }
}
