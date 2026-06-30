package com.example.rag.model;

import org.springframework.ai.document.Document;

/**
 * 封裝帶有分數的文檔，用於 RRF 混合計票與 LLM 重排
 */
public class ScoredDocument {
    private Document document;
    private double score;

    public ScoredDocument(Document document, double score) {
        this.document = document;
        this.score = score;
    }

    public Document getDocument() { 
        return document; 
    }
    
    public double getScore() { 
        return score; 
    }
    
    public void setScore(double score) { 
        this.score = score; 
    }
    
    public void addScore(double scoreToAdd) { 
        this.score += scoreToAdd; 
    }
}