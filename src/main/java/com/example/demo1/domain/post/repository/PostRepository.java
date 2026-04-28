package com.example.demo1.domain.post.repository;

import com.example.demo1.domain.post.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository // [★필수] 스프링이 이 인터페이스를 데이터 저장소로 인식하게 합니다.
public interface PostRepository extends JpaRepository<Post, Long> {
    // 아무 내용도 적지 않아도 기본 CRUD 기능이 작동합니다.
}