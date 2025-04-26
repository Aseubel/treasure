package com.aseubel.treasure.common;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.Data;

import java.util.List;

@Data
public class PageResult<T> {
    private long currentPage; // 当前页码
    private long pageSize; // 每页数量
    private long totalPages; // 总页数
    private long totalCount; // 总记录数
    private List<T> list; // 当前页数据列表

    /**
     * 从 MyBatis Plus 的 IPage 对象转换
     * 
     * @param page IPage 对象
     * @param <E>  数据类型
     * @return PageResult 对象
     */
    public static <E> PageResult<E> fromPage(IPage<E> page) {
        PageResult<E> result = new PageResult<>();
        result.setCurrentPage(page.getCurrent());
        result.setPageSize(page.getSize());
        result.setTotalPages(page.getPages());
        result.setTotalCount(page.getTotal());
        result.setList(page.getRecords());
        return result;
    }
}