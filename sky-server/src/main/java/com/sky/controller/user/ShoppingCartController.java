package com.sky.controller.user;

import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;
import com.sky.result.Result;
import com.sky.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 购物车控制器，处理购物车相关的HTTP请求
 * 提供添加商品、获取购物车列表、减少商品数量和清空购物车等功能
 */
@RestController
@RequestMapping("/user/shoppingCart")
@Slf4j
public class ShoppingCartController {
    // 自动注入购物车服务，用于处理业务逻辑
    @Autowired
    private ShoppingCartService shoppingCartService;

    /**
     * 添加购物车
     * @param shoppingCartDTO 购物车数据传输对象，包含商品信息、数量等
     * @return 返回操作结果，包含状态码和信息
     */
    @PostMapping("/add")
    public Result addShoppingCart(@RequestBody ShoppingCartDTO shoppingCartDTO){
    // 记录添加购物车的日志信息，包含传入的购物车数据
        log.info("添加购物车{}",shoppingCartDTO);
    // 调用服务层的方法，将购物车数据添加到数据库中
        shoppingCartService.addShoppingCart(shoppingCartDTO);
    // 返回操作成功的结果
        return Result.success();
    }


    /**
     * 获取购物车列表
     * @return 返回购物车列表
     */
    @GetMapping("/list")
    public Result getShoppingCartList(){
        // 记录获取购物车列表的日志信息
        log.info("获取购物车列表");
        // 调用服务层方法获取购物车列表
        List<ShoppingCart> list=shoppingCartService.getShoppingCartList();
        // 返回购物车列表
        return Result.success(list);
    }

    /**
     * 减少购物车
     * @param shoppingCartDTO 购物车数据传输对象，包含商品信息、数量等
     * @return 返回操作结果，包含状态码和信息
     */
    @PostMapping("/sub")
    public Result subShoppingCart(@RequestBody ShoppingCartDTO shoppingCartDTO){
        // 记录减少购物车的日志信息，包含传入的购物车数据
        log.info("减少购物车{}",shoppingCartDTO);
        // 调用服务层方法减少购物车商品数量
        shoppingCartService.subShoppingCart(shoppingCartDTO);
        // 返回操作成功的结果
        return Result.success();
    }

    /**
     * 清空购物车
     * @return 返回操作结果，包含状态码和信息
     */
    @DeleteMapping("/clean")
    public Result clearShoppingCart(){
        // 记录清空购物车的日志信息
        log.info("清空购物车");
        // 调用服务层方法清空购物车
        shoppingCartService.clearShoppingCart();
        // 返回操作成功的结果
        return Result.success();
    }
}
