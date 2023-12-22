package com.example.serialnumberchecker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.serialnumberchecker.ui.theme.SerialNumberCheckerTheme
import com.pax.poslink.PosLink
import com.pax.poslink.poslink.POSLinkCreator

class MainActivity : ComponentActivity() {
    private lateinit var posLink: PosLink
    private val songsViewModel by viewModels<SerialNumberViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        posLink = POSLinkCreator.createPoslink(applicationContext)
        setContent {
            SerialNumberCheckerTheme {
                val isClicked = remember { mutableStateOf(false) }
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(10.dp),
                    color = MaterialTheme.colorScheme.background,
                ) {
                    // getSerialNum(songsViewModel, posLink)
                    Greeting(songsViewModel, posLink)
                }
            }
        }
    }
}

@Composable
fun Greeting(
    songsViewModel: SerialNumberViewModel,
    posLink: PosLink,
) {
    val serialNumberState by songsViewModel._serialNumber.collectAsState()
    initialUI(serialNumberState, songsViewModel, posLink)
}

@Composable
fun isDataLoading() {
    Box(
        modifier = Modifier
            .fillMaxWidth(),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            Modifier
                .matchParentSize()
                .background(Color.Gray.copy(alpha = 0.5f)),

        )

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            CircularProgressIndicator()
            Text(text = "Processing..")
        }
    }
}

@Composable
fun initialUI(
    serialNumberState: SerialNumberCase,
    songsViewModel: SerialNumberViewModel,
    posLink: PosLink,
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(5.dp)) {
        Row {
            when (serialNumberState) {
                is SerialNumberCase.default -> Button(onClick = {
                    songsViewModel.getPaxSerialNumber(posLink)
                }, elevation = ButtonDefaults.buttonElevation(10.dp), modifier = Modifier.fillMaxWidth()) {
                    Text(text = "Get Serial Number", fontWeight = FontWeight.Bold)
                }
                is SerialNumberCase.isLoading -> isDataLoading()
                is SerialNumberCase.onSuccess -> Text(
                    text = "Success: " + serialNumberState.serialNumber.toString(),
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Red,
                    modifier = Modifier.padding(0.dp),
                )

                is SerialNumberCase.onError -> retryUI(songsViewModel, serialNumberState, posLink)
                else -> {}
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = "Steps to follow if serial number is not visible above before installing FIDO in order to make sure that serial number is accessible:\n" +
                "\n" +
                "1. Latest Firmware should be installed.\n" +
                "2. Check if there is any BroadPos App installed already such as BroadPos Test then uninstall it in order to install BroadPos Manager.\n" +
                "3. Make sure BroadPos manager is installed. It will not be visible on the OS on A920 pro. But it should be installed at first place.\n" +
                "4. Install Rapid connect app from PUSH task from pax store.\n" +
                "5. Install PosLink Demo app in order to initialize or test COMM SETTING's AIDL protocol. On COMM SETTING's tab click on start service. It should be showing success.\n" +
                "6. Open serial number checker standalone app and it should be showing serial number of the PAX A920 pro device.\n" +
                "7. Now install FIDO.\n",
            modifier = Modifier
                .background(color = Color.Gray.copy(alpha = 0.1f))
                .verticalScroll(rememberScrollState()),
        )
    }
}

@Composable
fun retryUI(viewModel: SerialNumberViewModel, serialNumberState: SerialNumberCase.onError, posLink: PosLink) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Button(onClick = {
            viewModel.getPaxSerialNumber(posLink)
        }) {
            Text(text = "Get Serial Number")
        }

        Text(text = serialNumberState.errorMsg.toString(), fontSize = 28.sp, color = Color.Red, modifier = Modifier.padding(5.dp))
    }
}
