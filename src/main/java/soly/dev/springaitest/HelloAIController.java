package soly.dev.springaitest;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.zhipuai.ZhiPuAiChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloAIController {

    // private ChatClient chatClient;
    // @Autowired
    // public HelloAIController(ChatClient.Builder chatClientBuilder) {
    //     this.chatClient = chatClientBuilder.build();
    // }
    // @GetMapping("/ask")
    // public String askAi(@RequestParam(value = "msg", defaultValue = "你好，你是谁？") String msg) {
    //     return chatClient.prompt()
    //             .user(msg)
    //             .call()
    //             .content();
    // }

    private final ChatClient deepseekChatClient;

    private final ChatClient zhiPuAiChatClient;

    // @Autowired
    public HelloAIController(OpenAiChatModel deepseekChatModel, ZhiPuAiChatModel zhiPuAiChatModel) {
        this.deepseekChatClient = ChatClient.builder(deepseekChatModel)
                .defaultAdvisors(new MessageChatMemoryAdvisor(new InMemoryChatMemory()))
                .build();
        this.zhiPuAiChatClient = ChatClient.builder(zhiPuAiChatModel)
                .defaultAdvisors(new MessageChatMemoryAdvisor(new InMemoryChatMemory()))
                .build();
    }

    @GetMapping("/ask/deepseek")
    public String askDeepseek(@RequestParam(value = "msg", defaultValue = "你好，你是谁？") String msg) {
        return deepseekChatClient.prompt()
                .user(msg)
                .call()
                .content();
    }

    @GetMapping("/ask/glm")
    public String askZhiPuAi(@RequestParam(value = "msg", defaultValue = "你好，你是谁？") String msg) {
        return zhiPuAiChatClient.prompt()
                .user(msg)
                .call()
                .content();
    }

    @GetMapping("/ask/deepseek/user")
    public String askDeepseekByUserId(@RequestParam(value = "msg", defaultValue = "你好，你是谁？") String msg,
                                      @RequestParam(value = "userId", defaultValue = "001") String userId){
        return deepseekChatClient.prompt()
                .user(msg)
                .advisors(advisorSpec -> advisorSpec.param(AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY, userId))
                .call()
                .content();
    }


}
