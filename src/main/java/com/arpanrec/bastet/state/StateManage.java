package com.arpanrec.bastet.state;

import com.arpanrec.bastet.encryption.AES256CBC;
import com.arpanrec.bastet.hash.Argon2;
import com.arpanrec.bastet.physical.KVData;
import com.arpanrec.bastet.physical.KVDataServiceImpl;
import jakarta.annotation.PostConstruct;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

@Service
public class StateManage {

    private final AES256CBC aes256CBC;

    private final Argon2 argon2;

    private final ApplicationContext context;

    private KVDataServiceImpl kvDataService;

    @PostConstruct
    public void setKVDataService() {
        this.kvDataService = context.getBean(KVDataServiceImpl.class);
    }

    public StateManage(@Autowired AES256CBC aes256CBC, @Autowired Argon2 argon2,
                       @Autowired ApplicationContext context) {
        this.aes256CBC = aes256CBC;
        this.argon2 = argon2;
        this.context = context;
    }

    public boolean isAES256CBCCAvailable() {
        return kvDataService.has(AES256CBC.INTERNAL_AES_SECRET_KEY_PATH)
            && kvDataService.has(AES256CBC.INTERNAL_AES_IV_PATH);
    }

    public void unSeal(Map<String, String> secretKeyAndIV) {
        String userSecretKeyBase64 = secretKeyAndIV.get("userSecretKeyBase64");
        String userIVBase64 = secretKeyAndIV.get("userIVBase64");
        AES256CBC userAES256CBC = new AES256CBC();
        userAES256CBC.setSecretKeyAndIv(userSecretKeyBase64, userIVBase64);

        String internalSecretKeyBase64Encrypted =
            kvDataService.getRaw(AES256CBC.INTERNAL_AES_SECRET_KEY_PATH).getValue();
        String internalIVBase64Encrypted =
            kvDataService.getRaw(AES256CBC.INTERNAL_AES_IV_PATH).getValue();

        String internalSecretKeyBase64 = userAES256CBC.decrypt(internalSecretKeyBase64Encrypted);
        String internalIVBase64 = userAES256CBC.decrypt(internalIVBase64Encrypted);

        this.aes256CBC.setSecretKeyAndIv(internalSecretKeyBase64, internalIVBase64);
        String argon2SaltBase64Encrypted =
            kvDataService.getRaw(Argon2.INTERNAL_ARGON2_SALT_PATH).getValue();
        String argon2SaltBase64 = this.aes256CBC.decrypt(argon2SaltBase64Encrypted);
        this.argon2.setArgon2Base64Salt(argon2SaltBase64);
    }


    public Map<String, String> init() {
        if (isAES256CBCCAvailable()) {
            return Map.of("message", "AES256CBC already initialized");
        }
        String userSecretKeyBase64 = AES256CBC.Companion.generateAESKeyBase64();
        String userIVBase64 = AES256CBC.Companion.generateIVBase64();
        AES256CBC userAES256CBC = new AES256CBC();
        userAES256CBC.setSecretKeyAndIv(userSecretKeyBase64, userIVBase64);

        String internalSecretKeyBase64 = AES256CBC.Companion.generateAESKeyBase64();
        String internalIVBase64 = AES256CBC.Companion.generateIVBase64();

        String internalSecretKeyBase64Encrypted = userAES256CBC.encrypt(internalSecretKeyBase64);
        String internalIVBase64Encrypted = userAES256CBC.encrypt(internalIVBase64);

        KVData aesSecretKeyKVDataEncrypted = new KVData(AES256CBC.INTERNAL_AES_SECRET_KEY_PATH,
            internalSecretKeyBase64Encrypted, new HashMap<>());
        kvDataService.saveRaw(aesSecretKeyKVDataEncrypted);

        KVData aesIVKVData = new KVData(AES256CBC.INTERNAL_AES_IV_PATH,
            internalIVBase64Encrypted, new HashMap<>());
        kvDataService.saveRaw(aesIVKVData);

        AES256CBC internalAES256CBC = new AES256CBC();
        internalAES256CBC.setSecretKeyAndIv(internalSecretKeyBase64, internalIVBase64);

        var argon2SaltBase64 = Argon2.Companion.generateSalt16ByteBase64EncodedString();
        var argon2SaltBase64Encrypted = internalAES256CBC.encrypt(argon2SaltBase64);
        KVData argon2SaltKVData = new KVData(Argon2.INTERNAL_ARGON2_SALT_PATH,
            argon2SaltBase64Encrypted, new HashMap<>());
        kvDataService.saveRaw(argon2SaltKVData);

        return Map.of("message", "AES256CBC initialized",
            "userSecretKeyBase64", userSecretKeyBase64,
            "userIVBase64", userIVBase64);
    }
}
