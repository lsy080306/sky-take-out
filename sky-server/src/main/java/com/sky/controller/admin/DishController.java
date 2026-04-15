package com.sky.controller.admin;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * 菜品管理控制器
 * 提供菜品的增删改查、状态更新等功能
 */
@RestController
@RequestMapping("/admin/dish")
@Slf4j
public class DishController {
    // 菜品服务接口
    @Autowired
    private DishService dishService;

    // Redis模板，用于缓存操作
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 新增菜品
     *
     * @param dishDTO 菜品数据传输对象
     * @return 返回操作结果
     */
    @PostMapping
    @CacheEvict(cacheNames = "dishCache", key = "#dishDTO.categoryId")
    public Result addDish(@RequestBody DishDTO dishDTO) {
        log.info("新增菜品");
        dishService.addDish(dishDTO);
        return Result.success();
    }

    /**
     * 分页查询菜品
     *
     * @param dishPageQueryDTO 菜品分页查询条件
     * @return 返回分页查询结果
     */
    @GetMapping("/page")
    public Result pageQuery(DishPageQueryDTO dishPageQueryDTO) {
        log.info("分页查询菜品");
        PageResult pageResult = dishService.pageQuery(dishPageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 根据id查询菜品
     *
     * @param id 菜品ID
     * @return 返回菜品详情
     */
    @GetMapping("/{id}")
    public Result getDishById(@PathVariable Integer id) {
        log.info("根据id查询菜品");
        DishVO dishVO = dishService.getDishById(id);
        return Result.success(dishVO);
    }

    /**
     * 根据分类id查询菜品
     *
     * @param categoryId 分类ID
     * @return 返回该分类下的所有菜品
     */
    @GetMapping("/list")
    public Result getDishByCategoryId(Integer categoryId) {
        log.info("根据分类id查询菜品");
        List<Dish> dish = dishService.getDishByCategoryId(categoryId);
        return Result.success(dish);
    }

    /**
     * 更新售卖状态
     *
     * @param status
     * @param id
     * @return
     */
    @PostMapping("/status/{status}")
    @CacheEvict(cacheNames = "dishCache", allEntries = true)
    public Result updateStatus(@PathVariable Integer status, Long id) {
        log.info("修改售卖状态");
        dishService.updateStatus(status, id);
        return Result.success();
    }

    /**
     * 更新菜品
     *
     * @param dishDTO
     * @return
     */
    @PutMapping
    @CacheEvict(cacheNames = "dishCache", allEntries = true)
    public Result updateDish(@RequestBody DishDTO dishDTO) {
        log.info("更新菜品");
        dishService.updateDish(dishDTO);
        return Result.success();
    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @DeleteMapping
    @CacheEvict(cacheNames = "dishCache", allEntries = true)
    public Result deleteDish(List<Long> ids) {
        log.info("批量删除");
        dishService.deleteDish(ids);
        return Result.success();
    }
}
