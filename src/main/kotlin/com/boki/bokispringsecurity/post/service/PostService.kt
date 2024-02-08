package com.boki.bokispringsecurity.post.service

import com.boki.bokispringsecurity.common.exception.InvalidInputException
import com.boki.bokispringsecurity.member.service.MemberService
import com.boki.bokispringsecurity.post.dto.PostCreateReq
import com.boki.bokispringsecurity.post.dto.PostReadRes
import com.boki.bokispringsecurity.post.repository.PostRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Transactional
@Service
class PostService(
    private val postRepository: PostRepository,
    private val memberService: MemberService,
) {
    /**
     * 글 생성
     */
    fun creatPost(createPostReq: PostCreateReq): String {
        val author = memberService.me()
        val post = createPostReq.toEntity(author)
        author.posts.add(post)
        postRepository.save(post)
        return "글이 작성되었습니다."
    }

    /**
     * 글 전체 조회
     */
    fun getPosts(): List<PostReadRes> {
        return postRepository.findAll().map { it.toDto() }
    }

    /**
     * 글 단일 조회
     */
    fun getPost(id: Long): PostReadRes {
        val post = postRepository.findByIdOrNull(id) ?: throw InvalidInputException("id", "글 번호($id)가 존재하지 않습니다.")
        return post.toDto()
    }

    /**
     * 글 삭제
     */
    fun deletePost(id: Long): String {
        val post = postRepository.findByIdOrNull(id) ?: throw InvalidInputException("id", "글 번호($id)가 존재하지 않습니다.")
        postRepository.delete(post)
        return "글 번호($id)가 삭제되었습니다."
    }
}
