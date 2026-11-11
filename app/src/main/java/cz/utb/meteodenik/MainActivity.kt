package cz.utb.meteodenik

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import cz.utb.meteodenik.navigace.MeteoDenikNavHost
import cz.utb.meteodenik.ui.theme.MeteoDenikTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MeteoDenikTheme {
                MeteoDenikNavHost()
            }
        }
    }
}
