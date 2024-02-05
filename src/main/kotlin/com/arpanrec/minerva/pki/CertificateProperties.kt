package com.arpanrec.minerva.pki

import com.fasterxml.jackson.annotation.JsonIgnore
import org.bouncycastle.asn1.x509.BasicConstraints

class CertificateProperties {

    lateinit var rfc2253name: String

    var validTillDay: Int = 7

    var keySize: Int = 2048

    var basicConstraints: BC = BC()

    var setBasicConstraints: Boolean = false

    var setAuthorityKeyIdentifier: Boolean = false

    var isAuthorityKeyIdentifierCritical: Boolean = false

    fun getBasicConstraints(): BasicConstraints {
        return if (this.basicConstraints.isCA) {
            BasicConstraints(this.basicConstraints.pathLen)
        } else {
            BasicConstraints(false)
        }
    }

    class BC {
        var isCA: Boolean = false
        var pathLen: Int = 0
        var isCritical: Boolean = false
    }
}
