package com.rssj.basecore.utils

import android.content.Context


/**
 * Description:
 * Date：2019/7/15-16:46
 * Author: cwh
 */
object ResourceUtils {

    /**
     * Return the id identifier by name.
     *
     * @param name The name of id.
     * @return the id identifier by name
     */
    fun getIdByName(name: String, context: Context): Int {
        return context.resources.getIdentifier(name, "id", context.packageName)
    }

    /**
     * Return the string identifier by name.
     *
     * @param name The name of string.
     * @return the string identifier by name
     */
    fun getStringIdByName(name: String, context: Context): Int {
        return context.resources.getIdentifier(name, "string", context.packageName)
    }

    /**
     * Return the color identifier by name.
     *
     * @param name The name of color.
     * @return the color identifier by name
     */
    fun getColorIdByName(name: String, context: Context): Int {
        return context.resources.getIdentifier(name, "color", context.packageName)
    }

    /**
     * Return the dimen identifier by name.
     *
     * @param name The name of dimen.
     * @return the dimen identifier by name
     */
    fun getDimenIdByName(name: String, context: Context): Int {
        return context.resources.getIdentifier(name, "dimen", context.packageName)
    }

    /**
     * Return the drawable identifier by name.
     *
     * @param name The name of drawable.
     * @return the drawable identifier by name
     */
    fun getDrawableIdByName(name: String, context: Context): Int {
        return context.resources.getIdentifier(name, "drawable", context.packageName)
    }

    /**
     * Return the mipmap identifier by name.
     *
     * @param name The name of mipmap.
     * @return the mipmap identifier by name
     */
    fun getMipmapIdByName(name: String, context: Context): Int {
        return context.resources.getIdentifier(name, "mipmap", context.packageName)
    }

    /**
     * Return the layout identifier by name.
     *
     * @param name The name of layout.
     * @return the layout identifier by name
     */
    fun getLayoutIdByName(name: String, context: Context): Int {
        return context.resources.getIdentifier(name, "layout", context.packageName)
    }

    /**
     * Return the style identifier by name.
     *
     * @param name The name of style.
     * @return the style identifier by name
     */
    fun getStyleIdByName(name: String, context: Context): Int {
        return context.resources.getIdentifier(name, "style", context.packageName)
    }

    /**
     * Return the anim identifier by name.
     *
     * @param name The name of anim.
     * @return the anim identifier by name
     */
    fun getAnimIdByName(name: String, context: Context): Int {
        return context.resources.getIdentifier(name, "anim", context.packageName)
    }

    /**
     * Return the menu identifier by name.
     *
     * @param name The name of menu.
     * @return the menu identifier by name
     */
    fun getMenuIdByName(name: String, context: Context): Int {
        return context.resources.getIdentifier(name, "menu", context.packageName)
    }

}