package com.boki.bokispringsecurity.member.entity

import com.boki.bokispringsecurity.common.status.Gender
import com.boki.bokispringsecurity.common.status.PRIVILEGE
import com.boki.bokispringsecurity.common.status.ROLE
import com.boki.bokispringsecurity.member.dto.MemberDtoResponse
import com.boki.bokispringsecurity.post.entity.Post
import jakarta.persistence.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Entity
@Table
class Member(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long? = null,

    @Column(nullable = false, length = 30, unique = true, updatable = false)
    val loginId: String,

    @Column(nullable = false, length = 100)
    var password: String,

    @Column(nullable = false, length = 10)
    val name: String,

    @Column(nullable = false)
    val birthDate: LocalDate,

    @Column(nullable = false, length = 5)
    @Enumerated(EnumType.STRING)
    val gender: Gender,

    @Column(nullable = false, length = 30)
    val email: String,
) {
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "member_roles",
        joinColumns = [JoinColumn(name = "member_id", referencedColumnName = "id")],
        inverseJoinColumns = [JoinColumn(name = "role_id", referencedColumnName = "id")]
    )
    val roles: MutableSet<Role> = mutableSetOf()

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "author")
    val posts: MutableList<Post> = mutableListOf()

    private fun LocalDate.formatDate(): String =
        this.format(DateTimeFormatter.ofPattern("yyyyMMdd"))

    fun toDto(): MemberDtoResponse =
        MemberDtoResponse(id!!, loginId, name, birthDate.formatDate(), gender.desc, email, posts.map { it.toDto() })
}

@Entity
class Role(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long? = null,

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    val name: ROLE,

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "role_privileges",
        joinColumns = [JoinColumn(name = "role_id", referencedColumnName = "id")],
        inverseJoinColumns = [JoinColumn(name = "privilege_id", referencedColumnName = "id")]
    )
    val privileges: Set<Privilege> = mutableSetOf()
)

@Entity
class Privilege(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long? = null,

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    val name: PRIVILEGE
)

