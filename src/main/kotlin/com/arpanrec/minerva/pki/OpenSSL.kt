package com.arpanrec.minerva.pki

import com.arpanrec.minerva.exceptions.MinervaException
import com.arpanrec.minerva.utils.FileUtils
import org.bouncycastle.asn1.x500.X500Name
import org.bouncycastle.asn1.x509.BasicConstraints
import org.bouncycastle.asn1.x509.Extension
import org.bouncycastle.cert.X509v3CertificateBuilder
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder
import org.bouncycastle.cert.jcajce.JcaX509ExtensionUtils
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.bouncycastle.openssl.PEMParser
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter
import org.bouncycastle.openssl.jcajce.JceOpenSSLPKCS8DecryptorProviderBuilder
import org.bouncycastle.operator.ContentSigner
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder
import org.bouncycastle.pkcs.PKCS10CertificationRequest
import org.bouncycastle.pkcs.PKCS10CertificationRequestBuilder
import org.bouncycastle.pkcs.PKCS8EncryptedPrivateKeyInfo
import org.bouncycastle.pkcs.jcajce.JcaPKCS10CertificationRequestBuilder
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.io.ByteArrayInputStream
import java.io.StringReader
import java.math.BigInteger
import java.security.KeyFactory
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.PublicKey
import java.security.SecureRandom
import java.security.Security
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import java.security.interfaces.RSAPrivateCrtKey
import java.security.spec.RSAPublicKeySpec
import java.util.*


@Component
class OpenSSL(
    @Value("\${minerva.pki.openssl.ca-private-key}") caPrivateKey: String,
    @Value("\${minerva.pki.openssl.ca-private-key-passphrase}") caPrivateKeyPassphrase: String?,
    @Value("\${minerva.pki.openssl.ca-certificate}") caCertificate: String
) {

    private final val log: Logger = LoggerFactory.getLogger(OpenSSL::class.java)

    private final val rootCax509Certificate: X509Certificate

    private final val rootCakeyPair: KeyPair

    init {
        log.info("Adding BouncyCastle provider.")
        Security.addProvider(BouncyCastleProvider())
        this.rootCax509Certificate = loadCertificate(caCertificate)
        this.rootCakeyPair = loadPrivateKey(caPrivateKey, caPrivateKeyPassphrase)
    }

    fun getRootCaCertificate(): X509Certificate {
        return this.rootCax509Certificate
    }

    fun getRootCaKeyPair(): KeyPair {
        return this.rootCakeyPair
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

    fun csrBuilder() {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DATE, -1)
        val startDate: Date = calendar.time
        calendar.add(Calendar.YEAR, 1)
        val endDate: Date = calendar.time
        val keyPairGenerator: KeyPairGenerator = KeyPairGenerator.getInstance("RSA", BouncyCastleProvider.PROVIDER_NAME)
        // Issued By and Issued To same for root certificate
        val issuedCertSubject = X500Name("CN=issued-cert")
        val issuedCertSerialNum = BigInteger(SecureRandom().nextLong().toString())
        val issuedCertKeyPair: KeyPair = keyPairGenerator.generateKeyPair()
        val p10Builder: PKCS10CertificationRequestBuilder =
            JcaPKCS10CertificationRequestBuilder(issuedCertSubject, issuedCertKeyPair.public)
        val csrBuilder: JcaContentSignerBuilder =
            JcaContentSignerBuilder("RSA").setProvider(BouncyCastleProvider.PROVIDER_NAME)
        // Sign the new KeyPair with the root cert Private Key
        val csrContentSigner: ContentSigner = csrBuilder.build(this.rootCakeyPair.private)
        val csr: PKCS10CertificationRequest = p10Builder.build(csrContentSigner)

        val issuedCertBuilder = X509v3CertificateBuilder(
            X500Name(this.rootCax509Certificate.subjectX500Principal.name),
            issuedCertSerialNum,
            startDate,
            endDate,
            csr.subject,
            csr.subjectPublicKeyInfo
        )
        val issuedCertExtUtils = JcaX509ExtensionUtils()
        // Add Extensions
        // Use BasicConstraints to say that this Cert is not a CA
        issuedCertBuilder.addExtension(Extension.basicConstraints, true, BasicConstraints(false))
    }
}
