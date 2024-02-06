package com.boki.bokispringsecurity.common.authority

data class TokenInfo(
    val grantType: String,
    val accessToken: String,
)
