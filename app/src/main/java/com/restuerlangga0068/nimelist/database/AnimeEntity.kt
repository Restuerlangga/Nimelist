package com.restuerlangga0068.nimelist.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "anime")
data class AnimeEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val description: String,
    val rating: Float,
    val review: String,
    val isCompleted: Boolean,
    val FimageRes: Int,
    val imageRes: Int,
    val trailerUrl: String
)