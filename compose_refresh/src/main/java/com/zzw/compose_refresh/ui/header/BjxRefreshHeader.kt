package com.zzw.compose_refresh.ui.header

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.zzw.compose_refresh.R
import com.zzw.compose_refresh.SmartSwipeStateFlag
import com.zzw.compose_refresh.config.SwipeRefreshConfig
import kotlinx.coroutines.delay

@Composable
fun BjxRefreshHeader(flag: SmartSwipeStateFlag) {
    val loadImg = remember { mutableStateOf(R.drawable.refresh_refresh1) }
    val imgList = SwipeRefreshConfig.defaultRefreshImages
    LaunchedEffect(flag) {
        var index = 0
        var isLoad = true
        while (isLoad) {
            if (flag == SmartSwipeStateFlag.REFRESHING) {
                loadImg.value = imgList[index]
                if (index == imgList.lastIndex) index = 0 else index++
                delay(40)
            } else {
                loadImg.value = imgList[0]
                isLoad = false
            }
        }
    }
    ConstraintLayout(modifier = Modifier.fillMaxWidth().height(50.dp).background(Color.White)) {
        val (img) = createRefs()
        Image(painter = painterResource(loadImg.value), contentDescription = null, modifier = Modifier.constrainAs(img) {
            start.linkTo(parent.start)
            end.linkTo(parent.end)
            top.linkTo(parent.top)
            bottom.linkTo(parent.bottom)
        })
    }
}
