package soly.dev.rag.util;

import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import soly.dev.rag.entity.KnowledgeChunk;
import soly.dev.rag.entity.SearchResult;

import java.util.ArrayList;
import java.util.List;

public class RagPromptBuilder {
    private RagPromptBuilder() {
        /* This utility class should not be instantiated */
    }


    public static List<Message> buildRagPrompt(String question, List<SearchResult> searchResults) {

        ArrayList<Message> messages = new ArrayList<>();
        String ragSystemPrompt = "你是一个专业的企业级知识库问答AI助手。" +
                "你需要根据用户的问题，必须严格根据给定的 <context> 标签内的参考资料回答。" +
                "如果参考资料中没有相关答案，请直接回答“没有匹配资料，无法根据知识库回答”，绝不允许编造。" +
                "回答时需要输出你的答案、分片内容、分片标识、资料来源和相似度分数，按照固定格式输出";

        messages.add(new SystemMessage(ragSystemPrompt));
        StringBuilder contextBuilder = new StringBuilder();
        for (int i = 0; i < searchResults.size(); i++) {
            KnowledgeChunk chunk = searchResults.get(i).getChunk();
            String context = String.format("[片段%d]%n分片内容:%s%n分片标识:%s%n来源:%s%n相似度:%.4f%n%n",
                    i + 1, chunk.getChunkContent(), chunk.getChunkId(), chunk.getSourceMeta().getSourceFileName(), searchResults.get(i).getSimilarityScore());
            contextBuilder.append(context);
        }
        String userPrompt = String.format("<context>%s</context>%n[用户问题]: %s", contextBuilder, question);
        messages.add(new UserMessage(userPrompt));

        return messages;
    }
}
