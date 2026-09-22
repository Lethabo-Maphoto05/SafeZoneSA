package com.groupeight.safezonesa.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.groupeight.safezonesa.R
import com.groupeight.safezonesa.ui.components.BrandedBackground
import com.groupeight.safezonesa.ui.theme.AlertRed
import com.groupeight.safezonesa.ui.theme.CardWhite
import com.groupeight.safezonesa.ui.theme.RoyalBlue
import com.groupeight.safezonesa.viewmodel.AuthViewModel

/** A frosted card that content sits on top of the branded background, so form fields stay
 *  legible against the dark artwork instead of losing contrast against it directly. */
@Composable
private fun AuthCard(content: @Composable ColumnScope.() -> Unit) {
    Surface(
        color = CardWhite,
        shape = RoundedCornerShape(24.dp),
        shadowElevation = 12.dp,
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            content = content
        )
    }
}

/** Section 4.2: FR1 login — email/password with show/hide toggle, Forgot Password, or SSO. */
@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onNavigateToRegister: () -> Unit,
    viewModel: AuthViewModel = viewModel()
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }
    val uiState by viewModel.uiState.collectAsState()

    BrandedBackground {
        Column(
            modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(24.dp))
            Image(
                painter = painterResource(R.drawable.logo_full),
                contentDescription = "SafeZone SA",
                modifier = Modifier.fillMaxWidth(0.45f)
            )
            Spacer(Modifier.height(24.dp))

            AuthCard {
                Text("Welcome Back!", fontSize = 22.sp, fontWeight = FontWeight.Bold)
                Text("Login to continue your safety journey", fontSize = 13.sp)
                Spacer(Modifier.height(24.dp))

                OutlinedTextField(
                    value = email, onValueChange = { email = it },
                    label = { Text("Email") }, modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(
                    value = password, onValueChange = { password = it },
                    label = { Text("Password") }, modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { showPassword = !showPassword }) {
                            Icon(if (showPassword) Icons.Filled.VisibilityOff else Icons.Filled.Visibility, contentDescription = null)
                        }
                    }
                )
                Spacer(Modifier.height(4.dp))
                TextButton(onClick = { /* TODO: forgot-password flow */ }, modifier = Modifier.align(Alignment.End)) {
                    Text("Forgot Password?")
                }

                if (uiState.errorMessage != null) {
                    Spacer(Modifier.height(8.dp))
                    Text(uiState.errorMessage!!, color = AlertRed, fontSize = 12.sp)
                }

                Spacer(Modifier.height(12.dp))
                Button(
                    onClick = { viewModel.login(email, password, onLoginSuccess) },
                    enabled = !uiState.isLoading,
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue)
                ) {
                    if (uiState.isLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
                    } else {
                        Text("LOGIN")
                    }
                }

                Spacer(Modifier.height(12.dp))
                Row {
                    Text("Don't have an account? ")
                    Text("Register", color = RoyalBlue, fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable(onClick = onNavigateToRegister))
                }
                Spacer(Modifier.height(16.dp))
                Text("or continue with", fontSize = 12.sp)
                Spacer(Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    OutlinedButton(onClick = { /* TODO: Google SSO */ }) { Text("Google") }
                    OutlinedButton(onClick = { /* TODO: Facebook SSO */ }) { Text("Facebook") }
                }
            }
            Spacer(Modifier.height(16.dp))
        }
    }
}

/** Section 4.2: FR1 registration — full name, email, phone, password + confirmation, T&Cs, then OTP. */
@Composable
fun RegisterScreen(
    onOtpRequired: (phone: String) -> Unit,
    onNavigateToLogin: () -> Unit,
    viewModel: AuthViewModel = viewModel()
) {
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var acceptedTerms by remember { mutableStateOf(false) }
    val uiState by viewModel.uiState.collectAsState()

    val passwordsMatch = confirmPassword.isEmpty() || password == confirmPassword

    BrandedBackground {
        Column(
            modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(vertical = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(16.dp))
            Image(
                painter = painterResource(R.drawable.logo_full),
                contentDescription = "SafeZone SA",
                modifier = Modifier.fillMaxWidth(0.38f)
            )
            Spacer(Modifier.height(16.dp))

            AuthCard {
                Text("Create Account", fontSize = 22.sp, fontWeight = FontWeight.Bold)
                Text("Join the SafeZone community", fontSize = 13.sp)
                Spacer(Modifier.height(20.dp))

                OutlinedTextField(fullName, { fullName = it }, label = { Text("Full Name") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                Spacer(Modifier.height(10.dp))
                OutlinedTextField(email, { email = it }, label = { Text("Email") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                Spacer(Modifier.height(10.dp))
                OutlinedTextField(phone, { phone = it }, label = { Text("Phone Number") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                Spacer(Modifier.height(10.dp))
                OutlinedTextField(
                    password, { password = it }, label = { Text("Password") },
                    visualTransformation = PasswordVisualTransformation(), modifier = Modifier.fillMaxWidth(), singleLine = true
                )
                Spacer(Modifier.height(10.dp))
                OutlinedTextField(
                    confirmPassword, { confirmPassword = it }, label = { Text("Confirm Password") },
                    visualTransformation = PasswordVisualTransformation(), modifier = Modifier.fillMaxWidth(), singleLine = true,
                    isError = !passwordsMatch
                )
                if (!passwordsMatch) {
                    Text("Passwords don't match", color = AlertRed, fontSize = 11.sp, modifier = Modifier.align(Alignment.Start))
                }
                Spacer(Modifier.height(10.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = acceptedTerms, onCheckedChange = { acceptedTerms = it })
                    Text("I agree to the Terms & Conditions", fontSize = 12.sp)
                }

                if (uiState.errorMessage != null) {
                    Spacer(Modifier.height(4.dp))
                    Text(uiState.errorMessage!!, color = AlertRed, fontSize = 12.sp)
                }

                Spacer(Modifier.height(12.dp))
                Button(
                    onClick = { viewModel.register(fullName, email, phone, password) { onOtpRequired(phone) } },
                    enabled = acceptedTerms && passwordsMatch && !uiState.isLoading,
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue)
                ) {
                    if (uiState.isLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
                    } else {
                        Text("REGISTER")
                    }
                }

                Spacer(Modifier.height(12.dp))
                Row {
                    Text("Already have an account? ")
                    Text("Login", color = RoyalBlue, fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable(onClick = onNavigateToLogin))
                }
            }
            Spacer(Modifier.height(16.dp))
        }
    }
}