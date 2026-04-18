package com.sky;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import springfox.documentation.annotations.Cacheable;

/**
 * Spring Boot应用程序的主启动类
 * 该类使用了多个Spring Boot和Spring框架的注解来配置应用程序
 */
@SpringBootApplication // Spring Boot应用程序的主启动注解，自动配置了Spring和Spring Boot的组件
@EnableTransactionManagement //开启注解方式的事务管理，允许使用@Transactional注解进行事务管理
@Slf4j // Lombok提供的注解，自动生成日志器变量log，便于日志记录
@EnableCaching // 启用Spring的缓存功能，允许使用@Cacheable等注解进行缓存操作
@EnableScheduling  // 启动Spring的定时任务功能，允许使用@Scheduled注解定义计划任务
/**
 * SkyApplication类
 * 作为应用程序的入口点，包含main方法
 */
public class SkyApplication {
    /**
     * 主方法，应用程序的入口点
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        // 启动Spring Boot应用程序
        SpringApplication.run(SkyApplication.class, args);
        // 输出日志信息，表示服务器已启动
        log.info("server started");
    }
}
