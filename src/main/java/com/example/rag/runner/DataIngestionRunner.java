package com.example.rag.runner;

import com.example.rag.repository.HybridDatabase;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * 啟動層：負責離線資料建庫流水線 (Offline Ingestion)
 * 專案啟動時自動執行
 */
@Component
public class DataIngestionRunner implements CommandLineRunner {

    private final ChatClient chatClient;
    private final HybridDatabase hybridDatabase;

    public DataIngestionRunner(ChatClient.Builder chatClientBuilder, HybridDatabase hybridDatabase) {
        this.chatClient = chatClientBuilder.build();
        this.hybridDatabase = hybridDatabase;
    }

    @Override
    public void run(String... args) {
        System.out.println("========== 📦 開始離線資料建庫 ==========");
        List<String> rawTexts = Arrays.asList(
            "Apple released a new M4 chip for MacBook Pro today.",
            "The company cafeteria will provide free apple and banana fruits every Friday."
        );

        for (String rawText : rawTexts) {
            // 【維度一：上下文增強】真實呼叫 LLM 提取分類
            String prompt = "請用一個中括號標籤概括以下文字的領域(例如[科技]或[食物])，只輸出標籤不要其他廢話：\n" + rawText;
            String tag = chatClient.prompt().user(prompt).call().content().trim();
            
            String enrichedText = tag + " " + rawText;
            System.out.println("✅ 增強後的文本: " + enrichedText);

            // 存入混合資料庫
            hybridDatabase.save(new Document(enrichedText));
        }
        System.out.println("========== 📦 建庫完成 ==========");
    }
}