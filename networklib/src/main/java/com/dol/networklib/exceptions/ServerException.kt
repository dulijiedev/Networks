package com.dol.networklib.exceptions

import java.lang.RuntimeException

/**
 * Created by dlj on 2019/9/23.
 */
class ServerException(val code: Int, val msg: String) : RuntimeException()