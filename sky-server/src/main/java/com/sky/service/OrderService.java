package com.sky.service;

import com.sky.dto.OrdersConfirmDTO;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.dto.OrdersPaymentDTO;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.result.PageResult;
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
}
