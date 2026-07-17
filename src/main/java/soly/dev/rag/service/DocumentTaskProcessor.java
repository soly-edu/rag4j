package soly.dev.rag.service;

import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import soly.dev.rag.embedding.EmbeddingEngine;
import soly.dev.rag.embedding.EmbeddingEngineFactory;
import soly.dev.rag.entity.KnowledgeChunk;
import soly.dev.rag.entity.UploadRequestContext;
import soly.dev.rag.parser.DocumentParserFactory;
import soly.dev.rag.vector.repo.VectorStoreRepository;
import soly.dev.rag.util.FileUtils;
import soly.dev.rag.util.TextSplitHelper;

import java.io.File;
import java.nio.file.Paths;
import java.util.List;

@Service
public class DocumentTaskProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(DocumentTaskProcessor.class);

    @Value("${rag.core.upload-path}")
    private String uploadDir;

    private final EmbeddingEngineFactory  embeddingEngineFactory;

    private final VectorStoreRepository vectorStoreRepository;

    private final TextSplitHelper textSplitHelper;

    public DocumentTaskProcessor(EmbeddingEngineFactory embeddingEngineFactory, VectorStoreRepository vectorStoreRepository,  TextSplitHelper textSplitHelper) {
        this.embeddingEngineFactory = embeddingEngineFactory;
        this.vectorStoreRepository = vectorStoreRepository;
        this.textSplitHelper = textSplitHelper;
    }

    public void taskExecute(UploadRequestContext requestContext){
        String uniqueFilename = requestContext.getUniqueFileName();
        String embeddingModel = requestContext.getEmbeddingModel();

        File sourceFile = Paths.get(uploadDir, uniqueFilename).toFile();
        if (!sourceFile.exists()) {
            throw new IllegalArgumentException("物理文件丢失或不存在，处理失败: " + uniqueFilename);
        }
        String sourceFilePath = sourceFile.getAbsolutePath();
        String fileExtension = FileUtils.getFileExtension(uniqueFilename);
        LOGGER.debug("开始处理文档: {}，文件格式：{}，当前线程: {}" , uniqueFilename, fileExtension, Thread.currentThread().getName());
        String fullContent = DocumentParserFactory.parseContent(sourceFilePath, fileExtension);
        // 1. 文档分片
        List<KnowledgeChunk> chunkList = textSplitHelper.split(requestContext, sourceFilePath, fullContent);
        // 批量生成向量
        List<String> chunkContentList = chunkList.stream().map(KnowledgeChunk::getChunkContent).toList();
        EmbeddingEngine engine = embeddingEngineFactory.getEngine(embeddingModel);
        if (ObjectUtils.isEmpty(engine)) {
            throw new IllegalStateException("EmbeddingEngine 初始化失败，类型: " + embeddingModel);
        }
        List<float[]> vectorList = engine.embed(chunkContentList);
        // 为分片装载向量
        for (int i = 0; i < chunkList.size(); i++) {
            chunkList.get(i).setVector(vectorList.get(i));
        }
        // 2. 分片入库
        vectorStoreRepository.saveAll(requestContext.getNamespace(), embeddingModel, chunkList);
    }
}
