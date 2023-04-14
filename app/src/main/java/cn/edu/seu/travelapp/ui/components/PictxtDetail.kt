package cn.edu.seu.travelapp.ui.components

import android.graphics.Rect
import android.util.Log
import android.view.ViewTreeObserver
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.outlined.Send
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.lerp
import cn.edu.seu.travelapp.model.Pictxt
import cn.edu.seu.travelapp.model.PtComment
import cn.edu.seu.travelapp.model.PtCommentUpload
import cn.edu.seu.travelapp.ui.state.HomeViewContentState
import cn.edu.seu.travelapp.ui.state.TravelAppState
import cn.edu.seu.travelapp.viewmodel.HomeViewModel
import coil.compose.AsyncImage
import com.google.accompanist.pager.*
import kotlinx.coroutines.delay
import kotlin.math.absoluteValue

@Composable
fun PictxtDetail(
    token: String,
    homeViewModel: HomeViewModel,
    travelAppState: TravelAppState
) {
    val pictxt = homeViewModel.uiState.collectAsState().value.pictxt
    val bottomBarState = remember { mutableStateOf(false) }
    val snackBarState = remember { mutableStateOf(false) }

    val isKeyboardOpen by keyboardAsState() // true or false
    val wasConditionTrue = remember { mutableStateOf(false) }

    var comment by remember { mutableStateOf("") }
    val currentCid = remember { mutableStateOf(0) }

    BackHandler {
        homeViewModel.getPictxtList(token = token)
        homeViewModel.updateContentState(HomeViewContentState.PICTXT_LIST)
        travelAppState.bottomBarState.value = true
    }

    Scaffold(
        topBar = {
            PictxtTopBar(
                pictxt = pictxt!!,
                homeViewModel = homeViewModel,
                snackBarState = snackBarState,
                token = token
            ) {
                homeViewModel.getPictxtList(token)
                homeViewModel.updateContentState(HomeViewContentState.PICTXT_LIST)
                travelAppState.bottomBarState.value = true
            }
        },
        bottomBar = {
            if (bottomBarState.value) {
                val focusRequester = remember { FocusRequester() }
                val focusManager = LocalFocusManager.current
                Surface(
                    elevation = 20.dp
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextField(
                            value = comment,
                            onValueChange = {
                                if (it.count() < 100)
                                    comment = it
                            },
                            modifier = Modifier
                                .focusRequester(focusRequester)
                                .weight(0.7f),
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                            keyboardActions = KeyboardActions(onDone = {
                                focusManager.clearFocus()
                                bottomBarState.value = false
                            }),
                            placeholder = {
                                Text("请在此进行评论")
                            },
                            colors = TextFieldDefaults.textFieldColors(
                                backgroundColor = Color.White,
                                unfocusedIndicatorColor = Color.White,
                                focusedIndicatorColor = Color.White
                            )
                        )
                        if (comment != "") {
                            Button(
                                onClick = {
                                    Log.d("currentCid", currentCid.value.toString())
                                    homeViewModel.comment(
                                        ptComment = PtCommentUpload(
                                            token = token,
                                            ptid = pictxt!!.ptid,
                                            belong = currentCid.value,
                                            comment = comment
                                        )
                                    )
                                    focusManager.clearFocus()
                                    bottomBarState.value = false
                                    wasConditionTrue.value = !wasConditionTrue.value
                                    comment = ""
                                },
                                colors = ButtonDefaults.buttonColors(
                                    backgroundColor = MaterialTheme.colors.primary,
                                    contentColor = Color.White
                                ),
                                modifier = Modifier
                                    .weight(0.3f)
                                    .padding(10.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Send,
                                    contentDescription = "send"
                                )
                                Spacer(modifier = Modifier.size(5.dp))
                                Text(
                                    text = "发送",
                                    fontWeight = FontWeight.Bold,
                                )
                            }
                        }
                        LaunchedEffect(Unit) {
                            focusRequester.requestFocus()
                        }
                        LaunchedEffect(isKeyboardOpen) {
                            Log.d("Keyboard", isKeyboardOpen.toString())
                            // Capture Opened -> Closed
                            if (isKeyboardOpen == Keyboard.Closed && wasConditionTrue.value) {
                                bottomBarState.value = false
                                wasConditionTrue.value = !wasConditionTrue.value
                            }
                            wasConditionTrue.value = isKeyboardOpen == Keyboard.Opened
                        }
                    }
                }
            }
        }
    ) { it ->
        PictxtContent(
            pictxt = pictxt!!,
            paddingValues = it,
            bottomBarState = bottomBarState,
            homeViewModel = homeViewModel,
            token = token,
            currentCid = currentCid,
            wasCondition = wasConditionTrue,
            snackState = snackBarState
        )

    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun PictxtContent(
    pictxt: Pictxt,
    bottomBarState: MutableState<Boolean>,
    token: String,
    homeViewModel: HomeViewModel,
    currentCid: MutableState<Int>,
    wasCondition: MutableState<Boolean>,
    snackState: MutableState<Boolean>,
    paddingValues: PaddingValues
) {
    val pagerState = rememberPagerState()
    val focusManager = LocalFocusManager.current
    val scrollState = rememberScrollState()
    val uploadSnackState = remember {
        mutableStateOf(false)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(state = scrollState)
    ) {
        if (snackState.value) {
            Snackbar(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = Color.White,
                contentColor = MaterialTheme.colors.primary
            ) {
                Text(
                    text = "您还未登录，请先登录！",
                    textAlign = TextAlign.Center
                )
            }
        }
        if (uploadSnackState.value) {
            Snackbar(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = Color.White,
                contentColor = MaterialTheme.colors.primary
            ) {
                Text(
                    text = "评论失败，请重试！",
                    textAlign = TextAlign.Center
                )
            }
        }
        LaunchedEffect(key1 = snackState.value) {
            delay(2000)
            snackState.value = false
        }
        LaunchedEffect(key1 = homeViewModel.isComment.value) {
            if (homeViewModel.isComment.value) {
                uploadSnackState.value = !homeViewModel.commentState.value
                delay(2000)
                uploadSnackState.value = false
                homeViewModel.isComment.value = false
            }
        }
        LaunchedEffect(key1 = homeViewModel.isComment.value) {
            if (homeViewModel.isComment.value) {
                Log.d("fetch", "triggered")
                homeViewModel.fetchComments(ptid = pictxt.ptid)
            }
        }
        HorizontalPager(
            count = pictxt.imagelist.size,
            modifier = Modifier
                .height(450.dp)
                .padding(start = 10.dp, end = 10.dp)
                .pointerInput(Unit) {
                    detectTapGestures(onTap = {
                        focusManager.clearFocus()
                        bottomBarState.value = false
                        wasCondition.value = !wasCondition.value
                    })
                },
            itemSpacing = 5.dp,
            state = pagerState
        ) { page ->
            Card(
                modifier = Modifier
                    .graphicsLayer {
                        // Calculate the absolute offset for the current page from the
                        // scroll position. We use the absolute value which allows us to mirror
                        // any effects for both directions
                        val pageOffset = calculateCurrentOffsetForPage(page).absoluteValue

                        // We animate the scaleX + scaleY, between 85% and 100%
                        lerp(
                            start = 0.85f,
                            stop = 1f,
                            fraction = 1f - pageOffset.coerceIn(0f, 1f)
                        ).also { scale ->
                            scaleX = scale
                            scaleY = scale
                        }

                        // We animate the alpha, between 50% and 100%
                        alpha = lerp(
                            start = 0.5f,
                            stop = 1f,
                            fraction = 1f - pageOffset.coerceIn(0f, 1f)
                        )
                    }
            ) {
                AsyncImage(
                    model = pictxt.imagelist[page],
                    contentDescription = "Picture of this spot.",
                    contentScale = ContentScale.FillWidth
                )
            }
        }
        HorizontalPagerIndicator(
            pagerState = pagerState,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(5.dp),
            activeColor = MaterialTheme.colors.primary
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 10.dp)
        ) {
            Text(
                text = pictxt.title,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp
            )
            Spacer(modifier = Modifier.size(10.dp))
            Text(
                text = pictxt.text,
                fontSize = 20.sp
            )
            Spacer(modifier = Modifier.size(20.dp))
            Text(
                text = pictxt.datetime,
                fontSize = 14.sp,
                color = Color.Gray
            )
            Divider(modifier = Modifier.padding(top = 10.dp, bottom = 10.dp))
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xfff6f6f6))
                    .alpha(0.5f)
                    .fillMaxWidth()
                    .clickable { // click comment
                        if (token != "") {
                            bottomBarState.value = true
                            currentCid.value = 0
                        } else {
                            snackState.value = true
                        }
                    },
                contentAlignment = Alignment.CenterStart
            ) {
                Text(
                    text = "发表一条友善的评论吧！",
                    modifier = Modifier.padding(10.dp)
                )
            }
            Spacer(modifier = Modifier.size(5.dp))
            homeViewModel.ptCommentList.value.forEach {
                PtCommentCard(
                    ptComment = it,
                    onClick = {
                        if (token != "") {
                            bottomBarState.value = true
                        } else {
                            snackState.value = true
                        }
                    },
                    currentCid = currentCid
                )
                Divider()
            }
        }
    }
}

