package cn.edu.seu.travelapp.ui.components

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Close
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cn.edu.seu.travelapp.data.DataStorage
import cn.edu.seu.travelapp.ui.state.TravelAppState
import cn.edu.seu.travelapp.ui.state.TravelAppViewState
import cn.edu.seu.travelapp.viewmodel.HomeViewModel
import coil.compose.AsyncImage
import kotlinx.coroutines.delay

@Composable
fun PictxtEditor(
    travelAppState: TravelAppState,
    homeViewModel: HomeViewModel,
    dataStore: DataStorage
) {
    if (travelAppState.isLogin.value) {
        PictxtEditorComponent(
            travelAppState = travelAppState,
            homeViewModel = homeViewModel,
            dataStore = dataStore,
        )
    } else {
        AlertDialog(
            title = {
                Text(text = "提示")
            },
            text = {
                Text(text = "您还未登录，请先登录！")
            },
            onDismissRequest = {
                when (travelAppState.travelAppViewState.value) {
                    TravelAppViewState.HOME -> travelAppState.navController.navigate(route = "home")
                    TravelAppViewState.EXPLORE -> travelAppState.navController.navigate(route = "explore")
                    TravelAppViewState.FAVORITE -> travelAppState.navController.navigate(route = "favorite")
                    TravelAppViewState.ME -> travelAppState.navController.navigate(route = "me")
                    else -> {}
                }
                travelAppState.topBarState.value = true
                travelAppState.bottomBarState.value = true
            },
            buttons = {
                Row(
                    modifier = Modifier.padding(all = 8.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {

                            when (travelAppState.travelAppViewState.value) {
                                TravelAppViewState.HOME -> travelAppState.navController.navigate(
                                    route = "home"
                                )
                                TravelAppViewState.EXPLORE -> travelAppState.navController.navigate(
                                    route = "explore"
                                )
                                TravelAppViewState.FAVORITE -> travelAppState.navController.navigate(
                                    route = "favorite"
                                )
                                TravelAppViewState.ME -> travelAppState.navController.navigate(route = "me")
                                else -> {}
                            }
                            travelAppState.topBarState.value = true
                            travelAppState.bottomBarState.value = true
                        }
                    ) {
                        Text("好")
                    }
                }
            }
        )
    }
}

