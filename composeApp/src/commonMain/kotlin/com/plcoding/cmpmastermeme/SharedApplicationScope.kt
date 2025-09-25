package com.plcoding.cmpmastermeme

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

object SharedApplicationScope {
    var scope = CoroutineScope(SupervisorJob())
}