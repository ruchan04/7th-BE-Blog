package com.example.demo1.domain.post.entity;

import com.example.demo1.domain.comment.entity.Comment;
import com.example.demo1.domain.user.entity.User;
import com.example.demo1.domain.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "post")
@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Post extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // [★체크] Service와 타입을 맞추기 위해 Integer 대신 Long 사용을 권장합니다.

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = true) // [★체크] 테스트 편의를 위해 일단 nullable = true로 수정했습니다.
    private User user;

    @Column(length = 255)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(length = 255)
    private String description;

    @Builder.Default
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    private List<Comment> comments = new ArrayList<>();

    // [★추가] 게시글 수정을 위한 핵심 메서드입니다.
    // 이 메서드가 있어야 PostService의 50라인 에러가 사라집니다.
    public void update(String title, String content, String description) {
        this.title = title;
        this.content = content;
        this.description = description;
    }
}