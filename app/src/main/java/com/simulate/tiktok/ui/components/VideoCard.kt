package com.simulate.tiktok.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.simulate.tiktok.model.VideoItem
import androidx.compose.foundation.clickable
@Composable
fun VideoCard(item: VideoItem,
              onLikeClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(), // 高度自适应
        shape = RoundedCornerShape(8.dp), // 圆角
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E)) // 深色背景
    ) {
        Column {
            // 1. 封面图区域
            AsyncImage(
                model = item.imageUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    // 核心：根据数据动态设置图片高度比例，实现瀑布流错落感
                    .aspectRatio(item.aspectRatio)
            )

            // 2. 文字信息区域
            Column(modifier = Modifier.padding(8.dp)) {
                // 标题
                Text(
                    text = item.title,
                    color = Color.White,
                    fontSize = 14.sp,
                    maxLines = 2, // 最多两行
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                // 底部栏：用户信息 + 点赞
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // 左侧：头像 + 用户名
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        AsyncImage(
                            model = item.avatarUrl,
                            contentDescription = null,
                            modifier = Modifier
                                .size(16.dp)
                                .clip(RoundedCornerShape(50)), // 圆形头像
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = item.userName,
                            color = Color.Gray,
                            fontSize = 11.sp,
                            maxLines = 1
                        )
                    }

                    // 右侧：点赞图标 + 数量
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clickable { onLikeClick() } // 点击触发回调
                            .padding(4.dp) // 增加一点点击区域，提升体验
                    ) {
                        Icon(
                            imageVector = if (item.isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Like",
                            // 动态颜色：点赞变红，未点赞灰色
                            tint = if (item.isLiked) Color(0xFFFF2C55) else Color.Gray,
                            modifier = Modifier.size(16.dp) // 图标稍微调大一点点
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = item.likeCount,
                            color = Color.Gray,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
    }
}
