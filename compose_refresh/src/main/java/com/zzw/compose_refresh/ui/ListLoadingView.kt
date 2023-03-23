package com.zzw.compose_refresh.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.constraintlayout.compose.ConstraintLayout
import com.zzw.compose_refresh.R
import com.zzw.compose_refresh.config.AnimImage
import kotlinx.coroutines.delay

@Composable
fun FirstLoadingView(isLoading: Boolean? = true) {
    isLoading?.let {
        val loadImg = remember { mutableStateOf(R.drawable.loading_00000) }
        LaunchedEffect(isLoading) {
            var index = 0
            var isLoad = true
            while (isLoad) {
                if (isLoading) {
                    loadImg.value = AnimImage.loadMoreList[index]
                    if (index == AnimImage.loadMoreList.lastIndex) index = 0 else index++
                    delay(40)
                } else {
                    loadImg.value = AnimImage.loadMoreList[0]
                    isLoad = false
                }
            }
        }
        ConstraintLayout(modifier = Modifier.fillMaxWidth().fillMaxHeight().background(Color.Transparent)) {
            val (img) = createRefs()
            Image(painter = painterResource(loadImg.value), contentDescription = null, modifier = Modifier.constrainAs(img) {
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                top.linkTo(parent.top)
                bottom.linkTo(parent.bottom)
            })
        }
    }
}