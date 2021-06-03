package hk.xhy.notification.group

/**
 * User: xuhaoyang
 * mail: xuhaoyang3x@gmail.com
 * Date: 2021/6/3
 * Time: 11:40 上午
 * Description: No Description
 */
data class GroupData(
    val channelGroupId: String = "default",
    val channelGroupName: String = "Default Channel Group",
    val channelId: String = "Group_Notification",
    val channelName: String = "Group Notification",
    val groupKey: String = "com.android.example.WORK_EMAIL",
    val summaryId: Int = 0
)
