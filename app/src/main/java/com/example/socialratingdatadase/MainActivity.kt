package com.example.socialratingdatadase

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.example.socialratingdatadase.data.MainDb
import com.example.socialratingdatadase.data.Member
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
    private val numberQR = mutableStateOf("")

    private val scanLauncher = registerForActivityResult(ScanContract()) { result ->
        if (result.contents == null) {
            Toast.makeText(this, "Scan content is null", Toast.LENGTH_SHORT).show()
        } else {
            CoroutineScope(Dispatchers.IO).launch {
                val memberByQr = mainDb.dao.getMemberByQr(result.contents)
                if (memberByQr == null) {
                    runOnUiThread {
                        insertDialogState.value = true
                        numberQR.value = result.contents
                    }
                }

            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SocialRatingDataDaseTheme {
                if (insertDialogState.value) {
                    InputNameDialog(insertDialogState = insertDialogState) { name ->
                        GetDialogName(
                            InsertDialogState = insertDialogState,
                            numberQr = numberQR.value
                        ) { name ->
                            CoroutineScope(Dispatchers.IO).launch {
                                mainDb.dao.insertMember(
                                    Member(
                                        null,
                                        0,
                                        name,
                                        numberQR.value
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    private fun scan() {
        scanLauncher.launch(getScanOptions())
    }

//    private fun scanCheck() {
//        scanCheckLauncher.launch(getScanOptions())
//    }

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
    InsertDialogState: MutableState<Boolean>, numberQr: String, onSubmit: (String) -> Unit
) {
    if (InsertDialogState.value) {
        InputNameDialog(insertDialogState = InsertDialogState) { name ->
            onSubmit(name)
        }
    }
}