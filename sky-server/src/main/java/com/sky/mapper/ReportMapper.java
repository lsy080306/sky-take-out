package com.sky.mapper;

import com.sky.dto.OrderAmountDTO;
import com.sky.dto.SaleDTO;
import com.sky.dto.TurnoverDTO;
import com.sky.dto.UserReportDTO;
import com.sky.vo.SaleVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ReportMapper {

    Double turnoverStatistics(TurnoverDTO turnoverDTO);

    Long getAmountOfUser(UserReportDTO userReportDTO);

    Long getOrderAmount(OrderAmountDTO orderAmountDTO);

    Long getCompletedOrder(OrderAmountDTO orderAmountDTO);

    List<SaleVO> getTop10(SaleDTO saleDTO);

    Integer getToBeConfirmedOrder(OrderAmountDTO orderAmountDTO);

    Integer getConfirmedOrder(OrderAmountDTO orderAmountDTO);

    Integer getCanceledOrder(OrderAmountDTO orderAmountDTO);
}
