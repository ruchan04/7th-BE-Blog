package com.example.demo1.domain.user.entity;

import com.example.demo1.domain.comment.entity.Comment;
import com.example.demo1.domain.post.entity.Post;
import com.example.demo1.domain.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Integer age;

    @Column(nullable = false, length = 20)
    private String name;

    @Column(nullable = false, unique = true, length = 100)
    private String email; // 중복 제거됨

    @Column(length = 50)
    private String nickname;

    @Column(nullable = false)
    private String password; // 클래스 내부로 이동

    @Builder.Default
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Post> posts = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    public void encodePassword(String encodedPassword) {
        this.password = encodedPassword;
    }
}