package com.arpanrec.minerva.api;

import com.arpanrec.minerva.secrets.SecretProperties;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Getter
@Component
@Slf4j
public class Cli implements CommandLineRunner {

    private SecretProperties secretProperties;

    @Autowired
    public void setProps(SecretProperties secretProperties) {
        this.secretProperties = secretProperties;
    }


    @Override
    public void run(String... args) throws Exception {
        log.info("encrypt: " + this.secretProperties.isEncrypt());
        log.info("encryptionKey: " + this.secretProperties.getEncryptionKey());
    }
}
