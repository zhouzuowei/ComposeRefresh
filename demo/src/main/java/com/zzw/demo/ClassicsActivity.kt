package com.zzw.demo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.zzw.compose_refresh.SmartSwipeRefresh
import com.zzw.compose_refresh.config.SwipeUiState
import com.zzw.compose_refresh.rememberSmartSwipeRefreshState
import com.zzw.compose_refresh.ui.footer.MyRefreshFooter
import com.zzw.compose_refresh.ui.header.MyRefreshHeader
import com.zzw.demo.ui.theme.ComposeRefreshTheme
import com.zzw.demo.ui.theme.c_f6f7f8
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn


/**
 * @ClassName: ClassicsActivity 经典样式
 * @Author: zhouzuowei
 * @Date: 2023/3/22 6:21 PM
 * @Description:
 */
class ClassicsActivity : ComponentActivity() {
    val listFlow: MutableSharedFlow<SwipeUiState<String>?> =
        MutableSharedFlow(0, 1, BufferOverflow.DROP_OLDEST)
    private val lists = listFlow.stateIn(lifecycleScope, SharingStarted.Eagerly, null)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposeRefreshTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    getList(listFlow)
                    getData()
                }
            }
        }
    }

    private fun getData() {
        lifecycleScope.launchWhenResumed {
            delay(100)
            val list = arrayListOf("", "", "", "", "", "", "", "", "", "")
            for (index in 0..100) {
                list.add("")
            }
            listFlow.tryEmit(SwipeUiState(list, refreshSuccess = true))
        }
    }

    private fun loadData() {
        lifecycleScope.launchWhenResumed {
            val more = lists.value?.list ?: arrayListOf()
            listFlow.tryEmit(SwipeUiState(more, isLoading = true))
            delay(1000)
            val list = arrayListOf("", "", "", "", "", "", "", "", "", "")
            for (index in 0..100) {
                list.add("")
            }
            list.addAll(0, more)
            listFlow.tryEmit(SwipeUiState(list, loadMoreSuccess = true))
        }
    }

    @Composable
    fun getList(listFlow: Flow<SwipeUiState<String>?>) {
        val scrollState = rememberLazyListState()
        val state = rememberSmartSwipeRefreshState()
        val swipeUiState = listFlow.collectAsState(SwipeUiState(isLoading = true))
        SmartSwipeRefresh(
            state = state,
            scrollState = scrollState,
            swipeUiState = swipeUiState.value,
            isAutoFill = false,
            isAutoLoad = false,
            onRefresh = {
                getData()
            },
            onLoadMore = {
                loadData()
            }, headerIndicator = { MyRefreshHeader(flag = state.refreshFlag, isNeedTimestamp = true) },
            footerIndicator = { MyRefreshFooter(flag = state.loadMoreFlag, isNeedTimestamp = true)}) {
            swipeUiState.value?.list?.let { list ->
                LazyColumn(
                    state = scrollState,
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    itemsIndexed(list) { index, it ->
                        Text(text = "   经典样式$index",
                            Modifier
                                .fillMaxWidth()
                                .padding(0.dp, 10.dp))
                        if (index < list.size - 1) {
                            Divider(color = c_f6f7f8)
                        }
                    }
                }
            }
        }
    }

    @Preview(showBackground = true)
    @Composable
    fun DefaultPreview() {
        ComposeRefreshTheme {
            getList(listFlow)
        }
    }
}