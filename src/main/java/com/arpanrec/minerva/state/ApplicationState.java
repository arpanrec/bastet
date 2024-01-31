package com.arpanrec.minerva.state;

import java.io.Serial;
import java.io.Serializable;

public class ApplicationState implements Serializable {
    @Serial
    private static final long serialVersionUID = -194651103624136847L;

    protected boolean rootUserCreated;

    protected String rootUserName;
}
