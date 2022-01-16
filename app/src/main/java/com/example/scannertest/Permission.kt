package com.example.scannertest

import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionRequired
import com.google.accompanist.permissions.rememberPermissionState

@ExperimentalPermissionsApi
@Composable
fun Permission (
    permision: String = android.Manifest.permission.CAMERA,
    rationale: String = "This permission is important for this app.  Please grant the permission.",
    permissionNotAvailableContent: @Composable () -> Unit = {},
    content: @Composable () -> Unit = {}
){
    val permissionState = rememberPermissionState(permision)
    PermissionRequired(
        permissionState = permissionState,
        permissionNotGrantedContent = {
            Rationale(
                text = rationale,
                onRequestPermission = {permissionState.launchPermissionRequest()}
            )
        },
        permissionNotAvailableContent = permissionNotAvailableContent,
        content = content
    )
}

@Composable
private fun Rationale(
    text: String,
    onRequestPermission: ()-> Unit
){
    AlertDialog(
        onDismissRequest = { /* Don't */ },
        title = {
            Text(text = "Permission Request")
        },
        text = {
            Text(text)
        },
        confirmButton = {
            Button(onClick = onRequestPermission)   {
                Text("Ok")

            }
        }
    )
}