package com.arpanrec.minerva.hash

import org.bouncycastle.crypto.generators.Argon2BytesGenerator
import org.bouncycastle.crypto.params.Argon2Parameters
import java.nio.charset.StandardCharsets
import java.security.SecureRandom


private fun generateSalt16Byte(): ByteArray {
    val secureRandom = SecureRandom()
    val salt = ByteArray(16)
    secureRandom.nextBytes(salt)
    return salt
}

fun hashString(password: String): String {
    val salt: ByteArray = generateSalt16Byte()
    val iterations = 2
    val memLimit = 66536
    val hashLength = 32
    val parallelism = 1

    val builder = Argon2Parameters.Builder(Argon2Parameters.ARGON2_id)
        .withVersion(Argon2Parameters.ARGON2_VERSION_13)
        .withIterations(iterations)
        .withMemoryAsKB(memLimit)
        .withParallelism(parallelism)
        .withSalt(salt)
    val generate = Argon2BytesGenerator()
    generate.init(builder.build())
    val result = ByteArray(hashLength)
    generate.generateBytes(password.toByteArray(StandardCharsets.UTF_8), result)
    return String(result)
}