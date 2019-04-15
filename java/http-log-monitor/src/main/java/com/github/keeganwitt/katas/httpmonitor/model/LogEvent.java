package com.github.keeganwitt.katas.httpmonitor.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.var;

/**
 * Stores data from parsed log.
 */
@EqualsAndHashCode
public class LogEvent {
    @Getter
    @Setter
    private String remoteHost;

    @Getter
    @Setter
    private String authUser;

    @Getter
    @Setter
    private int date;

    @Getter
    @Setter
    private String httpMethod;

    @Getter
    @Setter
    private String path;

    @Getter
    @Setter
    private short status;

    @Getter
    @Setter
    private long bytes;

    public String getSection() {
        var secondSlashIndex = path.indexOf("/", 1);
        return path.substring(0, secondSlashIndex > 0 ? secondSlashIndex : path.length());
    }
}
