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
import org.springframework.context.annotation.Bean
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Transactional
@Service
class MemberService(
    private val memberRepository: MemberRepository,
    private val memberRoleRepository: MemberRoleRepository,
    private val authenticationManagerBuilder: AuthenticationManagerBuilder,
    private val jwtTokenProvider: JwtTokenProvider,
    private val passwordEncoder: PasswordEncoder,
) {
    @Bean
    fun createInitUsers() {
        val adminReq = MemberDtoRequest(
            id = null,
            _loginId = "admin",
            _password = "admin",
            _name = "admin",
            _birthDate = "1992-05-30",
            _email = "admin@test.com",
            _gender = "MAN"
        )
        val admin = adminReq.toEntity().apply {
            password = passwordEncoder.encode(password)
        }
        val adminRole = MemberRole(role = ROLE.ADMIN, member = admin)

        val staffReq = MemberDtoRequest(
            id = null,
            _loginId = "staff",
            _password = "staff",
            _name = "staff",
            _birthDate = "1992-05-30",
            _email = "staff@test.com",
            _gender = "WOMAN"
        )
        val staff = staffReq.toEntity().apply {
            password = passwordEncoder.encode(password)
        }
        val staffRole = MemberRole(role = ROLE.STAFF, member = staff)

        memberRepository.save(admin)
        memberRoleRepository.save(adminRole)

        memberRepository.save(staff)
        memberRoleRepository.save(staffRole)
    }

    /**
     * 회원가입
     */
    fun signUp(memberDtoRequest: MemberDtoRequest): String {
        var member: Member? = memberRepository.findByLoginId(memberDtoRequest.loginId)
        if (member != null) {
            throw InvalidInputException("loginId", "이미 등록된 ID입니다")
        }

        member = memberDtoRequest.toEntity()
        member.apply {
            password = passwordEncoder.encode(password)
        }

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
        val member: Member =
            memberRepository.findByIdOrNull(id) ?: throw InvalidInputException("id", "회원번호($id)가 존재하지 않는 유저입니다.")
        return member.toDto()
    }

    /**
     * 내 정보 수정
     */
    fun saveMyInfo(memberDtoRequest: MemberDtoRequest): String {
        val member = memberDtoRequest.toEntity()
        memberRepository.save(member)
        return "수정 완료되었습니다."
    }
}
