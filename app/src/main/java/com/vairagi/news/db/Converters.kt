package com.vairagi.news.db

import androidx.room.TypeConverter
import com.vairagi.news.model.Source

class Converters {

    @TypeConverter
    fun fromSource(source: Source): String {
        return if(source.name!=null) {
            source.name!!
        } else {
            "abc"
        }

    }

    @TypeConverter
    fun toSource(name: String): Source {
        return Source(name,name)
    }
}