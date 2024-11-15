# Springboot整合ElasticSearch
## 导入依赖
```xml
        <dependency>
            <groupId>org.elasticsearch</groupId>
            <artifactId>elasticsearch</artifactId>
            <version>${elasticsearch.version}</version>
        </dependency>

        <dependency>
            <groupId>org.elasticsearch.client</groupId>
            <artifactId>elasticsearch-rest-client</artifactId>
            <version>${elasticsearch.version}</version>
        </dependency>
```
注意一定要显示指定ES版本，因为Springboot自带ES的版本，而版本不匹配会引来其他问题

## 配置
在application.yml
```yaml
elasticsearch:
  clusterName: es
  hosts: ES地址:9200
  scheme: http
  connectTimeOut: 1000 
  socketTimeOut: 30000
  connectionRequestTimeOut: 1000
  maxConnectNum: 100
  maxConnectNumPerRoute: 100
  # 有username和password就弄上去 
```
配置类
```java
/**
 * restHighLevelClient 客户端配置类
 */
@Slf4j
@Data
@Configuration
@ConfigurationProperties(prefix = "elasticsearch")
public class ElasticsearchConfig {

    // es host ip 地址（集群）
    private String hosts;
    // es用户名
//    private String userName;
    // es密码
//    private String password;
    // es 请求方式
    private String scheme;
    // es集群名称
    private String clusterName;
    // es 连接超时时间
    private int connectTimeOut;
    // es socket 连接超时时间
    private int socketTimeOut;
    // es 请求超时时间
    private int connectionRequestTimeOut;
    // es 最大连接数
    private int maxConnectNum;
    // es 每个路由的最大连接数
    private int maxConnectNumPerRoute;


    /**
     * 如果@Bean没有指定bean的名称，那么这个bean的名称就是方法名
     */
    @Bean(name = "restHighLevelClient")
    public RestHighLevelClient restHighLevelClient() {


        // 此处为单节点es
        HttpHost httpHost = HttpHost.create(hosts);
        // 构建连接对象
        RestClientBuilder builder = RestClient.builder(httpHost);

        // 设置用户名、密码
        //CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        //credentialsProvider.setCredentials(AuthScope.ANY,new UsernamePasswordCredentials(userName,password));

        // 连接延时配置
        builder.setRequestConfigCallback(requestConfigBuilder -> {
            requestConfigBuilder.setConnectTimeout(connectTimeOut);
            requestConfigBuilder.setSocketTimeout(socketTimeOut);
            requestConfigBuilder.setConnectionRequestTimeout(connectionRequestTimeOut);
            return requestConfigBuilder;
        });
        // 连接数配置
        builder.setHttpClientConfigCallback(httpClientBuilder -> {
            httpClientBuilder.setMaxConnTotal(maxConnectNum);
            httpClientBuilder.setMaxConnPerRoute(maxConnectNumPerRoute);
//            httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider);
            return httpClientBuilder;
        });

        return new RestHighLevelClient(builder);
    }
}
```

## 操作
通过RestHighLevelClient 类来操作，有个indices方法，可以执行创建索引库，文档的增删改查。

