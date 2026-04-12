package com.sky.service;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.vo.DishVO;

import java.util.List;

public interface DishService {
    void addDish(DishDTO dishDTO);

    PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO);

    DishVO getDishById(Integer id);

    List<Dish> getDishByCategoryId(Integer categoryId);

    void updateStatus(Integer status, Long id);

    void updateDish(DishDTO dishDTO);

    void deleteDish(List<Long> ids);
}
