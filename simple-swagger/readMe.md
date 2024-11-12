## 简单配置swagger

### 一. 添加依赖
```xml
        <dependency>
            <groupId>io.springfox</groupId>
            <artifactId>springfox-swagger2</artifactId>
            <version>2.9.2</version>
        </dependency>

        <dependency>
            <groupId>io.springfox</groupId>
            <artifactId>springfox-swagger-ui</artifactId>
            <version>2.9.2</version>
        </dependency>
```

### 二.填写配置类
```java
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
```

## 三. 在application.yml配置以下信息
```yml
spring:
  mvc:
    pathmatch:
      matching-strategy: ant_path_matcher # swagger
```
