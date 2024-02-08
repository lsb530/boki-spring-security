package com.boki.bokispringsecurity.post.controller

import com.boki.bokispringsecurity.common.dto.BaseResponse
import com.boki.bokispringsecurity.member.service.MemberService
import com.boki.bokispringsecurity.post.dto.PostCreateReq
import com.boki.bokispringsecurity.post.dto.PostReadRes
import com.boki.bokispringsecurity.post.service.PostService
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RequestMapping("/posts")
@RestController
class PostController(
    private val postService: PostService,
    private val memberService: MemberService,
) {
    @PreAuthorize("isAuthenticated()")
    @PostMapping
    fun createPost(@RequestBody createPostReq: PostCreateReq): BaseResponse<Unit> {
        val resultMsg: String = postService.creatPost(createPostReq)
        return BaseResponse(message = resultMsg)
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    fun getPosts(): BaseResponse<List<PostReadRes>> {
        val posts = postService.getPosts()
        return BaseResponse(data = posts)
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id}")
    fun getPost(@PathVariable id: Long): BaseResponse<PostReadRes> {
        val post = postService.getPost(id)
        val me = memberService.me()
        if (me.id != post.authorId) {
            throw AccessDeniedException("You do not have permission to access this post")
        }
        return BaseResponse(data = post)
    }

//    @PreAuthorize("hasRole('ADMIN')")
    @PreAuthorize("hasAuthority('DELETE')")
    @DeleteMapping("/{id}")
    fun deletePost(@PathVariable id: Long): BaseResponse<Unit> {
        val message = postService.deletePost(id)
        return BaseResponse(message = message)
    }

}
