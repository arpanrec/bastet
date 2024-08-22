package com.arpanrec.minerva.hash

import com.arpanrec.minerva.physical.KVData
import com.arpanrec.minerva.physical.KeyValuePersistence
import com.arpanrec.minerva.utils.FileUtils
import org.bouncycastle.crypto.generators.Argon2BytesGenerator
import org.bouncycastle.crypto.params.Argon2Parameters
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component
import java.security.SecureRandom
import java.util.Base64

@Component
class Argon2(
    @Autowired keyValuePersistence: KeyValuePersistence,
    @Value("\${minerva.hash.argon2.bring-your-own-salt:#{null}}") bringYourOwnSalt: String?
) : PasswordEncoder {

    private val log: Logger = LoggerFactory.getLogger(Argon2::class.java)

    private val encoderPrefix = "argon2:"

    private var argon2Salt: ByteArray? = null

    private var internalArgon2SaltPath: String? = null

    private var characters: String = "abcdefghijklmnopqrstuvwxyz"

    private fun generateSalt16ByteBase64EncodedString(): String {
        val secureRandom = SecureRandom()
        val salt = ByteArray(32)
        secureRandom.nextBytes(salt)
        return Base64.getEncoder().encodeToString(salt)
    }

    private final fun checkArgon2Salt(keyValuePersistence: KeyValuePersistence, bringYourOwnSalt: String?) {
        internalArgon2SaltPath = keyValuePersistence.internalStorageKey + "/argon2Salt"
        if (bringYourOwnSalt != null) {
            log.info("Using bring your own salt")
            argon2Salt = Base64.getDecoder().decode(FileUtils.fileOrString(bringYourOwnSalt))
            return
        }
        keyValuePersistence.get(internalArgon2SaltPath!!, 0).ifPresentOrElse({ kv: KVData ->
            log.info("Argon2 salt already exists")
            argon2Salt = Base64.getDecoder().decode(kv.value)
        }, {
            try {
                val saltString: String = generateSalt16ByteBase64EncodedString()
                val keyValue = KVData(saltString, mapOf("created" to System.currentTimeMillis().toString()))
                keyValuePersistence.save(internalArgon2SaltPath!!, keyValue)
                log.info("Argon2 salt created")
                argon2Salt = Base64.getDecoder().decode(saltString)
            } catch (e: Exception) {
                throw RuntimeException("Error while creating argon2 salt", e)
            }
        })
    }

    init {
        checkArgon2Salt(keyValuePersistence, bringYourOwnSalt)
    }

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
