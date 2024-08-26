package com.Lnn.config;

import com.google.common.base.Predicates;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;


/**
 * swagger 访问链接 ：根链接/swagger-ui.html
 */
@Configuration
@EnableSwagger2
public class SwaggerConfig {

    @Bean
    // 创建API基本信息
    public Docket createTestApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                // 对所有api进行监控
                .apis(RequestHandlerSelectors.any())
                //不显示错误的接口地址，也就是错误路径不监控
                .paths(Predicates.not(PathSelectors.regex("/error.*")))
                .build()
                .apiInfo(apiInfo());
    }

    private ApiInfo apiInfo(){
        Contact contact = new Contact("Lnn","http://xxx.xxx.com/联系人访问链接","联系人email");
        return new ApiInfo(
                "Swagger标题",
                "Swagger描述",
                "Swagger版本1.0",
                "https://www.baidu.com/", //填一个链接网址
                contact,//联系人信息
                "Apache 2.0", //许可
                "http://www.baidu.com/", //许可链接url
                new ArrayList<>() //扩展
        );

    }

    /**
     * 有拦截器就要记得放行以下地址址
     * 放行Swagger
     */
//    public static final String[] SWAGGER_WHITELIST = {
//            "/swagger-ui.html/**",
//            "/swagger-ui/**",
//            "/swagger-resources/**",
//            "/v2/api-docs",
//            "/v3/api-docs",
//            "/v3/api-docs/swagger-config",
//            "/webjars/**",
//            "/doc.html",
//    };

}
