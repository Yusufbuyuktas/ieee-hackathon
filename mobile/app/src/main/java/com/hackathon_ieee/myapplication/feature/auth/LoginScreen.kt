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
import androidx.compose.runtime.remember
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
fun LoginScreen(
    onBack: () -> Unit,
    onRegister: () -> Unit,
    onSignIn: (String, String) -> Unit,
    isSubmitting: Boolean,
    authenticationError: String?,
    onInputChanged: () -> Unit,
    modifier: Modifier = Modifier
) {
    var email by rememberSaveable {
        mutableStateOf("")
    }
    var password by remember {
        mutableStateOf("")
    }
    var passwordVisible by rememberSaveable {
        mutableStateOf(false)
    }
    var showEmailError by remember {
        mutableStateOf(false)
    }
    var showPasswordError by remember {
        mutableStateOf(false)
    }
    val normalizedEmail = email.trim()
    val isEmailValid = normalizedEmail.contains("@") &&
        normalizedEmail.substringAfter("@").contains(".") &&
        normalizedEmail.substringAfter(".").isNotBlank()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {

        Column(
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Text(
                text = "Sign in",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = "Sign in to continue to water observations and reporting.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            SubtlePanel {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
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
                            {
                                Text(text = "Please enter a valid email address.")
                            }
                        } else {
                            null
                        },
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
                            {
                                Text(text = "Please enter your password.")
                            }
                        } else {
                            null
                        },
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

                    if (authenticationError != null) {
                        Text(
                            text = authenticationError,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error
                        )
                    }

                    Button(
                        onClick = {
                            showEmailError = !isEmailValid
                            showPasswordError = password.isBlank()

                            if (isEmailValid && password.isNotBlank()) {
                                onSignIn(normalizedEmail, password)
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
                            Text(text = "Sign In")
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
                    text = "Don’t have an account?",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                TextButton(onClick = onRegister) {
                    Text(text = "Create account")
                }
            }
        }
    }
}
