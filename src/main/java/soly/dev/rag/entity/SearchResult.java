package soly.dev.rag.entity;


public class SearchResult {

    private final KnowledgeChunk chunk;

    private final double similarityScore;

    public SearchResult(KnowledgeChunk chunk, double similarityScore) {
        this.chunk = chunk;
        this.similarityScore = similarityScore;
    }

    public KnowledgeChunk getChunk() {
        return chunk;
    }

    public double getSimilarityScore() {
        return similarityScore;
    }
}
