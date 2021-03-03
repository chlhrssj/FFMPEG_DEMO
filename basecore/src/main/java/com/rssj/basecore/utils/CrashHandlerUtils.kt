package com.rssj.basecore.utils

import android.content.Context
import android.os.Build
import java.io.*

/**
 * Description:全局异常捕获，将异常信息缓存到文件
 * Date：2019/7/15-18:12
 * Author: cwh
 */
class CrashHandlerUtils private constructor() : Thread.UncaughtExceptionHandler {

    private var mDefaultExceptionHandler: Thread.UncaughtExceptionHandler? = null
    private var mContext: Context? = null


    /**
     * 在Application 中调用，实现全局捕获异常进行缓存到文件
     *
     * 缓存在Android/data/packageName/cache/crash
     */
    fun initHandler(context: Context) {
        mContext = context
        mDefaultExceptionHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler(instance)
    }


    override fun uncaughtException(thread: Thread, throwable: Throwable) {
        try {
            //收集处理错误信息
            saveExceptionToLocal(throwable)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        //交给系统处理异常
        mDefaultExceptionHandler?.uncaughtException(thread, throwable)

    }


    private fun saveExceptionToLocal(throwable: Throwable) {
        val sb = StringBuffer()
        val head = "************* Log Head ****************" +
                "\nTime Of Crash      : " + TimeCovertUtils.currentDate +
                "\nDevice Manufacturer: " + Build.MANUFACTURER +
                "\nDevice Model       : " + Build.MODEL +
                "\nAndroid Version    : " + Build.VERSION.RELEASE +
                "\nAndroid SDK        : " + Build.VERSION.SDK_INT +
                "\nApp VersionName    : " + Utils.getAppVersionName(mContext!!) +
                "\nApp VersionCode    : " + Utils.getAppVersionCode(mContext!!) +
                "\n************* Log Head ****************\n\n"

        sb.append(head)
        val writer = StringWriter()
        val printWriter = PrintWriter(writer)
        throwable.printStackTrace(printWriter)
        var cause = throwable.cause
//        while (cause != null) {
//            cause.printStackTrace(printWriter)
//            cause = throwable.cause
//            Log.d("Crash",cause?.toString())
//        }
        cause?.printStackTrace(printWriter)
        printWriter.close()
        val result = writer.toString()
        val body = "\n************* Info ****************\n"
        sb.append(body)
        sb.append(result)
        sb.append(body)
        saveInfoToFile(sb)
    }

    /**
     * 保存info到文件
     */
    private fun saveInfoToFile(sb: StringBuffer) {
        val cache = mContext!!.externalCacheDir?.absolutePath
        val crashFileDir = "$cache/Crash"
        val file = File(crashFileDir)
        if (!file.exists()) {
            file.mkdirs()
        }
        val crashInfoFile = File(
            file, "crash_info_" +
                    "${TimeCovertUtils.convertToDetailDate(System.currentTimeMillis())}_file.txt"
        )
        try {
            FileOutputStream(crashInfoFile).use {
                it.write(sb.toString().toByteArray())
            }
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
    }


    companion object {
        @Volatile
        private var instance: CrashHandlerUtils? = null

        fun initInstance(): CrashHandlerUtils {
            return instance ?: synchronized(this) {
                instance
                    ?: CrashHandlerUtils().also {
                        instance = it
                    }
            }
        }

    }


}