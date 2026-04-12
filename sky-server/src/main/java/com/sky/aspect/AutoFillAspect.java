package com.sky.aspect;

import com.sky.annotation.AutoFill;
import com.sky.constant.AutoFillConstant;
import com.sky.context.BaseContext;
import com.sky.enumeration.OperationType;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.time.LocalDateTime;

@Aspect
@Component
@Slf4j
public class AutoFillAspect {

    /**
     * 设置切入点
     */
    @Pointcut("execution (* com.sky.mapper.*.*(..)) && @annotation(com.sky.annotation.AutoFill)")
    public void autoFillPointcut() {}

    /**
     * 方法执行前切入
     * @param joinPoint
     */
    @Before("autoFillPointcut()")
    public void beforeAutoFill(JoinPoint joinPoint) {
        //获取方法签名
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        //获取方法注解
        AutoFill autoFill=signature.getMethod().getAnnotation(AutoFill.class);
        //获取注解的值
        OperationType operationType=autoFill.value();
        //获取参数对象
        Object[] args = joinPoint.getArgs();
        if(args == null && args.length == 0){
            return;
        }
        Object obj = args[0];
        //使用反射获取get set方法
        if(operationType.equals(OperationType.UPDATE)){
            try{
                Method method1 =obj.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
                Method method2 =obj.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);
                method1.invoke(obj,LocalDateTime.now());
                method2.invoke(obj, BaseContext.getCurrentId());
            }catch (Exception e){
                log.error("有异常{}",e.getMessage());
            }
        }else if(operationType.equals(OperationType.INSERT)){
            try{
                Method method1 =obj.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_TIME, LocalDateTime.class);
                Method method2 =obj.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
                Method method3 =obj.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_USER, Long.class);
                Method method4 =obj.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);
                method1.invoke(obj,LocalDateTime.now());
                method2.invoke(obj,LocalDateTime.now());
                method3.invoke(obj,BaseContext.getCurrentId());
                method4.invoke(obj,BaseContext.getCurrentId());
            }catch (Exception e){
                log.error("有异常",e);
            }
        }

    }
}
