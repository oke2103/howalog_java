package com.howalog.request;

import com.howalog.exception.InvalidException;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class PostCreate {

    @NotBlank(message = "타이틀을 입력해 주세요")
    private String title;
    @NotBlank(message = "컨텐츠를 입력해 주세요")
    private String content;

    @Builder
    public PostCreate(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public void isValid() {
        if (title.contains("ㅅㅂ")) {
            throw new InvalidException("title", "제목에 욕설을 포함할 수 없습니다.");
        }
    }
}
