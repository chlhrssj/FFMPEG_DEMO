package com.rssj.basecore.base

import com.google.gson.annotations.SerializedName

/**
 * Description:
 * Date：2020/1/2 0002-14:22
 * Author: cwh
 */

open class Entity<T>(val msg:String, @SerializedName("ret")val code:Int, var data:T){

    override fun toString(): String {
        return "Entity(msg='$msg', code=$code, data=$data)"
    }
}