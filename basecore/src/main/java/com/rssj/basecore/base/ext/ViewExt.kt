package com.rssj.basecore.base.ext

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import kotlin.math.abs

/**
 * Description:View扩展函数，便于一些方法的调用
 * Date：2019/7/15-12:27
 * Author: cwh
 */

/**
 * findViewById 的简写
 */
fun <T : View> View.find(@IdRes id: Int): T =
    this.findViewById(id)


/**
 * 上次点击事件的时间(不要对该值进行赋值操作)
 *
 *
 * */
var lastClickTime: Long = 0
    private set

/**
 * 上次点击的 view 对象对应的 hashcode (不要对该值进行赋值操作)
 */
var lastClickViewHashCode: Int = 0
    private set


/**
 * View添加防重复点击的clickListener 事件,在指定点击事件间隔内，
 * 重复点击同一个view,点击事件不生效
 * @param time 指定时间间隔内为重复点击，默认500毫秒
 *
 */
fun View.click(time: Long = 500, listener: (View) -> Unit) {
    this.setOnClickListener {
        if (abs(System.currentTimeMillis() - lastClickTime) > time || lastClickViewHashCode != this.hashCode()) {
            listener(this)
            lastClickTime = System.currentTimeMillis()
            lastClickViewHashCode = this.hashCode()
        }
    }
}


fun Context.inflate(@LayoutRes layoutId: Int, root: ViewGroup?): View =
    LayoutInflater.from(this).inflate(layoutId, root, false)



