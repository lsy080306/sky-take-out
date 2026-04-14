package com.sky.controller.admin;

import com.sky.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.web.bind.annotation.*;

@RestController("adminShopController")
@RequestMapping("/admin/shop")
@Slf4j
public class ShopController {

    public static final String KEY="SHOP_STATUS";

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 修改店铺营业状态
     * @param status
     * @return
     */
    @PutMapping("/{status}")
    public Result updateShopStatus(@PathVariable String status) {
        log.info("修改店铺营业状态{}", status);
        ValueOperations ops = redisTemplate.opsForValue();
        ops.set(KEY,status);
        return Result.success();
    }

    /**
     * 获取营业状态
     * @return
     */
    @GetMapping("/status")
    public Result getShopStatus() {
        log.info("获取营业状态");
        ValueOperations ops = redisTemplate.opsForValue();
        Integer status = Integer.parseInt((String) ops.get(KEY));
        return Result.success(status);
    }
}
