package com.arpanrec.minerva.physical

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "minerva.storage.file")
class FileStorage(val path: String) : Storage {

    override fun save(key: String, value: String): Storage {
        TODO()
    }

    override fun update(key: String, value: String): Storage {
        TODO()
    }

    override fun get(key: String): String {
        TODO()
    }

    override fun delete(key: String): Storage {
        TODO()
    }
}