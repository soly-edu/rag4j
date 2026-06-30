package soly.dev.rag.entity;

import java.util.Map;

public class KnowledgeChunk {

    /**
     * 分片唯一 ID（例: hash_chunk001）
     */
    private String chunkId;

    /**
     * 分片原文
     */
    private String chunkContent;

    /**
     * 嵌入向量，用于相似度计算
     */
    private float[] vector;

    /**
     * 命名空间（如 "tech"）
     */
    private String chunkNamespace;

    /**
     * 分片内容 Hash（用于去重校验）
     */
    private String chunkHash;

    /**
     * 分片索引（从 0 或 1 起，按项目约定）
     */
    private int chunkIndex;

    /**
     * 分片大小（字符数或字节数，按约定）
     */
    private int chunkSize;

    /**
     * 生成此向量的嵌入模型标识 (例如 "LOCAL_ONNX", "OPENAI")
     * TODO 指定嵌入模型解析出来的数据不能被其他嵌入模型检索召回，以避免不同模型的向量空间不一致导致无法召回结果或不准确。（解决方法：存放文件名上加嵌入模型标识、分片数据中增加嵌入模型标识）
     */
    private String chunkEmbeddingType;

    /**
     * 强类型通用元数据：存放过滤标签与溯源信息。
     * 预期示例字段：sourceFileName, sourceFilePath, sourceFileHash
     */
    private ChunkSourceMeta sourceMeta;

    /**
     * 扩展属性：用于存放业务特有的标签和值（保留灵活性）。
     * 例如：{"ide_version":"2025.1","author":"张三"}
     */
    private Map<String, Object> extAttributes;

    public String getChunkId() {
        return chunkId;
    }

    public void setChunkId(String chunkId) {
        this.chunkId = chunkId;
    }

    public String getChunkContent() {
        return chunkContent;
    }

    public void setChunkContent(String chunkContent) {
        this.chunkContent = chunkContent;
    }

    public float[] getVector() {
        return vector;
    }

    public void setVector(float[] vector) {
        this.vector = vector;
    }

    public String getChunkNamespace() {
        return chunkNamespace;
    }

    public void setChunkNamespace(String chunkNamespace) {
        this.chunkNamespace = chunkNamespace;
    }

    public String getChunkHash() {
        return chunkHash;
    }

    public void setChunkHash(String chunkHash) {
        this.chunkHash = chunkHash;
    }

    public int getChunkIndex() {
        return chunkIndex;
    }

    public void setChunkIndex(int chunkIndex) {
        this.chunkIndex = chunkIndex;
    }

    public int getChunkSize() {
        return chunkSize;
    }

    public void setChunkSize(int chunkSize) {
        this.chunkSize = chunkSize;
    }

    public String getChunkEmbeddingType() {
        return chunkEmbeddingType;
    }

    public void setChunkEmbeddingType(String chunkEmbeddingType) {
        this.chunkEmbeddingType = chunkEmbeddingType;
    }

    public ChunkSourceMeta getSourceMeta() {
        return sourceMeta;
    }

    public void setSourceMeta(ChunkSourceMeta sourceMeta) {
        this.sourceMeta = sourceMeta;
    }

    public Map<String, Object> getExtAttributes() {
        return extAttributes;
    }

    public void setExtAttributes(Map<String, Object> extAttributes) {
        this.extAttributes = extAttributes;
    }
}
