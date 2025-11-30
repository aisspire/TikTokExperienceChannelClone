package com.simulate.tiktok.data

import com.simulate.tiktok.model.VideoItem
import kotlinx.coroutines.delay

/**
 * 视频数据仓库
 * 负责提供数据，隐藏数据来源（Mock 或 真实网络）的细节
 */
class VideoRepository {

    // 模拟分页获取数据
    // page: 页码 (0 表示刷新)
    // size: 每页数量
    suspend fun fetchVideos(page: Int, size: Int): List<VideoItem> {
        // 1. 获取配置中的延迟时间
        delay(AppConfig.mockNetworkDelay)

        // 2. 计算 ID 起始偏移量
        val startId = page * size

        // 3. 生成数据
        return List(size) { index ->
            val realId = startId + index
            generateMockItem(realId)
        }
    }

    // 私有方法：生成单个 Mock 对象
    private fun generateMockItem(id: Int): VideoItem {
        val isLongTitle = id % 2 == 0
        // 随机生成宽高比 (0.7 ~ 1.2)
        val ratio = 0.7f + (id % 5) * 0.1f
        // 根据比例计算图片高度 (假设基准宽度 400)
        val imgWidth = 400
        val imgHeight = (imgWidth / ratio).toInt()

        return VideoItem(
            id = id,
            title = if (isLongTitle) "这是[${AppConfig.currentImageSource}]源的模拟标题 $id，测试换行效果" else "短标题 $id",

            // 使用配置好的 Provider 获取 URL
            imageUrl = ImageUrlProvider.getImageUrl(id, imgWidth, imgHeight),
            avatarUrl = ImageUrlProvider.getAvatarUrl(id),

            userName = "用户$id",
            likeCount = "${(10..999).random()}",
            isLiked = id % 3 == 0,
            aspectRatio = ratio
        )
    }
}
