package com.zzw.compose_refresh.config

data class SwipeUiState<T>(
    var list: List<T>? = null,
    var data: T? = null,
    var isLoading: Boolean = false,
    var refreshSuccess: Boolean? = null,//true：成功，false，失败或无数据
    var loadMoreSuccess: Boolean? = null,//true：成功，false，失败或无更多数据
)