package com.simulate.tiktok.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.simulate.tiktok.model.VideoItem
import com.simulate.tiktok.model.getMockData
import kotlinx.coroutines.delay

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ExperienceViewModel : ViewModel() {

    // _uiState 是我们内部修改的数据流
    private val _videoList = MutableStateFlow<List<VideoItem>>(emptyList())

    // uiState 是暴露给界面观察的（只读），界面一看到它变了，就会自动刷新
    val videoList: StateFlow<List<VideoItem>> = _videoList.asStateFlow()

    // 状态：是否正在下拉刷新
    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing = _isRefreshing.asStateFlow()

    // 状态：是否正在上拉加载更多
    private val _isLoadingMore = MutableStateFlow(false)
    val isLoadingMore = _isLoadingMore.asStateFlow()

    private val _isSingleColumn = MutableStateFlow(false)
    val isSingleColumn = _isSingleColumn.asStateFlow()
    init {
        // 初始化时加载 Mock 数据
        loadData()
    }
    fun toggleLayout() {
        _isSingleColumn.value = !_isSingleColumn.value
    }

    // 初始加载 (复用刷新的逻辑)
    private fun loadData() {
        refresh()
    }

    // 1. 下拉刷新逻辑
    fun refresh() {
        viewModelScope.launch {
            _isRefreshing.value = true
            // 模拟网络延迟 1.5秒
            delay(1500)
            // 重新生成新的 Mock 数据 (模拟获取了最新内容)
            _videoList.value = getMockData()
            _isRefreshing.value = false
        }
    }

    // 2. 上拉加载更多逻辑
    fun loadMore() {
        // 如果已经在加载中，或者正在刷新，就不要重复触发
        if (_isLoadingMore.value || _isRefreshing.value) return

        viewModelScope.launch {
            _isLoadingMore.value = true
            // 模拟网络延迟 1秒
            delay(1000)

            // 生成下一页数据 (模拟 10 条新数据)
            val currentList = _videoList.value
            val startId = currentList.size
            val newItems = List(10) { index ->
                val realIndex = startId + index
                VideoItem(
                    id = realIndex,
                    title = "加载更多的数据 - 编号 $realIndex",
                    imageUrl = "https://picsum.photos/id/${(realIndex + 10) % 100}/400/${(400 + (realIndex % 5) * 50)}",
                    avatarUrl = "https://picsum.photos/id/${(realIndex + 50) % 100}/100/100",
                    userName = "新用户$realIndex",
                    likeCount = "${(100..500).random()}",
                    isLiked = false,
                    aspectRatio = 0.7f + (realIndex % 5) * 0.1f
                )
            }

            // 将新数据追加到旧数据后面
            _videoList.update { it + newItems }
            _isLoadingMore.value = false
        }
    }


    // 处理点赞逻辑
    fun toggleLike(itemId: Int) {
        _videoList.update { currentList ->
            currentList.map { item ->
                if (item.id == itemId) {
                    // 找到被点击的卡片，复制一份并修改状态
                    val newIsLiked = !item.isLiked
                    // 简单的数字处理：如果是数字字符串，尝试转Int加减，防呆处理
                    val currentCount = item.likeCount.toIntOrNull() ?: 0
                    val newCount = if (newIsLiked) currentCount + 1 else currentCount - 1

                    item.copy(
                        isLiked = newIsLiked,
                        likeCount = newCount.toString()
                    )
                } else {
                    item // 其他卡片保持不变
                }
            }
        }
    }
}
