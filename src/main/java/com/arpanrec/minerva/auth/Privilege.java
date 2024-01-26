package com.arpanrec.minerva.auth;

import lombok.Data;

import java.util.Collection;

@Data
public class Privilege {

    private Long id;

    private String name;

    private Collection<Role> roles;
}
