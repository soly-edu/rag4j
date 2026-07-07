package soly.dev.rag.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import soly.dev.rag.embedding.EmbeddingEngine;
import soly.dev.rag.embedding.EmbeddingEngineFactory;
import soly.dev.rag.entity.KnowledgeChunk;
import soly.dev.rag.entity.SearchResult;
import soly.dev.rag.util.ObjectMapperUtils;
import soly.dev.rag.util.RagPromptBuilder;
import soly.dev.rag.util.SimilarityUtils;

import java.io.BufferedReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Service
public class QAService {

    @Value("${rag.vector-store.local-jsonl.storage-path}")
    private String vectorStorePath;

    @Value("${rag.vector-store.local-jsonl.file-extension}")
    private String fileExt;

    private final EmbeddingEngineFactory embeddingEngineFactory;

    private final ChatClient chatClient;

    public QAService(EmbeddingEngineFactory embeddingEngineFactory, OpenAiChatModel openAiChatModel) {
        this.embeddingEngineFactory = embeddingEngineFactory;
        this.chatClient = ChatClient.builder(openAiChatModel)
                // .defaultAdvisors(new MessageChatMemoryAdvisor(new InMemoryChatMemory()))// TODO 需要会话隔离则开启
                .build();
    }

    public List<SearchResult> searchSimilarTopK(String question, String namespace, String embeddingModel, int topK) {
        // TODO 需要根据分片存储方式进行分别处理
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
        EmbeddingEngine engine = embeddingEngineFactory.getEngine(embeddingModel);
        float[] queryVector = engine.embed(question);

        return SimilarityUtils.searchTopK(queryVector, chunks, topK);
    }

    public String askLLM(String question, List<SearchResult> searchResults) {
        List<Message> prompts = RagPromptBuilder.buildRagPrompt(question, searchResults);

        return chatClient.prompt().messages(prompts).call().content();
    }
}
