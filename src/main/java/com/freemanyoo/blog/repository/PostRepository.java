package com.freemanyoo.blog.repository;

import com.freemanyoo.blog.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {
}
