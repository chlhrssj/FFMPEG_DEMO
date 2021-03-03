package com.rssj.basecore.base.widget.itemdecoration

import android.graphics.drawable.Drawable
import androidx.annotation.ColorInt


/**
 * Description:
 * Date：2019/7/28-16:27
 * Author: cwh
 *
 * @param visibility 分割线是否可见
 *
 * @param color 分割线颜色
 *
 * @param drawable 分割线drawable,当同时设置drawable和color时，
 * 优先用drawable
 *
 * @param lineWidth 分割线宽度 这里对于竖直分割线为宽度，
 * 水平分割线为高度值 单位dp
 *
 * @param startPadding 竖直分割线为上部的padding ，水平分割线为左边的padding 单位dp
 *
 * @param endPadding  竖直分割线为底部的padding ,水平分割线为右边的padding 单位dp
 */
class SideLine(
    val visibility: Boolean,
    @ColorInt val color: Int,
    val drawable: Drawable?=null,
    val lineWidth: Float,
    val startPadding: Float,
    val endPadding: Float
)