package com.sky.service.impl;

import com.sky.dto.OrderAmountDTO;
import com.sky.dto.SaleDTO;
import com.sky.vo.SaleVO;
import com.sky.dto.TurnoverDTO;
import com.sky.dto.UserReportDTO;
import com.sky.entity.Orders;
import com.sky.mapper.ReportMapper;
import com.sky.service.ReportService;
import com.sky.service.WorkspaceService;
import com.sky.vo.BusinessDataVO;
import com.sky.vo.OrderReportVO;
import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class ReportServiceImpl implements ReportService {

    @Autowired
    private ReportMapper reportMapper;

    @Autowired
    private WorkspaceService workspaceService;

    /**
     * 营业额统计方法
     * @param begin 开始日期，统计的起始日期
     * @param end 结束日期，统计的结束日期
     * @return TurnoverReportVO 包含日期列表和营业额列表的报告对象
     */
    @Override
    public TurnoverReportVO turnoverStatistics(LocalDate begin, LocalDate end) {
        log.info("营业额统计"); // 记录日志，表示开始进行营业额统计
        // 创建营业额报告VO对象，用于封装返回结果
        TurnoverReportVO turnoverReportVO=new TurnoverReportVO();
        // 创建日期列表，用于存储从开始日期到结束日期的所有日期
        List<LocalDate> localDateList=new ArrayList<>();
        // 初始化临时日期为开始日期，并添加到日期列表中
        LocalDate temp=begin;
        localDateList.add(temp);
        //前端代码有问题
        //LocalDate tempend=end.plusDays(1);
    // 循环遍历，从开始日期到结束日期，将每一天的日期添加到列表中
        while (!temp.isEqual(end)){
            temp=temp.plusDays(1);
            localDateList.add(temp);
        }
    // 创建营业额列表，用于存储每天的营业额数据
        List<Double> turnoverList=new ArrayList<>();
    // 遍历日期列表，获取每天的营业额
        for(LocalDate localDate:localDateList){
        // 获取当天的开始时间和结束时间
            LocalDateTime beginTime=LocalDateTime.of(localDate, LocalTime.MIN);
            LocalDateTime endTime=LocalDateTime.of(localDate, LocalTime.MAX);
        // 创建营业额DTO对象，包含查询条件
            TurnoverDTO turnoverDTO=new TurnoverDTO(beginTime,endTime, Orders.COMPLETED);
        // 查询指定日期范围内的营业额数据
            Double turnover=reportMapper.turnoverStatistics(turnoverDTO);
        // 如果查询结果为空，则设置为0.0
            if(turnover==null){
                turnover=0.0;
            }
        // 将营业额添加到营业额列表中
            turnoverList.add(turnover);
        }
    // 设置日期列表和营业额列表到报告对象中
        turnoverReportVO.setDateList(StringUtils.join(localDateList,","));
        turnoverReportVO.setTurnoverList(StringUtils.join(turnoverList,","));
    // 返回营业额报告对象
        return turnoverReportVO;
    }

    /**
     * 用户统计方法
     * @param begin 开始日期
     * @param end 结束日期
     * @return 返回用户统计报告VO对象
     */
    @Override
    public UserReportVO userStatistics(LocalDate begin, LocalDate end) {
        // 记录日志：用户统计
        log.info("用户统计");
        // 创建用户统计报告VO对象
        UserReportVO userReportVO = new UserReportVO();
        // 创建日期列表，用于存储从开始日期到结束日期的所有日期
        List<LocalDate> localDateList = new ArrayList<>();
        LocalDate temp = begin;
        localDateList.add(temp);

        // 循环填充日期列表，直到达到结束日期
        while (!temp.isEqual(end)) {
            temp = temp.plusDays(1);
            localDateList.add(temp);
        }

        // 创建总用户数列表和新增用户数列表
        List<Long> totalList = new ArrayList<>();
        List<Long> incrementList = new ArrayList<>();

        // 设置前一天的起始和结束时间
        LocalDateTime beginTime = LocalDateTime.of(begin.plusDays(-1), LocalTime.MIN);
        LocalDateTime endTime = LocalDateTime.of(begin.plusDays(-1), LocalTime.MAX);
        UserReportDTO userReportDTO = new UserReportDTO(beginTime, endTime);
        // 获取前一天的用户数量，如果为null则设为0
        Long amountOfYesterday = reportMapper.getAmountOfUser(userReportDTO);
        if (amountOfYesterday == null) {
            amountOfYesterday = 0L;
        }
        // 初始化总用户数
        Long total = amountOfYesterday;
        // 遍历日期列表，计算每日的总用户数和新增用户数
        for (LocalDate localDate : localDateList) {
            // 设置当天的起始和结束时间
            beginTime = LocalDateTime.of(localDate, LocalTime.MIN);
            endTime = LocalDateTime.of(localDate, LocalTime.MAX);
            userReportDTO = new UserReportDTO(beginTime, endTime);
            // 获取当天的用户数量，如果为null则设为0
            Long amountOfToday = reportMapper.getAmountOfUser(userReportDTO);
            if (amountOfToday == null) {
                amountOfToday = 0L;
            }
            // 更新总用户数
            total += amountOfToday;
            // 将总用户数和新增用户数添加到对应的列表中
            totalList.add(total);
            incrementList.add(amountOfToday);
        }
        // 设置VO对象中的日期列表、总用户数列表和新增用户数列表
        userReportVO.setDateList(StringUtils.join(localDateList, ","));
        userReportVO.setTotalUserList(StringUtils.join(totalList, ","));
        userReportVO.setNewUserList(StringUtils.join(incrementList, ","));
        // 返回用户统计报告VO对象
        return userReportVO;
    }

    /**
     * 订单统计
     * @param begin
     * @param end
     * @return
     */
    @Override
    public OrderReportVO ordersStatistics(LocalDate begin, LocalDate end) {
        log.info("订单统计"); // 记录日志，表示开始进行订单统计
        OrderReportVO orderReportVO=new OrderReportVO();
        // 创建日期列表，用于存储从开始日期到结束日期的所有日期
        List<LocalDate> localDateList = new ArrayList<>();
        LocalDate temp = begin;
        localDateList.add(temp);
        // 循环填充日期列表，直到达到结束日期
        while (!temp.isEqual(end)) {
            temp = temp.plusDays(1);
            localDateList.add(temp);
        }
        List<Long> orderAmountList = new ArrayList<>();
        List<Long> completedList = new ArrayList<>();
        Long total=0L;
        Long completed=0L;
        Double completionRate=0.0;

        for(LocalDate localDate:localDateList){
            LocalDateTime beginTime = LocalDateTime.of(localDate, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(localDate, LocalTime.MAX);
            OrderAmountDTO orderAmountDTO=new OrderAmountDTO(beginTime,endTime,Orders.COMPLETED);
            Long orderAmount=reportMapper.getOrderAmount(orderAmountDTO);
            Long completedOrder=reportMapper.getCompletedOrder(orderAmountDTO);
            if(orderAmount==null){
                orderAmount=0L;
            }
            if(completedOrder==null){
                completedOrder=0L;
            }
            orderAmountList.add(orderAmount);
            completedList.add(completedOrder);
            total+=orderAmount;
            completed+=completedOrder;
        }
        if(total!=0){
            completionRate=completed.doubleValue()/total.doubleValue();
        }
        orderReportVO.setDateList(StringUtils.join(localDateList,","));
        orderReportVO.setOrderCountList(StringUtils.join(orderAmountList,","));
        orderReportVO.setValidOrderCountList(StringUtils.join(completedList,","));
        orderReportVO.setTotalOrderCount(Math.toIntExact(total));
        orderReportVO.setValidOrderCount(Math.toIntExact(completed));
        orderReportVO.setOrderCompletionRate(completionRate);
        return orderReportVO;
    }

    /**
     * 菜品销售统计
     * @param begin 开始日期
     * @param end 结束日期
     * @return 返回包含菜品名称列表和销售数量列表的SalesTop10ReportVO对象
     */
    @Override
    public SalesTop10ReportVO orderDetailStatistics(LocalDate begin, LocalDate end) {


    // 将输入的LocalDate类型转换为当天的开始时间和结束时间
        LocalDateTime beginTime = LocalDateTime.of(begin, LocalTime.MIN); // 开始时间设为当天的0点
        LocalDateTime endTime = LocalDateTime.of(end, LocalTime.MAX); // 结束时间设为当天的23:59:59
    // 创建SaleDTO对象，包含查询条件：时间范围和订单状态（已完成）
        SaleDTO saleDTO=new SaleDTO(beginTime,endTime,Orders.COMPLETED);
    // 调用reportMapper的getTop10方法，查询指定时间范围内的销售前10的数据
        List<SaleVO> list = reportMapper.getTop10(saleDTO);



    // 创建两个列表，分别用于存储菜品名称和销售数量
        List<String> nameList = new ArrayList<>(); // 存储菜品名称
        List<Integer> numberList = new ArrayList<>(); // 存储销售数量

    // 遍历查询结果，将菜品名称和销售数量分别添加到对应的列表中
        for (SaleVO vo : list) {
            nameList.add(vo.getName()); // 添加菜品名称
            numberList.add(vo.getSaleAmount()); // 添加销售数量
        }

    // 创建SalesTop10ReportVO对象，用于返回结果
        SalesTop10ReportVO vo = new SalesTop10ReportVO();
    // 将菜品名称列表用逗号连接成字符串
        vo.setNameList(StringUtils.join(nameList,","));
    // 将销售数量列表转换为字符串形式
        vo.setNumberList(StringUtils.join(numberList,","));
        return vo; // 返回统计结果
    }

    /**
     * 导出运营数据报表
     * @param response
     */
    @Override
    public void exportBusinessData(HttpServletResponse response) throws IOException {

        // ================= 1. 时间 =================
        LocalDate dateBegin = LocalDate.now().minusDays(30);
        LocalDate dateEnd = LocalDate.now().minusDays(1);

        // ================= 2. 查询数据 =================
        BusinessDataVO businessDataVO = workspaceService.getBusinessData(
                LocalDateTime.of(dateBegin, LocalTime.MIN),
                LocalDateTime.of(dateEnd, LocalTime.MAX)
        );

        // ================= 3. 读取模板 =================
        InputStream in = this.getClass().getClassLoader()
                .getResourceAsStream("template/运营数据报表模板.xlsx");

        if (in == null) {
            throw new RuntimeException("模板文件不存在");
        }

        XSSFWorkbook excel = new XSSFWorkbook(in);
        XSSFSheet sheet = excel.getSheetAt(0);

        // ================= 4. 时间 =================
        XSSFRow row = sheet.getRow(1);
        setCell(row, 1, "时间：" + dateBegin + " 至 " + dateEnd);

        // ================= 5. 概览 =================
        // 第4行（index=3）
        row = sheet.getRow(3);
        setCell(row, 2, businessDataVO.getTurnover());                         // 营业额（C列）
        setCell(row, 4, businessDataVO.getOrderCompletionRate());   // 完成率（E列）
        setCell(row, 6, businessDataVO.getNewUsers());                          // 新增用户（G列）

        // 第5行（index=4）
        row = sheet.getRow(4);
        setCell(row, 2, businessDataVO.getValidOrderCount());                   // 有效订单
        setCell(row, 4, businessDataVO.getUnitPrice());                         // 客单价

        // ================= 6. 明细 =================
        int rownum = 7; // ⚠️ 从第7行开始（避免覆盖表头）

        for (LocalDate date = dateEnd; !date.isBefore(dateBegin); date = date.plusDays(-1)) {

            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);

            BusinessDataVO dailyData = workspaceService.getBusinessData(beginTime, endTime);

            row = sheet.getRow(rownum);
            if (row == null) {
                row = sheet.createRow(rownum);
            }

            setCell(row, 1, date.toString());                                   // 日期（B列）
            setCell(row, 2, dailyData.getTurnover());                           // 营业额
            setCell(row, 3, dailyData.getValidOrderCount());                    // 有效订单
            setCell(row, 4, dailyData.getOrderCompletionRate());    // 完成率
            setCell(row, 5, dailyData.getUnitPrice());                          // 客单价
            setCell(row, 6, dailyData.getNewUsers());                           // 新增用户

            rownum++;
        }

        // ================= 7. 响应头 =================
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");

        String fileName = URLEncoder.encode("运营数据报表", "UTF-8");
        response.setHeader("Content-Disposition", "attachment;filename=" + fileName + ".xlsx");

        // ================= 8. 输出 =================
        ServletOutputStream out = response.getOutputStream();
        excel.write(out);

        // ================= 9. 关闭 =================
        out.close();
        excel.close();
        in.close();
    }

    private void setCell(XSSFRow row, int index, Object value) {
        XSSFCell cell = row.getCell(index);
        if (cell == null) {
            cell = row.createCell(index);
        }

        if (value == null) {
            cell.setCellValue("");
        } else if (value instanceof Number) {
            cell.setCellValue(((Number) value).doubleValue());
        } else {
            cell.setCellValue(value.toString());
        }
    }
}
