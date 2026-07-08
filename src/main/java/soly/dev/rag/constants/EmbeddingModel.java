package soly.dev.rag.constants;

public enum EmbeddingModel {
    LOCAL_ONNX("local_onnx"),
    ;

    private final String id;

    EmbeddingModel(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
}
