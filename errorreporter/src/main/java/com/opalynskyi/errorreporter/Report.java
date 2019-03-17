package com.opalynskyi.errorreporter;

import java.util.Objects;

public class Report {
    private long id;
    private long timestamp;
    private String message;
    private String trace;
    private boolean isFatal;
    private String threadName;
    private boolean isNew = true;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTrace() {
        return trace;
    }

    public void setTrace(String trace) {
        this.trace = trace;
    }

    public boolean isFatal() {
        return isFatal;
    }

    public void setFatal(boolean fatal) {
        isFatal = fatal;
    }

    public String getThreadName() {
        return threadName;
    }

    public void setThreadName(String threadName) {
        this.threadName = threadName;
    }

    public boolean isNew() {
        return isNew;
    }

    public void setNew(boolean isNew) {
        this.isNew = isNew;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Report report = (Report) o;
        return id == report.id &&
                timestamp == report.timestamp &&
                isFatal == report.isFatal &&
                isNew == report.isNew &&
                Objects.equals(message, report.message) &&
                Objects.equals(trace, report.trace) &&
                Objects.equals(threadName, report.threadName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, timestamp, message, trace, isFatal, threadName, isNew);
    }
}