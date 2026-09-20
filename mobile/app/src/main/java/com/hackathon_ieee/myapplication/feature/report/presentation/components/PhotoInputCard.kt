package com.hackathon_ieee.myapplication.feature.report.presentation.components

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import coil3.compose.AsyncImage
import com.hackathon_ieee.myapplication.core.media.PhotoFileProvider
import com.hackathon_ieee.myapplication.ui.components.GradientPanel

@Composable
fun PhotoInputCard(
    selectedPhotoUri: String?,
    onPhotoSelected: (String?) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    var showSourceDialog by rememberSaveable {
        mutableStateOf(false)
    }

    var pendingCameraUri by rememberSaveable {
        mutableStateOf<String?>(null)
    }

    var photoMessage by rememberSaveable {
        mutableStateOf<String?>(null)
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { photoCaptured ->
        if (photoCaptured) {
            onPhotoSelected(pendingCameraUri)
            photoMessage = null
        } else {
            photoMessage = "Photo capture was cancelled."
        }
        pendingCameraUri = null
    }

    val launchCamera = {
        runCatching {
            PhotoFileProvider.createCameraPhotoUri(context)
        }.onSuccess { photoUri ->
            pendingCameraUri = photoUri.toString()
            cameraLauncher.launch(photoUri)
        }.onFailure {
            photoMessage = "Camera could not be opened. Please try again."
        }
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { permissionGranted ->
        if (permissionGranted) {
            launchCamera()
        } else {
            photoMessage = "Camera permission was denied. You can still choose a photo from the gallery."
        }
    }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { photoUri ->
        if (photoUri != null) {
            runCatching {
                context.contentResolver.takePersistableUriPermission(
                    photoUri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            }
            onPhotoSelected(photoUri.toString())
            photoMessage = null
        }
    }

    GradientPanel(
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (selectedPhotoUri == null) {
                Text(text = "No photo selected")

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedButton(
                    onClick = {
                        showSourceDialog = true
                    }
                ) {
                    Text(text = "Add Photo")
                }
            } else {
                AsyncImage(
                    model = Uri.parse(selectedPhotoUri),
                    contentDescription = "Selected water observation photo",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(210.dp)
                        .clip(RoundedCornerShape(14.dp)),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = {
                            showSourceDialog = true
                        }
                    ) {
                        Text(text = "Change Photo")
                    }

                    TextButton(
                        onClick = {
                            onPhotoSelected(null)
                            photoMessage = null
                        }
                    ) {
                        Text(
                            text = "Remove",
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }

            photoMessage?.let { message ->
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = message,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }

    if (showSourceDialog) {
        AlertDialog(
            onDismissRequest = {
                showSourceDialog = false
            },
            title = {
                Text(text = "Add Photo")
            },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            showSourceDialog = false

                            if (
                                ContextCompat.checkSelfPermission(
                                    context,
                                    Manifest.permission.CAMERA
                                ) == PackageManager.PERMISSION_GRANTED
                            ) {
                                launchCamera()
                            } else {
                                cameraPermissionLauncher.launch(
                                    Manifest.permission.CAMERA
                                )
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = "Take Photo")
                    }

                    OutlinedButton(
                        onClick = {
                            showSourceDialog = false
                            photoPickerLauncher.launch(
                                PickVisualMediaRequest(
                                    ActivityResultContracts.PickVisualMedia.ImageOnly
                                )
                            )
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = "Choose from Gallery")
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showSourceDialog = false
                    }
                ) {
                    Text(text = "Cancel")
                }
            }
        )
    }
}
