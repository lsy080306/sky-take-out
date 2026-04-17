package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.context.BaseContext;
import com.sky.dto.OrdersConfirmDTO;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.dto.OrdersPaymentDTO;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.entity.AddressBook;
import com.sky.entity.OrderDetail;
import com.sky.entity.Orders;
import com.sky.entity.ShoppingCart;
import com.sky.exception.AddressBookBusinessException;
import com.sky.exception.OrderBusinessException;
import com.sky.exception.ShoppingCartBusinessException;
import com.sky.mapper.*;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.OrderService;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 订单服务实现类
 * 实现了订单提交的功能
 */
@Service
@Slf4j
@Transactional(rollbackFor = Exception.class)
public class OrderServiceImpl implements OrderService {

    // 自动注入订单数据访问层
    @Autowired
    OrderMapper orderMapper;
    // 自动注入购物车数据访问层
    @Autowired
    private ShoppingCartMapper shoppingCartMapper;
    // 自动注入地址簿数据访问层
    @Autowired
    private AddressBookMapper addressBookMapper;
    // 自动注入订单详情数据访问层
    @Autowired
    private OrderDetailMapper orderDetailMapper;
    // 自动注入用户数据访问层
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private OrderService orderService;

    /**
     * 提交订单的方法
     * @param ordersSubmitDTO 订单提交的数据传输对象，包含订单相关信息
     * @return OrderSubmitVO 订单提交后的视图对象，包含订单ID、订单号、金额和时间等信息
     */
    @Override
    public OrderSubmitVO submit(OrdersSubmitDTO ordersSubmitDTO) {
        // 记录提交订单的日志信息
        log.info("提交订单：{}", ordersSubmitDTO);
        // 获取地址簿信息，如果为空则抛出异常
        AddressBook addressBook= addressBookMapper.getById(ordersSubmitDTO.getAddressBookId());
        if(addressBook==null){
            throw new AddressBookBusinessException("地址不能为空");
        }
        // 获取当前登录用户的ID
        Long id= BaseContext.getCurrentId();
        // 获取用户的购物车列表，如果为空则抛出异常
        List<ShoppingCart> shoppingCartList = shoppingCartMapper.getShoppingCartList(id);
        if(shoppingCartList==null||shoppingCartList.size()==0){
            throw new ShoppingCartBusinessException("购物车不能为空");
        }


        // 创建订单对象并设置基本信息
        Orders orders=new Orders();
        // 将订单提交DTO中的属性复制到订单对象中
        BeanUtils.copyProperties(ordersSubmitDTO,orders);
        // 设置用户ID
        orders.setUserId(id);
        // 设置订单时间为当前时间
        orders.setOrderTime(LocalDateTime.now());
        // 设置订单状态为待支付
        orders.setStatus(Orders.PENDING_PAYMENT);
        // 设置支付状态为未支付
        orders.setPayStatus(Orders.UN_PAID);
        // 生成订单号（使用UUID和时间戳组合）
        orders.setNumber(UUID.randomUUID()+String.valueOf(System.currentTimeMillis()));
        // 设置收货人电话
        orders.setPhone(addressBook.getPhone());
        // 设置收货地址（省市区详细地址）
        orders.setAddress((addressBook.getProvinceName()+addressBook.getCityName()+addressBook.getDistrictName()+addressBook.getDetail()));
        orders.setConsignee(addressBook.getConsignee());
        orderMapper.submit(orders);

        List<OrderDetail> list= new ArrayList<>();
        for(ShoppingCart shoppingCart:shoppingCartList){
            OrderDetail orderDetail=new OrderDetail();
            BeanUtils.copyProperties(shoppingCart,orderDetail);
            orderDetail.setOrderId(orders.getId());
            list.add(orderDetail);

        }
        orderDetailMapper.submitOrderDetail(list);

        shoppingCartMapper.clearShoppingCart(id);

        return new OrderSubmitVO(orders.getId(),orders.getNumber(),orders.getAmount(),orders.getOrderTime());
    }

    @Override
    public void payment(OrdersPaymentDTO ordersPaymentDTO) {
        log.info("支付订单：{}", ordersPaymentDTO);
        Orders orders=orderMapper.getOrdersByNumber(ordersPaymentDTO.getOrderNumber());
        if (orders!=null){
            orders.setStatus(Orders.TO_BE_CONFIRMED);
            orders.setPayStatus(Orders.PAID);
            orders.setPayMethod(ordersPaymentDTO.getPayMethod());
            orders.setCheckoutTime(LocalDateTime.now());
            orderMapper.updateOrders(orders);
        }else {
            throw new OrderBusinessException("订单不存在");
        }
    }

