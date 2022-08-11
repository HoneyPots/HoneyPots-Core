package com.honeypot.domain.post.service;

import com.honeypot.domain.post.entity.enums.PostType;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class PostCrudServiceFactory {

    private final Map<PostType, PostCrudService> postCrudServiceMap = new HashMap<>();

    public PostCrudServiceFactory(List<PostCrudService> postCrudServices) {
        postCrudServices.forEach(s -> postCrudServiceMap.put(s.getPostType(), s));
    }

    public PostCrudService getService(PostType postType) {
        return postCrudServiceMap.get(postType);
    }

}
