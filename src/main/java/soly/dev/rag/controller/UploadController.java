package soly.dev.rag.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import soly.dev.rag.constants.SplitterStrategy;
import soly.dev.rag.constants.StaticRule;
import soly.dev.rag.embedding.EmbeddingEngineFactory;
import soly.dev.rag.entity.HttpResponseBody;
import soly.dev.rag.entity.UploadRequestContext;
import soly.dev.rag.service.UploadService;

@RestController
@RequestMapping("/upload")
public class UploadController {

    private final UploadService uploadService;

    private final EmbeddingEngineFactory embeddingEngineFactory;

    @Value("#{${rag.core.max-file-size} * 1024 * 1024}")
    private long maxFileSize;

    public UploadController(UploadService uploadService,  EmbeddingEngineFactory embeddingEngineFactory) {
        this.uploadService = uploadService;
        this.embeddingEngineFactory = embeddingEngineFactory;
    }

    @PostMapping("/file")
    public ResponseEntity<HttpResponseBody> uploadFile(@RequestParam(value = "file") MultipartFile file,
                                                       @RequestParam(value = "namespace", defaultValue = "default") String namespace,
                                                       @RequestParam(value = "embedding_model", defaultValue = "local_onnx") String embeddingModel,
                                                       @RequestParam(value = "splitter_strategy", defaultValue = "static_rule") String splitterStrategy,
                                                       @RequestParam(value = "static_rule", required = false, defaultValue = "newline") String staticRule,
                                                       @RequestParam(value = "custom_pattern", required = false) String customPattern) {
        embeddingModel = embeddingModel.toLowerCase();
        if (file.getSize() > maxFileSize) {
            return ResponseEntity.badRequest().body(HttpResponseBody.fail("文件大小超过限制，最大允许: " + (maxFileSize / (1024 * 1024)) + "MB"));
        }
        if (!embeddingEngineFactory.containsEngine(embeddingModel)) {
            return ResponseEntity.badRequest().body(HttpResponseBody.fail("嵌入模型 " + embeddingModel + " 未找到"));
        }

        // 构建上下文
        UploadRequestContext requestContext = buildRequestContext(file, namespace, embeddingModel, splitterStrategy, staticRule, customPattern);

        return uploadService.upload(requestContext);
    }

    public static UploadRequestContext buildRequestContext(MultipartFile file, String namespace, String embeddingModel, String splitterStrategy, String staticRule, String customPattern) {
        return new UploadRequestContext(file, namespace, embeddingModel,
                SplitterStrategy.valueOf(splitterStrategy.toUpperCase()),
                StaticRule.valueOf(staticRule.toUpperCase()),
                customPattern);
    }
}
