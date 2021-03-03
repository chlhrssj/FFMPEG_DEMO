package com.rssj.basecore.utils

import android.app.Application
import android.content.Context
import java.lang.NullPointerException

/**
 * Description:需要现在Application中初始化
 * Date：2020/1/6 0006-14:52
 * Author: cwh
 */

class ConUtils private constructor() {

    companion object {
        private var mContext: Context? = null
        private var mApplication: Application? = null
        fun init(application: Application) {
            mApplication = application
            mContext = application.applicationContext
        }


        fun mContext(): Context {
            if (mContext == null) {
                throw NullPointerException("mContext can not null,please init first")
            }
            return mContext!!
        }

        fun mApplication(): Application {
            if (mApplication == null) {
                throw NullPointerException("mApplication can not null,please init first")
            }
            return mApplication!!
        }

    }

}