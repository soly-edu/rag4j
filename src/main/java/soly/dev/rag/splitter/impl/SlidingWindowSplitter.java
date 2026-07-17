package soly.dev.rag.splitter.impl;

import soly.dev.rag.constants.SplitterStrategy;
import soly.dev.rag.constants.StaticRule;
import soly.dev.rag.splitter.DocumentSplitter;

import java.util.ArrayList;
import java.util.List;

/**
 * 滑动窗口切分器
 */
public class SlidingWindowSplitter implements DocumentSplitter {

    private final StaticRuleSplitter layer1Splitter;

    private final int windowSize;

    private final int overlapSize;

    public SlidingWindowSplitter(int windowSize, int overlapSize) {
        this.layer1Splitter = new StaticRuleSplitter(StaticRule.REGULAR_PUNCTUATION, windowSize);
        this.windowSize = windowSize;
        this.overlapSize = overlapSize;
    }

    @Override
    public List<String> split(String fullContent) {
        // 先静态拆分再进行滑动窗口拼接处理
        List<String> rawPieces = layer1Splitter.split(fullContent);

        // 缓冲区（存放重叠部分+分片部分）
        StringBuilder buffer = new StringBuilder();
        ArrayList<String> result = new ArrayList<>();
        for (String rawPiece : rawPieces) {
            // if (rawPiece.length() > chunkSize) {
            //     // 经历了layer1拆分后不应该存在，因为layer1使用的maxChunkSize就是chunkSize
            //     throw new IllegalStateException("Unexpected long piece after layer1 splitting: " + rawPiece);
            // }
            if (buffer.length() + rawPiece.length() <= windowSize) {
                // 如果 当前缓冲区数据 + 当前循环源数据分片 <= 窗口大小，则直接追加
                buffer.append(rawPiece);
            } else {
                // 如果 当前缓冲区数据 + 当前循环源数据分片 > 窗口大小，则将 缓冲区内容 输出为 结果分片
                result.add(buffer.toString());
                // 缓冲区 重置为 结果分片末尾重叠区域部分内容
                buffer = new StringBuilder(buffer.substring(Math.max(0, buffer.length() - overlapSize)));
                // 处理当前源数据分片数据（前面没有处理，只是处理了缓冲区内容）
                if (buffer.length() + rawPiece.length() <= windowSize) {
                    // 如果当前缓冲区（重复区域内容）+当前源数据分片长度小于等于windowSize，则直接追加
                    buffer.append(rawPiece);
                } else {
                    // 如果已经大于窗口大小，则直接舍弃重叠部分内容，将当前源数据分片作为新的缓冲区内容
                    buffer = new StringBuilder(rawPiece);
                }
            }
        }
        if (!buffer.isEmpty()) {
            result.add(buffer.toString());
        }
        return result;
    }

    @Override
    public String getStrategyName() {
        return SplitterStrategy.SLIDING_WINDOW.getName();
    }
}
