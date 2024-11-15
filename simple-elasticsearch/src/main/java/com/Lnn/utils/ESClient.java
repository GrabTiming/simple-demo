package com.Lnn.utils;

import com.alibaba.fastjson2.JSON;
import javafx.util.Pair;
import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

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
    public <T> boolean bulkCreateDocument(String index, List<Pair<String,T>> dataList) throws IOException {
        BulkRequest request = new BulkRequest();
        for(Pair<String, T> pair : dataList){
            request.add(new IndexRequest(index)
                    .id(pair.getKey())
                    .source(JSON.toJSONString(pair.getValue()), XContentType.JSON));
        }
        return !restHighLevelClient.bulk(request, RequestOptions.DEFAULT).hasFailures();
    }

    /**
     * 批量删除
     * @param index  索引库名
     * @param idList 需要删除的文档id
     * @return
     */
    public boolean bulkDeleteDocument(String index, List<String> idList) throws IOException {
        BulkRequest request = new BulkRequest();
        for(String id : idList){
            request.add(new DeleteRequest(index, id));
        }
        return !restHighLevelClient.bulk(request, RequestOptions.DEFAULT).hasFailures();
    }

}
