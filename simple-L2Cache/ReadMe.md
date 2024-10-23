## ���ػ���Ehcache

1.��������
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

2. �ڶ������������������@EnableCachingע��
3. ��resourceĿ¼������ehcache.xml
```xml
<ehcache xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:noNamespaceSchemaLocation="ehcache.xsd" updateCheck="true"
         monitoring="autodetect"
         dynamicConfig="true">

    <diskStore path="java.io.tmpdir"/>

    <!-- userList���� ��ʱ��Ϊ1Сʱ-->
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
         name : ����������
         maxElementsInMemory : �ڴ��л���Ԫ�ص������Ŀ
         maxElementsOnDisk : �����л���Ԫ�ص������Ŀ
         eternal : �����Ƿ����ڣ����Ϊ true �����timeToIdleSeconds �� timeToLiveSeconds
         overflowToDisk : �ڴ��л�������ʱ�Ƿ񻺴浽���̣����Ϊ false �����
         maxElementsOnDisk timeToIdleSeconds : ����Ԫ�ص��������ʱ�䣨�룩�����ʱ������������ʸ�Ԫ���򻺴�ʧЧ
         timeToLiveSeconds : ����Ԫ�ص��������ʱ�䣨�룩���������ʱ����ǿ�ƻ���ʧЧ
         memoryStoreEvictionPolicy : ʹ�� LFU �㷨�������
     -->
```
4. ����Ehcache
```java
@Configuration
@EnableCaching(proxyTargetClass = true)
public class LocalCacheConfig extends CachingConfigurerSupport {

    @Bean
    public EhCacheManagerFactoryBean ehCacheManagerFactoryBean() {
        return getEhCacheManagerFactoryBean();
    }

    /**
     * ��û����������Ĭ�ϵ�ΪEhCacheCacheManager
     */
    protected EhCacheManagerFactoryBean getEhCacheManagerFactoryBean() {
        EhCacheManagerFactoryBean ehCacheManagerFactoryBean = new EhCacheManagerFactoryBean();
        //ָ��ehcache.xml����������Ҫ��д�ļ���λ��
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
5. ����Ҫ����ĵط����@Cacheableע�⣬�����value��ehcache.xml�ļ��ж�Ӧ
```java
    @Override
    @Cacheable(value = "userDetail",key = "'users_'+#id")
    public User getUserById(Integer id) {
        User tmpUser = new User(id,"XXX","XXX");
        return tmpUser;
    }
```
