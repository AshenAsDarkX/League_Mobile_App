package com.example.mobilecw2

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.mobilecw2.dataClass.LeagueDao
import com.example.mobilecw2.dataClass.LeagueDatabase
import com.example.mobilecw2.ui.theme.MobileCw2Theme

// Global variables for database and DAO
lateinit var database: LeagueDatabase
lateinit var leagueDao: LeagueDao

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MobileCw2Theme {
                // A column container using the 'background' color from the theme
                Column (
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // Button to navigate to AddLeaguesToDB
                    Button(onClick = { Intent(applicationContext, AddLeaguesToDB::class.java ).also { startActivity(it) } },
                        colors = ButtonDefaults.buttonColors(Color.Blue), modifier = Modifier.width(250.dp)) {
                        Text(text = "Add Leagues to DB")
                    }

                    // Button to navigate to SearchByLeague
                    Button(onClick = { Intent(applicationContext, SearchByLeague::class.java ).also { startActivity(it) } },
                        colors = ButtonDefaults.buttonColors(Color.Blue), modifier = Modifier.width(250.dp)) {
                        Text(text = "Search for Clubs By League")
                    }

                    // Button to navigate to SearchForClubs activity
                    Button(onClick = { Intent(applicationContext, SearchForClubs::class.java).also { startActivity(it) } },
                        colors = ButtonDefaults.buttonColors(Color.Blue), modifier = Modifier.width(250.dp)) {
                        Text(text = "Search for Clubs")
                    }
                }
            }
        }
    }
}
