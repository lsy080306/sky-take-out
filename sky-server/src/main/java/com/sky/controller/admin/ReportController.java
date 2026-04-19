package com.sky.controller.admin;

import com.sky.result.Result;
import com.sky.service.ReportService;
import com.sky.vo.OrderReportVO;
import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;

@RestController
@RequestMapping("/admin/report")
@Slf4j
public class ReportController {

    @Autowired
    private ReportService reportService;

    /**
     * 营业额统计
     * @return
     */
    @GetMapping("/turnoverStatistics")
    public Result turnoverStatistics(@DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin
            , @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end){
        log.info("营业额统计");
        TurnoverReportVO turnoverReportVO = reportService.turnoverStatistics(begin,end);
        return Result.success(turnoverReportVO);
    }

    /**
     * 用户统计
     * @return
     */
    @GetMapping("/userStatistics")
    public Result userStatistics(@DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin
            , @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end){
        log.info("用户统计");
        UserReportVO userReportVO = reportService.userStatistics(begin,end);
        return Result.success(userReportVO);
    }

    /**
     * 订单统计
     * @return
     */
    @GetMapping("/ordersStatistics")
    public Result ordersStatistics(@DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin
            , @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end){
        log.info("订单统计");
        OrderReportVO orderReportVO = reportService.ordersStatistics(begin,end);
        return Result.success(orderReportVO);
    }

    /**
     * 菜品销量统计
     * @return
     */
    @GetMapping("/top10")
    public Result orderDetailStatistics(@DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin
            , @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end){
        log.info("菜品销量统计");
        SalesTop10ReportVO salesTop10ReportVO = reportService.orderDetailStatistics(begin,end);
        return Result.success(salesTop10ReportVO);
    }

    /**
     * 导出运营数据报表
     * @param response
     */
    @GetMapping("/export")
    public void export(HttpServletResponse response) throws IOException {
        log.info("导出运营数据报表");
        // 设置响应头
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=business_data.xlsx");
        // 调用service导出报表
        reportService.exportBusinessData(response);
    }
}
