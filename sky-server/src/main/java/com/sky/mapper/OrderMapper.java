package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.entity.Orders;
import com.sky.vo.OrderVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

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
}
