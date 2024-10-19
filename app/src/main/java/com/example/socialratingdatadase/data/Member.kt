package com.example.socialratingdatadase.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "members")
data class Member(
    @PrimaryKey(autoGenerate = true)
    val id: Int? = null,
    val rating: Int,
    val name: String,
    val numberQR: String,
)
