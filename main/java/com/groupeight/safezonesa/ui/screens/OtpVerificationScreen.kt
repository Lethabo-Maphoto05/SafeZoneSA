package com.groupeight.safezonesa.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.groupeight.safezonesa.network.ApiClient
import com.groupeight.safezonesa.network.apiErrorMessage
import com.groupeight.safezonesa.ui.theme.AlertRed
import com.groupeight.safezonesa.ui.theme.RoyalBlue
import kotlinx.coroutines.launch

/**
 * FR1: "a one-time PIN (OTP) is provided by SMS to confirm registration" before the
 * account is authorised. Calls POST /api/auth/verify-otp (Section 5.3).
 */
@Composable
fun OtpVerificationScreen(phone: String, onVerified: () -> Unit) {
    var otp by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    val context = androidx.compose.ui.platform.LocalContext.current
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Verify Your Number", fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        Text("Enter the OTP sent to $phone", fontSize = 13.sp, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
        Spacer(Modifier.height(24.dp))

        OutlinedTextField(
            value = otp,
            onValueChange = { if (it.length <= 6) otp = it },
            label = { Text("OTP Code") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        if (error != null) {
            Spacer(Modifier.height(8.dp))
            Text(error!!, color = AlertRed, fontSize = 12.sp)
        }

        Spacer(Modifier.height(16.dp))
        Button(
            onClick = {
                isLoading = true
                error = null
                scope.launch {
                    try {
                        val response = ApiClient.getService(context).verifyOtp(mapOf("phone" to phone, "otp" to otp))
                        isLoading = false
                        if (response.isSuccessful) {
                            onVerified()
                        } else {
                            error = response.apiErrorMessage("Incorrect code (${response.code()}). Please try again.")
                        }
                    } catch (e: Exception) {
                        isLoading = false
                        error = "Couldn't reach the SafeZone SA server: ${e.localizedMessage ?: "unknown error"}"
                    }
                }
            },
            enabled = otp.length in 4..6 && !isLoading,
            modifier = Modifier.fillMaxWidth().height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue)
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
            } else {
                Text("VERIFY")
            }
        }

        Spacer(Modifier.height(12.dp))
        TextButton(onClick = { /* TODO: resend OTP via /api/auth/register or a dedicated resend endpoint */ }) {
            Text("Resend code")
        }
    }
}
