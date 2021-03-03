package com.rssj.basecore.base

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Observer
import java.util.*

/**
 * Description:
 * Date：2020/1/2 0002-14:25
 * Author: cwh
 */


class EventObserver<T>( private var onEventUnHandleContent:(T)->Unit):Observer<Event<T>>{
    override fun onChanged(t: Event<T>?) {
        t?.getContentIfNotHandled()?.let {
            onEventUnHandleContent(it)
        }
    }
}


inline fun <T> LiveData<Event<T>>.observerEvent(owner: LifecycleOwner,
                                         crossinline onEventUnHandleContent:(T)->Unit ){
    observe(owner, Observer {event->
        event.getContentIfNotHandled()?.let {
           onEventUnHandleContent(it)
       }
    })
}

/**
 * 去重，主要用于在room数据库中，当其他表发生改变，
 * 导致当前重新返回了一个新的liveData，但实际数据未变
 * 这种情况需要比较value是否发生改变，若发生改变，则通知
 * 更新
 */
fun <T> LiveData<T>.getDistinct():LiveData<T>{
    val mediatorLiveData=MediatorLiveData<T>()
    mediatorLiveData.addSource(this,object :Observer<T>{
        private var initialized=false
        private var lastObj:T?=null
        override fun onChanged(t: T) {
            if(!initialized){
                initialized=true
                lastObj=t
                mediatorLiveData.postValue(t)
            }else if((t==null && lastObj!=null) ||
                    t!=lastObj){
                lastObj=t
                mediatorLiveData.postValue(t)
            }
        }

    })
    return mediatorLiveData
}