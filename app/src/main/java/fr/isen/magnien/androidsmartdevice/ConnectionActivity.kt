package fr.isen.magnien.androidsmartdevice

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class ConnectionActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Récupérer le nom et l'adresse de l'appareil à partir de l'Intent
        val deviceName = intent.getStringExtra("device_name") ?: "Nom inconnu"
        val deviceAddress = intent.getStringExtra("device_address") ?: "Adresse inconnue"

        setContent {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Nom : $deviceName",
                    fontSize = 24.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = "Adresse : $deviceAddress",
                    fontSize = 18.sp
                )
            }
        }
    }
}
