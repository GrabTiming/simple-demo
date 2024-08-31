package com.Lnn.controller;

import com.Lnn.entity.User;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

@Controller
public class AuthController {


    @GetMapping("/login")
    public String login(){

        return "login";
    }

    @GetMapping("/index")
    public String index(){

        return "index";
    }

    @GetMapping("/userLogin")
    public String userLogin(String username, String password,
                            @RequestParam(defaultValue = "false") boolean rememberMe,
                            HttpSession session){

        Subject subject = SecurityUtils.getSubject();

        UsernamePasswordToken token = new UsernamePasswordToken(username, password,rememberMe);

        try{
            subject.login(token);
            session.setAttribute("user",username);
            return "index";
        }catch (AuthenticationException e){
            return "login";
        }
    }


    /**
     * 记住我
     */
    @GetMapping("/userLoginRm")
    public String userLogin(HttpSession session){
        session.setAttribute("user","rememberMe");
        return "index";
    }

}
