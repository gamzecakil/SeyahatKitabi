package com.gamzeuysal.seyahatkitabimharitalarroomdatabase.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Place(
    @ColumnInfo(name="name")
    var name : String,
    @ColumnInfo(name="latitude")
    var latitude :Double,
    @ColumnInfo(name="longitude")
    var longitude : Double) {
    @PrimaryKey(autoGenerate = true)
    var uid :Int = 0
}