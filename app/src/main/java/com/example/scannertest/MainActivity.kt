package com.example.scannertest

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.scannertest.ui.theme.ScannerTestTheme
import com.google.accompanist.permissions.ExperimentalPermissionsApi

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalPermissionsApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ScannerTestTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    MainContent()
                }
            }
        }
    }
}

@ExperimentalPermissionsApi
@Composable
fun MainContent(modifier: Modifier = Modifier){
    val context = LocalContext.current
    Permission(
        permision = android.Manifest.permission.CAMERA,
        rationale = "You want to scan barcodes so I need access to the camera",
        permissionNotAvailableContent = {
            Column(modifier) {
                Text(text = "No Camera")
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = {
                    context.startActivity(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply { 
                        data= Uri.fromParts("package", context.packageName, null)
                    })
                }) {
                    Text(text = "Open Settings")
                }
            }
        }
    ){
        Text(text = "It Worked")
    }
}

