package com.Lnn;

import com.Lnn.entity.Goods;
import com.Lnn.utils.ESClient;
import com.alibaba.fastjson2.JSON;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.util.Pair;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.json.JsonXContent;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
