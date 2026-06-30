package soly.dev.rag.vector.repo;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;
import soly.dev.rag.entity.KnowledgeChunk;
import soly.dev.rag.util.ObjectMapperUtils;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;

@Repository
@ConditionalOnProperty(name = "rag.embedding.vector-store.type", havingValue = "LOCAL_JSONL", matchIfMissing = true)
public class LocalJsonlVectorStore implements VectorStoreRepository {

    @Value("${rag.embedding.vector-store.path}")
    private String vectorStorePath;

    @Value("${rag.embedding.vector-store.extension}")
    private String fileExt;

    @Value("${rag.embedding.type}")
    private String embeddingType;

    @Override
    public void saveAll(String namespace, List<KnowledgeChunk> chunks) {
        // 将分片数据保存到本地 JSONL 文件中
        Path path = Paths.get(vectorStorePath, String.format("%s_%s%s", namespace, embeddingType, fileExt));
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
    public String getStoreType() {
        return "LOCAL_JSONL";
    }
}
