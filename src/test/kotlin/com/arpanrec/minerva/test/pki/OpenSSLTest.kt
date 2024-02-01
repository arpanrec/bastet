package com.arpanrec.minerva.test.pki

import com.arpanrec.minerva.pki.OpenSSL
import org.junit.jupiter.api.Test
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class OpenSSLTest(
    @Autowired private val openSSL: OpenSSL
) {

    private final val log: Logger = LoggerFactory.getLogger(OpenSSLTest::class.java)

    private final val name: String = "CN=arpanrec trusted CA,OU=arpanrec CA,O=arpanrec,L=Arambagh,ST=West Bengal,C=IN"

    @Test
    fun test() {
        val name: String = openSSL.getX509CertificateCa().getSubjectX500Principal().name
        log.info("name: $name")
        assert(name == this.name)
        log.info("Certificate: ${openSSL.getX509CertificateCa()}")

        val keyPair = openSSL.getKeyPair()
        log.info("Private Key: ${keyPair.private}")

        log.info("Public Key from X509Certificate: ${openSSL.getX509CertificateCa().publicKey.algorithm}")
        log.info("Public Key from KeyPair: ${keyPair.public}")

        assert(openSSL.getX509CertificateCa().publicKey.equals(keyPair.public)) { "Public keys are not equal." }
    }
}
