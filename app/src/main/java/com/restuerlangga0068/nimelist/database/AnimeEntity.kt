package com.restuerlangga0068.nimelist.database

import androidx.room.Entity
import androidx.room.PrimaryKey

import com.google.gson.annotations.SerializedName

@Entity(tableName = "anime")
data class AnimeEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val description: String?,
    val rating: Double,
    val review: String?,
    val email: String,

    @SerializedName("is_completed")
    val isCompleted: Boolean,

    @SerializedName("image_url")
    val imageUrl: String?
)
