package com.pocketvibe.data.local

import androidx.room.TypeConverter

class Converters {
    @TypeConverter
    fun typeToString(t: TransactionType): String = t.name

    @TypeConverter
    fun stringToType(s: String): TransactionType = TransactionType.valueOf(s)

    @TypeConverter
    fun categoryToString(c: Category): String = c.name

    @TypeConverter
    fun stringToCategory(s: String): Category = Category.valueOf(s)

    @TypeConverter
    fun sourceToString(s: TxSource): String = s.name

    @TypeConverter
    fun stringToSource(s: String): TxSource = TxSource.valueOf(s)
}
