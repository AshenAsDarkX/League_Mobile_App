package com.example.mobilecw2.dataClass


import androidx.room.Database
import androidx.room.RoomDatabase

// Database class representing the Room database for managing leagues and clubs
@Database(entities = [League::class, Club::class], version=2)
abstract class LeagueDatabase: RoomDatabase() {
    //Method to retrieve the DAO interface for League entities
    abstract fun leagueDao(): LeagueDao

    //Method to retrieve the DAO interface for Club entities
    abstract fun clubDao(): ClubDao
}