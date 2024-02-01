package com.arpanrec.minerva.pki

import com.arpanrec.minerva.utils.FileUtils
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.io.ByteArrayInputStream
import java.security.Security
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate


@Component
class OpenSSL(
    @Value("\${minerva.pki.openssl.ca-private-key}") caPrivateKey: String,
    @Value("\${minerva.pki.openssl.ca-private-key-passphrase}") caPrivateKeyPassphrase: String?,
    @Value("\${minerva.pki.openssl.ca-certificate}") caCertificate: String
) {

    private final val log: Logger = LoggerFactory.getLogger(OpenSSL::class.java)

    private final val x509Certificate: X509Certificate

    init {
        log.info("Adding BouncyCastle provider.")
        Security.addProvider(BouncyCastleProvider())
        this.x509Certificate = loadCertificate(caCertificate)
    }

    fun getX509Certificate(): X509Certificate {
        return this.x509Certificate
    }

    private final fun loadCertificate(caCertificate: String): X509Certificate {
        val caCertificateString: String = FileUtils.fileOrString(caCertificate)
        val inputStream = ByteArrayInputStream(caCertificateString.toByteArray())
        val certFactory = CertificateFactory.getInstance("X.509", BouncyCastleProvider.PROVIDER_NAME)
        val certificate = certFactory.generateCertificate(inputStream) as X509Certificate
        return certificate
    }
}