package com.arpanrec.minerva.test.hash

import com.arpanrec.minerva.hash.Argon2
import com.arpanrec.minerva.physical.KeyValuePersistence
import org.junit.jupiter.api.Test
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class Argon2Test(@Autowired keyValuePersistence: KeyValuePersistence) {

    private val log: Logger = LoggerFactory.getLogger(Argon2Test::class.java)

    private val password: String = "password"

    private val salt: String = "vQlm0mVHOcpEiEYI3GEYxw=="

    private val encodedPassword: String = "DgJ+ivuTZPxlBzVU1TmM/1nFYvMyCKh2qGYdQdn+T0Q="

    private val argon2: Argon2 = Argon2(keyValuePersistence, salt)

    @Test
    fun test() {
        val encodedPassword = argon2.encode(this.password)
        log.info("Encoded password: $encodedPassword")
        assert(argon2.matches(password, encodedPassword))
        log.info("Password matches")
    }

    @Test
    fun wrongPasswordTest() {
        val wrongPassword = "wrongPassword"
        assert(!argon2.matches(wrongPassword, encodedPassword))
        log.info("Wrong password does not match")
    }

    @Test
    fun idempotentTest() {
        val encodedNewPassword = argon2.encode(password)
        assert(encodedNewPassword == encodedPassword)
        log.info("Encoded password is idempotent")

    }

}