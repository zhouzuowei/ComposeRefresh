package com.zzw.compose_refresh.ui.footer

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import com.zzw.compose_refresh.R
import com.zzw.compose_refresh.SmartSwipeStateFlag
import com.zzw.compose_refresh.config.AnimImage
import kotlinx.coroutines.delay

@Composable
fun BjxRefreshFooter(flag: SmartSwipeStateFlag) {
    val loadImg = remember { mutableStateOf(R.drawable.loading_00000) }
    LaunchedEffect(flag) {
        var index = 0
        var isLoad = true
        while (isLoad) {
            if (flag == SmartSwipeStateFlag.REFRESHING) {
                loadImg.value = AnimImage.loadMoreList[index]
                if (index == AnimImage.loadMoreList.lastIndex) index = 0 else index++
                delay(40)
            } else {
                loadImg.value = AnimImage.loadMoreList[0]
                isLoad = false
            }
        }
    }
    ConstraintLayout(modifier = Modifier.fillMaxWidth().height(50.dp).background(Color.White)) {
        val (img, textView) = createRefs()
        if (flag == SmartSwipeStateFlag.ERROR) {
            Text(text = "暂无更多数据", fontSize = 14.sp, color = "999999".toColor(), modifier = Modifier.constrainAs(textView) {
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                top.linkTo(parent.top)
                bottom.linkTo(parent.bottom)
            })
        } else {
            Image(painter = painterResource(loadImg.value), contentDescription = null, modifier = Modifier.constrainAs(img) {
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                top.linkTo(parent.top)
                bottom.linkTo(parent.bottom)
            })
        }
    }
}

fun String.toColor(): Color = Color(this.toLong(16) or -0x1000000)
