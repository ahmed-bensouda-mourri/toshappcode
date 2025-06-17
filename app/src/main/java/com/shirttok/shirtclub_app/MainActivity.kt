package com.shirttok.shirtclub_app

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import com.shirttok.shirtclub_app.ui.theme.ShirtClub_AppTheme
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            ShirtClub_AppTheme {
                ShirtClubApp()
            }
        }
    }
}


@Composable
fun ShirtClubApp() {
    val context = LocalContext.current
    var shirtNumber by remember { mutableStateOf("") }
    val errorMessage = remember { mutableStateOf("") }
    val loading = remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Display error message at the top if it exists
        if (errorMessage.value.isNotEmpty()) {
            Text(
                text = errorMessage.value,
                color = Color.Red,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        Text(
            text = "See a number on a shirt? Enter it here to receive message",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        TextField(
            value = shirtNumber,
            onValueChange = { shirtNumber = it },
            placeholder = { Text("Shirt Message Number", color = Color.Gray) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            )
        )

        Button(
            onClick = { searchMessage(shirtNumber, context, errorMessage, loading) },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp)
                .height(48.dp)
        ) {
            Text("Search a Shirt", color = Color.White, fontWeight = FontWeight.Bold)
        }

        Image(
            painter = painterResource(id = R.drawable.app_logo),
            contentDescription = "ShirtClub Logo",
            modifier = Modifier
                .size(200.dp)
                .padding(top = 24.dp)
        )

        Text(
            text = "Login to Shirtclub.net",
            color = Color(0xFF4CAF50),
            modifier = Modifier
                .padding(top = 16.dp)
                .clickable {
                    val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://shirtclub.net"))
                    context.startActivity(browserIntent)
                }
        )
    }
}
fun searchMessage(
    shirtNumber: String,
    context: Context,
    errorMessage: MutableState<String>,
    loading: MutableState<Boolean>
) {
    if (shirtNumber.isEmpty()) {
        errorMessage.value = "Please enter a Shirt number."
        return
    }

    // Clear the error message at the beginning of each search
    errorMessage.value = ""
    loading.value = true

    val apiService = ApiClient.getApiService()
    val call = apiService.getMessage(shirtNumber)

    call.enqueue(object : Callback<ApiResponse> {
        override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
            loading.value = false

            if (response.isSuccessful && response.body() != null) {
                val data = response.body()!!

                when (data.message) {
                    is MessageResult.NotFound -> {
                        // Show "No Message Found" if the number doesn’t exist
                        errorMessage.value = "No Message Found"
                    }
                    is MessageResult.Success -> {
                        val messageData = (data.message as MessageResult.Success).messageData

                        if (messageData.link.isNotEmpty()) {
                            var link = messageData.link
                            if (!link.startsWith("http://") && !link.startsWith("https://")) {
                                link = "https://$link"
                            }

                            try {
                                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
                                context.startActivity(browserIntent)
                                errorMessage.value = "" // Clear error if link opens successfully
                            } catch (e: Exception) {
                                errorMessage.value = "Error: Unable to open the browser."
                            }
                        } else {
                            errorMessage.value = "No Message Found"
                        }
                    }
                }
            } else {
                // API Error Handling
                errorMessage.value = "API Error: Unable to retrieve data. Please try again."
            }
        }

        override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
            loading.value = false
            // Network Error Handling
            //errorMessage.value = "Network Error: ${t.localizedMessage}. Please check your internet connection."
            errorMessage.value = "Message Not Found"
        }
    })
}