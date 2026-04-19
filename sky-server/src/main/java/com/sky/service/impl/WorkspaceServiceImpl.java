package com.sky.service.impl;

import com.sky.constant.StatusConstant;
import com.sky.dto.OrderAmountDTO;
import com.sky.dto.TurnoverDTO;
import com.sky.dto.UserReportDTO;
import com.sky.entity.Orders;
import com.sky.mapper.*;
import com.sky.service.WorkspaceService;
import com.sky.vo.BusinessDataVO;
import com.sky.vo.DishOverViewVO;
import com.sky.vo.OrderOverViewVO;
import com.sky.vo.SetmealOverViewVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class WorkspaceServiceImpl implements WorkspaceService {

    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private SetmealMapper setmealMapper;
    @Autowired
    private ReportMapper reportMapper;

    /**
     * 根据时间段统计营业数据
     * @param begin
     * @param end
     * @return
     */
    public BusinessDataVO getBusinessData(LocalDateTime begin, LocalDateTime end) {
        /**
         * 营业额：当日已完成订单的总金额
         * 有效订单：当日已完成订单的数量
         * 订单完成率：有效订单数 / 总订单数
         * 平均客单价：营业额 / 有效订单数
         * 新增用户：当日新增用户的数量
         */
        //查询总订单数
        Integer totalOrderCount = Math.toIntExact(reportMapper.getOrderAmount(new OrderAmountDTO(begin, end, null)));

        //营业额
        Double turnover = reportMapper.turnoverStatistics(new TurnoverDTO(begin, end, Orders.COMPLETED));
        turnover = turnover == null? 0.0 : turnover;

        //有效订单数
        Integer validOrderCount = Math.toIntExact(reportMapper.getCompletedOrder(new OrderAmountDTO(begin, end, Orders.COMPLETED)));

        Double unitPrice = 0.0;

        Double orderCompletionRate = 0.0;
        if(totalOrderCount != 0 && validOrderCount != 0){
            //订单完成率
            orderCompletionRate = validOrderCount.doubleValue() / totalOrderCount;
            //平均客单价
            unitPrice = turnover / validOrderCount;
        }

        //新增用户数
        Integer newUsers = Math.toIntExact(reportMapper.getAmountOfUser(new UserReportDTO(begin, end)) - reportMapper.getAmountOfUser(new UserReportDTO(begin.minusDays(1), end.minusDays(1))));

        return BusinessDataVO.builder()
                .turnover(turnover)
                .validOrderCount(validOrderCount)
                .orderCompletionRate(orderCompletionRate)
                .unitPrice(unitPrice)
                .newUsers(newUsers)
                .build();
    }


    /**
     * 查询订单管理数据
     *
     * @return
     */
    public OrderOverViewVO getOrderOverView() {
        //待接单
        Integer waitingOrders = reportMapper.getToBeConfirmedOrder(new OrderAmountDTO(LocalDateTime.now().with(LocalTime.MIN), LocalDateTime.now().with(LocalTime.MAX), Orders.TO_BE_CONFIRMED));
        //待派送
        Integer deliveredOrders = reportMapper.getConfirmedOrder(new OrderAmountDTO(LocalDateTime.now().with(LocalTime.MIN), LocalDateTime.now().with(LocalTime.MAX), Orders.CONFIRMED));

        //已完成
        Integer completedOrders = Math.toIntExact(reportMapper.getCompletedOrder(new OrderAmountDTO(LocalDateTime.now().with(LocalTime.MIN), LocalDateTime.now().with(LocalTime.MAX), Orders.COMPLETED)));

        //已取消
        Integer cancelledOrders = reportMapper.getCanceledOrder(new OrderAmountDTO(LocalDateTime.now().with(LocalTime.MIN), LocalDateTime.now().with(LocalTime.MAX), Orders.CANCELLED));

        //全部订单
        Integer allOrders = waitingOrders+ deliveredOrders + completedOrders + cancelledOrders;

        return OrderOverViewVO.builder()
                .waitingOrders(waitingOrders)
                .deliveredOrders(deliveredOrders)
                .completedOrders(completedOrders)
                .cancelledOrders(cancelledOrders)
                .allOrders(allOrders)
                .build();
    }

    /**
     * 查询菜品总览
     *
     * @return
     */
    public DishOverViewVO getDishOverView() {
        Integer sold = dishMapper.getEnableNum(StatusConstant.ENABLE);

        Integer discontinued = dishMapper.getDisableNum(StatusConstant.DISABLE);

        return DishOverViewVO.builder()
                .sold(sold)
                .discontinued(discontinued)
                .build();
    }

    /**
     * 查询套餐总览
     *

 * 该方法用于获取套餐的总览信息，包括已售出的套餐数量和已停售的套餐数量。
 * 通过调用setmealMapper的countByMap方法，分别统计状态为启用和禁用的套餐数量。
 *
     * @return 返回一个SetmealOverViewVO对象，包含sold（已售出套餐数量）和discontinued（已停售套餐数量）两个属性
     */
    public SetmealOverViewVO getSetmealOverView() {
        Integer sold = setmealMapper.getEnableNum(StatusConstant.ENABLE);

    // 更新查询条件为禁用状态，并查询禁用的套餐数量
        Integer discontinued = setmealMapper.getDisableNum(StatusConstant.DISABLE);

    // 使用建造者模式构建并返回SetmealOverViewVO对象
        return SetmealOverViewVO.builder()
                .sold(sold)          // 设置已售出套餐数量
                .discontinued(discontinued)  // 设置已停售套餐数量
                .build();            // 构建并返回对象
    }

}
