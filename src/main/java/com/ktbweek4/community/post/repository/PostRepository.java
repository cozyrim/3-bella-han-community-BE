package com.ktbweek4.community.post.repository;


import com.ktbweek4.community.post.entity.PostEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PostRepository extends JpaRepository<PostEntity, Long>, CustomPostRepository {
    Optional<PostEntity> findById(Long postId);
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update PostEntity p set p.viewCount = p.viewCount + 1 where p.postId = :postId")
    int incrementViewCount(@Param("postId") Long postId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update PostEntity p set p.likesCount = p.likesCount + 1 where p.postId = :postId")
    int incrementLikesCount(@Param("postId") Long postId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
           update PostEntity p 
              set p.likesCount = case when p.likesCount > 0 then p.likesCount - 1 else 0 end
            where p.postId = :postId
           """)
    int decrementLikesCountSafe(@Param("postId") Long postId);
}
