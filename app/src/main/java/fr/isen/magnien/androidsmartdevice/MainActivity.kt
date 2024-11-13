package fr.isen.magnien.androidsmartdevice

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import fr.isen.magnien.androidsmartdevice.ui.theme.AndroidSmartDeviceTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AndroidSmartDeviceTheme {
                HomeScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun HomeScreen() {
    // Utiliser LocalContext.current pour obtenir le contexte
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("BLE Scanner", color = Color.White) },
                colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = Color(0xFF105293))
            )
        },
        content = { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Image
                Image(
                    painter = painterResource(id = R.mipmap.bluetooth_foreground),
                    contentDescription = "App Icon",
                    modifier = Modifier.size(200.dp)
                )

                // Titre
                Text(
                    text = "Bienvenue dans BLE Scanner",
                    fontSize = 24.sp,
                    color = Color.Black,
                    modifier = Modifier.padding(top = 16.dp)
                )

                // Description
                Text(
                    text = "Cette application scanne les appareils Bluetooth Low Energy à proximité.",
                    fontSize = 16.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(top = 8.dp)
                )

                Spacer(modifier = Modifier.weight(1f))

                // Bouton bleu
                Button(
                    onClick = {
                        // Utiliser le contexte récupéré avec LocalContext.current
                        val intent = Intent(context, ScanActivity::class.java)
                        context.startActivity(intent)
                    },
                    modifier = Modifier
                        .padding(bottom = 24.dp)
                        .fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF105293))
                ) {
                    Text("Commencer le scan", color = Color.White)
                }
            }
        }
    )
}
