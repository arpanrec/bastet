package com.arpanrec.minerva.gnupg

import com.arpanrec.minerva.exceptions.MinervaException
import org.bouncycastle.bcpg.ArmoredInputStream
import org.bouncycastle.bcpg.ArmoredOutputStream
import org.bouncycastle.bcpg.CompressionAlgorithmTags
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.bouncycastle.openpgp.PGPCompressedData
import org.bouncycastle.openpgp.PGPCompressedDataGenerator
import org.bouncycastle.openpgp.PGPEncryptedData
import org.bouncycastle.openpgp.PGPEncryptedDataGenerator
import org.bouncycastle.openpgp.PGPEncryptedDataList
import org.bouncycastle.openpgp.PGPException
import org.bouncycastle.openpgp.PGPLiteralData
import org.bouncycastle.openpgp.PGPLiteralDataGenerator
import org.bouncycastle.openpgp.PGPObjectFactory
import org.bouncycastle.openpgp.PGPOnePassSignatureList
import org.bouncycastle.openpgp.PGPPrivateKey
import org.bouncycastle.openpgp.PGPPublicKey
import org.bouncycastle.openpgp.PGPPublicKeyEncryptedData
import org.bouncycastle.openpgp.PGPPublicKeyRingCollection
import org.bouncycastle.openpgp.PGPSecretKeyRingCollection
import org.bouncycastle.openpgp.PGPUtil
import org.bouncycastle.openpgp.operator.bc.BcPublicKeyKeyEncryptionMethodGenerator
import org.bouncycastle.openpgp.operator.jcajce.JcaKeyFingerprintCalculator
import org.bouncycastle.openpgp.operator.jcajce.JcePBESecretKeyDecryptorBuilder
import org.bouncycastle.openpgp.operator.jcajce.JcePGPDataEncryptorBuilder
import org.bouncycastle.openpgp.operator.jcajce.JcePublicKeyDataDecryptorFactoryBuilder
import org.slf4j.LoggerFactory
import org.springframework.boot.context.properties.ConfigurationProperties
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.nio.charset.StandardCharsets
import java.security.SecureRandom
import java.security.Security
import java.util.*

@ConfigurationProperties(prefix = "minerva.gnupg")
class GnuPG(armoredPublicKey: String, armoredPrivateKey: String, privateKeyPassphrase: String?) {

    private val log = LoggerFactory.getLogger(this.javaClass)
    private val pgpPrivateKey: PGPPrivateKey
    private val encryptedDataGenerator: PGPEncryptedDataGenerator

    init {
        log.info("Adding BouncyCastle provider.")
        Security.addProvider(BouncyCastleProvider())
        log.info("Loading GPG armored public key.")
        val gpgPublicKey = this.loadGpgPublicKeyFromArmoredString(armoredPublicKey)
        log.info("Creating encrypted data generator.")
        this.encryptedDataGenerator = this.createEncryptedDataGenerator(gpgPublicKey)
        log.info("Loading GPG armored private key.")
        this.pgpPrivateKey = this.loadGpgPrivateKeyFromArmoredString(armoredPrivateKey, privateKeyPassphrase)
    }


    private fun loadGpgPublicKeyFromArmoredString(armoredPublicKey: String): PGPPublicKey {
        val publicKeyStream: InputStream = ByteArrayInputStream(armoredPublicKey.toByteArray(StandardCharsets.UTF_8))
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
        log.info("Public key loaded.")
        return pgpPublicKey
    }

    private fun createEncryptedDataGenerator(gpgPublicKey: PGPPublicKey): PGPEncryptedDataGenerator {
        val encryptedDataGenerator = PGPEncryptedDataGenerator(
            JcePGPDataEncryptorBuilder(PGPEncryptedData.AES_256).setWithIntegrityPacket(true).setSecureRandom(SecureRandom()).setProvider(BouncyCastleProvider.PROVIDER_NAME)
        )
        encryptedDataGenerator.addMethod(BcPublicKeyKeyEncryptionMethodGenerator(gpgPublicKey))
        log.info("Encrypted data generator created with AES-256.")
        return encryptedDataGenerator
    }

