package soly.dev.springaitest;

import org.springframework.ai.transformers.TransformersEmbeddingModel;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class EmbeddingTestController {



    @GetMapping("/embedding/similarity/test")
    public String similarity(@RequestParam(value = "text1", defaultValue = "Banana") String text1,
                             @RequestParam(value = "text2", defaultValue = "Food") String text2,
                             @RequestParam(value = "text3", defaultValue = "Keyboard") String text3) {

        TransformersEmbeddingModel embeddingModel = new TransformersEmbeddingModel();
        try {
            embeddingModel.afterPropertiesSet();
        } catch (Exception e) {
            throw new IllegalStateException("TransformersEmbeddingModel 初始化失败");
        }
        List<Double> vectorText1 = embeddingModel.embed(text1);
        List<Double> vectorText2 = embeddingModel.embed(text2);
        List<Double> vectorText3 = embeddingModel.embed(text3);

        double similarityText1Text2 = SimpleVectorStore.EmbeddingMath.cosineSimilarity(vectorText1, vectorText2);
        double similarityText1Text3 = SimpleVectorStore.EmbeddingMath.cosineSimilarity(vectorText1, vectorText3);

        return String.format("本地离线嵌入引擎测试结果：%n【%s】 vs 【%s】 相似度：%.4f %n【%s】 vs 【%s】 相似度：%.4f",
                text1, text2, similarityText1Text2,
                text1, text3, similarityText1Text3);

    }

}
