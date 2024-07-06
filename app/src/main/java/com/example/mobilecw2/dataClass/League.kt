package com.example.mobilecw2.dataClass

import androidx.room.Entity
import androidx.room.PrimaryKey

//Entity class for League details
@Entity
data class League(
    @PrimaryKey val idLeague: String,
    val strLeague: String?,
    val strSport: String?,
    val strLeagueAlternate: String?,

)