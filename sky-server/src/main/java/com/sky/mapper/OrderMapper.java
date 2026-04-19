package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.entity.Orders;
import com.sky.vo.OrderVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 订单映射接口
 * 用于处理订单相关的数据库操作
 */
@Mapper
public interface OrderMapper {
    /**
     * 提交订单
     * @param orders 订单信息
     */
    void submit(Orders orders);


    /**
     * 根据订单号获取订单信息
     * @param orderNumber 订单号
     * @return 订单信息
     */
    @Select("select * from orders where number = #{orderNumber}")
    Orders getOrdersByNumber(String orderNumber);

    /**
     * 更新订单信息
     * @param orders 订单信息
     */
    void updateOrders(Orders orders);

    /**
     * 根据订单ID获取订单详情
     * @param id 订单ID
     * @return 订单详情视图对象
     */
    @Select("select * from orders where id = #{id}")
    OrderVO getOrderDetail(Long id);

    /**
     * 获取历史订单列表
     * @param ordersPageQueryDTO 订单分页查询条件
     * @return 分页订单视图对象列表
     */
    Page<OrderVO> historyOrders(OrdersPageQueryDTO ordersPageQueryDTO);

    /**
     * 根据订单ID获取订单信息
     * @param id 订单ID
     * @return 订单信息
     */
    @Select("select * from orders where id = #{id}")
    Orders getOrdersById(Long id);

    /**
     * 条件搜索订单
     * @param ordersPageQueryDTO 订单分页查询条件
     * @return 分页订单视图对象列表
     */
    Page<OrderVO> conditionSearch(OrdersPageQueryDTO ordersPageQueryDTO);

    /**
     * 获取待确认订单数量
     * @return 待确认订单数量
     */
    @Select("select count(*) from orders where status = 2")
    Integer getToBeConfirmedNum();

    /**
     * 获取已确认订单数量
     * @return 已确认订单数量
     */
    @Select("select count(*) from orders where status = 3")
    Integer getConfirmedNum();

    /**
     * 获取配送中订单数量
     * @return 配送中订单数量
     */
    @Select("select count(*) from orders where status = 4")
    Integer getDeliveryInProgressNum();

/**
 * 获取已完成数量
 * @return 返回已完成数量的整数值
 */
    @Select("select count(*) from orders where status = 5")
    Integer getCompletedNum();


/**
 * 获取已取消的数量
 * @return 返回已取消的数量，类型为Integer
 */
    @Select("select count(*) from orders where status = 6")
    Integer getCanceledNum();

    /**
     * 检查支付状态
     * 查询指定状态且下单时间早于限制时间的订单
     * @param unPaid 未支付状态
     * @param limitTime 限制时间
     * @return 符合条件的订单列表
     */
    @Select("select * from orders where status=#{unPaid} and orders.order_time < #{limitTime}")
    List<Orders> checkPayStatus(Integer unPaid, LocalDateTime limitTime);

    /**
     * 检查订单状态
     * 查询指定配送状态且下单时间早于限制时间的订单
     * @param deliveryInProgress 配送中状态
     * @param limitTime 限制时间
     * @return 符合条件的订单列表
     */
    @Select("select * from orders where status=#{deliveryInProgress} and orders.order_time < #{limitTime}")
    List<Orders> checkOrderStatus(Integer deliveryInProgress, LocalDateTime limitTime);

}
