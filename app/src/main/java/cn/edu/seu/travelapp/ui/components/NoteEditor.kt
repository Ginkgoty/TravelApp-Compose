/**
 * NoteEditor.kt
 *
 * This file is ui of note editor
 * @author Li Jiawen
 * @mail   nmjbh@qq.com
 */
package cn.edu.seu.travelapp.ui.components

import android.net.Uri
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
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import cn.edu.seu.travelapp.data.TokenStorage
import cn.edu.seu.travelapp.ui.state.HomeViewContentState
import cn.edu.seu.travelapp.viewmodel.HomeViewModel
import coil.compose.AsyncImage
import kotlinx.coroutines.delay

@Composable
fun NoteEditor(
    dataStore: TokenStorage,
    homeViewModel: HomeViewModel,
) {

    val context = LocalContext.current
    val focusManager = LocalFocusManager.current

    val token = dataStore.getAccessToken.collectAsState(initial = "").value

    // section dialog
    val sectionTitleDialogState = remember {
        mutableStateOf(0)  // 0 - close, 1 - create, 2 - modify
    }
    var sectionIndex = 0
    var initText = ""

    // close dialog
    val closeDialogState = remember { mutableStateOf(false) }

    // save
    val saveState = remember { mutableStateOf(false) }
    val saveSnackbarState = remember {
        mutableStateOf(false)
    }

    // check before update
    val checkState = remember {
        mutableStateOf(false)
    }

    // progress bar
    val progressState = remember { mutableStateOf(false) }

    val singlePhotoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri -> homeViewModel.editorBackground = uri?.toString() ?: "" }
    )


    val photosPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(),
        onResult = {
            homeViewModel.dealWithImgList(imgList = it)
        }
    )

    BackHandler(enabled = true) {
        closeDialogState.value = true
    }

    Scaffold(
        bottomBar = {
            Surface(
                elevation = 8.dp,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Row() {
                    IconButton(onClick = {
                        focusManager.clearFocus()
                        photosPickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    }) {
                        Icon(
                            imageVector = Icons.Filled.Image,
                            contentDescription = null
                        )
                    }
                    IconButton(onClick = {
                        focusManager.clearFocus()
                        initText = ""
                        sectionTitleDialogState.value = 1
                    }) {
                        Icon(
                            imageVector = Icons.Filled.TextFields,
                            contentDescription = null
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        // Indicators
        val scrollState = rememberScrollState()
        if (sectionTitleDialogState.value != 0) {
            SectionTitleInputDialog(
                dialogState = sectionTitleDialogState,
                textInit = initText,
                index = sectionIndex,
                homeViewModel = homeViewModel
            )
        }
        if (closeDialogState.value) {
            CloseAlertDialog(
                state = closeDialogState,
                isSave = saveState,
                homeViewModel = homeViewModel
            )
        }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .verticalScroll(state = scrollState)
                .fillMaxWidth()
                .padding(paddingValues = paddingValues)
        ) {
            if (progressState.value) {
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth()
                )
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
                if (homeViewModel.uploadNoteStatus.value) {
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
                    delay(2000)
                    homeViewModel.showUploadSnackBar.value = false
                    progressState.value = false
                    if (homeViewModel.uploadNoteStatus.value) {
                        homeViewModel.getNoteList()
                        homeViewModel.updateContentState(HomeViewContentState.NOTE_LIST)
                        homeViewModel.clearNoteEditor()
                    }
                }
            }
            if (checkState.value) {
                Snackbar(
                    backgroundColor = Color.White,
                    contentColor = MaterialTheme.colors.primary,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "背景图片、标题、地点不可为空，请检查后再提交！",
                        textAlign = TextAlign.Center
                    )
                }
                LaunchedEffect(key1 = true) {
                    delay(3000)
                    checkState.value = false
                }
            }
            // Contents
            Column(
                modifier = Modifier
                    .height(250.dp)
                    .fillMaxWidth()
                    .background(
                        color = Color(0xFFF6F6F6)
                    ),
            ) {
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
                            if (homeViewModel.checkBeforeUpload()) {
                                homeViewModel.uploadNote(token = token, context = context)
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
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable {
                            singlePhotoPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        },
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (homeViewModel.editorBackground == "") {
                        Icon(
                            imageVector = Icons.Filled.Image,
                            contentDescription = "BG",
                            modifier = Modifier.offset(y = 2.dp),
                            tint = Color(0xff444444)
                        )
                        Spacer(modifier = Modifier.size(10.dp))
                        Text(
                            text = "上传封面图片",
                            fontSize = 20.sp,
                            textAlign = TextAlign.Center,
                            color = Color(0xff444444)
                        )
                    } else {
                        AsyncImage(
                            model = Uri.parse(homeViewModel.editorBackground),
                            contentDescription = "BG PHOTO",
                            modifier = Modifier.fillMaxWidth(),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
            }
            Column(
                modifier = Modifier
                    .fillMaxSize(1f)
            ) {
                TextField(
                    value = homeViewModel.editorTitle,
                    onValueChange = {
                        homeViewModel.editorTitle = it
                    },
                    placeholder = {
                        Text(
                            modifier = Modifier
                                .alpha(ContentAlpha.medium),
                            text = "请输入标题",
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
                    )
                )
                Divider()
                TextField(
                    value = homeViewModel.editorRname,
                    onValueChange = {
                        homeViewModel.editorRname = it
                    },
                    placeholder = {
                        Text(
                            modifier = Modifier
                                .alpha(ContentAlpha.medium),
                            text = "地点",
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
                        fontSize = 20.sp,
                    )
                )
                Divider()
                Spacer(modifier = Modifier.size(5.dp))
                Column() {
                    homeViewModel.editorNoteContentList.forEachIndexed { index, item ->
                        when (item.first.value) {
                            0 -> {
                                Row(
                                    modifier = Modifier.padding(
                                        start = 10.dp,
                                        end = 10.dp,
                                        top = 5.dp,
                                        bottom = 5.dp
                                    ),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = item.second.value,
                                        fontSize = 24.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier
                                            .clickable {
                                                sectionIndex = index
                                                sectionTitleDialogState.value = 2
                                                initText = item.second.value
                                            }
                                    )
                                    Spacer(modifier = Modifier.weight(1f))
                                    IconButton(
                                        onClick = {
                                            homeViewModel.deleteSectionTitle(index)
                                        },
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Close,
                                            contentDescription = "Delete Section title",
                                        )
                                    }
                                }
                                Divider()
                                Spacer(modifier = Modifier.size(5.dp))
                            }
                            1 -> {
                                TextField(
                                    value = item.second.value,
                                    onValueChange = {
                                        item.second.value = it
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
                                        .fillMaxWidth()
                                        .onFocusChanged {
                                            homeViewModel.cleanUnusedItems()
                                        },
                                    colors = TextFieldDefaults.textFieldColors(
                                        backgroundColor = Color.White,
                                        unfocusedIndicatorColor = Color.White,
                                        focusedIndicatorColor = Color.White
                                    ),
                                    textStyle = TextStyle(
                                        fontSize = 20.sp,
                                    )
                                )
                            }
                            2 -> {
                                Box(
                                    contentAlignment = Alignment.TopEnd
                                ) {
                                    AsyncImage(
                                        model = Uri.parse(item.second.value),
                                        contentDescription = "Photo",
                                        modifier = Modifier
                                            .padding(start = 10.dp, end = 10.dp)
                                            .fillMaxWidth(),
                                        contentScale = ContentScale.Crop
                                    )
                                    IconButton(onClick = {
                                        homeViewModel.deleteCurrentImage(content = item.second.value)
                                    }) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "delete",
                                            tint = Color.White
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

@Composable
fun CloseAlertDialog(
    state: MutableState<Boolean>,
    isSave: MutableState<Boolean>,
    homeViewModel: HomeViewModel
) {
    AlertDialog(
        onDismissRequest = {

        },
        title = {
            Text(
                text = "退出游记编辑"
            )
        },
        text = {
            Text(
                text = "您将退出游记编辑页面，如需要保存请返回并点击\"保存\"。"
            )
        },
        buttons = {
            Row(
                modifier = Modifier.padding(10.dp)
            ) {
                Spacer(modifier = Modifier.weight(1f))
                OutlinedButton(
                    onClick = {
                        state.value = false
                        homeViewModel.updateContentState(HomeViewContentState.NOTE_LIST)
                        if (!isSave.value) {
                            homeViewModel.clearNoteEditor()
                        }
                    },
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "close",
                    )
                    Text(
                        text = "退出编辑"
                    )
                }
                Spacer(modifier = Modifier.size(10.dp))
                Button(
                    onClick = {
                        state.value = false
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Reply,
                        contentDescription = "check",
                    )
                    Text(
                        text = "继续编辑"
                    )
                }
            }
        },
        modifier = Modifier.padding(10.dp)
    )
}

@Composable
fun SectionTitleInputDialog(
    dialogState: MutableState<Int>,
    index: Int,
    textInit: String,
    homeViewModel: HomeViewModel
) {
    val text = remember { mutableStateOf(textInit) }
    Dialog(
        onDismissRequest = {
            dialogState.value = 0
        },
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = false,
        ),
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start,
            modifier = Modifier
                .background(
                    color = Color.White
                )
                .padding(20.dp)
        ) {
            Text(
                text = "章节标题",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
            )
            Spacer(modifier = Modifier.size(20.dp))
            TextField(
                value = text.value,
                onValueChange = { text.value = it },
                colors = TextFieldDefaults.textFieldColors(
                    backgroundColor = Color.Transparent,
                ),
                placeholder = {
                    Text(
                        modifier = Modifier
                            .alpha(ContentAlpha.medium),
                        text = "请输入章节标题",
                        color = Color.Black,
                    )
                }
            )
            Spacer(modifier = Modifier.size(20.dp))
            Row(
                horizontalArrangement = Arrangement.End
            ) {
                Spacer(modifier = Modifier.weight(1f))
                OutlinedButton(
                    onClick = {
                        dialogState.value = 0
                    },
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "close",
                    )
                    Text(
                        text = "取消"
                    )
                }
                Spacer(modifier = Modifier.size(10.dp))
                Button(
                    onClick = {
                        when (dialogState.value) {
                            1 -> homeViewModel.dealWithSectionTitle(st = text.value)
                            2 -> homeViewModel.updateSectionTitle(index = index, st = text.value)
                        }
                        dialogState.value = 0
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "check",
                    )
                    Text(
                        text = "确认"
                    )
                }
            }
        }
    }
}

