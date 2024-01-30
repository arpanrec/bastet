package com.arpanrec.minerva.exceptions;

import java.io.Serial;

public class MinervaException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 4029537891117232440L;

    public MinervaException(String message) {
        super(message);
    }

    public MinervaException(String message, Throwable cause) {
        super(message, cause);
    }
}
