package com.arpanrec.minerva.kv;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "keyvalue")
@Setter
@Getter
public class KeyValue implements Serializable {


    public KeyValue(String path, String content) {
        this.path = path;
        this.content = content;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;

    private String path;

    private String content;

    @Override
    public String toString() {
        return String.format("Secrets[id=%d, path='%s', content='%s']", id, path, content);
    }

}
