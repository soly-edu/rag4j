package soly.dev.rag.splitter.impl;

import soly.dev.rag.splitter.DocumentSplitter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 静态规则切分器
 */
public class StaticRuleSplitter implements DocumentSplitter {

    // 分隔符配置（例如："\n\n", "。", 或使用正则 "(?<=[。！？])" 保留标点）
    private final String separator;
    // 强制截断长度（防止按句号切分时，遇到极端的无标点长句）
    private final int maxChunkSize;

    public StaticRuleSplitter(String separator, int maxChunkSize) {
        this.separator = separator;
        this.maxChunkSize = maxChunkSize;
    }

    @Override
    public List<String> split(String fullContent) {
        ArrayList<String> chunks = new ArrayList<>();
        String[] rawPieces = fullContent.split(separator);
        for (String rawPiece : rawPieces) {
            if (rawPiece.length() <= maxChunkSize) {
                chunks.add(rawPiece);
            } else {
                chunks.addAll(splitLongPiece(rawPiece));
            }
        }
        return chunks;
    }

    @Override
    public String getStrategyName() {
        return "static_rule_" + separator;
        // switch (separator) {
        //     case "\n":
        //         return "StaticRule_NewLine";
        //         case "\r":
        //             return "StaticRule_Return";
        //     case "\\。":
        //         return "StaticRule_Period";
        // }
    }

    private List<String> splitLongPiece(String longRawPiece) {
        ArrayList<String> chunks = new ArrayList<>();
        for (int i = 0; i < longRawPiece.length(); i += maxChunkSize) {
            chunks.add(longRawPiece.substring(i, i + maxChunkSize));
        }
        return chunks;
    }

}
