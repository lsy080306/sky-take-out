package com.sky.service;

import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.result.PageResult;

import java.util.List;

public interface CategoryService {
    void addCategory(CategoryDTO categoryDTO);

    PageResult pageQuery(CategoryPageQueryDTO categoryPageQueryDTO);

    void updateCategoryStatus(Integer status, Long id);

    List<Category> getCategoryByType(Long type);

    void updateCategory(CategoryDTO categoryDTO);

    void deleteCategoryById(Long id);
}
