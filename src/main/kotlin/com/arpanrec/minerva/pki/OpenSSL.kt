package com.arpanrec.minerva.pki

import com.arpanrec.minerva.exceptions.MinervaException
import com.arpanrec.minerva.utils.FileUtils
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.bouncycastle.openssl.PEMParser
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter
import org.bouncycastle.openssl.jcajce.JceOpenSSLPKCS8DecryptorProviderBuilder
import org.bouncycastle.pkcs.PKCS8EncryptedPrivateKeyInfo
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.io.ByteArrayInputStream
import java.io.StringReader
import java.security.KeyFactory
import java.security.KeyPair
import java.security.PublicKey
import java.security.Security
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import java.security.interfaces.RSAPrivateCrtKey
import java.security.spec.RSAPublicKeySpec


@Component
class OpenSSL(
    @Value("\${minerva.pki.openssl.ca-private-key}") caPrivateKey: String,
    @Value("\${minerva.pki.openssl.ca-private-key-passphrase}") caPrivateKeyPassphrase: String?,
    @Value("\${minerva.pki.openssl.ca-certificate}") caCertificate: String
) {

    private final val log: Logger = LoggerFactory.getLogger(OpenSSL::class.java)

    private final val x509CertificateCa: X509Certificate

    private final val keyPair: KeyPair

    init {
        log.info("Adding BouncyCastle provider.")
        Security.addProvider(BouncyCastleProvider())
        this.x509CertificateCa = loadCertificate(caCertificate)
        this.keyPair = loadPrivateKey(caPrivateKey, caPrivateKeyPassphrase)
    }

    fun getX509CertificateCa(): X509Certificate {
        return this.x509CertificateCa
    }

    fun getKeyPair(): KeyPair {
        return this.keyPair
    }

    private final fun loadCertificate(caCertificate: String): X509Certificate {
        val caCertificateString: String = FileUtils.fileOrString(caCertificate)
        val inputStream = ByteArrayInputStream(caCertificateString.toByteArray())
        val certFactory = CertificateFactory.getInstance("X.509", BouncyCastleProvider.PROVIDER_NAME)
        val certificate = certFactory.generateCertificate(inputStream) as X509Certificate
        return certificate
    }

    private final fun loadPrivateKey(caPrivateKey: String, caPrivateKeyPassphrase: String?): KeyPair {

        val pemParser = PEMParser(StringReader(caPrivateKey))
        val objectPemParsed = pemParser.readObject()
        require(objectPemParsed is PKCS8EncryptedPrivateKeyInfo) { "Expected PKCS8EncryptedPrivateKeyInfo" }
        val decryptProvider = JceOpenSSLPKCS8DecryptorProviderBuilder().build(caPrivateKeyPassphrase?.toCharArray())
        val privateKeyInfo = objectPemParsed.decryptPrivateKeyInfo(decryptProvider)
        val converter = JcaPEMKeyConverter().setProvider("BC")
        val privateKey = converter.getPrivateKey(privateKeyInfo)
        val publicKey: PublicKey
        if (privateKey is RSAPrivateCrtKey) {
            val publicKeySpec = RSAPublicKeySpec(privateKey.modulus, privateKey.publicExponent)
            val keyFactory: KeyFactory = KeyFactory.getInstance("RSA")
            publicKey = keyFactory.generatePublic(publicKeySpec)
        } else {
            throw MinervaException("Expected RSAPrivateCrtKey")
        }
        return KeyPair(publicKey, privateKey)
    }
}
