package com.arpanrec.minerva.exceptions;

import java.io.Serial;

public class MinervaException extends Exception {
    @Serial
    private static final long serialVersionUID = 4029537891117232440L;

    public MinervaException(String message) {
        super(message);
    }

    public MinervaException(String message, Throwable cause) {
        super(message, cause);
    }

    public MinervaException(Throwable cause) {
        super(cause);
    }
}
