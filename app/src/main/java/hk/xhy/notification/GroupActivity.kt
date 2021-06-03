package hk.xhy.notification

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import hk.xhy.notification.group.GroupData
import hk.xhy.notification.group.GroupNotification
import hk.xhy.notification.ui.base.BaseActivity
import kotlinx.android.synthetic.main.activity_group.*

class GroupActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group)

        btnGroup.setOnClickListener {
            generateGroupNotification()
        }

        btnGroupOther.setOnClickListener {
            generateOtherOneGroupNotification()
        }

    }

    override fun backDisplay(): Boolean {
        return true
    }

    //todo !!!! 华为这么用有BUG
    /**
     * 先调用 generateGroupNotification 再调用generateOtherOneGroupNotification
     *
     * 具体表现是 先有一组 Group通知，后弹出的第三条通知，会加入这个Group中
     *
     * 华为系统BUG，第三条通知会覆盖先前那组Group
     */

    private fun generateGroupNotification() {
        val notification = GroupNotification(this, GroupData(summaryId = 200))
        notification.show()
    }

    private fun generateOtherOneGroupNotification() {
        val notification = GroupNotification(this, GroupData(summaryId = 200))
        notification.show2()
    }
}