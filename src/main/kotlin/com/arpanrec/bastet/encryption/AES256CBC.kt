package com.arpanrec.bastet.encryption

import com.arpanrec.bastet.physical.NameSpace
import org.springframework.stereotype.Component
import java.security.Key
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.spec.IvParameterSpec
import java.util.Base64

@Component
class AES256CBC {

    companion object {
        const val INTERNAL_KEY_PATH: String = NameSpace.INTERNAL_AES256CBC + "/key"
        const val INTERNAL_IV_PATH: String = NameSpace.INTERNAL_AES256CBC + "/iv"
    }

    private lateinit var secretKey: Key
    private lateinit var iv: IvParameterSpec

    fun setSecretKey(secretKey: Key) {
        this.secretKey = secretKey
    }

    fun setIV(iv: IvParameterSpec) {
        this.iv = iv
    }

    private fun generateAESKey(): Key {
        val keyGen = KeyGenerator.getInstance("AES")
        keyGen.init(256) // 256-bit key
        return keyGen.generateKey()
    }

    private fun generateIV(): IvParameterSpec {
        val iv = ByteArray(16) // AES block size is 16 bytes
        SecureRandom().nextBytes(iv)
        return IvParameterSpec(iv)
    }

    fun encrypt(plainText: String): String {
        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv)
        val cipherText = cipher.doFinal(plainText.toByteArray())
        return Base64.getEncoder().encodeToString(cipherText)
    }

    fun decrypt(cipherText: String): String {
        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        cipher.init(Cipher.DECRYPT_MODE, secretKey, iv)
        val plainText = cipher.doFinal(Base64.getDecoder().decode(cipherText))
        return String(plainText)
    }
}