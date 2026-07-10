package soly.dev.rag.vector.repo;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;
import soly.dev.rag.constants.VectorStoreType;
import soly.dev.rag.entity.KnowledgeChunk;
import soly.dev.rag.entity.ScoredKnowledgeChunk;
import soly.dev.rag.util.ObjectMapperUtils;
import soly.dev.rag.util.SimilarityUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;

@Repository
@ConditionalOnProperty(name = "rag.vector-store.type", havingValue = "LOCAL_JSONL", matchIfMissing = true)
public class LocalJsonlVectorStore implements VectorStoreRepository {

    @Value("${rag.vector-store.local-jsonl.storage-path}")
    private String vectorStorePath;

    @Value("${rag.vector-store.local-jsonl.file-extension}")
    private String fileExt;

    @Override
    public void saveAll(String namespace, String embeddingModel, List<KnowledgeChunk> chunks) {
        // 将分片数据保存到本地 JSONL 文件中
        Path path = Paths.get(vectorStorePath, String.format("%s_%s%s", namespace, embeddingModel, fileExt));
        if (path.getParent() != null) {
            try {
                Files.createDirectories(path.getParent());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {

            for (KnowledgeChunk chunk : chunks) {
                writer.write(ObjectMapperUtils.toJson(chunk));
                writer.newLine();
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to save chunks to local JSONL file", e);
        }
    }

    @Override
    public List<ScoredKnowledgeChunk> searchTopK(String namespace, String embeddingModel, int topK, float[] queryVector) {
        // 先检查有没有对应的向量分片文件
        Path path = Paths.get(vectorStorePath, String.format("%s_%s%s", namespace, embeddingModel, fileExt));
        if (!path.toFile().exists()) {
            throw new IllegalArgumentException("Vector store file not found: " + path);
        }

        List<KnowledgeChunk> chunks;
        try(BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            chunks = reader.lines().map(line -> ObjectMapperUtils.toEntity(line, KnowledgeChunk.class)).toList();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return SimilarityUtils.searchTopK(queryVector, chunks, topK);
    }

    @Override
    public String getStoreType() {
        return VectorStoreType.LOCAL_JSONL.toString();
    }
}
