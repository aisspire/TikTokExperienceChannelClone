package com.simulate.tiktok.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import com.simulate.tiktok.viewmodel.ExperienceViewModel
/**
 * 经验频道的主屏幕组件
 *
 * @param viewModel 通过依赖注入或默认参数获取 ExperienceViewModel 实例。
 *                  viewModel() 函数会自动处理 ViewModel 的生命周期，确保配置变更（如旋转）时数据不丢失。
 */
@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun ExperienceScreen(viewModel: ExperienceViewModel = viewModel()) {

    // --- 1. 状态收集 (State Collection) ---
    // 使用 collectAsState 将 ViewModel 中的 Flow 数据流转换为 Compose 可观察的 State。
    // 当 Flow 发射新值时，UI 会自动重组 (Recompose)。
    val isSingleColumn by viewModel.isSingleColumn.collectAsState() // 布局模式：单列 true / 双列 false
    val dataList by viewModel.videoList.collectAsState()            // 视频列表数据
    val isRefreshing by viewModel.isRefreshing.collectAsState()     // 下拉刷新状态
    val isLoadingMore by viewModel.isLoadingMore.collectAsState()   // 上拉加载更多状态
    // 列表的滚动状态对象，用于获取当前滚动位置和可见项信息
    val listState = rememberLazyStaggeredGridState()

    // --- 2. 滚动监听与无限加载逻辑 (Infinite Scrolling Logic) ---
    // 使用 derivedStateOf 是为了性能优化。
    // listState.layoutInfo 在滚动过程中变化极其频繁，直接在 Composition 中使用会导致每像素滚动都触发重组。
    // derivedStateOf 只会在计算结果（true/false）发生改变时，才通知下游更新。
    val shouldLoadMore by remember {
        derivedStateOf {
            val layoutInfo = listState.layoutInfo
            val totalItems = layoutInfo.totalItemsCount
            // 如果列表为空，不触发加载
            if (totalItems == 0) return@derivedStateOf false

            // 获取最后一个可见项的索引
            val lastVisibleItemIndex = layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0

            // 阈值判断：当滚动到倒数第 4 个元素时，触发加载更多
            lastVisibleItemIndex >= totalItems - 4
        }
    }
    // 当 shouldLoadMore 变为 true 时，启动副作用去请求数据
    LaunchedEffect(shouldLoadMore) {
        if (shouldLoadMore) {
            viewModel.loadMore()
        }
    }

    // --- 3. UI 布局结构 ---
    Scaffold(
        containerColor = Color(0xFF161616),
        topBar = {
            // 顶部搜索栏，传入布局状态和切换回调
            TopSearchBar(
                isSingleColumn = isSingleColumn,
                onToggleLayout = { viewModel.toggleLayout() },
                onSwitchSource = { viewModel.switchImageSource() }
            )
        }
    ) { paddingValues ->
        // paddingValues 包含了 Scaffold 顶部栏占据的高度，必须应用到内容区域

        // 下拉刷新容器
        PullToRefreshBox(
            isRefreshing = isRefreshing,// 控制刷新指示器的显示/隐藏
            onRefresh = { viewModel.refresh() },// 触发刷新动作
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)// 应用 Scaffold 的内边距
        ){
            // 核心列表组件：垂直交错网格 (瀑布流)
            LazyVerticalStaggeredGrid(
                state = listState,// 绑定滚动状态
                // 动态列数设置：单列模式用 Fixed(1)，双列模式用 Fixed(2)
                columns = if (isSingleColumn) StaggeredGridCells.Fixed(1) else StaggeredGridCells.Fixed(2),
                modifier = Modifier.fillMaxSize().padding(horizontal = 4.dp),
                verticalItemSpacing = 8.dp,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                // 渲染数据列表
                items(
                    items = dataList,
                    // 显式指定 key 是 Compose 列表优化的关键。
                    // 它可以帮助 Compose 在刷新或重排时识别哪些 Item 是移动了，而不是销毁重建，
                    // 同时也能保持 Item 内部的状态（如滚动位置、输入框内容等）。
                    key = { it.id }
                ) { item ->
                    VideoCard(
                        item = item,
                        // 事件回调：点击爱心 -> ViewModel 处理 -> 数据更新 -> UI 自动刷新
                        onLikeClick = { viewModel.toggleLike(item.id) }
                    )
                }
                // 底部加载指示器 (Footer)
                // 只有在加载更多时才显示
                if (isLoadingMore) {
                    item(
                        // span = FullLine 让这个 Item 无论在单列还是双列模式下，都独占一整行宽度
                        span = StaggeredGridItemSpan.FullLine) {
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

/**
 * 顶部搜索与工具栏组件
 *
 * @param isSingleColumn 当前是否为单列模式，用于决定显示哪个图标
 * @param onToggleLayout 点击切换按钮时的回调
 */
@Composable
fun TopSearchBar(
    isSingleColumn: Boolean,
    onToggleLayout: () -> Unit,
    onSwitchSource: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF161616))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 模拟搜索框
        Box(
            modifier = Modifier
                .weight(1f)
                .height(36.dp)
                .background(Color(0xFF2B2B2B), RoundedCornerShape(4.dp))
                .padding(horizontal = 8.dp)
                // 添加点击事件来切换数据源
                .clickable { onSwitchSource() },
            contentAlignment = Alignment.CenterStart
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                // 提示用户可以点击
                Text("点击此处切换 Mock/数据源", color = Color.Gray, fontSize = 13.sp)
            }
        }

        Spacer(modifier = Modifier.width(12.dp))
        // 布局切换按钮
        IconButton(
            onClick = onToggleLayout,
            modifier = Modifier.size(24.dp)
        ) {
            // 根据状态动态切换图标：
            // 如果当前是单列(Single)，显示 GridView 图标提示可以切回双列
            // 如果当前是双列，显示 ViewStream 图标提示可以切成单列流
            Icon(
                imageVector = if (isSingleColumn) Icons.Default.GridView else Icons.Default.ViewStream,
                contentDescription = "Switch Layout",
                tint = Color.White
            )
        }
    }
}
