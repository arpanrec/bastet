package com.arpanrec.bastet.physical

class NameSpace {
    companion object {
        private const val INTERNAL = "/internal"
        const val INTERNAL_ARGON2 = "$INTERNAL/argon2"
        const val INTERNAL_TF_STATE = "$INTERNAL/tf_state"
        const val INTERNAL_USERS = "$INTERNAL/users"
        const val INTERNAL_AES_CBC = "$INTERNAL/aescbc"
    }
}