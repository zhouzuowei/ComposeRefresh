package com.zzw.compose_refresh

import android.content.Context
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.*
import androidx.compose.ui.zIndex
import com.zzw.compose_refresh.config.SwipeRefreshConfig
import com.zzw.compose_refresh.config.SwipeUiState
import com.zzw.compose_refresh.ui.EmptyView
import com.zzw.compose_refresh.ui.FirstLoadingView
import com.zzw.compose_refresh.ui.footer.BjxRefreshFooter
import com.zzw.compose_refresh.ui.header.BjxRefreshHeader
import kotlinx.coroutines.delay

@Composable
fun <T> SmartSwipeRefresh(
    state: SmartSwipeRefreshState = rememberSmartSwipeRefreshState(),
    modifier: Modifier = Modifier,
    onRefresh: (suspend () -> Unit)? = null,
    onLoadMore: (suspend () -> Unit)? = null,
    swipeUiState: SwipeUiState<T>? = null,//如果传入了swipeUiState，则SmartSwipeRefresh帮你处理:上拉下拉状态、缺省图、首次加载loading
    scrollState: LazyListState? = rememberLazyListState(),
    isAutoLoad: Boolean = true,//是否自动加载
    isAutoFill: Boolean = true,//是否加载需要填充内容
    isNeedRefresh: Boolean = true,//是否需要下拉刷新
    isNeedLoadMore: Boolean = true,//是否需要上拉加载更多
    isNeedEmptyView: Boolean = true,//是否需要缺省图
    isNeedFirstLoadView: Boolean = true,//是否需要首次加载loading动画
    EmptyImg: Int = SwipeRefreshConfig.defaultEmptyImage,//缺省图icon
    EmptyTitle: String = SwipeRefreshConfig.defaultEmptyTitle,//缺省图文案
    onEmptyClick: () -> Unit = {},//缺省图点击
    headerThreshold: Dp? = null,
    footerThreshold: Dp? = null,
    headerIndicator: @Composable () -> Unit = { BjxRefreshHeader(state.refreshFlag) },//下拉headerView
    footerIndicator: @Composable () -> Unit = { BjxRefreshFooter(state.loadMoreFlag) },//上拉footerView
    firstLoadView: @Composable () -> Unit = { FirstLoadingView(swipeUiState?.isLoading) },//首次加载loadView
    content: @Composable () -> Unit,
) {
    val density = LocalDensity.current
    LaunchedEffect(Unit) {
        state.indicatorOffsetFlow.collect {
            val currentOffset = with(density) { state.indicatorOffset + it.toDp() }
            state.snapToOffset(when {
                state.footerIsShow -> currentOffset.coerceAtMost(0.dp).coerceAtLeast(-(footerThreshold ?: Dp.Infinity))
                state.headerIsShow -> currentOffset.coerceAtLeast(0.dp).coerceAtMost(headerThreshold ?: Dp.Infinity)
                else -> currentOffset
            })
        }
    }
    LaunchedEffect(state.refreshFlag) {
        when (state.refreshFlag) {
            SmartSwipeStateFlag.REFRESHING -> {
                swipeUiState?.isLoading = true
                onRefresh?.invoke()
                state.smartSwipeRefreshAnimateFinishing = state.smartSwipeRefreshAnimateFinishing.copy(isFinishing = false, isRefresh = true)
            }
            SmartSwipeStateFlag.SUCCESS, SmartSwipeStateFlag.ERROR -> {
                delay(50)
                state.animateToOffset(0.dp)
            }
            else -> {}
        }
    }
    LaunchedEffect(state.loadMoreFlag) {
        when (state.loadMoreFlag) {
            SmartSwipeStateFlag.REFRESHING -> {
                swipeUiState?.isLoading = true
                onLoadMore?.invoke()
                state.smartSwipeRefreshAnimateFinishing = state.smartSwipeRefreshAnimateFinishing.copy(isFinishing = false, isRefresh = false)
            }
            SmartSwipeStateFlag.SUCCESS -> {
                if (isAutoFill) {
                    delay(50)
                    state.animateToOffset()
                } else {
                    delay(300)
                    state.animateToOffset(0.dp)
                }
            }
            SmartSwipeStateFlag.ERROR -> {
                delay(300)
                state.animateToOffset(0.dp)
            }
            else -> {}
        }
    }
    Box(modifier = modifier.zIndex(-1f)) {
        SubComposeSmartSwipeRefresh(headerIndicator = headerIndicator,
            footerIndicator = footerIndicator,
            isNeedRefresh,
            isNeedLoadMore) { header, footer ->
            val smartSwipeRefreshNestedScrollConnection = remember(state, header, footer) {
                SmartSwipeRefreshNestedScrollConnection(state, header, footer, isAutoLoad = isAutoLoad)
            }
            Box(modifier.nestedScroll(smartSwipeRefreshNestedScrollConnection), contentAlignment = Alignment.TopCenter) {
                if (isNeedRefresh) {
                    Box(Modifier.offset(y = -header + state.indicatorOffset)) {
                        headerIndicator()
                    }
                }
                val context = LocalContext.current
                scrollState?.let {
                    LaunchedEffect(state.smartSwipeRefreshAnimateFinishing) {
                        if (state.smartSwipeRefreshAnimateFinishing.isFinishing && !state.smartSwipeRefreshAnimateFinishing.isRefresh) {
                            scrollState.scrollBy(-context.dp2px(state.indicatorOffset.value.toInt()).toFloat())
                            state.snapToOffset(0.dp)
                        }
                    }
                }
                swipeUiState?.let {
                    LaunchedEffect(swipeUiState) {
                        if (!swipeUiState.isLoading) {
                            state.refreshFlag = when (swipeUiState.refreshSuccess) {
                                true -> SmartSwipeStateFlag.SUCCESS
                                false -> SmartSwipeStateFlag.ERROR
                                else -> SmartSwipeStateFlag.IDLE
                            }
                            state.loadMoreFlag = when (swipeUiState.loadMoreSuccess) {
                                true -> SmartSwipeStateFlag.SUCCESS
                                false -> SmartSwipeStateFlag.ERROR
                                else -> SmartSwipeStateFlag.IDLE
                            }
                        }
                    }
                }
                Box(modifier = Modifier.offset(y = state.indicatorOffset).fillMaxSize()) {
                    if (isNeedFirstLoadView && swipeUiState?.isLoading == true && swipeUiState.list.isNullOrEmpty() && swipeUiState.data == null) {
                        firstLoadView()
                    } else if (isNeedEmptyView && swipeUiState?.list.isNullOrEmpty() && swipeUiState?.data == null && swipeUiState?.refreshSuccess == false) {
                        EmptyView(EmptyImg, EmptyTitle, onEmptyClick)
                    } else content()
                    if (isNeedLoadMore) {
                        Box(modifier = Modifier.align(Alignment.BottomCenter).offset(y = footer)) {
                            footerIndicator()
                        }
                    }
                }
            }
        }
    }
}

fun Context.dp2px(dpValue: Int): Int {
    val scale = resources.displayMetrics.density
    return (dpValue * scale + 0.5f).toInt()
}

@Composable
private fun SubComposeSmartSwipeRefresh(
    headerIndicator: @Composable () -> Unit,
    footerIndicator: @Composable () -> Unit,
    isNeedRefresh: Boolean,
    isNeedLoadMore: Boolean,
    content: @Composable (header: Dp, footer: Dp) -> Unit,
) {
    SubcomposeLayout { constraints: Constraints ->
        val headerIndicatorPlaceable = subcompose("headerIndicator", headerIndicator).first().measure(constraints)
        val footerIndicatorPlaceable = subcompose("footerIndicator", footerIndicator).first().measure(constraints)
        val contentPlaceable = subcompose("content") {
            content(if (isNeedRefresh) headerIndicatorPlaceable.height.toDp() else 0.dp,
                if (isNeedLoadMore) footerIndicatorPlaceable.height.toDp() else 0.dp)
        }.map {
            it.measure(constraints)
        }.first()
        layout(contentPlaceable.width, contentPlaceable.height) {
            contentPlaceable.placeRelative(0, 0)
        }
    }
}