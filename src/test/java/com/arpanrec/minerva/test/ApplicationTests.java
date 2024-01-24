package com.arpanrec.minerva.test;

import org.bouncycastle.bcpg.ArmoredInputStream;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openpgp.PGPCompressedData;
import org.bouncycastle.openpgp.PGPEncryptedData;
import org.bouncycastle.openpgp.PGPEncryptedDataList;
import org.bouncycastle.openpgp.PGPException;
import org.bouncycastle.openpgp.PGPLiteralData;
import org.bouncycastle.openpgp.PGPObjectFactory;
import org.bouncycastle.openpgp.PGPOnePassSignatureList;
import org.bouncycastle.openpgp.PGPPrivateKey;
import org.bouncycastle.openpgp.PGPPublicKeyEncryptedData;
import org.bouncycastle.openpgp.PGPSecretKey;
import org.bouncycastle.openpgp.PGPSecretKeyRing;
import org.bouncycastle.openpgp.PGPSecretKeyRingCollection;
import org.bouncycastle.openpgp.PGPUtil;
import org.bouncycastle.openpgp.operator.jcajce.JcaKeyFingerprintCalculator;
import org.bouncycastle.openpgp.operator.jcajce.JcePBESecretKeyDecryptorBuilder;
import org.bouncycastle.openpgp.operator.jcajce.JcePublicKeyDataDecryptorFactoryBuilder;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.Security;
import java.util.Iterator;

@SpringBootTest
class ApplicationTests {

    private static final Logger log = org.slf4j.LoggerFactory.getLogger(ApplicationTests.class);

    @Test
    void testGpg() throws Exception {
        Security.addProvider(new BouncyCastleProvider());
        String privateKeyString = """
                -----BEGIN PGP PRIVATE KEY BLOCK-----

                lIYEZa70ABYJKwYBBAHaRw8BAQdAqc7Zfp6aQyefH7FWJOHWGyKSwIZe2L9e+pVm
                umnaeIz+BwMCdNs9UHB91Gn/sq1FqE2sz9/ZguQjtGCOsmqjAUr5WJqGB2NE9RR4
                2GgMEWy5UzDZzcO5ckHEZVuqE0HbAH/X2Farkr6ZBIDDWeLNs2CqBbQsbWluZXJ2
                YS10ZXN0IDxtaW5lcnZhLXRlc3RAbWluZXJ2YS10ZXN0LmNvbT6ImQQTFgoAQRYh
                BH+1ftYl3tbRZvGFj+tj9wE9kLoABQJlrvQAAhsDBQkFo5qABQsJCAcCAiICBhUK
                CQgLAgQWAgMBAh4HAheAAAoJEOtj9wE9kLoA/Q4BAKdZAmAQMd1Ei6Aw5EIEuj2L
                uBOdM2BXei/2feH7Iw+aAP0c3b2b2jQ4hBsPIUKoTiPBtTRQMHr9pfZ2BeDAXTFq
                AJyLBGWu9AASCisGAQQBl1UBBQEBB0B6aiWarLiQWrmIS0VnIQQvB/vc5jsNxYUr
                k0i/TELPXwMBCAf+BwMCizbuwMKSJ0n/reXH0dlE03diLm6k8irQt4aoEf5Fr3GO
                uxzWPcrRJJqtwC/I5f4UplVmF3tz5/t2xahIr6nED8kzdk21qQSY5jmnIunMMIh+
                BBgWCgAmFiEEf7V+1iXe1tFm8YWP62P3AT2QugAFAmWu9AACGwwFCQWjmoAACgkQ
                62P3AT2QugCfHgD+KjrWed+V/PcHoqtyeXPbFfY6KXLugSbV614ZNt1t34YA/3wG
                1tw2qmtpjSy2EpCx9Om+vrY6TsbHdv2j2vOEd4QM
                =SQ4t
                -----END PGP PRIVATE KEY BLOCK-----
                """;
        String passphraseString = "password";
        String encryptedDataString = """
                -----BEGIN PGP MESSAGE-----
                Version: BCPG v1.77.00

                wV4DubSNXBIhRMMSAQdA7ywEj5XiEqzfjKfYBw2sCoUAexrM0/Cxos8fftzC0k4w
                EsYhtv+siMLVZydNjzBCazkQN0c8VG42+8+Mr0BuXQl3wIdKD5CAgbeOh2UuZlc0
                0kEBToPuek9wysc70FA8woybdSAzfKDbFTWVWMvB4MwDN/s5/e+CzHGmI/vF96ZP
                b18r34wN8L7Ej9P5e//J1EbGOQ==
                =SeUg
                -----END PGP MESSAGE-----

                """;

        // Convert strings to streams
        InputStream privateKeyStream =
                new ByteArrayInputStream(privateKeyString.getBytes(StandardCharsets.US_ASCII));
        InputStream encryptedDataStream = new ArmoredInputStream(
                new ByteArrayInputStream(encryptedDataString.getBytes(StandardCharsets.US_ASCII)));
        decrypt(privateKeyStream, passphraseString.toCharArray(), encryptedDataStream);
    }