@Composable
fun PictxtEditorComponent(
    travelAppState: TravelAppState,
    homeViewModel: HomeViewModel,
    dataStore: DataStorage
) {
    val focusManager = LocalFocusManager.current
    val token = dataStore.getAccessToken.collectAsState(initial = "").value
    val context = LocalContext.current
    // close dialog
    val closeDialogState = remember { mutableStateOf(false) }

    // save
    val saveState = homeViewModel.pictxtEditorSaveState
    val saveSnackbarState = remember {
        mutableStateOf(false)
    }

    // check before update
    val checkState = remember {
        mutableStateOf(false)
    }

    // progress bar
    val progressState = remember { mutableStateOf(false) }

    BackHandler(enabled = true) {
        closeDialogState.value = true
    }

    val initPhotosPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(
            maxItems = 9
        ),
        onResult = {
            if (it.isEmpty()) {
                when (travelAppState.travelAppViewState.value) {
                    TravelAppViewState.HOME -> travelAppState.navController.navigate(
                        route = "home"
                    )
                    TravelAppViewState.EXPLORE -> travelAppState.navController.navigate(
                        route = "explore"
                    )
                    TravelAppViewState.FAVORITE -> travelAppState.navController.navigate(
                        route = "favorite"
                    )
                    TravelAppViewState.ME -> travelAppState.navController.navigate(route = "me")
                    else -> {}
                }
                travelAppState.topBarState.value = true
                travelAppState.bottomBarState.value = true
            } else {
                it.forEach { uri ->
                    homeViewModel.pictxtImgList.add(uri)
                }
            }
        }
    )
    LaunchedEffect(key1 = true) {
        Log.d("saveState", saveState.value.toString())
        if (!saveState.value) {
            initPhotosPickerLauncher.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
            )
        }
    }
    val photosPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(
            maxItems = if (homeViewModel.pictxtImgList.size <= 7) {
                9 - homeViewModel.pictxtImgList.size
            } else {
                2
            }
        ),
        onResult = {
            if (it.isNotEmpty()) {
                it.forEach { uri ->
                    if (homeViewModel.pictxtImgList.size < 9) {
                        homeViewModel.pictxtImgList.add(uri)
                    }
                }
            }
        }
    )

    val singlePhotoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = {
            if (it != null) {
                homeViewModel.pictxtImgList.add(it)
            }
        }
    )

    if (closeDialogState.value) {
        CloseAlertDialog(
            state = closeDialogState,
            isSave = saveState,
            homeViewModel = homeViewModel,
            travelAppState = travelAppState,
            editorType = "pictxt"
        )
    }

    Column {
        if (progressState.value) {
            LinearProgressIndicator(
                modifier = Modifier.fillMaxWidth()
            )
        }
        if (checkState.value) {
            Snackbar(
                backgroundColor = Color.White,
                contentColor = MaterialTheme.colors.primary,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "标题和地点不可为空，请检查后再提交！",
                    textAlign = TextAlign.Center
                )
            }
            LaunchedEffect(key1 = true) {
                delay(3000)
                checkState.value = false
            }
        }
        if (saveSnackbarState.value) {
            Snackbar(
                backgroundColor = Color.White,
                contentColor = MaterialTheme.colors.primary,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "保存成功",
                    textAlign = TextAlign.Center
                )
            }
            LaunchedEffect(key1 = true) {
                delay(2000)
                saveSnackbarState.value = false
            }
        }
        if (homeViewModel.showUploadSnackBar.value) {
            if (homeViewModel.uploadPictxtStatus.value) {
                Snackbar(
                    backgroundColor = Color.White,
                    contentColor = MaterialTheme.colors.primary,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "上传成功",
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                Snackbar(
                    backgroundColor = Color.White,
                    contentColor = MaterialTheme.colors.primary,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "上传失败，请重试",
                        textAlign = TextAlign.Center
                    )
                }
            }
            LaunchedEffect(key1 = true) {
                progressState.value = false
                delay(2000)
                homeViewModel.showUploadSnackBar.value = false
                homeViewModel.getPictxtList(token)
                homeViewModel.clearPictxtEditor()
                if (homeViewModel.uploadPictxtStatus.value) {
                    when (travelAppState.travelAppViewState.value) {
                        TravelAppViewState.HOME -> travelAppState.navController.navigate(route = "home")
                        TravelAppViewState.EXPLORE -> travelAppState.navController.navigate(
                            route = "explore"
                        )
                        TravelAppViewState.FAVORITE -> travelAppState.navController.navigate(
                            route = "favorite"
                        )
                        TravelAppViewState.ME -> travelAppState.navController.navigate(route = "me")
                        else -> {}
                    }
                    travelAppState.topBarState.value = true
                    travelAppState.bottomBarState.value = true
                }
            }
        }
        Row(
            modifier = Modifier.padding(end = 5.dp)
        ) {
            IconButton(onClick = {
                closeDialogState.value = true
            }) {
                Icon(
                    imageVector = Icons.Outlined.Close,
                    contentDescription = "Close"
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            OutlinedButton(
                onClick = {
                    focusManager.clearFocus()
                    saveState.value = true
                    saveSnackbarState.value = true
                },
                shape = RoundedCornerShape(20.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Save,
                    contentDescription = "Save"
                )
                Text("保存")
            }
            Spacer(modifier = Modifier.size(10.dp))
            Button(
                onClick = {
                    focusManager.clearFocus()
                    if (homeViewModel.checkPictxtBeforeUpload()) {
                        homeViewModel.uploadPictxt(context = context, token = token)
                        progressState.value = true
                    } else {
                        checkState.value = true
                    }
                },
                shape = RoundedCornerShape(20.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Send,
                    contentDescription = "Send"
                )
                Text("上传")
            }
        }
        Surface(
            elevation = 4.dp,
            modifier = Modifier
                .fillMaxSize()
        ) {
            Column() {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                ) {
                    when (homeViewModel.pictxtImgList.size) {
                        1 -> {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Start
                            ) {
                                Pic(index = 0, homeViewModel = homeViewModel)
                                Spacer(modifier = Modifier.size(10.dp))
                                Picker {
                                    photosPickerLauncher.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                    )
                                }
                            }
                        }
                        2 -> {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Start
                            ) {
                                Pic(index = 0, homeViewModel = homeViewModel)
                                Spacer(modifier = Modifier.size(10.dp))
                                Pic(index = 1, homeViewModel = homeViewModel)
                                Spacer(modifier = Modifier.size(10.dp))
                                Picker {
                                    photosPickerLauncher.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                    )
                                }
                            }
                        }
                        3 -> {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Start,
                            ) {
                                Pic(index = 0, homeViewModel = homeViewModel)
                                Spacer(modifier = Modifier.size(10.dp))
                                Pic(index = 1, homeViewModel = homeViewModel)
                                Spacer(modifier = Modifier.size(10.dp))
                                Pic(index = 2, homeViewModel = homeViewModel)
                            }
                            Spacer(modifier = Modifier.size(10.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Start
                            ) {
                                Picker {
                                    photosPickerLauncher.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                    )
                                }
                            }
                        }
                        4 -> {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Start,
                            ) {
                                Pic(index = 0, homeViewModel = homeViewModel)
                                Spacer(modifier = Modifier.size(10.dp))
                                Pic(index = 1, homeViewModel = homeViewModel)
                                Spacer(modifier = Modifier.size(10.dp))
                                Pic(index = 2, homeViewModel = homeViewModel)
                            }
                            Spacer(modifier = Modifier.size(10.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Start
                            ) {
                                Pic(index = 3, homeViewModel = homeViewModel)
                                Spacer(modifier = Modifier.size(10.dp))
                                Picker {
                                    photosPickerLauncher.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                    )
                                }
                            }
                        }
                        5 -> {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Start,
                            ) {
                                Pic(index = 0, homeViewModel = homeViewModel)
                                Spacer(modifier = Modifier.size(10.dp))
                                Pic(index = 1, homeViewModel = homeViewModel)
                                Spacer(modifier = Modifier.size(10.dp))
                                Pic(index = 2, homeViewModel = homeViewModel)
                            }
                            Spacer(modifier = Modifier.size(10.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Start
                            ) {
                                Pic(index = 3, homeViewModel = homeViewModel)
                                Spacer(modifier = Modifier.size(10.dp))
                                Pic(index = 4, homeViewModel = homeViewModel)
                                Spacer(modifier = Modifier.size(10.dp))
                                Picker {
                                    photosPickerLauncher.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                    )
                                }
                            }
                        }
                        6 -> {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Start,
                            ) {
                                Pic(index = 0, homeViewModel = homeViewModel)
                                Spacer(modifier = Modifier.size(10.dp))
                                Pic(index = 1, homeViewModel = homeViewModel)
                                Spacer(modifier = Modifier.size(10.dp))
                                Pic(index = 2, homeViewModel = homeViewModel)
                            }
                            Spacer(modifier = Modifier.size(10.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Start
                            ) {
                                Pic(index = 3, homeViewModel = homeViewModel)
                                Spacer(modifier = Modifier.size(10.dp))
                                Pic(index = 4, homeViewModel = homeViewModel)
                                Spacer(modifier = Modifier.size(10.dp))
                                Pic(index = 5, homeViewModel = homeViewModel)
                            }
                            Spacer(modifier = Modifier.size(10.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Start
                            ) {
                                Picker {
                                    photosPickerLauncher.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                    )
                                }
                            }
                        }
                        7 -> {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Start,
                            ) {
                                Pic(index = 0, homeViewModel = homeViewModel)
                                Spacer(modifier = Modifier.size(10.dp))
                                Pic(index = 1, homeViewModel = homeViewModel)
                                Spacer(modifier = Modifier.size(10.dp))
                                Pic(index = 2, homeViewModel = homeViewModel)
                            }
                            Spacer(modifier = Modifier.size(10.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Start
                            ) {
                                Pic(index = 3, homeViewModel = homeViewModel)
                                Spacer(modifier = Modifier.size(10.dp))
                                Pic(index = 4, homeViewModel = homeViewModel)
                                Spacer(modifier = Modifier.size(10.dp))
                                Pic(index = 5, homeViewModel = homeViewModel)
                            }
                            Spacer(modifier = Modifier.size(10.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Start
                            ) {
                                Pic(index = 6, homeViewModel = homeViewModel)
                                Spacer(modifier = Modifier.size(10.dp))
                                Picker {
                                    photosPickerLauncher.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                    )
                                }
                            }
                        }
                        8 -> {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Start,
                            ) {
                                Pic(index = 0, homeViewModel = homeViewModel)
                                Spacer(modifier = Modifier.size(10.dp))
                                Pic(index = 1, homeViewModel = homeViewModel)
                                Spacer(modifier = Modifier.size(10.dp))
                                Pic(index = 2, homeViewModel = homeViewModel)
                            }
                            Spacer(modifier = Modifier.size(10.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Start
                            ) {
                                Pic(index = 3, homeViewModel = homeViewModel)
                                Spacer(modifier = Modifier.size(10.dp))
                                Pic(index = 4, homeViewModel = homeViewModel)
                                Spacer(modifier = Modifier.size(10.dp))
                                Pic(index = 5, homeViewModel = homeViewModel)
                            }
                            Spacer(modifier = Modifier.size(10.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Start
                            ) {
                                Pic(index = 6, homeViewModel = homeViewModel)
                                Spacer(modifier = Modifier.size(10.dp))
                                Pic(index = 7, homeViewModel = homeViewModel)
                                Spacer(modifier = Modifier.size(10.dp))
                                Picker {
                                    singlePhotoPickerLauncher.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                    )
                                }
                            }
                        }
                        9 -> {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Start,
                            ) {
                                Pic(index = 0, homeViewModel = homeViewModel)
                                Spacer(modifier = Modifier.size(10.dp))
                                Pic(index = 1, homeViewModel = homeViewModel)
                                Spacer(modifier = Modifier.size(10.dp))
                                Pic(index = 2, homeViewModel = homeViewModel)
                            }
                            Spacer(modifier = Modifier.size(10.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Start
                            ) {
                                Pic(index = 3, homeViewModel = homeViewModel)
                                Spacer(modifier = Modifier.size(10.dp))
                                Pic(index = 4, homeViewModel = homeViewModel)
                                Spacer(modifier = Modifier.size(10.dp))
                                Pic(index = 5, homeViewModel = homeViewModel)
                            }
                            Spacer(modifier = Modifier.size(10.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Start
                            ) {
                                Pic(index = 6, homeViewModel = homeViewModel)
                                Spacer(modifier = Modifier.size(10.dp))
                                Pic(index = 7, homeViewModel = homeViewModel)
                                Spacer(modifier = Modifier.size(10.dp))
                                Pic(index = 8, homeViewModel = homeViewModel)
                            }
                        }
                    }
                }
                TextField(
                    value = homeViewModel.pictxtEditorTitle,
                    onValueChange = {
                        if (it.count() <= 20) {
                            homeViewModel.pictxtEditorTitle = it
                        }
                    },
                    placeholder = {
                        Text(
                            modifier = Modifier
                                .alpha(ContentAlpha.medium),
                            text = "请输入标题(20字以内)",
                            color = Color.Black,
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.textFieldColors(
                        backgroundColor = Color.White,
                        unfocusedIndicatorColor = Color.White,
                        focusedIndicatorColor = Color.White
                    ),
                    singleLine = true,
                    textStyle = TextStyle(
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    trailingIcon = {
                        Text(
                            text = "${homeViewModel.pictxtEditorTitle.count()}/20"
                        )
                    }
                )
                TextField(
                    value = homeViewModel.pictxtEditorContent,
                    onValueChange = {
                        if (it.count() <= 200) {
                            homeViewModel.pictxtEditorContent = it
                        }
                    },
                    placeholder = {
                        Text(
                            modifier = Modifier
                                .alpha(ContentAlpha.medium),
                            text = "请输入内容",
                            color = Color.Black,
                        )
                    },
                    modifier = Modifier
                        .fillMaxSize(),
                    colors = TextFieldDefaults.textFieldColors(
                        backgroundColor = Color.White,
                        unfocusedIndicatorColor = Color.White,
                        focusedIndicatorColor = Color.White
                    ),
                    textStyle = TextStyle(
                        fontSize = 20.sp,
                    ),
                    trailingIcon = {
                        Text(
                            text = "${homeViewModel.pictxtEditorContent.count()}/200"
                        )
                    }
                )
            }
        }
    }
}


@Composable
fun Pic(
    index: Int,
    homeViewModel: HomeViewModel
) {
    Box() {
        AsyncImage(
            model = homeViewModel.pictxtImgList[index],
            contentDescription = "pictxt",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(120.dp)
                .clip(RoundedCornerShape(16.dp))
        )
        if (homeViewModel.pictxtImgList.size > 1) {
            IconButton(onClick = {
                homeViewModel.pictxtImgList.removeAt(index)
            }) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "close"
                )
            }
        }
    }
}

@Composable
fun Picker(
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .background(Color(0xfff6f6f6))
            .size(120.dp)
            .clickable {
                onClick()
            },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = "Add",
            modifier = Modifier.size(30.dp)
        )
    }
}