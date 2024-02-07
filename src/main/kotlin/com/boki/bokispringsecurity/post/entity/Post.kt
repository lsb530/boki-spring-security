package com.boki.bokispringsecurity.post.entity

import com.boki.bokispringsecurity.member.entity.Member
import com.boki.bokispringsecurity.post.dto.PostReadRes
import jakarta.persistence.*

@Entity
@Table
class Post(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long? = null,

    @Column(nullable = false, length = 30)
    val title: String,

    @Column(nullable = false)
    val content: String,

    @ManyToOne(fetch = FetchType.LAZY)
    val author: Member,
) {
    fun toDto(): PostReadRes =
        PostReadRes(id!!, title, content, author.id!!)
}
