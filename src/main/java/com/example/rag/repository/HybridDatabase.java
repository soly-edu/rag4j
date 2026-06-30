package com.example.rag.repository;

import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 儲存層：模擬混合檢索資料庫
 * 封裝了 Spring AI 的 VectorStore 以及傳統的關鍵詞儲存
 */
@Repository
public class HybridDatabase {
    
    private final VectorStore vectorStore;
    // 模擬傳統關聯式資料庫或 Elasticsearch 的字面儲存
    private final List<Document> keywordDatabase = new ArrayList<>();

    public HybridDatabase(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    /**
     * 同時將資料存入向量庫與關鍵詞庫
     */
    public void save(Document doc) {
        vectorStore.add(List.of(doc));
        keywordDatabase.add(doc);
    }

    /**
     * 向量語義檢索 (Dense Retrieval)
     */
    public List<Document> vectorSearch(String query, int topK) {
        return vectorStore.similaritySearch(SearchRequest.query(query).withTopK(topK));
    }

    /**
     * 關鍵詞字面檢索 (Sparse/BM25 Retrieval)
     */
    public List<Document> keywordSearch(String query) {
        return keywordDatabase.stream()
                .filter(doc -> doc.getContent().toLowerCase().contains(query.toLowerCase()))
                .collect(Collectors.toList());
    }
}