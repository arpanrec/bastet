package com.arpanrec.minerva.auth;

import lombok.Data;

import java.util.Collection;

@Data
public class Role {

    private Long id;

    private String name;

    private Collection<User> users;

    private Collection<Privilege> privileges;
}
