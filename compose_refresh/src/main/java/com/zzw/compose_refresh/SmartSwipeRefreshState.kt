package com.zzw.compose_refresh

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.tween
import androidx.compose.foundation.MutatePriority
import androidx.compose.foundation.MutatorMutex
import androidx.compose.runtime.*
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

enum class SmartSwipeStateFlag {
    IDLE, REFRESHING, SUCCESS, ERROR, TIPS_DOWN, TIPS_RELEASE
}

@Composable
fun rememberSmartSwipeRefreshState(): SmartSwipeRefreshState {
    return remember {
        SmartSwipeRefreshState()
    }
}

data class SmartSwipeRefreshAnimateFinishing(
    val isFinishing: Boolean = true,
    val isRefresh: Boolean = true,
)

class SmartSwipeRefreshState {
    private val mutatorMutex = MutatorMutex()
    private val indicatorOffsetAnimatable = Animatable(0.dp, Dp.VectorConverter)
    val indicatorOffset get() = indicatorOffsetAnimatable.value

    private val _indicatorOffsetFlow = MutableStateFlow(0f)
    val indicatorOffsetFlow: Flow<Float> get() = _indicatorOffsetFlow

    val headerIsShow by derivedStateOf { indicatorOffset > 0.dp }
    val footerIsShow by derivedStateOf { indicatorOffset < 0.dp }

    var refreshFlag: SmartSwipeStateFlag by mutableStateOf(SmartSwipeStateFlag.IDLE)
    var loadMoreFlag: SmartSwipeStateFlag by mutableStateOf(SmartSwipeStateFlag.IDLE)
    var smartSwipeRefreshAnimateFinishing: SmartSwipeRefreshAnimateFinishing by mutableStateOf(SmartSwipeRefreshAnimateFinishing(isFinishing = true,
        isRefresh = true))

    fun isRefreshing() =
        refreshFlag == SmartSwipeStateFlag.REFRESHING || loadMoreFlag == SmartSwipeStateFlag.REFRESHING || !smartSwipeRefreshAnimateFinishing.isFinishing

    fun updateOffsetDelta(value: Float) {
        _indicatorOffsetFlow.value = value
    }

    suspend fun snapToOffset(value: Dp) {
        mutatorMutex.mutate(MutatePriority.UserInput) {
            indicatorOffsetAnimatable.snapTo(value)
        }
    }

    suspend fun animateToOffset(value: Dp, time:Int = 300) {
        mutatorMutex.mutate {
            indicatorOffsetAnimatable.animateTo(value, tween(time)) {
                if (this.value == 0.dp && !smartSwipeRefreshAnimateFinishing.isFinishing) {
                    // 此时动画完全停止
                    smartSwipeRefreshAnimateFinishing = smartSwipeRefreshAnimateFinishing.copy(isFinishing = true)
                }
            }
        }
    }

    fun animateToOffset() {
//        mutatorMutex.mutate {
//            indicatorOffsetAnimatable.animateTo(value, tween(time)) {
                if (!smartSwipeRefreshAnimateFinishing.isFinishing) {
                    // 此时动画完全停止
                    smartSwipeRefreshAnimateFinishing = smartSwipeRefreshAnimateFinishing.copy(isFinishing = true)
                }
//            }
//        }
    }
}