### 工具类
```java
@Component
public class ESClient {

    @Autowired
    private RestHighLevelClient restHighLevelClient;


    /**
     * 创建索引库
     * @param index 索引名
     * @param settings 设置的分片，备份分片数量
     * @param mappings 索引库的结构
     */
    public boolean createIndex(String index, Settings.Builder settings, XContentBuilder mappings) throws IOException {

        CreateIndexRequest request = new CreateIndexRequest(index)
                .settings(settings)
                .mapping(mappings);

        return restHighLevelClient.indices().create(request, RequestOptions.DEFAULT).isAcknowledged();
    }

    /**
     * 创建索引
     * @param index
     * @param jsonMapping 索引库结构
     * @return
     * @throws IOException
     */
    public CreateIndexResponse createIndex(String index, String jsonMapping) throws IOException {
        CreateIndexRequest request = new CreateIndexRequest(index);
        request.source(jsonMapping, XContentType.JSON);
        return restHighLevelClient.indices().create(request, RequestOptions.DEFAULT);
    }

    /**
     * 删除索引库
     * @param index 索引库名
     */
    public boolean deleteIndex(String index) throws IOException {
        DeleteIndexRequest deleteRequest = new DeleteIndexRequest(index);
        AcknowledgedResponse response = restHighLevelClient.indices().delete(deleteRequest, RequestOptions.DEFAULT);
        return response.isAcknowledged();
    }


    /**
     * 创建文档 指定id
     * @param index 索引
     * @param id 文档id
     * @param jsonString 文档内容
     */
    public IndexResponse createDocument(String index, String id, String jsonString) throws IOException {
        IndexRequest request = new IndexRequest(index)
                .id(id)
                .source(jsonString, XContentType.JSON);
        return restHighLevelClient.index(request, RequestOptions.DEFAULT);
    }

    /**
     * 创建文档
     * @param index 索引
     * @param jsonString 文档内容
     */
    public IndexResponse createDocument(String index, String jsonString) throws IOException {
        IndexRequest request = new IndexRequest(index)
                .source(jsonString,XContentType.JSON);
        return restHighLevelClient.index(request, RequestOptions.DEFAULT);
    }

    /**
     * 获取文档
     * @param index 索引库
     * @param id 文档id
     */
    public String getDocument(String index, String id) throws IOException {
        GetRequest request = new GetRequest(index, id);
        return restHighLevelClient.get(request, RequestOptions.DEFAULT).getSourceAsString();
    }


    /**
     * 删除文档
     * @param index 索引库名
     * @param id 文档id
     */
    public boolean deleteDocument(String index, String id) throws IOException {
        DeleteRequest deleteRequest = new DeleteRequest(index, id);
        DocWriteResponse.Result result = restHighLevelClient.delete(deleteRequest, RequestOptions.DEFAULT).getResult();

        return result == DocWriteResponse.Result.DELETED;
    }

    /**
     * 更新文档
     * @param index 索引库名称
     * @param id 文档id
     * @param jsonString 更新的内容
     */
    public UpdateResponse updateDocument(String index, String id, String jsonString) throws IOException {
        UpdateRequest request = new UpdateRequest(index, id)
                .doc(jsonString, XContentType.JSON);
        return restHighLevelClient.update(request, RequestOptions.DEFAULT);
    }

    /**
     * 查询文档
     * @param index 索引库
     * @param query 查询条件
     */
    public SearchResponse searchDocuments(String index, SearchSourceBuilder query) throws IOException {
        SearchRequest searchRequest = new SearchRequest(index);
        searchRequest.source(query);
        return restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
    }

    /**
     * 批量插入,
     * @param index    索引库名
     * @param dataList <id,data>文档列表
     * @return
     */
    //批量插入
    public <T> BulkResponse bulkCreateDocument(String index, List<Pair<String,T>> dataList) throws IOException {
        BulkRequest request = new BulkRequest();
        for(Pair<String, T> pair : dataList){
            request.add(new IndexRequest(index)
                    .id(pair.getKey())
                    .source(JSON.toJSONString(pair.getValue()), XContentType.JSON));
        }
        return restHighLevelClient.bulk(request, RequestOptions.DEFAULT);
    }

    /**
     * 批量删除
     * @param index  索引库名
     * @param idList 需要删除的文档id
     * @return
     */
    public BulkResponse bulkDeleteDocument(String index, List<String> idList) throws IOException {
        BulkRequest request = new BulkRequest();
        for(String id : idList){
            request.add(new DeleteRequest(index, id));
        }
        return restHighLevelClient.bulk(request, RequestOptions.DEFAULT);
    }


}
```
### 使用
```java

@SpringBootTest
public class EsTest {

    @Autowired
    private ESClient client;

    @Test
    public void createIndex() throws IOException {

        Settings.Builder settings = Settings.builder()
                .put("number_of_shards", 5) //分片数量
                .put("number_of_replicas", 1); //备份分片数量

        //构造mappings
        XContentBuilder mappings = JsonXContent.contentBuilder()
                .startObject() //这个相当于{
                    .startObject("properties") //当属性名相当于 properties:{
                        .startObject("id")
                        .field("type", "long")
                        .endObject()
                        .startObject("title")
                        .field("type", "text")
                        .field("analyzer", "ik_max_word")
                        .endObject()
                        .startObject("price")
                        .field("type", "integer")
                        .endObject()
                    .endObject()
                .endObject(); //这个相当于}
        System.out.println(mappings);
        boolean success = client.createIndex("goods", settings, mappings);
        System.out.println(success);
    }

    @Test
    public void deleteIndex() throws IOException {
        boolean success = client.deleteIndex("goods");
        System.out.println(success);
    }


    @Test
    public void createDocument() throws IOException {

        for(int i=1;i<=10;i++){
            Goods goods = new Goods((long) i,"goods_"+i,20.0);
            client.createDocument("goods",goods.getId().toString(), JSON.toJSONString(goods));
        }
    }

    @Test
    public void getDocument() throws IOException {
        String source = client.getDocument("goods", "2");
        System.out.println(source);
    }

    @Test
    public void updateDocument() throws IOException {
        Goods goods = new Goods(1L, "goods_3", 30.0);
        IndexResponse response = client.createDocument("goods", "3", JSON.toJSONString(goods));
    }


    @Test
    public void deleteDocument() throws IOException {
        boolean success = client.deleteDocument("goods", "3");
        System.out.println(success);
    }


    @Test
    public void queryDocument() throws IOException {

        //SearchSourceBuilder构造搜索条件
        SearchSourceBuilder queryBuilder = new SearchSourceBuilder();
        queryBuilder.query(QueryBuilders.termQuery("price", 20.0));

        SearchResponse response = client.searchDocuments("goods", queryBuilder);
        //返回的response有hit数组，而每个hit中的source才是我们关心的数据
        response.getHits().forEach(hit -> System.out.println(hit.getSourceAsMap()));

    }


    @Test
    public void getAllDocument() throws IOException {
        SearchSourceBuilder builder = new SearchSourceBuilder();
        builder.query(QueryBuilders.matchAllQuery());
        SearchResponse response = client.searchDocuments("goods", builder);
        response.getHits().forEach(hit -> System.out.println(hit.getSourceAsMap()));
    }

    @Test
    public void bulkCreateDocument() throws IOException {
        List<Pair<String,Goods>> list = new ArrayList<>();
        for(int i=11;i<=20;i++){
            Goods goods = new Goods((long) i, "goods_"+i, i*10.0);
            list.add(new Pair<>(String.valueOf(i),goods));
        }
        client.bulkCreateDocument("goods",list);
    }

    @Test
    public void bulkDeleteDocument() throws IOException {
        List<String> ids = new ArrayList<>();
        for(int i=11;i<=20;i++){
            ids.add(String.valueOf(i));
        }
        client.bulkDeleteDocument("goods", ids);
    }


}
```