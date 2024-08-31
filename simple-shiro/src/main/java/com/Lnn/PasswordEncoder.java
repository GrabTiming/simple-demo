package com.Lnn;

import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.crypto.hash.Sha256Hash;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class PasswordEncoder {

    /**
     * 一般来说这段逻辑是要放在用户账号注册时加密密码的
     */
    public static void main(String[] args) {

        // 盐值
        String salt = "abc";
        // 原始密码
        String password = "123456";
        // 创建加密对象
        HashedCredentialsMatcher matcher = new HashedCredentialsMatcher();
        matcher.setHashAlgorithmName("sha-256"); // 设置为 SHA-256
        matcher.setHashIterations(3); // 设置迭代次数

        // 进行加密
        String hashedCredentials = new Sha256Hash(password, salt, 3).toHex();
        System.out.println(hashedCredentials);

    }
}