    void decrypt(InputStream privateKeyStream, char[] passphrase, InputStream encryptedDataStream)
            throws Exception {
        PGPSecretKeyRingCollection pgpSec = new PGPSecretKeyRingCollection(
                PGPUtil.getDecoderStream(privateKeyStream), new JcaKeyFingerprintCalculator());

        PGPPrivateKey pgpPrivateKey = null;
        PGPPublicKeyEncryptedData publicKeyEncryptedData = null;

        Iterator<PGPSecretKeyRing> keyRingIter = pgpSec.getKeyRings();
        while (keyRingIter.hasNext()) {
            PGPSecretKeyRing keyRing = keyRingIter.next();
            Iterator<PGPSecretKey> keyIter = keyRing.getSecretKeys();
            while (keyIter.hasNext()) {
                PGPSecretKey key = keyIter.next();

                if (key.isSigningKey())
                    continue;

                PGPPrivateKey privateKey =
                        key.extractPrivateKey(new JcePBESecretKeyDecryptorBuilder()
                                .setProvider(BouncyCastleProvider.PROVIDER_NAME).build(passphrase));
                if (privateKey != null) {
                    pgpPrivateKey = privateKey;
                    break;
                }
            }

            if (pgpPrivateKey != null) {
                break;
            }
        }

        if (pgpPrivateKey == null) {
            throw new IllegalArgumentException("No private key found.");
        }

        PGPObjectFactory pgpObjectFactory = new PGPObjectFactory(
                PGPUtil.getDecoderStream(encryptedDataStream), new JcaKeyFingerprintCalculator());
        Object o = pgpObjectFactory.nextObject();
        PGPEncryptedDataList encryptedDataList;

        if (o instanceof PGPEncryptedDataList) {
            encryptedDataList = (PGPEncryptedDataList) o;
        } else {
            encryptedDataList = (PGPEncryptedDataList) pgpObjectFactory.nextObject();
        }

        Iterator<PGPEncryptedData> it = encryptedDataList.getEncryptedDataObjects();
        while (it.hasNext()) {
            PGPEncryptedData data = it.next();

            if (data instanceof PGPPublicKeyEncryptedData) {
                publicKeyEncryptedData = (PGPPublicKeyEncryptedData) data;
                break;
            }
        }

        if (publicKeyEncryptedData == null) {
            throw new IllegalArgumentException("No encrypted data found.");
        }

        InputStream clear =
                publicKeyEncryptedData.getDataStream(new JcePublicKeyDataDecryptorFactoryBuilder()
                        .setProvider(BouncyCastleProvider.PROVIDER_NAME).build(pgpPrivateKey));
        PGPObjectFactory plainFact = new PGPObjectFactory(clear, new JcaKeyFingerprintCalculator());
        Object message = plainFact.nextObject();


        if (message instanceof PGPCompressedData cData) {
            PGPObjectFactory pgpFact =
                    new PGPObjectFactory(cData.getDataStream(), new JcaKeyFingerprintCalculator());

            message = pgpFact.nextObject();
        }

        if (message instanceof PGPLiteralData ld) {
            InputStream unc = ld.getInputStream();
            try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
                int ch;
                while ((ch = unc.read()) >= 0) {
                    out.write(ch);
                }
                String decryptedData = out.toString();
                log.info("Decrypted data: {}", decryptedData);

            } catch (Exception e) {
                throw new PGPException("Failed to decrypt message", e);
            }
        } else if (message instanceof PGPOnePassSignatureList) {
            throw new PGPException(
                    "Encrypted message contains a signed message - not literal data.");
        } else {
            throw new PGPException("Message is not a simple encrypted file - type unknown.");
        }
    }

}
