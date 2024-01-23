package com.arpanrec.minerva.test

import org.bouncycastle.bcpg.ArmoredOutputStream
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.bouncycastle.openpgp.*
import org.bouncycastle.openpgp.operator.jcajce.JcaKeyFingerprintCalculator
import org.bouncycastle.openpgp.operator.jcajce.JcePGPDataEncryptorBuilder
import org.bouncycastle.openpgp.operator.jcajce.JcePublicKeyKeyEncryptionMethodGenerator
import org.junit.jupiter.api.Test
import java.io.*
import java.nio.charset.StandardCharsets
import java.security.SecureRandom
import java.security.Security

class GpGTest {
    var privateKey: String = """
            -----BEGIN PGP PRIVATE KEY BLOCK-----

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
            """.trimIndent()
    var passphrase: String = "password"

    @Test
    @Throws(IOException::class, PGPException::class)
    fun test() {
        val dataToEncrypt = "Hello, World!" // The data you want to encrypt

        // Convert the public key string to an InputStream
        val publicKeyString = """
                -----BEGIN PGP PUBLIC KEY BLOCK-----

                mDMEZa70ABYJKwYBBAHaRw8BAQdAqc7Zfp6aQyefH7FWJOHWGyKSwIZe2L9e+pVm
                umnaeIy0LG1pbmVydmEtdGVzdCA8bWluZXJ2YS10ZXN0QG1pbmVydmEtdGVzdC5j
                b20+iJkEExYKAEEWIQR/tX7WJd7W0WbxhY/rY/cBPZC6AAUCZa70AAIbAwUJBaOa
                gAULCQgHAgIiAgYVCgkICwIEFgIDAQIeBwIXgAAKCRDrY/cBPZC6AP0OAQCnWQJg
                EDHdRIugMORCBLo9i7gTnTNgV3ov9n3h+yMPmgD9HN29m9o0OIQbDyFCqE4jwbU0
                UDB6/aX2dgXgwF0xagC4OARlrvQAEgorBgEEAZdVAQUBAQdAemolmqy4kFq5iEtF
                ZyEELwf73OY7DcWFK5NIv0xCz18DAQgHiH4EGBYKACYWIQR/tX7WJd7W0WbxhY/r
                Y/cBPZC6AAUCZa70AAIbDAUJBaOagAAKCRDrY/cBPZC6AJ8eAP4qOtZ535X89wei
                q3J5c9sV9jopcu6BJtXrXhk23W3fhgD/fAbW3Daqa2mNLLYSkLH06b6+tjpOxsd2
                /aPa84R3hAw=
                =kgso
                -----END PGP PUBLIC KEY BLOCK-----
                """.trimIndent()
        val publicKeyStream: InputStream = ByteArrayInputStream(
            publicKeyString.toByteArray(
                StandardCharsets.UTF_8
            )
        )

        // Read the public key
        val pgpPubRingCollection = PGPPublicKeyRingCollection(
            PGPUtil.getDecoderStream(publicKeyStream), JcaKeyFingerprintCalculator()
        )
        var pgpPublicKey: PGPPublicKey? = null
        val keyRingIter = pgpPubRingCollection.keyRings
        while (keyRingIter.hasNext() && pgpPublicKey == null) {
            val keyRing = keyRingIter.next()
            val keyIter = keyRing.publicKeys
            while (keyIter.hasNext()) {
                val key = keyIter.next()
                if (key.isEncryptionKey) {
                    pgpPublicKey = key
                    break
                }
            }
        }

        requireNotNull(pgpPublicKey) { "Can't find encryption key in key ring." }

        val encryptedOut = ByteArrayOutputStream()
        val out: OutputStream = ArmoredOutputStream(encryptedOut)

        val encryptedDataGenerator = PGPEncryptedDataGenerator(
            JcePGPDataEncryptorBuilder(PGPEncryptedData.CAST5)
                .setWithIntegrityPacket(true)
                .setSecureRandom(SecureRandom())
                .setProvider("BC")
        )

        encryptedDataGenerator.addMethod(
            JcePublicKeyKeyEncryptionMethodGenerator(pgpPublicKey).setProvider(
                "BC"
            )
        )

        val encryptedOutStream = encryptedDataGenerator.open(out, ByteArray(1 shl 16))
        encryptedOutStream.write(dataToEncrypt.toByteArray(StandardCharsets.UTF_8)) // dataToEncrypt is the string you want to encrypt
        encryptedOutStream.close()
        out.close()

        val encryptedData = encryptedOut.toString(StandardCharsets.UTF_8)
        println(encryptedData) // Your encrypted data
    }

    companion object {
        init {
            Security.addProvider(BouncyCastleProvider())
        }
    }
}