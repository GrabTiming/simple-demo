package com.Lnn.controller;

import com.Lnn.entity.User;
import com.Lnn.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
public class UserController {

    @Autowired
    UserService userService;

    @RequestMapping("/hello")
    public String hello(){
        return "Hello";
    }

    @RequestMapping("/userList")
    public List<User> getUserList(){
        return userService.getUsers();
    }

}