    /**
     * 订单详情
     * @param id
     * @return
     */
    @Override
    public OrderVO getOrderDetail(Long id) {
        log.info("订单详情");
        OrderVO orderVO= orderMapper.getOrderDetail(id);
        List<OrderDetail> list= orderDetailMapper.getOrderDetailList(id);
        orderVO.setOrderDetailList(list);
        return orderVO;
    }

    /**
     * 查询历史订单
     * @param ordersPageQueryDTO
     * @return
     */
    @Override
    public PageResult historyOrders(OrdersPageQueryDTO ordersPageQueryDTO) {
        log.info("历史订单");

        PageHelper.startPage(ordersPageQueryDTO.getPage(), ordersPageQueryDTO.getPageSize());

        Long userId = BaseContext.getCurrentId();
        ordersPageQueryDTO.setUserId(userId);

        Page<OrderVO> page = orderMapper.historyOrders(ordersPageQueryDTO);

        List<OrderVO> ordersList = page.getResult();

        if (ordersList != null && !ordersList.isEmpty()) {
            for (OrderVO orderVO : ordersList) {
                List<OrderDetail> detailList = orderDetailMapper.getOrderDetailList(orderVO.getId());
                orderVO.setOrderDetailList(detailList);
            }
        }
        return new PageResult(page.getTotal(), ordersList);
    }

    /**
     * 取消订单
     * @param id
     */
    @Override
    public void cancelOrder(Long id) {
        log.info("取消订单：{}", id);
        Orders orders=orderMapper.getOrdersById(id);
        orders.setStatus(Orders.CANCELLED);
        orders.setCancelTime(LocalDateTime.now());
        orderMapper.updateOrders(orders);
    }

    /**
     * 再来一单方法

 * 该方法用于复制已有的订单，创建一个新的订单，状态重置为待支付
     * @param id 原始订单的ID，用于获取订单详情和创建新订单
     */
    @Override
    public void repetition(Long id) {
    // 记录日志信息
        log.info("再来一单");
    // 根据ID获取原始订单信息
        Orders orders=orderMapper.getOrdersById(id);
    // 设置新订单的时间为当前时间
        orders.setOrderTime(LocalDateTime.now());
    // 设置订单状态为待支付
        orders.setStatus(Orders.PENDING_PAYMENT);
    // 设置支付状态为未支付
        orders.setPayStatus(Orders.UN_PAID);
    // 生成新的订单号（使用UUID和时间戳组合确保唯一性）
        orders.setNumber(UUID.randomUUID()+String.valueOf(System.currentTimeMillis()));
    // 提交新订单到数据库
        orderMapper.submit(orders);
    // 获取原始订单的订单详情列表
        List<OrderDetail> orderDetailList=orderDetailMapper.getOrderDetailList(id);
        for(OrderDetail orderDetail:orderDetailList){
            orderDetail.setOrderId(orders.getId());
        }
    // 提交订单详情到数据库
        orderDetailMapper.submitOrderDetail(orderDetailList);
    }

    /**
     * 条件查询订单
     * @param ordersPageQueryDTO 订单分页查询条件数据传输对象
     * @return 返回包含订单总数量和订单列表的分页结果对象
     */
    @Override
    public PageResult conditionSearch(OrdersPageQueryDTO ordersPageQueryDTO) {
    // 打印日志：条件查询订单
        log.info("条件查询订单");
    // 使用PageHelper设置分页参数
        PageHelper.startPage(ordersPageQueryDTO.getPage(), ordersPageQueryDTO.getPageSize());
    // 调用订单Mapper进行条件查询，获取订单分页数据
        Page<OrderVO> page = orderMapper.conditionSearch(ordersPageQueryDTO);
    // 获取分页结果中的订单列表
        List<OrderVO> ordersList = page.getResult();
    // 遍历订单列表，为每个订单获取订单详情
        for(OrderVO orderVO:ordersList){
        // 根据订单ID查询订单详情列表
            List<OrderDetail> detailList=orderDetailMapper.getOrderDetailList(orderVO.getId());
        // 创建列表用于存储订单菜品名称
            List<String> name=new ArrayList<>();
        // 遍历订单详情列表，提取菜品名称
            for(OrderDetail orderDetail:detailList){
            // 将菜品名称添加到列表中
                name.add(orderDetail.getName());
            }
        // 将菜品名称列表转换为字符串并设置到订单VO对象中
            orderVO.setOrderDishes(name.toString());
        }
    // 返回分页结果，包含订单总数量和订单列表
        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 确认订单
     * @param ordersConfirmDTO
     */
    @Override
    public void confirmOrder(OrdersConfirmDTO ordersConfirmDTO) {
        log.info("确认订单");
        Orders orders=orderMapper.getOrdersById(ordersConfirmDTO.getId());
        orders.setStatus(Orders.CONFIRMED);
        orderMapper.updateOrders(orders);
    }

}
