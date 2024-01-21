package com.arpanrec.minerva.kv;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "minerva.keyvalue")
@Data
public class KeyValueProperties {

    private boolean encrypt;
    private String encryptionKey = "minerva-default-encryption-key";
}
