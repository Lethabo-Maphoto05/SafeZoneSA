package com.groupeight.safezonesa.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.groupeight.safezonesa.R
import com.groupeight.safezonesa.data.SessionManager
import com.groupeight.safezonesa.ui.components.BrandedBackground
import kotlinx.coroutines.delay
import androidx.compose.ui.platform.LocalContext

/**
 * Section 4.2: gives the user instant visual evidence that the right, trustworthy app has
 * started, while the app checks for a valid session token, over the branded background used
 * throughout onboarding and auth.
 */
@Composable
fun SplashScreen(onFinished: (loggedIn: Boolean) -> Unit) {
    val context = LocalContext.current
    val scale = remember { Animatable(0.7f) }
    val alpha = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        scale.animateTo(1f, animationSpec = tween(600))
    }
    LaunchedEffect(Unit) {
        alpha.animateTo(1f, animationSpec = tween(800))
    }
    LaunchedEffect(Unit) {
        delay(1600)
        onFinished(SessionManager.isLoggedIn(context))
    }

    BrandedBackground {
        Column(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(R.drawable.logo_full),
                contentDescription = "SafeZone SA",
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .scale(scale.value)
                    .alpha(alpha.value)
            )
            Spacer(Modifier.height(28.dp))
            CircularProgressIndicator(
                modifier = Modifier.size(28.dp).alpha(alpha.value),
                color = Color.White,
                strokeWidth = 2.5.dp
            )
        }
    }
}
