package com.simulate.tiktok.data

import com.simulate.tiktok.model.VideoItem
import kotlin.random.Random

// 1. 定义图片源类型的枚举
enum class ImageSourceType {
    PICSUM,    // 使用 Picsum.photos
    //PLACEHOLD, // 使用 placehold.co (纯色占位图)
    //UNSPLASH   // 模拟 Unsplash
    SEOVX //使用seovx  https://cdn.seovx.com/
}

// 2. 数据配置单例 (Configuration Manager)
object AppConfig {
    // 当前使用的图片源
    var currentImageSource: ImageSourceType = ImageSourceType.PICSUM

    // 模拟网络延迟 (毫秒)
    var mockNetworkDelay: Long = 1000L
    //随机种子
    var randomSeed: Int = 0
}

// 3. 图片生成器工具 (Image Provider Strategy)
object ImageUrlProvider {

    fun getImageUrl(id: Int, width: Int, height: Int): String {
        val uniqueKey = "${id}_${AppConfig.randomSeed}"
        return when (AppConfig.currentImageSource) {
            ImageSourceType.PICSUM ->
                "https://picsum.photos/id/${(id + 10) % 100}/$width/$height"
            ImageSourceType.SEOVX ->
                "https://cdn.seovx.com/ha/?mom=302&sig=${id}"
        }
    }

    fun getAvatarUrl(id: Int): String {
        return when (AppConfig.currentImageSource) {
            ImageSourceType.PICSUM -> "https://picsum.photos/id/${(id + 50) % 100}/100/100"
            else -> "https://robohash.org/${Random.nextInt(1, 1001)}?size=200x200"
        }
    }
}
