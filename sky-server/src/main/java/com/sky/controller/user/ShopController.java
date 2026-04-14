package com.sky.controller.user;

import com.sky.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.web.bind.annotation.*;

@RestController("userShopController")
@RequestMapping("/user/shop")
@Slf4j
public class ShopController {

    public static final String KEY="SHOP_STATUS";

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 获取营业状态
     * @return
     */
    @GetMapping
    public Result getShopStatus() {
        log.info("获取营业状态");
        ValueOperations ops = redisTemplate.opsForValue();
        Integer status= (Integer) ops.get(KEY);
        return Result.success(status);
    }
}

