package com.aseubel.treasure.controller;

import com.aseubel.treasure.common.PageResult;
import com.aseubel.treasure.common.Result;
import com.aseubel.treasure.dto.CollectionDTO; // 导入 DTO
import com.aseubel.treasure.entity.Collection;
import com.aseubel.treasure.service.CollectionService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/collections")
public class CollectionController {

    @Autowired
    private CollectionService collectionService;

    // 获取所有藏品 (支持分页)
    @GetMapping
    public Result<PageResult<Collection>> getAllCollections(
            @RequestParam(defaultValue = "1") long pageNum, // 页码，默认为 1
            @RequestParam(defaultValue = "10") long pageSize // 每页数量，默认为 10
    // @RequestParam(required = false) Long userId // 如果需要根据用户过滤，添加此参数
    ) {
        // TODO: 实际应用中应从认证信息中获取 userId，而不是通过请求参数传递
        Long currentUserId = null; // 暂时设为 null，后续从认证获取

        // 创建 MyBatis Plus 的 Page 对象
        Page<Collection> page = new Page<>(pageNum, pageSize);

        // 调用 Service 层进行分页查询
        IPage<Collection> pageData = collectionService.getCollectionsByPage(page, currentUserId);

        // 将 IPage 转换为自定义的 PageResult
        PageResult<Collection> pageResult = PageResult.fromPage(pageData);

        return Result.success(pageResult);
    }

    // 根据 ID 获取藏品 (返回带标签的 DTO)
    @GetMapping("/{id}")
    public Result<CollectionDTO> getCollectionById(@PathVariable Long id) {
        CollectionDTO collectionDTO = collectionService.getCollectionWithTags(id); // 调用新方法
        if (collectionDTO != null) {
            // TODO: 校验是否属于当前用户
            return Result.success(collectionDTO);
        } else {
            return Result.error(404, "藏品未找到");
        }
    }

    // 创建藏品
    @PostMapping
    public Result<Collection> createCollection(@RequestBody Collection collection) {
        // TODO: 设置 userId 为当前登录用户 ID
        // collection.setUserId(getCurrentUserId());
        boolean success = collectionService.save(collection);
        if (success) {
            return Result.success("藏品创建成功", collection);
        } else {
            return Result.error("藏品创建失败");
        }
    }

    // 更新藏品 (不处理标签，标签通过专门接口管理)
    @PutMapping("/{id}")
    public Result<Collection> updateCollection(@PathVariable Long id, @RequestBody Collection collection) {
        // TODO: 校验该藏品是否属于当前用户
        collection.setCollectionId(id);
        // 注意：这个更新不包括标签信息，标签需要单独接口处理
        boolean success = collectionService.updateById(collection);
        if (success) {
            return Result.success("藏品更新成功", collection);
        } else {
            return Result.error(404, "藏品更新失败或藏品未找到");
        }
    }

    // 删除藏品 (Service 实现已包含删除关联标签的逻辑)
    @DeleteMapping("/{id}")
    public Result<Void> deleteCollection(@PathVariable Long id) {
        // TODO: 校验该藏品是否属于当前用户
        boolean success = collectionService.removeById(id); // 调用重写后的 removeById
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
     * @param id     藏品ID
     * @param tagIds 请求体中包含要添加的标签ID列表，例如：[1, 2, 3]
     * @return Result
     */
    @PostMapping("/{id}/tags")
    public Result<?> addTagsToCollection(@PathVariable Long id, @RequestBody List<Long> tagIds) {
        // TODO: 校验藏品和标签是否属于当前用户
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
     * @param id     藏品ID
     * @param tagIds 请求体中包含要移除的标签ID列表，例如：[1, 2]
     * @return Result
     */
    @DeleteMapping("/{id}/tags")
    public Result<?> removeTagsFromCollection(@PathVariable Long id, @RequestBody List<Long> tagIds) {
        // TODO: 校验藏品是否属于当前用户
        boolean success = collectionService.removeTagsFromCollection(id, tagIds);
        if (success) {
            return Result.success("标签移除成功");
        } else {
            return Result.error("标签移除失败");
        }
    }

}