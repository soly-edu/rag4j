package soly.dev.rag.parser;

import soly.dev.rag.parser.impl.JsonParser;
import soly.dev.rag.parser.impl.PdfParser;
import soly.dev.rag.parser.impl.TxtParser;
import soly.dev.rag.parser.impl.WordParser;

public class DocumentParserFactory {

    public static String parseContent(String filePath, String fileExtension) {
        DocumentParser documentParser = getDocumentParser(fileExtension);
        return documentParser.parse(filePath);
    }

    private static DocumentParser getDocumentParser(String fileExtension) {
        return switch (fileExtension.toLowerCase()) {
            case "txt", "md" -> new TxtParser();
            case "doc", "docx" -> new WordParser();
            case "json", "jsonl" -> new JsonParser();
            case "pdf" -> new PdfParser();
            default -> throw new IllegalArgumentException("Unsupported file extension: " + fileExtension);
        };
    }
}
