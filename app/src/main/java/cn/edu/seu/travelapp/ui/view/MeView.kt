package cn.edu.seu.travelapp.ui.view


import android.net.Uri
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Badge
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cn.edu.seu.travelapp.R
import cn.edu.seu.travelapp.data.DataStorage
import cn.edu.seu.travelapp.ui.state.MeContentState
import cn.edu.seu.travelapp.ui.state.TravelAppState
import cn.edu.seu.travelapp.viewmodel.MeViewModel
import coil.compose.AsyncImage
import kotlinx.coroutines.delay


@Composable
fun MeView(
    meViewModel: MeViewModel,
    dataStore: DataStorage,
    travelAppState: TravelAppState,
    paddingValues: PaddingValues
) {
    BackHandler(enabled = true) {

    }
    val meViewState = meViewModel.uiState.collectAsState().value
    val token = dataStore.getAccessToken.collectAsState(initial = "").value
    LaunchedEffect(key1 = token) {
        Log.d("Launched", "token")
        if (token == "") {
            meViewModel.updateContentState(MeContentState.INIT)
        } else {
            meViewModel.updateContentState(MeContentState.ME)
        }
    }
    when (meViewState.meContentState) {
        MeContentState.INIT -> {
            BackHandler(enabled = true) {

            }
            Log.d("Me content", "init")
            InitView(paddingValues = paddingValues, meViewModel = meViewModel)
        }
        MeContentState.SIGN_UP -> {
            BackHandler(enabled = true) {
                meViewModel.updateContentState(MeContentState.INIT)
            }
            SignUpView(meViewModel = meViewModel, paddingValues = paddingValues)
        }
        MeContentState.SIGN_IN -> {
            BackHandler(enabled = true) {
                meViewModel.updateContentState(MeContentState.INIT)
            }
            SignInView(meViewModel = meViewModel, paddingValues = paddingValues)
        }
        MeContentState.ME -> {
            BackHandler(enabled = true) {

            }
            meViewModel.updateContentState(MeContentState.ME)
            AfterLoginView(
                meViewModel = meViewModel,
                dataStore = dataStore,
                travelAppState = travelAppState
            )
        }
        MeContentState.CHANGE_PWD -> {
            BackHandler(enabled = true) {
                meViewModel.updateContentState(MeContentState.ME)
            }
            ChangePwdView(meViewModel = meViewModel, dataStore = dataStore)
        }
        MeContentState.CHANGE_UNAME -> {
            BackHandler(enabled = true) {
                meViewModel.updateContentState(MeContentState.ME)
            }
            ChangeUnameView(meViewModel = meViewModel, dataStore = dataStore)
        }
        MeContentState.CHANGE_UPIC -> {
            BackHandler(enabled = true) {
                meViewModel.updateContentState(MeContentState.ME)
            }
            ChangeUpicView(meViewModel = meViewModel, dataStore = dataStore)
        }

    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangePwdView(
    meViewModel: MeViewModel,
    dataStore: DataStorage
) {
    val meViewState = meViewModel.uiState.collectAsState()
    val token = dataStore.getAccessToken.collectAsState(initial = "").value

    var pwdIsError by rememberSaveable { mutableStateOf(false) }
    var repwdIsError by rememberSaveable { mutableStateOf(false) }

    var password by rememberSaveable { mutableStateOf("") }
    var passwordVisible by rememberSaveable { mutableStateOf(false) }

    var repassword by rememberSaveable { mutableStateOf("") }
    var repasswordVisible by rememberSaveable { mutableStateOf(false) }

    val focusManager = LocalFocusManager.current

    fun validatePwd() {
        pwdIsError = (password.length < 8 || password.length > 16)
    }

    fun validateRepwd() {
        repwdIsError = password != repassword
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        if (meViewState.value.changePwdStatus == 1 && meViewModel.snackBarShowingState) {
            Snackbar(
                containerColor = Color.White,
                modifier = Modifier.width(130.dp)
            ) {
                Text(
                    text = "密码修改成功！",
                    color = MaterialTheme.colors.primary
                )
            }
        }
        if (meViewState.value.changePwdStatus == 2 && meViewModel.snackBarShowingState) {
            Snackbar(
                containerColor = Color.White,
                modifier = Modifier.width(166.dp)
            ) {
                Text(
                    text = "服务器故障，请重试！",
                    color = MaterialTheme.colors.primary
                )
            }
        }
        if (meViewState.value.changePwdStatus == 3 && meViewModel.snackBarShowingState) {
            Snackbar(
                containerColor = Color.White,
                modifier = Modifier.width(200.dp)
            ) {
                Text(
                    text = "密码不合规，请修改！",
                    color = MaterialTheme.colors.primary
                )
            }
        }
        LaunchedEffect(key1 = meViewModel.snackBarShowingState) {
            delay(2000)
            meViewModel.snackBarShowingState = false
        }
        Text(
            text = "修改密码",
            fontFamily = FontFamily(Font(R.font.hggys)),
            fontSize = 40.sp,
            fontWeight = FontWeight.Bold,
        )
        OutlinedTextField(
            value = password,
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Password,
                    contentDescription = "Password Icon"
                )
            },
            onValueChange = { password = it },
            label = { Text("密码") },
            singleLine = true,
            placeholder = { Text("请输入密码") },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            trailingIcon = {
                val image = if (passwordVisible)
                    Icons.Filled.Visibility
                else Icons.Filled.VisibilityOff

                // Please provide localized description for accessibility services
                val description = if (passwordVisible) "Hide password" else "Show password"

                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(imageVector = image, description)
                }
            },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                containerColor = Color.White,
            ),
            isError = pwdIsError,
            supportingText = {
                if (pwdIsError) {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = "密码长度为8～16",
                        color = MaterialTheme.colors.error
                    )
                }
            },
            keyboardActions = KeyboardActions(
                onDone = {
                    focusManager.clearFocus()
                    validatePwd()
                }
            )
        )
        OutlinedTextField(
            value = repassword,
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Password,
                    contentDescription = "Password Icon"
                )
            },
            onValueChange = { repassword = it },
            label = { Text("密码确认") },
            singleLine = true,
            placeholder = { Text("请再次输入密码") },
            visualTransformation = if (repasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            trailingIcon = {
                val image = if (repasswordVisible)
                    Icons.Filled.Visibility
                else Icons.Filled.VisibilityOff

                // Please provide localized description for accessibility services
                val description = if (repasswordVisible) "Hide password" else "Show password"

                IconButton(onClick = { repasswordVisible = !repasswordVisible }) {
                    Icon(imageVector = image, description)
                }
            },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                containerColor = Color.White,
            ),
            isError = repwdIsError,
            supportingText = {
                if (repwdIsError) {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = "两次输入的密码不一致！",
                        color = MaterialTheme.colors.error
                    )
                }
            },
            keyboardActions = KeyboardActions(
                onDone = {
                    focusManager.clearFocus()
                    validateRepwd()
                }) {
            }
        )
        Spacer(modifier = Modifier.size(80.dp))
        Button(
            modifier = Modifier.size(width = 110.dp, height = 50.dp),
            onClick = {
                validatePwd()
                validateRepwd()
                if (!repwdIsError && !pwdIsError) {
                    meViewModel.changePwd(pwd = password, token = token)
                } else {
                    meViewModel.updateChangePwdStatus(newValue = 3)
                    meViewModel.snackBarShowingState = true
                }
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colors.primary
            )
        ) {
            Icon(
                imageVector = Icons.Default.Done,
                contentDescription = "button icon",
                modifier = Modifier.size(20.dp),
                tint = Color.White
            )
            Text(
                text = "确认",
                fontSize = 14.sp,
                color = Color.White,
                modifier = Modifier.padding(start = 10.dp)
            )
        }
    }

}

