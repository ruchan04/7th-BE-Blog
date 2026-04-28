package com.example.demo1.domain.post.service;

import com.example.demo1.domain.post.dto.PostBlockDto;
import com.example.demo1.domain.post.dto.PostRequestDto;
import com.example.demo1.domain.post.dto.PostResponseDto;
import com.example.demo1.domain.post.entity.Post;
import com.example.demo1.domain.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {

    private final PostRepository postRepository;

    // 1. 목록 조회 로직 (엔티티 리스트를 DTO 리스트로 변환해서 반환하는 것이 정석입니다)
    public List<PostResponseDto> findAll() {
        return postRepository.findAll().stream()
                .map(post -> PostResponseDto.builder()
                        .postId(post.getId())
                        .title(post.getTitle())
                        .content(post.getContent())
                        .description(post.getDescription())
                        .authorNickname("jae")
                        .createdAt(post.getCreatedAt())
                        .build())
                .collect(Collectors.toList());
    }

    // 2. 상세 조회 로직
    public PostResponseDto getPostDetail(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NoSuchElementException("해당 게시글을 찾을 수 없습니다."));

        List<PostBlockDto> mockBlocks = List.of(
                PostBlockDto.builder()
                        .blockType("TEXT")
                        .textContent("본문 내용입니다.")
                        .imageUrl(null)
                        .build()
        );

        return PostResponseDto.builder()
                .postId(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .description(post.getDescription())
                .authorNickname("jae")
                .createdAt(post.getCreatedAt())
                .build();
    }

    // 3. 작성 로직 (하나로 통합함!)
    @Transactional
    public PostResponseDto save(PostRequestDto dto) {
        Post post = Post.builder()
                .title(dto.getTitle())
                .content(dto.getContent())
                .description(dto.getDescription())
                .build();

        Post savedPost = postRepository.save(post);

        return PostResponseDto.builder()
                .postId(savedPost.getId())
                .title(savedPost.getTitle())
                .content(savedPost.getContent())
                .description(savedPost.getDescription())
                .authorNickname("yuchan")
                .createdAt(savedPost.getCreatedAt() != null ? savedPost.getCreatedAt() : LocalDateTime.now())
                .blocks(dto.getBlocks())
                .build();
    }

    // 4. 수정 로직
    @Transactional
    public PostResponseDto update(Long postId, PostRequestDto dto) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NoSuchElementException("해당 게시글을 찾을 수 없습니다."));

        post.update(dto.getTitle(), dto.getContent(), dto.getDescription());

        return PostResponseDto.builder()
                .postId(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .description(post.getDescription())
                .authorNickname("jae")
                .createdAt(post.getCreatedAt())
                .build();
    }

    // 5. 삭제 로직
    @Transactional
    public void delete(Long postId) {
        if (!postRepository.existsById(postId)) {
            throw new NoSuchElementException("해당 게시글을 찾을 수 없습니다.");
        }
        postRepository.deleteById(postId);
    }
}