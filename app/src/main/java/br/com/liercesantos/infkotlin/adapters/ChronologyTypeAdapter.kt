package br.com.liercesantos.infkotlin.adapters

import com.google.gson.*
import org.joda.time.Chronology
import org.joda.time.DateTimeZone
import java.lang.reflect.Type

class ChronologyTypeAdapter : JsonSerializer<Chronology>, JsonDeserializer<Chronology> {
  override fun serialize(src: Chronology?, typeOfSrc: Type?, context: JsonSerializationContext?): JsonElement {
    return JsonPrimitive(src?.javaClass?.name)
  }

  override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): Chronology {
    val className = json?.asString ?: throw JsonParseException("Invalid chronology class name")
    return try {
      Class.forName(className).getDeclaredField("INSTANCE").get(null) as Chronology
    } catch (e: Exception) {
      throw JsonParseException("Invalid chronology class name: $className")
    }
  }
}