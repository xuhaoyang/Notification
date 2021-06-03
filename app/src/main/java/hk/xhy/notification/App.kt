package hk.xhy.notification

import android.app.Application

/**
 * User: xuhaoyang
 * mail: xuhaoyang3x@gmail.com
 * Date: 2021/6/2
 * Time: 4:19 下午
 * Description: No Description
 */
open class App : Application() {
    override fun onCreate() {
        super.onCreate()
        Global.init(this)
    }
}