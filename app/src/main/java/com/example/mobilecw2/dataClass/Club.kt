package com.example.mobilecw2.dataClass

import androidx.room.Entity
import androidx.room.PrimaryKey

//Entity class for Club details
@Entity
data class Club (
    @PrimaryKey val id: String,
    val name: String?,
    val strTeamShort: String?,
    val strAlternate: String?,
    val intFormedYear: String?,
    val strLeague: String?,
    val strStadium: String?,
    val strKeywords: String?,
    val strStadiumLocation: String?,
    val intStadiumCapacity: String?,
    val strWebsite: String?,
    val strTeamJersey: String?,
    val strTeamLogo: String?
)