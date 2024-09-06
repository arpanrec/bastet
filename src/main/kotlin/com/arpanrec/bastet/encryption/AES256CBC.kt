package com.arpanrec.bastet.encryption

import com.arpanrec.bastet.physical.NameSpace
import org.springframework.beans.factory.annotation.Autowired
import java.security.Key
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.spec.IvParameterSpec
import java.util.Base64

class AES256CBC(
    @Autowired private val gnuPG: GnuPG
) {
    private val internalKeyPath: String = NameSpace.INTERNAL_AES256CBC + "/key"
    private val internalIVPath: String = NameSpace.INTERNAL_AES256CBC + "/iv"
    private lateinit var secretKey: Key
    private lateinit var iv: IvParameterSpec

    init {

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