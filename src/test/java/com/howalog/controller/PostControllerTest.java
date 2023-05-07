package com.howalog.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.howalog.domain.Post;
import com.howalog.repository.PostRepository;
import com.howalog.request.PostCreate;
import com.howalog.request.PostEdit;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class PostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @AfterEach
    void afterEach() {
        postRepository.deleteAll();
    }

    @Test
    @DisplayName("/posts 호출시 정상 동작")
    void postSuccess() throws Exception {
        // given
        PostCreate request = PostCreate.builder()
                .title("제목")
                .content("내용")
                .build();
        String json = objectMapper.writeValueAsString(request);

        System.out.println("json = " + json);

        // expected
        mockMvc.perform(post("/posts")
                        .contentType(APPLICATION_JSON)
                        .content(json)
                )
                .andExpect(status().isOk())
                .andExpect(content().string(""))
                .andDo(print());
    }

    @Test
    @DisplayName("/posts 요청 시 title은 필수다")
    void postRequiredTitle() throws Exception {
        // given
        PostCreate request = PostCreate.builder()
                .content("내용")
                .build();
        String json = objectMapper.writeValueAsString(request);

        // expected
        mockMvc.perform(post("/posts")
                        .contentType(APPLICATION_JSON)
                        .content(json)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.message").value("잘못된 요청입니다."))
                .andExpect(jsonPath("$.validation.title").value("타이틀을 입력해 주세요"))
                .andDo(print());

    }

    @Test
    @DisplayName("/posts 요청 시 DB에 값이 저장된다.")
    void postDbSave() throws Exception {
        // given
        PostCreate request = PostCreate.builder()
                .title("제목")
                .content("내용")
                .build();
        String json = objectMapper.writeValueAsString(request);

        // when
        mockMvc.perform(post("/posts")
                        .contentType(APPLICATION_JSON)
                        .content(json)
                )
                .andExpect(status().isOk())
                .andDo(print());

        // then
        assertThat(postRepository.count()).isEqualTo(1);
        Post post = postRepository.findAll().get(0);
        assertThat(post.getTitle()).isEqualTo(request.getTitle());
        assertThat(post.getContent()).isEqualTo(request.getContent());
    }

    @Test
    @DisplayName("글 한개 조회")
    void getPost() throws Exception {
        // given
        Post post = Post.builder()
                .title("제목입니다")
                .content("내용입니다")
                .build();
        postRepository.save(post);

        // expected
        mockMvc.perform(get("/posts/{postId}", post.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(post.getId()))
                .andExpect(jsonPath("$.title").value("제목입니다"))
                .andExpect(jsonPath("$.content").value("내용입니다"))
                .andDo(print());

    }

    @Test
    @DisplayName("글 여러개 조회")
    void getPostList() throws Exception {
        // given
        List<Post> requestPosts = IntStream.range(1, 31)
                .mapToObj(i -> Post.builder()
                        .title("title_" + i)
                        .content("content_" + i)
                        .build())
                .collect(Collectors.toList());
        postRepository.saveAll(requestPosts);

        // expected
        mockMvc.perform(get("/posts?page=1&size=10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("title_30"))
                .andExpect(jsonPath("$[9].title").value("title_21"))
                .andDo(print());
    }

    @Test
    @DisplayName("페이지를 0으로 요청하면 첫 페이지가 나온다")
    void getPostFirstPage() throws Exception {
        // given
        List<Post> requestPosts = IntStream.range(1, 31)
                .mapToObj(i -> Post.builder()
                        .title("title_" + i)
                        .content("content_" + i)
                        .build())
                .collect(Collectors.toList());
        postRepository.saveAll(requestPosts);

        // expected
        mockMvc.perform(get("/posts?page=0&size=10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("title_30"))
                .andExpect(jsonPath("$[9].title").value("title_21"))
                .andDo(print());
    }

    @Test
    @DisplayName("글 제목 수정")
    void editPostTitle() throws Exception {
        // given
        Post post = Post.builder()
                .title("before_title")
                .content("before_content")
                .build();
        postRepository.save(post);

        PostEdit postEdit = PostEdit.builder()
                .title("after_title")
                .build();
        String json = objectMapper.writeValueAsString(postEdit);

        // expected
        mockMvc.perform(patch("/posts/{postId}", post.getId())
                        .contentType(APPLICATION_JSON)
                        .content(json)
                )
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("글 내용 수정")
    void editPostContent() throws Exception {
        // given
        Post post = Post.builder()
                .title("before_title")
                .content("before_content")
                .build();
        postRepository.save(post);

        PostEdit postEdit = PostEdit.builder()
                .content("after_content")
                .build();
        String json = objectMapper.writeValueAsString(postEdit);

        // expected
        mockMvc.perform(patch("/posts/{postId}", post.getId())
                        .contentType(APPLICATION_JSON)
                        .content(json)
                )
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("글 내용 삭제")
    void deletePost() throws Exception {
        // given
        Post post = Post.builder()
                .title("before_title")
                .content("before_content")
                .build();
        postRepository.save(post);

        // expected
        mockMvc.perform(delete("/posts/{postId}", post.getId()))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("존재하지 않는 게시글 조회")
    void getNotExist() throws Exception {
        // expected
        mockMvc.perform(get("/posts/{postId}", 1L))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    @DisplayName("존재하지 않는 게시글 수정")
    void editNotExist() throws Exception {
        // given
        PostEdit postEdit = PostEdit.builder()
                .content("after_content")
                .build();
        String json = objectMapper.writeValueAsString(postEdit);

        // expected
        mockMvc.perform(patch("/posts/{postId}", 1L)
                        .contentType(APPLICATION_JSON)
                        .content(json)
                )
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    @DisplayName("게시글 작성 시 욕설은 제목으로 작성할 수 없습니다.")
    void postNotAbuse() throws Exception {
        // given
        PostCreate request = PostCreate.builder()
                .title("에라이ㅅㅂ")
                .content("내용이 왜이래")
                .build();
        String json = objectMapper.writeValueAsString(request);

        mockMvc.perform(post("/posts")
                        .contentType(APPLICATION_JSON)
                        .content(json)
                )
                .andExpect(status().isBadRequest())
                .andDo(print());
    }
}