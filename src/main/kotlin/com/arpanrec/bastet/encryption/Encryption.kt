package com.arpanrec.bastet.encryption

interface Encryption {
    fun encrypt(plainText: String): String
    fun decrypt(encryptedText: String): String
}