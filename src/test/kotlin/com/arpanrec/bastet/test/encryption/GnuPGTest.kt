package com.arpanrec.minerva.test.encryption

import com.arpanrec.bastet.encryption.GnuPG
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class GnuPGTest(@Autowired private val gnuPG: GnuPG) {
    private val log = LoggerFactory.getLogger(GnuPGTest::class.java)
    private val message = "Hello, World!"
    private val armoredBcEncryptedMessage = """-----BEGIN PGP MESSAGE-----
Version: BCPG v1.77.00

wV4DubSNXBIhRMMSAQdAMzXed6cKZmK5X/PkUavSUSXUyxfCqcCs1kYUhGxRDkQw
ojse2dwnw/9aPptGoD2aVzlLkSVQ0lxpkjLeQrVUlHCMnjhyAMeoqce4mvsI0bxo
0mIBNEyj/X0lby5nFUg0wRpEfQ4gwUX5wAy0NJPNBDHko1OE8tuyTk44sMeJAcDD
bEfLuDjkxbJffFsILsiy4JukCOT2YOS1g2eop3Coc+QGbpdVNfmbj2YB5m46ZiJL
7jndTQ==
=XQSg
-----END PGP MESSAGE-----

"""
    private val armoredCliEncryptedMessage = """-----BEGIN PGP MESSAGE-----

hF4DubSNXBIhRMMSAQdAt9CNm3Bpm/jWLJhs8CKTnw13kV7m3hgzFw0AbuZqBlow
MMwyyeFE92pUNljB6OW7ayl1gKkw4K6b2GK+AckTHZTKG7VTihSV/p/WN6Z1/mba
1FsBCQIQyoMaLr6mnJ5FJsbCOOG24MM6etvVN8RK88M37DXEsc2RAe+b+O7PBjib
GVD/xf7huNGhmdOr8OLf1X7jmzpUdEZLblbZ0fFR9V68E59oU6TT87nt4NVc
=R8qx
-----END PGP MESSAGE-----

"""

    @Test
    fun testEncrypt() {
        val encryptedMessage = gnuPG.encrypt(message)
        log.info("Encrypted message: {}", encryptedMessage)
    }

    @Test
    fun testDecrypt() {
        val decryptedMessage = gnuPG.decrypt(armoredBcEncryptedMessage)
        log.info("Decrypted message: {}", decryptedMessage)
        assert(decryptedMessage == message)
    }

    @Test
    fun testEncryptAndDecrypt() {
        val encryptedMessage = gnuPG.encrypt(message)
        log.info("Encrypted message: {}", encryptedMessage)
        val decryptedMessage = gnuPG.decrypt(encryptedMessage)
        log.info("Decrypted message: {}", decryptedMessage)
        assert(decryptedMessage == message)
    }

    @Test
    fun testCliEncryptedMessage() {
        val decryptedMessage = gnuPG.decrypt(armoredCliEncryptedMessage)
        log.info("Decrypted message: {}", decryptedMessage)
        assert(decryptedMessage == message)
    }
}
