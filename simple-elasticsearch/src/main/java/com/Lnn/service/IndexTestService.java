package com.Lnn.service;

import java.util.Map;

public interface IndexTestService {

    public boolean indexCreate() throws Exception;

    public Map<String,Object> getMapping(String indexName) throws Exception;

    public boolean indexDelete(String indexName) throws Exception;

    public boolean indexExists(String indexName) throws Exception;
}