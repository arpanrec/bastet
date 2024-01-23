package com.arpanrec.minerva.gnupg

import org.bouncycastle.bcpg.ArmoredOutputStream
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.bouncycastle.openpgp.PGPEncryptedData
import org.bouncycastle.openpgp.PGPEncryptedDataGenerator
import org.bouncycastle.openpgp.PGPObjectFactory
import org.bouncycastle.openpgp.PGPPublicKey
import org.bouncycastle.openpgp.PGPPublicKeyEncryptedData
import org.bouncycastle.openpgp.PGPPublicKeyRingCollection
import org.bouncycastle.openpgp.PGPUtil
import org.bouncycastle.openpgp.operator.jcajce.JcaKeyFingerprintCalculator
import org.bouncycastle.openpgp.operator.jcajce.JcePGPDataEncryptorBuilder
import org.bouncycastle.openpgp.operator.jcajce.JcePublicKeyKeyEncryptionMethodGenerator
import org.slf4j.LoggerFactory
import org.springframework.boot.context.properties.ConfigurationProperties
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.nio.charset.StandardCharsets
import java.security.SecureRandom
import java.security.Security

@ConfigurationProperties(prefix = "minerva.gnupg")
class GnuPG {
    private val log = LoggerFactory.getLogger(this.javaClass)

    init {
        log.info("Adding BouncyCastle provider.")
        Security.addProvider(BouncyCastleProvider())
    }

    private var privateKey: String? = null
    private var passphrase: String? = null
    private var publicKeyString: String? = null
    private var encryptedDataGenerator: PGPEncryptedDataGenerator? = null
    fun setPrivateKey(privateKey: String) {
        this.privateKey = privateKey
    }

    fun setPassphrase(passphrase: String) {
        this.passphrase = passphrase
    }

    fun setPublicKeyString(publicKey: String) {
        this.publicKeyString = publicKey
    }

    private fun setEncryptedDataGenerator() {
        log.info("Setting up encrypted data generator.")
        requireNotNull(this.publicKeyString) { "Public key string is null." }
        val publicKeyStream: InputStream = ByteArrayInputStream(
            this.publicKeyString!!.toByteArray(StandardCharsets.UTF_8)
        )
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

        val encryptedDataGenerator = PGPEncryptedDataGenerator(
            JcePGPDataEncryptorBuilder(PGPEncryptedData.AES_256)
                .setWithIntegrityPacket(true)
                .setSecureRandom(SecureRandom())
                .setProvider("BC")
        )
        encryptedDataGenerator.addMethod(
            JcePublicKeyKeyEncryptionMethodGenerator(pgpPublicKey).setProvider(
                "BC"
            )
        )
        this.encryptedDataGenerator = encryptedDataGenerator
    }

    fun encrypt(data: String): String {
        if (this.encryptedDataGenerator == null) {
            this.setEncryptedDataGenerator()
        }
        val encryptedOut = ByteArrayOutputStream()
        val out: OutputStream = ArmoredOutputStream(encryptedOut)
        val encryptedOutStream = encryptedDataGenerator!!.open(out, ByteArray(1 shl 16))
        encryptedOutStream.write(data.toByteArray(StandardCharsets.UTF_8))
        encryptedOutStream.close()
        out.close()
        val encryptedData = encryptedOut.toString(StandardCharsets.UTF_8)
        return encryptedData
    }
}