@Composable
fun PtCommentCard(
    ptComment: PtComment,
    currentCid: MutableState<Int>,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
    ) {
        AsyncImage(
            model = ptComment.upic,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape),
        )
        Spacer(modifier = Modifier.size(10.dp))
        Column() {
            Column(
                modifier = Modifier.clickable {
                    currentCid.value = ptComment.cid
                    onClick()
                },
            ) {
                Text(
                    text = ptComment.uname,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(modifier = Modifier.size(5.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,

                    ) {
                    Text(
                        text = ptComment.comment
                    )
                }
                Text(
                    text = ptComment.datetime,
                    fontSize = 14.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.End,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            ProcessReply(
                replies = ptComment.replies,
                funame = ptComment.uname,
                fbelong = ptComment.belong,
                currentCid = currentCid,
                onClick = onClick
            )
        }
    }
}

@Composable
fun SubPtComment(
    ptComment: PtComment,
    funame: String,
    fbelong: Int,
    currentCid: MutableState<Int>,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 10.dp, bottom = 10.dp, top = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = ptComment.upic,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(20.dp)
                .clip(CircleShape),
        )
        Spacer(modifier = Modifier.size(10.dp))
        Column(
            modifier = Modifier.clickable {
                currentCid.value = ptComment.cid
                onClick()
            }
        ) {
            Text(
                text = ptComment.uname,
                fontWeight = FontWeight.Bold,
            )
            Spacer(modifier = Modifier.size(5.dp))
            Row() {
                if (fbelong != 0) {
                    Text(
                        text = "回复 $funame : "
                    )
                }
                Text(
                    text = ptComment.comment
                )
            }
            Text(
                text = ptComment.datetime,
                fontSize = 14.sp,
                color = Color.Gray,
                textAlign = TextAlign.End,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun ProcessReply(
    replies: List<PtComment>,
    funame: String,
    fbelong: Int,
    currentCid: MutableState<Int>,
    onClick: () -> Unit
) {
    if (replies.isEmpty()) {
        return
    }
    replies.forEach {
        SubPtComment(
            ptComment = it,
            funame = funame,
            fbelong = fbelong,
            onClick = onClick,
            currentCid = currentCid
        )
        ProcessReply(
            replies = it.replies,
            funame = it.uname,
            fbelong = it.belong,
            currentCid = currentCid,
            onClick = onClick
        )
    }
}

@Composable
fun PictxtTopBar(
    pictxt: Pictxt,
    homeViewModel: HomeViewModel,
    snackBarState: MutableState<Boolean>,
    token: String,
    onArrowClick: () -> Unit
) {
    Surface(
        elevation = 8.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .height(60.dp)
                .padding(10.dp)
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = null,
                modifier = Modifier.clickable {
                    onArrowClick()
                }
            )
            Spacer(modifier = Modifier.size(10.dp))
            AsyncImage(
                model = pictxt.upic,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape),
            )
            Spacer(modifier = Modifier.size(10.dp))
            Text(
                text = pictxt.uname,
                fontSize = 20.sp
            )
            Spacer(modifier = Modifier.weight(1f))
            var favState by remember { mutableStateOf(pictxt.isfavorite) }
            if (favState) {
                Icon(
                    imageVector = Icons.Rounded.Favorite,
                    contentDescription = "",
                    tint = Color.Red,
                    modifier = Modifier.clickable {
                        if (token != "") {
                            favState = !favState
                            pictxt.isfavorite = !pictxt.isfavorite
                            homeViewModel.unfavPictxt(pictxt.ptid, token)
                            pictxt.favcount -= 1
                        } else {
                            snackBarState.value = true
                        }
                    }
                )
            } else {
                Icon(
                    imageVector = Icons.Rounded.FavoriteBorder,
                    contentDescription = "",
                    modifier = Modifier.clickable {
                        if (token != "") {
                            favState = !favState
                            pictxt.isfavorite = !pictxt.isfavorite
                            homeViewModel.favPictxt(pictxt.ptid, token)
                            pictxt.favcount += 1
                        } else {
                            snackBarState.value = true
                        }
                    })
            }
            Spacer(modifier = Modifier.size(5.dp))
            Text(text = pictxt.favcount.toString())
            homeViewModel.ptCommentList.value.forEach {

            }
        }
    }

}

enum class Keyboard {
    Opened, Closed
}

@Composable
fun keyboardAsState(): State<Keyboard> {
    val keyboardState = remember { mutableStateOf(Keyboard.Closed) }
    val view = LocalView.current
    DisposableEffect(view) {
        val onGlobalListener = ViewTreeObserver.OnGlobalLayoutListener {
            val rect = Rect()
            view.getWindowVisibleDisplayFrame(rect)
            val screenHeight = view.rootView.height
            val keypadHeight = screenHeight - rect.bottom
            keyboardState.value = if (keypadHeight > screenHeight * 0.15) {
                Keyboard.Opened
            } else {
                Keyboard.Closed
            }
        }
        view.viewTreeObserver.addOnGlobalLayoutListener(onGlobalListener)

        onDispose {
            view.viewTreeObserver.removeOnGlobalLayoutListener(onGlobalListener)
        }
    }

    return keyboardState
}
