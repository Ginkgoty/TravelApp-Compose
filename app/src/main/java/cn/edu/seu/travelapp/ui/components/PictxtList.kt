package cn.edu.seu.travelapp.ui.components

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cn.edu.seu.travelapp.R
import cn.edu.seu.travelapp.model.Pictxt
import cn.edu.seu.travelapp.ui.state.HomeViewContentState
import cn.edu.seu.travelapp.ui.state.TravelAppState
import cn.edu.seu.travelapp.ui.view.header
import cn.edu.seu.travelapp.viewmodel.HomeViewModel
import coil.compose.AsyncImage
import kotlinx.coroutines.delay


@Composable
fun PictxtList(
    token: String,
    homeViewModel: HomeViewModel,
    travelAppState: TravelAppState,
    paddingValues: PaddingValues
) {
    BackHandler {

    }
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        verticalArrangement = Arrangement.spacedBy(5.dp),
        horizontalArrangement = Arrangement.spacedBy(5.dp),
        modifier = Modifier
            .padding(5.dp)
            .padding(bottom = paddingValues.calculateBottomPadding())
    ) {
        val snackBarState = mutableStateOf(false)
        header {
            if (snackBarState.value) {
                Snackbar(
                    modifier = Modifier.fillMaxWidth(),
                    backgroundColor = Color.White,
                    contentColor = MaterialTheme.colors.primary
                ) {
                    Text("您还未登录，请先登录！")
                }
            }
            LaunchedEffect(key1 = snackBarState.value) {
                if (snackBarState.value) {
                    delay(2000)
                    snackBarState.value = false
                }
            }
        }
        header {
            Row(
                modifier = Modifier
                    .padding(5.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "热门图文",
                    fontFamily = FontFamily(Font(R.font.hggys)),
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(modifier = Modifier.size(10.dp))
                Text(
                    text = "热门游记",
                    fontFamily = FontFamily(Font(R.font.hggys)),
                    fontSize = 28.sp,
                    modifier = Modifier
                        .padding(start = 5.dp)
                        .alpha(0.5f)
                        .clickable {
                            homeViewModel.updateContentState(HomeViewContentState.NOTE_LIST)
                        }
                )
            }
        }
        items(homeViewModel.pictxtList) {
            PictxtItem(
                pictxt = it,
                token = token,
                homeViewModel = homeViewModel,
                travelAppState = travelAppState,
                snackBarState = snackBarState,
            )
        }
    }
}

@Composable
fun PictxtItem(
    pictxt: Pictxt,
    token: String,
    homeViewModel: HomeViewModel,
    travelAppState: TravelAppState,
    snackBarState: MutableState<Boolean>,
) {
    Surface(
        elevation = 8.dp,
        // Add op here
        modifier = Modifier.clickable {
            homeViewModel.updateCurrentPictxt(pictxt = pictxt)
            homeViewModel.fetchComments(pictxt.ptid)
            homeViewModel.updateContentState(HomeViewContentState.PICTXT_DETAIL)
            travelAppState.bottomBarState.value = false
        }
    ) {
        Column() {
            AsyncImage(
                model = pictxt.cover,
                contentDescription = "",
                modifier = Modifier.height(220.dp),
                contentScale = ContentScale.Crop
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(5.dp)
            ) {
                Text(
                    text = pictxt.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
                Spacer(modifier = Modifier.size(5.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    AsyncImage(
                        model = pictxt.upic,
                        contentDescription = "",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .clip(CircleShape)
                            .size(20.dp)
                    )
                    Spacer(modifier = Modifier.size(5.dp))
                    Text(
                        text = pictxt.uname
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
                }
            }
        }
    }
}