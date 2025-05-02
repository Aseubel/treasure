package com.aseubel.treasure.controller;

import cn.hutool.core.util.ObjectUtil;
import com.aseubel.treasure.common.PageResult;
import com.aseubel.treasure.common.Result;
import com.aseubel.treasure.dto.collection.*;
import com.aseubel.treasure.entity.Collection;
import com.aseubel.treasure.service.CollectionService;
import com.aseubel.treasure.service.UserService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin("*")
@RestController
@RequestMapping("/collections")
public class CollectionController {

    @Autowired
    private CollectionService collectionService;

    @Autowired
    private UserService userService;

    /**
     * 获取所有藏品信息
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return Result
     */
    @GetMapping
    public Result<PageResult<Collection>> getAllCollections(
            @RequestParam(defaultValue = "1") long pageNum, // 页码，默认为 1
            @RequestParam(defaultValue = "10") long pageSize // 每页数量，默认为 10
    // @RequestParam(required = false) Long userId // 如果需要根据用户过滤，添加此参数
    ) {
        Long currentUserId = userService.getUserId();

        // 创建 MyBatis Plus 的 Page 对象
        Page<Collection> page = new Page<>(pageNum, pageSize);

        // 调用 Service 层进行分页查询
        IPage<Collection> pageData = collectionService.getCollectionsByPage(page, currentUserId);

        // 将 IPage 转换为自定义的 PageResult
        PageResult<Collection> pageResult = PageResult.fromPage(pageData);

        return Result.success(pageResult);
    }

    /**
     * 获取藏品信息
     * @param id 藏品 ID
     * @return Result
     */
    @GetMapping("/{id}")
    public Result<CollectionDTO> getCollectionById(@PathVariable Long id) {
        CollectionDTO collectionDTO = collectionService.getCollectionWithTags(id); // 调用新方法
        if (collectionDTO != null) {
            if (!validate(id, userService.getUserId())) {
                return Result.error(403, "无权操作");
            }
            return Result.success(collectionDTO);
        } else {
            return Result.error(404, "藏品未找到");
        }
    }

    /**
     * 创建藏品
     * @param request 藏品信息
     * @return Result
     */
    @PostMapping
    public Result<Collection> createCollection(@RequestBody CreateCollectionRequest request) {
        Collection collection = request.convertToEntity();
        collection.setUserId(userService.getUserId());
        boolean success = collectionService.save(collection);
        if (success) {
            return Result.success("藏品创建成功", collection);
        } else {
            return Result.error("藏品创建失败");
        }
    }

    /**
     * 更新藏品
     * 注意：更新时不包括标签信息，标签需要单独接口处理
     * @param request 藏品信息
     * @return Result
     */
    @PutMapping("")
    public Result<Collection> updateCollection(@RequestBody UpdateCollectionRequest request) {
        Collection collection = request.convertToEntity();
        Long id = collection.getCollectionId();

        if (!validate(id, userService.getUserId())) {
            return Result.error(403, "无权操作");
        }
        collection.setCollectionId(id);
        // 注意：这个更新不包括标签信息，标签需要单独接口处理
        boolean success = collectionService.updateById(collection);
        if (success) {
            return Result.success("藏品更新成功", collection);
        } else {
            return Result.error(404, "藏品更新失败或藏品未找到");
        }
    }

    /**
     * 删除藏品
     *
     * @param id 藏品 ID
     * @return Result
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteCollection(@PathVariable Long id) {
        if (!validate(id, userService.getUserId())) {
            return Result.error(403, "无权删除");
        }
        boolean success = collectionService.removeById(id);
        if (success) {
            return Result.success();
        } else {
            return Result.error(404, "藏品删除失败或藏品未找到");
        }
    }

    // --- 藏品标签管理接口 ---

    /**
     * 为指定藏品添加标签
     * 
     * @param request 请求，包含藏品 ID 和标签 ID 列表
     * @return Result
     */
    @PostMapping("/tags")
    public Result<?> addTagsToCollection(@RequestBody AddTagsRequest request) {
        Long id = request.getCollectionId();
        List<Long> tagIds = request.getTagIds();

        if (!validate(id, userService.getUserId())) {
            return Result.error(403, "无权操作");
        }
        boolean success = collectionService.addTagsToCollection(id, tagIds);
        if (success) {
            return Result.success("标签添加成功");
        } else {
            // 根据 Service 层实现细化错误信息
            return Result.error("标签添加失败");
        }
    }

    /**
     * 从指定藏品移除标签
     * 
     * @param request 请求，包含藏品 ID 和标签 ID 列表
     * @return Result
     */
    @DeleteMapping("/tags")
    public Result<?> removeTagsFromCollection(@RequestBody DeleteTagsRequest request) {
        Long id = request.getCollectionId();
        List<Long> tagIds = request.getTagIds();

        if (!validate(id, userService.getUserId())) {
            return Result.error(403, "无权操作");
        }
        boolean success = collectionService.removeTagsFromCollection(id, tagIds);
        if (success) {
            return Result.success("标签移除成功");
        } else {
            return Result.error("标签移除失败");
        }
    }

    /**
     * 校验该藏品是否属于当前用户
     * @param collectionId 藏品 ID
     * @param userId 用户 ID
     * @return true 属于当前用户，false 不是当前用户
     */
    private boolean validate(Long collectionId, Long userId) {
        return ObjectUtil.isNotEmpty(collectionService
                .getOne(new QueryWrapper<Collection>()
                        .eq("collection_id", collectionId)
                        .eq("user_id", userId)));
    }

}