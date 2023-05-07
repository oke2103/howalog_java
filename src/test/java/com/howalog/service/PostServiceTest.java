package com.howalog.service;

import com.howalog.domain.Post;
import com.howalog.exception.PostNotFound;
import com.howalog.repository.PostRepository;
import com.howalog.request.PostCreate;
import com.howalog.request.PostEdit;
import com.howalog.request.PostSearch;
import com.howalog.response.PostResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class PostServiceTest {

    @Autowired
    private PostService postService;
    @Autowired
    private PostRepository postRepository;

    @AfterEach
    void afterEach() {
        postRepository.deleteAll();
    }

    @Test
    @DisplayName("글 작성")
    void writePost() {
        // given
        PostCreate request = PostCreate.builder()
                .title("제목")
                .content("내용")
                .build();

        // when
        postService.write(request);

        // then
        assertThat(postRepository.count()).isEqualTo(1);
        Post post = postRepository.findAll().get(0);
        assertThat(post.getTitle()).isEqualTo(request.getTitle());
        assertThat(post.getContent()).isEqualTo(request.getContent());
    }

    @Test
    @DisplayName("글 한개 조회")
    void getPost() {
        // given
        Post post = Post.builder()
                .title("제목입니다")
                .content("내용입니다")
                .build();
        postRepository.save(post);

        // when
        PostResponse response = postService.get(post.getId());

        // then
        assertThat(response).isNotNull();
        assertThat(postRepository.count()).isEqualTo(1);
        assertThat(response.getTitle()).isEqualTo(post.getTitle());
        assertThat(response.getContent()).isEqualTo(post.getContent());
    }

    @Test
    @DisplayName("글 여러개 조회")
    void getPostList() {
        // given
        List<Post> requestPosts = IntStream.range(1, 31)
                .mapToObj(i -> Post.builder()
                        .title("title_" + i)
                        .content("content_" + i)
                        .build())
                .collect(Collectors.toList());
        postRepository.saveAll(requestPosts);

        PostSearch postSearch = PostSearch.builder()
                .page(1)
                .size(10)
                .build();

        // when
        List<PostResponse> posts = postService.getList(postSearch);

        // then
        assertThat(posts.size()).isEqualTo(10);
        assertThat(posts.get(0).getTitle()).isEqualTo("title_30");
    }

    @Test
    @DisplayName("글 제목 수정")
    void editPostTitle() {
        // given
        Post post = Post.builder()
                .title("before_title")
                .content("before_content")
                .build();
        postRepository.save(post);

        PostEdit postEdit = PostEdit.builder()
                .title("after_title")
                .build();

        // when
        postService.edit(post.getId(), postEdit);

        // then
        Post changedPost = postRepository.findById(post.getId())
                .orElseThrow(() -> new RuntimeException("글이 존재하지 않습니다. id = " + post.getId()));
        assertThat(changedPost.getTitle()).isEqualTo("after_title");
    }

    @Test
    @DisplayName("글 내용 수정")
    void editPostContent() {
        // given
        Post post = Post.builder()
                .title("before_title")
                .content("before_content")
                .build();
        postRepository.save(post);

        PostEdit postEdit = PostEdit.builder()
                .content("after_content")
                .build();

        // when
        postService.edit(post.getId(), postEdit);

        // then
        Post changedPost = postRepository.findById(post.getId())
                .orElseThrow(() -> new RuntimeException("글이 존재하지 않습니다. id = " + post.getId()));
        assertThat(changedPost.getContent()).isEqualTo("after_content");
    }

    @Test
    @DisplayName("게시글 삭제")
    void deletePost() {
        // given
        Post post = Post.builder()
                .title("title")
                .content("content")
                .build();

        postRepository.save(post);

        // when
        postService.delete(post.getId());

        // then
        assertThat(postRepository.count()).isEqualTo(0);
    }

    @Test
    @DisplayName("글 한개 조회 :: 실패")
    void getPostFail() {
        // expected
        assertThatThrownBy(() -> postService.get(1L))
                .isInstanceOf(PostNotFound.class);
    }

    @Test
    @DisplayName("게시글 삭제 :: 실패")
    void deleteFail() {
        // expected
        assertThatThrownBy(() -> postService.delete(1L))
                .isInstanceOf(PostNotFound.class);
    }

    @Test
    @DisplayName("게시글 수정 :: 실패")
    void editFail() {
        // expected
        assertThatThrownBy(() -> postService.edit(1L, null))
                .isInstanceOf(PostNotFound.class);
    }
}