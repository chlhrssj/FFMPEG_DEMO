package com.rssj.basecore

import android.app.Activity
import android.app.Application
import android.os.Bundle
import com.rssj.basecore.utils.ConUtils
import com.rssj.basecore.base.AppManager
import com.rssj.basecore.utils.CrashHandlerUtils
import java.lang.NullPointerException

/**
 * Description:
 * Date：2020/1/6 0006-14:35
 * Author: cwh
 */
abstract class BaseApplication : Application() {


    override fun onCreate() {
        super.onCreate()
        initApplication(this)
        ConUtils.init(this)
        CrashHandlerUtils.initInstance().initHandler(applicationContext)
        init()
    }

    /**
     * 初始化参数位置
     */
    abstract fun init()


    companion object {
        private var instance: Application? = null

        val callback = object : ActivityLifecycleCallbacks {
            override fun onActivityPaused(activity: Activity) {
            }

            override fun onActivityStarted(activity: Activity) {
            }

            override fun onActivityDestroyed(activity: Activity) {
                AppManager.getAppManager().removeActivityForStack(activity)
            }

            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
            }

            override fun onActivityStopped(activity: Activity) {
            }

            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
                AppManager.getAppManager().addActivityToStack(activity)
            }

            override fun onActivityResumed(activity: Activity) {
            }

        }


        /**
         * 设置application
         */
        fun initApplication(application: Application) {
            instance = application
            application.registerActivityLifecycleCallbacks(callback)
        }

        fun instance(): Application {
            if (instance == null) {
                throw NullPointerException("Application instance is Null")
            }
            return instance!!
        }
    }

    override fun onTerminate() {
        super.onTerminate()
        unregisterActivityLifecycleCallbacks(callback)
        instance = null
    }
}