package com.boki.bokispringsecurity.member.repository

import com.boki.bokispringsecurity.member.entity.Member
import com.boki.bokispringsecurity.member.entity.MemberRole
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface MemberRepository : JpaRepository<Member, Long> {
    fun findByLoginId(loginId: String): Member?
}

@Repository
interface MemberRoleRepository : JpaRepository<MemberRole, Long>
