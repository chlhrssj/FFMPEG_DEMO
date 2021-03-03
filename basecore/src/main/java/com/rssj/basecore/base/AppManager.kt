package com.rssj.basecore.base

import android.app.Activity
import java.util.*

/**
 * Description:
 * Date：2020/1/6 0006-14:07
 * Author: cwh
 */
class AppManager private constructor() {

    companion object {
        private val mActivityStack = Stack<Activity>()

        private var instance: AppManager? = null

        fun getAppManager(): AppManager {

            return instance ?: synchronized(this) {
                instance ?: AppManager().also {
                    instance = it
                }
            }
        }
    }

    /**
     * 添加到activity 到 Stack
     */
    fun addActivityToStack(activity: Activity) {
        mActivityStack.add(activity)
    }

    /**
     * 移除activity
     */
    fun removeActivityForStack(activity: Activity) {
        mActivityStack.remove(activity)
    }

    /**
     * 栈中是否有activity ,true 存在 false 不存在
     */
    fun isStoreActivity(): Boolean {
        return !mActivityStack.isNullOrEmpty()
    }

    /**
     * 结束指定Activity
     */
    fun finishActivity(activity: Activity) {
        if (!activity.isFinishing) {
            activity.finish()
        }
    }

    /**
     * 结束指定Activity
     */
    fun finishActivity(cls: Class<*>) {
        mActivityStack.forEach {
            if (it.javaClass == cls) {
                finishActivity(it)
            }
        }
    }

    /**
     * 获取当前栈顶Activity
     */
    fun topStackActivity(): Activity? {
        return if (mActivityStack.size <= 0) {
            null
        } else mActivityStack.lastElement()
    }

    /**
     * 结束当前栈顶Activity
     */
    fun finishTopActivity() {
        topStackActivity()?.let {
            finishActivity(it)
        }
    }

    /**
     * 结束所有Activity
     */
    fun finishAllActivity() {
        val iterator = mActivityStack.iterator()
        while (iterator.hasNext()) {
            finishActivity(iterator.next())
            iterator.remove()
        }
    }

    /**
     * 获取指定Activity
     */
    fun getActivity(cls: Class<*>): Activity? {
        mActivityStack.forEach {
            return if (it.javaClass == cls) {
                it
            } else {
                null
            }
        }
        return null
    }
}