@Composable
fun ChangeUpicView(
    meViewModel: MeViewModel,
    dataStore: DataStorage
) {
    val context = LocalContext.current
    val token = dataStore.getAccessToken.collectAsState(initial = "").value

    val singlePhotoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri -> meViewModel.upicUri = uri?.toString() ?: "" }
    )

    val checkBarState = remember { mutableStateOf(false) }
    val uploadBarState = remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (meViewModel.upicUri == "") {
            Box(
                modifier = Modifier
                    .size(250.dp)
                    .clip(CircleShape)
                    .clickable {
                        singlePhotoPickerLauncher.launch(
                            PickVisualMediaRequest(mediaType = ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    }
                    .background(Color(0xfff6f6f6)),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Image,
                        contentDescription = null,
                        tint = Color.Gray
                    )
                    Spacer(modifier = Modifier.size(10.dp))
                    Text(
                        text = "点击上传图片",
                        color = Color.Gray
                    )
                }
            }
        } else {
            AsyncImage(
                model = Uri.parse(meViewModel.upicUri),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .clip(CircleShape)
                    .size(250.dp)
                    .clickable {
                        singlePhotoPickerLauncher.launch(
                            PickVisualMediaRequest(mediaType = ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    }
            )
        }
        Spacer(modifier = Modifier.size(20.dp))
        Button(
            onClick = {
                if (meViewModel.upicUri == "") {
                    checkBarState.value = true
                } else {
                    meViewModel.changeUpic(
                        context = context,
                        token = token
                    )
                }
            },
            colors = ButtonDefaults.buttonColors(
                contentColor = Color.White,
                containerColor = MaterialTheme.colors.primary
            )
        ) {
            Text(text = "上传头像")
        }
    }
    if (checkBarState.value) {
        Snackbar(
            containerColor = Color.White,
            contentColor = MaterialTheme.colors.primary,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "您未选择图片！",
                textAlign = TextAlign.Center
            )
        }
    }
    LaunchedEffect(key1 = checkBarState.value) {
        if (checkBarState.value) {
            delay(2000)
            checkBarState.value = false
        }
    }
    if (uploadBarState.value) {
        if (!meViewModel.uploadStatus.value) {
            Snackbar(
                containerColor = Color.White,
                contentColor = MaterialTheme.colors.primary,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "上传失败，请重试",
                    textAlign = TextAlign.Center
                )
            }
        } else {
            Snackbar(
                containerColor = Color.White,
                contentColor = MaterialTheme.colors.primary,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "上传成功",
                    textAlign = TextAlign.Center
                )
            }
        }
    }
    LaunchedEffect(key1 = uploadBarState.value) {
        if (uploadBarState.value) {
            delay(2000)
            uploadBarState.value = false
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangeUnameView(
    meViewModel: MeViewModel,
    dataStore: DataStorage
) {
    val meViewState = meViewModel.uiState.collectAsState()

    var uname by rememberSaveable { mutableStateOf("") }

    var unameIsError by rememberSaveable { mutableStateOf(false) }

    val focusManager = LocalFocusManager.current

    val currentToken = dataStore.getAccessToken.collectAsState(initial = "").value

    fun validateUname() {
        unameIsError = (uname.isEmpty() || uname.length > 16)
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        if (meViewState.value.changeUnameStatus == 1 && meViewModel.snackBarShowingState) {
            Snackbar(
                containerColor = Color.White,
                modifier = Modifier.width(140.dp)
            ) {
                Text(
                    text = "用户名修改成功！",
                    color = MaterialTheme.colors.primary
                )
            }
        }
        if (meViewState.value.changeUnameStatus == 2 && meViewModel.snackBarShowingState) {
            Snackbar(
                containerColor = Color.White,
                modifier = Modifier.width(166.dp)
            ) {
                Text(
                    text = "用户名重复，请重试！",
                    color = MaterialTheme.colors.primary
                )
            }
        }
        if (meViewState.value.changeUnameStatus == 3 && meViewModel.snackBarShowingState) {
            Snackbar(
                containerColor = Color.White,
                modifier = Modifier.width(200.dp)
            ) {
                Text(
                    text = "用户名不合规，请修改！",
                    color = MaterialTheme.colors.primary
                )
            }
        }
        LaunchedEffect(key1 = meViewModel.snackBarShowingState) {
            delay(2000)
            meViewModel.snackBarShowingState = false
//            if (meViewState.value.changeUnameStatus == 1) {
//                meViewModel.updateContentState(MeContentState.ME)
//            }
        }
        Text(
            text = "修改用户名",
            fontFamily = FontFamily(Font(R.font.hggys)),
            fontSize = 40.sp,
            fontWeight = FontWeight.Bold,
        )
        Spacer(modifier = Modifier.size(20.dp))
        OutlinedTextField(
            value = uname,
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Person Icon"
                )
            },
            onValueChange = {
                uname = it
            },
            label = { Text(text = "用户名") },
            placeholder = { Text(text = "请输入您的用户名") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            colors = TextFieldDefaults.outlinedTextFieldColors(

            ),
            isError = unameIsError,
            supportingText = {
                if (unameIsError) {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = "用户名长度为1～16",
                        color = MaterialTheme.colors.error
                    )
                }
            },
            keyboardActions = KeyboardActions(
                onDone = {
                    focusManager.clearFocus()
                    validateUname()
                }
            )
        )
        Spacer(modifier = Modifier.size(100.dp))
        Button(
            modifier = Modifier.size(width = 120.dp, height = 50.dp),
            onClick = {
                validateUname()
                if (!unameIsError) {
                    meViewModel.changeUname(uname = uname, token = currentToken)
                } else {
                    meViewModel.snackBarShowingState = true
                    meViewModel.updateChangeUnameStatus(newValue = 3)
                }
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colors.primary
            )
        ) {
            Icon(
                imageVector = Icons.Default.Logout,
                contentDescription = "button icon",
                modifier = Modifier.size(24.dp),
                tint = Color.White
            )
            Text(
                text = "确认",
                fontSize = 18.sp,
                color = Color.White,
                modifier = Modifier.padding(start = 10.dp)
            )
        }
    }
}

