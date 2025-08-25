package com.guess.server;

import com.guess.common.*;
import com.guess.model.ScoreEntry;

import java.io.*;
import java.net.Socket;
import java.util.*;

public class ClientHandler implements Runnable {
    private final Socket socket;
    private final GameService game;
    private final Scoreboard board;

    // estado por sesión
    private long startMillis = 0;
    private int attempts = 0;
    private String currentName = "";

    public ClientHandler(Socket socket, GameService game, Scoreboard board) {
        this.socket = socket; this.game = game; this.board = board;
    }

    @Override public void run() {
        try (ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream  in  = new ObjectInputStream(socket.getInputStream())) {

            while (true) {
                Object obj = in.readObject();
                if (!(obj instanceof Request)) {
                    out.writeObject(Response.error("Request inválido"));
                    continue;
                }
                Request req = (Request) obj;
                Response resp;

                switch (req.getCommand()) {
                    case GUESS: {
                        @SuppressWarnings("unchecked")
                        Map<String,Object> m = (Map<String,Object>) req.getPayload();
                        String name = Objects.toString(m.get("name"), "Jugador");
                        int guess = (Integer) m.get("guess");
                        if (startMillis == 0) { startMillis = System.currentTimeMillis(); attempts = 0; currentName = name; }
                        attempts++;

                        int cmp = game.check(guess); // -1 alto, 1 bajo, 0 correcto
                        Map<String,Object> data = new HashMap<>();
                        if (cmp < 0) { data.put("result", "más alto"); resp = Response.ok(data); }
                        else if (cmp > 0) { data.put("result", "más bajo"); resp = Response.ok(data); }
                        else {
                            long elapsed = System.currentTimeMillis() - startMillis;
                            board.add(new ScoreEntry(currentName, attempts, elapsed));
                            data.put("result", "¡correcto!");
                            data.put("attempts", attempts);
                            data.put("millis", elapsed);
                            resp = Response.ok(data);
                            // reiniciar sesión del jugador para una nueva ronda
                            startMillis = 0; attempts = 0; currentName = "";
                        }
                        out.writeObject(resp); out.flush();
                        break;
                    }
                    case GET_TOP: {
                        List<ScoreEntry> top = board.getTop();
                        out.writeObject(Response.ok(top)); out.flush();
                        break;
                    }
                    default:
                        out.writeObject(Response.error("Comando no soportado")); out.flush();
                }
            }
        } catch (Exception e) {
            // cierre normal del cliente
        }
    }
}
