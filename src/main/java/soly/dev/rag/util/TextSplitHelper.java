package soly.dev.rag.util;

import org.apache.tomcat.util.buf.HexUtils;
import org.springframework.stereotype.Component;
import soly.dev.rag.constants.SplitterStrategy;
import soly.dev.rag.constants.StaticRule;
import soly.dev.rag.entity.ChunkSourceMeta;
import soly.dev.rag.entity.KnowledgeChunk;
import soly.dev.rag.entity.UploadRequestContext;
import soly.dev.rag.splitter.DocumentSplitter;
import soly.dev.rag.splitter.DocumentSplitterFactory;
import soly.dev.rag.splitter.impl.StaticRuleSplitter;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
/**
 * 将完整内容切分并装配成 KnowledgeChunk 实体集合（不包含向量）
 */
@Component
public class TextSplitHelper {

    private final DocumentSplitterFactory documentSplitterFactory;

    public TextSplitHelper(DocumentSplitterFactory documentSplitterFactory) {
        this.documentSplitterFactory = documentSplitterFactory;
    }

    public List<KnowledgeChunk> split(UploadRequestContext requestContext, String sourceFilePath, String fullContent) {
        try {
            ArrayList<KnowledgeChunk> knowledgeChunks = new ArrayList<>();
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            // 计算并获取源文件的 Hash
            String sourceFileHash = HexUtils.toHexString(messageDigest.digest(fullContent.getBytes(StandardCharsets.UTF_8)));
            // 构造源文件元数据 (所有分片共享此引用，节省内存)
            ChunkSourceMeta chunkSourceMeta = new ChunkSourceMeta();
            chunkSourceMeta.setSourceFileName(requestContext.getUniqueFileName());
            chunkSourceMeta.setSourceFilePath(sourceFilePath);
            chunkSourceMeta.setSourceFileHash(sourceFileHash);

            // 执行物理切分
            DocumentSplitter staticRuleSplitter = documentSplitterFactory.getDocumentSplitter(requestContext);
            List<String> rawChunks = staticRuleSplitter.split(fullContent);

            //循环装配每一个分片
            for (int i = 0; i < rawChunks.size(); i++) {
                String chunkContent = rawChunks.get(i);
                // 构造 KnowledgeChunk 对象
                KnowledgeChunk chunk = new KnowledgeChunk();

                String chunkId = String.format("%s_chunk%05d", sourceFileHash, i + 1);
                chunk.setChunkId(chunkId);
                chunk.setChunkContent(chunkContent);
                chunk.setChunkSize(chunkContent.length());
                chunk.setChunkNamespace(requestContext.getNamespace());
                chunk.setChunkIndex(i + 1);
                chunk.setChunkHash(HexUtils.toHexString(messageDigest.digest(chunkContent.getBytes(StandardCharsets.UTF_8))));
                chunk.setChunkSplitRule(staticRuleSplitter.getStrategyName());
                chunk.setChunkEmbeddingType(requestContext.getEmbeddingModel());
                chunk.setSourceMeta(chunkSourceMeta);
                // 明确声明此时无向量，留给外层去批量生成并回填
                chunk.setVector(null);
                // chunk.setExtAttributes();
                knowledgeChunks.add(chunk);
            }

            return knowledgeChunks;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("系统不支持 SHA-256 哈希算法", e);
        }
    }
}
