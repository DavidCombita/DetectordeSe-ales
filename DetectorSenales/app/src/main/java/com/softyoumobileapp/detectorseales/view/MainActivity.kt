package com.softyoumobileapp.detectorseales.view

import android.Manifest
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import com.softyoumobileapp.detectorseales.view.theme.DetectorSeñalesTheme
import java.io.ByteArrayOutputStream
import java.io.InputStream

class MainActivity : ComponentActivity() {

    private var bitmap: Bitmap? = null
    val REQUEST_IMAGE_CAPTURE = 1

    val pickMedia = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        // Callback is invoked after the user selects a media item or closes the
        // photo picker.
        if (uri != null) {
            val inputStream: InputStream? = contentResolver.openInputStream(uri)
            bitmap = BitmapFactory.decodeStream(inputStream)
            //TODO pasarlo al viewmodel
            if (bitmap != null) {
                val outputStream = ByteArrayOutputStream()
                bitmap!!.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
                val byteArray: ByteArray = outputStream.toByteArray()
            }
        } else {
            Log.d("PhotoPicker", "No media selected")
        }
    }

    private val takePictureLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val imageBitmap = result.data?.extras?.get("data") as? Bitmap
                //TODO pasarlo al viewmodel
                if (imageBitmap != null) {
                    val outputStream = ByteArrayOutputStream()
                    imageBitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
                    val byteArray: ByteArray = outputStream.toByteArray()
                    Log.d("PhotoPicker", "Seleccionada una nueva imagen: ${byteArray}")
                }
            } else {
                Log.d("PhotoPicker camera", "Captura de imagen cancelada")
            }
        }

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DetectorSeñalesTheme {
                Scaffold(
                    topBar = {
                        CenterAlignedTopAppBar(
                            title = {
                                Text(
                                    "Top app bar",
                                    modifier = Modifier.padding(30.dp),
                                    color = Color.White
                                )
                            },
                            colors = TopAppBarDefaults.mediumTopAppBarColors(
                                containerColor = Color(114, 147, 243)
                            )
                        )
                    },
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.White)
                ) { innerPadding ->
                    Column(
                        modifier = Modifier
                            .background(Color.White)
                            .padding(50.dp)
                    ) {
                        Divider(
                            Modifier
                                .height(30.dp),
                            color = Color.White
                        )
                        val context = LocalContext.current
                        ScreenOrganisms(onClickSelectedCamera = { requestCameraPermission(context) },
                            onClickSelectedGallery = { selectPictureGalery() })
                    }
                }
            }
        }
    }

    fun selectPictureGalery() {
        pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private fun requestCameraPermission(context: Context) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED
        ) {
            // Si el permiso no ha sido otorgado, solicítalo.
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA),
                1001
            )
        } else {
            // El permiso de la cámara ya está concedido, puedes lanzar la intención de tomar la foto.
            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            if (takePictureIntent.resolveActivity(context.packageManager) != null) {
                takePictureLauncher.launch(takePictureIntent)
            }
        }
    }
}

@Composable
fun ScreenOrganisms(onClickSelectedCamera: () -> Unit,onClickSelectedGallery: () -> Unit) {
    var showDetail = true
    Column (modifier = Modifier
        .fillMaxSize()
        .background(
            Color(217, 217, 217),
            RoundedCornerShape(50.dp)
        ),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally){
        if(showDetail){
            ButtonsOrganisms(onClickSelectedCamera = onClickSelectedCamera,
                onClickSelectedGallery = onClickSelectedGallery )
            Text(text = "Attach your traffic sign here...",
                color = Color.Black,
                fontSize = 18.sp,
                modifier = Modifier.padding(20.dp)
            )
        }else{

        }
    }
}

@Composable
fun ButtonsOrganisms(onClickSelectedCamera: () -> Unit, onClickSelectedGallery: () -> Unit){
    Row {
        CardButtonMolecule(icon = Icons.Default.Add,
            text = "Take Picture", 12.sp, onClickSelected = onClickSelectedCamera)
        CardButtonMolecule(icon = Icons.Default.AccountBox,
            text = "Select Picture", 12.sp, onClickSelected = onClickSelectedGallery)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CardButtonMolecule(icon: ImageVector, text: String,
                       textSize: TextUnit,
                       onClickSelected: () -> Unit){

    Card(onClick = onClickSelected,
        modifier = Modifier
        .padding(5.dp),
        shape = RoundedCornerShape(35.dp)
    ){
        Column (horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .background(Color.Black, CircleShape)
                .padding(20.dp)
        ){
            Icon(imageVector = icon,
                contentDescription = "Icon añadir foto galeria",
                tint = Color.White
            )
            Text(text = text,
                color = Color.White,
                fontSize = textSize)
        }
    }
}

@Composable
fun PermissionDeniedDialog(
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Permission Denied") },
        text = {
            Text("Camera and storage permissions are required to use this app.")
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("OK")
            }
        }
    )
}

@Composable
fun PermissionRationaleDialog(
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Permission Required") },
        text = {
            Text("Camera and storage permissions are required to use this app.")
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("OK")
            }
        }
    )
}