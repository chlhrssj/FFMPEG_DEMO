package com.rssj.basecore.base.repository

import com.rssj.basecore.base.repository.IRepository

/**
 * Description:不需要Repository时，传入该类型即可
 * Date：2020/11/9 0009-11:39
 * Author: cwh
 */
class NoRepository : IRepository {
    override fun onClear() {
    }
}