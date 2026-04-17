package com.sky.mapper;

import com.sky.entity.ShoppingCart;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ShoppingCartMapper {
    List<ShoppingCart> list(ShoppingCart shoppingCart);

    void updateShoppingCart(ShoppingCart shoppingCart);

    void insert(ShoppingCart shoppingCart);

    @Select("select * from shopping_cart where user_id = #{userId}")
    List<ShoppingCart> getShoppingCartList(Long userId);

    @Delete("delete from shopping_cart where dish_id=#{dishId} and user_id=#{userId}")
    void deleteByDishId(@Param("dishId") Long dishId,
                        @Param("userId") Long userId);

    @Delete("delete from shopping_cart where setmeal_id=#{setmealId} and user_id=#{userId}")
    void deleteBySetmealId(@Param("setmealId") Long setmealId,
                           @Param("userId") Long userId);

    @Delete("delete from shopping_cart where user_id=#{userId}")
    void clearShoppingCart(Long userId);
}