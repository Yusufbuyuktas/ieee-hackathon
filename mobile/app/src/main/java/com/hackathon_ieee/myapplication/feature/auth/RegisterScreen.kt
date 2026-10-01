package com.hackathon_ieee.myapplication.feature.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.hackathon_ieee.myapplication.ui.components.PasswordVisibilityIcon
import com.hackathon_ieee.myapplication.ui.components.SubtlePanel

@Composable
fun RegisterScreen(
    onBack: () -> Unit,
    onLogin: () -> Unit,
    onRegistered: (String, String, String) -> Unit,
    isSubmitting: Boolean,
    registrationError: String?,
    onInputChanged: () -> Unit,
    modifier: Modifier = Modifier
) {
    var fullName by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var passwordVisible by rememberSaveable { mutableStateOf(false) }
    var showNameError by rememberSaveable { mutableStateOf(false) }
    var showEmailError by rememberSaveable { mutableStateOf(false) }
    var showPasswordError by rememberSaveable { mutableStateOf(false) }

    val normalizedEmail = email.trim()
    val isEmailValid = normalizedEmail.contains("@") &&
        normalizedEmail.substringAfter("@").contains(".") &&
        normalizedEmail.substringAfterLast(".").isNotBlank()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {


        Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
            Text(
                text = "Create account",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = "Create an account to continue to water observations and reporting.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            SubtlePanel {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    OutlinedTextField(
                        value = fullName,
                        onValueChange = {
                            fullName = it
                            showNameError = false
                            onInputChanged()
                        },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text(text = "Full name") },
                        isError = showNameError,
                        supportingText = if (showNameError) {
                            { Text(text = "Please enter your full name.") }
                        } else null,
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                    )

                    OutlinedTextField(
                        value = email,
                        onValueChange = {
                            email = it
                            showEmailError = false
                            onInputChanged()
                        },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text(text = "Email") },
                        isError = showEmailError,
                        supportingText = if (showEmailError) {
                            { Text(text = "Please enter a valid email address.") }
                        } else null,
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email,
                            imeAction = ImeAction.Next
                        )
                    )

                    OutlinedTextField(
                        value = password,
                        onValueChange = {
                            password = it
                            showPasswordError = false
                            onInputChanged()
                        },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text(text = "Password") },
                        isError = showPasswordError,
                        supportingText = if (showPasswordError) {
                            { Text(text = "Please enter a password.") }
                        } else null,
                        singleLine = true,
                        visualTransformation = if (passwordVisible) {
                            VisualTransformation.None
                        } else {
                            PasswordVisualTransformation()
                        },
                        trailingIcon = {
                            IconButton(
                                onClick = {
                                    passwordVisible = !passwordVisible
                                }
                            ) {
                                PasswordVisibilityIcon(passwordVisible = passwordVisible)
                            }
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Done
                        )
                    )

                    if (registrationError != null) {
                        Text(
                            text = registrationError,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error
                        )
                    }

                    Button(
                        onClick = {
                            val isNameValid = fullName.isNotBlank()
                            val isPasswordValid = password.isNotBlank()
                            showNameError = !isNameValid
                            showEmailError = !isEmailValid
                            showPasswordError = !isPasswordValid

                            if (isNameValid && isEmailValid && isPasswordValid) {
                                onRegistered(fullName.trim(), normalizedEmail, password)
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isSubmitting
                    ) {
                        if (isSubmitting) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        } else {
                            Text(text = "Create Citizen Account")
                        }
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Already have an account?",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                TextButton(onClick = onLogin) {
                    Text(text = "Log in")
                }
            }
        }
    }
}
