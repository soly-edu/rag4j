package soly.dev.rag.util;

import soly.dev.rag.entity.KnowledgeChunk;
import soly.dev.rag.entity.SearchResult;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;

public class SimilarityUtils {
    private SimilarityUtils() {
        /* This utility class should not be instantiated */
    }

    public static List<SearchResult> searchTopK(float[] queryVector, List<KnowledgeChunk> chunks, int topK) {
        PriorityQueue<SearchResult> minHeap = new PriorityQueue<>(Comparator.comparing(SearchResult::getSimilarityScore));
        for (KnowledgeChunk chunk : chunks) {
            double similarityScore = cosineSimilarity(queryVector, chunk.getVector());
            SearchResult searchResult = new SearchResult(chunk, similarityScore);
            if (minHeap.size() < topK) {
                minHeap.offer(searchResult);
            } else {
                assert minHeap.peek() != null;
                if (similarityScore > minHeap.peek().getSimilarityScore()) {
                    minHeap.poll();
                    minHeap.offer(searchResult);
                }
            }
        }
        LinkedList<SearchResult> searchResults = new LinkedList<>();
        while (!minHeap.isEmpty()) {
            searchResults.addFirst(minHeap.poll());
        }
        return searchResults;
    }

    private static double cosineSimilarity(float[] vectorA, float[] vectorB) {
        if (vectorA.length != vectorB.length) {
            throw new IllegalArgumentException("向量维度长度不一致，无法计算！");
        }

        double dotProduct = 0.0;
        double normA = 0.0;
        double normB = 0.0;

        for (int i = 0; i < vectorA.length; i++) {
            dotProduct += vectorA[i] * vectorB[i];
            normA += Math.pow(vectorA[i], 2);
            normB += Math.pow(vectorB[i], 2);
        }

        return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
    }
}
