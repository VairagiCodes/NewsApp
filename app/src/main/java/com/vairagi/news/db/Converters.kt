package com.vairagi.news.db

import androidx.room.TypeConverter
import com.vairagi.news.Source

class Converters {

    @TypeConverter
    fun fromSource(source: com.vairagi.news.Source): String {
        return source.name
    }

    @TypeConverter
    fun toSource(name: String): Source {
        return Source(name,name)
    }
}