package com.simulate.tiktok.model

data class VideoItem(
    val id: Int,
    val title: String,
    val imageUrl: String,     // 封面图 URL
    val avatarUrl: String,    // 头像 URL
    val userName: String,
    val likeCount: String,
    val isLiked: Boolean = false,
    val aspectRatio: Float    // 图片宽高比 (宽/高)，例如 0.75f 代表 3:4
)

// 生成一些静态 Mock 数据用于测试
fun getMockData(): List<VideoItem> {
    return List(20) { index ->
        VideoItem(
            id = index,
            title = if (index % 2 == 0) "这是抖音经验频道的模拟标题，稍微长一点测试换行效果" else "短标题测试",
            // 使用 Picsum 获取随机图片，指定 id 保证每次加载一样
            imageUrl = "https://picsum.photos/id/${index + 10}/400/${(400 + index * 50)}",
            avatarUrl = "https://picsum.photos/id/${index + 50}/100/100",
            userName = "用户${index + 9527}",
            likeCount = "${(10..999).random()}",
            isLiked = index % 3 == 0,
            // 随机生成 0.7 到 1.3 之间的宽高比，模拟不同图片高度
            aspectRatio = 0.7f + (index % 5) * 0.1f
        )
    }
}
