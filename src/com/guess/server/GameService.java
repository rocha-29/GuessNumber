package com.guess.server;

import java.util.Random;

public class GameService {
    private final Random rnd = new Random();
    private int secret = 1 + rnd.nextInt(100);

    public synchronized int check(int guess) {
        if (guess < secret) return -1;   // más alto
        if (guess > secret) return 1;    // más bajo
        // acertó: regenerar número para la siguiente ronda
        secret = 1 + rnd.nextInt(100);
        return 0;
    }
}
