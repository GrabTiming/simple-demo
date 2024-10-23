package com.Lnn.config;

import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.cache.CacheManager;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.cache.ehcache.EhCacheManagerFactoryBean;

@Configuration
@EnableCaching(proxyTargetClass = true)
public class LocalCacheConfig extends CachingConfigurerSupport {

    @Bean
    public EhCacheManagerFactoryBean ehCacheManagerFactoryBean() {
        return getEhCacheManagerFactoryBean();
    }

    /**
     * 获得缓存管理器。默认的为EhCacheCacheManager
     */
    protected EhCacheManagerFactoryBean getEhCacheManagerFactoryBean() {
        EhCacheManagerFactoryBean ehCacheManagerFactoryBean = new EhCacheManagerFactoryBean();
        ehCacheManagerFactoryBean.setConfigLocation(new ClassPathResource("ehcache.xml"));
        return ehCacheManagerFactoryBean;
    }

    @Bean
    public CacheManager cacheManager() {
        return getCacheManager();
    }

    protected CacheManager getCacheManager() {
        EhCacheCacheManager ehCacheCacheManager = new EhCacheCacheManager();
        ehCacheCacheManager.setCacheManager(ehCacheManagerFactoryBean().getObject());
        return ehCacheCacheManager;
    }

}
