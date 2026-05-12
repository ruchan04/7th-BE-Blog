package com.example.demo1.domain.user.repository;

import com.example.demo1.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {

    // [추가] 이메일로 가입된 유저가 있는지 확인하는 메서드
    boolean existsByEmail(String email);

    // [추가] 로그인할 때 이메일로 유저를 찾아오기 위한 메서드
    Optional<User> findByEmail(String email);
}
