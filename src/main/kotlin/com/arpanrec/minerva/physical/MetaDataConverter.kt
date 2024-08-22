package com.arpanrec.minerva.physical

import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Converter
class MetaDataConverter : AttributeConverter<Map<String, String>, String> {
    override fun convertToDatabaseColumn(attribute: Map<String, String>): String {
        val json = Json.encodeToString(attribute)
        return json
    }

    override fun convertToEntityAttribute(dbData: String): Map<String, String> {
        return Json.decodeFromString(dbData)
    }
}