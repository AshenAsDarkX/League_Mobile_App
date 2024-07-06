package com.example.mobilecw2.dataClass

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

// DAO interface for interacting with the League entity
@Dao
interface LeagueDao {
    //Method to insert leagues into the database
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLeague(leagues: List<League>)
}