    fun encrypt(clearTextData: String): String {
        val clearTextDataByteOutputStream = ByteArrayOutputStream()
        val gpgCompressedDataGenerator = PGPCompressedDataGenerator(CompressionAlgorithmTags.ZIP)
        val gpgLiteralDataGenerator = PGPLiteralDataGenerator()
        val pOut: OutputStream = gpgLiteralDataGenerator.open(
            gpgCompressedDataGenerator.open(clearTextDataByteOutputStream), PGPLiteralData.UTF8, GnuPG::class.java.canonicalName, clearTextData.length.toLong(), Date()
        )
        pOut.write(clearTextData.toByteArray(StandardCharsets.UTF_8))
        pOut.close()
        gpgCompressedDataGenerator.close()

        val encryptedOut = ByteArrayOutputStream()
        val out: OutputStream = ArmoredOutputStream(encryptedOut)
        val encryptedOutStream = encryptedDataGenerator.open(out, clearTextDataByteOutputStream.toByteArray().size.toLong())
        encryptedOutStream.write(clearTextDataByteOutputStream.toByteArray())
        encryptedOutStream.close()
        out.close()
        val encryptedData = encryptedOut.toString(StandardCharsets.UTF_8)
        log.debug("Encrypted data: {}", encryptedData)
        return encryptedData
    }

    private fun loadGpgPrivateKeyFromArmoredString(armoredPrivateKey: String, privateKeyPassphrase: String?): PGPPrivateKey {
        val armoredPrivateKeyInputStreamStream: InputStream = ArmoredInputStream(ByteArrayInputStream(armoredPrivateKey.toByteArray(StandardCharsets.UTF_8)))
        var privateKeyPassphraseCharArray: CharArray? = null

        if (privateKeyPassphrase != null) {
            privateKeyPassphraseCharArray = privateKeyPassphrase.toCharArray()
        }

        val pgpSec = PGPSecretKeyRingCollection(PGPUtil.getDecoderStream(armoredPrivateKeyInputStreamStream), JcaKeyFingerprintCalculator())
        var pgpPrivateKey: PGPPrivateKey? = null
        val keyRingIter = pgpSec.keyRings
        while (keyRingIter.hasNext()) {
            val keyRing = keyRingIter.next()
            val keyIter = keyRing.secretKeys
            while (keyIter.hasNext()) {
                val key = keyIter.next()

                if (key.isSigningKey) continue
                val privateKey = key.extractPrivateKey(
                    JcePBESecretKeyDecryptorBuilder().setProvider(
                        BouncyCastleProvider.PROVIDER_NAME
                    ).build(privateKeyPassphraseCharArray)
                )

                if (privateKey != null) {
                    pgpPrivateKey = privateKey
                    break
                }
            }

            if (pgpPrivateKey != null) {
                break
            }
        }

        requireNotNull(pgpPrivateKey) { "No private key found." }
        log.info("Private key loaded.")
        return pgpPrivateKey
    }

    fun decrypt(encryptedArmoredData: String): String {

        log.debug("Decrypting data: {}", encryptedArmoredData)

        val encryptedDataStream: InputStream = ArmoredInputStream(ByteArrayInputStream(encryptedArmoredData.toByteArray(StandardCharsets.US_ASCII)))

        var publicKeyEncryptedData: PGPPublicKeyEncryptedData? = null

        val pgpObjectFactory = PGPObjectFactory(
            PGPUtil.getDecoderStream(encryptedDataStream), JcaKeyFingerprintCalculator()
        )
        val o = pgpObjectFactory.nextObject()

        val encryptedDataList = if (o is PGPEncryptedDataList) {
            o
        } else {
            pgpObjectFactory.nextObject() as PGPEncryptedDataList
        }

        val it = encryptedDataList.encryptedDataObjects
        while (it.hasNext()) {
            val data = it.next()

            if (data is PGPPublicKeyEncryptedData) {
                publicKeyEncryptedData = data
                break
            }
        }

        requireNotNull(publicKeyEncryptedData) { "No encrypted data found." }

        val clear = publicKeyEncryptedData.getDataStream(
            JcePublicKeyDataDecryptorFactoryBuilder().setProvider(BouncyCastleProvider.PROVIDER_NAME).build(this.pgpPrivateKey)
        )
        val plainFact = PGPObjectFactory(clear, JcaKeyFingerprintCalculator())
        var message: Any = plainFact.nextObject()


        if (message is PGPCompressedData) {
            val pgpFact = PGPObjectFactory(message.dataStream, JcaKeyFingerprintCalculator())
            message = pgpFact.nextObject()
        }

        if (message is PGPLiteralData) {
            val unc: InputStream = message.inputStream
            try {
                ByteArrayOutputStream().use { out ->
                    var ch: Int
                    while ((unc.read().also { ch = it }) >= 0) {
                        out.write(ch)
                    }
                    val decryptedData = out.toString()
                    return decryptedData
                }
            } catch (e: Exception) {
                throw PGPException("Failed to decrypt message", e)
            }
        } else if (message is PGPOnePassSignatureList) {
            throw MinervaException("Encrypted message contains a signed message - not literal data.")
        } else {
            throw MinervaException("Message is not a simple encrypted file - type unknown.")
        }
    }
}
