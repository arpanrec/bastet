package com.arpanrec.minerva.hash

import com.arpanrec.minerva.physical.KeyValue
import com.arpanrec.minerva.physical.KeyValuePersistence
import org.bouncycastle.crypto.generators.Argon2BytesGenerator
import org.bouncycastle.crypto.params.Argon2Parameters
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.security.SecureRandom
import java.util.Base64

@Component
class Argon2(@Autowired keyValuePersistence: KeyValuePersistence) {

    private val log: Logger = LoggerFactory.getLogger(Argon2::class.java)

    private var argon2Salt: ByteArray? = null

    private var internalArgon2SaltPath: String? = null

    private fun generateSalt16ByteBase64EncodedString(): String {
        val secureRandom = SecureRandom()
        val salt = ByteArray(16)
        secureRandom.nextBytes(salt)
        return Base64.getEncoder().encodeToString(salt)
    }

    private final fun checkArgon2Salt(keyValuePersistence: KeyValuePersistence) {
        internalArgon2SaltPath = keyValuePersistence.internalStorageKey + "/argon2Salt"
        keyValuePersistence.get(internalArgon2SaltPath!!, 0).ifPresentOrElse({ kv: KeyValue ->
            log.info("Argon2 salt already exists")
            argon2Salt = Base64.getDecoder().decode(kv.value)
        }, {
            try {
                val salt = generateSalt16ByteBase64EncodedString()
                val keyValue = KeyValue()
                keyValue.key = internalArgon2SaltPath
                keyValue.value = salt
                keyValuePersistence.save(keyValue)
                log.info("Argon2 salt created")
                argon2Salt = Base64.getDecoder().decode(salt)
            } catch (e: Exception) {
                throw RuntimeException("Error while creating argon2 salt", e)
            }
        })
    }

    init {
        checkArgon2Salt(keyValuePersistence)
    }

    fun hashString(inputString: String): String {
        val iterations = 2
        val memLimit = 66536
        val hashLength = 32
        val parallelism = 1
        val builder = Argon2Parameters.Builder(Argon2Parameters.ARGON2_id).withVersion(Argon2Parameters.ARGON2_VERSION_13).withIterations(iterations).withMemoryAsKB(memLimit)
            .withParallelism(parallelism).withSalt(argon2Salt)

        val generate = Argon2BytesGenerator()
        generate.init(builder.build())
        val result = ByteArray(hashLength)
        generate.generateBytes(inputString.toByteArray(Charsets.UTF_8), result, 0, result.size)
        return Base64.getEncoder().encodeToString(result)
    }
}
