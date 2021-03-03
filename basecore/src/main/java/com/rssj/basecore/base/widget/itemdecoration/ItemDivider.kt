package com.rssj.basecore.base.widget.itemdecoration

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.view.View
import androidx.recyclerview.widget.RecyclerView

/**
 * Description:
 * Date：2019/7/28-17:14
 * Author: cwh
 *
 * Recycler item间的分割线，可用drawable作为分割线，对于分割线，
 * 这里是把其分割为上、下、左、右来进行画各个位置的分割线，对于
 * 分割线的宽高这里需要注意下，中间item的总的分割线的高度，是以上部item
 * 的底部分割线，下个item的顶部分割线，一起作为一个总的分割线的，这里需要
 * 注意计算下，分割线的宽高。
 *
 * 另外，可以根据不同item的position,来画不同的分割线
 *
 */
abstract class ItemDivider(val context: Context) : RecyclerView.ItemDecoration() {

    private val mPaint = Paint()

    init {
        mPaint.isAntiAlias=true
        mPaint.style = Paint.Style.FILL_AND_STROKE
        mPaint.color = Color.WHITE
    }


    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {

        val position = parent.getChildLayoutPosition(view)

        val divider = getDivider(position)
        val left: Float = if (divider.leftSideLine.visibility)
            dip2px(divider.leftSideLine.lineWidth) else 0f
        val top = if (divider.topSideLine.visibility)
            dip2px(divider.topSideLine.lineWidth) else 0f
        val right = if (divider.rightSideLine.visibility)
            dip2px(divider.rightSideLine.lineWidth) else 0f
        val bottom = if (divider.bottomSideLine.visibility)
            dip2px(divider.bottomSideLine.lineWidth) else 0f
        //设置间隔
        outRect.set(left.toInt(), top.toInt(), right.toInt(), bottom.toInt())
    }

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        val childCount = parent.childCount
        for (i in 0 until childCount) {
            val childView = parent.getChildAt(i)
            val position = parent.getChildLayoutPosition(childView)
            val divider = getDivider(position)
            if (divider.leftSideLine.visibility) {
                val leftLineWidth = dip2px(divider.leftSideLine.lineWidth)
                val leftStartPadding = dip2px(divider.leftSideLine.startPadding)
                val leftEndPadding = dip2px(divider.leftSideLine.endPadding)
                drawLeftVertical(
                    c, childView, parent, divider.leftSideLine.color,
                    leftLineWidth, leftStartPadding, leftEndPadding, divider.leftSideLine.drawable
                )
            }

            if (divider.topSideLine.visibility) {
                val topLineWidth = dip2px(divider.topSideLine.lineWidth)
                val topStartPadding = dip2px(divider.topSideLine.startPadding)
                val topEndPadding = dip2px(divider.topSideLine.endPadding)
                drawTopHorizontal(
                    c, childView, parent, divider.topSideLine.color,
                    topLineWidth, topStartPadding, topEndPadding, divider.topSideLine.drawable
                )
            }

            if (divider.rightSideLine.visibility) {
                val rightLineWidth = dip2px(divider.rightSideLine.lineWidth)
                val rightStartPading = dip2px(divider.rightSideLine.startPadding)
                val rightEndPadding = dip2px(divider.rightSideLine.endPadding)
                drawRightVertical(
                    c, childView, parent, divider.rightSideLine.color,
                    rightLineWidth, rightStartPading, rightEndPadding,
                    divider.rightSideLine.drawable
                )
            }

            if (divider.bottomSideLine.visibility) {
                val bottomLineWidth = dip2px(divider.bottomSideLine.lineWidth)
                val bottomStartPadding = dip2px(divider.bottomSideLine.startPadding)
                val bottomEndPadding = dip2px(divider.bottomSideLine.endPadding)
                drawBottomHorizontal(
                    c, childView, parent, divider.bottomSideLine.color,
                    bottomLineWidth, bottomStartPadding, bottomEndPadding
                    , divider.bottomSideLine.drawable
                )
            }
        }

    }

    private fun drawBottomHorizontal(
        c: Canvas,
        childView: View,
        parent: RecyclerView,
        color: Int,
        lineWidth: Float,
        startPadding: Float,
        endPadding: Float,
        drawable: Drawable?
    ) {
        val params = childView.layoutParams as RecyclerView.LayoutParams
        val left = childView.left - params.leftMargin + startPadding
        val right = childView.right + params.rightMargin - endPadding
        val top = childView.bottom + params.bottomMargin
        val bottom = top + lineWidth
        if (drawable != null) {
            drawable.setBounds(left.toInt(), top, right.toInt(), bottom.toInt())
            drawable.draw(c)
        } else {
            mPaint.color = color
            c.drawRect(left.toFloat(), top.toFloat(), right.toFloat(), bottom.toFloat(), mPaint)
        }

    }

    private fun drawRightVertical(
        c: Canvas,
        childView: View,
        parent: RecyclerView,
        color: Int,
        lineWidth: Float,
        startPadding: Float,
        endPadding: Float,
        drawable: Drawable?
    ) {

        val params = childView.layoutParams as RecyclerView.LayoutParams
        val top = childView.top - params.topMargin + startPadding
        val left = childView.right + params.rightMargin
        val right = left + lineWidth
        val bottom = childView.bottom + params.bottomMargin - endPadding
        if (drawable != null) {
            drawable.setBounds(left, top.toInt(), right.toInt(), bottom.toInt())
            drawable.draw(c)
        } else {
            mPaint.color = color
            c.drawRect(left.toFloat(), top.toFloat(), right.toFloat(), bottom.toFloat(), mPaint)
        }

    }

    private fun drawTopHorizontal(
        c: Canvas,
        childView: View,
        parent: RecyclerView,
        color: Int,
        lineWidth: Float,
        startPadding: Float,
        endPadding: Float,
        drawable: Drawable?
    ) {
        val params = childView.layoutParams as RecyclerView.LayoutParams
        val top = childView.top - lineWidth - params.topMargin
        val bottom = top + lineWidth
        val left = childView.left - params.leftMargin + startPadding
        val right = childView.right + params.rightMargin - endPadding
        if (drawable != null) {
            drawable.setBounds(left.toInt(), top.toInt(), right.toInt(), bottom.toInt())
            drawable.draw(c)
        } else {
            mPaint.color = color
            c.drawRect(left.toFloat(), top.toFloat(), right.toFloat(), bottom.toFloat(), mPaint)
        }

    }

    //画左边分割线
    private fun drawLeftVertical(
        c: Canvas,
        childView: View,
        parent: RecyclerView,
        color: Int,
        lineWidth: Float,
        startPadding: Float,
        endPadding: Float,
        drawable: Drawable?
    ) {
        val params = childView.layoutParams as RecyclerView.LayoutParams
        val top = childView.top - params.topMargin + startPadding
        val left = childView.left - lineWidth - params.leftMargin
        val right = childView.left - params.leftMargin
        val bottom = childView.bottom + params.bottomMargin - endPadding
        if (drawable != null) {
            drawable.setBounds(left.toInt(), top.toInt(), right, bottom.toInt())
            drawable.draw(c)
        } else {
            mPaint.color = color
            c.drawRect(left.toFloat(), top.toFloat(), right.toFloat(), bottom.toFloat(), mPaint)
        }
    }

    /**
     *
     * @param position item对应的position
     *
     * 根据位置，返回Divider
     */
    abstract fun getDivider(position: Int): Divider


    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    private fun dip2px(dpValue: Float): Float {
        val scale = context.resources.displayMetrics.density
        return dpValue * scale + 0.5f
    }

}