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

/**
 * 单个卡片组件
 *
 * @param item 卡片展示的数据实体对象
 * @param onLikeClick 当用户点击点赞区域时的回调函数。只通知
 *
 */
@Composable
fun VideoCard(item: VideoItem,
              onLikeClick: () -> Unit) {
    // 外层容器：使用 Material3 的 Card 组件
    Card(
        modifier = Modifier
            .fillMaxWidth()//填满列
            .wrapContentHeight(),//高度自适应
        shape = RoundedCornerShape(8.dp),//圆角
        //深灰色背景，模拟暗色体验
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E))
    ) {
        //垂直布局
        Column {
            //1.封面大图，使用Coil异步加载
            AsyncImage(
                model = item.imageUrl,//图片
                contentDescription = null,//描述
                contentScale = ContentScale.Crop,//裁剪模式：保持比例充满宽度，多余高度裁剪
                modifier = Modifier
                    .fillMaxWidth()
                    // 关键属性：动态设置图片的宽高比。
                    // 这让瀑布流能正确计算每个 Item 的高度，并在图片加载前预占位置，
                    // 防止图片加载出来后布局发生跳动 (Layout Shift)。
                    .aspectRatio(item.aspectRatio)
            )
            //2.底部文字信息
            Column(modifier = Modifier.padding(8.dp)) {
                //标题
                Text(
                    text = item.title,
                    color = Color.White,
                    fontSize = 14.sp,
                    maxLines = 2, // 最多两行
                    overflow = TextOverflow.Ellipsis,//超出显示省略号
                    lineHeight = 18.sp//设置行高
                )
                Spacer(modifier = Modifier.height(8.dp))
                //底部栏，左侧用户信息，右侧点赞信息
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,//两端对齐
                    verticalAlignment = Alignment.CenterVertically//垂直居中
                ) {
                    //左侧 头像+昵称
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
                            maxLines = 1//昵称限制一行
                        )
                    }
                    //右侧 点赞图标+数量
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            // 点击事件绑定在这个 Row 上，而不是仅绑定在 Icon 上。
                            // 这样增加了可点击区域（Touch Target），提升用户体验。
                            .clickable { onLikeClick() }
                            .padding(4.dp)
                    ) {
                        // 根据 isLiked 状态动态切换图标和颜色
                        Icon(
                            // 已点赞显示实心爱心，未点赞显示空心边框
                            imageVector = if (item.isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Like",
                            // 已点赞显示红色，未点赞显示灰色
                            tint = if (item.isLiked) Color(0xFFFF2C55) else Color.Gray,
                            modifier = Modifier.size(16.dp)
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
