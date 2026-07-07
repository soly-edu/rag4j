package soly.dev.rag.embedding.impl;

import org.springframework.ai.transformers.TransformersEmbeddingModel;
import org.springframework.stereotype.Component;
import soly.dev.rag.constants.EmbeddingModel;
import soly.dev.rag.embedding.EmbeddingEngine;

import java.util.List;

@Component
public class LocalTransformersEngine implements EmbeddingEngine {

    private final TransformersEmbeddingModel embeddingModel;

    public LocalTransformersEngine(TransformersEmbeddingModel embeddingModel) {
        this.embeddingModel = embeddingModel;
    }

    @Override
    public float[] embed(String text) {
        List<Double> embedded = embeddingModel.embed(text);
        return toFloatArray(embedded);
    }

    @Override
    public List<float[]> embed(List<String> texts) {
        List<List<Double>> embedded = embeddingModel.embed(texts);
        return embedded.stream()
                .map(this::toFloatArray)
                .toList();
    }

    private float[] toFloatArray(List<Double> list) {
        float[] floats = new float[list.size()];
        for (int i = 0; i < list.size(); i++) {
            floats[i] = list.get(i).floatValue();
        }
        return floats;
    }

    @Override
    public String getEngineType() {
        return EmbeddingModel.LOCAL_ONNX.getId();
    }
}
