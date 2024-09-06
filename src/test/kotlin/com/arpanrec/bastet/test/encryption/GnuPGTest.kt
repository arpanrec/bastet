package com.arpanrec.bastet.test.encryption

import com.arpanrec.bastet.encryption.GnuPG
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.ApplicationContext

@SpringBootTest
class GnuPGTest(
    @Autowired private val gnuPG: GnuPG,
) {

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
    private val armoredPrivateKey = """-----BEGIN PGP PRIVATE KEY BLOCK-----

lIYEZa70ABYJKwYBBAHaRw8BAQdAqc7Zfp6aQyefH7FWJOHWGyKSwIZe2L9e+pVm
umnaeIz+BwMCdNs9UHB91Gn/sq1FqE2sz9/ZguQjtGCOsmqjAUr5WJqGB2NE9RR4
2GgMEWy5UzDZzcO5ckHEZVuqE0HbAH/X2Farkr6ZBIDDWeLNs2CqBbQsbWluZXJ2
YS10ZXN0IDxtaW5lcnZhLXRlc3RAbWluZXJ2YS10ZXN0LmNvbT6ImQQTFgoAQRYh
BH+1ftYl3tbRZvGFj+tj9wE9kLoABQJlrvQAAhsDBQkFo5qABQsJCAcCAiICBhUK
CQgLAgQWAgMBAh4HAheAAAoJEOtj9wE9kLoA/Q4BAKdZAmAQMd1Ei6Aw5EIEuj2L
uBOdM2BXei/2feH7Iw+aAP0c3b2b2jQ4hBsPIUKoTiPBtTRQMHr9pfZ2BeDAXTFq
AJyLBGWu9AASCisGAQQBl1UBBQEBB0B6aiWarLiQWrmIS0VnIQQvB/vc5jsNxYUr
k0i/TELPXwMBCAf+BwMCizbuwMKSJ0n/reXH0dlE03diLm6k8irQt4aoEf5Fr3GO
uxzWPcrRJJqtwC/I5f4UplVmF3tz5/t2xahIr6nED8kzdk21qQSY5jmnIunMMIh+
BBgWCgAmFiEEf7V+1iXe1tFm8YWP62P3AT2QugAFAmWu9AACGwwFCQWjmoAACgkQ
62P3AT2QugCfHgD+KjrWed+V/PcHoqtyeXPbFfY6KXLugSbV614ZNt1t34YA/3wG
1tw2qmtpjSy2EpCx9Om+vrY6TsbHdv2j2vOEd4QM
=SQ4t
-----END PGP PRIVATE KEY BLOCK-----

"""
    private val privateKeyPassphrase = "password"


    @Test
    fun testEncrypt() {
        gnuPG.setPgpPrivateKeyFromArmoredString(armoredPrivateKey, privateKeyPassphrase)
        val encryptedMessage = gnuPG.encrypt(message)
        log.info("Encrypted message: {}", encryptedMessage)
    }

    @Test
    fun testDecrypt() {
        gnuPG.setPgpPrivateKeyFromArmoredString(armoredPrivateKey, privateKeyPassphrase)
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
