package com.boki.bokispringsecurity.member.controller

import com.boki.bokispringsecurity.member.dto.MemberDtoRequest
import com.boki.bokispringsecurity.member.service.MemberService
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
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
    fun signUp(@RequestBody @Valid memberDtoRequest: MemberDtoRequest): ResponseEntity<String> {
        return ResponseEntity.ok().body(memberService.signUp(memberDtoRequest))
    }
}
