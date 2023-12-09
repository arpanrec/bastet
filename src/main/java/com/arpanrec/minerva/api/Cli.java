package com.arpanrec.minerva.api;

import com.arpanrec.minerva.secrets.MinervaSecretProperties;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Getter
@Component
@Slf4j
public class Cli implements CommandLineRunner {

    private MinervaSecretProperties minervaSecretProperties;

    @Autowired
    public void setProps(MinervaSecretProperties minervaSecretProperties) {
        this.minervaSecretProperties = minervaSecretProperties;
    }


    @Override
    public void run(String... args) throws Exception {
        log.info("encrypt: " + this.minervaSecretProperties.isEncrypt());
        log.info("encryptionKey: " + this.minervaSecretProperties.getEncryptionKey());
    }
}
