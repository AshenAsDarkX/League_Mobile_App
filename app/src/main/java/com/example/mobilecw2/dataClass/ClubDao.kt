package com.example.mobilecw2.dataClass

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

// DAO interface for interacting with the Club entity
@Dao
interface ClubDao {
    //Method to insert clubs into the database
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertClubs(clubs: List<Club>)

    //Method to search clubs from the database
    @Query("SELECT * FROM Club WHERE UPPER(Name) LIKE UPPER(:searchQuery) OR UPPER(strLeague) LIKE UPPER(:searchQuery)")
    suspend fun searchClubOrLeague(searchQuery: String): List<Club>
}