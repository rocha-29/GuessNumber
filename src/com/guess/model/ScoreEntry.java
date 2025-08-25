package com.guess.model;

import java.io.Serializable;
import java.util.Date;

public class ScoreEntry implements Serializable, Comparable<ScoreEntry> {
    private static final long serialVersionUID = 1L;
    private String name;
    private int attempts;
    private long millis; // menor es mejor
    private Date date;

    public ScoreEntry(String name, int attempts, long millis) {
        this.name = name; this.attempts = attempts; this.millis = millis; this.date = new Date();
    }
    public String getName() { return name; }
    public int getAttempts() { return attempts; }
    public long getMillis() { return millis; }
    public Date getDate() { return date; }

    @Override public int compareTo(ScoreEntry o) {
        int byTime = Long.compare(this.millis, o.millis);
        return (byTime != 0) ? byTime : Integer.compare(this.attempts, o.attempts);
    }

    @Override public String toString() {
        return name + " — " + attempts + " intentos — " + (millis/1000.0) + "s";
    }
}
