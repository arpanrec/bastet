package com.arpanrec.minerva.secrets;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "secrets")
public class Secret {

    public Secret(String path, String content) {
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
