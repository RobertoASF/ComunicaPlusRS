package cl.duoc.comunicaplusrs

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import cl.duoc.comunicaplusrs.ui.theme.ComunicaPlusRSTheme
import cl.duoc.comunicaplusrs.navigation.AppNavigation

class MainActivity : ComponentActivity() {

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {
        super.onCreate(savedInstanceState)

        setContent {

            ComunicaPlusRSTheme {

                AppNavigation()
            }
        }
    }
}
