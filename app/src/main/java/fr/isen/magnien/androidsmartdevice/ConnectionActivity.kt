package fr.isen.magnien.androidsmartdevice

import android.bluetooth.*
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.isen.magnien.androidsmartdevice.ui.theme.AndroidSmartDeviceTheme

class ConnectionActivity : ComponentActivity() {

    private var bluetoothGatt: BluetoothGatt? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val deviceName = intent.getStringExtra("device_name") ?: "Nom inconnu"
        val deviceAddress = intent.getStringExtra("device_address") ?: "Adresse inconnue"
        Log.d("ConnectionActivity", "Nom reçu : $deviceName")
        Log.d("ConnectionActivity", "Adresse reçue : $deviceAddress")

        setContent {
            AndroidSmartDeviceTheme {
                ConnectionScreen(
                    deviceName = deviceName,
                    deviceAddress = deviceAddress,
                    onConnectClick = { connectToDevice(deviceAddress) }
                )
            }
        }
    }

    private fun connectToDevice(deviceAddress: String) {
        val bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        val bluetoothAdapter = bluetoothManager.adapter
        val device = bluetoothAdapter.getRemoteDevice(deviceAddress)

        if (device == null) {
            Toast.makeText(this, "Appareil introuvable", Toast.LENGTH_SHORT).show()
            return
        }

        bluetoothGatt = device.connectGatt(this, false, gattCallback)
        Toast.makeText(this, "Connexion en cours...", Toast.LENGTH_SHORT).show()
    }

    private val gattCallback = object : BluetoothGattCallback() {
        override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
            super.onConnectionStateChange(gatt, status, newState)

            when (newState) {
                BluetoothProfile.STATE_CONNECTED -> {
                    Log.d("BLE", "Connecté à l'appareil")
                    runOnUiThread {
                        Toast.makeText(this@ConnectionActivity, "Connecté", Toast.LENGTH_SHORT).show()
                    }
                    gatt.discoverServices()
                }
                BluetoothProfile.STATE_DISCONNECTED -> {
                    Log.d("BLE", "Déconnecté de l'appareil")
                    runOnUiThread {
                        Toast.makeText(this@ConnectionActivity, "Déconnecté", Toast.LENGTH_SHORT).show()
                    }
                }
                else -> Log.d("BLE", "État inconnu : $newState")
            }
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
            super.onServicesDiscovered(gatt, status)

            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.d("BLE", "Services découverts : ${gatt.services.size}")
                runOnUiThread {
                    Toast.makeText(this@ConnectionActivity, "Services découverts", Toast.LENGTH_SHORT).show()
                }
            } else {
                Log.d("BLE", "Échec de la découverte des services : $status")
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        bluetoothGatt?.close()
        bluetoothGatt = null
    }
}

@Composable
fun ConnectionScreen(
    deviceName: String,
    deviceAddress: String,
    onConnectClick: () -> Unit
) {
    var isLoading by remember { mutableStateOf(false) }
    var isConnected by remember { mutableStateOf(false) }
    var hasDiscoveredServices by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Appareil sélectionné",
            fontSize = 24.sp,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        Text(text = "Nom : $deviceName", fontSize = 18.sp)
        Text(text = "Adresse : $deviceAddress", fontSize = 18.sp, modifier = Modifier.padding(bottom = 32.dp))

        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.padding(16.dp))
        } else if (!isConnected) {
            Button(
                onClick = {
                    isLoading = true
                    onConnectClick()
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF105293))
            ) {
                Text(text = "Connecter")
            }
        } else if (isConnected && hasDiscoveredServices) {
            ShowFeatures()
        }

        // Affichage conditionnel des états
        if (isConnected && !hasDiscoveredServices) {
            Text(
                text = "Connexion réussie, découverte des services en cours...",
                color = Color.Gray,
                fontSize = 16.sp,
                modifier = Modifier.padding(top = 16.dp)
            )
        } else if (!isConnected && !isLoading) {
            Text(
                text = "Veuillez vous connecter à l'appareil",
                color = Color.Gray,
                fontSize = 16.sp,
                modifier = Modifier.padding(top = 16.dp)
            )
        }
    }

    // Simuler l'état de connexion (à adapter en fonction du callback réel)
    LaunchedEffect(isLoading) {
        if (isLoading) {
            // Simule une connexion réussie après 2 secondes
            kotlinx.coroutines.delay(2000)
            isLoading = false
            isConnected = true
            hasDiscoveredServices = true // Changez en fonction de la découverte réelle des services
        }
    }
}

@Composable
fun ShowFeatures() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text("Fonctionnalités disponibles :", fontSize = 20.sp, modifier = Modifier.padding(bottom = 8.dp))

        Button(
            onClick = { /* Ajouter votre fonctionnalité */ },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Fonctionnalité 1")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = { /* Ajouter une autre fonctionnalité */ },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Fonctionnalité 2")
        }
    }
}
