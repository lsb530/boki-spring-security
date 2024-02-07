package com.boki.bokispringsecurity.member.repository

import com.boki.bokispringsecurity.member.entity.Member
import com.boki.bokispringsecurity.member.entity.MemberRole
import org.springframework.data.jpa.repository.JpaRepository

interface MemberRepository: JpaRepository<Member, Long> {
    fun findByLoginId(loginId: String): Member?
}

interface MemberRoleRepository: JpaRepository<MemberRole, Long>
