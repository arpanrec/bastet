package com.arpanrec.minerva.test

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class GnuPGTest {

    @Test
    fun testGnuPG() {
        val publicKey = "-----BEGIN PGP PUBLIC KEY BLOCK-----\n" +
                "\n" +
                "mDMEZa70ABYJKwYBBAHaRw8BAQdAqc7Zfp6aQyefH7FWJOHWGyKSwIZe2L9e+pVm\n" +
                "umnaeIy0LG1pbmVydmEtdGVzdCA8bWluZXJ2YS10ZXN0QG1pbmVydmEtdGVzdC5j\n" +
                "b20+iJkEExYKAEEWIQR/tX7WJd7W0WbxhY/rY/cBPZC6AAUCZa70AAIbAwUJBaOa\n" +
                "gAULCQgHAgIiAgYVCgkICwIEFgIDAQIeBwIXgAAKCRDrY/cBPZC6AP0OAQCnWQJg\n" +
                "EDHdRIugMORCBLo9i7gTnTNgV3ov9n3h+yMPmgD9HN29m9o0OIQbDyFCqE4jwbU0\n" +
                "UDB6/aX2dgXgwF0xagC4OARlrvQAEgorBgEEAZdVAQUBAQdAemolmqy4kFq5iEtF\n" +
                "ZyEELwf73OY7DcWFK5NIv0xCz18DAQgHiH4EGBYKACYWIQR/tX7WJd7W0WbxhY/r\n" +
                "Y/cBPZC6AAUCZa70AAIbDAUJBaOagAAKCRDrY/cBPZC6AJ8eAP4qOtZ535X89wei\n" +
                "q3J5c9sV9jopcu6BJtXrXhk23W3fhgD/fAbW3Daqa2mNLLYSkLH06b6+tjpOxsd2\n" +
                "/aPa84R3hAw=\n" +
                "=kgso\n" +
                "-----END PGP PUBLIC KEY BLOCK-----"
        val privateKey = "-----BEGIN PGP PRIVATE KEY BLOCK-----\n" +
                "\n" +
                "lIYEZa70ABYJKwYBBAHaRw8BAQdAqc7Zfp6aQyefH7FWJOHWGyKSwIZe2L9e+pVm\n" +
                "umnaeIz+BwMCdNs9UHB91Gn/sq1FqE2sz9/ZguQjtGCOsmqjAUr5WJqGB2NE9RR4\n" +
                "2GgMEWy5UzDZzcO5ckHEZVuqE0HbAH/X2Farkr6ZBIDDWeLNs2CqBbQsbWluZXJ2\n" +
                "YS10ZXN0IDxtaW5lcnZhLXRlc3RAbWluZXJ2YS10ZXN0LmNvbT6ImQQTFgoAQRYh\n" +
                "BH+1ftYl3tbRZvGFj+tj9wE9kLoABQJlrvQAAhsDBQkFo5qABQsJCAcCAiICBhUK\n" +
                "CQgLAgQWAgMBAh4HAheAAAoJEOtj9wE9kLoA/Q4BAKdZAmAQMd1Ei6Aw5EIEuj2L\n" +
                "uBOdM2BXei/2feH7Iw+aAP0c3b2b2jQ4hBsPIUKoTiPBtTRQMHr9pfZ2BeDAXTFq\n" +
                "AJyLBGWu9AASCisGAQQBl1UBBQEBB0B6aiWarLiQWrmIS0VnIQQvB/vc5jsNxYUr\n" +
                "k0i/TELPXwMBCAf+BwMCizbuwMKSJ0n/reXH0dlE03diLm6k8irQt4aoEf5Fr3GO\n" +
                "uxzWPcrRJJqtwC/I5f4UplVmF3tz5/t2xahIr6nED8kzdk21qQSY5jmnIunMMIh+\n" +
                "BBgWCgAmFiEEf7V+1iXe1tFm8YWP62P3AT2QugAFAmWu9AACGwwFCQWjmoAACgkQ\n" +
                "62P3AT2QugCfHgD+KjrWed+V/PcHoqtyeXPbFfY6KXLugSbV614ZNt1t34YA/3wG\n" +
                "1tw2qmtpjSy2EpCx9Om+vrY6TsbHdv2j2vOEd4QM\n" +
                "=SQ4t\n" +
                "-----END PGP PRIVATE KEY BLOCK-----"
        val passphrase = "password"
        throw NotImplementedError()
    }

}