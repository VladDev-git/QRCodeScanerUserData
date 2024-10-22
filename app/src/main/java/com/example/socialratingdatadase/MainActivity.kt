package com.example.socialratingdatadase

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.example.socialratingdatadase.data.MainDb
import com.example.socialratingdatadase.data.Member
import com.example.socialratingdatadase.dialogs.GetRatingScoreDialog
import com.example.socialratingdatadase.dialogs.InputNameDialog
import com.example.socialratingdatadase.ui.theme.SocialRatingDataDaseTheme
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var mainDb: MainDb

    private val insertDialogState = mutableStateOf(false)
    private val ratingDialogState = mutableStateOf(false)
    private val selectedPhotoUri = mutableStateOf<Uri?>(null)
    private var currentQrItem = mutableStateOf<String?>(null)

    private val scanLauncher = registerForActivityResult(ScanContract()) { result ->
        if (result.contents == null) {
            Toast.makeText(this, "Scan content is null", Toast.LENGTH_SHORT).show()
        } else {
            CoroutineScope(Dispatchers.IO).launch {
                val memberByQr = mainDb.dao.getMemberByQr(result.contents)
                if (memberByQr == null) {
                    runOnUiThread {
                        insertDialogState.value = true
                        setContent {
                            SocialRatingDataDaseTheme {
                                if (insertDialogState.value) {
                                    GetDialogName(
                                        DialogStateValue = insertDialogState
                                    ) { name ->
                                        CoroutineScope(Dispatchers.IO).launch {
                                            mainDb.dao.insertMember(
                                                Member(
                                                    null,
                                                    0,
                                                    name,
                                                    numberQR = result.contents,
                                                    imageUri = null
                                                )
                                            )
                                            runOnUiThread {
                                                insertDialogState.value = false
                                                setContent {
                                                    SocialRatingDataDaseTheme {
                                                        MainScreen(mainDb = mainDb,
                                                            onPhotoClick = { qr ->
                                                                currentQrItem.value = qr
                                                                selectPhoto(qr)
                                                            },
                                                            onScanClick = { scan() }
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                } else {
                    runOnUiThread {
                        Toast.makeText(this@MainActivity, "Duplicated item", Toast.LENGTH_SHORT).show()
                        ratingDialogState.value = true
                        setContent {
                            SocialRatingDataDaseTheme {
                                if (ratingDialogState.value) {
                                    GetRatingScoreDialog(ratingDialogState = ratingDialogState) { rating ->
                                        CoroutineScope(Dispatchers.IO).launch {
                                            val newRating = memberByQr.rating + rating.toInt()
                                            mainDb.dao.updateMember(memberByQr.copy(rating = newRating))
                                        }
                                        runOnUiThread {
                                            ratingDialogState.value = false
                                            setContent {
                                                SocialRatingDataDaseTheme {
                                                    MainScreen(mainDb = mainDb,
                                                        onPhotoClick = { qr ->
                                                            currentQrItem.value = qr
                                                            selectPhoto(qr)
                                                        },
                                                        onScanClick = { scan() }
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private val photoPickerLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            result.data?.data?.let { uri ->
                selectedPhotoUri.value = uri
                updatePhotoUriInDataBase(uri)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SocialRatingDataDaseTheme {
                MainScreen(mainDb = mainDb,
                    onPhotoClick = { qr ->
                        currentQrItem.value = qr
                        selectPhoto(currentQrItem.value.toString())
                    }, onScanClick = { scan() })
            }
        }
    }

    private fun scan() {
        scanLauncher.launch(getScanOptions())
    }

    private fun selectPhotoFromGallery(launcher: ActivityResultLauncher<Intent>) {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        launcher.launch(intent)
    }

    private fun selectPhoto(qrCode: String) {
        currentQrItem.value = qrCode
        selectPhotoFromGallery(photoPickerLauncher)
    }

    private fun updatePhotoUriInDataBase(uri: Uri) {
        CoroutineScope(Dispatchers.IO).launch {
            val member = mainDb.dao.getMemberByQr(currentQrItem.value ?: "")
            if (member != null) {
                runOnUiThread {
                    Toast.makeText(
                        this@MainActivity,
                        "Member ${member.imageUri}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                mainDb.dao.updateMember(member.copy(imageUri = uri.toString()))
                val memberlater = mainDb.dao.getMemberByQr(currentQrItem.value ?: "")
                if (memberlater != null) {
                    runOnUiThread {
                        Toast.makeText(
                            this@MainActivity,
                            "Member ${memberlater.imageUri}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }

    private fun getScanOptions(): ScanOptions {
        return ScanOptions().apply {
            val options = ScanOptions()
            options.setDesiredBarcodeFormats(ScanOptions.QR_CODE)
            options.setPrompt("Scan a QR code")
            options.setCameraId(0)
            options.setBeepEnabled(false)
            options.setBarcodeImageEnabled(true)
        }
    }
}

@Composable
fun GetDialogName(
    DialogStateValue: MutableState<Boolean>, onSubmit: (String) -> Unit
) {
    if (DialogStateValue.value) {
        InputNameDialog(insertDialogState = DialogStateValue) { name ->
            onSubmit(name)
        }
    }
}

@Composable
fun GetDialogRating(
    DialogStateValue: MutableState<Boolean>, onSubmit: (Int) -> Unit
) {
    if (DialogStateValue.value) {
        GetRatingScoreDialog(ratingDialogState = DialogStateValue) { rating ->
            onSubmit(rating.toInt())
        }
    }
}












