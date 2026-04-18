package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.entity.Orders;
import com.sky.vo.OrderVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface OrderMapper {
    void submit(Orders orders);


    @Select("select * from orders where number = #{orderNumber}")
    Orders getOrdersByNumber(String orderNumber);

    void updateOrders(Orders orders);

    @Select("select * from orders where id = #{id}")
    OrderVO getOrderDetail(Long id);

    Page<OrderVO> historyOrders(OrdersPageQueryDTO ordersPageQueryDTO);

    @Select("select * from orders where id = #{id}")
    Orders getOrdersById(Long id);

    Page<OrderVO> conditionSearch(OrdersPageQueryDTO ordersPageQueryDTO);

    @Select("select count(*) from orders where status = 2")
    Integer getToBeConfirmedNum();

    @Select("select count(*) from orders where status = 3")
    Integer getConfirmedNum();

    @Select("select count(*) from orders where status = 4")
    Integer getDeliveryInProgressNum();

    @Select("select * from orders where status=#{unPaid} and orders.order_time < #{limitTime}")
    List<Orders> checkPayStatus(Integer unPaid, LocalDateTime limitTime);

    @Select("select * from orders where status=#{deliveryInProgress} and orders.order_time < #{limitTime}")
    List<Orders> checkOrderStatus(Integer deliveryInProgress, LocalDateTime limitTime);
}
