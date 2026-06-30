package soly.dev.rag.splitter.impl;

import soly.dev.rag.splitter.DocumentSplitter;

import java.util.List;

/**
 * 滑动窗口切分器
 * TODO 待实现
 */
public class SlidingWindowSplitter implements DocumentSplitter {
    @Override
    public List<String> split(String fullContent) {
        return List.of();
    }

    @Override
    public String getStrategyName() {
        return "";
    }
}
