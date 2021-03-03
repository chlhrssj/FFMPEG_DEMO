package com.rssj.basecore.base.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.rssj.basecore.base.viewmodel.BaseViewModel

/**
 * Description:懒加载Fragment 结合setMaxLife()方法一起使用
 * 对于FragmentPagerAdapter 需要设置参数 BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT，
 * 此时会自动设置为Lifecycle.State.STARTED,在结合ViewPager使用时，只有当前显示的Fragment
 * 为onResume状态，其他均为onStart 或者 onPause状态，当Fragment对用户展示时才走到
 * onResume状态
 *
 * Date：2019/12/31 0031-15:00
 * Author: cwh
 */
abstract class BaseLazyFragment<VM : BaseViewModel<*>, V : ViewBinding> :
    BaseFragment<VM, V>() {

    /**
     * 是否是第一次对于用户可见
     */
    private var isFirstVisible: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isFirstVisible = true
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        isFirstVisible = true
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    //onViewCreated -> onStart -> onResume
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
        if (isFirstVisible) {
            onFragmentFirstVisible()
            isFirstVisible = false
        }

        onFragmentVisibleToUser()

    }

    override fun onDestroy() {
        super.onDestroy()
        isFirstVisible = false
    }

    /**
     * Fragment第一次对于用户可见
     */
    abstract fun onFragmentFirstVisible()

    /**
     * Fragment对于用户可见时
     */
    abstract fun onFragmentVisibleToUser()

}