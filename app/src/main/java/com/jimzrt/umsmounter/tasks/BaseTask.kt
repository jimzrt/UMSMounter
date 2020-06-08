package com.jimzrt.umsmounter.tasks

import android.app.Activity
import java.lang.ref.WeakReference

abstract class BaseTask {
    //  Context ctx;
    @JvmField
    var name: String? = null

    @JvmField
    var description: String? = null

    @JvmField
    var result: String? = null

    @JvmField
    var ctx: WeakReference<Activity>? = null

    abstract fun execute(): Boolean

    fun setContext(context: WeakReference<Activity>?) {
        ctx = context
    }
}