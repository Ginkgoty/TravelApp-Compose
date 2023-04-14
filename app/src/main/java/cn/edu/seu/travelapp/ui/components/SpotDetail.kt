package cn.edu.seu.travelapp.ui.components


import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.UnfoldLess
import androidx.compose.material.icons.filled.UnfoldMore
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.lerp
import androidx.core.content.ContextCompat.startActivity
import cn.edu.seu.travelapp.R
import cn.edu.seu.travelapp.model.Detail
import coil.compose.AsyncImage
import com.amap.api.maps.MapsInitializer
import com.amap.api.maps.model.CameraPosition
import com.amap.api.maps.model.LatLng
import com.google.accompanist.pager.*
import com.melody.map.gd_compose.GDMap
import com.melody.map.gd_compose.poperties.MapProperties
import com.melody.map.gd_compose.poperties.MapUiSettings
import com.melody.map.gd_compose.position.rememberCameraPositionState
import kotlin.math.absoluteValue

@OptIn(ExperimentalPagerApi::class)
@Composable
fun SpotDetail(
    sname: String,
    detail: Detail,
    paddingValues: PaddingValues
) {
    val context = LocalContext.current

    MapsInitializer.updatePrivacyAgree(context, true)
    MapsInitializer.updatePrivacyShow(context, true, true)

    val images = arrayOf(detail.pic1, detail.pic2, detail.pic3)
    val pagerState = rememberPagerState()
    var introFoldState by remember { mutableStateOf(true) }
    var trafficFoldState by remember { mutableStateOf(true) }
    var ticketFoldState by remember { mutableStateOf(true) }
    var opennessFoldState by remember { mutableStateOf(true) }
    val cameraPostionState = rememberCameraPositionState()

    // intent to open Gaode Map
    val intent = Intent()
    intent.action = Intent.ACTION_VIEW
    intent.addCategory(Intent.CATEGORY_DEFAULT)

    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = sname,
            fontFamily = FontFamily(Font(R.font.hggys)),
            fontSize = 40.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(10.dp)
        )
        HorizontalPager(
            count = 3,
            modifier = Modifier
                .aspectRatio(16f / 9f)
                .size(200.dp)
                .padding(start = 10.dp, end = 10.dp),
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
                    model = images[page],
                    contentDescription = "Picture of this spot.",
                    contentScale = ContentScale.Crop
                )
            }
        }
        HorizontalPagerIndicator(
            pagerState = pagerState,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(16.dp),
        )
        Spacer(modifier = Modifier.size(5.dp))
        Surface(
            elevation = 2.dp,
            modifier = Modifier
                .padding(start = 10.dp, end = 10.dp)
                .fillMaxWidth()
        ) {
            Column() {
                if (detail.intro.length > 150) {
                    Row() {
                        Text(
                            text = "景点介绍",
                            fontWeight = FontWeight.Bold,
                            fontSize = 28.sp,
                            fontFamily = FontFamily(Font(R.font.hggys)),
                            modifier = Modifier.offset(x = 10.dp, y = 10.dp),
                        )
                        if (introFoldState) {
                            IconButton(
                                onClick = {
                                    introFoldState = !introFoldState
                                },
                                modifier = Modifier.offset(y = 4.dp),
                            ) {
                                Icon(
                                    imageVector = Icons.Default.UnfoldMore,
                                    contentDescription = "expand"
                                )
                            }
                        } else {
                            IconButton(
                                onClick = {
                                    introFoldState = !introFoldState
                                },
                                modifier = Modifier.offset(y = 4.dp),
                            ) {
                                Icon(
                                    imageVector = Icons.Default.UnfoldLess,
                                    contentDescription = "expand"
                                )
                            }
                        }
                    }
                } else {
                    Text(
                        text = "景点介绍",
                        fontWeight = FontWeight.Bold,
                        fontSize = 28.sp,
                        fontFamily = FontFamily(Font(R.font.hggys)),
                        modifier = Modifier.offset(x = 10.dp, y = 10.dp),
                    )
                }
                Spacer(modifier = Modifier.padding(5.dp))
                Log.d("introFoldState", introFoldState.toString())
                if (detail.intro.length > 150 && introFoldState) {
                    Text(
                        text = detail.intro.substring(0, 150) + "...",
                        modifier = Modifier.padding(start = 20.dp, end = 20.dp),
                        fontSize = 20.sp,
                    )
                } else {
                    Text(
                        text = detail.intro,
                        modifier = Modifier.padding(start = 20.dp, end = 20.dp),
                        fontSize = 20.sp,
                    )
                }
                Spacer(modifier = Modifier.padding(5.dp))
            }
        }
        Surface(
            elevation = 2.dp,
            modifier = Modifier
                .padding(10.dp)
                .fillMaxWidth()
        ) {
            Column() {
                Text(
                    text = "联系方式",
                    fontWeight = FontWeight.Bold,
                    fontSize = 28.sp,
                    fontFamily = FontFamily(Font(R.font.hggys)),
                    modifier = Modifier.offset(x = 10.dp, y = 10.dp),
                )
                Spacer(modifier = Modifier.padding(10.dp))
                Text(
                    text = detail.tel,
                    modifier = Modifier.padding(start = 20.dp, end = 20.dp),
                    fontSize = 20.sp,
                )
                Spacer(modifier = Modifier.padding(5.dp))
            }
        }

        Surface(
            elevation = 2.dp,
            modifier = Modifier
                .padding(10.dp)
                .fillMaxWidth()
        ) {
            Column() {
                Text(
                    text = "用时参考",
                    fontWeight = FontWeight.Bold,
                    fontSize = 28.sp,
                    fontFamily = FontFamily(Font(R.font.hggys)),
                    modifier = Modifier.offset(x = 10.dp, y = 10.dp),
                )
                Spacer(modifier = Modifier.padding(10.dp))
                Text(
                    text = detail.consumption,
                    modifier = Modifier.padding(start = 20.dp, end = 20.dp),
                    fontSize = 20.sp,
                )
                Spacer(modifier = Modifier.padding(5.dp))
            }
        }
        Spacer(modifier = Modifier.size(5.dp))
        Surface(
            elevation = 2.dp,
            modifier = Modifier
                .padding(start = 10.dp, end = 10.dp)
                .fillMaxWidth()
        ) {
            Column() {
                if (detail.traffic.length > 100) {
                    Row() {
                        Text(
                            text = "交通",
                            fontWeight = FontWeight.Bold,
                            fontSize = 28.sp,
                            fontFamily = FontFamily(Font(R.font.hggys)),
                            modifier = Modifier.offset(x = 10.dp, y = 10.dp),
                        )
                        if (trafficFoldState) {
                            IconButton(
                                onClick = {
                                    trafficFoldState = !trafficFoldState
                                },
                                modifier = Modifier.offset(y = 4.dp),
                            ) {
                                Icon(
                                    imageVector = Icons.Default.UnfoldMore,
                                    contentDescription = "expand"
                                )
                            }
                        } else {
                            IconButton(
                                onClick = {
                                    trafficFoldState = !trafficFoldState
                                },
                                modifier = Modifier.offset(y = 4.dp),
                            ) {
                                Icon(
                                    imageVector = Icons.Default.UnfoldLess,
                                    contentDescription = "expand"
                                )
                            }
                        }
                    }
                } else {
                    Text(
                        text = "交通",
                        fontWeight = FontWeight.Bold,
                        fontSize = 28.sp,
                        fontFamily = FontFamily(Font(R.font.hggys)),
                        modifier = Modifier.offset(x = 10.dp, y = 10.dp),
                    )
                }
                Spacer(modifier = Modifier.padding(5.dp))
                if (detail.traffic.length > 100 && trafficFoldState) {
                    Text(
                        text = detail.traffic.substring(0, 100) + "...",
                        modifier = Modifier.padding(start = 20.dp, end = 20.dp),
                        fontSize = 20.sp,
                    )
                } else {
                    Text(
                        text = detail.traffic,
                        modifier = Modifier.padding(start = 20.dp, end = 20.dp),
                        fontSize = 20.sp,
                    )
                }
                Spacer(modifier = Modifier.padding(5.dp))
            }
        }
        Spacer(modifier = Modifier.padding(5.dp))
        Surface(
            elevation = 2.dp,
            modifier = Modifier
                .padding(start = 10.dp, end = 10.dp)
                .fillMaxWidth()
        ) {
            Column() {
                if (detail.ticket.length > 100) {
                    Row() {
                        Text(
                            text = "门票",
                            fontWeight = FontWeight.Bold,
                            fontSize = 28.sp,
                            fontFamily = FontFamily(Font(R.font.hggys)),
                            modifier = Modifier.offset(x = 10.dp, y = 10.dp),
                        )
                        IconButton(
                            onClick = {
                                ticketFoldState = !ticketFoldState
                            },
                            modifier = Modifier.offset(y = 4.dp),
                        ) {
                            Icon(
                                imageVector = Icons.Default.UnfoldMore,
                                contentDescription = "expand"
                            )
                        }
                    }
                } else {
                    Text(
                        text = "门票",
                        fontWeight = FontWeight.Bold,
                        fontSize = 28.sp,
                        fontFamily = FontFamily(Font(R.font.hggys)),
                        modifier = Modifier.offset(x = 10.dp, y = 10.dp),
                    )
                }
                Spacer(modifier = Modifier.padding(5.dp))
                if (detail.ticket.length > 100 && ticketFoldState) {
                    Text(
                        text = detail.ticket.substring(0, 100) + "...",
                        modifier = Modifier.padding(start = 20.dp, end = 20.dp),
                        fontSize = 20.sp,
                    )
                } else {
                    Text(
                        text = detail.ticket,
                        modifier = Modifier.padding(start = 20.dp, end = 20.dp),
                        fontSize = 20.sp,
                    )
                }
                Spacer(modifier = Modifier.padding(5.dp))
            }
        }
        Spacer(modifier = Modifier.padding(5.dp))
        Surface(
            elevation = 2.dp,
            modifier = Modifier
                .padding(start = 10.dp, end = 10.dp)
                .fillMaxWidth()
        ) {
            Column() {
                if (detail.openness.length > 100) {
                    Row() {
                        Text(
                            text = "开放时间",
                            fontWeight = FontWeight.Bold,
                            fontSize = 28.sp,
                            fontFamily = FontFamily(Font(R.font.hggys)),
                            modifier = Modifier.offset(x = 10.dp, y = 10.dp),
                        )
                        if (opennessFoldState) {
                            IconButton(
                                onClick = {
                                    opennessFoldState = !opennessFoldState
                                },
                                modifier = Modifier.offset(y = 4.dp),
                            ) {
                                Icon(
                                    imageVector = Icons.Default.UnfoldMore,
                                    contentDescription = "expand"
                                )
                            }
                        } else {
                            IconButton(
                                onClick = {
                                    opennessFoldState = !opennessFoldState
                                },
                                modifier = Modifier.offset(y = 4.dp),
                            ) {
                                Icon(
                                    imageVector = Icons.Default.UnfoldLess,
                                    contentDescription = "expand"
                                )
                            }
                        }
                    }
                } else {
                    Text(
                        text = "开放时间",
                        fontWeight = FontWeight.Bold,
                        fontSize = 28.sp,
                        fontFamily = FontFamily(Font(R.font.hggys)),
                        modifier = Modifier.offset(x = 10.dp, y = 10.dp),
                    )
                }
                Spacer(modifier = Modifier.padding(5.dp))
                if (detail.openness.length > 100 && opennessFoldState) {
                    Text(
                        text = detail.openness.substring(0, 100) + "...",
                        modifier = Modifier.padding(start = 20.dp, end = 20.dp),
                        fontSize = 20.sp,
                    )
                } else {
                    Text(
                        text = detail.openness,
                        modifier = Modifier.padding(start = 20.dp, end = 20.dp),
                        fontSize = 20.sp,
                    )
                }
                Spacer(modifier = Modifier.padding(5.dp))
            }
        }
        Spacer(modifier = Modifier.padding(5.dp))
        Surface(
            elevation = 2.dp,
            modifier = Modifier
                .padding(start = 10.dp, end = 10.dp)
                .fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.clickable {
                    startActivity(context, intent, null)
                }
            ) {
                Row() {
                    Text(
                        text = "位置",
                        fontWeight = FontWeight.Bold,
                        fontSize = 28.sp,
                        fontFamily = FontFamily(Font(R.font.hggys)),
                        modifier = Modifier.offset(x = 10.dp, y = 10.dp),
                    )
                    Spacer(modifier = Modifier.size(15.dp))
                    Icon(
                        modifier = Modifier.offset(y = 17.dp),
                        imageVector = Icons.Default.OpenInNew,
                        contentDescription = "redirect"
                    )
                }
                Spacer(modifier = Modifier.padding(5.dp))
                Text(
                    text = detail.location,
                    fontSize = 20.sp,
                    modifier = Modifier.offset(x = 20.dp, y = 5.dp),
                )
                GDMap(
                    modifier = Modifier
                        .size(width = 400.dp, height = 300.dp)
                        .padding(20.dp),
                    properties = MapProperties(
                        isShowMapLabels = true
                    ),
                    uiSettings = MapUiSettings(
                        showMapLogo = true,
                        isScrollGesturesEnabled = true,
                        isScaleControlsEnabled = true
                    ),
                    cameraPositionState = cameraPostionState
                )
                LaunchedEffect(key1 = detail) {
                    Log.d("Launched", detail.lat.toString())
                    cameraPostionState.position = CameraPosition.fromLatLngZoom(
                        LatLng(
                            detail.lat,
                            detail.lng
                        ), 15F
                    )
                    val url = if (getInstallerPackageName(
                            context = context,
                            packageName = "com.autonavi.minimap"
                        )
                    ) {
                        // "androidamap://viewMap?sourceApplication=travelapp&poiname=${sname}&lat=${detail.lat}&lon=${detail.lng}&dev=0"
                        "amapuri://poi/detail?sourceApplication=travelapp&poiname=${sname}&lat=${detail.lat}&lon=${detail.lng}"
                    } else {
                        "https://m.amap.com/regeo?lat=${detail.lat}&lng=${detail.lng}"
                    }
                    intent.data = Uri.parse(url)
                }
            }
        }
        Spacer(
            modifier = Modifier
                .padding(5.dp)
                .padding(bottom = paddingValues.calculateBottomPadding())
        )
    }
}

fun getInstallerPackageName(context: Context, packageName: String): Boolean {
//    runCatching {
//    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
//        return !context.packageManager.getInstallSourceInfo(packageName).installingPackageName.isNullOrEmpty()
//    else {
    @Suppress("DEPRECATION")
    val pinfo = context.packageManager.getInstalledPackages(0) // 获取所有已安装程序的包信息
    for (i in pinfo.indices) {
        val pn = pinfo[i].packageName
        Log.d("pn", pn)
        if (pn == packageName) {
            return true
        }
    }
//    }
//    }
    return false
}

