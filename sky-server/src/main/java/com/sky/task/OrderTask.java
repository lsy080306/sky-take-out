package com.sky.task;

import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@Slf4j
public class OrderTask {

    @Autowired
    private OrderMapper orderMapper;

    /**
     * 每分钟检查一次订单支付状态
     */
    @Scheduled(cron = "0 * * * * ?")
    public void checkPayStatus() {
        log.info("检查超时支付");
        LocalDateTime limitTime=LocalDateTime.now().plusMinutes(-15);
        List<Orders> ordersList=orderMapper.checkPayStatus(Orders.UN_PAID, limitTime);
        if(ordersList!=null&&ordersList.size()>0){
            for(Orders orders:ordersList){
                //TODO 发送消息通知用户支付超时
                log.info("订单超时未支付");
                orders.setStatus(Orders.CANCELLED);
                orderMapper.updateOrders(orders);
            }
        }
    }

    /**
     * 每天凌晨1点检查订单状态
     */
    @Scheduled(cron = "0 0 1 * * ?")
    public void checkOrderStatus(){
        log.info("检查未完成订单");
        LocalDateTime limitTime=LocalDateTime.now().plusHours(-1);
        List<Orders> ordersList=orderMapper.checkOrderStatus(Orders.DELIVERY_IN_PROGRESS, limitTime);
        if(ordersList!=null&&ordersList.size()>0){
            for(Orders orders:ordersList) {
                //TODO 发送消息通知用户订单超时
                log.info("订单超时未完成");
                orders.setStatus(Orders.COMPLETED);
                orderMapper.updateOrders(orders);
            }
        }
    }

}
