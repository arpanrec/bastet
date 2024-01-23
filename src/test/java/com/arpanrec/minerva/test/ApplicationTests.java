package com.arpanrec.minerva.test;

import com.arpanrec.minerva.gnupg.GnuPG;
import groovy.util.logging.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ApplicationTests {

    @Autowired
    GnuPG gpg;

    @Test
    void contextLoads() {
        System.out.println(gpg.encrypt("Hello World!"));
    }

}
