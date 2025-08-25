package com.guess.common;

public enum Command {
    GUESS,        // payload: { "name":String, "guess":Integer }
    GET_TOP       // payload: null
}
