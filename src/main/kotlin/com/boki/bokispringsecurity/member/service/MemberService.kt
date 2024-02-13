package com.boki.bokispringsecurity.member.service

import com.boki.bokispringsecurity.common.authority.JwtTokenProvider
import com.boki.bokispringsecurity.common.authority.TokenInfo
import com.boki.bokispringsecurity.common.dto.CustomUser
import com.boki.bokispringsecurity.common.exception.InvalidInputException
import com.boki.bokispringsecurity.common.status.Gender
import com.boki.bokispringsecurity.common.status.PRIVILEGE
import com.boki.bokispringsecurity.common.status.ROLE
import com.boki.bokispringsecurity.member.dto.LoginDto
import com.boki.bokispringsecurity.member.dto.MemberDtoRequest
import com.boki.bokispringsecurity.member.dto.MemberDtoResponse
import com.boki.bokispringsecurity.member.entity.Member
import com.boki.bokispringsecurity.member.entity.Privilege
import com.boki.bokispringsecurity.member.entity.Role
import com.boki.bokispringsecurity.member.repository.MemberRepository
import com.boki.bokispringsecurity.member.repository.PrivilegeRepository
import com.boki.bokispringsecurity.member.repository.RoleRepository
import org.springframework.context.annotation.Bean
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.time.Period

@Transactional
@Service
class MemberService(
    private val memberRepository: MemberRepository,
    private val roleRepository: RoleRepository,
    private val privilegeRepository: PrivilegeRepository,
    private val authenticationManagerBuilder: AuthenticationManagerBuilder,
    private val jwtTokenProvider: JwtTokenProvider,
    private val passwordEncoder: PasswordEncoder,
) {
    @Bean
    fun createInitUsers() {
        // PRIVILEGE 인스턴스 생성 및 저장
        val allPrivileges = PRIVILEGE.entries.map { privilege ->
            privilegeRepository.findByName(privilege) ?: Privilege(name = privilege).also { privilegeRepository.save(it) }
        }.toSet()

        val adminPrivileges = allPrivileges // ADMIN은 모든 권한
        val staffPrivileges = allPrivileges.filterNot { it.name == PRIVILEGE.DELETE }.toSet() // STAFF는 DELETE 제외
        val memberPrivileges = allPrivileges.filter { it.name == PRIVILEGE.READ }.toSet() // MEMBER는 READ만

        // Admin 사용자 생성
//        val adminRole = roleRepository.findByName(ROLE.ADMIN) ?: Role(name = ROLE.ADMIN, privileges = emptySet())
        val adminRole = roleRepository.findByName(ROLE.ADMIN) ?: Role(name = ROLE.ADMIN, privileges = adminPrivileges)
        val admin = Member(
            loginId = "admin",
            password = passwordEncoder.encode("admin"),
            name = "Admin",
            birthDate = LocalDate.parse("1992-05-30"),
            gender = Gender.MAN,
            email = "admin@test.com"
        ).apply {
            roles.add(adminRole)
        }
        roleRepository.save(adminRole)
        memberRepository.save(admin)

        // Staff 사용자 생성
//        val staffRole = roleRepository.findByName(ROLE.STAFF) ?: Role(name = ROLE.STAFF, privileges = emptySet())
        val staffRole = roleRepository.findByName(ROLE.STAFF) ?: Role(name = ROLE.STAFF, privileges = staffPrivileges)
        val staff = Member(
            loginId = "staff",
            password = passwordEncoder.encode("staff"),
            name = "Staff",
            birthDate = LocalDate.parse("1992-05-30"),
            gender = Gender.WOMAN,
            email = "staff@test.com"
        ).apply {
            roles.add(staffRole)
        }
        roleRepository.save(staffRole)
        memberRepository.save(staff)
    }

    /**
     * 로그인된 회원 정보 반환
     */
    fun me(): Member {
        val authentication = SecurityContextHolder.getContext().authentication
        val userId = (authentication.principal as CustomUser).userId
        val member: Member =
            memberRepository.findByIdOrNull(userId) ?: throw InvalidInputException("userId", "회원번호($userId)가 존재하지 않는 유저입니다.")
        return member
    }

    fun isAdult(member: Member): Boolean {
        val age = Period.between(member.birthDate, LocalDate.now()).years
        return age >= 20
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
        val memberRole = roleRepository.findByName(ROLE.MEMBER) ?: Role(name = ROLE.MEMBER, privileges = emptySet())
        roleRepository.save(memberRole)

        member.apply {
            password = passwordEncoder.encode(password)
            roles.add(memberRole)
        }

        memberRepository.save(member)

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

    fun getMember(id: Long): Member {
        val member: Member =
            memberRepository.findByIdOrNull(id) ?: throw InvalidInputException("id", "회원번호($id)가 존재하지 않는 유저입니다.")
        return member
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
