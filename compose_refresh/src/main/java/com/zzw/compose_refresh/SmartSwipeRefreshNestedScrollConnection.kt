package com.zzw.compose_refresh

import android.util.Log
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp

class SmartSwipeRefreshNestedScrollConnection(
    val state: SmartSwipeRefreshState,
    val headerHeight: Dp,
    val footerHeight: Dp,
    val dragRate:Int = 3, //下拉阻尼比例
    val isAutoLoad:Boolean = true //自动加载
) : NestedScrollConnection {
    /**
     * 预先劫持滑动事件，消费后再交由子布局
     *
     * header展示如果反向滑动优先父布局处理 做动画并拦截滑动事件
     * footer展示如果反向滑动优先父布局处理 做动画并拦截滑动事件
     */
    override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
        if (BuildConfig.DEBUG) {
            Log.d("NestedScrollConnection", "onPreScroll:${available.y}")
        }
        if (state.isRefreshing()) {
            return super.onPreScroll(available, source)
        }
        return if (source == NestedScrollSource.Drag) {
            if (state.headerIsShow || state.footerIsShow) {
                when {
                    state.headerIsShow -> {
                        // header已经在展示
                        state.refreshFlag =
                            if (state.indicatorOffset > headerHeight) SmartSwipeStateFlag.TIPS_RELEASE else SmartSwipeStateFlag.TIPS_DOWN
                        if (available.y < 0f) {
                            // 头部已经展示并且上滑动
                            state.updateOffsetDelta(available.y / dragRate)
                            Offset(x = 0f, y = available.y)
                        } else {
                            Offset.Zero
                        }
                    }
                    state.footerIsShow -> {
                        // footer已经在展示
                        state.loadMoreFlag =
                            if (state.indicatorOffset < -footerHeight) SmartSwipeStateFlag.TIPS_RELEASE else SmartSwipeStateFlag.TIPS_DOWN
                        if (available.y > 0f) {
                            // 尾部已经展示并且上滑动
                            state.updateOffsetDelta(available.y / dragRate)
                            Offset(x = 0f, y = available.y)
                        } else {
                            Offset.Zero
                        }
                    }
                    else -> Offset.Zero
                }
            } else Offset.Zero
        } else {
            if (state.isRefreshing()) {
                Offset(x = 0f, y = available.y)
            } else {
                Offset.Zero
            }
        }
    }

    /**
     * 获取子布局处理后的滑动事件
     *
     * consumed==0代表子布局没有消费事件 即列表没有被滚动
     * 此时事件在available中 把其中的事件传递给header||footer
     * 调用state.updateOffsetDelta(available.y / dragRate)做父布局动画
     * 并且消费掉滑动事件
     *
     * 刷新中不消费事件 拦截子布局即列表的滚动
     */
    private var availableY = false
    override fun onPostScroll(
        consumed: Offset,
        available: Offset,
        source: NestedScrollSource
    ): Offset {
        availableY = false
        if (BuildConfig.DEBUG) {
            Log.d("NestedScrollConnection", "onPostScroll:${consumed.y} ， ${available.y}， $source")
        }
        if (state.isRefreshing()) {
            return super.onPostScroll(consumed, available, source)
        }
        return if (source == NestedScrollSource.Drag) {
            if (available.y != 0f && consumed.y == 0f) {
                if (headerHeight != 0.dp && available.y > 0f) {
                    state.updateOffsetDelta(available.y / dragRate)
                }
                if (footerHeight != 0.dp && available.y < 0f) {
                    state.updateOffsetDelta(available.y / dragRate)
                }
                Offset(x = 0f, y = available.y)
            } else {
                Offset.Zero
            }
//        } else if (isAutoLoad && source == NestedScrollSource.Fling && available.y < (-footerHeight.value) && footerHeight != 0.dp) {
//            //还有余量
//            availableY = true
////            state.updateOffsetDelta(available.y / dragRate)
////            Offset(x = 0f, y = available.y)
//            Offset.Zero
        } else {
            Offset.Zero
        }
    }

    /**
     * indicatorOffset>=0 header显示 indicatorOffset<=0 footer显示
     * 拖动到头部快速滑动时 如果indicatorOffset>headerHeight则
     */
    override suspend fun onPreFling(available: Velocity): Velocity {
        if (BuildConfig.DEBUG) {
            Log.d("NestedScrollConnection", "onPreFling:${available.y}")
        }
        if (!state.isRefreshing()) {
            if (state.indicatorOffset > headerHeight) {
                state.animateToOffset(headerHeight)
                state.refreshFlag = SmartSwipeStateFlag.REFRESHING
            } else if (state.indicatorOffset < -footerHeight) {
                state.animateToOffset(-footerHeight)
                state.loadMoreFlag = SmartSwipeStateFlag.REFRESHING
            } else {
                if (state.indicatorOffset != 0.dp) {
                    state.animateToOffset(0.dp)
                } else {
                    return super.onPreFling(available)
                }
            }
            return Velocity(available.x, available.y)
        }
        return super.onPreFling(available)
    }

    override suspend fun onPostFling(consumed: Velocity, available: Velocity): Velocity {
        if (BuildConfig.DEBUG) {
            Log.d("NestedScrollConnection", "onPostFling:${consumed.y} ， ${available.y}")
        }
//        if (isAutoLoad && available.y < 0 && availableY) {
        //设置一个固定值
        if (isAutoLoad && available.y < -3000) {
            //还有向下的余量
            state.snapToOffset(-footerHeight)
            state.loadMoreFlag = SmartSwipeStateFlag.REFRESHING
        }
        return super.onPostFling(consumed, available)
    }
}