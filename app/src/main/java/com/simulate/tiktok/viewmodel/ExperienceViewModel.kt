package com.simulate.tiktok.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.simulate.tiktok.data.AppConfig
import com.simulate.tiktok.data.ImageSourceType
import com.simulate.tiktok.data.VideoRepository
import com.simulate.tiktok.model.VideoItem
import com.simulate.tiktok.model.getMockData
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
/**
 * 经验频道的 ViewModel
 * 负责管理界面状态（UI State）和业务逻辑。
 *
 * 继承自 ViewModel，因此它的生命周期比 Activity/Fragment 长，
 * 能够处理配置变更（如屏幕旋转）导致的数据保持问题。
 */
class ExperienceViewModel : ViewModel() {
    // 引入 Repository
    private val repository = VideoRepository()

    // --- UI 状态定义 (StateFlow) ---
    // 使用 StateFlow 是 Compose 中管理状态的推荐方式。
    // MutableStateFlow 用于内部修改，asStateFlow() 暴露给 UI 层只读访问，保证数据单向流动安全性。

    // 1. 视频列表数据流
    private val _videoList = MutableStateFlow<List<VideoItem>>(emptyList())
    val videoList: StateFlow<List<VideoItem>> = _videoList.asStateFlow()

    // 2. 下拉刷新状态流 (true 表示正在刷新)
    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing = _isRefreshing.asStateFlow()

    // 3. 上拉加载更多状态流 (true 表示正在加载下一页)
    private val _isLoadingMore = MutableStateFlow(false)
    val isLoadingMore = _isLoadingMore.asStateFlow()

    // 4. 布局模式状态流 (false: 双列瀑布流, true: 单列大图流)
    private val _isSingleColumn = MutableStateFlow(false)
    val isSingleColumn = _isSingleColumn.asStateFlow()
    // 当前页码记录
    private var currentPage = 0
    private val pageSize = 20

    // 初始化块：ViewModel 创建时立即加载第一页数据
    init {
        //println("DEBUG: ViewModel init called")
        refresh()
    }



    // --- 业务逻辑方法 ---

    /**
     * 切换列表布局模式 (单列 <-> 双列)
     * 这是一个简单的状态取反操作。
     */
    fun toggleLayout() {
        _isSingleColumn.value = !_isSingleColumn.value
    }



    /**
     * 循环切换图片源
     * PICSUM -> PLACEHOLD -> KITTEN -> ROBOT -> PICSUM ...
     */
    fun switchImageSource() {
        // 1. 获取所有可用的源
        val sources = ImageSourceType.values()

        // 2. 获取当前源的索引
        val currentIndex = sources.indexOf(AppConfig.currentImageSource)

        // 3. 计算下一个索引 (取模运算实现循环)
        val nextIndex = (currentIndex + 1) % sources.size

        // 4. 更新配置
        AppConfig.currentImageSource = sources[nextIndex]

        // 5. 立即刷新列表以显示新图片
        refresh()
    }

    /**
     * 下拉刷新逻辑
     * 清空当前列表或重新获取第一页数据。
     */
    fun refresh() {
        //if (_isRefreshing.value) return
        // 使用 viewModelScope 启动协程。
        // 当 ViewModel 被销毁时，这个协程会自动取消，防止内存泄漏。
        viewModelScope.launch {
            _isRefreshing.value = true// 标记开始刷新，UI 会显示 Loading 指示器
            currentPage = 0 // 重置页码
            // 调用仓库获取数据
            val newData = repository.fetchVideos(currentPage, pageSize)
            _videoList.value = newData
            _isRefreshing.value = false //标记刷新结束，隐藏 Loading 指示器
        }
    }

    /**
     * 上拉加载更多逻辑 (无限滚动)
     */
    fun loadMore() {
        // 防抖/节流检查：
        // 如果正在加载更多，或者正在下拉刷新中，则忽略这次请求，防止重复请求和数据错乱。
        if (_isLoadingMore.value || _isRefreshing.value) return

        viewModelScope.launch {
            _isLoadingMore.value = true// 标记开始加载更多，UI 底部显示 Loading 条

            currentPage++ // 页码 +1

            // 调用仓库获取下一页
            val newItems = repository.fetchVideos(currentPage, pageSize)
            // 使用 update 线程安全地更新列表：旧列表 + 新列表
            _videoList.update { it + newItems }
            _isLoadingMore.value = false// 标记加载结束
        }
    }


    /**
     * 点赞/取消点赞逻辑
     * @param itemId 被点击的视频 ID
     */
    fun toggleLike(itemId: Int) {
        _videoList.update { list ->
            list.map {
                if (it.id == itemId) {
                    val newLike = !it.isLiked
                    val count = it.likeCount.toIntOrNull() ?: 0
                    it.copy(isLiked = newLike, likeCount = (if(newLike) count + 1 else count - 1).toString())
                } else it
            }
        }
    }
}
