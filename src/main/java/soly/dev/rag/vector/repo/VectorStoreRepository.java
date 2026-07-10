package soly.dev.rag.vector.repo;

import soly.dev.rag.entity.KnowledgeChunk;
import soly.dev.rag.entity.ScoredKnowledgeChunk;

import java.util.List;

public interface VectorStoreRepository {

    void saveAll(String namespace, String embeddingModel, List<KnowledgeChunk> chunks);

    List<ScoredKnowledgeChunk> searchTopK(String namespace, String embeddingModel, int topK, float[] queryVector);

    // List<ScoredKnowledgeChunk> searchAll(String namespace, String embeddingModel, float[] queryVector);

    String getStoreType();
}
