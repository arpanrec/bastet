package com.arpanrec.bastet.state;

import com.arpanrec.bastet.encryption.AES256CBC;
import com.arpanrec.bastet.encryption.gpg.GnuPG;
import com.arpanrec.bastet.hash.Argon2;
import com.arpanrec.bastet.physical.KVData;
import com.arpanrec.bastet.physical.KVDataService;
import com.arpanrec.bastet.physical.KVDataServiceImpl;
import java.util.HashMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StateManage {

    private final AES256CBC aes256CBC;

    private final Argon2 argon2;

    private final KVDataService kvDataService;

    public StateManage(@Autowired AES256CBC aes256CBC, @Autowired Argon2 argon2,
                       @Autowired KVDataServiceImpl kvDataServiceImpl) {
        this.aes256CBC = aes256CBC;
        this.argon2 = argon2;
        this.kvDataService = kvDataServiceImpl;
    }

    public boolean isArgon2Available() {
        return kvDataService.has(Argon2.INTERNAL_ARGON2_SALT_PATH);
    }

    public boolean isAES256CBCCAvailable() {
        return kvDataService.has(AES256CBC.INTERNAL_AES_SECRET_KEY_PATH)
            && kvDataService.has(AES256CBC.INTERNAL_AES_IV_PATH);
    }

    public void init() {
        if (!isArgon2Available()) {
            var argon2Salt = Argon2.Companion.generateSalt16ByteBase64EncodedString();
            KVData argon2SaltKVData = new KVData(Argon2.INTERNAL_ARGON2_SALT_PATH, argon2Salt, new HashMap<>());
            kvDataService.save(argon2SaltKVData);
        }
        if (!isAES256CBCCAvailable()) {
            KVData aesSecretKeyKVData = new KVData(AES256CBC.INTERNAL_AES_SECRET_KEY_PATH,
                AES256CBC.Companion.generateAESKey(), new HashMap<>());
        }
    }
}
