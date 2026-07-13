package soly.dev.rag.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.stereotype.Service;
import soly.dev.rag.embedding.EmbeddingEngine;
import soly.dev.rag.embedding.EmbeddingEngineFactory;
import soly.dev.rag.entity.ScoredKnowledgeChunk;
import soly.dev.rag.util.RagPromptBuilder;
import soly.dev.rag.vector.repo.VectorStoreRepository;

import java.util.List;

@Service
public class QAService {

    private final ChatClient chatClient;
    private final EmbeddingEngineFactory embeddingEngineFactory;
    private final VectorStoreRepository vectorStoreRepository;

    public QAService(OpenAiChatModel openAiChatModel, EmbeddingEngineFactory embeddingEngineFactory, VectorStoreRepository vectorStoreRepository) {
        this.chatClient = ChatClient.builder(openAiChatModel)
                // .defaultAdvisors(new MessageChatMemoryAdvisor(new InMemoryChatMemory()))// TODO 需要会话隔离则开启
                .build();
        this.embeddingEngineFactory = embeddingEngineFactory;
        this.vectorStoreRepository = vectorStoreRepository;
    }

    public List<ScoredKnowledgeChunk> searchSimilarTopK(String question, String namespace, String embeddingModel, int topK) {
        // TODO 需要根据分片存储方式进行分别处理
        EmbeddingEngine engine = embeddingEngineFactory.getEngine(embeddingModel);
        float[] queryVector = engine.embed(question);
        return vectorStoreRepository.searchTopK(namespace, embeddingModel, topK, queryVector);
    }

    public String askLLM(String question, List<ScoredKnowledgeChunk> scoredKnowledgeChunks) {
        List<Message> prompts = RagPromptBuilder.buildRagPrompt(question, scoredKnowledgeChunks);

        return chatClient.prompt().messages(prompts).call().content();
    }
}
