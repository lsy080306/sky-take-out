package com.sky.controller.admin;

import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/category")
@Slf4j
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    /**
     * 新增分类
     * @param categoryDTO
     * @return
     */
    @PostMapping
    public Result addCategory(@RequestBody CategoryDTO categoryDTO) {
        log.info("新增分类{}",categoryDTO);
        categoryService.addCategory(categoryDTO);
        return Result.success();
    }

    /**
     * 分页查询分类
     * @param categoryPageQueryDTO
     * @return
     */
    @GetMapping("/page")
    public Result<PageResult> pageQuery(CategoryPageQueryDTO categoryPageQueryDTO) {
        log.info("分页查询分类{}",categoryPageQueryDTO);
        PageResult pageResult=categoryService.pageQuery(categoryPageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 修改分类启用状态
     * @param status
     * @param id
     * @return
     */
    @PostMapping("/status/{status}")
    public Result updateCategoryStatus(@PathVariable Integer status,Long id) {
        log.info("修改分类{}启用状态{}",id,status);
        categoryService.updateCategoryStatus(status,id);
        return Result.success();
    }

    /**
     * 根据类型查询分类
     * @return
     */
    @GetMapping("/list")
    public Result<List<Category>> getCategoryByType(Long type) {
        log.info("根据类型查询分类{}",type);
        List<Category> list=categoryService.getCategoryByType(type);
        return Result.success(list);
    }

    /**
     * 修改分类
     * @param categoryDTO
     * @return
     */
    @PutMapping
    public Result updateCategory(@RequestBody CategoryDTO categoryDTO) {
        log.info("修改分类{}",categoryDTO);
        categoryService.updateCategory(categoryDTO);
        return Result.success();
    }

    /**
     * 根据id删除分类
     * @param id
     * @return
     */
    @DeleteMapping
    public Result deleteCategoryById(Long id) {
        log.info("根据id删除分类{}",id);
        categoryService.deleteCategoryById(id);
        return  Result.success();
    }
}
