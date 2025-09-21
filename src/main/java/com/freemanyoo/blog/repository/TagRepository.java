package com.freemanyoo.blog.repository;

import com.freemanyoo.blog.domain.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TagRepository extends JpaRepository<Tag, Long> {
}
