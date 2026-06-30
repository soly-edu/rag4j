package com.example.rag.service;

import com.example.rag.model.ScoredDocument;
import com.example.rag.repository.HybridDatabase;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 服務層：處理在線問答流水線的核心業務邏輯 (Online Retrieval)
 */
@Service
public class AdvancedRagService {

    private final ChatClient chatClient;
    private final HybridDatabase hybridDatabase;

    public AdvancedRagService(ChatClient.Builder chatClientBuilder, HybridDatabase hybridDatabase) {
        this.chatClient = chatClientBuilder.build();
        this.hybridDatabase = hybridDatabase;
    }

    public String processQuery(String query) {
        System.out.println("\n========== 🚀 收到客戶提問: " + query + " ==========");
        String chatHistory = "用戶上一句問：公司有什麼好吃的水果福利嗎？";

        // 【維度二：查詢重寫】
        String rewrittenQuery = rewriteQuery(query, chatHistory);
        System.out.println("🤖 【維度二】LLM 查詢重寫結果: " + rewrittenQuery);

        // 【維度三：混合檢索 (Hybrid Search) & RRF】
        List<Document> denseResults = hybridDatabase.vectorSearch(rewrittenQuery, 5);
        List<Document> sparseResults = hybridDatabase.keywordSearch(query);
        List<ScoredDocument> rrfResults = applyRRF(denseResults, sparseResults);

        // 【維度四：精準重排 (Reranking)】
        System.out.println("\n⚖️ 【維度四】呼叫 LLM 作為裁判進行重排打分...");
        List<ScoredDocument> rerankedResults = rerank(rrfResults, rewrittenQuery);

        String bestContext = rerankedResults.isEmpty() ? "無相關資料" : rerankedResults.get(0).getDocument().getContent();

        // 【最終生成】
        System.out.println("\n📝 呼叫 LLM 進行最終總結...");
        String finalAnswer = generateAnswer(query, bestContext);
        System.out.println("✨ 最終回覆: " + finalAnswer);

        return finalAnswer;
    }

    private String rewriteQuery(String query, String chatHistory) {
        String rewritePrompt = String.format(
            "使用者搜尋了關鍵詞：'%s'。結合歷史對話：'%s'。請推斷他的真實意圖，並將其擴寫為一句適合搜尋引擎的長句。只返回長句本身。", 
            query, chatHistory
        );
        return chatClient.prompt().user(rewritePrompt).call().content().trim();
    }

    private List<ScoredDocument> applyRRF(List<Document> dense, List<Document> sparse) {
        Map<String, ScoredDocument> resultMap = new HashMap<>();
        final int K = 60;

        for (int i = 0; i < dense.size(); i++) {
            Document doc = dense.get(i);
            resultMap.putIfAbsent(doc.getId(), new ScoredDocument(doc, 0.0));
            resultMap.get(doc.getId()).addScore(1.0 / (K + i + 1));
        }

        for (int i = 0; i < sparse.size(); i++) {
            Document doc = sparse.get(i);
            resultMap.putIfAbsent(doc.getId(), new ScoredDocument(doc, 0.0));
            resultMap.get(doc.getId()).addScore(1.0 / (K + i + 1));
        }

        List<ScoredDocument> resultList = new ArrayList<>(resultMap.values());
        resultList.sort((d1, d2) -> Double.compare(d2.getScore(), d1.getScore()));
        return resultList;
    }

    private List<ScoredDocument> rerank(List<ScoredDocument> rrfResults, String rewrittenQuery) {
        List<ScoredDocument> rerankedResults = new ArrayList<>();

        for (ScoredDocument sDoc : rrfResults) {
            String rerankPrompt = String.format(
                "你是一個精準的相關性打分系統。請評估 [文本] 對於回答 [問題] 的相關性。\n" +
                "請給出 0 到 10 之間的純數字分數 (例如 8.5)。分數越高代表邏輯越吻合。\n" +
                "[問題]: %s\n[文本]: %s", 
                rewrittenQuery, sDoc.getDocument().getContent()
            );
            
            try {
                String scoreStr = chatClient.prompt().user(rerankPrompt).call().content().trim().replaceAll("[^0-9.]", ""); 
                double logicScore = Double.parseDouble(scoreStr);
                
                sDoc.setScore(logicScore);
                rerankedResults.add(sDoc);
                System.out.printf("   -> 文本: %-30s | LLM 評分: %.1f\n", 
                    sDoc.getDocument().getContent().substring(0, 20) + "...", logicScore);
            } catch (Exception e) {
                System.out.println("打分解析失敗，略過此條目。");
            }
        }

        rerankedResults.sort((d1, d2) -> Double.compare(d2.getScore(), d1.getScore()));
        return rerankedResults;
    }

    private String generateAnswer(String query, String context) {
        String finalPrompt = String.format(
            "請根據以下背景資料，回答使用者的問題。如果資料無關，請說不知道。\n背景資料：%s\n使用者問題：%s",
            context, query
        );
        return chatClient.prompt().user(finalPrompt).call().content();
    }
}