// src/main/java/com/ktbweek4/community/post/like/repository/PostLikeRepository.java
package com.ktbweek4.community.post.like.repository;

import com.ktbweek4.community.post.entity.PostEntity;
import com.ktbweek4.community.post.like.entity.PostLike;
import com.ktbweek4.community.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PostLikeRepository extends JpaRepository<PostLike, Long> {
    boolean existsByPostAndUser(PostEntity post, User user);
    long countByPost(PostEntity post);

    boolean existsByPost_PostIdAndUser_UserId(Long postId, Long userId);
    long countByPost_PostId(Long postId);
    // 삭제는 "영향받은 행 수"를 반환해야 함
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("delete from PostLike pl where pl.post = :post and pl.user = :user")
    int deleteByPostAndUser(@Param("post") PostEntity post, @Param("user") User user);
}