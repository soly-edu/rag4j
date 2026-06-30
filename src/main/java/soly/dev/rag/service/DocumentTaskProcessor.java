package soly.dev.rag.service;

import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import soly.dev.rag.embedding.EmbeddingEngine;
import soly.dev.rag.embedding.EmbeddingEngineFactory;
import soly.dev.rag.entity.KnowledgeChunk;
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

    @Value("${rag.upload-file-path}")
    private String uploadDir;

    @Value("${rag.embedding.type}")
    private String embeddingType;

    private final EmbeddingEngineFactory  embeddingEngineFactory;

    private final VectorStoreRepository vectorStoreRepository;

    public DocumentTaskProcessor(EmbeddingEngineFactory embeddingEngineFactory, VectorStoreRepository vectorStoreRepository) {
        this.embeddingEngineFactory = embeddingEngineFactory;
        this.vectorStoreRepository = vectorStoreRepository;
    }

    public void taskExecute(String uniqueFilename, String namespace){
        File sourceFile = Paths.get(uploadDir, uniqueFilename).toFile();
        if (!sourceFile.exists()) {
            throw new IllegalArgumentException("物理文件丢失或不存在，处理失败: " + uniqueFilename);
        }
        String sourceFilePath = sourceFile.getAbsolutePath();
        String fileExtension = FileUtils.getFileExtension(uniqueFilename);
        LOGGER.debug("开始处理文档: {}，文件格式：{}，当前线程: {}" , uniqueFilename, fileExtension, Thread.currentThread().getName());
        String fullContent = DocumentParserFactory.parseContent(sourceFilePath, fileExtension);
        // TODO 1. 文档分片
        List<KnowledgeChunk> chunkList = TextSplitHelper.split(namespace, embeddingType, uniqueFilename, sourceFilePath, fullContent);
        // 批量生成向量
        List<String> chunkContentList = chunkList.stream().map(KnowledgeChunk::getChunkContent).toList();
        EmbeddingEngine engine = embeddingEngineFactory.getEngine(embeddingType);
        if (ObjectUtils.isEmpty(engine)) {
            throw new IllegalStateException("EmbeddingEngine 初始化失败，类型: " + embeddingType);
        }
        List<float[]> vectorList = engine.embed(chunkContentList);
        // 为分片装载向量
        for (int i = 0; i < chunkList.size(); i++) {
            chunkList.get(i).setVector(vectorList.get(i));
        }
        // TODO 2. 分片入库
        vectorStoreRepository.saveAll(namespace, chunkList);
    }
}
