## 本地缓存Ehcache

1.配置依赖
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-cache</artifactId>
</dependency>
<dependency>
    <groupId>net.sf.ehcache</groupId>
    <artifactId>ehcache-core</artifactId>
    <version>2.6.11</version>
    <exclusions>
         <exclusion>
             <groupId>commons-logging</groupId>
             <artifactId>commons-logging</artifactId>
         </exclusion>
     </exclusions>
</dependency>
```

2. 第二步，在启动类上添加@EnableCaching注解
3. 在resource目录下增加ehcache.xml
```xml
<ehcache xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:noNamespaceSchemaLocation="ehcache.xsd" updateCheck="true"
         monitoring="autodetect"
         dynamicConfig="true">

    <diskStore path="java.io.tmpdir"/>

    <!-- userList缓存 ，时间为1小时-->
    <cache name="userList" timeToLiveSeconds="3600"
           maxElementsInMemory="500" eternal="false" overflowToDisk="false"
           maxElementsOnDisk="1000" diskPersistent="false"
           memoryStoreEvictionPolicy="LRU"/>

    <cache name="userDetail" timeToLiveSeconds="3600"
           maxElementsInMemory="500" eternal="false" overflowToDisk="false"
           maxElementsOnDisk="1000" diskPersistent="false"
           memoryStoreEvictionPolicy="LRU"/>

</ehcache>


    <!--
         name : 缓存器名称
         maxElementsInMemory : 内存中缓存元素的最大数目
         maxElementsOnDisk : 磁盘中缓存元素的最大数目
         eternal : 缓存是否会过期，如果为 true 则忽略timeToIdleSeconds 和 timeToLiveSeconds
         overflowToDisk : 内存中缓存已满时是否缓存到磁盘，如果为 false 则忽略
         maxElementsOnDisk timeToIdleSeconds : 缓存元素的最大闲置时间（秒），这段时间内如果不访问该元素则缓存失效
         timeToLiveSeconds : 缓存元素的最大生存时间（秒），超过这段时间则强制缓存失效
         memoryStoreEvictionPolicy : 使用 LFU 算法清除缓存
     -->
```
4. 配置Ehcache
```java
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
        //指定ehcache.xml，下面这里要填写文件的位置
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
```
5. 在想要缓存的地方添加@Cacheable注解，下面的value和ehcache.xml文件中对应
```java
    @Override
    @Cacheable(value = "userDetail",key = "'users_'+#id")
    public User getUserById(Integer id) {
        User tmpUser = new User(id,"XXX","XXX");
        return tmpUser;
    }
```
