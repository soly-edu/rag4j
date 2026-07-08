package soly.dev.rag.embedding;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public final class EmbeddingEngineFactory {

    private final Map<String, EmbeddingEngine> engineMap = new HashMap<>();

    public EmbeddingEngineFactory(List<EmbeddingEngine> embeddingEngineList) {
        for (EmbeddingEngine embeddingEngine : embeddingEngineList) {
            engineMap.put(embeddingEngine.getEngineType(), embeddingEngine);
        }
    }

    public EmbeddingEngine getEngine(String engineType) {
        return engineMap.get(engineType);
    }

    public boolean containsEngine(String engineType) {
        return engineMap.containsKey(engineType);
    }
}
