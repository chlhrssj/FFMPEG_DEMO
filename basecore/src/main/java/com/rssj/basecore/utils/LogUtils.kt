package com.rssj.basecore.utils

import android.util.Log

import com.rssj.basecore.*


object LogUtils {

    val TAG = "LOG"

    //以后发正式版要要为下面的标志，除去log
    //BuildConfig.DEBUG


    fun d(tag: String, message: String) {
        if (isDebug)
            Log.d(tag, message)
    }

    fun v(tag: String, message: String) {
        if (isDebug)
            Log.v(tag, message)
    }

    fun i(tag: String, message: String) {
        if (isDebug)
            Log.i(tag, message)
    }

    fun e(tag: String, message: String) {
        Log.e(tag, message)
    }

    fun w(tag: String, message: String) {
        if (isDebug)
            Log.w(tag, message)
    }


    fun d(message: String) {
        d(TAG, message)
    }

    fun v(message: String) {
        v(TAG, message)
    }

    fun i(message: String) {
        i(TAG, message)
    }

    fun e(message: String) {
        e(TAG, message)
    }

    fun w(message: String) {
        w(TAG, message)
    }


}
