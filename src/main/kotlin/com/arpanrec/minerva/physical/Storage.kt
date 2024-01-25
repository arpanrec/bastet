package com.arpanrec.minerva.physical

interface Storage {

    fun save(key: String, value: String): Storage
    fun update(key: String, value: String): Storage
    fun get(key: String): String
    fun delete(key: String): Storage

}