@Composable
fun AfterLoginView(
    dataStore: DataStorage,
    travelAppState: TravelAppState,
    meViewModel: MeViewModel
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box() {
            Image(
                painter = painterResource(id = R.drawable.mebg),
                contentDescription = "Background",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .aspectRatio(18 / 9f)
                    .alpha(0.7f)
                    .fillMaxWidth(),
            )
            Row(
                modifier = Modifier
                    .padding(top = 70.dp, start = 20.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    model = dataStore.getCurrentUpic.collectAsState(initial = "").value,
                    contentDescription = "user icon",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                )
                Spacer(modifier = Modifier.size(30.dp))
                Text(
                    text = dataStore.getCurrentUname.collectAsState("").value,
                    fontSize = 32.sp,
                )
            }
        }
        Spacer(modifier = Modifier.size(5.dp))
        // 修改用户名栏
        Surface(
            shadowElevation = 4.dp,
            modifier = Modifier.clickable {
                meViewModel.updateContentState(MeContentState.CHANGE_UNAME)
            }
        ) {
            Row(
                modifier = Modifier
                    .padding(15.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Outlined.Badge,
                    contentDescription = "Icon",
                    modifier = Modifier.size(40.dp)
                )
                Spacer(modifier = Modifier.size(30.dp))
                Text(
                    text = "修改用户名",
                    fontSize = 22.sp
                )
            }
        }
        Spacer(modifier = Modifier.size(5.dp))
        // 修改密码栏
        Surface(
            shadowElevation = 4.dp,
            modifier = Modifier.clickable {
                meViewModel.updateContentState(MeContentState.CHANGE_PWD)
            }
        ) {
            Row(
                modifier = Modifier
                    .padding(15.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Outlined.Lock,
                    contentDescription = "Icon",
                    modifier = Modifier.size(40.dp)
                )
                Spacer(modifier = Modifier.size(30.dp))
                Text(
                    text = "修改密码",
                    fontSize = 22.sp
                )
            }
        }
        Spacer(modifier = Modifier.size(5.dp))

        // 修改头像
        Surface(
            shadowElevation = 4.dp,
            modifier = Modifier.clickable {
                meViewModel.updateContentState(MeContentState.CHANGE_UPIC)
            }
        ) {
            Row(
                modifier = Modifier
                    .padding(15.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Outlined.Person,
                    contentDescription = "Icon",
                    modifier = Modifier.size(40.dp)
                )
                Spacer(modifier = Modifier.size(30.dp))
                Text(
                    text = "修改头像",
                    fontSize = 22.sp
                )
            }
        }
        Spacer(modifier = Modifier.size(150.dp))
        Button(
            modifier = Modifier.size(width = 160.dp, height = 60.dp),
            onClick = {
                meViewModel.logOut()
                travelAppState.isLogin.value = false
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colors.error
            )
        ) {
            Icon(
                imageVector = Icons.Default.Logout,
                contentDescription = "button icon",
                modifier = Modifier.size(24.dp),
                tint = Color.White
            )
            Text(
                text = "退出登录",
                fontSize = 18.sp,
                color = Color.White,
                modifier = Modifier.padding(start = 10.dp)
            )
        }
    }
}

