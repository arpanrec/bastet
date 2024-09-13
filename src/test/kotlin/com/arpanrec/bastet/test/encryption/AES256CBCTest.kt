package com.arpanrec.bastet.test.encryption

import com.arpanrec.bastet.encryption.AES256CBC
import com.arpanrec.bastet.encryption.Encryption
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory

class AES256CBCTest {
    private val log = LoggerFactory.getLogger(this.javaClass)
    private var encryption: Encryption? = null
    private val secretKeyBase64 = "5jcK7IMk3+QbNLikFRl3Zwgl9xagKD87s5dT2UqaSR4="
    private val ivBase64 = "5jcK7IMk3+QbNLikFRl3Zw=="
    private val plainText = "Hello, World!"
    private val cipherTextBase64 = "yQp5HF92QfpV/jdmPIDYJQ=="

    @BeforeEach
    fun loadPrivateKey() {
        val enc = AES256CBC()
        enc.setSecretKeyAndIv(secretKeyBase64, ivBase64)
        encryption = enc
    }

    @Test
    fun testEncrypt() {
        log.info("Encrypting plain text")
        val cipherText = encryption!!.encrypt(plainText)
        log.info("Cipher text: $cipherText")
        assert(cipherText == cipherTextBase64) { "Cipher text does not match" }
    }

    @Test
    fun testDecrypt() {
        log.info("Decrypting cipher text")
        val decryptedText = encryption!!.decrypt(cipherTextBase64)
        log.info("Decrypted text: $decryptedText")
        assert(decryptedText == plainText) { "Decrypted text does not match" }
    }
}