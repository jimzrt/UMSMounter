package com.jimzrt.umsmounter.tasks

import com.topjohnwu.superuser.Shell

class CheckRootTask : BaseTask() {
    override fun execute(): Boolean {
        return if (Shell.rootAccess()) {
            result = "Root working!\n"
            true
        } else {
            result = "Root not working!\n"
            false
        }
    }

    init {
        name = "Check Root"
        description = "Checking root..."
    }
}