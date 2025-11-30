package com.simulate.tiktok.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ViewStream
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simulate.tiktok.ui.components.VideoCard
import androidx.lifecycle.viewmodel.compose.viewModel // 新增
import androidx.compose.runtime.collectAsState      // 新增
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue            // 新增
import androidx.compose.runtime.remember
import com.simulate.tiktok.viewmodel.ExperienceViewModel

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun ExperienceScreen(viewModel: ExperienceViewModel = viewModel()) {
    val isSingleColumn by viewModel.isSingleColumn.collectAsState()
    // 获取 Mock 数据
    val dataList by viewModel.videoList.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()
    val isLoadingMore by viewModel.isLoadingMore.collectAsState()
// 1. 获取列表的滚动状态
    val listState = rememberLazyStaggeredGridState()

    // 2. 监听滚动位置实现“上拉加载”
    // derivedStateOf 确保只在计算结果变化时触发重组，优化性能
    val shouldLoadMore by remember {
        derivedStateOf {
            val layoutInfo = listState.layoutInfo
            val totalItems = layoutInfo.totalItemsCount
            if (totalItems == 0) return@derivedStateOf false

            // 获取最后一个可见 item 的索引
            val lastVisibleItemIndex = layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0

            // 如果最后一个可见项 >= 总数 - 4 (倒数第4个)，且不在加载中，则触发
            lastVisibleItemIndex >= totalItems - 4
        }
    }

    // 当 shouldLoadMore 变为 true 时，执行 loadMore
    LaunchedEffect(shouldLoadMore) {
        if (shouldLoadMore) {
            viewModel.loadMore()
        }
    }
    Scaffold(
        containerColor = Color(0xFF161616), // 全局深色背景 (仿抖音)
        topBar = {
            TopSearchBar(
                isSingleColumn = isSingleColumn,
                onToggleLayout = { viewModel.toggleLayout() }
            )
        }
    ) { paddingValues ->

        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = { viewModel.refresh() },
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ){
            LazyVerticalStaggeredGrid(
                state = listState, // 绑定滚动状态
                columns = if (isSingleColumn) StaggeredGridCells.Fixed(1) else StaggeredGridCells.Fixed(2),
                modifier = Modifier.fillMaxSize().padding(horizontal = 4.dp),
                verticalItemSpacing = 8.dp,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                // 正常的视频列表
                items(
                    items = dataList,
                    key = { it.id }
                ) { item ->
                    VideoCard(
                        item = item,
                        onLikeClick = { viewModel.toggleLike(item.id) }
                    )
                }

                // 4. 底部加载指示器 (Loading Footer)
                // 如果正在加载更多，显示一个转圈圈
                if (isLoadingMore) {
                    item(span = StaggeredGridItemSpan.FullLine) { // 占满一行
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                color = Color.White,
                                modifier = Modifier.size(24.dp),
                                strokeWidth = 2.dp
                            )
                        }
                    }
                }
            }
        }


    }
}

// 模拟顶部的搜索栏 UI
@Composable
fun TopSearchBar(
    isSingleColumn: Boolean,     // 新增参数
    onToggleLayout: () -> Unit   // 新增参数
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF161616))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // ... 搜索框 Box 代码保持不变 (如果你想省事，可以直接复制之前的) ...
        Box(
            modifier = Modifier
                .weight(1f)
                .height(36.dp)
                .background(Color(0xFF2B2B2B), RoundedCornerShape(4.dp))
                .padding(horizontal = 8.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("深圳野生动物园攻略", color = Color.Gray, fontSize = 13.sp)
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        // --- 修改：把原来的“搜索”文字改成 图标按钮 ---
        IconButton(
            onClick = onToggleLayout,
            modifier = Modifier.size(24.dp)
        ) {
            Icon(
                // 根据状态显示不同图标：如果是单列，显示“变网格”的图标；反之亦然
                imageVector = if (isSingleColumn) Icons.Default.GridView else Icons.Default.ViewStream,
                contentDescription = "Switch Layout",
                tint = Color.White
            )
        }
    }
}
