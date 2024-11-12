package fr.isen.magnien.androidsmartdevice

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.*
import android.Manifest
import android.content.pm.PackageManager
import androidx.compose.foundation.background
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import fr.isen.magnien.androidsmartdevice.ui.theme.AndroidSmartDeviceTheme

class ScanActivity : ComponentActivity() {

    private lateinit var bluetoothAdapter: BluetoothAdapter
    private lateinit var bluetoothLeScanner: android.bluetooth.le.BluetoothLeScanner

    private val devicesList = mutableStateListOf<ScanResult>()

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions.all { it.value }) {
            // Toutes les permissions sont accordées
            Toast.makeText(this, "Permissions Bluetooth accordées", Toast.LENGTH_SHORT).show()
            // Vous pouvez maintenant commencer le scan BLE ou d'autres actions
            startScan()

        } else {
            Toast.makeText(this, "Permissions Bluetooth requises", Toast.LENGTH_SHORT).show()
        }
    }
    // Demande de permissions pour Bluetooth et Localisation
    /*private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions[Manifest.permission.BLUETOOTH_SCAN] == true &&
            permissions[Manifest.permission.BLUETOOTH_CONNECT] == true &&
            permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true) {
            // Permissions accordées, démarrer le scan
            startScan()
        } else {
            // Si les permissions ne sont pas accordées, informer l'utilisateur
            Toast.makeText(this, "Les permissions Bluetooth sont nécessaires", Toast.LENGTH_SHORT).show()
        }
    }*/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothAdapter = bluetoothManager.adapter

        // Vérifier si Bluetooth est disponible et activé
        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled) {
            Toast.makeText(this, "Bluetooth non disponible ou désactivé", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Demander les permissions en fonction de la version Android
        checkAndRequestPermissions()


        setContent {
            ScanScreen(
                onStopScanClick = {
                    stopScan() // Appel à la méthode stopScan
                }
            )
        }
    }
    private fun checkAndRequestPermissions() {
        val permissions = getRequiredPermissions()

        // Vérifiez si toutes les permissions sont déjà accordées
        val permissionsToRequest = permissions.filter { permission ->
            ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED
        }

        if (permissionsToRequest.isNotEmpty()) {
            // Si des permissions sont manquantes, les demander
            requestPermissionLauncher.launch(permissionsToRequest.toTypedArray())
        } else {
            // Si toutes les permissions sont déjà accordées
            Toast.makeText(this, "Toutes les permissions sont déjà accordées", Toast.LENGTH_SHORT).show()
            // Commencer le scan BLE ou toute autre action
            startScan()
        }
    }
    private fun getRequiredPermissions(): List<String> {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            // Permissions pour Android 12 et plus
            listOf(
                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.BLUETOOTH_CONNECT,
                Manifest.permission.BLUETOOTH_ADMIN,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        } else {
            // Permissions pour les versions antérieures à Android 12
            listOf(
                Manifest.permission.BLUETOOTH,
                Manifest.permission.BLUETOOTH_ADMIN,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            )
        }
    }

    private fun startScan() {
        bluetoothLeScanner = bluetoothAdapter.bluetoothLeScanner
        bluetoothLeScanner.startScan(object : ScanCallback() {
            override fun onScanResult(callbackType: Int, result: ScanResult) {
                // Vérifier si l'appareil est déjà dans la liste
                if (devicesList.none { it.device.address == result.device.address }) {
                    devicesList.add(result)  // Ajouter l'appareil détecté
                }
            }
            override fun onScanFailed(errorCode: Int) {
                Toast.makeText(applicationContext, "Scan échoué : $errorCode", Toast.LENGTH_SHORT).show()
            }
        })
    }


    private fun stopScan() {
        bluetoothLeScanner.stopScan(object : ScanCallback() {})
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScanScreen(onStopScanClick: () -> Unit) {
    var showList by remember { mutableStateOf(false) }

    // Liste des appareils détectés (ce sera la liste de ScanResult)
    val devicesList = remember { mutableStateListOf<ScanResult>() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (devicesList.isNotEmpty()) {
            DeviceList(devicesList)  // Afficher la liste des appareils détectés
        } else {
            Text("Aucun appareil détecté", fontSize = 18.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { showList = !showList },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3))
        ) {
            Text(if (showList) "Masquer la liste" else "Afficher la liste")
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = onStopScanClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Arrêter le scan")
        }
    }
}

@Composable
fun DeviceList(devicesList: List<ScanResult>) {
    Text(
        text = "Liste des différents appareils disponibles",
        fontSize = 24.sp,
        modifier = Modifier.padding(bottom = 16.dp)
    )
    LazyColumn {
        items(devicesList) { result ->
            val device = result.device  // Récupérer le périphérique Bluetooth
            val deviceName = device.name ?: "Appareil inconnu"
            val deviceAddress = device.address

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(text = "Nom : $deviceName", fontSize = 18.sp)
                    Text(
                        text = "Adresse : $deviceAddress",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
            }
        }
    }
}

@Composable
fun ScanDevices() {
    Text(
        text="SCAN EN COURS",
        fontSize = 30.sp,
        color = Color.White,
        modifier = Modifier
            .background(
                color = Color(0xFF2196F3),      // Couleur de fond personnalisée
                shape = RoundedCornerShape(8.dp) // Forme arrondie avec un rayon de 8.dp
            )
            .padding(12.dp)
    )
}
@Composable
fun ScanNoDevices() {
    Text(
        text="SCAN ARRETER",
        fontSize = 30.sp,
        color = Color.White,
        modifier = Modifier
            .background(
                color = Color(0xFF2196F3),      // Couleur de fond personnalisée
                shape = RoundedCornerShape(8.dp) // Forme arrondie avec un rayon de 8.dp
            )
            .padding(12.dp)
    )
}