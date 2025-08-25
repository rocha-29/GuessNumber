package com.guess.common;

import java.io.Serializable;

public class Response implements Serializable {
    private static final long serialVersionUID = 1L;
    private boolean ok;
    private String message;
    private Object data;

    public Response(boolean ok, String message, Object data) {
        this.ok = ok; this.message = message; this.data = data;
    }
    public static Response ok(Object data) { return new Response(true, "OK", data); }
    public static Response error(String msg) { return new Response(false, msg, null); }

    public boolean isOk() { return ok; }
    public String getMessage() { return message; }
    public Object getData() { return data; }
}
