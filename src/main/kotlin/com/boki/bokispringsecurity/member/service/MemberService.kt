package com.boki.bokispringsecurity.member.service

import com.boki.bokispringsecurity.common.exception.InvalidInputException
import com.boki.bokispringsecurity.common.status.ROLE
import com.boki.bokispringsecurity.member.dto.MemberDtoRequest
import com.boki.bokispringsecurity.member.entity.Member
import com.boki.bokispringsecurity.member.entity.MemberRole
import com.boki.bokispringsecurity.member.repository.MemberRepository
import com.boki.bokispringsecurity.member.repository.MemberRoleRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Transactional
@Service
class MemberService(
    private val memberRepository: MemberRepository,
    private val memberRoleRepository: MemberRoleRepository,
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

        val memberRole: MemberRole = MemberRole(role = ROLE.MEMBER, member = member)
        memberRoleRepository.save(memberRole)

        return "회원가입이 완료되었습니다."
    }
}
