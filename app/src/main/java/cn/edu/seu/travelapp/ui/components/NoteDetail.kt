package cn.edu.seu.travelapp.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.magnifier
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cn.edu.seu.travelapp.model.NoteDetail
import cn.edu.seu.travelapp.R
import coil.compose.AsyncImage
import kotlinx.coroutines.launch
import org.intellij.lang.annotations.JdkConstants.HorizontalAlignment

@Composable
fun NoteDetial(
    noteDetail: NoteDetail,
    state: LazyListState,
    paddingValues: PaddingValues
) {
    Surface(
        modifier = Modifier.padding(paddingValues = paddingValues)
    ) {
        LazyColumn(
            state = state,
            modifier = Modifier
                .padding(start = 10.dp, end = 10.dp)
                .fillMaxSize()
        ) {
            item {
                Spacer(modifier = Modifier.size(5.dp))
                AsyncImage(
                    model = noteDetail.background,
                    contentDescription = "Backgroud",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .aspectRatio(16 / 9f)
                        .fillMaxWidth()
                )
            }
            item {
                Text(
                    text = noteDetail.title,
                    fontSize = 34.sp,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(modifier = Modifier.size(5.dp))
            }
            item {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "作者：",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    AsyncImage(
                        model = noteDetail.upic,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .clip(CircleShape)
                            .size(30.dp)
                    )
                    Spacer(modifier = Modifier.size(5.dp))
                    Text(
                        text = noteDetail.uname,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.size(20.dp))
                    Text(
                        text = "时间：",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = noteDetail.time,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(top = 1.dp)
                    )
                }
                Spacer(modifier = Modifier.size(10.dp))
            }
            items(noteDetail.content) { item ->
                when (item.kind) {
                    0 -> {
                        Text(
                            text = item.content,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 10.dp, bottom = 10.dp)
                        )
                    }
                    1 -> {
                        Text(
                            text = item.content,
                            fontSize = 18.sp,
                            modifier = Modifier.padding(start = 5.dp, end = 5.dp),
                            lineHeight = 30.sp
                        )
                    }
                    2 -> {
                        AsyncImage(
                            model = item.content,
                            contentDescription = "Pic in Content",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .padding(start = 10.dp, end = 10.dp)
                        )
                        Spacer(modifier = Modifier.size(5.dp))
                    }
                }
            }
        }
    }
}
