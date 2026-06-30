package soly.dev.rag.parser.impl;

import soly.dev.rag.parser.DocumentParser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

// TODO 现在TXT与MD合用，后面分开
public class TxtParser implements DocumentParser {
    @Override
    public String parse(String filePath) {
        String fullContent;
        try {
            fullContent = Files.readString(Paths.get(filePath));
        } catch (IOException e) {
            throw new RuntimeException("读取文件内容失败：" + filePath, e);
        }
        return fullContent;
    }

    @Override
    public boolean supports(String fileExtension) {
        return "txt".equalsIgnoreCase(fileExtension) || "md".equalsIgnoreCase(fileExtension);
    }
}
