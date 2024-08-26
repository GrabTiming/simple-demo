package com.Lnn;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/shortLink")
public class ShortLinkController {


    @Autowired
    private ShortLinkService shortLinkService;


    @RequestMapping("/create/{url}")
    public String createShortLink(@PathVariable("url") String url) {
        return shortLinkService.createShortLink(url);
    }

    @RequestMapping("/redirect")
    public void redirect(String shortLink, HttpServletResponse response) {

        String originalUrl = shortLinkService.redirect(shortLink);
        if (originalUrl != null) {
            response.setStatus(302);
            response.setHeader("Location", originalUrl);
        } else {
            response.setStatus(404);//找不到页面
        }
    }

}
