package com.softyoumobileapp.detectorseales.view

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.softyoumobileapp.detectorseales.data.models.SignalTransit
import com.softyoumobileapp.detectorseales.view.theme.DetectorSeñalesTheme
import com.softyoumobileapp.detectorseales.view.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.io.ByteArrayOutputStream
import java.io.InputStream
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var viewModel: MainViewModel

    val pickMedia = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        // Callback is invoked after the user selects a media item or closes the
        // photo picker.
        if (uri != null) {
            val bitmap = BitmapFactory.decodeStream(contentResolver.openInputStream(uri))
            viewModel.predictImage(bitmap, this.applicationContext)
        } else {
            Log.d("PhotoPicker", "No media selected")
        }
    }

    private val takePictureLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val imageBitmap = result.data?.extras?.get("data") as? Bitmap
                viewModel.predictImage(imageBitmap, this.applicationContext)
            } else {
                Log.d("PhotoPicker camera", "Captura de imagen cancelada")
            }
        }

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // Estado para controlar la visibilidad del diálogo
            val alert by viewModel.signalPre.collectAsState()

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

                        if(alert != null){
                            ShowPictureDialog(
                                onDismiss = { viewModel.clearAlert() },
                                alert!!.id,
                                alert!!.name,
                                alert!!.description
                            )
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun ShowPictureDialog(
        onDismiss: () -> Unit,
        id: Int,
        tittle: String,
        description: String
    ) {
        val context = LocalContext.current
        val imageId = context.resources.getIdentifier(if(id>=10)"i000${id}" else "i0000${id}",
            "drawable", context.packageName)


        AlertDialog(
            onDismissRequest = { onDismiss() },
            title = { Text(text = tittle)},
            text = {
                Image(painter = painterResource(id = imageId),
                    contentDescription = "Image complete",
                    modifier = Modifier
                        .width(500.dp)
                        .height(400.dp)
                        .padding(10.dp))

                Text(text = description)
            },
            confirmButton = {
                Button(
                    onClick = {
                        onDismiss()
                    }
                ) {
                    Text("Cerrar")
                }
            }
        )


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
fun ScreenOrganisms(
    onClickSelectedCamera: () -> Unit,
    onClickSelectedGallery: () -> Unit
) {
    Column (modifier = Modifier
        .fillMaxSize()
        .background(
            Color(217, 217, 217),
            RoundedCornerShape(50.dp)
        ),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally){

        ButtonsOrganisms(onClickSelectedCamera = onClickSelectedCamera,
            onClickSelectedGallery = onClickSelectedGallery )
        Text(text = "Attach your traffic sign here...",
            color = Color.Black,
            fontSize = 18.sp,
            modifier = Modifier.padding(20.dp)
        )
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