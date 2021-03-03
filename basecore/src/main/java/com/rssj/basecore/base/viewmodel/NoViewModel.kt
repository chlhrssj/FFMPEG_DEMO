package com.rssj.basecore.base.viewmodel

import android.app.Application
import com.rssj.basecore.base.repository.IRepository
import com.rssj.basecore.base.viewmodel.BaseViewModel

/**
 * Description:不需要ViewModel时，
 * 传入该类型ViewModel即可(但是还是拥有一些基本的ViewModel功能，
 * 如跳转界面等)
 * Date：2020/1/2 0002-14:00
 * Author: cwh
 */
class NoViewModel(application: Application) : BaseViewModel<IRepository>(application) {
    override var repo: IRepository = object : IRepository {
        override fun onClear() {

        }
    }


}