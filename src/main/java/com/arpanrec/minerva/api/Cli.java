package com.arpanrec.minerva.api;

import com.arpanrec.minerva.kv.KeyValueProperties;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Getter
@Component
@Slf4j
public class Cli implements CommandLineRunner {

    private KeyValueProperties keyValueProperties;

    @Autowired
    public void setProps(KeyValueProperties keyValueProperties) {
        this.keyValueProperties = keyValueProperties;
    }


    @Override
    public void run(String... args) throws Exception {
        log.info("encrypt: " + this.keyValueProperties.isEncrypt());
        log.info("encryptionKey: " + this.keyValueProperties.getEncryptionKey());
    }
}
