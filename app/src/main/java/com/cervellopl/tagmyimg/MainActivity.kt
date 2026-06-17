package com.cervellopl.tagmyimg

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.cervellopl.tagmyimg.ui.TaggingScreen
import com.cervellopl.tagmyimg.ui.theme.TagMyImgTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TagMyImgTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    TaggingScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}
