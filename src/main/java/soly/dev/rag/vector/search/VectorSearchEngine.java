package soly.dev.rag.vector.search;

public interface VectorSearchEngine {

    /**
     * 向量检索
     * @param namespace 命名空间
     * @param queryVector 查询向量
     * @param topK 返回的最相似向量数量
     * @return 检索结果列表
     */
    // VectorSearchResult search(String namespace, List<Double> queryVector, int topK);

    String getEngineType();
}
