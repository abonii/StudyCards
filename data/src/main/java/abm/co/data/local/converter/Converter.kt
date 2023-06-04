package abm.co.data.local.converter

import abm.co.data.model.library.ChapterEntityDTO
import abm.co.data.model.library.ImageEntityDTO
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

object DataConverter {
    @TypeConverter
    fun fromChapterEntitiesToString(chapterEntities: List<ChapterEntityDTO>?): String? {
        if (chapterEntities == null) {
            return null
        }
        val gson = Gson()
        return gson.toJson(chapterEntities)
    }

    @TypeConverter
    fun fromStringToChapterEntity(string: String?): List<ChapterEntityDTO>? {
        if (string == null) {
            return null
        }
        val gson = Gson()
        val type: Type = object : TypeToken<List<ChapterEntityDTO?>?>() {}.type
        return gson.fromJson<List<ChapterEntityDTO>>(string, type)
    }

    @TypeConverter
    fun fromImageEntityToString(imageEntities: List<ImageEntityDTO>?): String? {
        if (imageEntities == null) {
            return null
        }
        val gson = Gson()
        return gson.toJson(imageEntities)
    }

    @TypeConverter
    fun fromStringToImageEntity(string: String?): List<ImageEntityDTO>? {
        if (string == null) {
            return null
        }
        val gson = Gson()
        val type: Type = object : TypeToken<List<ImageEntityDTO?>?>() {}.type
        return gson.fromJson<List<ImageEntityDTO>>(string, type)
    }

    @TypeConverter
    fun restoreList(listOfString: String?): List<String?>? {
        return Gson().fromJson(listOfString, object : TypeToken<List<String?>?>() {}.type)
    }

    @TypeConverter
    fun saveList(listOfString: List<String?>?): String? {
        return Gson().toJson(listOfString)
    }
}