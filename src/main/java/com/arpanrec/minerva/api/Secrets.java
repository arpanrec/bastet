package com.arpanrec.minerva.api;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Getter
@Slf4j
@Entity
public class Secrets {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String path;
    private String content;

    protected Secrets() {}

    public Secrets(String path, String content) {
        this.path = path;
        this.content = content;
    }

    @Override
    public String toString() {
        return String.format("Secrets[id=%d, path='%s', content='%s']", id, path, content);
    }

}
