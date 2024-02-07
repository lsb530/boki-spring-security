package com.boki.bokispringsecurity.post.dto

import com.boki.bokispringsecurity.member.entity.Member
import com.boki.bokispringsecurity.post.entity.Post
import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.NotBlank

data class PostCreateReq(
    var id: Long?,

    @field:NotBlank
    @JsonProperty("title")
    private val _title: String?,

    @field:NotBlank
    @JsonProperty("content")
    private val _content: String?,
    ) {
    val title: String
        get() = _title!!

    val content: String
        get() = _content!!

    fun toEntity(member: Member): Post =
        Post(id, title, content, member)
}

data class PostReadRes(
    val id: Long,
    val title: String,
    val content: String,
    val authorId: Long,
)
