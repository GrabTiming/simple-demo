package com.Lnn.controller;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class TestController {

    @GetMapping("/")
    @ApiOperation(value = "hello打招呼",notes = "返回打招呼") //接口描述
    public String hello(){
        return "Hello";
    }

    @GetMapping("/hi/{name}")
    @ApiImplicitParams(
            @ApiImplicitParam(name = "name", paramType = "path", required = true, dataType = "String")
    )
    public String hi(@PathVariable String name){

        return "Hi,"+name;
    }

    /**
     * 除此之外的一些接口
     *  @Api：用在controller上，对controller进行注释；
     *  @ApiOperation：用在API方法上，对该API做注释，说明API的作用；
     *  @ApiImplicitParams：用来包含API的一组参数注解，可以简单的理解为参数注解的集合声明；
     *  @ApiImplicitParam：用在@ApiImplicitParams注解中，也可以单独使用，说明一个请求参数的各个方面，该注解包含的常用选项有：
     *  paramType：参数所放置的地方，包含query、header、path、body以及form，最常用的是前四个。
     *  name：参数名；
     *  dataType：参数类型，可以是基础数据类型，也可以是一个class；
     *  required：参数是否必须传；
     *  value：参数的注释，说明参数的意义；
     *  defaultValue：参数的默认值；
     */

}
