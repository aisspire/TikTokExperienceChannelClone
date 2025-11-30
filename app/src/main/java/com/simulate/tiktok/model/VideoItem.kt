package com.simulate.tiktok.model

/**
 * 帖子实体类
 * 定义了单个卡片所需的所有数据
 */
data class VideoItem(
    val id: Int,                    //作为LazyGrid的key
    val title: String,              //内容，根据长度自动换行，超出限制显示省略号
    val imageUrl: String,           //封面大图的url地址
    val avatarUrl: String,          //作者头像的url地址
    val userName: String,           //作者昵称
    val likeCount: String,          //点赞数量
    val isLiked: Boolean = false,   //是否点赞
    val aspectRatio: Float          //长宽比，用于预占位
)

/**
 * 生成模拟数据的方法
 * 图片内容、长宽比各异，点赞数量随机，用户id唯一，约1/3帖子已点赞
 * @return 返回一个包含20个VideoItem对象的列表
 */
fun getMockData(): List<VideoItem> {
    return List(20) { index ->
        VideoItem(
            id = index,
            title = if (index % 2 == 0) "这是抖音经验频道的模拟标题，稍微长一点测试换行效果" else "短标题测试",
            imageUrl = "https://picsum.photos/id/${index + 10}/400/${(400 + index * 50)}",
            avatarUrl = "https://picsum.photos/id/${index + 50}/100/100",
            userName = "用户${index + 9527}",
            likeCount = "${(10..999).random()}",
            isLiked = index % 3 == 0,
            aspectRatio = 0.7f + (index % 5) * 0.1f
        )
    }
}
