package cn.edu.seu.travelapp.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.PinDrop
import cn.edu.seu.travelapp.model.Note
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cn.edu.seu.travelapp.R
import cn.edu.seu.travelapp.ui.state.HomeViewContentState
import cn.edu.seu.travelapp.viewmodel.HomeViewModel
import coil.compose.AsyncImage
import kotlinx.coroutines.launch

@Composable
fun NoteList(
    noteList: List<Note>,
    homeViewModel: HomeViewModel,
    paddingValues: PaddingValues,
    token: String
) {
    val scope = rememberCoroutineScope()
    val homeViewState = homeViewModel.uiState.collectAsState()
    Surface(
        modifier = Modifier
            .padding(start = 5.dp, end = 5.dp)
            .fillMaxSize(),
    ) {
        LazyColumn(
            modifier = Modifier.padding(bottom = paddingValues.calculateBottomPadding())
        ) {
            item {
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
                        modifier = Modifier
                            .padding(start = 5.dp)
                            .alpha(0.5f)
                            .clickable {
                                homeViewModel.getPictxtList(token = token)
                                homeViewModel.updateContentState(HomeViewContentState.PICTXT_LIST)
                            }
                    )
                    Spacer(modifier = Modifier.size(10.dp))
                    Text(
                        text = "热门游记",
                        fontFamily = FontFamily(Font(R.font.hggys)),
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
            items(noteList) { item ->
                NoteOutline(note = item, onNoteOutlineClicked = {
                    scope.launch {
                        homeViewState.value.drawerState.close()
                        homeViewState.value.lazyListState = LazyListState()
                        homeViewModel.updateContentState(HomeViewContentState.NOTE_DETAIL)
                        homeViewModel.setCurrentNote(item)
                        homeViewModel.getNoteDetail()
                    }
                })
                Divider(
                    thickness = 2.dp,
                    modifier = Modifier.padding(start = 5.dp, end = 5.dp)
                )
                Spacer(modifier = Modifier.size(2.dp))
            }
        }
    }
}

@Composable
fun NoteOutline(
    note: Note,
    onNoteOutlineClicked: () -> Unit
) {
    Row(
        modifier = Modifier
            .padding(start = 5.dp, end = 5.dp)
            .fillMaxWidth()
            .clickable(onClick = onNoteOutlineClicked),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = note.img,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.size(width = 120.dp, height = 120.dp)
        )
        Column(
            modifier = Modifier.padding(start = 10.dp, end = 10.dp)
        ) {
            Text(
                text = note.title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = "        " + if (note.intro.length > 100) {
                    note.intro.substring(0, 100) + "..."
                } else {
                    note.intro
                },
                fontSize = 11.sp,
            )
            Row(
            ) {
                Spacer(modifier = Modifier.weight(2f))
                Icon(
                    imageVector = Icons.Outlined.PinDrop,
                    contentDescription = "Icon",
                    modifier = Modifier
                        .size(12.dp)
                        .offset(y = 2.dp)
                )
                Text(
                    text = note.rname,
                    fontSize = 10.sp,
                )
            }
        }
    }
}