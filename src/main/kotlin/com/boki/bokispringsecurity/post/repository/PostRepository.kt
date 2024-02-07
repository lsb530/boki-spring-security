package com.boki.bokispringsecurity.post.repository

import com.boki.bokispringsecurity.post.entity.Post
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface PostRepository : JpaRepository<Post, Long>
