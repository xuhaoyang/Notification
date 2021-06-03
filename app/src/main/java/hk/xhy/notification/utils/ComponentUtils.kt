package hk.xhy.notification.utils

import android.content.ComponentName
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import hk.xhy.notification.Global
import kotlin.reflect.KClass

val KClass<*>.componentName: ComponentName
    @RequiresApi(Build.VERSION_CODES.M)
    get() = ComponentName.createRelative(Global.application, this.java.name)

val KClass<*>.intent: Intent
    get() = Intent(Global.application, this.java)

