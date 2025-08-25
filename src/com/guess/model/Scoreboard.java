package com.guess.server;

import com.guess.model.ScoreEntry;
import java.io.*;
import java.util.*;

public class Scoreboard {
    private final File file = new File("scores.ser");
    private final List<ScoreEntry> top = new ArrayList<>();

    public Scoreboard() { load(); }

    public synchronized void add(ScoreEntry e) {
        top.add(e);
        Collections.sort(top);
        if (top.size() > 5) top.remove(top.size()-1); // mantener top 5
        save();
    }

    public synchronized List<ScoreEntry> getTop() {
        return new ArrayList<>(top);
    }

    @SuppressWarnings("unchecked")
    private void load() {
        if (!file.exists()) return;
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {
            Object o = in.readObject();
            if (o instanceof List<?>) {
                top.clear();
                top.addAll((List<ScoreEntry>) o);
            }
        } catch (Exception ignored) {}
    }

    private void save() {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file))) {
            out.writeObject(top);
        } catch (Exception ignored) {}
    }
}
