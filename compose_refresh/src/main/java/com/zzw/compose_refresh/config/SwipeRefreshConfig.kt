package com.zzw.compose_refresh.config

import com.zzw.compose_refresh.R

/**
 * 可全局修改一些默认值
 * 需在Application修改
 * */
class SwipeRefreshConfig {
    companion object {
//        var isBjxMedia = false
        val defaultEmptyImage get() = R.drawable.no_data
        val defaultRefreshImages get() = AnimImage.loadingMediaList
        var defaultEmptyTitle = "暂无数据"
        //还需要什么继续加...
    }
}