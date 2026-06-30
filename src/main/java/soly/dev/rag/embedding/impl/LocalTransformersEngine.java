package soly.dev.rag.embedding.impl;

import org.springframework.ai.transformers.TransformersEmbeddingModel;
import org.springframework.stereotype.Component;
import soly.dev.rag.embedding.EmbeddingEngine;

import java.util.ArrayList;
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
        float[] result = new float[embedded.size()];
        for (int i = 0; i < embedded.size(); i++) {
            result[i] = embedded.get(i).floatValue();
        }
        return result;
    }

    @Override
    public List<float[]> embed(List<String> texts) {
        List<List<Double>> embedded = embeddingModel.embed(texts);
        // 数据结构转换
        ArrayList<float[]> result = new ArrayList<>(embedded.size());
        for (List<Double> list : embedded) {
            float[] floats = new float[list.size()];
            for (int i = 0; i < list.size(); i++) {
                floats[i] = list.get(i).floatValue();
            }
            result.add(floats);
        }
        return result;
    }

    @Override
    public String getEngineType() {
        return "LOCAL_ONNX";
    }
}
