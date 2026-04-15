package com.sky.config;

import com.sky.interceptor.JwtTokenAdminInterceptor;
import com.sky.interceptor.JwtTokenUserInterceptor;
import com.sky.json.JacksonObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

import java.util.List;

/**
 * 配置类，注册web层相关组件
 */
@Configuration
@Slf4j
public class WebMvcConfiguration extends WebMvcConfigurationSupport {

    @Autowired
    private JwtTokenAdminInterceptor jwtTokenAdminInterceptor;

    @Autowired
    private JwtTokenUserInterceptor jwtTokenUserInterceptor;

    /**
     * 注册自定义拦截器
     * 用于配置JWT令牌管理员和用户的拦截器

     * 管理员拦截器处理/admin/**路径的请求，但排除登录接口
     * 用户拦截器处理/user/**路径的请求，但排除登录接口和商店状态查询接口
     *
     * @param registry 拦截器注册器，用于添加和配置拦截器
     */
    protected void addInterceptors(InterceptorRegistry registry) {
        log.info("开始注册自定义拦截器...");
        registry.addInterceptor(jwtTokenAdminInterceptor)
                .addPathPatterns("/admin/**")  // 拦截所有管理员相关路径
                .excludePathPatterns("/admin/employee/login");  // 排除管理员登录接口
        registry.addInterceptor(jwtTokenUserInterceptor)
                .addPathPatterns("/user/**")  // 拦截所有用户相关路径
                .excludePathPatterns("/user/user/login")  // 排除用户登录接口
                .excludePathPatterns("/user/shop/status");  // 排除商店状态查询接口
    }

    /**
     * 通过knife4j生成接口文档

     * 配置Swagger接口文档，包括标题、版本和描述等信息
     * 指定controller包路径，自动生成该路径下所有接口的文档
     *
     * @return Docket对象，包含Swagger文档的所有配置信息
     */
    @Bean
    public Docket docket() {
        // 创建API信息对象，包含文档标题、版本和描述
        ApiInfo apiInfo = new ApiInfoBuilder()
                .title("苍穹外卖项目接口文档")
                .version("2.0")
                .description("苍穹外卖项目接口文档")
                .build();
        // 创建Docket对象，配置Swagger文档
        Docket docket = new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo)  // 设置API信息
                .select()  // 启动选择器
                .apis(RequestHandlerSelectors.basePackage("com.sky.controller"))  // 指定controller包路径
                .paths(PathSelectors.any())  // 匹配所有路径
                .build();
        return docket;
    }

    /**
     * 设置静态资源映射

     * 配置静态资源访问路径，用于访问Swagger UI界面
     * 将/doc.html映射到classpath:/META-INF/resources/
     * 将/webjars/**映射到classpath:/META-INF/resources/webjars/
     *
     * @param registry 资源处理器注册器，用于配置静态资源映射
     */
    protected void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 配置Swagger UI的访问路径
        registry.addResourceHandler("/doc.html").addResourceLocations("classpath:/META-INF/resources/");
        // 配置webjars资源的访问路径
        registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
    }

    /**
     * 指定时间格式

     * 扩展消息转换器，配置JSON序列化和反序列化的规则
     * 使用JacksonObjectMapper自定义日期格式等配置
     *
     * @param converters 消息转换器列表，用于处理HTTP请求和响应的转换
     */
    protected void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        log.info("扩展消息转换器...");

        // 创建一个消息转换器对象
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();

        // 需要为消息转换器设置一个对象转换器，对象转换器可以将Java对象序列化为json数据
        converter.setObjectMapper(new JacksonObjectMapper());

        // 将自己的消息转换器加入容器中
        converters.add(0, converter);
    }
}
