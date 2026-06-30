package soly.dev.rag.vector.repo;

import soly.dev.rag.entity.KnowledgeChunk;

import java.util.List;

public interface VectorStoreRepository {

    void saveAll(String namespace, List<KnowledgeChunk> chunks);

    String getStoreType();
}
