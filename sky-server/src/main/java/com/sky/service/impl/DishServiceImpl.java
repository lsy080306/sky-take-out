package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Slf4j
@Transactional(rollbackFor = Exception.class)
@Service
public class DishServiceImpl implements DishService {
    @Autowired
    private DishMapper dishMapper;

    @Autowired
    private DishFlavorMapper dishFlavorMapper;

    /**
     * 新增菜品
     * @param dishDTO
     */
    @Override
    public void addDish(DishDTO dishDTO) {
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO,dish);
        dishMapper.addDish(dish);
        Long id=dish.getId();
        List<DishFlavor> list=dishDTO.getFlavors();
        //批量写入数据库
        if (list != null && !list.isEmpty()) {
            for (DishFlavor dishFlavor : list) {
                dishFlavor.setDishId(id);
            }
            dishFlavorMapper.addDishFlavor(list); // ⭐ 推荐批量
        }
    }

    /**
     * 分页查询菜品
     * @param dishPageQueryDTO
     * @return
     */
    @Override
    public PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO) {
        log.info("分页查询菜品");
        PageHelper.startPage(dishPageQueryDTO.getPage(),dishPageQueryDTO.getPageSize());
        Page<DishVO> page=dishMapper.pageQuery(dishPageQueryDTO);
        return new PageResult(page.getTotal(),page.getResult());
    }

    /**
     * 根据id查询菜品
     * @param id
     * @return
     */
    @Override
    public DishVO getDishById(Integer id) {
        log.info("根据id查询菜品");
        DishVO dishVO=dishMapper.getDishById(id);
        dishVO.setFlavors(dishFlavorMapper.getFlavorById(id));
        return dishVO;
    }

    /**
     * 根据分类id查询菜品
     *
     * @param categoryId
     * @return
     */
    @Override
    public List<Dish> getDishByCategoryId(Integer categoryId) {
        log.info("根据分类id查询菜品");
        List<Dish> dish=dishMapper.getDishByCategoryId(categoryId);
        return dish;
    }

    /**
     * 更新售卖状态
     * @param status
     * @param id
     */
    @Override
    public void updateStatus(Integer status, Long id) {
        log.info("更新{}售卖状态",id);
        Dish dish=new Dish();
        dish.setStatus(status);
        dish.setId(id);
        BeanUtils.copyProperties(dish,dish);
        dishMapper.updateDishInfo(dish);
    }

    /**
     * 更新菜品
     * @param dishDTO
     */
    @Override
    public void updateDish(DishDTO dishDTO) {
        log.info("更新菜品{}",dishDTO);
        Dish dish=new Dish();
        BeanUtils.copyProperties(dishDTO,dish);
        dishMapper.updateDishInfo(dish);
        dishFlavorMapper.deleteFlavor(Collections.singletonList(dish.getId()));
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if (flavors != null && !flavors.isEmpty()) {
            for (DishFlavor dishFlavor : flavors) {
                dishFlavor.setDishId(dish.getId());
            }
            dishFlavorMapper.addDishFlavor(flavors);
        }
    }

    /**
     * 批量删除
     * @param ids
     */
    @Override
    public void deleteDish(List<Long> ids) {
        log.info("批量删除");
        dishMapper.deleteDish(ids);
        dishFlavorMapper.deleteFlavor(ids);
    }
}
