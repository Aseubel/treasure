package com.aseubel.treasure.controller;

import com.aseubel.treasure.common.Result;
import com.aseubel.treasure.entity.Tag;
import com.aseubel.treasure.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tags")
public class TagController {

    @Autowired
    private TagService tagService;

    // 获取所有标签 (可以考虑根据用户过滤)
    @GetMapping
    public Result<List<Tag>> getAllTags() {
        // TODO: 实际应用中可能需要根据用户 ID 查询
        List<Tag> tags = tagService.list();
        return Result.success(tags);
    }

    // 根据 ID 获取标签
    @GetMapping("/{id}")
    public Result<Tag> getTagById(@PathVariable Long id) {
        Tag tag = tagService.getById(id);
        if (tag != null) {
            // TODO: 校验是否属于当前用户
            return Result.success(tag);
        } else {
            return Result.error(404, "标签未找到");
        }
    }

    // 创建标签
    @PostMapping
    public Result<Tag> createTag(@RequestBody Tag tag) {
        // TODO: 设置 userId 为当前登录用户 ID
        // tag.setUserId(getCurrentUserId());
        // TODO: 校验标签名在当前用户下是否唯一
        boolean success = tagService.save(tag);
        if (success) {
            return Result.success("标签创建成功", tag);
        } else {
            return Result.error("标签创建失败");
        }
    }

    // 更新标签
    @PutMapping("/{id}")
    public Result<Tag> updateTag(@PathVariable Long id, @RequestBody Tag tag) {
        // TODO: 校验该标签是否属于当前用户
        tag.setTagId(id);
        boolean success = tagService.updateById(tag);
        if (success) {
            return Result.success("标签更新成功", tag);
        } else {
            return Result.error(404, "标签更新失败或标签未找到");
        }
    }

    // 删除标签
    @DeleteMapping("/{id}")
    public Result<Void> deleteTag(@PathVariable Long id) {
        // TODO: 校验该标签是否属于当前用户
        boolean success = tagService.removeById(id);
        if (success) {
            // TODO: 可能需要同时删除 collection_tags 表中的关联记录
            return Result.success();
        } else {
            return Result.error(404, "标签删除失败或标签未找到");
        }
    }
}