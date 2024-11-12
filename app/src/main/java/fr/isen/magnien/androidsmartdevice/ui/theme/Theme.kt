package fr.isen.magnien.androidsmartdevice.ui.theme

import androidx.compose.material3.* // Pour Material 3 et Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp


// Définir les couleurs principales
val BluePrimary = Color(0xFF2196F3)
val White = Color(0xFFFFFFFF)

// Palette de couleurs pour le thème clair
private val LightColors = lightColorScheme(
    primary = BluePrimary,
    onPrimary = White,
    background = Color.White,
    surface = Color.White,
)

/*val Typography = androidx.compose.material3.Typography(
    titleLarge = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp
    ),
    bodyLarge = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp
    )
)*/

// Fonction de thème principal de l’application
@Composable
fun AndroidSmartDeviceTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColors,
        typography = Typography,
        content = content
    )
}
