package com.rssj.basecore.utils

import android.content.Context
import android.view.Gravity
import android.view.View
import android.widget.Toast
import com.rssj.basecore.base.ExceptionHandle


/**
 * Description:
 * Data：2018/10/25-14:54
 * Author: cwh
 */
object ToastUtils {
    private var mToast: Toast? = null
    private var mLastShowTime: Long = 0
    private var mLastMessage = ""

    /**
     * show Toast
     *
     * @param context
     * @param msg
     */
    fun showToast(context: Context, msg: String) {
        if (mToast == null) {
            mToast = Toast.makeText(context.applicationContext, msg, Toast.LENGTH_SHORT)
            mToast!!.setGravity(Gravity.CENTER, 0, 0)
            mToast!!.show()
            mLastShowTime = System.currentTimeMillis()
            mLastMessage = msg
        } else {
            if (mLastMessage == msg) {
                if (System.currentTimeMillis() - mLastShowTime > Toast.LENGTH_SHORT) {
                    mToast!!.setText(msg)
                    mToast!!.show()
                    mLastShowTime = System.currentTimeMillis()
                } else {
                    mToast!!.setText(msg)
                    mToast!!.show()
                }
            } else {
                mLastMessage = msg
                mToast!!.setText(msg)
                mToast!!.show()
                mLastShowTime = System.currentTimeMillis()
            }
        }
    }

    /**
     * Toasts弹文字和弹View不能混用
     */
    fun initToast(context: Context, v: View, duration: Int) {
        val toast = Toast(context)
        toast.setGravity(Gravity.CENTER, 0, 0)
        toast.duration = duration
        toast.view = v
        toast.show()
    }


    fun showToast(context: Context, id: Int) {
        showToast(context, context.getString(id))
    }

    fun showToast(context: Context, responseThrowable: ExceptionHandle.ResponseThrowable) {
        val code = responseThrowable.code
        if (code == ExceptionHandle.ERROR.UNKONW_HOST_EXCEPTION) {
            showToast(context, "请检查网络是否连接")
        } else if (code == ExceptionHandle.ERROR.NETWORD_ERROR || code == ExceptionHandle.ERROR.SERVER_ADDRESS_ERROR) {
            showToast(context, "无法连接到服务器")
        } else if (code == ExceptionHandle.ERROR.PARSE_ERROR) {
            showToast(context, "解析数据出现错误")
        } else if (code == ExceptionHandle.ERROR.HTTP_ERROR) {
            showToast(context, "未连接到服务器")
        } else {
            showToast(context, "服务器开小差了，请稍候重试  $code")
        }
    }


}
