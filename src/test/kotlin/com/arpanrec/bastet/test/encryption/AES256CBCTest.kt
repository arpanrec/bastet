package com.arpanrec.bastet.test.encryption

import com.arpanrec.bastet.encryption.AES256CBC
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest


@SpringBootTest
class AES256CBCTest(@Autowired private val aes256CBC: AES256CBC) {
    private val log = LoggerFactory.getLogger(this.javaClass)

    private val secretKeyBase64 = "5jcK7IMk3+QbNLikFRl3Zwgl9xagKD87s5dT2UqaSR4="
    private val ivBase64 = "5jcK7IMk3+QbNLikFRl3Zw=="
    private val plainText = "Hello, World!"
    private val cipherTextBase64 = "yQp5HF92QfpV/jdmPIDYJQ=="

    @BeforeEach
    fun loadPrivateKey() {
        aes256CBC.setSecretKeyAndIv(secretKeyBase64, ivBase64)
    }

    @Test
    fun testEncrypt() {
        log.info("Encrypting plain text")
        val cipherText = aes256CBC.encrypt(plainText)
        log.info("Cipher text: $cipherText")
        assert(cipherText == cipherTextBase64) { "Cipher text does not match" }
    }

    @Test
    fun testDecrypt() {
        log.info("Decrypting cipher text")
        val decryptedText = aes256CBC.decrypt(cipherTextBase64)
        log.info("Decrypted text: $decryptedText")
        assert(decryptedText == plainText) { "Decrypted text does not match" }
    }
}