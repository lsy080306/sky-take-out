package com.sky.service;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.result.PageResult;
import com.sky.vo.DishItemVO;
import com.sky.vo.SetmealVO;

import java.util.List;

public interface SetmealService {
    PageResult pageQuery(SetmealPageQueryDTO setmealPageQueryDTO);

    void addSetmeal(SetmealDTO setmealDTO);

    SetmealVO getSetmealById(Integer id);

    void updateStatus(Integer status, Long id);

    void deleteSetmeal(List<Long> ids);

    void updateSetmeal(SetmealDTO setmealDTO);

    List<DishItemVO> getDishItemById(Long id);

    List<Setmeal> list(Setmeal setmeal);
}
