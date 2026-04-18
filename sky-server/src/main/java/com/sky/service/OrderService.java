package com.sky.service;

import com.sky.dto.*;
import com.sky.result.PageResult;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;

public interface OrderService {
    OrderSubmitVO submit(OrdersSubmitDTO ordersSubmitDTO);

    void payment(OrdersPaymentDTO ordersPaymentDTO);

    OrderVO getOrderDetail(Long id);

    PageResult historyOrders(OrdersPageQueryDTO ordersPageQueryDTO);

    void cancelOrder(Long id);

    void repetition(Long id);

    PageResult conditionSearch(OrdersPageQueryDTO ordersPageQueryDTO);

    void confirmOrder(OrdersConfirmDTO ordersConfirmDTO);

    void rejectOrder(OrdersRejectionDTO ordersRejectionDTO);

    void delivery(Long id);

    void adminCancelOrder(OrdersCancelDTO ordersCancelDTO);

    void completeOrder(Long id);

    OrderStatisticsVO statistics();

    void reminder(Long id);
}
