package com.arpanrec.bastet.physical

class NameSpace {
    companion object {
        private const val INTERNAL = "internal/"
        const val ARGON2_SALT_KEY = INTERNAL + "argon2/salt"
        const val TF_STATE_KEY = INTERNAL + "tfstate"
        const val USERS = INTERNAL + "users"
    }
}