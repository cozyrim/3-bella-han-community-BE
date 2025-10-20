package com.ktbweek4.community.post.repository.impl;

import com.ktbweek4.community.post.dto.PostListItemDTO;
import com.ktbweek4.community.post.entity.QPostEntity;
import com.ktbweek4.community.post.entity.QPostImageEntity;
import com.ktbweek4.community.post.repository.CustomPostRepository;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;


import java.util.List;

import static com.querydsl.core.types.dsl.Expressions.stringTemplate;


@Repository
@RequiredArgsConstructor
public class CustomPostRepositoryImpl implements CustomPostRepository {

    private final JPAQueryFactory query;
    @Override
    public List<PostListItemDTO> fetchSliceByCursor(Long cursorPostId, int size) {
        QPostEntity post = QPostEntity.postEntity;
        QPostImageEntity image = QPostImageEntity.postImageEntity;

        // 대표 이미지 선택 규칙:
        // 1) isPrimary = true 우선
        // 2) 없으면 orderIndex 최솟값 한 장
        // → 간단하게는 isPrimary = true 인 것만 left join 하고, 없으면 subquery/CASE로 대체 가능.
        // 여기서는 가장 간단히: isPrimary = true 만 조인해서 하나만 끌고오되 없으면 null.
        var base = query
                .select(Projections.constructor(
                        PostListItemDTO.class,
                        post.postId,
                        post.title,
                        post.content,
                        post.author.userId,
                        post.author.nickname,
                        image.postImageUrl, // 대표 이미지일 때만 값 나옴
                        post.createdAt
                ))
                .from(post)
                .leftJoin(image).on(image.post.eq(post)
                        .and(image.isPrimary.isTrue()))
                .where(cursorPostId != null ? post.postId.lt(cursorPostId) : null)
                .orderBy(post.postId.desc())
                .limit(size);

        return base.fetch();
    }
}
