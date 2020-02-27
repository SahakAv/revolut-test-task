package com.revolut.task.exception

import java.lang.RuntimeException

open class RuntimeServiceException(val msg: String) : RuntimeException(msg)