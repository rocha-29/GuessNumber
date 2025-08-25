package com.guess.common;

import java.io.Serializable;

public class Request implements Serializable {
    private static final long serialVersionUID = 1L;
    private Command command;
    private Object payload;

    public Request(Command command, Object payload) {
        this.command = command; this.payload = payload;
    }
    public Command getCommand() { return command; }
    public Object getPayload() { return payload; }
}
