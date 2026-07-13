package soly.dev.rag.util;

import soly.dev.rag.entity.KnowledgeChunk;
import soly.dev.rag.entity.ScoredKnowledgeChunk;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;

public class SimilarityUtils {
    private SimilarityUtils() {
        /* This utility class should not be instantiated */
    }

    public static List<ScoredKnowledgeChunk> searchTopK(float[] queryVector, List<KnowledgeChunk> chunks, int topK) {
        PriorityQueue<ScoredKnowledgeChunk> minHeap = new PriorityQueue<>(Comparator.comparing(ScoredKnowledgeChunk::getSimilarityScore));
        for (KnowledgeChunk chunk : chunks) {
            double similarityScore = cosineSimilarity(queryVector, chunk.getVector());
            ScoredKnowledgeChunk scoredKnowledgeChunk = new ScoredKnowledgeChunk(chunk, similarityScore);
            if (minHeap.size() < topK) {
                minHeap.offer(scoredKnowledgeChunk);
            } else {
                assert minHeap.peek() != null;
                if (similarityScore > minHeap.peek().getSimilarityScore()) {
                    minHeap.poll();
                    minHeap.offer(scoredKnowledgeChunk);
                }
            }
        }
        LinkedList<ScoredKnowledgeChunk> scoredKnowledgeChunks = new LinkedList<>();
        while (!minHeap.isEmpty()) {
            scoredKnowledgeChunks.addFirst(minHeap.poll());
        }
        return scoredKnowledgeChunks;
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
