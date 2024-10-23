package com.example.socialratingdatadase

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.socialratingdatadase.data.MainDb
import com.example.socialratingdatadase.data.Member
import com.example.socialratingdatadase.ui.theme.MemberCardColor
import com.example.socialratingdatadase.ui.theme.ScanButtonColor
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

@Composable
fun MainScreen(
    mainDb: MainDb, onPhotoClick: (String) -> Unit,
    onScanClick: () -> Unit
) {

    val systemUiController = rememberSystemUiController()
    val darkTheme = true
    if (darkTheme) {
        systemUiController.setSystemBarsColor(
            color = Color.Transparent,
            darkIcons = true
        )
    } else {
        systemUiController.setSystemBarsColor(
            color = Color.White
        )
    }
    val members = mainDb.dao.getAllMembers().collectAsState(initial = emptyList()).value

    Image(
        painter = painterResource(id = R.drawable.com_china), contentDescription = "Sky background",
        modifier = Modifier
            .fillMaxSize()
            .alpha(0.6f),
        contentScale = ContentScale.Crop
    )
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Spacer(modifier = Modifier.size(40.dp))
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.85f)
        ) {
            items(members) { member ->
                MemberCard(member = member, onPhotoClick = { qrCode -> onPhotoClick(qrCode) },
                    onDeleteClick = { memberToDelete ->
                        CoroutineScope(Dispatchers.IO).launch {
                            mainDb.dao.deleteMember(memberToDelete)
                        }
                    }
                )
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = {
                    onScanClick()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 10.dp, end = 10.dp, top = 5.dp, bottom = 5.dp)
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ScanButtonColor
                )
            ) {
                Text(text = "Scan QR")
            }
        }
    }
}

@Composable
fun MemberCard(member: Member, onPhotoClick: (String) -> Unit, onDeleteClick: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        shape = RoundedCornerShape(13.dp),
        colors = CardDefaults.cardColors(
            containerColor = MemberCardColor
        ),
        modifier = Modifier
            .padding(start = 10.dp, end = 10.dp, top = 5.dp, bottom = 5.dp)
            .fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row {
                member.imageUri?.let {
                    Image(
                        painter = rememberAsyncImagePainter(model = File(member.imageUri)),
                        contentDescription = "Member image",
                        modifier = Modifier
                            .size(80.dp)
                            .clickable {
                                onPhotoClick(member.numberQR)
                            },
                        contentScale = ContentScale.Crop
                    )
                } ?: Image(
                    painter = rememberAsyncImagePainter(model = R.drawable.place_holder_image),
                    contentDescription = "Member placeholder image",
                    modifier = Modifier
                        .size(80.dp)
                        .clickable {
                            onPhotoClick(member.numberQR)
                        },
                    contentScale = ContentScale.Crop,
                )
                Column {
                    Text(
                        text = "Name: ${member.name}",
                        fontSize = 16.sp,
                        color = Color.Yellow,
                        modifier = Modifier.padding(start = 10.dp, top = 5.dp)
                    )
                    Text(
                        text = "Rating: ${member.rating}",
                        fontSize = 16.sp,
                        color = Color.Yellow,
                        modifier = Modifier.padding(start = 10.dp, top = 5.dp)
                    )
                }
            }
            IconButton(
                onClick = { expanded = true },
                colors = IconButtonDefaults.iconButtonColors(
                    contentColor = Color.Yellow
                )
            ) {
                Icon(imageVector = Icons.Default.MoreVert,
                    contentDescription = "Menu")
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                offset = DpOffset(x = 0.dp, y = 0.dp)
            ) {
                DropdownMenuItem(
                    text = { Text("Delete") },
                    onClick = {
                        expanded = false
                        onDeleteClick(member.numberQR)
                    }
                )
            }
        }
    }
}