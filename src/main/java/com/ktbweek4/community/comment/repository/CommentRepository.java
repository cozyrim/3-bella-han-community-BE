package com.ktbweek4.community.comment.repository;

import com.ktbweek4.community.comment.entity.Comment;
import com.ktbweek4.community.post.entity.PostEntity;
import org.apache.catalina.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    int countByPost_PostId(Long postId);
}
