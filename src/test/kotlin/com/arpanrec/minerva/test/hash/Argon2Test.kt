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

    private val oldEncodedPassword: String = "w1NkfH5pC/tyKNLHc6Aw/9F2vMlQ8KUivOLfJaU9lnk="

    @Test
    fun testOldEncodedPassword() {
        log.info("OLD Encoded password: $oldEncodedPassword")
        assert(argon2.matches(password, oldEncodedPassword)) { "Old encoded password does not match" }
        log.info("Old encoded password matches")
    }

    @Test
    fun testNewEncodedPassword() {
        val newEncodedPassword = argon2.encode(this.password)
        log.info("NEW Encoded password: $newEncodedPassword")
        assert(argon2.matches(password, newEncodedPassword)) { "New encoded password does not match" }
        log.info("New encoded password matches")
    }

    @Test
    fun wrongPasswordTest() {
        val wrongPassword = "wrongPassword"
        assert(!argon2.matches(wrongPassword, oldEncodedPassword))
        log.info("Wrong password does not match")
    }
}
