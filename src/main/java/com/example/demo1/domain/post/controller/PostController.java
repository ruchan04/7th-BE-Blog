package com.example.demo1.domain.post.controller;

import com.example.demo1.domain.global.response.BaseResponse;
import com.example.demo1.domain.post.dto.PostRequestDto;
import com.example.demo1.domain.post.dto.PostResponseDto;
import com.example.demo1.domain.post.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    // [API 1] 목록 조회
    @GetMapping
    public BaseResponse<List<PostResponseDto>> getPosts() {
        return BaseResponse.onSuccess("2000", "게시글 목록 조회 성공", postService.findAll());
    }

    // [API 2] 상세 조회 (findById 대신 getPostDetail로 수정)
    @GetMapping("/{postId}")
    public BaseResponse<PostResponseDto> getPost(@PathVariable Long postId) {
        return BaseResponse.onSuccess("2001", "게시글 조회 성공", postService.getPostDetail(postId));
    }

    // [API 3] 작성
    @PostMapping
    public BaseResponse<PostResponseDto> createPost(@Valid @RequestBody PostRequestDto requestDto) {
        return BaseResponse.onSuccess("2010", "게시글 생성 성공", postService.save(requestDto));
    }

    // [API 4] 수정
    @PutMapping("/{postId}")
    public BaseResponse<PostResponseDto> updatePost(@PathVariable Long postId, @Valid @RequestBody PostRequestDto requestDto) {
        return BaseResponse.onSuccess("2002", "게시글 수정 성공", postService.update(postId, requestDto));
    }

    // [API 5] 삭제
    @DeleteMapping("/{postId}")
    public BaseResponse<String> deletePost(@PathVariable Long postId) {
        postService.delete(postId);
        return BaseResponse.onSuccess("2003", "게시글 삭제 성공", "삭제된 게시글 ID: " + postId);
    }
    // domain/post/controller/PostController.java (기존 파일에 추가)
    @PatchMapping("/{postId}/hide")
    public BaseResponse<String> hidePost(@PathVariable Long postId) {
        postService.hidePost(postId); // postStatus를 HIDDEN으로 바꾸는 로직
        return BaseResponse.onSuccess("POST200", "게시글 숨김 처리가 완료되었습니다.", null);
    }
}