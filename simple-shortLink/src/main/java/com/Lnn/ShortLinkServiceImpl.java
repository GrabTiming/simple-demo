package com.Lnn;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * 长链转短链
 * 将hashcode转为62进制，然后
 */
@Service
public class ShortLinkServiceImpl implements ShortLinkService {

    private static final String CHARACTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    private static final String salt = "Lnn.?/*";

    //这里简单用map存储，实际上要存进数据库，还要依据访问的速度存到redis
    private static final HashMap<String,String> shortLinkMap = new HashMap<>();

    //记录出现过的hash码
    private static final Set<Integer> hashSet = new HashSet<>();

    private String convertToBase62(int hash) {
        StringBuilder sb = new StringBuilder();
        while (hash > 0) {
            int remainder = hash % 62;
            sb.insert(0, CHARACTERS.charAt(remainder));
            hash /= 62;
        }
        return sb.toString();
    }

    @Override
    public String createShortLink(String originalUrl) {
        StringBuilder tmpUrl = new StringBuilder(originalUrl);
        int cnt = 0; //存在hash冲突就加盐重试, 最多重试5次
        while(cnt<5){
            cnt++;
            int hash = tmpUrl.toString().hashCode();
            String result = convertToBase62(hash);
            if(!hashSet.contains(hash)){ //如果hash码不冲突
                shortLinkMap.put(result,originalUrl);
                hashSet.add(hash);
                return result;
            }
            tmpUrl.append(salt);
        }

        return null; //会不会到这步？能到这一步的话赶紧到彩票店买张彩票
    }

    @Override
    public String redirect(String shortLink) {
            return shortLinkMap.get(shortLink);
    }

}
