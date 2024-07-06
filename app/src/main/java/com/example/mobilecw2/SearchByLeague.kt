package com.example.mobilecw2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.room.Room
import com.example.mobilecw2.dataClass.Club
import com.example.mobilecw2.dataClass.LeagueDatabase
import com.example.mobilecw2.ui.theme.MobileCw2Theme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class SearchByLeague : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        database = Room.databaseBuilder(
            applicationContext,
            LeagueDatabase::class.java,
            "leagues.db"
        ).build()


        setContent {
            MobileCw2Theme {
                GUI(database)
            }
        }
    }
}

// Function to display the GUI for searching and saving clubs
@Composable
fun GUI(database: LeagueDatabase) {
    //Initializing variables
    var leagueInfoDisplay by rememberSaveable { mutableStateOf("") }
    var keyword by remember { mutableStateOf("") }
    var dataSaved by rememberSaveable { mutableStateOf(false) }
    var clubList by rememberSaveable { mutableStateOf<List<Club>?>(null) }
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextField(
            value = keyword,
            onValueChange = { keyword = it },
            label = { Text("Football league") },
            modifier = Modifier
                .width(250.dp)
                .padding(10.dp)
        )
        Button(
            colors = ButtonDefaults.buttonColors(Color.Blue),
            onClick = {
            if (keyword.isNotEmpty()) {
                scope.launch {
                    // Fetch league details and update state variables
                    leagueInfoDisplay = fetchLeagues(keyword) { fetchLeagues ->
                        clubList = fetchLeagues
                    }
                }
            }
            else{
                leagueInfoDisplay = "Please enter a league"
            }
        }, modifier = Modifier.width(200.dp)) {
            Text(text = "Retrieve Clubs")
        }
        Button(
            colors = ButtonDefaults.buttonColors(Color.Blue),
            onClick = {
            scope.launch {
                if (clubList != null){
                    dataSaved = insertClubDetailsToDB(database, clubList!!)
                }
            }
            }, modifier = Modifier.width(200.dp)) {
            Text(text = "Save clubs to Database")
        }
        Text(
            modifier = Modifier.verticalScroll(rememberScrollState()),
            text = if (dataSaved) "Data saved successfully" else "Data not saved yet"
        )
        Text(
            modifier = Modifier.verticalScroll(rememberScrollState()),
            text = leagueInfoDisplay)
    }
}

// Function to fetch clubs based on football league keyword using API
suspend fun fetchLeagues(keyword: String, onFetch: (List<Club>) -> Unit): String {

    val urlString = "https://www.thesportsdb.com/api/v1/json/3/search_all_teams.php?l=$keyword"
    val url = URL(urlString)
    val con: HttpURLConnection = withContext(Dispatchers.IO) {
        url.openConnection()
    } as HttpURLConnection

    var clubInfo = ""
    val fetchedDetails = mutableListOf<Club>()

    withContext(Dispatchers.IO) {
        con.run {
            requestMethod = "GET"
            inputStream.bufferedReader().use { read ->
                val res = StringBuilder()
                var line: String?
                while (read.readLine().also { line = it } !=null){
                    res.append(line)
            }
                val jsonObject = JSONObject(res.toString())
                val teams: JSONArray? = jsonObject.optJSONArray("teams")

                if (teams != null){
                    for (i in 0 until  teams.length()){
                        val team = teams.getJSONObject(i)
                        val club = Club(
                            id = team.getString("idTeam"),
                            name = team.getString("strTeam"),
                            strTeamShort = team.getString("strTeamShort"),
                            strAlternate = team.getString("strAlternate"),
                            intFormedYear = team.getString("intFormedYear"),
                            strLeague = team.getString("strLeague"),
                            strStadium = team.getString("strStadium"),
                            strKeywords = team.getString("strKeywords"),
                            strStadiumLocation = team.getString("strStadiumLocation"),
                            intStadiumCapacity = team.getString("intStadiumCapacity"),
                            strWebsite = team.getString("strWebsite"),
                            strTeamJersey = team.getString("strTeamJersey"),
                            strTeamLogo = team.getString("strTeamLogo")
                        )
                        fetchedDetails.add(club)
                        clubInfo += formatDetails(club) + "\n\n"

                    }
                }
        }
        }

    }
    onFetch(fetchedDetails)
    return clubInfo
}

// Function to format club details as a string
fun formatDetails(club: Club): String {
    return """
        idTeam       : "${club.id}",
        Name         : "${club.name}",
        strTeamShort : "${club.strTeamShort}",
        strAlternate : "${club.strAlternate}",
        intFormedYear: "${club.intFormedYear}",
        strLeague    : "${club.strLeague}",
        strStadium   : "${club.strStadium}",
        strKeywords  : "${club.strKeywords}",
        strStadiumLocation: "${club.strStadiumLocation}",
        intStadiumCapacity: "${club.intStadiumCapacity}",
        strWebsite   : "${club.strWebsite}",
        strTeamJersey: "${club.strTeamJersey}",
        strTeamLogo  : "${club.strTeamLogo}"
    """.trimIndent()
}

// Function to insert club details into Room database
suspend fun insertClubDetailsToDB(db: LeagueDatabase, clubs: List<Club>):Boolean {
    return try {
        db.clubDao().insertClubs(clubs)
        true
    } catch (e: Exception) {
        false
    }
}

