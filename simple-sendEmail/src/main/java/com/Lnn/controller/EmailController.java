package com.Lnn.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EmailController {


    @Autowired
    JavaMailSender sender;

    @GetMapping("/sendEmail")
    public String sendEmail() {
        // 发送邮件的逻辑
        SimpleMailMessage message = new SimpleMailMessage();
        //设置邮件标题
        message.setSubject("email-subject");
        //设置邮件内容
        message.setText("比奇堡海滩邀请您参加2025年度海底灭火行动");
        //邮件接收者
        message.setTo("xxx@qq.com");
        //邮件发送者，这里要与配置文件中的保持一致
        message.setFrom("xxx@163.com");
        sender.send(message);
        return "success";
    }


}
