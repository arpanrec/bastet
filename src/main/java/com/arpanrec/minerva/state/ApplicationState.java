package com.arpanrec.minerva.state;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

@Setter
@Getter
public class ApplicationState implements Serializable {

    @Serial
    private static final long serialVersionUID = -194651103624136847L;

    private boolean rootUserCreated;

}
