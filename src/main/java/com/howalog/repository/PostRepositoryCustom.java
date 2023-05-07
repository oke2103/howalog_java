package com.howalog.repository;

import com.howalog.domain.Post;
import com.howalog.request.PostSearch;

import java.util.List;

public interface PostRepositoryCustom {

    List<Post> getList(PostSearch postSearch);
}
