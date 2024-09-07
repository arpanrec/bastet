package com.arpanrec.bastet.exceptions;

import java.io.Serial;

public class CaughtException extends Exception {
    @Serial
    private static final long serialVersionUID = 4029537891117232440L;

    public CaughtException(String message) {
        super(message);
    }

    public CaughtException(String message, Throwable cause) {
        super(message, cause);
    }

    public CaughtException(Throwable cause) {
        super(cause);
    }
}
