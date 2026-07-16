package soly.dev.rag.entity;

import org.springframework.web.multipart.MultipartFile;
import soly.dev.rag.constants.SplitterStrategy;
import soly.dev.rag.constants.StaticRule;

public class UploadRequestContext {

    private MultipartFile file;
    private String namespace;
    private String embeddingModel;
    private SplitterStrategy splitterStrategy;
    private StaticRule staticRule;
    private String customPattern;
    private String uniqueFileName;

    public UploadRequestContext(MultipartFile file, String namespace, String embeddingModel, SplitterStrategy splitterStrategy, StaticRule staticRule, String customPattern) {
        this.file = file;
        this.namespace = namespace;
        this.embeddingModel = embeddingModel;
        this.splitterStrategy = splitterStrategy;
        this.staticRule = staticRule;
        this.customPattern = customPattern;
    }

    public MultipartFile getFile() {
        return file;
    }

    public void setFile(MultipartFile file) {
        this.file = file;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public String getEmbeddingModel() {
        return embeddingModel;
    }

    public void setEmbeddingModel(String embeddingModel) {
        this.embeddingModel = embeddingModel;
    }

    public SplitterStrategy getSplitterStrategy() {
        return splitterStrategy;
    }

    public void setSplitterStrategy(SplitterStrategy splitterStrategy) {
        this.splitterStrategy = splitterStrategy;
    }

    public StaticRule getStaticRule() {
        return staticRule;
    }

    public void setStaticRule(StaticRule staticRule) {
        this.staticRule = staticRule;
    }

    public String getCustomPattern() {
        return customPattern;
    }

    public void setCustomPattern(String customPattern) {
        this.customPattern = customPattern;
    }

    public String getUniqueFileName() {
        return uniqueFileName;
    }

    public void setUniqueFileName(String uniqueFileName) {
        this.uniqueFileName = uniqueFileName;
    }
}
