package com.arpanrec.minerva.pki

import com.arpanrec.minerva.exceptions.MinervaException
import com.arpanrec.minerva.utils.FileUtils
import org.bouncycastle.asn1.ASN1Encodable
import org.bouncycastle.asn1.DERSequence
import org.bouncycastle.asn1.x500.X500Name
import org.bouncycastle.asn1.x509.ExtendedKeyUsage
import org.bouncycastle.asn1.x509.Extension
import org.bouncycastle.asn1.x509.GeneralName
import org.bouncycastle.asn1.x509.KeyPurposeId
import org.bouncycastle.asn1.x509.KeyUsage
import org.bouncycastle.cert.X509v3CertificateBuilder
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter
import org.bouncycastle.cert.jcajce.JcaX509ExtensionUtils
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.bouncycastle.openssl.PEMParser
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter
import org.bouncycastle.openssl.jcajce.JcaPEMWriter
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
import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Component
import java.io.ByteArrayInputStream
import java.io.StringReader
import java.io.StringWriter
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


@Lazy
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

    fun csrBuilder(certificateProperties: CertificateProperties) {
        val keyPairGenerator: KeyPairGenerator = KeyPairGenerator.getInstance(
            "RSA", BouncyCastleProvider.PROVIDER_NAME
        )
        keyPairGenerator.initialize(certificateProperties.keySize)
        val issuedCertKeyPair: KeyPair = keyPairGenerator.generateKeyPair()

        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DATE, -1)
        val startDate: Date = calendar.time
        calendar.add(Calendar.DATE, certificateProperties.validTillDay)
        val endDate: Date = calendar.time

        // Issued By and Issued To same for root certificate
        val issuedCertSubject = X500Name(certificateProperties.rfc2253name)

        val p10Builder: PKCS10CertificationRequestBuilder =
            JcaPKCS10CertificationRequestBuilder(issuedCertSubject, issuedCertKeyPair.public)
        val csrBuilder: JcaContentSignerBuilder =
            JcaContentSignerBuilder("SHA256withRSA").setProvider(BouncyCastleProvider.PROVIDER_NAME)
        // Sign the new KeyPair with the root cert Private Key
        val csrContentSigner: ContentSigner = csrBuilder.build(this.rootCakeyPair.private)
        val csr: PKCS10CertificationRequest = p10Builder.build(csrContentSigner)

        val issuedCertBuilder = X509v3CertificateBuilder(
            X500Name(this.rootCax509Certificate.subjectX500Principal.name),
            BigInteger(SecureRandom().nextLong().toString()),
            startDate,
            endDate,
            csr.subject,
            csr.subjectPublicKeyInfo
        )

        val issuedCertExtUtils = JcaX509ExtensionUtils()

        if (certificateProperties.setBasicConstraints) {
            issuedCertBuilder.addExtension(
                Extension.basicConstraints, certificateProperties.basicConstraints.isCritical,
                certificateProperties.getBasicConstraints()
            )
        }

        if (certificateProperties.setAuthorityKeyIdentifier) {
            issuedCertBuilder.addExtension(
                Extension.authorityKeyIdentifier,
                certificateProperties.isAuthorityKeyIdentifierCritical,
                issuedCertExtUtils.createAuthorityKeyIdentifier(this.rootCax509Certificate)
            )
        }
        issuedCertBuilder.addExtension(
            Extension.subjectKeyIdentifier,
            false,
            issuedCertExtUtils.createSubjectKeyIdentifier(issuedCertKeyPair.public)
        )

        // Add intended key usage extension if needed
        issuedCertBuilder.addExtension(
            Extension.keyUsage,
            false,
            KeyUsage(KeyUsage.digitalSignature or KeyUsage.keyEncipherment)
        )

        issuedCertBuilder.addExtension(
            Extension.extendedKeyUsage,
            false,
            ExtendedKeyUsage(arrayOf(KeyPurposeId.id_kp_serverAuth, KeyPurposeId.id_kp_clientAuth))
        )


        // Add DNS name is cert is to used for SSL
        issuedCertBuilder.addExtension(
            Extension.subjectAlternativeName, false, DERSequence(
                arrayOf<ASN1Encodable>(
                    GeneralName(GeneralName.dNSName, "mydomain.local"),
                    GeneralName(GeneralName.iPAddress, "127.0.0.1")
                )
            )
        )

        val issuedCertHolder = issuedCertBuilder.build(csrContentSigner)
        val issuedCert: X509Certificate =
            JcaX509CertificateConverter().setProvider(BouncyCastleProvider.PROVIDER_NAME)
                .getCertificate(issuedCertHolder)

        issuedCert.verify(rootCax509Certificate.publicKey, BouncyCastleProvider.PROVIDER_NAME)
        val sw = StringWriter()
        JcaPEMWriter(sw).use { pw ->
            pw.writeObject(issuedCertKeyPair.public)
        }
        println(sw.toString())
    }
}
