package com.mallchat.controller;

import com.mallchat.common.PageResult;
import com.mallchat.common.Result;
import com.mallchat.dto.PostCreateRequest;
import com.mallchat.exception.BusinessException;
import com.mallchat.exception.ForbiddenException;
import com.mallchat.model.Post;
import com.mallchat.security.UserContext;
import com.mallchat.service.PostService;
import com.mallchat.vo.ContentCreateResult;
import com.mallchat.vo.PostDetail;
import com.mallchat.vo.PostListItem;
import javax.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** TODO: update docs. */
@RestController
@RequestMapping("/posts")
public class PostController {
    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    /** TODO: update docs. */
    @GetMapping
    public Result<PageResult<PostListItem>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) Long categoryId) {
        return Result.ok(postService.list(page, size, title, categoryId));
    }

    /** TODO: update docs. */
    @GetMapping("/me")
    public Result<PageResult<PostListItem>> myPosts(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        Long userId = UserContext.getUserId();
        return Result.ok(postService.listByUserId(userId, page, size));
    }

    /** TODO: update docs. */
    @GetMapping("/{id}")
    public Result<PostDetail> detail(@PathVariable Long id) {
        return Result.ok(postService.findById(id));
    }

    /** TODO: update docs. */
    @PostMapping
    public Result<ContentCreateResult> create(@Valid @RequestBody PostCreateRequest request) {
        Long userId = UserContext.getUserId();
        request.setUserId(userId);
        return Result.ok(postService.create(request));
    }

    /** TODO: update docs. */
    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        Long userId = UserContext.getUserId();
        Post post = postService.findByIdRaw(id);
        if (post == null) {
            throw new BusinessException("帖子不存在");
        }
        if (!userId.equals(post.getUserId())) {
            throw new ForbiddenException("无权限删除他人帖子");
        }
        return Result.ok(postService.deleteById(id));
    }
}
