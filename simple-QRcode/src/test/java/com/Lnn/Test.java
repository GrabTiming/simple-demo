package com.Lnn;

import com.Lnn.service.QRService;
import com.google.zxing.WriterException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

@SpringBootTest
public class Test {

    @Autowired
    private QRService qrService;

    @org.junit.jupiter.api.Test
    public void test(){
        String content = "Hello, World!";
        String filePath = "qrcode.png";
        int width = 350;
        int height = 350;

        try {
            qrService.generateQRCode(content,width,height,filePath);
        } catch (IOException | WriterException e) {
            throw new RuntimeException(e);
        }
    }

}
