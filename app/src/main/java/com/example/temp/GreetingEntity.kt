package com.example.temp

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "greetings")
data class GreetingEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val userId: String,

    val name: String,
    val gender: String?,      // может быть null
    val relation: String?,    // может быть null
    val occasion: String,
    val tone: String?,        // может быть null
    val age: Int?,            // может быть null

    val greetingText: String,

    val createdAt: Long       // timestamp (System.currentTimeMillis)
)
