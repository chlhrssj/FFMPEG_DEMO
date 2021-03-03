package com.rssj.basecore.base


/**
 * Used as a wrapper for data that is exposed via a LiveData that represents an event.
 *
 * 主要是用来包装LiveData的data,避免有时当观察数据变化时，由于如屏幕旋转等事件，导致事件有重新
 * 发送，从而导致重新的观察到数据的变化，进行一些UI的处理（在这种情况下应该不需要重新发送事件，触发
 * onchange执行的）
 */
open class Event<out T>(private val content: T) {

    var hasBeenHandled = false
        private set
    /**
     * Returns the showContentView and prevents its use again.
     */
    fun getContentIfNotHandled(): T? {
        return if (hasBeenHandled) {
            null
        } else {
            hasBeenHandled = true
            content
        }
    }

    /**
     * Returns the showContentView, even if it's already been handled.
     */
    fun peekContent(): T = content
}