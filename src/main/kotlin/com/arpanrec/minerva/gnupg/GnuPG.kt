package com.arpanrec.minerva.gnupg

import org.bouncycastle.bcpg.ArmoredOutputStream
import org.bouncycastle.bcpg.CompressionAlgorithmTags
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.bouncycastle.openpgp.PGPCompressedDataGenerator
import org.bouncycastle.openpgp.PGPEncryptedData
import org.bouncycastle.openpgp.PGPEncryptedDataGenerator
import org.bouncycastle.openpgp.PGPLiteralData
import org.bouncycastle.openpgp.PGPLiteralDataGenerator
import org.bouncycastle.openpgp.PGPPublicKey
import org.bouncycastle.openpgp.PGPPublicKeyRingCollection
import org.bouncycastle.openpgp.PGPUtil
import org.bouncycastle.openpgp.operator.bc.BcPublicKeyKeyEncryptionMethodGenerator
import org.bouncycastle.openpgp.operator.jcajce.JcaKeyFingerprintCalculator
import org.bouncycastle.openpgp.operator.jcajce.JcePGPDataEncryptorBuilder
import org.slf4j.LoggerFactory
import org.springframework.boot.context.properties.ConfigurationProperties
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.nio.charset.StandardCharsets
import java.security.SecureRandom
import java.security.Security
import java.util.Date

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
        val publicKeyStream: InputStream = ByteArrayInputStream(this.publicKeyString!!.toByteArray(StandardCharsets.UTF_8))
        val pgpPubRingCollection = PGPPublicKeyRingCollection(PGPUtil.getDecoderStream(publicKeyStream), JcaKeyFingerprintCalculator())
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
            JcePGPDataEncryptorBuilder(PGPEncryptedData.AES_256).setWithIntegrityPacket(true)
                .setSecureRandom(SecureRandom()).setProvider(BouncyCastleProvider.PROVIDER_NAME)
        )
        encryptedDataGenerator.addMethod(BcPublicKeyKeyEncryptionMethodGenerator(pgpPublicKey))

        this.encryptedDataGenerator = encryptedDataGenerator
    }

    fun encrypt(clearTextData: String): String {
        if (this.encryptedDataGenerator == null) {
            this.setEncryptedDataGenerator()
        }
        val bOut = ByteArrayOutputStream()
        val comData = PGPCompressedDataGenerator(CompressionAlgorithmTags.ZIP)
        val lData = PGPLiteralDataGenerator()
        val pOut = lData.open(comData.open(bOut), PGPLiteralData.UTF8, "noFile", clearTextData.length.toLong(), Date())
        pOut.write(clearTextData.toByteArray(StandardCharsets.UTF_8))
        pOut.close()
        comData.close()

        val encryptedOut = ByteArrayOutputStream()
        val out: OutputStream = ArmoredOutputStream(encryptedOut)
        val encryptedOutStream = encryptedDataGenerator!!.open(out, bOut.toByteArray().size.toLong())
        encryptedOutStream.write(bOut.toByteArray())
        encryptedOutStream.close()
        out.close()
        val encryptedData = encryptedOut.toString(StandardCharsets.UTF_8)
        return encryptedData
    }
}
