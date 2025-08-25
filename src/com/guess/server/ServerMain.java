package com.guess.server;

import java.net.*;
import java.util.concurrent.*;

public class ServerMain {
    public static void main(String[] args) throws Exception {
        GameService game = new GameService();
        Scoreboard board = new Scoreboard();
        ExecutorService pool = Executors.newCachedThreadPool();

        try (ServerSocket server = new ServerSocket(5000)) {
            System.out.println("Servidor GuessNumber en puerto 5000");
            while (true) {
                pool.submit(new ClientHandler(server.accept(), game, board));
            }
        }
    }
}
