package soly.dev.rag.parser;

public interface DocumentParser {

    String parse(String filePath);

    boolean supports(String fileExtension);
}
