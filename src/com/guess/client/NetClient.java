package com.guess.client;

import com.guess.common.*;

import java.io.*;
import java.net.Socket;

public class NetClient implements Closeable {
    private final Socket socket;
    private final ObjectOutputStream out;
    private final ObjectInputStream in;

    public NetClient(String host, int port) throws IOException {
        socket = new Socket(host, port);
        out = new ObjectOutputStream(socket.getOutputStream());
        in  = new ObjectInputStream(socket.getInputStream());
    }

    public Response send(Request r) throws IOException, ClassNotFoundException {
        out.writeObject(r); out.flush();
        return (Response) in.readObject();
    }

    @Override public void close() throws IOException { socket.close(); }
}
