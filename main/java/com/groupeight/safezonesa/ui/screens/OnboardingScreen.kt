package com.groupeight.safezonesa.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.groupeight.safezonesa.data.SessionManager
import com.groupeight.safezonesa.ui.components.BrandedBackground
import com.groupeight.safezonesa.ui.theme.BrightBlue
import kotlinx.coroutines.launch

private data class OnboardingSlide(val icon: ImageVector, val title: String, val body: String)

private val slides = listOf(
    OnboardingSlide(
        Icons.Filled.Shield,
        "Your Community, Watching Out for You",
        "SafeZone SA connects you with verified neighbours to report and confirm incidents in real time."
    ),
    OnboardingSlide(
        Icons.Filled.Warning,
        "Help, One Tap Away",
        "The SOS button — or a silent shake — instantly shares your location with emergency contacts and nearby responders."
    ),
    OnboardingSlide(
        Icons.Filled.People,
        "Walk Safer, Together",
        "Share your live location with someone you trust while you're on the move, and see verified safe spaces near you."
    )
)

/** Shown once, on the very first launch (see SessionManager.hasSeenOnboarding). */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(onFinished: () -> Unit) {
    val context = LocalContext.current
    val pagerState = rememberPagerState(pageCount = { slides.size })
    val scope = rememberCoroutineScope()

    fun complete() {
        SessionManager.setOnboardingSeen(context)
        onFinished()
    }

    BrandedBackground {
        Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                TextButton(onClick = { complete() }) {
                    Text("Skip", color = Color.White.copy(alpha = 0.8f))
                }
            }

            HorizontalPager(
                state = pagerState,
                modifier = Modifier.weight(1f)
            ) { page ->
                val slide = slides[page]
                Column(
                    modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier.size(110.dp).background(BrightBlue.copy(alpha = 0.18f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(slide.icon, contentDescription = null, tint = BrightBlue, modifier = Modifier.size(56.dp))
                    }
                    Spacer(Modifier.height(32.dp))
                    Text(
                        slide.title,
                        color = Color.White,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(12.dp))
                    Text(
                        slide.body,
                        color = Color.White.copy(alpha = 0.75f),
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                repeat(slides.size) { index ->
                    val active = pagerState.currentPage == index
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .size(if (active) 10.dp else 8.dp)
                            .background(
                                if (active) BrightBlue else Color.White.copy(alpha = 0.3f),
                                CircleShape
                            )
                    )
                }
            }

            Button(
                onClick = {
                    if (pagerState.currentPage < slides.lastIndex) {
                        scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
                    } else {
                        complete()
                    }
                },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BrightBlue)
            ) {
                Text(if (pagerState.currentPage < slides.lastIndex) "Next" else "Get Started")
            }
        }
    }
}