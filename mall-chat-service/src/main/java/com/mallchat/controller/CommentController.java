package com.mallchat.controller;

import com.mallchat.common.Result;
import com.mallchat.common.PageResult;
import com.mallchat.dto.CommentCreateRequest;
import com.mallchat.exception.BusinessException;
import com.mallchat.exception.ForbiddenException;
import com.mallchat.model.Comment;
import com.mallchat.security.UserContext;
import com.mallchat.service.CommentService;
import com.mallchat.vo.CommentItem;
import com.mallchat.vo.ContentCreateResult;
import com.mallchat.vo.MyCommentItem;
import java.util.List;
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
@RequestMapping("/comments")
public class CommentController {
    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    /** TODO: update docs. */
    @GetMapping
    public Result<List<CommentItem>> list(@RequestParam Long postId) {
        return Result.ok(commentService.listByPostId(postId));
    }

    /** TODO: update docs. */
    @GetMapping("/me")
    public Result<PageResult<MyCommentItem>> myComments(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        Long userId = UserContext.getUserId();
        return Result.ok(commentService.listByUserId(userId, page, size));
    }

    /** TODO: update docs. */
    @PostMapping
    public Result<ContentCreateResult> create(@Valid @RequestBody CommentCreateRequest request) {
        Long userId = UserContext.getUserId();
        request.setUserId(userId);
        return Result.ok(commentService.create(request));
    }

    /** TODO: update docs. */
    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        Long userId = UserContext.getUserId();
        Comment comment = commentService.findById(id);
        if (comment == null) {
            throw new BusinessException("评论不存在");
        }
        if (!userId.equals(comment.getUserId())) {
            throw new ForbiddenException("无权限删除他人评论");
        }
        return Result.ok(commentService.deleteById(id));
    }
}
