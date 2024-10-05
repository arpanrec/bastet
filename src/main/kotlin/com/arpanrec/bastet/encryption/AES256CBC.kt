package com.arpanrec.bastet.encryption

import com.arpanrec.bastet.exceptions.CaughtException
import com.arpanrec.bastet.physical.NameSpace
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.security.Key
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.spec.IvParameterSpec
import java.util.Base64
import javax.crypto.spec.SecretKeySpec

@Component
class AES256CBC : Encryption {

    private val log = LoggerFactory.getLogger(this.javaClass)

    private var secretKey: Key? = null
    private var iv: IvParameterSpec? = null

    fun setSecretKeyAndIv(secretKeyBase64: String, ivBase64: String) {
        if (this.secretKey != null) {
            throw CaughtException("Secret key already exists")
        }
        log.info("Setting secret key")
        val decodedKey: ByteArray = Base64.getDecoder().decode(secretKeyBase64)
        this.secretKey = SecretKeySpec(decodedKey, 0, decodedKey.size, "AES")

        if (this.iv != null) {
            throw CaughtException("IV already exists")
        }
        log.info("Setting IV")
        val decodedIV: ByteArray = Base64.getDecoder().decode(ivBase64)
        this.iv = IvParameterSpec(decodedIV)
    }

    override fun encrypt(plainText: String): String {
        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv)
        val cipherText = cipher.doFinal(plainText.toByteArray())
        return Base64.getEncoder().encodeToString(cipherText)
    }

    override fun decrypt(encryptedText: String): String {
        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        cipher.init(Cipher.DECRYPT_MODE, secretKey, iv)
        val plainText = cipher.doFinal(Base64.getDecoder().decode(encryptedText))
        return String(plainText)
    }

    companion object {
        fun generateAESKey(): Key {
            val keyGen = KeyGenerator.getInstance("AES")
            keyGen.init(256) // 256-bit key
            return keyGen.generateKey()
        }

        fun generateIV(): IvParameterSpec {
            val iv = ByteArray(16) // AES block size is 16 bytes
            SecureRandom().nextBytes(iv)
            return IvParameterSpec(iv)
        }

        const val INTERNAL_AES_SECRET_KEY_PATH = NameSpace.INTERNAL_AES_CBC + "/secret_key"
        const val INTERNAL_AES_IV_PATH = NameSpace.INTERNAL_AES_CBC + "/iv"
    }
}