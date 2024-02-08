package com.boki.bokispringsecurity.member.repository

import com.boki.bokispringsecurity.common.status.PRIVILEGE
import com.boki.bokispringsecurity.common.status.ROLE
import com.boki.bokispringsecurity.member.entity.Member
import com.boki.bokispringsecurity.member.entity.Privilege
import com.boki.bokispringsecurity.member.entity.Role
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface MemberRepository : JpaRepository<Member, Long> {
    fun findByLoginId(loginId: String): Member?
}

@Repository
interface RoleRepository : JpaRepository<Role, Long> {
    fun findByName(name: ROLE): Role?
}

@Repository
interface PrivilegeRepository : JpaRepository<Privilege, Long> {
    fun findByName(name: PRIVILEGE): Privilege?
}

