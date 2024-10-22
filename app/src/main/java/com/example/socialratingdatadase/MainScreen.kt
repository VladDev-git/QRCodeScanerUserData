package com.example.socialratingdatadase

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.socialratingdatadase.data.MainDb
import com.example.socialratingdatadase.data.Member
import com.example.socialratingdatadase.ui.theme.DialogDarkGray
import com.example.socialratingdatadase.ui.theme.MemberCardColor
import com.google.accompanist.systemuicontroller.rememberSystemUiController

@Composable
fun MainScreen(
    mainDb: MainDb, onPhotoClick: (String) -> Unit,
    onScanClick: () -> Unit) {

    val systemUiController = rememberSystemUiController()
    val darkTheme = true
    if(darkTheme){
        systemUiController.setSystemBarsColor(
            color = Color.Transparent
        )
    }else{
        systemUiController.setSystemBarsColor(
            color = Color.White
        )
    }
    val members = mainDb.dao.getAllMembers().collectAsState(initial = emptyList()).value

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DialogDarkGray)
    ) {
        Spacer(modifier = Modifier.size(30.dp))
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.8f)
        ) {
            items(members) { member ->
                MemberCard(member = member, onPhotoClick = { qrCode -> onPhotoClick(qrCode) })
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Button(onClick = {
                onScanClick()
            },
                modifier = Modifier
                    .background(Color.Gray)
            ) {
                Text(text = "Scan QR")
            }
        }
    }
}

@Composable
fun MemberCard(member: Member, onPhotoClick: (String) -> Unit) {
    //val QRCode = member.numberQR
    Card(
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = MemberCardColor
        ),
        modifier = Modifier
            .padding(10.dp)
            .fillMaxWidth()
    ) {
        Row {
            member.imageUri?.let {
                Image(
                    painter = rememberAsyncImagePainter(model = it),
                    contentDescription = "Member image",
                    modifier = Modifier
                        .size(64.dp)
                        .clickable {
                            onPhotoClick(member.numberQR)
                        },
                    contentScale = ContentScale.Crop
                )
            } ?: Image(
                painter = rememberAsyncImagePainter(model = R.drawable.place_holder_image),
                contentDescription = "Member placeholder image",
                modifier = Modifier
                    .size(64.dp)
                    .clickable {
                        onPhotoClick(member.numberQR)
                    },
                contentScale = ContentScale.Crop,
            )
            Column {
                Text(
                    text = "Name: ${member.name}",
                    fontSize = 20.sp,
                    color = Color.White
                )
                Text(
                    text = "Rating: ${member.rating}",
                    fontSize = 20.sp,
                    color = Color.White
                )
            }
        }
    }
}