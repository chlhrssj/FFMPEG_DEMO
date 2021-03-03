package com.rssj.basecore.base

/**
 * Description:带加载状态的结果
 *
 * 在正在加载时 可以返回loading 状态给livedata,通过观察livedata的status,
 * 显示不同UI
 *
 * 注意---------------------------------------------------------------注意
 *
 *  Result的状态只有这几种，没有complete状态
 *
 *  现在这几种状态是互斥的，不会存储覆盖的情况
 *
 *  但是Complete会存在包含（Success 和 Fail 和 Error的情况，造成问题）
 *
 *  例如问题：由于当界面由onResume 到 onStop时，界面处于后台，这是后操作发送给liveData
 *  的数据只会保存最后一次传给它的数据，在界面再到达onResume时，再发送出去。所以如果有Complete
 *  就会有问题
 *
 *  例如：进入一个界面，开始请求数据，在正在请求过程中使界面变为onStop，
 *  这时当在后台完成请求后，应该LiveData发送数据顺序为onSuccess 再onComplete
 *  当界面再次到OnResume时，由于LiveData只发送最后一次数据，这里会只发送
 *  onComplete，导致实际应该在onSuccess中执行的UI显示操作无法执行，因为现在
 *  不会收到onSuccess的消息，从而导致数据丢失，界面显示问题.这就是由于onComplete
 *  包含了onSuccess而造成的问题，所以几个状态必须不能有包含关系
 *  (当所有逻辑在前台执行时，看不出来问题)
 *
 *
 * 注意----------------------------------------------------------------注意
 *
 * Date：2020/3/4-18:51
 * Author: cwh
 */

data class Result<out T>(val status: Status, val data: T?, val message: String?,val code:Int?) {
    companion object {
        fun <T> success(data: T?): Result<T> {
            return Result(Status.SUCCESS, data, null,null)
        }

        fun <T> error(msg: String?=null,data:T?=null): Result<T> {
            return Result(Status.ERROR, data, msg,null)
        }

        fun <T> loading(data: T?=null): Result<T> {
            return Result(Status.LOADING, data, null,null)
        }

        fun <T> fail(msg:String,code:Int):Result<T>{
            return Result(Status.FAIL,null,msg,code)
        }
    }
}

enum class Status{

    LOADING,

    SUCCESS,

    FAIL,

    ERROR
}