package com.arpanrec.bastet.state;

import com.arpanrec.bastet.encryption.AES256CBC;
import com.arpanrec.bastet.encryption.GnuPG;
import com.arpanrec.bastet.hash.Argon2;
import com.arpanrec.bastet.physical.KVDataService;
import com.arpanrec.bastet.physical.KVDataServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class State {

    private final GnuPG gnuPG;

    private final AES256CBC aes256CBC;

    private final Argon2 argon2;

    private final KVDataService kvDataService;

    public State(@Autowired GnuPG gnuPG,
                 @Autowired AES256CBC aes256CBC,
                 @Autowired Argon2 argon2, @Autowired KVDataServiceImpl kvDataServiceImpl) {
        this.gnuPG = gnuPG;
        this.aes256CBC = aes256CBC;
        this.argon2 = argon2;
        this.kvDataService = kvDataServiceImpl;
    }

    public boolean isGnuPGAvailable() {
        var hasPrivateKeyPassphrase = kvDataService.has(GnuPG.INTERNAL_GNUPG_PRIVATE_KEY_PASSPHRASE_PATH);
        var hasPrivateKey = kvDataService.has(GnuPG.INTERNAL_GNUPG_PRIVATE_KEY_PATH);
        return hasPrivateKeyPassphrase && hasPrivateKey;
    }

    public boolean isAES256Available() {
        var hasKey = kvDataService.has(AES256CBC.INTERNAL_KEY_PATH);
        var hasIV = kvDataService.has(AES256CBC.INTERNAL_IV_PATH);
        return hasKey && hasIV;
    }

}
