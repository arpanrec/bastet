package com.arpanrec.bastet.state;

import com.arpanrec.bastet.encryption.AES256CBC;
import com.arpanrec.bastet.encryption.gpg.GnuPG;
import com.arpanrec.bastet.hash.Argon2;
import com.arpanrec.bastet.physical.KVDataService;
import com.arpanrec.bastet.physical.KVDataServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StateManage {

    private final AES256CBC aes256CBC;

    private final Argon2 argon2;

    private final KVDataService kvDataService;

    public StateManage(@Autowired AES256CBC aes256CBC,
                       @Autowired Argon2 argon2,
                       @Autowired KVDataServiceImpl kvDataServiceImpl) {
        this.aes256CBC = aes256CBC;
        this.argon2 = argon2;
        this.kvDataService = kvDataServiceImpl;
    }

    public boolean isGnuPGAvailable() {
        var hasPrivateKeyPassphrase = kvDataService.has(GnuPG.INTERNAL_GNUPG_PRIVATE_KEY_PASSPHRASE_PATH);
        var hasPrivateKey = kvDataService.has(GnuPG.INTERNAL_GNUPG_PRIVATE_KEY_PATH);
        return hasPrivateKeyPassphrase && hasPrivateKey;
    }

    public boolean isArgon2Available() {
        return kvDataService.has(Argon2.INTERNAL_ARGON2_SALT_PATH);
    }

    public void init() {

    }
}
