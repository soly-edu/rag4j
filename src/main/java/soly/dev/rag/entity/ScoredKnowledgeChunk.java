package soly.dev.rag.entity;


public class ScoredKnowledgeChunk {

    private final KnowledgeChunk chunk;

    private final double similarityScore;

    public ScoredKnowledgeChunk(KnowledgeChunk chunk, double similarityScore) {
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
