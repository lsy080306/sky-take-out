package com.sky.controller.admin;

import com.sky.dto.OrdersCancelDTO;
import com.sky.dto.OrdersConfirmDTO;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.dto.OrdersRejectionDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.OrderService;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController("adminOrderController")
@RequestMapping("/admin/order")
@Slf4j
public class OrderController {

    @Autowired
    private OrderService orderService;

    /**
     * 查询订单
     * @param ordersPageQueryDTO
     * @return
     */
    @GetMapping("/conditionSearch")
    public Result<PageResult> conditionSearch(OrdersPageQueryDTO ordersPageQueryDTO) {
        log.info("查询订单{}",ordersPageQueryDTO);
        PageResult pageResult= orderService.conditionSearch(ordersPageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 查询订单详情
     * @param id
     * @return
     */
    @GetMapping("/details/{id}")
    public Result getOrderDetails(@PathVariable Long id) {
        log.info("查询订单详情{}",id);
        OrderVO orderVO= orderService.getOrderDetail(id);
        return Result.success(orderVO);
    }

    /**
     * 确认订单
     * @param ordersConfirmDTO
     * @return
     */
    @PutMapping("/confirm")
    public Result confirmOrder(@RequestBody OrdersConfirmDTO ordersConfirmDTO) {
        log.info("确认订单{}", ordersConfirmDTO);
        orderService.confirmOrder(ordersConfirmDTO);
        return Result.success();
    }

    /**
     * 拒绝订单
     * @param ordersRejectionDTO
     * @return
     */
    @PutMapping("/rejection")
    public Result rejectOrder(@RequestBody OrdersRejectionDTO ordersRejectionDTO) {
        log.info("拒绝订单{}", ordersRejectionDTO);
        orderService.rejectOrder(ordersRejectionDTO);
        return Result.success();
    }

    /**
     * 订单发货
     * @param id
     * @return
     */
    @PutMapping("/delivery/{id}")
    public Result delivery(@PathVariable Long id) {
        log.info("订单发货{}",id);
        orderService.delivery(id);
        return Result.success();
    }

    /**
     * 取消订单
     * @param ordersCancelDTO
     * @return
     */
    @PutMapping("/cancel")
    public Result cancelOrder(@RequestBody OrdersCancelDTO ordersCancelDTO){
        log.info("取消订单{}",ordersCancelDTO);
        orderService.adminCancelOrder(ordersCancelDTO);
        return Result.success();
    }


    /**
     * 订单完成
     * @param id
     * @return
     */
    @PutMapping("/complete/{id}")
    public Result completeOrder(@PathVariable Long id) {
        log.info("订单完成{}",id);
        orderService.completeOrder(id);
        return Result.success();
    }

    /**
     * 订单统计
     * @return
     */
    @GetMapping("/statistics")
    public Result statistics() {
        log.info("订单统计");
        OrderStatisticsVO orderStatisticsVO= orderService.statistics();
        return Result.success(orderStatisticsVO);
    }
}
