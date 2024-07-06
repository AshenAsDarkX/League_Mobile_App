package com.example.mobilecw2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import com.example.mobilecw2.dataClass.League
import com.example.mobilecw2.dataClass.LeagueDao
import com.example.mobilecw2.dataClass.LeagueDatabase
import com.example.mobilecw2.ui.theme.MobileCw2Theme
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.nio.charset.Charset

class AddLeaguesToDB : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        database = Room.databaseBuilder(// Initialize Room database
            this, LeagueDatabase::class.java,"leagues.db"
        ).fallbackToDestructiveMigration().build()

        leagueDao = database.leagueDao()// Retrieve LeagueDao instance database

        setContent {
            MobileCw2Theme {
                // A surface container using the 'background' color from the theme
                Column (
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {

                    Button(
                        colors = ButtonDefaults.buttonColors(Color.Blue),
                        onClick = { insertLeaguesToDatabase() }) {
                        Text(text = "Add Leagues to Database")
                    }
                }
            }
        }
    }
    private fun insertLeaguesToDatabase(){// Function to insert leagues into Room database from JSON
        lifecycleScope.launch {
            try {
                val jsonObject = JSONObject(loadJson())// Load JSON data
                val jsonArray = jsonObject.getJSONArray("leagues")
                val leagueList = mutableListOf<League>()// Parse JSON array and create a list of League objects
                for (i in 0 until jsonArray.length()){
                    val leagueDetail = jsonArray.getJSONObject(i)
                    leagueList.add(
                        League(
                            idLeague = leagueDetail.getString("idLeague"),
                            strLeague = leagueDetail.getString("strLeague"),
                            strSport = leagueDetail.getString("strSport"),
                            strLeagueAlternate = leagueDetail.getString("strLeagueAlternate")
                        )
                    )
                }
                leagueDao.insertLeague(leagueList)// Insert leagues into Room database using LeagueDao
            }catch (e: Exception){
                e.printStackTrace()
            }
        }
    }

    private fun loadJson(): String {// Function to load JSON data from raw resources
        val fileInputStream = resources.openRawResource(R.raw.leagues)
        return fileInputStream.use { stream ->
            val length = stream.available()
            val buffer = ByteArray(length)
            stream.read(buffer)
            String(buffer, Charset.forName("UTF-8"))
        }
    }
}


