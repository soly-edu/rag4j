package soly.dev.rag.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import soly.dev.rag.embedding.EmbeddingEngineFactory;
import soly.dev.rag.entity.HttpResponseBody;
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
                                                       @RequestParam(value = "embeddingModel", defaultValue = "local_onnx") String embeddingModel) {
        embeddingModel = embeddingModel.toLowerCase();
        if (file.getSize() > maxFileSize) {
            return ResponseEntity.badRequest().body(HttpResponseBody.fail("文件大小超过限制，最大允许: " + (maxFileSize / (1024 * 1024)) + "MB"));
        }
        if (!embeddingEngineFactory.containsEngine(embeddingModel)) {
            return ResponseEntity.badRequest().body(HttpResponseBody.fail("嵌入模型 " + embeddingModel + " 未找到"));
        }
        return uploadService.upload(file, namespace, embeddingModel);
    }
}
