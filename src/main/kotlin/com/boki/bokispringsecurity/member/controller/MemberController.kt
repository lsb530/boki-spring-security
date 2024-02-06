package com.boki.bokispringsecurity.member.controller

import com.boki.bokispringsecurity.common.authority.TokenInfo
import com.boki.bokispringsecurity.common.dto.BaseResponse
import com.boki.bokispringsecurity.member.dto.LoginDto
import com.boki.bokispringsecurity.member.dto.MemberDtoRequest
import com.boki.bokispringsecurity.member.dto.MemberDtoResponse
import com.boki.bokispringsecurity.member.service.MemberService
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class MemberController(
    private val memberService: MemberService,
) {
    /**
     * 회원가입
     */
    @PostMapping("/signup")
    fun signUp(@RequestBody @Valid memberDtoRequest: MemberDtoRequest): BaseResponse<Unit> {
        val resultMsg: String = memberService.signUp(memberDtoRequest)
        return BaseResponse(message = resultMsg)
    }

    /**
     * 로그인
     */
    @PostMapping("/login")
    fun login(@RequestBody @Valid loginDto: LoginDto): BaseResponse<TokenInfo> {
        val tokenInfo = memberService.login(loginDto)
        return BaseResponse(data = tokenInfo)
    }

    /**
     * 내 정보 보기
     */
    @GetMapping("/info/{id}")
    fun searchMyInfo(@PathVariable id: Long): BaseResponse<MemberDtoResponse> {
        val response = memberService.searchMyInfo(id)
        return BaseResponse(data = response)
    }
}
