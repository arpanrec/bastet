package com.arpanrec.bastet.hash

import com.arpanrec.bastet.exceptions.CaughtException
import com.arpanrec.bastet.physical.NameSpace
import org.bouncycastle.crypto.generators.Argon2BytesGenerator
import org.bouncycastle.crypto.params.Argon2Parameters
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component
import java.security.SecureRandom
import java.util.Base64

@Component
class Argon2 : PasswordEncoder {

    private val encoderPrefix = "argon2:"

    private var argon2Salt: ByteArray? = null

    fun setArgon2Salt(salt: String) {
        if (argon2Salt != null) {
            throw CaughtException("Argon2 salt already set")
        }
        argon2Salt = Base64.getDecoder().decode(salt)
    }

    companion object {
        const val INTERNAL_ARGON2_SALT_PATH = NameSpace.INTERNAL_ARGON2 + "/salt"
        fun generateSalt16ByteBase64EncodedString(): String {
            val secureRandom = SecureRandom()
            val salt = ByteArray(32)
            secureRandom.nextBytes(salt)
            return Base64.getEncoder().encodeToString(salt)
        }
    }

    private var characters = "abcdefghijklmnopqrstuvwxyz"

    fun hashString(inputString: String): String {
        val random = SecureRandom()
        val randomIndex = random.nextInt(characters.length)
        val randomChar = characters[randomIndex]
        return hashString(inputString, randomChar)
    }

    fun hashString(inputString: String, paper: Char): String {
        val inputStringWithPepper = inputString + paper
        val iterations = 2
        val memLimit = 66536
        val hashLength = 32
        val parallelism = 1
        val builder =
            Argon2Parameters.Builder(Argon2Parameters.ARGON2_id).withVersion(Argon2Parameters.ARGON2_VERSION_13)
                .withIterations(iterations).withMemoryAsKB(memLimit).withParallelism(parallelism).withSalt(argon2Salt)

        val generate = Argon2BytesGenerator()
        generate.init(builder.build())
        val result = ByteArray(hashLength)
        generate.generateBytes(inputStringWithPepper.toByteArray(Charsets.UTF_8), result, 0, result.size)
        return Base64.getEncoder().encodeToString(result)
    }

    override fun encode(rawPasswordChar: CharSequence?): String {
        val rawPassword: String = rawPasswordChar.toString()
        if (rawPassword.startsWith(encoderPrefix)) {
            return rawPassword
        }
        return encoderPrefix + hashString(rawPassword)
    }

    override fun matches(rawPassword: CharSequence, encodedPasswordWithPrefix: String): Boolean {
        if (!encodedPasswordWithPrefix.startsWith(encoderPrefix)) {
            return false
        }
        val encodedPassword = encodedPasswordWithPrefix.substring(encoderPrefix.length)
        for (c: Char in characters) {
            if (hashString(rawPassword.toString(), c) == encodedPassword) {
                return true
            }
        }
        return false
    }
}
