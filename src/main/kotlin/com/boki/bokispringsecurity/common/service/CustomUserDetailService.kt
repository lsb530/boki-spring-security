package com.boki.bokispringsecurity.common.service

import com.boki.bokispringsecurity.common.dto.CustomUser
import com.boki.bokispringsecurity.member.entity.Member
import com.boki.bokispringsecurity.member.repository.MemberRepository
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class CustomUserDetailService(
    private val memberRepository: MemberRepository,
) : UserDetailsService {
    override fun loadUserByUsername(username: String): UserDetails =
        memberRepository.findByLoginId(username)
            ?.let { createUserDetails(it) } ?: throw UsernameNotFoundException("해당 유저는 없습니다.")

    private fun createUserDetails(member: Member): UserDetails {
        val authorities = member.roles.map { role ->
            SimpleGrantedAuthority("ROLE_${role.name}")
        }.toSet()

        return CustomUser(
            member.id!!,
            member.loginId,
            member.password,
            authorities
        )
    }
}
