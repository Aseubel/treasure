package com.aseubel.treasure.controller;

import cn.hutool.core.util.ObjectUtil;
import com.aseubel.treasure.common.JwtUtil;
import com.aseubel.treasure.common.Result;
import com.aseubel.treasure.dto.tag.CreateTagRequest;
import com.aseubel.treasure.dto.tag.UpdateTagRequest;
import com.aseubel.treasure.entity.Tag;
import com.aseubel.treasure.service.CollectionService;
import com.aseubel.treasure.service.TagService;
import com.aseubel.treasure.service.UserService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin("*")
@RestController
@RequestMapping("/tags")
public class TagController {

    @Autowired
    private TagService tagService;

    @Autowired
    private UserService userService;

    @Autowired
    private CollectionService collectionService;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * 获取所有标签
     *
     * @return Result
     */
    @GetMapping
    public Result<List<Tag>> getAllTags() {
        Long userId = userService.getUserId();
        List<Tag> tags = tagService.list(new QueryWrapper<Tag>().eq("user_id", userId));
        return Result.success(tags);
    }

    /**
     * 获取标签详情
     *
     * @param id 标签 ID
     * @return Result
     */
    @GetMapping("/{id}")
    public Result<Tag> getTagById(@PathVariable Long id) {
        Tag tag = tagService.getById(id);
        if (ObjectUtil.isNotEmpty(tag)) {
            if (!tag.getUserId().equals(userService.getUserId())) {
                return Result.error(403, "无权访问该标签");
            }
            return Result.success(tag);
        } else {
            return Result.error(404, "标签未找到");
        }
    }

    /**
     * 创建标签
     *
     * @param request 创建标签请求
     * @return Result
     */
    @PostMapping
    public Result<Tag> createTag(@RequestBody CreateTagRequest request) {
        Tag tag = request.toEntity();
        tag.setUserId(userService.getUserId());
        if (ObjectUtil.isNotEmpty(tagService.getOne(new QueryWrapper<Tag>().eq("tag_name", tag.getTagName())))) {
            return Result.error(400, "标签名已存在");
        }
        boolean success = tagService.save(tag);
        if (success) {
            return Result.success("标签创建成功", tag);
        } else {
            return Result.error("标签创建失败");
        }
    }

    /**
     * 更新标签
     *
     * @param request 更新标签请求
     * @return Result
     */
    @PutMapping
    public Result<Tag> updateTag(@RequestBody UpdateTagRequest request) {
        Tag tag = request.toEntity();

        if (!validate(tag.getTagId(), userService.getUserId())) {
            return Result.error(403, "无权修改该标签");
        }
        boolean success = tagService.updateById(tag);
        if (success) {
            return Result.success("标签更新成功", tag);
        } else {
            return Result.error(404, "标签更新失败或标签未找到");
        }
    }

    /**
     * 删除标签
     *
     * @param id 标签 ID
     * @return Result
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteTag(@PathVariable Long id) {
        if (!validate(id, userService.getUserId())) {
            return Result.error(403, "无权删除该标签");
        }
        boolean success = tagService.removeById(id);
        if (success) {
            collectionService.removeCollectionTag(id);
            return Result.success();
        } else {
            return Result.error(404, "标签删除失败或标签未找到");
        }
    }

    /**
     * 校验该标签是否属于当前用户
     * @param tagId 标签 ID
     * @param userId 用户 ID
     * @return true 属于当前用户，false 不是当前用户
     */
    private boolean validate(Long tagId, Long userId) {
        return ObjectUtil.isNotEmpty(tagService
                .getOne(new QueryWrapper<Tag>()
                        .eq("tag_id", tagId)
                        .eq("user_id", userId)));
    }
}