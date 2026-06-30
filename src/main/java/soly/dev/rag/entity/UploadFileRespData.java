package soly.dev.rag.entity;

public class UploadFileRespData {

    private String filename;
    private String originalFilename;
    private String fileExtension;
    private String filePath;
    private long fileSize;
    private String contentType;

    public UploadFileRespData(String filename, String originalFilename, String fileExtension, String filePath, long fileSize, String contentType) {
        this.filename = filename;
        this.originalFilename = originalFilename;
        this.fileExtension = fileExtension;
        this.filePath = filePath;
        this.fileSize = fileSize;
        this.contentType = contentType;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getOriginalFilename() {
        return originalFilename;
    }

    public void setOriginalFilename(String originalFilename) {
        this.originalFilename = originalFilename;
    }

    public String getFileExtension() {
        return fileExtension;
    }

    public void setFileExtension(String fileExtension) {
        this.fileExtension = fileExtension;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    @Override
    public String toString() {
        return "UploadFileRespData{" +
                "filename='" + filename + '\'' +
                ", oraginalFilename='" + originalFilename + '\'' +
                ", fileExtension='" + fileExtension + '\'' +
                ", filePath='" + filePath + '\'' +
                ", fileSize=" + fileSize +
                ", contentType='" + contentType + '\'' +
                '}';
    }
}
