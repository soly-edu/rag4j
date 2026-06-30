package soly.dev.rag.entity;

/**
 * 强类型的来源元数据 (Universal Meta)
 */
public class ChunkSourceMeta {
    // 强制使用明确的类型
    /**
     * 来源文件名称
     */
    private String sourceFileName;
    /**
     * 来源文件路径
     */
    private String sourceFilePath;
    /**
     * 来源文件 Hash
     */
    private String sourceFileHash;

    public String getSourceFileName() {
        return sourceFileName;
    }

    public void setSourceFileName(String sourceFileName) {
        this.sourceFileName = sourceFileName;
    }

    public String getSourceFilePath() {
        return sourceFilePath;
    }

    public void setSourceFilePath(String sourceFilePath) {
        this.sourceFilePath = sourceFilePath;
    }

    public String getSourceFileHash() {
        return sourceFileHash;
    }

    public void setSourceFileHash(String sourceFileHash) {
        this.sourceFileHash = sourceFileHash;
    }
}
