package soly.dev.rag.embedding;

import java.util.List;

public interface EmbeddingEngine {

    float[] embed(String text);

    List<float[]> embed(List<String> texts);

    String getEngineType();
}
