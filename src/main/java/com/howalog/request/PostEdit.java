package com.howalog.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;

@Getter
public class PostEdit {

    @NotBlank(message = "타이틀을 입력해 주세요")
    private String title;
    @NotBlank(message = "컨텐츠를 입력해 주세요")
    private String content;

    @Builder
    public PostEdit(String title, String content) {
        this.title = title;
        this.content = content;
    }
}
