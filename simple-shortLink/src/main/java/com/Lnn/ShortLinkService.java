package com.Lnn;

public interface ShortLinkService {

    String createShortLink(String originalUrl);

    String redirect(String shortLink);

}
