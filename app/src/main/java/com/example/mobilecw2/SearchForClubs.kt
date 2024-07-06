package com.example.mobilecw2

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import com.example.mobilecw2.dataClass.Club
import com.example.mobilecw2.dataClass.LeagueDatabase
import com.example.mobilecw2.ui.theme.MobileCw2Theme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URL

class SearchForClubs : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MobileCw2Theme {
                GUI()
            }
        }
    }
}

@Composable
fun GUI(){
    //Initialising variables
    var keyword by rememberSaveable { mutableStateOf("") }
    var searchResult by rememberSaveable { mutableStateOf(emptyList<Club>()) }
    val scope = rememberCoroutineScope()
    var error = rememberSaveable{mutableStateOf(false)}

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,

    //verticalArrangement = Arrangement.Center
    ) {
        TextField(
            value = keyword,
            onValueChange = { keyword = it },
            label = { Text("Club name") },
            modifier = Modifier
                .width(350.dp)
                .padding(10.dp)
        )
        Button(
            colors = ButtonDefaults.buttonColors(Color.Blue),
            onClick = {
            if (keyword.isNotBlank()){
                scope.launch {
                    // Perform club search using keyword and update search results
                    searchResult = searchForClubs(keyword, database)
                    error.value = false
                }
            }
            else{
                error.value = true
            }
        }) {
            Text(text = "Search")
        }

        if (error.value){// Display error message if club name is not entered
            Text(
                text = "Please enter a valid club name",
                color = MaterialTheme.colorScheme.error)

        }

        if(searchResult.isEmpty()){// Display message if no search results are found
            Text(
                text = "No results found",
                color = MaterialTheme.colorScheme.error)
        }

        if(searchResult.isNotEmpty()){// Display search results if available
            Text(text = "Results")
            Column (
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ){
                searchResult.forEach {club -> 
                    var logo by remember { mutableStateOf<ImageBitmap?>(null) }
                    
                    LaunchedEffect(key1 = club.strTeamLogo) {// Load club logo asynchronously using URL
                        logo = club.strTeamLogo?.let { loadImage(it) }?.asImageBitmap()
                    }

                    if (logo != null) {// Display club logo if available, otherwise show placeholder text
                        Image(
                            bitmap = logo!!,
                            contentDescription = "Logo Pic",
                            modifier = Modifier.size(100.dp)
                        )
                    }
                    else{
                        Text(text = "No logo found")
                        Spacer(modifier = Modifier.height(10.dp))
                    }
                    // Display club details
                    Text(text = """
                        Name: ${club.name}
                        League: ${club.strLeague}
                        Formed year: ${club.intFormedYear}
                        Stadium: ${club.strStadium}
                        Stadium location: ${club.strStadiumLocation}
                        Keywords: ${club.strKeywords}
                    """.trimIndent())

                    Text(text = "\n\n")
                }
            }
        }
    }
}

// Function to search for clubs by club name in the Room database
suspend fun searchForClubs(keyword: String, database: LeagueDatabase): List<Club> {
    val searchKey = "%$keyword%"
    return database.clubDao().searchClubOrLeague(searchKey)
}

// Function to load an image from a given URL and return as Bitmap
suspend fun loadImage(url: String): Bitmap? {
    try {
        val inputStream = withContext(Dispatchers.IO) {
            URL(url).openStream()
        }
        return BitmapFactory.decodeStream(inputStream)
    } catch (e: Exception) {
        return null
    }
}
