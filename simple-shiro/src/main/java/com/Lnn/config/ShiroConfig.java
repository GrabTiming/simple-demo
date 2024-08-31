package com.Lnn.config;

import com.Lnn.realm.UserRealm;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.codec.Base64;
import org.apache.shiro.mgt.RememberMeManager;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.spring.web.config.DefaultShiroFilterChainDefinition;
import org.apache.shiro.web.mgt.CookieRememberMeManager;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * shiro配置类
 * 三大组件：
 * WebSecurityManager:
 * realm: 安全数据的来源
 * ShiroFilterFactory:
 */
@Configuration
public class ShiroConfig {

    @Autowired
    private UserRealm userRealm;

    @Bean
    public ShiroFilterFactoryBean shiroFilterFactoryBean(DefaultWebSecurityManager securityManager){

        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        shiroFilterFactoryBean.setSecurityManager(securityManager);

        Map<String,String> filterMap = new LinkedHashMap<>();

        /**
         * anon : 无需认证
         * authc:所有url都必须认证通过才可以访问; anon:所有url都都可以匿名访问;
         * user:认证通过或者记住了登录状态(remeberMe)则可以通过
         * permit: 权限
         * role:  角色
         */

        filterMap.put("/login","anon");
        filterMap.put("/userLogin", "anon");
        filterMap.put("/**", "authc");
        filterMap.put("/**", "user");

        shiroFilterFactoryBean.setFilterChainDefinitionMap(filterMap);
        shiroFilterFactoryBean.setLoginUrl("/login");//设置登录页面
        shiroFilterFactoryBean.setSuccessUrl("/index");
        shiroFilterFactoryBean.setUnauthorizedUrl("/noAuth");//设置未授权页面

        return shiroFilterFactoryBean;
    }

    @Bean
    public DefaultWebSecurityManager securityManager(UserRealm userRealm){
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();

        //创建加密对象
        HashedCredentialsMatcher matcher = new HashedCredentialsMatcher();
        //设置加密算法
        matcher.setHashAlgorithmName("sha-256");
        //迭代加密次数
        matcher.setHashIterations(3);
        userRealm.setCredentialsMatcher(matcher);

        securityManager.setRealm(userRealm);

        //设置记住我管理器
        securityManager.setRememberMeManager(rememberMeManager());

        return securityManager;
    }



    @Bean
    public CookieRememberMeManager rememberMeManager(){
        CookieRememberMeManager cookieRememberMeManager = new CookieRememberMeManager();
        cookieRememberMeManager.setCookie(rememberMeCookie());
        //rememberMe cookie加密的密钥 建议每个项目都不一样 默认AES算法 密钥长度(128 256 512 位)
        cookieRememberMeManager.setCipherKey(Base64.decode("uAo6JbB7pRecRI1/6DpZyw=="));
        return cookieRememberMeManager;
    }


    @Bean
    public SimpleCookie rememberMeCookie(){
        SimpleCookie simpleCookie = new SimpleCookie("rememberMe");
        simpleCookie.setPath("/");
        simpleCookie.setHttpOnly(true);
        simpleCookie.setMaxAge(24*60*60);
        return simpleCookie;
    }



}
