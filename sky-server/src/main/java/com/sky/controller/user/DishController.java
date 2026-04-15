package com.sky.controller.user;

import com.sky.constant.StatusConstant;
import com.sky.entity.Dish;
import com.sky.result.Result;
import com.sky.service.DishService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController("userDishController")
@RequestMapping("/user/dish")
@Slf4j
@Api(tags = "C端-菜品浏览接口")
public class DishController {
    @Autowired
    private DishService dishService;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 根据分类id查询菜品
     *
     * @param categoryId 分类ID
     * @return 返回菜品列表
     */
    @GetMapping("/list")
    @ApiOperation("根据分类id查询菜品")
    @Cacheable(cacheNames = "dishCache",key = "#categoryId")
    public Result<List<Dish>> list(Integer categoryId) {

    // 记录查询日志，包含分类id
        log.info("根据分类id查询菜品,categoryId:{}", categoryId);

//    // 构造Redis的键
//        String key="dish_" + categoryId;
//    // 尝试从Redis缓存中获取菜品列表
//        List<Dish> list=(List<Dish>) redisTemplate.opsForValue().get(key);
//    // 如果缓存中存在数据且不为空，则直接返回
//        if(list!=null&& !list.isEmpty()){
//            return Result.success(list);
//        }

    // 如果缓存中没有数据，则从数据库查询
    // 创建Dish对象并设置查询条件
        Dish dish = new Dish();
        dish.setCategoryId(Long.valueOf(categoryId));
        dish.setStatus(StatusConstant.ENABLE);//查询起售中的菜品
        List<Dish>list = dishService.getDishByCategoryId(categoryId);
        return Result.success(list);
    }

}
