package com.boki.bokispringsecurity.common.dto

import com.boki.bokispringsecurity.common.status.ResultCode

data class BaseResponse<T>(
    val resultCode: String = ResultCode.SUCCESS.name,
    val data: T? = null,
    val message: String = ResultCode.SUCCESS.msg,
)