@Composable
fun InitView(
    paddingValues: PaddingValues,
    meViewModel: MeViewModel
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.padding(bottom = paddingValues.calculateBottomPadding())
    ) {
        Image(
            painter = painterResource(id = R.drawable.bg),
            contentDescription = "bg",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
            alpha = 0.7f
        )
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = "你好，旅行者！",
                fontFamily = FontFamily(Font(R.font.hggys)),
                fontSize = 60.sp,
                fontWeight = FontWeight.Bold,
            )
            Spacer(modifier = Modifier.size(20.dp))
            Row() {
                OutlinedButton(
                    modifier = Modifier.size(width = 110.dp, height = 50.dp),
                    onClick = {
                        meViewModel.updateContentState(MeContentState.SIGN_UP)
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White
                    )
                ) {
                    Image(
                        imageVector = Icons.Default.PersonAdd,
                        contentDescription = "button icon",
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "注册",
                        fontSize = 14.sp,
                        color = Color.Black,
                        modifier = Modifier.padding(start = 10.dp)
                    )
                }
                Spacer(modifier = Modifier.size(60.dp))
                OutlinedButton(
                    modifier = Modifier.size(width = 110.dp, height = 50.dp),
                    onClick = {
                        meViewModel.updateContentState(MeContentState.SIGN_IN)
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White
                    )
                ) {
                    Image(
                        imageVector = Icons.Default.Login,
                        contentDescription = "button icon",
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "登录",
                        fontSize = 14.sp,
                        color = Color.Black,
                        modifier = Modifier.padding(start = 10.dp)
                    )
                }
            }

        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpView(
    meViewModel: MeViewModel,
    paddingValues: PaddingValues
) {

    val meViewState = meViewModel.uiState.collectAsState()

    var uname by rememberSaveable { mutableStateOf("") }

    var unameIsError by rememberSaveable { mutableStateOf(false) }
    var pwdIsError by rememberSaveable { mutableStateOf(false) }
    var repwdIsError by rememberSaveable { mutableStateOf(false) }

    var password by rememberSaveable { mutableStateOf("") }
    var passwordVisible by rememberSaveable { mutableStateOf(false) }

    var repassword by rememberSaveable { mutableStateOf("") }
    var repasswordVisible by rememberSaveable { mutableStateOf(false) }

    val focusManager = LocalFocusManager.current

    fun validatePwd() {
        pwdIsError = (password.length < 8 || password.length > 16)
    }

    fun validateRepwd() {
        repwdIsError = password != repassword
    }

    fun validateUname() {
        unameIsError = (uname.isEmpty() || uname.length > 16)
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.padding(bottom = paddingValues.calculateBottomPadding())
    ) {
        Image(
            painter = painterResource(id = R.drawable.bg),
            contentDescription = "bg",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
            alpha = 0.7f
        )
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (meViewState.value.signUpStatus == 2 && meViewModel.snackBarShowingState) {
                Snackbar(
                    containerColor = Color.White,
                    modifier = Modifier.width(166.dp)
                ) {
                    Text(
                        text = "用户名重复，请重试！",
                        color = MaterialTheme.colors.primary
                    )
                }
            }
            if (meViewState.value.signUpStatus == 3 && meViewModel.snackBarShowingState) {
                Snackbar(
                    containerColor = Color.White,
                    modifier = Modifier.width(220.dp)
                ) {
                    Text(
                        text = "用户名或密码不合规，请修改！",
                        color = MaterialTheme.colors.primary
                    )
                }
            }
            LaunchedEffect(key1 = meViewModel.snackBarShowingState) {
                delay(2000)
                meViewModel.snackBarShowingState = false
            }
            Text(
                text = "注册",
                fontFamily = FontFamily(Font(R.font.hggys)),
                fontSize = 44.sp,
                fontWeight = FontWeight.Bold,
            )
            OutlinedTextField(
                value = uname,
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Person Icon"
                    )
                },
                onValueChange = {
                    uname = it
                },
                label = { Text(text = "用户名") },
                placeholder = { Text(text = "请输入您的用户名") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    containerColor = Color.White,
                    focusedLabelColor = Color.White
                ),
                isError = unameIsError,
                supportingText = {
                    if (unameIsError) {
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            text = "用户名长度为1～16",
                            color = MaterialTheme.colors.error
                        )
                    }
                },
                keyboardActions = KeyboardActions(
                    onDone = {
                        focusManager.clearFocus()
                        validateUname()
                    }
                )
            )
            OutlinedTextField(
                value = password,
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Password,
                        contentDescription = "Password Icon"
                    )
                },
                onValueChange = { password = it },
                label = { Text("密码") },
                singleLine = true,
                placeholder = { Text("请输入密码") },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = {
                    val image = if (passwordVisible)
                        Icons.Filled.Visibility
                    else Icons.Filled.VisibilityOff

                    // Please provide localized description for accessibility services
                    val description = if (passwordVisible) "Hide password" else "Show password"

                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(imageVector = image, description)
                    }
                },
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    containerColor = Color.White,
                    focusedLabelColor = Color.White
                ),
                isError = pwdIsError,
                supportingText = {
                    if (pwdIsError) {
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            text = "密码长度为8～16",
                            color = MaterialTheme.colors.error
                        )
                    }
                },
                keyboardActions = KeyboardActions(
                    onDone = {
                        focusManager.clearFocus()
                        validatePwd()
                    }
                )
            )
            OutlinedTextField(
                value = repassword,
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Password,
                        contentDescription = "Password Icon"
                    )
                },
                onValueChange = { repassword = it },
                label = { Text("密码确认") },
                singleLine = true,
                placeholder = { Text("请再次输入密码") },
                visualTransformation = if (repasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = {
                    val image = if (repasswordVisible)
                        Icons.Filled.Visibility
                    else Icons.Filled.VisibilityOff

                    // Please provide localized description for accessibility services
                    val description = if (repasswordVisible) "Hide password" else "Show password"

                    IconButton(onClick = { repasswordVisible = !repasswordVisible }) {
                        Icon(imageVector = image, description)
                    }
                },
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    containerColor = Color.White,
                    focusedLabelColor = Color.White
                ),
                isError = repwdIsError,
                supportingText = {
                    if (repwdIsError) {
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            text = "两次输入的密码不一致！",
                            color = MaterialTheme.colors.error
                        )
                    }
                },
                keyboardActions = KeyboardActions(
                    onDone = {
                        focusManager.clearFocus()
                        validateRepwd()
                    }) {
                }
            )
            Spacer(modifier = Modifier.size(10.dp))
            Button(
                modifier = Modifier.size(width = 110.dp, height = 50.dp),
                onClick = {
                    validateUname()
                    validatePwd()
                    validateRepwd()
                    if (!repwdIsError && !unameIsError && !pwdIsError) {
                        meViewModel.signUp(uname = uname, pwd = password)
                    } else {
                        meViewModel.updateSignUpStatus(3)
                        meViewModel.snackBarShowingState = true
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colors.primary
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Done,
                    contentDescription = "button icon",
                    modifier = Modifier.size(20.dp),
                    tint = Color.White
                )
                Text(
                    text = "确认",
                    fontSize = 14.sp,
                    color = Color.White,
                    modifier = Modifier.padding(start = 10.dp)
                )
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignInView(
    meViewModel: MeViewModel,
    paddingValues: PaddingValues
) {

    val meViewState = meViewModel.uiState.collectAsState()

    var uname by rememberSaveable { mutableStateOf("") }

    var unameIsError by rememberSaveable { mutableStateOf(false) }
    var pwdIsError by rememberSaveable { mutableStateOf(false) }

    var password by rememberSaveable { mutableStateOf("") }
    var passwordVisible by rememberSaveable { mutableStateOf(false) }


    val focusManager = LocalFocusManager.current

    fun validatePwd() {
        pwdIsError = (password.length < 8 || password.length > 16)
    }

    fun validateUname() {
        unameIsError = (uname.isEmpty() || uname.length > 16)
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.padding(bottom = paddingValues.calculateBottomPadding())
    ) {
        Image(
            painter = painterResource(id = R.drawable.bg),
            contentDescription = "bg",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
            alpha = 0.7f
        )
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (meViewState.value.signInStatus == 2 && meViewModel.snackBarShowingState) {
                Snackbar(
                    containerColor = Color.White,
                    modifier = Modifier.width(240.dp)
                ) {
                    Text(
                        text = "登录失败，请检查用户名和密码！",
                        color = MaterialTheme.colors.primary
                    )
                }
            }
            LaunchedEffect(key1 = meViewModel.snackBarShowingState) {
                delay(2000)
                meViewModel.snackBarShowingState = false
            }
            Text(
                text = "登录",
                fontFamily = FontFamily(Font(R.font.hggys)),
                fontSize = 44.sp,
                fontWeight = FontWeight.Bold,
            )
            OutlinedTextField(
                value = uname,
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Person Icon"
                    )
                },
                onValueChange = {
                    uname = it
                },
                label = { Text(text = "用户名") },
                placeholder = { Text(text = "请输入您的用户名") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    containerColor = Color.White,
                    focusedLabelColor = Color.White
                ),
                isError = unameIsError,
                supportingText = {
                    if (unameIsError) {
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            text = "用户名长度为1～16",
                            color = MaterialTheme.colors.error
                        )
                    }
                },
                keyboardActions = KeyboardActions(
                    onDone = {
                        focusManager.clearFocus()
                        validateUname()
                    }
                )
            )
            OutlinedTextField(
                value = password,
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Password,
                        contentDescription = "Password Icon"
                    )
                },
                onValueChange = { password = it },
                label = { Text("密码") },
                singleLine = true,
                placeholder = { Text("请输入密码") },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = {
                    val image = if (passwordVisible)
                        Icons.Filled.Visibility
                    else Icons.Filled.VisibilityOff

                    // Please provide localized description for accessibility services
                    val description = if (passwordVisible) "Hide password" else "Show password"

                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(imageVector = image, description)
                    }
                },
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    containerColor = Color.White,
                    focusedLabelColor = Color.White
                ),
                isError = pwdIsError,
                supportingText = {
                    if (pwdIsError) {
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            text = "密码长度为8～16",
                            color = MaterialTheme.colors.error
                        )
                    }
                },
                keyboardActions = KeyboardActions(
                    onDone = {
                        focusManager.clearFocus()
                        validatePwd()
                    }
                )
            )
            Spacer(modifier = Modifier.size(10.dp))
            Button(
                modifier = Modifier.size(width = 110.dp, height = 50.dp),
                onClick = {
                    validateUname()
                    validatePwd()
                    if (!pwdIsError && !unameIsError) {
                        meViewModel.signIn(uname = uname, pwd = password)
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colors.primary
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Done,
                    contentDescription = "button icon",
                    modifier = Modifier.size(20.dp),
                    tint = Color.White
                )
                Text(
                    text = "确认",
                    fontSize = 14.sp,
                    color = Color.White,
                    modifier = Modifier.padding(start = 10.dp)
                )
            }
        }
    }
}


