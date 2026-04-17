package com.sky.service.impl;


import com.sky.context.BaseContext;
import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.service.ShoppingCartService;
import com.sky.vo.DishVO;
import com.sky.vo.SetmealVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 购物车服务实现类
 * 实现了购物车的添加、查询、减少和清空等功能
 */
@Service
@Slf4j
public class ShoppingCartServiceImpl implements ShoppingCartService {

    // 注入购物车数据访问层
    @Autowired
    private ShoppingCartMapper shoppingCartMapping;
    // 注入菜品数据访问层
    @Autowired
    private DishMapper dishMapper;
    // 注入套餐数据访问层
    @Autowired
    private SetmealMapper setmealMapper;

    /**
     * 添加购物车
     * @param shoppingCartDTO 购物车数据传输对象，包含要添加的商品信息
     */
    @Override
    public void addShoppingCart(ShoppingCartDTO shoppingCartDTO) {
        log.info("添加购物车{}",shoppingCartDTO);
        // 创建购物车实体对象
        ShoppingCart shoppingCart=new ShoppingCart();
        // 将DTO属性复制到实体对象
        BeanUtils.copyProperties(shoppingCartDTO,shoppingCart);
        // 设置当前用户ID
        shoppingCart.setUserId(BaseContext.getCurrentId());
        // 查询购物车中是否已存在该商品
        List<ShoppingCart> list= shoppingCartMapping.list(shoppingCart);

        // 如果购物车中已存在该商品
        if(list!=null&&list.size()!=0){
            // 获取已存在的购物车项
            shoppingCart= list.get(0);
            // 数量加1
            shoppingCart.setNumber(shoppingCart.getNumber()+1);
            // 更新购物车
            shoppingCartMapping.updateShoppingCart(shoppingCart);
        }else{
            // 如果商品不存在，获取商品ID
            Long dishId= shoppingCartDTO.getDishId();
            Long setmealId= shoppingCartDTO.getSetmealId();
            // 如果是菜品
            if(dishId!=null){
                // 根据ID获取菜品信息
                DishVO dish=dishMapper.getDishById(Math.toIntExact(dishId));
                // 设置菜品名称、图片和价格
                shoppingCart.setName(dish.getName());
                shoppingCart.setImage(dish.getImage());
                shoppingCart.setAmount(dish.getPrice());
            }else{
                // 如果是套餐
                SetmealVO setmealVO=setmealMapper.getSetmealById(Math.toIntExact(setmealId));
                // 设置套餐名称、图片和价格
                shoppingCart.setName(setmealVO.getName());
                shoppingCart.setImage(setmealVO.getImage());
                shoppingCart.setAmount(setmealVO.getPrice());
            }
            // 设置数量为1
            shoppingCart.setNumber(1);
            // 设置创建时间
            shoppingCart.setCreateTime(LocalDateTime.now());
            // 插入新购物车项
            shoppingCartMapping.insert(shoppingCart);
        }
    }

    /**
     * 获取购物车列表
     * @return 返回当前用户的购物车列表
     */
    @Override
    public List<ShoppingCart> getShoppingCartList() {
        log.info("获取购物车列表");
        // 获取当前用户ID
        Long userId=BaseContext.getCurrentId();
        // 返回用户的购物车列表
        return shoppingCartMapping.getShoppingCartList(userId);
    }

    /**
     * 减少购物车中的商品数量
     * @param shoppingCartDTO 购物车数据传输对象，包含要减少的商品信息
     */
    @Override
    public void subShoppingCart(ShoppingCartDTO shoppingCartDTO) {
        log.info("减少购物车{}",shoppingCartDTO);
        // 创建购物车实体对象
        ShoppingCart shoppingCart=new ShoppingCart();
        // 将DTO属性复制到实体对象
        BeanUtils.copyProperties(shoppingCartDTO,shoppingCart);
        // 获取当前用户ID
        Long userId=BaseContext.getCurrentId();
        // 设置用户ID
        shoppingCart.setUserId(userId);
        // 查询购物车中是否已存在该商品
        List<ShoppingCart> list= shoppingCartMapping.list(shoppingCart);
        // 获取查询结果的第一项
        shoppingCart=list.get(0);
        // 获取商品ID
        Long dishId=shoppingCartDTO.getDishId();
        Long setmealId=shoppingCartDTO.getSetmealId();
        // 如果商品数量为1，则删除该商品
        if(shoppingCart.getNumber()==1){
            if(dishId!=null){
                // 根据菜品ID和用户ID删除
                shoppingCartMapping.deleteByDishId(dishId,userId);
            }else{
                // 根据套餐ID和用户ID删除
                shoppingCartMapping.deleteBySetmealId(setmealId,userId);
            }
        }else{
            // 如果数量大于1，则数量减1
            shoppingCart.setNumber(shoppingCart.getNumber()-1);
            // 更新购物车
            shoppingCartMapping.updateShoppingCart(shoppingCart);
        }
    }

    /**
     * 清空购物车
     * 删除当前用户的所有购物车项
     */
    @Override
    public void clearShoppingCart() {
        log.info("清空购物车");
        // 获取当前用户ID
        Long userId=BaseContext.getCurrentId();
        // 清空用户的购物车
        shoppingCartMapping.clearShoppingCart(userId);
    }
}
