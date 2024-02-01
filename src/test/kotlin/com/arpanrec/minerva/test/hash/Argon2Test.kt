package com.arpanrec.minerva.test.hash

import com.arpanrec.minerva.hash.Argon2
import org.junit.jupiter.api.Test
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class Argon2Test(@Autowired private val argon2: Argon2) {

    private val log: Logger = LoggerFactory.getLogger(Argon2Test::class.java)

    private val password: String = "password"

    private val encodedPassword: String = "DgJ+ivuTZPxlBzVU1TmM/1nFYvMyCKh2qGYdQdn+T0Q="

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
