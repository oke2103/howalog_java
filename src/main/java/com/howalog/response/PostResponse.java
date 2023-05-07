package com.howalog.response;

import com.howalog.domain.Post;
import lombok.Builder;
import lombok.Getter;

@Getter
public class PostResponse {

    private Long id;
    private String title;
    private String content;

    public PostResponse(Post post) {
        this.id= post.getId();
        this.title = post.getTitle();
        this.content = post.getContent();
    }

    @Builder
    public PostResponse(Long id, String title, String content) {
        this.id = id;
        this.title = title;
        this.content = content;
    }

}
