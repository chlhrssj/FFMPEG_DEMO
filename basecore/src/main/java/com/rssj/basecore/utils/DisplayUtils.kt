package com.rssj.basecore.utils

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Point
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.DisplayMetrics
import android.view.View
import android.view.WindowManager
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat.getSystemService


/**
 * Description:
 * Date：2019/7/15-12:06
 * Author: cwh
 */
object DisplayUtils {

    /**
     * 存储宽高的数组
     */
    private val widthAndHeight = IntArray(2)

    /**
     * 获取屏幕宽高(PX)
     *
     * @param context (Activity)
     * @return 屏幕宽高 单位px
     */
    fun getDeviceWidthAndHeight(context: Activity): IntArray {
        if (widthAndHeight[0] == 0 && widthAndHeight[1] == 0) {
            val metrics = DisplayMetrics()
            context.windowManager.defaultDisplay
                .getMetrics(metrics)

            widthAndHeight[0] = metrics.widthPixels
            widthAndHeight[1] = metrics.heightPixels
        }
        return widthAndHeight
    }

    /**
     * 获取屏幕宽高(PX)
     */
    fun getDeviceWidthAndHeight(): IntArray {
        val wm = ConUtils.mApplication().getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val point = Point()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            wm.defaultDisplay.getRealSize(point)
        } else {
            wm.defaultDisplay.getSize(point)
        }
        return intArrayOf(point.x, point.y)
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    fun dip2px(context: Context, dpValue: Float): Int {
        val scale = context.resources.displayMetrics.density
        return (dpValue * scale + 0.5f).toInt()
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    fun px2dip(context: Context, pxValue: Float): Int {
        val scale = context.resources.displayMetrics.density
        return (pxValue / scale + 0.5f).toInt()
    }

    /**
     * drawable 转Bitmap
     *
     * @param drawable
     * @return
     */
    fun drawableToBitmap(drawable: Drawable): Bitmap {
        var bitmap: Bitmap? = null

        if (drawable is BitmapDrawable) {
            if (drawable.bitmap != null) {
                return drawable.bitmap
            }
        }

        if (drawable.intrinsicWidth <= 0 || drawable.intrinsicHeight <= 0) {
            bitmap =
                Bitmap.createBitmap(
                    1,
                    1,
                    Bitmap.Config.ARGB_8888
                ) // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(
                drawable.intrinsicWidth,
                drawable.intrinsicHeight,
                Bitmap.Config.ARGB_8888
            )
        }

        val canvas = Canvas(bitmap!!)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
    }

    /**
     * 设置状态栏透明(兼容手机适用)
     */
    fun setStatusBarTransparent(activity: Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            try {
                val decorViewClazz = Class.forName("com.android.internal.policy.DecorView")
                val field = decorViewClazz.getDeclaredField("mSemiTransparentStatusBarColor")
                field.isAccessible = true
                field.setInt(activity.window.decorView, Color.TRANSPARENT)  //设置透明
            } catch (e: Exception) {
            }

        }
    }


    /**
     * 设置顶部通知栏透明 需要时在 中调用
     *
     * @param isSet 是否设置为透明状态栏
     */
    fun setSystemUI(activity: Activity, isSet: Boolean) {
        if (!isSet) {
            return
        }
        val decorView = activity.window.decorView
        var systemUI = decorView.systemUiVisibility
        activity.window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        val flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        systemUI = systemUI or flags
        decorView.systemUiVisibility = systemUI
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            activity.window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            activity.window.statusBarColor = Color.TRANSPARENT
        }
    }

    /**
     * 设置状态栏字体颜色，亮色状态栏设置字体黑色（Android 6.0 以上才可以设置）
     */
    fun lightStatusBar(activity: Activity,isLight:Boolean){
        if(Build.VERSION.SDK_INT<Build.VERSION_CODES.M){
            return
        }
        val window = activity.window
        val decorView = window.decorView
        var visibility = decorView.systemUiVisibility
        visibility = if (isLight) {
            visibility or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        } else {
            visibility and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv() //inv 按位取反
        }
        decorView.systemUiVisibility = visibility
    }


    /**
     * 获取状态栏高度方法
     */
    fun getStatusHeight(context: Context): Int {
        var statusBarHeight = -1
        //获取status_bar_height资源的ID
        val resourceId = context.resources.getIdentifier(
            "status_bar_height", "dimen",
            "android"
        )
        if (resourceId > 0) {
            //根据资源ID获取响应的尺寸值
            statusBarHeight = context.resources.getDimensionPixelSize(resourceId)
        } else {
            statusBarHeight = dip2px(context, 20f)
        }
        return statusBarHeight
    }

    /**
     * 获取toolbar高度 ，默认56dp
     *
     * @return
     */
    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    fun getToolBarHeight(mToolbar: Toolbar?, context: Context): Int {
        return if (mToolbar != null) {
            mToolbar!!.minimumHeight
        } else dip2px(context, 56f)
    }


    /**
     * 获取底部导航栏高度
     */
    fun getNavigationHeight(context: Context): Int {
        if (hasNavigationBar(context)) {
            val rs = context.resources
            val id = rs.getIdentifier("navigation_bar_height", "dimen", "android")
            return rs.getDimensionPixelSize(id)
        }
        return 0
    }

    /**
     * 是否显示底部导航栏
     */
    fun hasNavigationBar(context: Context): Boolean {
        var hasNavigation = false
        val rs = context.resources
        val id = rs.getIdentifier("config_showNavigationBar", "bool", "android")
        if (id > 0) {
            hasNavigation = rs.getBoolean(id)
        }
        try {
            val systemPropertiesClass = Class.forName("android.os.SystemProperties")
            val m = systemPropertiesClass.getMethod("get", String::class.java)
            val navBarOverride = m.invoke(systemPropertiesClass, "qemu.hw.mainkeys") as String
            if ("1" == navBarOverride) {
                hasNavigation = false
            } else if ("0" == navBarOverride) {
                hasNavigation = true
            }
        } catch (e: Exception) {
        }

        return hasNavigation
    }


}