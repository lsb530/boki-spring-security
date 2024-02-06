package com.boki.bokispringsecurity.member.service

import com.boki.bokispringsecurity.common.authority.JwtTokenProvider
import com.boki.bokispringsecurity.common.authority.TokenInfo
import com.boki.bokispringsecurity.common.exception.InvalidInputException
import com.boki.bokispringsecurity.common.status.ROLE
import com.boki.bokispringsecurity.member.dto.LoginDto
import com.boki.bokispringsecurity.member.dto.MemberDtoRequest
import com.boki.bokispringsecurity.member.dto.MemberDtoResponse
import com.boki.bokispringsecurity.member.entity.Member
import com.boki.bokispringsecurity.member.entity.MemberRole
import com.boki.bokispringsecurity.member.repository.MemberRepository
import com.boki.bokispringsecurity.member.repository.MemberRoleRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Transactional
@Service
class MemberService(
    private val memberRepository: MemberRepository,
    private val memberRoleRepository: MemberRoleRepository,
    private val authenticationManagerBuilder: AuthenticationManagerBuilder,
    private val jwtTokenProvider: JwtTokenProvider,
) {
    /**
     * 회원가입
     */
    fun signUp(memberDtoRequest: MemberDtoRequest): String {
        var member: Member? = memberRepository.findByLoginId(memberDtoRequest.loginId)
        if (member != null) {
            throw InvalidInputException("loginId", "이미 등록된 ID입니다")
        }

        member = memberDtoRequest.toEntity()
        memberRepository.save(member)

        val memberRole = MemberRole(role = ROLE.MEMBER, member = member)
        memberRoleRepository.save(memberRole)

        return "회원가입이 완료되었습니다."
    }

    /**
     * 로그인 -> 토큰 발행
     */
    fun login(loginDto: LoginDto): TokenInfo {
        val authenticationToken = UsernamePasswordAuthenticationToken(loginDto.loginId, loginDto.password)
        val authentication = authenticationManagerBuilder.`object`.authenticate(authenticationToken)

        return jwtTokenProvider.createToken(authentication)
    }

    /**
     * 내 정보 조회
     */
    fun searchMyInfo(id: Long): MemberDtoResponse {
        val member: Member = memberRepository.findByIdOrNull(id) ?: throw InvalidInputException("id", "회원번호($id)가 존재하지 않는 유저입니다.")
        return member.toDto()
    }
}
