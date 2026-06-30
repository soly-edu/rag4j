package soly.dev.rag.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import soly.dev.rag.entity.HttpResponseBody;
import soly.dev.rag.service.UploadService;

@RestController
@RequestMapping("/upload")
public class UploadController {

    private final UploadService uploadService;

    @Autowired
    public UploadController(UploadService uploadService) {
        this.uploadService = uploadService;
    }

    @PostMapping("/file")
    public ResponseEntity<HttpResponseBody> uploadFile(@RequestParam(value = "file") MultipartFile file, @RequestParam(value = "namespace", defaultValue = "default") String namespace) {
        return uploadService.upload(file, namespace);
    }
}
