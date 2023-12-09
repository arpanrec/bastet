package com.arpanrec.minerva.secrets;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "minerva.secret")
@Data
public class MinervaSecretProperties {

    private boolean encrypt;
    private String encryptionKey = "minerva-default-encryption-key";
}
