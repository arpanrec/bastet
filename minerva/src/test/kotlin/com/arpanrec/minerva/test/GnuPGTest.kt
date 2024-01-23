package com.arpanrec.minerva.test

import com.arpanrec.minerva.gnupg.GnuPG
import org.bouncycastle.openpgp.PGPException
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.io.IOException

@SpringBootTest
class GpGTest {
    @Autowired
    private var gnuPG: GnuPG? = null

    @Test
    @Throws(IOException::class, PGPException::class)
    fun test() {
        println(gnuPG!!.encrypt("Hello World!"))
    }
}