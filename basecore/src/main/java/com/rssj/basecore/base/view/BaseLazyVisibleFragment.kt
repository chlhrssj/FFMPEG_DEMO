package com.rssj.basecore.base.view

import android.os.Bundle
import android.view.View
import androidx.viewbinding.ViewBinding
import com.rssj.basecore.base.viewmodel.BaseViewModel

/**
 * Description:懒加载Fragment 结合setMaxLife()方法一起使用
 * 对于FragmentPagerAdapter 需要设置参数 BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT，
 * 此时会自动设置为Lifecycle.State.STARTED,在结合ViewPager使用时，只有当前显示的Fragment
 * 为onResume状态，其他均为onStart 或者 onPause状态，当Fragment对用户展示时才走到
 * onResume状态
 *
 * 对于FragmentPagerAdapter不设置第二个Behavior参数时，默认使用 BEHAVIOR_SET_USER_VISIBLE_HINT模式,
 * 此时对于Fragment的懒加载，需要通过使用setUserVisibleHint函数，确定是否对用户可见
 *
 * Date：2020/1/6 0006-11:52
 * Author: cwh
 */

@Deprecated(
    "建议设置FragmentPagerAdapter 的第二个参数为BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT," +
            "结合BaseLazyFragment 使用Fragment的懒加载"
)
abstract class BaseLazyVisibleFragment<VM : BaseViewModel<*>, V : ViewBinding> :
    BaseFragment<VM, V>() {


    /**
     * fragment 是否是第一次可见的标志
     */
    private var isFirstVisible = false


    /**
     * fragment 是否已准备好
     */
    private var isPrepareView = false

    /**
     * 对用户来说是否可见
     */
    private var isVisibleToUser = false


    private fun initStatus() {
        isFirstVisible = true
        isPrepareView = false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initStatus()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        initStatus()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        isPrepareView = true
        if (isVisibleToUser) {
            if (isFirstVisible) {
                isFirstVisible = false
                onFragmentFirstVisible()
            }
            onFragmentVisibleToUser(true)
        }
    }


    /**
     * Fragment 是否已准备好，且对用户可见
     *
     * @return
     */
    fun isFragmentVisible(): Boolean {
        return isVisibleToUser && isPrepareView
    }

    override fun onResume() {
        super.onResume()
        if (isVisibleToUser) {
            onFragmentVisibleToUser(true)
        }
    }


    /**
     * fragment 对用户是否可见发生改变时调用
     *
     * @param isVisible 是否可见
     */
    protected abstract fun onFragmentVisibleToUser(isVisible: Boolean)

    /**
     * fragment 第一次对用户可见
     */
    protected abstract fun onFragmentFirstVisible()

    //当对用户是否可见改变时，调用
    override fun setUserVisibleHint(isVisible: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        isVisibleToUser = isVisible
        if (!isPrepareView) {
            return
        }
        if (isFirstVisible && isVisibleToUser) {
            isFirstVisible = false
            onFragmentFirstVisible()
        }
        onFragmentVisibleToUser(isVisibleToUser)
    }

}