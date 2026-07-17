package soly.dev.rag.splitter.impl;

import org.apache.commons.lang3.StringUtils;
import soly.dev.rag.constants.SplitterStrategy;
import soly.dev.rag.constants.StaticRule;
import soly.dev.rag.splitter.DocumentSplitter;

import java.util.ArrayList;
import java.util.List;

/**
 * 静态规则切分器
 */
public class StaticRuleSplitter implements DocumentSplitter {

    private final StaticRule staticRule;
    // 分隔符配置（例如："\n\n", "。", 或使用正则 "(?<=[。！？])" 保留标点）
    private final String pattern;
    // 强制截断长度（防止按句号切分时，遇到极端的无标点长句）
    private final int maxChunkSize;

    // 使用预设枚举 StaticRule 作为分隔符配置
    public StaticRuleSplitter(StaticRule staticRule, int maxChunkSize) {
        this.staticRule = staticRule;
        this.pattern = staticRule.getPattern();
        this.maxChunkSize = maxChunkSize;
    }

    // 用户自定义正则
    public StaticRuleSplitter(String pattern, int maxChunkSize) {
        this.staticRule = StaticRule.CUSTOM;
        this.pattern = pattern;
        this.maxChunkSize = maxChunkSize;
    }

    @Override
    public List<String> split(String fullContent) {
        ArrayList<String> chunks = new ArrayList<>();
        String[] rawPieces = fullContent.split(pattern);
        for (String rawPiece : rawPieces) {
            if (StringUtils.isBlank(rawPiece)) {
                continue;
            }
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
        return String.format("%s:%s", SplitterStrategy.STATIC_RULE.getName(),
                staticRule == StaticRule.CUSTOM
                        ? String.format("%s(%s)", staticRule.getName(), pattern)
                        : staticRule.getName());
    }

    private List<String> splitLongPiece(String longRawPiece) {
        ArrayList<String> chunks = new ArrayList<>();
        for (int i = 0; i < longRawPiece.length(); i += maxChunkSize) {
            chunks.add(longRawPiece.substring(i, Math.min(i + maxChunkSize, longRawPiece.length())));
        }
        return chunks;
    }

}
