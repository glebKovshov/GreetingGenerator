package com.example.temp

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface GreetingDao {

    @Insert
    suspend fun insert(greeting: GreetingEntity)

    @Query("SELECT * FROM greetings WHERE userId = :userId ORDER BY createdAt DESC")
    suspend fun getGreetingsForUser(userId: String): List<GreetingEntity>

    @Query("DELETE FROM greetings WHERE id = :id")
    suspend fun deleteById(id: Long)
}
