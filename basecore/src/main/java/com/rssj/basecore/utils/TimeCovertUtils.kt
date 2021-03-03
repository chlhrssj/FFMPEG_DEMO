package com.rssj.basecore.utils

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

/**
 * Description:
 * Date：2019/7/15-13:52
 * Author: cwh
 */
object TimeCovertUtils {

    /**
     * 获取当前时间戳，转换为yyyy-MM-dd HH:mm
     *
     * @return
     */
    val currentDate: String
        get() {
            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA)
            return sdf.format(Date())
        }

    /**
     * 时间戳转化位date
     *
     * @param millisTime 毫秒
     * @return yyyy-MM-dd HH:mm
     */
    fun convertToDate(millisTime: Long): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA)
        return sdf.format(Date(millisTime))
    }


    /**
     * 获取下一天
     * @param millisTime
     * @return yyyy-MM-dd
     */
    fun convertToNextDate(millisTime: Long): String {
        var millisTime = millisTime
        millisTime += 24 * 60 * 60 * 1000
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.CHINA)
        return sdf.format(Date(millisTime))
    }


    /**
     *
     * @param millisTime
     * @return  yyyy-MM-dd HH:mm:ss
     */
    fun convertToDetailDate(millisTime: Long): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA)
        return sdf.format(Date(millisTime))
    }


    /**
     * @param millisTime 毫秒
     * @return 2019-03-05
     */
    fun convertToDateFront(millisTime: Long): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.CHINA)
        return sdf.format(Date(millisTime))
    }

    /**
     * 转化获取 小时和分
     *
     * @param millisTime
     * @return
     */
    fun covertToHourAndMin(millisTime: Long): String {
        val date = convertToDate(millisTime)
        val index = date.indexOf(" ")
        return if (index != -1) {
            date.substring(index + 1)
        } else {
            date
        }
    }

    /**
     * 将date转化为time
     *
     * @param time yyyy-MM-dd HH:mm
     * @return millions 毫秒
     */
    fun convertToMills(time: String): Long {
        var millisTime: Long = 0
        val format = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA)
        var date: Date? = null
        try {
            date = format.parse(time)
            millisTime = date!!.time
        } catch (e: ParseException) {
            e.printStackTrace()
        } finally {
            return millisTime
        }
    }

    /**
     * 获取当前月份
     */
    fun covertMonth():Int{
        val calendar=Calendar.getInstance(Locale.CHINA)
        return calendar.get(Calendar.MONTH)+1
    }

    /**
     * 年
     */
    fun covertYear():Int{
        val calendar=Calendar.getInstance(Locale.CHINA)
        return calendar.get(Calendar.YEAR)
    }

    /**
     * 日
     */
    fun covertDay():Int{
        val calendar=Calendar.getInstance(Locale.CHINA)
        return calendar.get(Calendar.DAY_OF_MONTH)
    }

    /**
     * week
     */
    fun convertWeek():Int{
        val calendar=Calendar.getInstance(Locale.CHINA)
        return calendar.get(Calendar.DAY_OF_WEEK)
    }
}