package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.context.BaseContext;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.dto.PasswordEditDTO;
import com.sky.entity.Employee;
import com.sky.exception.*;
import com.sky.mapper.EmployeeMapper;
import com.sky.result.PageResult;
import com.sky.service.EmployeeService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.time.LocalDateTime;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    private EmployeeMapper employeeMapper;

    /**
     * 员工登录
     *
     * @param employeeLoginDTO
     * @return
     */
    public Employee login(EmployeeLoginDTO employeeLoginDTO) {
        String username = employeeLoginDTO.getUsername();
        String password = employeeLoginDTO.getPassword();

        //1、根据用户名查询数据库中的数据
        Employee employee = employeeMapper.getByUsername(username);

        //2、处理各种异常情况（用户名不存在、密码不对、账号被锁定）
        if (employee == null) {
            //账号不存在
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
        }

        //密码比对
        // TODO 后期需要进行md5加密，然后再进行比对
        password=DigestUtils.md5DigestAsHex(password.getBytes());
        if (!password.equals(employee.getPassword())) {
            //密码错误
            throw new PasswordErrorException(MessageConstant.PASSWORD_ERROR);
        }

        if (employee.getStatus() == StatusConstant.DISABLE) {
            //账号被锁定
            throw new AccountLockedException(MessageConstant.ACCOUNT_LOCKED);
        }

        //3、返回实体对象
        return employee;
    }


    /**
     * 新增员工
     * @param employeeDTO
     */
    @Override
    public void addEmployee(EmployeeDTO employeeDTO) {
        Employee emp=new Employee();
        //拷贝对象属性
        BeanUtils.copyProperties(employeeDTO,emp);
        //处理格式异常
        if(emp.getPhone()==null||emp.getPhone().length()==0){
            throw new PhoneFormatException("电话号码格式有误");
        }
        if(emp.getIdNumber()==null||emp.getIdNumber().length()==0){
            throw new IdNumberFormatException("身份证格式有误");
        }
        //设置员工的其他属性
        //启用状态默认为启用
        emp.setStatus(StatusConstant.ENABLE);
        //默认密码123456
        emp.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));
        //创建时间和修改时间
        emp.setCreateTime(LocalDateTime.now());
        emp.setUpdateTime(LocalDateTime.now());
        //创建人与修改人
        emp.setCreateUser(BaseContext.getCurrentId());
        emp.setUpdateUser(BaseContext.getCurrentId());

        employeeMapper.addEmployee(emp);
    }

    /**
     * 分页查询员工
     * @param employeePageQueryDTO
     * @return
     */
    @Override
    public PageResult pageQuery(EmployeePageQueryDTO employeePageQueryDTO) {
        //使用pagehelper实现分页查询操作
        PageHelper.startPage(employeePageQueryDTO.getPage(),employeePageQueryDTO.getPageSize());
        Page<Employee> page=employeeMapper.pageQuery(employeePageQueryDTO);
        return new PageResult(page.getTotal(),page.getResult());
    }

    /**
     * 更新员工状态
     * @param status
     * @param id
     */
    @Override
    public void updateStatus(Integer status, Long id) {
        Employee emp=new Employee();
        emp.setStatus(status);
        emp.setId(id);
        emp.setUpdateTime(LocalDateTime.now());
        emp.setUpdateUser(BaseContext.getCurrentId());
        employeeMapper.updateEmpInfo(emp);
    }

    /**
     * 根据id回显员工信息
     * @param id
     * @return
     */
    @Override
    public Employee getEmpById(Long id) {
        Employee emp=employeeMapper.getEmpById(id);
        return emp;
    }

    /**
     * 修改员工信息
     * @param employeeDTO
     */
    @Override
    public void updateEmp(EmployeeDTO employeeDTO) {
        Employee emp=new Employee();
        BeanUtils.copyProperties(employeeDTO,emp);
        emp.setUpdateTime(LocalDateTime.now());
        emp.setUpdateUser(BaseContext.getCurrentId());
        employeeMapper.updateEmpInfo(emp);
    }

    /**
     * 修改密码
     * @param passwordEditDTO
     */
    @Override
    public void editPassword(PasswordEditDTO passwordEditDTO) {
        Employee  emp=new Employee();
        emp.setId(BaseContext.getCurrentId());
        BeanUtils.copyProperties(passwordEditDTO,emp);
        emp.setPassword(DigestUtils.md5DigestAsHex(passwordEditDTO.getNewPassword().getBytes()));
        emp.setUpdateTime(LocalDateTime.now());
        emp.setUpdateUser(BaseContext.getCurrentId());
        employeeMapper.updateEmpInfo(emp);
    }

}