@Composable
fun MeAppBar(
    meViewModel: MeViewModel
) {
    val meViewState = meViewModel.uiState.collectAsState()
    when (meViewState.value.meContentState) {
        MeContentState.INIT -> {
            InitAppBar()
        }
        MeContentState.SIGN_IN, MeContentState.SIGN_UP -> {
            SignAppBar(meViewModel = meViewModel)
        }
        MeContentState.ME -> {
            InitAppBar()
        }
        MeContentState.CHANGE_UNAME, MeContentState.CHANGE_PWD, MeContentState.CHANGE_UPIC -> {
            OpAppBar(meViewModel = meViewModel)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InitAppBar() {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = "行者",
                color = Color.White,
                fontFamily = FontFamily(Font(R.font.hggys)),
                fontSize = 32.sp
            )
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = MaterialTheme.colors.primary,
        )
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignAppBar(
    meViewModel: MeViewModel
) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = "行者",
                color = Color.White,
                fontFamily = FontFamily(Font(R.font.hggys)),
                fontSize = 32.sp
            )
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = MaterialTheme.colors.primary,
        ),
        navigationIcon = {
            IconButton(onClick = {
                meViewModel.updateContentState(MeContentState.INIT)
            }) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = "Back to forward UI",
                    tint = Color.White,
                )
            }
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OpAppBar(
    meViewModel: MeViewModel
) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = "行者",
                color = Color.White,
                fontFamily = FontFamily(Font(R.font.hggys)),
                fontSize = 32.sp
            )
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = MaterialTheme.colors.primary,
        ),
        navigationIcon = {
            IconButton(onClick = {
                meViewModel.updateContentState(MeContentState.ME)
            }) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = "Back to forward UI",
                    tint = Color.White,
                )
            }
        },
    )
}

@Preview
@Composable
fun AfterLoginViewPreview() {

}