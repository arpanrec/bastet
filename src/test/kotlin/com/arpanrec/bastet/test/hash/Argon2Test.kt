package com.arpanrec.bastet.test.hash

import com.arpanrec.bastet.hash.Argon2
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class Argon2Test(@Autowired private val argon2: Argon2) {

    private val log = LoggerFactory.getLogger(Argon2Test::class.java)

    private val password = "password"
    private val hashedPassword = "argon2:w1NkfH5pC/tyKNLHc6Aw/9F2vMlQ8KUivOLfJaU9lnk="
    private val wrongHashedPassword = "argon2:w2NkfH5pC/tyKNLHc6Aw/9F2vMlQ8KUivOLfJaU9lnk="
    private val argon2Salt = "vQlm0mVHOcpEiEYI3GEYxw=="

    @BeforeEach
    fun loadPrivateKey() {
        argon2.setArgon2Salt(argon2Salt)
    }

    @Test
    fun testHashedPassword() {
        log.info("OLD Encoded Password: $hashedPassword")
        assert(argon2.matches(password, hashedPassword)) { "Old hashed password does not match" }
        log.info("Old encoded password matches")
    }

    @Test
    fun testNewHashedPassword() {
        val newEncodedPassword = argon2.encode(this.password)
        log.info("New Hashed Password: $newEncodedPassword")
        assert(argon2.matches(password, newEncodedPassword)) { "New encoded password does not match" }
        log.info("New hashed password matches")
    }

    @Test
    fun testNewHash() {
        val newHash = argon2.encode(this.password)
        log.info("New Hash: $newHash")
        assert(newHash != hashedPassword) { "New hash matches old hash" }
        log.info("New hash does not match old hash")
    }

    @Test
    fun wrongPasswordTest() {
        assert(!argon2.matches(password, wrongHashedPassword)) { "Wrong password matches" }
        log.info("Wrong password does not match")
    }
}
