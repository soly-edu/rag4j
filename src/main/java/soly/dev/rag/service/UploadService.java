package soly.dev.rag.service;

import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import soly.dev.rag.dispatcher.DocumentTaskDispatcher;
import soly.dev.rag.entity.HttpResponseBody;
import soly.dev.rag.entity.UploadFileRespData;
import soly.dev.rag.util.FileUtils;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class UploadService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UploadService.class);

    @Value("${rag.core.upload-path}")
    private String uploadDir;

    // 根据配置获取对应的分发器
    private final DocumentTaskDispatcher dispatcher;

    public UploadService(DocumentTaskDispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }

    public ResponseEntity<HttpResponseBody> upload(MultipartFile file, String namespace, String embeddingModel) {
        HttpResponseBody responseBody;
        try {
            if (file.isEmpty()) {
                responseBody = HttpResponseBody.fail("文件不能为空");
                return ResponseEntity.badRequest().body(responseBody);
            }

            String originalFilename = file.getOriginalFilename();

            if (ObjectUtils.isEmpty(originalFilename)) {
                responseBody = HttpResponseBody.fail("文件名不能为空");
                return ResponseEntity.badRequest().body(responseBody);
            }
            //TODO 防重校验，看看有没有以该文件名结尾的文件，有的话依次取出做摘要对比，如果不一样则存放，否则直接成功，依旧使用那个文件

            String fileExtension = FileUtils.getFileExtension(originalFilename);
            if (!FileUtils.isValidFileExtension(fileExtension)) {
                responseBody = HttpResponseBody.fail("不支持的文件格式");
                return ResponseEntity.badRequest().body(responseBody);
            }

            String uniqueFilename = UUID.randomUUID().toString().replace("-", "") + "_" + originalFilename;
            Path uploadPath = Paths.get(uploadDir);
            if (Files.notExists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            Path filePath = uploadPath.resolve(uniqueFilename);
            Files.copy(file.getInputStream(), filePath);

            responseBody = HttpResponseBody.success(String.format("文件上传成功: %s", uniqueFilename));
            UploadFileRespData data = new UploadFileRespData(uniqueFilename, originalFilename, fileExtension, filePath.toString(), file.getSize(), file.getContentType());
            responseBody.setData(data);
            // 分片任务分发
            dispatcher.taskDispatch(uniqueFilename, namespace, embeddingModel);
            return ResponseEntity.ok(responseBody);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            responseBody = HttpResponseBody.fail("上传文件失败", e.getMessage());
            return ResponseEntity.badRequest().body(responseBody);
        }
    }

}
