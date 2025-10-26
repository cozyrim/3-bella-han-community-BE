package com.ktbweek4.community.post.repository;


import com.ktbweek4.community.post.entity.PostEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostRepository extends JpaRepository<PostEntity, Long>, CustomPostRepository {
    Optional<PostEntity> findById(Long postId);
}
